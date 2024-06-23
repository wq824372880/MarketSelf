package com.zeekrlife.market.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.hjq.shape.view.ShapeImageView
import com.kingja.loadsir.core.Transport
import com.zeekr.component.tv.button.ZeekrTVButton
import com.zeekr.component.tv.input.ZeekrTVInputView
import com.zeekr.component.tv.input.ZeekrTVOnClickerListener
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.util.decoration.builder.XDividerOrientation
import com.zeekrlife.common.widget.state.BaseEmptyCallback
import com.zeekrlife.market.R
import com.zeekrlife.market.app.aop.SingleClick
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.eventViewModel
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.ActivityTvSearchBinding
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.ui.adapter.SearchAdapter
import com.zeekrlife.market.ui.viewmodel.SearchViewModel
import com.zeekrlife.market.widget.AppDialog
import com.zeekrlife.market.widget.AppletDialog
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.XLog
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 描述: SearchTVActivity
 */
class SearchTVActivity : BaseActivity<SearchViewModel, ActivityTvSearchBinding>(),
    // 安装状态改变监听器的匿名实现体，主要用于处理搜索功能的状态管理。
    InstallStateChangeListener {
    private val searchEmptyKey = "SEARCH_EMPTY" // 用于标识空搜索的键名。
    private var mKeyword: String = "" // 用户输入的搜索关键字。
    private var editChangeToEmpty: Boolean = false // 编辑框内容是否变为为空。
    private var searchEmpty = false //记录是否执行过空搜索（联想出全部列表）
    private var isEntry = false // 标记是否从网关入口进入的搜索页面，此类页面不自动打开键盘。
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(
            R.layout.item_search_view,
            mutableListOf()
        ) // 懒加载搜索适配器，初始化时为空列表。
    }
    private lateinit var mInputMethodManager: InputMethodManager

    @Volatile
    private var appletDialog: AppletDialog? = null

    companion object{
        private const val TAG = "zzzSearchTVActivity"
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(searchEmptyKey, searchEmpty)
        super.onSaveInstanceState(outState)
    }

    /**
     * 初始化视图。
     * 该方法首先调用超类的initView方法，然后设置用户界面，改变搜索结果的样式。如果存在保存的状态，
     * 恢复搜索框和搜索列表的数据。设置RecyclerView的网格布局和适配器，并处理键盘显示和搜索框的焦点问题。
     *
     * @param savedInstanceState 如果Activity被系统重新创建，这个参数包含了之前Activity结束时的状态。
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setUpUI(((findViewById(android.R.id.content)) as ViewGroup).getChildAt(0))
        changeSearchResultStyle(0.4f)

        if (savedInstanceState != null) {
            //恢复搜索框及搜索列表数据,如果搜索框内容为空，并且搜索列表有数据，则更新显示
            if (mBind.searchBar.getText().isNullOrEmpty()) {
                searchEmpty = savedInstanceState.getBoolean(searchEmptyKey, false)
                if (searchEmpty) {
                    editChangeToEmpty = false
                    mViewModel.getSearchList("", isRefresh = true, loadingXml = true)
                }
            }
        }

        // 设置RecyclerView的布局和适配器
        mBind.recyclerView.run {
            grid(3).divider2(XDividerOrientation.GRID, blockGrid = {
                hLineSpacing = R.dimen.category_apps_rv_item_horizontal_space
                vLineSpacing = R.dimen.search_apps_rv_item_vertical_space
                isIncludeEdge = false
            }).adapter = searchAdapter
        }
        mInputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // 从Intent中获取关键字并执行搜索
        intent?.extras?.getString("keyword")?.let {
            "EntryActivity    keyword=$it".logE(TAG)
            isEntry = true
            mBind.searchBar.setText(it)
            doSearch(it)
        }

        // 如果Intent中没有额外的数据但搜索框中已有文本，则执行搜索
        if (intent?.extras == null && !mBind.searchBar.getText().isNullOrEmpty()) {
            mBind.searchBar.getText()?.let {
                doSearch(it)
            }
        }

        // 设置搜索框的请求焦点，以及编辑框的过滤器和键盘弹出行为
        mBind.searchBar.let { view ->
            view.requestFocus()
            view.getIcon1View().run {
                isFocusable = false
                isFocusableInTouchMode = false
            }
            view.getEditText().run {
                filters = arrayOf(LengthFilter(20))
                if (!isEntry) {
                    postDelayed({
                        openKeyboard()
                        popupSoftKeyboard = true
                    }, 300)
                }
            }
        }
    }

    /**
     * 当Activity接收到新的Intent时调用此方法。
     *
     * @param intent 新的Intent对象，可能为null。
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent) // 更新当前Activity的Intent为新的Intent
        intent?.extras?.getString("keyword")?.let {
            // 如果Intent中包含关键字，则进行日志记录并执行搜索
            "EntryActivity    onNewIntent keyword=${it} ".logE(TAG)
            if (it.isNotEmpty()) {
                isEntry = true
                mBind.searchBar.setText(it) // 在搜索栏中显示关键字
                doSearch(it) // 执行搜索操作
            }
        }

    }

    /**
     * 执行搜索操作。
     * @param key 用户输入的搜索关键字。
     */
    private fun doSearch(key: String) {
        // 设置当前搜索关键字，并更新搜索适配器
        mKeyword = key
        searchAdapter.setKeyWord(mKeyword)
        // 触发基于关键字的搜索列表加载，同时设置为刷新状态并显示加载动画
        mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = true)
        // 根据关键字是否为空，更新搜索结果为空的显示状态
        searchEmpty = mKeyword.isEmpty()
        // 延迟300毫秒后隐藏键盘并重置软键盘弹出状态
        mBind.searchBar.postDelayed({
            hideOffKeyboard() // 隐藏键盘
            popupSoftKeyboard = false // 重置软键盘弹出状态为未弹出
        }, 300)
    }

    override fun initObserver() {}

    /**
     * 当绑定视图后，设置点击事件和文本变化监听。
     * 主要处理搜索框的点击事件、文本变化(包括清空文本)事件、以及搜索按钮的点击事件。
     */
    override fun onBindViewClick() {
        // 设置搜索按钮点击事件，隐藏键盘并结束当前活动
        mBind.tvSearchAction.setOnClickListener {
            hideOffKeyboard()
            finishCurrentActivity(this)
        }

        // 设置搜索框文本变化监听，以处理搜索关键字的变化及搜索建议的更新
        mBind.searchBar.getEditText().apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                /**
                 * 当文本变化时触发，用于处理搜索图标可见性、关键字更新及搜索结果的更新。
                 */
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // 当文本不为空时，显示搜索图标，更新关键字并触发搜索
                    takeIf { p0?.isNotEmpty() == true }?.let {
                        mBind.searchBar.setIcon2Visibility(View.VISIBLE)
                        mKeyword = p0.toString()
                        searchAdapter.setKeyWord(mKeyword)
                        if (mKeyword.isNotEmpty()) {
                            searchEmpty = false
                            mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = false)
                        }
                    } ?: run {
                        // 当文本为空时，隐藏搜索图标，更新搜索结果样式并清空搜索结果
                        editChangeToEmpty = p0.isNullOrEmpty()
                        mKeyword = p0.toString()
                        mBind.searchBar.setIcon2Visibility(View.GONE)
                        changeSearchResultStyle(0.4f)
                        searchAdapter.setList(mutableListOf())
                        uiStatusManger.showSuccess()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })

            // 设置搜索框按键监听，处理回车和方向键事件
            setOnKeyListener { v, keyCode, event ->
                XLog.i("searchBar ==> $keyCode")
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    openKeyboard()
                    return@setOnKeyListener true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    clearFocus()
                    hideKeyboard()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }

        // 设置搜索框图标点击事件，包括清除文本、打开键盘等操作
        mBind.searchBar.setOnIconClickerListener(object : ZeekrTVOnClickerListener{
            override fun onIcon1Click(view: View) {
            }

            override fun onIcon2Click(view: View) {
                mBind.searchBar.setText("")
                mBind.searchBar.setIcon2Visibility(View.GONE)
                mBind.searchBar.getEditText().run {
                    openKeyboard()
                    popupSoftKeyboard = true
                }
            }

            override fun onIcon3Click(view: View) {
            }

        })

        // 设置搜索结果项的点击事件处理
        searchAdapter.setOnItemClickListener @SingleClick { adapter, view, position ->

            val item: AppItemInfoBean = searchAdapter.getItem(position)
            if (item.dataType == 1) {
                // 处理小程序卡片的点击事件
                if (appletDialog == null) {
                    appletDialog = AppletDialog(this)
                }
                appletDialog?.show(item)

            } else {
                // 处理其他类型项的点击事件
                AppDialog().show(this, item, this)
            }

        }

    }

    /**
     * 请求成功时的处理逻辑。
     * 此函数观察 ViewModel 中的 searchData 变化，并根据情况更新搜索结果的展示样式，
     * 加载数据，以及切换 UI 状态（成功、空结果、错误）。
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestSuccess() {
        // 观察 searchData 变化，当数据变化时执行相应的逻辑
        mViewModel.searchData.observe(this, Observer { it ->
            // 当搜索结果刷新时，加载更多数据
            mBind.smartRefreshLayout.loadMore {
                mViewModel.getSearchList(mKeyword, isRefresh = false, loadingXml = false)
            }
            // 改变搜索结果的展示样式
            changeSearchResultStyle(1f)
            // 对收到的数据进行处理
            it.list?.run {
                // 如果搜索关键词为空且之前进行了编辑操作，将搜索结果样式改为半透明，并清空列表
                if (mKeyword.isEmpty() && editChangeToEmpty) {
                    editChangeToEmpty = false
                    changeSearchResultStyle(0.4f)
                    searchAdapter.setList(mutableListOf())
                    uiStatusManger.showSuccess()
                    return@run
                }

                // 如果有数据，则加载数据成功，更新 UI
                if ((it.list?.size ?: 0) > 0) {
                    searchAdapter.loadListSuccess(it, mBind.smartRefreshLayout)
                    showSuccessUi()
                } else {
                    // 如果没有数据，延时展示空结果的 UI，并处理空结果的情况
                    lifecycleScope.launch {
                        delay(300)
                        showEmptyUi()
                        handleResponseEmpty()
                    }
                }
            }

        })
    }

    /**
     * 当加载失败时进行重试的逻辑处理。
     * 该方法重写了父类的onLoadRetry方法，首先调用父类的重试逻辑，然后更新编辑状态为非空，判断搜索关键词是否为空，
     * 并基于关键词进行搜索列表的刷新操作。
     */
    override fun onLoadRetry() {
        super.onLoadRetry() // 调用父类的onLoadRetry方法
        editChangeToEmpty = false // 将编辑状态的空值标记设置为false
        searchEmpty = mKeyword.isEmpty() // 根据关键词判断搜索框是否为空
        // 发起搜索列表的加载请求，刷新数据，且显示加载动画
        mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = true)
    }

    /**
     * 当请求为空时调用此函数。
     * 对于传入的加载状态，除了调用超类的相应处理方法外，还会执行自定义的空响应处理。
     *
     * @param loadStatus 表示加载状态的实体，包含关于加载操作的详细信息。
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        super.onRequestEmpty(loadStatus) // 调用父类的空请求处理方法
        handleResponseEmpty() // 处理自定义的空响应逻辑
    }

    /**
     * 当请求出错时的处理逻辑。
     *
     * @param loadStatus 包含请求信息和错误状态的实体。
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        // 根据请求代码处理不同的错误情况
        when (loadStatus.requestCode) {
            NetUrl.APP_LIST -> {
                // 对搜索结果样式进行变更
                changeSearchResultStyle(1f)
                // 通知适配器加载列表出错，并传入错误状态和刷新控件
                searchAdapter.loadListError(loadStatus, mBind.smartRefreshLayout)
            }
        }
    }

    /**
     * 更改搜索结果的显示样式。
     * @param mAlpha 透明度值，用于控制搜索结果文本的可见度。
     */
    private fun changeSearchResultStyle(mAlpha: Float) {
        if (mAlpha == 1f) {
            // 当mAlpha为1f时，显示搜索结果文本，并设置其不透明，同时启用下拉加载更多功能
            mBind.tvSearchResult.text = "搜索结果"
            mBind.tvSearchResult.alpha = 1f
            mBind.smartRefreshLayout.setEnableLoadMore(true)
//            mBind.tvSearchResult.visible()
        } else {
            // 当mAlpha不为1f时，显示“暂无搜索结果”文本，并设置其为半透明，同时禁用下拉加载更多功能
            mBind.tvSearchResult.text = "暂无搜索结果"
            mBind.tvSearchResult.alpha = 0.4f
//            mBind.tvSearchResult.visible()
            mBind.smartRefreshLayout.setEnableLoadMore(false)

        }

    }

    /**
     * 处理空响应的场景，即当没有搜索结果时展示特定的UI并提供操作。
     * 该函数不接受参数，也不返回任何值。
     */
    private fun handleResponseEmpty() {
        uiStatusManger.setCallBack(BaseEmptyCallback::class.java, object : Transport {
            override fun order(context: Context?, view: View?) {
                val button = view?.findViewById<ZeekrTVButton>(R.id.state_empty_text)
                val content = view?.findViewById<TextView>(R.id.tv_content)
                button?.apply {
                    text = "去精品推荐看看"
                    visible()
                    setTVClickListener {
                        eventViewModel.switchTabEvent.postValue(0)
                        hideOffKeyboard()
                        finishCurrentActivity(this@SearchTVActivity)
                    }

                }
                view?.setOnClickListener {
                    hideOffKeyboard()
                    popupSoftKeyboard = false
                }
                content?.text = "暂无搜索结果"
            }

        })

        changeSearchResultStyle(1f)

    }

    override fun getLoadingView(): View? {
        return mBind.smartRefreshLayout
    }

    override fun showToolBar(): Boolean {
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
//        if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action != KeyEvent.ACTION_UP) {
//            mBind.searchBar.getText()?.let {
//                mKeyword = it
//                searchAdapter.setKeyWord(mKeyword)
//                editChangeToEmpty = false
//                mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = true)
//                searchEmpty = mKeyword.isEmpty()
//            }
//            hideOffKeyboard()
//            popupSoftKeyboard = false
//        }
        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InstallAppManager.addInstallStateChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        InstallAppManager.removeInstallStateChangeListener(this)
    }

    /**
     * 当应用卸载成功时调用此方法。
     * 对于给定的包名，它会尝试在应用列表中找到对应的位置，并在UI线程上通知适配器更新这一项。
     *
     * @param packageName 卸载应用的包名。
     */
    override fun onUnInstallSuccess(packageName: String) {
        try {
            // 尝试获取应用在列表中的位置
            val position = mViewModel.getAppListPosition(packageName)
            if (position != -1) {
                // 如果找到了对应位置，则在UI线程上通知适配器更新该项
                lifecycleScope.launch(Dispatchers.Main) {
                    searchAdapter.notifyItemChanged(position)
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            // 捕获索引越界异常，可能是列表中不存在该包名的应用
            Log.e(TAG, "IndexOutOfBoundsException: ${e.message}")
        } catch (e: IllegalStateException) {
            // 捕获非法状态异常，可能是视图模型或适配器的状态不正确
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: NullPointerException) {
            // 捕获空指针异常，可能是变量未初始化
            Log.e(TAG, "NullPointerException: ${e.message}")
        } catch (e: Exception) {
            // 捕获其他所有异常，进行日志记录
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    /**
     * 设置UI的相关初始化操作。
     * @param view 需要进行UI设置的视图对象。
     */
    private fun setUpUI(view: View) {
        // 如果传入的view不是EditText，则为view设置触摸监听器
        if (view !is EditText) {
            view.setOnTouchListener { v, _ ->
                // 如果被触摸的视图不是ShapeImageView，则隐藏软键盘，并设置popupSoftKeyboard为false
                if(v !is ShapeImageView){
                    hideSoftKeyboard(this)
                    popupSoftKeyboard = false
                }
                false // 返回false，表示不消耗事件
            }
        }

        // 如果传入的view是ViewGroup类型，则对其进行进一步处理
        if (view is ViewGroup) {
            // 如果view不是ZeekrTVInputView类型，则遍历其所有子视图，并对每个子视图调用setupUI方法
            if (view !is ZeekrTVInputView) {
                for (i in 0 until view.childCount) {
                    val innerView = view.getChildAt(i)
                    setupUI(innerView)
                }
            }
        }
    }
}

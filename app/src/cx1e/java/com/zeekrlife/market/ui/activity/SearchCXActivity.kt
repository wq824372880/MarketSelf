package com.zeekrlife.market.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.util.Log
import android.view.ActionMode
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.kingja.loadsir.core.Transport
import com.zeekr.basic.findActivity
import com.zeekr.component.button.ZeekrButton
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.util.decoration.builder.XDividerOrientation
import com.zeekrlife.common.widget.state.BaseEmptyCallback
import com.zeekrlife.market.R
import com.zeekrlife.market.app.aop.SingleClick
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.eventViewModel
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.ActivitySearchBinding
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.ui.adapter.SearchAdapter
import com.zeekrlife.market.ui.viewmodel.SearchCXViewModel
import com.zeekrlife.market.ui.viewmodel.SearchViewModel
import com.zeekrlife.market.widget.AppletDialog
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.CharacterHandler
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 描述　:
 */
class SearchCXActivity : BaseActivity<SearchCXViewModel, ActivitySearchBinding>(),
    InstallStateChangeListener {
    private val searchEmptyKey = "SEARCH_EMPTY"
    private var mKeyword: String = ""
    private var editChangeToEmpty: Boolean = false
    private var searchEmpty = false //记录是否执行过空搜索（联想出全部列表）
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(
            R.layout.item_search_view,
            mutableListOf()
        )
    }
    private lateinit var mInputMethodManager: InputMethodManager

    companion object {
        private const val TAG = "zzzSearchCXActivity"
    }

    @Volatile
    private var appletDialog: AppletDialog? = null

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(searchEmptyKey, searchEmpty)
        super.onSaveInstanceState(outState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        "SearchCXActivity initView".logE(TAG)
        setupUI(((findViewById(android.R.id.content)) as ViewGroup).getChildAt(0))
        changeSearchResultStyle(0.4f)

        if (savedInstanceState != null) {
            //恢复搜索框及搜索列表数据,如果搜索框内容为空，并且搜索列表有数据，则更新显示
            if (mBind.searchBar.text?.toString().isNullOrEmpty()) {
                searchEmpty = savedInstanceState.getBoolean(searchEmptyKey, false)
                if (searchEmpty) {
                    editChangeToEmpty = false
                    mViewModel.getSearchList("", isRefresh = true, loadingXml = true)
                }
            }
        }
        mBind.recyclerView.run {
            grid(3).divider2(XDividerOrientation.GRID, blockGrid = {
                hLineSpacing = R.dimen.category_apps_rv_item_horizontal_space
                vLineSpacing = R.dimen.search_apps_rv_item_vertical_space
                isIncludeEdge = false
            }).adapter = searchAdapter
        }
        mInputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        intent?.extras?.getString("keyword")?.let {
            if (it.isNotEmpty()) {
                mBind.searchBar.setText(it)
                mBind.searchBar.setSelection(it.length)
                doSearch(it)
            }
        }

        mBind.searchBar.run {
            filters = arrayOf(LengthFilter(20))
            if (mKeyword.isEmpty()) {
                postDelayed({
                    openKeyboard()
                    popupSoftKeyboard = true
                }, 500)
            }
        }

        if (intent?.extras == null && !mBind.searchBar.text?.toString().isNullOrEmpty()) {
            mBind.searchBar.text.toString().let {
                doSearch(it)
            }
        }

        mBind.searchBar.customSelectionActionModeCallback = actionModeCallback
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        intent?.extras?.getString("keyword")?.let {
            "SearchCXActivity    onNewIntent keyword::${it} ".logE(TAG)
            if (it.isNotEmpty()) {
                mBind.searchBar.setText(it)
                mBind.searchBar.setSelection(it.length)
                doSearch(it)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        "SearchCXActivity onResume".logE(TAG)
    }

    /**
     * 创建一个用于ActionMode的回调对象。这个对象定义了在创建、准备、点击动作项以及销毁ActionMode时的行为。
     */
    private val actionModeCallback = object : ActionMode.Callback {
        /**
         * 当ActionMode被创建时调用此方法。这个方法会过滤掉菜单中不需要的项。
         *
         * @param mode ActionMode对象，表示当前的ActionMode实例。
         * @param menu 菜单对象，表示ActionMode下的菜单。
         * @return 返回true表示成功创建ActionMode，false则表示失败。
         */
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.let {
                // 遍历菜单项并移除不需要的项（如剪切、复制、粘贴等）
                val size = menu.size()
                for (i in size - 1 downTo 0) {
                    val item = menu.getItem(i)
                    val itemId = item.itemId
                    if (itemId != android.R.id.cut
                        && itemId != android.R.id.copy
                        && itemId != android.R.id.selectAll
                        && itemId != android.R.id.paste
                    ) {
                        menu.removeItem(itemId)
                    }
                }
            }
            return true
        }

        /**
         * 当ActionMode即将显示时调用此方法。在这里不做任何操作。
         *
         * @param mode ActionMode对象，表示当前的ActionMode实例。
         * @param menu 菜单对象，表示ActionMode下的菜单。
         * @return 返回false表示不需要进一步的准备，true则表示需要。
         */
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        /**
         * 当点击ActionMode中的动作项时调用此方法。在这里不做任何操作。
         *
         * @param mode ActionMode对象，表示当前的ActionMode实例。
         * @param item 被点击的菜单项项对象。
         * @return 返回false表示点击事件未被处理，true则表示已处理。
         */
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        /**
         * 当ActionMode被销毁时调用此方法。在这里不做任何操作。
         *
         * @param mode ActionMode对象，表示被销毁的ActionMode实例。
         */
        override fun onDestroyActionMode(mode: ActionMode?) {}
    }


    /**
     * 执行搜索操作。
     * @param key 用户输入的搜索关键字。
     */
    private fun doSearch(key: String) {
        mKeyword = key
        searchAdapter.setKeyWord(mKeyword)
        mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = true)
        searchEmpty = mKeyword.isEmpty()
        mBind.searchBar.postDelayed({
            hideOffKeyboard()
            popupSoftKeyboard = false
        }, 300)
    }

    override fun initObserver() {}

    /**
     * 当绑定视图点击时的处理逻辑。
     * 该方法设置了搜索框的点击事件监听、文本变化监听以及搜索按钮和关闭按钮的点击事件。
     */
    override fun onBindViewClick() {
        // 设置搜索按钮点击事件，隐藏软键盘并结束当前活动
        mBind.tvSearchAction.setOnClickListener {
            hideOffKeyboard()
            val fadeInAnimation = ObjectAnimator.ofFloat(mBind.tvSearchAction, View.ALPHA, 1f, 0f)
            fadeInAnimation.duration = 300 // 设置动画时长
            fadeInAnimation.interpolator = DecelerateInterpolator(3f)
            fadeInAnimation.start() // 启动动画

            val resultfadeInAnimation = ObjectAnimator.ofFloat(mBind.tvSearchResult, View.ALPHA, 0.4f, 0f)
            resultfadeInAnimation.duration = 300 // 设置动画时长
            resultfadeInAnimation.interpolator = DecelerateInterpolator(3f)
            resultfadeInAnimation.start() // 启动动画

            finishAfterTransition()
        }

        // 设置搜索栏的文本变化监听，用于处理搜索关键字的改变和搜索的触发
        mBind.searchBar.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                @SuppressLint("LogNotTimber")
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.e("SearchCXActivity", "onTextChanged $p0")
                }

                @SuppressLint("LogNotTimber")
                override fun afterTextChanged(p0: Editable?) {
                    Log.e("SearchCXActivity", "afterTextChanged $p0")
                    takeIf { p0?.isNotEmpty() == true }?.let {
                        mBind.ivSearchClose.visible()
                        mKeyword = p0.toString()
                        searchAdapter.setKeyWord(mKeyword)
                        if (mKeyword.isNotEmpty()) {
                            searchEmpty = false
                            mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = false)
                        }
                    } ?: run {
                        editChangeToEmpty = p0.isNullOrEmpty()
                        mKeyword = p0.toString()
                        mBind.ivSearchClose.inVisible()
                        changeSearchResultStyle(0.4f)
                        searchAdapter.setList(mutableListOf())
                        uiStatusManger.showSuccess()
                    }
                }

            })
        }

        // 设置搜索框关闭按钮的点击事件，用于清空搜索框文本和隐藏关闭按钮
        mBind.ivSearchClose.setOnClickListener {
            mBind.searchBar.setText("")
            mBind.ivSearchClose.inVisible()
            mBind.searchBar.run {
//                if (!popupSoftKeyboard) {
                    openKeyboard()
                    popupSoftKeyboard = true
//                }
            }
        }

        // 设置搜索结果列表的点击事件监听，用于处理点击应用的逻辑
        searchAdapter.setOnItemClickListener @SingleClick { adapter, view, position ->
            // 根据应用类型执行相应的操作，如打开小程序或跳转到应用详情
            val item: AppItemInfoBean = searchAdapter.getItem(position)
            if (item.dataType == 1) {
                if (appletDialog == null) {
                    appletDialog = AppletDialog(this)
                }
                appletDialog?.show(item)

            } else {
                AppDetailCXActivity.start(this, searchAdapter.getItem(position).id)
            }

        }

    }

    /**
     * 请求成功时的处理逻辑。
     * 该方法首先观察搜索数据的变化，根据数据的变化来更新UI，包括加载更多数据、改变搜索结果样式，
     * 处理空结果的展示等。
     *
     * @since API 24 (Build.VERSION_CODES.N)
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestSuccess() {
        // 观察搜索数据的变化，根据新的数据更新UI
        mViewModel.searchData.observe(this, Observer { it ->
            // 加载更多搜索结果
            mBind.smartRefreshLayout.loadMore {
                mViewModel.getSearchList(mKeyword, isRefresh = false, loadingXml = false)
            }
            // 改变搜索结果的展示样式
            changeSearchResultStyle(1f)
            // 数据处理逻辑
            it.list?.run {
                // 当搜索关键字为空且之前有编辑操作变为为空时，改变搜索结果样式并清空展示内容
                if (mKeyword.isEmpty() && editChangeToEmpty) {
                    editChangeToEmpty = false
                    changeSearchResultStyle(0.4f)
                    searchAdapter.setList(mutableListOf())
                    uiStatusManger.showSuccess()
                    return@run
                }

                // 当有搜索结果时，更新适配器数据并处理UI展示
                if ((it.list?.size ?: 0) > 0) {
                    searchAdapter.loadListSuccess(it, mBind.smartRefreshLayout)
                    // 展示成功UI
                    showSuccessUi()
                } else {
                    // 当无搜索结果时，延时展示空UI并处理空响应
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
        super.onLoadRetry()
        // 将编辑状态的空值标记设置为false
        editChangeToEmpty = false
        // 根据关键词判断搜索框是否为空
        searchEmpty = mKeyword.isEmpty()
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
        super.onRequestEmpty(loadStatus)
        handleResponseEmpty()
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
            mBind.tvSearchResult.text = "搜索结果"
            mBind.tvSearchResult.alpha = 1f
            mBind.smartRefreshLayout.setEnableLoadMore(true)
//            mBind.tvSearchResult.visible()
        } else {
            mBind.tvSearchResult.text = "暂无搜索结果"
            mBind.tvSearchResult.alpha = 0.4f
//            mBind.tvSearchResult.visible()
            mBind.smartRefreshLayout.setEnableLoadMore(false)

        }

    }

    /**
     * 处理空响应的情况，即当没有搜索结果时显示的界面。
     * 此函数设置界面状态，包括显示"暂无搜索结果"的信息和一个按钮，
     * 按钮点击时会尝试切换到精品推荐标签或打开精品推荐活动页面。
     */
    private fun handleResponseEmpty() {
        uiStatusManger.setCallBack(BaseEmptyCallback::class.java, object : Transport {
            override fun order(context: Context?, view: View?) {
                val button = view?.findViewById<ZeekrButton>(R.id.state_empty_text)
                val content = view?.findViewById<TextView>(R.id.tv_content)
                button?.apply {
                    text = "去精品推荐看看"
                    visible()
                    setOnClickListener {
                        val result = findActivity().find { it?.javaClass?.simpleName?.contentEquals("HomeCXActivity") == true }
                        if(result != null) {
                            eventViewModel.switchTabCXEvent.postValue(0)
                        }else{
                            toStartActivity(this@SearchCXActivity, HomeCXActivity::class.java, Bundle())
                        }
                        hideOffKeyboard()
                        finishCurrentActivity(this@SearchCXActivity)
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

    /**
     * 分发键盘事件。
     * 此函数主要用于处理键盘输入事件，特别是当用户按下Enter键时的处理逻辑。
     *
     * @param event 键盘事件对象，包含了事件的详细信息，如按键码、事件动作等。
     * @return 返回一个布尔值，指示事件是否已被处理（即是否消耗了该事件）。
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // 当按下的是Enter键且当前不是释放动作时，执行搜索逻辑
        if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action != KeyEvent.ACTION_UP) {
            // 更新关键词并通知适配器
            mBind.searchBar.text?.toString()?.let {
                mKeyword = it
                searchAdapter.setKeyWord(mKeyword)
                editChangeToEmpty = false
                // 触发搜索列表的加载
                mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = true)
                // 根据关键词是否为空，更新搜索结果为空视图的显示状态
                searchEmpty = mKeyword.isEmpty()
            }
            // 隐藏软键盘
            hideOffKeyboard()
            popupSoftKeyboard = false
        }
        // 调用父类的dispatchKeyEvent方法，处理未被本函数处理的键盘事件
        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        window.sharedElementExitTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        window.sharedElementEnterTransition.duration = 200
        window.sharedElementEnterTransition.interpolator = DecelerateInterpolator(3f)
        window.sharedElementExitTransition.duration = 200
        window.sharedElementExitTransition.interpolator = DecelerateInterpolator(3f)

        val fadeInAnimation = ObjectAnimator.ofFloat(mBind.tvSearchAction, View.ALPHA, 0f, 1f)
        fadeInAnimation.duration = 200 // 设置动画时长
        fadeInAnimation.interpolator = DecelerateInterpolator(3f)
        fadeInAnimation.start() // 启动动画

        val resultfadeInAnimation = ObjectAnimator.ofFloat(mBind.tvSearchResult, View.ALPHA, 0f, 0.4f)
        resultfadeInAnimation.duration = 200 // 设置动画时长
        fadeInAnimation.interpolator = DecelerateInterpolator(3f)
        resultfadeInAnimation.start() // 启动动画

        InstallAppManager.addInstallStateChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        InstallAppManager.removeInstallStateChangeListener(this)
    }

    /**
     * 监听到应用卸载，刷新列表
     * @param packageName 包名
     */
    override fun onUnInstallSuccess(packageName: String) {
        try {
            val position = mViewModel.getAppListPosition(packageName)
            if (position != -1) {
                lifecycleScope.launch(Dispatchers.Main) {
                    searchAdapter.notifyItemChanged(position)
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }
}

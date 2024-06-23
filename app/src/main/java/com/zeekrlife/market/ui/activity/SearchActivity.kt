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
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.kingja.loadsir.core.Transport
import com.zeekr.basic.appContext
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
import com.zeekrlife.market.ui.viewmodel.SearchViewModel
import com.zeekrlife.market.utils.ScreenDensityUtils
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
class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>(),
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

    companion object{
        private const val TAG = "zzzSearchActivity"
    }

    @Volatile
    private var appletDialog: AppletDialog? = null

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(searchEmptyKey, searchEmpty)
        super.onSaveInstanceState(outState)
    }

    /**
     * 界面初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        "SearchActivity initView".logE(TAG)
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
            "SearchActivity    onNewIntent keyword::${it} ".logE(TAG)
            if (it.isNotEmpty()) {
                mBind.searchBar.setText(it)
                mBind.searchBar.setSelection(it.length)
                doSearch(it)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        "SearchActivity onResume".logE(TAG)
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.let {
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

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }

    /**
     * 搜索
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

    /**
     * 监听请求结果
     */
    override fun initObserver() {}

    /**
     * 监听点击事件
     */
    override fun onBindViewClick() {
        mBind.tvSearchAction.setOnClickListener {
            hideOffKeyboard()
            val fadeInAnimation = ObjectAnimator.ofFloat(mBind.tvSearchAction, View.ALPHA, 1f, 0f)
            fadeInAnimation.duration = 300 // 设置动画时长
            fadeInAnimation.interpolator = DecelerateInterpolator(3f)
            fadeInAnimation.start() // 启动动画

            val resultfadeInAnimation = ObjectAnimator.ofFloat(mBind.tvSearchResult, View.ALPHA, 0.4f, 0f)
            resultfadeInAnimation.duration = 300 // 设置动画时长
            fadeInAnimation.interpolator = DecelerateInterpolator(3f)
            resultfadeInAnimation.start() // 启动动画

            finishAfterTransition()
        }

        mBind.searchBar.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                @SuppressLint("LogNotTimber")
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.e("SearchActivity", "onTextChanged $p0")
                }

                @SuppressLint("LogNotTimber")
                override fun afterTextChanged(p0: Editable?) {
                    Log.e("SearchActivity", "afterTextChanged $p0")
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

        searchAdapter.setOnItemClickListener @SingleClick { adapter, view, position ->

            val item: AppItemInfoBean = searchAdapter.getItem(position)
            if (item.dataType == 1) {
                if (appletDialog == null) {
                    appletDialog = AppletDialog(this)
                }
                appletDialog?.show(item)

            } else {
                AppDetailActivity.start(this, searchAdapter.getItem(position).id)
            }

        }

    }

    /**
     * 监听请求成功结果
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestSuccess() {
        mViewModel.searchData.observe(this, Observer { it ->
            mBind.smartRefreshLayout.loadMore {
                mViewModel.getSearchList(mKeyword, isRefresh = false, loadingXml = false)
            }
            changeSearchResultStyle(1f)
            it.list?.run {
                if (mKeyword.isEmpty() && editChangeToEmpty) {
                    editChangeToEmpty = false
                    changeSearchResultStyle(0.4f)
                    searchAdapter.setList(mutableListOf())
                    uiStatusManger.showSuccess()
                    return@run
                }

                if ((it.list?.size ?: 0) > 0) {
                    searchAdapter.loadListSuccess(it, mBind.smartRefreshLayout)
                    showSuccessUi()
                } else {
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
     * 加载重试
     */
    override fun onLoadRetry() {
        super.onLoadRetry()
        editChangeToEmpty = false
        searchEmpty = mKeyword.isEmpty()
        mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = true)
    }

    /**
     * 监听请求空数据结果
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        super.onRequestEmpty(loadStatus)
        handleResponseEmpty()
    }

    /**
     * 监听请求错误结果
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            NetUrl.APP_LIST -> {
                changeSearchResultStyle(1f)
                searchAdapter.loadListError(loadStatus, mBind.smartRefreshLayout)
            }
        }

    }

    /**
     * 搜索结果样式
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
     * 处理空数据
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
                        val result = findActivity().find { it?.javaClass?.simpleName?.contentEquals("HomeActivity") == true }
                        if(result != null) {
                            eventViewModel.switchTabEvent.postValue(0)
                        }else{
                            toStartActivity(HomeActivity::class.java)
                        }
                        hideOffKeyboard()
                        finishCurrentActivity(this@SearchActivity)
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

    /**
     * 获取刷新布局
     */
    override fun getLoadingView(): View? {
        return mBind.smartRefreshLayout
    }

    /**
     * 显示toolbar
     */
    override fun showToolBar(): Boolean {
        return false
    }

    /**
     * 监听键盘事件
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action != KeyEvent.ACTION_UP) {
            mBind.searchBar.text?.toString()?.let {
                mKeyword = it
                searchAdapter.setKeyWord(mKeyword)
                editChangeToEmpty = false
                mViewModel.getSearchList(mKeyword, isRefresh = true, loadingXml = true)
                searchEmpty = mKeyword.isEmpty()
            }
            hideOffKeyboard()
            popupSoftKeyboard = false
        }
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

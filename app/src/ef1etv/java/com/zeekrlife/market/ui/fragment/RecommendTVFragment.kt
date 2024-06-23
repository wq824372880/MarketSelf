package com.zeekrlife.market.ui.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout.LayoutParams
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.sensorsdata.analytics.android.sdk.util.AppInfoUtils
import com.zeekr.basic.appContext
import com.zeekr.basic.getPackageNameName
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.imageloader.ImageLoader.load
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.decoration.builder.XDividerOrientation
import com.zeekrlife.market.R
import com.zeekrlife.market.app.App
import com.zeekrlife.market.app.base.BaseFragment
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.FragmentTvRecommendBinding
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.ui.activity.AppDetailActivity
import com.zeekrlife.market.ui.activity.EntryActivity
import com.zeekrlife.market.ui.activity.HomeTVActivity
import com.zeekrlife.market.ui.adapter.BannerAdapter
import com.zeekrlife.market.ui.adapter.RecommendAppListTVAdapter
import com.zeekrlife.market.ui.viewmodel.RecommendViewModel
import com.zeekrlife.market.utils.FocusBorderFrameLayout
import com.zeekrlife.market.widget.AppDialog
import com.zeekrlife.market.widget.AppletDialog
import com.zeekrlife.market.widget.CustomPageTransformer
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zhpan.bannerview.BannerViewPager.OnPageClickListener
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.indicator.enums.IndicatorSlideMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("LogNotTimber")
class RecommendTVFragment : BaseFragment<RecommendViewModel, FragmentTvRecommendBinding>(),
    InstallStateChangeListener {

    companion object {
        private const val TAG = "RecommendTVFragment"
    }

    private var firstFocusId = View.NO_ID  //精品推荐右键，落焦第一个推荐位，推荐为空时，落焦AppList第一个卡片
    private var lastLeftClickTime = 0L
    private var lastRightClickTime = 0L
    private val appsAdapter: RecommendAppListTVAdapter by lazy { RecommendAppListTVAdapter() }
    private val bannerAdapter: BannerAdapter by lazy { BannerAdapter() }
    private var appletDialog: AppletDialog? = null
    private var bannerDataChangedWhenHidden = mutableListOf<String>()
    private var isRefresh: Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private var appRoutePageName = ""
    private var isShowRefreshToast: Boolean = false  //bugfix 标记toast弹出

    private var tvRefreshing = false
    var onBannerPageClickListener = OnPageClickListener { _, position ->
        try {
            onBannerItemClick(position)
        } catch (e: IndexOutOfBoundsException) {
            // 处理索引越界异常
            Log.e(TAG, "IndexOutOfBoundsException: ${e.message}")
        } catch (e: NullPointerException) {
            // 处理空指针异常
            Log.e(TAG, "NullPointerException: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // 处理非法参数异常
            Log.e(TAG, "IllegalArgumentException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        //Banner
        mBind.bannerView.apply {
            setAdapter(bannerAdapter)
            setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
            setIndicatorSlideMode(IndicatorSlideMode.NORMAL)
            setIndicatorView(mBind.indicatorView.apply {
                setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.tv_5))
//                setIndicatorSliderRadius(resources.getDimensionPixelOffset(dimen.dp_4), resources.getDimensionPixelOffset(dimen.dp_5))
                setIndicatorHeight(resources.getDimensionPixelOffset(R.dimen.tv_5))
                setIndicatorSliderWidth(
                    resources.getDimensionPixelOffset(R.dimen.tv_5),
                    resources.getDimensionPixelOffset(R.dimen.tv_12)
                )
                setIndicatorDrawable(
                    R.drawable.banner_indicator_nornal,
                    R.drawable.banner_indicator_slider
                )
            })
//            setPageMargin(15.dp)
            setScrollDuration(800)
            setRevealWidth(resources.getDimensionPixelOffset(R.dimen.tv_173))
            setOnPageClickListener(onBannerPageClickListener)
            setInterval(5000)

//            setRoundCorner(resources.getDimensionPixelOffset(R.dimen.recommend_banner_imageview_round))
            removeDefaultPageTransformer()
            setPageTransformer(CustomPageTransformer())
            create()
        }
        mBind.viewSelectBannerItem.setOnFocusChangeListener { _, hasFocus ->
            when (hasFocus) {
                true -> mBind.bannerView.stopLoop()
                false -> mBind.bannerView.startLoop()
            }
        }

        mBind.viewSelectBannerItem.setTVClickListener(false) {
            onBannerItemClick(mBind.bannerView.currentItem)
        }

        mBind.viewSelectBannerItem.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                val currentTime = System.currentTimeMillis()
                if (lastLeftClickTime != 0L && (currentTime - lastLeftClickTime < 500)) {
                    return@setOnKeyListener true
                }
                lastLeftClickTime = currentTime
                if (mBind.bannerView.currentItem == 0) {
                    mBind.bannerView.setCurrentItem(mBind.bannerView.data.size - 1, false)
                } else {
                    mBind.bannerView.setCurrentItem(mBind.bannerView.currentItem - 1, false)
                }
                return@setOnKeyListener true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                val currentTime = System.currentTimeMillis()
                if (lastRightClickTime != 0L && (currentTime - lastRightClickTime < 500)) {
                    return@setOnKeyListener true
                }
                lastRightClickTime = currentTime
                if (mBind.bannerView.currentItem == mBind.bannerView.data.size - 1) {
                    mBind.bannerView.setCurrentItem(0, false)
                } else {
                    mBind.bannerView.setCurrentItem(mBind.bannerView.currentItem + 1, false)
                }
                return@setOnKeyListener true
            }else if(KeyEventUtil.isOkKeyEvent(keyCode)){
                onBannerItemClick(mBind.bannerView.currentItem)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }

        mBind.smartRefreshLayout.refresh {
            isRefresh = true
            tvRefreshing = false
            isShowRefreshToast = true
            mViewModel.getRecommendApps(false,isRefresh = true)
        }
        //推荐列表初始化
        mBind.rvRecommendApps.grid(2).apply {
            divider2(XDividerOrientation.GRID, blockGrid = {
                hLineSpacing = R.dimen.tv_34
                vLineSpacing = R.dimen.tv_86
                isIncludeEdge = false
            })
            adapter = appsAdapter
        }

        appsAdapter.setTVItemClickListener { _, position ->

            val item: AppItemInfoBean = appsAdapter.getItem(position)

            if (item.dataType == 1) {
                if (appletDialog == null) {
                    appletDialog = AppletDialog(requireActivity())
                }
                appletDialog?.show(item)
            } else {
//                startAppPage(Constants.APPSTORE_RECOMMEND_APP_LIST, position)
                AppDialog().show(requireActivity(), item, viewLifecycleOwner)
            }

        }

        getRecommendTabId()
    }

    /**
     * 这个函数用于处理视图隐藏状态改变的事件。函数首先调用父类的onHiddenChanged方法，
     * 然后根据当前是否隐藏，执行不同的操作
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            mBind.bannerView.stopLoop()
        } else {
            bannerDataChangedWhenHidden.takeIf {
                it.size > 0
            }?.run {
                mBind.bannerView.create(this)
                bannerDataChangedWhenHidden.clear()
            }
            mBind.bannerView.startLoop()
            registerStartupStateObserver(appsAdapter.data)
        }
    }

    /**
     * 该函数是Kotlin中的一个函数，重写了initObserver()方法。
     * 该函数主要实现了对三个不同数据列表的观察，分别是recBannerList、recAdsenseList和recAppList。
     * 当这些数据列表发生变化时，会根据不同的情况更新UI。
     */
    override fun initObserver() {
        mViewModel.recBannerList.observe(viewLifecycleOwner, Observer {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(true)
            if (it.isNullOrEmpty()) {
                mBind.bannerView.visibility = View.GONE
                mBind.indicatorView.visibility = View.GONE
            } else {
                mBind.bannerView.visibility = View.VISIBLE
                mBind.indicatorView.visibility = View.VISIBLE
                showSuccessUi()
                if (!isHidden) {
                    mBind.bannerView.create(it)
                } else {
                    bannerDataChangedWhenHidden = it as MutableList<String>
                }
            }
        })
        //推荐位
        mViewModel.recAdsenseList.observe(viewLifecycleOwner, Observer {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(true)
            if (it.isNullOrEmpty()) {
                mBind.scrollViewRecommendAdsense.visibility = View.GONE
            } else {
                mBind.scrollViewRecommendAdsense.visibility = View.VISIBLE
                mBind.layoutRecommendAdsense.root.removeAllViews()
                showSuccessUi()
                val realSenseList: MutableList<String> = mutableListOf()
                if (it.size < 4) {
                    realSenseList.addAll(it)
                } else {
                    realSenseList.addAll(it.take(4))
                }
                realSenseList.forEachIndexed { index, url ->
                    mBind.layoutRecommendAdsense.root.addView(buildRecAdvView(index, url))
                }
            }
        })
        //app列表
        mViewModel.recAppList.observe(viewLifecycleOwner, Observer {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.finishLoadMoreWithNoMoreData()
            mBind.smartRefreshLayout.setEnableRefresh(true)
            showSuccessUi()
            if (it.isNullOrEmpty()) {
                mBind.rvRecommendApps.visibility = View.GONE
            } else {
                mBind.rvRecommendApps.visibility = View.VISIBLE
                if (mViewModel.recAdsenseList.value.isNullOrEmpty()) {
                    appsAdapter.firstItemViewUpId = mBind.viewSelectBannerItem.id
                }
                appsAdapter.setList(it)
            }
        })

        mViewModel.noDataLiveData.observe(viewLifecycleOwner) {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(true)
            if (!it) {
                showEmptyUi()
            }
            if (isRefresh) {
                ToastUtils.show("刷新成功")
                isRefresh = false
            }
        }
        mViewModel.errorDataLiveData.observe(viewLifecycleOwner) {
            mBind.smartRefreshLayout.finishRefresh()
            if (it) { //非空数据方式的请求失败
                if(!mViewModel.recommendList.isNullOrEmpty()){
                    mBind.smartRefreshLayout.setEnableRefresh(true)
                    if(isShowRefreshToast){
                        ToastUtils.show("网络不佳，请检查网络设置")
                    }
                }else{
                    mBind.smartRefreshLayout.setEnableRefresh(false)
                    showErrorUi("")
//                    if (isRefresh) {
                    if(isShowRefreshToast){
                        ToastUtils.show("网络不佳，请检查网络设置")
                    }
                    isRefresh = false
//                    }
                }

            } else {
                mBind.smartRefreshLayout.setEnableRefresh(true)
                showEmptyUi()
                if (isRefresh) {
                    ToastUtils.show("刷新成功")
                    isRefresh = false
                }
            }
        }
    }

    /**
     * 用于在特定条件下加载数据。函数首先尝试将activity强制转换为HomeTVActivity类型，并调用apply函数。
     */
    override fun lazyLoadData() {
        (activity as? HomeTVActivity)?.apply {
            if (isEmptyList) {
                this@RecommendTVFragment.showErrorUi("")
                this@RecommendTVFragment.mBind.smartRefreshLayout.setEnableRefresh(false)
            } else {
                this@RecommendTVFragment.mViewModel.getRecommendApps(false)
            }
        }
    }

    /**
     * 用于在某个条件满足时重新加载数据。 首先，它通过强制转换将activity变量转换为HomeTVActivity类型，
     * 并使用apply函数来设置一些属性和调用方法。
     */
    override fun onLoadRetry() {
        (activity as? HomeTVActivity)?.apply {
            if (isEmptyList) {
                loadingVisible(true)
                mViewModel.getHomeList() //首页数据会触发lazyLoadData
            } else {
                isShowRefreshToast = true
                if(App.configurationChanged){
                    LogUtils.e(RecommendFragment.TAG, "onLoadRetry:: showLoadingUi() configChanged:${App.configurationChanged}")
                    isShowRefreshToast = false
                }
                this@RecommendTVFragment.mViewModel.getRecommendApps(true)
            }
        }
    }

    /**
     * banner点击事件
     * @param position
     */
    private fun onBannerItemClick(position: Int) {
        if (position != mBind.bannerView.currentItem) {
            mBind.bannerView.setCurrentItem(position, true)
        }
        startAppPage(Constants.APPSTORE_RECOMMEND_BANNER, position)
    }

    /**
     * 设置推荐位
     * @param position
     * @param url
     */
    private fun buildRecAdvView(position: Int, url: String): View? = context?.run {
        val frameLayout = FocusBorderFrameLayout(this)
        frameLayout.run {
            scaleValue = 1.05F
            isFocusable = true
            isFocusableInTouchMode = true
            defaultFocusHighlightEnabled = false
            background = ResourcesCompat.getDrawable(resources, R.drawable.selector_focus_border_view, null)
            layoutParams = LayoutParams(
                resources.getDimensionPixelOffset(R.dimen.tv_324),
                resources.getDimensionPixelOffset(R.dimen.tv_162),
            ).apply {
                setMargins(
                    resources.getDimensionPixelOffset(R.dimen.tv_19),
                    0,
                    resources.getDimensionPixelOffset(R.dimen.tv_19),
                    0,
                )
                setPadding(resources.getDimensionPixelOffset(R.dimen.tv_6))
            }
            setTVClickListener(false) {
                startAppPage(Constants.APPSTORE_RECOMMEND_ADSENSE, position)
            }
            nextFocusUpId = mBind.viewSelectBannerItem.id
            if (position == 0) {
                id = View.generateViewId()
                firstFocusId = id
            }
        }

        val imageFilterView = ImageFilterView(this).apply {
            round = resources.getDimensionPixelOffset(R.dimen.tv_11)
                .toFloat()
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ScaleType.CENTER_CROP
            load(
                url,
                R.drawable.img_bg_default_large_app_rec_bg,
                R.drawable.img_bg_error_default_large_app_rec_bg,
            )
            setCover()
        }

        frameLayout.addView(imageFilterView)
        return@run frameLayout
    }

    /**
     * 跳转应用页面
     * @param position 点位码
     * @param position
     */
    private fun startAppPage(pointCode: String, position: Int) {
        val appRoutePage = mViewModel.getAppRouteSource(pointCode, position)
        if (appRoutePage.isNotEmpty()) {
            handler.removeCallbacks(startActivityRunnable)
            appRoutePageName = appRoutePage
            handler.postDelayed(startActivityRunnable, 350)
        } else {
            startAppDetailActivityByVersionId(pointCode, position)
        }
    }

    /**
     * 启动应用详情页
     * @param pointCode 点位码
     * @param position
     */
    private val startActivityRunnable = Runnable {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appRoutePageName))
            val componentName = intent.resolveActivity(appContext.packageManager)
            Log.e(TAG, "uri: ${appRoutePageName}, componentName:$componentName")
            if (componentName != null && componentName.packageName == getPackageNameName(appContext)) {
                EntryActivity.startActivity(activity, intent)
            } else {
                startActivity(intent.apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                })
            }
        } catch (e: ActivityNotFoundException) {
            // 处理未找到Activity异常
            Log.e(TAG, "ActivityNotFoundException: ${e.message}")
            ToastUtils.show(getString(R.string.recommend_app_page_not_found))
        } catch (e: SecurityException) {
            // 处理安全异常
            Log.e(TAG, "SecurityException: ${e.message}")
            ToastUtils.show(getString(R.string.recommend_app_page_not_found))
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            ToastUtils.show(getString(R.string.recommend_app_page_not_found))
        }
    }

    private fun startAppDetailActivityByVersionId(pointCode: String, position: Int) {
        val appVersionId = mViewModel.getAppVersionId(pointCode, position)
        if (appVersionId != -1L) {
            AppDetailActivity.start(activity, appVersionId)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            mBind.bannerView.startLoop()
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: UnsupportedOperationException) {
            // 处理不支持操作异常
            Log.e(TAG, "UnsupportedOperationException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mBind.bannerView.stopLoop()
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: UnsupportedOperationException) {
            // 处理不支持操作异常
            Log.e(TAG, "UnsupportedOperationException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
    }

    /**
     * 监听到应用卸载，刷新列表
     * @param packageName 包名
     */
    override fun onUnInstallSuccess(packageName: String) {
        try {
            val position = mViewModel.getRecAppListPosition(packageName)
            if (position != -1) {
                lifecycleScope.launch(Dispatchers.Main) {
                    appsAdapter.notifyItemChanged(position)
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            // 处理索引越界异常
            Log.e(TAG, "IndexOutOfBoundsException: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // 处理参数异常
            Log.e(TAG, "IllegalArgumentException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
    }

    /**
     * 监听到应用安装，刷新列表
     * @param packageName 包名
     */
    override fun onInstallSuccess(packageName: String) {
        super.onInstallSuccess(packageName)
        AppInfoUtils.getAppVersionName(appContext)
        val data = appsAdapter.data
        val installedAppVesionCode =
            ApkUtils.getPackageInfo(appContext, packageName)?.versionCode ?: -1
        if (data.isNotEmpty() && installedAppVesionCode != -1) {
            data.forEachIndexed { index, appItemInfoBean ->
                if (packageName == appItemInfoBean.apkPackageName &&
                    !appItemInfoBean.apkVersion.isNullOrEmpty()
                ) {
                    if (installedAppVesionCode < appItemInfoBean.apkVersion!!.toLong()) {
                        appItemInfoBean.taskInfo = null
                        appsAdapter.setData(index, appItemInfoBean)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            InstallAppManager.addInstallStateChangeListener(this)
        } catch (e: SecurityException) {
            // 处理安全异常
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // 处理参数异常
            Log.e(TAG, "IllegalArgumentException: ${e.message}")
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * 获取加载中的布局
     * @return View
     */
    override fun getLoadingView(): View {
        return mBind.layoutRecommendContent
    }

    override fun onDestroyView() {
        try {
            InstallAppManager.removeInstallStateChangeListener(this)
            handler.removeCallbacksAndMessages(null)
        } catch (e: SecurityException) {
            // 处理安全异常
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // 处理参数异常
            Log.e(TAG, "IllegalArgumentException: ${e.message}")
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
        super.onDestroyView()
    }

    /**
     * 刷新列表
     */
    fun refresh() {
        if (tvRefreshing) {
            return
        }
        tvRefreshing = true
        mBind.smartRefreshLayout.autoRefresh()
    }

    /**
     * 获取推荐tab的id
     * @return Int
     */
    private fun getRecommendTabId(): Int {
        val act = activity
        if (act is HomeTVActivity) {
            lifecycleScope.launch {
                delay(600)
                val tabView = act.mBind.tablayout.getTabAt(0)
                val searchView = act.mBind.searchBar
                if (firstFocusId == -1) {
                    firstFocusId = appsAdapter.firstItemViewId
                }
                if (firstFocusId != -1) {
                    tabView.nextFocusRightId = firstFocusId
                    searchView.nextFocusRightId = firstFocusId
                    act.mBind.searchBar.nextFocusRightId = firstFocusId
                }
                tabView.id = View.generateViewId()
                appsAdapter.recommendTabId = tabView.id
            }
        }
        return appsAdapter.recommendTabId
    }
}

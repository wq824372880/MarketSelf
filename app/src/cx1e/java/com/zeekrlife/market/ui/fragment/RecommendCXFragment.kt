package com.zeekrlife.market.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout.LayoutParams
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.sensorsdata.analytics.android.sdk.util.AppInfoUtils
import com.zeekr.basic.appContext
import com.zeekr.basic.getPackageNameName
import com.zeekrlife.common.ext.divider2
import com.zeekrlife.common.ext.grid
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.observeByChanged
import com.zeekrlife.common.ext.refresh
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
import com.zeekrlife.market.databinding.FragmentRecommendBinding
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.ui.activity.AppDetailCXActivity
import com.zeekrlife.market.ui.activity.EntryActivity
import com.zeekrlife.market.ui.activity.HomeCXActivity
import com.zeekrlife.market.ui.adapter.BannerAdapter
import com.zeekrlife.market.ui.adapter.RecommendAppListAdapter
import com.zeekrlife.market.ui.viewmodel.RecommendCXViewModel
import com.zeekrlife.market.widget.AppletDialog
import com.zeekrlife.market.widget.CustomPageTransformer
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zhpan.bannerview.BannerViewPager.OnPageClickListener
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.indicator.enums.IndicatorSlideMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("LogNotTimber")
class RecommendCXFragment : BaseFragment<RecommendCXViewModel, FragmentRecommendBinding>(), InstallStateChangeListener {

    private val appsAdapter: RecommendAppListAdapter by lazy { RecommendAppListAdapter() }
    private val bannerAdapter: BannerAdapter by lazy { BannerAdapter() }
    private var appletDialog: AppletDialog? = null
    private var bannerDataChangedWhenHidden = mutableListOf<String>()
    private var isRefresh: Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private var appRoutePageName = ""
    private var isShowRefreshToast: Boolean = false  //bugfix 标记toast弹出

    var onBannerPageClickListener = OnPageClickListener { _, position ->
        try {
            onBannerItemClick(position)
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    companion object{
        const val TAG = "zzzRecommendFragment"
        const val REQUEST_APP_LIST_RECOMMEND_FRAGMENT = "request_app_list_recommend_fragment"
    }
    /**
     * 在视图创建时被调用，用于创建Fragment的视图。
     *
     * @param inflater           用于加载布局文件的LayoutInflater。
     * @param container          承载Fragment的View容器，如果Fragment不提供UI，则可以为null。
     * @param savedInstanceState 如果Fragment被重新构建，则此处可以恢复之前保存的状态。
     * @return 返回Fragment的根视图。如果Fragment不提供UI，则返回null。
     */
    override fun initView(savedInstanceState: Bundle?) {
        //Banner
        mBind.bannerView.apply {
            setAdapter(bannerAdapter)
            setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
            setIndicatorSlideMode(IndicatorSlideMode.NORMAL)
            setIndicatorView(mBind.indicatorView.apply {
                setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.recommend_banner_indicator_gap_padding))
//                setIndicatorSliderRadius(resources.getDimensionPixelOffset(dimen.dp_4), resources.getDimensionPixelOffset(dimen.dp_5))
                setIndicatorHeight(resources.getDimensionPixelOffset(R.dimen.recommend_banner_indicator_height))
                setIndicatorSliderWidth(
                    resources.getDimensionPixelOffset(R.dimen.recommend_banner_indicator_slider_width_normal),
                    resources.getDimensionPixelOffset(R.dimen.recommend_banner_indicator_slider_width_check)
                )
                setIndicatorDrawable(
                    R.drawable.banner_indicator_nornal,
                    R.drawable.banner_indicator_slider
                )
            })
//            setPageMargin(15.dp)
            setScrollDuration(800)
            setRevealWidth(resources.getDimensionPixelOffset(R.dimen.recommend_banner_reveal_width))
            setOnPageClickListener(onBannerPageClickListener)
            setInterval(5000)

//            setRoundCorner(resources.getDimensionPixelOffset(R.dimen.recommend_banner_imageview_round))
            removeDefaultPageTransformer()
            setPageTransformer(CustomPageTransformer())
            create()
        }
        mBind.smartRefreshLayout.refresh {
            isRefresh = true
            isShowRefreshToast = true
            mViewModel.getRecommendApps(false, isRefresh = true)
        }
        //推荐列表初始化
        mBind.rvRecommendApps.grid(2).apply {
            divider2(XDividerOrientation.GRID, blockGrid = {
                hLineSpacing = R.dimen.recommend_apps_rv_item_horizontal_space
                vLineSpacing = R.dimen.recommend_apps_rv_item_vertical_space
                isIncludeEdge = false
            })
            adapter = appsAdapter
        }

        appsAdapter.setOnItemClickListener { _, _, position ->

            val item: AppItemInfoBean = appsAdapter.getItem(position)

            if (item.dataType == 1) {
                if (appletDialog == null) {
                    appletDialog = AppletDialog(requireActivity())
                }
                appletDialog?.show(item)
            } else {
                startAppPage(Constants.APPSTORE_RECOMMEND_APP_LIST, position)
            }

        }
    }

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

    override fun initObserver() {
        mViewModel.recBannerList.observeByChanged(viewLifecycleOwner, {
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
        },{
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(true)
        })
        //推荐位
        mViewModel.recAdsenseList.observeByChanged(viewLifecycleOwner, Observer {
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
                    mBind.layoutRecommendAdsense.root.addView(buildRecAdvImageView(index, url))
                }
            }
        })
        {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(true)
        }
        //app列表
        mViewModel.recAppList.observeByChanged(viewLifecycleOwner, {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(true)
            mBind.smartRefreshLayout.finishLoadMoreWithNoMoreData()
            showSuccessUi()
            if (it.isNullOrEmpty()) {
                mBind.rvRecommendApps.visibility = View.GONE
            } else {
                mBind.rvRecommendApps.visibility = View.VISIBLE
                appsAdapter.setList(it)
            }
        }
            ,{
            mBind.smartRefreshLayout.finishRefresh()
        }
        )

        mViewModel.noDataLiveData.observeByChanged(viewLifecycleOwner, {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(true)
            mBind.smartRefreshLayout.setEnableLoadMore(false)
            showSuccessUi()
            if (!it) {
                showEmptyUi()
            }
            if (isRefresh) {
                ToastUtils.show(activity?:appContext,"刷新成功")
                isRefresh = false
            }
        },{
            mBind.smartRefreshLayout.finishRefresh()
            if (isRefresh) {
                ToastUtils.show(activity?:appContext,"刷新成功")
                isRefresh = false
            }
        }
        )
        mViewModel.errorDataLiveData.observe(viewLifecycleOwner) {
            mBind.smartRefreshLayout.finishRefresh()
            mBind.smartRefreshLayout.setEnableRefresh(false)
            mBind.smartRefreshLayout.setEnableLoadMore(false)
            showSuccessUi()
            if (it) { //非空数据方式的请求失败
                if (!mViewModel.recommendCXList.isNullOrEmpty()) {
                    mBind.smartRefreshLayout.setEnableRefresh(true)
                    if (isShowRefreshToast) {
                        ToastUtils.show(activity?:appContext,"网络不佳，请检查网络设置")
                    }

                } else {
                    mBind.smartRefreshLayout.setEnableRefresh(false)
                    showErrorUi("")
//                    if (isRefresh) {
                    if (isShowRefreshToast) {
                        ToastUtils.show(activity?:appContext,"网络不佳，请检查网络设置")
                    }
                    isRefresh = false
//                    }
                }

            } else {
                mBind.smartRefreshLayout.setEnableRefresh(true)
                showEmptyUi()
                if (isRefresh) {
                    ToastUtils.show(activity?:appContext,"刷新成功")
                    isRefresh = false
                }
            }
        }
    }

    override fun lazyLoadData() {
        (activity as? HomeCXActivity)?.apply {
            if (isEmptyList) {
                this@RecommendCXFragment.showErrorUi("")
                this@RecommendCXFragment.mBind.smartRefreshLayout.setEnableRefresh(false)
            } else {
                this@RecommendCXFragment.mViewModel.getRecommendApps(false)
            }
        }
    }

    override fun onLoadRetry() {
        (activity as? HomeCXActivity)?.apply {
            if (isEmptyList) {
                loadingVisible(true)
                mViewModel.getHomeList() //首页数据会触发lazyLoadData
            } else {
                isShowRefreshToast = true
                if(App.configurationChanged){
                    LogUtils.e(TAG, "onLoadRetry:: showLoadingUi() configChanged:${App.configurationChanged}")
                    isShowRefreshToast = false
                }

                this@RecommendCXFragment.mViewModel.getRecommendApps(true)
            }
        }
    }

    private fun onBannerItemClick(position: Int) {
        startAppPage(Constants.APPSTORE_RECOMMEND_BANNER, position)
    }

    /**
     * 设置推荐位
     * @param position
     * @param url
     */
    private fun buildRecAdvImageView(position: Int, url: String): ImageFilterView? = context?.run {
        ImageFilterView(this).apply {
            scaleType = ScaleType.CENTER_CROP
            round = resources.getDimensionPixelOffset(R.dimen.recommend_adsense_imageview_round)
                .toFloat()
            load(
                url,
                R.drawable.img_bg_default_large_app_rec_bg,
                R.drawable.img_bg_error_default_large_app_rec_bg
            )
            setCover()
            layoutParams = LayoutParams(
                resources.getDimensionPixelOffset(R.dimen.app_recommend_adsense_width),
                resources.getDimensionPixelOffset(R.dimen.app_recommend_adsense_height),
            ).apply {
                if (position != 0) {
                    setMargins(
                        resources.getDimensionPixelOffset(R.dimen.recommend_adsense_imageview_margin_left),
                        0,
                        0,
                        0
                    )
                }
            }
            setOnClickListener {
                startAppPage(Constants.APPSTORE_RECOMMEND_ADSENSE, position)
            }
        }
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

    private val startActivityRunnable = Runnable {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appRoutePageName))
            val componentName =intent.resolveActivity(appContext.packageManager)
            Log.e(TAG, "uri: ${appRoutePageName}, componentName:$componentName")
            if(componentName != null && componentName.packageName == getPackageNameName(appContext)) {
                EntryActivity.startActivity(activity, intent)
            } else {
                startActivity(intent.apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                })
            }
        } catch (e: Exception) {
            e.logStackTrace()
            ToastUtils.show(activity?:appContext,getString(R.string.recommend_app_page_not_found))
        }
    }

    private fun startAppDetailActivityByVersionId(pointCode: String, position: Int) {
        val appVersionId = mViewModel.getAppVersionId(pointCode, position)
        if (appVersionId != -1L) {
            AppDetailCXActivity.start(activity, appVersionId)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            mBind.bannerView.startLoop()
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mBind.bannerView.stopLoop()
        } catch (e: Exception) {
            e.logStackTrace()
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
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

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
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getLoadingView(): View {
        return mBind.layoutRecommendContent
    }

    override fun onDestroyView() {
        try {
            InstallAppManager.removeInstallStateChangeListener(this)
            handler.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            e.logStackTrace()
        }
        super.onDestroyView()
    }
}

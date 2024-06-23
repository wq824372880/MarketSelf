package com.zeekrlife.market.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.sensorsdata.analytics.android.sdk.util.AppInfoUtils
import com.zeekr.basic.appContext
import com.zeekr.component.tv.button.ZeekrTVButton
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.decoration.builder.XDividerOrientation
import com.zeekrlife.common.widget.state.BaseErrorCallback
import com.zeekrlife.market.R
import com.zeekrlife.market.app.App
import com.zeekrlife.market.app.base.BaseFragment
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.FragmentTvCategoryBinding
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.ui.activity.HomeTVActivity
import com.zeekrlife.market.ui.adapter.diff.AppItemDiffCallback
import com.zeekrlife.market.ui.adapter.CategoryTVAdapter
import com.zeekrlife.market.ui.viewmodel.CategoryViewModel
import com.zeekrlife.market.widget.AppDialog
import com.zeekrlife.market.widget.AppletDialog
import com.zeekrlife.market.widget.verticaltablayout.widget.AbstractTVTabView
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 应用分类
 */
class CategoryTVFragment : BaseFragment<CategoryViewModel, FragmentTvCategoryBinding>(),
    InstallStateChangeListener {

    companion object {
        private const val TAG = "CategoryTVFragment"
        const val REQUEST_APP_LIST_CATEGORY_FRAGMENT = "request_app_list_category_fragment"
    }

    private var categoryPid = 0
    private var categoryName = ""
    private var position = 1
    private var isLoaded = false
    private val categoryTVAdapter: CategoryTVAdapter by lazy {
        CategoryTVAdapter(
            R.layout.item_category_view,
            arrayListOf()
        )
    }
    private var isRefresh: Boolean = false
    private var isShowRefreshToast: Boolean = false  //bugfix 标记toast弹出

    @Volatile
    private var appletDialog: AppletDialog? = null

    private var tvRefreshing = false

    override fun initView(savedInstanceState: Bundle?) {
        categoryTVAdapter.setDiffCallback(AppItemDiffCallback())
    }

    override fun initObserver() {
    }

    /**
     * 懒加载 第一次获取视图的时候 触发
     * 由于纵向tablayout配合Fragment剔除了viewadapter 所以此方法实现懒加载，正常重写 lazyLoadData()即可实现懒加载
     */
    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden && !isLoaded) {
            isLoaded = true
            categoryPid = arguments?.getInt("id") ?: 0
            categoryName = arguments?.getString("tag") ?: ""
            position = arguments?.getInt("position") ?: 1
            LogUtils.e(TAG, "onHiddenChanged:: categoryPid:$categoryPid,categoryName:" +
                    "$categoryName,position:$position,configChanged:${App.configurationChanged}")
            mBind.categoryTitle.text = categoryName
            mBind.smartRefreshLayout.refresh {
                isRefresh = true
                isShowRefreshToast = true
                mViewModel.getCategoryList(categoryPid, isRefresh = isRefresh, loadingXml = false)
            }.loadMore {
                isRefresh = false
                mViewModel.getCategoryList(categoryPid, isRefresh = isRefresh, loadingXml = false)
            }

            mBind.recyclerView.apply {
                grid(2)
                divider2(XDividerOrientation.GRID, blockGrid = {
                    hLineSpacing = R.dimen.tv_26
                    vLineSpacing = R.dimen.tv_100
                    isIncludeEdge = false
                })
                adapter = categoryTVAdapter

                val act = activity
                if (act is HomeTVActivity) {
                    lifecycleScope.launch {
                        delay(300)
                        val tabView = act.mBind.tablayout.getTabAt(position)
                        tabView.nextFocusRightId = categoryTVAdapter.firstItemViewId
                    }
                }
            }
            uiStatusManger.setCallBack(
                BaseErrorCallback::class.java
            ) { _, view ->
                val button = view?.findViewById<ZeekrTVButton>(com.zeekrlife.common.R.id.state_error_text)
                button?.apply {
                    val act = activity
                    var tabView: AbstractTVTabView? = null
                    if (act is HomeTVActivity) {
                        tabView = act.mBind.tablayout.getTabAt(position)
                        tabView.id = View.generateViewId()
                        button.nextFocusLeftId = tabView.id
                        button.nextFocusUpId = tabView.id
                        button.nextFocusDownId = tabView.id
                    }
                    setOnClickListener {
                        tabView?.requestFocus()
                        onLoadRetry()
                    }
                }
            }

            onLoadRetry()
        }
        if (!hidden) {
            registerStartupStateObserver(categoryTVAdapter.data)
        }
    }

    /**
     * 请求成功
     */
    override fun onRequestSuccess() {
        mViewModel.listData.observe(this) {
            tvRefreshing = false
            if (isRefresh) {
                showSuccessUi()
                mBind.categoryLoading.root.gone()
                if (isShowRefreshToast) {
                    ToastUtils.show("刷新成功")
                    isShowRefreshToast = false
                }
            }
            categoryTVAdapter.loadListSuccess(it, mBind.smartRefreshLayout, true)

        }

    }

    /**
     * 请求成功，数据为空
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        tvRefreshing = false
        when (loadStatus.requestCode) {
            REQUEST_APP_LIST_CATEGORY_FRAGMENT -> {
                //列表数据请求失败
                categoryTVAdapter.loadListEmpty(loadStatus, mBind.smartRefreshLayout)
                mBind.categoryLoading.root.gone()
                if (isShowRefreshToast) {
                    ToastUtils.show("刷新成功")
                    isShowRefreshToast = false
                }
            }
        }
        super.onRequestEmpty(loadStatus)
    }


    /**
     * 请求失败
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        tvRefreshing = false
        when (loadStatus.requestCode) {
            REQUEST_APP_LIST_CATEGORY_FRAGMENT -> {
                //列表数据请求失败
                categoryTVAdapter.loadListError(loadStatus, mBind.smartRefreshLayout)
                mBind.categoryLoading.root.gone()
                if (isShowRefreshToast) {
                    ToastUtils.show("网络不佳，请检查网络设置")
                    isShowRefreshToast = false
                }
            }
        }
    }

    /**
     * 错误界面 空界面 点击重试
     */
    override fun onLoadRetry() {
        isRefresh = true
        mViewModel.getCategoryList(categoryPid, isRefresh = isRefresh, loadingXml = true)
        if(App.configurationChanged){
            LogUtils.e(TAG, "onLoadRetry:: showLoadingUi(),categoryName:$categoryName,configChanged:${App.configurationChanged}")
            isShowRefreshToast = false
            mBind.categoryLoading.root.visible()
        }
    }

    override fun onBindViewClick() {
        super.onBindViewClick()

        categoryTVAdapter.setTVItemClickListener { _, position ->

            val item: AppItemInfoBean = categoryTVAdapter.getItem(position)
            if (item.dataType == 1) {
                if (appletDialog == null) {
                    appletDialog = AppletDialog(requireActivity())
                }
                appletDialog?.show(item)

            } else {
//                AppDetailActivity.start(activity, categoryAdapter.getItem(position).id)
                AppDialog().show(requireActivity(), item, viewLifecycleOwner)
            }

        }
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
                    categoryTVAdapter.notifyItemChanged(position)
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            // 处理索引越界异常
            Log.e(TAG, "IndexOutOfBoundsException: ${e.message}")
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: NullPointerException) {
            // 处理空指针异常
            Log.e(TAG, "NullPointerException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
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
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        try {
            InstallAppManager.removeInstallStateChangeListener(this)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            e.logStackTrace()
            Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
        }
        super.onDestroyView()
    }

    override fun getLoadingView(): View {
        return mBind.recyclerView
    }

    /**
     * 安装成功
     * @param packageName String
     */
    override fun onInstallSuccess(packageName: String) {
        super.onInstallSuccess(packageName)
        AppInfoUtils.getAppVersionName(appContext)
        val data = categoryTVAdapter.data
        val installedAppVesionCode =
            ApkUtils.getPackageInfo(appContext, packageName)?.versionCode ?: -1
        if (data.isNotEmpty() && installedAppVesionCode != -1) {
            data.forEachIndexed { index, appItemInfoBean ->
                if (packageName == appItemInfoBean.apkPackageName) {
                    if (installedAppVesionCode < appItemInfoBean.apkVersion!!.toLong()) {
                        appItemInfoBean.taskInfo = null
                        categoryTVAdapter.setData(index, appItemInfoBean)
                    }
                }
            }
        }
    }

    /**
     * 刷新列表
     */
    fun refresh() {
        if (tvRefreshing) return
        tvRefreshing = true
        isRefresh = true
        isShowRefreshToast = true
        mBind.smartRefreshLayout.autoRefresh()
    }
}
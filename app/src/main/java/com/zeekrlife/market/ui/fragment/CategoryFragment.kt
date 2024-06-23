package com.zeekrlife.market.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.sensorsdata.analytics.android.sdk.util.AppInfoUtils
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.decoration.builder.XDividerOrientation
import com.zeekrlife.market.R
import com.zeekrlife.market.app.App
import com.zeekrlife.market.app.aop.SingleClick
import com.zeekrlife.market.app.base.BaseFragment
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.FragmentCategoryBinding
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.ui.activity.AppDetailActivity
import com.zeekrlife.market.ui.adapter.diff.AppItemDiffCallback
import com.zeekrlife.market.ui.adapter.CategoryAdapter
import com.zeekrlife.market.ui.viewmodel.CategoryViewModel
import com.zeekrlife.market.widget.AppletDialog
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 应用分类
 */
class CategoryFragment : BaseFragment<CategoryViewModel, FragmentCategoryBinding>(),
    InstallStateChangeListener {

    private var categoryPid = 0
    private var categoryName = ""
    private var isLoaded = false
    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(
            R.layout.item_category_view,
            arrayListOf()
        )
    }
    private var isRefresh: Boolean = false
    private var isShowRefreshToast: Boolean = false  //bugfix 标记toast弹出

    @Volatile
    private var appletDialog: AppletDialog? = null

    companion object{
        const val TAG = "zzzCategoryFragment"
        const val REQUEST_APP_LIST_CATEGORY_FRAGMENT = "request_app_list_category_fragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        categoryAdapter.setDiffCallback(AppItemDiffCallback())
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
            LogUtils.e(TAG, "onHiddenChanged:: categoryPid:$categoryPid,categoryName:$categoryName,configChanged:${App.configurationChanged}")
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
                    hLineSpacing = R.dimen.category_apps_rv_item_horizontal_space
                    vLineSpacing = R.dimen.category_apps_rv_item_vertical_space
                    isIncludeEdge = false
                })
                adapter = categoryAdapter
            }

            onLoadRetry()
        }
        if (!hidden) {
            registerStartupStateObserver(categoryAdapter.data)
        }
    }

    /**
     * 请求成功
     */
    override fun onRequestSuccess() {
        mViewModel.listData.observe(this) {
            if (isRefresh) {
                showSuccessUi()
                mBind.categoryLoading.root.gone()
                if (isShowRefreshToast) {
                    ToastUtils.show("刷新成功")
                    isShowRefreshToast = false
                }
            }
            categoryAdapter.loadListSuccess(it, mBind.smartRefreshLayout, true)

        }

    }

    /**
     * 请求为空
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            REQUEST_APP_LIST_CATEGORY_FRAGMENT -> {
                //列表数据请求失败
                categoryAdapter.loadListEmpty(loadStatus, mBind.smartRefreshLayout)
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
        when (loadStatus.requestCode) {
            REQUEST_APP_LIST_CATEGORY_FRAGMENT -> {
                //列表数据请求失败
                categoryAdapter.loadListError(loadStatus, mBind.smartRefreshLayout)
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

    /**
     * 绑定点击事件
     */
    override fun onBindViewClick() {
        super.onBindViewClick()

        categoryAdapter.setOnItemClickListener @SingleClick
        { _, _, position ->

            val item: AppItemInfoBean = categoryAdapter.getItem(position)
            if (item.dataType == 1) {
                if (appletDialog == null) {
                    appletDialog = AppletDialog(requireActivity())
                }
                appletDialog?.show(item)

            } else {
                AppDetailActivity.start(activity, categoryAdapter.getItem(position).id)
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
                    categoryAdapter.notifyItemChanged(position)
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
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

    override fun onDestroyView() {
        try {
            InstallAppManager.removeInstallStateChangeListener(this)
        } catch (e: Exception) {
            e.logStackTrace()
        }
        super.onDestroyView()
    }

    /**
     * 获取加载动画的view
     */
    override fun getLoadingView(): View {
        return mBind.recyclerView
    }

    /**
     * 安装成功
     * @param packageName String
     */
    override fun onInstallSuccess(packageName: String) {
        super.onInstallSuccess(packageName)
        LogUtils.e(TAG,"zzzOnInstallSuccess:$packageName")
        AppInfoUtils.getAppVersionName(appContext)
        val data = categoryAdapter.data
        val installedAppVesionCode =
            ApkUtils.getPackageInfo(appContext, packageName)?.versionCode ?: -1
        if (data.isNotEmpty() && installedAppVesionCode != -1) {
            data.forEachIndexed { index, appItemInfoBean ->
                if (packageName == appItemInfoBean.apkPackageName) {
                    if (installedAppVesionCode < appItemInfoBean.apkVersion!!.toLong()) {
                        appItemInfoBean.taskInfo = null
                        categoryAdapter.setData(index, appItemInfoBean)
                    }
                }
            }
        }
    }
}
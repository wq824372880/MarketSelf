package com.zeekrlife.market.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.gyf.immersionbar.ImmersionBar
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCorner
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.common.util.TimeUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.Utils
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.ext.initBack
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.ActivityAppDetailBinding
import com.zeekrlife.market.sensors.SensorsTrack
import com.zeekrlife.market.sensors.trackDownload
import com.zeekrlife.market.ui.adapter.AppDetailImagesAdapter
import com.zeekrlife.market.ui.viewmodel.AppDetailModel
import com.zeekrlife.market.utils.ScreenDensityUtils
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import com.zeekrlife.task.base.constant.TaskState

/**
 * 应用详情页面
 * 支持外部跳转：xc://com.zeekrlife.market/detail?id=
 */
class AppDetailActivity : BaseActivity<AppDetailModel, ActivityAppDetailBinding>() {

    companion object {

        private const val param_type_version_id = 1

        private const val param_type_app_id = 2

        private const val param_type_package_name = 3

        private const val KEY_APP_PARAM_TYPE = "app_param_type"

        private const val KEY_APP_ID = "app_id"

        private const val KEY_APP_VERSION_ID = "app_version_id"

        private const val KEY_APP_PACKAGE_NAME = "app_package_name"

        fun start(activity: Activity?, appVersionId: Long) {
            toStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_version_id)
                putLong(KEY_APP_VERSION_ID, appVersionId)
            })
        }

        fun startByAppId(activity: Activity?, appId: Long) {
            toStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_app_id)
                putLong(KEY_APP_ID, appId)
            })
        }

        fun startByPackageName(activity: Activity?, packageName: String) {
            deepLinkToStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_package_name)
                putString(KEY_APP_PACKAGE_NAME, packageName)
            })
        }
    }

    private val imagesAdapter: AppDetailImagesAdapter by lazy { AppDetailImagesAdapter() }

//    private var unInstallStateDialog: ZeekrDialogCreate.Loading? = null

    //防止外部卸载时被详情页面监听到，再次进入详情页面toast提示问题
    private var isUninstalling = false

    private var unInstallDialogAction : com.zeekr.component.dialog.ZeekrDialogAction? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!onAppExtraInit(intent)) {
            finish()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mToolbar?.initBack { finishCurrentActivity(this) }
//        mToolbar.setLeftTitle(R.string.app_detail_back)

        onLoadRetry()

        mBind.layoutAppDetailImages.rvImage.horizontal().divider {
            setDivider(
                30,
                true
            )
        }.adapter = imagesAdapter

        imagesAdapter.setOnItemClickListener { _, _, position ->
            AppDetailImgPreviewActivity.start(this, position, mViewModel.getPreviewPics().toMutableList())
        }

        //安装、更新按钮深色样式
        mBind.layoutAppDetailInfo.layoutTask.setDarkStyle()

        //卸载按钮
        mBind.layoutAppDetailInfo.btnUninstall.setOnClickListener {
            unInstallDialogAction?.dismiss()
            unInstallDialogAction = com.zeekr.component.dialog.ZeekrDialogCreate(this).show {
                title(getString(R.string.app_detail_uninstall))
                content(
                    String.format(
                        getString(R.string.app_detail_uninstall_dialog_content),
                        mViewModel.appDetail.value?.apkName ?: ""
                    )
                )
                realButton(text = getString(R.string.app_detail_dialog_button_confirm)) { action ->
                    if (mViewModel.appIsAllowInstall.value == false) {
                        ToastUtils.show(
                            this@AppDetailActivity,
                            getString(R.string.app_detail_toast_uninstall_warning)
                        )
                        return@realButton
                    }

                    if (mViewModel.appUnInstall()) {
                        mBind.layoutAppDetailInfo.btnUninstall.isEnabled = false
                        showUninstalling()
                    }
                    action.dismiss()
                }
                ghostButton(text = getString(R.string.app_detail_dialog_button_cancel)) { action ->
                    //取消卸载埋点
                    mViewModel.appDetail.value?.apply {
                        SensorsTrack.onAppCancelUninstall("应用详情", this)
                    }
                    action.dismiss()
                }
            }
        }

        //隐私协议跳转
        mBind.layoutAppDetailInfo.tvPrivacyPolicyValue.setOnClickListener {
            val privacyPolicy = mViewModel.appDetail.value?.privacyPolicy
            if (!privacyPolicy.isNullOrEmpty() && Utils.isHttpUrl(privacyPolicy)) {
                toStartActivity(this, WebActivity::class.java, Bundle().apply {
                    putString(Constants.URL_KEY, privacyPolicy)
                    putString(
                        Constants.URL_KEY_TITLE,
                        getString(R.string.app_detail_privacy_policy)
                    )
                })
            }
        }

        mBind.layoutAppDetailInfo.btnStopDownloading.setOnClickListener {
            mBind.layoutAppDetailInfo.layoutTask.cancelDownloadingTask(mBind.layoutAppDetailInfo.layoutTask.taskInfo.id)
        }
        //监听下载状态，判断是否显示停止按钮
        mBind.layoutAppDetailInfo.layoutTask.setOnTaskChangeListener { taskState, isForceUpdate ->
            when (taskState) {
                TaskState.DOWNLOAD_PROGRESS -> {
                    mBind.layoutAppDetailInfo.layoutBtnUninstall.gone()
                    mBind.layoutAppDetailInfo.btnStopDownloading.gone()
                }
                TaskState.DOWNLOAD_PAUSED -> {
                    mBind.layoutAppDetailInfo.layoutBtnUninstall.gone()
                    mBind.layoutAppDetailInfo.btnStopDownloading.visible()
                }
                TaskState.UPDATABLE -> {
                    mBind.layoutAppDetailInfo.btnStopDownloading.gone()
                    mViewModel.appIsInstall.value?.let {
                        mBind.layoutAppDetailInfo.layoutBtnUninstall.visibility =
                            if (it) View.VISIBLE else View.GONE
                    }
                }
                else -> {
                    mBind.layoutAppDetailInfo.btnStopDownloading.gone()
                }
            }
        }
    }

    private fun showUninstalling() {
        isUninstalling = true
//        unInstallStateDialog?.dismiss()
//        unInstallStateDialog = ZeekrDialogCreate.Loading(this)
//        unInstallStateDialog?.show<ZeekrDialogCreate.Loading> {
//            content(getString(R.string.app_detail_uninstalling))
//            lifecycleOwner(this@AppDetailActivity)
//        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent != null && onAppExtraInit(intent)) {
            onLoadRetry()
        }
    }

    private fun onAppExtraInit(intent: Intent?) : Boolean {
        try {
            val paramType = intent?.getIntExtra(KEY_APP_PARAM_TYPE, -1)
            mViewModel.obtainDetailParamType = paramType ?: -1
            when (paramType) {
                param_type_version_id -> {
                    mViewModel.appVersionId = intent.getLongExtra(KEY_APP_VERSION_ID, -1)
                    return true
                }
                param_type_app_id -> {
                    mViewModel.appId = intent.getLongExtra(KEY_APP_ID, -1)
                    return true
                }
                param_type_package_name -> {
                    mViewModel.appPackageName = intent.getStringExtra(KEY_APP_PACKAGE_NAME) ?: ""
                    return true
                }
            }
        } catch (e: NullPointerException) {
            // 处理空指针异常
            LogUtils.e("info", "NullPointerException: " + e.message)
        } catch (e: IllegalArgumentException) {
            // 处理非法参数异常
            LogUtils.e("info", "IllegalArgumentException: " + e.message)
        } catch (e: Exception) {
            // 处理其他未知异常
            LogUtils.e("info", "Exception: " + e.message)
        }
        return false
    }

    override fun initObserver() {
        //是否安装
        mViewModel.appIsInstall.observe(this) {
            if (
                mBind.layoutAppDetailInfo.layoutTask.taskInfo?.state == null ||
                mBind.layoutAppDetailInfo.layoutTask.taskInfo.state == TaskState.DOWNLOAD_CONNECTED ||
                mBind.layoutAppDetailInfo.layoutTask.taskInfo.state == TaskState.DOWNLOAD_PROGRESS ||
                mBind.layoutAppDetailInfo.layoutTask.taskInfo.state == TaskState.DOWNLOAD_PAUSED
            ) {
                return@observe
            }
            mBind.layoutAppDetailInfo.layoutBtnUninstall.visibility =
                if (it) View.VISIBLE else View.GONE
        }
        //是否可以卸载
        mViewModel.appIsAllowInstall.observe(this) {
            it?.let {
                mBind.layoutAppDetailInfo.btnUninstall.isEnabled = it
                if (it) {
                    mBind.layoutAppDetailInfo.btnUninstallTip.visibility = View.GONE
                    mBind.layoutAppDetailInfo.btnUninstallTip.setOnClickListener(null)
                } else {
                    mBind.layoutAppDetailInfo.btnUninstallTip.visibility = View.VISIBLE
                    mBind.layoutAppDetailInfo.btnUninstallTip.setOnClickListener {
                        if (mViewModel.appIsAllowInstall.value == false) {
                            ToastUtils.show(
                                this@AppDetailActivity,
                                getString(R.string.app_detail_toast_uninstall_warning)
                            )
                        }
                    }
                }
            }
        }
        //卸载结果
        mViewModel.appUnInstallResult.observe(this) {
            mBind.layoutAppDetailInfo.btnUninstall.isEnabled = true
//            unInstallStateDialog?.apply {
//                dismiss()
//                it?.let {
//                    if (it) {
//                        ToastUtils.show(
//                            this@AppDetailActivity,
//                            getString(R.string.app_detail_uninstall_success)
//                        )
//                    } else {
//                        ToastUtils.show(
//                            this@AppDetailActivity,
//                            getString(R.string.app_detail_uninstall_fail)
//                        )
//                    }
//                }
//            }
            it?.let {
                if (isUninstalling) {
                    isUninstalling = false
                    if (it) {
                        ToastUtils.show(
                            this@AppDetailActivity,
                            getString(R.string.app_detail_uninstall_success)
                        )
                    } else {
                        ToastUtils.show(
                            this@AppDetailActivity,
                            getString(R.string.app_detail_uninstall_fail)
                        )
                    }
                }
                mViewModel.appUnInstallResult.value = null
            }
        }
    }

    /**
     * 请求成功
     */
    override fun onRequestSuccess() {
        mViewModel.appDetail.observe(this) {
            if (it == null) {
                showEmptyUi()
            } else {
                appDetailShow(it)
                //应用概览图
                mViewModel.getPreviewPics().let { list ->
                    imagesAdapter.setNewInstance(list.toMutableList())
                }
            }
            mBind.detailLoading.flLoading.gone()
            registerStartupStateObserver(if (it == null) null else listOf(it))
        }
        //安装、下载按钮状态设置
        mViewModel.taskInfo.observe(this) {
            mBind.layoutAppDetailInfo.layoutTask.init(it)
        }
    }

    /**
     * 应用详情展示
     * 应用详情展示
     * 应用详情展示
     */
    private fun appDetailShow(app: AppItemInfoBean) {
        try {
            mToolbar?.setLeftTitle(app.apkName ?: getString(R.string.app_detail_app_name))
            mToolbar?.setCenterTitle(getString(R.string.app_detail_preview))
            mBind.layoutAppDetailInfo.apply {
                ivIcon.loadWithCorner(
                    app.icon ?: "",
                    R.drawable.img_bg_default,
                    R.drawable.img_bg_error,
                    20.dp
                )
                ivIcon.setCover()
                tvAppName.text = app.apkName
                tvAppSlogan.text = app.slogan
                "${app.apkSize}MB".also { layoutAppDetailApkInfo.tvAppSizeValue.text = it }
                layoutAppDetailApkInfo.tvAppVersionNameValue.text =
                    app.apkVersionName.ifEmptySetDef()

                tvAppDetailDesc.text = app.appDesc.ifEmptySetDef()
                val tvAppDetailDescEllipsisCount: Int =
                    tvAppDetailDesc.layout?.getEllipsisCount(tvAppDetailDesc.lineCount - 1) ?: 0
                //ellipsisCount>0说明没有显示全部，存在省略部分。
                ivAppDetailDescCheckAll.visibility = if (tvAppDetailDescEllipsisCount > 0) {
                    layoutAppDetailDesc.setOnClickListener {
                        com.zeekr.component.dialog.ZeekrDialogCreate(this@AppDetailActivity)
                            .show {
                                title(getString(R.string.app_detail_introduce))
                                content(app.appDesc.ifEmptySetDef())
                                mediumSize()
                                lifecycleOwner(this@AppDetailActivity)
                                realButton(text = getStringExt(R.string.common_confirm)) { action ->
                                    action.dismiss()
                                }
                            }
                    }
                    View.VISIBLE
                } else {
                    View.GONE
                }

                tvAppDetailVersionInfo.text = app.updates.ifEmptySetDef()
                val tvAppDetailVersionInfoEllipsisCount: Int =
                    tvAppDetailVersionInfo.layout.getEllipsisCount(tvAppDetailVersionInfo.lineCount - 1)
                ivAppDetailVersionInfoCheckAll.visibility =
                    if (tvAppDetailVersionInfoEllipsisCount > 0) {
                        layoutAppDetailVersionInfo.setOnClickListener {
                            com.zeekr.component.dialog.ZeekrDialogCreate(this@AppDetailActivity)
                                .show {
                                    title(getString(R.string.app_detail_version_info))
                                    mediumSize()
                                    content(app.updates.ifEmptySetDef())
                                    lifecycleOwner(this@AppDetailActivity)
                                    realButton(text = getStringExt(R.string.common_i_see)) { action ->
                                        action.dismiss()
                                    }
                                }
                        }
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                tvDevelopersValue.text = app.appDeveloper.ifEmptySetDef()
                val privacyPolicyColor =
                    if (app.privacyPolicy.isNullOrEmpty() || !Utils.isHttpUrl(app.privacyPolicy)) R.color.theme_main_text_color else R.color.color_F88650
                tvPrivacyPolicyValue.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        privacyPolicyColor
                    )
                )
                tvPrivacyPolicyValue.text = if (Utils.isHttpUrl(
                        app.privacyPolicy ?: ""
                    )
                ) app.privacyPolicy.ifEmptySetDef() else "无"
                layoutAppDetailApkInfo.tvAppUpdateTimeValue.text =
                    (TimeUtils.getTime(
                        app.updateTime?.toLong(),
                        TimeUtils.DATE_FORMAT_DATE
                    )).ifEmptySetDef()
                mBind.layoutAppDetailInfo.layoutTask.trackDownload(app)
            }
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }catch (e: Exception) {
            e.logStackTrace()
        }
    }

    private fun String?.ifEmptySetDef() = if (isNullOrEmpty()) "--" else this

    /**
     * 请求失败
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            NetUrl.APP_DETAIL -> {
                mBind.detailLoading.flLoading.gone()
                showErrorUi("")
                ToastUtils.show(loadStatus.errorMessage)
            }
        }
    }

    override fun showEmptyUi() {
        super.showEmptyUi()
        mToolbar?.setLeftTitle(R.string.app_detail_back)
        mToolbar?.setCenterTitle("")
    }

    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        super.onRequestEmpty(loadStatus)
        mToolbar?.setLeftTitle(R.string.app_detail_app_name)
    }

    override fun onLoadRetry() {
        mBind.detailLoading.flLoading.visible()
        showSuccessUi()
        when (mViewModel.obtainDetailParamType) {
            param_type_version_id -> {
                mViewModel.getAppDetailByVersionId(false)
            }
            param_type_app_id -> {
                mViewModel.getAppDetailByAppId(false)
            }
            param_type_package_name -> {
                mViewModel.getAppDetailByPackageName(false)
            }
        }
    }

    override fun initImmersionBar() {
        ImmersionBar.with(this).navigationBarColor(R.color.theme_main_background_color).init()
        //设置共同沉浸式样式
        if (showToolBar()) {
//            ImmersionBar.with(this).fitsSystemWindows(true).titleBar(mToolbar).init()
        }
    }

    override fun onStop() {
        super.onStop()
        "AppDetailActivity onStop".logE("AppDetailActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        "AppDetailActivity onDestroy".logE("AppDetailActivity")
    }
}

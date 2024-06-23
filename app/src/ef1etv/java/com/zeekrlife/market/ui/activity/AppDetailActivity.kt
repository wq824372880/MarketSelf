package com.zeekrlife.market.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.zeekr.component.tv.dialog.ZeekrTVDialogCreate
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCorner
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.common.util.TimeUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.Utils
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.ActivityAppDetailBinding
import com.zeekrlife.market.sensors.SensorsTrack
import com.zeekrlife.market.ui.adapter.AppDetailImagesTVAdapter
import com.zeekrlife.market.ui.viewmodel.AppDetailModel
import com.zeekrlife.market.widget.initBack
import com.zeekrlife.net.api.NetUrl
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

        /**
         * 此函数是一个Kotlin函数，用于启动一个新的Activity，并向其传递一些参数。
         * activity: Activity?：表示当前的Activity对象，可以为null。
         * appVersionId: Long：表示应用程序的版本ID，是一个长整型数值。
         */
        fun start(activity: Activity?,appVersionId: Long) {
            toStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_version_id)
                putLong(KEY_APP_VERSION_ID, appVersionId)
            })
        }

        /**
         * 该函数是一个Kotlin函数，用于根据应用程序ID启动AppDetailActivity活动。
         * 它接受两个参数：一个Activity对象和一个长整型的应用程序ID。函数内部通过调用toStartActivity方法，
         * 创建一个Bundle对象，将应用程序ID和类型参数放入Bundle中，然后将Bundle作为参数传递给toStartActivity方法，
         * 启动AppDetailActivity。
         */
        fun startByAppId(activity: Activity?, appId: Long) {
            toStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_app_id)
                putLong(KEY_APP_ID, appId)
            })
        }

        /**
         * 该函数用于根据包名启动一个新的Activity。函数接受两个参数，一个是activity，用于启动新的Activity；
         * 另一个是packageName，表示要启动的Activity的包名。函数内部通过创建一个Bundle对象，并向其中放入两个参数，
         * 然后调用toStartActivity方法来启动新的Activity。
         */
        fun startByPackageName(activity: Activity?, packageName: String) {
            toStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_package_name)
                putString(KEY_APP_PACKAGE_NAME, packageName)
            })
        }
    }

    private val imagesAdapter: AppDetailImagesTVAdapter by lazy { AppDetailImagesTVAdapter() }

//    private var unInstallStateDialog: ZeekrDialogCreate.Loading? = null

    //防止外部卸载时被详情页面监听到，再次进入详情页面toast提示问题
    private var isUninstalling = false

    private var isClick = false

    /**
     * 初始化界面
     */
    override fun initView(savedInstanceState: Bundle?) {
        mBind.toolBarTv.requestFocus()
        mBind.toolBarTv.initBack {
            finishCurrentActivity(this)
        }
        mBind.toolBarTv.setLeftTitle(R.string.app_detail_back)

        onAppExtraInit(intent)
        onLoadRetry()

        mBind.layoutAppDetailImages.rvImage.horizontal().divider {
            setDivider(
                30,
                true
            )
        }.adapter = imagesAdapter

        imagesAdapter.setTVItemClickListener { _, position ->
            AppTVDetailImgPreviewActivity.start(this, position, mViewModel.getPreviewPics().toMutableList())
        }

        //安装、更新按钮深色样式
        mBind.layoutAppDetailInfo.layoutTask.setDarkStyle()
        AppDetailImagesTVAdapter.firstButtonId = mBind.layoutAppDetailInfo.layoutTask.id

        //卸载按钮
        mBind.layoutAppDetailInfo.btnUninstall.setTVClickListener(false) {
            ZeekrTVDialogCreate(this).show {
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
                    isClick = true
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
        AppDetailImagesTVAdapter.secondButtonId = mBind.layoutAppDetailInfo.btnUninstall.id

        //隐私协议跳转
        mBind.layoutAppDetailInfo.tvPrivacyPolicyValue.setTVClickListener(false) {
            val privacyPolicy = mViewModel.appDetail.value?.privacyPolicy
            if (!privacyPolicy.isNullOrEmpty() && Utils.isHttpUrl(privacyPolicy)) {
                toStartActivity(WebTVActivity::class.java, Bundle().apply {
                    putString(Constants.URL_KEY, privacyPolicy)
                    putString(
                        Constants.URL_KEY_TITLE,
                        getString(R.string.app_detail_privacy_policy)
                    )
                })
            }
        }

        mBind.layoutAppDetailInfo.btnStopDownloading.setTVClickListener(false) {
            isClick = true
            mBind.layoutAppDetailInfo.layoutTask.cancelDownloadingTask(mBind.layoutAppDetailInfo.layoutTask.taskInfo.id)
        }
        //监听下载状态，判断是否显示停止按钮
        mBind.layoutAppDetailInfo.layoutTask.setOnTaskChangeListener { taskState, isForceUpdate ->
            when (taskState) {
                TaskState.DOWNLOAD_PROGRESS -> {
                    AppDetailImagesTVAdapter.secondButtonIsVisible = false
                    mBind.layoutAppDetailInfo.layoutBtnUninstall.gone()
                    mBind.layoutAppDetailInfo.btnStopDownloading.gone()
                    layoutTaskRequestFocus()
                }
                TaskState.DOWNLOAD_PAUSED -> {
                    AppDetailImagesTVAdapter.secondButtonIsVisible = false
                    mBind.layoutAppDetailInfo.layoutBtnUninstall.gone()
                    mBind.layoutAppDetailInfo.btnStopDownloading.visible()
                }
                TaskState.UPDATABLE -> {
                    mBind.layoutAppDetailInfo.btnStopDownloading.gone()
                    mViewModel.appIsInstall.value?.let {
                        mBind.layoutAppDetailInfo.layoutBtnUninstall.visibility =
                            if (it) View.VISIBLE else View.GONE
                        if (!it) {
                            layoutTaskRequestFocus()
                        }
                        AppDetailImagesTVAdapter.secondButtonIsVisible = it
                    }
                }
                else -> {
                    mBind.layoutAppDetailInfo.btnStopDownloading.gone()
                    layoutTaskRequestFocus()
                }
            }
            isClick = false
        }
    }

    /**
     * 布局任务请求焦点
     * 该函数检查是否发生了点击事件，如果是，则请求布局任务获取焦点。
     */
    private fun layoutTaskRequestFocus() {
        if (isClick) { // 检查是否发生点击事件
            mBind.layoutAppDetailInfo.layoutTask.requestFocus() // 请求布局任务获取焦点
        }
    }

    /**
     * 显示卸载中
     */
    private fun showUninstalling() {
        isUninstalling = true
//        unInstallStateDialog?.dismiss()
//        unInstallStateDialog = ZeekrDialogCreate.Loading(this)
//        unInstallStateDialog?.show<ZeekrDialogCreate.Loading> {
//            content(getString(R.string.app_detail_uninstalling))
//            lifecycleOwner(this@AppDetailActivity)
//        }
    }

    /**
     * 当Activity接收到新的Intent时调用此方法。
     * 对于Activity来说，这个方法会在调用[Activity.onCreate]之后调用，用于处理新的Intent。
     *
     * @param intent 新接收到的Intent，可能为null。
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent) // 调用父类的onNewIntent方法
        // 当intent不为空时，进行额外的初始化操作并尝试重新加载
        if (intent != null) {
            onAppExtraInit(intent) // 对新接收到的Intent进行额外的初始化操作
            onLoadRetry() // 尝试重新加载
        }
    }

    /**
     * 该函数主要功能是在应用初始化时，根据传入的intent获取相应的参数，并更新mViewModel中的相关字段。 具体步骤如下：
     * 尝试从intent中获取paramType参数，若不存在则将其设置为-1。
     * 根据paramType的值，更新mViewModel中相应字段的值：
     * 当paramType为param_type_version_id时，获取并更新appVersionId字段；
     * 当paramType为param_type_app_id时，获取并更新appId字段；
     * 当paramType为param_type_package_name时，获取并更新appPackageName字段。
     * 捕获可能发生的异常，并打印异常栈信息。
     * 注意：该函数为私有函数，只能在当前类中调用。
     */
    private fun onAppExtraInit(intent: Intent?) {
        try {
            val paramType = intent?.getIntExtra(KEY_APP_PARAM_TYPE, -1)
            mViewModel.obtainDetailParamType = paramType ?: -1
            when (paramType) {
                param_type_version_id -> {
                    mViewModel.appVersionId = intent.getLongExtra(KEY_APP_VERSION_ID, -1)
                }
                param_type_app_id -> {
                    mViewModel.appId = intent.getLongExtra(KEY_APP_ID, -1)
                }
                param_type_package_name -> {
                    mViewModel.appPackageName = intent.getStringExtra(KEY_APP_PACKAGE_NAME) ?: ""
                }
            }
        } catch (e: NullPointerException) {
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 初始化观察者
     */
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
            if (!it) {
                layoutTaskRequestFocus()
            }
            AppDetailImagesTVAdapter.secondButtonIsVisible = it
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
     * 该函数用于展示应用的详细信息。
     * 设置应用名称和预览标签。
     * 设置应用图标、应用名称、应用标语、应用大小、应用版本号、应用描述、更新信息和开发者信息。
     * 如果应用描述或更新信息存在省略部分，则点击可展开查看完整信息。
     * 设置隐私政策文本和颜色，如果是HTTP链接则点击可跳转。
     * 展示应用的更新时间。
     * 捕获可能的异常并记录堆栈跟踪。
     */
    private fun appDetailShow(app: AppItemInfoBean) {
        try {
            mBind.toolBarTv.setLeftTitle(app.apkName ?: getString(R.string.app_detail_app_name))
            mBind.toolBarTv.setCenterTitle(getString(R.string.app_detail_preview))
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
                tvAppDetailDescCheckAll.visibility = if (tvAppDetailDescEllipsisCount > 0) {
                    tvAppDetailDescCheckAll.setTVClickListener(false) {
                        ZeekrTVDialogCreate(this@AppDetailActivity)
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
                tvAppDetailVersionInfoCheckAll.visibility =
                    if (tvAppDetailVersionInfoEllipsisCount > 0) {
                        tvAppDetailVersionInfoCheckAll.setTVClickListener(false) {
                            ZeekrTVDialogCreate(this@AppDetailActivity)
                                .show {
                                    title(getString(R.string.app_detail_version_info))
                                    content(app.updates.ifEmptySetDef())
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

    /**
     * 如果字符串为空，则返回默认值"--"
     * @return String
     */
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

    /**
     * 这个函数是Kotlin编写的，用于显示空的用户界面。它首先调用了超类的showEmptyUi()方法，
     * 然后设置了工具栏的左标题为"返回"，中心标题为空字符串。
     */
    override fun showEmptyUi() {
        super.showEmptyUi()
        mBind.toolBarTv.setLeftTitle(R.string.app_detail_back)
        mBind.toolBarTv.setCenterTitle("")
    }

    /**
     * 当请求为空时，显示成功的用户界面。
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        super.onRequestEmpty(loadStatus)
        mBind.toolBarTv.setLeftTitle(R.string.app_detail_app_name)
    }

    /**
     * 当请求重试时，显示加载中的用户界面。
     */
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

    /**
     * 显示工具栏
     * @return Boolean
     */
    override fun showToolBar(): Boolean {
        return false
    }
}

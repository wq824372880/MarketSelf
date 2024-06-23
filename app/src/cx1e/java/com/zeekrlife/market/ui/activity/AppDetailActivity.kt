package com.zeekrlife.market.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.gyf.immersionbar.ImmersionBar
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

        /**
         * 启动App详情页面的函数。
         *
         * @param activity 当前的Activity实例，可以为null。用于启动新的Activity。
         * @param appVersionId 应用版本ID，长整型。用于在新启动的Activity中展示特定版本的详情。
         */
        fun start(activity: Activity?, appVersionId: Long) {
            // 使用提供的activity启动AppDetailActivity，并通过bundle传递参数
            toStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_version_id)
                putLong(KEY_APP_VERSION_ID, appVersionId)
            })
        }

        /**
         * 根据应用ID启动App详情页面。
         *
         * @param activity 当前活动，可以为null。如果非null，将在此活动上下文中启动新活动。
         * @param appId 需要启动的应用的ID，类型为Long。
         */
        fun startByAppId(activity: Activity?, appId: Long) {
            // 准备启动App详情页的意图并设置必要参数
            toStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_app_id)
                putLong(KEY_APP_ID, appId)
            })
        }

        /**
         * 根据包名启动应用的特定Activity。
         *
         * @param activity 当前的Activity实例，用于启动新的Activity。
         * @param packageName 需要启动的应用的包名。
         * 该函数不返回任何内容。
         */
        fun startByPackageName(activity: Activity?, packageName: String) {
            // 使用深度链接方式启动AppDetailActivity，并传递包名和其他参数
            deepLinkToStartActivity(activity, AppDetailActivity::class.java, Bundle().apply {
                putInt(KEY_APP_PARAM_TYPE, param_type_package_name)
                putString(KEY_APP_PACKAGE_NAME, packageName)
            })
        }
    }

    private val imagesAdapter: AppDetailImagesAdapter by lazy { AppDetailImagesAdapter() }

    //防止外部卸载时被详情页面监听到，再次进入详情页面toast提示问题
    private var isUninstalling = false

    private var unInstallDialogAction: com.zeekr.component.dialog.ZeekrDialogAction? = null

    /**
     * 当Activity创建时被调用。
     *
     * @param savedInstanceState 如果Activity被系统重新创建，这个参数包含了之前Activity结束时的状态。否则是null。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "AppDetailActivity onCreate".logE("AppDetailActivity")
        if (!onAppExtraInit(intent)) {
            finish()
        }
    }

    /**
     * 初始化视图的函数。
     * 这个函数会在组件创建时被调用，用于初始化界面的视图组件和设置初始状态。
     *
     * @param savedInstanceState 如果Activity或Fragment被系统重新创建，这个参数会包含之前保存的状态信息。可以用于恢复之前的状态。
     */
    override fun initView(savedInstanceState: Bundle?) {
        // 在这里进行视图的初始化和状态恢复工作。
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
            AppDetailImgPreviewActivity.start(
                this,
                position,
                mViewModel.getPreviewPics().toMutableList()
            )
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

    /**
     * 当Activity接收到新的Intent时调用此方法。
     * 对传入的Intent进行处理，并在特定条件下触发加载重试的逻辑。
     *
     * @param intent 新接收到的Intent，可能为null。
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && onAppExtraInit(intent)) {
            onLoadRetry()
        }
    }

    /**
     * 在应用初始化时，根据传入的Intent处理额外的初始化逻辑。
     *
     * @param intent 启动应用时传入的Intent，可能包含应用参数类型、版本ID、应用ID或应用包名等信息。
     * @return Boolean 如果成功处理了Intent中的信息并获取了相应的参数，则返回true；否则返回false。
     */
    private fun onAppExtraInit(intent: Intent?): Boolean {
        try {
            // 尝试从Intent中获取应用参数类型，并设置到视图模型中
            val paramType = intent?.getIntExtra(KEY_APP_PARAM_TYPE, -1)
            mViewModel.obtainDetailParamType = paramType ?: -1

            // 根据参数类型，从Intent中获取相应的应用信息，并保存到视图模型中
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
            // 处理可能的空指针异常
            LogUtils.e("info", "NullPointerException: " + e.message)
        } catch (e: IllegalArgumentException) {
            // 处理可能的非法参数异常
            LogUtils.e("info", "IllegalArgumentException: " + e.message)
        } catch (e: Exception) {
            // 处理其他未知异常
            LogUtils.e("info", "Exception: " + e.message)
        }
        // 如果没有成功处理Intent中的信息，则返回false
        return false
    }

    /**
     * 初始化观察者模式，用于处理界面与数据的实时更新。
     */
    override fun initObserver() {
        // 观察应用是否已安装的状态
        mViewModel.appIsInstall.observe(this) {
            // 在特定任务状态下，不进行界面更新
            if (
                mBind.layoutAppDetailInfo.layoutTask.taskInfo?.state == null ||
                mBind.layoutAppDetailInfo.layoutTask.taskInfo.state == TaskState.DOWNLOAD_CONNECTED ||
                mBind.layoutAppDetailInfo.layoutTask.taskInfo.state == TaskState.DOWNLOAD_PROGRESS ||
                mBind.layoutAppDetailInfo.layoutTask.taskInfo.state == TaskState.DOWNLOAD_PAUSED
            ) {
                return@observe
            }
            // 根据应用是否安装，更新卸载按钮的可见性
            mBind.layoutAppDetailInfo.layoutBtnUninstall.visibility =
                if (it) View.VISIBLE else View.GONE
        }
        // 观察应用是否允许卸载的状态
        mViewModel.appIsAllowInstall.observe(this) {
            it?.let {
                // 根据应用是否允许卸载，更新卸载按钮的启用状态和提示信息的可见性
                mBind.layoutAppDetailInfo.btnUninstall.isEnabled = it
                if (it) {
                    mBind.layoutAppDetailInfo.btnUninstallTip.visibility = View.GONE
                    mBind.layoutAppDetailInfo.btnUninstallTip.setOnClickListener(null)
                } else {
                    mBind.layoutAppDetailInfo.btnUninstallTip.visibility = View.VISIBLE
                    // 点击提示信息时，显示卸载警告信息
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
        // 观察卸载操作的结果
        mViewModel.appUnInstallResult.observe(this) {
            // 卸载完成后，重置卸载按钮状态
            mBind.layoutAppDetailInfo.btnUninstall.isEnabled = true
            // 根据卸载结果，显示相应的提示信息
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
                // 重置卸载结果状态
                mViewModel.appUnInstallResult.value = null
            }
        }
    }

    /**
     * 请求成功时的处理逻辑。
     * 该函数没有参数和返回值。
     */
    override fun onRequestSuccess() {
        // 观察应用详情的变化
        mViewModel.appDetail.observe(this) {
            if (it == null) {
                // 如果应用详情为空，则展示空的UI
                showEmptyUi()
            } else {
                // 展示应用详情
                appDetailShow(it)
                // 获取并设置应用概览图片
                mViewModel.getPreviewPics().let { list ->
                    imagesAdapter.setNewInstance(list.toMutableList())
                }
            }
            // 隐藏加载中的动画
            mBind.detailLoading.flLoading.gone()
            // 根据应用详情注册启动状态观察者
            registerStartupStateObserver(if (it == null) null else listOf(it))
        }
        // 观察安装/下载任务状态，用于更新按钮状态
        mViewModel.taskInfo.observe(this) {
            // 初始化安装/下载按钮状态
            mBind.layoutAppDetailInfo.layoutTask.init(it)
        }
    }

    /**
     * 显示应用的详细信息
     * @param app AppItemInfoBean对象，包含应用的各种信息
     */
    private fun appDetailShow(app: AppItemInfoBean) {
        try {
            // 设置工具栏的左标题和中心标题
            mToolbar?.setLeftTitle(app.apkName ?: getString(R.string.app_detail_app_name))
            mToolbar?.setCenterTitle(getString(R.string.app_detail_preview))

            // 初始化应用详细信息布局中的组件和显示内容
            mBind.layoutAppDetailInfo.apply {
                // 加载应用图标，并设置圆角
                ivIcon.loadWithCorner(
                    app.icon ?: "",
                    R.drawable.img_bg_default,
                    R.drawable.img_bg_error,
                    20.dp
                )
                ivIcon.setCover()
                // 设置应用名称和标语
                tvAppName.text = app.apkName
                tvAppSlogan.text = app.slogan
                // 设置应用大小
                "${app.apkSize}MB".also { layoutAppDetailApkInfo.tvAppSizeValue.text = it }
                // 设置应用版本号
                layoutAppDetailApkInfo.tvAppVersionNameValue.text =
                    app.apkVersionName.ifEmptySetDef()

                // 设置应用描述，如果描述内容过多，点击可展开查看完整描述
                tvAppDetailDesc.text = app.appDesc.ifEmptySetDef()
                val tvAppDetailDescEllipsisCount: Int =
                    tvAppDetailDesc.layout?.getEllipsisCount(tvAppDetailDesc.lineCount - 1) ?: 0
                //ellipsisCount>0说明没有显示全部，存在省略部分。
                ivAppDetailDescCheckAll.visibility = if (tvAppDetailDescEllipsisCount > 0) {
                    // 如果存在省略部分，则设置展开按钮可见，并设置点击事件
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

                // 设置更新信息，如果信息过多，点击可展开查看完整信息
                tvAppDetailVersionInfo.text = app.updates.ifEmptySetDef()

                var tvAppDetailVersionInfoEllipsisCount: Int = 0
                if(tvAppDetailVersionInfo.layout != null){
                    tvAppDetailVersionInfoEllipsisCount =
                        tvAppDetailVersionInfo.layout.getEllipsisCount(tvAppDetailVersionInfo.lineCount - 1)
                }

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

                // 设置开发者信息
                tvDevelopersValue.text = app.appDeveloper.ifEmptySetDef()

                // 设置隐私政策信息，如果是有效的HTTP链接则显示链接，否则显示“无”
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

                // 设置应用更新时间
                layoutAppDetailApkInfo.tvAppUpdateTimeValue.text =
                    (TimeUtils.getTime(
                        app.updateTime?.toLong(),
                        TimeUtils.DATE_FORMAT_DATE
                    )).ifEmptySetDef()

                // 监听应用的下载进度
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
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 如果字符串为空或空字符串，则返回"--"，否则返回原字符串。
     *
     * @param this 字符串参数，可以是null。
     * @return 如果输入字符串为空或空字符串，则返回"--"，否则返回原字符串。
     */
    private fun String?.ifEmptySetDef() = if (isNullOrEmpty()) "--" else this

    /**
     * 当请求错误发生时被调用。
     *
     * @param loadStatus 包含请求信息和错误信息的实体。
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        // 根据请求代码处理不同的错误情况
        when (loadStatus.requestCode) {
            NetUrl.APP_DETAIL -> {
                // 如果请求的是应用详情页，隐藏加载动画，显示错误界面，并显示错误信息
                mBind.detailLoading.flLoading.gone()
                showErrorUi("")
                ToastUtils.show(loadStatus.errorMessage)
            }
        }
    }

    /**
     * 显示空的用户界面。
     * 该函数没有参数。
     * 该函数没有返回值。
     */
    override fun showEmptyUi() {
        super.showEmptyUi()
        mToolbar?.setLeftTitle(R.string.app_detail_back)
        mToolbar?.setCenterTitle("")
    }

    /**
     * 当请求为空时调用此函数。
     * 对于加载状态为空的情况进行处理，通常会进行一些UI的更新或显示无数据的提示。
     *
     * @param loadStatus 表示加载状态的实体，包含了加载成功、失败或空等各种状态的信息。
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        super.onRequestEmpty(loadStatus)
        mToolbar?.setLeftTitle(R.string.app_detail_app_name)
    }

    /**
     * 当加载失败时尝试重新加载数据。
     * 此函数没有参数和返回值，因为它是一个重载函数，主要通过调用不同方法来获取应用详情，
     * 根据的是mViewModel中obtainDetailParamType的值决定是通过版本ID、应用ID还是包名来获取详情。
     */
    override fun onLoadRetry() {
        // 显示加载中的UI
        mBind.detailLoading.flLoading.visible()
        // 显示成功UI的通用操作，此处具体操作未给出，可能包括隐藏错误信息、准备数据等
        showSuccessUi()

        // 根据获取详情的参数类型，调用相应的获取应用详情的方法
        when (mViewModel.obtainDetailParamType) {
            param_type_version_id -> {
                // 通过版本ID获取应用详情
                mViewModel.getAppDetailByVersionId(false)
            }

            param_type_app_id -> {
                // 通过应用ID获取应用详情
                mViewModel.getAppDetailByAppId(false)
            }

            param_type_package_name -> {
                // 通过包名获取应用详情
                mViewModel.getAppDetailByPackageName(false)
            }
        }
    }

    /**
     * 初始化沉浸式状态栏
     * 本函数没有参数和返回值，因为它是一个初始化函数，主要作用是配置界面的沉浸式效果。
     * 根据条件选择是否启用工具栏，并设置状态栏和导航栏的颜色。
     */
    override fun initImmersionBar() {
        // 设置导航栏颜色，并初始化
        ImmersionBar.with(this).navigationBarColor(R.color.theme_main_background_color).init()
        // 判断是否需要显示工具栏，若需要则配置沉浸式风格
        if (showToolBar()) {
            // 配置为适应系统窗口，并将工具栏设为标题栏
            // 注：此行代码被注释，表示当前不启用此配置
            // ImmersionBar.with(this).fitsSystemWindows(true).titleBar(mToolbar).init()
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

package com.zeekrlife.market.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.zeekr.component.dialog.ZeekrDialogAction
import com.zeekr.component.dialog.ZeekrDialogCreate
import com.zeekr.component.dialog.common.DialogParam
import com.zeekr.zui_common.ktx.clickWithTrigger
import com.zeekr.zui_common.ktx.getServiceInflate
import com.zeekrlife.common.ext.code
import com.zeekrlife.common.ext.dp
import com.zeekrlife.common.ext.getStringExt
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.msg
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseFragment
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.entity.AppUpdateState
import com.zeekrlife.market.data.entity.AppUpdateState.Status
import com.zeekrlife.market.databinding.ActivitySettingsBinding
import com.zeekrlife.market.databinding.CustomCheckLoadingLayoutBinding
import com.zeekrlife.market.databinding.CustomCheckUpdateLayoutBinding
import com.zeekrlife.market.ui.activity.HomeActivity
import com.zeekrlife.market.ui.viewmodel.SettingViewModel
import com.zeekrlife.market.widget.WebViewDialog
import com.zeekrlife.net.BaseNetConstant.EMPTY_CODE
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.load.LoadStatusEntity
import com.zeekrlife.task.base.util.TaskUtils
import com.zeekrlife.task.base.widget.CircleProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * @author mac
 * 公共组件库不同，分开写
 */
class SettingFragment : BaseFragment<SettingViewModel, ActivitySettingsBinding>() {

    private var updateAction: ZeekrDialogAction? = null
    private var downLoadingAction: ZeekrDialogAction? = null
    private var loadingDialog: ZeekrDialogAction? = null

    private var userAgreementDialog: WebViewDialog? = null
    private var privacyAgreementDialog: WebViewDialog? = null

    private fun ViewGroup.inflateLoadingDialogLayout() =
        CustomCheckUpdateLayoutBinding.inflate(context.getServiceInflate(), this, true)

    private fun ViewGroup.inflateCheckLoadingDialogLayout() =
        CustomCheckLoadingLayoutBinding.inflate(context.getServiceInflate(), this, true)

    private var downloadBinding: CustomCheckUpdateLayoutBinding? = null

    @Volatile
    private var actionOutSide = false  //记录是否转后台更新，点击过下次查询更新 直接显示进度
    private var updateStateValue = 0
    private var checkAction = false //主动发起检查更新,规避黑夜模式被动弹框

    companion object{
        const val checkUpdateRequestCode = "check_update_request_code" //此种请求表示主动发起检查更新，不包含初始化mViewModel.checkUpdate(true)
    }

    override fun initView(savedInstanceState: Bundle?) {
        userAgreementDialog = WebViewDialog(requireContext(), viewLifecycleOwner)
        privacyAgreementDialog = WebViewDialog(requireContext(), viewLifecycleOwner)

        mBind.settingLayoutAutoUpdate.itemSwitch.apply {
            setSwitchChecked(mViewModel.isAutoUpdate())
            contentDescription = getStringExt(R.string.setting_auto_update)
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }

        mBind.settingLayoutAutoUpdate.apply {

            ivSettingArrow.visibility = View.GONE
            tvItemSubtitleText.visibility = View.GONE
            itemSwitchContainer.visibility = View.VISIBLE
            tvItemTitleText.visibility = View.GONE
            itemSwitch.setZeekrSwitchListener { checked ->
                mViewModel.updateAutoUpdateState(checked)
            }
        }

        mBind.settingLayoutVersion.apply {
            tvItemTitleText.text = getString(R.string.setting_version_number)
            tvItemSubtitleText.text = mViewModel.getAppVersion()
            ivSettingArrow.visibility = View.GONE
        }

        mBind.settingLayoutCheckUpdate.apply {
            tvItemTitleText.text = getString(R.string.setting_check_update)
            settingItem.setOnClickListener {
                checkAction = true
                updateStateValue = mViewModel.appUpdateState.value?.state ?: Status.INVALID
                if (updateStateValue > Status.CHECK_UPDATE && updateStateValue <= Status.DOWNLOAD_PROGRESS) {
                    showDownLoadingDialog()
                    return@setOnClickListener
                }

                showLoadingDialog()
                lifecycleScope.launch {
                    if (mViewModel.isUpdate.value == true) {
                        delay(2500)
                        loadingDialog?.dismiss()
                        if (updateStateValue > Status.CHECK_UPDATE && updateStateValue <= Status.DOWNLOAD_ERROR) {
                            showUpdateDialog(isUpdate = true, breadPoint = true)
                        } else {
                            showUpdateDialog(isUpdate = true, breadPoint = false)
                        }
                    } else {
                        delay(2500)
                        loadingDialog?.dismiss()
                        mViewModel.checkUpdate(false, getString(R.string.setting_detecting_update))
                    }
                }
            }
        }

        mBind.settingLayoutUserAgreement.apply {
            tvItemTitleText.text = getString(R.string.setting_user_agreement)
            settingItem.clickWithTrigger(1000) {
                val info = CacheExt.getUserAgreement()?.h5Url ?: ""
                userAgreementDialog?.show(getStringExt(R.string.launcher_dialog_title_ua))
            }
        }

        mBind.settingLayoutPrivacyAgreement.apply {
            tvItemTitleText.text = getString(R.string.setting_privacy_agreement)

            settingItem.clickWithTrigger(1000) {
                val info = CacheExt.getProtocol()?.h5Url ?: ""
                privacyAgreementDialog?.show(getStringExt(R.string.launcher_dialog_title_pp))
            }
        }
    }

    private fun showUpdateRedDotTip() {
        context?.let { _ ->
            mBind.settingLayoutCheckUpdate.tvItemTitleRightIcon.visibility = View.VISIBLE
            mBind.settingLayoutCheckUpdate.tvItemTitleRightIcon.setBackgroundResource(R.drawable.shape_setting_update_tip)
        }
    }

    private fun dismissUpdateRedDotTip() {
        mBind.settingLayoutCheckUpdate.tvItemTitleRightIcon.visibility = View.INVISIBLE
    }

    @SuppressLint("LogNotTimber")
    override fun initObserver() {
        mViewModel.isUpdate.observe(viewLifecycleOwner) {
            if (it) {
                showUpdateRedDotTip()
            } else {
                dismissUpdateRedDotTip()

            }
            showHomeSettingBadge(it)
        }

        mViewModel.appUpdateState.observe(viewLifecycleOwner) {
            try {
                it?.let {
                    when (it.state) {
                        Status.CHECK_UPDATE -> {
                            if (updateStateValue > Status.CHECK_UPDATE && updateStateValue <= Status.INSTALLING) {
                                showUpdateDialog(it.update, true, it.throwable)
                            } else {
                                showUpdateDialog(it.update, false, it.throwable)
                            }
                        }
                        Status.SHOW_DOWNLOADING -> {
                            showDownLoadingDialog()
                        }
                        Status.DOWNLOAD_PROGRESS -> {
                            it.taskId?.takeIf { taskId ->
                                taskId.contentEquals(
                                    TaskUtils.getTaskId(
                                        context,
                                        mViewModel.appInfo?.taskInfo
                                    )
                                )
                            }?.apply {
                                val db = DecimalFormat("0.0")
                                if ((it.totalBytes ?: 0) > 0) {
                                    var soFar = db.format((it.soFarBytes ?: 0) / (1024 * 1024))
                                    var total = db.format((it.totalBytes ?: 0) / (1024 * 1024))
                                    downloadBinding?.tvDownload?.text = "${soFar}MB/${total}MB"
                                }
                                downloadBinding?.downloadProgressBar?.status =
                                    CircleProgressBar.Status.Update
                                downloadBinding?.downloadProgressBar?.progress = it.progress
                            }

                        }
                        Status.INSTALLING -> {
                            downloadBinding?.tvDownload?.text =
                                getString(R.string.setting_app_update_installing)

                        }
                        Status.INSTALL_COMPLETED -> {
                            downLoadingAction?.dismiss()
                        }
                        Status.DOWNLOAD_ERROR, Status.INSTALL_ERROR -> {
                            downLoadingAction?.dismiss()
                            ToastUtils.show(getString(R.string.setting_app_update_failed_please_try_again))
                        }
                        Status.ERROR_NO_SPACE_ENOUGH -> {
                            ToastUtils.show(getString(R.string.app_install_no_space_enough))
                        }
                        else -> {}
                    }
                }
            } catch (e: IllegalStateException) {
                // 处理状态异常
                Log.e("DownloadStatusHandler", "IllegalStateException: ${e.message}")
            } catch (e: NullPointerException) {
                // 处理空指针异常
                Log.e("DownloadStatusHandler", "NullPointerException: ${e.message}")
            } catch (e: IllegalArgumentException) {
                // 处理参数异常
                Log.e("DownloadStatusHandler", "IllegalArgumentException: ${e.message}")
            } catch (e: Exception) {
                // 处理其他未知异常
                Log.e("DownloadStatusHandler", "Exception: ${e.message}")
            }
        }
    }

    /**
     * 显示主页设置角标
     * 显示主页设置角标
     * 显示主页设置角标
     */
    @SuppressLint("LogNotTimber")
    private fun showHomeSettingBadge(show: Boolean) {
        try {
            val act = activity
            if (act is HomeActivity && act.mBind.tablayoutBottom.getTabAt(1) != null) {
                act.mBind.tablayoutBottom.getTabAt(1).badgeView.badgeText = if (show) "" else null
            }
        } catch (e: NullPointerException) {
            // 处理空指针异常
            Log.e("BadgeViewHandler", "NullPointerException: ${e.message}")
        } catch (e: ClassCastException) {
            // 处理类转换异常
            Log.e("BadgeViewHandler", "ClassCastException: ${e.message}")
        } catch (e: IndexOutOfBoundsException) {
            // 处理索引越界异常
            Log.e("BadgeViewHandler", "IndexOutOfBoundsException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他未知异常
            Log.e("BadgeViewHandler", "Exception: ${e.message}")
        }
    }

    /**
     * 检查loading
     */
    private fun showLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = ZeekrDialogCreate(requireContext()).show {
            lifecycleOwner(this@SettingFragment)
            title("检查更新")
            mergeLayout {
                it.inflateCheckLoadingDialogLayout().apply {
                    dialogParam(
                        DialogParam(
                            isDismissOnBackPressed = true,
                            isDismissOnTouchOutside = true
                        )
                    )
                }
            }
            realButton(text = getString(R.string.common_cancel)) { action ->
                checkAction = false
                action.dismiss()
            }

        }
    }

    /**
     * 展示更新弹窗
     */
    private fun showUpdateDialog(isUpdate: Boolean, breadPoint: Boolean, t: Throwable? = null) {
        if (isUpdate) {
            updateAction?.dismiss()
            updateAction = null
            downLoadingAction?.dismiss()
            downLoadingAction = null
            downloadBinding = null
            loadingDialog?.dismiss()

            updateAction = ZeekrDialogCreate(requireContext()).show {
                lifecycleOwner(viewLifecycleOwner)
                title(
                    if (breadPoint) getString(R.string.setting_app_update_break_point_title) else getString(
                        R.string.setting_app_update_new_version
                    )
                )
                content(if (breadPoint) getString(R.string.setting_app_update_break_point_content) else "${mViewModel.appInfo?.updates}")
                contentMargin(28.dp, 28.dp)
                dialogParam(
                    DialogParam(
                        isDismissOnBackPressed = false,
                        isDismissOnTouchOutside = false
                    )
                )
                realButton(
                    text = if (breadPoint) getString(R.string.setting_app_update_continue) else getString(
                        R.string.setting_app_update_now
                    )
                ) {
                    mViewModel.startUpdate()
                    it.dismiss()
                }
                ghostButton(text = "稍后再说") {
                    if (mViewModel.isUpdate.value != true) {
                        mViewModel.isUpdate.postValue(true)
                    }
                    it.dismiss()
                }
            }
        } else if (checkAction && (t == null || t.code.toString() == EMPTY_CODE)) {
            if (mViewModel.isUpdate.value == true) {
                mViewModel.isUpdate.postValue(false)
            }
            mViewModel.appUpdateState.postValue(AppUpdateState())
            Handler(Looper.getMainLooper()).postDelayed({
                loadingDialog?.dismiss()
                updateAction?.dismiss()
                ToastUtils.show(getString(R.string.setting_app_is_latest_version))
            }, 2000)
        } else if(checkAction && t != null && t.code == -1) {
            ToastUtils.show(t.msg)
            loadingDialog?.dismiss()
        }
        checkAction = false
    }

    /**
     * @downLoading 下载
     */
    private fun showDownLoadingDialog() {
        updateAction?.dismiss()
        updateAction = null
        downLoadingAction?.dismiss()
        downLoadingAction = null
        downloadBinding = null

        downLoadingAction = ZeekrDialogCreate(requireContext()).show {
            lifecycleOwner(this@SettingFragment)
            title("正在更新")
            mergeLayout {
                downloadBinding = it.inflateLoadingDialogLayout().apply {
                    dialogParam(
                        DialogParam(
                            isDismissOnBackPressed = false,
                            isDismissOnTouchOutside = false
                        )
                    )
                    downloadProgressBar.status = CircleProgressBar.Status.Update
                }
            }
            realButton(text = "后台更新") { action ->
                action.dismiss()
            }

        }
    }

    override fun lazyLoadData() {
        mViewModel.checkUpdate(true)
    }

    /**
     * 请求成功
     */
    override fun onRequestSuccess() {}

    /**
     * 请求失败
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            NetUrl.APP_LIST -> {
                ToastUtils.show(loadStatus.errorMessage)
            }
        }
    }

    /**
     * 请求数据为空
     * 请求数据为空
     * 请求数据为空
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            checkUpdateRequestCode -> {
                if (!isHidden) {
                    loadingDialog?.dismiss()
                    updateAction?.dismiss()
                    ToastUtils.show(
                        requireContext(),
                        getString(R.string.setting_app_is_latest_version)
                    )
                }
            }
        }
    }
}
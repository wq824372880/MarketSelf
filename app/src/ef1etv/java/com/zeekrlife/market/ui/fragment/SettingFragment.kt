package com.zeekrlife.market.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.zeekr.component.tv.dialog.ZeekrTVDialogAction
import com.zeekr.component.tv.dialog.ZeekrTVDialogCreate
import com.zeekr.component.tv.dialog.common.DialogTVParam
import com.zeekr.zui_common.tv.ktx.getServiceInflate
import com.zeekrlife.common.ext.dp
import com.zeekrlife.common.ext.getStringExt
import com.zeekrlife.common.ext.setTVClickListener
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseFragment
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.entity.AppUpdateState
import com.zeekrlife.market.data.entity.AppUpdateState.Status
import com.zeekrlife.market.databinding.ActivitySettingsBinding
import com.zeekrlife.market.databinding.CustomCheckLoadingLayoutBinding
import com.zeekrlife.market.databinding.CustomCheckUpdateLayoutBinding
import com.zeekrlife.market.ui.activity.HomeTVActivity
import com.zeekrlife.market.ui.viewmodel.SettingViewModel
import com.zeekrlife.market.widget.WebViewDialog
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


    private var updateAction: ZeekrTVDialogAction? = null
    private var downLoadingAction: ZeekrTVDialogAction? = null
    private var loadingDialog: ZeekrTVDialogAction? = null
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
        const val checkUpdateRequestCode = "check_update_request_code"
    }

    override fun initView(savedInstanceState: Bundle?) {
        val act = activity
        if (act is HomeTVActivity) {
            lifecycleScope.launch {
                delay(300)
                val tabView = act.mBind.tablayoutBottom.getTabAt(1)
                tabView?.also {
                    it.id = View.generateViewId()
                    mBind.settingLayoutAutoUpdate.settingItem.nextFocusLeftId = it.id
                    mBind.settingLayoutVersion.settingItem.nextFocusLeftId = it.id
                    mBind.settingLayoutCheckUpdate.settingItem.nextFocusLeftId = it.id
                    mBind.settingLayoutUserAgreement.settingItem.nextFocusLeftId = it.id
                    mBind.settingLayoutPrivacyAgreement.settingItem.nextFocusLeftId = it.id

                    mBind.settingLayoutAutoUpdate.settingItem.nextFocusUpId = it.id
                    mBind.settingLayoutPrivacyAgreement.settingItem.nextFocusDownId = it.id
                }
            }
        }

        mBind.settingLayoutAutoUpdate.itemSwitch.isChecked = mViewModel.isAutoUpdate()
        mBind.settingLayoutAutoUpdate.apply {

            ivSettingArrow.visibility = View.GONE
            tvItemSubtitleText.visibility = View.GONE
            itemSwitchContainer.visibility = View.VISIBLE
            tvItemTitleText.visibility = View.GONE
            settingItem.setTVClickListener(needChangeAlpha = false) {
                val checked = !mBind.settingLayoutAutoUpdate.itemSwitch.isChecked
                mBind.settingLayoutAutoUpdate.itemSwitch.isChecked = checked
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
            settingItem.setTVClickListener(needChangeAlpha = false) {
                checkAction = true
                updateStateValue = mViewModel.appUpdateState.value?.state ?: Status.INVALID
                if (updateStateValue > Status.CHECK_UPDATE && updateStateValue <= Status.DOWNLOAD_PROGRESS) {
                    showDownLoadingDialog()
                    return@setTVClickListener
                }

                showLoadingDialog()
                lifecycleScope.launch {
                    delay(2000)
                    if (mViewModel.isUpdate.value == true) {
                        if (updateStateValue > Status.CHECK_UPDATE && updateStateValue <= Status.DOWNLOAD_ERROR) {
                            showUpdateDialog(isUpdate = true, breadPoint = true)
                        } else {
                            showUpdateDialog(isUpdate = true, breadPoint = false)
                        }
                    } else {
                        mViewModel.checkUpdate(false, getString(R.string.setting_detecting_update))
                    }
                }
            }
        }

        mBind.settingLayoutUserAgreement.apply {
            tvItemTitleText.text = getString(R.string.setting_user_agreement)
            settingItem.setTVClickListener(needChangeAlpha = false) {
                val info = CacheExt.getUserAgreement()?.h5Url ?: ""
                WebViewDialog(
                    requireContext(),
                    viewLifecycleOwner
                ).show(getStringExt(R.string.launcher_dialog_title_ua))
            }
        }

        mBind.settingLayoutPrivacyAgreement.apply {
            tvItemTitleText.text = getString(R.string.setting_privacy_agreement)

            settingItem.setTVClickListener(needChangeAlpha = false) {
                val info = CacheExt.getProtocol()?.h5Url ?: ""
                WebViewDialog(
                    requireContext(),
                    viewLifecycleOwner
                ).show(getStringExt(R.string.launcher_dialog_title_pp))
            }
        }
    }

    /**
     * 显示更新提示红点
     */
    private fun showUpdateRedDotTip() {
        context?.let { _ ->
            mBind.settingLayoutCheckUpdate.tvItemTitleRightIcon.visibility = View.VISIBLE
            mBind.settingLayoutCheckUpdate.tvItemTitleRightIcon.setBackgroundResource(R.drawable.shape_setting_update_tip)
        }
    }

    /**
     * 隐藏更新提示红点
     */
    private fun dismissUpdateRedDotTip() {
        mBind.settingLayoutCheckUpdate.tvItemTitleRightIcon.visibility = View.INVISIBLE
    }

    /**
     * 该函数是用于初始化观察者。它观察mViewModel.isUpdate和mViewModel.appUpdateState两个变量的变化，
     * 并根据不同的变化情况执行相应的逻辑处理。
     */
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
                                showUpdateDialog(it.update, true)
                            } else {
                                showUpdateDialog(it.update, false)
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
     * 显示Home页的设置红点
     */
    private fun showHomeSettingBadge(show: Boolean) {
        try {
            val act = activity
            if (act is HomeTVActivity) {
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
        loadingDialog = ZeekrTVDialogCreate(requireContext()).show {
            lifecycleOwner(this@SettingFragment)
            title("检查更新")
            mergeLayout {
                speciallySize()
                dialogParam(
                    DialogTVParam(
                        dialogSpeciallyWidth = resources.getDimensionPixelSize(R.dimen.tv_806),
                        dialogSpeciallyHeight = resources.getDimensionPixelSize(R.dimen.tv_486),
                    )
                )
                it.inflateCheckLoadingDialogLayout().apply {
//                    dialogParam(
//                        DialogParam(
//                            isDismissOnBackPressed = true,
//                            isDismissOnTouchOutside = true
//                        )
//                    )
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
    private fun showUpdateDialog(isUpdate: Boolean, breadPoint: Boolean) {
        if (isUpdate) {
            updateAction?.dismiss()
            updateAction = null
            downLoadingAction?.dismiss()
            downLoadingAction = null
            downloadBinding = null
            loadingDialog?.dismiss()

            updateAction = ZeekrTVDialogCreate(requireContext()).show {
                lifecycleOwner(viewLifecycleOwner)
                title(
                    if (breadPoint) getString(R.string.setting_app_update_break_point_title) else getString(
                        R.string.setting_app_update_new_version
                    )
                )
                content(if (breadPoint) getString(R.string.setting_app_update_break_point_content) else "${mViewModel.appInfo?.updates}")
                contentMargin(28.dp, 28.dp)
//                dialogParam(
//                    DialogParam(
//                        isDismissOnBackPressed = false,
//                        isDismissOnTouchOutside = false
//                    )
//                )
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
        } else if (checkAction) {
            loadingDialog?.dismiss()
            if (mViewModel.isUpdate.value == true) {
                mViewModel.isUpdate.postValue(false)
            }
            mViewModel.appUpdateState.postValue(AppUpdateState())
            ToastUtils.show(getString(R.string.setting_app_is_latest_version))
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

        downLoadingAction = ZeekrTVDialogCreate(requireContext()).show {
            lifecycleOwner(this@SettingFragment)
            title("正在更新")
            mergeLayout {
                speciallySize()
                dialogParam(
                    DialogTVParam(
                        dialogSpeciallyWidth = resources.getDimensionPixelSize(R.dimen.tv_806),
                        dialogSpeciallyHeight = resources.getDimensionPixelSize(R.dimen.tv_486),
                    )
                )
                downloadBinding = it.inflateLoadingDialogLayout().apply {
//                    dialogParam(
//                        DialogParam(
//                            isDismissOnBackPressed = false,
//                            isDismissOnTouchOutside = false
//                        )
//                    )
                    downloadProgressBar.status = CircleProgressBar.Status.Update
                }
            }
            realButton(text = "后台更新") { action ->
                action.dismiss()
            }

        }
    }

    /**
     * 懒加载
     */
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
     * 请求空数据
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            checkUpdateRequestCode -> {
                loadingDialog?.dismiss()
                if (!isHidden && checkAction) {
                    ToastUtils.show(
                        requireContext(),
                        getString(R.string.setting_app_is_latest_version)
                    )
                }
            }
        }
    }
}
package com.zeekrlife.market.widget

import android.app.Activity
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.zeekr.component.tv.dialog.ZeekrTVDialogAction
import com.zeekr.component.tv.dialog.ZeekrTVDialogCreate
import com.zeekr.component.tv.dialog.common.DialogTVParam
import com.zeekr.zui_common.tv.ktx.getDimen
import com.zeekr.zui_common.tv.ktx.getServiceInflate
import com.zeekrlife.common.ext.getStringExt
import com.zeekrlife.common.ext.gone
import com.zeekrlife.common.ext.setTVClickListener
import com.zeekrlife.common.ext.visible
import com.zeekrlife.common.imageloader.ImageLoader.load
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.market.R
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.DialogAppBinding
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.market.ui.activity.AppDetailActivity
import com.zeekrlife.task.base.constant.TaskState

/**
 * @author mac
 * 公共组件库不同，分开写
 */
class AppDialog {
    private var dialogAction: ZeekrTVDialogAction? = null
    private var binding: DialogAppBinding? = null
    private fun ViewGroup.inflateDialogLayout() =
        DialogAppBinding.inflate(context.getServiceInflate(), this)

    fun show(
        activity: Activity,
        item: AppItemInfoBean,
        viewLifecycleOwner: LifecycleOwner,
        isLocalApp: Boolean = false
    ) {
        dialogAction = ZeekrTVDialogCreate(activity).show {
            speciallySize()
            dialogParam(
                DialogTVParam(
                    dialogSpeciallyWidth = activity.getDimen(R.dimen.tv_806),
                    dialogSpeciallyHeight = activity.getDimen(R.dimen.tv_326),
                    isDismissOnBackPressed = true,
                    isDismissOnTouchOutside = true
                )
            )
            lifecycleOwner(viewLifecycleOwner)
            mergeLayout {
                binding = it.inflateDialogLayout().apply {
                    if (isLocalApp) {
                        ApkUtils.getAppInfo(
                            activity,
                            item.apkPackageName ?: ""
                        )?.icon?.let { icon ->
                            imageViewIcon.setImageDrawable(icon)
                            imageViewIcon.setCover()
                        }
                    } else {
                        imageViewIcon.load(
                            item.icon ?: "", R.drawable.img_bg_default, R.drawable.img_bg_error
                        )
                    }

                    textViewName.text = item.apkName
                    textViewSlogan.text = item.slogan
                    if (item.dataType == 1) {
                        clAppButtons.gone()
                    } else {
                        clAppButtons.visible()
                        val taskInfo = TaskHelper.getTaskInfo(item)
                        btDownload.apply {
                            setDarkStyle()
                            requestViewActionFocus()
                            init(taskInfo)
                        }
                        if (taskInfo?.state == TaskState.UPDATABLE) {
                            btNewVersionInfo.visible()
                            btNewVersionInfo.setTVClickListener(false) {
                                ZeekrTVDialogCreate(activity)
                                    .show {
                                        dialogParam(
                                            DialogTVParam(
                                                isDismissOnBackPressed = true,
                                                isDismissOnTouchOutside = true
                                            )
                                        )
                                        title(activity.resources.getString(R.string.bt_check_new_version_info))
                                        content(item.updates ?: "")
                                        lifecycleOwner(viewLifecycleOwner)
                                        realButton(text = getStringExt(R.string.common_confirm)) { action ->
                                            action.dismiss()
                                        }
                                    }
                            }
                        } else {
                            btNewVersionInfo.gone()
                        }
                        btAppInfo.setTVClickListener(false) {
                            dialogAction.dismiss()
                            AppDetailActivity.start(activity, item.id)
                        }
                    }
                }
            }
        }

    }

}



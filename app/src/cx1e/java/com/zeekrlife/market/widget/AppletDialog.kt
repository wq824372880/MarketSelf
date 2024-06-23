package com.zeekrlife.market.widget

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.zeekr.component.dialog.ZeekrDialogAction
import com.zeekrlife.market.R
import com.zeekr.component.dialog.ZeekrDialogCreate
import com.zeekr.component.dialog.common.DialogParam
import com.zeekr.zui_common.ktx.getServiceInflate
import com.zeekrlife.common.imageloader.ImageLoader.load
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCornerByEach
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.DialogAppletBinding
import com.zeekrlife.market.manager.AppletPropertyManager
import com.zeekrlife.market.utils.applet.AppletUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * @author mac
 * 公共组件库不同，分开写
 */
class AppletDialog(val activity: FragmentActivity) {
    private var appletDialogAction: ZeekrDialogAction? = null
    private var dialogAppletBinding: DialogAppletBinding? = null
    private fun ViewGroup.inflateDialogLayout() =
        DialogAppletBinding.inflate(context.getServiceInflate(), this)

    /**
     * 显示小程序的对话框。
     *
     * @param item 包含小程序信息的Bean对象，如小程序的ID、名称、标语和图标等。
     */
    fun show(item: AppItemInfoBean) {
        // 检查小程序是否已经添加到应用中心
        val isExist = AppletPropertyManager.shortcutExist(item.miniAppId ?: 0)
        // 设置对话框的基本参数，如是否允许通过返回键或点击外部区域关闭对话框
        val dialogParam = DialogParam(
            isDismissOnBackPressed = false,
            isDismissOnTouchOutside = false,
        )
        // 先关闭当前的小程序对话框，避免重复显示
        appletDialogAction?.dismiss()
        appletDialogAction = null
        dialogAppletBinding = null
        // 创建并显示小程序对话框
        appletDialogAction = ZeekrDialogCreate(activity).show {
            title("小程序")
            showCloseIcon(true)
            dialogParam(dialogParam)
            lifecycleOwner(activity)
            mergeLayout {
                // 初始化对话框布局绑定
                dialogAppletBinding = it.inflateDialogLayout().apply {
                    // 加载小程序图标，未加载成功时显示默认图标
                    ivIcon.load(item.icon ?: "", R.drawable.img_bg_default, R.drawable.img_bg_error)
                    // 加载小程序水印图标，设置圆角，并在错误时显示默认图标
                    ivWatermark.loadWithCornerByEach(
                        drawableId = R.drawable.applet_shadow,
                        placeHolder = R.drawable.applet_shadow,
                        error = R.drawable.applet_shadow,
                        leftCorner = 0,
                        topCorner = 0,
                        rightCorner = 0,
                        bottomCorner = 16
                    )
                    // 设置图标为覆盖显示
                    ivIcon.setCover()
                    ivWatermark.setCover()
                    // 设置小程序名称和标语
                    tvAppletText.text = item.apkName
                    tvAppletDesc.text = item.slogan
                }
            }
            // 设置“添加至应用中心”或“从应用中心移除”的按钮文本和点击事件
            realButton(text = if (isExist) "从应用中心移除" else "添加至应用中心") { action ->
                if (isExist) {
                    // 如果小程序已存在，则从应用中心移除
                    AppletPropertyManager.removeShortcut(item.miniAppId ?: 0)
                    // 显示移除成功的提示信息
                    MainScope().launch {
                        ToastUtils.show("${item.apkName}已成功移除！")
                    }

                } else {
                    // 如果小程序不存在，则添加到应用中心
                    AppletPropertyManager.addShortcut(
                        item.miniAppId ?: 0,
                        item.apkName,
                        item.slogan,
                        item.icon,
                        R.mipmap.ic_logo
                    )
                    // 显示添加成功的提示信息
                    MainScope().launch {
                        ToastUtils.show("${item.apkName}已成功添加至桌面！")
                    }

                }
                action.dismiss()
            }
            // 设置“打开”按钮的文本和点击事件，点击后打开小程序
            ghostButton(text = "打开") { action ->
                AppletUtils.startAppletProcess(item.miniAppId.toString(), false) {

                }
                action.dismiss()
            }
        }

    }

}



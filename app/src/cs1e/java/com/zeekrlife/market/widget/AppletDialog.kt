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
     * 显示小程序弹窗
     * 显示小程序弹窗
     * 显示小程序弹窗
     */
    fun show(item: AppItemInfoBean) {
        val isExist =  AppletPropertyManager.shortcutExist(item.miniAppId?:0)
        val dialogParam = DialogParam(
            isDismissOnBackPressed = false,
            isDismissOnTouchOutside = false,
        )
        appletDialogAction?.dismiss()
        appletDialogAction = null
        dialogAppletBinding = null
        appletDialogAction = ZeekrDialogCreate(activity).show {
            title("小程序")
            showCloseIcon(true)
            dialogParam(dialogParam)
            lifecycleOwner(activity)
            mergeLayout {
                dialogAppletBinding = it.inflateDialogLayout().apply {
                    ivIcon.load(item.icon?:"",R.drawable.img_bg_default,R.drawable.img_bg_error)
                    ivWatermark.loadWithCornerByEach(drawableId = R.drawable.applet_shadow, placeHolder = R.drawable.applet_shadow,  error = R.drawable.applet_shadow, leftCorner = 0, topCorner = 0, rightCorner = 0, bottomCorner =  16)
                    ivIcon.setCover()
                    ivWatermark.setCover()
                    tvAppletText.text = item.apkName
                    tvAppletDesc.text = item.slogan
                }
            }
            realButton(  text = if(isExist) "从应用中心移除" else "添加至应用中心") {
                action ->
                if(isExist){
                    AppletPropertyManager.removeShortcut(item.miniAppId?:0)
                    MainScope().launch {
                        ToastUtils.show("${item.apkName}已成功移除！")
                    }

                }else{
                    AppletPropertyManager.addShortcut(item.miniAppId?:0,item.apkName,item.slogan,item.icon,R.mipmap.ic_logo)
                    MainScope().launch {
                        ToastUtils.show("${item.apkName}已成功添加至桌面！")
                    }

                }
                action.dismiss()
            }
            ghostButton(text = "打开") { action ->
                AppletUtils.startAppletProcess(item.miniAppId.toString(),false) {

                }
                action.dismiss()
            }
        }

    }

}



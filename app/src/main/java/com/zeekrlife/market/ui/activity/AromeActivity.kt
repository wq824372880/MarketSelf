package com.zeekrlife.market.ui.activity

import android.os.Bundle
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.databinding.ActivityEntryBinding
import com.zeekrlife.market.utils.applet.AppletResultCallBack
import com.zeekrlife.market.utils.applet.AppletUtils
import com.zeekrlife.net.interception.logging.util.logE


class AromeActivity : BaseActivity<BaseViewModel, ActivityEntryBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setDecorFitsSystemWindows(false)
        window.decorView.setOnApplyWindowInsetsListener { v, insets ->
            window.decorView.setPadding(0, 0, 0, 0)
            insets
        }
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent == null) {
            finish()
            return
        }
        val extras = intent.extras
        if(extras?.containsKey("appletId") == true && extras.containsKey("startApplet")){
            val appletId  = extras.getString("appletId")
            AppletUtils.startAppletProcess(appletId,false){
                AppletResultCallBack.appletResultListener?.onResult(it)
                finish()
            }
        }
        if(extras?.containsKey("serviceCode") == true && extras.containsKey("customScene")){
            val serviceCode  = extras.getString("serviceCode")
            AppletUtils.startCustomService(serviceCode,false){
//                AppletResultCallBack.appletResultListener?.onResult(it)
                finish()
            }
        }
        "AromeActivity执行了".logE("zzzAromeActivity")
    }

    override fun showToolBar(): Boolean {
        return false
    }

}

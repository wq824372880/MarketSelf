package com.zeekrlife.market.utils

import com.zeekrlife.ampe.aidl.AppletInfo
import com.zeekrlife.market.utils.applet.AppletInterface
import com.zeekrlife.market.utils.applet.AppletUtils

class AppletImpl : AppletInterface {

    override fun initArome() {

    }

    override fun startAppletProcess(
        id: String?,
        fullScreen: Boolean,
        retryTimes: Int,
        callBack: ((info: AppletInfo) -> Unit)?
    ) {


    }

    override fun exitApplet() {

    }
}
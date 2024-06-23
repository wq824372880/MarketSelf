package com.zeekrlife.market.utils

import com.zeekrlife.ampe.aidl.AppletInfo
import com.zeekrlife.market.utils.applet.AppletInterface
import com.zeekrlife.market.utils.applet.AppletUtils

class AppletImpl : AppletInterface {

    /**
     * 初始化 Arome。
     * 该函数是重写函数，用于初始化 Arome 应用框架。
     * 无参数。
     * 无返回值。
     */
    override fun initArome() {

        // 初始化 Arome 工具类
        AppletUtils.initArome()
    }

    /**
     * 启动小程序进程的函数。
     *
     * @param id 小程序的唯一标识符，可以为null。
     * @param fullScreen 指定小程序是否以全屏模式启动。
     * @param retryTimes 启动失败时的重试次数。
     * @param callBack 启动完成后的回调函数，接收一个AppletInfo类型的参数。
     */
    override fun startAppletProcess(
        id: String?,
        fullScreen: Boolean,
        retryTimes: Int,
        callBack: ((info: AppletInfo) -> Unit)?
    ) {
        // 调用AppletUtils类的同名函数以启动小程序进程
        AppletUtils.startAppletProcess(id,fullScreen,retryTimes,callBack)

    }

    /**
     * 退出小程序的方法。
     * 这个方法没有参数。
     * 没有返回值。
     */
    override fun exitApplet() {

        // 使用AppletUtils工具类的exitApplet方法来退出小程序
        AppletUtils.exitApplet()

    }
}
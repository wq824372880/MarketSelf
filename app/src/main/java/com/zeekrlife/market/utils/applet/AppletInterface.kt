package com.zeekrlife.market.utils.applet

import com.zeekrlife.ampe.aidl.AppletInfo

interface AppletInterface {

    /**
     * 初始化Arome模块。
     * 该函数没有参数。
     * 没有返回值。
     */
    fun initArome()

    /**
     * 启动小程序进程的函数。
     *
     * @param id 小程序的唯一标识符，可以为null。
     * @param fullScreen 是否全屏显示，默认为false。
     * @param retryTimes 重试次数，默认为0。用于在启动失败时进行重试。
     * @param callBack 启动完成后的回调函数，接收一个AppletInfo类型的参数，可以为null。
     */
    fun startAppletProcess(
        id: String?,
        fullScreen: Boolean,
        retryTimes: Int = 0,
        callBack: ((info: AppletInfo) -> Unit)? = null
    )

    /**
     * 退出小程序。
     * 该函数没有参数。
     * 也没有返回值。
     */
    fun exitApplet()

}
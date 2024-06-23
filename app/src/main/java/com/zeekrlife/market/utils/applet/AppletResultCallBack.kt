package com.zeekrlife.market.utils.applet

import com.zeekrlife.ampe.aidl.AppletInfo


object AppletResultCallBack {
    @get:JvmName("invokeWithA")
    var appletResultListener: AppletResultListener? = null

    /**
     * 定义了一个应用结果监听器接口，用于接收应用查询结果。
     */
    interface AppletResultListener {
        /**
         * 当查询到应用结果时调用此方法。
         *
         * @param result 包含应用信息的AppletInfo对象。
         */
        fun onResult(result: AppletInfo)
    }

    /**
     * 设置应用结果监听器。
     * 该函数用于指定一个监听器，以便在应用中发生特定结果时进行回调。
     *
     * @param listener AppletResultListener类型的对象，是一个回调接口，用于监听应用结果。
     *                 具体的监听逻辑需要在调用者中实现此接口来定义。
     */
    @JvmName("invokeWithB")
    fun setAppletResultListener(listener: AppletResultListener) {
        appletResultListener = listener
    }

}
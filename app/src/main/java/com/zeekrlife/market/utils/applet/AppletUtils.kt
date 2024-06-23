package com.zeekrlife.market.utils.applet

import com.alipay.arome.aromecli.AromeInit
import com.zeekr.basic.appContext
import com.zeekr.car.api.DeviceApiManager
import com.zeekr.sdk.navi.bean.*
import com.zeekr.sdk.navi.bean.client.NaviRoutePlan
import com.zeekr.sdk.navi.bean.service.RouteInfo
import com.zeekr.sdk.navi.bean.service.RspRoutePlanResult
import com.zeekr.sdk.navi.callback.INaviAPICallback
import com.zeekr.sdk.navi.impl.NaviAPI
import com.zeekrlife.ampe.aidl.AppletInfo
import com.zeekrlife.ampe.core.AromeServiceInteract
import com.zeekrlife.ampe.lib.manager.AppletManager
import com.zeekrlife.ampe.lib.proxy.AppletProxy
import com.zeekrlife.common.util.NetworkUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.market.app.ext.mmkvSave
import com.zeekrlife.market.data.ValueKey
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.AppletSignatureInfo
import com.zeekrlife.market.manager.AppletPropertyManager
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object AppletUtils {
    const val TAG = "zzzAppletUtils"

    /**
     * 初始化 Arome 系统。
     * 该函数首先获取设备签名，然后验证签名。如果服务可用并且 Arome 服务在线，则初始化 Arome。
     * 在初始化过程中，会进行一些额外的设置和请求。
     */
    fun initArome() {
        AppletPropertyManager.getDeviceSignature(
            callbackSuccess = {
                "验签值:$it,deviceId=${DeviceApiManager.getInstance().deviceAPI?.ihuid}".logE("zzzArome")
                if (!AppletProxy.getInstance().ensureServiceAvailable() || !AromeInit.isServiceOnline()) {
                    AppletProxy.getInstance().initArome(appContext, DeviceApiManager.getInstance().deviceAPI?.ihuid ?: "", it.sign) { appletInfo->
                        "applet initArome:success:${appletInfo.success}".logE("zzzArome")
                        if(appletInfo.success){
//                            AppletPropertyManager.getLegalAppletIdSet{legalAppletIds->
//                                "applet legalAppletIds:$legalAppletIds".logE("zzzArome")
//                                val legals = if(legalAppletIds.toList().size > 10) legalAppletIds.toList().take(10) else legalAppletIds.toList()
//                                AppletProxy.getInstance().batchPreLoad(legals){ preloadIds->
//                                    "applet legals:${legals.toJsonStr()},applet batchPreLoad:${preloadIds.toJsonStr()}".logE("zzzArome")
//                                }
//                            }

                            AppletProxy.getInstance().extendBridgeRequest(null,null){

                            }

//                            com.zeekrlife.ampe.lib.proxy.AppletProxy.getInstance().initAromeExt(DeviceApiManager.getInstance().deviceAPI?.ihuid ?: "",null){ itExt->
//                                "applet initAromeExt:success:${itExt.success}".logE("zzzArome")
//
//                            }

                        }
                    }
                }

            }
        )

    }

    /**
     * 启动小程序进程的函数。
     *
     * @param id 小程序的ID，如果为空则启动失败。
     * @param fullScreen 是否全屏显示。
     * @param retryTimes 重试次数，默认为0。
     * @param callBack 启动完成后的回调函数，可选。
     */
    fun startAppletProcess(
        id: String?,
        fullScreen: Boolean,
        retryTimes: Int = 0,
        callBack: ((info: AppletInfo) -> Unit)? = null
    ) {
        // 开启Wi-Fi
        NetworkUtils.setWifiEnabled(true)
        // 检查小程序ID是否为空
        if (id.isNullOrEmpty()) {
            launcherAppletFail(callBack)
            return
        }
        // 获取设备签名
        AppletPropertyManager.getDeviceSignature(
            callbackSuccess = {
                "验签值:$it,deviceId=${DeviceApiManager.getInstance().deviceAPI.ihuid}".logE("zzzArome")
                if (!AppletProxy.getInstance().ensureServiceAvailable() || !AromeInit.isServiceOnline() || !AppletManager.initAromeSuccess) {
                    AppletProxy.getInstance().initArome(appContext, DeviceApiManager.getInstance().deviceAPI?.ihuid ?: "", it.sign) { appletInfo->
                        if(appletInfo.success){
                            launcherApplet(id, fullScreen, retryTimes,callBack)
                        }else{
                            CacheExt.setAppletDeviceSignature(AppletSignatureInfo(AromeServiceInteract.HOST_APP_ID,"4806052",""))
                            launcherAppletFail(callBack)
                        }
                    }
                } else {
                    // 服务可用，直接启动小程序
                    launcherApplet(id, fullScreen, retryTimes, callBack)
                }
            },
            callbackFail = {
                // 验签失败的处理逻辑
                CacheExt.setAppletDeviceSignature(
                    AppletSignatureInfo(
                        AromeServiceInteract.HOST_APP_ID,
                        "4806052",
                        ""
                    )
                )
                launcherAppletFail(callBack)
            }
        )
    }

    /**
     * 启动小程序应用。
     *
     * @param id 小程序的ID，可能为null。
     * @param fullScreen 是否全屏启动。
     * @param retryTimes 重试次数，默认为0。
     * @param callBack 启动完成后的回调，可选。
     */
    private fun launcherApplet(
        id: String?,
        fullScreen: Boolean,
        retryTimes: Int = 0,
        callBack: ((info: AppletInfo) -> Unit)? = null
    ) {
//        if (AromeServiceInteract.carLifeAppletId.contentEquals(id)) {
//            AppletProxy.getInstance().launcherAppletWithFullScreen(id) { info ->
//                callBack?.invoke(info)
//            }
//        } else {
        MainScope().launch(Dispatchers.Main) {
            AppletProxy.getInstance().launcherApplet(id) { info ->
                if(info.success){
                    callBack?.invoke(info)
//                    val intents = IntentUtils.getHomeIntent()
//                    appContext.startActivity(intents)
                }else{ //重试
                    mmkvSave.removeValueForKey(ValueKey.APPLET_DEVICE_SIGNATURE)
                    if(retryTimes < 1){
                        startAppletProcess(id,fullScreen,retryTimes + 1){
                            if (!it.success) {
                                launcherAppletFail(callBack)
                            }
                        }
                    }else{
                        launcherAppletFail(callBack)
                    }
                }

//            }
            }
        }

    }

    /**
     * 处理启动小程序失败的逻辑。
     *
     * @param callBack 回调函数，当启动失败时被调用。接收一个 AppletInfo 类型的参数，其中包含了失败的详情。
     *                 此参数为可选，如果提供，会在失败时被调用以通知调用者失败的信息。
     */
    private fun launcherAppletFail(callBack: ((info: AppletInfo) -> Unit)? = null) {
        MainScope().launch {
            ToastUtils.show(if(NetworkUtils.isConnected()) "打开失败，请稍后重试" else "网络不佳，请稍后重试")
        }
        callBack?.invoke(AppletInfo().apply {
            success = false
            code = 1
            message = if(NetworkUtils.isConnected()) "打开失败，请稍后重试" else "网络不佳，请稍后重试"
        })
    }

    /**
     * 退出小程序。
     * 该函数没有参数。
     * 该函数没有返回值。
     */
    fun exitApplet() {
        // 获取AppletProxy的实例并调用其exitApplet方法来退出小程序
        AppletProxy.getInstance().exitApplet()
    }

//    fun loadWidget(
//        query: JSONObject?,
//        showPlaceholder: Boolean,
//        callBack: ((info: AppletInfo) -> Unit)? = null
//    ) {
//        if (!AppletProxy.getInstance().ensureServiceAvailable() || !AromeInit.isServiceOnline()) {
//            initArome()
//            MainScope().launch {
//                delay(300)
//                AppletProxy.getInstance().loadWidget(query?.toJSONString(),showPlaceholder,callBack)
//            }
//        }else{
//            AppletProxy.getInstance().loadWidget(query?.toJSONString(),showPlaceholder,callBack)
//        }
//    }


    /**
     * 导航到目的地
     */
    fun routerPlan() {
        val poiInfo = PoiInfo()
        poiInfo.latLng = LatLng(31.245369, 121.50626)
        poiInfo.name = "东方明珠"
        //设置目的地
        val reqModel = NaviRoutePlan(poiInfo)

        val viaPoi = PoiInfo()
        viaPoi.latLng = LatLng(30.697702, 120.806668)
        viaPoi.name = "嘉兴南站"
        val viaList: MutableList<PoiInfo> = ArrayList()
        viaList.add(viaPoi)
        //设置途经点
        reqModel.setViaPoiInfos(viaList)
        //算路偏好-使用地图默认的算路规则
        reqModel.setStrategy(RoutePlanStrategy.DEFAULT)
        //发起路线规划
        reqModel.setAction(NaviRoutePlan.ACTION_ROUTE_PLAN)

        NaviAPI.get().routePlanOrNavi(reqModel, object : INaviAPICallback {
            override fun onSuccess(model: NaviBaseModel) {
                val routePlanResult = model as RspRoutePlanResult
                //路线信息
                val routes: List<RouteInfo> = routePlanResult.routeInfoList
            }

            override fun onError(model: NaviErrorModel) {
                model.logE("zzzNaviAPI")

            }
        })

    }

    /**
     * 启动自定义服务。
     *
     * @param customServiceCode 自定义服务的代码，不能为空。
     * @param fullScreen 是否全屏显示。
     * @param retryTimes 重试次数，默认为0。
     * @param callBack 启动服务成功或失败的回调函数。
     */
    fun startCustomService(
        customServiceCode: String?,
        fullScreen: Boolean,
        retryTimes: Int = 0,
        callBack: ((info: AppletInfo) -> Unit)? = null
    ) {
        if (customServiceCode.isNullOrEmpty()){
            "startCustomService launcherAppletFail11".logE(TAG)
            launcherAppletFail(callBack)
            return
        }
        AppletPropertyManager.getDeviceSignature(
            callbackSuccess = {
                "验签值:$it,deviceId=${DeviceApiManager.getInstance().deviceAPI.ihuid}".logE("zzzArome")
                if (!AppletProxy.getInstance().ensureServiceAvailable() || !AromeInit.isServiceOnline() || !AppletManager.initAromeSuccess) {
                    AppletProxy.getInstance().initArome(appContext, DeviceApiManager.getInstance().deviceAPI?.ihuid ?: "", it.sign) { appletInfo->
                        if(appletInfo.success){
                            launcherAppletCustomService(customServiceCode, fullScreen, retryTimes,callBack)
                        }else{
                            "startCustomService launcherAppletFail22".logE(TAG)
                            launcherAppletFail(callBack)
                        }
                    }
                } else {
                    launcherAppletCustomService(customServiceCode, fullScreen, retryTimes,callBack)
                }
            },
            callbackFail = {
                "startCustomService launcherAppletFail33".logE(TAG)
                launcherAppletFail(callBack)
            }
        )




    }

    /**
     * 启动小程序自定义服务。
     *
     * @param customServiceCode 自定义服务代码，用于标识特定的服务。
     * @param fullScreen 是否全屏显示。
     * @param retryTimes 重试次数，默认为0，表示不重试。
     * @param callBack 回调函数，当服务启动成功或失败时被调用。
     */
    private fun launcherAppletCustomService(
        customServiceCode: String?,
        fullScreen: Boolean,
        retryTimes: Int = 0,
        callBack: ((info: AppletInfo) -> Unit)? = null
    ) {
        MainScope().launch(Dispatchers.Main) {
            "AppletUtils launcherAppletCustomService customServiceCode:$customServiceCode".logE(TAG)
            AppletProxy.getInstance().launcherCustomService(customServiceCode,"") { info ->
                if(info.success){
                    "AppletUtils launcherAppletCustomService AppletInfo success:${info.success},message:${info.message}".logE(TAG)
                    callBack?.invoke(info)
                }else{ //重试
                    mmkvSave.removeValueForKey(ValueKey.APPLET_DEVICE_SIGNATURE)
                    if(retryTimes < 1){
                        "AppletUtils launcherAppletCustomService AppletInfo fail:${info.success},message:${info.message}".logE(TAG)
                        launcherAppletCustomService(customServiceCode,fullScreen,retryTimes + 1){
                            if (!it.success) {
                                launcherAppletFail(callBack)
                            }
                        }
                    }else{
                        launcherAppletFail(callBack)
                    }
                }

            }
        }

    }

    /**
     * 发送事件的函数，用于向应用小程序发送事件。
     *
     * @param eventName 事件名称，类型为String。
     * @param eventData 事件数据，类型为String。
     * @param callBack 回调函数，可选参数。当事件发送成功时，通过此回调函数返回相关信息。
     */
    fun sendEvent(
        eventName: String,
        eventData: String,
        callBack: ((info: AppletInfo) -> Unit)? = null
    ) {
        MainScope().launch(Dispatchers.Main) {
            "AppletUtils sendEvent eventName:$eventName,eventData:$eventData".logE(TAG)
            AppletProxy.getInstance().sendEvent(eventName,eventData) { info ->
                if(info.success){
                    "AppletUtils sendEvent AppletInfo success:${info.success},message:${info.message}".logE(TAG)
                    callBack?.invoke(info)
                }

            }
        }

    }
}
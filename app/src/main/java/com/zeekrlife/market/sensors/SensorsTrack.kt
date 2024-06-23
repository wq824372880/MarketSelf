package com.zeekrlife.market.sensors

import android.util.Log
import androidx.collection.arrayMapOf
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.zeekr.basic.appContext
import com.zeekrlife.market.utils.CarManager
import com.zeekr.car.api.PolicyApiManager
import com.zeekr.sdk.analysis.impl.AnalysisAPI
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.net.api.NetUrl

object SensorsTrack {

    //页面曝光
    private const val EVENTS_PAGE_EXPOSURE = "APPstore_page_exposure"

    //应用有效曝光
    private const val EVENTS_APP_EXPOSURE = "APPstore_AppExposure"

    //应用搜索有效曝光
//    private const val EVENTS_APP_SEARCH = "IHU_AppStore_events_Search"

    //应用详细展示
    private const val EVENTS_APP_DETAIL_SHOW = "APPstore_AppDetailShow"

    //应用购买
//    private const val EVENTS_APP_BUY = "IHU_AppStore_events_AppBuy"

    //应用下载
    private const val EVENTS_APP_DOWNLOAD = "APPstore_AppDownload"

    //应用更新
    private const val EVENTS_APP_UPDATE = "APPstore_AppUpdate"

    //应用卸载
    private const val EVENTS_APP_UNINSTALL = "APPstore_App_unload"

    //应用取消卸载
    private const val APPSTORE_APP_UNINSTALL_CANCEL = "APPstore_App_unload_cancel"

    //精品推荐Banner页展示
//    private const val EVENTS_BANNER_SHOW = "IHU_AppStore_events_banner_show"

    //精品推荐Banner点击事件
//    private const val EVENTS_BANNER_CLICK = "IHU_AppStore_events_banner_click"

    //应用市场首页：侧边栏按钮
//    private const val EVENTS_HOME_TAB_SELECTED = "IHU_AppStore_events_Home_Tab_Selected"

    @Volatile
    var isSensorTrackInit = false


    interface SensorCallback {
        fun onCallback(result: Boolean)
    }

    fun init(callback: SensorCallback) {
            ThreadPoolUtil.runOnUiThread {
                if(isSensorTrackInit){
                    callback.onCallback(true)
                    return@runOnUiThread
                }
                val saConfigOptions = SAConfigOptions(NetUrl.sensorServer)
                saConfigOptions.setAutoTrackEventType(
                    SensorsAnalyticsAutoTrackEventType.TYPE_NONE
                ).enableLog(false)
                AnalysisAPI.get().config(appContext, "ZeekrMarket", "app_market", false, saConfigOptions)
                AnalysisAPI.get().init(appContext
                ) { result, msg ->
                    isSensorTrackInit = result
                    callback.onCallback(result)
                    Log.e(PolicyApiManager.TAG, "SensorSDK init result ==> $result, msg ==> $msg")
                }
                //作用于应用详情埋点：跳转来源
                appContext.registerActivityLifecycleCallbacks(TrackActivityLifecycleCallbacks())
                //埋点按需适配处理
                CarManager.sensorAdapter()?.registerSuperProperties(appContext)
            }

    }

    /**
     * 页面曝光
     */
    fun onPageExposure(fromPage: String) {
        try {
            val properties = arrayMapOf<String, Any>()
            properties["page_name"] = fromPage
            track(EVENTS_PAGE_EXPOSURE, properties)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 应用有效曝光
     * @param showPalace 应用曝光位置
     * @param
     */
    fun onAppExposure(showPalace: String, apps: List<AppItemInfoBean>) {
        try {
            val properties = arrayMapOf<String, Any>()
            properties["AppShowPalace"] = showPalace
            apps.forEach { app ->
                properties.putAll(commonProperties(app))
                track(EVENTS_APP_EXPOSURE, properties)
            }
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 应用搜索曝光
     * @param keyWords
     * @param apps
     */
    fun onAppSearchExposure(keyWords: String, apps: List<AppItemInfoBean>) {
//        try {
//            val properties = arrayMapOf<String, Any>()
//            properties["keyWords"] = keyWords
//            apps.forEach { app ->
//                properties.putAll(commonProperties(app))
//                track(EVENTS_APP_SEARCH, properties)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    /**
     * 应用详情展示
     * @fromPage
     * @app
     */
    fun onAppDetailShow(fromPage: String, app: AppItemInfoBean) {
        try {
            val properties = arrayMapOf<String, String>()
            properties["AppShowPalace"] = fromPage
            properties["AppScore"] = "0"
            properties.putAll(commonProperties(app))
            track(EVENTS_APP_DETAIL_SHOW, properties)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 应用购买
     */
    fun onAppBuy(fromPage: String, app: AppItemInfoBean) {
//        val properties = arrayMapOf<String, String>()
//        properties["AppShowPalace"] = fromPage
//        properties.putAll(commonProperties(app))
//        track(EVENTS_APP_BUY, properties)
    }

    /**
     * 应用下载
     */
    fun onAppDownload(fromPage: String, app: AppItemInfoBean) {
        val properties = arrayMapOf<String, Any>()
        properties["AppShowPalace"] = fromPage
        properties["page_name"] = fromPage
        properties["AppScore"] = "0"
        properties.putAll(commonProperties(app))
        track(EVENTS_APP_DOWNLOAD, properties)
    }

    /**
     * 应用更新
     */
    fun onAppUpdate(fromPage: String, appName: String, updatableAppNum: Int, UpdateWay: Int) {
        val properties = arrayMapOf<String, String>()
        properties["AppName"] = appName
        properties["page_name"] = fromPage
        properties["UpdatableAppNum"] = updatableAppNum.toString()
        properties["UpdateWay"] = UpdateWay.toString()
        track(EVENTS_APP_UPDATE, properties)
    }

    /**
     * 应用卸载
     */
    fun onAppUninstall(fromPage: String, app: AppItemInfoBean) {
        val properties = arrayMapOf<String, String>()
        properties["AppShowPalace"] = fromPage
        properties.putAll(commonProperties(app))
        track(EVENTS_APP_UNINSTALL, properties)
    }

    /**
     * 取消卸载
     */
    fun onAppCancelUninstall(fromPage: String, app: AppItemInfoBean) {
        val properties = arrayMapOf<String, String>()
        properties["AppShowPalace"] = fromPage
        properties.putAll(commonProperties(app))
        track(APPSTORE_APP_UNINSTALL_CANCEL, properties)
    }

    /**
     * 首页侧边栏按钮
     * @param pageName
     * @param viewId
     */
    fun onHomeTabSelected(pageName: String, viewId: Int) {
        try {
//            val properties = arrayMapOf<String, String>()
//            properties["screen_name"] = pageName
//            properties["element_id"] = viewId.toString()
//            track(EVENTS_HOME_TAB_SELECTED, properties)
            onPageExposure(pageName)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 精品推荐：Banner页展示
     * @param bannerId
     * @param position banner位置
     * @param bannerShowTime banner展示时间
     * @param bannerEndTime banner结束时间
     * @param bannerActionStart 操作类型（进入）
     * @param bannerActionEnd 操作类型（离开）
     * @param bannerDuration 展示持续时间
     */
    fun onRecommendBannerShow(
        bannerId: String?, position: Int, bannerShowTime: String?, bannerEndTime: String?,
        bannerActionStart: String?, bannerActionEnd: String?, bannerDuration: Long,
    ) {
//        try {
//            val properties = arrayMapOf<String, String>()
//            properties["appStore_banner_ID"] = bannerId ?: ""
//            properties["appStore_banner_index"] = position.toString()
//            properties["appStore_banner_showtime"] = bannerShowTime ?: ""
//            properties["appStore_banner_endtime"] = bannerEndTime ?: ""
//            properties["appStore_banner_action_start"] = bannerActionStart ?: ""
//            properties["appStore_banner_action_end"] = bannerActionEnd ?: ""
//            properties["appStore_banner_duration"] = bannerDuration.toString()
//            track(EVENTS_BANNER_SHOW, properties)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    /**
     * 精品推荐：Banner点击
     * @param bannerId
     * @param position
     */
    fun onRecommendBannerClick(bannerId: String?, position: Int) {
//        try {
//            val properties = arrayMapOf<String, String>()
//            properties["appStore_banner_ID"] = bannerId ?: ""
//            properties["appStore_banner_index"] = position.toString()
//            properties["appStore_banner_clicktime"] = TimeUtils.currentTimeInString
//            track(EVENTS_BANNER_CLICK, properties)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    private fun track(eventName: String, properties: Map<String, Any>) {
        ThreadPoolUtil.runOnSubThread({
            try {
                if (!isSensorTrackInit) {
                    init(object : SensorCallback {
                        override fun onCallback(result: Boolean) {
                            if (result) {
                                AnalysisAPI.get().event.track(eventName, GsonUtils.toJson(properties), 0)
                            }
                        }
                    })
                }else{
                    AnalysisAPI.get().event.track(eventName, GsonUtils.toJson(properties), 0)
                }
            } catch (e: NullPointerException) {
                // 处理空指针异常
                e.logStackTrace()
            } catch (e: ClassCastException) {
                // 处理类型转换异常
                e.logStackTrace()
            } catch (e: IllegalStateException) {
                // 处理非法状态异常
                e.logStackTrace()
            } catch (e: Exception) {
                // 处理其他未知异常
                e.logStackTrace()
            }
        },3000L)
    }

    private fun commonProperties(app: AppItemInfoBean) = mapOf(
        //应用名称
        "AppName" to app.apkName,
        //应用ID
        "AppId" to app.appPid.toString(),
        //应用类型
//        "AppType" to app.appType,
        //应用分类
        "AppCategory" to app.categoryName,
        //版本ID
        "AppVersionId" to app.appVersionPid.toString(),
        //应用版本
        "AppVersion" to app.apkVersionName,
        //应用大小
        "AppSize" to app.apkSize.toString(),
        //更新时间
        "AppUpdateTime" to "${app.updateTime}",
        //应用价格
        "AppPrice" to "0",
        //开发者
        "AppDeveloper" to app.appDeveloper,
        //是否已安装
        "AppIsInstalled" to yesOrNo(InstallAppManager.isAlreadyInstalled(app.apkPackageName)),
        //是否可更新
        "AppIsUpdatable" to yesOrNo(InstallAppManager.isRequiredUpdate(app.apkPackageName, app.apkVersion?.toLong())),
    )

    private fun yesOrNo(result: Boolean): String = if (result) "是" else "否"
}
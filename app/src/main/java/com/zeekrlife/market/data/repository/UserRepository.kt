package com.zeekrlife.market.data.repository

import android.util.ArraySet
import com.zeekrlife.market.utils.CarManager
import com.zeekr.car.api.DeviceApiManager
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.ValueKey
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.*
import com.zeekrlife.net.api.ApiPagerResponse
import com.zeekrlife.net.api.NetUrl
import org.json.JSONArray
import org.json.JSONObject
import rxhttp.wrapper.coroutines.Await
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponse

/**
 * 描述　: 数据仓库
 */
object UserRepository {

    /**
     * 车辆信息
     */
    @Deprecated("deviceInfo Deprecated")
    private val deviceInfo by lazy { CacheExt.getOpenApi()?.deviceInfo }

    /**
     * 车辆款式
     * DeviceApi.getInstance().vehicleModel
     */
    private val vehicleModel = /*deviceInfo?.getVehicleModel ?: */""

    /**
     * 车辆系列，必填,如：DC1E-012
     * DeviceApi.getInstance().vehicleType
     */
    private val vehicleType = deviceInfo?.getVehicleType.run {
        if (isNullOrEmpty()) "BX1E" else this
    }

    /**
     * 启动页协议详情
     * code:协议编码
     */
    fun getProtocolInfo(code:String): Await<ProtocolInfoBean> {
        val json = JSONObject().apply {
            put("code", code)
            put("carModel", DeviceApiManager.getInstance().vehicleModel ?: "")

        }
        return RxHttp.postJson(NetUrl.PROTOCOL_INFO)
            .setDomainToBaseUrlOperateIfAbsent()
            .addAll(json.toString())
            .toResponse()
    }

    /**
     * 用户签订协议
     */
    fun postProtocolSign(
        user: ProtocolInfoBean?,
        protocol: ProtocolInfoBean?,
        userInfo: OpenApiUserInfo?,
        status: String
    ): Await<MutableList<ProtocolSignBean>> {
        val jSONObject1 = JSONObject().apply {
            put("code", Constants.APPSTOREPP_BX1E)
            put("version", protocol?.version)
            put("status", status)
            put("vin",  DeviceApiManager.getInstance().deviceAPI?.vin ?: "")
            put("userCode", userInfo?.userId)
            put("userName", userInfo?.username)
        }

        val signDto = JSONArray().apply {
            put(jSONObject1)
        }
        val json = JSONObject().apply {
            put("signDto", signDto)
        }

        return RxHttp.postJson(NetUrl.PROTOCOL_SIGN)
            .setDomainToBaseUrlOperateIfAbsent()
            .addAll(json.toString())
            .toResponse()
    }
//    fun postProtocolSign(
//        user: ProtocolInfoBean?,
//        protocol: ProtocolInfoBean?,
//        userInfo: OpenApiUserInfo?,
//        status: String
//    ): Await<ProtocolSignBean> {
//        val codes = arrayOf(Constants.APPSTOREUA_BX1E, Constants.APPSTOREPP_BX1E)
//        val jSONObject1 = JSONObject().apply {
//            put("code", codes[0])
//            put("version", user?.version)
//        }
//        val jSONObject2 = JSONObject().apply {
//            put("code", codes[1])
//            put("version", protocol?.version)
//        }
//        val jsonArray = JSONArray().apply {
//            put(jSONObject1)
//            put(jSONObject2)
//        }
//
//        val json = JSONObject().apply {
//            put("protocolInfos", jsonArray)
//                .put("userCode", userInfo?.userId)
//                .put("userName", userInfo?.username)
//                .put("status", status)
//        }
//        return RxHttp.postJson(NetUrl.PROTOCOL_SIGN)
////            .setDomainToBaseUrlOperateIfAbsent()
//            .addAll(json.toString())
//            .toResponse()
//    }

    /**
     * 登录
     */
    fun login(userName: String, password: String): Await<UserInfo> {
        return RxHttp.postForm(NetUrl.LOGIN)
            .add("username", userName)
            .add("password", password)
            .toResponse()
    }

    /**
     * 获取列表信息
     */
    fun getList(pageIndex: Int): Await<ApiPagerResponse<Any>> {
        return RxHttp.get(NetUrl.HOME_LIST, pageIndex)
            .toResponse()
    }

    /**
     * 获取分类列表信息
     */
    fun getHomeList(screenType: String = CarManager.ScreenType.getValueByName("CSD")): Await<MutableList<HomeItemCategoryBean>> {
        return RxHttp.get(NetUrl.HOME_CATEGORT_LIST)
            .addHeader("x-machine-position", screenType)
            .toResponse()
    }

    /**
     * 获取分类信息对应的app
     */
    fun getCategoryList(pageIndex: Int, categoryPid: Int,screenType: String = CarManager.ScreenType.getValueByName("CSD")): Await<ApiPagerResponse<AppItemInfoBean>> {
        val json = JSONObject()
        json.put("categoryPid", categoryPid)
        json.put("pageNum", pageIndex)
        json.put("pageSize", ValueKey.REQUEST_PAGE_SIZE)
        json.put("vehicleType", vehicleType)
        json.put("vehicleModel", vehicleModel)
        return RxHttp.postJson(NetUrl.APP_LIST, pageIndex)
            .addAll(json.toString())
            .addHeader("x-machine-position", screenType)
            .toResponse()
    }

    /**
     * 搜索页对应的app
     */
    fun getSearchList(pageIndex: Int, searchInfo: String,screenType: String = CarManager.ScreenType.getValueByName("CSD")): Await<ApiPagerResponse<AppItemInfoBean>> {
        val json = JSONObject()
        json.put("pageNum", pageIndex)
        json.put("pageSize", ValueKey.REQUEST_PAGE_SIZE)
        json.put("vehicleType", vehicleType)
        json.put("searchInfo", searchInfo)
        json.put("vehicleModel", vehicleModel)
        return RxHttp.postJson(NetUrl.APP_LIST, pageIndex)
            .addAll(json.toString())
            .addHeader("x-machine-position", screenType)
            .toResponse()
    }

    /**
     * 小程序注册&加签
     */
    fun getAppletDeviceSignature(): Await<AppletSignatureInfo> {
        return RxHttp.get(NetUrl.APPLET_DEVICE_SIGNATURE)
            .toResponse()
    }
    /**
     * 上架的小程序列表
     */
    fun getAppletIdSet(): Await<ArraySet<String>> {
        return RxHttp.get(NetUrl.APPLET_USABLE_APPLET_IDS)
            .toResponse()
    }

}


package com.zeekrlife.net.api

import com.zeekr.basic.appContext
import com.zeekr.car.tsp.TspAPI
import rxhttp.wrapper.annotation.DefaultDomain
import rxhttp.wrapper.annotation.Domain

object NetUrl {

    const val BASE_URL_DEV = "https://snc-api-gw-dev.zeekrlife.com/app-market/openserver/"
    const val BASE_URL_TESTING = "https://snc-api-gw-sit.zeekrlife.com/app-market/openserver/"
    const val BASE_URL_PRODUCTION = "https://snc-api-gw.zeekrlife.com/app-market/openserver/"


    /**
     * 运营点位单独接口
     *
     * : https://snc-api-gw-dev.zeekrlife.com/operationPoint/openApi
     *
     * : http://snc-api-gw-sit.zeekrlife.com/operationPoint/openApi
     *
     * : http://snc-api-gw.zeekrlife.com/operationPoint/openApi
     */
    const val BASE_URL_OPERATION_POINT_DEV = "https://snc-api-gw-dev.zeekrlife.com/operationPoint/openApi/"
    const val BASE_URL_OPERATION_POINT_TESTING = "https://snc-api-gw-sit.zeekrlife.com/operationPoint/openApi/"
    const val BASE_URL_OPERATION_POINT_PRODUCTION = "https://snc-api-gw.zeekrlife.com/operationPoint/openApi/"

    @JvmField
    @DefaultDomain //设置为默认域名
    var BASE_URL = BASE_URL_TESTING

    @JvmField
    @Domain(name = "BaseUrlOperate") //运营点位域名
    var BASE_URL_OPERATE = TspAPI.create(appContext).envType.run {
        if (isDevelopment) BASE_URL_OPERATION_POINT_DEV
        else if (isTestingEnv) BASE_URL_OPERATION_POINT_TESTING
        else BASE_URL_OPERATION_POINT_PRODUCTION
    }


    private var sensorServerTestUrl = "https://touchpoint-api-test.zeekrlife.com/sa?project=app_market"
    private var sensorServerUrl = "https://touchpoint-api.zeekrlife.com/sa?project=app_market"

    var sensorServer = TspAPI.create(appContext).envType.run {
        if (isProductionEnv) sensorServerUrl
        else sensorServerTestUrl
    }


    const val API_VERSION_V1 = "v1"
    const val API_VERSION_V2 = "v2"

    //协议详情
//    const val PROTOCOL_INFO = "clientApi/${API_VERSION_V1}/protocol/info"
    const val PROTOCOL_INFO = "protocol/info"

    //用户签订协议
//    const val PROTOCOL_SIGN = "clientApi/${API_VERSION_V1}/protocol/sign"
    const val PROTOCOL_SIGN = "protocol/sign"

    //登录
    const val LOGIN = "user/login"

    //获取首页列表数据
    const val HOME_LIST = "article/list/%1\$d/json"

    //获取商城首页分类列表
    const val HOME_CATEGORT_LIST = "clientApi/${API_VERSION_V1}/category/allList"

    //app详情页
    const val HOME_APP_DETAIL = "/clientApi/app/detail/%1\$d/%2\$d"

    const val UPLOAD_URL = "http://t.xinhuo.com/index.php/Api/Pic/uploadPic"

    const val DOWNLOAD_URL = "http://update.9158.com/miaolive/Miaolive.apk"

    //获取App详情
    const val APP_DETAIL = "clientApi/${API_VERSION_V1}/app/detail/app/%1\$d"

    //获取App列表
    const val APP_LIST = "clientApi/${API_VERSION_V1}/app/appList"

    //查询已安装的应用是否支持双音源
    const val APP_QUERY_DUAL_AUDIO = "clientApi/${API_VERSION_V1}/app/queryDualAudio"

    //精品推荐接口
    const val APP_QUERY_ADVERTISEMNETS = "clientApi/${API_VERSION_V1}/app/queryAdvertisements"

    //获取应用的安裝数字签名
    const val APP_INSTALL_DIGITAL_SIGNATURE = "clientApi/${API_VERSION_V2}/app/signature"

    //获取应用属性
    const val APP_ATTRIBUTES = "clientApi/${API_VERSION_V2}/app/attributes"

    //预置应用版本:检测app更新接口
    const val APP_UPDATE_APP_LIST = "clientApi/${API_VERSION_V1}/ota/update/appList"

    //预置应用版本:按车辆信息获取所有适用的包路径列表
    const val APP_GET_ALL_PACKAGES = "clientApi/${API_VERSION_V1}/ota/getAllPackagePath"

    //小程序设备注册&验签
    const val APPLET_DEVICE_SIGNATURE = "clientApi/${API_VERSION_V1}/applet/acquireDeviceSignature"

    //小程序上架列表
    const val APPLET_USABLE_APPLET_IDS = "clientApi/${API_VERSION_V1}/applet/usableAppletIds"
}
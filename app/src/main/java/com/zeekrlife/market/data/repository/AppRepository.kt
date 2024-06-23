package com.zeekrlife.market.data.repository

import com.zeekr.basic.appContext
import com.zeekrlife.market.utils.CarManager
import com.zeekr.car.api.DeviceApiManager
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.EncryptUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.market.data.ValueKey
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.entity.ApkInfo
import com.zeekrlife.market.data.request.DualAudioAppParams
import com.zeekrlife.market.data.request.GetAdvertisemnetsParams
import com.zeekrlife.market.data.request.GetAppAttributesParams
import com.zeekrlife.market.data.request.GetAppsParams
import com.zeekrlife.market.data.request.GetDigitalSignatureParams
import com.zeekrlife.market.data.request.GetUpdateAppListParams
import com.zeekrlife.market.data.response.AdvertisementInfoBean
import com.zeekrlife.market.data.response.AppAttributes
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.data.response.InstallDigitalSignature
import com.zeekrlife.net.api.ApiPagerResponse
import com.zeekrlife.net.api.NetUrl
import rxhttp.wrapper.coroutines.Await
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponse

object AppRepository {

    /**
     * 用户信息
     */
    private val userInfo by lazy { CacheExt.getOpenApi()?.userInfo }

    /**
     * 车辆信息
     */
    private val deviceInfo by lazy { CacheExt.getOpenApi()?.deviceInfo }

    /**
     * 车辆款式
     * DeviceApi.getInstance().vehicleModel
     */
    private val vehicleModel = /*deviceInfo?.getVehicleModel ?:*/ ""

    /**
     * 车辆系列，必填,如：DC1E-012
     * DeviceApi.getInstance().vehicleType
     */
    private val vehicleType = deviceInfo?.getVehicleType.run {
        if (isNullOrEmpty()) "BX1E" else this
    }

    /**
     * 车辆VIN
     */
    private val carVin = DeviceApiManager.getInstance().deviceAPI.vin.run {
        if (isNullOrEmpty()) "" else this
    }

    /**
     * 根据包名查询Apk信息
     */
    fun getApkInfo(packageName: String): ApkInfo? =
        ApkUtils.getAppInfo(appContext, packageName)?.run {
            ApkInfo(
                name ?: "unKnow", icon, packageName,
                versionName, versionCode, isSD, isUser
            )
        }

    /**
     * 查询本地应用列表
     * @param filterNotActive 是否过滤不能启动App
     */
    fun getAllLocalApps(filterNotActive: Boolean = false): Map<String, ApkInfo> =
        ApkUtils.getAllAppsInfo(appContext, filterNotActive).asSequence().filter {
            val pkgName = it.packageName ?: ""
            !appContext.packageName.equals(pkgName)
        }.associate { app ->
            Pair(
                app.packageName ?: "", ApkInfo(
                    app.name ?: "unKnow", app.icon, app.packageName,
                    app.versionName, app.versionCode, app.isSD, app.isUser
                )
            )
        }

    /**
     * 应用商城自己的检测更新接口
     */
    fun checkUpdate(): Await<ApiPagerResponse<AppItemInfoBean>> {
        val packageNames = arrayOf(ApkUtils.getAppInfo(appContext)?.packageName ?: "")
        return getApps(packages = packageNames, pageNum = 1)
    }

    /**
     * 应用详细信息
     * @param appVersionId 应用版本ID
     */
    fun getAppDetail(appVersionId: Long,screenType: String = CarManager.ScreenType.getValueByName("CSD")): Await<AppItemInfoBean> =
        RxHttp.get(NetUrl.APP_DETAIL, appVersionId)
//            .addHeader(screenType)
            .toResponse()

    /**
     * 获取应用列表信息
     * @param pageNum 当前页码
     * @param ids 应用版本ID
     * @param packages 应用包名
     * @param categoryId 所属应用分类ID
     * @param searchInfo 搜索的名称，可以是应用名称或拼音首字母
     */
    fun getApps(
        pageNum: Int,
        appIds: LongArray? = null,
        ids: IntArray? = null,
        packages: Array<String>? = null,
        categoryId: Int? = null,
        searchInfo: String? = null,
        pageSize: Int = ValueKey.REQUEST_PAGE_SIZE,
        screenType: String = CarManager.ScreenType.getValueByName("CSD")
    ): Await<ApiPagerResponse<AppItemInfoBean>> {
        return RxHttp.postJson(NetUrl.APP_LIST).addAll(
            GsonUtils.toJson(
                GetAppsParams(
                    ids = ids, appIds = appIds, appPackageNames = packages, categoryPid = categoryId,
                    pageNum = pageNum, searchInfo = searchInfo, pageSize = pageSize,
                    vehicleModel = vehicleModel, vehicleType = vehicleType
                )
            )
        )
//            .addHeader(screenType)
            .toResponse()
    }

    /**
     * 查询已安装的应用是否支持双音源
     * @param packages
     */
    fun getDualAudioApps(
        packages: List<DualAudioAppParams.App>,
    ): Await<Map<String, String>> {
        return RxHttp.postJson(NetUrl.APP_QUERY_DUAL_AUDIO).addAll(
            GsonUtils.toJson(DualAudioAppParams(packages, vehicleModel, vehicleType))
        ).toResponse()
    }

    /**
     * 运营点位业务
     * 精品推荐列表
     * @param pointCodes 点位码
     */
    fun getRecommendAppList(
        pointCodes: Array<String>? = arrayOf(),
        screenType: String = CarManager.ScreenType.getValueByName("CSD")
    ): Await<List<AdvertisementInfoBean>> {
        return RxHttp.postJson(NetUrl.APP_QUERY_ADVERTISEMNETS).addAll(
            GsonUtils.toJson(GetAdvertisemnetsParams(pointCodes, vehicleType))
        )
            .addHeader("x-machine-position", screenType)
            .toResponse()
    }

    /**
     * 用于安装校验：获取安装校验签名后，导入安装校验服务
     * @param packageName 包名
     * @param apkVersion 应用版本
     * @param apkMd5 应用文件MD5
     */
    fun getInstallDigitalSignature(packageName: String, apkVersion: Long, apkSha256: String,screenType: String = CarManager.ScreenType.getValueByName("CSD")): Await<InstallDigitalSignature> {
        val userId = userInfo?.userId ?: ""
        return RxHttp.postJson(NetUrl.APP_INSTALL_DIGITAL_SIGNATURE).addAll(
            GsonUtils.toJson(
                GetDigitalSignatureParams(
                    packageName, apkVersion, apkSha256, userId, carVin, 0, vehicleModel, vehicleType
                )
            )
        )
            .addHeader("x-machine-position", screenType)
            .toResponse()
    }

    /**
     * 查询应用属性
     * @param packages  ${包名}_${VersionCode}
     */
    fun getAppAttributes(packages: List<String>,sha256: String? = "",screenType: String = CarManager.ScreenType.getValueByName("CSD")): Await<Map<String, AppAttributes>> {
        return RxHttp.postJson(NetUrl.APP_ATTRIBUTES).addAll(
            GsonUtils.toJson(GetAppAttributesParams(packages, sha256,vehicleType))
        )
            .addHeader("x-machine-position", screenType)
            .toResponse()
    }

    /**
     * 请求更新列表：自动 + 强制更新
     */
    fun getUpdateAppList(packages: String, isAutoUpdate: Boolean, vin: String, vehicleType: String ,screenType: String = CarManager.ScreenType.getValueByName("CSD"))
        : Await<List<AppItemInfoBean>> {
        val packagesMD5 = EncryptUtils.encryptMD5ToString(packages)
        return RxHttp.postJson(NetUrl.APP_UPDATE_APP_LIST).addAll(
            GsonUtils.toJson(
                GetUpdateAppListParams(
                    appPackageNames = packages, appPackageMd5 = packagesMD5,
                    autoUpdateSwitch = if (isAutoUpdate) 1 else 0,
                    vin = vin, vehicleType = vehicleType
                )
            )
        )
            .addHeader("x-machine-position", screenType)
            .toResponse()
    }

    /**
     * 请求市场上架的全部应用包名
     */
    fun getAllAppPackages(): Await<List<String>> {
        return RxHttp.get(NetUrl.APP_GET_ALL_PACKAGES).toResponse()
    }
}
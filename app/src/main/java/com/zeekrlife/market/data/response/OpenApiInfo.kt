package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OpenApiInfo(
    var userInfo: OpenApiUserInfo,
    var deviceInfo: OpenApiDeviceInfo,
    ) : Parcelable

@Parcelize
data class OpenApiUserInfo(

    var userId: String? = null,
    var username: String? = null,
    var avatar: String? = null,
    var mobile: String? = null,
    var sex: String? = null,
    var identity: String? = null,
    var expand: String? = null,
    var hasLogin: Boolean = false,
    var getToken: String? = null,
    var loginout: Boolean = false,
    var getData: String? = null,

//    var getUserId: String? = null,
//    var getNickname:  String? = null,
//    var getAvatarUrl: String? = null,
//    var getMobile:  String? = null,
//    var getEmail:  String? = null,
//
//    var getSex: String? = null,
//    var getBirthday:  String? = null,
//    var getAddress:  String? = null,
//    var getIsLogin: Boolean = false,
//    var getToken:  String? = null,
//
//    var getRefreshToken:  String? = null,

) : Parcelable

@Parcelize
@Deprecated("use DeviceApi")
data class OpenApiDeviceInfo(

//    var getIhuId: String? = null,
//    var getVIN:  String? = null,
//    var getOperatorCode: String? = null,
//    var getSupplierCode:  String? = null,
//    var getProjectCode:  String? = null,
//
//    var getVehicleType: String? = null,
//    var getVehicleModel:  String? = null,
//    var getXDSN:  String? = null,
//    var getECarXDeviceId: String? = null,
//    var getIMEI:  String? = null,
//
//    var getIMSI:  String? = null,
//    var getICCID:  String? = null,
//    var getMSISDN:  String? = null,
//    var getVehicleTypeConfig:  String? = null,

//    var getDayNightMode: IDayNightMode?,

    var getIHUID: String? = null,

    var getVIN: String? = null,

    var getDVRID: String? = null,

    var getXDSN: String? = null,

    var getICCID: String? = null,

    var getVehicleType: String? = null,

    var getProjectCode: String? = null,

    var getSupplierCode: String? = null,

    var getOperatorCode: Int = -1,

    var getOperatorName: String? = null,

    var getOpenIHUID: String? = null,

    var getOpenVIN: String? = null,

    var getIHUSerialNo: String? = null,

    var getDeviceServiceIDJson: String? = null,

    var getVehicleTypeConfig: String? = null,

    ) : Parcelable
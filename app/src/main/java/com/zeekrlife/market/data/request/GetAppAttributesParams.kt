package com.zeekrlife.market.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Lei.Chen29
 * @date 2022/6/27 19:45
 * description：应用属性
 * @param apkPackageVersionInfos  apkPackageName +'_'+ apkVersion
 * @param vehicleType 入参为DC1E 车系数据
 */
@Parcelize
data class GetAppAttributesParams(
    val apkPackageVersionInfos: List<String>,
    val sha256: String? = "",
    val vehicleType: String? = ""
) : Parcelable
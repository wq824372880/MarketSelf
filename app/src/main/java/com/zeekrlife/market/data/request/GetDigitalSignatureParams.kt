package com.zeekrlife.market.data.request

/**
 * @author Lei.Chen29
 * @date 2022/6/20 18:30
 * description：安装前的数字签名获取
 */
data class GetDigitalSignatureParams(
    //包名
    val packageName: String,
    //版本号
    val apkVersion: Long? = 0L,
    val apkSha256: String? = "",
    //车主唯一凭据
    val userId: String? = "",
    //车辆编码,（必传虽然可能不用，但考虑为后续计入安装统计预留）
    val carVin: String? = "",
    //签名依据类型，0 根据车辆VIN，1 根据车主唯一凭据,不传默认0
    val signType: Int? = 0,
    //车辆款式，预留暂时不传
    val vehicleModel: String? = "",
    //车辆型号必填,如DC1E，防止此车型不允许安装相应应用
    val vehicleType: String,
)

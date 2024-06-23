package com.zeekrlife.market.data.response

/**
 * @author Lei.Chen29
 * @date 2022/6/20 18:45
 * description：安装证书
 * @param apkMd5 小写的md5
 * @param sign 数字签名，为空时说明不是应用市场配置过的应用不允许安装，
 * content约定为：packageName_md5_{carVin}{userId}
 * （后面两个视signType二选一),
 * 签名算法约定为 SHA1withRSA ,publickey 找云端约定
 * @param signType 签名依据类型，0 根据车辆carVin，1 根据车主唯一凭据userId
 */
data class InstallDigitalSignature(
    val apkMd5: String? = "",
    val sign: String? = "",
    val signType: Int? = 0
)
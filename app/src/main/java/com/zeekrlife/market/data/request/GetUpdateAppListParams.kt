package com.zeekrlife.market.data.request

/**
 * @author Lei.Chen29
 */
data class GetUpdateAppListParams(
    //请车机将包名按名称排序,并使用','分隔
    val appPackageNames: String = "",
    //根据包路径列表appPackageNames,计算出来的固定32位MD5值，用于缓存key
    val appPackageMd5: String = "",
    //用户自动更新开关,0 关闭,1 开启
    val autoUpdateSwitch: Int = 0,
    val vin: String = "",
    val vehicleType: String = ""
)

package com.zeekrlife.market.data.request

/**
 * @author Lei.Chen29
 * @date 2022/5/20 15:39
 * description：
 */
data class DualAudioAppParams(
    val infos: List<App>,
    val vehicleModel: String? = "",
    val vehicleType: String? = ""
) {
    class App(val apkPackageName: String, val apkVersion: Long)
}


package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Lei.Chen29
 * @date 2022/6/27 19:45
 * description：应用属性
 * @param supportGamepad 是否支持手柄：0-不支持，1-支持
 * @param supportDrivingPassengerUser 行车中是否允许使用（滑移屏副驾位）：0-不允许，1-允许
 * @param supportDrivingUser          行车中是否允许使用（主驾位）：0-不允许，1-允许
 * @param dualSoundSource 是否支持双音源：0-不支持，1-支持
 */
@Parcelize
data class AppAttributes(
    val apkPackageName: String? = null,
    val apkVersion: Long? = -1,
    val supportGamepad: Int? = -1,
    val supportDrivingPassengerUser: Int? = -1,
    val supportDrivingUser: Int? = -1,
    val dualSoundSource: Int? = -1
) : Parcelable
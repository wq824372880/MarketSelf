package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfigBean(
    var coinSize: Int = 2,
    val createBy: Int = 0,
    val createTime: String = "",
    val deleted: Boolean = false,
    var desc: String = "",
    val enabled: Int = 0,
    val id: Int = 0,
    val pictureUrl: String = "",
    val priority: String = "",
    var type: String = "h5",
    val updateBy: Int = 0,
    val updateTime: String = "",
    var value: String = "",
) : Parcelable
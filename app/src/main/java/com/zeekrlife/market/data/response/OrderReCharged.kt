package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderReCharged(
    var orderNo: String? = null,
    var orderStatus:String? = null,
    var userId: String? = null,
    var description: String? = null,
    var orderSource: String? = null,
    var isRecharged: Boolean? = false,
): Parcelable
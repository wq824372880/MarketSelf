package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppletSignatureInfo(
   var appId: String? = null,
   var productId: String? = null,
   var sign: String? = null,
) : Parcelable
package com.zeekrlife.ampe.core.bean
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BridgeCallLocation(
//    var __appxDomain:String?= null,
    var address:String?= null,
    var latitude:String?= null,
    var longitude:String?= null
): Parcelable
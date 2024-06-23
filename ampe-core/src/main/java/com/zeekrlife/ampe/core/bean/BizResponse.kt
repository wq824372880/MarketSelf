package com.zeekrlife.ampe.core.bean
import android.os.Parcelable
import kotlinx.parcelize.Parcelize



@Parcelize
data class BizResponse(
    val bizContent: BizContent? = null,
    val deviceProductId: Long? = 0,
    val messageId: String? = null,
    val deviceId: String? = null
): Parcelable

@Parcelize
data class BizContent(
    val sceneCode: String? = null,
    val serviceName: String? = null
): Parcelable
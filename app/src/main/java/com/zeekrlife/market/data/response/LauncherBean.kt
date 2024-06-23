package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RichTextBean(
    val code: String?= null,
    val title: String?= null,
    val outline: String?= null,
    val context: String?= null
) : Parcelable


@Parcelize
data class ProtocolInfoBean(
    var needUpdate: Boolean = false,
    val code: String? = null,
    val name: String? = null,
    val hmi: String? = null,
    val terminalType: String? = null,
    var version: String? = null,
    val serialOrder: Int = 0,
    val ruleType: Int = 0,
    val startTime: String? = null,
    val endTime: String? = null,
    val protocolFormat: String? = null,
    val context: String? = null,
    val remark: String? = null,
    val richText: String? = null,
    var richInfo: RichTextBean? = null,
    var h5Url: String? = null,
) : Parcelable


@Parcelize
data class ProtocolSignBean(
    val code: String?= null,
    val user: String?= null,
    val version: String?= null,
    val success: String?= null,
    val msg: String?= null,
    val status: String?= null
) : Parcelable



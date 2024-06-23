package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppletShortcutListBean(
    val appletShortcutList: MutableMap<String, AppletShortcutBean>? = null
) : Parcelable

@Parcelize
data class AppletShortcutBean(
    val appletId: String,
    val appletName: String,
    val appletDesc: String,
    val appletUrl: String,
) : Parcelable
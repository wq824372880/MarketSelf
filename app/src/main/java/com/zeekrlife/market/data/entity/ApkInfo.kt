package com.zeekrlife.market.data.entity

import android.graphics.drawable.Drawable
import com.zeekrlife.market.data.response.AppItemInfoBean

/**
 * @param name        名称
 * @param icon        图标
 * @param packageName 包名
 * @param versionName 版本号
 * @param versionCode 版本Code
 * @param isSD        是否安装在SD卡
 * @param isUser      是否是用户程序
 */
data class ApkInfo(
    val name: String,
    val icon: Drawable? = null,
    val packageName: String? = null,
    val versionName: String? = null,
    val versionCode: Int = 0,
    val isSD: Boolean = false,
    val isUser: Boolean = false,
    val apkUrl: String? = null,
    var appId: Int = 0
)
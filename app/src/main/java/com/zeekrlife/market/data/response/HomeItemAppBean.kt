package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 应用程序列表 item
 */
@Parcelize
data class HomeItemAppBean(
    var apkMd5:	String? = null,
    var apkName:String?=null,
    var apkPackage:String?=null,
    var apkSize:Int = 0,
    var apkUrl :String? =null,
    var apkVersion:Int = 0,
    var apkVersionName:String? = null,
    var appPid:Int= 0,

    var appVersionPid:Int = 0,
    var auditDesc:String? = null,
    var auditStatus:Int = 0,
    var auditorId:Int = 0,
    var createTime:String?=null,

    var createTimeDisplay:String? = null,
    var creatorId:Int = 0,
    var delFlag:Int=0,
    var desc:String?=null,
    var displayStatus:Int=0,

    var displayTime:String? = null,
    var displayTimeDisplay:String? = null,
    var editorId:Int=0,
    var expectDisplayTime:String?=null,
    var expectDisplayTimeDisplay:String?=null,

    var icon:String? = null,
    var id:Int = 0,
    var inputAppName:String?=null,
    var labelName:String?=null,
    var labelPid:Int =0,

    var minAdaptApi:String? = null,
    var minAndroid:String? = null,
    var minHmi:Int = 0,
    var minOpenApi:String?=null,
    var previewPic:String?=null,

    var reviewApplicationNotes:String? = null,
    var slogan:String? = null,
    var suitProduct:String?=null,
    var updateTime:String?=null,
    var updateTimeDisplay:String?=null,

    var updates:String?=null,
    var versionRemark:String?=null,
) : Parcelable

/**
 * 小程序列表 item
 */
@Parcelize
data class HomeAppletItemBean(
    var appletPid:Int= 0,
    var auditStatus:Int = 0,
    var createTime:String?=null,
    var createTimeDisplay:String? = null,
    var creatorId:Int = 0,

    var delFlag:Int=0,
    var devName:String?=null,
    var displayStatus:Int=0,
    var editorId:Int=0,
    var id:Int =0,

    var labelPid:Int =0,
    var mailSendStatus:Int = 0,
    var miniAppId:String? = null,
    var miniAppLogo:String? = null,
    var miniAppName:String? = null,

    var miniAppSlogan:String?=null,
    var online:Int =0,
    var suitProduct:String?=null,
    var supportAmpe:Int=0,
    var updateTime:String?=null,

    var updateTimeDisplay:String?=null,


) : Parcelable
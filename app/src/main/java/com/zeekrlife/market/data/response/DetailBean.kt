package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailBean(
    var appDeveloper:String?=null,
    var apkName:String?=null,
    var appPid:Int= 0,
    var appPrice:Int= 0,
    var auditDesc:String?=null,

    var auditStatus:Int = 0,
    var auditTime:String?=null,
    var auditTimeDisplay:String?=null,
    var auditorId:Int = 0,
    var createTime:String?=null,

    var createTimeDisplay:String? = null,
    var creatorId:Int = 0,
    var delFlag:Int=0,
    var displayStatus:Int=0,

    var editorId:Int=0,
    var id:Int = 0,

    var labelPid:Int =0,
    var reviewApplicationNotes:String?=null,
    var updateTime:String?=null,
    var updateTimeDisplay:String?=null,
) : Parcelable

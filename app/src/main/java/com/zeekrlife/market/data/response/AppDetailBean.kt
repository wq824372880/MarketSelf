package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppDetailBean(
    //开发者
    val appDeveloper: String? = "",
    //分类名称
    val appName: String? = "",
    //审核通过前ID
    val appId: Int,
    //价格
    val appPrice: Float,
    //审核员备注
    val auditDesc: String?,
    //审核状态：0-已创建，1-初创审核中，2-修改审核中，3-下架审核中，4-重新上架审核中，5-已审核，6-已修改
    val auditStatus: Int,
    //审核时间
    val auditTime: String?,
    val auditTimeDisplay: String?,
    //审核人员ID
    val auditorId: Int,
    //创建时间
    val createTime: String?,
    val createTimeDisplay: String?,
    //创建人ID
    val creatorId: Int,
    //状态,0使用中，-id已删除
    val delFlag: Int,
    //上下架状态：0-未上架，1-已上架
    val displayStatus: Int,
    //修改人ID
    val editorId: Int,
    val id: Int,
    //所属分类ID
    val labelPid: Int,
    //审核申请人员备注
    val reviewApplicationNotes: String?,
    //修改时间
    val updateTime: String?,
    val updateTimeDisplay: String?,
) : Parcelable
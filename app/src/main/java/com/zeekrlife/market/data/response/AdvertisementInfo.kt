package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Lei.Chen29
 * @date 2022/6/10 11:42
 * description：精品推荐
 */
@Parcelize
data class AdvertisementInfoBean(
    //广告信息
    var advertisementApiDTOS: ArrayList<AdvertisementDot>? = null,
    //点位编号
    var pointCode: String? = null,
    //点位名称
    var pointName: String? = null,
    //点位示意图
    var pointPic: String? = null,
) : Parcelable

@Parcelize
data class AdvertisementDot(
    //广告位编码
    val code: String? = null,
    //主标题
    val mainTitle: String? = null,
    val mediaTypes: List<MediaType>? = null,
    val order: Int? = 0,
    //概述
    val outline: String? = null,
    //点位编号
    val pointCode: String? = null,
    //备注
    val remark: String? = null,
    //副标题
    val subTitle: String? = null,
    //广告位主题
    val title: String? = null
) : Parcelable

@Parcelize
data class MediaType(
    //id
    val id: Int? = null,
    //安卓文件
    val androidFile: String? = null,
    //安卓文件封面
    val androidPic: String? = null,
    //应用ID
    val appId: String? = null,
    //指定跳转地址.应用指定地址
    val appSource: String? = null,
    //版本正式上下架信息
    val appVersionInfo: AppItemInfoBean? = null,
    //是否跳转应用：0不跳转，1跳转
    val beSlip: Int? = null,
    //业务编码
    val bussinessCode: String? = null,
    //创建时间
    val createTime: String? = null,
    //媒体类型 app
    val mediaType: String? = null,
    //点击跳转地址 H5，富文本
    val source: String? = null,
    //修改时间
    val updateTime: String? = null,
) : Parcelable
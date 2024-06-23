package com.zeekrlife.market.data.response

import android.os.Parcelable
import com.zeekrlife.task.base.bean.TaskInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppItemInfoBean(
    //apkMd5
    var apkMd5: String? = "",
    //sha256
    var sha256: String? = "",
    //apkName
    var apkName: String? = "",
    //apk包名
    val apkPackageName: String? = "",
    //apk包签名值 apkSha1，sha1算法
    val apkSign: String? = "",
    //apk包文件大小，单位MB，解析获得
    var apkSize: Double? = 0.0,
    //apk包文件URL
    var apkUrl: String? = "",
    //apk包版本号
    var apkVersion: String? = "",
    //apk包版本号名称，解析获得
    var apkVersionName: String? = "",
    //应用名称,拼音首字母
    val appChSpell: String? = "",
    //应用描述
    val appDesc: String? = "",
    //开发者
    val appDeveloper: String? = "",
    //所属应用ID
    val appPid: Int = 0,
    //所属应用版本ID
    val appVersionPid: Int = 0,
    //分类名称
    var categoryName: String? = "",
    //所属应用分类ID
    var categoryPid: Int = 0,
    //创建时间
    val createTime: String? = "",
    val createTimeDisplay: String? = "",
    //最近上架时间
    val displayTime: String? = "",
    val displayTimeDisplay: String? = "",
    //是否支持双音源 ：0-不支持，1-支持
    var dualSoundSource: Int = 0,
    //期待上架的时间
    val expectDisplayTime: String? = "",
    val expectDisplayTimeDisplay: String? = "",
    //是否强制更新:0否,1是
    val forcedUpdate: Int = 0,
    //是否将图标隐藏(不展示在应用市场),0否,1是
    val hideIcon: Int = 0,
    // 应用图标地址
    var icon: String? = "",
    var id: Long = -1,
    //手填输入的应用名称
    val inputAppName: String? = "",
    //最大可用安卓系统版本
    val maxAndroid: String? = "",
    //最小可用Adaptapi版本
    val minAdaptApi: String? = "",
    //最小可用安卓系统版本
    val minAndroid: String? = "",
    //最小可用HMI版本
    val minHmi: Int = -1,
    //最小可用openapi版本
    val minOpenApi: String? = "",
    val miniAppId:Long? = 0,
    //应用浏览图标地址，多个图标以‘,’分隔
    val previewPic: String? = "",
    //隐私政策
    val privacyPolicy: String? = "",
    //一句话亮点
    var slogan: String? = "",
    //适用车型,多个以‘,’分隔
    val suitProduct: String? = "",
    //是否在应用中心隐藏图标：0-不支持，1-支持
    val supportAppCenterHideIcon: Int = -1,
    //行车中是否允许副驾使用（滑移屏副驾位）：0-不允许，1-允许
    var supportDrivingPassengerUser: Int = -1,
    //行车中是否允许主驾使用（主驾位）：0-不允许，1-允许
    var supportDrivingUser: Int = -1,
    //是否支持手柄：0-不支持，1-支持
    val supportGamepad: Int = -1,
//    //是否支持卸载：0-不支持，1-支持
//    var supportUninstall: Int = -1,
    //修改时间
    val updateTime: Long? = 0,
    var updateTimeDisplay: String? = "",
    //更新内容
    var updates: String? = "",
    //备注，手输
    val versionRemark: String? = "",
    var taskInfo: TaskInfo? = null,
    //0 应用   1 小程序
    var dataType: Int = 0,
) : Parcelable
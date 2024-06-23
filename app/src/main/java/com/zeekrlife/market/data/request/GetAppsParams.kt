package com.zeekrlife.market.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @param ids 正式应用版本ID
 * @param appIds 所属应用ID
 * @param appPackageNames 应用包名
 * @param categoryPid 所属应用分类ID
 * @param pageNum 当前页码
 * @param searchInfo 搜索的名称，可以是应用名称或拼音首字母
 * @param pageSize 每页数据量
 * @param type 查询的应用类型，1 应用，2 小程序
 * @param vehicleModel 车辆款式
 * @param vehicleType 车辆型号必填,如DC1E
 */
@Parcelize
data class GetAppsParams(
    val ids: IntArray? = null,
    val appIds: LongArray? = null,
    val appPackageNames: Array<String>? = null,
    val categoryPid: Int? = null,
    val pageNum: Int,
    val searchInfo: String? = "",
    val pageSize: Int,
    val type: Int? = null,
    val vehicleModel: String? = "",
    val vehicleType: String? = ""
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetAppsParams

        if (ids != null) {
            if (other.ids == null) return false
            if (!ids.contentEquals(other.ids)) return false
        } else if (other.ids != null) return false
        if (appIds != null) {
            if (other.appIds == null) return false
            if (!appIds.contentEquals(other.appIds)) return false
        } else if (other.appIds != null) return false
        if (appPackageNames != null) {
            if (other.appPackageNames == null) return false
            if (!appPackageNames.contentEquals(other.appPackageNames)) return false
        } else if (other.appPackageNames != null) return false
        if (categoryPid != other.categoryPid) return false
        if (pageNum != other.pageNum) return false
        if (searchInfo != other.searchInfo) return false
        if (pageSize != other.pageSize) return false
        if (type != other.type) return false
        if (vehicleModel != other.vehicleModel) return false
        if (vehicleType != other.vehicleType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ids?.contentHashCode() ?: 0
        result = 31 * result + (appIds?.contentHashCode() ?: 0)
        result = 31 * result + (appPackageNames?.contentHashCode() ?: 0)
        result = 31 * result + (categoryPid ?: 0)
        result = 31 * result + pageNum
        result = 31 * result + (searchInfo?.hashCode() ?: 0)
        result = 31 * result + pageSize
        result = 31 * result + (type ?: 0)
        result = 31 * result + (vehicleModel?.hashCode() ?: 0)
        result = 31 * result + (vehicleType?.hashCode() ?: 0)
        return result
    }

}
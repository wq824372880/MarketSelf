package com.zeekrlife.market.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Lei.Chen29
 * @date 2022/6/10 11:34
 * description：精品推荐请求参数
 * @param pointCodes 点位码
 * @param vehicleType 车辆型号必填,如DC1E
 */
@Parcelize
data class GetAdvertisemnetsParams(
    val pointCodes: Array<String>? = null,
    val vehicleType: String? = ""
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetAdvertisemnetsParams

        if (pointCodes != null) {
            if (other.pointCodes == null) return false
            if (!pointCodes.contentEquals(other.pointCodes)) return false
        } else if (other.pointCodes != null) return false
        if (vehicleType != other.vehicleType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pointCodes?.contentHashCode() ?: 0
        result = 31 * result + (vehicleType?.hashCode() ?: 0)
        return result
    }

}
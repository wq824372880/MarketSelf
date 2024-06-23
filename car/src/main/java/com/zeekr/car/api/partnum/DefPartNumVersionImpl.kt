package com.zeekr.car.api.partnum

import android.util.Log
import com.zeekr.car.util.SystemProperties

/**
 * @author Lei.Chen29
 * @date 2023/6/2 14:35
 * description：零件号版本
 */
class DefPartNumVersionImpl : PartNumVersion {

    /**
     * 系统零件号
     */
    override fun systemPartNumVersion(): String {
        try {
            val versionNumber = SystemProperties.get("ro.product.build.version_number", "")
            if (versionNumber.length > 15) {
                val yearNumber = versionNumber[10].toString().toInt(16) + 2012 //年份16进制转10进制并对其zeekr规则
                val monthDayNumber = versionNumber.substring(11, 15)
                val vNumber = yearNumber.toString() + monthDayNumber
                if (isNumber(vNumber)) {
                    return vNumber
                }
            }
        } catch (e: Exception) {
            Log.e("DefPartNumVersion", "get systemPartNumVersion exception:${Log.getStackTraceString(e)}")
        }
        return ""
    }

    override fun systemPartNumVerifyIfReload(listener: PartNumLoadListener?): Boolean {
        return false
    }

    private fun isNumber(s: String?): Boolean {
        return !s.isNullOrEmpty() && s.matches(Regex("\\d+"))
    }

}
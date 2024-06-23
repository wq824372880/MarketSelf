package com.zeekrlife.common.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author
 */
object TimeUtils {

    @SuppressLint("SimpleDateFormat")
    val DEFAULT_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @SuppressLint("SimpleDateFormat")
    val DATE_FORMAT_DATE = SimpleDateFormat("yyyy-MM-dd")

    /**
     * long time to string
     */
    fun getTime(timeInMillis: Long?, dateFormat: SimpleDateFormat): String? = timeInMillis?.run {
        dateFormat.format(Date(this))
    }

    /**
     * long time to string, format is [.DEFAULT_DATE_FORMAT]
     */
    fun getTime(timeInMillis: Long): String? {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT)
    }

    /**
     * get current time in milliseconds
     */
    val currentTimeInLong: Long
        get() = System.currentTimeMillis()

    /**
     * get current time in milliseconds, format is [.DEFAULT_DATE_FORMAT]
     */
    val currentTimeInString: String?
        get() = getTime(currentTimeInLong)

    /**
     * get current time in milliseconds
     */
    fun getCurrentTimeInString(dateFormat: SimpleDateFormat): String? {
        return getTime(currentTimeInLong, dateFormat)
    }
}
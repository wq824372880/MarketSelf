package com.zeekr.car.util

import android.util.Log

object CarLogUtils {

    @JvmStatic
    fun logStackTrace(throwable: Throwable) {
        try {
            Log.e("System.error", throwable.stackTraceToString())
        } catch (_: Exception) {
        }
    }
}
package com.zeekrlife.common.util

import android.content.Context
import android.util.Log
import com.zeekr.basic.appContext
import com.zeekr.component.toast.showToast
import com.zeekrlife.common.ext.logStackTrace

/**
 * @author
 */
object ToastUtils {

    const val TAG = "ToastUtils"

    @JvmStatic
    fun show(context: Context, message: String, icon: Int? = null) {
        try {
            Log.e(TAG, "show1")
            if (message.isEmpty()) return
            if (icon != null) {
                context.showToast(message, icon = icon)
            } else {
                context.showToast(message)
            }

        }  catch (e: NullPointerException) {
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    @JvmStatic
    fun show(message: String) {
        try {
            Log.e(TAG, "show2")
            if (message.isEmpty()) return
            appContext.showToast(message)
        } catch (e: NullPointerException) {
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

//    fun showNoPadding(message: String) {
//        try {
//            Log.e(TAG, "show3")
//            if (message.isEmpty()) return
//            ZeekrToast.show(appContext) { appContext.inflateToastLayout(message) }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
}
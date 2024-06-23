package com.zeekrlife.task.base.specialapp

import android.annotation.SuppressLint
import android.util.Log
import com.zeekr.basic.appContext
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.task.base.constant.TaskState

/**
 * @author Lei.Chen29
 * @date 2023/12/16 16:11
 * description：SpecialAppHelper 用于特殊APP需求
 */
@SuppressLint("LogNotTimber")
object SpecialAppHelper {

    const val TAG = "SpecialAppHelper"

    fun getInstalledStatus(packageName: String?): Int {
        if (isHiCarByVersionCode22(packageName)) {
            return TaskState.DOWNLOADABLE
        }
        return TaskState.UPDATABLE
    }

    fun isNoAutoUpdate(packageName: String?): Boolean {
        return isHiCarByVersionCode22(packageName)
    }

    private fun isHiCarByVersionCode22(packageName: String?): Boolean {
        //比较包名
        if ("com.zeekrlife.hicar" == packageName) {
            //获取versionCode
            val app = ApkUtils.getAppInfo(appContext, packageName)
            if (app?.versionCode == 22) {
                return true
            }
        }
        return false
    }

}
package com.zeekrlife.market.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.zeekrlife.common.ext.logStackTrace

object NetUtils {

    /**
     * 检查当前是否有可用的网络连接。
     *
     * @param context 上下文，用于获取ConnectivityManager实例。
     * @return Boolean 返回true表示网络可用，false表示网络不可用。
     */
    fun hasNetworkAvailable(context: Context): Boolean {
        try {
            // 获取ConnectivityManager实例以查询网络状态
            val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivityManager.activeNetworkInfo
            // 判断是否连接到网络
            val isConnected = info != null && info.isConnected

            // API 23及以上版本的额外网络验证
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // 获取当前活动网络的网络能力
                val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
                // 判断网络是否已经验证
                val isActiveNetworkValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                Log.e("NetUtils", "isConnected: $isConnected; isActiveNetworkValidated: $isActiveNetworkValidated")
                // 返回网络连接且已验证的状态
                return isConnected && isActiveNetworkValidated
            } else {
                // API 23以下版本只检查是否连接
                Log.e("NetUtils", "isConnected: $isConnected")
                return isConnected
            }
        } catch (e: Exception) {
            // 捕获异常并记录堆栈跟踪
            e.logStackTrace()
        }
        // 异常情况下返回网络不可用
        return false
    }
}
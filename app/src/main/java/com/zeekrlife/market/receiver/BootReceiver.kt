package com.zeekrlife.market.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.zeekrlife.ampe.core.AppletService
import com.zeekrlife.market.manager.AppletPropertyManager
import com.zeekrlife.market.worker.RefreshPropertiesWorker
import com.zeekrlife.market.worker.ThirdUpdateStartWorker

/**
 * 开机广播
 * （1）启动自动更新服务
 */
class BootReceiver : BroadcastReceiver() {

    private val TAG = "BootReceiver"

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED != intent.action) {
            return
        }
        Log.e(TAG, "-----------bootReceiver--------------")
        //自动更新
        ThirdUpdateStartWorker.startWorker(context)
        //App属性刷新
        RefreshPropertiesWorker.startWorker(context)
        //启动小程序服务
        val serviceIntent = Intent(context, AppletService::class.java)
        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startForegroundService(serviceIntent)
        //获取小程序列表
        AppletPropertyManager.getLegalAppletIdSet()
    }
}
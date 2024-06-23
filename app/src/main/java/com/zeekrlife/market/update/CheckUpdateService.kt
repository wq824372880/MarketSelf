package com.zeekrlife.market.update

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.Utils
import com.zeekrlife.market.R
import com.zeekrlife.market.autoupdate.ThirdUpdateProvider
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.task.base.bean.AppInfo
import com.zeekrlife.task.base.proxy.TaskProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * @author Lei.Chen29
 * @date 2023/6/28 10:09
 * description：对外提供检测更新服务
 */
@SuppressLint("LogNotTimber")
class CheckUpdateService : Service() {

    private val TAG = "CheckUpdateService"

    private var appCheckUpdateImpl: AppCheckUpdateImpl? = null

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate() called")
        appCheckUpdateImpl = AppCheckUpdateImpl(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "onBind() called")
        return appCheckUpdateImpl
    }

    private class AppCheckUpdateImpl(private val context: Context) : IAppCheckUpdater.Stub() {

        private val TAG = "AppCheckUpdateImpl"

        /**
         * 检查应用更新。
         *
         * @param packageName 应用的包名，可为空。
         * @param callback 检查更新的回调接口，可为空。
         * @return 返回一个布尔值，如果启动了更新检查流程则为true，否则为false。
         */
        override fun checkAppUpdate(
            packageName: String?, callback: ICheckUpdateCallback?
        ): Boolean {
            // 打印日志，记录传入的包名和回调信息
            Log.e(TAG, "packageName:$packageName; callback:$callback")
            // 如果包名为空或空字符串，则直接返回false
            if (packageName.isNullOrEmpty()) return false
            // 在主线程上启动一个协程来异步执行更新检查
            MainScope().launch(Dispatchers.Default) {
                // 尝试执行更新检查，捕获任何可能发生的异常
                runCatching {
                    checkUpdate(packageName, callback)
                }.onFailure {
                    // 如果在检查更新的过程中发生了异常，打印错误日志
                    Log.e(TAG, "packageName:$packageName checkAppUpdate error:${Log.getStackTraceString(it)}")
                }
            }
            // 如果检查流程启动成功，返回true
            return true
        }


        /**
         * 检查指定包名的应用是否有可用的新版本。
         *
         * @param packageName 要检查的应用的包名，如果为空或null则返回false。
         * @param callback 检查结果的回调接口，如果为null，则不执行回调。
         * @return 总是返回true，表示已启动版本检查流程。实际的可用版本检查结果通过回调接口返回。
         */
        override fun hasAvailableVersion(packageName: String?, callback: IAvailableVersionCallback?): Boolean {
            // 记录调用时的包名和回调信息
            Log.e(TAG, "packageName:$packageName; callback:$callback")
            // 如果包名为空或null，则直接返回false
            if (packageName.isNullOrEmpty()) return false
            // 在主线程上启动一个协程来异步执行可用版本检查
            MainScope().launch(Dispatchers.Default) {
                // 尝试执行应用版本检查，捕获任何可能发生的异常
                runCatching {
                    checkAppAvailableVersion(packageName, callback)
                }.onFailure {
                    // 如果检查过程中发生异常，记录错误信息
                    Log.e(TAG, "packageName:$packageName hasAvailableVersion error:${Log.getStackTraceString(it)}")
                }
            }
            // 协程启动成功，表示版本检查流程已启动，因此返回true
            return true
        }

        /**
         * 根据包名检测跟新
         * @param packageName 包名
         * @param callback
         */
        private suspend fun checkUpdate(packageName: String, callback: ICheckUpdateCallback?) {

            var updatable = false

            var isForcedUpdate = false

            var iAppInfo: IAppInfo? = null

            var appVersionInfo: AppItemInfoBean? = null

            try {
                //获取应用信息
                val apkInfo = ApkUtils.getPackageInfo(context, packageName)
                if (apkInfo != null) {
                    //获取本地版本
                    val apkVersionCode: Long = if (VERSION.SDK_INT >= VERSION_CODES.P) {
                        apkInfo.longVersionCode
                    } else {
                        apkInfo.versionCode.toLong()
                    }
                    Log.e(TAG, "installed versionCode -> $apkVersionCode")
                    //检查更新
                    val apps = AppRepository.getApps(packages = arrayOf(packageName), pageNum = 1).await()
                    apps.list?.apply {
                        if (isNotEmpty()) {
                            val appInfo = this[0]
                            appVersionInfo = appInfo
                            Log.e(TAG, "request app : $appInfo")
                            appInfo.apply {
                                iAppInfo = IAppInfo().apply {
                                    this.appName = apkName
                                    this.packageName = apkPackageName
                                    this.versionName = apkVersionName
                                    this.versionCode = apkVersion?.toLong() ?: 0
                                    this.appDescription = appDesc
                                    this.updateDesc = updates
                                }

                                isForcedUpdate = forcedUpdate == 1
                                apkPackageName?.apply {
                                    updatable = apkVersionCode < (apkVersion?.toLong() ?: 0)
                                }
                                taskInfo = TaskHelper.toTaskInfo(app = this, updatable)
                            }
                        } else {
                            Log.e(TAG, "request app versionInfo is empty")
                        }
                    }
                } else {
                    Log.e(TAG, "$packageName not installed")
                }
            } catch (e: Exception) {
                e.logStackTrace()
                Log.e(TAG, "request checkUpdate exception : ${Log.getStackTraceString(e)}")
            }

            //是否打开了自动更新
            val isOpenAutoUpdate = ThirdUpdateProvider.isOpenAutoUpdate()
            Log.e(TAG, "isOpenAutoUpdate -> $isOpenAutoUpdate ; isForcedUpdate -> $isForcedUpdate")

            //获取外部是否需要执行更新
            val executeUpdate = callback?.onAppUpdate(updatable, iAppInfo) ?: false
            Log.e(TAG, "updatable -> $updatable ; executeUpdate -> $executeUpdate")

            //如果应用是非强制更新的应用并且未打开自动更新 return，只能通过进入应用商城点击更新
            if (!isForcedUpdate && !isOpenAutoUpdate) {
                Log.e(TAG, "can not update by CheckUpdateService")
                return
            }
            if (updatable && executeUpdate && hasSpaceEnough(appVersionInfo)) {
                if (appVersionInfo?.taskInfo != null) {
                    val addTaskResult = TaskProxy.getInstance().addTask(appVersionInfo?.taskInfo!!)
                    Log.e(TAG, "add to TaskService : $addTaskResult")
                } else {
                    Log.e(TAG, "add to TaskService fail : taskInfo is null")
                }
            }
        }

        /**
         * 磁盘检测：是否还有存储空间
         */
        private fun hasSpaceEnough(appVersionInfo: AppItemInfoBean?): Boolean {
            try {
                if (!Utils.isSpaceEnough(appVersionInfo?.apkSize?.toString() ?: "")) {
                    Handler(Looper.getMainLooper()).post {
                        ToastUtils.show(appContext.getString(R.string.app_install_no_space_enough))
                    }
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }

        /**
         * 根据包名检测是否有可安装版本
         */
        private suspend fun checkAppAvailableVersion(packageName: String, callback: IAvailableVersionCallback?) {

            var appVersionInfo: AppItemInfoBean? = null

            var iAppInfo: IAppInfo? = null

            var appIsInstall = false

            var updatable = false

            var availableVersion = false

            try {
                val apps = AppRepository.getApps(packages = arrayOf(packageName), pageNum = 1).await()
                //获取到的应用版本
                apps.list?.apply {
                    if (isNotEmpty()) {
                        val appInfo = this[0]
                        appVersionInfo = appInfo
                        Log.e(TAG, "checkAppAvailableVersion request app : $appInfo")
                        appInfo.apply {
                            iAppInfo = IAppInfo().apply {
                                this.appName = apkName
                                this.packageName = apkPackageName
                                this.versionName = apkVersionName
                                this.versionCode = apkVersion?.toLong() ?: 0
                                this.appDescription = appDesc
                                this.updateDesc = updates
                            }
                        }
                    } else {
                        Log.e(TAG, "checkAppAvailableVersion request app versionInfo is empty")
                    }
                }

                try {
                    //获取已安装版本，检测是否是可更新版本
                    val apkInfo = ApkUtils.getPackageInfo(context, packageName)
                    if (apkInfo != null) {
                        appIsInstall = true
                        val apkVersionCode: Long = if (VERSION.SDK_INT >= VERSION_CODES.P) {
                            apkInfo.longVersionCode
                        } else {
                            apkInfo.versionCode.toLong()
                        }

                        Log.e(TAG, "checkAppAvailableVersion installed versionCode -> $apkVersionCode")

                        appVersionInfo?.apply {
                            updatable = apkVersionCode < (apkVersion?.toLong() ?: 0)
                        }
                    }
                } catch (e: Exception) {
                    e.logStackTrace()
                    Log.e(TAG, "checkAppAvailableVersion exception1 : ${Log.getStackTraceString(e)}")
                }

                appVersionInfo?.apply {
                    taskInfo = TaskHelper.toTaskInfo(app = this, updatable)
                }

                //应用是否有可安装版本：未安装并且获取到版本不为空 或者 已安装并且检测到了新版本
                availableVersion = (!appIsInstall && appVersionInfo != null) || (appIsInstall && updatable)

            } catch (e: Exception) {
                e.logStackTrace()
                Log.e(TAG, "checkAppAvailableVersion exception2 : ${Log.getStackTraceString(e)}")
            }

            //获取外部是否需要执行下载安装
            val executeInstall = callback?.onAppAvailableVersion(availableVersion, iAppInfo) ?: false

            Log.e(TAG, "checkAppAvailableVersion availableVersion : $availableVersion ; executeInstall : $executeInstall")

            if (executeInstall && hasSpaceEnough(appVersionInfo)) {
                //检测是否有安装中的任务并移除
                val iTasks = TaskProxy.getInstance().taskList
                iTasks.forEach {
                    val appInfo = GsonUtils.fromJson(it.expand, AppInfo::class.java)
                    if (packageName == appInfo.packageName) {
                        Log.e(TAG, "checkAppAvailableVersion removeTask old task : $it")
                        TaskProxy.getInstance().removeTask(it.id)
                        return@forEach
                    }
                }
                Log.e(TAG, "checkAppAvailableVersion addTask : $packageName")
                TaskProxy.getInstance().addTask(appVersionInfo?.taskInfo!!)
            }
        }
    }
}
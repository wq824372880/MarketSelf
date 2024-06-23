package com.zeekrlife.market.manager

import android.util.Log
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AppAttributes
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.property.AppProperty
import com.zeekrlife.market.property.DrivingMainUseProperty
import com.zeekrlife.market.property.DrivingPassengerUseProperty
import com.zeekrlife.market.property.DualAudioProperty
import com.zeekrlife.market.property.IsGameProperty
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest

/**
 * @author Lei.Chen29
 * @date 2022/6/24 16:20
 * description：应用属性管理
 */
object AppPropertyManager : InstallStateChangeListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    /**
     * 写一个tag表示此类,使用变量为TAG
     */
    private const val TAG = "zzzAppPropertyManager"

    /**
     * 属性集合
     */
    val properties = mutableMapOf<String, AppProperty>()

    /**
     * 是否是游戏属性
     */
    private var isGameProperty: IsGameProperty? = null

    /**
     * 行车中是否允许副驾使用属性
     */
    private var drivingPassengerUseProperty: DrivingPassengerUseProperty? = null

    /**
     * 行车中是否允许主驾使用属性
     */
    private var drivingMainUseProperty: DrivingMainUseProperty? = null

    /**
     * 是否支持双音源
     */
    private var dualAudioProperty: DualAudioProperty? = null

    /**
     * 同步属性最近时间
     */
    private var syncLastTime: Long = 0

    init {
        InstallAppManager.addInstallStateChangeListener(this)
        //是否为游戏属性
        isGameProperty = IsGameProperty().apply { properties[propertyName] = this }
        //行车中是否允许使用（滑移屏副驾位）
        drivingPassengerUseProperty = DrivingPassengerUseProperty().apply { properties[propertyName] = this }
        //行车中是否允许使用（主驾位）
        drivingMainUseProperty = DrivingMainUseProperty().apply { properties[propertyName] = this }
        //双音源属性
        dualAudioProperty = DualAudioProperty().apply { properties[propertyName] = this }
    }

//    fun release() {
//        InstallAppManager.removeInstallStateChangeListener(this)
//    }

    private fun execute(func: suspend () -> Unit, failure: (suspend (it: Throwable) -> Unit)? = null) {
        scope.launch {
            runCatching {
                func()
            }.onFailure {
                "error:${Log.getStackTraceString(it)}".logE()
                failure?.invoke(it)
            }
        }
    }

    /**
     * 云端查询指定包名属性值
     */
    fun cloudQueryPropertyValue(packageName: String? = null, versionCode: Long? = -1, propertyName: String? = null) {

    }

    /**
     * 属性云端同步所以已安装属性值
     */
    fun cloudSyncProperties() {
        val currentSyncTime = System.currentTimeMillis()
        if (syncLastTime == 0L || currentSyncTime - syncLastTime > 30000) {
            syncLastTime = currentSyncTime
            "cloudSyncProperties() call success".logE()
            execute({
                //当应用未打开时请求会失败，这边临时延迟500，后续优化（通过监听确定网络初始化完成后再发起请求）
                delay(500)
                val apps = ApkUtils.getAllAppsInfo(appContext).asSequence().map {
                    "${it.packageName}_${it.versionCode}"
                }.toList()

                if (apps.isNotEmpty()) {
                    handleResult(request(apps))
                }
            })
        } else {
            "cloudSyncProperties() call interval is less than 30000".logE()
        }
    }

    private suspend fun request(packages: List<String>): Map<String, AppAttributes>? =
        AppRepository.getAppAttributes(packages,calculateSHA256Sorted(packages)).await()


    private fun calculateSHA256Sorted(packages: List<String>): String {
        val sortedPackages = packages.sorted()
        val sortedString = sortedPackages.joinToString(",")
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(sortedString.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * 处理请求结果
     * @param result key: ${包名}-${versionCode} value: ${属性}
     */
    private fun handleResult(result: Map<String, AppAttributes>?) {
        try {
            result?.values?.forEach {
                val packageName = it.apkPackageName ?: ""
                val versionCode = it.apkVersion ?: 0L
                //行车中是否允许副驾使用属性
                val supportDrivingPassengerUser = it.supportDrivingPassengerUser ?: -1
                drivingPassengerUseProperty?.propertyChange(packageName, versionCode, supportDrivingPassengerUser)
                //行车中是否允许主驾使用属性
                val supportDrivingUser = it.supportDrivingUser ?: -1
                drivingMainUseProperty?.propertyChange(packageName, versionCode, supportDrivingUser)
                //双音源
                val dualSoundSource = it.dualSoundSource ?: -1
                dualAudioProperty?.propertyChange(packageName, versionCode, dualSoundSource)
            }
            "checkAndInsert: $result".logE()
        } catch (e: NullPointerException) {
            // 处理 NullPointerException 的逻辑
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理 IllegalStateException 的逻辑
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            // 处理 IllegalArgumentException 的逻辑
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他未特别处理的异常
            e.logStackTrace()
        }
    }

    /**
     * 应用安装成功
     * @param packageName
     */
    override fun onInstallSuccess(packageName: String) {
        LogUtils.e(TAG,"zzzOnInstallSuccess:$packageName")
        try {
            InstallAppManager.getInstallAppMaps()?.get(packageName)?.apply {
                //是否为游戏
                isGameProperty?.propertyChange(packageName, apkVersion?.toLong() ?: 0L, if (categoryPid == 1) 1 else 0)
                //行车中是否允许副驾使用属性
                drivingPassengerUseProperty?.propertyChange(packageName, apkVersion?.toLong() ?: 0L, supportDrivingPassengerUser)
                //行车中是否允许主驾使用属性
                drivingMainUseProperty?.propertyChange(packageName, apkVersion?.toLong() ?: 0L, supportDrivingUser)
                //双音源
                dualAudioProperty?.propertyChange(packageName, apkVersion?.toLong() ?: 0L, dualSoundSource)
            }
        } catch (e: NullPointerException) {
            // 处理 NullPointerException 的逻辑
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理 IllegalStateException 的逻辑
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            // 处理 IllegalArgumentException 的逻辑
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他未特别处理的异常
            e.logStackTrace()
        }
    }

    /**
     * 应用卸载成功
     * @param packageName
     */
    override fun onUnInstallSuccess(packageName: String) {
        try {
            InstallAppManager.getInstallAppMaps()?.get(packageName)?.apply {

                isGameProperty?.propertyChange(packageName, apkVersion?.toLong() ?: 0L, -1)

                drivingPassengerUseProperty?.propertyChange(packageName, apkVersion?.toLong() ?: 0L, -1)

                dualAudioProperty?.propertyChange(packageName, apkVersion?.toLong() ?: 0L, -1)
            }
        } catch (e: NullPointerException) {
            // 处理 NullPointerException 的逻辑
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理 IllegalStateException 的逻辑
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他未特别处理的异常
            e.logStackTrace()
        }
    }
}
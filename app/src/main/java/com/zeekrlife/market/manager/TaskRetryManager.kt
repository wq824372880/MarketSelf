package com.zeekrlife.market.manager

import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.zeekr.basic.appContext
import com.zeekr.car.api.DeviceApiManager
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.market.task.ITaskInfo
import com.zeekrlife.market.task.TaskErrorCode
import com.zeekrlife.task.base.bean.TaskInfo
import com.zeekrlife.task.base.proxy.TaskProxy
import org.json.JSONException

/**
 * @author Lei.Chen29
 * @date 2023/6/5 19:26
 * description：重试下载
 */
object TaskRetryManager {

    private const val TAG = "RetryDownloadManager"

    private const val APP_RETRIED_TASK_CACHE = "app_retried_task_cache"

    private const val APP_RETRIED_TASK_SYSTEM_PN = "app_retried_task_system_pn"

    private const val APP_RETRIED_TASK_CACHE_MAP = "app_retried_task_cache_map"

    private var retriedTaskCacheHolder: MMKV? = null

    private var retryTasks: ArrayMap<String, RetryInfo>? = null

    /**
     * 获取重试任务缓存的MMKV实例。
     * 该函数没有参数。
     * @return 返回一个用于存储重试任务的MMKV实例。
     */
    private fun getRetryTaskCache(): MMKV {
        // 初次检查MMKV根目录是否已初始化，若未初始化则进行初始化
        if (MMKV.getRootDir() == null) {
            MMKV.initialize(appContext)
        }
        // 检查重试任务缓存持有者是否已被实例化，若未实例化则进行实例化
        if (retriedTaskCacheHolder == null) {
            retriedTaskCacheHolder = MMKV.mmkvWithID(APP_RETRIED_TASK_CACHE)
        }
        // 返回重试任务缓存持有者的MMKV实例
        return retriedTaskCacheHolder!!
    }

    /**
     * 添加任务重试
     * @param packageName 包名
     * @param taskId    任务ID
     * @param errorCode  错误码
     * @param task     任务
     */
    @Synchronized
    @JvmStatic
    fun addRetryTask(
        packageName: String, versionCode: Long, taskId: String?, errorCode: Int, task: ITaskInfo?
    ) {
        Log.e(TAG, "addRetryTask : packageName :$packageName taskId -> $taskId; errorCode -> $errorCode; task -> $task")
        if (taskId == null || task == null) {
            return
        }

        try {
            //检测是否已经安装成功
            val packageInfo = ApkUtils.getPackageInfo(appContext, packageName)
            val vCode = if (VERSION.SDK_INT >= VERSION_CODES.P) {
                packageInfo?.longVersionCode
            } else {
                packageInfo?.versionCode
            }
            if (vCode == versionCode) {
                Log.e(TAG, "addRetryTask fail : app installed")
                return
            }

            if (retryTasks == null) {
                retryTasks = loadRetriedTaskCache()
            }

            var retryInfo = retryTasks?.get(taskId)

            if (retryInfo == null) {
                val taskInfo = TaskInfo()
                taskInfo.setData(task)
                retryInfo = RetryInfo(packageName, 0, 0, taskInfo)
                retryTasks?.put(taskId, retryInfo)
            }

            if (retryInfo.retryCount < 3) {
                Log.e(TAG, "addRetryTask success : retry task -> ${task.id} ; retryCount -> ${retryInfo.retryCount}")
                TaskProxy.getInstance().addTask(retryInfo.task)
                retryInfo.retryCount++
                //非网络情况导致的任务失败计次
                if (errorCode != TaskErrorCode.DOWNLOAD_FAILURE_BY_NET_ERROR) {
                    retryInfo.taskFailCount++
                }
            } else {
                Log.e(TAG, "addRetryTask fail : retryCount -> ${retryInfo.retryCount} ; task -> ${task.expand}")
            }

            saveRetriedTaskCache(retryTasks)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "addRetryTask exception : Package name not found")
            e.logStackTrace()
        } catch (e: NullPointerException) {
            Log.e(TAG, "addRetryTask exception : Null pointer exception")
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "addRetryTask exception : Illegal state exception")
            e.logStackTrace()
        } catch (e: SecurityException) {
            Log.e(TAG, "addRetryTask exception : Security exception")
            e.logStackTrace()
        }
    }

    /**
     * 是否可以添加到任务
     * @param packageName 包名
     */
    @Synchronized
    @JvmStatic
    fun canAddToTasks(packageName: String?): Boolean {

        var canAdd = true

        if (retryTasks == null) {
            retryTasks = loadRetriedTaskCache()
        }

        retryTasks?.values?.forEach {
            //需要更新的应用失败错误超过3次
            if (packageName == it.packageName && it.taskFailCount >= 3) {
                canAdd = false
                Log.e(TAG, "canAddToTasks  : packageName taskFailCount >= 3")
                return@forEach
            }
        }
        return canAdd
    }

    /**
     * 加载重试任务的缓存数据。
     * 该函数尝试从缓存中读取重试任务的JSON字符串，并将其转换为[ArrayMap<String, RetryInfo>]格式。
     * 如果读取过程中发生异常，或者读取的内容为空，则返回一个空的[ArrayMap]对象。
     *
     * @return [ArrayMap<String, RetryInfo>] 包含重试信息的键值对集合，如果无法加载则返回空集合。
     */
    private fun loadRetriedTaskCache(): ArrayMap<String, RetryInfo> {
        try {
            // 尝试从缓存中获取重试任务的JSON字符串
            val retriedTaskJson = getRetryTaskCache().getString(APP_RETRIED_TASK_CACHE_MAP, null)
            if (!retriedTaskJson.isNullOrEmpty()) {
                // 如果JSON字符串不为空，则将其转换为ArrayMap对象并返回
                return GsonUtils.fromJson(retriedTaskJson, object : TypeToken<ArrayMap<String, RetryInfo>>() {}.type)
            }
        } catch (e: JSONException) {
            // 处理JSON解析异常
            Log.e(TAG, "loadRetriedTaskCache exception : JSON exception")
            e.logStackTrace()
        } catch (e: NullPointerException) {
            // 处理空指针异常
            Log.e(TAG, "loadRetriedTaskCache exception : Null pointer exception")
            e.logStackTrace()
        }
        // 如果发生异常或没有找到有效数据，则返回空的ArrayMap对象
        return arrayMapOf()
    }

    /**
     * 保存重试任务的缓存数据。
     * 该函数将重试任务的信息（任务ID与重试信息的映射）转换为JSON字符串，并存储在缓存中。
     *
     * @param tasks 一个包含重试信息的Map，键为任务ID，值为RetryInfo对象。如果为null，则存空映射。
     */
    private fun saveRetriedTaskCache(tasks: Map<String, RetryInfo>?) {
        try {
            // 将任务映射转换为JSON字符串，若tasks为null，则转换为空映射的JSON字符串
            val retriedTaskJson = GsonUtils.toJson(tasks ?: arrayMapOf<String, RetryInfo>())
            // 将JSON字符串存储到缓存中
            getRetryTaskCache().putString(APP_RETRIED_TASK_CACHE_MAP, retriedTaskJson)
        } catch (e: JSONException) {
            // 捕获JSON转换异常，记录日志
            Log.e(TAG, "loadRetriedTaskCache exception : JSON exception")
            e.logStackTrace()
        } catch (e: NullPointerException) {
            // 捕获空指针异常，记录日志
            Log.e(TAG, "loadRetriedTaskCache exception : Null pointer exception")
            e.logStackTrace()
        }
    }

    /**
     * 清除任务
     */
    @Synchronized
    @JvmStatic
    fun arrangeRetryTask() {
        try {
            val systemPn = DeviceApiManager.getInstance().deviceSystemPn
            val systemPnCache = getRetryTaskCache().getString(APP_RETRIED_TASK_SYSTEM_PN, "")
            Log.e(TAG, "arrangeRetryTask: systemPn -> $systemPn ; systemPnCache -> $systemPnCache")

            //判断是否升级，如果升级清除历史失败的任务
            if (!systemPn.isNullOrEmpty() && systemPn != systemPnCache) {
                Log.e(TAG, "arrangeRetryTask systemPn diff, reset systemPn and clear cache")
                retryTasks?.clear()
                getRetryTaskCache().clearAll()
                getRetryTaskCache().putString(APP_RETRIED_TASK_SYSTEM_PN, systemPn)
            } else {
                //清除重试次数
                if (retryTasks == null) {
                    retryTasks = loadRetriedTaskCache()
                }

                retryTasks?.values?.forEach {
                    it.retryCount = 0
                }

                saveRetriedTaskCache(retryTasks)
                Log.e(TAG, "arrangeRetryTask retryTasks : $retryTasks")
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "arrangeRetryTask exception : Illegal state exception")
            e.logStackTrace()
        } catch (e: JSONException) {
            Log.e(TAG, "arrangeRetryTask exception : JSON exception")
            e.logStackTrace()
        } catch (e: NullPointerException) {
            Log.e(TAG, "arrangeRetryTask exception : Null pointer exception")
            e.logStackTrace()
        }
    }

    /**
     * @param packageName 包名
     * @param retryCount 重试次数
     * @param taskFailCount 任务失败次数
     * @param task 任务
     */
    private data class RetryInfo(val packageName: String, var retryCount: Int, var taskFailCount: Int, val task: TaskInfo)
}
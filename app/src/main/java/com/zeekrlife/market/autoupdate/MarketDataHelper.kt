package com.zeekrlife.market.autoupdate

import android.content.Context
import android.util.Log
import com.zeekrlife.market.utils.CarManager
import com.zeekr.car.api.DeviceApiManager
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.manager.TaskRetryManager
import com.zeekrlife.task.base.specialapp.SpecialAppHelper
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.task.base.bean.TaskInfo
import com.zeekrlife.task.base.constant.TaskState
import com.zeekrlife.task.base.constant.TaskStatus
import com.zeekrlife.task.base.constant.TaskType
import com.zeekrlife.task.base.proxy.TaskProxy
import com.zeekrlife.task.base.util.TaskUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import rxhttp.async

object MarketDataHelper {

    /**
     * 不需要自动更新列表
     *
     * @return
     */
    fun getNotAutoUpdatePackageNameList() = mutableListOf<String>()

    /**
     * 需自动更新应用列表（应用商城所管理需自动更新应用）
     * @return
     */
    fun getAutoDownloadTaskInfo(call: GetTaskInfoCall) {
        //1、获取本地应用
        Log.e("ThirdUpdateService", "getAutoDownloadTaskInfo start")
        val appsMap = AppRepository.getAllLocalApps()
        val packageNames = appsMap.keys.toList()
        //2、根据已安装应用列表，云端检测需要更新的应用列表
        MainScope().launch {
            runCatching {
                if (packageNames.isNotEmpty()) {
                    //过滤掉Android或者Google系统应用
                    val pkgNames =
                        packageNames.asSequence().filter { !ApkUtils.isAndroidOrGoogleApp(it) && !SpecialAppHelper.isNoAutoUpdate(it) }
                            .toList().sorted()
                    //是否打开自动更新
                    val isOpenAutoUpdate = ThirdUpdateProvider.isOpenAutoUpdate()

                    val packages = pkgNames.joinToString(",")
                    Log.e("ThirdUpdateService", "packages: $packages")

                    val vin = DeviceApiManager.getInstance().deviceAPI.vin ?: ""
                    val vehicleType = DeviceApiManager.getInstance().deviceAPI.vehicleType ?:""

                    val appsOfCSD = AppRepository.getUpdateAppList(packages, isOpenAutoUpdate, vin, vehicleType,
                        CarManager.ScreenType.getValueByName("CSD")).async(this@launch).await()
                    val appsOfBackrest = AppRepository.getUpdateAppList(packages, isOpenAutoUpdate, vin, vehicleType,
                        CarManager.ScreenType.getValueByName("BACKREST")).async(this@launch).await()
                    val apps = appsOfCSD?.union(appsOfBackrest)

                    val needUpdateApp = mutableListOf<AppItemInfoBean>()
                    //判断需要更新App
                    apps?.forEach { app ->
                        Log.e("ThirdUpdateService", "check app -> ${app.apkPackageName}")
                        val appVersionCode = app.apkVersion?.toLong() ?: 0L
                        appsMap[app.apkPackageName]?.apply {
                            Log.e("ThirdUpdateService", "app version->$appVersionCode localVersion->$versionCode")
                            if (appVersionCode > versionCode) {
                                if (app.forcedUpdate == 1) {
                                    //需强制更新的应用排最前面
                                    if(TaskRetryManager.canAddToTasks(app.apkPackageName)) {
                                        needUpdateApp.add(0, app)
                                    } else {
                                        Log.e("ThirdUpdateService", "Task failure has reached the maximum limit")
                                    }
                                } else {
                                    needUpdateApp.add(app)
                                }

                            }
                        }
                    }

                    Log.e("ThirdUpdateService", "need update apps : $needUpdateApp")
                    //需更新应用列表转TaskInfo
                    if (needUpdateApp.isNotEmpty()) {
                        call.onResult(fillDownloadTaskInfoMap(needUpdateApp))
                    } else {
                        call.onResult(mapOf())
                    }
                }
            }.onFailure {
                "getAutoDownloadTaskInfo error:${it.logStackTrace()}".logE()
                call.onResult(mapOf())
            }
        }
    }

    /**
     * 填充下载任务信息映射表
     *
     * 本函数遍历给定的应用列表，并为每个应用生成一个下载任务信息对象。如果任务信息对象非空，
     * 则将其类型设置为下载，并以应用的包名、版本和ID作为键，存储到一个可变的映射表中。
     *
     * @param apps 应用项目信息列表，每个应用项目包含应用的包名、版本和ID等信息。
     * @return 返回一个可变的映射表，键为应用的包名、版本和ID的组合字符串，值为对应的下载任务信息对象。
     */
    private fun fillDownloadTaskInfoMap(apps: List<AppItemInfoBean>): MutableMap<String, TaskInfo> {
        val tasks = mutableMapOf<String, TaskInfo>()
        for (app in apps) {
            // 生成任务的唯一标识符
            val id = "${app.apkPackageName}_${app.apkVersion}_${app.id}"
            // 根据应用信息创建任务信息对象，如果对象非空，则进行下一步处理
            TaskHelper.toTaskInfo(app, true)?.apply {
                type = TaskType.DOWNLOAD
                tasks[id] = this
            }
        }
        return tasks
    }

    /**
     * 填充installTaskInfoMap
     *
     * @param installTaskInfoMap
     * @param updateIdMap
     */
    fun fillInstallTaskInfoMap(
        installTaskInfoMap: MutableMap<String, TaskInfo>, updateIdMap: MutableMap<String, String>
    ) {
        val iterator = updateIdMap.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            val task = TaskProxy.getInstance().getTask(key)
            if (task != null && task.status == TaskStatus.DOWNLOAD_COMPLETED) {
                val taskInfo = TaskInfo(TaskState.INSTALLABLE)
                taskInfo.setData(task)
                taskInfo.type = TaskType.INSTALL
                installTaskInfoMap["$key,$value"] = taskInfo
            } else {
                // 与服务数据不一致(已经通过其他渠道装掉了等情况)
                iterator.remove()
            }
        }
    }

    /**
     * 下载完成处理函数。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @param taskId 完成下载的任务ID，可能为null。
     * @param successful 表示下载是否成功。
     * @param downloadTaskInfoMap 正在下载或已下载任务的信息映射表，键为任务唯一标识。
     * @param tmpInstallTaskInfoMap 临时安装任务信息映射表，用于存放成功下载的任务信息。
     * @param installTaskInfoMap 待安装任务信息映射表，存放准备进行安装的任务信息。
     * @param updateIdMap 用于更新任务ID的映射表，键为旧任务ID，值为新任务ID。
     * @return 如果处理完成且安装任务更新完毕，则返回true；否则返回false。
     */
    fun onDownloadFinished(
        context: Context, taskId: String?, successful: Boolean,
        downloadTaskInfoMap: MutableMap<String, TaskInfo>,
        tmpInstallTaskInfoMap: MutableMap<String, TaskInfo>,
        installTaskInfoMap: MutableMap<String, TaskInfo>,
        updateIdMap: MutableMap<String, String>
    ): Boolean {
        // 遍历下载任务信息映射表，移除已完成或暂停的下载任务
        val iterator = downloadTaskInfoMap.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            val id = TaskUtils.getTaskId(context, value)
            if (id == taskId) {
                iterator.remove()
                // 如果下载成功，则将任务信息转移到临时安装任务信息映射表
                if (successful) {
                    tmpInstallTaskInfoMap["$taskId,$key"] = value
                }
            } else {
                // 检查任务是否暂停，如果是则移除
                val task = TaskProxy.getInstance().getTask(id)
                if (task != null && task.status == TaskStatus.DOWNLOAD_PAUSED) {
                    iterator.remove()
                }
            }
        }
        // 如果下载任务信息映射表为空，说明所有任务已完成处理，进行安装任务的更新
        if (downloadTaskInfoMap.isEmpty()) {
            // 清空待安装任务信息映射表，并将临时安装任务信息映射表的内容合并进来
            installTaskInfoMap.clear()
            installTaskInfoMap.putAll(tmpInstallTaskInfoMap)
            // 清空任务ID更新映射表，并更新任务ID
            updateIdMap.clear()
            for (key in installTaskInfoMap.keys) {
                val split = key.split(",").toTypedArray()
                updateIdMap[split[0]] = split[1]
            }
            return true
        }
        return false
    }

    /**
     * 处理安装完成的逻辑。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @param taskId 完成安装的任务ID。
     * @param installTaskInfoMap 一个存储待安装任务信息的可变映射，键为任务ID，值为任务信息。
     * @return 返回一个布尔值，如果所有安装任务都已完成或移除，则为true；否则为false。
     */
    fun onInstallFinished(
        context: Context, taskId: String,
        installTaskInfoMap: MutableMap<String, TaskInfo>
    ): Boolean {
        // 遍历installTaskInfoMap，移除已完成或出错的任务，以及当前任务
        val iterator = installTaskInfoMap.entries.iterator()
        while (iterator.hasNext()) {
            val (_, value) = iterator.next()
            val id = TaskUtils.getTaskId(context, value)
            if (id == taskId) {
                // 如果当前遍历到的任务ID与taskId相等，移除该任务
                iterator.remove()
            } else {
                // 获取任务对象，并检查任务状态是否为已完成或出错
                val task = TaskProxy.getInstance().getTask(id)
                if (task != null &&
                    (task.status == TaskStatus.INSTALL_COMPLETED || task.status == TaskStatus.INSTALL_ERROR)
                ) {
                    // 如果任务存在且状态为已完成或出错，移除该任务
                    iterator.remove()
                }
            }
        }
        // 如果installTaskInfoMap为空，则所有任务都已处理完毕，返回true；否则返回false
        return installTaskInfoMap.isEmpty()
    }

    interface GetTaskInfoCall {
        fun onResult(result: Map<String, TaskInfo>)
    }
}
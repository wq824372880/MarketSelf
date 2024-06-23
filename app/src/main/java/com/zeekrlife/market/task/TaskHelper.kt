package com.zeekrlife.market.task

import android.text.TextUtils
import android.util.Log
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.common.util.TimeUtils
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.task.base.bean.ExpandAppInfo
import com.zeekrlife.task.base.bean.ExpandType
import com.zeekrlife.task.base.bean.TaskInfo
import com.zeekrlife.task.base.constant.TaskState
import com.zeekrlife.task.base.manager.TaskInfoManager
import com.zeekrlife.task.base.proxy.TaskProxy
import com.zeekrlife.task.base.specialapp.SpecialAppHelper
import com.zeekrlife.task.base.util.TaskUtils
import kotlinx.coroutines.runBlocking

object TaskHelper {

    fun toTaskInfo(app: AppItemInfoBean, isUpdate: Boolean = false): TaskInfo? {
        if (TextUtils.isEmpty(app.apkPackageName) || !TextUtils.isDigitsOnly(app.apkVersion)) {
            return null
        }

        var taskInfo: TaskInfo? = null

        val pkgToTaskInfo = TaskProxy.getInstance().pkgToTaskInfoMap[app.apkPackageName]
        Log.i("pkgToTaskInfopkgToTaskInfo", "${pkgToTaskInfo?.hash}, ${pkgToTaskInfo?.expand}")
        if ((pkgToTaskInfo != null) &&
            ((pkgToTaskInfo.state == TaskState.DOWNLOAD_STARTED) || (pkgToTaskInfo.state == TaskState.DOWNLOAD_PAUSED)
                || (pkgToTaskInfo.state == TaskState.DOWNLOAD_CONNECTED) || (pkgToTaskInfo.state == TaskState.DOWNLOAD_PROGRESS)
                )
        ) {// 有正在下载中的任务，不要更新当前taskInfo中的数据
            taskInfo = TaskInfo(pkgToTaskInfo.state)
            taskInfo.setData(pkgToTaskInfo)
        } else {
            val id = "${app.apkPackageName}_${app.apkVersion}_${app.id}"
            taskInfo = TaskInfoManager.getInstance().taskInfoMap[id]
            if (taskInfo == null) {
                taskInfo = TaskInfo(TaskState.DOWNLOADABLE)
                TaskInfoManager.getInstance().taskInfoMap[id] = taskInfo
            }

            taskInfo.url = app.apkUrl
            taskInfo.hash = app.apkMd5
            taskInfo.apkSha256 = app.sha256
            taskInfo.type = TaskType.DOWNLOAD_INSTALL

            try {
                checkUpdateTimeDisplay(app)
                val expandApp = ExpandAppInfo(
                    ExpandType.APK,
                    app.apkPackageName,
                    app.apkName,
                    app.icon,
                    app.apkSize.toString(),
                    app.apkVersionName,
                    app.apkVersion?.toLong() ?: 0L,
                    app.categoryName,
                    app.categoryPid,
                    app.id.toString(),
                    app.dualSoundSource,
                    app.supportDrivingPassengerUser,
                    app.supportDrivingUser,
                    app.updateTimeDisplay,
                    app.updates,
                    app.slogan,
                    app.apkSign,
                    isUpdate,
                    app.forcedUpdate == 1,
                    app.hideIcon == 1
                )

                taskInfo.expand = GsonUtils.toJson(expandApp)

                compareWithLocalData(
                    app.apkPackageName, app.apkVersion?.toLong() ?: 0, taskInfo, expandApp
                )
                runBlocking {
                    InstallAppManager.getInstallAppMaps()?.get(app.apkPackageName)?.apply {
                        if (apkVersion != app.apkVersion) {
                            InstallAppManager.updateInstallApp(app.apkUrl, app.apkMd5, app.sha256, expandApp)
                        }
                    }
                }
            } catch (e: Exception) {
                e.logStackTrace()
            }
            compareWithServiceData(taskInfo)
        }
        return taskInfo
    }

    /**
     * 检查并更新应用的显示更新时间。
     * 该函数会检查应用的更新时间是否已经以可读格式设置，如果没有，则会根据应用的实际更新时间生成一个格式化的更新时间显示。
     *
     * @param app 一个包含应用信息的Bean对象，其中需要包含更新时间和更新时间显示字段。
     */
    private fun checkUpdateTimeDisplay(app: AppItemInfoBean) {
        try {
            // 如果应用的更新时间显示为空，且更新时间有效（大于0），则格式化并设置更新时间显示
            if (app.updateTimeDisplay.isNullOrEmpty() && app.updateTime != null && app.updateTime > 0) {
                app.updateTimeDisplay =
                    TimeUtils.getTime(app.updateTime, TimeUtils.DEFAULT_DATE_FORMAT)
            }
        } catch (e: Exception) {
            // 捕获异常并记录堆栈跟踪
            e.logStackTrace()
        }
    }

    /**
     * 对比本地是否安装、是否需要更新
     * @param appPackageName
     * @param appVersionCode
     * @param taskInfo
     * @param expandApp
     */
    private fun compareWithLocalData(
        appPackageName: String?, appVersionCode: Long, taskInfo: TaskInfo, expandApp: ExpandAppInfo?
    ) {
        if (appPackageName.isNullOrEmpty()) return

        val apk = ApkUtils.getPackageInfo(appContext, appPackageName)
        if (apk != null) {
            if (apk.versionCode < appVersionCode) {
                //taskInfo.state = TaskState.UPDATABLE
                taskInfo.state = SpecialAppHelper.getInstalledStatus(appPackageName)
                if (expandApp != null) {
                    expandApp.isUpdate = true
                    taskInfo.expand = GsonUtils.toJson(expandApp)
                }
            } else {
                taskInfo.state = TaskState.OPENABLE
            }
        } else {
            taskInfo.state = TaskState.DOWNLOADABLE
        }
        Log.e("TaskHelper", "expandApp:${expandApp?.isUpdate} ${taskInfo.state}")
    }

    /**
     * 对比TaskService中的TaskInfo,更新taskInfo
     */
    private fun compareWithServiceData(taskInfo: TaskInfo) {
        val taskList = TaskProxy.getInstance().taskList
        if (taskList.isNullOrEmpty()) {
            return
        }

        for (task in taskList) {
            if (TaskUtils.getTaskId(appContext, taskInfo) == task.id) {
                when (task.status) {
                    TaskStatus.DOWNLOAD_PENDING -> {
                        taskInfo.state = TaskState.DOWNLOAD_PENDING
                        taskInfo.setData(task)
                    }
                    TaskStatus.DOWNLOAD_STARTED -> {
                        taskInfo.state = TaskState.DOWNLOAD_STARTED
                        taskInfo.setData(task)
                    }
                    TaskStatus.DOWNLOAD_PROGRESS -> {
                        taskInfo.state = TaskState.DOWNLOAD_PROGRESS
                        taskInfo.setData(task)
                    }
                    TaskStatus.DOWNLOAD_PAUSED -> {
                        taskInfo.state = TaskState.DOWNLOAD_PAUSED
                        if (taskInfo.url.isNotEmpty() && taskInfo.url.equals(task.url)) {
                            taskInfo.setData(task)
                        }
                    }
                    TaskStatus.DOWNLOAD_COMPLETED -> {
                        taskInfo.state = TaskState.INSTALLABLE
                        taskInfo.setData(task)
                    }
                    TaskStatus.INSTALL_PENDING -> {
                        taskInfo.state = TaskState.INSTALL_PENDING
                        taskInfo.setData(task)
                    }
                    TaskStatus.INSTALL_PROGRESS -> {
                        taskInfo.state = TaskState.INSTALL_PROGRESS
                        taskInfo.setData(task)
                    }
                    else -> {
                    }
                }
                return
            }
        }
    }

    /**
     * 获取TaskInfo
     */
    fun getTaskInfo(app: AppItemInfoBean): TaskInfo? {
        try {
            if (app.taskInfo == null) {
                app.taskInfo = toTaskInfo(app = app)
            } else {
                val apk = InstallAppManager.getInstallAppMaps()?.get(app.apkPackageName)
                if ((app.taskInfo?.state == TaskState.OPENABLE ||
                        app.taskInfo?.state == TaskState.INSTALL_COMPLETED) && apk == null
                    && !ApkUtils.isAppInstalled(appContext, app.apkPackageName ?: "")
                ) {
                    app.taskInfo?.state = TaskState.DOWNLOADABLE
                } else {
                    //解决有时候状态不一致问题
                    app.taskInfo?.apply {
                        compareWithLocalData(
                            app.apkPackageName, app.apkVersion?.toLong() ?: 0, this, null
                        )
                        compareWithServiceData(this)
                    }
                }
            }
            return app.taskInfo
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return null
    }
}
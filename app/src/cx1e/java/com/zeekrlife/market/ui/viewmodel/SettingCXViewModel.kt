package com.zeekrlife.market.ui.viewmodel

import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.zeekr.basic.Common
import com.zeekr.basic.appContext
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.msg
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.common.util.SPUtils
import com.zeekrlife.common.util.Utils
import com.zeekrlife.common.util.constant.SpConfig
import com.zeekrlife.market.data.entity.AppUpdateState
import com.zeekrlife.market.data.entity.AppUpdateState.Status
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.task.IArrangeCallback
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.market.ui.fragment.SettingFragment
import com.zeekrlife.net.load.LoadingType
import com.zeekrlife.task.base.bean.ExpandAppInfo
import com.zeekrlife.task.base.manager.TaskManager
import com.zeekrlife.task.base.proxy.TaskProxy

class SettingCXViewModel : BaseViewModel(), IArrangeCallback {

    fun getAppVersion() = "v ${ApkUtils.getAppInfo(Common.app)?.versionName}"

    var appInfo: AppItemInfoBean? = null

    val isUpdate = MutableLiveData<Boolean>()

    val appUpdateState = MutableLiveData<AppUpdateState>()

    fun isAutoUpdate() = SPUtils.getInstance().getBoolean(SpConfig.BooleanKey.AUTO_UPDATE, true)

    fun updateAutoUpdateState(isChecked: Boolean) {
        if (isChecked != isAutoUpdate()) {
            SPUtils.getInstance().put(SpConfig.BooleanKey.AUTO_UPDATE, isChecked)
        }
    }

    /**
     * 检查更新
     */
    fun checkUpdate(justCheck: Boolean, message: String = "") {
        rxHttpRequest {
            onRequest = {
                val response = AppRepository.checkUpdate().await()
                response.list?.apply {
                    if (isNotEmpty()) {
                        appInfo = this[0]
                        appInfo?.apply {
                            taskInfo = TaskHelper.toTaskInfo(app = this)

                            appInfo?.apkPackageName?.apply {
                                val apk = ApkUtils.getPackageInfo(appContext, this)
                                val updatable = (apk?.versionCode ?: 1) <= (appInfo?.apkVersion?.toLong() ?: 0)
                                if (justCheck) {
                                    isUpdate.postValue(updatable)
                                } else {
                                    appUpdateState.postValue(AppUpdateState(update = updatable, state = Status.CHECK_UPDATE))
                                }
                            }
                        }
                    }
                }
            }

            onError = {
                appUpdateState.postValue(AppUpdateState(update = false, state = Status.CHECK_UPDATE, throwable = it))
            }

            loadingType =  LoadingType.LOADING_NULL
            loadingMessage = message
            requestCode = if(justCheck) "" else SettingFragment.checkUpdateRequestCode // 如果要判断接口错误业务 - 必传
        }
    }

    /**
     * 开始更新应用
     */
    fun startUpdate() {
        try {
            val taskInfo = appInfo?.taskInfo
            if (taskInfo != null) {
                //磁盘检测
                val appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo::class.java)
                if (!Utils.isSpaceEnough(appInfo.apkSize)) {
                    appUpdateState.postValue(AppUpdateState(state = Status.ERROR_NO_SPACE_ENOUGH))
                    return
                }
                TaskProxy.getInstance().addTask(taskInfo)
                TaskManager.getInstance().registerArrangeCallback(this)
                appUpdateState.postValue(AppUpdateState(state = Status.SHOW_DOWNLOADING))
            } else {
                appUpdateState.postValue(AppUpdateState(state = Status.DOWNLOAD_ERROR))
            }
        } catch (e: Exception) {
            e.logStackTrace()
            appUpdateState.postValue(AppUpdateState(state = Status.DOWNLOAD_ERROR))
        }

    }

    override fun onCleared() {
        super.onCleared()
        TaskManager.getInstance().unregisterArrangeCallback(this)
    }

    override fun asBinder(): IBinder? = null

    override fun onDownloadPending(taskId: String?) {}

    override fun onDownloadStarted(taskId: String?) {}

    override fun onDownloadConnected(taskId: String?, soFarBytes: Long, totalBytes: Long) {}

    override fun onDownloadProgress(taskId: String?, soFarBytes: Long, totalBytes: Long) {
            val progress = ((soFarBytes * 1.0F / totalBytes) * 100).toInt()
            appUpdateState.postValue(AppUpdateState(state = Status.DOWNLOAD_PROGRESS, progress = progress, taskId = taskId, soFarBytes = soFarBytes, totalBytes = totalBytes))
    }

    override fun onDownloadCompleted(taskId: String?) {
        appUpdateState.postValue(AppUpdateState(state = Status.DOWNLOAD_COMPLETED))
    }

    override fun onDownloadPaused(taskId: String?) {
    }

    override fun onDownloadError(taskId: String?, errorCode: Int) {
        appUpdateState.postValue(AppUpdateState(state = Status.DOWNLOAD_ERROR))
    }

    override fun onInstallPending(taskId: String?) {}

    override fun onInstallStarted(taskId: String?) {
        appUpdateState.postValue(AppUpdateState(state = Status.INSTALLING))
    }

    override fun onInstallProgress(taskId: String?, progress: Float) {}

    override fun onInstallCompleted(taskId: String?) {
        appUpdateState.postValue(AppUpdateState(state = Status.INSTALL_COMPLETED))
    }

    override fun onInstallError(taskId: String?, errorCode: Int) {
        appUpdateState.postValue(AppUpdateState(state = Status.INSTALL_ERROR))
    }
}
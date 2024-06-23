package com.zeekrlife.market.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zeekr.basic.Common
import com.zeekr.basic.appContext
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.market.task.uninstall.UninstallCallback
import com.zeekrlife.market.task.uninstall.UninstallSilentManager
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.load.LoadingType
import com.zeekrlife.task.base.bean.TaskInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDetailModel() : BaseViewModel(), UninstallCallback, InstallStateChangeListener {

    var appDetail = MutableLiveData<AppItemInfoBean?>()

    var appIsInstall = MutableLiveData<Boolean>()

    var appIsAllowInstall = MutableLiveData<Boolean>()

    var appUnInstallResult = MutableLiveData<Boolean>()

    var taskInfo = MutableLiveData<TaskInfo?>()

    var obtainDetailParamType: Int = -1

    var appVersionId: Long = -1

    var appId: Long = -1

    var appPackageName: String = ""

    private var isUnInstalling = false

    init {
        InstallAppManager.addInstallStateChangeListener(this)
    }

    /**
     * 卸载按钮
     * @param packageName
     */
    private fun checkInstall(packageName: String?): Boolean {
        var isInstall = false
        if (!packageName.isNullOrEmpty()) {
            isInstall = ApkUtils.isAppInstalled(appContext, packageName)
        }
        return isInstall
    }

    /**
     * 获取App详情
     * @param loadingXml Boolean 请求时是否需要展示界面加载中loading
     */
    fun getAppDetailByVersionId(loadingXml: Boolean = false) {
        rxHttpRequest {
            onRequest = {
                updateState(AppRepository.getAppDetail(appVersionId).await())
            }
            loadingType = if (loadingXml) LoadingType.LOADING_XML else LoadingType.LOADING_NULL
            requestCode = NetUrl.APP_DETAIL
        }
    }

    /**
     * 通过AppId获取
     */
    fun getAppDetailByAppId(loadingXml: Boolean = false) {
        rxHttpRequest {
            onRequest = {
                val response = AppRepository.getApps(appIds = longArrayOf(appId), pageNum = 1, pageSize = 1).await()
                val appVersionInfo = response.list?.getOrNull(0)
                updateState(AppRepository.getAppDetail(appVersionInfo?.id ?: -1).await())
            }
            loadingType = if (loadingXml) LoadingType.LOADING_XML else LoadingType.LOADING_NULL
            requestCode = NetUrl.APP_DETAIL
        }
    }

    /**
     * 通过包名获取
     */
    fun getAppDetailByPackageName(loadingXml: Boolean = false) {
        rxHttpRequest {
            onRequest = {
                val response = AppRepository.getApps(packages = arrayOf(appPackageName), pageNum = 1, pageSize = 1).await()
                val appVersionInfo = response.list?.getOrNull(0)
                updateState(AppRepository.getAppDetail(appVersionInfo?.id ?: -1).await())
            }
            loadingType = if (loadingXml) LoadingType.LOADING_XML else LoadingType.LOADING_NULL
            requestCode = NetUrl.APP_DETAIL
        }
    }

    /**
     * 更新TaskLayout按钮、卸载按钮状态
     */
    private fun updateState(detail: AppItemInfoBean?) {
        detail?.apply {
            updateTaskLayoutState(this)
            updateUnInstallState(this)
        }
        appDetail.postValue(detail)
    }

    /**
     * TaskLayout
     */
    private fun updateTaskLayoutState(detail: AppItemInfoBean) {
        val mTaskInfo = TaskHelper.toTaskInfo(app = detail)
        taskInfo.postValue(mTaskInfo)
    }

    /**
     * 卸载按钮
     */
    private fun updateUnInstallState(detail: AppItemInfoBean) {
        //卸载按钮是否显示
        val isInstall = checkInstall(detail.apkPackageName)
        appIsInstall.postValue(isInstall)
        //判断是否可卸载：可卸载应用：1、普通应用，2、是系统应用并且更新过
        var isAllowUnInstall = true
        try {
            val applicationInfo = Common.app.packageManager.getApplicationInfo(detail.apkPackageName ?: "", 0)
//            isAllowUnInstall = !ApkUtils.isSystemApp(applicationInfo) ||
//                (ApkUtils.isSystemApp(applicationInfo) && ApkUtils.isUpdatedSystemApp(applicationInfo))
            isAllowUnInstall = !ApkUtils.isSystemApp(applicationInfo)
            LogUtils.e("zzzupdateUnInstallState","isAllowUnInstall::$isAllowUnInstall")
        } catch (e: Exception) {
            e.logStackTrace()
        }
        appIsAllowInstall.postValue(isAllowUnInstall)
    }

    /**
     * 当监听到卸载时更新卸载按钮状态
     */
    private fun onInstallUpdateState() {
        appDetail.value?.apply {
            updateTaskLayoutState(this)
            updateUnInstallState(this)
        }
    }

    /**
     * 获取预览图
     */
    fun getPreviewPics(): List<String> =
        appDetail.value?.previewPic?.split(",")?.toList()?.take(5) ?: arrayListOf()

    /**
     * 获取包名
     */
    private fun getPackageName() = appDetail.value?.apkPackageName ?: ""

    /**
     * 获取TaskInfo
     */
    fun getTaskInfo() = taskInfo.value

    /**
     * 卸载
     */
    fun appUnInstall(): Boolean {
        try {
            val packageName = getPackageName()
            if (packageName.isNotEmpty() && !appContext.packageName.equals(packageName) && !isUnInstalling) {
                isUnInstalling = true
                viewModelScope.launch(Dispatchers.Default) {
                    try {
                        UninstallSilentManager.getInstance(appContext).uninstallSilent4L(
                            packageName, this@AppDetailModel
                        )
                    } catch (e: Exception) {
                        e.logStackTrace()
                        uninstallError()
                    }
                }
                return true
            } else {
                uninstallError()
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        InstallAppManager.removeInstallStateChangeListener(this)
        UninstallSilentManager.getInstance(appContext).removeUninstallSilent4LCallBack(getPackageName(), this)
    }

    /**
     * 开始卸载回调
     */
    override fun uninstallStarted() {
        InstallAppManager.unInstallStarted(getPackageName())
    }

    /**
     * 完成卸载回调
     */
    override fun uninstallCompleted() {
        isUnInstalling = false
    }

    /**
     * 卸载失败回调
     */
    override fun uninstallError() {
        isUnInstalling = false
        appUnInstallResult.postValue(false)
        InstallAppManager.unInstallFinished(getPackageName(), false)
    }

    /**
     * 安装成功回调
     */
    override fun onInstallSuccess(packageName: String) {
        LogUtils.e("zzzAppDetailModel","zzzonInstallSuccess:$packageName")
        isUnInstalling = false
        onInstallUpdateState()
    }

    /**
     * 卸载成功回调
     */
    override fun onUnInstallSuccess(packageName: String) {
        onInstallUpdateState()
        appUnInstallResult.postValue(true)
    }
}
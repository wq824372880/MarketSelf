package com.zeekrlife.market.ui.viewmodel

import android.app.Activity
import androidx.collection.arrayMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.code
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.msg
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.util.SPUtils
import com.zeekrlife.common.util.constant.SpConfig
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.manager.InstallAppManager.InstallStateChangeListener
import com.zeekrlife.market.sensors.SensorsTrack
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.market.ui.activity.AppDetailActivity
import com.zeekrlife.net.api.ApiPagerResponse
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.load.LoadStatusEntity
import com.zeekrlife.net.load.LoadingType
import com.zeekrlife.task.base.constant.TaskState
import com.zeekrlife.task.base.proxy.TaskProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAppCXViewModel : BaseViewModel(), InstallStateChangeListener {

    //需要更新的应用
    private val needUpdateAppMaps = arrayMapOf<String, AppItemInfoBean>()

    val pageData = MutableLiveData<ApiPagerResponse<AppItemInfoBean>>()

    // 更新展示
    val updateTips = MutableLiveData<Int>()

    //当app安装
    val onAppInstallPosition = MutableLiveData<Int>()

    //当app卸载
    val appUnInstallPosition = MutableLiveData<Int>()

    companion object{
        const val TAG = "zzzMyAppViewModel"
    }

    init {
        InstallAppManager.addInstallStateChangeListener(this)
    }

    override fun onCleared() {
        InstallAppManager.removeInstallStateChangeListener(this)
    }

    private fun isAutoUpdate() =
        SPUtils.getInstance().getBoolean(SpConfig.BooleanKey.AUTO_UPDATE, true)

    /**
     * 获取用户已安装应用、最近使用小程序
     */
    fun getMyApps(loadingXml: Boolean) {
        viewModelScope.launch {
            val isRefresh = true
            var loadXml = loadingXml
            //已安装应用
            val packageNames = InstallAppManager.installedPackageNamesFromPm()
            //优先缓存加载
            val pageCaches = InstallAppManager.getInstallAppCache()

            val hasCache = (pageCaches.list?.size ?: 0) > 0

            //是否显示加载页面
            if (hasCache && loadXml) {
                pageCaches.run { pageData.postValue(this) }
                loadXml = false
            }
            //请求
            rxHttpRequest {
                onRequest = {
                    needUpdateAppMaps.clear()
                    //根据包名请求获取应用信息
                    val appPager = AppRepository.getApps(
                        packages = packageNames.toTypedArray(),
                        pageNum = 1,
                        pageSize = packageNames.size
                    ).await()
                    //设置taskInfo,并获取更新数
                    withContext(Dispatchers.Default) {
                        //过滤不展示应用（不展示应用会设置为强制更新）
                        appPager.list = appPager.list?.filter { it.hideIcon == 0 }?.let { ArrayList(it) }
                        appPager.list?.forEach {
                            it.taskInfo = TaskHelper.toTaskInfo(app = it)
                            if (it.taskInfo != null && (it.taskInfo?.state ?: 0) >= TaskState.UPDATABLE) {
                                it.apkPackageName?.apply {
                                    //需要展示更新气泡提示
                                    needUpdateAppMaps[this] = it
                                    LogUtils.e(TAG,"getMyApps needUpdateAppMaps.keys::${needUpdateAppMaps.keys},needUpdateAppMaps.verCode::${
                                        needUpdateAppMaps.map{ (key,value)->
                                            key to value.apkVersion
                                        }
                                    }")
                                }
                            }
                        }

                        //小红点
                        LogUtils.e(TAG,"getMyApps11 needUpdateAppMaps.keys::${needUpdateAppMaps.keys},needUpdateAppMaps.verCode::${
                            needUpdateAppMaps.map{ (key,value)->
                                key to value.apkVersion
                            }
                        }")
                        updateTips.postValue(needUpdateAppMaps.size)
                        //刷新已安装缓存
                        InstallAppManager.flushInstallAppCache(appPager)
                    }
                    pageData.value = appPager
                }
                //因为可以加载缓存，异常需特殊处理
                onError = {
                    //如果没有缓存
                    if (!hasCache) {
                        loadingChange.showEmpty.value = LoadStatusEntity(
                            requestCode = requestCode,
                            throwable = it,
                            errorCode = it.code,
                            errorMessage = it.msg,
                            isRefresh = isRefreshRequest,
                            loadingType = loadingType,
                            intentData = intentData
                        )
                    } else {
                        loadingChange.showError.value =
                            LoadStatusEntity(
                                requestCode = requestCode,
                                throwable = it,
                                errorCode = it.code,
                                errorMessage = it.msg,
                                isRefresh = isRefreshRequest,
                            )
                    }
                }
                loadingType = if (loadXml) LoadingType.LOADING_XML else LoadingType.LOADING_NULL
                isRefreshRequest = isRefresh
                requestCode = NetUrl.APP_LIST
            }
        }
    }

    /**
     * 筛选出需要更新的应用
     */
//    private fun refreshNeedUpdateApps(): Boolean {
//        var refresh = false
//        needUpdateAppMaps.forEach { map ->
//            map.value.let {
//                it.taskInfo?.apply {
//                    if (state >= TaskState.UPDATABLE) {
//                        refresh = true
//                    }
//                }
//            }
//        }
//        return refresh
//    }

    fun refreshUpdateSummaryTips() {
        viewModelScope.launch {
            val appCache = InstallAppManager.getInstallAppCache()
            var refreshTips = false
            appCache.list?.forEach {
                it.taskInfo?.let { task ->
                    if (task.state > TaskState.DOWNLOADABLE) { //有需要更新的应用
                        if (!needUpdateAppMaps.contains(it.apkPackageName)) {
                            needUpdateAppMaps[it.apkPackageName] = it
                            refreshTips = true
                        }
                    } else { //没有需要更新的应用
                        if (needUpdateAppMaps.contains(it.apkPackageName)) {
                            needUpdateAppMaps.remove(it.apkPackageName)
                            refreshTips = true
                        }
                    }
                }
            }
            if (refreshTips) {
                LogUtils.e(TAG,"refreshUpdateSummaryTips needUpdateAppMaps.keys::${needUpdateAppMaps.keys},needUpdateAppMaps.verCode::${
                    needUpdateAppMaps.map{ (key,value)->
                        key to value.apkVersion
                    }
                }")
                updateTips.postValue(needUpdateAppMaps.size)
            }
        }
    }

    /**
     * 一键更新
     */
    fun startUpdate(): Boolean {
        if (needUpdateAppMaps.isEmpty) {
            return false
        }
        val sb = StringBuilder()
        needUpdateAppMaps.values.forEach { app ->
            val task = if (app.taskInfo == null || app.taskInfo?.state != TaskState.UPDATABLE) {
                // 校验下数据
                TaskHelper.toTaskInfo(app, true)
            } else {
                app.taskInfo
            }
            task?.apply {
                if (state == TaskState.UPDATABLE || state == TaskState.DOWNLOAD_PAUSED ||
                    state == TaskState.DOWNLOAD_ERROR || state == TaskState.INSTALL_ERROR
                ) {
                    sb.append("${app.apkName}|")
                    TaskProxy.getInstance().addTask(this)
                }
            }
        }

        //埋点
        if (sb.isNotEmpty()) {
            SensorsTrack.onAppUpdate("我的应用", sb.toString(), needUpdateAppMaps.size, 2)
        }
        return true
    }

    /**
     * 计算需更新App大小
     */
    fun getNeedUpdateSize(): Long {
        var total = 0L
        try {
            needUpdateAppMaps.values.forEach {
                total += it.apkSize?.toLong() ?: 0L
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return total
    }

    /**
     * 点击App跳转处理
     */
    fun startApp(activity: Activity?, position: Int) {
        pageData.value?.list?.get(position)?.let { AppDetailActivity.start(activity, it.id) }
    }

    /**
     * 安装下载开始
     * @param
     */
    override fun onInstallDownloadStarted(packageName: String) {
        try {
//            //刷新小红点
//            needUpdateBubbleTips.remove(packageName)
//            updateBubbleTips.postValue(needUpdateBubbleTips.size)
//            //更新概况
//            val app = InstallAppManager.getInstallAppMaps()?.get(packageName)
//            if (app != null && packageName.isNotEmpty() && !needUpdateAppMaps.contains(packageName)) {
//                needUpdateAppMaps[packageName] = app
//                updateSummaryTips.postValue(needUpdateAppMaps.size)
//            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 安装成功回调
     * @param
     */
    override fun onInstallSuccess(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                needUpdateAppMaps.remove(packageName)
                //刷新更新概况
                LogUtils.e(TAG,"getMyApps33 needUpdateAppMaps.keys::${needUpdateAppMaps.keys},needUpdateAppMaps.verCode::${
                    needUpdateAppMaps.map{ (key,value)->
                        key to value.apkVersion
                    }
                }")
                updateTips.postValue(needUpdateAppMaps.size)
            } catch (e: Exception) {
                e.logStackTrace()
            }

            val page = pageData.value
            if (page == null) {
                pageData.postValue(InstallAppManager.getInstallAppCache())
            } else {
                val installApp = InstallAppManager.getInstallAppMaps()?.get(packageName)
                installApp?.apply {
                    var apps = page.list
                    if (apps == null) {
                        apps = arrayListOf()
                        apps.add(this)
                        page.apply { pageData.postValue(this) }
                    } else {
                        apps.forEachIndexed { index, app ->
                            if (packageName == app.apkPackageName) {
                                //更新
                                onAppInstallPosition.postValue(index)
                                return@launch
                            }
                        }
                        //新增
                        page.list?.add(this)
                        val size = page.list?.size ?: 0
                        if (size > 0) {
                            onAppInstallPosition.postValue(size - 1)
                        }
                    }
                }
            }

            //如果页面为空或错误刷新页面
            if (loadingChange.showSuccess.value != true) {
                loadingChange.showSuccess.postValue(true)
            }
        }
    }

    /**
     * 应用卸载成功
     */
    override fun onUnInstallSuccess(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            synchronized(pageData){ //出现bug java.util.ConcurrentModificationException  加锁防止多线程操作
                val apps = pageData.value?.list?.iterator()
                var index = 0
                while (apps?.hasNext() == true) {
                    val app = apps.next()
                    if (app.apkPackageName.equals(packageName)) {
                        apps.remove()
                        appUnInstallPosition.postValue(index)
                    }
                    index++
                }

                needUpdateAppMaps.remove(packageName)
                //刷新更新概况
                LogUtils.e(TAG,"getMyApps44 needUpdateAppMaps.keys::${needUpdateAppMaps.keys},needUpdateAppMaps.verCode::${
                    needUpdateAppMaps.map{ (key,value)->
                        key to value.apkVersion
                    }
                }")
                updateTips.postValue(needUpdateAppMaps.size)
            }
        }
    }
}
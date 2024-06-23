package com.zeekrlife.market.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import com.zeekr.basic.Common
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.common.util.StringUtils
import com.zeekrlife.market.app.ext.mmkvSave
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.ValueKey.CACHE_MY_INSTALL_APPS
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.task.AbstractArrangeCallback
import com.zeekrlife.market.task.ITaskInfo
import com.zeekrlife.net.api.ApiPagerResponse
import com.zeekrlife.net.interception.logging.util.logD
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.task.base.bean.ExpandAppInfo
import com.zeekrlife.task.base.manager.TaskManager
import com.zeekrlife.task.base.proxy.TaskProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONException
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.CancellationException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 商城应用内监听应用下载、安装、卸载
 * 注意：商城中的卸载只有一处，App详情，所以详情中调用了InstallAppManager
 */
object InstallAppManager : AbstractArrangeCallback() {

    private const val TAG = "InstallAppManager"

    private var installAppCache: MutableMap<String, AppItemInfoBean>? = null

    private var pageCaches: ApiPagerResponse<AppItemInfoBean>? = null

    @Volatile
    private var installStateChangeListeners = CopyOnWriteArrayList<InstallStateChangeListener>()

    @Volatile private var isLoadCache = false

    private val mutex = Mutex()

    /**
     * 应用卸载Receiver
     */
    private val packageRemovedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.data?.schemeSpecificPart?.let {
                //更新情况下也会接收包删除的广播
                if (ApkUtils.isAppInstalled(appContext, it)) return
                unInstallFinished(it, true)
            }
        }
    }

    /**
     * 初始化
     */
    fun init() {
        //全局监听任务下载状态以及安装状态
        TaskManager.getInstance().registerArrangeCallback(this)
        //加载缓存中已安装应用集合
        MainScope().launch(Dispatchers.Default) {
            mutex.withLock { loadInstallAppCache() }
        }
        //监听卸载广播
        registerPackageRemovedReceiver()
    }

    private fun registerPackageRemovedReceiver() {
        try {
            Common.app.registerReceiver(packageRemovedReceiver, IntentFilter(Intent.ACTION_PACKAGE_REMOVED).apply {
                addDataScheme("package");
            })
        } catch (e: SecurityException) {
            // 处理 SecurityException 异常
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            // 处理 IllegalArgumentException 异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 卸载完成
     * @param packageName
     * @param isUninstall
     */
    private fun unRegisterPackageRemovedReceiver() {
        try {
            Common.app.unregisterReceiver(packageRemovedReceiver)
        } catch (e: SecurityException) {
            // 处理 SecurityException 异常
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            // 处理 IllegalArgumentException 异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    fun release() {
        TaskManager.getInstance().unregisterArrangeCallback(this)
        installStateChangeListeners.clear()
        unRegisterPackageRemovedReceiver()
    }

    /**
     * 添加安装、卸载状态监听
     * @param listener
     */
    fun addInstallStateChangeListener(listener: InstallStateChangeListener) {
        try {
            if (!installStateChangeListeners.contains(listener)) {
                installStateChangeListeners.add(listener)
            }
            Log.e(TAG, "addInstallStateChangeListener:${installStateChangeListeners}")
        } catch (e: UnsupportedOperationException) {
            // 处理 UnsupportedOperationException 异常
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            // 处理 IllegalArgumentException 异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 移除安装、卸载状态监听
     * @param listener
     */
    fun removeInstallStateChangeListener(listener: InstallStateChangeListener) {
        try {
            installStateChangeListeners.remove(listener)
        } catch (e: UnsupportedOperationException) {
            // 处理 UnsupportedOperationException 异常
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            // 处理 IllegalArgumentException 异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 下载之前
     */
    override fun onDownloadStarted(taskId: String) {
        val task = TaskProxy.getInstance().getTask(taskId)
        getExpandAppInfo(task)?.let { app ->
            //导入App证书
            importAppSign(task, app)
            installDownloadStarted(app.packageName)
        }
    }

    /**
     * 下载结束
     */
    override fun onDownloadFinished(taskId: String, successful: Boolean, errorCode: Int) {
        "onDownloadFinished:$taskId successful -> $successful".logE(TAG)
        try {
            val task = TaskProxy.getInstance().getTask(taskId)
            getExpandAppInfo(task)?.apply {
                if(isForceUpdate && !successful) {
                    //对强制更新应用添加重试策略
                    TaskRetryManager.addRetryTask(packageName, versionCode, taskId, errorCode, task)
                }
            }
        } catch (e: IllegalStateException) {
            // 处理 IllegalStateException 异常
            e.logStackTrace()
        } catch (e: NullPointerException) {
            // 处理 NullPointerException 异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 安装之前
     */
    override fun onInstallStarted(taskId: String) {}

    /**
     * 安装结束
     */
    override fun onInstallFinished(taskId: String, successful: Boolean, errorCode: Int) {
        "onInstallFinished: successful -> $successful  $taskId".logD(TAG)
        if (successful) {
            installSuccess(taskId)
        } else {
            installFail(taskId)
        }
    }

    /**
     * 当安装下载开始时触发的操作。
     * 这个函数使用了协程来异步执行操作，主要步骤包括记录日志和调用`onInstallDownloadStarted`函数。
     *
     * @param packageName 要安装的软件包名称。
     */
    private fun installDownloadStarted(packageName: String) {
        try {
            // 在默认的协程调度器上启动一个新的协程
            MainScope().launch(Dispatchers.Default) {
                // 记录下载开始的日志
                "installDownloadStarted :${packageName}".logE(TAG)
                // 触发下载开始的回调
                onInstallDownloadStarted(packageName)
            }
        } catch (e: CancellationException) {
            // 处理协程被取消的异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时被取消的异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获并记录其他类型的异常
            e.logStackTrace()
        }
    }


    /**
     * 安装成功的处理逻辑。
     * @param taskId 任务ID，用于标识此次安装的任务。
     * 该函数没有返回值。
     */
    private fun installSuccess(taskId: String) {
        try {
            // 在默认线程池中启动一个协程来处理安装成功的逻辑
            MainScope().launch(Dispatchers.Default) {
                // 根据任务ID获取扩展应用信息
                getExpandAppInfo(taskId)?.let { app ->
                    // 打印安装成功的日志
                    "install success:${app.packageName}".logE(TAG)
                    // 更新已安装应用的缓存信息
                    updateInstallApp(expandApp = app)
                    // 调用安装成功的回调函数
                    onInstallSuccess(app)
                }
            }
        } catch (e: CancellationException) {
            // 处理协程被取消的异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时被取消的异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获并处理其他类型的异常
            e.logStackTrace()
        }
    }


    /**
     * 处理安装失败的逻辑。
     * @param taskId 任务ID，用于标识具体的安装任务。
     */
    private fun installFail(taskId: String) {
        try {
            // 在默认线程池中启动一个新的协程来处理安装失败的情况
            MainScope().launch(Dispatchers.Default) {
                // 尝试根据任务ID获取扩展应用信息
                getExpandAppInfo(taskId)?.let { app ->
                    // 如果获取到应用信息，则记录安装失败的日志并调用安装失败的回调
                    "install fail:${app.packageName}".logE(TAG)
                    onInstallFail(app)
                }
            }
        } catch (e: CancellationException) {
            // 处理协程被取消的异常，通常发生在应用程序主动取消协程的情况下
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时被取消的异常，可能发生在协程执行时间超过预定阈值时
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他所有类型的异常，进行通用的异常处理
            e.logStackTrace()
        }
    }

    /**
     * 当安装下载开始时调用此函数，将会通知所有的安装状态变更监听器。
     *
     * @param packageName 要安装的软件包名称。
     */
    private fun onInstallDownloadStarted(packageName: String) {
        try {
            // 遍历并调用所有安装状态变更监听器的下载开始回调
            installStateChangeListeners.forEach {
                it.onInstallDownloadStarted(packageName)
            }
        } catch (e: CancellationException) {
            // 处理协程取消异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时取消异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获并记录其他所有异常
            e.logStackTrace()
        }
    }

    /**
     * 安装成功时的处理逻辑。
     *
     * @param app 表示安装成功的应用信息。
     */
    private fun onInstallSuccess(app: ExpandAppInfo) {
        try {
            // 遍历并调用所有安装状态监听器的安装成功回调
            installStateChangeListeners.forEach {
                it.onInstallSuccess(app.packageName)
            }
        } catch (e: CancellationException) {
            // 处理协程取消异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时取消异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他异常
            e.logStackTrace()
        }
    }

    /**
     * 当应用安装失败时调用此函数，通知所有安装状态监听器安装失败。
     *
     * @param app 表示安装失败的应用信息。
     */
    private fun onInstallFail(app: ExpandAppInfo) {
        try {
            // 遍历并通知所有安装状态监听器该应用安装失败
            installStateChangeListeners.forEach {
                it.onInstallFail(app.packageName)
            }
        } catch (e: CancellationException) {
            // 处理协程取消异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时取消异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获并记录其他异常
            e.logStackTrace()
        }
    }

    /**
     * 卸载开始
     * @param packageName 包名
     */
    fun unInstallStarted(packageName: String) {}

    /**
     * 卸载结束
     * @param packageName
     * @param successful
     */
    fun unInstallFinished(packageName: String, successful: Boolean) {
        if (successful) {
            unInstallSuccess(packageName)
        }
    }

    /**
     * 当卸载应用成功时调用的函数。
     *
     * @param packageName 被卸载的应用的包名。
     * 该函数会在一个新启动的协程中执行，首先检查是否有关于该应用的安装缓存信息，
     * 如果存在，则调用 [onUnInstallSuccess] 和 [removeUnInstallApp] 进行相应处理。
     */
    private fun unInstallSuccess(packageName: String) {
        try {
            // 在 Default 调度器上启动一个新的协程来处理卸载成功的逻辑
            MainScope().launch(Dispatchers.Default) {
                // 检查安装缓存中是否存在该包名的应用，如果存在，则进行处理
                if(installAppCache?.get(packageName) != null) {
                    // 处理卸载成功的回调
                    onUnInstallSuccess(packageName)
                    // 从卸载应用列表中移除该应用
                    removeUnInstallApp(packageName)
                }
            }
        } catch (e: CancellationException) {
            // 处理协程被取消的异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时被取消的异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获并处理其他类型的异常
            e.logStackTrace()
        }
    }

    /**
     * 当卸载成功时调用此函数，通知所有的安装状态变更监听器。
     *
     * @param packageName 被卸载的应用程序的包名。
     */
    private fun onUnInstallSuccess(packageName: String) {
        try {
            // 遍历并通知所有的安装状态变更监听器卸载成功
            installStateChangeListeners.forEach {
                it.onUnInstallSuccess(packageName)
            }
        } catch (e: CancellationException) {
            // 处理协程取消异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时取消异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他异常
            e.logStackTrace()
        }
    }

    /**
     * 从任务信息中获取扩展应用信息。
     *
     * @param taskInfo 任务信息，不可为null。
     * @return 返回解析得到的扩展应用信息对象，如果无法解析或输入的任务信息为空，则返回null。
     */
    private fun getExpandAppInfo(taskInfo: ITaskInfo?): ExpandAppInfo? {
        try {
            taskInfo?.apply {
                // 尝试从任务信息的expand字段中解析出ExpandAppInfo对象
                if (!StringUtils.isEmpty(expand)) {
                    return GsonUtils.fromJson(expand, ExpandAppInfo::class.java)
                }
            }
        } catch (e: CancellationException) {
            // 处理协程取消异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时取消异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他异常
            e.logStackTrace()
        }
        // 如果解析失败或没有找到有效信息，返回null
        return null
    }

    /**
     * 获取扩展应用信息。
     *
     * 该函数通过给定的任务ID，获取对应任务的扩展应用信息。它首先调用[TaskProxy.getInstance().getTask(taskId)]获取特定任务对象，
     * 然后基于这个任务对象获取扩展应用信息。
     *
     * @param taskId 任务的唯一标识符。
     * @return [ExpandAppInfo] 对象，如果找不到对应的扩展应用信息，则返回null。
     */
    private fun getExpandAppInfo(taskId: String): ExpandAppInfo? {
        return getExpandAppInfo(TaskProxy.getInstance().getTask(taskId))
    }

    /**
     * 获取App证书，为安装做准备
     */
    private fun importAppSign(task: ITaskInfo, app: ExpandAppInfo) {
        try {
            MainScope().launch(Dispatchers.Default) {
                runCatching {
                    val result = AppRepository.getInstallDigitalSignature(app.packageName, app.versionCode, task.apkSha256).await()
                    //导入签名
                    result?.apply {
                        "sign ==>${sign}; signType ==> ${signType}".logD(TAG)
                        importSignSync(app.packageName, app.versionCode, task.apkSha256, app.appSign, sign, signType)
                    }
                }.onFailure {
                    "getInstallDigitalSignature onFailure :$${task.id}".logD(TAG)
                }
            }
        } catch (e: CancellationException) {
            // 处理协程取消异常
            e.logStackTrace()
        } catch (e: TimeoutCancellationException) {
            // 处理协程超时取消异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他异常
            e.logStackTrace()
        }
    }

    /**
     * 因为PMS安装时需要确认安装的合法性，需往安装校验服务导入签名，触发时机准备下载前
     * @param packageName 包名
     * @param versionCode
     * @param apkMD5
     * @param sign
     * @param signType 签名依据类型，0 根据车辆carVin，1 根据车主唯一凭据userId
     */
    private fun importSignSync(
        packageName: String, versionCode: Long, apkMD5: String?, apkSign: String?, sign: String?, signType: Int?
    ) {
        try {
            val intent = Intent().apply {
                action = Constants.INSTALL_VERIFIER_ACTION
                setPackage(Constants.INSTALL_VERIFIER_PACKAGE)
                putExtras(Bundle().apply {
                    putInt(Constants.INSTALL_VERIFIER_CMD_KEY, Constants.INSTALL_VERIFIER_CMD_IMPORT_RECORD);
                    putString(Constants.EXTAR_PKG_NAME, packageName)
                    putLong(Constants.EXTAR_PKG_VERSION_CODE, versionCode)
                    putString(Constants.EXTAR_APK_SIGN, apkSign ?: "")
                    putString(Constants.EXTAR_APK_MD5, apkMD5 ?: "")
                    putString(Constants.EXTAR_SIGN_TEXT, sign ?: "")
                    putInt(Constants.EXTAR_SIGN_TYPE, signType ?: 0)
                })
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                appContext.startForegroundService(intent)
            } else {
                appContext.startService(intent)
            }
        } catch (e: SecurityException) {
            // 处理权限异常的逻辑
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理 IllegalStateException 的逻辑
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他未特别处理的异常
            e.logStackTrace()
        }
    }

    /**
     * 获取本地已安装应用包名,过滤掉Android或者Google系统应用以及应用市场自己
     */
    suspend fun installedPackageNamesFromPm() = withContext(Dispatchers.Default) {
        ApkUtils.getAllAppsInfo(appContext).asSequence().filter {
            val packageName = it.packageName ?: ""
            !appContext.packageName.equals(packageName) && !ApkUtils.isAndroidOrGoogleApp(packageName)
        }.map { it.packageName ?: "" }.toList()
    }

    fun getInstallAppMaps(): Map<String, AppItemInfoBean>? = installAppCache

    /**
     * 获取安装应用的缓存数据。
     * 该函数是挂起函数，它在默认的线程池中执行，并使用互斥锁确保线程安全。
     * 如果缓存已加载，则直接使用缓存数据；否则，从磁盘或网络加载安装应用的缓存数据。
     *
     * @return 返回应用安装的缓存数据。
     */
    suspend fun getInstallAppCache() = withContext(Dispatchers.Default) {
        mutex.withLock {
            // 判断缓存是否已加载，若已加载直接返回缓存，否则加载缓存
            if (isLoadCache) pageInstance(pageCaches) else pageInstance(loadInstallAppCache())
        }
    }

    /**
     * 创建页面实例
     *
     * 该函数用于创建一个ApiPagerResponse实例，主要为了处理和返回页面数据。
     *
     * @param page 可空的ApiPagerResponse<AppItemInfoBean>对象，包含页面数据列表。
     * @return 返回一个新的ApiPagerResponse<AppItemInfoBean>实例，其中包含了固定的页码、数据列表、总页数和列表大小。
     */
    private fun pageInstance(page: ApiPagerResponse<AppItemInfoBean>?): ApiPagerResponse<AppItemInfoBean> {
        // 创建并返回一个新的ApiPagerResponse实例，如果传入的page不为空，则使用其list字段，否则使用空的mutableList
        return ApiPagerResponse(
            1, ArrayList(page?.list ?: mutableListOf()), 1, page?.list?.size ?: 0
        )
    }

    /**
     * 加载缓存中已安装应用
     */
    private suspend fun loadInstallAppCache(): ApiPagerResponse<AppItemInfoBean>? {
        try {
            if (!isLoadCache) {
                isLoadCache = true
                val installedPackages = installedPackageNamesFromPm()
                val myAppsCache = mmkvSave.getString(CACHE_MY_INSTALL_APPS, "")
                if (!myAppsCache.isNullOrEmpty()) {
                    val type: Type = ParameterizedTypeImpl[ApiPagerResponse::class.java, AppItemInfoBean::class.java]
                    GsonUtils.fromJson<ApiPagerResponse<AppItemInfoBean>>(myAppsCache, type)?.apply {
                        if (list != null && (list?.size ?: 0) > 0) {
                            //从缓存中清除被卸载的应用
                            val caches = list?.asSequence()?.filter {
                                it != null && installedPackages.contains(it.apkPackageName)
                            }?.toList()

                            val cacheSize = caches?.size ?: 0
                            //转map
                            installAppCache = caches?.associate { app ->
                                val packageName = app?.apkPackageName ?: ""
                                Pair(packageName, app?:AppItemInfoBean())
                            }?.toMutableMap()

                            var pager: ApiPagerResponse<AppItemInfoBean>? = this
                            //判断缓存是否一致
                            if (cacheSize != list?.size) {
                                var pageCacheJson = ""
                                if (cacheSize > 0) {
                                    caches?.apply {
                                        pager = ApiPagerResponse(1, ArrayList(caches), 1, caches.size)
                                        pageCacheJson = GsonUtils.toJson(pager)
                                    }
                                } else {
                                    pager = null
                                }
                                mmkvSave.putString(CACHE_MY_INSTALL_APPS, pageCacheJson)
                            }
                            pageCaches = pager
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.logStackTrace()
            mmkvSave.putString(CACHE_MY_INSTALL_APPS, "")
        } catch (e: IOException) {
            e.logStackTrace()
            mmkvSave.putString(CACHE_MY_INSTALL_APPS, "")
        } catch (e: IllegalStateException) {
            e.logStackTrace()
            mmkvSave.putString(CACHE_MY_INSTALL_APPS, "")
        } catch (e: TypeCastException) {
            e.logStackTrace()
            mmkvSave.putString(CACHE_MY_INSTALL_APPS, "")
        }
        return pageCaches
    }

    /**
     * 添加、更新成功安装的应用应用
     * @param expandApp
     */
    suspend fun updateInstallApp(apkUrl: String? = "", apkMd5: String? = "",apkSha256:String?="", expandApp: ExpandAppInfo) {
        try {
            if(expandApp.isHideIcon) {
                return
            }

            if (!isLoadCache) {
                loadInstallAppCache()
            }
            var app: AppItemInfoBean? = installAppCache?.get(expandApp.packageName)
            //应用新增
            if (app == null) {
                app = AppItemInfoBean(
                    id = expandApp.appVersionId.toLong(),
                    apkName = expandApp.apkName,
                    apkPackageName = expandApp.packageName,
                    apkVersionName = expandApp.versionName,
                    apkVersion = expandApp.versionCode.toString(),
                    categoryPid = expandApp.categoryId,
                    categoryName = expandApp.categoryName,
                    updateTimeDisplay = expandApp.updateTimeDisplay,
                    updates = expandApp.updateDescription,
                    dualSoundSource = expandApp.isDualAudio,
                    supportDrivingPassengerUser = expandApp.supportDrivingPassengerUser,
                    supportDrivingUser = expandApp.supportDrivingUser,
                    slogan = expandApp.appSlogan,
                )

                var apps = pageCaches?.list
                if (apps.isNullOrEmpty()) {
                    apps = arrayListOf()
                }
                apps.add(app)

                val page = ApiPagerResponse(1, apps, 1, apps.size)
                pageCaches = page

                //app maps
                if (installAppCache == null) {
                    installAppCache = mutableMapOf()
                }
                installAppCache?.put(expandApp.packageName, app)
            } else {
                //应用更新
                if (app.apkVersion != expandApp.versionCode.toString()) {
                    app.id = expandApp.appVersionId.toLong()
                    app.apkVersionName = expandApp.versionName
                    app.apkVersion = expandApp.versionCode.toString()
                    app.categoryPid = expandApp.categoryId
                    app.categoryName = expandApp.categoryName
                    app.updateTimeDisplay = expandApp.updateTimeDisplay
                    app.updates = expandApp.updateDescription
                    app.dualSoundSource = expandApp.isDualAudio
                    app.supportDrivingPassengerUser = expandApp.supportDrivingPassengerUser
                    app.supportDrivingUser = expandApp.supportDrivingUser
                    app.slogan = expandApp.appSlogan
                    app.apkSize = expandApp.apkSize.toDouble()
                    if(!apkUrl.isNullOrEmpty() && app.apkUrl != apkUrl) {
                        app.apkUrl = apkUrl
                    }
                    if(!apkMd5.isNullOrEmpty() && app.apkMd5 != apkMd5) {
                        app.apkMd5 = apkMd5
                    }
                    if(!apkSha256.isNullOrEmpty() && app.sha256 != apkSha256) {
                        app.sha256 = apkSha256
                    }
                }
            }
            pageCaches?.apply { mmkvSave.putString(CACHE_MY_INSTALL_APPS, GsonUtils.toJson(this)) }
        } catch (e: JSONException) {
            e.logStackTrace()
        } catch (e: IOException) {
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            e.logStackTrace()
        } catch (e: TypeCastException) {
            e.logStackTrace()
        }
    }

    /**
     * 删除已卸载应用
     * @param packageName
     */
    private suspend fun removeUnInstallApp(packageName: String) {
        try {
            if (!isLoadCache) {
                loadInstallAppCache()
            }
            val app = installAppCache?.get(packageName)
            app?.let {
                val list = pageCaches?.list?:return@let
                list.removeIf{
                    app.apkPackageName.contentEquals(packageName)
                }
                installAppCache?.remove(packageName)
                mmkvSave.putString(CACHE_MY_INSTALL_APPS, GsonUtils.toJson(pageCaches))
            }
        } catch (e: NoSuchElementException) {
            e.logStackTrace()
        } catch (e: ConcurrentModificationException) {
            e.logStackTrace()
        } catch (e: UnsupportedOperationException) {
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            e.logStackTrace()
        }
    }

    /**
     * 刷新缓存列表
     * @param appPager
     */
    fun flushInstallAppCache(appPager: ApiPagerResponse<AppItemInfoBean>) {
        try {
            val page = pageInstance(appPager)
            mmkvSave.putString(CACHE_MY_INSTALL_APPS, GsonUtils.toJson(page))
            installAppCache = page.list?.associate { app -> Pair(app.apkPackageName ?: "", app) }?.toMutableMap()
            pageCaches = page
        } catch (e: IllegalStateException) {
            e.logStackTrace()
        } catch (e: JSONException) {
            e.logStackTrace()
        } catch (e: IOException) {
            e.logStackTrace()
        }
    }

    /**
     * 基于缓存判断是否已安装
     * @param packageName 包名
     */
    fun isAlreadyInstalled(packageName: String?): Boolean {
        if (packageName.isNullOrEmpty()) return false
        return installAppCache?.get(packageName) != null
    }

    /**
     * 判断是否需要更新指定的软件包。
     *
     * @param packageName 要检查更新的软件包名称。如果为空或null，则认为不需要更新。
     * @param versionCode 要检查更新的软件包版本代码。如果为null，则认为不需要更新。
     * @return 如果当前安装的软件包版本低于指定的版本代码，则返回true，表示需要更新；否则返回false。
     */
    fun isRequiredUpdate(packageName: String?, versionCode: Long?): Boolean {
        // 如果包名为空或versionCode为null，直接返回false，不需要更新
        if (packageName.isNullOrEmpty() || versionCode == null) return false
        try {
            // 尝试从安装应用的缓存中获取指定包名的APP信息
            val app = installAppCache?.get(packageName)
            // 获取应用的版本代码，如果不存在则将其设置为Long的最大值，表示一个非常旧的版本
            val appVersionCode = app?.apkVersion?.toLong() ?: Long.MAX_VALUE
            // 如果指定的versionCode大于应用当前的版本代码，则需要更新
            if (versionCode > appVersionCode) {
                return true
            }
        } catch (e: NumberFormatException) {
            // 捕获转换版本号时可能发生的数字格式异常
            e.logStackTrace()
        } catch (e: NoSuchElementException) {
            // 捕获尝试获取不存在的元素时抛出的异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他所有异常
            e.logStackTrace()
        }
        // 如果没有发现需要更新的情况，则返回false
        return false
    }

    /**
     * App安装、卸载状态监听
     */
    interface InstallStateChangeListener {

        /**
         * 应用安装下载任务开始
         */
        fun onInstallDownloadStarted(packageName: String) {}

        /**
         * 应用安装成功
         */
        fun onInstallSuccess(packageName: String) {}

        /**
         * 应用安装失败
         */
        fun onInstallFail(packageName: String) {}

        /**
         * 应用卸载成功
         */
        fun onUnInstallSuccess(packageName: String) {}
    }
}
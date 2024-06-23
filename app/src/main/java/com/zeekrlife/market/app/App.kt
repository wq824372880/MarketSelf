package com.zeekrlife.market.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDex
import com.effective.android.anchors.AnchorsManager
import com.effective.android.anchors.anchors
import com.effective.android.anchors.graphics
import com.effective.android.anchors.startUp
import com.effective.android.anchors.taskFactory
import com.zeekr.basic.Common
import com.zeekr.basic.appContext
import com.zeekr.basic.currentProcessName
import com.zeekr.car.tsp.TspAPI
import com.zeekrlife.common.ext.getUINightMode
import com.zeekrlife.market.BuildConfig
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.ui.viewmodel.EventViewModel
import com.zeekrlife.market.utils.CrashHandler
import com.zeekrlife.market.utils.ScreenSizeCompat
import com.zeekrlife.market.utils.applet.AppletUtils
import com.zeekrlife.market.worker.ThirdUpdateStartWorker
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { App.eventViewModelInstance }

open class App : Application(), ViewModelStoreOwner {

    companion object {
        lateinit var eventViewModelInstance: EventViewModel
        var configurationChanged = false
    }

    private lateinit var mAppViewModelStore: ViewModelStore

    private var mFactory: ViewModelProvider.Factory? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        mAppViewModelStore = ViewModelStore()
        eventViewModelInstance = getAppViewModelProvider()[EventViewModel::class.java]
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandler.getInstance().init()
        Common.init(this, BuildConfig.DEBUG)
//        ScreenSizeCompat.init(this)
        val processName = currentProcessName
        if (currentProcessName == packageName) {
            // 主进程初始化
            onMainProcessInit()
        } else {
            // 其他进程初始化
            processName?.let { onOtherProcessInit(it) }
        }
    }

    /**
     * @description  代码的初始化请不要放在onCreate直接操作，按照下面新建异步方法
     */
    @SuppressLint("LogNotTimber")
    private fun onMainProcessInit() {

        AnchorsManager.getInstance().debuggable(true).taskFactory { AppTaskFactory() }
            //设置锚点
            .anchors {
                arrayOf(
                    InitNetWork.TASK_ID,
                    InitZeekrCommonAPI.TASK_ID,
                    InitUtils.TASK_ID,
                )
            }.graphics {
                arrayOf(
                    InitDefault.TASK_ID,
                    InitNetWork.TASK_ID,
                    InitComm.TASK_ID,
                    InitUtils.TASK_ID,
                    InitToast.TASK_ID,
                    InitMarketTask.TASK_ID,
                    InitDataProviderTask.TASK_ID,
                    InitZeekrCommonAPI.TASK_ID,
                    InitAdapterAPI.TASK_ID,
                    InitStrModeRegister.TASK_ID
                )
            }.startUp()

        eventViewModelInstance.strModeChangeEvent.observeForever { strMode ->
            //退出str模式
            if (strMode == 0) {
                android.util.Log.e("App", "str模式 == 0 触发检测更新")
                //检测更新服务（自动更新+强制更新）
                ThirdUpdateStartWorker.startWorker(appContext)
            }
        }

        //校验并清理缓存
        checkCacheValidity()

        //预加载数据
        PreloadDataUtils.asyncLoad()
    }

    @SuppressLint("LogNotTimber")
    private fun checkCacheValidity() {
        try {
            val envStr = TspAPI.create(appContext).envType.string()
            val cacheEnvStr = CacheExt.getDhuEnv()
            Log.e("App", "check currEnvStr -> $envStr ; cacheEnvStr -> $cacheEnvStr")
            if (envStr != cacheEnvStr) {
                CacheExt.setDhuEnv(envStr)
                CacheExt.setCategoryList("")
                CacheExt.setCategoryListMd5("")
                CacheExt.setRecommendList("")
//                CacheExt.setRecommendListMd5("")
            }
        } catch (e: IOException) {
            // 处理输入输出异常
            Log.e("App", "IOException: ${e.message}")
        } catch (e: JSONException) {
            // 处理 JSON 解析异常
            Log.e("App", "JSONException: ${e.message}")
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e("App", "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            Log.e("App", "checkCacheValidity error ${Log.getStackTraceString(e)}")
        }
    }

    /**
     * 其他进程初始化，[processName] 进程名
     */
    private fun onOtherProcessInit(processName: String) {

    }

    /**
     * 获取一个全局的ViewModel
     */
    private fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, this.getAppFactory())
    }

    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mFactory as ViewModelProvider.Factory
    }

    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }

    @SuppressLint("LogNotTimber")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChanged = true
        Log.e("zzzApp", "onConfigurationChanged:${newConfig.uiMode}")
//        ScreenSizeCompat.getInstance(this).onConfigurationChanged(newConfig)
        val themeMode = if (getUINightMode()) {
            "dark"
        } else {
            "light"
        }
        val `object` = JSONObject()
        try {
            `object`.put("themeMode", themeMode)
            AppletUtils.sendEvent("ampeHWEnvChanged", `object`.toString())
        } catch (e: JSONException) {
            // 处理 JSON 异常
            Log.e("zzzApp", "JSONException: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // 处理参数异常
            Log.e("zzzApp", "IllegalArgumentException: ${e.message}")
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e("zzzApp", "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.printStackTrace()
            Log.e("zzzApp", "Exception: ${Log.getStackTraceString(e)}")
        }

    }

}
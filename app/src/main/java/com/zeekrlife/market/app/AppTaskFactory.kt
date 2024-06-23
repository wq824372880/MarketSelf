package com.zeekrlife.market.app

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Secure
import android.util.Log
import com.effective.android.anchors.task.Task
import com.effective.android.anchors.task.TaskCreator
import com.effective.android.anchors.task.project.Project
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import com.zeekr.basic.appContext
import com.zeekr.car.adaptapi.CarApiProxy
import com.zeekr.car.api.*
import com.zeekr.sdk.multidisplay.impl.MultidisplayAPI
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil
import com.zeekrlife.common.widget.refresh.CustomRefreshFooter
import com.zeekrlife.common.widget.refresh.CustomRefreshHeader
import com.zeekrlife.common.widget.state.BaseEmptyCallback
import com.zeekrlife.common.widget.state.BaseErrorCallback
import com.zeekrlife.common.widget.state.BaseLoadingCallback
import com.zeekrlife.market.manager.AppletPropertyManager
import com.zeekrlife.market.manager.InstallAppManager
import com.zeekrlife.market.sensors.RequestInterceptor
import com.zeekrlife.market.user.UserLoginCallback
import com.zeekrlife.market.utils.AppletImpl
import com.zeekrlife.net.api.NetHttpClient
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.task.base.proxy.TaskProxy
import rxhttp.RxHttpPlugins
import java.util.*

object TaskCreate : TaskCreator {
    override fun createTask(taskName: String): Task {
        return when (taskName) {
            InitNetWork.TASK_ID -> InitNetWork()
            InitComm.TASK_ID -> InitComm()
            InitUtils.TASK_ID -> InitUtils()
            InitToast.TASK_ID -> InitToast()
            InitMarketTask.TASK_ID -> InitMarketTask()
            InitDataProviderTask.TASK_ID -> InitDataProviderTask()
            InitZeekrCommonAPI.TASK_ID -> InitZeekrCommonAPI()
            InitAdapterAPI.TASK_ID -> InitAdapterAPI()
            InitStrModeRegister.TASK_ID -> InitStrModeRegister()
            else -> InitDefault()
        }
    }
}

class InitDefault : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "0"
    }

    override fun run(name: String) {

    }
}

/**
 * 初始化网络
 */
class InitNetWork : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "1"
    }

    override fun run(name: String) {

        NetHttpClient.getNetEnvParams().apply {
            NetUrl.BASE_URL = url
            //传入自己的OKHttpClient 并添加了自己的拦截器
            RxHttpPlugins.init(
                NetHttpClient.getDefaultOkHttpClient(appContext, this)
                    .addInterceptor(RequestInterceptor()).build()
            )
            //扩展请求头
            NetHttpClient.addHttpHeadExt(mutableMapOf<String, String>().apply {
                this["x-app-version"] = ApkUtils.getAppInfo(appContext)?.versionCode?.toString() ?: ""
            })
            com.zeekrlife.net.interception.logging.util.common = true
            "mVersionCode:${ApkUtils.getAppInfo(appContext)?.versionCode}".logE("marketAppVersionCode")
        }

        DeviceApiManager.getInstance().deviceAPI.init(appContext) { p0, _ ->
            if (p0) {
                InitZeekrCommonAPI.mDeviceApiReady = p0
                ThreadPoolUtil.runOnSubThread({ //初始化Arome
                    AppletImpl().initArome()
                },0)
            }
        }
    }
}

//初始化常用控件类
class InitComm : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "2"
    }

    override fun run(name: String) {
        SmartRefreshLayout.setDefaultRefreshInitializer { _, layout ->
            //设置 SmartRefreshLayout 通用配置
            layout.setEnableScrollContentWhenLoaded(true)//是否在加载完成时滚动列表显示新的内容
            layout.setFooterTriggerRate(0.6f)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            //设置 Head
            CustomRefreshHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            //设置 Footer
            CustomRefreshFooter(context)
        }
        //注册界面状态管理
        LoadSir.beginBuilder()
            .addCallback(BaseErrorCallback())
            .addCallback(BaseEmptyCallback())
            .addCallback(BaseLoadingCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .commit()

    }
}

//初始化Utils
class InitUtils : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "3"
    }

    override fun run(name: String) {
        //初始化Log打印
        MMKV.initialize(appContext)
        //HookWebView.hookWebView()
    }
}

//初始化Utils
class InitToast : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "4"
    }

    override fun run(name: String) {
        //初始化吐司 这个吐司必须要主线程中初始化
//        ToastUtils.init(appContext)
//        ToastUtils.setGravity(Gravity.BOTTOM, 0, 100.dp)
        AppletPropertyManager.getAllApplet()
    }
}

//初始化下载、更新服务
class InitMarketTask : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "5"
    }

    override fun run(name: String) {
        TaskProxy.getInstance().init(appContext) {
            "InitMarketTask init $it".logE("lchen")
        }
        InstallAppManager.init()
    }
}

//初始化埋点
class InitDataProviderTask : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "6"
    }

    @SuppressLint("LogNotTimber")
    override fun run(name: String) {
//        val saConfigOptions = SAConfigOptions(NetUrl.sensorServer)
//        saConfigOptions.setAutoTrackEventType(
//            SensorsAnalyticsAutoTrackEventType.TYPE_NONE
//        ).enableLog(true)
//        AnalysisAPI.get().config(appContext, "ZeekrMarket", "app_market", false, saConfigOptions)
//        AnalysisAPI.get().init(appContext
//        ) { result, msg -> Log.e(PolicyApiManager.TAG, "SensorSDK init result ==> $result, msg ==> $msg") }
//        //作用于应用详情埋点：跳转来源
//        appContext.registerActivityLifecycleCallbacks(TrackActivityLifecycleCallbacks())
//        //埋点按需适配处理
//        CarManager.sensorAdapter()?.registerSuperProperties(appContext)
    }
}

class InitZeekrCommonAPI : Task(TASK_ID, false) {
    companion object {
        const val TAG = "InitZeekrCommonAPI"
        const val TASK_ID = "9"
        var mUserApiReady = false
        var mDeviceApiReady = false
        var mNaviApiReady = false
        var mPolicyApiReady = false
        var mMultiDisplayApiReady = false
    }

    @SuppressLint("LogNotTimber")
    override fun run(name: String) {
        UserApiManager.getInstance().userAPI.init(appContext) { p0, _ ->
            Log.e(TAG, "UserAPI init result ==> $p0")
            if (p0) {
                mUserApiReady = p0
                //监听账号的登录登出
                UserApiManager.getInstance().userAPI.registerCallback(UserLoginCallback())
            }
        }

        //车辆能力实例化
        VehicleApiManager.getInstance().init(appContext)

        //媒体中心初始化
        MediaCenterApiManager.getInstance().init(appContext)
        //车身控制的能力
        CarApiManager.getInstance().init(appContext)
        //AdaptApi实例化 (DC1E上初始化如果是子线程报错)
        CarApiProxy.getInstance(appContext)

        //导航初始化
        NaviApiManager.getInstance().naviAPI.init(appContext) { p0, _ ->
            Log.e(TAG, "NaviAPI init result ==> $p0")
            if (p0) {
                mNaviApiReady = p0
            }
        }

        PolicyApiManager.getInstance().init(appContext) { boolean, msg ->
            Log.e(TAG, "PolicyApi init result ==> $boolean, msg ==> $msg")
            if (boolean) {
                mPolicyApiReady = boolean
            }
        }

//        MultidisplayAPI.get().init(appContext,object : ApiReadyCallback {
//            override fun onAPIReady(p0: Boolean, p1: String?) {
//                Log.e(TAG, "MultidisplayAPI init result ==> $p0")
//            }
//        })
    }
}

class InitAdapterAPI : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "10"
    }

    override fun run(name: String) {

    }
}

//初始化注册STR(Suspend to RAM)模式监听
class InitStrModeRegister : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "11"
    }

    @SuppressLint("LogNotTimber")
    override fun run(name: String) {
        try {
            val powerStrModeState = "power_str_mode_state"
            appContext.contentResolver.registerContentObserver(Secure.getUriFor(powerStrModeState), false,
                object : ContentObserver(Handler(Looper.getMainLooper())) {
                    override fun onChange(selfChange: Boolean) {
                        super.onChange(selfChange)
                        val strMode: Int = Secure.getInt(appContext.contentResolver, powerStrModeState, 0)
                        App.eventViewModelInstance.strModeChangeEvent.postValue(strMode)
                        "STRModechange,strMode=$strMode".logE("StrModeRegister")
                    }
                }
            )
        } catch (e: SecurityException) {
            // 处理安全异常
            Log.e("TaskCreate", "SecurityException: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // 处理参数异常
            Log.e("TaskCreate", "IllegalArgumentException: ${e.message}")
        } catch (e: IllegalStateException) {
            // 处理状态异常
            Log.e("TaskCreate", "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e("TaskCreate", "Exception: ${Log.getStackTraceString(e)}")
        }
    }
}

class AppTaskFactory : Project.TaskFactory(TaskCreate)

/**
 * 模拟初始化SDK
 * @param millis Long
 */
fun doJob(millis: Long) {
    val nowTime = System.currentTimeMillis()
    while (System.currentTimeMillis() < nowTime + millis) {
        //程序阻塞指定时间
        val min = 10
        val max = 99
        val random = Random()
        val num = random.nextInt(max) % (max - min + 1) + min
    }
}
package com.zeekrlife.net.api

import android.content.Context
import com.zeekr.basic.appContext
import com.zeekr.car.tsp.TspAPI
import com.zeekr.car.util.CarLogUtils
import com.zeekrlife.net.BaseNetConstant
import com.zeekrlife.net.interception.LogInterceptor
import okhttp3.OkHttpClient
import rxhttp.wrapper.cookie.CookieStore
import rxhttp.wrapper.ssl.HttpsUtils
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 描述　:
 */
object NetHttpClient {

    private var headInterceptor: HeadInterceptor? = null

    private val devEnvParams = NetEnvParams(
        NetUrl.BASE_URL_DEV, "c932d0a10e9a44599623f71c726c36d9", "34137fae617911edae3a0c42a1e7eefa"
    )

    private val testEnvParams = NetEnvParams(
        NetUrl.BASE_URL_TESTING, "c932d0a10e9a44599623f71c726c36d9", "34137fae617911edae3a0c42a1e7eefa"
    )

    private val prodEnvParams = NetEnvParams(
        NetUrl.BASE_URL_PRODUCTION, "c932d0a10e9a44599623f71c726c36d9", "c1739e8ba9424a649abdffb2b7632976"
    )

    /**
     * 获取环境参数
     */
    fun getNetEnvParams(): NetEnvParams = TspAPI.create(appContext).envType?.run {
        if (isDevelopment) devEnvParams
        else if (isTestingEnv) testEnvParams
        else prodEnvParams
    } ?: prodEnvParams

    fun getDefaultOkHttpClient(context: Context, envParams: NetEnvParams): OkHttpClient.Builder {
        //在这里面可以写你想要的配置 太多了，我就简单的写了一点，具体可以看rxHttp的文档，有很多
        val sslParams = HttpsUtils.getSslSocketFactory()
        val mHeadInterceptor = HeadInterceptor(envParams.appId, envParams.appSecret)
        headInterceptor = mHeadInterceptor
        return OkHttpClient.Builder()
            //使用CookieStore对象磁盘缓存,自动管理cookie 玩安卓自动登录验证
            .cookieJar(CookieStore(File(context.externalCacheDir, "RxHttpCookie")))
            .connectTimeout(BaseNetConstant.CONNECT_TIME_OUT, TimeUnit.SECONDS)//读取连接超时时间 15秒
            .readTimeout(BaseNetConstant.READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(BaseNetConstant.WRITE_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(mHeadInterceptor)//自定义头部参数拦截器
            .addInterceptor(LogInterceptor())//添加Log拦截器
//            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
//            .hostnameVerifier { _, _ -> true } //忽略host验证
    }

    /**
     * 接入ZeeKrHttp：通过zeekrHttp初始化配置OkHttpClient
     * 请求配置（tsp加签、缓存、ssl）通过zeekrhttp，请求的发起、响应通过RxHttp；
     * 1、比较大的问题，不支持拦截器的设置
     */
//    fun getZeeKrHttpClient(context: Application, vararg interceptors: Interceptor = arrayOf()) = ZEHttp.config()
//        .setApplication(context)
//        .setBaseUrl(NetUrl.DEV_URL)// 可以通过RxHttp设置
//        .setAddInterceptor(*interceptors) //自定义请求拦截器
//        //.setHeaderParams(mutableMapOf() //添加公共header
//        //.setProjectName("SCENE_ENGINE")//场景引擎
//        .setCache(true) //是否使用缓存
//        .setDebug(true) //debug模式
//        .setTsp(true) //是否使用TSP加签
//        .setTspAppId("app_market_cloud") //APP ID 平台提供
//        .setTspAppSecret("U2FsdGVkX1+3UOIKTmVmI7MpMXwIpH5p+TahAE8YoQU=") //AppSecret 平台提供
//        .init().run { //初始化
//            ZEHttp.getOptions().okHttpClient ?: getDefaultOkHttpClient(context).build()
//        }

    /**
     * @param heads
     */
    fun addHttpHeadExt(heads: MutableMap<String, String>) {
        try {
            headInterceptor?.headsExt?.putAll(heads)
        } catch (e: Exception) {
            CarLogUtils.logStackTrace(e)
        }
    }
}
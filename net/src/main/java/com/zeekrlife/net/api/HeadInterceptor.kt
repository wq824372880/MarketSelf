package com.zeekrlife.net.api

import android.os.Build
import android.text.TextUtils
import android.util.Base64
import androidx.annotation.RequiresApi
import com.zeekr.basic.appContext
import com.zeekr.car.api.DeviceApiManager
import com.zeekr.car.tsp.TspAPI
import com.zeekrlife.net.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Locale
import java.util.Random

/**
 * Created by Qiang.Wang21 on 2022/6/24.
 */
class HeadInterceptor(
    private val APP_ID: String,
    private val APP_SECRET: String,
) : Interceptor {

    private val UTF_8 = Charset.forName("UTF-8")
//    private val APP_ID = EnvConstant.APP_ID
//    private val APP_SECRET = EnvConstant.BASE_SECRET

    val headsExt: MutableMap<String, String> by lazy { mutableMapOf() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val bodyString: String? = request.let {
            it.body
        }?.let {
            val buffer = Buffer()
            it.writeTo(buffer)
            buffer.clone().readString(UTF_8)
        }

        val query = request.url.query

        var r = request.run {
            val builder = this.newBuilder()

            sign(
                this.method,
                this.url.toUri().path,
                "application/json;responseformat=1",
                query,
                bodyString
            ).forEach {
                builder.addHeader(it.key, it.value)
            }
            builder.build()
        }
        val response = chain.proceed(r)

        return response
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun sign(
        methodParam: String,
        uriPathParam: String?,
        acceptParam: String,
        requestParameters: String?,
        rb: String?
    ): Map<String, String> {

        val appSecret = APP_SECRET
        val x_app_id: String = APP_ID
        val headerMap = mutableMapOf<String, String>()
        val x_env_type = if(TspAPI.create(appContext).envType.isProductionEnv) "production" else "testing"
        val method = methodParam
        var uriPath = if (uriPathParam == null) "" else subPath(uriPathParam, 0)
        val accept = acceptParam
        val x_timestamp = getTimes()//时间戳
        val x_api_signature_nonce = getX_Api_Signature_Nonce()//唯一随机数，防止网络重复攻击
        val x_api_signature_none_version = "1.0"//随机数版本
        var x_signature = ""//签名结果  val tspAppSecret = "test1VDG5"
        val requestBody = if (rb == null) "" else rb

        // 构建请求头
        val headerTreeMap = java.util.TreeMap<String, String>()
        //需要自行计算
        headerTreeMap["x-api-signature-nonce"] =
            x_api_signature_nonce //"F5A307DE-6B58-4634-84E1" //==nonce的长度不能超过36且不能小与8== ==建议使用"项目缩写+时间戳+随机数"作为nonce的值
        headerTreeMap["x-api-signature-version"] = x_api_signature_none_version//随机数版本

        val headersBuilder = StringBuilder()
        headerTreeMap.forEach { (k, v) ->
            headersBuilder.append(k.lowercase(Locale.getDefault())).append(':').append(v).append('\n')
        }
        val headersStr = headersBuilder.toString()
        println("请求头:$headersStr")

        // 计算Content-MD5的值

        var contentMD5 = requestBody.run {
            val input = requestBody.toByteArray(Charset.forName("utf-8"))
            var messageDigest: java.security.MessageDigest? = null
            messageDigest = java.security.MessageDigest.getInstance("MD5")
            messageDigest!!.update(input)
            val md5Bytes = messageDigest.digest()
            Base64.encodeToString(md5Bytes, Base64.NO_WRAP);
        }
//        log("TSP -> contentMD5 : $contentMD5")

        val stringBuilder = java.lang.StringBuilder()

        accept.run {
            stringBuilder.append(this)
        }
        stringBuilder.append('\n'.toString())
        headersStr.run {
            stringBuilder.append(this)
        }
        stringBuilder.append('\n'.toString())
        requestParameters?.run {
            stringBuilder.append(this)
        }

        stringBuilder.append('\n'.toString())
        contentMD5?.run {
            stringBuilder.append(this)
        }
        stringBuilder.append('\n'.toString())
        x_timestamp.run {
            stringBuilder.append(this)
        }

        stringBuilder.append('\n'.toString())
        method.run {
            stringBuilder.append(this)
        }

        stringBuilder.append('\n'.toString())
        uriPath.run {
            stringBuilder.append(this)
        }

//        log("TSP -> $stringBuilder")
        // 计算出signature
        val mac = javax.crypto.Mac.getInstance("HMACSHA1")
        mac.init(
            javax.crypto.spec.SecretKeySpec(
                appSecret.toByteArray(
                    Charset.forName(
                        "utf-8"
                    )
                ), "HMACSHA1"
            )
        )
        val signData =
            mac.doFinal(
                stringBuilder.toString().toByteArray(Charset.forName("utf-8"))
            )

        x_signature = Base64.encodeToString(signData, Base64.NO_WRAP)

        headerMap.put("x-api-signature-nonce", x_api_signature_nonce)
        headerMap.put("x-api-signature-version", x_api_signature_none_version)
        headerMap.put("x-env-type", x_env_type)
        headerMap.put("accept", accept)
        headerMap.put("x-timestamp", x_timestamp.toString())
        headerMap.put("x-app-id", x_app_id)
        headerMap.put("x-signature", x_signature)

        headerMap.put("x-device-id", DeviceApiManager.getInstance().deviceAPI?.ihuid ?: "")
        headerMap.put("x-vehicle-identifier", DeviceApiManager.getInstance().deviceAPI?.vin ?: "")
        headerMap.put("x-vehicle-brand", "ZEEKR")
        headerMap.put("x-vehicle-type", "CM2E")
        headerMap.put("x-vehicle-model", "512068_DHU7A_CM2E")
        headerMap.put("x-system-pn", DeviceApiManager.getInstance().deviceSystemPn)
        headerMap.put("X-ROM-MEMORY", DeviceApiManager.getInstance().deviceAccessMemory)


        //扩展请求头
        headsExt.forEach { (k, v) -> headerMap[k] = v }

//        log("TSP -> ${headerMap}")

        return headerMap
    }

    private fun getTimes(): Long {
        return System.currentTimeMillis()
    }

    private fun getX_Api_Signature_Nonce(): String {
        return "ZeekrMarket-${System.currentTimeMillis()}-${
            Random().nextInt(10000) + 99999
        }"
    }

//    private fun log(msg: String) {
//        Log.e("Request", msg)
//    }

    private fun subPath(path: String?, num: Int): String {
        if (path == null)
            return ""

        if (TextUtils.isEmpty(path) || num == 0)
            return path

        var str = ""
        var biasSum = 0
        for (c in path) {
            if ('/' == c) {
                biasSum++
            }

            if (biasSum >= num + 1)
                str += c
        }

        if (str.isEmpty())
            return path
        return str
    }

}
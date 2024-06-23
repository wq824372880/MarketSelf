package com.zeekrlife.market.sensors

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.JsonSyntaxException
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.request.GetAppsParams
import com.zeekrlife.market.data.response.AdvertisementInfoBean
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.net.api.ApiPagerResponse
import com.zeekrlife.net.api.ApiResponse
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.LogInterceptor
import com.zeekrlife.net.interception.logging.util.CharacterHandler.Companion.jsonFormat
import com.zeekrlife.net.interception.logging.util.UrlEncoderUtils.Companion.hasUrlEncoded
import com.zeekrlife.net.interception.logging.util.ZipHelper
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.Buffer
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.net.URLDecoder
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.util.*
import kotlin.jvm.Throws

/**
 * 用于埋点事件：应用有效曝光
 * 考虑到去页面做埋点有些复杂，怕影响正常业务，通过请求拦截器，按数据加载上报
 */
@SuppressLint("LogNotTimber")
class RequestInterceptor : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val originalResponse: Response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            // 处理输入输出异常
            Log.e("RequestInterceptor", "<-- HTTP FAILED: IOException - ${e.message}")
            throw e
        } catch (e: SocketTimeoutException) {
            // 处理超时异常
            Log.e("RequestInterceptor", "<-- HTTP FAILED: SocketTimeoutException - ${e.message}")
            throw e
        } catch (e: UnknownHostException) {
            // 处理未知主机异常
            Log.e("RequestInterceptor", "<-- HTTP FAILED: UnknownHostException - ${e.message}")
            throw e
        } catch (e: Exception) {
            // 处理其他未知异常
            Log.e("RequestInterceptor", "<-- HTTP FAILED: Exception - ${e.message}")
            throw e
        }

        if (originalResponse.isSuccessful) {
            //如果请求的是应用列表
            if (request.url.toString().contains(NetUrl.APP_LIST)) {
                asyncReporting(originalResponse) { bodyString ->
                    onReportApps(request, bodyString)
                }
            }
            //如果是精品推荐列表
            else if (request.url.toString().contains(NetUrl.APP_QUERY_ADVERTISEMNETS)) {
                asyncReporting(originalResponse) { bodyString ->
                    onReportRecommendApps(bodyString)
                }
            }
        }
        return originalResponse
    }

    /**
     * 上报APP列表
     * @param request 请求参数
     * @param responseJson 请求返回
     */
    private fun onReportApps(request: Request, responseJson: String?) {
        try {
            request.body?.apply {
                if (isJson(contentType())) {
                    val paramsStr = parseParams(request)
                    val params = GsonUtils.fromJson(paramsStr, GetAppsParams::class.java)

                    //根据请求条件判断应用曝光位置
                    var showPalace = ""
                    //分类列表页面
                    val categoryId = params.categoryPid ?: 0
                    //搜索页面
                    val searchKey = params.searchInfo ?: ""
                    if (categoryId > 0) {
                        showPalace = "应用分类"
                    } else if (searchKey.isNotEmpty()) {
                        showPalace = "搜索结果"
                    }

                    if (showPalace.isNotEmpty()) {
//                        "responseJson：$responseJson".logE()
                        val type: Type = ParameterizedTypeImpl[
                            ApiResponse::class.java, ParameterizedTypeImpl[ApiPagerResponse::class.java, AppItemInfoBean::class.java]
                        ]
                        val pager = GsonUtils.fromJson<ApiResponse<ApiPagerResponse<AppItemInfoBean>>>(responseJson, type)

                        if (!pager?.data?.list.isNullOrEmpty()) {
//                            if (searchKey.isNotEmpty()) {
//                                SensorsTrack.onAppSearchExposure(searchKey, pager?.data?.list!!)
//                            } else {
                                SensorsTrack.onAppExposure(showPalace, pager?.data?.list!!)
//                            }
                        }
                    }
                }
            }
        } catch (jsonSyntaxException: JsonSyntaxException) {
            // 处理 JSON 解析异常
            Log.e("RequestHandler", "JSON syntax exception: ${jsonSyntaxException.message}")
        } catch (e: IOException) {
            // 处理输入输出异常
            Log.e("RequestHandler", "IO exception: ${e.message}")
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 精品推荐请求
     * @param responseJson
     */
    private fun onReportRecommendApps(responseJson: String?) {
        try {
            val palace = "精品推荐"
            val type: Type = ParameterizedTypeImpl[
                ApiResponse::class.java, ParameterizedTypeImpl[List::class.java, AdvertisementInfoBean::class.java]
            ]
            val pager = GsonUtils.fromJson<ApiResponse<List<AdvertisementInfoBean>>>(responseJson, type)
            pager?.data?.forEach {
                var showPalace = palace
                when (it.pointCode) {
                    Constants.APPSTORE_RECOMMEND_BANNER -> {
                        showPalace = "${palace}banner"
                    }
                    Constants.APPSTORE_RECOMMEND_ADSENSE -> {
                        showPalace = "${palace}推荐图"
                    }
                    Constants.APPSTORE_RECOMMEND_APP_LIST -> {
                        showPalace = "${palace}应用位"
                    }
                }
                val appList = mutableListOf<AppItemInfoBean>()
                it.advertisementApiDTOS?.forEach { advDots ->
                    if (!advDots.mediaTypes.isNullOrEmpty()) {
                        advDots.mediaTypes[0].appVersionInfo?.apply {
                            appList.add(this)
                        }
                    }
                }
                SensorsTrack.onAppExposure(showPalace, appList)
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 上报
     * @param originalResponse
     * @param report
     */
    private fun asyncReporting(
        originalResponse: Response, report: (bodyString: String?) -> Unit
    ) {
        val responseBody = originalResponse.body
        var bodyString: String? = null
        if (responseBody != null && isParseable(responseBody.contentType())) {
            bodyString = getResResult(originalResponse)
        }
        if (!bodyString.isNullOrEmpty()) {
            MainScope().launch(Dispatchers.Default) {
                runCatching {
                    report(bodyString)
                }.onFailure {
                    "onFailure:${Log.getStackTraceString(it)}".logE()
                }
            }
        }
    }

    companion object {
        /**
         * 解析请求服务器的请求参数
         *
         * @param request [Request]
         * @return 解析后的请求信息
         * @throws UnsupportedEncodingException
         */
        @Throws(UnsupportedEncodingException::class)
        fun parseParams(request: Request): String {
            return try {
                val body = request.newBuilder().build().body ?: return ""
                val requestbuffer = Buffer()
                body.writeTo(requestbuffer)
                var charset = Charset.forName("UTF-8")
                val contentType = body.contentType()
                if (contentType != null) {
                    charset = contentType.charset(charset)
                }
                var json = requestbuffer.readString(charset)
                if (hasUrlEncoded(json)) {
                    json = URLDecoder.decode(
                        json,
                        convertCharset(charset)
                    )
                }
                jsonFormat(json)
            } catch (e: IOException) {
                e.logStackTrace()
                "{\"error\": \"" + e.message + "\"}"
            }
        }

        /**
         * 是否可以解析
         *
         * @param mediaType [MediaType]
         * @return `true` 为可以解析
         */
        fun isParseable(mediaType: MediaType?): Boolean {
            return if (mediaType?.type == null) {
                false
            } else isText(mediaType) || isPlain(
                mediaType
            )
                || isJson(mediaType) || isForm(
                mediaType
            )
                || isHtml(mediaType) || isXml(
                mediaType
            )
        }

        /**
         * 判断给定的MediaType是否为文本类型。
         *
         * @param mediaType MediaType对象，可能为null。该对象代表一种媒体类型，如文本、图片等。
         * @return Boolean值。如果mediaType不为null且类型为"text"，则返回true；否则返回false。
         */
        fun isText(mediaType: MediaType?): Boolean {
            return if (mediaType?.type == null) {
                false
            } else "text" == mediaType.type
        }

        /**
         * 判断给定的媒体类型是否为纯文本类型。
         *
         * @param mediaType MediaType对象，表示待检查的媒体类型。
         * @return Boolean值，如果媒体类型的子类型(subtype)包含"plain"（不区分大小写），则返回true；否则返回false。
         */
        fun isPlain(mediaType: MediaType?): Boolean {
            // 先判断媒体类型的子类型是否为空，若为空，则直接返回false
            return if (mediaType?.subtype == null) {
                false
            } else mediaType.subtype
                .lowercase(Locale.getDefault()).contains("plain")
        }

        /**
         * 判断给定的媒体类型是否为JSON类型。
         *
         * @param mediaType 待判断的媒体类型，是一个MediaType对象。
         * @return 返回一个布尔值，如果媒体类型是JSON，则返回true；否则返回false。
         */
        @JvmStatic
        fun isJson(mediaType: MediaType?): Boolean {
            // 判断媒体类型的子类型是否为null，或者是否包含"json"字符串（不区分大小写）
            return if (mediaType?.subtype == null) {
                false
            } else mediaType.subtype.toLowerCase(Locale.getDefault()).contains("json")
        }

        /**
         * 判断给定的媒体类型是否为XML类型。
         *
         * @param mediaType MediaType对象，表示待检查的媒体类型。
         * @return Boolean值，如果媒体类型的子类型包含"xml"（不区分大小写），则返回true；否则返回false。
         */
        @JvmStatic
        fun isXml(mediaType: MediaType?): Boolean {
            // 检查媒体类型的子类型是否为null，若为null则直接返回false
            return if (mediaType?.subtype == null) {
                false
            } else mediaType.subtype.lowercase(Locale.getDefault()).contains("xml")
        }

        /**
         * 判断给定的媒体类型是否为HTML。
         *
         * @param mediaType MediaType对象，表示待检查的媒体类型。
         * @return Boolean值，如果媒体类型的子类型包含"html"（不区分大小写），则返回true；否则返回false。
         */
        fun isHtml(mediaType: MediaType?): Boolean {
            // 检查媒体类型的子类型是否为null，若为null则直接返回false
            return if (mediaType?.subtype == null) {
                false
            } else mediaType.subtype.lowercase(Locale.getDefault()).contains("html")
        }

        /**
         * 检查给定的媒体类型是否为表单类型。
         *
         * @param mediaType MediaType? 类型，表示待检查的媒体类型，可以为 null。
         * @return Boolean 类型，如果媒体类型的子类型包含 "x-www-form-urlencoded"（不区分大小写），则返回 true；否则返回 false。
         */
        fun isForm(mediaType: MediaType?): Boolean {
            // 当媒体类型的子类型不存在时，直接返回 false
            return if (mediaType?.subtype == null) {
                false
            } else mediaType.subtype.lowercase(Locale.getDefault())
                .contains("x-www-form-urlencoded")
        }

        /**
         * 转换字符集的表示形式。
         *
         * @param charset 字符集对象，可能为null。
         * @return 如果输入的charset不包含"["和"]"，则直接返回其toString()结果；
         *         如果包含，则返回去掉"["和"]"之间的部分。
         */
        fun convertCharset(charset: Charset?): String {
            val s = charset.toString()
            val i = s.indexOf("[")
            return if (i == -1) {
                s
            } else s.substring(i + 1, s.length - 1)
        }

        /**
         * @param response    [Response]
         * @return 解析后的响应结果
         * @throws IOException
         */
        @Throws(IOException::class)
        private fun getResResult(
            response: Response,
        ): String? {
            return try {
                //读取服务器返回的结果
                val responseBody = response.newBuilder().build().body
                val source = responseBody!!.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer

                //获取content的压缩类型
                val encoding = response
                    .headers["Content-Encoding"]
                val clone = buffer.clone()

                //解析response content
                parseContent(responseBody, encoding, clone)
            } catch (e: IOException) {
                e.logStackTrace()
                "{\"error\": \"" + e.message + "\"}"
            }
        }

        /**
         * @param responseBody [ResponseBody]
         * @param encoding     编码类型
         * @param clone        克隆后的服务器响应内容
         * @return 解析后的响应结果
         */
        private fun parseContent(
            responseBody: ResponseBody?,
            encoding: String?,
            clone: Buffer
        ): String? {
            var charset = Charset.forName("UTF-8")
            val contentType = responseBody!!.contentType()
            if (contentType != null) {
                charset = contentType.charset(charset)
            }
            //content 使用 gzip 压缩
            return if ("gzip".equals(encoding, ignoreCase = true)) {
                //解压
                ZipHelper.decompressForGzip(
                    clone.readByteArray(),
                    LogInterceptor.convertCharset(charset)
                )
            } else if ("zlib".equals(encoding, ignoreCase = true)) {
                //content 使用 zlib 压缩
                ZipHelper.decompressToStringForZlib(
                    clone.readByteArray(),
                    LogInterceptor.convertCharset(charset)
                )
            } else {
                //content 没有被压缩, 或者使用其他未知压缩方式
                clone.readString(charset)
            }
        }
    }
}
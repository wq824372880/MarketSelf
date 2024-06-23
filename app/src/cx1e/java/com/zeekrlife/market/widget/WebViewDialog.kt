package com.zeekrlife.market.widget

import android.content.Context
import android.net.http.SslError
import android.view.View
import android.webkit.*
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebResourceErrorCompat
import com.zeekr.component.dialog.ZeekrDialogAction
import com.zeekr.component.dialog.ZeekrDialogCreate
import com.zeekr.component.dialog.button.WhichButton
import com.zeekr.component.dialog.common.DialogParam
import com.zeekr.component.dialog.custom.inflateDialogWebViewLayout
import com.zeekr.component.webview.ZeekrWebChromeClient
import com.zeekr.component.webview.ZeekrWebView
import com.zeekr.component.webview.ZeekrWebViewClient
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.util.NetworkUtils
import com.zeekrlife.market.R
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.repository.UserRepository
import com.zeekrlife.market.data.response.ProtocolInfoBean
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * @author mac
 * 公共组件库不同，分开写
 */
class WebViewDialog(val activity: Context, val viewLifecycleOwner: LifecycleOwner) {
    private var zeekrDialogCreate: ZeekrDialogCreate? = null
    private var webViewDialogAction: ZeekrDialogAction? = null
    private var zeekrWebView: ZeekrWebView? = null
    private var isWebViewLoadError = false

    private var mTitle: String? = null

    /**
     * 显示一个带有WebView的对话框。
     * 该函数首先会设置对话框的标题，然后关闭当前正在显示的WebView对话框（如果有的话），并创建一个新的WebView对话框。
     * 根据传入的标题不同，函数会加载不同的网页内容（用户协议或隐私政策）。
     * 用户点击取消按钮时，会停止WebView的加载，关闭对话框，并清空相关变量。
     *
     * @param title 对话框的标题，用于区分加载的不同内容。
     */
    fun show(title: String) {
        //HookWebView.hookWebView()
        mTitle = title
        webViewDialogAction?.dismiss()
        webViewDialogAction = null
        webViewDialogAction = ZeekrDialogCreate(activity).show {
            zeekrDialogCreate = this
            title(title)
            mediumSize()
            dialogParam(
                DialogParam(
                    isDismissOnBackPressed = false,
                    isDismissOnTouchOutside = false,
                )
            )
            lifecycleOwner(viewLifecycleOwner)
            mergeLayout {
                zeekrWebView = it.inflateDialogWebViewLayout().apply {
                    isWebViewLoadError = false
                    setError(
                        activity.getString(R.string.helper_loading_error_tip),
                        R.drawable.load_error
                    )
                    errorView.setOnClickListener(null)
                    statusText.setOnClickListener(null)
                    when (title) {
                        getStringExt(R.string.launcher_dialog_title_ua) -> {
                            getUserAgreement(
                                callbackSuccess = { info ->
                                    agentWebSubmit(this, info.h5Url ?: "")
                                },
                                callbackFail = {
                                    agentWebSubmit(this, "")
                                })
                        }

                        else -> {
                            getProtocol(
                                callbackSuccess = { info ->
                                    agentWebSubmit(this, info.h5Url ?: "")
                                },
                                callbackFail = {
                                    agentWebSubmit(this, "")
                                })
                        }
                    }


                }
            }

            // 设置取消按钮及其点击事件
            realButton(text = getStringExt(R.string.common_cancel)) { action ->
                zeekrWebView?.stopLoading()
                action.dismiss()
                zeekrWebView = null
                webViewDialogAction = null
            }
        }

    }


    /**
     * 获取用户协议的内容。
     *
     * @param callbackSuccess 当获取协议内容成功时调用的回调函数，接收一个 [ProtocolInfoBean] 类型的参数。
     * @param callbackFail 当获取协议内容失败时调用的回调函数，接收一个 [String] 类型的错误信息参数。
     */
    fun getUserAgreement(
        callbackSuccess: ((str: ProtocolInfoBean) -> Unit) = {},
        callbackFail: ((str: String) -> Unit) = {}
    ) {

        // 尝试从缓存中获取用户协议，如果缓存存在且URL不为空，则直接使用缓存中的数据调用成功回调
        CacheExt.getUserAgreement().takeIf {
            it?.h5Url?.isNotEmpty() == true
        }?.run {
            callbackSuccess.invoke(this)
        } ?: kotlin.run {
            // 如果缓存中没有用户协议，则异步从服务器获取
            MainScope().launch {
                // 尝试从服务器获取协议信息
                kotlin.runCatching {
                    UserRepository.getProtocolInfo(Constants.APPSTOREUA_BX1E).await()
                }.onSuccess {
                    // 获取成功，将协议信息存入缓存并调用成功回调
                    CacheExt.setUserAgreement(it)
                    callbackSuccess.invoke(it)
                }.onFailure {
                    // 获取失败，记录错误信息并调用失败回调
                    "WebViewDialog getUserAgreement()：${it}".logE("zzz")
                    callbackFail.invoke(it.msg)
                }
            }
        }

    }


    /**
     * 获取协议信息的函数。
     *
     * @param callbackSuccess 成功时的回调函数，接收一个 ProtocolInfoBean 类型的参数。
     * @param callbackFail 失败时的回调函数，接收一个 String 类型的错误信息参数。
     */
    private fun getProtocol(
        callbackSuccess: ((str: ProtocolInfoBean) -> Unit) = {},
        callbackFail: ((str: String) -> Unit) = {}
    ) {

        // 尝试从缓存中获取协议信息，如果存在且h5Url不为空，则调用成功回调
        CacheExt.getProtocol().takeIf {
            it?.h5Url?.isNotEmpty() == true
        }?.run {
            callbackSuccess.invoke(this)
        } ?: kotlin.run {
            // 如果缓存中没有协议信息，则异步从服务器获取
            MainScope().launch {
                kotlin.runCatching {
                    // 尝试从服务器获取协议信息
                    UserRepository.getProtocolInfo(Constants.APPSTOREPP_BX1E).await()
                }.onSuccess {
                    // 获取成功，更新缓存并调用成功回调
                    CacheExt.setUserAgreement(it)
                    callbackSuccess.invoke(it)
                }.onFailure {
                    // 获取失败，记录错误信息并调用失败回调
                    "WebViewDialog getUserAgreement()：${it}".logE("zzz")
                    callbackFail.invoke(it.msg)
                }
            }
        }

    }

    /**
     * 使用AgentWeb提交网页表单。
     * 该函数首先检查传入的URL是否为空以及网络是否连接，如果URL为空或网络不可用，则标记WebView加载错误。
     * 接着获取协议URL并打印日志。
     * 最后，配置并加载WebView，使其能够显示指定的协议URL。
     *
     * @param zeekrWebView WebView控件，用于加载网页。
     * @param url 需要提交的URL字符串。
     */
    private fun agentWebSubmit(zeekrWebView: ZeekrWebView?, url: String) {
        if (url.isEmpty() || !NetworkUtils.isConnected()) {
            // 标记WebView加载错误
            isWebViewLoadError = true
        }
        // 获取协议URL
        val agreementUrl = getAgreementUrl(url)

        // 打印日志，显示协议URL
        "WebViewDialog agreementUrl：${agreementUrl}".logE()

        zeekrWebView?.apply {
            webViewClient = AgentWebViewClient()
            webChromeClient = AgentWebChromeClient()
//            val webSettings: WebSettings = settings
//            webSettings.textZoom = 100
//            webSettings.defaultTextEncodingName = "UTF-8"
//            webSettings.setSupportZoom(false)
//            webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
//            webSettings.allowFileAccess = true
//            webSettings.domStorageEnabled = true
//            webSettings.useWideViewPort = true
//            webSettings.loadWithOverviewMode = true
//            // 判断系统版本是不是5.0或之上 让系统不屏蔽混合内容和第三方Cookie
//            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
//            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//            webSettings.javaScriptEnabled = true
//            webSettings.allowUniversalAccessFromFileURLs = true
            loadUrl(agreementUrl)
        }
    }

    /**
     * 一个内部类，继承自ZeekrWebViewClient，用于自定义WebView的客户端行为。
     */
    inner class AgentWebViewClient : ZeekrWebViewClient() {
        /**
         * 当WebView接收到来自SSL错误时调用此方法。
         * @param view WebView发生错误的实例。
         * @param handler 用于处理SSL错误的处理器。
         * @param error 表示发生的SSL错误的详细信息。
         */
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            "onReceivedSslError:$error".logE("zzz")
            handler?.cancel()
        }

        /**
         * 当WebView在加载资源时发生错误时调用此方法。
         * @param view 发生错误的WebView。
         * @param request 请求的Web资源信息。
         * @param error 表示发生的Web资源错误的详细信息。
         */
        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceErrorCompat
        ) {
            super.onReceivedError(view, request, error)
            "onReceivedError:$error".logE("zzz")
            isWebViewLoadError = true
        }
    }

    /**
     * 根据当前的夜间模式和屏幕宽度获取协议链接。
     * @param url 原始协议链接。
     * @return 根据当前设置修改后的协议链接。
     */
    private fun getAgreementUrl(url: String): String {
        return if (getUINightMode()) {
            if (getScreenWidthIs3200()) {
                "$url?mode=night&res=3200*2000"
            } else if (getScreenHeightIs1440()) {
                "$url?mode=night&res=2560*1440"
            } else {
                "$url?mode=night&res=2560*1600"
            }
        } else {
            if (getScreenWidthIs3200()) {
                "$url?mode=day&res=3200*2000"
            } else if (getScreenHeightIs1440()) {
                "$url?mode=day&res=2560*1440"
            } else {
                "$url?mode=day&res=2560*1600"
            }
        }
    }

    /**
     * 一个继承自ZeekrWebChromeClient的内部类，用于处理WebView进度改变的事件。
     */
    inner class AgentWebChromeClient : ZeekrWebChromeClient() {
        /**
         * 当WebView的进度发生变化时调用此方法。
         * @param view 指定的WebView。
         * @param newProgress 新的进度值，范围在0到100之间。
         */
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // 当WebView加载完成（进度为100）时执行的逻辑
            if (newProgress == 100) {
                // 如果之前WebView加载出错，则显示刷新按钮
                if (isWebViewLoadError) {
                    webViewDialogAction?.apply {
                        // 设置确认（积极）按钮可见，并设置刷新文本和点击事件
                        getActionButton(WhichButton.POSITIVE).apply {
                            visible()
                            text = getStringExt(R.string.common_refresh)
                            setOnClickListener {
                                isWebViewLoadError = false
                                zeekrWebView?.setLoadingState()

                                // 隐藏消极按钮，设置积极按钮为取消文本和点击事件
                                getActionButton(WhichButton.NEGATIVE).visibility = View.GONE
                                getActionButton(WhichButton.POSITIVE).let { cancelButton ->
                                    cancelButton.text = getStringExt(R.string.common_cancel)
                                    cancelButton.setOnClickListener {
                                        zeekrWebView?.stopLoading()
                                        webViewDialogAction?.dismiss()
                                        zeekrWebView = null
                                        webViewDialogAction = null
                                    }
                                }

                                // 根据不同的标题，执行不同的加载逻辑
                                when (mTitle) {
                                    getStringExt(R.string.launcher_dialog_title_ua) -> {
                                        getUserAgreement(
                                            callbackSuccess = { info ->
                                                agentWebSubmit(zeekrWebView, info.h5Url ?: "")
                                            },
                                            callbackFail = {
                                                agentWebSubmit(zeekrWebView, "")
                                            })
                                    }

                                    else -> {
                                        getProtocol(
                                            callbackSuccess = { info ->
                                                agentWebSubmit(zeekrWebView, info.h5Url ?: "")
                                            },
                                            callbackFail = {
                                                agentWebSubmit(zeekrWebView, "")
                                            })
                                    }
                                }
                            }

                        }
                        getActionButton(WhichButton.NEGATIVE).apply {
                            visible()
                            zeekrDialogCreate?.layoutButtons()
                            text = getStringExt(R.string.common_cancel)
                            setOnClickListener {
                                zeekrWebView?.stopLoading()
                                dismiss()
                                webViewDialogAction?.dismiss()
                                webViewDialogAction = null
                                zeekrWebView = null
                            }
                        }
                    }
                } else {
                    webViewDialogAction?.apply {
                        webViewDialogAction?.getActionButton(WhichButton.POSITIVE)?.apply {
                            text = getStringExt(R.string.common_see)
                            setOnClickListener {
                                zeekrWebView?.stopLoading()
                                dismiss()
                                webViewDialogAction?.dismiss()
                                zeekrWebView = null
                                webViewDialogAction = null
                            }
                        }
                    }
                }
            }
        }
    }

}




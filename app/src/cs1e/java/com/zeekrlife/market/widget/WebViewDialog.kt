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
     * 显示WebView弹窗
     * 显示WebView弹窗
     * 显示WebView弹窗
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
            realButton(text = getStringExt(R.string.common_cancel)) { action ->
                zeekrWebView?.stopLoading()
                action.dismiss()
                zeekrWebView = null
                webViewDialogAction = null
            }
        }

    }


    fun getUserAgreement(
        callbackSuccess: ((str: ProtocolInfoBean) -> Unit) = {},
        callbackFail: ((str: String) -> Unit) = {}
    ) {

        CacheExt.getUserAgreement().takeIf {
            it?.h5Url?.isNotEmpty() == true
        }?.run {
            callbackSuccess.invoke(this)
        } ?: kotlin.run {
            MainScope().launch {
                kotlin.runCatching {
                    UserRepository.getProtocolInfo(Constants.APPSTOREUA_BX1E).await()
                }.onSuccess {
                    CacheExt.setUserAgreement(it)
                    callbackSuccess.invoke(it)
                }.onFailure {
                    "WebViewDialog getUserAgreement()：${it}".logE("zzz")
                    callbackFail.invoke(it.msg)
                }
            }
        }

    }


    private fun getProtocol(
        callbackSuccess: ((str: ProtocolInfoBean) -> Unit) = {},
        callbackFail: ((str: String) -> Unit) = {}
    ) {

        CacheExt.getProtocol().takeIf {
            it?.h5Url?.isNotEmpty() == true
        }?.run {
            callbackSuccess.invoke(this)
        } ?: kotlin.run {
            MainScope().launch {
                kotlin.runCatching {
                    UserRepository.getProtocolInfo(Constants.APPSTOREPP_BX1E).await()
                }.onSuccess {
                    CacheExt.setUserAgreement(it)
                    callbackSuccess.invoke(it)
                }.onFailure {
                    "WebViewDialog getUserAgreement()：${it}".logE("zzz")
                    callbackFail.invoke(it.msg)
                }
            }
        }

    }

    private fun agentWebSubmit(zeekrWebView: ZeekrWebView?, url: String) {
        if (url.isEmpty() || !NetworkUtils.isConnected()) {
            isWebViewLoadError = true
        }
        val agreementUrl = getAgreementUrl(url)
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

    inner class AgentWebViewClient : ZeekrWebViewClient() {
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            "onReceivedSslError:$error".logE("zzz")
            handler?.cancel()
        }

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

    private fun getAgreementUrl(url: String): String {
        return if (getUINightMode()) {
            if(getScreenWidthIs3200()){
                "$url?mode=night&res=3200*2000"
            }else{
                "$url?mode=night&res=2560*1600"
            }

        } else {
            if(getScreenWidthIs3200()){
                "$url?mode=day&res=3200*2000"
            }else{
                "$url?mode=day&res=2560*1600"
            }
        }
    }


    inner class AgentWebChromeClient : ZeekrWebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                if (isWebViewLoadError) {
                    webViewDialogAction?.apply {
                        getActionButton(WhichButton.POSITIVE).apply {
                            visible()
                            text = getStringExt(R.string.common_refresh)
                            setOnClickListener {
                                isWebViewLoadError = false
                                zeekrWebView?.setLoadingState()

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




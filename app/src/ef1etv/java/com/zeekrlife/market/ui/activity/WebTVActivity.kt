package com.zeekrlife.market.ui.activity

import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.finishCurrentActivity
import com.zeekrlife.common.ext.getScreenWidthIs2560
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.UtilsBridge
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.databinding.ActivityWebTvBinding
import com.zeekrlife.market.widget.initBack

class WebTVActivity : BaseActivity<BaseViewModel, ActivityWebTvBinding>() {

    private var mUrl: String? = null
    private var mUrlKeyTitle: String = ""
    private var isWebError = false

    /**
     * 初始化视图。
     * 该函数首先从Intent中提取URL和标题，配置工具栏的返回按钮，设置网页错误时的重载功能，
     * 清理WebView的缓存，并配置WebView的各种设置，最后加载URL。
     *
     * @param savedInstanceState 如果Activity被系统重新创建，这参数包含之前Activity结束时的状态。否则是null。
     */
    override fun initView(savedInstanceState: Bundle?) {
        // 从Intent中获取URL和默认标题
        mUrl = intent.getStringExtra(Constants.URL_KEY)
        mUrlKeyTitle = intent.getStringExtra(Constants.URL_KEY_TITLE) ?: "网页加载中..."
        // 配置工具栏的返回按钮，处理返回逻辑
        mBind.toolBarTv.initBack(titleLeftStr = mUrlKeyTitle) {
            //解决已经在首页 canGoBack 依然返回true的情况
            if (mBind.agentWebView.url == mUrl) {
                finishCurrentActivity(this)
                return@initBack
            }

            // 处理WebView的返回操作
            if (mBind.agentWebView.canGoBack()) {
                mBind.agentWebView.goBack()
            } else {
                // 如果WebView不能返回，则结束当前Activity
                finishCurrentActivity(this)
            }
        }
        // 设置错误页面的重载功能
        mBind.webError.stateErrorTextview.text = "加载失败"
        mBind.webError.stateErrorText.setOnClickListener {
            mBind.agentWebView.reload()

            // 隐藏错误页面，显示加载页面
            mBind.webError.root.visibility = View.GONE
            mBind.webLoading.root.visibility = View.VISIBLE

            // 重置错误标志
            isWebError = false
        }

        //清空缓存
        AgentWebConfig.clearDiskCache(this)
        // 配置WebView的详细设置
        mBind.agentWebView.apply {
            val webSettings: WebSettings = settings
            // 设置字体缩放和默认编码
            webSettings.textZoom = if (getScreenWidthIs2560()) 160 else 135
            webSettings.defaultTextEncodingName = "UTF-8"
            // 关闭支持缩放，设置缓存模式，允许文件访问，开启DOM存储，使用宽视图端口，和整体模式
            webSettings.setSupportZoom(false)
            webSettings.cacheMode = WebSettings.LOAD_NO_CACHE;
            webSettings.allowFileAccess = true
            webSettings.domStorageEnabled = true
            webSettings.useWideViewPort = true
            webSettings.loadWithOverviewMode = true
            // 判断系统版本是不是5.0或之上 让系统不屏蔽混合内容和第三方Cookie
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webSettings.domStorageEnabled = true
            webSettings.javaScriptEnabled = true

            // 设置WebView客户端和WebChrome客户端
            webViewClient = mWebViewClient
            webChromeClient = mWebChromeClient

            // 尝试根据系统版本设置强制黑暗模式
            try {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    if (UtilsBridge.isModelNight(this@WebTVActivity.application)) {
                        WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                    } else {
                        WebSettingsCompat.setForceDark(
                            webSettings,
                            WebSettingsCompat.FORCE_DARK_OFF
                        )
                    }
                }
            } catch (e: NoSuchMethodError) {
                // 处理方法不存在异常
                Log.e("WebActivity", "NoSuchMethodError: ${e.message}")
            } catch (e: NoSuchFieldError) {
                // 处理字段不存在异常
                Log.e("WebActivity", "NoSuchFieldError: ${e.message}")
            } catch (e: SecurityException) {
                // 处理安全异常
                Log.e("WebActivity", "SecurityException: ${e.message}")
            } catch (e: UnsupportedOperationException) {
                // 处理不支持操作异常
                Log.e("WebActivity", "UnsupportedOperationException: ${e.message}")
            } catch (e: Exception) {
                // 处理其他未知异常
                Log.e("WebActivity", "Exception: ${e.message}")
            }catch (e: Exception) {
                e.logStackTrace()
            }

            // 加载经过校验的URL
            loadUrl(checkUrlValid(mUrl))
        }
    }

    /**
     * 检查URL是否有效。
     *
     * @param url 待检查的URL字符串，可以为null。
     * @return 如果url不为null，则返回原url字符串；如果url为null，则返回空字符串。
     */
    private fun checkUrlValid(url: String?): String {
        // 如果url不为空，则直接返回url，否则返回空字符串
        return url ?: ""
    }

    /**
     * 创建一个继承自WebChromeClient的实例，用于处理WebView的标题和进度变化。
     */
    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        /**
         * 当WebView接收到页面标题时调用。
         * @param view 页面的WebView。
         * @param title 页面的标题。
         */
        override fun onReceivedTitle(view: WebView, title: String?) {
            super.onReceivedTitle(view, title)
            // 如果当前加载的页面不是"网页加载中"的页面，则不进行任何操作
            if (!mUrlKeyTitle.contains("网页加载中")) {
                return
            }
            // 根据标题的长度，设置工具栏标题，如果标题过长，则截取前10个字符并添加"..."
            if ((title?.length ?: 0) > 10) {
                mBind.toolBarTv.setCenterTitle(title?.substring(0, 10) + "...")
            } else {
                mBind.toolBarTv.setCenterTitle(title ?: "")
            }
        }

        /**
         * 当WebView的加载进度发生变化时调用。
         * @param view 页面的WebView。
         * @param newProgress 当前的加载进度（百分比）。
         */
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // 如果已经检测到网页加载错误，则不进行任何操作
            if (isWebError) {
                return
            }
            // 当页面加载完成（进度100%）时，隐藏加载动画，显示网页；否则显示加载动画，隐藏网页内容
            if (newProgress == 100) {
                mBind.webLoading.root.visibility = View.GONE
                view?.visibility = View.VISIBLE
            } else {
                mBind.webLoading.root.visibility = View.VISIBLE
                view?.visibility = View.GONE
            }
        }
    }


    /**
     * 自定义的 WebViewClient，用于处理 WebView 相关的事件。
     */
    private val mWebViewClient = object : WebViewClient() {
        /**
         * 当 WebView 加载页面时发生错误时调用。
         * @param view 产生错误的 WebView。
         * @param request 请求的 Web 资源。
         * @param error 产生的 Web 资源错误。
         */
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            isWebError = true
            mBind.webError.root.visibility = View.VISIBLE
            mBind.webLoading.root.visibility = View.GONE
            view?.visibility = View.GONE
        }

        /**
         * 当接收到来自 SSL 证书的错误时调用。
         * @param view 产生错误的 WebView。
         * @param handler 处理 SSL 错误的处理器。
         * @param error 产生的 SSL 错误。
         */
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
            isWebError = true
            mBind.webError.root.visibility = View.VISIBLE
            mBind.webLoading.root.visibility = View.GONE
            view?.visibility = View.GONE
        }

        /**
         * 决定是否应该拦截并处理 WebView 加载的 URL。
         * @param view 与请求相关的 WebView。
         * @param request 请求的 Web 资源请求信息。
         * @return 如果返回 true，则表示该 URL 加载事件被拦截并处理；否则，允许 WebView 自行处理。
         */
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.e("WebActivity", "requestUrl : ${request?.url}")
            Handler(Looper.getMainLooper()).post {
                ToastUtils.show(getString(R.string.web_view_policy_should_override_url))
            }
            return true
        }

        /**
         * 旧版的 shouldOverrideUrlLoading 方法，用于兼容低版本 Android。
         * @param view 与请求相关的 WebView。
         * @param url 请求加载的 URL 字符串。
         * @return 如果返回 true，则表示该 URL 加载事件被拦截并处理；否则，允许 WebView 自行处理。
         */
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.e("WebActivity", "requestUrl : $url")
            Handler(Looper.getMainLooper()).post {
                ToastUtils.show(getString(R.string.web_view_policy_should_override_url))
            }
            return true
        }
    }


    /**
     * 显示工具栏的方法。
     * 这是一个覆盖方法，用于控制是否显示工具栏。
     *
     * @return 返回一个布尔值，指示是否显示工具栏。在这个实现中，总是返回false，意味着工具栏不显示。
     */
    override fun showToolBar(): Boolean {
        return false
    }
}
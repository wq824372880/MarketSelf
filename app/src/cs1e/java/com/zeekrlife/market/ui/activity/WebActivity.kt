package com.zeekrlife.market.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWebConfig
import com.zeekr.component.webview.ZeekrWebChromeClient
import com.zeekr.component.webview.ZeekrWebViewClient
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.finishCurrentActivity
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.UtilsBridge
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.ext.initBack
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.databinding.ActivityWebBinding

class WebActivity : BaseActivity<BaseViewModel, ActivityWebBinding>() {

    private var mUrl: String? = null
    private var mUrlKeyTitle: String = ""
    private var mWebChromeClient:ZeekrWebChromeClient?= null
    private var mWebViewClient:ZeekrWebViewClient?= null



    @SuppressLint("LogNotTimber")
    override fun initView(savedInstanceState: Bundle?) {
        mUrl = intent.getStringExtra(Constants.URL_KEY)
        mUrlKeyTitle = intent.getStringExtra(Constants.URL_KEY_TITLE) ?: "网页加载中..."
        mToolbar?.initBack(titleLeftStr = mUrlKeyTitle) {
            //解决已经在首页 canGoBack 依然返回true的情况
            if (mBind.agentWebView.url == mUrl) {
                finishCurrentActivity(this)
                return@initBack
            }

            if (mBind.agentWebView.canGoBack()) {
                mBind.agentWebView.goBack()
            } else {
                finishCurrentActivity(this)
            }
        }

        /**
         * WebViewClient shouldOverrideUrlLoading函数
         */
         mWebViewClient = object : ZeekrWebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                Log.e("WebActivity", "requestUrl : ${request?.url}")
                Handler(Looper.getMainLooper()).post {
                    ToastUtils.show(getString(R.string.web_view_policy_should_override_url))
                }
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.e("WebActivity", "requestUrl : $url")
                Handler(Looper.getMainLooper()).post {
                    ToastUtils.show(getString(R.string.web_view_policy_should_override_url))
                }
                return true
            }
        }

        mWebChromeClient = object : ZeekrWebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String?) {
                super.onReceivedTitle(view, title)
                if (!mUrlKeyTitle.contains("网页加载中")) {
                    return
                }
                if ((title?.length ?: 0) > 10) {
                    mToolbar?.setCenterTitle(title?.substring(0, 10) + "...")
                } else {
                    mToolbar?.setCenterTitle(title ?: "")
                }
            }
        }

        //清空缓存
        AgentWebConfig.clearDiskCache(this)
        mBind.agentWebView.apply {
            val webSettings: WebSettings = settings
            webSettings.defaultTextEncodingName = "UTF-8"
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


            webViewClient = mWebViewClient as ZeekrWebViewClient
            webChromeClient = mWebChromeClient as ZeekrWebChromeClient

            try {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    if (UtilsBridge.isModelNight(this@WebActivity.application)) {
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

            loadUrl(mUrl!!)
        }
    }





    /**
     * 是否显示initImmersionBar
     */
    override fun initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar?.navigationBarColor(R.color.theme_main_background_color)?.init()
        //设置共同沉浸式样式
        if (showToolBar()) {
//            ImmersionBar.with(this).fitsSystemWindows(true).titleBar(mToolbar).init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mWebViewClient = null
        mWebChromeClient = null
        mBind?.agentWebView?.destroy()
        CookieManager.getInstance().removeAllCookie()

    }
}
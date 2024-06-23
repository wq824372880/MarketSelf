package com.zeekrlife.market.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.zeekr.sdk.user.bean.UserInfoBean
import com.zeekr.sdk.user.callback.ILoginCallback
import com.zeekr.sdk.user.impl.UserAPI
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.databinding.ActivityLoginBinding
import com.zeekrlife.market.ui.viewmodel.LoginViewModel
import com.zeekrlife.net.interception.logging.util.logD

/**
 * 描述　:
 */
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    /**
     * 登录回调
     */
    private var loginListener: ILoginCallback = object : ILoginCallback {
        override fun onLogin() {
            handleLogin()
        }

        override fun onLogout() {
            handleLogout()
        }

        override fun onTokenChanged(p0: String?) {
        }

        override fun onUserInfoChanged(p0: UserInfoBean?) {
        }

        override fun onAccountSwitch(p0: String?, p1: String?) {

        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        UserAPI.get().registerCallback(loginListener)
        mViewModel.launchToLogin()
    }

    /**
     * 监听Intent
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        realPath = intent?.getStringExtra(REAL_PATH)
        if (intent != null) {
            val rawUriStr = intent.getStringExtra(RAW_URI)
            if (!TextUtils.isEmpty(rawUriStr)) {
                val rawUri = Uri.parse(rawUriStr)
                if (rawUri != null) {
                    val queryParameterNameSet = rawUri.queryParameterNames
                    if (queryParameterNameSet != null) {
                        if (queryParameterNameSet.contains(REAL_PATH)) {
                            realPath = rawUri.getQueryParameter(REAL_PATH)
                        }
                    }
                }
            }
        }

    }

    /**
     * 处理登录成功
     */
    private fun handleLogin() {
        "LoginAcitiviy收到登录成功".logD()
        when (realPath) {
            EntryActivity.ACT_MAIN -> toStartActivity(HomeActivity::class.java)
            EntryActivity.ACT_APPS -> toStartActivity(AppDetailActivity::class.java)
            EntryActivity.ACT_SEARCH -> toStartActivity(SearchActivity::class.java)
            EntryActivity.ACT_MANAGE_HOME -> toStartActivity(HomeActivity::class.java)
            else -> toStartActivity(HomeActivity::class.java)
        }
        finish()

    }

    /**
     * 处理登录失败
     */
    private fun handleLogout() {
        "LoginAcitiviy收到登录失败".logD()
        toStartActivity(HomeActivity::class.java)
        finish()

    }

    /**
     * 显示标题栏
     */
    override fun showToolBar(): Boolean {
        return false
    }
}
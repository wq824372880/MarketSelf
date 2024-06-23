package com.zeekrlife.market.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.gyf.immersionbar.ktx.immersionBar
import com.zeekr.car.api.DeviceApiManager
import com.zeekr.car.api.UserApiManager
import com.zeekrlife.common.ext.*
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.data.ValueKey
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.OpenApiDeviceInfo
import com.zeekrlife.market.data.response.OpenApiUserInfo
import com.zeekrlife.market.data.response.ProtocolInfoBean
import com.zeekrlife.market.databinding.ActivityTvLauncherBinding
import com.zeekrlife.market.ui.viewmodel.LauncherViewModel
import com.zeekrlife.market.widget.WebViewDialog
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 启动页
 */
class LauncherTVActivity : BaseActivity<LauncherViewModel, ActivityTvLauncherBinding>() {
    var userApiInfo: OpenApiUserInfo? = null
    var deviceApiInfo: OpenApiDeviceInfo? = null
    var userAgreement: ProtocolInfoBean? = null //用户协议
    var protocolInfo: ProtocolInfoBean? = null //隐私信息
    private var mBundle = Bundle()

    override fun onCreateDispatchEvent(): Boolean {
        return false
    }

    /**
     * 初始化视图的函数。
     * 该函数首先调用超类的initView函数，然后获取用户和设备的API信息，并请求焦点到tvConfirm视图。
     *
     * @param savedInstanceState 如果Activity被系统重新创建，这个参数包含了之前Activity结束时的状态。否则是null。
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState) // 调用父类的initView函数
        // 获取用户和设备的API信息
        mViewModel.getOpenApiInfo(
            UserApiManager.getInstance().userAPI,
            DeviceApiManager.getInstance().deviceAPI
        )
        // 请求焦点到tvConfirm视图
        mBind.tvConfirm.requestFocus()
    }

    /**
     * 当Activity重新回到前台时调用的函数。
     * 该函数没有参数，也没有返回值。
     * 主要用于设置导航栏的颜色。
     */
    override fun onResume() {
        super.onResume()
        // 设置导航栏颜色为launcher_bg_color
        immersionBar {
            navigationBarColor(R.color.launcher_bg_color)
        }
    }

    /**
     * 初始化观察者模式，用于监听数据的变化并作出相应处理。
     * 此函数没有参数和返回值。
     */
    override fun initObserver() {
        // 观察openApiInfo的变化，当发生变化时，更新用户和设备的API信息，并根据协议状态进行相应处理
        mViewModel.mOpenApiInfo.observe(this) {
            "openapiinfo****$it****\n${NetUrl.BASE_URL}".logE("openapiinfo")
            userApiInfo = it.userInfo
            deviceApiInfo = it.deviceInfo
            mBundle.apply {
                putString(EntryActivity.EXTRA_KEY_USERID, userApiInfo?.userId)
            }
            // 如果用户未同意协议，则获取协议信息并更新内容属性
            when {
                !CacheExt.isAgreementProtocol() -> { //未同意协议
                    mViewModel.getProtocolInfo()
                    contentAttr(true)
                }
            }
        }
        // 观察远程协议信息的变化，当发生变化时，更新用户协议信息，并在用户ID和协议信息都有效时，将其保存到Bundle中
        mViewModel.mRemoteProtocolInfo.observe(this) { it ->
            it?.apply {
                userAgreement = this
                if (userApiInfo?.userId != null && userAgreement != null) {
                    mBundle.apply {
                        putString(EntryActivity.EXTRA_KEY_USERID, userApiInfo?.userId)
                        putParcelable(
                            ValueKey.USER_AGREEMENT_INFO,
                            userAgreement ?: ProtocolInfoBean()
                        )
                    }
                }
            }
        }
        // 观察远程协议策略的变化，当发生变化时，更新协议策略信息，并在用户ID和协议策略信息都有效时，将其保存到Bundle中
        mViewModel.mRemoteProtocolPolicy.observe(this) {
            it?.apply {
                protocolInfo = this
                if (userApiInfo?.userId != null && protocolInfo != null) {
                    mBundle.apply {
                        putString(EntryActivity.EXTRA_KEY_USERID, userApiInfo?.userId)
                        putParcelable(
                            ValueKey.LAUNCHER_PROTOCOL_INFO,
                            protocolInfo ?: ProtocolInfoBean()
                        )
                    }
                }
            }
        }

    }

    /**
     * 当绑定视图点击时触发的函数。
     * 该函数设置了取消和确认按钮的点击事件，以及用户协议和隐私政策内容的点击事件。
     * 点击取消按钮会上传协议被拒绝的日志，并在延时后结束当前活动。
     * 点击确认按钮会记录用户同意的协议信息，并跳转到首页，然后结束当前活动。
     * 点击用户协议和隐私政策内容会弹出相应的WebView对话框显示详细信息。
     */
    override fun onBindViewClick() {
        // 设置取消按钮点击事件
        mBind.tvCancel.setTVClickListener(false) {
            userAgreement?.let { // 上传协议被拒绝日志
                mViewModel.postProtocolSign(userAgreement, protocolInfo, userApiInfo, "1")
            }
            lifecycleScope.launch {
                delay(500) // 延时500毫秒后结束当前活动
                finishAffinity()
            }
        }

        // 设置确认按钮点击事件
        mBind.tvConfirm.setTVClickListener(false) {
            CacheExt.setAgreementProtocol()
            userAgreement?.let {
                CacheExt.setUserAgreement(it)
            }
            protocolInfo?.let {
                CacheExt.setProtocol(it)
            }
            mViewModel.postProtocolSign(userAgreement, protocolInfo, userApiInfo, "0")
            toStartActivity(HomeTVActivity::class.java, mBundle)
            finishCurrentActivity(this)
        }

        // 设置用户协议内容点击事件，弹出WebView显示协议内容
        mBind.contentUa.setTVClickListener(false) {
            WebViewDialog(this@LauncherTVActivity, this@LauncherTVActivity).show(
                getStringExt(R.string.launcher_dialog_title_ua)
            )
        }
        // 设置隐私政策内容点击事件，弹出WebView显示政策内容
        mBind.contentPp.setTVClickListener(false) {
            WebViewDialog(this@LauncherTVActivity, this@LauncherTVActivity).show(
                getStringExt(R.string.launcher_dialog_title_pp)
            )
        }
    }

    /**
     * 设置内容区域的属性。
     * 该函数主要用于在首次启动应用或特定条件下，对内容区域的相关组件进行初始化或显示设置。
     *
     * @param firstLauncher 布尔值，标记是否为首次启动应用。用于控制应用启动时的内容显示逻辑。
     */
    private fun contentAttr(firstLauncher: Boolean) {
        // 显示内容布局和协议布局
        mBind.ctlLayout.visible()
        mBind.ctlProtocol.visible()
    }

    /**
     * 当请求为空时被调用。
     *
     * @param loadStatus 表示加载状态的实体，包含了加载过程中的各种状态信息。
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        // 此函数为重写函数，用于处理请求为空的情况，具体实现逻辑根据业务需求来定。
    }

    /**
     * 显示工具栏的函数。
     * 该函数为覆盖函数（override），用于指定是否显示工具栏。
     *
     * @return 返回一个布尔值，指示是否显示工具栏。在本实现中，始终返回false，表示不显示工具栏。
     */
    override fun showToolBar() = false
}
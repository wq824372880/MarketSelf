package com.zeekrlife.market.ui.activity

import android.app.ActivityManager
import android.app.ActivityManager.RecentTaskInfo
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.gyf.immersionbar.ktx.immersionBar
import com.zeekr.basic.appContext
import com.zeekr.basic.finishAllActivity
import com.zeekr.car.api.DeviceApiManager
import com.zeekr.car.api.UserApiManager
import com.zeekrlife.common.ext.finishCurrentActivity
import com.zeekrlife.common.ext.getColorExt
import com.zeekrlife.common.ext.getStringExt
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.common.ext.visible
import com.zeekrlife.market.R
import com.zeekrlife.market.app.aop.SingleClick
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.ext.mmkv
import com.zeekrlife.market.app.ext.mmkvSave
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.ValueKey
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.OpenApiDeviceInfo
import com.zeekrlife.market.data.response.OpenApiUserInfo
import com.zeekrlife.market.data.response.ProtocolInfoBean
import com.zeekrlife.market.databinding.ActivityLauncherBinding
import com.zeekrlife.market.ui.viewmodel.LauncherCXViewModel
import com.zeekrlife.market.ui.viewmodel.LauncherViewModel
import com.zeekrlife.market.utils.DiffUtils
import com.zeekrlife.market.utils.ScreenDensityUtils
import com.zeekrlife.market.widget.WebViewDialog
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class LauncherCXActivity : BaseActivity<LauncherCXViewModel, ActivityLauncherBinding>() {
    var userApiInfo: OpenApiUserInfo? = null
    var deviceApiInfo: OpenApiDeviceInfo? = null
    var userAgreement: ProtocolInfoBean? = null //用户协议
    var protocolInfo: ProtocolInfoBean? = null //隐私信息
    private var mBundle = Bundle()
    private val webViewDialog: WebViewDialog by lazy { WebViewDialog(this, this) }

    //最后点击的时间
    private var triggerLastTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!isTaskRoot) {
//            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
//            var rootActivity: String? = null
//            // 获取当前任务信息
//            val appTask = am.appTasks?.get(0)
//            val recentTaskInfo: RecentTaskInfo? = appTask?.taskInfo
//            rootActivity = recentTaskInfo?.baseActivity?.className
//            if (rootActivity != null) {
//                LogUtils.e("zzzLauncherActivity isTaskRoot","Task Root zzzLauncherActivity: $rootActivity")
//            }
//            finish()
//            return
//        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewModel.getOpenApiInfo(
            UserApiManager.getInstance().userAPI,
            DeviceApiManager.getInstance().deviceAPI
        )
    }

    override fun onResume() {
        super.onResume()
        immersionBar {
            navigationBarColor(R.color.launcher_bg_color)
        }
    }

    override fun initObserver() {
        mViewModel.mOpenApiInfo.observe(this) {
            "openapiinfo****$it****\n${NetUrl.BASE_URL}".logE("openapiinfo")
            userApiInfo = it.userInfo
            deviceApiInfo = it.deviceInfo
            mBundle.apply {
                putString(EntryActivity.EXTRA_KEY_USERID, userApiInfo?.userId)
            }
            when {
                !CacheExt.isAgreementProtocol() -> { //未同意协议
                    mViewModel.getProtocolInfo()
                    contentAttr(true)
                }
//                CacheExt.getUserAgreement()?.needUpdate == true || CacheExt.getProtocol()?.needUpdate == true -> { //更新协议
//                    userAgreement = CacheExt.getUserAgreement()
//                    protocolInfo = CacheExt.getProtocol()
//                    contentAttr(false)
//                }
//                else -> { //不需要更新协议
//                    toStartActivity(HomeActivity::class.java, mBundle)
//                    finish()
//                    overridePendingTransition(0, 0)
//                }
            }
        }
        mViewModel.mRemoteProtocolInfo.observe(this) { it ->
            it?.apply {
                userAgreement = this
                if (userApiInfo?.userId != null && userAgreement != null) {
                    mBundle.apply {
                        putString(EntryActivity.EXTRA_KEY_USERID, userApiInfo?.userId)
                        putParcelable(ValueKey.USER_AGREEMENT_INFO, userAgreement ?: ProtocolInfoBean())
                    }
                }
            }
        }
        mViewModel.mRemoteProtocolPolicy.observe(this){
            it?.apply {
                protocolInfo = this
                if (userApiInfo?.userId != null && protocolInfo != null) {
                    mBundle.apply {
                        putString(EntryActivity.EXTRA_KEY_USERID, userApiInfo?.userId)
                        putParcelable(ValueKey.LAUNCHER_PROTOCOL_INFO, protocolInfo ?: ProtocolInfoBean())
                    }
                }
            }
        }

    }

    override fun onBindViewClick() {
        mBind.tvCancel.setOnClickListener @SingleClick {
            userAgreement?.let { //上传协议被拒绝日志
                mViewModel.postProtocolSign(userAgreement, protocolInfo, userApiInfo, "1")
            }
            lifecycleScope.launch {
                mmkv.clear()
                mmkvSave.clear()
                delay(500)
                finishAllActivity()
                exitProcess(0)
            }
        }

        mBind.tvConfirm.setOnClickListener @SingleClick {
            CacheExt.setAgreementProtocol()
            userAgreement?.let {
//                it.needUpdate = false
                CacheExt.setUserAgreement(it)
            }
            protocolInfo?.let {
//                it.needUpdate = false
                CacheExt.setProtocol(it)
            }
            mViewModel.postProtocolSign(userAgreement, protocolInfo, userApiInfo, "0")
            toStartActivity(this@LauncherCXActivity,HomeCXActivity::class.java, mBundle)
            finishCurrentActivity(this)
        }
    }

    /**
     * 初始化隐私政策内容
     * @param firstLauncher 是否是首次启动应用
     * @return SpannableStringBuilder 类型的隐私政策内容，其中协议名称会设置为特定的样式
     */
    private fun initPrivacy(firstLauncher: Boolean): SpannableStringBuilder {
        mBind.ctlProtocol.visible()
        val content = if (firstLauncher) getStringExt(R.string.privacy) else getStringExt(R.string.privacy_update)
        val spannableStringBuilder = SpannableStringBuilder(content)
        val userAgreementBeginIndex = content.indexOf("《")
        val userAgreementEndIndex = content.indexOf("》") + 1
        val protocolBeginIndex = content.lastIndexOf("《")
        val protocolEndIndex = content.lastIndexOf("》") + 1

        setSpanStyle(spannableStringBuilder, userAgreementBeginIndex, userAgreementEndIndex, Constants.APPSTOREUA_BX1E)
        setSpanStyle(spannableStringBuilder, protocolBeginIndex, protocolEndIndex, Constants.APPSTOREPP_BX1E)
        return spannableStringBuilder

    }

    /**
     * 为指定的 SpannableStringBuilder 设置特定样式的文本段落。
     * @param spannableStringBuilder 要操作的 SpannableStringBuilder 对象。
     * @param start 段落的起始位置。
     * @param end 段落的结束位置。
     * @param type 标识段落类型的字符串，用于决定后续操作。
     */
    private fun setSpanStyle(
        spannableStringBuilder: SpannableStringBuilder,
        start: Int,
        end: Int,
        type: String
    ) {
        // 创建并设置一个 ClickableSpan，用于响应文本点击事件
        spannableStringBuilder.setSpan(object : ClickableSpan() {
            // 点击事件的处理逻辑
            override fun onClick(widget: View) {
                val currentClickTime = System.currentTimeMillis()
                // 防止快速连续点击，只有当两次点击间隔超过1秒时才响应
                if (triggerLastTime == 0L || currentClickTime - triggerLastTime > 1000) {
                    triggerLastTime = currentClickTime
                    // 根据 type 的值决定显示哪个对话框
                    if (type == Constants.APPSTOREUA_BX1E) {
                        webViewDialog.show(
                            getStringExt(R.string.launcher_dialog_title_ua)
                        )
                    } else {
                        webViewDialog.show(
                            getStringExt(R.string.launcher_dialog_title_pp)
                        )
                    }
                }
            }

            // 更新绘制状态，去除下划线并设置特定颜色
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = getColorExt(R.color.theme_orange_text_color)
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    }

    /**
     * 设置内容区域的属性。
     * @param firstLauncher 指示是否是首次启动应用。
     */
    private fun contentAttr(firstLauncher: Boolean) {
        // 显示内容布局
        mBind.ctlLayout.visible()
        // 设置隐私政策内容，根据是否首次启动应用进行初始化
        mBind.tvContent.text = initPrivacy(firstLauncher)
        // 设置高亮颜色为透明，以便显示下划线
        mBind.tvContent.highlightColor = Color.TRANSPARENT
        // 允许文本内容的链接点击
        mBind.tvContent.movementMethod = LinkMovementMethod()
        // 因长按会崩溃，禁用长按事件
        mBind.tvContent.isLongClickable = false
    }

    /**
     * 当请求为空时被调用。
     *
     * @param loadStatus 表示加载状态的实体，包含了加载过程中的各种状态信息。
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        // 此函数为重写函数，用于处理请求为空的情况，具体实现逻辑根据业务需求来定。
    }

    override fun showToolBar() = false
}
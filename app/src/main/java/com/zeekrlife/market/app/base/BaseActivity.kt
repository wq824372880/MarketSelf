package com.zeekrlife.market.app.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.hjq.shape.view.ShapeImageView
import com.zeekr.basic.appContext
import com.zeekr.basic.finishAllActivity
import com.zeekr.car.api.PolicyApiManager
import com.zeekrlife.common.base.BaseVBActivity
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.market.R
import com.zeekrlife.market.app.widget.CustomToolBar
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.utils.ScreenDensityUtils
import com.zeekrlife.net.interception.logging.util.LogUtils
import kotlin.system.exitProcess


/**
 * 描述　: 新创建的 使用 ViewBinding 需要自定义修改什么就重写什么 具体方法可以 搜索 BaseIView 查看
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : BaseVBActivity<VM, VB>(){

    var mToolbar: CustomToolBar?= null
    var mTitleBarView: View?= null
    var mImmersionBar:ImmersionBar ?= null
    private var closeAppReceiver: BroadcastReceiver? = null
    protected var hasLauncher = true
    protected var isActive = false
    protected var realPath :String? = ""
    protected var popupSoftKeyboard = false //bugfix 记录搜索页软键盘弹出

     companion object{
        const val REAL_PATH = "INTERNAL_realPath"
        const val RAW_URI = "NTeRQWvye18AkPd6G"
        const val ACTION_ECARX_VR_APP_CLOSE = "ecarx.intent.broadcast.action.ECARX_VR_APP_CLOSE"
        const val CATEGORY_ECARX_VR_APP_CLOSE_STORE = "ecarx.intent.broadcast.category.ECARX_VR_APP_CLOSE_STORE"
        const val EXTRA_NAME_CLOSE_TYPE = "close_type"
        const val FROM_KEY = "from_key"
        const val MEMBER_CENTER_PACKAGE_NAME = "ecarx.membercenter"
        const val EXTRA_VALUE_MOVE_TASK_TO_BACK = 0 // 退到后台
        const val EXTRA_VALUE_EXIT_APPLICATION = 1 // 完全退出
    }



    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取当前Activity关联的Display对象
        val display = window.windowManager.defaultDisplay
        // 获取Display的ID
        val displayId = display.displayId
        LogUtils.e("zzzDisplayId", "Activity Display ID: $displayId")
        if(displayId == 0){
//            ScreenDensityUtils.setCustomDensity(this, appContext)
        }

        if (hasLauncher) {
            try {
                // 设置vr监听
                closeAppReceiver = CloseAppReceiver()
                val intentFilter = IntentFilter(ACTION_ECARX_VR_APP_CLOSE)
                intentFilter.addCategory(CATEGORY_ECARX_VR_APP_CLOSE_STORE)
                registerReceiver(closeAppReceiver, intentFilter)
            } catch (e: IllegalArgumentException) {
                // 处理参数异常
                Log.e("zzzBaseActivity", "IllegalArgumentException: ${e.message}")
            } catch (e: SecurityException) {
                // 处理安全异常
                Log.e("zzzBaseActivity", "SecurityException: ${e.message}")
            } catch (e: Exception) {
                // 处理其他异常
                e.logStackTrace()
                Log.e("zzzBaseActivity", "Exception: ${Log.getStackTraceString(e)}")
            }
        }
    }


    override fun getTitleBarView(): View? {
        mTitleBarView = LayoutInflater.from(this).inflate(R.layout.layout_titlebar_view, null)
        mToolbar = mTitleBarView?.findViewById(R.id.customToolBar)
        return mTitleBarView
    }

    override fun initImmersionBar() {
        mImmersionBar =  ImmersionBar.with(this)
        mImmersionBar?.navigationBarColor(R.color.theme_main_background_color)?.init()
        //设置共同沉浸式样式
        if (showToolBar()) {
//            mToolbar.setBackgroundResource(R.color.white)
//            ImmersionBar.with(this).titleBar(mToolbar).init()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        //hasLauncher = IntentUtils.getLaunchAppIntent(packageName) != null
//        setupUI(((findViewById(android.R.id.content))as ViewGroup).getChildAt(0))
    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    @SuppressLint("LogNotTimber")
    override fun onDestroy() {
        super.onDestroy()
        mTitleBarView = null
        mToolbar = null
        mImmersionBar = null
        if (hasLauncher) {
            try {
                unregisterReceiver(closeAppReceiver)
            } catch (e: IllegalArgumentException) {
                // 处理参数异常
                Log.e("zzzBaseActivity", "IllegalArgumentException: ${e.message}")
            } catch (e: IllegalStateException) {
                // 处理状态异常
                Log.e("zzzBaseActivity", "IllegalStateException: ${e.message}")
            } catch (e: SecurityException) {
                // 处理安全异常
                Log.e("zzzBaseActivity", "SecurityException: ${e.message}")
            } catch (e: Exception) {
                // 处理其他异常
                e.logStackTrace()
                Log.e("zzzBaseActivity", "Exception: ${Log.getStackTraceString(e)}")
            } finally {
                closeAppReceiver = null
            }
        }
    }

    open fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if(v !is ShapeImageView){
                    hideSoftKeyboard(this@BaseActivity)
                    popupSoftKeyboard = false
                }
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    open fun hideSoftKeyboard(activity: AppCompatActivity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (activity.currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken, 0
        )
    }

    protected fun registerStartupStateObserver(appItemList: List<AppItemInfoBean>?) {
        if (appItemList.isNullOrEmpty()) {
            PolicyApiManager.getInstance().unregisterStartupStateObserver()
        }
        val pkgNameList = mutableListOf<String>()
        appItemList?.forEach {
            if (!it.apkPackageName.isNullOrEmpty()) {
                Log.i(PolicyApiManager.TAG, "registerStartupStateObserver ==> ${it.apkPackageName}")
                pkgNameList.add(it.apkPackageName)
            }
        }
        PolicyApiManager.getInstance().registerStartupStateObserver(pkgNameList)
    }

    inner class CloseAppReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null) {
                return
            }
            if (MEMBER_CENTER_PACKAGE_NAME != intent.getStringExtra(FROM_KEY)) {
                // 根据from_key判断, 不符合用户中心包名的不处理，只能通过用户中心打开或者关闭应用市场
                return
            }
            if (ACTION_ECARX_VR_APP_CLOSE == intent.action) {
                when (intent.getIntExtra(EXTRA_NAME_CLOSE_TYPE, EXTRA_VALUE_EXIT_APPLICATION)) {
                    EXTRA_VALUE_MOVE_TASK_TO_BACK -> moveTaskToBack(true)
                    else -> {
                        finishAllActivity()
                        exitProcess(0)
                    }
                }
            }
        }


    }
}
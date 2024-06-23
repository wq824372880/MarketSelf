package com.zeekrlife.market.app.base

import android.util.Log
import androidx.viewbinding.ViewBinding
import com.zeekr.car.api.PolicyApiManager
import com.zeekrlife.common.base.BaseVbFragment
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.market.data.response.AppItemInfoBean

/**
 * 描述　: 新创建的 使用 ViewBinding，需要自定义修改什么就重写什么 具体方法可以 搜索 BaseIView 查看
 */
abstract class BaseFragment<VM : BaseViewModel, DB : ViewBinding> : BaseVbFragment<VM, DB>() {

    //需要自定义修改什么就重写什么 具体方法可以 搜索 BaseIView 查看
    /**
     * 注册App启动限制回调
     */
    protected fun registerStartupStateObserver(appItemList: List<AppItemInfoBean>?) {
//        if (appItemList.isNullOrEmpty()) {
//            PolicyApiManager.getInstance().unregisterStartupStateObserver()
//        }
//        val pkgNameList = mutableListOf<String>()
//        appItemList?.forEach {
//            if (!it.apkPackageName.isNullOrEmpty()) {
//                Log.i(PolicyApiManager.TAG, "registerStartupStateObserver ==> ${it.apkPackageName}")
//                pkgNameList.add(it.apkPackageName)
//            }
//        }
//        val boolean = PolicyApiManager.getInstance().registerStartupStateObserver(pkgNameList)
//        Log.i(PolicyApiManager.TAG, "register success ==> ${boolean}")
    }
}
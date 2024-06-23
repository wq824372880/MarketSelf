package com.zeekrlife.market.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.zeekr.basic.getPackageNameName
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.deepLinkToStartActivity
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.toJsonStr
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.eventViewModel
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.OrderReCharged
import com.zeekrlife.market.databinding.ActivityEntryBinding
import com.zeekrlife.market.utils.DiffUtils
import com.zeekrlife.net.interception.logging.util.logE

/**
 * 网关层
 * 外部跳转入口类 所有从外部跳转到AppStore的,必须经过这个类分发
 * 除了EntryActivity和LauncherActivity之外，所有的Activity子类都要设置export=false
 */
class EntryActivity : BaseActivity<BaseViewModel, ActivityEntryBinding>() {

    companion object {
        private const val TAG = "zzzEntryActivity"
        var ACT_MAIN = "/main/MainActivity"
        var ACT_APPS = "/detail/DetailActivity"
        var ACT_SEARCH = "/search/SearchActivity"
        var ACT_MANAGE_HOME = "/manage/ManageHomeActivity"
        var AROME_PROCESS = "/arome/process"

        var EXTRA_KEY_ORDERNO = "KEY_ORDERNO"
        var EXTRA_KEY_ORDERSTATUS = "KEY_ORDERSTATUS"
        var EXTRA_KEY_USERID = "KEY_USERID"
        var EXTRA_KEY_DESCRIPTION = "KEY_DESCRIPTION"
        var EXTRA_KEY_ORDERSOURCE = "KEY_ORDERSOURCE"

        private val routeMap: HashMap<String?, String?> = object : HashMap<String?, String?>() {
            init {
                put("main", ACT_MAIN)
                put("detail", ACT_APPS)
                put("search", ACT_SEARCH)
                put("manage", ACT_MANAGE_HOME)
                put("arome", AROME_PROCESS)
                put("apps", ACT_APPS) //兼容dc的应用跳转详情
            }
        }

        /**
         * 跳转APP
         */
        fun startActivity(activity: Activity?, intent: Intent) {
            val uri = intent.data
            val extras = intent.extras
            "EntryActivity startActivity uri::$uri,extras::${extras.toJsonStr()}".logE(TAG)
            if (uri != null) {
                if (uri.getQueryParameters(REAL_PATH) != null
                    && uri.getQueryParameters(REAL_PATH).size != 0
                    || extras != null && extras.containsKey(REAL_PATH)
                ) {
                    return
                }
                val pathSegments = uri.pathSegments
                if (pathSegments == null) {
                    activity?.finish()
                    return
                }
                val pathLayer = pathSegments.size
                if (pathLayer == 1 && routeMap.containsKey(pathSegments[0])) {
                    if(!CacheExt.isAgreementProtocol()){
                        toStartActivity(LauncherActivity::class.java)
                    }else{
                        when (pathSegments[0]) {
                            "main" -> startHomeActivity(activity,uri)
                            "apps", "detail" -> startAppDetailActivity(activity, uri)
                            "search" -> startSearchActivity(activity, uri)
                            "manage" -> toStartActivity(HomeActivity::class.java)
                            else -> toStartActivity(HomeActivity::class.java)
                        }
                    }

                } else if (pathLayer == 1 && "rechargedevent" == pathSegments[0]) {
                    val orderno = intent.getStringExtra(EXTRA_KEY_ORDERNO)
                    val orderStatus = intent.getStringExtra(EXTRA_KEY_ORDERSTATUS)
                    val userid = intent.getStringExtra(EXTRA_KEY_USERID)
                    val description = intent.getStringExtra(EXTRA_KEY_DESCRIPTION)
                    val ordersource = intent.getStringExtra(EXTRA_KEY_ORDERSOURCE)

                    eventViewModel.orderReChargedEvent.postValue(OrderReCharged(orderno, orderStatus, userid, description, ordersource))
                }
            }
        }

        /**
         * 跳转首页
         * @param uri   xc://com.zeekrlife.market/main/displayId=xxx
         */
        private fun startHomeActivity(activity: Activity?, uri: Uri) {
            "EntryActivity startHomeActivity ${activity?.javaClass?.simpleName},uri:$uri".logE(TAG)
            activity?.let {
                checkDisplayId(uri,{
                    DiffUtils.toCx(activity)
                },{
                    deepLinkToStartActivity(activity,HomeActivity::class.java, Bundle())
                })

            }
        }
        /**
         * 跳转详情
         * @param uri  外部跳转：
         * 1、xc://com.zeekrlife.market/detail||apps?id=xxx&displayId=xxx
         * 2、xc://com.zeekrlife.market/detail||apps?appId=xxx&displayId=xxx
         * 3、xc://com.zeekrlife.market/detail||apps?packagename=xxx&displayId=xxx
         * 4、新增displayId=csd、backrest等
         */
        private fun startAppDetailActivity(activity: Activity?, uri: Uri) {
            "EntryActivity startAppDetailActivity ${activity?.javaClass?.simpleName},uri:$uri".logE(TAG)
            try {
                //版本ID
                val appVersionId = uri.getQueryParameter("id")?.toLong() ?: -1
                if (appVersionId != -1L) {
                    checkDisplayId(uri,{
                        DiffUtils.toDetailCxActivity(activity, appVersionId)
                    },{
                        AppDetailActivity.start(activity, appVersionId)
                    })

                    return
                }
                //AppID
                val appId = uri.getQueryParameter("appId")?.toLong() ?: -1
                if (appId != -1L) {
                    checkDisplayId(uri,{
                        DiffUtils.toDetailCxActivityByAppId(activity, appId)
                    },{
                        AppDetailActivity.startByAppId(activity, appId)
                    })

                    return
                }
                //包名
                val packageName = uri.getQueryParameter("packagename") ?: ""
                if (packageName.isNotEmpty()) {
                    checkDisplayId(uri,{
                        DiffUtils.toDetailCxActivityByPackageName(activity, packageName)
                    },{
                        AppDetailActivity.startByPackageName(activity, packageName)
                    })

                    return
                }
            } catch (e: Exception) {
                e.logStackTrace()
            }
        }

        /**
         * 跳转搜索
         * @param uri   xc://com.zeekrlife.market/search?keyword=xxx
         */
        private fun startSearchActivity(activity: Activity?, uri: Uri) {
            val keyword = uri.getQueryParameter("keyword")
            "EntryActivity startSearchActivity ${activity?.javaClass?.simpleName},uri:$uri".logE(TAG)
            activity?.let {
                val packageName = getPackageNameName(activity)
                if ("com.zeekr.market.rear".contentEquals(packageName)) {
                    DiffUtils.toSearchTvActivity(activity, Bundle().apply {
                        putString("keyword", keyword)
                    })
                }else{
                    checkDisplayId(uri,{
                            DiffUtils.toSearchCxActivity(activity, Bundle().apply {
                            putString("keyword", keyword)
                        })

                    },{
                        deepLinkToStartActivity(activity, SearchActivity::class.java, Bundle().apply {
                            putString("keyword", keyword)
                        })
                    })


                }

            }
        }

        private fun checkDisplayId(uri: Uri, onSuccess: () -> Unit, onFailure: () -> Unit) {
            val displayId = uri.getQueryParameter("displayId") ?: ""
            if (displayId.contentEquals("backrest", ignoreCase = true)) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent == null) {
            "EntryActivity onCreate intent == null".logE(TAG)
            finish()
            return
        }

        startActivity(this, intent)
        "EntryActivity finish".logE(TAG)
        if(isFinishing || isDestroyed){
            return
        }
        finish()
    }
}

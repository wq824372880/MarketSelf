package com.zeekrlife.market.sensors

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.market.sensors.TrackActivityLifecycleCallbacks.Companion.getFromPage
import com.zeekrlife.market.ui.activity.AppDetailActivity
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.task.base.R
import com.zeekrlife.task.base.constant.TaskState

class AppDetailActivityTrack : ActivityLifecycleTrack() {

    private var isTrackAppDetail = false

    private var fromPage = "应用详情"

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        fromPage = getFromPage()
    }

    /**
     * 应用详情页面展示
     * 上报时机：应用详情页面加载完成时
     */
    override fun onActivityResumed(activity: Activity) {
        try {
            if (!isTrackAppDetail) {
                isTrackAppDetail = true
                if (activity is AppDetailActivity) {
                    trackAppDetailShow(activity)
                    trackAppUninstall(activity)
                    trackAppInstall(activity)
                }
            }
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 应用详情展示事件
     * 上报时机：应用详情页面加载完成时
     */
    private fun trackAppDetailShow(activity: AppDetailActivity) {
        "trackAppDetailShow fromPage:$fromPage".logE()
        activity.mViewModel.appDetail.observe(activity, Observer {
            it?.apply {
                if (fromPage.isNotEmpty()) {
                    SensorsTrack.onAppDetailShow(fromPage, it)
                }
            }
        })
    }

    /**
     * 应用下载、更新事件
     */
    private fun trackAppInstall(activity: AppDetailActivity) {
        var isTrack = false
        val taskLayout = activity.mBind.layoutAppDetailInfo.layoutTask
        var button = taskLayout.findViewById<View>(R.id.view_action)
        if(button == null) {
            //taskLayout中为action
            button = taskLayout.findViewById(R.id.action)
        }
        button.postDelayed({
            TrackUtils.hookViewOnClick(button) {
                val app = activity.mViewModel.appDetail.value
                val taskInfo = activity.mViewModel.getTaskInfo()
                if (!isTrack && app != null && taskInfo != null) {
                    isTrack = true
                    when (taskInfo.state) {
                        TaskState.DOWNLOADABLE -> {
                            SensorsTrack.onAppDownload(fromPage, app)
                        }
                        TaskState.UPDATABLE -> {
                            SensorsTrack.onAppUpdate(fromPage,app.apkName ?: "", 1, 1)
                        }
                    }
                }
            }
        }, 500)
    }

    /**
     * 应用卸载事件
     * 目前商城中的卸载只有详情页面中的按钮
     * 上报时机：应用详情点击卸载时
     */
    private fun trackAppUninstall(activity: AppDetailActivity) {
        if (fromPage.isNotEmpty()) {
            val unInstallBtn = activity.mBind.layoutAppDetailInfo.btnUninstall
            unInstallBtn.postDelayed({
                TrackUtils.hookViewOnClick(unInstallBtn) {
                    activity.mViewModel.appDetail.value?.apply {
                        SensorsTrack.onAppUninstall(fromPage, this)
                    }
                }
            }, 500)
        }
    }
}
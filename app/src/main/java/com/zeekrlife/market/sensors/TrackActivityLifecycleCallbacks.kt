package com.zeekrlife.market.sensors

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.market.ui.activity.AppDetailActivity
import com.zeekrlife.market.ui.activity.HomeActivity
import com.zeekrlife.market.ui.activity.SearchActivity
import java.util.*

class TrackActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    companion object {

        private val trackActs = mutableMapOf<Activity, ActivityLifecycleTrack>()

        private val acts = LinkedList<Activity>()

        /**
         * 从当前页面获取相关信息。
         * 该函数首先检查活动栈(acts)是否不为空，然后根据栈顶活动的类型返回不同的字符串信息。
         * 如果栈顶活动是HomeActivity，则尝试获取当前选中的顶部标签和底部标签的文本信息，并优先返回顶部标签的文本。
         * 如果栈顶活动是SearchActivity，则返回"搜索结果"。
         * 如果栈顶活动是AppDetailActivity，则返回"应用详情"。
         * 如果在处理过程中捕获到任何异常，包括NullPointerException、ClassCastException、
         * IllegalStateException和其他未知异常，都会记录异常的堆栈跟踪，并返回空字符串。
         *
         * @return 根据当前页面状态返回的相关信息字符串，可能为空。
         */
        fun getFromPage(): String {
            try {
                if(acts.isNotEmpty()) {
                    when (val lastActivity = acts.last) {
                        is HomeActivity -> {
                            // 尝试获取当前选中的顶部标签和底部标签的文本信息
                            val selectedTab = lastActivity.mBind.tablayout.selectedTab
                            val bottomSelectedTab = lastActivity.mBind.tablayoutBottom.selectedTab

                            // 优先返回顶部标签的文本，若无则返回底部标签的文本
                            if (selectedTab != null) {
                                return selectedTab.titleView.text?.toString() ?: ""
                            }

                            if (bottomSelectedTab != null) {
                                return bottomSelectedTab.titleView.text?.toString() ?: ""
                            }
                            return ""
                        }
                        is SearchActivity -> {
                            // 当前页面为搜索结果页面
                            return "搜索结果"
                        }
                        is AppDetailActivity -> {
                            // 当前页面为应用详情页面
                            return "应用详情"
                        }
                    }
                }
            } catch (e: NullPointerException) {
                // 处理空指针异常
                e.logStackTrace()
            } catch (e: ClassCastException) {
                // 处理类型转换异常
                e.logStackTrace()
            } catch (e: IllegalStateException) {
                // 处理非法状态异常
                e.logStackTrace()
            } catch (e: Exception) {
                // 处理其他未知异常
                e.logStackTrace()
            }
            // 在所有情况下，如果无法获取有效信息则返回空字符串
            return ""
        }
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        try {
            var activityTrack = trackActs[activity]
            if (activityTrack == null) {
                activityTrack = getActivityTrack(activity)
                trackActs[activity] = activityTrack
            }
            activityTrack.onActivityCreated(activity, bundle)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 根据提供的Activity类型获取相应的活动跟踪对象。
     *
     * @param activity 传入的Activity对象，将根据此对象的类型决定返回哪种活动跟踪对象。
     * @return 返回一个活动跟踪对象，根据传入的Activity类型，可能返回[HomeActivityTrack]、[AppDetailActivityTrack]或[ActivityLifecycleTrack]。
     */
    private fun getActivityTrack(activity: Activity) = when (activity) {
        is HomeActivity -> HomeActivityTrack()
        is AppDetailActivity -> AppDetailActivityTrack()
        else -> ActivityLifecycleTrack()
    }

    override fun onActivityStarted(activity: Activity) {
        try {
            trackActs[activity]?.onActivityStarted(activity)
            acts.add(activity)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        try {
            trackActs[activity]?.onActivityResumed(activity)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        try {
            trackActs[activity]?.onActivityPaused(activity)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        try {
            trackActs[activity]?.onActivityStopped(activity)
            acts.remove(activity)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        try {
            trackActs[activity]?.onActivityDestroyed(activity)
            trackActs.remove(activity)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        try {
            trackActs[activity]?.onActivitySaveInstanceState(activity, bundle)
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
    }
}
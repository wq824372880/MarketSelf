package com.zeekrlife.market.sensors

import android.app.Activity
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.TimeUtils
import com.zeekrlife.market.app.widget.verticaltablayout.VerticalTabLayout
import com.zeekrlife.market.app.widget.verticaltablayout.widget.TabView
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.ui.activity.HomeActivity
import com.zeekrlife.market.ui.fragment.RecommendFragment
import com.zhpan.bannerview.BannerViewPager.OnPageClickListener

class HomeActivityTrack : ActivityLifecycleTrack() {

    private var isTrackTabSelect = false

    private val BANNER_SCROLL_AUTO = 0

    private val BANNER_SCROLL_MANUAL = 1

    private val tabSelectListener = object : VerticalTabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabView?, position: Int) {
            val title: String = tab?.title?.content ?: ""
            val tabId: Int = tab?.id ?: -1
            SensorsTrack.onHomeTabSelected(title, tabId)
        }

        override fun onTabReselected(tab: TabView?, position: Int) {}
    }

    private val fragmentLifecycleCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, fragment: Fragment, v: View, savedInstanceState: Bundle?) {
            if (fragment is RecommendFragment) {
                trackRecommendBanner(fragment)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        try {
            if (!isTrackTabSelect) {
                isTrackTabSelect = true
                if (activity is HomeActivity) {
                    trackHomeTabSelect(activity)
                    SensorsTrack.onPageExposure("精品推荐")
                    trackRecommendFragment(activity)
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
    }

    /**
     * 精品推荐页面
     * @param activity
     */
    override fun onActivityDestroyed(activity: Activity) {
        try {
            if (activity is HomeActivity) {
                unTrackHomeTabSelect(activity)
                unTrackRecommendFragment(activity)
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
    }

    /**
     * 精品推荐页面
     * @param activity
     */
    private fun trackRecommendFragment(activity: HomeActivity) {
        try {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallback, false)
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
     * 停止追踪推荐Fragment的生命周期。
     * 该方法会尝试从当前活动的supportFragmentManager中注销fragmentLifecycleCallback。
     * 如果过程中发生异常，会分别捕获并记录不同类型的异常日志。
     *
     * @param activity 当前的HomeActivity实例，用于执行Fragment生命周期的注销操作。
     */
    private fun unTrackRecommendFragment(activity: HomeActivity) {
        try {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallback)
        } catch (e: NullPointerException) {
            // 捕获并记录空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 捕获并记录类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 捕获并记录非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获并记录其他未知异常
            e.logStackTrace()
        }
    }

    /**
     * 精品推荐：Banner
     */
    private fun trackRecommendBanner(fragment: RecommendFragment) {
        try {
            trackBannerShow(fragment)
            trackBannerClick(fragment)
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
     * Banner展示
     */
    private fun trackBannerShow(fragment: RecommendFragment) {
        val bannerView = fragment.mBind.bannerView
        val layoutRecommendContent = fragment.mBind.layoutRecommendContent
        //NestedScrollView 监听滚动判断banner视图是否可见
        var bannerIsVisible = true
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            val mRect = Rect()
            layoutRecommendContent.setOnScrollChangeListener { _, _, _, _, _ ->
                bannerIsVisible = bannerView.getGlobalVisibleRect(mRect)
            }
        }
        //banner view 轮播监听
        bannerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            private var currPageSelected = -1

            private var lastBannerAction = BANNER_SCROLL_AUTO

            private var currBannerAction = BANNER_SCROLL_AUTO

            private var bannerInTime = 0L

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        currBannerAction = BANNER_SCROLL_AUTO
                    }
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        currBannerAction = BANNER_SCROLL_MANUAL
                    }
                    else -> {}
                }
            }

            override fun onPageSelected(position: Int) {
                if (currPageSelected != position) {
                    val bannerOutTime = TimeUtils.currentTimeInLong
                    //停留时间
                    val interval = if (bannerInTime <= 0) 0 else (bannerOutTime - bannerInTime)
                    //上报条件：Banner可见+间隔小于五分钟
                    if (currPageSelected != -1 && bannerIsVisible && interval < 1000 * 60 * 5) {
                        fragment.mViewModel.getAppAdvertisementDot(Constants.APPSTORE_RECOMMEND_BANNER, currPageSelected)?.apply {
                            if (mediaTypes?.isNotEmpty() == true) {
                                val id = mediaTypes[0].id ?: -1
                                SensorsTrack.onRecommendBannerShow(
                                    id.toString(), currPageSelected,
                                    TimeUtils.getTime(bannerInTime), TimeUtils.getTime(bannerOutTime),
                                    getBannerScrollType(lastBannerAction), getBannerScrollType(currBannerAction), interval
                                )
                            }
                        }
                    }
                    //记为上一次状态
                    bannerInTime = TimeUtils.currentTimeInLong
                    lastBannerAction = currBannerAction
                    currPageSelected = position
                }
            }
        })
    }

    /**
     * 获取横幅滚动类型的描述字符串
     *
     * @param type 横幅的滚动类型，参考BANNER_SCROLL_AUTO等常量定义
     * @return 返回描述横幅滚动方式的字符串，分为"轮播"和"手动"两种
     */
    private fun getBannerScrollType(type: Int): String = if (type == BANNER_SCROLL_AUTO) "轮播" else "手动"

    /**
     * Banner点击事件
     */
    private fun trackBannerClick(fragment: RecommendFragment) {
        val onPageClickListener = fragment.onBannerPageClickListener
        fragment.onBannerPageClickListener = OnPageClickListener { clickedView, position ->
            onPageClickListener.onPageClick(clickedView, position)
            try {
                fragment.mViewModel.getAppAdvertisementDot(Constants.APPSTORE_RECOMMEND_BANNER, position)?.apply {
                    if (mediaTypes?.isNotEmpty() == true) {
                        val id = mediaTypes[0].id ?: -1
                        SensorsTrack.onRecommendBannerClick(id.toString(), position)
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
        }
    }

    /**
     * 应用侧边栏
     */
    private fun trackHomeTabSelect(activity: HomeActivity) {
        try {
            //TabLayout 精品推荐、xx分类列表
            activity.mBind.tablayout.addOnTabSelectedListener(tabSelectListener)
            //tablayoutBottom 我的应用、设置
            activity.mBind.tablayoutBottom.addOnTabSelectedListener(tabSelectListener)
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
     * 停止追踪HomeActivity的Tab选中事件。
     * 该函数尝试从当前的HomeActivity中移除tab选中监听器，以防止在Activity销毁后仍被调用导致异常。
     * @param activity 需要停止追踪Tab选中事件的HomeActivity实例。
     */
    private fun unTrackHomeTabSelect(activity: HomeActivity) {
        try {
            activity.mBind.tablayout.removeOnTabSelectedListener(tabSelectListener)
            activity.mBind.tablayoutBottom.removeOnTabSelectedListener(tabSelectListener)
        } catch (e: NullPointerException) {
            // 处理可能的空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理可能的类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理可能的非法状态异常，例如当TabLayout未初始化时
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获并记录其他未知异常
            e.logStackTrace()
        }
    }
}
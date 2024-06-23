package com.zeekrlife.market.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.zeekrlife.common.ext.*
import com.zeekrlife.market.R
import com.zeekrlife.market.app.aop.Log
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.eventViewModel
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.HomeItemCategoryBean
import com.zeekrlife.market.databinding.ActivityTvHomeBinding
import com.zeekrlife.market.manager.AppPropertyManager
import com.zeekrlife.market.ui.adapter.HomeTVTabAdapter
import com.zeekrlife.market.ui.fragment.CategoryTVFragment
import com.zeekrlife.market.ui.fragment.MyAppsTVFragment
import com.zeekrlife.market.ui.fragment.RecommendTVFragment
import com.zeekrlife.market.ui.fragment.SettingFragment
import com.zeekrlife.market.ui.viewmodel.HomeViewModel
import com.zeekrlife.market.widget.verticaltablayout.VerticalTVTabLayout
import com.zeekrlife.market.widget.verticaltablayout.VerticalTVTabLayout.OnTabTVListener
import com.zeekrlife.market.widget.verticaltablayout.widget.QTVTabView
import com.zeekrlife.market.widget.verticaltablayout.widget.AbstractTVTabView
import com.zeekrlife.market.worker.ThirdUpdateStartWorker
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 应用市场首页
 */
class HomeTVActivity : BaseActivity<HomeViewModel, ActivityTvHomeBinding>() {

    private val fragments: MutableList<Fragment> = ArrayList()
    var isEmptyList = false
    var lastIndex = 0 //记录白天黑夜模式切换前tabIndex

    var isReload = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!CacheExt.isAgreementProtocol()) {
            toStartActivity(this, LauncherTVActivity::class.java, Bundle())
            finish()
            return
        }
        AppPropertyManager.cloudQueryPropertyValue()
        intent.extras?.apply {
            lastIndex = this.getInt(Constants.HOME_TAB_INDEX)
            isReload = this.getBoolean(Constants.HOME_IS_RELOAD, false)
        }
    }

    @Log("首页")
    override fun initView(savedInstanceState: Bundle?) {
        if (!isReload) {
            mViewModel.getHomeList()
        }
    }

    /**
     * 延迟2秒后，调用ThirdUpdateStartWorker.startWorker()工作线程
     */
    private fun delayStartWorker() {
        lifecycleScope.launch {
            delay(2000)
            ThirdUpdateStartWorker.startWorker(this@HomeTVActivity.applicationContext)
        }
    }

    /**
     * 初始化观察者模式的相关逻辑。
     * 主要负责处理UI更新和数据的加载。
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun initObserver() {
        //自动更新服务
        mViewModel.mDoStartUpdateWorker.observe(this) {
            if (it == true && !isReload) {
                delayStartWorker()
            }
        }
        // 观察分类列表数据的变化，以更新UI
        mViewModel.mCategoryList.observe(this) { list ->

            try {
                // 清理当前的Fragment，为新的分类列表准备
                val realFragments = supportFragmentManager.fragments
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                realFragments.forEach {
                    if (it is RecommendTVFragment || it is CategoryTVFragment || it is MyAppsTVFragment || it is SettingFragment) {
                        fragmentTransaction.remove(it)
                    }
                }
                fragmentTransaction.commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            } catch (e: IllegalStateException) {
                // 处理Fragment状态异常
                e.logStackTrace()
            } catch (e: NullPointerException) {
                // 处理空指针异常
                e.logStackTrace()
            } catch (e: IllegalArgumentException) {
                // 处理非法参数异常
                e.logStackTrace()
            } catch (e: Exception) {
                // 处理其他异常
                e.logStackTrace()
            }

            // 更新分类列表UI
            fragments.clear()
            isEmptyList = list.isEmpty()

            val categoryList = ArrayList<HomeItemCategoryBean>(list)
            categoryList.add(0, HomeItemCategoryBean(categoryName = "精品推荐", id = 0))
            for (i in 0 until categoryList.size) {
                when (categoryList[i].categoryName) {
                    "精品推荐" -> {
                        addFragment(RecommendTVFragment(), categoryList[i].categoryName!!)
                    }
                    else -> {
                        addFragment(CategoryTVFragment(), categoryList[i].categoryName ?: "", categoryList[i].id, i)
                    }
                }
            }
            categoryList.add(HomeItemCategoryBean(categoryName = "我的应用", id = -1))
            categoryList.add(HomeItemCategoryBean(categoryName = "设置", id = -2))
            addFragment(MyAppsTVFragment(), "我的应用", -1)
            addFragment(SettingFragment(), "设置", -2)
            // 根据分类列表的长度，设置TabLayout和Fragment容器的适配器
            lifecycleScope.launch {
                // 控制加载状态的可见性
                if (lastIndex < 6) {
                    loadingVisible(false)
                }
                // 根据分类列表的长度设置TabLayout的适配器
                if (categoryList.size <= 8) {
                    mBind.tablayout.setupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        fragments.dropLast(2),
                        HomeTVTabAdapter(
                            this@HomeTVActivity,
                            categoryList.dropLast(2) as MutableList<HomeItemCategoryBean>,
                            true
                        )
                    )
                } else {
                    mBind.tablayout.setupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        fragments.take(6),
                        HomeTVTabAdapter(
                            this@HomeTVActivity,
                            categoryList.take(6) as MutableList<HomeItemCategoryBean>,
                            true
                        )
                    )
                }
                // 设置底部TabLayout的适配器
                mBind.tablayoutBottom.setupWithFragment(
                    supportFragmentManager,
                    R.id.fragment_container,
                    fragments.takeLast(2),
                    HomeTVTabAdapter(
                        this@HomeTVActivity,
                        categoryList.takeLast(2) as MutableList<HomeItemCategoryBean>,
                        false
                    )
                )

                //给tv屏设置焦点顺序
                val myAppTabView = mBind.tablayoutBottom.getTabAt(0)
                    myAppTabView.id = View.generateViewId()
                val settingTabView = mBind.tablayoutBottom.getTabAt(1)
                    settingTabView.id = View.generateViewId()
                settingTabView.nextFocusUpId = myAppTabView.id
                myAppTabView.nextFocusDownId = settingTabView.id
                // 根据之前的选中状态，切换到正确的Tab
                if (lastIndex < 6) {
                    when (lastIndex) {
                        -2, -1 -> {
                            mBind.tablayout.clearTabFragment()
                            mBind.tablayout.setReset()
                            mBind.tablayoutBottom.removeSelectedTab() //解决切换主题后默认SelectedTab不为空
                            mBind.tablayoutBottom.setTabSelected(lastIndex + 2)
                            mBind.tablayoutBottom.setBold()
                        }
                        else -> {
                            mBind.tablayoutBottom.clearTabFragment()
                            mBind.tablayoutBottom.setReset()
                            mBind.tablayout.setTabSelected(lastIndex)
                            mBind.tablayout.setBold()
                        }
                    }
                }
                // 处理分类分页加载导致的切换主题操作fragment重叠问题
                if (categoryList.size >= 9) {
                    val lazyFragment = fragments.drop(6)
                    val lazyList = categoryList.drop(6)
                    delay(500)
                    mBind.tablayout.addSetupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        lazyFragment.dropLast(2),
                        HomeTVTabAdapter(
                            this@HomeTVActivity,
                            lazyList.dropLast(2) as MutableList<HomeItemCategoryBean>,
                            true
                        )
                    )

                    if (lastIndex < 0) {  //解决分类分页加载导致切换主题操作fragment会重叠
                        mBind.tablayout.clearTabFragment()
                        mBind.tablayout.setReset()
                        mBind.tablayoutBottom.setTabSelected(lastIndex)
                    } else {
                        mBind.tablayout.setBold()
                    }

                    if (lastIndex >= 6) {
                        mBind.tablayoutBottom.clearTabFragment()
                        mBind.tablayoutBottom.setReset()
                        mBind.tablayout.setTabSelected(lastIndex)
                        mBind.tablayout.setBold()
                        delay(100)
                        loadingVisible(false)
                    }
                }

            }
            mBind.searchBar.isFocusable = true

        }
        //跳转对应tab
        eventViewModel.switchTabEvent.observe(this) { i ->
            if (isDestroyed) {
                toStartActivity(HomeTVActivity::class.java)
            } else {
                mBind.tablayoutBottom.setReset()
                mBind.tablayoutBottom.clearTabFragment()
                mBind.tablayout.setBold()
                mBind.tablayout.setTabSelected(i)
            }
        }
    }

    /**
     * 绑定视图点击事件。
     * 主要处理Tab的点击和焦点变化事件。
     */
    override fun onBindViewClick() {
        // 设置Tab的点击和焦点变化逻辑
        fun setTabView(position: Int, hasFocus: Boolean, oldFocusView: View?, top: Boolean) {
            if (oldFocusView == null || oldFocusView is QTVTabView) {
                lastIndex = if (top) position else position - 2
            }
            "setTabView position=$position, hasFocus=$hasFocus, oldFocusView=${oldFocusView?.id}, top=$top,lastIndex=$lastIndex".logE("zzzsetTabView")
            when (lastIndex) {

                -2, -1 -> {
                    if (oldFocusView == null || oldFocusView is QTVTabView) {
                        mBind.tablayoutBottom.setTabSelected(position)
                        lastIndex = position - 2
                    } else {
                        mBind.tablayoutBottom.setTabSelected(lastIndex + 2)
                    }
                    mBind.tablayout.setReset()
                    mBind.tablayout.clearTabFragment()
                    mBind.tablayoutBottom.setBold()
                    "setTabView mBind.tablayoutBottom.getTabAt(0):${mBind.tablayoutBottom.getTabAt(0).id},mBind.tablayoutBottom.getTabAt(1):${mBind.tablayoutBottom.getTabAt(1).id},${oldFocusView?.id}}".logE("zzzsetTabView")
//                    mBind.tablayoutBottom.getTabAt(1).nextFocusUpId = mBind.tablayoutBottom.getTabAt(0).id
//                    mBind.tablayoutBottom.getTabAt(1).nextFocusForwardId = mBind.tablayoutBottom.getTabAt(0).id

                }
                else -> {
                    if (oldFocusView == null || oldFocusView is QTVTabView) {
                        mBind.tablayout.setTabSelected(position)
                        lastIndex = position
                    } else {
                        mBind.tablayout.setTabSelected(lastIndex)
                    }
                    mBind.tablayoutBottom.setReset()
                    mBind.tablayoutBottom.clearTabFragment()
                    mBind.tablayout.setBold()
                }
            }
        }
        mBind.tablayout.addOnTabTVListener(object : OnTabTVListener {
            override fun onTabFocusChange(position: Int, hasFocus: Boolean, oldFocusView: View?) {
                if (hasFocus) {
                    setTabView(position, hasFocus, oldFocusView, true)
                }
            }

            override fun onTabEnter(view: View, position: Int) {
                view.clickWithTrigger(300) {
                    val fragment = fragments[position]
                    when {
                        fragment is RecommendTVFragment -> {
                            fragment.refresh()
                        }
                        fragment is CategoryTVFragment -> {
                            fragment.refresh()
                        }
                    }
                }
            }
        })

        mBind.tablayoutBottom.addOnTabTVListener { position, hasFocus, oldFocusView ->
            if (hasFocus) {
                lastIndex = position - 2
                setTabView(position, hasFocus, oldFocusView, false)
            }
        }

        // 设置Tab选中事件处理
        mBind.tablayout.addOnTabSelectedListener(object :
            VerticalTVTabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onTabSelected(tab: AbstractTVTabView?, position: Int) {
                lastIndex = position
                mBind.tablayoutBottom.setReset()
                mBind.tablayoutBottom.clearTabFragment()
                mBind.tablayout.setBold()
            }

            override fun onTabReselected(tab: AbstractTVTabView?, position: Int) {}
        })
        mBind.tablayoutBottom.addOnTabSelectedListener(object :
            VerticalTVTabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onTabSelected(tab: AbstractTVTabView?, position: Int) {
                lastIndex = position - 2
                mBind.tablayout.setReset()
                mBind.tablayout.clearTabFragment()
                mBind.tablayoutBottom.setBold()
            }

            override fun onTabReselected(tab: AbstractTVTabView?, position: Int) {}
        })
        // 搜索按钮的点击事件处理
        mBind.searchBar.setTVClickListener {
            toStartActivity(SearchTVActivity::class.java)
            overridePendingTransition(0, 0)
        }

        // 搜索框聚焦时的处理
        mBind.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mBind.tablayout.setReset()
            }
        }
    }

    /**
     * 用于向fragments列表中添加一个Fragment对象，并为其设置一些参数。
     * fragment：要添加的Fragment对象。
     * tag：给Fragment设置的标签。
     * id：给Fragment设置的ID，默认为0。
     * position：Fragment的位置，默认为1。 函数将这些参数打包到一个Bundle对象中，并将其设置为fragment的参数，
     * 然后将fragment添加到fragments列表中。
     */
    private fun addFragment(fragment: Fragment, tag: String, id: Int = 0, position: Int = 1) {
        val bundle = Bundle()
        bundle.putString("tag", tag)
        bundle.putInt("id", id)
        bundle.putInt("position", position)
        fragment.arguments = bundle
        fragments.add(fragment)
    }

    /**
     * 显示ToolBar
     */
    override fun showToolBar(): Boolean {
        return false
    }

    /**
     * 显示空UI
     */
    override fun showEmptyUi() {
    }

    /**
     * 显示加载动画
     */
    fun loadingVisible(visible: Boolean) {
        if (visible) {
            mBind.homeLoadingSkeleton.visible()
        } else {
            mBind.homeLoadingSkeleton.gone()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        intent?.putExtra(Constants.HOME_TAB_INDEX, lastIndex)
        intent?.putExtra(Constants.HOME_IS_RELOAD, true)
    }

    private fun overridePendingTransition() {
        overridePendingTransition(0, 0)
        intent.putExtra(Constants.HOME_TAB_INDEX, lastIndex)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finishCurrentActivity(this)
        startActivity(intent)
    }

}
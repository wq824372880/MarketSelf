package com.zeekrlife.market.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.zeekrlife.common.ext.*
import com.zeekrlife.market.R
import com.zeekrlife.market.app.aop.Log
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.eventViewModel
import com.zeekrlife.market.app.widget.verticaltablayout.VerticalTabLayout
import com.zeekrlife.market.app.widget.verticaltablayout.widget.TabView
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.HomeItemCategoryBean
import com.zeekrlife.market.databinding.ActivityHomeBinding
import com.zeekrlife.market.ui.adapter.HomeTabAdapter
import com.zeekrlife.market.ui.fragment.CategoryCXFragment
import com.zeekrlife.market.ui.fragment.MyAppsCXFragment
import com.zeekrlife.market.ui.fragment.RecommendCXFragment
import com.zeekrlife.market.ui.fragment.SettingCXFragment
import com.zeekrlife.market.ui.viewmodel.HomeCXViewModel
import com.zeekrlife.market.utils.DiffUtils
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 应用市场首页
 */
class HomeCXActivity : BaseActivity<HomeCXViewModel, ActivityHomeBinding>() {

    private val fragments: MutableList<Fragment> = mutableListOf()
    var isEmptyList = false
    var lastIndex = 0 //记录白天黑夜模式切换前tabIndex

    var isReload = false

    companion object {
        private const val TAG = "zzzHomeCXActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!CacheExt.isAgreementProtocol()) {
            if (!DiffUtils.toLauncherTVActivity(this)) {
                toStartActivity(this, LauncherCXActivity::class.java, Bundle())
            }
            finish()
            return
        }
        intent.extras?.apply {
            lastIndex = this.getInt(Constants.HOME_TAB_INDEX)
            isReload = this.getBoolean(Constants.HOME_IS_RELOAD, false)
        }

    }

    @Log("首页")
    override fun initView(savedInstanceState: Bundle?) {
        "HomeCXActivity initView()".logE(TAG)
        if (!isReload) {
            mViewModel.getHomeList()
        }
    }

    override fun onResume() {
        super.onResume()
        "HomeCXActivity onResume()".logE(TAG)
    }

//    private fun delayStartWorker() {
//        lifecycleScope.launch {
//            delay(3000)
//            ThirdUpdateStartWorker.startWorker(this@HomeActivity.applicationContext)
//        }
//    }

    /**
     * 初始化观察者模式, 用于数据和界面的绑定与更新。
     * 此函数主要负责处理界面组件与数据模型之间的交互，包括：
     * 1. 更新分类列表。
     * 2. 根据分类列表更新TabLayout和Fragment。
     * 3. 处理界面切换时的逻辑。
     */
    override fun initObserver() {
        //过渡调用更新注释掉
        //自动更新服务
//        mViewModel.mDoStartUpdateWorker.observe(this) {
//            if (it == true && !isReload) {
//                delayStartWorker()
//            }
//        }
        //分类列表
        mViewModel.mCategoryList.observeDistinct(this) { list ->

            try {
                val realFragments = supportFragmentManager.fragments
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                realFragments.forEach {
                    if (it is RecommendCXFragment || it is CategoryCXFragment || it is MyAppsCXFragment || it is SettingCXFragment) {
                        fragmentTransaction.remove(it)
                    }
                }
                fragmentTransaction.commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            } catch (e: Exception) {
                e.logStackTrace(android.util.Log.getStackTraceString(e))
            }
            // 更新分类列表数据并根据数据添加对应的Fragment
            fragments.clear()
            isEmptyList = list.isEmpty()

            val categoryList = ArrayList<HomeItemCategoryBean>(list)
            categoryList.add(0, HomeItemCategoryBean(categoryName = "精品推荐", id = 0))
            categoryList.forEach {
                when (it.categoryName) {
                    "精品推荐" -> {
                        addFragment(RecommendCXFragment(), it.categoryName!!)
                    }

                    else -> {
                        addFragment(CategoryCXFragment(), it.categoryName ?: "", it.id)
                    }
                }
            }
            categoryList.add(HomeItemCategoryBean(categoryName = "我的应用", id = -1))
            categoryList.add(HomeItemCategoryBean(categoryName = "设置", id = -2))
            addFragment(MyAppsCXFragment(), "我的应用", -1)
            addFragment(SettingCXFragment(), "设置", -2)
            // 根据分类列表的数量，动态设置TabLayout和Fragment
            lifecycleScope.launch {
                // 控制加载状态的显示与隐藏
                if (lastIndex < 6) {
                    loadingVisible(false)
                }
                // 设置TabLayout和Fragment，处理数据不足和数据超出的情况
                if (categoryList.size <= 8) {
                    mBind.tablayout.setupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        fragments.dropLast(2),
                        HomeTabAdapter(
                            this@HomeCXActivity,
                            categoryList.dropLast(2) as MutableList<HomeItemCategoryBean>,
                            true
                        )
                    )
                } else {
                    mBind.tablayout.setupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        fragments.take(6),
                        HomeTabAdapter(
                            this@HomeCXActivity,
                            categoryList.take(6) as MutableList<HomeItemCategoryBean>,
                            true
                        )
                    )
                }

                mBind.tablayoutBottom.setupWithFragment(
                    supportFragmentManager,
                    R.id.fragment_container,
                    fragments.takeLast(2),
                    HomeTabAdapter(
                        this@HomeCXActivity,
                        categoryList.takeLast(2) as MutableList<HomeItemCategoryBean>,
                        false
                    )
                )
                // 根据之前选中的标签，更新Tab的选中状态
                if (lastIndex < 6) {
                    when (lastIndex) {
                        -2, -1 -> {
                            mBind.tablayout.clearTabFragment()
                            mBind.tablayout.setReset()
                            mBind.tablayoutBottom.removeSelectedTab() // 解决切换主题后默认SelectedTab不为空
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
                // 处理更多分类的动态加载
                if (categoryList.size >= 9) {
                    val lazyFragment = fragments.drop(6)
                    val lazyList = categoryList.drop(6)
                    delay(300)
                    mBind.tablayout.addSetupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        lazyFragment.dropLast(2),
                        HomeTabAdapter(
                            this@HomeCXActivity,
                            lazyList.dropLast(2) as MutableList<HomeItemCategoryBean>,
                            true
                        )
                    )
                    // 解决分类分页加载导致切换主题操作fragment会重叠的问题
                    if (lastIndex < 0) {
                        mBind.tablayout.clearTabFragment()
                        mBind.tablayout.setReset()
                        mBind.tablayoutBottom.setTabSelected(lastIndex)
                        mBind.tablayoutBottom.setBold()
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

        }
        //跳转对应tab
        eventViewModel.switchTabCXEvent.observe(this) { i ->
            if (isDestroyed) {
                toStartActivity(this@HomeCXActivity,HomeCXActivity::class.java,Bundle())
            } else {
                // 否则更新Tab的选中状态，并清除底部Tab的选中状态
                mBind.tablayoutBottom.setReset()
                mBind.tablayoutBottom.clearTabFragment()
                mBind.tablayout.setBold()
                mBind.tablayout.setTabSelected(i)
            }
        }
    }

    /**
     * 当绑定视图点击时的处理逻辑。
     * 该函数重写了父类方法，主要实现了对TabLayout选择事件的监听和处理，以及对搜索栏点击事件的监听和处理。
     * 无参数和返回值。
     */
    override fun onBindViewClick() {
        // 为上方TabLayout添加选中监听器
        mBind.tablayout.addOnTabSelectedListener(object : VerticalTabLayout.OnTabSelectedListener {
            /**
             * 当Tab被选中时的处理逻辑。
             * 主要用于更新底部TabLayout的状态，并清除和设置当前Tab的样式。
             */
            override fun onTabSelected(tab: TabView?, position: Int) {
                lastIndex = position
                mBind.tablayoutBottom.setReset()
                mBind.tablayoutBottom.clearTabFragment()
                mBind.tablayout.setBold()
            }

            // 当Tab被重新选中时的处理逻辑，此处无具体实现。
            override fun onTabReselected(tab: TabView?, position: Int) {}
        })
        // 为底部TabLayout添加选中监听器
        mBind.tablayoutBottom.addOnTabSelectedListener(object :
            VerticalTabLayout.OnTabSelectedListener {
            /**
             * 当底部Tab被选中时的处理逻辑。
             * 主要用于更新上方TabLayout的状态，并清除和设置当前Tab的样式。
             */
            override fun onTabSelected(tab: TabView?, position: Int) {
                lastIndex = position - 2
                mBind.tablayout.setReset()
                mBind.tablayout.clearTabFragment()
                mBind.tablayoutBottom.setBold()
            }

            // 当底部Tab被重新选中时的处理逻辑，此处无具体实现。
            override fun onTabReselected(tab: TabView?, position: Int) {}
        })

        // 设置搜索栏的点击事件处理，点击后跳转到搜索界面
        mBind.searchBar.setOnClickListener {

            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@HomeCXActivity,
                Pair.create(mBind.searchBar, "share_search_bar"),
                Pair.create(mBind.tvSearchAction, "share_tv_search_action"),
                Pair.create(mBind.tvSearchResult, "share_tv_search_result"),

                ).toBundle()
            val intent = Intent(this, SearchCXActivity::class.java)
            startActivity(intent, bundle)
        }
    }

    /**
     * 向片段集合中添加一个新的片段实例。
     * @param fragment 要添加的片段实例。
     * @param tag 为该片段指定的标签，用于后续识别或查找该片段。
     * @param id 该片段对应的视图ID，默认为0，可选参数。
     */
    private fun addFragment(fragment: Fragment, tag: String, id: Int = 0) {
        // 创建一个Bundle用于传递参数
        val bundle = Bundle()
        // 将标签和ID打包到Bundle中
        bundle.putString("tag", tag)
        bundle.putInt("id", id)
        // 将打包好的参数赋值给片段的arguments属性
        fragment.arguments = bundle
        // 将该片段添加到片段集合中
        fragments.add(fragment)
    }

    override fun showToolBar(): Boolean {
        return false
    }

    /**
     * 当请求错误发生时调用此函数。
     *
     * @param loadStatus 包含请求信息和错误状态的实体。
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        // 如果请求的代码匹配HOME_CATEGORT_LIST，则隐藏加载视图
        if (loadStatus.requestCode == NetUrl.HOME_CATEGORT_LIST) {
            loadingVisible(false)
        }
    }

    /**
     * 控制加载状态视图的可见性。
     * @param visible 指定加载状态视图是否可见。如果为true，则显示加载状态视图；如果为false，则隐藏加载状态视图。
     */
    fun loadingVisible(visible: Boolean) {
        if (visible) {
            // 根据屏幕宽度决定显示哪个加载视图
            if (getScreenWidthIs2560()) {
                // 如果屏幕宽度为2560，显示骨架屏，隐藏旋转动画视图
                mBind.homeLoadingSkeleton.visible()
                mBind.homeLoading.flLoading.gone()
            } else {
                // 如果屏幕宽度不是2560，隐藏骨架屏，显示旋转动画视图
                mBind.homeLoadingSkeleton.gone()
                mBind.homeLoading.flLoading.visible()
            }
        } else {
            // 不论屏幕宽度如何，都隐藏骨架屏和旋转动画视图
            mBind.homeLoadingSkeleton.gone()
            mBind.homeLoading.flLoading.gone()
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
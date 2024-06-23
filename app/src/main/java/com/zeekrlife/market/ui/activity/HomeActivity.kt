package com.zeekrlife.market.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.transition.ChangeBounds
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
import com.zeekrlife.market.ui.fragment.CategoryFragment
import com.zeekrlife.market.ui.fragment.MyAppsFragment
import com.zeekrlife.market.ui.fragment.RecommendFragment
import com.zeekrlife.market.ui.fragment.SettingFragment
import com.zeekrlife.market.ui.viewmodel.HomeViewModel
import com.zeekrlife.market.utils.DiffUtils
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 应用市场首页
 */
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    private val fragments: MutableList<Fragment> = mutableListOf()
    var isEmptyList = false
    var lastIndex = 0 //记录白天黑夜模式切换前tabIndex

    var isReload = false

    companion object {
        private const val TAG = "zzzHomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       LogUtils.e(TAG, "HomeActivity onCreate()")
        if (!CacheExt.isAgreementProtocol()){
            if(!DiffUtils.toLauncherTVActivity(this)){

                toStartActivity(this, LauncherActivity::class.java, Bundle())
            }
            return
        }
        intent.extras?.apply {
            lastIndex = this.getInt(Constants.HOME_TAB_INDEX)
            isReload = this.getBoolean(Constants.HOME_IS_RELOAD, false)
        }

    }

    /**
     * 跳转TV端
     */
    override fun onCreateDispatchEvent(): Boolean {
        if (DiffUtils.toTv(this)) {
            finish()
            return true
        }
        return false
    }

    @Log("首页")
    override fun initView(savedInstanceState: Bundle?) {
        "HomeActivity initView()".logE(TAG)
        layoutModify()
        if (!isReload) {
            mViewModel.getHomeList()
        }
    }

    override fun onResume() {
        super.onResume()
        "HomeActivity onResume()".logE(TAG)
    }

//    private fun delayStartWorker() {
//        lifecycleScope.launch {
//            delay(3000)
//            ThirdUpdateStartWorker.startWorker(this@HomeActivity.applicationContext)
//        }
//    }

    /**
     * 监听数据变化
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
                    if (it is RecommendFragment || it is CategoryFragment || it is MyAppsFragment || it is SettingFragment) {
                        fragmentTransaction.remove(it)
                    }
                }
                fragmentTransaction.commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            } catch (e: Exception) {
                e.logStackTrace(android.util.Log.getStackTraceString(e))
            }

            fragments.clear()
            isEmptyList = list.isEmpty()

            val categoryList = ArrayList<HomeItemCategoryBean>(list)
            categoryList.add(0, HomeItemCategoryBean(categoryName = "精品推荐", id = 0))
            categoryList.forEach {
                when (it.categoryName) {
                    "精品推荐" -> {
                        addFragment(RecommendFragment(), it.categoryName!!)
                    }

                    else -> {
                        addFragment(CategoryFragment(), it.categoryName ?: "", it.id)
                    }
                }
            }
            categoryList.add(HomeItemCategoryBean(categoryName = "我的应用", id = -1))
            categoryList.add(HomeItemCategoryBean(categoryName = "设置", id = -2))
            addFragment(MyAppsFragment(), "我的应用", -1)
            addFragment(SettingFragment(), "设置", -2)
            lifecycleScope.launch {

                if (lastIndex < 6) {
                    loadingVisible(false)
                }

                if (categoryList.size <= 8) {
                    mBind.tablayout.setupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        fragments.dropLast(2),
                        HomeTabAdapter(
                            this@HomeActivity,
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
                            this@HomeActivity,
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
                        this@HomeActivity,
                        categoryList.takeLast(2) as MutableList<HomeItemCategoryBean>,
                        false
                    )
                )
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
                if (categoryList.size >= 9) {
                    val lazyFragment = fragments.drop(6)
                    val lazyList = categoryList.drop(6)
                    delay(300)
                    mBind.tablayout.addSetupWithFragment(
                        supportFragmentManager,
                        R.id.fragment_container,
                        lazyFragment.dropLast(2),
                        HomeTabAdapter(
                            this@HomeActivity,
                            lazyList.dropLast(2) as MutableList<HomeItemCategoryBean>,
                            true
                        )
                    )

                    if (lastIndex < 0) {  //解决分类分页加载导致切换主题操作fragment会重叠
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
        eventViewModel.switchTabEvent.observe(this) { i ->
            if (isDestroyed) {
                toStartActivity(HomeActivity::class.java)
            } else {
                mBind.tablayoutBottom.setReset()
                mBind.tablayoutBottom.clearTabFragment()
                mBind.tablayout.setBold()
                mBind.tablayout.setTabSelected(i)
            }
        }
    }

    /**
     * 监听事件
     */
    override fun onBindViewClick() {
        mBind.tablayout.addOnTabSelectedListener(object : VerticalTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabView?, position: Int) {
                lastIndex = position
                mBind.tablayoutBottom.setReset()
                mBind.tablayoutBottom.clearTabFragment()
                mBind.tablayout.setBold()
            }

            override fun onTabReselected(tab: TabView?, position: Int) {}
        })
        mBind.tablayoutBottom.addOnTabSelectedListener(object :
            VerticalTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabView?, position: Int) {
                lastIndex = position - 2
                mBind.tablayout.setReset()
                mBind.tablayout.clearTabFragment()
                mBind.tablayoutBottom.setBold()
            }

            override fun onTabReselected(tab: TabView?, position: Int) {}
        })

        mBind.searchBar.setOnClickListener {

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@HomeActivity,
                    Pair.create(mBind.searchBar, "share_search_bar"),
                    Pair.create(mBind.tvSearchAction, "share_tv_search_action"),
                    Pair.create(mBind.tvSearchResult, "share_tv_search_result"),

                ).toBundle()
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent, bundle)
                overridePendingTransition(0, 0)

        }
    }

    /**
     * 添加fragment
     */
    private fun addFragment(fragment: Fragment, tag: String, id: Int = 0) {
        val bundle = Bundle()
        bundle.putString("tag", tag)
        bundle.putInt("id", id)
        fragment.arguments = bundle
        fragments.add(fragment)
    }

    /**
     * 是否显示toolbar
     */
    override fun showToolBar(): Boolean {
        return false
    }

    /**
     * 错误回调
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        if (loadStatus.requestCode == NetUrl.HOME_CATEGORT_LIST) {
            loadingVisible(false)
        }
    }

    /**
     * 显示加载动画
     */
    fun loadingVisible(visible: Boolean) {
        if (visible) {
            if (getScreenWidthIs2560(this)) {
                mBind.homeLoadingSkeleton.visible()
                mBind.homeLoading.flLoading.gone()
            } else {
                mBind.homeLoadingSkeleton.gone()
                mBind.homeLoading.flLoading.visible()
            }
        } else {
            mBind.homeLoadingSkeleton.gone()
            mBind.homeLoading.flLoading.gone()
        }
    }

    /**
     * 保存状态
     */
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

    private fun layoutModify(){

        val searchBarLayoutParams: ViewGroup.LayoutParams = mBind.searchBar.layoutParams
//        searchBarLayoutParams.width = (resources.getDimension(R.dimen.home_search_width)).toInt()
        searchBarLayoutParams.height = (resources.getDimension(R.dimen.home_search_height)).toInt()
        mBind.searchBar.layoutParams = searchBarLayoutParams

        mBind.tablayout.setTabHeight(dp2px(this,resources.getDimension(R.dimen.home_tab_height)))
    }

}
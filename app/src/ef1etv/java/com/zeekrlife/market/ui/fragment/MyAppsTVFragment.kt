package com.zeekrlife.market.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.zeekr.component.tv.dialog.ZeekrTVDialogCreate
import com.zeekr.dialog.ZeekrDialogCreate
import com.zeekr.dialog.button.WhichButton
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.market.R
import com.zeekrlife.market.app.aop.SingleClick
import com.zeekrlife.market.app.base.BaseFragment
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.FragmentMyAppListBinding
import com.zeekrlife.market.ui.activity.HomeTVActivity
import com.zeekrlife.market.ui.adapter.MyAppTVAdapter
import com.zeekrlife.market.ui.viewmodel.MyAppViewModel
import com.zeekrlife.market.utils.RecycleGridDivider
import com.zeekrlife.market.widget.AppDialog
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import q.rorbin.badgeview.QBadgeView
import java.util.*

class MyAppsTVFragment : BaseFragment<MyAppViewModel, FragmentMyAppListBinding>() {

    private val TAG = "MyAppsTVFragment"

    private var qBadgeView: QBadgeView? = null

    private val myAppAdapter: MyAppTVAdapter by lazy { MyAppTVAdapter() }

    /**
     * 是否已经加载过数据
     */
    private var isLoaded = false

    override fun initView(savedInstanceState: Bundle?) {

        mBind.textViewMyappUpdateTip.text =
            String.format(getString(R.string.my_app_update_tips), "--", "--")

        mBind.smartRefreshLayout.refresh {
            mViewModel.getMyApps(false)
        }

        mBind.recyclerView.grid(2).apply {
            addItemDecoration(
                RecycleGridDivider(
                    context,
                    resources.getDimension(R.dimen.my_app_item_decoration).toInt(), true
                )
            )
            adapter = myAppAdapter
        }
        mBind.recyclerView.itemAnimator = null
        myAppAdapter.setOnItemClickListener { _, _, position ->
            mViewModel.pageData.value?.list?.get(position)?.let { item ->
                AppDialog().show(requireActivity(), item, viewLifecycleOwner, true)
            }
        }
        myAppAdapter.setOnClickUpdateInfo {
            ZeekrTVDialogCreate(requireContext()).show {
                title(getString(R.string.app_detail_version_info))
                content(it)
                lifecycleOwner(viewLifecycleOwner)
                realButton(text = getString(R.string.common_i_see)) { zeekrTVDialogAction ->
                    zeekrTVDialogAction.dismiss()
                }
            }
        }

        //是否启用下拉上拉刷新功能
        mBind.smartRefreshLayout.setEnableRefresh(false)
        mBind.smartRefreshLayout.setEnableLoadMore(false)

        initBadgeView()
        getMyAppTabId()
    }

    @SuppressLint("LogNotTimber")
    override fun onResume() {
        super.onResume()
        Log.e(TAG, "isHidden -> $isHidden  isLoaded -> $isLoaded")
        if (isLoaded) {
            mViewModel.refreshUpdateSummaryTips()
            if (!isHidden) {
                referMyAppStates()
            }
        }
    }

    /**
     * 小红点
     */
    private fun initBadgeView() {
        qBadgeView = QBadgeView(context).apply {
            bindTarget(mBind.textViewMyappUpdateNum)
            setBadgeTextSize(resources.getDimension(R.dimen.tv_badge_text_size), true)
            badgeBackgroundColor = -0x10000
            badgeGravity = Gravity.TOP or Gravity.END
            isExactMode = false
            isShowShadow = false
            setGravityOffset(1f.dp, 10f.dp, true)
        }
    }

    override fun getLoadingView(): View {
        return mBind.recyclerView
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initObserver() {
        //应用列表
        mViewModel.pageData.observe(viewLifecycleOwner) {
            myAppAdapter.loadListSuccess(it, mBind.smartRefreshLayout)
            isLoaded = true
        }

        //更新概况
        mViewModel.updateTips.observe(viewLifecycleOwner) {
            try {
                mBind.textViewMyappUpdateTip.text = String.format(
                    getString(R.string.my_app_update_tips),
                    it, mViewModel.getNeedUpdateSize()
                )

                //按钮上气泡
                if (it > 0) {
                    qBadgeView?.badgeNumber = it
                    mBind.textViewMyappUpdateNum.visibility = View.VISIBLE
                } else {
                    qBadgeView?.badgeNumber = 0
                    mBind.textViewMyappUpdateNum.visibility = View.INVISIBLE
                }

                //home tablayoutBottom Badge
                val act = activity
                if (act is HomeTVActivity) {
                    act.mBind.tablayoutBottom.getTabAt(0).badgeView.badgeNumber = it
                }
            } catch (e: IndexOutOfBoundsException) {
                // 处理索引越界异常
                Log.e(TAG, "IndexOutOfBoundsException: ${e.message}")
            } catch (e: NullPointerException) {
                // 处理空指针异常
                Log.e(TAG, "NullPointerException: ${e.message}")
            } catch (e: IllegalStateException) {
                // 处理状态异常
                Log.e(TAG, "IllegalStateException: ${e.message}")
            } catch (e: Exception) {
                // 处理其他异常
                e.logStackTrace()
                Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
            }
        }

        //当应用安装新增
        mViewModel.onAppInstallPosition.observe(viewLifecycleOwner) {
            if (myAppAdapter.data.size == 1) {
                // 0 到 1 刷新EmptyUi
                showSuccessUi()
            }
            myAppAdapter.notifyItemRangeChanged(0, myAppAdapter.data.size)
        }

        //当应用卸载
        mViewModel.appUnInstallPosition.observe(viewLifecycleOwner) {
            if (myAppAdapter.data.size == 0) {
                showEmptyUi()
            }
            myAppAdapter.notifyItemRangeChanged(0, myAppAdapter.data.size)
        }
    }

    override fun lazyLoadData() {
        onLoadRetry()
    }

    /**
     * 请求失败
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            NetUrl.APP_LIST -> {
                mBind.smartRefreshLayout.finishRefresh()
            }
        }
    }

    /**
     * 请求成功 数据为空
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
        super.onRequestEmpty(loadStatus)
        when (loadStatus.requestCode) {
            NetUrl.APP_LIST -> {
                if (loadStatus.isRefresh && mBind.smartRefreshLayout.isRefreshing) {
                    mBind.smartRefreshLayout.finishRefresh()
                }
            }
        }
    }

    /**
     * 错误界面 空界面 点击重试
     */
    override fun onLoadRetry() {
        mViewModel.getMyApps(true)
    }

    /**
     * 绑定View点击事件
     */
    override fun onBindViewClick() {
        mBind.btnMyappQuickUpdate.setTVClickListener(false) {
            if (!mViewModel.startUpdate()) {
                ToastUtils.show(getString(R.string.my_app_no_update))
            } else {
                ToastUtils.show(getString(R.string.my_app_updating))
            }
        }

        mBind.btnMyappQuickUpdate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.requestFocus()
                val act = activity
                if (act is HomeTVActivity) {
                    lifecycleScope.launch {
                        delay(300)
                        val tabView = act.mBind.tablayoutBottom.getTabAt(0)
                        tabView.id = View.generateViewId()
                        view.nextFocusLeftId = tabView.id
                        view.nextFocusUpId = tabView.id
                    }
                }
            }
        }
    }

    /**
     * 监听Fragment隐藏显示
     * @param hidden Boolean
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            referMyAppStates()
        }
    }

    /**
     * 刷新应用状态
     */
    private fun referMyAppStates() {
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    withContext(Dispatchers.Default) {
                        //需要更新的应用优先排前
                        Collections.sort(
                            myAppAdapter.data,
                            Comparator<AppItemInfoBean> { app1, app2 ->
                                if ((app1.taskInfo?.state ?: 0) < (app2.taskInfo?.state ?: 0)) {
                                    return@Comparator 1
                                } else if ((app1.taskInfo?.state ?: 0) > (app2.taskInfo?.state
                                        ?: 0)
                                ) {
                                    return@Comparator -1
                                }
                                return@Comparator 0
                            })
                    }
                    myAppAdapter.notifyItemRangeChanged(0, myAppAdapter.data.size)
                    //刷新更新概况
                    mViewModel.refreshUpdateSummaryTips()
                    registerStartupStateObserver(myAppAdapter.data)
                } catch (e: ConcurrentModificationException) {
                    // 处理并发修改异常
                    Log.e(TAG, "ConcurrentModificationException: ${e.message}")
                } catch (e: IllegalStateException) {
                    // 处理状态异常
                    Log.e(TAG, "IllegalStateException: ${e.message}")
                } catch (e: Exception) {
                    // 处理其他异常
                    e.logStackTrace()
                    Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 获取MyAppTabId
     * @return Int
     */
    private fun getMyAppTabId(): Int {
        val act = activity
        if (act is HomeTVActivity) {
            lifecycleScope.launch {
                delay(300)
                val tabView = act.mBind.tablayoutBottom.getTabAt(0)
                tabView.id = View.generateViewId()
                MyAppTVAdapter.myAppTabId = tabView.id
            }
        }
        return MyAppTVAdapter.myAppTabId
    }
}
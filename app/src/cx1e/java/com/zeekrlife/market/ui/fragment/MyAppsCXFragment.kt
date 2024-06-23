package com.zeekrlife.market.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.zeekr.basic.appContext
import com.zeekr.dialog.ZeekrDialogCreate
import com.zeekr.dialog.button.WhichButton
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.market.R
import com.zeekrlife.market.app.aop.SingleClick
import com.zeekrlife.market.app.base.BaseFragment
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.FragmentMyAppListBinding
import com.zeekrlife.market.ui.activity.HomeCXActivity
import com.zeekrlife.market.ui.adapter.MyAppAdapter
import com.zeekrlife.market.ui.viewmodel.MyAppCXViewModel
import com.zeekrlife.market.ui.viewmodel.MyAppViewModel
import com.zeekrlife.market.utils.RecycleGridDivider
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import q.rorbin.badgeview.QBadgeView
import java.util.*

class MyAppsCXFragment : BaseFragment<MyAppCXViewModel, FragmentMyAppListBinding>() {

    private val TAG = "MyAppsCXFragment"

    private var qBadgeView: QBadgeView? = null

    private val myAppAdapter: MyAppAdapter by lazy { MyAppAdapter() }

    /**
     * 是否已经加载过数据
     */
    private var isLoaded = false
    /**
     * 在视图创建时被调用，用于创建Fragment的视图。
     *
     * @param inflater           用于加载布局文件的LayoutInflater。
     * @param container          承载Fragment的View容器，如果Fragment不提供UI，则可以为null。
     * @param savedInstanceState 如果Fragment被重新构建，则此处可以恢复之前保存的状态。
     * @return 返回Fragment的根视图。如果Fragment不提供UI，则返回null。
     */
    override fun initView(savedInstanceState: Bundle?) {

        mBind.textViewMyappUpdateTip.text =
            String.format(getString(R.string.my_app_update_tips), "--", "--")

        mBind.smartRefreshLayout.refresh {
            mViewModel.getMyApps(false)
        }

        mBind.recyclerView.grid(2).apply {
            addItemDecoration(RecycleGridDivider(context,
                resources.getDimension(R.dimen.my_app_item_decoration).toInt(), true))
            adapter = myAppAdapter
        }
        mBind.recyclerView.itemAnimator = null
        myAppAdapter.setOnItemClickListener { _, _, position ->
            mViewModel.startApp(activity, position)
        }
        myAppAdapter.setOnClickUpdateInfo {
            ZeekrDialogCreate.Confirm(requireContext())
                .show<ZeekrDialogCreate.Confirm> {
                    title(getString(R.string.app_detail_version_info))
                    content(it)
                    lifecycleOwner(viewLifecycleOwner)
                    buttonsVisible(WhichButton.POSITIVE)
                    positiveButton(text = getString(R.string.common_i_see)) {
                        dismiss()
                    }
                }
        }

        //是否启用下拉上拉刷新功能
        mBind.smartRefreshLayout.setEnableRefresh(false)
        mBind.smartRefreshLayout.setEnableLoadMore(false)

        initBadgeView()
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
            badgeTextColor = getColorExt(R.color.setting_red_point_text_color)
            badgeBackgroundColor = getColorExt(R.color.setting_red_point_color)
            setBadgeTextSize(resources.getDimension(R.dimen.tab_app_red_point_text_size), true)
            badgeGravity = Gravity.TOP or Gravity.END
            isExactMode = false
            isShowShadow = false
            setGravityOffset(resources.getDimension(R.dimen.my_app_red_point_offset_x), resources.getDimension(R.dimen.my_app_red_point_offset_y), true)
        }
    }

    override fun getLoadingView(): View {
        return mBind.recyclerView
    }

    @SuppressLint("NotifyDataSetChanged", "LogNotTimber")
    override fun initObserver() {
        //应用列表
        mViewModel.pageData.observe(viewLifecycleOwner) {
            myAppAdapter.loadListSuccess(it, mBind.smartRefreshLayout)
            mBind.smartRefreshLayout.setEnableLoadMore(false)
            isLoaded = true

        }

        //更新概况
        mViewModel.updateTips.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(300) //防止切主题后badgeView还未创建完成导致空指针
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
                    if (act is HomeCXActivity) {
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

    override fun onBindViewClick() {
        mBind.btnMyappQuickUpdate.setOnClickListener @SingleClick {
            if (!mViewModel.startUpdate()) {
                ToastUtils.show(activity?: appContext,getString(R.string.my_app_no_update))
            } else {
                ToastUtils.show(activity?:appContext,getString(R.string.my_app_updating))
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            referMyAppStates()
        }
    }

    @SuppressLint("LogNotTimber")
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
}
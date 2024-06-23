package com.zeekrlife.common.ext

import com.chad.library.adapter.base.BaseQuickAdapter
import com.zeekr.component.rebound.ZeekrReboundRefreshLayout
import com.zeekrlife.net.api.BasePage
import com.zeekrlife.net.interception.logging.util.toast
import com.zeekrlife.net.load.LoadStatusEntity

/**
 * 下拉刷新
 * @receiver SmartRefreshLayout
 * @param refreshAction Function0<Unit>
 * @return SmartRefreshLayout
 */
fun ZeekrReboundRefreshLayout.refresh(refreshAction: () -> Unit = {}): ZeekrReboundRefreshLayout {
    this.setOnRefreshListener {
        refreshAction.invoke()
    }
    return this
}

/**
 * 上拉加载
 * @receiver SmartRefreshLayout
 * @param loadMoreAction Function0<Unit>
 * @return SmartRefreshLayout
 */
fun ZeekrReboundRefreshLayout.loadMore(loadMoreAction: () -> Unit = {}): ZeekrReboundRefreshLayout {
    this.setOnLoadMoreListener {
        loadMoreAction.invoke()
    }
    return this
}

/**
 * 列表数据加载成功
 * @receiver BaseQuickAdapter<T,*>
 * @param baseListNetEntity BaseListNetEntity<T>
 */
fun <T> BaseQuickAdapter<T, *>.loadListSuccess(
    baseListNetEntity: BasePage<T>,
    smartRefreshLayout: ZeekrReboundRefreshLayout,
    isDiff: Boolean = false
) {
    //关闭头部刷新
    if (baseListNetEntity.isRefresh()) {
        //如果是第一页 那么设置最新数据替换
        if (isDiff) {
            this.setDiffNewData(baseListNetEntity.getPageData())
        } else {
            this.setNewInstance(baseListNetEntity.getPageData())
        }
        smartRefreshLayout.finishRefresh()
    } else {
        //不是第一页，累加数据
        baseListNetEntity.getPageData()?.let { this.addData(it) }
        //刷新一下分割线
        this.recyclerView.post { this.recyclerView.invalidateItemDecorations() }
    }
    //乳沟还有下一页数据 那么设置 smartRefreshLayout 还可以加载更多数据
    if (baseListNetEntity.hasMore()) {
        smartRefreshLayout.finishLoadMore()
        smartRefreshLayout.setNoMoreData(false)
        smartRefreshLayout.setEnableLoadMore(true)
    } else {
        //乳沟没有更多数据了，设置 smartRefreshLayout 加载完毕 没有更多数据
//        smartRefreshLayout.finishLoadMoreWithNoMoreData()
        smartRefreshLayout.finishLoadMore()
        smartRefreshLayout.setEnableLoadMore(false)
    }
}

/**
 * 列表数据 null
 * @receiver BaseQuickAdapter<*, *>
 * @param loadStatus LoadStatusEntity
 * @param status LoadService<*>
 * @param smartRefreshLayout SmartRefreshLayout
 */
fun BaseQuickAdapter<*, *>.loadListEmpty(
    loadStatus: LoadStatusEntity,
    smartRefreshLayout: ZeekrReboundRefreshLayout
) {
    //关闭头部刷新
    if (loadStatus.isRefresh) {
        smartRefreshLayout.finishRefresh()
        smartRefreshLayout.setEnableLoadMore(false)
        //第一页，但是之前有数据，只给提示
        loadStatus.errorMessage.toast()
    }
}


/**
 * 列表数据请求失败
 * @receiver BaseQuickAdapter<*, *>
 * @param loadStatus LoadStatusEntity
 * @param status LoadService<*>
 * @param smartRefreshLayout SmartRefreshLayout
 */
fun BaseQuickAdapter<*, *>.loadListError(
    loadStatus: LoadStatusEntity,
    smartRefreshLayout: ZeekrReboundRefreshLayout
) {
    //关闭头部刷新
    if (loadStatus.isRefresh) {
        smartRefreshLayout.finishRefresh()
        //第一页，但是之前有数据，只给提示
        loadStatus.errorMessage.toast()
    } else {
        // 不是第一页请求，让recyclerview设置加载失败
        smartRefreshLayout.finishLoadMore(false)
        //给个错误提示
        loadStatus.errorMessage.toast()
    }
}
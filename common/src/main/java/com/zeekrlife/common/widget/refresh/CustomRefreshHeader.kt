package com.zeekrlife.common.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.zeekrlife.common.R
import com.zeekrlife.common.ext.gone
import com.zeekrlife.common.ext.visible

/**
 * @author mac
 * @date 2023/1/3 13:18
 * description：TODO
 */
class CustomRefreshHeader : LinearLayout, RefreshHeader {

    constructor(context: Context) : this(context, null) {
        initView(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private lateinit var mPbLoading: ProgressBar
    private lateinit var mIvLoading: ImageView
    private fun initView(context: Context) {
        val view = inflate(context, R.layout.custom_refresh_header_layout, this)
        mPbLoading = view.findViewById(R.id.loading)
        mIvLoading = view.findViewById(R.id.iv_loading)
    }

    var refreshListener: RefreshStateListener? = null
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {

        if (newState == RefreshState.PullDownToRefresh) {
            mIvLoading.visible()
            mPbLoading.gone()
        } else if (newState == RefreshState.Refreshing) {
            mIvLoading.gone()
            mPbLoading.visible()

        }
        refreshListener?.run {
            onStateChanged(newState)
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun setPrimaryColors(vararg colors: Int) {

    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {

    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {

    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        mIvLoading.gone()
        mPbLoading.postDelayed({ mPbLoading.gone() }, 300)
        return 300 //延迟300毫秒之后再弹回

    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {

    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    interface RefreshStateListener {
        fun onStateChanged(state: RefreshState) {}
    }

}
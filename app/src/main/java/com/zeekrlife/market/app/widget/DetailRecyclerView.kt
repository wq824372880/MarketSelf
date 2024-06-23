package com.zeekrlife.market.app.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewConfiguration
import android.view.MotionEvent

class DetailRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defAttrStyle: Int = 0
) : RecyclerView(context, attributeSet, defAttrStyle) {

    private val TAG = DetailRecyclerView::class.java.simpleName

    private var mScrollPointerId = 0
    private var mInitialTouchX = 0
    private var mInitialTouchY = 0
    private var mTouchSlop = 0

    init {
        val vc = ViewConfiguration.get(context)
        this.mTouchSlop = vc.scaledTouchSlop
    }

    override fun setScrollingTouchSlop(slopConstant: Int) {
        val vc = ViewConfiguration.get(context)
        mTouchSlop = when (slopConstant) {
            TOUCH_SLOP_DEFAULT -> vc.scaledTouchSlop
            TOUCH_SLOP_PAGING -> vc.scaledPagingTouchSlop
            else -> vc.scaledTouchSlop
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val canScrollHorizontally = layoutManager!!.canScrollHorizontally()
        val canScrollVertically = layoutManager!!.canScrollVertically()
        val action = e.actionMasked
        val actionIndex = e.actionIndex
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mScrollPointerId = e.getPointerId(0)
                mInitialTouchX = (e.x + 0.5f).toInt()
                mInitialTouchY = (e.y + 0.5f).toInt()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                mScrollPointerId = e.getPointerId(actionIndex)
                mInitialTouchX = (e.getX(actionIndex) + 0.5f).toInt()
                mInitialTouchY = (e.getY(actionIndex) + 0.5f).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val index = e.findPointerIndex(mScrollPointerId)
                if (index < 0) {
//                    LogUtils.e(TAG, "Error processing scroll; pointer index for id "
//                            + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false
                }
                val x = (e.getX(index) + 0.5f).toInt()
                val y = (e.getY(index) + 0.5f).toInt()
                if (scrollState != SCROLL_STATE_DRAGGING) {
                    val dx = x - mInitialTouchX
                    val dy = y - mInitialTouchY
                    var startScroll = false
                    if (canScrollHorizontally && Math.abs(dx) > mTouchSlop && Math.abs(dx) > Math.abs(
                            dy
                        )
                    ) {
                        startScroll = true
                    }
                    if (canScrollVertically && Math.abs(dy) > mTouchSlop && Math.abs(dy) > Math.abs(
                            dx
                        )
                    ) {
                        startScroll = true
                    }
                    //LogUtils.d(TAG, "canScrollHorizontally: " + canScrollHorizontally + ", canScrollVertically: " + canScrollVertically
//                            + ", dx: " + dx + ", dy: " + dy
//                            + ", startScroll: " + startScroll
//                            + ", mTouchSlop: " + mTouchSlop);
                    if (!startScroll) {
                        return false
                    }
                }
            }
            else -> {
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}
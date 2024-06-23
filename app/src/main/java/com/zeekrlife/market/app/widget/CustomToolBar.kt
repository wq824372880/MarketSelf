package com.zeekrlife.market.app.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.zeekrlife.market.R

/**
 * 描述　: 头部样式
 */
class CustomToolBar : FrameLayout {

    private lateinit var toolBar: Toolbar
    private lateinit var toolBarLeftTitle: AppCompatTextView
    private lateinit var toolBarTitle: AppCompatTextView
    private lateinit var toolBarRightTitle: AppCompatTextView
    private lateinit var toolBarRightIcon: AppCompatImageView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.toolbar_layout_custom, this)
        toolBar = view.findViewById(R.id.toolBar)
        toolBar.title = ""
        toolBarLeftTitle = view.findViewById(R.id.toolbar_left_title)
        toolBarTitle = view.findViewById(R.id.toolbar_center_title)
        toolBarRightTitle = view.findViewById(R.id.toolbar_right_title)
        toolBarRightIcon = view.findViewById(R.id.toolbar_right_icon)
    }

    fun setLeftTitle(titleStr: String) {
        toolBarLeftTitle.text = titleStr
    }

    fun setLeftTitle(titleResId: Int) {
        toolBarLeftTitle.text = context.getString(titleResId)
    }

    fun setLeftTitleColor(colorResId: Int) {
        toolBarLeftTitle.setTextColor(colorResId)
    }

    fun setCenterTitle(titleStr: String) {
        toolBarTitle.text = titleStr
    }

    fun setCenterTitle(titleResId: Int) {
        toolBarTitle.text = context.getString(titleResId)
    }

    fun setCenterTitleColor(colorResId: Int) {
        toolBarTitle.setTextColor(colorResId)
    }

    fun setToolbarBackGround(colorResId: Int) {
        toolBar.setBackgroundColor(colorResId)
    }

    fun getBaseToolBar(): Toolbar {
        return toolBar
    }

    fun setRightTitle(titleStr: String) {
        toolBarRightTitle.text = titleStr
    }

    fun setRightTitle(titleResId: Int) {
        toolBarRightTitle.text = context.getString(titleResId)
    }

    fun setRightTitleColor(colorResId: Int) {
        toolBarRightTitle.setTextColor(colorResId)
    }

    fun setRightIcon(iconResId: Int) {
        toolBarRightIcon.setImageResource(iconResId)
    }

    fun getLeftTitle(): AppCompatTextView {
        return toolBarLeftTitle
    }

}

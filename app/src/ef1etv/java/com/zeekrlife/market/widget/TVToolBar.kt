package com.zeekrlife.market.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.zeekrlife.common.ext.getColorExt
import com.zeekrlife.market.R
import com.zeekrlife.market.utils.FocusBorderFrameLayout

class TVToolBar : FocusBorderFrameLayout {

    private lateinit var toolBar: Toolbar
    private lateinit var toolBarLeftTitle: AppCompatTextView
    private lateinit var toolBarTitle: AppCompatTextView
    private lateinit var toolBarRightTitle: AppCompatTextView
    private lateinit var toolBarRightIcon: AppCompatImageView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.toolbar_layout_tv, this)
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

fun TVToolBar.initBack(
    titleLeftStr: String = "",
    titleLeftColor: Int = getColorExt(R.color.theme_main_text_color),
    titleStr: String = "",
    titleColor: Int = getColorExt(R.color.theme_main_text_color),
    titleRightStr: String = "",
    titleRightColor: Int = getColorExt(R.color.theme_main_text_color),
    backImg: Int = R.drawable.icon_titlebar_back,
    rightIcon: Int = 0,
    onBack: (toolbar: TVToolBar) -> Unit,

    ): TVToolBar {
    this.setLeftTitle(titleLeftStr)
    this.setLeftTitleColor(titleLeftColor)
    this.setCenterTitle(titleStr)
    this.getBaseToolBar().setNavigationIcon(backImg)
    this.setCenterTitleColor(titleColor)
    this.setRightTitle(titleRightStr)
    this.setRightTitleColor(titleRightColor)
    this.setRightIcon(rightIcon)
    this.getBaseToolBar().setNavigationOnClickListener { onBack.invoke(this) }
    this.setOnClickListener { onBack.invoke(this) }
    return this
}
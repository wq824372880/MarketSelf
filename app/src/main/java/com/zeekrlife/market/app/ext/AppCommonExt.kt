package com.zeekrlife.market.app.ext

import com.zeekrlife.common.ext.getColorExt
import com.zeekrlife.market.R
import com.zeekrlife.market.app.widget.CustomToolBar


/**
 * 初始化有返回键的toolbar
 */
fun CustomToolBar.initBack(
    titleLeftStr: String = "",
    titleLeftColor: Int = getColorExt(R.color.theme_main_text_color),
    titleStr: String = "",
    titleColor: Int = getColorExt(R.color.theme_main_text_color),
    titleRightStr: String = "",
    titleRightColor: Int = getColorExt(R.color.theme_main_text_color),
    backImg: Int = R.drawable.icon_titlebar_back,
    rightIcon: Int = 0,
    onBack: (toolbar: CustomToolBar) -> Unit,

    ): CustomToolBar {
    this.setLeftTitle(titleLeftStr)
    this.setLeftTitleColor(titleLeftColor)
    this.setCenterTitle(titleStr)
    this.getBaseToolBar().setNavigationIcon(backImg)
    this.setCenterTitleColor(titleColor)
    this.setRightTitle(titleRightStr)
    this.setRightTitleColor(titleRightColor)
    this.setRightIcon(rightIcon)
    this.getBaseToolBar().setNavigationOnClickListener { onBack.invoke(this) }
    this.getLeftTitle().setOnClickListener { onBack.invoke(this) }
    return this
}
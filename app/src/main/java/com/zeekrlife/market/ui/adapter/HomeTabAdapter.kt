package com.zeekrlife.market.ui.adapter

import android.os.Build.VERSION_CODES
import android.view.Gravity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.zeekrlife.common.ext.dp
import com.zeekrlife.common.ext.dp2px
import com.zeekrlife.common.ext.getColorExt
import com.zeekrlife.common.ext.getUINightMode
import com.zeekrlife.market.R
import com.zeekrlife.market.app.widget.verticaltablayout.adapter.TabAdapter
import com.zeekrlife.market.app.widget.verticaltablayout.widget.ITabView
import com.zeekrlife.market.app.widget.verticaltablayout.widget.QTabView
import com.zeekrlife.market.data.response.HomeItemCategoryBean

class HomeTabAdapter(var mActivity: AppCompatActivity, var list: MutableList<HomeItemCategoryBean>, var isTab: Boolean) : TabAdapter {

    /**
     * 返回数量
     */
    override fun getCount(): Int {
        return list.size
    }

    /**
     * 返回角标
     */
    override fun getBadge(position: Int): ITabView.TabBadge? {
        return if (!isTab && position == 0) {
            ITabView.TabBadge.Builder()
                .setExactMode(false)
                .setShowShadow(false)
                .setBadgeTextSize(mActivity.resources.getDimension(R.dimen.tab_app_red_point_text_size))
                .setBadgeTextColor(getColorExt(R.color.setting_red_point_text_color))
                .setBackgroundColor(getColorExt(R.color.setting_red_point_color))
                .setBadgeGravity(Gravity.END or Gravity.TOP)
                .setGravityOffset(
                    dp2px(mActivity,mActivity.resources.getDimension(R.dimen.tab_app_red_point_offset_x)),
                    dp2px(mActivity,mActivity.resources.getDimension(R.dimen.tab_app_red_point_offset_y))
                ).build()
        } else if (!isTab && position == 1) {
            ITabView.TabBadge.Builder()
                .setExactMode(false)
                .setShowShadow(false)
                .setBadgeTextSize(10f.dp)
                .setBackgroundColor(getColorExt(R.color.setting_red_point_color))
                .setBadgeGravity(Gravity.END or Gravity.TOP)
                .setGravityOffset(
                    dp2px(mActivity,mActivity.resources.getDimension(R.dimen.tab_setting_red_point_offset_x)),
                    dp2px(mActivity,mActivity.resources.getDimension(R.dimen.tab_setting_red_point_offset_y))
                ).build()
        } else {
            null
        }
    }

    /**
     * 返回图标
     */
    override fun getIcon(position: Int): ITabView.TabIcon? {
        return null
    }

    /**
     * 返回标题
     */
    @RequiresApi(VERSION_CODES.M)
    override fun getTitle(position: Int): ITabView.TabTitle {
        val tabTitle = ITabView.TabTitle.Builder()
        tabTitle.setContent(list[position].categoryName)
            .setTextSize(mActivity.resources.getDimension(R.dimen.tab_title_size).toInt())
            .setTextColor(

                mActivity.getColor(R.color.theme_main_text_color),
                mActivity.getColor(R.color.theme_main_text_color),
            ).setLeftMargin(dp2px(mActivity,mActivity.resources.getDimension(R.dimen.tab_title_margin_left)))
            .setImageWidth(dp2px(mActivity,mActivity.resources.getDimension(R.dimen.tab_image_width)))

        when {
            isTab -> {
                if (position == 0 && list[position].icon.isNullOrEmpty()) {
                    tabTitle.setImageUrl(QTabView.ICON_RECOMMEND_UNABLE)
                } else {
                    if (getUINightMode()) {
                        tabTitle.setImageUrl(list[position].nightIcon ?: "")
                    } else {
                        tabTitle.setImageUrl(list[position].icon ?: "")
                    }
                }
            }
            position == 0 -> {
                tabTitle.setImageUrl(QTabView.ICON_LOGO_UNABLE)
            }
            else -> {
                tabTitle.setImageUrl(QTabView.ICON_SETTING_UNABLE)
            }
        }
        return tabTitle.build()
    }

    /**
     * 返回背景
     */
    override fun getBackground(position: Int): Int {
        return 0
    }
}
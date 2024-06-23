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
import com.zeekrlife.market.widget.verticaltablayout.adapter.TVTabAdapter
import com.zeekrlife.market.widget.verticaltablayout.widget.ITVTabView

class HomeTVTabAdapter(
    var mActivity: AppCompatActivity,
    var list: MutableList<HomeItemCategoryBean>,
    var isTab: Boolean
) :
    TVTabAdapter {

    override fun getCount(): Int {
        return list.size
    }

    override fun getBadge(position: Int): ITVTabView.TabBadge? {
        return if (!isTab && position == 0) {
            ITVTabView.TabBadge.Builder()
                .setExactMode(false)
                .setBadgeTextSize(mActivity.resources.getDimension(R.dimen.tv_badge_text_size))
                .setBadgeTextColor(getColorExt(R.color.setting_red_point_text_color))
                .setBackgroundColor(getColorExt(R.color.setting_red_point_color))
                .setBadgeGravity(Gravity.END or Gravity.TOP)
                .setGravityOffset(
                    dp2px(mActivity.resources.getDimension(R.dimen.tab_setting_red_point_offset_x)),
                    dp2px(mActivity.resources.getDimension(R.dimen.tab_setting_red_point_offset_y))
                )
                .build()
        } else if (!isTab && position == 1) {
            ITVTabView.TabBadge.Builder()
                .setBadgeTextSize(10f.dp)
                .setBackgroundColor(getColorExt(R.color.setting_red_point_color))
                .setBadgeGravity(Gravity.END or Gravity.TOP)
                .setGravityOffset(
                    dp2px(mActivity.resources.getDimension(R.dimen.tv_112)),
                    dp2px(mActivity.resources.getDimension(R.dimen.tv_31))
                ).build()
        } else {
            null
        }
    }

    override fun getIcon(position: Int): ITVTabView.TabIcon? {
        return null
    }

    @RequiresApi(VERSION_CODES.M)
    override fun getTitle(position: Int): ITVTabView.TabTitle {
        val tabTitle = ITVTabView.TabTitle.Builder()
        tabTitle.setContent(list[position].categoryName)
            .setTextSize(mActivity.resources.getDimension(R.dimen.tv_menu_text_size).toInt())
            .setTextColor(
                mActivity.getColor(R.color.theme_main_text_color),
                mActivity.getColor(R.color.theme_main_text_color),
            )

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

    override fun getBackground(position: Int): Int {
        return R.color.transparent
    }
}
package com.zeekrlife.market.app.widget.verticaltablayout.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import q.rorbin.badgeview.QBadgeView;



public class TabBadgeView extends QBadgeView {
    private TabBadgeView(Context context) {
        super(context);
    }

    public static TabBadgeView bindTab(TabView tab) {
        TabBadgeView badge = null;
        for (int i = 0; i < tab.getChildCount(); i++) {
            View child = tab.getChildAt(i);
            if (child != null && child instanceof TabBadgeView) {
                badge = (TabBadgeView) child;
                break;
            }
        }
        if (badge == null) {
            badge = new TabBadgeView(tab.getContext());
            tab.addView(badge, new TabView.LayoutParams(TabView.LayoutParams.MATCH_PARENT, TabView.LayoutParams.MATCH_PARENT));
        }
        badge.mTargetView = tab;
        return badge;
    }

    /**
     * 根据指定条件将当前视图从窗口中移出或加入。
     * 如果 screen 为 true，则将当前视图添加到 mActivityRoot 中；
     * 如果 screen 为 false，并且 mTargetView 是 TabView 的实例，则将当前视图添加到 TabView 中；
     * 否则，尝试将当前视图绑定到 mTargetView。
     *
     * @param screen 指示是否应将当前视图从窗口中显示出来的布尔值。
     */
    @Override
    protected void screenFromWindow(boolean screen) {
        // 如果当前视图已有父视图，则先从其父视图中移除
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        // 根据 screen 参数决定如何处理当前视图
        if (screen) {
            // 将当前视图添加到 mActivityRoot 中，使其显示出来
            mActivityRoot.addView(this, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
        } else {
            // 如果 mTargetView 是 TabView 的实例，则将当前视图添加到 TabView 中
            if (mTargetView instanceof TabView) {
                ((TabView) mTargetView).addView(this,
                        new TabView.LayoutParams(TabView.LayoutParams.MATCH_PARENT,
                                TabView.LayoutParams.MATCH_PARENT));
            } else {
                // 否则，尝试将当前视图绑定到 mTargetView
                bindTarget(mTargetView);
            }
        }
    }

}

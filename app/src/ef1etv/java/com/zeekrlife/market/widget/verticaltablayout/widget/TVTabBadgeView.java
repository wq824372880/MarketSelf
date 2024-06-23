package com.zeekrlife.market.widget.verticaltablayout.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import q.rorbin.badgeview.QBadgeView;



public class TVTabBadgeView extends QBadgeView {
    private TVTabBadgeView(Context context) {
        super(context);
    }

    public static TVTabBadgeView bindTab(AbstractTVTabView tab) {
        TVTabBadgeView badge = null;
        for (int i = 0; i < tab.getChildCount(); i++) {
            View child = tab.getChildAt(i);
            if (child != null && child instanceof TVTabBadgeView) {
                badge = (TVTabBadgeView) child;
                break;
            }
        }
        if (badge == null) {
            badge = new TVTabBadgeView(tab.getContext());
            tab.addView(badge, new AbstractTVTabView.LayoutParams(AbstractTVTabView.LayoutParams.MATCH_PARENT, AbstractTVTabView.LayoutParams.MATCH_PARENT));
        }
        badge.mTargetView = tab;
        return badge;
    }

    @Override
    protected void screenFromWindow(boolean screen) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (screen) {
            mActivityRoot.addView(this, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
        } else {
            if (mTargetView instanceof AbstractTVTabView) {
                ((AbstractTVTabView) mTargetView).addView(this,
                        new AbstractTVTabView.LayoutParams(AbstractTVTabView.LayoutParams.MATCH_PARENT,
                                AbstractTVTabView.LayoutParams.MATCH_PARENT));
            } else {
                bindTarget(mTargetView);
            }
        }
    }
}

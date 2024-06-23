package com.zeekrlife.market.widget.verticaltablayout.widget;

import android.content.Context;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeekrlife.market.utils.FocusBorderFrameLayout;

import q.rorbin.badgeview.Badge;

public abstract class AbstractTVTabView extends FocusBorderFrameLayout implements Checkable, ITVTabView {

    public AbstractTVTabView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setScaleValue1(1.05F);
    }

    @Override
    public AbstractTVTabView getTabView() {
        return this;
    }

    @Deprecated
    public abstract ImageView getIconView();

    public abstract TextView getTitleView();

    public abstract Badge getBadgeView();
}

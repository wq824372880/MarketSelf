package com.zeekrlife.market.widget.verticaltablayout.adapter;


import com.zeekrlife.market.widget.verticaltablayout.widget.AbstractTVTabView;

public interface TVTabAdapter {
    int getCount();

    AbstractTVTabView.TabBadge getBadge(int position);

    AbstractTVTabView.TabIcon getIcon(int position);

    AbstractTVTabView.TabTitle getTitle(int position);

    int getBackground(int position);
}

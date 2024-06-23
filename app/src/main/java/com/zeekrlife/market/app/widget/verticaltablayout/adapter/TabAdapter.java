package com.zeekrlife.market.app.widget.verticaltablayout.adapter;


import com.zeekrlife.market.app.widget.verticaltablayout.widget.TabView;

public interface TabAdapter {
    int getCount();

    TabView.TabBadge getBadge(int position);

    TabView.TabIcon getIcon(int position);

    TabView.TabTitle getTitle(int position);

    int getBackground(int position);
}

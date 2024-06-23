package com.zeekrlife.market.app.widget.verticaltablayout.adapter;


import com.zeekrlife.market.app.widget.verticaltablayout.widget.TabView;


public abstract class SimpleTabAdapter implements TabAdapter {
    @Override
    public abstract int getCount();

    /**
     * 获取指定位置的标签徽章。
     *
     * @param position 标签的位置索引。
     * @return 返回该位置上的标签徽章对象，如果不存在则返回null。
     */
    @Override
    public TabView.TabBadge getBadge(int position) {
        return null;
    }

    /**
     * 根据指定位置获取标签视图的图标。
     *
     * @param position 标签的位置索引。
     * @return 返回该位置处的图标对象，此实现返回null。
     */
    @Override
    public TabView.TabIcon getIcon(int position) {
        return null;
    }

    /**
     * 获取指定位置的标签标题。
     *
     * @param position 标签的位置索引。
     * @return 返回该位置上的标签标题对象，此实现返回null。
     */
    @Override
    public TabView.TabTitle getTitle(int position) {
        return null;
    }

    /**
     * 获取指定位置的背景色。
     *
     * @param position 指定位置的索引。
     * @return 返回该位置的背景色，此方法默认返回0。
     */
    @Override
    public int getBackground(int position) {
        return 0;
    }
}

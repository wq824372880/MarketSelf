package com.zeekrlife.net.api;

import java.lang.System;

/**
 * 分页帮助类
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u0016\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0005j\n\u0012\u0004\u0012\u00028\u0000\u0018\u0001`\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\bH&J\b\u0010\n\u001a\u00020\bH&\u00a8\u0006\u000b"}, d2 = {"Lcom/zeekrlife/net/api/BasePage;", "T", "", "()V", "getPageData", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "hasMore", "", "isEmpty", "isRefresh", "net_cs1eDebug"})
public abstract class BasePage<T extends java.lang.Object> {
    
    public BasePage() {
        super();
    }
    
    /**
     * 列表数据
     * @return ArrayList<T>
     */
    @org.jetbrains.annotations.Nullable
    public abstract java.util.ArrayList<T> getPageData();
    
    /**
     * 是否是第一页数据
     */
    public abstract boolean isRefresh();
    
    /**
     * 数据是否为空
     */
    public abstract boolean isEmpty();
    
    /**
     * 是否还有更多数据
     */
    public abstract boolean hasMore();
}
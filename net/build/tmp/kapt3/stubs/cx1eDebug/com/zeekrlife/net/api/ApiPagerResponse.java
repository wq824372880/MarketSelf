package com.zeekrlife.net.api;

import java.lang.System;

/**
 * 描述　: 服务器返回的列表数据基类
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B9\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u001a\u0010\u0005\u001a\u0016\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0006j\n\u0012\u0004\u0012\u00028\u0000\u0018\u0001`\u0007\u0012\u0006\u0010\b\u001a\u00020\u0004\u0012\u0006\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0017\u001a\u00020\u0004H\u00c6\u0003J\u001d\u0010\u0018\u001a\u0016\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0006j\n\u0012\u0004\u0012\u00028\u0000\u0018\u0001`\u0007H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0004H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0004H\u00c6\u0003JK\u0010\u001b\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\u001c\b\u0002\u0010\u0005\u001a\u0016\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0006j\n\u0012\u0004\u0012\u00028\u0000\u0018\u0001`\u00072\b\b\u0002\u0010\b\u001a\u00020\u00042\b\b\u0002\u0010\t\u001a\u00020\u0004H\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u00d6\u0003J\u0010\u0010 \u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0006H\u0016J\b\u0010!\u001a\u00020\u001dH\u0016J\t\u0010\"\u001a\u00020\u0004H\u00d6\u0001J\b\u0010#\u001a\u00020\u001dH\u0016J\b\u0010$\u001a\u00020\u001dH\u0016J\t\u0010%\u001a\u00020&H\u00d6\u0001R.\u0010\u0005\u001a\u0016\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0006j\n\u0012\u0004\u0012\u00028\u0000\u0018\u0001`\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\b\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0010\"\u0004\b\u0014\u0010\u0012R\u001a\u0010\t\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012\u00a8\u0006\'"}, d2 = {"Lcom/zeekrlife/net/api/ApiPagerResponse;", "T", "Lcom/zeekrlife/net/api/BasePage;", "pageNum", "", "list", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "pageSize", "total", "(ILjava/util/ArrayList;II)V", "getList", "()Ljava/util/ArrayList;", "setList", "(Ljava/util/ArrayList;)V", "getPageNum", "()I", "setPageNum", "(I)V", "getPageSize", "setPageSize", "getTotal", "setTotal", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "getPageData", "hasMore", "hashCode", "isEmpty", "isRefresh", "toString", "", "net_cx1eDebug"})
public final class ApiPagerResponse<T extends java.lang.Object> extends com.zeekrlife.net.api.BasePage<T> {
    private int pageNum;
    @org.jetbrains.annotations.Nullable
    private java.util.ArrayList<T> list;
    private int pageSize;
    private int total;
    
    /**
     * 描述　: 服务器返回的列表数据基类
     */
    @org.jetbrains.annotations.NotNull
    public final com.zeekrlife.net.api.ApiPagerResponse<T> copy(int pageNum, @org.jetbrains.annotations.Nullable
    java.util.ArrayList<T> list, int pageSize, int total) {
        return null;
    }
    
    /**
     * 描述　: 服务器返回的列表数据基类
     */
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    /**
     * 描述　: 服务器返回的列表数据基类
     */
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * 描述　: 服务器返回的列表数据基类
     */
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public ApiPagerResponse(int pageNum, @org.jetbrains.annotations.Nullable
    java.util.ArrayList<T> list, int pageSize, int total) {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int getPageNum() {
        return 0;
    }
    
    public final void setPageNum(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.util.ArrayList<T> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.util.ArrayList<T> getList() {
        return null;
    }
    
    public final void setList(@org.jetbrains.annotations.Nullable
    java.util.ArrayList<T> p0) {
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int getPageSize() {
        return 0;
    }
    
    public final void setPageSize(int p0) {
    }
    
    public final int component4() {
        return 0;
    }
    
    public final int getTotal() {
        return 0;
    }
    
    public final void setTotal(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    @java.lang.Override
    public java.util.ArrayList<T> getPageData() {
        return null;
    }
    
    @java.lang.Override
    public boolean isRefresh() {
        return false;
    }
    
    @java.lang.Override
    public boolean isEmpty() {
        return false;
    }
    
    @java.lang.Override
    public boolean hasMore() {
        return false;
    }
}
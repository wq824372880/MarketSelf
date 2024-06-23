package com.zeekrlife.net.load;

import java.lang.System;

/**
 * 描述　: 请求失败，请求数据为空 状态类
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\'\b\u0086\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0007\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0001\u00a2\u0006\u0002\u0010\rJ\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0007H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\nH\u00c6\u0003J\t\u0010*\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0001H\u00c6\u0003JQ\u0010,\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00072\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00c6\u0001J\u0013\u0010-\u001a\u00020\n2\b\u0010.\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010/\u001a\u00020\u0007H\u00d6\u0001J\t\u00100\u001a\u00020\u0003H\u00d6\u0001R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001c\u0010\f\u001a\u0004\u0018\u00010\u0001X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u000b\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u000f\"\u0004\b\u001e\u0010\u0011R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0013\"\u0004\b \u0010\u0015R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$\u00a8\u00061"}, d2 = {"Lcom/zeekrlife/net/load/LoadStatusEntity;", "", "requestCode", "", "throwable", "", "errorCode", "", "errorMessage", "isRefresh", "", "loadingType", "intentData", "(Ljava/lang/String;Ljava/lang/Throwable;ILjava/lang/String;ZILjava/lang/Object;)V", "getErrorCode", "()I", "setErrorCode", "(I)V", "getErrorMessage", "()Ljava/lang/String;", "setErrorMessage", "(Ljava/lang/String;)V", "getIntentData", "()Ljava/lang/Object;", "setIntentData", "(Ljava/lang/Object;)V", "()Z", "setRefresh", "(Z)V", "getLoadingType", "setLoadingType", "getRequestCode", "setRequestCode", "getThrowable", "()Ljava/lang/Throwable;", "setThrowable", "(Ljava/lang/Throwable;)V", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "toString", "net_ef1etvDebug"})
public final class LoadStatusEntity {
    @org.jetbrains.annotations.NotNull
    private java.lang.String requestCode;
    @org.jetbrains.annotations.NotNull
    private java.lang.Throwable throwable;
    private int errorCode;
    @org.jetbrains.annotations.NotNull
    private java.lang.String errorMessage;
    private boolean isRefresh;
    private int loadingType;
    @org.jetbrains.annotations.Nullable
    private java.lang.Object intentData;
    
    /**
     * 描述　: 请求失败，请求数据为空 状态类
     */
    @org.jetbrains.annotations.NotNull
    public final com.zeekrlife.net.load.LoadStatusEntity copy(@org.jetbrains.annotations.NotNull
    java.lang.String requestCode, @org.jetbrains.annotations.NotNull
    java.lang.Throwable throwable, int errorCode, @org.jetbrains.annotations.NotNull
    java.lang.String errorMessage, boolean isRefresh, @LoadingType
    int loadingType, @org.jetbrains.annotations.Nullable
    java.lang.Object intentData) {
        return null;
    }
    
    /**
     * 描述　: 请求失败，请求数据为空 状态类
     */
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    /**
     * 描述　: 请求失败，请求数据为空 状态类
     */
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * 描述　: 请求失败，请求数据为空 状态类
     */
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public LoadStatusEntity(@org.jetbrains.annotations.NotNull
    java.lang.String requestCode, @org.jetbrains.annotations.NotNull
    java.lang.Throwable throwable, int errorCode, @org.jetbrains.annotations.NotNull
    java.lang.String errorMessage, boolean isRefresh, @LoadingType
    int loadingType, @org.jetbrains.annotations.Nullable
    java.lang.Object intentData) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getRequestCode() {
        return null;
    }
    
    public final void setRequestCode(@org.jetbrains.annotations.NotNull
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.Throwable component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.Throwable getThrowable() {
        return null;
    }
    
    public final void setThrowable(@org.jetbrains.annotations.NotNull
    java.lang.Throwable p0) {
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int getErrorCode() {
        return 0;
    }
    
    public final void setErrorCode(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getErrorMessage() {
        return null;
    }
    
    public final void setErrorMessage(@org.jetbrains.annotations.NotNull
    java.lang.String p0) {
    }
    
    public final boolean component5() {
        return false;
    }
    
    public final boolean isRefresh() {
        return false;
    }
    
    public final void setRefresh(boolean p0) {
    }
    
    public final int component6() {
        return 0;
    }
    
    public final int getLoadingType() {
        return 0;
    }
    
    public final void setLoadingType(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getIntentData() {
        return null;
    }
    
    public final void setIntentData(@org.jetbrains.annotations.Nullable
    java.lang.Object p0) {
    }
}
package com.zeekrlife.net.api;

import java.lang.System;

/**
 * 描述　:
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\t\u001a\u00020\n2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\fJ\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0004J\u0006\u0010\u0013\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/zeekrlife/net/api/NetHttpClient;", "", "()V", "devEnvParams", "Lcom/zeekrlife/net/api/NetEnvParams;", "headInterceptor", "Lcom/zeekrlife/net/api/HeadInterceptor;", "prodEnvParams", "testEnvParams", "addHttpHeadExt", "", "heads", "", "", "getDefaultOkHttpClient", "Lokhttp3/OkHttpClient$Builder;", "context", "Landroid/content/Context;", "envParams", "getNetEnvParams", "net_bx1eDebug"})
public final class NetHttpClient {
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.api.NetHttpClient INSTANCE = null;
    private static com.zeekrlife.net.api.HeadInterceptor headInterceptor;
    private static final com.zeekrlife.net.api.NetEnvParams devEnvParams = null;
    private static final com.zeekrlife.net.api.NetEnvParams testEnvParams = null;
    private static final com.zeekrlife.net.api.NetEnvParams prodEnvParams = null;
    
    private NetHttpClient() {
        super();
    }
    
    /**
     * 获取环境参数
     */
    @org.jetbrains.annotations.NotNull
    public final com.zeekrlife.net.api.NetEnvParams getNetEnvParams() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final okhttp3.OkHttpClient.Builder getDefaultOkHttpClient(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.zeekrlife.net.api.NetEnvParams envParams) {
        return null;
    }
    
    /**
     * @param heads
     */
    public final void addHttpHeadExt(@org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.String> heads) {
    }
}
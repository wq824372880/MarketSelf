package com.zeekrlife.net.api;

import java.lang.System;

/**
 * Created by Qiang.Wang21 on 2022/6/24.
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\b\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u0003H\u0002J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0017JB\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00172\u0006\u0010\u0018\u001a\u00020\u00032\b\u0010\u0019\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u00032\b\u0010\u001c\u001a\u0004\u0018\u00010\u0003H\u0007J\u001a\u0010\u001d\u001a\u00020\u00032\b\u0010\u001e\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u001f\u001a\u00020 H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\'\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\n8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f\u00a8\u0006!"}, d2 = {"Lcom/zeekrlife/net/api/HeadInterceptor;", "Lokhttp3/Interceptor;", "APP_ID", "", "APP_SECRET", "(Ljava/lang/String;Ljava/lang/String;)V", "UTF_8", "Ljava/nio/charset/Charset;", "kotlin.jvm.PlatformType", "headsExt", "", "getHeadsExt", "()Ljava/util/Map;", "headsExt$delegate", "Lkotlin/Lazy;", "getTimes", "", "getX_Api_Signature_Nonce", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "sign", "", "methodParam", "uriPathParam", "acceptParam", "requestParameters", "rb", "subPath", "path", "num", "", "net_ef1etvDebug"})
public final class HeadInterceptor implements okhttp3.Interceptor {
    private final java.lang.String APP_ID = null;
    private final java.lang.String APP_SECRET = null;
    private final java.nio.charset.Charset UTF_8 = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy headsExt$delegate = null;
    
    public HeadInterceptor(@org.jetbrains.annotations.NotNull()
    java.lang.String APP_ID, @org.jetbrains.annotations.NotNull()
    java.lang.String APP_SECRET) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.String> getHeadsExt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    @java.lang.Override()
    public okhttp3.Response intercept(@org.jetbrains.annotations.NotNull()
    okhttp3.Interceptor.Chain chain) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @kotlin.jvm.Throws(exceptionClasses = {java.security.NoSuchAlgorithmException.class, java.security.InvalidKeyException.class})
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    public final java.util.Map<java.lang.String, java.lang.String> sign(@org.jetbrains.annotations.NotNull()
    java.lang.String methodParam, @org.jetbrains.annotations.Nullable()
    java.lang.String uriPathParam, @org.jetbrains.annotations.NotNull()
    java.lang.String acceptParam, @org.jetbrains.annotations.Nullable()
    java.lang.String requestParameters, @org.jetbrains.annotations.Nullable()
    java.lang.String rb) {
        return null;
    }
    
    private final long getTimes() {
        return 0L;
    }
    
    private final java.lang.String getX_Api_Signature_Nonce() {
        return null;
    }
    
    private final java.lang.String subPath(java.lang.String path, int num) {
        return null;
    }
}
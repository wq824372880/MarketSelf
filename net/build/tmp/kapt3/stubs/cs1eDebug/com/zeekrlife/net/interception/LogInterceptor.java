package com.zeekrlife.net.interception;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u0000 \u00182\u00020\u0001:\u0002\u0018\u0019B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J&\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\"\u0010\u0012\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/zeekrlife/net/interception/LogInterceptor;", "Lokhttp3/Interceptor;", "()V", "mPrinter", "Lcom/zeekrlife/net/interception/logging/FormatPrinter;", "printLevel", "Lcom/zeekrlife/net/interception/LogInterceptor$Level;", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "parseContent", "", "responseBody", "Lokhttp3/ResponseBody;", "encoding", "clone", "Lokio/Buffer;", "printResult", "request", "Lokhttp3/Request;", "response", "logResponse", "", "Companion", "Level", "net_cs1eDebug"})
public final class LogInterceptor implements okhttp3.Interceptor {
    private final com.zeekrlife.net.interception.logging.FormatPrinter mPrinter = null;
    private final com.zeekrlife.net.interception.LogInterceptor.Level printLevel = com.zeekrlife.net.interception.LogInterceptor.Level.ALL;
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.interception.LogInterceptor.Companion Companion = null;
    
    public LogInterceptor() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    @java.lang.Override
    public okhttp3.Response intercept(@org.jetbrains.annotations.NotNull
    okhttp3.Interceptor.Chain chain) throws java.io.IOException {
        return null;
    }
    
    /**
     * 打印响应结果
     *
     * @param request     [Request]
     * @param response    [Response]
     * @param logResponse 是否打印响应结果
     * @return 解析后的响应结果
     * @throws IOException
     */
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    private final java.lang.String printResult(okhttp3.Request request, okhttp3.Response response, boolean logResponse) throws java.io.IOException {
        return null;
    }
    
    /**
     * 解析服务器响应的内容
     *
     * @param responseBody [ResponseBody]
     * @param encoding     编码类型
     * @param clone        克隆后的服务器响应内容
     * @return 解析后的响应结果
     */
    private final java.lang.String parseContent(okhttp3.ResponseBody responseBody, java.lang.String encoding, okio.Buffer clone) {
        return null;
    }
    
    @kotlin.jvm.JvmStatic
    public static final boolean isJson(@org.jetbrains.annotations.Nullable
    okhttp3.MediaType mediaType) {
        return false;
    }
    
    @kotlin.jvm.JvmStatic
    public static final boolean isXml(@org.jetbrains.annotations.Nullable
    okhttp3.MediaType mediaType) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/zeekrlife/net/interception/LogInterceptor$Level;", "", "(Ljava/lang/String;I)V", "NONE", "REQUEST", "RESPONSE", "ALL", "net_cs1eDebug"})
    public static enum Level {
        /*public static final*/ NONE /* = new NONE() */,
        /*public static final*/ REQUEST /* = new REQUEST() */,
        /*public static final*/ RESPONSE /* = new RESPONSE() */,
        /*public static final*/ ALL /* = new ALL() */;
        
        Level() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0010\u0010\u000b\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0012\u0010\f\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0007J\u0010\u0010\r\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0010\u0010\u000e\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0010\u0010\u000f\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0012\u0010\u0010\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0007J\u000e\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0013\u00a8\u0006\u0014"}, d2 = {"Lcom/zeekrlife/net/interception/LogInterceptor$Companion;", "", "()V", "convertCharset", "", "charset", "Ljava/nio/charset/Charset;", "isForm", "", "mediaType", "Lokhttp3/MediaType;", "isHtml", "isJson", "isParseable", "isPlain", "isText", "isXml", "parseParams", "request", "Lokhttp3/Request;", "net_cs1eDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * 解析请求服务器的请求参数
         *
         * @param request [Request]
         * @return 解析后的请求信息
         * @throws UnsupportedEncodingException
         */
        @org.jetbrains.annotations.NotNull
        @kotlin.jvm.Throws(exceptionClasses = {java.io.UnsupportedEncodingException.class})
        public final java.lang.String parseParams(@org.jetbrains.annotations.NotNull
        okhttp3.Request request) throws java.io.UnsupportedEncodingException {
            return null;
        }
        
        /**
         * 是否可以解析
         *
         * @param mediaType [MediaType]
         * @return `true` 为可以解析
         */
        public final boolean isParseable(@org.jetbrains.annotations.Nullable
        okhttp3.MediaType mediaType) {
            return false;
        }
        
        public final boolean isText(@org.jetbrains.annotations.Nullable
        okhttp3.MediaType mediaType) {
            return false;
        }
        
        public final boolean isPlain(@org.jetbrains.annotations.Nullable
        okhttp3.MediaType mediaType) {
            return false;
        }
        
        @kotlin.jvm.JvmStatic
        public final boolean isJson(@org.jetbrains.annotations.Nullable
        okhttp3.MediaType mediaType) {
            return false;
        }
        
        @kotlin.jvm.JvmStatic
        public final boolean isXml(@org.jetbrains.annotations.Nullable
        okhttp3.MediaType mediaType) {
            return false;
        }
        
        public final boolean isHtml(@org.jetbrains.annotations.Nullable
        okhttp3.MediaType mediaType) {
            return false;
        }
        
        public final boolean isForm(@org.jetbrains.annotations.Nullable
        okhttp3.MediaType mediaType) {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String convertCharset(@org.jetbrains.annotations.Nullable
        java.nio.charset.Charset charset) {
            return null;
        }
    }
}
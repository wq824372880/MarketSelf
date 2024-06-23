package com.zeekrlife.net.interception.logging;

import java.lang.System;

/**
 * 描述　:
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&JH\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u000e\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u00102\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000eH&J\u0018\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u000eH&J\\\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u0014\u001a\u0004\u0018\u00010\u000e2\u000e\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u00102\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000eH&\u00a8\u0006\u0018"}, d2 = {"Lcom/zeekrlife/net/interception/logging/FormatPrinter;", "", "printFileRequest", "", "request", "Lokhttp3/Request;", "printFileResponse", "chainMs", "", "isSuccessful", "", "code", "", "headers", "", "segments", "", "message", "responseUrl", "printJsonRequest", "bodyString", "printJsonResponse", "contentType", "Lokhttp3/MediaType;", "net_cs1eDebug"})
public abstract interface FormatPrinter {
    
    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 可以解析的情况
     *
     * @param request
     * @param bodyString 发送给服务器的请求体中的数据(已解析)
     */
    public abstract void printJsonRequest(@org.jetbrains.annotations.NotNull
    okhttp3.Request request, @org.jetbrains.annotations.NotNull
    java.lang.String bodyString);
    
    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 为 `null` 或不可解析的情况
     *
     * @param request
     */
    public abstract void printFileRequest(@org.jetbrains.annotations.NotNull
    okhttp3.Request request);
    
    /**
     * 打印网络响应信息, 当网络响应时 {[okhttp3.ResponseBody]} 可以解析的情况
     *
     * @param chainMs      服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code         响应码
     * @param headers      请求头
     * @param contentType  服务器返回数据的数据类型
     * @param bodyString   服务器返回的数据(已解析)
     * @param segments     域名后面的资源地址
     * @param message      响应信息
     * @param responseUrl  请求地址
     */
    public abstract void printJsonResponse(long chainMs, boolean isSuccessful, int code, @org.jetbrains.annotations.NotNull
    java.lang.String headers, @org.jetbrains.annotations.Nullable
    okhttp3.MediaType contentType, @org.jetbrains.annotations.Nullable
    java.lang.String bodyString, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> segments, @org.jetbrains.annotations.NotNull
    java.lang.String message, @org.jetbrains.annotations.NotNull
    java.lang.String responseUrl);
    
    /**
     * 打印网络响应信息, 当网络响应时 {[okhttp3.ResponseBody]} 为 `null` 或不可解析的情况
     *
     * @param chainMs      服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code         响应码
     * @param headers      请求头
     * @param segments     域名后面的资源地址
     * @param message      响应信息
     * @param responseUrl  请求地址
     */
    public abstract void printFileResponse(long chainMs, boolean isSuccessful, int code, @org.jetbrains.annotations.NotNull
    java.lang.String headers, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> segments, @org.jetbrains.annotations.NotNull
    java.lang.String message, @org.jetbrains.annotations.NotNull
    java.lang.String responseUrl);
}
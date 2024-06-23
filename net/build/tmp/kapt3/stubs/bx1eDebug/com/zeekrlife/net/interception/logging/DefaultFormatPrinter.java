package com.zeekrlife.net.interception.logging;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00192\u00020\u0001:\u0001\u0019B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016JH\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u000e\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u00112\u0006\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u000fH\u0016J\u0018\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u000fH\u0016J\\\u0010\u0016\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0015\u001a\u0004\u0018\u00010\u000f2\u000e\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u00112\u0006\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u000fH\u0016\u00a8\u0006\u001a"}, d2 = {"Lcom/zeekrlife/net/interception/logging/DefaultFormatPrinter;", "Lcom/zeekrlife/net/interception/logging/FormatPrinter;", "()V", "printFileRequest", "", "request", "Lokhttp3/Request;", "printFileResponse", "chainMs", "", "isSuccessful", "", "code", "", "headers", "", "segments", "", "message", "responseUrl", "printJsonRequest", "bodyString", "printJsonResponse", "contentType", "Lokhttp3/MediaType;", "Companion", "net_bx1eDebug"})
public final class DefaultFormatPrinter implements com.zeekrlife.net.interception.logging.FormatPrinter {
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.interception.logging.DefaultFormatPrinter.Companion Companion = null;
    private static final java.lang.String TAG = "HttpLog";
    private static final java.lang.String LINE_SEPARATOR = null;
    private static final java.lang.String DOUBLE_SEPARATOR = null;
    private static final java.lang.String[] OMITTED_RESPONSE = null;
    private static final java.lang.String[] OMITTED_REQUEST = null;
    private static final java.lang.String N = "\n";
    private static final java.lang.String T = "\t";
    private static final java.lang.String REQUEST_UP_LINE = "   \u250c\u2500\u2500\u2500\u2500\u2500\u2500 Request \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
    private static final java.lang.String END_LINE = "   \u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
    private static final java.lang.String RESPONSE_UP_LINE = "   \u250c\u2500\u2500\u2500\u2500\u2500\u2500 Response \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
    private static final java.lang.String BODY_TAG = "Body:";
    private static final java.lang.String URL_TAG = "URL: ";
    private static final java.lang.String METHOD_TAG = "Method: @";
    private static final java.lang.String HEADERS_TAG = "Headers:";
    private static final java.lang.String STATUS_CODE_TAG = "Status Code: ";
    private static final java.lang.String RECEIVED_TAG = "Received in: ";
    private static final java.lang.String CORNER_UP = "\u250c ";
    private static final java.lang.String CORNER_BOTTOM = "\u2514 ";
    private static final java.lang.String CENTER_LINE = "\u251c ";
    private static final java.lang.String DEFAULT_LINE = "\u2502 ";
    private static final java.lang.String[] ARMS = {"-A-", "-R-", "-M-", "-S-"};
    private static final java.lang.ThreadLocal<java.lang.Integer> last = null;
    
    public DefaultFormatPrinter() {
        super();
    }
    
    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 可以解析的情况
     *
     * @param request
     * @param bodyString
     */
    @java.lang.Override
    public void printJsonRequest(@org.jetbrains.annotations.NotNull
    okhttp3.Request request, @org.jetbrains.annotations.NotNull
    java.lang.String bodyString) {
    }
    
    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 为 `null` 或不可解析的情况
     *
     * @param request
     */
    @java.lang.Override
    public void printFileRequest(@org.jetbrains.annotations.NotNull
    okhttp3.Request request) {
    }
    
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
    @java.lang.Override
    public void printJsonResponse(long chainMs, boolean isSuccessful, int code, @org.jetbrains.annotations.NotNull
    java.lang.String headers, @org.jetbrains.annotations.Nullable
    okhttp3.MediaType contentType, @org.jetbrains.annotations.Nullable
    java.lang.String bodyString, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> segments, @org.jetbrains.annotations.NotNull
    java.lang.String message, @org.jetbrains.annotations.NotNull
    java.lang.String responseUrl) {
    }
    
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
    @java.lang.Override
    public void printFileResponse(long chainMs, boolean isSuccessful, int code, @org.jetbrains.annotations.NotNull
    java.lang.String headers, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> segments, @org.jetbrains.annotations.NotNull
    java.lang.String message, @org.jetbrains.annotations.NotNull
    java.lang.String responseUrl) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001f\u001a\u00020\u0005H\u0002J\u0010\u0010 \u001a\u00020\u00052\u0006\u0010!\u001a\u00020\u0005H\u0002J\u001d\u0010\"\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00042\u0006\u0010#\u001a\u00020$H\u0002\u00a2\u0006\u0002\u0010%JM\u0010&\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00042\u0006\u0010!\u001a\u00020\u00052\u0006\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u001e2\u0006\u0010*\u001a\u00020+2\u000e\u0010,\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050-2\u0006\u0010.\u001a\u00020\u0005H\u0002\u00a2\u0006\u0002\u0010/J\u0010\u00100\u001a\u00020\u00052\u0006\u00101\u001a\u00020+H\u0002J\u0010\u00102\u001a\u00020+2\u0006\u00103\u001a\u00020\u0005H\u0002J-\u00104\u001a\u0002052\u0006\u00106\u001a\u00020\u00052\u000e\u00107\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00042\u0006\u00108\u001a\u00020+H\u0002\u00a2\u0006\u0002\u00109J\u0010\u0010:\u001a\u00020\u00052\u0006\u00106\u001a\u00020\u0005H\u0002J\u0018\u0010;\u001a\u00020\u00052\u000e\u0010,\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050-H\u0002R\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\n \u0010*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0013\u001a\u0010\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00050\u00050\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u001e\u0010\u0014\u001a\u0010\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00050\u00050\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u000e\u0010\u0015\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006<"}, d2 = {"Lcom/zeekrlife/net/interception/logging/DefaultFormatPrinter$Companion;", "", "()V", "ARMS", "", "", "[Ljava/lang/String;", "BODY_TAG", "CENTER_LINE", "CORNER_BOTTOM", "CORNER_UP", "DEFAULT_LINE", "DOUBLE_SEPARATOR", "END_LINE", "HEADERS_TAG", "LINE_SEPARATOR", "kotlin.jvm.PlatformType", "METHOD_TAG", "N", "OMITTED_REQUEST", "OMITTED_RESPONSE", "RECEIVED_TAG", "REQUEST_UP_LINE", "RESPONSE_UP_LINE", "STATUS_CODE_TAG", "T", "TAG", "URL_TAG", "last", "Ljava/lang/ThreadLocal;", "", "computeKey", "dotHeaders", "header", "getRequest", "request", "Lokhttp3/Request;", "(Lokhttp3/Request;)[Ljava/lang/String;", "getResponse", "tookMs", "", "code", "isSuccessful", "", "segments", "", "message", "(Ljava/lang/String;JIZLjava/util/List;Ljava/lang/String;)[Ljava/lang/String;", "getTag", "isRequest", "isEmpty", "line", "logLines", "", "tag", "lines", "withLineSize", "(Ljava/lang/String;[Ljava/lang/String;Z)V", "resolveTag", "slashSegments", "net_bx1eDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        private final boolean isEmpty(java.lang.String line) {
            return false;
        }
        
        /**
         * 对 `lines` 中的信息进行逐行打印
         *
         * @param tag
         * @param lines
         * @param withLineSize 为 `true` 时, 每行的信息长度不会超过110, 超过则自动换行
         */
        private final void logLines(java.lang.String tag, java.lang.String[] lines, boolean withLineSize) {
        }
        
        private final java.lang.String computeKey() {
            return null;
        }
        
        /**
         * 此方法是为了解决在 AndroidStudio v3.1 以上 Logcat 输出的日志无法对齐的问题
         *
         *
         * 此问题引起的原因, 据 JessYan 猜测, 可能是因为 AndroidStudio v3.1 以上将极短时间内以相同 tag 输出多次的 log 自动合并为一次输出
         * 导致本来对称的输出日志, 出现不对称的问题
         * AndroidStudio v3.1 此次对输出日志的优化, 不小心使市面上所有具有日志格式化输出功能的日志框架无法正常工作
         * 现在暂时能想到的解决方案有两个: 1. 改变每行的 tag (每行 tag 都加一个可变化的 token) 2. 延迟每行日志打印的间隔时间
         *
         *
         * [.resolveTag] 使用第一种解决方案
         *
         * @param tag
         */
        private final java.lang.String resolveTag(java.lang.String tag) {
            return null;
        }
        
        private final java.lang.String[] getRequest(okhttp3.Request request) {
            return null;
        }
        
        private final java.lang.String[] getResponse(java.lang.String header, long tookMs, int code, boolean isSuccessful, java.util.List<java.lang.String> segments, java.lang.String message) {
            return null;
        }
        
        private final java.lang.String slashSegments(java.util.List<java.lang.String> segments) {
            return null;
        }
        
        /**
         * 对 `header` 按规定的格式进行处理
         *
         * @param header
         * @return
         */
        private final java.lang.String dotHeaders(java.lang.String header) {
            return null;
        }
        
        private final java.lang.String getTag(boolean isRequest) {
            return null;
        }
    }
}
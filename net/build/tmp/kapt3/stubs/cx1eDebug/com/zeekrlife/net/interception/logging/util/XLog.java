package com.zeekrlife.net.interception.logging.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0016\n\u0002\u0010\u0011\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u001f\u001a\u00020 J\u0010\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u001a\u0010\u001f\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u0006\u0010#\u001a\u00020 J\u0010\u0010#\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u001a\u0010#\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u0006\u0010$\u001a\u00020 J\u0010\u0010$\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u001a\u0010$\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u0014\u0010%\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u0002J\u0006\u0010&\u001a\u00020 J\u0010\u0010&\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u001a\u0010&\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u000e\u0010\'\u001a\u00020 2\u0006\u0010(\u001a\u00020\u000bJ\u0018\u0010\'\u001a\u00020 2\u0006\u0010(\u001a\u00020\u000b2\b\u0010\"\u001a\u0004\u0018\u00010\u0007J\u0010\u0010)\u001a\u00020 2\b\u0010*\u001a\u0004\u0018\u00010\u0007J\u001a\u0010)\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u0010*\u001a\u0004\u0018\u00010\u0007J*\u0010+\u001a\u00020 2\u0006\u0010,\u001a\u00020\u00042\b\u0010\"\u001a\u0004\u0018\u00010\u00072\u0006\u0010-\u001a\u00020\u00042\b\u0010!\u001a\u0004\u0018\u00010\u0001J*\u0010.\u001a\u00020 2\u0006\u0010,\u001a\u00020\u00042\b\u0010\"\u001a\u0004\u0018\u00010\u00072\u0006\u0010-\u001a\u00020\u00042\b\u0010/\u001a\u0004\u0018\u00010\u0007J,\u00100\u001a\u00020 2\u0006\u0010,\u001a\u00020\u00042\b\u00101\u001a\u0004\u0018\u00010\u00072\u0006\u0010-\u001a\u00020\u00042\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u0002J\b\u00102\u001a\u00020 H\u0002J\u0006\u00103\u001a\u00020 J\u0006\u00104\u001a\u00020 J\u0010\u00104\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u001a\u00104\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u0006\u00105\u001a\u00020 J\u0010\u00105\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001J\u001a\u00105\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001J1\u00106\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u0007072\u0006\u0010-\u001a\u00020\u00042\b\u00101\u001a\u0004\u0018\u00010\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u0002\u00a2\u0006\u0002\u00108J\u0010\u00109\u001a\u00020 2\b\u00109\u001a\u0004\u0018\u00010\u0007J\u001a\u00109\u001a\u00020 2\b\u0010\"\u001a\u0004\u0018\u00010\u00072\b\u00109\u001a\u0004\u0018\u00010\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0017\u001a\u00020\u00188BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001b\u0010\u001c\u001a\u0004\b\u0019\u0010\u001aR\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006:"}, d2 = {"Lcom/zeekrlife/net/interception/logging/util/XLog;", "", "()V", "A", "", "D", "DEFAULT_MESSAGE", "", "E", "I", "IS_SHOW_LOG", "", "JSON", "KT", "NULL_TIPS", "STACK_TRACE_INDEX_4", "STACK_TRACE_INDEX_5", "STACK_TRACE_INDEX_7", "SUFFIX", "TAG_DEFAULT", "V", "W", "XML", "gson", "Lcom/google/gson/Gson;", "getGson", "()Lcom/google/gson/Gson;", "gson$delegate", "Lkotlin/Lazy;", "mGlobalTag", "mIsGlobalTagEmpty", "a", "", "msg", "tag", "d", "e", "getObjectsString", "i", "init", "isShowLog", "json", "jsonFormat", "logType", "type", "stackTraceIndex", "logXmlOrJson", "xmlOrJson", "printLog", "tagStr", "printStackTrace", "trace", "v", "w", "wrapperContent", "", "(ILjava/lang/String;Ljava/lang/Object;)[Ljava/lang/String;", "xml", "net_cx1eDebug"})
public final class XLog {
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.interception.logging.util.XLog INSTANCE = null;
    private static final kotlin.Lazy gson$delegate = null;
    private static final java.lang.String NULL_TIPS = "Log with null object";
    private static final java.lang.String DEFAULT_MESSAGE = "execute";
    private static final java.lang.String TAG_DEFAULT = "XLog";
    private static final java.lang.String SUFFIX = ".java";
    private static final java.lang.String KT = ".kt";
    public static final int V = 1;
    public static final int D = 2;
    public static final int I = 3;
    public static final int W = 4;
    public static final int E = 5;
    public static final int A = 6;
    public static final int JSON = 7;
    public static final int XML = 8;
    private static final int STACK_TRACE_INDEX_5 = 5;
    private static final int STACK_TRACE_INDEX_4 = 4;
    public static final int STACK_TRACE_INDEX_7 = 7;
    private static java.lang.String mGlobalTag;
    private static boolean mIsGlobalTagEmpty = true;
    private static boolean IS_SHOW_LOG = true;
    
    private XLog() {
        super();
    }
    
    private final com.google.gson.Gson getGson() {
        return null;
    }
    
    public final void init(boolean isShowLog) {
    }
    
    public final void init(boolean isShowLog, @org.jetbrains.annotations.Nullable
    java.lang.String tag) {
    }
    
    public final void v() {
    }
    
    public final void v(@org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void v(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void d() {
    }
    
    public final void d(@org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void d(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void i() {
    }
    
    public final void i(@org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void i(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void w() {
    }
    
    public final void w(@org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void w(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void e() {
    }
    
    public final void e(@org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void e(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void a() {
    }
    
    public final void a(@org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void a(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void json(@org.jetbrains.annotations.Nullable
    java.lang.String jsonFormat) {
    }
    
    public final void json(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.String jsonFormat) {
    }
    
    public final void xml(@org.jetbrains.annotations.Nullable
    java.lang.String xml) {
    }
    
    public final void xml(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.String xml) {
    }
    
    public final void trace() {
    }
    
    private final void printStackTrace() {
    }
    
    private final void printLog(int type, java.lang.String tagStr, int stackTraceIndex, java.lang.Object msg) {
    }
    
    public final void logType(int type, @org.jetbrains.annotations.Nullable
    java.lang.String tag, int stackTraceIndex, @org.jetbrains.annotations.Nullable
    java.lang.Object msg) {
    }
    
    public final void logXmlOrJson(int type, @org.jetbrains.annotations.Nullable
    java.lang.String tag, int stackTraceIndex, @org.jetbrains.annotations.Nullable
    java.lang.String xmlOrJson) {
    }
    
    private final java.lang.String[] wrapperContent(int stackTraceIndex, java.lang.String tagStr, java.lang.Object msg) {
        return null;
    }
    
    private final java.lang.String getObjectsString(java.lang.Object msg) {
        return null;
    }
}
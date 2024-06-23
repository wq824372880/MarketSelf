package com.zeekrlife.net.interception.logging.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2 = {"Lcom/zeekrlife/net/interception/logging/util/LogUtils;", "", "()V", "Companion", "net_cx1eDebug"})
public final class LogUtils {
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.interception.logging.util.LogUtils.Companion Companion = null;
    private static final java.lang.String DEFAULT_TAG = "Market";
    private static boolean isLog = true;
    
    private LogUtils() {
        super();
    }
    
    @kotlin.jvm.JvmStatic
    public static final void debugInfo(@org.jetbrains.annotations.Nullable
    java.lang.String msg) {
    }
    
    @kotlin.jvm.JvmStatic
    public static final void e(@org.jetbrains.annotations.Nullable
    java.lang.String tag, @org.jetbrains.annotations.Nullable
    java.lang.String msg) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\t\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\u0004H\u0007J\u001a\u0010\u0007\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\u000b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004J\u001a\u0010\u000b\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u00042\u0006\u0010\t\u001a\u00020\u0004H\u0002J\u001c\u0010\f\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004H\u0007J\u001a\u0010\r\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004J\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u000e\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0006J\u001a\u0010\u000f\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004J\u0010\u0010\u0010\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\u0004J\u001a\u0010\u0010\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/zeekrlife/net/interception/logging/util/LogUtils$Companion;", "", "()V", "DEFAULT_TAG", "", "isLog", "", "debugInfo", "", "msg", "tag", "debugLongInfo", "e", "i", "setLog", "w", "warnInfo", "net_cx1eDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final boolean isLog() {
            return false;
        }
        
        public final void setLog(boolean isLog) {
        }
        
        public final void debugInfo(@org.jetbrains.annotations.Nullable
        java.lang.String tag, @org.jetbrains.annotations.Nullable
        java.lang.String msg) {
        }
        
        @kotlin.jvm.JvmStatic
        public final void debugInfo(@org.jetbrains.annotations.Nullable
        java.lang.String msg) {
        }
        
        public final void i(@org.jetbrains.annotations.Nullable
        java.lang.String tag, @org.jetbrains.annotations.Nullable
        java.lang.String msg) {
        }
        
        public final void w(@org.jetbrains.annotations.Nullable
        java.lang.String tag, @org.jetbrains.annotations.Nullable
        java.lang.String msg) {
        }
        
        @kotlin.jvm.JvmStatic
        public final void e(@org.jetbrains.annotations.Nullable
        java.lang.String tag, @org.jetbrains.annotations.Nullable
        java.lang.String msg) {
        }
        
        public final void warnInfo(@org.jetbrains.annotations.Nullable
        java.lang.String tag, @org.jetbrains.annotations.Nullable
        java.lang.String msg) {
        }
        
        public final void warnInfo(@org.jetbrains.annotations.Nullable
        java.lang.String msg) {
        }
        
        /**
         * 这里使用自己分节的方式来输出足够长度的 message
         *
         * @param tag 标签
         * @param msg 日志内容
         */
        private final void debugLongInfo(java.lang.String tag, java.lang.String msg) {
        }
        
        public final void debugLongInfo(@org.jetbrains.annotations.NotNull
        java.lang.String msg) {
        }
    }
}
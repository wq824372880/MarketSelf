package com.zeekrlife.net.interception.logging.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2 = {"Lcom/zeekrlife/net/interception/logging/util/UrlEncoderUtils;", "", "()V", "Companion", "net_ef1etvDebug"})
public final class UrlEncoderUtils {
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.interception.logging.util.UrlEncoderUtils.Companion Companion = null;
    
    private UrlEncoderUtils() {
        super();
    }
    
    /**
     * 判断 str 是否已经 URLEncoder.encode() 过
     * 经常遇到这样的情况, 拿到一个 URL, 但是搞不清楚到底要不要 URLEncoder.encode()
     * 不做 URLEncoder.encode() 吧, 担心出错, 做 URLEncoder.encode() 吧, 又怕重复了
     *
     * @param str 需要判断的内容
     * @return 返回 `true` 为被 URLEncoder.encode() 过
     */
    @kotlin.jvm.JvmStatic
    public static final boolean hasUrlEncoded(@org.jetbrains.annotations.NotNull
    java.lang.String str) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\f\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0002\u00a8\u0006\n"}, d2 = {"Lcom/zeekrlife/net/interception/logging/util/UrlEncoderUtils$Companion;", "", "()V", "hasUrlEncoded", "", "str", "", "isValidHexChar", "c", "", "net_ef1etvDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * 判断 str 是否已经 URLEncoder.encode() 过
         * 经常遇到这样的情况, 拿到一个 URL, 但是搞不清楚到底要不要 URLEncoder.encode()
         * 不做 URLEncoder.encode() 吧, 担心出错, 做 URLEncoder.encode() 吧, 又怕重复了
         *
         * @param str 需要判断的内容
         * @return 返回 `true` 为被 URLEncoder.encode() 过
         */
        @kotlin.jvm.JvmStatic
        public final boolean hasUrlEncoded(@org.jetbrains.annotations.NotNull
        java.lang.String str) {
            return false;
        }
        
        /**
         * 判断 c 是否是 16 进制的字符
         *
         * @param c 需要判断的字符
         * @return 返回 `true` 为 16 进制的字符
         */
        private final boolean isValidHexChar(char c) {
            return false;
        }
    }
}
package com.zeekrlife.net.interception.logging.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\"$\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0000\u001a\u00020\u0001@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"value", "", "common", "getCommon", "()Z", "setCommon", "(Z)V", "net_bx1eDebug"})
public final class XLogKt {
    
    /**
     * 打印日志开关，框架是否打印请求日志、输出Log日志 默认为 true 打印数据
     */
    private static boolean common = true;
    
    public static final boolean getCommon() {
        return false;
    }
    
    public static final void setCommon(boolean value) {
    }
}
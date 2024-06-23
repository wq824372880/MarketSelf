package com.zeekrlife.net;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/zeekrlife/net/BaseNetConstant;", "", "()V", "CONNECT_TIME_OUT", "", "EMPTY_CODE", "", "ERROR_CODE", "READ_TIME_OUT", "SUCCESS_CODE", "SUCCESS_STATUS", "", "WRITE_TIME_OUT", "net_cs1eDebug"})
public final class BaseNetConstant {
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.BaseNetConstant INSTANCE = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String SUCCESS_CODE = "0";
    public static final int SUCCESS_STATUS = 200;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String ERROR_CODE = "10000";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String EMPTY_CODE = "99999";
    
    /**
     * 连接超时时间
     */
    public static final long CONNECT_TIME_OUT = 5L;
    
    /**
     * 读取超时时间
     */
    public static final long READ_TIME_OUT = 5L;
    
    /**
     * 写入超时时间
     */
    public static final long WRITE_TIME_OUT = 5L;
    
    private BaseNetConstant() {
        super();
    }
}
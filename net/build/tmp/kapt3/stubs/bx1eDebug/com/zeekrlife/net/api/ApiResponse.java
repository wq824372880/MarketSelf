package com.zeekrlife.net.api;

import java.lang.System;

/**
 * 描述　:  服务器返回的数据基类
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b&\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002:\u00011B9\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00028\u0000\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010%\u001a\u00020\u0004H\u00c6\u0003J\t\u0010&\u001a\u00020\u0004H\u00c6\u0003J\u000e\u0010\'\u001a\u00028\u0000H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\u000b\u0010(\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010)\u001a\u00020\nH\u00c6\u0003J\t\u0010*\u001a\u00020\fH\u00c6\u0003JR\u0010+\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00028\u00002\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001\u00a2\u0006\u0002\u0010,J\u0013\u0010-\u001a\u00020\f2\b\u0010.\u001a\u0004\u0018\u00010\u0002H\u00d6\u0003J\t\u0010/\u001a\u00020\nH\u00d6\u0001J\t\u00100\u001a\u00020\u0004H\u00d6\u0001R\u001a\u0010\u0005\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001c\u0010\u0006\u001a\u00028\u0000X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0016\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u000f\"\u0004\b\u001c\u0010\u0011R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$\u00a8\u00062"}, d2 = {"Lcom/zeekrlife/net/api/ApiResponse;", "T", "", "msg", "", "code", "data", "debug", "Lcom/zeekrlife/net/api/ApiResponse$Debug;", "total", "", "success", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Lcom/zeekrlife/net/api/ApiResponse$Debug;IZ)V", "getCode", "()Ljava/lang/String;", "setCode", "(Ljava/lang/String;)V", "getData", "()Ljava/lang/Object;", "setData", "(Ljava/lang/Object;)V", "Ljava/lang/Object;", "getDebug", "()Lcom/zeekrlife/net/api/ApiResponse$Debug;", "setDebug", "(Lcom/zeekrlife/net/api/ApiResponse$Debug;)V", "getMsg", "setMsg", "getSuccess", "()Z", "setSuccess", "(Z)V", "getTotal", "()I", "setTotal", "(I)V", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Lcom/zeekrlife/net/api/ApiResponse$Debug;IZ)Lcom/zeekrlife/net/api/ApiResponse;", "equals", "other", "hashCode", "toString", "Debug", "net_bx1eDebug"})
public final class ApiResponse<T extends java.lang.Object> {
    
    /**
     * //    msg	String	业务信息/错误信息/异常信息
     *   //    msgCode	String	全局状态码
     *   //    <T>content	object	业务返回数据实体
     *   //    debug	object	调试数据
     *   //    --  traceId	String	全局链路唯一标识
     *   //    --  bizName	String	微服务名称
     *   //   {
     *   //    "msg": "服务器内部异常",
     *   //    "msgCode": "01A01",
     *   //    "content": null,
     *   //    "debug":
     *   //     {
     *   //    "traceId": "848ea6fda4544390b6f8f9a977f8d68a.107.16512156336490007",
     *   //    "bizName": "snc-media-user"
     *   //     }
     *   //  }
     */
    @org.jetbrains.annotations.NotNull
    private java.lang.String msg;
    @org.jetbrains.annotations.NotNull
    private java.lang.String code;
    private T data;
    @org.jetbrains.annotations.Nullable
    private com.zeekrlife.net.api.ApiResponse.Debug debug;
    private int total;
    private boolean success;
    
    /**
     * 描述　:  服务器返回的数据基类
     */
    @org.jetbrains.annotations.NotNull
    public final com.zeekrlife.net.api.ApiResponse<T> copy(@org.jetbrains.annotations.NotNull
    java.lang.String msg, @org.jetbrains.annotations.NotNull
    java.lang.String code, T data, @org.jetbrains.annotations.Nullable
    com.zeekrlife.net.api.ApiResponse.Debug debug, int total, boolean success) {
        return null;
    }
    
    /**
     * 描述　:  服务器返回的数据基类
     */
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    /**
     * 描述　:  服务器返回的数据基类
     */
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * 描述　:  服务器返回的数据基类
     */
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public ApiResponse(@org.jetbrains.annotations.NotNull
    java.lang.String msg, @org.jetbrains.annotations.NotNull
    java.lang.String code, T data, @org.jetbrains.annotations.Nullable
    com.zeekrlife.net.api.ApiResponse.Debug debug, int total, boolean success) {
        super();
    }
    
    /**
     * //    msg	String	业务信息/错误信息/异常信息
     *   //    msgCode	String	全局状态码
     *   //    <T>content	object	业务返回数据实体
     *   //    debug	object	调试数据
     *   //    --  traceId	String	全局链路唯一标识
     *   //    --  bizName	String	微服务名称
     *   //   {
     *   //    "msg": "服务器内部异常",
     *   //    "msgCode": "01A01",
     *   //    "content": null,
     *   //    "debug":
     *   //     {
     *   //    "traceId": "848ea6fda4544390b6f8f9a977f8d68a.107.16512156336490007",
     *   //    "bizName": "snc-media-user"
     *   //     }
     *   //  }
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    /**
     * //    msg	String	业务信息/错误信息/异常信息
     *   //    msgCode	String	全局状态码
     *   //    <T>content	object	业务返回数据实体
     *   //    debug	object	调试数据
     *   //    --  traceId	String	全局链路唯一标识
     *   //    --  bizName	String	微服务名称
     *   //   {
     *   //    "msg": "服务器内部异常",
     *   //    "msgCode": "01A01",
     *   //    "content": null,
     *   //    "debug":
     *   //     {
     *   //    "traceId": "848ea6fda4544390b6f8f9a977f8d68a.107.16512156336490007",
     *   //    "bizName": "snc-media-user"
     *   //     }
     *   //  }
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getMsg() {
        return null;
    }
    
    /**
     * //    msg	String	业务信息/错误信息/异常信息
     *   //    msgCode	String	全局状态码
     *   //    <T>content	object	业务返回数据实体
     *   //    debug	object	调试数据
     *   //    --  traceId	String	全局链路唯一标识
     *   //    --  bizName	String	微服务名称
     *   //   {
     *   //    "msg": "服务器内部异常",
     *   //    "msgCode": "01A01",
     *   //    "content": null,
     *   //    "debug":
     *   //     {
     *   //    "traceId": "848ea6fda4544390b6f8f9a977f8d68a.107.16512156336490007",
     *   //    "bizName": "snc-media-user"
     *   //     }
     *   //  }
     */
    public final void setMsg(@org.jetbrains.annotations.NotNull
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getCode() {
        return null;
    }
    
    public final void setCode(@org.jetbrains.annotations.NotNull
    java.lang.String p0) {
    }
    
    public final T component3() {
        return null;
    }
    
    public final T getData() {
        return null;
    }
    
    public final void setData(T p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.zeekrlife.net.api.ApiResponse.Debug component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.zeekrlife.net.api.ApiResponse.Debug getDebug() {
        return null;
    }
    
    public final void setDebug(@org.jetbrains.annotations.Nullable
    com.zeekrlife.net.api.ApiResponse.Debug p0) {
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int getTotal() {
        return 0;
    }
    
    public final void setTotal(int p0) {
    }
    
    public final boolean component6() {
        return false;
    }
    
    public final boolean getSuccess() {
        return false;
    }
    
    public final void setSuccess(boolean p0) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\b\u0018\u00002\u00020\u0001B%\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0007R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\r"}, d2 = {"Lcom/zeekrlife/net/api/ApiResponse$Debug;", "", "bizName", "", "time", "", "traceId", "(Ljava/lang/String;JLjava/lang/String;)V", "getBizName", "()Ljava/lang/String;", "getTime", "()J", "getTraceId", "net_bx1eDebug"})
    public static final class Debug {
        @org.jetbrains.annotations.Nullable
        private final java.lang.String bizName = null;
        private final long time = 0L;
        @org.jetbrains.annotations.Nullable
        private final java.lang.String traceId = null;
        
        public Debug(@org.jetbrains.annotations.Nullable
        java.lang.String bizName, long time, @org.jetbrains.annotations.Nullable
        java.lang.String traceId) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String getBizName() {
            return null;
        }
        
        public final long getTime() {
            return 0L;
        }
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String getTraceId() {
            return null;
        }
    }
}
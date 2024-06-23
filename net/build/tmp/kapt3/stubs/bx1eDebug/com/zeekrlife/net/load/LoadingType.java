package com.zeekrlife.net.load;

import java.lang.System;

/**
 * 描述　:
 */
@androidx.annotation.IntDef(value = {0, 1, 2, 3})
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0002\b\u0002\b\u0087\u0002\u0018\u0000 \u00022\u00020\u0001:\u0001\u0002B\u0000\u00a8\u0006\u0003"}, d2 = {"Lcom/zeekrlife/net/load/LoadingType;", "", "Companion", "net_bx1eDebug"})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.SOURCE)
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.SOURCE)
public abstract @interface LoadingType {
    @org.jetbrains.annotations.NotNull
    public static final com.zeekrlife.net.load.LoadingType.Companion Companion = null;
    public static final int LOADING_NULL = 0;
    public static final int LOADING_DIALOG = 1;
    public static final int LOADING_XML = 2;
    public static final int LOADING_CUSTOM = 3;
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/zeekrlife/net/load/LoadingType$Companion;", "", "()V", "LOADING_CUSTOM", "", "LOADING_DIALOG", "LOADING_NULL", "LOADING_XML", "net_bx1eDebug"})
    public static final class Companion {
        public static final int LOADING_NULL = 0;
        public static final int LOADING_DIALOG = 1;
        public static final int LOADING_XML = 2;
        public static final int LOADING_CUSTOM = 3;
        
        private Companion() {
            super();
        }
    }
}
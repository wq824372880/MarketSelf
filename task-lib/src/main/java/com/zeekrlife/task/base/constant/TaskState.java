package com.zeekrlife.task.base.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        TaskState.OPENABLE,
        TaskState.DOWNLOADABLE,
        TaskState.UPDATABLE,
        TaskState.DOWNLOAD_PENDING,
        TaskState.DOWNLOAD_STARTED,
        TaskState.DOWNLOAD_CONNECTED,
        TaskState.DOWNLOAD_PROGRESS,
        TaskState.DOWNLOAD_PAUSED,
        TaskState.DOWNLOAD_COMPLETED,
        TaskState.DOWNLOAD_ERROR,
        TaskState.INSTALLABLE,
        TaskState.INSTALL_PENDING,
        TaskState.INSTALL_STARTED,
        TaskState.INSTALL_PROGRESS,
        TaskState.INSTALL_COMPLETED,
        TaskState.INSTALL_ERROR,
//        TaskState.NEED_PAY_FEE,
//        TaskState.NEED_PAY_ACTIVITY,
        TaskState.CANCEL_DOWNLOAD,
})
public @interface TaskState {
    int OPENABLE = 0;
    int DOWNLOADABLE = 1;
    int UPDATABLE = 2;
    int DOWNLOAD_PENDING = 3;
    int DOWNLOAD_STARTED = 4;
    int DOWNLOAD_CONNECTED = 5;
    int DOWNLOAD_PROGRESS = 6;
    int DOWNLOAD_PAUSED = 7;
    int DOWNLOAD_COMPLETED = 8;
    int DOWNLOAD_ERROR = 9;
    int INSTALLABLE = 10;
    int INSTALL_PENDING = 11;
    int INSTALL_STARTED = 12;
    int INSTALL_PROGRESS = 13;
    int INSTALL_COMPLETED = 14;
    int INSTALL_ERROR = 15;
//    int NEED_PAY_FEE = 100;
//    int NEED_PAY_ACTIVITY = 101;
    int CANCEL_DOWNLOAD = 16;
}

package com.zeekrlife.common.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author
 */
public class EmptyException extends Exception {

    private final String errorCode;

    public EmptyException(@NonNull String code, String message) {
        super(message);
        errorCode = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Nullable
    @Override
    public String getLocalizedMessage() {
        return errorCode;
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" + "\n\nCode=" + errorCode + " message=" + getMessage();
    }
}
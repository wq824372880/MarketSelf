package com.zeekrlife.ampe.core.util;

import android.content.Intent;

import androidx.annotation.NonNull;

public class IntentUtils {
    public static final String PACKAGE_LAUNCHER = "ecarx.launcher3";

    public static @NonNull
    Intent getHomeIntent() {
        return new Intent()
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setPackage(PACKAGE_LAUNCHER)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}

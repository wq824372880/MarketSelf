package com.zeekrlife.task.base.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.zeekrlife.common.util.EncryptUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.task.base.bean.ExpandInfo;
import com.zeekrlife.task.base.bean.ExpandType;
import com.zeekrlife.task.base.bean.TaskInfo;
import com.zeekrlife.task.base.constant.TaskType;

import java.io.File;

public class TaskUtils {

    public static String getTaskId(Context context, TaskInfo taskInfo) {
        if (TextUtils.isEmpty(taskInfo.url)) {
            return "";
        }
        String filesDir = null;
        File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (externalFilesDir != null) {
            filesDir = externalFilesDir.getAbsolutePath();
        }

        if (taskInfo.type == TaskType.INSTALL) {
            return EncryptUtils.encryptMD5ToString(taskInfo.path);
        }
        int expandType;
        String expandStr = taskInfo.expand;
        if (TextUtils.isEmpty(expandStr)) {
            expandType = ExpandType.APK;
        } else {
            ExpandInfo expand = GsonUtils.fromJson(expandStr, ExpandInfo.class);
            if (expand == null) {
                expandType = ExpandType.APK;
            } else {
                expandType = expand.getType();
            }
        }
        String subDir;
        String suffix;
        switch (expandType) {
            case ExpandType.APK:
                subDir = "apk";
                suffix = ".apk";
                break;
            case ExpandType.SPECIAL:
                subDir = "special";
                suffix = ".apk";
                break;
            case ExpandType.OVERLAY:
                subDir = "overlay";
                suffix = ".overlay";
                break;
            case ExpandType.FRAMEWORK:
                subDir = "framework";
                suffix = ".fwk";
                break;
            default:
                subDir = "file";
                suffix = "";
                break;
        }

        String uriPath = Uri.parse(taskInfo.url).getPath();

        String path = filesDir + File.separator + subDir + File.separator
                + EncryptUtils.encryptMD5ToString(uriPath) + suffix;
        return EncryptUtils.encryptMD5ToString(path);
    }
}

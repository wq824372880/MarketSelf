package com.zeekrlife.common.util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;

import com.zeekr.basic.Common;
import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;


public final class IntentUtils {

    private IntentUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether the intent is available.
     *
     * @param intent The intent.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIntentAvailable(final Intent intent) {
        return Common.app
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    /**
     * Return the intent of install app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     * @return the intent of install app
     */
    public static Intent getInstallAppIntent(final String filePath) {
        return getInstallAppIntent(UtilsBridge.getFileByPath(filePath));
    }

    /**
     * Return the intent of install app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     * @return the intent of install app
     */
    public static Intent getInstallAppIntent(final File file) {
        if (!UtilsBridge.isFileExists(file)) {
            return null;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            String authority = Common.app.getPackageName() + ".utilcode.provider";
            uri = FileProvider.getUriForFile(Common.app, authority, file);
        }
        return getInstallAppIntent(uri);
    }

    /**
     * Return the intent of install app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param uri The uri.
     * @return the intent of install app
     */
    public static Intent getInstallAppIntent(final Uri uri) {
        if (uri == null) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(uri, type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Return the intent of uninstall app.
     * <p>Target APIs greater than 25 must hold
     * Must hold {@code <uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />}</p>
     *
     * @param pkgName The name of the package.
     * @return the intent of uninstall app
     */
    public static Intent getUninstallAppIntent(final String pkgName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkgName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app details settings
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName) {
        return getLaunchAppDetailsSettingsIntent(pkgName, false);
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app details settings
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName, final boolean isNewTask) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + pkgName));
        return getIntent(intent, isNewTask);
    }

    /**
     * Return the intent of share text.
     *
     * @param content The content.
     * @return the intent of share text
     */
    public static Intent getShareTextIntent(final String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * Return the intent of share image.
     *
     * @param imageUri The uri of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final Uri imageUri) {
        return getShareTextImageIntent("", imageUri);
    }

    /**
     * Return the intent of share image.
     *
     * @param content  The content.
     * @param imageUri The uri of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }


    /**
     * Return the intent of share images.
     *
     * @param uris The uris of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final ArrayList<Uri> uris) {
        return getShareTextImageIntent("", uris);
    }

    /**
     * Return the intent of share images.
     *
     * @param content The content.
     * @param uris    The uris of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final ArrayList<Uri> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName, final String className) {
        return getComponentIntent(pkgName, className, null, false);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final boolean isNewTask) {
        return getComponentIntent(pkgName, className, null, isNewTask);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle) {
        return getComponentIntent(pkgName, className, bundle, false);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle,
                                            final boolean isNewTask) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        ComponentName cn = new ComponentName(pkgName, className);
        intent.setComponent(cn);
        return getIntent(intent, isNewTask);
    }

    /**
     * Return the intent of shutdown.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.SHUTDOWN" />}
     * in manifest.</p>
     *
     * @return the intent of shutdown
     */
    public static Intent getShutdownIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Intent.ACTION_SHUTDOWN);
        } else {
            intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
        }
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Return the intent of dial.
     *
     * @param phoneNumber The phone number.
     * @return the intent of dial
     */
    public static Intent getDialIntent(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        return getIntent(intent, true);
    }

    /**
     * Return the intent of call.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber The phone number.
     * @return the intent of call
     */
    @RequiresPermission(CALL_PHONE)
    public static Intent getCallIntent(final String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
        return getIntent(intent, true);
    }

    /**
     * Return the intent of send SMS.
     *
     * @param phoneNumber The phone number.
     * @param content     The content of SMS.
     * @return the intent of send SMS
     */
    public static Intent getSendSmsIntent(final String phoneNumber, final String content) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        return getIntent(intent, true);
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri The uri of output.
     * @return the intent of capture
     */
    public static Intent getCaptureIntent(final Uri outUri) {
        return getCaptureIntent(outUri, false);
    }

    /**
     * Return the intent of launch app.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app
     */
    public static Intent getLaunchAppIntent(final String pkgName) {
        String launcherActivity = UtilsBridge.getLauncherActivity(pkgName);
        if (UtilsBridge.isSpace(launcherActivity)) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(pkgName, launcherActivity);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri    The uri of output.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of capture
     */
    public static Intent getCaptureIntent(final Uri outUri, final boolean isNewTask) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }
}
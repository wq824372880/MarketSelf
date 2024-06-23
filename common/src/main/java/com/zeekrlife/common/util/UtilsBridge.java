package com.zeekrlife.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class UtilsBridge {

    ///////////////////////////////////////////////////////////////////////////
    // ActivityUtils
    ///////////////////////////////////////////////////////////////////////////
    static boolean isActivityAlive(final Activity activity) {
        return ActivityUtils.isActivityAlive(activity);
    }

    static String getLauncherActivity(final String pkg) {
        return ActivityUtils.getLauncherActivity(pkg);
    }

    static Activity getActivityByContext(Context context) {
        return ActivityUtils.getActivityByContext(context);
    }

    ///////////////////////////////////////////////////////////////////////////
    // StringUtils
    ///////////////////////////////////////////////////////////////////////////
    static boolean isSpace(final String s) {
        return StringUtils.INSTANCE.isSpace(s);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ConvertUtils
    ///////////////////////////////////////////////////////////////////////////
    static String bytes2HexString(final byte[] bytes) {
        return ConvertUtils.bytes2HexString(bytes);
    }

    static byte[] hexString2Bytes(String hexString) {
        return ConvertUtils.hexString2Bytes(hexString);
    }

    static byte[] string2Bytes(final String string) {
        return ConvertUtils.string2Bytes(string);
    }

    static String bytes2String(final byte[] bytes) {
        return ConvertUtils.bytes2String(bytes);
    }

    static byte[] jsonObject2Bytes(final JSONObject jsonObject) {
        return ConvertUtils.jsonObject2Bytes(jsonObject);
    }

    static JSONObject bytes2JSONObject(final byte[] bytes) {
        return ConvertUtils.bytes2JSONObject(bytes);
    }

    static byte[] jsonArray2Bytes(final JSONArray jsonArray) {
        return ConvertUtils.jsonArray2Bytes(jsonArray);
    }

    static JSONArray bytes2JSONArray(final byte[] bytes) {
        return ConvertUtils.bytes2JSONArray(bytes);
    }

    static byte[] parcelable2Bytes(final Parcelable parcelable) {
        return ConvertUtils.parcelable2Bytes(parcelable);
    }

    static <T> T bytes2Parcelable(final byte[] bytes,
                                  final Parcelable.Creator<T> creator) {
        return ConvertUtils.bytes2Parcelable(bytes, creator);
    }

    static byte[] serializable2Bytes(final Serializable serializable) {
        return ConvertUtils.serializable2Bytes(serializable);
    }

    static Object bytes2Object(final byte[] bytes) {
        return ConvertUtils.bytes2Object(bytes);
    }

    static String byte2FitMemorySize(final long byteSize) {
        return ConvertUtils.byte2FitMemorySize(byteSize);
    }

    static byte[] inputStream2Bytes(final InputStream is) {
        return ConvertUtils.inputStream2Bytes(is);
    }

    static ByteArrayOutputStream input2OutputStream(final InputStream is) {
        return ConvertUtils.input2OutputStream(is);
    }

    static List<String> inputStream2Lines(final InputStream is, final String charsetName) {
        return ConvertUtils.inputStream2Lines(is, charsetName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // FileUtils
    ///////////////////////////////////////////////////////////////////////////
    static boolean isFileExists(final File file) {
        return FileUtils.INSTANCE.isFileExists(file);
    }

    static File getFileByPath(final String filePath) {
        return FileUtils.INSTANCE.getFileByPath(filePath);
    }

    static boolean deleteAllInDir(final File dir) {
        return FileUtils.INSTANCE.deleteAllInDir(dir);
    }

    static boolean createOrExistsFile(final File file) {
        return FileUtils.INSTANCE.createOrExistsFile(file);
    }

    static boolean createOrExistsDir(final File file) {
        return FileUtils.INSTANCE.createOrExistsDir(file);
    }

    static boolean createFileByDeleteOldFile(final File file) {
        return FileUtils.INSTANCE.createFileByDeleteOldFile(file);
    }

    static long getFsTotalSize(String path) {
        return FileUtils.INSTANCE.getFsTotalSize(path);
    }

    static long getFsAvailableSize(String path) {
        return FileUtils.INSTANCE.getFsAvailableSize(path);
    }

    ///////////////////////////////////////////////////////////////////////////
    // EncodeUtils
    ///////////////////////////////////////////////////////////////////////////
    static byte[] base64Encode(final byte[] input) {
        return EncodeUtils.base64Encode(input);
    }

    static byte[] base64Decode(final byte[] input) {
        return EncodeUtils.base64Decode(input);
    }

    ///////////////////////////////////////////////////////////////////////////
    // EncryptUtils
    ///////////////////////////////////////////////////////////////////////////
    static byte[] hashTemplate(final byte[] data, final String algorithm) {
        return EncryptUtils.hashTemplate(data, algorithm);
    }

    ///////////////////////////////////////////////////////////////////////////
    // GsonUtils
    ///////////////////////////////////////////////////////////////////////////
    static String toJson(final Object object) {
        return GsonUtils.toJson(object);
    }

    static <T> T fromJson(final String json, final Type type) {
        return GsonUtils.fromJson(json, type);
    }

    static Gson getGson4LogUtils() {
        return GsonUtils.getGson4LogUtils();
    }

    ///////////////////////////////////////////////////////////////////////////
    // SDCardUtils
    ///////////////////////////////////////////////////////////////////////////
    static boolean isSDCardEnableByEnvironment() {
        return SDCardUtils.isSDCardEnableByEnvironment();
    }

    ///////////////////////////////////////////////////////////////////////////
    // SizeUtils
    ///////////////////////////////////////////////////////////////////////////
    static int dp2px(final float dpValue) {
        return SizeUtils.dp2px(dpValue);
    }

    static int px2dp(final float pxValue) {
        return SizeUtils.px2dp(pxValue);
    }

    static int sp2px(final float spValue) {
        return SizeUtils.sp2px(spValue);
    }

    static int px2sp(final float pxValue) {
        return SizeUtils.px2sp(pxValue);
    }

    public static boolean isModelNight(Context context){
        return Configuration.UI_MODE_NIGHT_YES == (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
    }
}

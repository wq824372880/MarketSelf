package com.zeekrlife.market.update;

import androidx.annotation.NonNull;

/**
 * @author
 */
public class AppInfo {

    private String appName;

    private String packageName;

    private String versionName;

    private long versionCode;

    public String appDescription;

    public String updateDesc;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppInfo{"
            + "appName='"
            + appName
            + '\''
            + ", packageName='"
            + packageName
            + '\''
            + ", versionName='"
            + versionName
            + '\''
            + ", versionCode='"
            + versionCode
            + '\''
            + ", appDescription='"
            + appDescription
            + '\''
            + ", updateDesc='"
            + updateDesc
            + '\''
            + '}';
    }
}

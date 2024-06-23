package com.zeekrlife.task.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo extends ExpandInfo implements Parcelable {
    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    private String packageName;
    private String apkName;
    private String apkIcon;
    private String apkSize;
    private String versionName;
    private long versionCode;

    public AppInfo() {
    }

    public AppInfo(int type, String packageName, String apkName, String apkIcon, String apkSize, String versionName, long versionCode) {
        super(type);
        this.packageName = packageName;
        this.apkName = apkName;
        this.apkIcon = apkIcon;
        this.apkSize = apkSize;
        this.versionName = versionName;
        this.versionCode = versionCode;
    }

    protected AppInfo(Parcel in) {
        super(in);
        packageName = in.readString();
        apkName = in.readString();
        apkIcon = in.readString();
        apkSize = in.readString();
        versionName = in.readString();
        versionCode = in.readLong();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getApkIcon() {
        return apkIcon;
    }

    public void setApkIcon(String apkIcon) {
        this.apkIcon = apkIcon;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
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

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", apkName='" + apkName + '\'' +
                ", apkIcon='" + apkIcon + '\'' +
                ", apkSize='" + apkSize + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                "} " + super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(packageName);
        dest.writeString(apkName);
        dest.writeString(apkIcon);
        dest.writeString(apkSize);
        dest.writeString(versionName);
        dest.writeLong(versionCode);
    }
}

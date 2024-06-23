package com.zeekrlife.market.task.data.expand;


public class AppEntity extends ExpandEntity {
    private static final long serialVersionUID = -4543747059115972681L;

    private String packageName;
    private String apkName;
    private String apkIcon;
    private String apkSize;
    private String versionName;
    private long versionCode;

    public AppEntity() {
    }

    public AppEntity(int type, String packageName, String apkName, String apkIcon, String apkSize, String versionName, long versionCode) {
        super(type);
        this.packageName = packageName;
        this.apkName = apkName;
        this.apkIcon = apkIcon;
        this.apkSize = apkSize;
        this.versionName = versionName;
        this.versionCode = versionCode;
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
        return "AppEntity{" +
                "packageName='" + packageName + '\'' +
                ", apkName='" + apkName + '\'' +
                ", apkIcon='" + apkIcon + '\'' +
                ", apkSize='" + apkSize + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                "} " + super.toString();
    }
}

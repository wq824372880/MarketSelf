package com.zeekrlife.market.task.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PackagesBean {
    @SerializedName("packageName")
    public String packageName;
    @SerializedName("internalVersion")
    public long internalVersion;
    @SerializedName("externalVersion")
    public String externalVersion;
    /**
     * sas/overlay
     */
    @SerializedName(value = "pkgs_type")
    public String pkgsType;
    @SerializedName(value = "pkgs_list")
    public List<PackageBean> packageBeanList;

    @Override
    public String toString() {
        return "PackagesBean{" +
                "packageName='" + packageName + '\'' +
                ", internalVersion=" + internalVersion +
                ", externalVersion='" + externalVersion + '\'' +
                ", pkgsType='" + pkgsType + '\'' +
                ", packageBeanList=" + packageBeanList +
                '}';
    }

    public static class PackageBean {
        @SerializedName(value = "apk_name")
        public String apkName;
        @SerializedName(value = "pkg_name")
        public String pkgName;
        /**
         * sas/overlay/target
         */
        @SerializedName(value = "pkg_type")
        public String pkgType;
        @SerializedName(value = "pkg_version")
        public long pkgVersion;

        @SerializedName(value = "need_enable")
        public String needEnable;

        @SerializedName("installed")
        public boolean installed;
        @SerializedName("backupPath")
        public String backupPath;
        @SerializedName("backupVersionCode")
        public int backupVersionCode;

        public boolean needEnable() {
            return !TextUtils.isEmpty(needEnable);
        }

        public boolean hasBackup() {
            return !TextUtils.isEmpty(this.backupPath);
        }

        @Override
        public String toString() {
            return "PackageBean{" +
                    "apkName='" + apkName + '\'' +
                    ", pkgName='" + pkgName + '\'' +
                    ", pkgType='" + pkgType + '\'' +
                    ", pkgVersion=" + pkgVersion +
                    ", needEnable='" + needEnable + '\'' +
                    ", installed=" + installed +
                    ", backupPath='" + backupPath + '\'' +
                    ", backupVersionCode=" + backupVersionCode +
                    '}';
        }
    }
}

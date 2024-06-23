package com.zeekrlife.market.task.data.source;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task")
public class TaskEntity implements Cloneable {

    @PrimaryKey
    @NonNull
    private String id;
    private String url;
    private String path;
    private String hash;

    private String apkSha256;
    private int type;
    private String expand;

    @ColumnInfo(name = "download_id")
    private String downloadId;
    @ColumnInfo(name = "install_id")
    private String installId;

    private int status;

    @Ignore
    private long soFarBytes;
    @Ignore
    private long totalBytes;
    @Ignore
    private float installProgress;
    @Ignore
    private int errorCode;

    public TaskEntity(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSoFarBytes() {
        return soFarBytes;
    }

    public void setSoFarBytes(long soFarBytes) {
        this.soFarBytes = soFarBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public float getInstallProgress() {
        return installProgress;
    }

    public void setInstallProgress(float installProgress) {
        this.installProgress = installProgress;
    }

    public String getApkSha256() {
        return apkSha256;
    }

    public void setApkSha256(String apkSha256) {
        this.apkSha256 = apkSha256;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", hash='" + hash + '\'' +
                ", apkSha256='" + apkSha256 + '\'' +
                ", type=" + type +
                ", expand='" + expand + '\'' +
                ", downloadId='" + downloadId + '\'' +
                ", installId='" + installId + '\'' +
                ", status=" + status +
                ", soFarBytes=" + soFarBytes +
                ", totalBytes=" + totalBytes +
                ", installProgress=" + installProgress +
                ", errorCode=" + errorCode +
                '}';
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

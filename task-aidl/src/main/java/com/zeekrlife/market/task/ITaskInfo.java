package com.zeekrlife.market.task;

import android.os.Parcel;
import android.os.Parcelable;

public class ITaskInfo implements Parcelable {
    public static final Creator<ITaskInfo> CREATOR = new Creator<ITaskInfo>() {
        @Override
        public ITaskInfo createFromParcel(Parcel source) {
            return new ITaskInfo(source);
        }

        @Override
        public ITaskInfo[] newArray(int size) {
            return new ITaskInfo[size];
        }
    };

    public String id;

    public String url;
    public String path;
    public String hash;

    public int type;
    public String expand;

    public int status;

    public long soFar;
    public long total;
    public float installProgress;

    public String apkSha256;

    public ITaskInfo() {
    }

    protected ITaskInfo(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
        this.path = in.readString();
        this.hash = in.readString();
        this.status = in.readInt();
        this.type = in.readInt();
        this.expand = in.readString();
        this.soFar = in.readLong();
        this.total = in.readLong();
        this.installProgress = in.readFloat();
        this.apkSha256 = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.path);
        dest.writeString(this.hash);
        dest.writeInt(this.status);
        dest.writeInt(this.type);
        dest.writeString(this.expand);
        dest.writeLong(this.soFar);
        dest.writeLong(this.total);
        dest.writeFloat(this.installProgress);
        dest.writeString(this.apkSha256);
    }

    @Override
    public String toString() {
        return "ITaskInfo{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", hash='" + hash + '\'' +
                ", type=" + type +
                ", expand='" + expand + '\'' +
                ", status=" + status +
                ", soFar=" + soFar +
                ", total=" + total +
                ", installProgress=" + installProgress +
                ", apkSha256='" + apkSha256 + '\'' +
                '}';
    }
}

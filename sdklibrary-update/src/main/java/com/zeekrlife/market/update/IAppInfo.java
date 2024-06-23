package com.zeekrlife.market.update;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author
 */
 class IAppInfo implements Parcelable {

    public static final Creator<IAppInfo> CREATOR = new Creator<IAppInfo>() {
        @Override
        public IAppInfo createFromParcel(Parcel source) {
            return new IAppInfo(source);
        }

        @Override
        public IAppInfo[] newArray(int size) {
            return new IAppInfo[size];
        }
    };

    public String appName;

    public String packageName;

    public String versionName;

    public long versionCode;

    public String appDescription;

    public String updateDesc;

    public IAppInfo() {
    }

    protected IAppInfo(Parcel in) {
        this.appName = in.readString();
        this.packageName = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readLong();
        this.appDescription = in.readString();
        this.updateDesc = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeString(this.versionName);
        dest.writeLong(this.versionCode);
        dest.writeString(this.appDescription);
        dest.writeString(this.updateDesc);
    }

    @Override
    public String toString() {
        return "IAppInfo{"
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

package com.zeekrlife.task.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author
 */
public class ExpandInfo implements Parcelable {
    public static final Creator<ExpandInfo> CREATOR = new Creator<ExpandInfo>() {
        @Override
        public ExpandInfo createFromParcel(Parcel in) {
            return new ExpandInfo(in);
        }

        @Override
        public ExpandInfo[] newArray(int size) {
            return new ExpandInfo[size];
        }
    };

    /**
     * 拓展类型
     */
    protected int type;

    public ExpandInfo() {
    }

    public ExpandInfo(int type) {
        this.type = type;
    }

    protected ExpandInfo(Parcel in) {
        type = in.readInt();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
    }

    @Override
    public String toString() {
        return "ExpandInfo{" +
                "type=" + type +
                '}';
    }
}

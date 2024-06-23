package com.zeekrlife.market.update;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author
 */
public class IAppInfo implements Parcelable {

    public static final Creator<IAppInfo> CREATOR = new Creator<IAppInfo>() {
        /**
         * 从Parcel对象中创建一个IAppInfo实例。
         *
         * @param source 表示包含IAppInfo对象数据的Parcel对象。
         * @return 返回一个新创建的IAppInfo对象，该对象的数据来源于传入的Parcel对象。
         */
        @Override
        public IAppInfo createFromParcel(Parcel source) {
            return new IAppInfo(source);
        }

        /**
         * 创建一个包含指定数量IAppInfo对象的数组。
         *
         * @param size 数组的大小，决定了返回的数组能存储的IAppInfo对象的数量。
         * @return 返回一个长度为size的IAppInfo对象数组。数组中的每个元素都尚未被初始化。
         */
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

    /**
     * 描述当前对象的内容复杂度。
     * 该方法用于返回一个整数，表示对象在进行序列化或者传输时需要描述的内容复杂度。
     * 返回的值越小，表示内容越简单，不需要特殊的处理；返回的值越大，表示内容越复杂，可能需要更复杂的处理方式。
     *
     * @return 返回一个整数，表示对象的内容复杂度。在本实现中，始终返回0，表示内容最简单。
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将当前对象的信息写入到Parcel中。
     * @param dest 目标Parcel对象，用于存储对象信息。
     * @param flags 传递给Parcel的标志位，可用于控制序列化的方式。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 将应用名称写入Parcel
        dest.writeString(this.appName);
        // 将应用包名写入Parcel
        dest.writeString(this.packageName);
        // 将应用版本名称写入Parcel
        dest.writeString(this.versionName);
        // 将应用版本代码写入Parcel
        dest.writeLong(this.versionCode);
        // 将应用描述信息写入Parcel
        dest.writeString(this.appDescription);
        // 将更新描述信息写入Parcel
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
            + ", appDescription='"
            + appDescription
            + ", updateDesc='"
            + updateDesc
            + '\''
            + '}';
    }
}

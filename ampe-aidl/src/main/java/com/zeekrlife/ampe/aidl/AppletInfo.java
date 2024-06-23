package com.zeekrlife.ampe.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mac
 * @date 2023/2/24 13:46
 * description：
 */
public class AppletInfo implements Parcelable {
    private static final boolean SUCCESS_DEFAULT = false;
    private static final int CODE_DEFAULT = 0;
    private static final String MESSAGE_DEFAULT = "";

    private boolean success;
    private int code;
    private String message;

    protected AppletInfo(Parcel in) {
        success = in.readByte() != 0;
        code = in.readInt();
        message = in.readString();
    }

    public AppletInfo() {
        success = SUCCESS_DEFAULT;
        code = CODE_DEFAULT;
        message = MESSAGE_DEFAULT;
    }

    public static final Creator<AppletInfo> CREATOR = new Creator<AppletInfo>() {
        @Override
        public AppletInfo createFromParcel(Parcel in) {
            return new AppletInfo(in);
        }

        @Override
        public AppletInfo[] newArray(int size) {
            return new AppletInfo[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeInt(code);
        dest.writeString(message);
    }
}

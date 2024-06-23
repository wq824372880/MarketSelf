package com.zeekrlife.task.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author
 */
public class ExpandAppInfo extends AppInfo implements Parcelable {
    public static final Creator<ExpandAppInfo> CREATOR = new Creator<ExpandAppInfo>() {
        @Override
        public ExpandAppInfo createFromParcel(Parcel in) {
            return new ExpandAppInfo(in);
        }

        @Override
        public ExpandAppInfo[] newArray(int size) {
            return new ExpandAppInfo[size];
        }
    };

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类ID
     */
    private int categoryId;

    /**
     * 应用版本ID
     */
    private String appVersionId;
    /**
     * 是否是更新
     */
    private boolean update;
    /**
     * 是否支持双音源
     */
    private int isDualAudio;

    /**
     * 行车中是否允许副驾使用：0-不允许，1-允许
     */
    private int supportDrivingPassengerUser;

    /**
     * 行车中是否允许主驾使用：0-不允许，1-允许
     */
    private int supportDrivingUser;

    /**
     * 更新时间
     */
    private String updateTimeDisplay;

    /**
     * 更新内容
     */
    private String updateDescription;

    /**
     * slogan
     */
    private String appSlogan;

    /**
     * appSign
     */
    private String appSign;

    /**
     * 是否是强制更新
     */
    private boolean forceUpdate;

    /**
     * 是否隐藏
     */
    private boolean hideIcon;

    public ExpandAppInfo() {
    }

    public ExpandAppInfo(int type, String packageName, String apkName, String apkIcon, String apkSize, String versionName, long versionCode,
        String categoryName, int categoryId, String appVersionId, int isDualAudio, int supportDrivingPassengerUser, int supportDrivingUser,
        String updateTimeDisplay, String updateDescription, String appSlogan, String appSign, boolean update, boolean forceUpdate,
        boolean hideIcon) {
        super(type, packageName, apkName, apkIcon, apkSize, versionName, versionCode);
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.appVersionId = appVersionId;
        this.isDualAudio = isDualAudio;
        this.supportDrivingPassengerUser = supportDrivingPassengerUser;
        this.supportDrivingUser = supportDrivingUser;
        this.updateTimeDisplay = updateTimeDisplay;
        this.updateDescription = updateDescription;
        this.appSlogan = appSlogan;
        this.update = update;
        this.appSign = appSign;
        this.forceUpdate = forceUpdate;
        this.hideIcon = hideIcon;
    }

    protected ExpandAppInfo(Parcel in) {
        super(in);
        categoryName = in.readString();
        categoryId = in.readInt();
        appVersionId = in.readString();
        isDualAudio = in.readInt();
        supportDrivingPassengerUser = in.readInt();
        supportDrivingUser = in.readInt();
        updateTimeDisplay = in.readString();
        updateDescription = in.readString();
        appSlogan = in.readString();
        appSign = in.readString();
        update = in.readByte() != 0;
        forceUpdate = in.readByte() != 0;
        hideIcon = in.readByte() != 0;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAppVersionId() {
        return appVersionId;
    }

    public void setAppVersionId(String appVersionId) {
        this.appVersionId = appVersionId;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public int getIsDualAudio() {
        return isDualAudio;
    }

    public void setIsDualAudio(int isDualAudio) {
        this.isDualAudio = isDualAudio;
    }

    public int getSupportDrivingPassengerUser() {
        return supportDrivingPassengerUser;
    }

    public void setSupportDrivingPassengerUser(int supportDrivingPassengerUser) {
        this.supportDrivingPassengerUser = supportDrivingPassengerUser;
    }

    public int getSupportDrivingUser() {
        return supportDrivingUser;
    }

    public void setSupportDrivingUser(int supportDrivingUser) {
        this.supportDrivingUser = supportDrivingUser;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUpdateTimeDisplay() {
        return updateTimeDisplay;
    }

    public void setUpdateTimeDisplay(String updateTimeDisplay) {
        this.updateTimeDisplay = updateTimeDisplay;
    }

    public String getUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(String updateDescription) {
        this.updateDescription = updateDescription;
    }

    public String getAppSlogan() {
        return appSlogan;
    }

    public void setAppSlogan(String appSlogan) {
        this.appSlogan = appSlogan;
    }

    public String getAppSign() {
        return appSign;
    }

    public void setAppSign(String appSign) {
        this.appSign = appSign;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isHideIcon() {
        return hideIcon;
    }

    public void setHideIcon(boolean hideIcon) {
        this.hideIcon = hideIcon;
    }

    @Override
    public String toString() {
        return "ExpandAppInfo{"
            + "category='"
            + categoryName
            + '\''
            + ", productId='"
            + appVersionId
            + '\''
            + ", update="
            + update
            + "} "
            + super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(categoryName);
        dest.writeInt(categoryId);
        dest.writeString(appVersionId);
        dest.writeInt(isDualAudio);
        dest.writeInt(supportDrivingPassengerUser);
        dest.writeInt(supportDrivingUser);
        dest.writeString(updateTimeDisplay);
        dest.writeString(updateDescription);
        dest.writeString(appSlogan);
        dest.writeString(appSign);
        dest.writeByte((byte) (update ? 1 : 0));
        dest.writeByte((byte) (forceUpdate ? 1 : 0));
        dest.writeByte((byte) (hideIcon ? 1 : 0));
    }
}
package com.zeekrlife.market.update;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.market.update.constant.TaskState;
import org.json.JSONObject;

/**
 * @author
 */
public class AppTaskInfo extends ITaskInfo implements Parcelable {

    public static final Creator<AppTaskInfo> CREATOR = new Creator<AppTaskInfo>() {
        @Override
        public AppTaskInfo createFromParcel(Parcel in) {
            return new AppTaskInfo(in);
        }

        @Override
        public AppTaskInfo[] newArray(int size) {
            return new AppTaskInfo[size];
        }
    };

    @TaskState
    private int state;

    private int errorCode;

    private String appName;

    private String appIcon;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 版本号
     */
    private long versionCode;

    /**
     * 是否强制更新
     */
    private boolean forcedUpdate;

    public AppTaskInfo() {
    }

    public AppTaskInfo(@TaskState int state) {
        this.state = state;
    }

    protected AppTaskInfo(Parcel in) {
        super(in);
        state = in.readInt();
        errorCode = in.readInt();
        packageName = in.readString();
        versionName = in.readString();
        versionCode = in.readLong();
        forcedUpdate = in.readByte() != 0;
    }

    public void setData(ITaskInfo taskInfo) {
        this.id = taskInfo.id;
        this.url = taskInfo.url;
        this.path = taskInfo.path;
        this.hash = taskInfo.hash;
        this.type = taskInfo.type;
        this.expand = taskInfo.expand;
        this.status = taskInfo.status;
        this.soFar = taskInfo.soFar;
        this.total = taskInfo.total;
        this.installProgress = taskInfo.installProgress;
        analysisExpand(this.expand);
    }

    private void analysisExpand(String expand) {
        try {
            if (expand == null || expand.length() == 0) {
                return;
            }
            JSONObject appInfoObject = new JSONObject(expand);
            setAppName(getString(appInfoObject, "apkName"));
            setAppIcon(getString(appInfoObject, "apkIcon"));
            setPackageName(getString(appInfoObject, "packageName"));
            setVersionName(getString(appInfoObject, "versionName"));
            setVersionCode(getLong(appInfoObject, "versionCode"));
            setForcedUpdate(getBoolean(appInfoObject, "forceUpdate"));
        } catch (Exception e) {
            Log.e("AppTaskInfo", "e -> " + e.getMessage());
        }
    }

    private String getString(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private long getLong(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getLong(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private Boolean getBoolean(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getBoolean(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public boolean isForcedUpdate() {
        return forcedUpdate;
    }

    public void setForcedUpdate(boolean forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "AppTaskInfo{"
            + "state="
            + state
            + ", errorCode="
            + errorCode
            + ", packageName "
            + packageName
            + ", versionName "
            + versionName
            + ", versionCode "
            + versionCode
            + ", forcedUpdate "
            + forcedUpdate
            + ", appName "
            + appName
            + ", appIcon "
            + appIcon
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
        dest.writeInt(state);
        dest.writeInt(errorCode);
        dest.writeString(packageName);
        dest.writeString(versionName);
        dest.writeLong(versionCode);
        dest.writeByte((byte) (forcedUpdate ? 1 : 0));
    }
}

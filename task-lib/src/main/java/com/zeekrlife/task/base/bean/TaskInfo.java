package com.zeekrlife.task.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.task.base.constant.TaskState;

public class TaskInfo extends ITaskInfo implements Parcelable {
    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
        @Override
        public TaskInfo createFromParcel(Parcel in) {
            return new TaskInfo(in);
        }

        @Override
        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }
    };

    @TaskState
    private int state;
    /**
     * @see TaskErrorCode
     */
    private int errorCode;

    public TaskInfo() {
    }

    public TaskInfo(@TaskState int state) {
        this.state = state;
    }

    protected TaskInfo(Parcel in) {
        super(in);
        state = in.readInt();
        errorCode = in.readInt();
    }

    public void setData(ITaskInfo taskInfo) {
        this.id = taskInfo.id;
        this.url = taskInfo.url;
        this.path = taskInfo.path;
        this.hash = taskInfo.hash;
        this.apkSha256 = taskInfo.apkSha256;
        this.type = taskInfo.type;
        this.expand = taskInfo.expand;
        this.status = taskInfo.status;
        this.soFar = taskInfo.soFar;
        this.total = taskInfo.total;
        this.installProgress = taskInfo.installProgress;
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

    @Override
    public String toString() {
        return "TaskInfo{" +
                "state=" + state +
                ", errorCode=" + errorCode +
                "} " + super.toString();
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
    }
}

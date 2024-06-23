// ITaskCallback.aidl
package com.zeekrlife.market.task;

import com.zeekrlife.market.task.ITaskInfo;

interface ITaskCallback {

    void onTaskAdded(in ITaskInfo taskInfo);

    void onTaskRemoved(in ITaskInfo taskInfo);
}

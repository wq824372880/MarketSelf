// ITaskService.aidl
package com.zeekrlife.market.task;

import com.zeekrlife.market.task.IArrangeCallback;
import com.zeekrlife.market.task.ITaskCallback;
import com.zeekrlife.market.task.ITaskInfo;

interface ITaskService {

    List<ITaskInfo> getTaskList();

    ITaskInfo getTask(String taskId);

    boolean addTask(in ITaskInfo taskInfo);

    boolean removeTask(String taskId);

    boolean registerTaskCallback(in ITaskCallback callback);

    boolean unregisterTaskCallback(in ITaskCallback callback);

    boolean pauseDownload(String taskId);

    boolean resumeDownload(String taskId);

    boolean registerArrangeCallback(in IArrangeCallback callback);

    boolean unregisterArrangeCallback(in IArrangeCallback callback);
}

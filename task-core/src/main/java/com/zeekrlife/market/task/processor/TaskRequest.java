package com.zeekrlife.market.task.processor;

import com.zeekrlife.market.task.data.source.TaskEntity;

public class TaskRequest {

    public TaskEntity taskInfo;

    public TaskRequest(TaskEntity taskInfo) {
        this.taskInfo = taskInfo;
    }
}

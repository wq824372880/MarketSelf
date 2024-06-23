package com.zeekrlife.market.task.data.source;

import java.util.List;


public interface TasksDataSource {

    List<TaskEntity> getTasks();

    TaskEntity getTask(String taskId);

    void addTask(TaskEntity taskEntity);

    void removeTask(TaskEntity taskEntity);

    void updateTask(TaskEntity taskEntity);
}

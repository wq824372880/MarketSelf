package com.zeekrlife.market.task.data.source.cache;

import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.data.source.TasksDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TasksCacheDataSource implements TasksDataSource {
    private final Map<String, TaskEntity> tasksMap = new ConcurrentHashMap<>();

    /**
     * 获取所有任务实体的列表。
     * 这个方法会返回当前任务管理器中所有任务的一个深拷贝列表。
     *
     * @return 返回一个包含所有任务实体的列表。列表中的元素是不可变的。
     */
    @Override
    public List<TaskEntity> getTasks() {
        // 从tasksMap中获取所有任务实体并转换为列表返回
        return new ArrayList<>(tasksMap.values());
    }

    /**
     * 根据任务ID获取任务实体。
     *
     * @param taskId 任务的唯一标识符。
     * @return 返回对应任务ID的任务实体，如果不存在，则返回null。
     */
    @Override
    public TaskEntity getTask(String taskId) {
        // 从任务映射中获取指定ID的任务实体
        return tasksMap.get(taskId);
    }


    /**
     * 向任务集合中添加一个新的任务实体。
     * 该方法会通过任务实体的ID将任务实体添加到内部的任务映射表中。
     *
     * @param taskEntity 要添加的任务实体，不应为null。
     *                   任务实体必须包含有效的ID属性。
     */
    @Override
    public void addTask(TaskEntity taskEntity) {
        // 将任务实体添加到任务映射表中，以便通过ID快速访问
        tasksMap.put(taskEntity.getId(), taskEntity);
    }


    /**
     * 从任务集合中移除指定的任务实体。
     *
     * @param taskEntity 要移除的任务实体，不可为null。
     *                   该实体的id将被用于在任务映射中查找并移除对应的任务。
     */
    @Override
    public void removeTask(TaskEntity taskEntity) {
        // 通过任务实体的id从任务映射中移除对应的任务实体
        tasksMap.remove(taskEntity.getId());
    }


    /**
     * 更新任务实体。将给定的任务实体添加到任务映射中，通过任务的ID作为键。
     * 如果该ID已经存在映射中，则会用新的任务实体替换原有的实体。
     *
     * @param taskEntity 需要更新的任务实体，不应为null。
     */
    @Override
    public void updateTask(TaskEntity taskEntity) {
        // 将任务实体添加到任务映射中，用任务ID作为键
        tasksMap.put(taskEntity.getId(), taskEntity);
    }

}

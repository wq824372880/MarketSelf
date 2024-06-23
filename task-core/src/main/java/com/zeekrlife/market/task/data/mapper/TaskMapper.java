package com.zeekrlife.market.task.data.mapper;


import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.market.task.data.source.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class TaskMapper {

    /**
     * 将 AIDL 接口中的任务信息转换为实体类 TaskEntity。
     *
     * @param taskInfo ITaskInfo 接口实例，包含任务的详细信息。
     * @return TaskEntity 实体类实例，包含了从 AIDL 接口获取的任务信息。
     */
    public static TaskEntity aidlToEntity(ITaskInfo taskInfo) {
        // 创建一个空的 TaskEntity 实例
        TaskEntity taskEntity = new TaskEntity("");
        // 设置任务的 URL
        taskEntity.setUrl(taskInfo.url);
        // 设置任务的路径
        taskEntity.setPath(taskInfo.path);
        // 设置任务的哈希值
        taskEntity.setHash(taskInfo.hash);
        // 设置任务的 APK SHA256 校验和
        taskEntity.setApkSha256(taskInfo.apkSha256);
        // 设置任务的类型
        taskEntity.setType(taskInfo.type);
        // 设置任务的扩展信息
        taskEntity.setExpand(taskInfo.expand);
        return taskEntity;
    }

    /**
     * 将 TaskEntity 实例转换为 ITaskInfo 实例。
     *
     * @param taskEntity 任务实体类，包含任务的各种属性信息。
     * @return 返回转换后的 ITaskInfo 实例，包含了任务的各个属性。
     */
    public static ITaskInfo entityToAidl(TaskEntity taskEntity) {
        ITaskInfo taskInfo = new ITaskInfo();
        // 从TaskEntity中提取信息并设置到ITaskInfo中
        taskInfo.id = taskEntity.getId();
        taskInfo.url = taskEntity.getUrl();
        taskInfo.path = taskEntity.getPath();
        taskInfo.hash = taskEntity.getHash();
        taskInfo.apkSha256 = taskEntity.getApkSha256();
        taskInfo.type = taskEntity.getType();
        taskInfo.expand = taskEntity.getExpand();
        taskInfo.status = taskEntity.getStatus();
        taskInfo.total = taskEntity.getTotalBytes();
        taskInfo.soFar = taskEntity.getSoFarBytes();
        taskInfo.installProgress = taskEntity.getInstallProgress();
        return taskInfo;
    }

    /**
     * 将 AIDL 接口列表转换为实体列表。
     *
     * @param taskInfoList 包含 AIDL 中定义的任务信息对象的列表。
     * @return 转换后包含 TaskEntity 实体的列表。
     */
    public static List<TaskEntity> aidlListToEntityList(List<ITaskInfo> taskInfoList) {
        // 创建一个空的实体列表，用于存放转换后的实体对象
        List<TaskEntity> taskEntityList = new ArrayList<>();
        // 遍历 AIDL 列表，将每个 AIDL 对象转换为实体对象，并添加到实体列表中
        for (ITaskInfo taskInfo : taskInfoList) {
            taskEntityList.add(aidlToEntity(taskInfo));
        }
        return taskEntityList;
    }

    /**
     * 将任务实体列表转换为任务信息接口列表。
     *
     * @param taskEntityList 任务实体列表，包含多个TaskEntity对象。
     * @return 返回一个ITaskInfo接口的列表，每个接口对象是由相应的TaskEntity转换而来。
     */
    public static List<ITaskInfo> entityListToAidlList(List<TaskEntity> taskEntityList) {
        // 初始化任务信息接口列表
        List<ITaskInfo> taskInfoList = new ArrayList<>();
        // 遍历任务实体列表，将每个实体转换为接口对象并添加到任务信息接口列表中
        for (TaskEntity taskEntity : taskEntityList) {
            taskInfoList.add(entityToAidl(taskEntity));
        }
        return taskInfoList;
    }
}

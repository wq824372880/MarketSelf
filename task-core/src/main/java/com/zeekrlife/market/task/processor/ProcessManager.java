package com.zeekrlife.market.task.processor;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.zeekr.basic.Common;
import com.zeekrlife.common.util.EncryptUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.market.task.TaskStatus;
import com.zeekrlife.market.task.TaskType;
import com.zeekrlife.market.task.data.expand.ExpandEntity;
import com.zeekrlife.market.task.data.expand.ExpandType;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.data.source.TasksRepository;
import com.zeekrlife.market.task.processor.handler.DownloadHandler;
import com.zeekrlife.market.task.processor.handler.InstallHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProcessManager {

    /**
     * 获取ProcessManager的单例实例。
     * 这个方法是静态的，可以通过类名直接调用，而不需要先创建类的实例。
     * 它确保了整个应用中只有一个ProcessManager实例存在，符合单例模式的设计原则。
     *
     * @return ProcessManager 单例实例的引用。
     */
    public static ProcessManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    private static class SingletonHolder {
        private static final ProcessManager INSTANCE = new ProcessManager();
    }

    private ProcessManager() {
        File externalFilesDir = Common.app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (externalFilesDir != null) {
            filesDir = externalFilesDir.getAbsolutePath();
        }
    }

    private String filesDir;

    /**
     * 提交一个任务实体到任务仓库。
     * 如果任务不存在，则将其添加为新任务并处理；
     * 如果任务已存在，根据任务的状态决定是更新任务信息并继续处理，还是不进行处理。
     *
     * @param taskEntity 任务实体，包含任务的各种属性信息。
     * @return boolean 如果任务成功提交（即新任务添加或现有任务成功更新并处理），返回true；如果任务不存在或正在处理中，返回false。
     */
    public boolean submit(final TaskEntity taskEntity) {
        amend(taskEntity); // 校正任务实体信息
        TasksRepository.getInstance().addTask(taskEntity); // 将任务添加到任务仓库
        final TaskEntity task = TasksRepository.getInstance().getTask(taskEntity.getId()); // 从任务仓库获取相同ID的任务实体

        // 判断任务是否已经存在
        if (task == taskEntity) {
            // 任务不存在，处理一个新任务
            process(task);
            return true;
        } else {
            if (task == null) {
                // 任务实体不存在，返回false
                return false;
            }
            // 任务已存在，判断任务是否正在进行中
            final int status = task.getStatus();
            if (status == TaskStatus.DOWNLOAD_PENDING
                    || status == TaskStatus.DOWNLOAD_STARTED
                    || status == TaskStatus.DOWNLOAD_CONNECTED
                    || status == TaskStatus.DOWNLOAD_PROGRESS
                    || status == TaskStatus.INSTALL_PENDING
                    || status == TaskStatus.INSTALL_STARTED
                    || status == TaskStatus.INSTALL_PROGRESS) {
                // 任务正在进行中，不进行处理，返回false
                return false;
            } else {
                // 任务非进行中，更新任务信息后继续处理
                task.setType(taskEntity.getType());
                task.setUrl(taskEntity.getUrl());
                TasksRepository.getInstance().updateTask(task); // 更新任务仓库中的任务信息
                process(task); // 处理任务
                return true;
            }
        }
    }


    /**
     * 修改任务实体的ID和路径。
     * 该方法首先根据任务类型处理任务ID的生成和设置，针对不同的扩展类型处理文件的子目录和后缀名，
     * 最后更新任务实体的ID和路径字段。
     *
     * @param taskEntity 任务实体，不可为null。
     */
    private void amend(final TaskEntity taskEntity) {
        String id;
        // 根据任务类型是安装还是其他，来处理ID的生成逻辑
        if (taskEntity.getType() == TaskType.INSTALL) {
            id = EncryptUtils.encryptMD5ToString(taskEntity.getPath());
            taskEntity.setId(id);
            return;
        }

        int expandType;
        String expandStr = taskEntity.getExpand();
        // 处理扩展信息，判断是否为空或者解析失败，从而决定扩展类型
        if (TextUtils.isEmpty(expandStr)) {
            expandType = ExpandType.APK;
        } else {
            ExpandEntity expand = GsonUtils.fromJson(expandStr, ExpandEntity.class);
            if (expand == null) {
                expandType = ExpandType.APK;
            } else {
                expandType = expand.getType();
            }
        }

        // 根据扩展类型决定文件存储的子目录和后缀名
        String subDir;
        String suffix;
        switch (expandType) {
            case ExpandType.APK:
                subDir = "apk";
                suffix = ".apk";
                break;
            case ExpandType.SPECIAL:
                subDir = "special";
                suffix = ".apk";
                break;
            case ExpandType.OVERLAY:
                subDir = "overlay";
                suffix = ".overlay";
                break;
            case ExpandType.FRAMEWORK:
                subDir = "framework";
                suffix = ".fwk";
                break;
            default:
                subDir = "file";
                suffix = "";
                break;
        }

        // 解析任务URL获取文件路径，并生成最终的文件存储路径
        String uriPath = Uri.parse(taskEntity.getUrl()).getPath();

        String path = filesDir + File.separator + subDir + File.separator
                + EncryptUtils.encryptMD5ToString(uriPath) + suffix;
        id = EncryptUtils.encryptMD5ToString(path);
        taskEntity.setId(id);
        taskEntity.setPath(path);
    }

    /**
     * 处理给定的任务实体。
     * 根据任务的类型和状态，选择合适的任务处理器进行处理。
     *
     * @param task 任务实体，包含任务的基本信息和状态。
     */
    private void process(final TaskEntity task) {
        // 初始化任务处理器列表
        List<TaskHandler> taskHandlerList = new ArrayList<>();
        // 获取任务类型
        final int type = task.getType();
        // 根据任务类型选择处理器
        if (type == TaskType.DOWNLOAD) {
            taskHandlerList.add(new DownloadHandler());
        } else if (type == TaskType.INSTALL) {
            taskHandlerList.add(new InstallHandler());
        } else {
            // 对于其他类型的任务，根据任务状态决定处理器
            if (task.getStatus() == TaskStatus.DOWNLOAD_COMPLETED) {
                taskHandlerList.add(new InstallHandler());
            } else {
                taskHandlerList.add(new DownloadHandler());
                taskHandlerList.add(new InstallHandler());
            }
        }
        // 创建任务请求对象
        TaskRequest taskRequest = new TaskRequest(task);
        // 初始化处理链
        ProcessChain taskChain = new ProcessChain(taskHandlerList, taskRequest);
        // 启动处理链
        taskChain.proceed(taskRequest);
    }


    public void shutdown(String taskId) {

    }
}

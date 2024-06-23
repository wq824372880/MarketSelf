package com.zeekrlife.market.task.data.source;

import android.content.Context;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.FileUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.market.task.TaskStatus;
import com.zeekrlife.market.task.data.expand.AppEntity;
import com.zeekrlife.market.task.data.expand.ExpandEntity;
import com.zeekrlife.market.task.data.expand.ExpandType;
import com.zeekrlife.market.task.data.source.cache.TasksCacheDataSource;
import com.zeekrlife.market.task.data.source.local.TasksLocalDataSource;
import com.zeekrlife.market.task.download.DownloadManager;

import java.util.List;

public class TasksRepository implements TasksDataSource {
    private volatile static TasksRepository INSTANCE = null;
    private TasksDataSource cacheDataSource;
    private TasksDataSource localDataSource;

    public static TasksRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (TasksRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksRepository();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private TasksRepository() {
        cacheDataSource = new TasksCacheDataSource();
        localDataSource = new TasksLocalDataSource();
    }

    //数据初始化和状态修正
    public void init(Context context) {
        try {
            List<TaskEntity> tasks = localDataSource.getTasks();
            for (TaskEntity task : tasks) {
                switch (task.getStatus()) {
                    case TaskStatus.DOWNLOAD_STARTED:
                    case TaskStatus.DOWNLOAD_PAUSED:
                    case TaskStatus.DOWNLOAD_CONNECTED:
                    case TaskStatus.DOWNLOAD_PROGRESS:
                        task.setTotalBytes(DownloadManager.get().getTotalLength(task));
                        task.setSoFarBytes(DownloadManager.get().getCurrentOffset(task));
                        task.setStatus(TaskStatus.DOWNLOAD_PAUSED);
                        cacheDataSource.addTask(task);
                        break;
                    case TaskStatus.DOWNLOAD_COMPLETED:
                    case TaskStatus.INSTALL_PENDING:
                    case TaskStatus.INSTALL_STARTED:
                    case TaskStatus.INSTALL_PROGRESS:
                        if (DownloadManager.get().getStatus(task) == TaskStatus.DOWNLOAD_COMPLETED) {
                            try {
                                ExpandEntity expandEntity = GsonUtils.fromJson(task.getExpand(), ExpandEntity.class);
                                if (expandEntity.getType() == ExpandType.APK) {
                                    AppEntity appEntity = GsonUtils.fromJson(task.getExpand(), AppEntity.class);
                                    if (context.getPackageName().equals(appEntity.getPackageName())
                                            && context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode
                                            >= appEntity.getVersionCode()) {
                                        DownloadManager.get().clear(task);
                                        localDataSource.removeTask(task);
                                        return;
                                    }
                                } else if (expandEntity.getType() == ExpandType.OVERLAY || expandEntity.getType() == ExpandType.FRAMEWORK) {
                                    task.setInstallProgress(task.getInstallProgress());
                                }
                            } catch (Throwable throwable) {
                                //
                            }
                            long fileLength = FileUtils.INSTANCE.getFileLength(task.getPath());
                            task.setTotalBytes(fileLength);
                            task.setSoFarBytes(fileLength);
                            task.setStatus(TaskStatus.DOWNLOAD_COMPLETED);
                            cacheDataSource.addTask(task);
                        } else {
                            DownloadManager.get().clear(task);
                            localDataSource.removeTask(task);
                        }
                        break;
                    default:
                        DownloadManager.get().clear(task);
                        localDataSource.removeTask(task);
                        break;
                }
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    /**
     * 获取所有任务实体的列表。
     * <p>此方法通过调用缓存数据源的getTasks方法来获取任务列表。</p>
     *
     * @return 返回一个包含所有任务实体的列表。列表中的元素类型为TaskEntity。
     */
    @Override
    public List<TaskEntity> getTasks() {
        // 从缓存数据源获取任务列表
        return cacheDataSource.getTasks();
    }

    /**
     * 获取指定任务ID的任务实体。
     *
     * @param taskId 任务的唯一标识符。
     * @return 返回对应任务ID的任务实体，如果不存在，则返回null。
     */
    @Override
    public TaskEntity getTask(String taskId) {
        // 从缓存数据源中获取指定任务ID的任务实体
        return cacheDataSource.getTask(taskId);
    }


    @Override
    public void addTask(TaskEntity taskEntity) {
        try {
            if (cacheDataSource.getTask(taskEntity.getId()) == null) {
                cacheDataSource.addTask(taskEntity);
                localDataSource.addTask(taskEntity);
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    @Override
    public void removeTask(TaskEntity taskEntity) {
        try {
            cacheDataSource.removeTask(taskEntity);
            localDataSource.removeTask(taskEntity);
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    /**
     * 更新任务信息。
     * 该方法会尝试更新任务实体的信息。首先尝试通过缓存数据源更新任务信息，如果失败，则尝试通过本地数据源进行更新。
     * 注意：当前代码中的缓存数据源更新任务实体的代码行被注释掉了。
     *
     * @param taskEntity 需要更新的任务实体。
     */
    @Override
    public void updateTask(TaskEntity taskEntity) {
        try {
            // 尝试通过本地数据源更新任务信息
            localDataSource.updateTask(taskEntity);
        } catch (Exception e) {
            // 如果更新过程中出现异常，记录异常堆栈信息
            CommExtKt.logStackTrace(e);
        }
    }
}

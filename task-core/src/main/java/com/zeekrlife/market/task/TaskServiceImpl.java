package com.zeekrlife.market.task;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.market.task.data.mapper.TaskMapper;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.data.source.TasksRepository;
import com.zeekrlife.market.task.download.DownloadManager;
import com.zeekrlife.market.task.processor.ProcessManager;
import com.zeekrlife.net.interception.logging.util.XLog;

import java.util.List;

/**
 * This class provides an implementation for the ITaskService interface,
 * offering a series of methods to manage tasks, such as getting task lists,
 * adding, removing, pausing, and resuming tasks.
 */
public class TaskServiceImpl extends ITaskService.Stub {
    private static final String TAG = "TaskServiceImpl";

    /**
     * Constructs the TaskServiceImpl with a context.
     * Initializes the TasksRepository and TaskCallbackImpl.
     *
     * @param context The context used to initialize the task repository.
     */
    public TaskServiceImpl(Context context) {
        TasksRepository.getInstance().init(context);
        TaskCallbackImpl.getInstance().init();
    }

    /**
     * Retrieves the list of all tasks.
     *
     * @return A list of ITaskInfo objects representing the tasks.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public List<ITaskInfo> getTaskList() throws RemoteException {
        try {
            List<TaskEntity> taskList = TasksRepository.getInstance().getTasks();
            if(taskList != null && !taskList.isEmpty()){
                List<ITaskInfo> taskInfoList = TaskMapper.entityListToAidlList(taskList);
                XLog.INSTANCE.d(TAG, "getTaskList() taskList::"+ taskInfoList);
                return taskInfoList;
            }
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "getTaskList:" + Log.getStackTraceString(throwable));
        }
        return null;
    }

    /**
     * Retrieves a specific task by its ID.
     *
     * @param taskId The unique identifier of the task.
     * @return The ITaskInfo object representing the task, or null if not found.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public ITaskInfo getTask(String taskId) throws RemoteException {
        XLog.INSTANCE.d(TAG, "getTask() called with: taskId = [" + taskId + "]");
        try {
            TaskEntity taskEntity = TasksRepository.getInstance().getTask(taskId);
            if (taskEntity == null){
                return null;
            }else {
                return TaskMapper.entityToAidl(taskEntity);
            }
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "getTask:" + Log.getStackTraceString(throwable));
        }
        return null;
    }

    /**
     * Adds a new task to the task list.
     *
     * @param taskInfo The task information to be added.
     * @return true if the task was successfully added, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean addTask(ITaskInfo taskInfo) throws RemoteException {
        XLog.INSTANCE.d(TAG, "addTask() called with: taskInfo = [" + taskInfo + "]");
        if (taskInfo == null) {
            return false;
        }
        TaskEntity task = TaskMapper.aidlToEntity(taskInfo);
        if (ProcessManager.getInstance().submit(task)) {
            TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onTaskAdded, task);
            return true;
        }
        return false;
    }

    /**
     * Removes a task by its ID.
     *
     * @param taskId The unique identifier of the task to be removed.
     * @return true if the task was successfully removed, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean removeTask(String taskId) throws RemoteException {
        XLog.INSTANCE.d(TAG, "removeTask() called with: taskId = [" + taskId + "]");
        try {
            TaskEntity task = TasksRepository.getInstance().getTask(taskId);
            if (task != null) {
                // Installation related tasks cannot be removed.
                if (task.getStatus() == TaskStatus.INSTALL_PENDING
                    || task.getStatus() == TaskStatus.INSTALL_STARTED
                    || task.getStatus() == TaskStatus.INSTALL_PROGRESS
                    || task.getStatus() == TaskStatus.INSTALL_COMPLETED) {
                    return false;
                }
                DownloadManager.get().pause(task);
                TasksRepository.getInstance().removeTask(task);
                DownloadManager.get().clear(task);
                TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onTaskRemoved, task);
                return true;
            }
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "removeTask:" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    /**
     * Registers a callback for task updates.
     *
     * @param callback The callback interface to receive task updates.
     * @return true if the callback was successfully registered, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean registerTaskCallback(ITaskCallback callback) throws RemoteException {
        try {
            return TaskCallbackImpl.getInstance().registerTaskCallback(callback);
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "registerTaskCallback:" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    /**
     * Unregisters a previously registered task callback.
     *
     * @param callback The callback interface to be unregistered.
     * @return true if the callback was successfully unregistered, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean unregisterTaskCallback(ITaskCallback callback) throws RemoteException {
        try {
            return TaskCallbackImpl.getInstance().unregisterTaskCallback(callback);
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "unregisterTaskCallback:" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    /**
     * Pauses the download of a task by its ID.
     *
     * @param taskId The unique identifier of the task to be paused.
     * @return true if the download was successfully paused, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean pauseDownload(String taskId) throws RemoteException {
        XLog.INSTANCE.d(TAG, "pauseDownload() called with: taskId = [" + taskId + "]");
        try {
            TaskEntity task = TasksRepository.getInstance().getTask(taskId);
            if (task != null && task.getStatus() == TaskStatus.DOWNLOAD_PROGRESS) {
                DownloadManager.get().pause(task);
                return true;
            }
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "pauseDownload:" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    /**
     * Resumes the download of a task by its ID.
     *
     * @param taskId The unique identifier of the task to be resumed.
     * @return true if the download was successfully resumed, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean resumeDownload(String taskId) throws RemoteException {
        XLog.INSTANCE.d(TAG, "resumeDownload() called with: taskId = [" + taskId + "]");
        try {
            TaskEntity task = TasksRepository.getInstance().getTask(taskId);
            if (task != null && task.getStatus() == TaskStatus.DOWNLOAD_PAUSED) {
                if (ProcessManager.getInstance().submit(task)) {
                    return true;
                }
            }
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "resumeDownload:" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    /**
     * Registers a callback for arrangement related updates.
     *
     * @param callback The callback interface to receive arrangement updates.
     * @return true if the callback was successfully registered, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean registerArrangeCallback(IArrangeCallback callback) throws RemoteException {
        try {
            return TaskCallbackImpl.getInstance().registerArrangeCallback(callback);
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "registerArrangeCallback:" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    /**
     * Unregisters a previously registered arrange callback.
     *
     * @param callback The callback interface to be unregistered.
     * @return true if the callback was successfully unregistered, false otherwise.
     * @throws RemoteException if the operation fails.
     */
    @Override
    public boolean unregisterArrangeCallback(IArrangeCallback callback) throws RemoteException {
        try {
            return TaskCallbackImpl.getInstance().unregisterArrangeCallback(callback);
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "unregisterArrangeCallback:" + Log.getStackTraceString(throwable));
        }
        return false;
    }
}

package com.zeekrlife.task.base.proxy;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.ApkUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.market.task.IArrangeCallback;
import com.zeekrlife.market.task.ITaskCallback;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.net.interception.logging.util.XLog;
import com.zeekrlife.task.base.bean.AppInfo;
import com.zeekrlife.task.base.bean.ExpandAppInfo;
import com.zeekrlife.task.base.bean.TaskInfo;
import com.zeekrlife.task.base.constant.TaskState;
import com.zeekrlife.task.base.constant.TaskStatus;
import com.zeekrlife.task.base.manager.TaskInfoManager;
import com.zeekrlife.task.base.manager.TaskManager;
import com.zeekrlife.task.base.widget.TaskLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskProxy implements IArrangeCallback, ITaskCallback {
    private static final String TAG = "TaskProxy";

    /**
     * 存放临时taskInfo对象，保证downloadProgress和installProgress回调首次收到时设置数据，
     * 在此之前以及任务结束时移除
     */
    private final Map<String, TaskInfo> tmpTaskInfoMap = new ConcurrentHashMap<>();

    private final List<TaskLayout> taskLayouts = new ArrayList<>();

    private final List<TaskInfoChangeListener> taskInfoListeners = new ArrayList<>();
    /**
     * 临时存放 taskInfo对象,<包名，TaskInfo>
     */
    private Map<String, TaskInfo> pkgToTaskInfoMap = new ConcurrentHashMap<>();

    /**
     * 向任务布局列表中添加一个新的任务布局。
     * 如果该任务布局不存在于列表中，则将其添加。
     * 该方法是同步的，以避免多线程环境下的并发问题。
     *
     * @param layout 要添加的任务布局对象。
     */
    public synchronized void addTaskLayout(TaskLayout layout) {
        // 检查列表中是否已包含该任务布局，若不包含则添加
        if (!taskLayouts.contains(layout)) {
            taskLayouts.add(layout);
        }
    }


    /**
     * 从任务布局集合中移除指定的任务布局。
     * 这个方法是同步的，以避免多线程环境下的并发问题。
     *
     * @param layout 要被移除的任务布局对象。
     *              该参数不可为null，否则可能导致异常。
     * @return 无返回值。
     */
    public synchronized void removeTaskLayout(TaskLayout layout) {
        taskLayouts.remove(layout); // 从任务布局集合中移除指定的布局
    }


    /**
     * 向任务信息监听器列表中添加一个新的监听器。如果该监听器尚未被添加，则添加到列表中。
     * 这个方法是同步的，以避免多线程环境下的并发问题。
     *
     * @param listener 要添加的任务信息改变监听器。不能为null。
     */
    public synchronized void addTaskInfoChangeListener(TaskInfoChangeListener listener) {
        // 检查监听器列表中是否已包含该监听器，如果没有则添加
        if (!taskInfoListeners.contains(listener)) {
            taskInfoListeners.add(listener);
        }
    }


    /**
     * 从任务信息监听器列表中移除指定的监听器。
     * 这个方法是同步的，以确保在多线程环境下操作的原子性。
     *
     * @param layout 要被移除的任务信息改变监听器。这是一个实现了TaskInfoChangeListener接口的对象。
     */
    public synchronized void removeTaskInfoChangeListener(TaskInfoChangeListener layout) {
        taskInfoListeners.remove(layout); // 从监听器列表中移除指定的监听器
    }


    /**
     * 当任务信息发生变化时调用此方法，来更新任务的状态和数据，并通知相关的监听器。
     * 这个方法会遍历所有的任务布局，查找与变更的任务信息匹配的任务，更新其状态和数据，
     * 并触发任务布局的任务信息变更回调。接着，它会通知所有任务信息变更监听器关于任务信息的变更。
     * 最后，它会初始化或更新包到任务信息的映射，以便于快速访问任务信息。
     *
     * @param taskInfo 变更后的任务信息对象。
     */
    private void taskInfoChanged(TaskInfo taskInfo) {
        // 遍历任务布局列表，更新匹配任务的状态和数据，并在UI线程上触发任务信息变更回调
        for (TaskLayout layout : taskLayouts) {
            TaskInfo task = layout.getTaskInfo();
            if (task != null && task.hash.equals(taskInfo.hash)) {
                task.setState(taskInfo.getState());
                task.setData(taskInfo);
                task.setErrorCode(taskInfo.getErrorCode());
                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        layout.onTaskInfoChanged(task);
                    }
                });
            }
        }

        // 通知所有任务信息变更监听器
        for (TaskInfoChangeListener listener : taskInfoListeners) {
            listener.onTaskInfoChanged(taskInfo);
        }

        // 初始化或更新包到任务信息的映射
        initPkgToTaskInfoMap();
        try {
            // 尝试从任务信息的扩展字段中解析应用信息，并更新或插入到包到任务信息的映射中
            ExpandAppInfo expandAppInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
            TaskInfo pkgToTaskInfo = pkgToTaskInfoMap.get(expandAppInfo.getPackageName());
            if (pkgToTaskInfo == null) {
                pkgToTaskInfoMap.put(expandAppInfo.getPackageName(), taskInfo);
            } else {
                // 在特定条件下更新已存在的任务信息
                if (taskInfo.getState() == TaskState.DOWNLOAD_PROGRESS && taskInfo.soFar != pkgToTaskInfo.soFar) {
                    pkgToTaskInfo.soFar = taskInfo.soFar;
                } else if (taskInfo.getState() != pkgToTaskInfo.getState()) {
                    pkgToTaskInfoMap.put(expandAppInfo.getPackageName(), taskInfo);
                }
            }
        } catch (Exception e) {
            // 捕获并记录解析异常
            CommExtKt.logStackTrace(e);
        }
    }

    private volatile static TaskProxy instance;

    public static TaskProxy getInstance() {
        if (instance == null) {
            synchronized (TaskProxy.class) {
                if (instance == null) {
                    instance = new TaskProxy();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化函数，用于初始化任务管理器，并注册当前实例作为任务和安排的回调接收者。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @param callback 初始化完成后的回调接口，可选参数。
     */
    public void init(@NonNull Context context, @Nullable TaskManager.OnInitCallback callback) {
        // 初始化任务管理器，并在初始化完成后执行回调
        TaskManager.getInstance().init(context, result -> {
            try {
                // 如果回调非空，则执行回调函数
                if(callback != null) {
                    callback.onInit(result);
                }
            }catch (Exception e) {
                // 捕获并打印异常
                e.printStackTrace();
            }
            // 初始化包到任务信息的映射
            initPkgToTaskInfoMap();
        });

        // 注册当前实例为任务回调接收者
        TaskManager.getInstance().registerTaskCallback(this);
        // 注册当前实例为安排回调接收者
        TaskManager.getInstance().registerArrangeCallback(this);
    }


    /**
     * 释放资源的方法。该方法将当前对象从任务管理器中注销，取消其作为任务回调和安排回调的资格。
     * 这样可以避免内存泄漏，确保资源被正确释放。
     * 注意：该方法不接受任何参数，也不返回任何值。
     */
    public void release() {
        // 从任务管理器中注销当前对象作为任务回调
        TaskManager.getInstance().unregisterTaskCallback(this);
        // 从任务管理器中注销当前对象作为安排回调
        TaskManager.getInstance().unregisterArrangeCallback(this);
    }


    /**
     * 释放所有资源或取消所有任务的方法。
     * 这个方法通过调用TaskManager的静态实例的release方法来实现。
     * 没有参数。
     * 没有返回值。
     */
    public void releaseAll() {
        // 调用任务管理器的实例的release方法来释放所有资源或取消所有任务
        TaskManager.getInstance().release();
    }


    /**
     * 确保服务可用。
     * 此方法会尝试确保关键服务处于可用状态。它通过调用TaskManager的实例方法来实现。
     *
     * @return boolean - 如果服务成功确保可用，则返回true；否则返回false。
     */
    public boolean ensureServiceAvailable() {
        // 调用TaskManager的实例方法来确保服务可用
        return TaskManager.getInstance().ensureServiceAvailable();
    }


    public void openApp(@NonNull Context context, @NonNull TaskInfo taskInfo,int displayId) {
        AppInfo appInfo;
        try {
            appInfo = GsonUtils.fromJson(taskInfo.expand, AppInfo.class);
        } catch (Throwable throwable) {
            XLog.INSTANCE.e(TAG, "parse expand to AppInfo failed:" + Log.getStackTraceString(throwable));
            return;
        }
        String packageName = appInfo.getPackageName();
        if (TextUtils.isEmpty(packageName) || packageName.equals(context.getPackageName())) {
            return;
        }
        ApkUtils.INSTANCE.openAppByPackageName(context, packageName,displayId);
    }

    public List<ITaskInfo> getTaskList() {
        return TaskManager.getInstance().getTaskList();
    }

    public ITaskInfo getTask(@NonNull String taskId) {
        return TaskManager.getInstance().getTask(taskId);
    }

    public boolean addTask(@NonNull TaskInfo taskInfo) {
        return TaskManager.getInstance().addTask(taskInfo);
    }

    public boolean removeTask(@NonNull String taskId) {
        return TaskManager.getInstance().removeTask(taskId);
    }

    public boolean pauseDownload(@NonNull String taskId) {
        return TaskManager.getInstance().pauseDownload(taskId);
    }

    public boolean resumeDownload(@NonNull TaskInfo taskInfo) {
        return TaskManager.getInstance().resumeDownload(taskInfo.id);
    }

    @Override
    public void onDownloadPending(String taskId) {
        XLog.INSTANCE.d(TAG, "onDownloadPending() called with: taskId = [" + taskId + "]");

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            TaskInfo taskInfo = new TaskInfo(TaskState.DOWNLOAD_PENDING);
            taskInfo.setData(task);
            saveTaskInfo(taskInfo);

            taskInfoChanged(taskInfo);
        }
    }

    /**
     * 当下载开始时被调用。
     *
     * @param taskId 下载任务的唯一标识符。
     */
    @Override
    public void onDownloadStarted(String taskId) {
        // 记录日志，显示下载开始时被调用，并打印出任务ID
        XLog.INSTANCE.d(TAG, "onDownloadStarted() called with: taskId = [" + taskId + "]");

        // 根据任务ID获取任务信息
        ITaskInfo task = getTask(taskId);
        if (task != null) {
            // 创建任务信息对象，设置状态为下载开始，并设置任务数据
            TaskInfo taskInfo = new TaskInfo(TaskState.DOWNLOAD_STARTED);
            taskInfo.setData(task);

            // 保存更新后的任务信息
            saveTaskInfo(taskInfo);

            // 发送任务信息变更事件，通知其他监听者
            // EventBus.getDefault().post(taskInfo);
            taskInfoChanged(taskInfo);
        }
    }


    /**
     * 当下载连接建立时被调用。
     * 这个方法会记录连接建立时的状态，并更新任务信息。
     *
     * @param taskId 下载任务的唯一标识符。
     * @param soFarBytes 到目前为止已经下载的字节数。
     * @param totalBytes 下载文件的总字节数。
     */
    @Override
    public void onDownloadConnected(String taskId, long soFarBytes, long totalBytes) {
        // 使用日志记录函数被调用时的参数信息
        XLog.INSTANCE.d(TAG, "onDownloadConnected() called with: taskId = ["
                + taskId
                + "], soFarBytes = ["
                + soFarBytes
                + "], totalBytes = ["
                + totalBytes
                + "]");

        // 从临时任务信息映射中移除当前任务
        tmpTaskInfoMap.remove(taskId);

        // 尝试获取任务对象
        ITaskInfo task = getTask(taskId);
        if (task != null) {
            // 创建一个新的任务信息对象，表示下载已经连接
            TaskInfo taskInfo = new TaskInfo(TaskState.DOWNLOAD_CONNECTED);
            taskInfo.setData(task);

            // 保存更新后的任务信息
            saveTaskInfo(taskInfo);

            // 注释掉的代码原意是使用EventBus发送任务信息变化的通知
            // taskInfoChanged(taskInfo) 是一个替代方案，用于直接处理任务信息变化
            taskInfoChanged(taskInfo);
        }
    }


    /**
     * 下载进度更新回调方法。
     *
     * @param taskId 任务ID，用于标识当前下载任务。
     * @param soFarBytes 到目前为止已经下载的字节数。
     * @param totalBytes 任务总字节数。
     * 该方法首先根据taskId检查临时任务信息映射中是否存在对应的任务信息。如果不存在，则尝试从特定方法获取任务，
     * 如果获取成功，则创建新的任务信息并保存到临时任务信息映射及持久化存储中。如果任务信息已存在，则更新其状态和进度。
     * 最后，如果任务信息非空，则将其变化通知给相关监听者。
     */
    @Override
    public void onDownloadProgress(String taskId, long soFarBytes, long totalBytes) {
        // 根据taskId获取当前任务的详细信息
        TaskInfo taskInfo = tmpTaskInfoMap.get(taskId);
        if (taskInfo == null) {
            // 如果任务信息不存在，则尝试从特定来源获取任务
            ITaskInfo task = getTask(taskId);
            if (task != null) {
                // 创建新的任务信息并设置为下载进行中状态，然后保存
                taskInfo = new TaskInfo(TaskState.DOWNLOAD_PROGRESS);
                taskInfo.setData(task);

                saveTaskInfo(taskInfo);
                tmpTaskInfoMap.put(taskId, taskInfo);
            }
        } else {
            // 如果任务信息已存在，则更新其状态和下载进度
            taskInfo.status = TaskStatus.DOWNLOAD_PROGRESS;
            taskInfo.soFar = soFarBytes;
            taskInfo.total = totalBytes;
        }

        // 如果存在有效的任务信息，则通知任务信息发生变化
        if (taskInfo != null) {
            taskInfoChanged(taskInfo);
        }
    }


    /**
     * 当下载任务完成时被调用。
     *
     * @param taskId 下载任务的唯一标识符。
     */
    @Override
    public void onDownloadCompleted(String taskId) {
        // 记录日志，显示函数被调用以及传入的taskId
        XLog.INSTANCE.d(TAG, "onDownloadCompleted() called with: taskId = [" + taskId + "]");

        // 根据taskId获取对应的下载任务
        ITaskInfo task = getTask(taskId);
        if (task != null) {
            // 创建一个新的TaskInfo实例，表示下载已完成
            TaskInfo taskInfo = new TaskInfo(TaskState.DOWNLOAD_COMPLETED);
            taskInfo.setData(task);

            // 保存更新后的任务信息
            saveTaskInfo(taskInfo);

            // 触发任务信息变更的事件，通知其他监听者
            taskInfoChanged(taskInfo);

            // 统计下载完成的相关数据
            statisticsDownload(taskInfo);
        }
    }


    /**
     * 当下载暂停时被调用。
     *
     * @param taskId 下载任务的唯一标识符。
     */
    @Override
    public void onDownloadPaused(String taskId) {
        // 记录日志，显示函数被调用以及传入的taskId
        XLog.INSTANCE.d(TAG, "onDownloadPaused() called with: taskId = [" + taskId + "]");

        // 根据taskId获取对应的下载任务
        ITaskInfo task = getTask(taskId);
        if (task != null) {
            // 创建一个新的TaskInfo实例，表示任务已暂停
            TaskInfo taskInfo = new TaskInfo(TaskState.DOWNLOAD_PAUSED);
            taskInfo.setData(task);

            // 保存任务信息
            saveTaskInfo(taskInfo);

            // 发送任务信息变更事件，通知其他监听者
            //EventBus.getDefault().post(taskInfo);
            taskInfoChanged(taskInfo);
        }
    }


    /**
     * 当下载任务发生错误时调用此方法。
     *
     * @param taskId 表示下载任务的唯一标识符。
     * @param errorCode 表示下载过程中遇到的错误码。
     */
    @Override
    public void onDownloadError(String taskId, int errorCode) {
        // 记录下载错误日志
        XLog.INSTANCE.w(TAG, "onDownloadError() called with: taskId = [" + taskId + "], errorCode = [" + errorCode + "]");

        // 从临时任务信息映射中移除当前任务
        tmpTaskInfoMap.remove(taskId);

        // 尝试获取任务信息
        ITaskInfo task = getTask(taskId);
        if (task != null) {
            // 创建任务信息，标记为下载错误状态，并设置错误码
            TaskInfo taskInfo = new TaskInfo(TaskState.DOWNLOAD_ERROR);
            taskInfo.setData(task);
            taskInfo.setErrorCode(errorCode);

            // 保存更新后的任务信息
            saveTaskInfo(taskInfo);

            // 触发任务信息变更事件，而不直接使用EventBus发送消息
            taskInfoChanged(taskInfo);
            // 统计下载任务信息
            statisticsDownload(taskInfo);
        }
    }


    /**
     * 当安装任务处于待处理状态时被调用。
     * 该方法会记录任务处于安装待处理状态，并保存该任务的信息。
     *
     * @param taskId 任务的ID，用于标识待安装的任务。
     */
    @Override
    public void onInstallPending(String taskId) {
        // 记录调用此方法时的日志，包括传入的taskId
        XLog.INSTANCE.d(TAG, "onInstallPending() called with: taskId = [" + taskId + "]");

        // 根据taskId获取相应的任务信息
        ITaskInfo task = getTask(taskId);
        if (task != null) {
            // 创建一个新的任务信息对象，设置任务状态为安装待处理
            TaskInfo taskInfo = new TaskInfo(TaskState.INSTALL_PENDING);
            taskInfo.setData(task); // 将获取到的任务信息设置到新的任务信息对象中

            // 保存更新后的任务信息
            saveTaskInfo(taskInfo);

            // 发送任务信息变更的事件，通知其他监听者
            taskInfoChanged(taskInfo);
        }
    }


    /**
     * 当安装任务开始时被调用。
     * 该方法会记录日志，从临时任务信息映射中移除对应的任务ID，然后根据任务ID获取任务信息，
     * 如果任务信息不为空，则创建一个新的任务信息表示安装已开始，并保存这个新的任务信息。
     * 最后，会通知任务信息发生变化。
     *
     * @param taskId 任务的ID，用于标识特定的任务。
     */
    @Override
    public void onInstallStarted(String taskId) {
        // 记录日志，显示函数被调用以及传入的taskId
        XLog.INSTANCE.d(TAG, "onInstallStarted() called with: taskId = [" + taskId + "]");

        // 从临时任务信息映射中移除指定的taskId
        tmpTaskInfoMap.remove(taskId);

        // 尝试根据taskId获取任务信息
        ITaskInfo task = getTask(taskId);
        if (task != null) {
            // 创建一个新的任务信息对象，表示该任务的安装已经开始
            TaskInfo taskInfo = new TaskInfo(TaskState.INSTALL_STARTED);
            taskInfo.setData(task);

            // 保存更新后的任务信息
            saveTaskInfo(taskInfo);

            // 触发任务信息变更事件，通知其他监听者任务状态已更新
            taskInfoChanged(taskInfo);
        }
    }


    @Override
    public void onInstallProgress(String taskId, float progress) {
        // LogUtils.d(TAG, "onInstallProgress() called with: taskId = [" + taskId + "], progress = [" + progress + "]");

        TaskInfo taskInfo = tmpTaskInfoMap.get(taskId);
        if (taskInfo == null) {
            ITaskInfo task = getTask(taskId);
            if (task != null) {
                taskInfo = new TaskInfo(TaskState.INSTALL_PROGRESS);
                taskInfo.setData(task);

                saveTaskInfo(taskInfo);
                tmpTaskInfoMap.put(taskId, taskInfo);
            }
        } else {
            taskInfo.status = TaskStatus.INSTALL_PROGRESS;
            taskInfo.installProgress = progress;
        }

        //EventBus.getDefault().post(taskInfo);
        if (taskInfo != null) {
            taskInfoChanged(taskInfo);
        }
    }

    @Override
    public void onInstallCompleted(String taskId) {
        XLog.INSTANCE.d(TAG, "onInstallCompleted() called with: taskId = [" + taskId + "]");

        tmpTaskInfoMap.remove(taskId);

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            TaskInfo taskInfo = new TaskInfo(TaskState.INSTALL_COMPLETED);
            taskInfo.setData(task);

            saveTaskInfo(taskInfo);

            taskInfoChanged(taskInfo);
        }
    }

    @Override
    public void onInstallError(String taskId, int errorCode) {
        XLog.INSTANCE.w(TAG, "onInstallError() called with: taskId = [" + taskId + "], errorCode = [" + errorCode + "]");

        tmpTaskInfoMap.remove(taskId);

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            TaskInfo taskInfo = new TaskInfo(TaskState.INSTALL_ERROR);
            taskInfo.setData(task);
            taskInfo.setErrorCode(errorCode);

            saveTaskInfo(taskInfo);
            //EventBus.getDefault().post(taskInfo);
            taskInfoChanged(taskInfo);
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    private void statisticsDownload(@NonNull TaskInfo taskInfo) {

    }

    private void saveTaskInfo(@NonNull TaskInfo taskInfo) {
        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
        TaskInfoManager.getInstance().taskInfoMap.put(
                appInfo.getPackageName() + "_" + appInfo.getVersionCode() + "_" + appInfo.getAppVersionId(), taskInfo);
        //// 处理统一页面多个同包名应用，按钮状态需要刷新情况
        //if (taskInfo.getState() == TaskState.INSTALL_COMPLETED) {
        //    for (Map.Entry<String, TaskInfo> entry : TaskInfoManager.getInstance().taskInfoMap.entrySet()) {
        //        if (entry.getKey().startsWith(appInfo.getPackageName())) {
        //            TaskInfo task = entry.getValue();
        //            if (task != taskInfo) {
        //                ExpandAppInfo app = GsonUtils.fromJson(task.expand, ExpandAppInfo.class);
        //                if (app.getVersionCode() > appInfo.getVersionCode()) {
        //                    task.setState(TaskState.UPDATABLE);
        //                } else {
        //                    task.setState(TaskState.OPENABLE);
        //                }
        //            }
        //        }
        //    }
        //}
    }

    @Override
    public void onTaskAdded(ITaskInfo taskInfo) throws RemoteException {
        try {
            ExpandAppInfo expandAppInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
            getPkgToTaskInfoMap().put(expandAppInfo.getPackageName(), taskToTaskInfo(taskInfo));
        } catch (Exception exception) {
            CommExtKt.logStackTrace(exception);
        }
    }

    /**
     * 当任务被移除时的处理逻辑。
     * 从临时任务信息映射中移除任务，尝试从包名到任务信息的映射中移除相关任务信息，
     * 更新任务状态为取消下载，并保存更新后的任务信息。
     *
     * @param task 被移除的任务信息
     * @throws RemoteException 远程调用异常
     */
    @Override
    public void onTaskRemoved(ITaskInfo task) throws RemoteException {
        XLog.INSTANCE.d(TAG, "onRemoveTask() called with: taskId = [" + task.id + "]");
        tmpTaskInfoMap.remove(task.id); // 从临时任务映射中移除任务
        try {
            // 尝试根据扩展信息中的包名从包名到任务信息的映射中移除任务信息
            getPkgToTaskInfoMap().remove(GsonUtils.fromJson(task.expand, ExpandAppInfo.class).getPackageName());
        } catch (Exception e) {
            CommExtKt.logStackTrace(e); // 捕获并记录异常
        }
        // 更新任务状态并保存
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setState(TaskState.CANCEL_DOWNLOAD);
        taskInfo.setData(task);
        saveTaskInfo(taskInfo);
        taskInfoChanged(taskInfo);
    }

    /**
     * 获取包名到任务信息的映射。
     * 如果映射未初始化，则先进行初始化。
     *
     * @return 包名到任务信息的映射
     */
    public Map<String, TaskInfo> getPkgToTaskInfoMap() {
        initPkgToTaskInfoMap(); // 确保映射已初始化
        return pkgToTaskInfoMap;
    }

    /**
     * 初始化包名到任务信息的映射。
     * 遍历任务列表，将每个任务的包名和相关信息映射起来。
     * 如果任务列表为空或者映射已初始化，则不执行任何操作。
     */
    private void initPkgToTaskInfoMap() {
        if (TaskManager.getInstance().getTaskList() == null) {
            return; // 任务列表为空，直接返回
        }
        if (TaskManager.getInstance().getTaskList().isEmpty()) {
            pkgToTaskInfoMap.clear(); // 任务列表为空，清空映射
            return;
        }
        if (!pkgToTaskInfoMap.isEmpty()) {
            return; // 映射已初始化，不执行任何操作
        }
        pkgToTaskInfoMap = new ConcurrentHashMap<>();
        for (ITaskInfo iTaskInfo : TaskManager.getInstance().getTaskList()) {
            try {
                // 将任务信息解析并映射到包名到任务信息的映射中
                ExpandAppInfo expandAppInfo = GsonUtils.fromJson(iTaskInfo.expand, ExpandAppInfo.class);
                TaskInfo taskInfo = taskToTaskInfo(iTaskInfo);
                pkgToTaskInfoMap.put(expandAppInfo.getPackageName(), taskInfo);
            } catch (Exception e) {
                CommExtKt.logStackTrace(e); // 捕获并记录异常
            }
        }
    }


    /**
     * 将任务对象转换为任务信息对象。
     * 该转换根据任务的状态，设置任务信息对象的状态和数据。
     *
     * @param task 任务对象，实现了ITaskInfo接口。
     * @return 转换后的任务信息对象。
     */
    private TaskInfo taskToTaskInfo(ITaskInfo task) {
        TaskInfo taskInfo = new TaskInfo(task.status);
        // 根据任务状态，设置任务信息对象的状态和数据
        switch (task.status) {
            case TaskStatus.DOWNLOAD_PENDING: {
                taskInfo.setState(TaskState.DOWNLOAD_PENDING);
                taskInfo.setData(task);
                break;
            }
            case TaskStatus.DOWNLOAD_PROGRESS: {
                taskInfo.setState(TaskState.DOWNLOAD_PROGRESS);
                taskInfo.setData(task);
                break;
            }
            case TaskStatus.DOWNLOAD_PAUSED: {
                taskInfo.setState(TaskState.DOWNLOAD_PAUSED);
                taskInfo.setData(task);
                break;
            }
            case TaskStatus.DOWNLOAD_COMPLETED: {
                taskInfo.setState(TaskState.INSTALLABLE);
                taskInfo.setData(task);
                break;
            }
            case TaskStatus.INSTALL_PENDING: {
                taskInfo.setState(TaskState.INSTALL_PENDING);
                taskInfo.setData(task);
                break;
            }
            case TaskStatus.INSTALL_PROGRESS: {
                taskInfo.setState(TaskState.INSTALL_PROGRESS);
                taskInfo.setData(task);
                break;
            }
            default: {
                // 如果任务状态未匹配到任何情况，不设置任务信息对象的状态和数据
            }
        }
        return taskInfo;
    }


    public interface TaskInfoChangeListener {
        void onTaskInfoChanged(TaskInfo task);
    }
}

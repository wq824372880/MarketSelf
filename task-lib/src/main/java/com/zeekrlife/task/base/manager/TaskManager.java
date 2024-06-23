package com.zeekrlife.task.base.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.collection.ArraySet;

import com.zeekrlife.market.task.IArrangeCallback;
import com.zeekrlife.market.task.ITaskCallback;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.market.task.ITaskService;
import com.zeekrlife.net.interception.logging.util.XLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TaskManager extends ITaskService.Stub {
    private static final String TAG = "TaskManager";

    private Context context;

    private ServiceConnection serviceConnection;
    private ITaskService taskService;

    private Set<ITaskCallback> taskCallbackSet;
    private Set<IArrangeCallback> arrangeCallbackSet;

    private volatile static TaskManager instance;
    private Intent intent;

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    public class Connection implements ServiceConnection {
        private OnInitCallback onInitCallback;

        public Connection(OnInitCallback onInitCallback) {
            this.onInitCallback = onInitCallback;
        }

        /**
         * 当与服务连接成功时调用此方法。
         * @param name 表示服务的组件名称。
         * @param service 服务提供的接口，通过该接口与服务进行交互。
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 日志记录：任务服务连接成功
            XLog.INSTANCE.d(TAG, "taskService connected!");

            // 将IBinder转换为接口类型，以便于调用服务的方法
            taskService = Stub.asInterface(service);

            try {
                // 注册安排回调和任务回调到任务服务
                taskService.registerArrangeCallback(arrangeCallback);
                taskService.registerTaskCallback(taskCallback);
            } catch (Throwable throwable) {
                // 如果注册回调失败，记录错误日志
                XLog.INSTANCE.e(TAG, "register arrangeCallback or taskCallback:" + Log.getStackTraceString(throwable));
            }

            // 如果初始化回调不为空，则调用初始化成功回调
            if (onInitCallback != null) {
                onInitCallback.onInit(true);
            }
        }


        /**
         * 当与服务连接断开时调用此方法。
         * @param name 组件名称，表示断开连接的服务的名称。
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 记录服务断开连接的日志
            XLog.INSTANCE.e(TAG, "taskService disconnected!");

            try {
                // 尝试注销安排回调和任务回调
                taskService.unregisterArrangeCallback(arrangeCallback);
                taskService.unregisterTaskCallback(taskCallback);
            } catch (Throwable throwable) {
                // 如果在注销回调时发生异常，记录异常日志
                XLog.INSTANCE.e(TAG, "unregister arrangeCallback or taskCallback:" + Log.getStackTraceString(throwable));
            }

            // 清除任务服务的引用
            taskService = null;
        }

    }

    public interface OnInitCallback {
        void onInit(boolean result);
    }

    /**
     * 初始化函数，用于建立与特定服务的连接并初始化相关回调。
     *
     * @param context 应用的上下文环境，通常是指Activity或者Application对象，用于访问应用全局功能。
     * @param callback 初始化完成后的回调接口，用于通知调用者初始化成功或失败。
     */
    public void init(Context context, OnInitCallback callback) {
        // 检查回调集合是否已经初始化，如果没有则进行初始化
        if (taskCallbackSet == null || arrangeCallbackSet == null) {
            taskCallbackSet = new ArraySet<>();
            arrangeCallbackSet = new ArraySet<>();
        }
        // 使用应用的ApplicationContext，以避免内存泄露和权限问题
        this.context = context.getApplicationContext();
        // 创建一个新的服务连接对象，并传入回调
        this.serviceConnection = new Connection(callback);

        // 检查意图是否已经初始化，如果没有则初始化并设置相应的动作和包名
        if(intent == null){
            intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction("zeekrlife.intent.action.APPSTORE_TASK_SERVICE_START");
        }
        boolean result = false;
        try {
            // 尝试绑定服务，如果成功则会调用服务连接中的onServiceConnected方法
            result = this.context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable throwable) {
            // 捕获绑定服务过程中的异常，并记录日志
            XLog.INSTANCE.e(TAG, "bind taskService:" + Log.getStackTraceString(throwable));
        }

        // 根据绑定服务的结果，记录日志并调用回调函数
        if (result) {
            XLog.INSTANCE.d(TAG, "bind taskService success!");
        } else {
            XLog.INSTANCE.e(TAG, "bind taskService failure!");
            // 如果初始化失败，并且提供了回调，则调用回调函数通知初始化失败
            if (callback != null) {
                callback.onInit(false);
            }
        }
    }


    /**
     * 释放资源的方法。此方法主要用于清理和释放与当前对象相关的资源，包括服务连接、任务回调和排列回调。
     * 它通过解绑服务连接，清空和设置为null任务回调集和排列回调集来达到释放资源的目的。
     * 此方法不接受参数且无返回值。
     */
    public void release() {
        // 如果上下文和服务连接均不为null，则解绑服务连接
        if (context != null && serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
        // 将上下文和服务连接设置为null，以避免内存泄露
        context = null;
        serviceConnection = null;
        // 如果任务回调集不为null，则清空集合并设置为null
        if (taskCallbackSet != null) {
            taskCallbackSet.clear();
            taskCallbackSet = null;
        }
        // 如果排列回调集不为null，则清空集合并设置为null
        if (arrangeCallbackSet != null) {
            arrangeCallbackSet.clear();
            arrangeCallbackSet = null;
        }
    }

    /**
     * 获取任务列表的接口实现。
     * 该方法尝试从任务服务中获取任务列表。如果获取失败，将捕获并记录异常信息。
     *
     * @return List<ITaskInfo> 返回任务列表。如果无法获取任务列表，则返回一个空的列表。
     */
    @Override
    public List<ITaskInfo> getTaskList() {
        try {
            // 尝试从任务服务获取任务列表
            return taskService.getTaskList();
        } catch (Throwable throwable) {
            // 捕获任何异常，并记录异常信息
            XLog.INSTANCE.e(TAG, "getTaskList:" + Log.getStackTraceString(throwable));
        }
        // 如果获取过程出错，返回空列表
        return new ArrayList<>();
    }

    /**
     * 根据任务ID获取任务信息。
     *
     * @param taskId 任务的唯一标识符。
     * @return 返回任务的信息对象，如果获取失败则返回null。
     */
    @Override
    public ITaskInfo getTask(String taskId) {
        try {
            // 尝试通过任务服务获取指定ID的任务信息
            return taskService.getTask(taskId);
        } catch (Throwable throwable) {
            // 捕获任何异常，并记录日志
            XLog.INSTANCE.e(TAG, "getTaskList:" + Log.getStackTraceString(throwable));
        }
        // 如果发生异常则返回null
        return null;
    }

    /**
     * 添加任务到任务服务。
     *
     * @param taskInfo 任务信息对象，实现了ITaskInfo接口，代表一个待添加的任务。
     * @return 返回一个布尔值，若任务成功添加则返回true，否则返回false。
     *         如果在添加任务过程中发生异常，则不会返回true，并且会记录异常信息。
     */
    @Override
    public boolean addTask(ITaskInfo taskInfo) {
        try {
            // 尝试将任务添加到任务服务
            return taskService.addTask(taskInfo);
        } catch (Throwable throwable) {
            // 捕获添加任务过程中可能抛出的任何异常，并记录异常信息
            XLog.INSTANCE.e(TAG, "addTask" + Log.getStackTraceString(throwable));
        }
        // 如果添加任务过程中发生异常，则返回false
        return false;
    }

    /**
     * 从任务服务中移除指定的任务。
     *
     * @param taskId 任务的唯一标识符。
     * @return 如果任务成功被移除则返回true，否则返回false。如果发生异常，也返回false。
     */
    @Override
    public boolean removeTask(String taskId) {
        try {
            // 尝试使用任务服务移除指定ID的任务
            return taskService.removeTask(taskId);
        } catch (Throwable throwable) {
            // 捕获任何在移除任务过程中抛出的异常，并记录日志
            XLog.INSTANCE.e(TAG, "removeTask" + Log.getStackTraceString(throwable));
        }
        // 在异常或其他情况下返回false
        return false;
    }

    /**
     * 注册一个任务回调接口。
     * 该方法用于将一个ITaskCallback接口的实例添加到任务回调集合中。
     * 如果任务回调集合或回调实例为null，或者添加操作失败，则方法返回false。
     * 如果添加成功，则返回true。
     *
     * @param callback ITaskCallback 实例，待注册的任务回调。
     * @return boolean 添加操作是否成功。
     */
    @Override
    public boolean registerTaskCallback(ITaskCallback callback) {
        // 检查任务回调集合和回调对象是否为null，任一为null则直接返回false
        if (taskCallbackSet == null || callback == null) {
            return false;
        }
        // 尝试将回调对象添加到集合中，并返回操作结果
        return taskCallbackSet.add(callback);
    }


    /**
     * 注销指定的任务回调接口。
     * 如果回调集合或指定的回调对象为空，则不进行操作并返回 false。
     * 如果成功从回调集合中移除指定的回调对象，则返回 true；否则返回 false。
     *
     * @param callback 要注销的 ITaskCallback 接口实例。
     * @return 返回一个布尔值，表示是否成功移除了回调对象。
     */
    @Override
    public boolean unregisterTaskCallback(ITaskCallback callback) {
        // 检查回调集合和待移除的回调对象是否为空
        if (taskCallbackSet == null || callback == null) {
            return false;
        }
        // 尝试从集合中移除指定的回调对象并返回操作结果
        return taskCallbackSet.remove(callback);
    }


    /**
     * 暂停指定任务ID的下载任务。
     *
     * @param taskId 任务的唯一标识符，用于指定要暂停的下载任务。
     * @return 如果成功暂停下载任务返回true，否则返回false。若出现异常，也返回false。
     */
    @Override
    public boolean pauseDownload(String taskId) {
        try {
            // 尝试通过任务服务暂停下载任务
            return taskService.pauseDownload(taskId);
        } catch (Throwable throwable) {
            // 捕获任何异常，并在日志中记录异常信息
            XLog.INSTANCE.e(TAG, "pauseDownload" + Log.getStackTraceString(throwable));
        }
        // 如果尝试暂停任务过程中发生异常或失败，则返回false
        return false;
    }

    /**
     * 试图恢复指定任务的下载。
     *
     * @param taskId 任务的唯一标识符，用于指定要恢复下载的任务。
     * @return 如果任务成功恢复下载，则返回true；如果无法恢复或出现异常，则返回false。
     */
    @Override
    public boolean resumeDownload(String taskId) {
        try {
            // 尝试调用任务服务来恢复指定任务的下载
            return taskService.resumeDownload(taskId);
        } catch (Throwable throwable) {
            // 捕获任何在尝试恢复下载过程中抛出的异常，并记录日志
            XLog.INSTANCE.e(TAG, "resumeDownload:" + Log.getStackTraceString(throwable));
        }
        // 如果尝试恢复下载过程中发生异常，则最终返回false
        return false;
    }


    /**
     * 注册一个排列回调接口实例。
     * 该方法用于向系统注册一个回调接口，以便在特定的排列事件发生时调用。
     *
     * @param callback 待注册的排列回调接口实例。如果为null，则不进行注册。
     * @return 若注册成功返回true，如果输入的回调实例或回调集合为null，则返回false。
     */
    @Override
    public boolean registerArrangeCallback(IArrangeCallback callback) {
        // 检查回调集合和回调对象是否为空，若为空则直接返回false
        if (arrangeCallbackSet == null || callback == null) {
            return false;
        }
        // 尝试向回调集合中添加新的回调对象，并返回添加结果
        return arrangeCallbackSet.add(callback);
    }


    /**
     * 注销给定的排列回调接口。
     * 如果回调集合或给定的回调对象为空，则不进行操作并返回 false。
     * 如果成功从集合中移除给定的回调对象，则返回 true。
     *
     * @param callback 要注销的 IArrangeCallback 接口实例。
     * @return 返回一个布尔值，表示是否成功移除了回调对象。
     */
    @Override
    public boolean unregisterArrangeCallback(IArrangeCallback callback) {
        // 检查回调集合和给定的回调对象是否为空
        if (arrangeCallbackSet == null || callback == null) {
            return false;
        }
        // 尝试从集合中移除给定的回调对象并返回操作结果
        return arrangeCallbackSet.remove(callback);
    }


    /**
     * 确保服务可用。
     * 该方法检查任务服务（taskService）是否已初始化，以及其绑定（binder）是否存活。
     * 如果服务未初始化或绑定不可用，将通过日志记录错误信息并返回 false。
     *
     * @return boolean - 如果服务确认可用，则返回 true；否则返回 false。
     */
    public boolean ensureServiceAvailable() {
        // 检查 taskService 是否为 null
        if (taskService == null) {
            XLog.INSTANCE.e(TAG, "service = null");
            return false;
        }

        IBinder binder = taskService.asBinder();
        // 检查获取的 IBinder 对象是否为 null
        if (binder == null) {
            XLog.INSTANCE.e(TAG, "service.getBinder() = null");
            return false;
        }

        // 检查 IBinder 对象是否存活
        if (!binder.isBinderAlive()) {
            XLog.INSTANCE.e(TAG, "service.getBinder().isBinderAlive() = false");
            return false;
        }

        // 通过 pingBinder() 检查服务的连通性
        if (!binder.pingBinder()) {
            XLog.INSTANCE.e(TAG, "service.getBinder().pingBinder() = false");
            return false;
        }

        // 所有检查通过，服务确认可用
        return true;
    }


    private ITaskCallback taskCallback = new ITaskCallback.Stub() {
        /**
         * 当任务被添加时调用此方法，通知所有的任务回调对象。
         *
         * @param taskInfo 代表被添加的任务的信息对象，不可为null。
         * @throws RemoteException 如果在调用回调方法时发生远程通信异常。
         */
        @Override
        public void onTaskAdded(ITaskInfo taskInfo) throws RemoteException {
            // 如果任务回调集合为空，则直接返回，不执行后续操作
            if (taskCallbackSet == null) {
                return;
            }
            // 遍历任务回调集合，对每个回调执行onTaskAdded方法
            for (ITaskCallback callback : taskCallbackSet) {
                callback.onTaskAdded(taskInfo);
            }
        }

        /**
         * 当任务被移除时，通知所有的回调接口。
         * @param taskInfo 代表被移除任务的信息。
         * @throws RemoteException 如果调用回调接口时发生远程通信异常。
         */
        @Override
        public void onTaskRemoved(ITaskInfo taskInfo) throws RemoteException {
            // 如果回调集合为空，则直接返回，不执行后续操作
            if (taskCallbackSet == null) {
                return;
            }
            // 遍历回调集合，调用每个回调接口的onTaskRemoved方法，通知任务被移除
            for (ITaskCallback callback : taskCallbackSet) {
                callback.onTaskRemoved(taskInfo);
            }
        }
    };

    private IArrangeCallback arrangeCallback = new IArrangeCallback.Stub() {
        /**
         * 下载任务暂停的回调方法。
         * @param taskId 任务ID，用于标识具体的下载任务。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onDownloadPending(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadPending(taskId);
            }
        }
        /**
         * 下载任进程中的回调方法。
         * @param taskId 任务ID，用于标识具体的下载任务。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onDownloadStarted(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadStarted(taskId);
            }
        }
        /**
         * 下载任进程中的回调方法。
         * @param taskId 任务ID，用于标识具体的下载任务。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onDownloadConnected(String taskId, long soFarBytes, long totalBytes) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadConnected(taskId, soFarBytes, totalBytes);
            }
        }
        /**
         * 下载任进程中的回调方法。
         * @param taskId 任务ID，用于标识具体的下载任务。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onDownloadProgress(String taskId, long soFarBytes, long totalBytes) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadProgress(taskId, soFarBytes, totalBytes);
            }
        }
        /**
         * 下载任完成停的回调方法。
         * @param taskId 任务ID，用于标识具体的下载任务。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onDownloadCompleted(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadCompleted(taskId);
            }
        }
        /**
         * 下载任务暂停的回调方法。
         * @param taskId 任务ID，用于标识具体的下载任务。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onDownloadPaused(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadPaused(taskId);
            }
        }

        /**
         * 下载任务错误的回调方法。
         * @param taskId 任务ID，用于标识具体的下载任务。
         * @param errorCode 错误码，用于描述下载过程中的错误类型。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onDownloadError(String taskId, int errorCode) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadError(taskId, errorCode);
            }
        }

        @Override
        public void onInstallPending(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onInstallPending(taskId);
            }
        }
        /**
         * 安装任务开始的回调方法。
         * @param taskId 任务ID，用于标识具体的安装任务。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onInstallStarted(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onInstallStarted(taskId);
            }
        }
        /**
         * 安装任务进度的回调方法。
         * @param taskId 任务ID，用于标识具体的安装任务。
         * @param progress 安装进度，为一个0到1之间的小数。
         * @throws RemoteException 远程调用异常。
         */
        @Override
        public void onInstallProgress(String taskId, float progress) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onInstallProgress(taskId, progress);
            }
        }
        // 以下方法均遵循类似的模式，当某个事件发生时，通知所有注册的回调函数。
        // 由于模式一致，这里不再对每个方法进行详细注释。
        @Override
        public void onInstallCompleted(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onInstallCompleted(taskId);
            }
        }

        @Override
        public void onInstallError(String taskId, int errorCode) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onInstallError(taskId, errorCode);
            }
        }
    };
}

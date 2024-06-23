package com.zeekrlife.market.update;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Keep;
import com.zeekrlife.market.task.IArrangeCallback;
import com.zeekrlife.market.task.ITaskCallback;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.market.update.constant.TaskState;
import com.zeekrlife.market.update.constant.TaskStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
public class ZKMarketManager implements IArrangeCallback, ITaskCallback {

    private static final String TAG = "ZKMarketManager";

    private volatile static ZKMarketManager instance;

    /**
     * key : taskId
     * value : AppTaskInfo
     */
    private final Map<String, AppTaskInfo> taskInfoMap = new ConcurrentHashMap<>();

    /**
     * key : packageName
     * value : AppTaskInfo
     */
    private final Map<String, AppTaskInfo> taskInfoCaChe = new ConcurrentHashMap<>();

    private final List<AppTaskInfoChangeListener> taskInfoChangeListeners = new ArrayList<>();

    @Keep
    public static ZKMarketManager getInstance() {
        if (instance == null) {
            synchronized (ZKMarketManager.class) {
                if (instance == null) {
                    instance = new ZKMarketManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    @Keep
    public void init(Context context, MarketTaskManager.OnInitCallback callback) {
        MarketTaskManager.getInstance().init(context, callback);
        //监听任务添加、删除状态
        MarketTaskManager.getInstance().registerTaskCallback(this);
        //监听任务下载、安装状态
        MarketTaskManager.getInstance().registerArrangeCallback(this);
    }

    @Keep
    public void release() {
        Log.e(TAG, "release");
        MarketTaskManager.getInstance().release();
    }

    @Keep
    public boolean ensureServiceAvailable() {
        return MarketTaskManager.getInstance().ensureServiceAvailable();
    }

    @Keep
    public synchronized void addTaskInfoChangedListener(AppTaskInfoChangeListener listener) {
        if (!taskInfoChangeListeners.contains(listener)) {
            taskInfoChangeListeners.add(listener);
        }
    }

    @Keep
    public synchronized void removeTaskInfoChangedListener(AppTaskInfoChangeListener listener) {
        taskInfoChangeListeners.remove(listener);
    }

    private void taskInfoChanged(AppTaskInfo appTaskInfo) {
        try {
            for (AppTaskInfoChangeListener listener : taskInfoChangeListeners) {
                listener.onAppTaskInfoChanged(appTaskInfo);
            }
            Log.e(TAG, "taskInfoChanged:" + taskInfoChangeListeners);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "taskInfoChanged exception : " + Log.getStackTraceString(e));
        }
    }

    /**
     * 我得到
     * @param taskId
     * @return
     */
    public ITaskInfo getTask(String taskId) {
        Log.e(TAG, "getTask:" + taskId);
        return MarketTaskManager.getInstance().getTask(taskId);
    }

    @Keep
    public List<AppTaskInfo> getAppTaskInfoList() {
        List<AppTaskInfo> appTaskInfoList = new ArrayList<>();
        List<String> appNames = new ArrayList<>();
        try {
            List<ITaskInfo> taskInfoList = MarketTaskManager.getInstance().getTaskList();
            for (ITaskInfo task : taskInfoList) {
                AppTaskInfo taskInfo = new AppTaskInfo();
                updateAppTaskInfo(taskInfo, task);
                appNames.add(taskInfo.getAppName());
                appTaskInfoList.add(taskInfo);
            }
            Log.e(TAG, "getAppTaskInfoList::  appNames::"+ appNames);
        } catch (Exception e) {
            Log.e(TAG, "getAppTaskInfoList exception ->" + Log.getStackTraceString(e));
        }
        return appTaskInfoList;
    }

    @Keep
    public AppTaskInfo getAppTaskInfo(String packageName) {
        Log.e(TAG, "getAppTaskInfo:" + packageName);
        AppTaskInfo appTaskInfo = taskInfoCaChe.get(packageName);
        try {
            if (appTaskInfo == null) {
                List<ITaskInfo> taskInfoList = MarketTaskManager.getInstance().getTaskList();
                if (taskInfoList == null) {
                    return null;
                }
                for (ITaskInfo task : taskInfoList) {
                    AppTaskInfo taskInfo = new AppTaskInfo(TaskState.DOWNLOAD_PROGRESS);
                    updateAppTaskInfo(taskInfo, task);
                    taskInfoCaChe.put(taskInfo.getPackageName(), taskInfo);
                    if (taskInfo.getPackageName().equals(packageName)) {
                        return taskInfo;
                    }
                }
            } else {
                ITaskInfo iTask = MarketTaskManager.getInstance().getTask(appTaskInfo.id);
                if (iTask != null && iTask.status != appTaskInfo.status) {
                    updateAppTaskInfo(appTaskInfo, iTask);
                }
                Log.d(TAG, "ITaskInfo -> " + iTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appTaskInfo;
    }

    private void updateAppTaskInfo(AppTaskInfo taskInfo, ITaskInfo task) {
        taskInfo.setData(task);
        switch (task.status) {
            case TaskStatus.INVALID:
            case TaskStatus.DOWNLOAD_PENDING:
                taskInfo.setState(TaskState.DOWNLOAD_PENDING);
                break;
            case TaskStatus.DOWNLOAD_PROGRESS:
                taskInfo.setState(TaskState.DOWNLOAD_PROGRESS);
                break;
            case TaskStatus.DOWNLOAD_PAUSED:
                taskInfo.setState(TaskState.DOWNLOAD_PAUSED);
                break;
            case TaskStatus.DOWNLOAD_COMPLETED:
                taskInfo.setState(TaskState.DOWNLOAD_COMPLETED);
                break;
            case TaskStatus.DOWNLOAD_ERROR:
                taskInfo.setState(TaskState.DOWNLOAD_ERROR);
                break;
            case TaskStatus.INSTALL_PENDING:
                taskInfo.setState(TaskState.INSTALL_PENDING);
                break;
            case TaskStatus.INSTALL_STARTED:
                taskInfo.setState(TaskState.INSTALL_STARTED);
                break;
            case TaskStatus.INSTALL_PROGRESS:
                taskInfo.setState(TaskState.INSTALL_PROGRESS);
                break;
            case TaskStatus.INSTALL_COMPLETED:
                taskInfo.setState(TaskState.INSTALL_COMPLETED);
                break;
            case TaskStatus.INSTALL_ERROR:
                taskInfo.setState(TaskState.INSTALL_ERROR);
                break;
            default:
        }
    }

    @Override
    public void onTaskAdded(ITaskInfo iTaskInfo) throws RemoteException {
        try {
            Log.e(TAG, "taskCallback onTaskAdded : " + iTaskInfo);
            if (iTaskInfo != null) {
                AppTaskInfo appTaskInfo = new AppTaskInfo();
                updateAppTaskInfo(appTaskInfo, iTaskInfo);
                for (AppTaskInfoChangeListener listener : taskInfoChangeListeners) {
                    listener.onAppTaskAdd(appTaskInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "taskCallback onTaskAdded exception : " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void onTaskRemoved(ITaskInfo iTaskInfo) throws RemoteException {
        try {
            Log.e(TAG, "taskCallback onTaskRemoved : " + iTaskInfo);
            if (iTaskInfo != null) {
                AppTaskInfo appTaskInfo = new AppTaskInfo();
                updateAppTaskInfo(appTaskInfo, iTaskInfo);
                for (AppTaskInfoChangeListener listener : taskInfoChangeListeners) {
                    listener.onAppTaskRemove(appTaskInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "taskCallback onTaskRemoved exception : " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void onDownloadPending(String taskId) throws RemoteException {
        Log.e(TAG, "onDownloadPending() called with: taskId = [" + taskId + "]");

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.DOWNLOAD_PENDING);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onDownloadStarted(String taskId) throws RemoteException {
        Log.e(TAG, "onDownloadStarted() called with: taskId = [" + taskId + "]");

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.DOWNLOAD_STARTED);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onDownloadConnected(String taskId, long soFarBytes, long totalBytes) throws RemoteException {
        Log.e(TAG, "onDownloadConnected() called with: taskId = ["
            + taskId
            + "], soFarBytes = ["
            + soFarBytes
            + "], totalBytes = ["
            + totalBytes
            + "]");

        taskInfoMap.remove(taskId);

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.DOWNLOAD_CONNECTED);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onDownloadProgress(String taskId, long soFarBytes, long totalBytes) throws RemoteException {

        AppTaskInfo appTaskInfo = taskInfoMap.get(taskId);
        if (appTaskInfo == null) {
            ITaskInfo task = getTask(taskId);
            if (task != null) {
                appTaskInfo = new AppTaskInfo(TaskState.DOWNLOAD_PROGRESS);
                appTaskInfo.setData(task);
                taskInfoMap.put(taskId, appTaskInfo);
            }
        } else {
            appTaskInfo.status = TaskStatus.DOWNLOAD_PROGRESS;
            appTaskInfo.soFar = soFarBytes;
            appTaskInfo.total = totalBytes;
        }
        taskInfoChanged(appTaskInfo);
    }

    @Override
    public void onDownloadCompleted(String taskId) throws RemoteException {
        Log.e(TAG, "onDownloadCompleted() called with: taskId = [" + taskId + "]");

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.DOWNLOAD_COMPLETED);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onDownloadPaused(String taskId) throws RemoteException {
        Log.e(TAG, "onDownloadPaused() called with: taskId = [" + taskId + "]");

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.DOWNLOAD_PAUSED);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onDownloadError(String taskId, int errorCode) throws RemoteException {
        Log.e(TAG, "onDownloadError() called with: taskId = [" + taskId + "], errorCode = [" + errorCode + "]");

        taskInfoMap.remove(taskId);

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.DOWNLOAD_ERROR);
            appTaskInfo.setData(task);
            appTaskInfo.setErrorCode(errorCode);
            if (errorCode == -210) {
                Log.e(TAG, "onDownloadError() reason net error");
                appTaskInfo.setState(TaskState.DOWNLOAD_PAUSED);
            }
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onInstallPending(String taskId) throws RemoteException {
        Log.e(TAG, "onInstallPending() called with: taskId = [" + taskId + "]");

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.INSTALL_PENDING);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onInstallStarted(String taskId) throws RemoteException {
        Log.e(TAG, "onInstallStarted() called with: taskId = [" + taskId + "]");

        taskInfoMap.remove(taskId);

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.INSTALL_STARTED);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onInstallProgress(String taskId, float progress) throws RemoteException {

        AppTaskInfo appTaskInfo = taskInfoMap.get(taskId);

        if (appTaskInfo == null) {
            ITaskInfo task = getTask(taskId);
            if (task != null) {
                appTaskInfo = new AppTaskInfo(TaskState.INSTALL_PROGRESS);
                appTaskInfo.setData(task);
            }
        } else {
            appTaskInfo.status = TaskStatus.INSTALL_PROGRESS;
            appTaskInfo.installProgress = progress;
        }
        taskInfoChanged(appTaskInfo);
    }

    @Override
    public void onInstallCompleted(String taskId) throws RemoteException {
        Log.e(TAG, "onInstallCompleted() called with: taskId = [" + taskId + "]");

        taskInfoMap.remove(taskId);

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.INSTALL_COMPLETED);
            appTaskInfo.setData(task);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public void onInstallError(String taskId, int errorCode) throws RemoteException {
        Log.e(TAG, "onInstallError() called with: taskId = [" + taskId + "], errorCode = [" + errorCode + "]");

        taskInfoMap.remove(taskId);

        ITaskInfo task = getTask(taskId);
        if (task != null) {
            AppTaskInfo appTaskInfo = new AppTaskInfo(TaskState.INSTALL_ERROR);
            appTaskInfo.setData(task);
            appTaskInfo.setErrorCode(errorCode);
            taskInfoChanged(appTaskInfo);
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    /**
     * 暂停下载
     */
    @Keep
    public boolean pauseDownload(String taskId) {
        if (taskId == null || taskId.length() <= 0) {
            return false;
        }
        Log.e(TAG, "pauseDownload:" + taskId);
        return MarketTaskManager.getInstance().pauseDownload(taskId);
    }

    /**
     * 继续下载
     */
    @Keep
    public boolean resumeDownload(String taskId) {
        if (taskId == null || taskId.length() <= 0) {
            return false;
        }
        Log.e(TAG, "resumeDownload:" + taskId);
        return MarketTaskManager.getInstance().addTask(getTask(taskId));
    }

    /**
     * 移除下载
     */
    @Keep
    public boolean removeDownload(String taskId) {
        if (taskId == null || taskId.length() <= 0) {
            return false;
        }
        Log.e(TAG, "removeDownload:" + taskId);
        return MarketTaskManager.getInstance().removeTask(taskId);
    }

    /**
     * 检测更新
     *
     * @param packageName 包名
     */
    @Keep
    public void checkAppUpdate(Context context, String packageName, AppCheckUpdateCallback callback) {
        Log.e(TAG, "checkAppUpdate() called with: packageName = [" + packageName + "]");
        MarketAppUpdateManager.getInstance().init(context, result -> {
            Log.e(TAG, "AppUpdateService init result : " + result);
            if (result) {
                try {
                    MarketAppUpdateManager.getInstance().checkAppUpdate(packageName, new ICheckUpdateCallback.Stub() {
                        @Override
                        public boolean onAppUpdate(boolean update, IAppInfo appInfo) throws RemoteException {
                            if (callback != null) {
                                AppInfo aInfo = null;
                                if (appInfo != null) {
                                    aInfo = new AppInfo();
                                    aInfo.setAppName(appInfo.appName);
                                    aInfo.setPackageName(appInfo.packageName);
                                    aInfo.setVersionName(appInfo.versionName);
                                    aInfo.setVersionCode(appInfo.versionCode);
                                    aInfo.setAppDescription(appInfo.appDescription);
                                    aInfo.setUpdateDesc(appInfo.updateDesc);
                                }
                                return callback.onAppUpdate(update, aInfo);
                            }
                            return false;
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "AppUpdateService checkAppUpdate called exception:" + Log.getStackTraceString(e));
                }
            }
        });
    }

    /**
     * 检测应用是否有可用版本
     */
    @Keep
    public void checkAppAvailableVersion(Context context, String packageName, AppAvailableVersionCallback callback) {
        Log.e(TAG, "checkAppAvailableVersion() called with: packageName = [" + packageName + "]");
        MarketAppUpdateManager.getInstance().init(context, result -> {
            Log.e(TAG, "AppUpdateService init result : " + result);
            if (result) {
                try {
                    MarketAppUpdateManager.getInstance().hasAvailableVersion(packageName, new IAvailableVersionCallback.Stub() {
                        @Override
                        public boolean onAppAvailableVersion(boolean hasAvailableVersion, IAppInfo appInfo) throws RemoteException {
                            if (callback != null) {
                                AppInfo aInfo = null;
                                if (appInfo != null) {
                                    aInfo = new AppInfo();
                                    aInfo.setAppName(appInfo.appName);
                                    aInfo.setPackageName(appInfo.packageName);
                                    aInfo.setVersionName(appInfo.versionName);
                                    aInfo.setVersionCode(appInfo.versionCode);
                                    aInfo.setAppDescription(appInfo.appDescription);
                                    aInfo.setUpdateDesc(appInfo.updateDesc);
                                }
                                return callback.onAppAvailableVersion(hasAvailableVersion, aInfo);
                            }
                            return false;
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "AppUpdateService checkAppAvailableVersion called exception:" + Log.getStackTraceString(e));
                }
            }
        });
    }

    /**
     * AppInfo callback
     */
    public interface AppTaskInfoChangeListener {
        /**
         * App下载、安装状态、进度回调
         */
        public void onAppTaskInfoChanged(AppTaskInfo appTaskInfo);

        /**
         * App下载、安装任务添加回调
         */
        public void onAppTaskAdd(AppTaskInfo appTaskInfo);

        /**
         * App下载、安装任务删除回调
         */
        public void onAppTaskRemove(AppTaskInfo appTaskInfo);
    }

    /**
     * App CheckUpdateCallback
     */
    public interface AppCheckUpdateCallback {
        boolean onAppUpdate(boolean update, AppInfo appInfo);
    }

    /**
     * App AvailableVersionCallback
     */
    public interface AppAvailableVersionCallback {
        boolean onAppAvailableVersion(boolean hasAvailableVersion, AppInfo appInfo);
    }
}
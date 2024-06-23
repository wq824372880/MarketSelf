package com.zeekrlife.market.update;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import com.zeekrlife.market.task.IArrangeCallback;
import com.zeekrlife.market.task.ITaskCallback;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.market.task.ITaskService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author
 * @date 2023/2/14 13:54
 */
public class MarketTaskManager extends ITaskService.Stub {

    private static final String TAG = "MarketTaskManager";

    private Context context;

    private ServiceConnection serviceConnection;
    private ITaskService taskService;

    private Set<ITaskCallback> taskCallbackSet;
    private Set<IArrangeCallback> arrangeCallbackSet;

    private volatile static MarketTaskManager instance;
    private Intent intent;

    public static MarketTaskManager getInstance() {
        if (instance == null) {
            synchronized (MarketTaskManager.class) {
                if (instance == null) {
                    instance = new MarketTaskManager();
                }
            }
        }
        return instance;
    }

    public class Connection implements ServiceConnection {

        private final OnInitCallback onInitCallback;

        public Connection(OnInitCallback onInitCallback) {
            this.onInitCallback = onInitCallback;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "taskService connected!");

            taskService = ITaskService.Stub.asInterface(service);

            try {
                taskService.registerArrangeCallback(arrangeCallback);
                taskService.registerTaskCallback(taskCallback);
            } catch (Throwable throwable) {
                Log.e(TAG, "register arrangeCallback or taskCallback:" + Log.getStackTraceString(throwable));
            }

            if (onInitCallback != null) {
                onInitCallback.onInit(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "taskService disconnected!");

            try {
                taskService.unregisterArrangeCallback(arrangeCallback);
                taskService.unregisterTaskCallback(taskCallback);
            } catch (Throwable throwable) {
                Log.e(TAG, "unregister arrangeCallback or taskCallback:" + Log.getStackTraceString(throwable));
            }

            taskService = null;
        }
    }

    public interface OnInitCallback {
        void onInit(boolean result);
    }

    @SuppressLint("NewApi")
    public void init(Context context, OnInitCallback callback) {
        if (taskCallbackSet == null || arrangeCallbackSet == null) {
            taskCallbackSet = new ArraySet<>();
            arrangeCallbackSet = new ArraySet<>();
        }
        this.context = context.getApplicationContext();
        this.serviceConnection = new Connection(callback);

        if (intent == null) {
            intent = new Intent();
            intent.setPackage("com.zeekrlife.market");
            intent.setAction("zeekrlife.intent.action.APPSTORE_TASK_SERVICE_START");
        }
        boolean result = false;
        try {
            result = this.context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable throwable) {
            Log.e(TAG, "bind taskService:" + Log.getStackTraceString(throwable));
        }

        if (result) {
            Log.e(TAG, "bind taskService success!");
        } else {
            Log.e(TAG, "bind taskService failure!");
            if (callback != null) {
                callback.onInit(false);
            }
        }
    }

    public void release() {
        if (context != null && serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
        context = null;
        serviceConnection = null;
        if (taskCallbackSet != null) {
            taskCallbackSet.clear();
            taskCallbackSet = null;
        }
        if (arrangeCallbackSet != null) {
            arrangeCallbackSet.clear();
            arrangeCallbackSet = null;
        }
    }

    private boolean checkServiceAvailable() {
        if (!ensureServiceAvailable()) {
            Log.e(TAG, "taskService is unavailable reInit!");
            init(context, null);
            return false;
        }
        return true;
    }

    @Override
    public List<ITaskInfo> getTaskList() {
        try {
            if(checkServiceAvailable()) {
                return taskService.getTaskList();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "getTaskList:" + Log.getStackTraceString(throwable));
        }
        return new ArrayList<>();
    }

    @Override
    public ITaskInfo getTask(String taskId) {
        try {
            if(checkServiceAvailable()) {
                return taskService.getTask(taskId);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "getTaskList:" + Log.getStackTraceString(throwable));
        }
        return null;
    }

    @Override
    public boolean addTask(ITaskInfo taskInfo) {
        try {
            if(checkServiceAvailable()) {
                return taskService.addTask(taskInfo);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "addTask" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    @Override
    public boolean removeTask(String taskId) {
        try {
            if(checkServiceAvailable()) {
                return taskService.removeTask(taskId);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "removeTask" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    @Override
    public boolean registerTaskCallback(ITaskCallback callback) {
        if (taskCallbackSet == null || callback == null) {
            return false;
        }
        return taskCallbackSet.add(callback);
    }

    @Override
    public boolean unregisterTaskCallback(ITaskCallback callback) {
        if (taskCallbackSet == null || callback == null) {
            return false;
        }
        return taskCallbackSet.remove(callback);
    }

    @Override
    public boolean pauseDownload(String taskId) {
        try {
            if(checkServiceAvailable()) {
                return taskService.pauseDownload(taskId);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "pauseDownload" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    @Override
    public boolean resumeDownload(String taskId) {
        try {
            if(checkServiceAvailable()) {
                return taskService.resumeDownload(taskId);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "resumeDownload:" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    @Override
    public boolean registerArrangeCallback(IArrangeCallback callback) {
        if (arrangeCallbackSet == null || callback == null) {
            return false;
        }
        return arrangeCallbackSet.add(callback);
    }

    @Override
    public boolean unregisterArrangeCallback(IArrangeCallback callback) {
        if (arrangeCallbackSet == null || callback == null) {
            return false;
        }
        return arrangeCallbackSet.remove(callback);
    }

    public boolean ensureServiceAvailable() {
        if (taskService == null) {
            Log.e(TAG, "service = null");
            return false;
        }
        IBinder binder = taskService.asBinder();
        if (binder == null) {
            Log.e(TAG, "service.getBinder() = null");
            return false;
        }
        if (!binder.isBinderAlive()) {
            Log.e(TAG, "service.getBinder().isBinderAlive() = false");
            return false;
        }
        if (!binder.pingBinder()) {
            Log.e(TAG, "service.getBinder().pingBinder() = false");
            return false;
        }
        return true;
    }

    private ITaskCallback taskCallback = new ITaskCallback.Stub() {
        @Override
        public void onTaskAdded(ITaskInfo taskInfo) throws RemoteException {
            if (taskCallbackSet == null) {
                return;
            }
            for (ITaskCallback callback : taskCallbackSet) {
                callback.onTaskAdded(taskInfo);
            }
        }

        @Override
        public void onTaskRemoved(ITaskInfo taskInfo) throws RemoteException {
            if (taskCallbackSet == null) {
                return;
            }
            for (ITaskCallback callback : taskCallbackSet) {
                callback.onTaskRemoved(taskInfo);
            }
        }
    };

    private IArrangeCallback arrangeCallback = new IArrangeCallback.Stub() {
        @Override
        public void onDownloadPending(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadPending(taskId);
            }
        }

        @Override
        public void onDownloadStarted(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadStarted(taskId);
            }
        }

        @Override
        public void onDownloadConnected(String taskId, long soFarBytes, long totalBytes) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadConnected(taskId, soFarBytes, totalBytes);
            }
        }

        @Override
        public void onDownloadProgress(String taskId, long soFarBytes, long totalBytes) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadProgress(taskId, soFarBytes, totalBytes);
            }
        }

        @Override
        public void onDownloadCompleted(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadCompleted(taskId);
            }
        }

        @Override
        public void onDownloadPaused(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onDownloadPaused(taskId);
            }
        }

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

        @Override
        public void onInstallStarted(String taskId) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onInstallStarted(taskId);
            }
        }

        @Override
        public void onInstallProgress(String taskId, float progress) throws RemoteException {
            if (arrangeCallbackSet == null) {
                return;
            }
            for (IArrangeCallback callback : arrangeCallbackSet) {
                callback.onInstallProgress(taskId, progress);
            }
        }

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

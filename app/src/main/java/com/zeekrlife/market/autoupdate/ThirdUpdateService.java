package com.zeekrlife.market.autoupdate;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

//import com.zeekr.sdk.base.ApiReadyCallback;
//import com.zeekr.sdk.openapi.vehicle.dashboard.IVehicleACCStatusObserver;
//import com.zeekr.sdk.vehicle.ability.IDashboard;
//import com.zeekr.sdk.vehicle.ability.ISensor;
//import com.ecarx.sdk.openapi.ECarXApiClient;
//import com.ecarx.sdk.vehicle.VehicleAPI;
//import com.ecarx.sdk.vehicle.car.sensor.ISensor;
//import com.ecarx.sdk.vehicle.car.sensor.ISensorEvent;
//import com.ecarx.sdk.vehicle.car.sensor.ISensorListener;
//import com.ecarx.sdk.vehicle.dashboard.IDashboard;
//import com.zeekr.sdk.vehicle.bean.FunctionStatus;
//import com.zeekr.sdk.vehicle.callback.CarKeyStatusListener;
//import com.zeekr.sdk.vehicle.impl.VehicleAPI;
import com.zeekr.basic.Common;
import com.zeekrlife.common.ComConstants;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.common.util.NetworkUtils;
import com.zeekrlife.common.util.SPUtils;
import com.zeekrlife.common.util.Utils;
import com.zeekrlife.common.util.constant.SpConfig;
import com.zeekrlife.market.sensors.SensorsTrack;
import com.zeekrlife.market.task.AbstractArrangeCallback;
import com.zeekrlife.market.autoupdate.observer.AbstractOnNetworkStatusChangedListener;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.net.interception.logging.util.XLog;
import com.zeekrlife.task.base.bean.AppInfo;
import com.zeekrlife.task.base.bean.TaskInfo;
import com.zeekrlife.task.base.constant.TaskState;
import com.zeekrlife.task.base.manager.TaskManager;
import com.zeekrlife.task.base.proxy.TaskProxy;
import com.zeekrlife.task.base.util.TaskUtils;
import com.zeekrlife.market.autoupdate.utils.PackageUtils;
import com.zeekrlife.market.autoupdate.utils.ShellUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 更新服务，执行静默更新、自动更新任务
 *
 * @author
 */
public class ThirdUpdateService extends Service {

    private static final String TAG = "ThirdUpdateService";

    private static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "thirdUpdateService_channel";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "thirdUpdateService";
    private static final int MSG_WHAT_DOWNLOAD_FINISHED = 1;
    private static final int MSG_WHAT_INSTALL_FINISHED = 2;
    private static final int MSG_WHAT_DO_TASK = 100;
    private static final int TIME_OUT_IN_MILLIS_DOWNLOAD = 60 * 1000;
    private static final int TIME_OUT_IN_MILLIS_INSTALL = 30 * 1000;

    /**
     * 任务延迟时间
     */
    private static final String TASK_DELAYED_TIME_MILLIS_KEY = "task_delayed_time_millis";

    /**
     * 动作类型
     */
    private static final String ACTION_TYPE_KEY = "action_type";
    /**
     * 只下载，触发时机：进入主页面、推送
     */
    private static final String ACTION_TYPE_DOWNLOAD = "download";
    /**
     * 只安装，触发时机：ACC OFF
     */
    private static final String ACTION_TYPE_INSTALL = "install";
    /**
     * 安装完再下载，触发时机：开机
     */
    private static final String ACTION_TYPE_BOTH = "both";

    /**
     * 服务启动时间
     */
    private long serviceStartedTimeMillis;
    /**
     * 任务延迟时间
     */
    private long taskDelayedTimeMillis;

    /**
     * 动作类型 {@link ThirdUpdateService#ACTION_TYPE_DOWNLOAD}, {@link ThirdUpdateService#ACTION_TYPE_INSTALL} and {@link
     * ThirdUpdateService#ACTION_TYPE_BOTH}
     */
    private String actionType;

    /**
     * 未安装完成的自动更新任务id表，key为taskId，value为packageName_versionCode_productId
     */
    private final Map<String, String> autoUpdateIdMap = new ConcurrentHashMap<>();
    /**
     * 需要下载的任务表，key为id(packageName_versionCode_productId)
     */
    private final Map<String, TaskInfo> autoDownloadTaskInfoMap = new ConcurrentHashMap<>();
    /**
     * 临时需要安装的任务表，key为id(packageName_versionCode_productId)
     */
    private final Map<String, TaskInfo> autoTmpInstallTaskInfoMap = new ConcurrentHashMap<>();
    /**
     * 需要安装的任务表，key为id(packageName_versionCode_productId),
     */
    private final Map<String, TaskInfo> autoInstallTaskInfoMap = new ConcurrentHashMap<>();

    /**
     * 下载、安装超时监听，超时当做成功处理
     */
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_WHAT_DOWNLOAD_FINISHED:
                    arrangeCallback.onDownloadFinished(null, true, -1);
                    break;
                case MSG_WHAT_INSTALL_FINISHED:
                    arrangeCallback.onInstallFinished(null, true, -1);
                    break;
                case MSG_WHAT_DO_TASK:
                    taskDelayedTimeMillis = 0;
                    doFetchData();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 自动启动任务。这是一个静态方法，用于在指定延迟时间后启动一个任务。
     * 延迟时间过后，会根据传入的ACTION_TYPE执行相应的操作。
     *
     * @param context 上下文对象，用于访问应用全局功能。
     * @param taskDelayedTimeMillis 任务的延迟时间，单位为毫秒。从调用此方法开始计时。
     */
    public static void startSelf(Context context, long taskDelayedTimeMillis) {
        startSelf(context, taskDelayedTimeMillis, ACTION_TYPE_BOTH);
    }

    /**
     * 启动服务的辅助方法。
     * 该方法用于在特定的延迟时间后，根据指定的动作类型启动一个服务。
     *
     * @param context 上下文，用于启动服务。
     * @param taskDelayedTimeMillis 任务的延迟时间，单位为毫秒。
     * @param actionType 动作类型，用于标识服务启动后执行的具体操作。
     */
    public static void startSelf(Context context, long taskDelayedTimeMillis, String actionType) {
        Intent intent = new Intent(context, ThirdUpdateService.class);
        intent.putExtra(TASK_DELAYED_TIME_MILLIS_KEY, taskDelayedTimeMillis);
        intent.putExtra(ACTION_TYPE_KEY, actionType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public ThirdUpdateService() {
    }

    @Override
    public void onCreate() {
        XLog.INSTANCE.d(TAG, "onCreate() called");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        XLog.INSTANCE.d(TAG,
                "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        serviceForeground();
        if (startId == 1) {
            serviceStartedTimeMillis = System.currentTimeMillis();
            if (intent != null) {
                taskDelayedTimeMillis = intent.getLongExtra(TASK_DELAYED_TIME_MILLIS_KEY, 0);
                actionType = intent.getStringExtra(ACTION_TYPE_KEY);
            }
            if (ACTION_TYPE_DOWNLOAD.equals(actionType) || ACTION_TYPE_BOTH.equals(actionType)) {
                // empty
            } else {
                actionType = ACTION_TYPE_BOTH;
            }
            checkPermissionGranted();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        XLog.INSTANCE.d(TAG, "onDestroy() called");
        autoUpdateIdMap.clear();
        autoDownloadTaskInfoMap.clear();
        autoTmpInstallTaskInfoMap.clear();
        autoInstallTaskInfoMap.clear();

        if (NetworkUtils.isRegisteredNetworkStatusChangedListener(onNetworkStatusChangedListener)) {
            NetworkUtils.unregisterNetworkStatusChangedListener(onNetworkStatusChangedListener);
        }

        //try {
        //    VehicleAPI.get().unRegisterCarKeyStatusListener(carKeyStatusListener);
        //} catch (Throwable throwable) {
        //    //
        //}

        //try {
        //    unregisterReceiver(accOffReceiver);
        //} catch (Throwable throwable) {
        //    //
        //}

        TaskManager.getInstance().unregisterArrangeCallback(arrangeCallback);
        super.onDestroy();
    }

    /**
     * 检查是否获得了权限。
     * 该方法首先检查当前应用是否为系统应用或具有root权限，如果未获得必要的权限，则停止自身服务。
     * 如果权限检查通过，会尝试确保相关服务可用并执行任务。
     * 该方法不接受任何参数且无返回值。
     */
    @SuppressLint("CheckResult")
    private void checkPermissionGranted() {
        // 检查当前应用是否为系统应用或具有root权限
        boolean permissionGranted = PackageUtils.isSystemApplication(this) || ShellUtils.checkRootPermission();
        if (!permissionGranted) {
            // 如果未获得必要权限，则记录日志并停止自身服务
            XLog.INSTANCE.w(TAG, "require system/root permission");
            stopSelf();
            return;
        }

        // 确保相关服务可用
        if (TaskProxy.getInstance().ensureServiceAvailable()) {
            // 服务可用，注册安排回调并执行任务
            TaskManager.getInstance().registerArrangeCallback(arrangeCallback);
            doTask();
        } else {
            // 服务不可用，初始化服务并注册回调，以在初始化完成后执行任务
            TaskProxy.getInstance().init(Common.app, new TaskManager.OnInitCallback() {
                @Override
                public void onInit(boolean result) {
                    if (result) {
                        // 初始化成功，注册安排回调并执行任务
                        TaskManager.getInstance().registerArrangeCallback(arrangeCallback);
                        doTask();
                    } else {
                        // 初始化失败，记录日志并停止自身服务
                        XLog.INSTANCE.w(TAG, "TaskProxy init failed");
                        stopSelf();
                    }
                }
            });
        }
    }

    /**
     * 开始做任务
     */
    @SuppressLint("CheckResult")
    private void doTask() {
        XLog.INSTANCE.d(TAG, "doTask() called with: actionType = [" + actionType + "]");

        autoUpdateIdMap.clear();
        Set<String> autoUpdateIdSet = SPUtils.getInstance().getStringSet(SpConfig.StringSetKey.AUTO_UPDATE_ID_SET, new HashSet<>());
        for (String autoUpdateId : autoUpdateIdSet) {
            String[] split = autoUpdateId.split(",");
            autoUpdateIdMap.put(split[0], split[1]);
        }

        if (autoUpdateIdMap.isEmpty()) {
            XLog.INSTANCE.w(TAG, "both silenceUpdateIdMap and autoUpdateIdMap are empty");
            if (ACTION_TYPE_DOWNLOAD.equals(actionType) || ACTION_TYPE_BOTH.equals(actionType)) {
                actionType = ACTION_TYPE_DOWNLOAD;
                checkNetworkConnected();
            } else {
                stopSelf();
            }
        } else {
            XLog.INSTANCE.d(TAG, "autoUpdateIdMap = [" + autoUpdateIdMap + "]");
            //获取不需要自动更新的应用包名列表，从未安装完成的自动更新任务id表 剔除
            List<String> notAutoUpdatePackageNameList = MarketDataHelper.INSTANCE.getNotAutoUpdatePackageNameList();
            if (ACTION_TYPE_DOWNLOAD.equals(actionType)) {
                stopSelf();
            } else if (ACTION_TYPE_INSTALL.equals(actionType) || ACTION_TYPE_BOTH.equals(actionType)) {
                if (!notAutoUpdatePackageNameList.isEmpty()) {
                    doDelete(notAutoUpdatePackageNameList);
                }
                doInstall();
            } else {
                XLog.INSTANCE.d(TAG, "actionType=" + actionType);
            }
        }
    }

    /**
     * 检测网络是否连接
     */
    private void checkNetworkConnected() {
        XLog.INSTANCE.d(TAG, "checkNetworkConnected() called");
        if (NetworkUtils.isConnected()) {
            XLog.INSTANCE.d(TAG, "NetworkUtils.isConnected() = [true]");
            if (NetworkUtils.isRegisteredNetworkStatusChangedListener(onNetworkStatusChangedListener)) {
                NetworkUtils.unregisterNetworkStatusChangedListener(onNetworkStatusChangedListener);
            }

            // 只在下载前延迟
            long delayMillis = taskDelayedTimeMillis - (System.currentTimeMillis() - serviceStartedTimeMillis);
            XLog.INSTANCE.d(TAG, "checkNetworkConnected, delayMillis = " + delayMillis);
            handler.sendEmptyMessageDelayed(MSG_WHAT_DO_TASK, delayMillis);
        } else {
            XLog.INSTANCE.w(TAG, "NetworkUtils.isConnected() = [false]");
            if (!NetworkUtils.isRegisteredNetworkStatusChangedListener(onNetworkStatusChangedListener)) {
                NetworkUtils.registerNetworkStatusChangedListener(onNetworkStatusChangedListener);
            }
        }
    }

    /**
     * 获取需要自动更新的应用列表
     */
    private void doFetchData() {
        XLog.INSTANCE.d(TAG, "doFetchDataAndDownload() called");

        autoDownloadTaskInfoMap.clear();
        autoTmpInstallTaskInfoMap.clear();
        //获取需自动更新列表
        MarketDataHelper.INSTANCE.getAutoDownloadTaskInfo(result -> {
            autoDownloadTaskInfoMap.putAll(result);
            doDownload();
        });
    }

    /**
     * 根据条件执行下载
     */
    private void doDownload() {
        long autoSize = 0;
        StringBuilder appNames = new StringBuilder();
        for (TaskInfo taskInfo : autoDownloadTaskInfoMap.values()) {
            AppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, AppInfo.class);
            appNames.append(appInfo.getApkName()).append("|");
            autoSize += Utils.INSTANCE.calculateMBSize(appInfo.getApkSize());
        }

        if (!Utils.INSTANCE.isSpaceEnough(autoSize)) {
            XLog.INSTANCE.w(TAG, "auto download space is not enough");
            autoDownloadTaskInfoMap.clear();
            stopSelf();
        } else {
            for (TaskInfo taskInfo : autoDownloadTaskInfoMap.values()) {
                TaskProxy.getInstance().addTask(taskInfo);
            }
            String appNamesString = appNames.toString();
            if (appNamesString.length() > 0) {
                //自动更新埋点
                SensorsTrack.INSTANCE.onAppUpdate("自动更新", appNames.toString(), autoDownloadTaskInfoMap.values().size(), 2);
            }
        }

        if (autoDownloadTaskInfoMap.isEmpty()) {
            stopSelf();
        }
    }

    /**
     * 执行安装
     */
    private void doInstall() {
        XLog.INSTANCE.d(TAG, "doInstall() called with: actionType = [" + actionType + "]");
        if (ACTION_TYPE_DOWNLOAD.equals(actionType)) {
            stopSelf();
        } else if (ACTION_TYPE_INSTALL.equals(actionType) || ACTION_TYPE_BOTH.equals(actionType)) {

            autoInstallTaskInfoMap.clear();
            MarketDataHelper.INSTANCE.fillInstallTaskInfoMap(autoInstallTaskInfoMap, autoUpdateIdMap);
            if (autoInstallTaskInfoMap.isEmpty()) {

                XLog.INSTANCE.w(TAG, "autoInstallTaskInfoMap is empty");
                autoUpdateIdMap.clear();
                SPUtils.getInstance().remove(SpConfig.StringSetKey.AUTO_UPDATE_ID_SET);

                //检测下载
                if (ACTION_TYPE_BOTH.equals(actionType)) {
                    XLog.INSTANCE.w(TAG, "continue checkNetworkConnected");
                    checkNetworkConnected();
                }
            } else {
                for (TaskInfo taskInfo : autoInstallTaskInfoMap.values()) {
                    TaskProxy.getInstance().addTask(taskInfo);
                }
            }
        } else {
            XLog.INSTANCE.d(TAG, "actionType=" + actionType);
        }
    }

    /**
     * 执行删除
     */
    private void doDelete(@NonNull List<String> deletePackageNameList) {
        XLog.INSTANCE.d(TAG, "doDelete() called with: actionType = [" + actionType + "]");
        for (String deletePackageName : deletePackageNameList) {
            for (Iterator<Map.Entry<String, TaskInfo>> iterator = autoInstallTaskInfoMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, TaskInfo> entry = iterator.next();
                if (entry.getKey().startsWith(deletePackageName)) {
                    iterator.remove();
                }
            }

            Set<String> autoUpdateIdSet = new HashSet<>();
            for (Iterator<Map.Entry<String, String>> iterator = autoUpdateIdMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> entry = iterator.next();
                if (entry.getValue().startsWith(deletePackageName)) {
                    ITaskInfo task = TaskProxy.getInstance().getTask(entry.getKey());
                    if (task != null) {
                        TaskInfo taskInfo = new TaskInfo(TaskState.INSTALLABLE);
                        taskInfo.setData(task);
                        TaskProxy.getInstance().removeTask(TaskUtils.getTaskId(this, taskInfo));
                    }
                    iterator.remove();
                }
            }
            for (Map.Entry<String, String> entry : autoUpdateIdMap.entrySet()) {
                autoUpdateIdSet.add(entry.getKey() + "," + entry.getValue());
            }
            SPUtils.getInstance().put(SpConfig.StringSetKey.AUTO_UPDATE_ID_SET, autoUpdateIdSet);
        }
    }

    /**
     * 执行ACC OFF监听
     */
    //private void doAccOffMonitoring() {
    //    XLog.INSTANCE.d(TAG, "doAccOffMonitoring() called with: actionType = [" + actionType + "]");
    //
    //    actionType = ACTION_TYPE_INSTALL;
    //
    //    VehicleAPI vehicleApi = VehicleAPI.get();
    //    if (vehicleApi != null) {
    //        vehicleApi.init(getApplicationContext(), (ready, reason) -> {
    //            if (!ready) {
    //                XLog.INSTANCE.w(TAG, "vehicleApi init failed:" + reason);
    //            }
    //        });
    //        vehicleApi.registerCarKeyStatusListener(carKeyStatusListener);
    //    }
    //
    //    registerReceiver(accOffReceiver, new IntentFilter("ecarx.intent.action.ACC_OFF_TEST"));
    //}

    private NetworkUtils.OnNetworkStatusChangedListener onNetworkStatusChangedListener = new AbstractOnNetworkStatusChangedListener() {
        @Override
        public void onConnected(NetworkUtils.NetworkType networkType) {
            XLog.INSTANCE.d(TAG, "onConnected() called with: networkType = [" + networkType + "]");
            checkNetworkConnected();
        }
    };

    //private final CarKeyStatusListener carKeyStatusListener = new CarKeyStatusListener() {
    //    @Override
    //    public void onSensorEventChanged(int accStatus) {
    //        XLog.INSTANCE.d(TAG, "CarKeyStatusListener called with: accStatus = [" + accStatus + "]");
    //        if (accStatus == IGNITION_STATE_OFF) {
    //            doTask();
    //        }
    //    }
    //
    //    @Override
    //    public void onSensorSupportChanged(FunctionStatus functionStatus) {
    //
    //    }
    //};

    //private final BroadcastReceiver accOffReceiver = new BroadcastReceiver() {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        if ("ecarx.intent.action.ACC_OFF_TEST".equals(intent.getAction())) {
    //            try {
    //                unregisterReceiver(this);
    //            } catch (Throwable throwable) {
    //                //
    //            }
    //            doTask();
    //        }
    //    }
    //};

    private final AbstractArrangeCallback arrangeCallback = new AbstractArrangeCallback() {
        @Override
        public void onDownloadFinished(String taskId, boolean successful, int errorCode) {
            try {
                synchronized (this) {
                    XLog.INSTANCE.d(TAG, "onDownloadFinished() called with: taskId = [" + taskId + "], successful = [" + successful + "]");
                    boolean autoResult = MarketDataHelper.INSTANCE.onDownloadFinished(Common.app, taskId, successful, autoDownloadTaskInfoMap,
                            autoTmpInstallTaskInfoMap, autoInstallTaskInfoMap, autoUpdateIdMap);
                    if (autoResult) {
                        Set<String> autoUpdateIdSet = new HashSet<>(autoInstallTaskInfoMap.keySet());
                        SPUtils.getInstance().put(SpConfig.StringSetKey.AUTO_UPDATE_ID_SET, autoUpdateIdSet);
                    }

                    if (handler.hasMessages(MSG_WHAT_DOWNLOAD_FINISHED)) {
                        handler.removeMessages(MSG_WHAT_DOWNLOAD_FINISHED);
                    }

                    XLog.INSTANCE.w(TAG,
                            "autoResult = [" + autoResult + "], autoDownloadTaskInfoMap = [" + autoDownloadTaskInfoMap.keySet() + "]");
                    if (autoResult) {
                        XLog.INSTANCE.d(TAG, "download finished with: actionType = [" + actionType + "]");
                        switch (actionType) {
                            case ACTION_TYPE_DOWNLOAD:
                            case ACTION_TYPE_BOTH:
                                actionType = ACTION_TYPE_INSTALL;
                                doTask();
                                break;
                            case ACTION_TYPE_INSTALL:
                                doTask();
                                break;
                            default:
                                break;
                        }
                    } else {
                        handler.sendEmptyMessageDelayed(MSG_WHAT_DOWNLOAD_FINISHED, TIME_OUT_IN_MILLIS_DOWNLOAD);
                    }
                }
            } catch (Exception e) {
                CommExtKt.logStackTrace(e);
            }
        }

        @Override
        public void onInstallFinished(String taskId, boolean successful, int errorCode) {
            try {
                synchronized (this) {
                    XLog.INSTANCE.d(TAG, "onInstallFinished() called with: taskId = [" + taskId + "], successful = [" + successful + "]");
                    boolean autoResult = MarketDataHelper.INSTANCE.onInstallFinished(Common.app, taskId, autoInstallTaskInfoMap);
                    if (autoResult) {
                        autoUpdateIdMap.clear();
                        SPUtils.getInstance().remove(SpConfig.StringSetKey.AUTO_UPDATE_ID_SET);
                    }

                    XLog.INSTANCE.w(TAG,
                            "autoResult = [" + autoResult + "], autoInstallTaskInfoMap = [" + autoInstallTaskInfoMap.keySet() + "]");

                    if (handler.hasMessages(MSG_WHAT_INSTALL_FINISHED)) {
                        handler.removeMessages(MSG_WHAT_INSTALL_FINISHED);
                    }

                    if (autoResult) {
                        XLog.INSTANCE.d(TAG, "install finished with: actionType = [" + actionType + "]");
                        switch (actionType) {
                            case ACTION_TYPE_INSTALL:
                                stopSelf();
                                break;
                            case ACTION_TYPE_BOTH:
                                doTask();
                                break;
                            default:
                                break;
                        }
                    } else {
                        handler.sendEmptyMessageDelayed(MSG_WHAT_INSTALL_FINISHED, TIME_OUT_IN_MILLIS_INSTALL);
                    }
                }
            } catch (Exception e) {
                CommExtKt.logStackTrace(e);
            }
        }
    };

    /**
     * 服务置前台
     */
    private void serviceForeground() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel =
                        new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID, DEFAULT_NOTIFICATION_CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(notificationChannel);

                Notification notification = new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID).setAutoCancel(true)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setOngoing(true)
                        .setPriority(NotificationManager.IMPORTANCE_LOW)
                        .build();

                startForeground(ComConstants.FOREGROUND_SERVICE_THIRD_UPDATE, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

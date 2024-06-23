package com.zeekrlife.task.base.widget;

import static com.zeekrlife.task.base.widget.DownloadProgressLayout.DOWNLOAD_STATUS_LOADING;
import static com.zeekrlife.task.base.widget.DownloadProgressLayout.DOWNLOAD_STATUS_PAUSE;
import static com.zeekrlife.task.base.widget.DownloadProgressLayout.DOWNLOAD_STATUS_WAITING;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.zeekr.basic.AppExtKt;
import com.zeekr.car.api.CarApiManager;
import com.zeekr.car.api.MultiDisplayManager;
import com.zeekr.car.api.PolicyApiManager;
import com.zeekr.car.api.VehicleApiManager;
import com.zeekr.car.api.policy.IPolicyListener;
import com.zeekr.sdk.car.callback.MultiScreenListener;
import com.zeekr.sdk.car.impl.module.config.MultiScreen;
import com.zeekr.sdk.car.impl.module.config.MultiScreens;
import com.zeekr.sdk.multidisplay.setting.bean.ActivityConfig;
import com.zeekr.sdk.multidisplay.setting.bean.MultiDisplayActivityInfo;
import com.zeekr.sdk.policy.bean.AppPolicyInfo;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.ext.DensityExtKt;
import com.zeekrlife.common.util.ApkUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.common.util.IntentUtils;
import com.zeekrlife.common.util.ToastUtils;
import com.zeekrlife.common.util.Utils;
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.net.interception.logging.util.LogUtils;
import com.zeekrlife.net.interception.logging.util.XLog;
import com.zeekrlife.task.base.R;
import com.zeekrlife.task.base.bean.AppInfo;
import com.zeekrlife.task.base.bean.ExpandAppInfo;
import com.zeekrlife.task.base.bean.TaskInfo;
import com.zeekrlife.task.base.constant.TaskState;
import com.zeekrlife.task.base.constant.TaskType;
import com.zeekrlife.task.base.manager.TaskManager;
import com.zeekrlife.task.base.proxy.TaskProxy;

import com.zeekrlife.task.base.specialapp.SpecialAppHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务控件，包含action按钮、下载进度条、状态文本
 *
 * @author
 */
public class TaskLayoutX extends ConstraintLayout
        implements TaskProxy.TaskInfoChangeListener, View.OnClickListener, IPolicyListener {

    public static final String TAG = "zzzTaskLayoutX";

    private Context context;

    private View viewAction;

    private DownloadProgressLayout downloadProgress;

    private TextView textState;

    private TaskInfo taskInfo;

    private AppInfo appInfo;

    private TaskLayout.OnViewActionClickListener onViewActionClickListener;

    private OnTaskChangeListener onTaskChangeListener;

    private Drawable btnBackground;

    private int textSelectorId = R.color.selector_text_color_btn_common;

    /**
     * 是否可以使用
     */
    private volatile boolean isCanUse = true;

    /**
     * App 可启动列表
     */

    public TaskLayoutX(@NonNull Context context) {
        this(context, null);
    }

    public TaskLayoutX(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskLayoutX(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
        initView(context);
    }

    private void initData(Context context) {
        this.context = context;
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.task_layout_task_x, this, true);
        viewAction = findViewById(R.id.action);
        btnBackground = viewAction.getBackground();
        downloadProgress = findViewById(R.id.progress);
        textState = findViewById(R.id.status);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        TaskProxy.getInstance().addTaskInfoChangeListener(this);
        viewAction.setOnClickListener(this);
        //监听应用是否可以启动
        PolicyApiManager.getInstance().addIPolicyListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        TaskProxy.getInstance().removeTaskInfoChangeListener(this);
        viewAction.setOnClickListener(null);
        PolicyApiManager.getInstance().removeIPolicyListener(this);
        onViewActionClickListener = null;
        super.onDetachedFromWindow();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textState.setEnabled(enabled);
        viewAction.setEnabled(enabled);
    }

    /**
     * 设置深色主题
     */
    public void setDarkStyle() {
        viewAction.setVisibility(View.GONE);
        viewAction = findViewById(R.id.action_dark);
        btnBackground = viewAction.getBackground();
        viewAction.setOnClickListener(this);
        viewAction.setVisibility(View.VISIBLE);
        textSelectorId = R.color.selector_text_color_btn_common_dark;
        textState.setTextColor(ContextCompat.getColorStateList(context, textSelectorId));
    }

    @Override
    public void onTaskInfoChanged(TaskInfo taskInfo) {
        if (getTaskInfo() == null || taskInfo == null) {
            return;
        }
        if (getTaskInfo().hash.equals(taskInfo.hash)) {
            getTaskInfo().setState(taskInfo.getState());
            getTaskInfo().setData(taskInfo);
            getTaskInfo().setErrorCode(taskInfo.getErrorCode());
            post(new Runnable() {
                @Override
                public void run() {
                    if (getTaskInfo().hash.equals(taskInfo.hash)) {
                        init(taskInfo, true);
                    }
                }
            });
        }
    }

    public void init(TaskInfo taskInfo) {
        init(taskInfo, false);
    }

    public void init(TaskInfo taskInfo, boolean showToastIfErrorOccurred) {
        if (taskInfo == null) {
            setVisibility(View.INVISIBLE);
            return;
        }

        if (getTaskInfo() != null) {
            if (!getTaskInfo().hash.equals(taskInfo.hash)) {
                downloadProgress.setProgress(0);
            }
        }
        XLog.INSTANCE.i(TAG, "init taskInfo 1 >>>> " + taskInfo);

        int lastState = this.taskInfo == null ? -1 : this.taskInfo.getState();
        this.taskInfo = taskInfo;

        downloadProgress.setAlpha(1);
        setEnabled(true);

        boolean isForceUpdate = false;

        switch (taskInfo.getState()) {
            case TaskState.OPENABLE:
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                textState.setText(R.string.task_state_openable);

                try {
                    AppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, AppInfo.class);
                    this.appInfo = appInfo;
                    setStateUpState();
                    if (isSelfApp(appInfo.getPackageName())) {
                        setVisibility(View.INVISIBLE);
                        return;
                    }
                    if (IntentUtils.getLaunchAppIntent(appInfo.getPackageName()) == null) {
                        LogUtils.e(TAG, "已安装::OPENABLE" + appInfo.getPackageName());
                        setEnabled(false);
                        textState.setText(R.string.task_state_install_completed);
                    }
                } catch (Throwable throwable) {
                    //
                }
                break;
            case TaskState.DOWNLOADABLE:
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                if (getTaskState(taskInfo) == TaskState.UPDATABLE) {
                    textState.setText(R.string.task_state_updatable);
                } else {
                    textState.setText(R.string.task_state_downloadable);
                }
                canUse();
                break;
            case TaskState.UPDATABLE:
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                textState.setText(R.string.task_state_updatable);
                canUse();
                break;
            case TaskState.DOWNLOAD_PENDING:
            case TaskState.DOWNLOAD_STARTED:
            case TaskState.DOWNLOAD_CONNECTED:
                setEnabled(false);
                textState.setText("");
                viewAction.setBackgroundResource(0);
                downloadProgress.setVisibility(View.VISIBLE);
                downloadProgress.setProgressStatus(DOWNLOAD_STATUS_WAITING);
                break;
            case TaskState.DOWNLOAD_PROGRESS:
                if (lastState == TaskState.DOWNLOAD_COMPLETED || lastState == TaskState.DOWNLOAD_ERROR) {
                    break;
                }
                if (taskInfo.total == 0) {
                    downloadProgress.setProgress(0);
                } else {
                    int progress = (int) (((double) taskInfo.soFar) / ((double) taskInfo.total) * 100);
                    if (progress > downloadProgress.getProgress()) {
                        downloadProgress.setProgress(progress);
                    }
                    if (downloadProgress.getProgress() == 0) {
                        downloadProgress.setProgress(4);
                    }
                }
                downloadProgress.setVisibility(View.VISIBLE);
                downloadProgress.setProgressStatus(DOWNLOAD_STATUS_LOADING);
                viewAction.setBackgroundResource(0);
                textState.setText("");

                //如果强制更新不可显示样式
                try {
                    ExpandAppInfo app = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                    if (app.isForceUpdate() && textState.isEnabled() &&
                            ApkUtils.INSTANCE.isAppInstalled(context, app.getPackageName())) {
                        isForceUpdate = true;
                        textState.setEnabled(false);
                        downloadProgress.setAlpha(0.4f);
                    }
                } catch (Exception e) {
                    CommExtKt.logStackTrace(e);
                }
                break;
            case TaskState.DOWNLOAD_PAUSED:
                downloadPaused();
                break;
            case TaskState.DOWNLOAD_COMPLETED:
                downloadProgress.setVisibility(View.INVISIBLE);
                if (taskInfo.type == TaskType.DOWNLOAD) {
                    downloadProgress.setProgress(0);
                    viewAction.setBackground(btnBackground);
                    textState.setText(R.string.task_state_download_completed);
                }
                break;
            case TaskState.DOWNLOAD_ERROR:
                //网络问题导致的下载失败，暂停处理
//                if (taskInfo.getErrorCode() == -210) {
//                    downloadPaused();
//                } else {
                TaskProxy.getInstance().removeTask(taskInfo.id);//122改动，下载失败清除缓存，避免下载没有权限问题
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                if (getTaskState(taskInfo) == TaskState.UPDATABLE) {
                    textState.setText(R.string.task_state_updatable);
                } else {
                    textState.setText(R.string.task_state_downloadable);
                }
//                }

                if (showToastIfErrorOccurred) {
                    showToast(R.string.task_state_download_error_default_toast);
                }
                break;
            case TaskState.INSTALLABLE:
                downloadProgress.setVisibility(View.INVISIBLE);
                viewAction.setBackground(btnBackground);
                textState.setText(R.string.task_state_installable);
                break;
            case TaskState.INSTALL_PENDING:
            case TaskState.INSTALL_STARTED:
                setEnabled(false);
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                textState.setText(R.string.task_state_install_pending);
                break;
            case TaskState.INSTALL_PROGRESS:
                if (lastState == TaskState.INSTALL_COMPLETED || lastState == TaskState.INSTALL_ERROR) {
                    break;
                }
                setEnabled(false);
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                textState.setText(R.string.task_state_install_progress);
                break;
            case TaskState.INSTALL_COMPLETED:
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                textState.setText(R.string.task_state_openable);
                try {
                    AppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, AppInfo.class);
                    if (isSelfApp(appInfo.getPackageName())) {
                        setVisibility(View.INVISIBLE);
                        return;
                    }
                    AppPolicyInfo policyInfo = PolicyApiManager.getInstance().checkStartup(appInfo.getPackageName());
                    if (policyInfo != null && policyInfo.getCode() == 4) {
                        canUse();
                    } else if (policyInfo != null && policyInfo.getCode() == 1) {
                        onTheRoadCanNotUse();
                    } else {
                    }
                    LogUtils.e(TAG, "已安装::INSTALL_COMPLETED" + appInfo.getPackageName());
                    if (IntentUtils.getLaunchAppIntent(appInfo.getPackageName()) == null) {
                        setEnabled(false);
                        textState.setText(R.string.task_state_install_completed);
                    }
                } catch (Throwable throwable) {
                    //
                }
                break;
            case TaskState.INSTALL_ERROR:
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                int taskState = getTaskState(taskInfo);
                if (taskState == TaskState.UPDATABLE) {
                    textState.setText(R.string.task_state_updatable);
                } else if (taskState == TaskState.OPENABLE || taskState == TaskState.INSTALL_COMPLETED) {
                    textState.setText(R.string.task_state_openable);
                    return;
                } else {
                    textState.setText(R.string.task_state_downloadable);
                }
                if (showToastIfErrorOccurred) {
                    showToast(R.string.task_state_install_error_default_toast);
                }
                break;
            case TaskState.CANCEL_DOWNLOAD:
                downloadProgress.setVisibility(View.INVISIBLE);
                downloadProgress.setProgress(0);
                viewAction.setBackground(btnBackground);
                if (getTaskState(taskInfo) == TaskState.UPDATABLE) {
                    textState.setText(R.string.task_state_updatable);
                } else {
                    textState.setText(R.string.task_state_downloadable);
                }
                break;
            default:
                downloadProgress.setVisibility(View.INVISIBLE);
                viewAction.setBackground(btnBackground);
                break;
        }

        //下载任务状态回调
        if (onTaskChangeListener != null) {
            //下载中状态单独处理，处理是否强制下载的情况
            onTaskChangeListener.onTaskChangeListener(taskInfo.getState(), isForceUpdate);
        }
    }

    private void downloadPaused() {
        viewAction.setBackgroundResource(0);
        textState.setText("");
        downloadProgress.setVisibility(View.VISIBLE);
        downloadProgress.setProgressStatus(DOWNLOAD_STATUS_PAUSE);
        if (taskInfo.total != 0) {
            int progress = (int) (((double) taskInfo.soFar) / ((double) taskInfo.total) * 100);
            if (progress > downloadProgress.getProgress()) {
                downloadProgress.setProgress(progress);
            }
        } else {
            Log.e(TAG, "taskInfo.total == 0 !!!!");
        }
        if (downloadProgress.getProgress() == 0) {
            downloadProgress.setProgress(4);
        }
    }

    private void showToast(int resId) {
        try {

            if (AppExtKt.getCurrentActivity() != null) {
                ToastUtils.show(AppExtKt.getCurrentActivity(), AppExtKt.getCurrentActivity().getString(resId), null);
            }

            ThreadPoolUtil.runOnUiThread(() -> {
                        if (AppExtKt.getCurrentCXActivity() != null) {
                            ToastUtils.show(AppExtKt.getCurrentCXActivity(), AppExtKt.getCurrentCXActivity().getString(resId), null);
                        }
                    }
                    , 2000);
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    private void showToast(String msg) {
        try {

            if (AppExtKt.getCurrentActivity() != null) {
                ToastUtils.show(AppExtKt.getCurrentActivity(), msg, null);
            }

            ThreadPoolUtil.runOnUiThread(() -> {
                        if (AppExtKt.getCurrentCXActivity() != null) {
                            ToastUtils.show(AppExtKt.getCurrentCXActivity(), msg, null);
                        }
                    }
                    , 2000);

        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (viewAction != null) {
            if (taskInfo == null) {
                return;
            }
            taskInfo.type = TaskType.DOWNLOAD_INSTALL;
            XLog.INSTANCE.e(TAG, "onClick this.taskInfo ===> " + taskInfo);

            switch (taskInfo.getState()) {
                case TaskState.INSTALL_COMPLETED:
                case TaskState.OPENABLE:
                    try {
                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                        XLog.INSTANCE.e(TAG, "openable >>>> " + appInfo.getApkName());
                        if (!checkAppIsAvailable(appInfo.getPackageName())) {
                            return;
                        }
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                    TaskProxy.getInstance().openApp(context, taskInfo, getDisplayId(context));
                    break;
                case TaskState.DOWNLOADABLE:
                    try {
                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
//                        String category = appInfo.getCategoryName();
//                        String productId = appInfo.getAppVersionId();
//                        String name = appInfo.getApkName();
                        String apkSize = appInfo.getApkSize();
//                        String versionName = appInfo.getVersionName();
                        //磁盘检测
                        if (!Utils.INSTANCE.isSpaceEnough(apkSize)) {
                            showToast(R.string.app_install_no_space_enough);
                            return;
                        }
                    } catch (Throwable throwable) {
                        //
                    }
//                    int downloadAbleSize = 0;
//                    if(TaskProxy.getInstance().getTaskList() != null && TaskProxy.getInstance().getTaskList().size() >= 3){
//                        for (ITaskInfo taskInfo : TaskProxy.getInstance().getTaskList()) {
//                            if((taskInfo.status == TaskState.DOWNLOAD_PENDING) ||(taskInfo.status == TaskState.DOWNLOAD_STARTED)
//                                    || (taskInfo.status == TaskState.DOWNLOAD_PAUSED) || (taskInfo.status == TaskState.DOWNLOAD_CONNECTED)
//                                    || (taskInfo.status == TaskState.DOWNLOAD_PROGRESS)){
//                                downloadAbleSize++;
//                            }
//                        }
//                        if(downloadAbleSize >= 3){
//                            showToast("最多同时下载3个应用");
//                            setEnabled(true);
//                            return;
//                        }
//                    }

//                    boolean cxWidth = DensityExtKt.getScreenWidthIs3200();
//                    boolean cxHeight = DensityExtKt.getScreenHeightIs1440();
//                    Log.e(TAG, "onClick cxWidth = " + cxWidth + " cxHeight = " + cxHeight);
//                    if (cxWidth || cxHeight) {
//                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
//                        if (appInfo != null && appInfo.getPackageName() != null) {
//                            List<MultiDisplayActivityInfo> infos = MultiDisplayManager.getInstance().activityInfoByPackageName(appInfo.getPackageName());
//                            Log.e(TAG, "MultiDisplayManager activityInfoByPackageName::" + infos);
//                            if (infos == null || infos.isEmpty()) {
//                                int screenId = getDisplayId(context) == 0 ? 0 : 7;
//                                CarApiManager.getInstance().getRegisterMultiScreenListener(screenId, new MultiScreenListener() {
//
//                                    @Override
//                                    public void getMultiScreenStatus(int i, MultiScreens multiScreens) {
//                                        Log.e(TAG, "CarApiManager getRegisterMultiScreenListener i:" + i + "multiScreens:" + multiScreens);
//                                        if (multiScreens != null && multiScreens.getInfos() != null) {
//                                            List<MultiDisplayActivityInfo> multiDisplayActivityInfosList = Optional.ofNullable(multiScreens.getInfos())
//                                                    .map(list -> list.stream()
//                                                            .map(multiScreen -> {
//                                                                ActivityConfig activityConfig = new ActivityConfig();
//                                                                activityConfig.setScreenName(CommExtKt.covert2ScreenType(i));
//                                                                activityConfig.setVisible(multiScreen.getVisible() == 1);
//                                                                activityConfig.setEnable(multiScreen.getAvailable() == 1);
//                                                                return new MultiDisplayActivityInfo(
//                                                                        multiScreen.getPackageName(),
//                                                                        multiScreen.getAppName(),
//                                                                        multiScreen.getActivityName(),
//                                                                        Collections.singletonList(activityConfig),
//                                                                        multiScreen.getPreemptivePolicy() + 1,
//                                                                        0
//                                                                );
//                                                            })
//                                                            .collect(Collectors.toList()))
//                                                    .orElse(Collections.emptyList());
//                                            MultiDisplayManager.getInstance().syncMultiDisplayActivityInfo(multiDisplayActivityInfosList);
//                                            if (onViewActionClickListener == null || !onViewActionClickListener.onViewActionClick(taskInfo,
//                                                    TaskState.DOWNLOADABLE)) {
//                                                setEnabled(false);
//                                                if (!TaskProxy.getInstance().addTask(taskInfo)) {
//                                                    setEnabled(true);
//                                                }
//                                            }
//
//                                        }else{
//                                            showToast("该应用暂时下架,敬请期待！");
//                                            setEnabled(true);
//                                        }
//
//                                    }
//                                });
//
//                            }else if (onViewActionClickListener == null || !onViewActionClickListener.onViewActionClick(taskInfo,
//                                    TaskState.DOWNLOADABLE)) {
//                                setEnabled(false);
//                                if (!TaskProxy.getInstance().addTask(taskInfo)) {
//                                    setEnabled(true);
//                                }
//                            }
//
//                        }
//
//                    }else {
                        if (onViewActionClickListener == null || !onViewActionClickListener.onViewActionClick(taskInfo,
                                TaskState.DOWNLOADABLE)) {
                            setEnabled(false);
                            if (!TaskProxy.getInstance().addTask(taskInfo)) {
                                setEnabled(true);
                            }
                        }
//                    }

                    break;
                case TaskState.UPDATABLE:
                    try {
                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                        String apkSize = appInfo.getApkSize();

                        //磁盘检测
                        if (!Utils.INSTANCE.isSpaceEnough(apkSize)) {
                            showToast(R.string.app_install_no_space_enough);
                            return;
                        }
                    } catch (Throwable throwable) {
                        //
                    }

                    if (onViewActionClickListener == null || !onViewActionClickListener.onViewActionClick(taskInfo, TaskState.UPDATABLE)) {
                        setEnabled(false);
                        if (!TaskProxy.getInstance().addTask(taskInfo)) {
                            setEnabled(true);
                        }
                    }
                    break;
                case TaskState.DOWNLOAD_PENDING:
                    // empty
                    break;
                case TaskState.DOWNLOAD_STARTED:
                    // empty
                    break;
                case TaskState.DOWNLOAD_CONNECTED:
                    // empty
                    break;
                case TaskState.DOWNLOAD_PROGRESS:
                    try {
                        ExpandAppInfo app = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                        if (app.isForceUpdate() &&
                                ApkUtils.INSTANCE.isAppInstalled(context, app.getPackageName())) {
                            showToast(R.string.app_install_force_update_tips);
                            return;
                        }
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }

                    setEnabled(false);
                    if (!TaskProxy.getInstance().pauseDownload(taskInfo.id)) {
                        ITaskInfo iTaskInfo = TaskProxy.getInstance().getTask(taskInfo.id);
                        if (iTaskInfo != null) {
                            int currentStatus = iTaskInfo.status;
                            Log.e("info", "current status:" + currentStatus);
                        }
                        setEnabled(true);
                    }
                    break;
                case TaskState.DOWNLOAD_PAUSED:

                    if (onViewActionClickListener == null || !onViewActionClickListener.onViewActionClick(taskInfo,
                            TaskState.DOWNLOAD_PAUSED)) {
                        setEnabled(false);
                        // 不用resumeDownload，会有问题
                        if (!TaskProxy.getInstance().addTask(taskInfo)) {
                            setEnabled(true);
                        }
                    }
                    break;
                case TaskState.DOWNLOAD_COMPLETED:
                    setEnabled(false);
                    if (!TaskProxy.getInstance().addTask(taskInfo)) {
                        setEnabled(true);
                    }
                    break;
                case TaskState.DOWNLOAD_ERROR:
                    setEnabled(false);
                    if (!TaskProxy.getInstance().addTask(taskInfo)) {
                        setEnabled(true);
                    }
                    break;
                case TaskState.INSTALLABLE:
                    setEnabled(false);
                    if (!TaskProxy.getInstance().addTask(taskInfo)) {
                        setEnabled(true);
                    }
                    break;
                case TaskState.INSTALL_PENDING:
                    // empty
                    break;
                case TaskState.INSTALL_STARTED:
                    // empty
                    break;
                case TaskState.INSTALL_PROGRESS:
                    showToast(R.string.task_state_install_progress_default_toast);
                    break;
                case TaskState.INSTALL_ERROR:
                    setEnabled(false);
                    if (!TaskManager.getInstance().addTask(taskInfo)) {
                        setEnabled(true);
                    }
                    break;
                default:
                    break;
            }
        } else if (v == this) {
            //非P档限制
            if (VehicleApiManager.getInstance().isOnTheRoad()) {
                showToast(R.string.driving_restricted_use);
            } else {
                onClick(viewAction);
            }
        } else {
        }
    }

    /**
     * 下载失败、安装失败后获取任务状态
     */
    private int getTaskState(@NonNull TaskInfo taskInfo) {
        ExpandAppInfo appInfo = null;
        try {
            appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
        } catch (Throwable throwable) {
            //
        }
        if (appInfo != null) {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
            for (PackageInfo packageInfo : packageInfos) {
                if (packageInfo == null || !packageInfo.packageName.equals(appInfo.getPackageName())) {
                    continue;
                }
                if (packageInfo.versionCode < appInfo.getVersionCode()) {
                    int state = SpecialAppHelper.INSTANCE.getInstalledStatus(packageInfo.packageName);
                    taskInfo.setState(state);
                    return state;
                }
            }
        }
        taskInfo.setState(TaskState.DOWNLOADABLE);
        return TaskState.DOWNLOADABLE;
    }

    /**
     * 取消下载任务
     *
     * @param taskId
     */
    public void cancelDownloadingTask(String taskId) {
        TaskProxy.getInstance().removeTask(taskId);
    }


    /**
     * action按钮点击拦截监听
     *
     * @param listener 监听器
     */
    public void setOnViewActionClickListener(TaskLayout.OnViewActionClickListener listener) {
        onViewActionClickListener = listener;
    }

    public TaskLayout.OnViewActionClickListener getOnViewActionClickListener() {
        return onViewActionClickListener;
    }

    /**
     * 下载状态
     *
     * @param listener
     */
    public void setOnTaskChangeListener(OnTaskChangeListener listener) {
        onTaskChangeListener = listener;
    }

    /**
     * 检查App是否可用
     *
     * @param pkgName
     * @return
     */
    private boolean checkAppIsAvailable(String pkgName) {
        AppPolicyInfo appPolicyInfo = PolicyApiManager.getInstance().checkStartup(pkgName);
        Log.i(PolicyApiManager.TAG, "pkgName ==> " + pkgName + "; appPolicyInfo -> " + appPolicyInfo);
        if (appPolicyInfo == null) {
            return true;
        }
        Log.i(PolicyApiManager.TAG, "code ==> " + appPolicyInfo.getCode());
        if (appPolicyInfo.getCode() == 4) {
            return true;
        }
        showToast(appPolicyInfo.getMsg());
        return false;
    }

    private void canUse() {
        if (isCanUse) {
            return;
        }
        isCanUse = true;
        setAlpha(1.0f);
        viewAction.setEnabled(true);
        viewAction.setOnClickListener(this);
        setClickable(false);
        setOnClickListener(null);
    }

    private void onTheRoadCanNotUse() {
//        isCanUse = false;
//        setAlpha(0.6f);
//        setClickable(true);
//        setOnClickListener(this);
//        viewAction.setOnClickListener(null);
//        viewAction.setEnabled(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !isCanUse || super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onStateChange(List<AppPolicyInfo> appPolicyInfoList) {
        for (AppPolicyInfo appPolicyInfo : appPolicyInfoList) {
            if (getTaskInfo() != null && getTaskInfo().getState() == TaskState.OPENABLE &&
                    appInfo != null && Objects.equals(appInfo.getPackageName(), appPolicyInfo.getPkgName())) {
                Log.i(PolicyApiManager.TAG, "change ==> pkgName ==> " + appPolicyInfo.getPkgName() + "," + "code ==> " + appPolicyInfo.getCode());
                post(() -> {
                    init(getTaskInfo());
                });
            }
        }
    }

    /**
     * action按钮点击拦截监听
     */
    public interface OnViewActionClickListener {
        /**
         * action按钮点击
         *
         * @return 点击事件是否消费
         */
        boolean onViewActionClick(TaskInfo taskInfo, @TaskState int taskState);
    }

    /**
     * 下载状态
     */
    public interface OnTaskChangeListener {
        /**
         * @param taskState
         * @param isForceUpdate 是否强制下载
         */
        void onTaskChangeListener(@TaskState int taskState, Boolean isForceUpdate);
    }

    /**
     * 是否应用自己
     */
    public boolean isSelfApp(String packageName) {
        try {
            if (getContext() != null) {
                return getContext().getPackageName().equals(packageName);
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
        return false;
    }

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    private void setStateUpState() {
        List<String> canUserList = PolicyApiManager.getInstance().getCanUsePkgNameList();
        List<String> canNotUserList = PolicyApiManager.getInstance().getAppNotUsePkgNameList();
        if (canUserList != null && canUserList.contains(appInfo.getPackageName())) {
            canUse();
            Log.d(PolicyApiManager.TAG, "canUse pkgName ==> " + appInfo.getPackageName());
        }
        if (canNotUserList != null && canNotUserList.contains(appInfo.getPackageName())) {
            Log.e(PolicyApiManager.TAG, "onTheRoadCanNotUse pkgName ==> " + appInfo.getPackageName());
            onTheRoadCanNotUse();
        }
    }

    private int getDisplayId(Context context) {
        if (context != null) {
            var wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            return wm.getDefaultDisplay().getDisplayId();
        }
        return 0;
    }

}

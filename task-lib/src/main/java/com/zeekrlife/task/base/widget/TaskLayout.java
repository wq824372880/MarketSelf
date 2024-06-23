package com.zeekrlife.task.base.widget;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.zeekr.car.adaptapi.CarApiProxy;
import com.zeekr.car.adaptapi.CsdPosn;
import com.zeekr.car.adaptapi.SlideCsdPosnListener;
import com.zeekr.car.api.VehicleApiManager;
import com.zeekr.car.api.vehicle.IVehicleListener;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.ApkUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.common.util.IntentUtils;
import com.zeekrlife.common.util.ToastUtils;
import com.zeekrlife.common.util.Utils;
import com.zeekrlife.market.task.ITaskInfo;
import com.zeekrlife.net.interception.logging.util.XLog;
import com.zeekrlife.task.base.R;
import com.zeekrlife.task.base.bean.AppInfo;
import com.zeekrlife.task.base.bean.ExpandAppInfo;
import com.zeekrlife.task.base.bean.TaskInfo;
import com.zeekrlife.task.base.constant.TaskState;
import com.zeekrlife.task.base.constant.TaskType;
import com.zeekrlife.task.base.manager.TaskManager;
import com.zeekrlife.task.base.proxy.TaskProxy;

import java.util.List;

/**
 * 任务控件，包含action按钮、下载进度条、状态文本
 *
 * @author
 */
public class TaskLayout extends ConstraintLayout implements View.OnClickListener, IVehicleListener, SlideCsdPosnListener {

    public static final String TAG = "TaskLayout";
    /**
     * action按钮
     */
    private View viewAction;
    /**
     * 下载/安装进度条
     */
    private CircleProgressBar progressBar;
    /**
     * 状态文本
     */
    private TextView tvState;

    private Context context;

    private TaskInfo taskInfo;

    private OnViewActionClickListener onViewActionClickListener;

    private int btnSelectorId = R.drawable.selector_bg_btn_common;

    private int textSelectorId = R.color.selector_text_color_btn_common;

    /**
     * 是否可以使用
     */
    private volatile boolean isCanUse = true;

    private volatile boolean isOnTheRoad = false;

    /**
     * 是否在副驾位置
     */
    private volatile boolean csdInCopilotPosition = false;

    public TaskLayout(Context context) {
        this(context, null);
    }

    public TaskLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
        initView(context);
    }

    private void initData(Context context) {
        this.context = context;
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.task_layout_task, this, true);
        viewAction = findViewById(R.id.view_action);
        progressBar = findViewById(R.id.progress_bar);
        tvState = findViewById(R.id.tv_state);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        TaskProxy.getInstance().addTaskLayout(this);
        viewAction.setOnClickListener(this);
        //监听车辆挡位
        VehicleApiManager.getInstance().addIVehicleListener(this);
        //主驾、副驾切换监听
        CarApiProxy.getInstance(getContext().getApplicationContext()).addSlideCsdPosnListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        TaskProxy.getInstance().removeTaskLayout(this);
        viewAction.setOnClickListener(null);
        //移除车辆挡位监听
        VehicleApiManager.getInstance().removeIVehicleListener(this);
        //主驾、副驾监听移除
        CarApiProxy.getInstance(getContext().getApplicationContext()).removeSlideCsdPosnListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tvState.setEnabled(enabled);
        viewAction.setEnabled(enabled);
    }

    public void onTaskInfoChanged(TaskInfo taskInfo) {
        if (this.taskInfo == null || taskInfo == null) {
            return;
        }
        init(taskInfo, true);
    }

    /**
     * 设置深色主题
     */
    public void setDarkStyle() {
        btnSelectorId = R.drawable.selector_bg_btn_common_dark;
        textSelectorId = R.color.selector_text_color_btn_common_dark;
        viewAction.setBackgroundResource(btnSelectorId);
        tvState.setTextColor(ContextCompat.getColorStateList(context, textSelectorId));
        //progressBar.setDarkStyle();
    }

    public void init(TaskInfo taskInfo) {
        init(taskInfo, false);
    }

    public void init(TaskInfo taskInfo, boolean showToastIfErrorOccurred) {
        if (taskInfo == null) {
            setVisibility(View.INVISIBLE);
            return;
        }

        XLog.INSTANCE.i(TAG, "init taskInfo  >>>> " + taskInfo);

        int lastState = this.taskInfo == null ? -1 : this.taskInfo.getState();
        this.taskInfo = taskInfo;

        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setAlpha(1);
        setEnabled(true);

        switch (taskInfo.getState()) {
            case TaskState.OPENABLE:
                viewAction.setBackgroundResource(btnSelectorId);
                tvState.setText(R.string.task_state_openable);
                try {
                    AppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, AppInfo.class);

                    if (isSelfApp(appInfo.getPackageName())) {
                        setVisibility(View.INVISIBLE);
                        return;
                    }
                    if (IntentUtils.getLaunchAppIntent(appInfo.getPackageName()) == null) {
                        setEnabled(false);
                        tvState.setText(R.string.task_state_install_completed);
                    }
                    //检测行车挡位&以及主、副驾
                    verifyUsageRestrictions(csdInCopilotPosition, true);
                } catch (Throwable throwable) {
                    //
                }
                break;
            case TaskState.DOWNLOADABLE:
                viewAction.setBackgroundResource(btnSelectorId);
                tvState.setText(R.string.task_state_downloadable);
                canUse();
                break;
            case TaskState.UPDATABLE:
                viewAction.setBackgroundResource(btnSelectorId);
                tvState.setText(R.string.task_state_updatable);
                canUse();
                break;
            case TaskState.DOWNLOAD_PENDING:
            case TaskState.DOWNLOAD_STARTED:
            case TaskState.DOWNLOAD_CONNECTED:
                setEnabled(false);
                tvState.setText("");
                viewAction.setBackgroundResource(0);
                progressBar.setStatus(CircleProgressBar.Status.Waiting);
                progressBar.setVisibility(View.VISIBLE);
                //progressBar.setProgress(0);
                //viewAction.setBackgroundResource(btnSelectorId);
                //tvState.setText(R.string.task_state_download_pending);
                break;
            case TaskState.DOWNLOAD_PROGRESS:
                if (lastState == TaskState.DOWNLOAD_COMPLETED || lastState == TaskState.DOWNLOAD_ERROR) {
                    break;
                }

                if (taskInfo.total == 0) {
                    progressBar.setProgress(0);
                } else {
                    int progress = (int) (((double) taskInfo.soFar) / ((double) taskInfo.total) * progressBar.getMax());
                    if (progress > progressBar.getProgress()) {
                        progressBar.setProgress(progress);
                    }
                    if (progressBar.getProgress() == 0) {
                        progressBar.setProgress(4);
                    }
                }
                progressBar.setStatus(CircleProgressBar.Status.Loading);
                progressBar.setVisibility(View.VISIBLE);
                viewAction.setBackgroundResource(0);
                tvState.setText("");
                //tvState.setText(R.string.task_state_download_progress);

                //如果强制更新不可显示样式
                try {
                    ExpandAppInfo app = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                    if (app.isForceUpdate() && tvState.isEnabled() && ApkUtils.INSTANCE.isAppInstalled(context, app.getPackageName())) {
                        tvState.setEnabled(false);
                        progressBar.setAlpha(0.4f);
                    }
                } catch (Exception e) {
                    CommExtKt.logStackTrace(e);
                }
                break;
            case TaskState.DOWNLOAD_PAUSED:

                viewAction.setBackgroundResource(0);
                tvState.setText("");
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setStatus(CircleProgressBar.Status.Pause);
                if (taskInfo.total != 0) {
                    int progress = (int) (((double) taskInfo.soFar) / ((double) taskInfo.total) * progressBar.getMax());
                    if (progress > progressBar.getProgress()) {
                        progressBar.setProgress(progress);
                    }
                } else {
                    Log.e(TAG, "taskInfo.total == 0 !!!!");
                }
                break;
            case TaskState.DOWNLOAD_COMPLETED:
                if (taskInfo.type == TaskType.DOWNLOAD) {
                    progressBar.setProgress(0);
                    viewAction.setBackgroundResource(btnSelectorId);
                    tvState.setText(R.string.task_state_download_completed);
                }
                break;
            case TaskState.DOWNLOAD_ERROR:
                progressBar.setProgress(0);
                viewAction.setBackgroundResource(btnSelectorId);
                if (getTaskState(taskInfo) == TaskState.UPDATABLE) {
                    tvState.setText(R.string.task_state_updatable);
                } else {
                    tvState.setText(R.string.task_state_downloadable);
                }
                if (showToastIfErrorOccurred) {
                    showToast(R.string.task_state_download_error_default_toast);
                }
                break;
            case TaskState.INSTALLABLE:
                viewAction.setBackgroundResource(btnSelectorId);
                tvState.setText(R.string.task_state_installable);
                break;
            case TaskState.INSTALL_PENDING:
            case TaskState.INSTALL_STARTED:
                setEnabled(false);
                progressBar.setProgress(0);
                viewAction.setBackgroundResource(btnSelectorId);
                tvState.setText(R.string.task_state_install_pending);
                break;
            case TaskState.INSTALL_PROGRESS:
                if (lastState == TaskState.INSTALL_COMPLETED || lastState == TaskState.INSTALL_ERROR) {
                    break;
                }
                setEnabled(false);
                progressBar.setProgress(0);
                viewAction.setBackgroundResource(btnSelectorId);
                tvState.setText(R.string.task_state_install_progress);
                break;
            case TaskState.INSTALL_COMPLETED:
                progressBar.setProgress(0);
                viewAction.setBackgroundResource(btnSelectorId);
                tvState.setText(R.string.task_state_openable);
                try {
                    AppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, AppInfo.class);

                    if (isSelfApp(appInfo.getPackageName())) {
                        setVisibility(View.INVISIBLE);
                        return;
                    }

                    if (IntentUtils.getLaunchAppIntent(appInfo.getPackageName()) == null) {
                        setEnabled(false);
                        tvState.setText(R.string.task_state_install_completed);
                    }
                    //检测行车挡位&以及主、副驾
                    verifyUsageRestrictions(csdInCopilotPosition, true);
                } catch (Throwable throwable) {
                    //
                }
                break;
            case TaskState.INSTALL_ERROR:
                progressBar.setProgress(0);
                viewAction.setBackgroundResource(btnSelectorId);
                int taskState = getTaskState(taskInfo);
                if (taskState == TaskState.UPDATABLE) {
                    tvState.setText(R.string.task_state_updatable);
                } else if(taskState == TaskState.OPENABLE || taskState == TaskState.INSTALL_COMPLETED) {
                    tvState.setText(R.string.task_state_openable);
                    return;
                } else {
                    tvState.setText(R.string.task_state_downloadable);
                }
                if (showToastIfErrorOccurred) {
                    showToast(R.string.task_state_install_error_default_toast);
                }
                break;
            default:
                viewAction.setBackgroundResource(btnSelectorId);
                break;
        }
    }

    private void showToast(int resId) {
        try {
            ToastUtils.INSTANCE.show(getContext().getString(resId));
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == viewAction) {
            if (taskInfo == null) {
                return;
            }
            taskInfo.type = TaskType.DOWNLOAD_INSTALL;
            XLog.INSTANCE.e(TAG, "onClick this.taskInfo >>>> " + taskInfo);

            switch (taskInfo.getState()) {
                case TaskState.INSTALL_COMPLETED:
                case TaskState.OPENABLE:
                    try {
                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                        XLog.INSTANCE.e(TAG, "openable >>>> " + appInfo.getApkName());

                        boolean isOnTheRoad = VehicleApiManager.getInstance().isOnTheRoad();
                        XLog.INSTANCE.e(TAG, "isOnTheRoad >>>> " + isOnTheRoad);
                        if (!duringDrivingIsAvailable(appInfo, csdInCopilotPosition, isOnTheRoad)) {
                            showToast(R.string.driving_restricted_use);
                            return;
                        }
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                    TaskProxy.getInstance().openApp(context, taskInfo,0);
                    break;
                case TaskState.DOWNLOADABLE:
                    try {
                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                        String category = appInfo.getCategoryName();
                        String productId = appInfo.getAppVersionId();
                        String name = appInfo.getApkName();
                        String apkSize = appInfo.getApkSize();
                        String versionName = appInfo.getVersionName();
                        //磁盘检测
                        if (!Utils.INSTANCE.isSpaceEnough(apkSize)) {
                            showToast(R.string.app_install_no_space_enough);
                            return;
                        }
                    } catch (Throwable throwable) {
                        //
                    }

                    if (onViewActionClickListener == null || !onViewActionClickListener.onViewActionClick(taskInfo,
                        TaskState.DOWNLOADABLE)) {
                        setEnabled(false);
                        if (!TaskProxy.getInstance().addTask(taskInfo)) {
                            setEnabled(true);
                        }
                    }
                    break;
                case TaskState.UPDATABLE:
                    try {
                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                        String category = appInfo.getCategoryName();
                        String productId = appInfo.getAppVersionId();
                        String name = appInfo.getApkName();
                        String apkSize = appInfo.getApkSize();
                        String versionName = appInfo.getVersionName();

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
                        if (app.isForceUpdate() && ApkUtils.INSTANCE.isAppInstalled(context, app.getPackageName())) {
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

                    //try {
                    //    ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                    //    String apkSize = appInfo.getApkSize();
                    //    //磁盘检测
                    //    if (!Utils.INSTANCE.isSpaceEnough(apkSize)) {
                    //        showToast(R.string.app_install_no_space_enough);
                    //        return;
                    //    }
                    //} catch (Throwable throwable) {
                    //    //
                    //}

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
                    taskInfo.setState(TaskState.UPDATABLE);
                    return TaskState.UPDATABLE;
                }
            }
        }
        taskInfo.setState(TaskState.DOWNLOADABLE);
        return TaskState.DOWNLOADABLE;
    }

    /**
     * action按钮点击拦截监听
     *
     * @param listener 监听器
     */
    public void setOnViewActionClickListener(OnViewActionClickListener listener) {
        onViewActionClickListener = listener;
    }

    public OnViewActionClickListener getOnViewActionClickListener() {
        return onViewActionClickListener;
    }

    /**
     * @param isSupport isSupport
     */
    @Override
    public void onGearSupportChanged(boolean isSupport) {
        Log.e("info", "onGearSupportChanged:" + isSupport);
    }

    /**
     * 挡位变化监听
     *
     * @param i i
     */
    @Override
    public void onGearChanged(int i) {
        try {
            verifyUsageRestrictions(csdInCopilotPosition, false);
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    /**
     * 校验P挡使用限制
     */
    public void verifyUsageRestrictions(boolean isCopilot, boolean isForceRefresh) {

        boolean isOnTheRoad = VehicleApiManager.getInstance().isOnTheRoad();

        boolean change = isForceRefresh || (this.isOnTheRoad != isOnTheRoad) || (this.csdInCopilotPosition != isCopilot);

        if (change) {
            this.isOnTheRoad = isOnTheRoad;
            this.csdInCopilotPosition = isCopilot;

            Log.e("info", "TaskState:" + taskInfo.getState());
            if (taskInfo.getState() != TaskState.OPENABLE && taskInfo.getState() != TaskState.INSTALL_COMPLETED) {
                Log.e("info", "TaskState != TaskState.OPENABLE && taskInfo.getState() != TaskState.INSTALL_COMPLETED");
                return;
            }

            Log.e("info", "postVerifyUsage");
            post(new Runnable() {
                @Override
                public void run() {
                    if (taskInfo != null && taskInfo.expand != null && taskInfo.expand.length() > 0) {
                        ExpandAppInfo appInfo = GsonUtils.fromJson(taskInfo.expand, ExpandAppInfo.class);
                        if (duringDrivingIsAvailable(appInfo, isCopilot, isOnTheRoad)) {
                            canUse();
                        } else {
                            onTheRoadCanNotUse();
                        }
                    }
                }
            });
        }
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
        isCanUse = false;
        setAlpha(0.6f);
        setClickable(true);
        setOnClickListener(this);
        viewAction.setOnClickListener(null);
        viewAction.setEnabled(false);
    }

    /**
     * 行车中应用是否可用
     *
     * @param appInfo 应用信息
     * @param isCopilot 是否在副驾位置
     * @param isOnTheRoad 是否行车中
     * @return true 可以使用  false 有行车限制不能使用
     */
    private boolean duringDrivingIsAvailable(ExpandAppInfo appInfo, boolean isCopilot, boolean isOnTheRoad) {
        if (isCopilot) {
            //副驾位置
            return duringDrivingIsAvailable(isOnTheRoad, appInfo.getSupportDrivingPassengerUser());
        } else {
            //主驾位置
            return duringDrivingIsAvailable(isOnTheRoad, appInfo.getSupportDrivingUser());
        }
    }

    private boolean duringDrivingIsAvailable(boolean isOnTheRoad, int isSupportDrivingUse) {
        if (isOnTheRoad && isSupportDrivingUse == 0) {
            //on The Road CanNotUse;
            return false;
        } else {
            //can Use;
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !isCanUse || super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onCsdSlide(int position) {
        //判断是否副驾驶位置
        verifyUsageRestrictions(position == CsdPosn.SLAG_CSD_POSN_COPILOTPOSN, false);
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
}

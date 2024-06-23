package com.zeekrlife.market.task.processor.handler;

import android.util.Log;
import com.zeekr.basic.Common;
import com.zeekrlife.market.task.TaskCallbackImpl;
import com.zeekrlife.market.task.TaskErrorCode;
import com.zeekrlife.market.task.TaskStatus;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.data.source.TasksRepository;
import com.zeekrlife.market.task.download.DownloadManager;
import com.zeekrlife.market.task.install.InstallCallback;
import com.zeekrlife.market.task.install.InstallCenter;
import com.zeekrlife.market.task.processor.TaskHandler;
import com.zeekrlife.market.task.processor.TaskRequest;
import com.zeekrlife.net.interception.logging.util.LogUtils;
import com.zeekrlife.net.interception.logging.util.XLog;

public class InstallHandler implements TaskHandler {
    private static final String TAG = "zzzInstallHandler";

/**
 * 处理安装任务的请求。
 * 对于给定的任务请求，此方法会根据安装流程的不同阶段更新任务状态，
 * 并通过回调接口通知外部安装的进度和状态变化。
 *
 * @param chain 任务链，用于控制任务流程的继续或终止。
 */
@Override
public void handle(final Chain chain) {
    // 获取当前任务请求和任务实体
    final TaskRequest taskRequest = chain.getRequest();
    final TaskEntity taskInfo = taskRequest.taskInfo;

    // 注释掉的代码块：系统限制检查和任务限制检查。
    // 如果开启系统限制检查并且任务受系统限制，则终止任务并报安装错误。

    // 记录安装开始的日志，并更新任务状态为安装等待。
    XLog.INSTANCE.e(TAG, "install pending: taskId = " + taskInfo.getId());
    taskInfo.setStatus(TaskStatus.INSTALL_PENDING);
    TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onInstallPending, taskInfo);

    // 执行安装操作，并在安装的各个阶段更新任务状态和进度。
    Log.e("info", "开始执行安装：" + taskInfo.getExpand());
    InstallCenter.getInstance().install(Common.app, taskInfo, new InstallCallback() {
        @Override
        public void installStarted() {
            // 安装开始时更新任务状态，并通知安装开始。
            XLog.INSTANCE.e(TAG, "install started: taskId = " + taskInfo.getId());
            taskInfo.setStatus(TaskStatus.INSTALL_STARTED);
            TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onInstallStarted, taskInfo);
        }

        @Override
        public void installProgress(float progress) {
            // 安装进行中，更新安装进度和任务状态，并通知进度更新。
            Log.d(TAG, "install progress: taskId = " + taskInfo.getId() + ", progress = " + progress);
            taskInfo.setStatus(TaskStatus.INSTALL_PROGRESS);
            taskInfo.setInstallProgress(progress);
            TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onInstallProgress, taskInfo);
        }

        @Override
        public void installCompleted() {
            // 安装完成时更新任务状态，清除相关资源，并通知安装完成。
            XLog.INSTANCE.e(TAG, "install completed: taskId = " + taskInfo.getId());
            taskInfo.setStatus(TaskStatus.INSTALL_COMPLETED);
            TasksRepository.getInstance().updateTask(taskInfo);
            DownloadManager.get().clear(taskInfo);
            TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onInstallCompleted, taskInfo);
            chain.proceed(taskRequest); // 继续任务链的执行
        }

        @Override
        public void installError() {
            // 安装出错时更新任务状态为错误，并清除相关资源，然后终止任务链的执行。
            // Log.d(TAG, "install error: taskId = " + taskInfo.getId());
            Log.d(TAG, "安装失败：" + taskInfo.getExpand());
            taskInfo.setStatus(TaskStatus.INSTALL_ERROR);
            taskInfo.setErrorCode(TaskErrorCode.INSTALL_FAILURE_DEFAULT);
            TasksRepository.getInstance().updateTask(taskInfo);
            DownloadManager.get().clear(taskInfo);
            TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onInstallError, taskInfo);
            chain.abort(); // 终止任务链的执行
        }
    });
}

}

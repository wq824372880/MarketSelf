package com.zeekrlife.market.task.processor.handler;

import android.util.Log;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.market.task.TaskCallbackImpl;
import com.zeekrlife.market.task.TaskErrorCode;
import com.zeekrlife.market.task.TaskStatus;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.data.source.TasksRepository;
import com.zeekrlife.market.task.download.DownloadListener;
import com.zeekrlife.market.task.download.DownloadManager;
import com.zeekrlife.market.task.processor.TaskHandler;
import com.zeekrlife.market.task.processor.TaskRequest;
import com.zeekrlife.net.interception.logging.util.XLog;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import okhttp3.HttpUrl;

public class DownloadHandler implements TaskHandler {
    private static final String TAG = "zzzDownloadHandler";

    /**
     * 处理下载任务的请求。
     * 对于给定的下载任务，此方法会验证其URL的合法性，并根据下载的不同状态调用相应的回调方法。
     * 如果任务启动成功，则会继续处理链中的下一个请求；如果遇到错误，则会终止任务并反馈错误信息。
     *
     * @param chain 包含下载任务请求信息的链式结构，用于继续或中断请求处理。
     */
    @Override
    public void handle(final Chain chain) {
        final TaskRequest taskRequest = chain.getRequest();
        final TaskEntity taskInfo = taskRequest.taskInfo;

        String url = taskInfo.getUrl();
        Log.d(TAG, "parse url: " + url);
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            // URL解析失败，更新任务状态为错误，并提交错误回调
            taskInfo.setStatus(TaskStatus.DOWNLOAD_ERROR);
            taskInfo.setErrorCode(TaskErrorCode.INSTALL_FAILURE_URL_ILLEGAL);
            TasksRepository.getInstance().updateTask(taskInfo);
            TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadError, taskInfo);
            chain.abort();
            return;
        }

        // 下载管理器开始处理下载任务，并注册下载状态监听器
        DownloadManager.get().start(taskRequest.taskInfo, new DownloadListener() {
            @Override
            protected void pending(final TaskEntity taskInfo) {
                // 任务进入等待状态，更新任务状态并提交等待回调
                XLog.INSTANCE.d(TAG, "pending: taskId = " + taskInfo.getId());
                TasksRepository.getInstance().updateTask(taskInfo);
                TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadPending, taskInfo);
            }

            @Override
            protected void started(final TaskEntity taskInfo) {
                // 任务开始下载，更新任务状态并提交开始回调
                XLog.INSTANCE.d(TAG, "started: taskId = " + taskInfo.getId());
                TasksRepository.getInstance().updateTask(taskInfo);
                TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadStarted, taskInfo);
            }

            @Override
            protected void connected(final TaskEntity taskInfo) {
                // 下载连接建立，更新任务状态并提交连接回调
                XLog.INSTANCE.d(TAG, "connected: taskId = "
                        + taskInfo.getId()
                        + ", currentOffset = "
                        + taskInfo.getSoFarBytes()
                        + ", totalLength = "
                        + taskInfo.getTotalBytes());
                TasksRepository.getInstance().updateTask(taskInfo);
                TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadConnected, taskInfo);
            }

            @Override
            protected void progress(final TaskEntity taskInfo) {
                Log.d(TAG, "progress: taskId = " + taskInfo.getId()
                        + ", currentOffset = " + taskInfo.getSoFarBytes() + ", totalLength = " + taskInfo.getTotalBytes());
                // 下载进度更新，提交进度回调
                TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadProgress, taskInfo);
            }

            @Override
            protected void completed(final TaskEntity taskInfo) {
                // 任务下载完成，更新任务状态并提交完成回调，然后继续处理请求链
                XLog.INSTANCE.d(TAG, "completed: taskId = " + taskInfo.getId());
                TasksRepository.getInstance().updateTask(taskInfo);
                TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadCompleted, taskInfo);
                chain.proceed(taskRequest);
            }

            @Override
            protected void paused(final TaskEntity taskInfo) {
                // 任务暂停，更新任务状态并提交暂停回调，然后中断请求链
                XLog.INSTANCE.d(TAG, "paused: taskId = " + taskInfo.getId());
                TasksRepository.getInstance().updateTask(taskInfo);
                TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadPaused, taskInfo);
                chain.abort();
            }

            @Override
            protected void error(final TaskEntity taskInfo, Exception e) {
                try {
                    // 下载过程中出现错误，根据错误类型更新任务状态并提交错误回调，然后中断请求链
                    XLog.INSTANCE.e(TAG, "error: taskId = " + taskInfo.getId());
                    XLog.INSTANCE.e(TAG, "error:" + Log.getStackTraceString(e));
                    if (!(e instanceof SSLException || e instanceof UnknownHostException || e instanceof SocketException
                            || e instanceof SocketTimeoutException)) {
                        XLog.INSTANCE.e(TAG, "error:clear app");
                        DownloadManager.get().clear(taskInfo);
                    } else {
                        taskInfo.setStatus(TaskStatus.DOWNLOAD_PAUSED);
                        taskInfo.setErrorCode(TaskErrorCode.DOWNLOAD_FAILURE_BY_NET_ERROR);
                        XLog.INSTANCE.e(TAG, "error:retain app");
                    }
                    TasksRepository.getInstance().updateTask(taskInfo);
                    TaskCallbackImpl.getInstance().submit(TaskCallbackImpl.Type.onDownloadError, taskInfo);
                    chain.abort();
                } catch (Throwable throwable) {
                    CommExtKt.logStackTrace(throwable);
                }
            }
        });
    }
}

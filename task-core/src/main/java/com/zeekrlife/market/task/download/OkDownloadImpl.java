package com.zeekrlife.market.task.download;

import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListener3;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil;
import com.zeekrlife.market.task.TaskErrorCode;
import com.zeekrlife.market.task.TaskStatus;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.utils.FileVerifyUtils;

import java.io.File;

class OkDownloadImpl extends DownloadManager {

    public OkDownloadImpl() {
        DownloadDispatcher.setMaxParallelRunningCount(1);
    }

    /**
     * 开始下载任务。
     *
     * @param taskInfo 任务实体信息，包含下载任务的URL和保存路径等。
     * @param listener 下载状态监听器，用于回调下载过程中的各种状态。
     */
    @Override
    public void start(@NonNull final TaskEntity taskInfo, @NonNull final DownloadListener listener) {
        // 构建下载任务配置，并创建下载任务实例
        DownloadTask downloadTask =
            new DownloadTask.Builder(taskInfo.getUrl(), new File(taskInfo.getPath())).setMinIntervalMillisCallbackProcess(250)
                .setAutoCallbackToUIThread(false)
                .setConnectionCount(1)
                .build();

        // 设置任务的downloadId为下载任务的ID
        taskInfo.setDownloadId(String.valueOf(downloadTask.getId()));

        // 设置任务状态为准备中，并通知监听器
        taskInfo.setStatus(TaskStatus.DOWNLOAD_PENDING);
        listener.pending(taskInfo);

        // 将下载任务加入队列，开始下载
        downloadTask.enqueue(new DownloadListener3() {
            @Override
            protected void started(@NonNull DownloadTask task) {
                // 下载开始，更新任务状态并通知监听器
                taskInfo.setStatus(TaskStatus.DOWNLOAD_STARTED);
                listener.started(taskInfo);
            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
                // 连接成功，更新任务状态及进度，并通知监听器
                taskInfo.setStatus(TaskStatus.DOWNLOAD_CONNECTED);
                taskInfo.setSoFarBytes(currentOffset);
                taskInfo.setTotalBytes(totalLength);
                listener.connected(taskInfo);
            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                // 下载中，更新进度，并通知监听器
                if (taskInfo.getStatus() == TaskStatus.DOWNLOAD_CONNECTED) {
                    taskInfo.setStatus(TaskStatus.DOWNLOAD_PROGRESS);
                }
                taskInfo.setSoFarBytes(currentOffset);
                taskInfo.setTotalBytes(totalLength);
                listener.progress(taskInfo);
            }

            @Override
            protected void canceled(@NonNull DownloadTask task) {
                // 下载取消，更新任务状态并通知监听器
                taskInfo.setStatus(TaskStatus.DOWNLOAD_PAUSED);
                listener.paused(taskInfo);
            }

            @Override
            protected void completed(@NonNull DownloadTask task) {
                // 下载完成，异步处理完成状态
                if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                    ThreadPoolUtil.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            taskCompleted();
                        }
                    },0);
                } else {
                    taskCompleted();
                }
            }

            private void taskCompleted() {
                // 完成下载，进行文件校验，根据结果更新任务状态并通知监听器
                taskInfo.setStatus(TaskStatus.DOWNLOAD_COMPLETED);
                if (FileVerifyUtils.verify(taskInfo.getPath(), taskInfo.getHash())) {
                    listener.completed(taskInfo);
                } else {
                    taskInfo.setStatus(TaskStatus.DOWNLOAD_ERROR);
                    taskInfo.setErrorCode(TaskErrorCode.PRE_INSTALL_FAILURE_CHECK_HASH_FAILED);
                    listener.error(taskInfo, new Exception("file verification exception"));
                }
            }

            @Override
            protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
                // 下载出错，更新任务状态并通知监听器
                try {
                    taskInfo.setStatus(TaskStatus.DOWNLOAD_ERROR);
                    taskInfo.setErrorCode(TaskErrorCode.DOWNLOAD_FAILURE_DEFAULT);
                    listener.error(taskInfo, e);
                } catch (Throwable throwable) {
                    CommExtKt.logStackTrace(throwable);
                }
            }

            @Override
            protected void warn(@NonNull DownloadTask task) {
                // 警告，当前实现为空
            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
                // 重试逻辑，当前实现为空
            }
        });
    }

    @Override
    public void pause(@NonNull final TaskEntity taskInfo) {
        OkDownload.with().downloadDispatcher().cancel(Integer.parseInt(taskInfo.getDownloadId()));
    }

    @Override
    public void clear(@NonNull final TaskEntity taskInfo) {
        try {
            String downloadId = taskInfo.getDownloadId();
            if (downloadId == null) {
                return;
            }
            //清除数据库
            OkDownload.with().breakpointStore().remove(Integer.parseInt(downloadId));
            //清除文件
            ThreadPoolUtil.runOnSubThread(() -> {
                try {
                    final File targetFile = new File(taskInfo.getPath());
                    if (targetFile.exists()) {
                        if (!targetFile.delete()) {
                            Log.e("", "delete file error" + taskInfo.getPath());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },0);
        } catch (Throwable throwable) {
            CommExtKt.logStackTrace(throwable);
        }
    }

    @Override
    public void pauseAll() {
        OkDownload.with().downloadDispatcher().cancelAll();
    }

    @Override
    public long getTotalLength(@NonNull final TaskEntity taskInfo) {
        final BreakpointInfo info = OkDownload.with().breakpointStore().get(Integer.parseInt(taskInfo.getDownloadId()));
        if (info == null) {
            return 0;
        }
        return info.getTotalLength();
    }

    @Override
    public long getCurrentOffset(@NonNull final TaskEntity taskInfo) {
        final BreakpointInfo info = OkDownload.with().breakpointStore().get(Integer.parseInt(taskInfo.getDownloadId()));
        if (info == null) {
            return 0;
        }
        return info.getTotalOffset();
    }

    @Override
    public int getStatus(@NonNull final TaskEntity taskInfo) {
        String filePath = taskInfo.getPath();
        int index = filePath.lastIndexOf("/");
        String parentPath = filePath.substring(0, index);
        String fileName = filePath.substring(index + 1);
        StatusUtil.Status status = StatusUtil.getStatus(taskInfo.getUrl(), parentPath, fileName);
        switch (status) {
            case PENDING:
                return TaskStatus.DOWNLOAD_PENDING;
            case RUNNING:
                return TaskStatus.DOWNLOAD_PROGRESS;
            case IDLE:
                return TaskStatus.DOWNLOAD_PAUSED;
            case COMPLETED:
                return TaskStatus.DOWNLOAD_COMPLETED;
            default:
                return TaskStatus.INVALID;
        }
    }
}

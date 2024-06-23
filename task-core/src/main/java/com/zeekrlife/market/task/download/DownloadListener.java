package com.zeekrlife.market.task.download;


import com.zeekrlife.market.task.data.source.TaskEntity;

public abstract class DownloadListener {

    protected abstract void pending(TaskEntity taskInfo);

    protected abstract void started(TaskEntity taskInfo);

    protected abstract void connected(TaskEntity taskInfo);

    protected abstract void progress(TaskEntity taskInfo);

    protected abstract void completed(TaskEntity taskInfo);

    protected abstract void paused(TaskEntity taskInfo);

    protected abstract void error(TaskEntity taskInfo, Exception e);
}

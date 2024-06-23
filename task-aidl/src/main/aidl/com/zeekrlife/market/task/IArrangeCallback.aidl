// IArrangeCallback.aidl
package com.zeekrlife.market.task;

interface IArrangeCallback {

    void onDownloadPending(String taskId);

    void onDownloadStarted(String taskId);

    void onDownloadConnected(String taskId, long soFarBytes, long totalBytes);

    void onDownloadProgress(String taskId, long soFarBytes, long totalBytes);

    void onDownloadCompleted(String taskId);

    void onDownloadPaused(String taskId);

    void onDownloadError(String taskId, int errorCode);

    void onInstallPending(String taskId);

    void onInstallStarted(String taskId);

    void onInstallProgress(String taskId, float progress);

    void onInstallCompleted(String taskId);

    void onInstallError(String taskId, int errorCode);
}

package com.zeekrlife.market.task

import android.os.IBinder

abstract class AbstractArrangeCallback : IArrangeCallback {
    abstract fun onDownloadFinished(taskId: String, successful: Boolean, errorCode: Int)
    abstract fun onInstallFinished(taskId: String, successful: Boolean, errorCode: Int)
    override fun onDownloadPending(taskId: String) {}
    override fun onDownloadStarted(taskId: String) {}
    override fun onDownloadConnected(taskId: String, soFarBytes: Long, totalBytes: Long) {}
    override fun onDownloadProgress(taskId: String, soFarBytes: Long, totalBytes: Long) {}
    override fun onDownloadPaused(taskId: String) {}
    override fun onDownloadCompleted(taskId: String) {
        onDownloadFinished(taskId, true, -1)
    }

    override fun onDownloadError(taskId: String, errorCode: Int) {
        onDownloadFinished(taskId, false, errorCode)
    }

    override fun onInstallPending(taskId: String) {}
    override fun onInstallStarted(taskId: String) {}
    override fun onInstallProgress(taskId: String, progress: Float) {}
    override fun onInstallCompleted(taskId: String) {
        onInstallFinished(taskId, true, -1)
    }

    override fun onInstallError(taskId: String, errorCode: Int) {
        onInstallFinished(taskId, false, errorCode)
    }

    override fun asBinder(): IBinder? = null
}
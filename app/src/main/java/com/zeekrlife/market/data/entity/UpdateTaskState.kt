package com.zeekrlife.market.data.entity

data class AppUpdateState(val update: Boolean = false, val state: Int = Status.INVALID, val progress: Int = 0,val taskId:String? = null,val soFarBytes: Long?=0,val totalBytes: Long?=0,
                          val throwable: Throwable? = null) {
    internal object Status {
        val INVALID = 0
        val CHECK_UPDATE = 1
        val DOWNLOAD_STARTED = 2
        val SHOW_DOWNLOADING = 3
        val DOWNLOAD_PROGRESS = 4
        val DOWNLOAD_ERROR = 5
        val DOWNLOAD_COMPLETED = 6
        val INSTALLING = 7
        val INSTALL_COMPLETED = 8
        val INSTALL_ERROR = 9
        val ERROR_NO_SPACE_ENOUGH = 10
    }
}



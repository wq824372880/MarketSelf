package com.zeekrlife.market.task;

public interface TaskStatus {
    /**
     * 0
     */
    int INVALID = 0;
    /**
     * 1
     */
    int DOWNLOAD_PENDING = 1;
    /**
     * 6
     */
    int DOWNLOAD_STARTED = 6;
    /**
     * -2
     */
    int DOWNLOAD_PAUSED = -2;
    /**
     * 2
     */
    int DOWNLOAD_CONNECTED = 2;
    /**
     * 3
     */
    int DOWNLOAD_PROGRESS = 3;
    /**
     * -3
     */
    int DOWNLOAD_COMPLETED = -3;
    /**
     * -1
     */
    int DOWNLOAD_ERROR = -1;
    /**
     * 100
     */
    int INSTALL_PENDING = 100;
    /**
     * 101
     */
    int INSTALL_STARTED = 101;
    /**
     * 102
     */
    int INSTALL_PROGRESS = 102;
    /**
     * 103
     */
    int INSTALL_COMPLETED = 103;
    /**
     * 104
     */
    int INSTALL_ERROR = 104;
}

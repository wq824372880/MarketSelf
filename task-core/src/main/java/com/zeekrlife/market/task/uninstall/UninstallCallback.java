package com.zeekrlife.market.task.uninstall;

public interface UninstallCallback {
    /**
     * 卸载开始
     */
    void uninstallStarted();

    /**
     * 卸载成功
     */
    void uninstallCompleted();

    /**
     * 卸载失败
     */
    void uninstallError();
}

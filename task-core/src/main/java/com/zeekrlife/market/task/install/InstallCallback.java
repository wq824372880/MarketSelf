package com.zeekrlife.market.task.install;

public interface InstallCallback {
    /**
     * 安装开始
     */
    void installStarted();

    /**
     * 安装中
     */
    void installProgress(float progress);

    /**
     * 安装成功
     */
    void installCompleted();

    /**
     * 安装失败
     */
    void installError();
}

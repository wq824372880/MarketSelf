package com.zeekrlife.market.task.download;

import com.zeekrlife.market.task.data.source.TaskEntity;

public abstract class DownloadManager {

    private static volatile DownloadManager INSTANCE;

    /**
     * 获取DownloadManager的单例实例。
     * 该方法采用双重检查锁定（Double-Checked Locking）的方式确保线程安全地创建并返回单例实例。
     * 在多线程环境下，第一个线程发现INSTANCE为null时，会加锁，然后检查INSTANCE是否为null，
     * 如果是，则创建实例。后续的线程在第一次加锁之后，会再次检查INSTANCE是否已经创建，
     * 如果已经创建，则直接返回，避免了重复加锁和创建实例的操作。
     *
     * @return DownloadManager的单例实例。
     */
    public static DownloadManager get() {
        // 如果INSTANCE未被初始化，则进入同步块
        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                // 在同步块内再次检查INSTANCE是否为null，防止多个线程同时通过第一层null检查
                if (INSTANCE == null) {
                    INSTANCE = create(); // 安全地创建单例实例
                }
            }
        }
        return INSTANCE; // 返回单例实例
    }

    /**
     * 创建并返回一个DownloadManager实例。
     * 该方法为私有静态方法，不接受任何参数。
     *
     * @return DownloadManager 返回一个实现了下载管理接口的实例，
     *         用于后续的下载操作。
     */
    private static DownloadManager create() {
        // 实例化OkDownloadImpl作为DownloadManager的实现
        return new OkDownloadImpl();
    }

    public abstract void start(TaskEntity taskInfo, DownloadListener listener);

    public abstract void pause(TaskEntity taskInfo);

    public abstract void clear(TaskEntity taskInfo);

    public abstract void pauseAll();

    public abstract long getTotalLength(TaskEntity taskInfo);

    public abstract long getCurrentOffset(TaskEntity taskInfo);

    public abstract int getStatus(TaskEntity taskInfo);
}

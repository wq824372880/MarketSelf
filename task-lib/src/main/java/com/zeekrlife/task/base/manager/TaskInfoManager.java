/**
 * TaskInfoManager类，用于管理任务信息的单例模式实现。
 * 使用ConcurrentHashMap来存储任务ID与任务信息的映射，以支持线程安全的读写操作。
 */
package com.zeekrlife.task.base.manager;

import com.zeekrlife.task.base.bean.TaskInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskInfoManager {
    // 单例模式的实例，使用volatile修饰以保证可见性
    private volatile static TaskInfoManager instance;

    // 任务信息的映射，使用ConcurrentHashMap以支持线程安全的操作
    public final Map<String, TaskInfo> taskInfoMap = new ConcurrentHashMap<>();

    /**
     * 获取TaskInfoManager的单例实例。
     * 实现双检锁模式，以保证线程安全的单例初始化。
     *
     * @return TaskInfoManager的单例实例。
     */
    public static TaskInfoManager getInstance() {
        if (instance == null) {
            synchronized (TaskInfoManager.class) {
                if (instance == null) {
                    instance = new TaskInfoManager();
                }
            }
        }
        return instance;
    }

    // 私有构造函数，防止外部实例化
    public TaskInfoManager() {

    }
}


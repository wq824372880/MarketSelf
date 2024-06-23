package com.zeekrlife.market.task;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.zeekrlife.net.interception.logging.util.XLog;


/**
 * 任务服务类，继承自Service。
 * 用于在后台执行任务。
 */
public class TaskService extends Service {
    private static final String TAG = "TaskService"; // 日志标签
    private TaskServiceImpl taskService; // 任务服务实现类的实例

    /**
     * 绑定服务时调用。
     *
     * @param intent 意图，提供绑定时传递的数据。
     * @return 返回服务的IBinder接口，允许客户端与服务建立连接。
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return taskService;
    }

    /**
     * 服务创建时调用，用于初始化服务。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        XLog.INSTANCE.d(TAG, "onCreate() called"); // 记录日志
        taskService = new TaskServiceImpl(this); // 初始化任务服务实现
    }

    /**
     * 服务启动时调用，用于处理传入的意图。
     *
     * @param intent  启动服务时传递的意图。
     * @param flags   启动标志，提供额外数据。
     * @param startId 一个唯一的整数，标识此启动请求。
     * @return 返回命令执行的结果。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 服务销毁时调用，用于清理资源。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 系统内存不足时调用，提示服务释放资源。
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 系统要求服务释放未使用的资源时调用。
     *
     * @param level  内存级别，指示当前内存状况。
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}


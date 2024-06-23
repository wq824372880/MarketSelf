package com.zeekr.car.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil {

    public static Handler sMainHandler = new Handler(Looper.getMainLooper());
    private static final String TAG = ThreadPoolUtil.class.getName();

    /**
     * 线程池的参数采用AsyncTask的配置 -- android 26
     */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    public static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    public static final int KEEP_ALIVE_SECONDS = 30;
    private static final BlockingQueue<Runnable> POOL_WORK_QUEUE = new LinkedBlockingQueue<>(128);
    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    private static final ScheduledThreadPoolExecutor THREAD_POOL_EXECUTOR;

    public static class CustomThreadFactory implements ThreadFactory {

        public static String namePrefix = "CustomThreadFactory";

        public CustomThreadFactory(String name) {
            namePrefix = name;
        }

        public CustomThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            final AtomicInteger mCount = new AtomicInteger(1);
            return new Thread(r, namePrefix + " # " + mCount.getAndIncrement());
        }
    }

    static {
//        ThreadPoolExecutor threadPoolExecutor =
//            new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, POOL_WORK_QUEUE,
//                new CustomThreadFactory());
//        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, new CustomThreadFactory());
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR.setKeepAliveTime(KEEP_ALIVE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 切换到主线程
     *
     * @param runnable Runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        runOnUiThread(runnable, 0);
    }

    /**
     * 延时切换到主线程
     *
     * @param runnable Runnable
     * @param delayed 时长 Millis
     */
    public static void runOnUiThread(Runnable runnable, long delayed) {
        sMainHandler.postDelayed(runnable, delayed);
    }

    /**
     * 从线程池中创建子线程执行异步任务
     * 在任务数超过128，或者线程池Shutdown时将跳过这条任务
     *
     * @param runnable Runnable
     */
    public static void runOnSubThread(Runnable runnable, long delayed) {

        if (THREAD_POOL_EXECUTOR.getQueue().size() == 128 || THREAD_POOL_EXECUTOR.isShutdown()) {
            Log.e(TAG, "线程池爆满警告，请查看是否开启了过多的耗时线程");
            return;
        }
        THREAD_POOL_EXECUTOR.schedule(runnable,delayed,TimeUnit.MILLISECONDS);
    }
}
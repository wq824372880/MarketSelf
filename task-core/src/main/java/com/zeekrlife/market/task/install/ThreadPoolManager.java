package com.zeekrlife.market.task.install;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager<T> {

    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程闲置时超时1s
     */
    private static final int KEEP_ALIVE = 1;
    /**
     * 线程池的对象
     */
    private volatile ThreadPoolExecutor executor;

    /**
     * 要确保该类只有一个实例对象，避免产生过多对象消费资源，所以采用单例模式
     */
    private ThreadPoolManager() {
    }

    private static class LazyHolder {
        private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }

    public static final ThreadPoolManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 开启一个无返回结果的线程
     *
     * @param r task
     */
    public void execute(Runnable r) {
        if (executor == null) {
            synchronized (ThreadPoolManager.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(1, 1,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>());
                }
            }
        }
        executor.execute(r);
    }

    /**
     * 开启一个有返回结果的线程
     *
     * @param r task
     * @return
     */
    public Future<T> submit(Callable<T> r) {
        if (executor == null) {
            synchronized (ThreadPoolManager.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(1, 1,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>());
                }
            }
        }
        return executor.submit(r);
    }

    /**
     * 把任务移除等待队列
     *
     * @param r task
     */
    public void cancel(Runnable r) {
        if (r != null) {
            if (executor == null) {
                synchronized (ThreadPoolManager.class) {
                    if (executor == null) {
                        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                                KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20),
                                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
                    }
                }
            }
            executor.getQueue().remove(r);
        }
    }
}

package com.zeekrlife.market.task;

import android.os.RemoteCallbackList;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zeekr.car.util.ThreadPoolUtil;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.market.task.data.mapper.TaskMapper;
import com.zeekrlife.market.task.data.source.TaskEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class TaskCallbackImpl {
    private RemoteCallbackList<ITaskCallback> taskCallbackList;
    private RemoteCallbackList<IArrangeCallback> arrangeCallbackList;

    private ExecutorService dispatchExecutor;

    private static volatile TaskCallbackImpl instance;

    public static TaskCallbackImpl getInstance() {
        if (instance == null) {
            synchronized (TaskCallbackImpl.class) {
                if (instance == null) {
                    instance = new TaskCallbackImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化函数
     * 该函数初始化任务回调列表和安排回调列表，并配置一个特定名称格式的线程工厂，用于创建一个单线程的执行器服务，
     * 该执行器服务使用链接阻塞队列作为任务缓存，并具有严格的容量控制。
     */
    public void init() {
        // 初始化任务回调列表和安排回调列表
        taskCallbackList = new RemoteCallbackList<>();
        arrangeCallbackList = new RemoteCallbackList<>();

        // 配置线程工厂，用于创建具有特定名称格式的线程
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("taskcallback-pool-%d").build();

        // 初始化任务分发执行器，使用单线程、固定大小的线程池，队列容量为1024
        dispatchExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }


    /**
     * 注册一个任务回调接口到任务回调列表中。
     * @param callback ITaskCallback 实例，代表要注册的回调对象。
     * @return 返回一个布尔值，表示回调是否成功注册。true 表示成功，false 表示失败。
     */
    public boolean registerTaskCallback(ITaskCallback callback) {
        // 将回调对象注册到任务回调列表中
        return taskCallbackList.register(callback);
    }


    /**
     * 注销指定的任务回调接口。
     * 从回调列表中移除给定的回调接口，如果成功移除则返回 true，否则返回 false。
     *
     * @param callback ITaskCallback 接口实例，是要被注销的回调。
     * @return boolean 返回值表示是否成功注销了回调。成功为 true，失败为 false。
     */
    public boolean unregisterTaskCallback(ITaskCallback callback) {
        return taskCallbackList.unregister(callback);
    }


    /**
     * 注册一个排列回调接口到回调列表中。
     *
     * @param callback IArrangeCallback类型的回调对象，表示一个排列回调接口实例。
     * @return 返回一个布尔值，表示注册操作是否成功。true表示成功注册，false表示注册失败。
     */
    public boolean registerArrangeCallback(IArrangeCallback callback) {
        // 将回调对象注册到安排回调列表中
        return arrangeCallbackList.register(callback);
    }


    /**
     * 注销给定的排列回调接口。
     * 从安排回调列表中移除指定的回调对象。如果回调对象成功被移除，返回true；否则返回false。
     *
     * @param callback 要注销的IArrangeCallback接口实例。
     * @return boolean 返回true如果成功从列表中移除回调，否则返回false。
     */
    public boolean unregisterArrangeCallback(IArrangeCallback callback) {
        return arrangeCallbackList.unregister(callback);
    }


    /**
     * 提交一个任务实体到指定的执行器。
     *
     * @param type 任务的类型，用于区分不同种类的任务。
     * @param taskEntity 待提交的任务实体，包含了任务的具体信息。
     * 该方法尝试克隆任务实体，然后将克隆后的任务提交给一个调度执行器。
     * 如果克隆失败，则记录错误信息并终止操作。
     * 不返回任何结果。
     */
    public void submit(Type type, TaskEntity taskEntity) {
        TaskEntity taskInfo;
        try {
            // 尝试克隆任务实体，以避免直接修改原任务实体的状态。
            taskInfo = (TaskEntity) taskEntity.clone();
        } catch (CloneNotSupportedException e) {
            // 克隆支持异常处理，记录堆栈跟踪。
            CommExtKt.logStackTrace(e);
            return;
        }
        // 提交克隆后的任务实体到调度执行器。
        dispatchExecutor.submit(new DispatchRunnable(type, taskInfo));
    }

    private class DispatchRunnable implements Runnable {

        private Type type;
        private TaskEntity taskInfo;

        DispatchRunnable(Type type, TaskEntity taskInfo) {
            this.type = type;
            this.taskInfo = taskInfo;
        }

        /**
         * 该方法根据不同的任务类型执行相应的操作。
         * 无参数和返回值，但要求 taskInfo 和 type 必须在调用前被正确设置。
         */
        @Override
        public void run() {
            // 如果任务信息为空，则直接返回，不执行任何操作
            if (taskInfo == null) {
                return;
            }
            // 根据任务类型执行相应的处理方法
            switch (type) {
                case onTaskAdded:
                    onTaskAdded(taskInfo);
                    break;
                case onTaskRemoved:
                    onTaskRemoved(taskInfo);
                    break;
                case onDownloadPending:
                    onDownloadPending(taskInfo);
                    break;
                case onDownloadStarted:
                    onDownloadStarted(taskInfo);
                    break;
                case onDownloadConnected:
                    onDownloadConnected(taskInfo);
                    break;
                case onDownloadProgress:
                    onDownloadProgress(taskInfo);
                    break;
                case onDownloadCompleted:
                    onDownloadCompleted(taskInfo);
                    break;
                case onDownloadPaused:
                    onDownloadPaused(taskInfo);
                    break;
                case onDownloadError:
                    onDownloadError(taskInfo);
                    break;
                case onInstallPending:
                    onInstallPending(taskInfo);
                    break;
                case onInstallStarted:
                    onInstallStarted(taskInfo);
                    break;
                case onInstallProgress:
                    onInstallProgress(taskInfo);
                    break;
                case onInstallCompleted:
                    onInstallCompleted(taskInfo);
                    break;
                case onInstallError:
                    onInstallError(taskInfo);
                    break;
                default:
                    // 如果类型不匹配，则不执行任何操作
                    break;
            }
        }


        /**
         * 当任务被添加时调用此方法，通知所有注册的回调任务已被添加。
         *
         * @param taskInfo 添加的任务实体信息。
         */
        private void onTaskAdded(TaskEntity taskInfo) {
            // 将任务实体转换为AIDL接口实现，以便于跨进程调用
            ITaskInfo task = TaskMapper.entityToAidl(taskInfo);
            try {
                // 开始广播任务添加的消息，获取当前注册的回调数量
                final int len = taskCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并调用每个注册的回调接口，通知任务被添加
                    ITaskCallback callback = taskCallbackList.getBroadcastItem(i);
                    try {
                        callback.onTaskAdded(task);
                    } catch (Exception e) {
                        // 捕获并记录回调中异常，避免影响其他回调执行
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 确保广播结束，清理相关资源
                try {
                    taskCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 处理广播未正常开始的异常情况
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当任务被移除时调用此方法，通知所有注册的回调任务已被移除。
         *
         * @param taskInfo 代表被移除任务的信息实体。
         */
        private void onTaskRemoved(TaskEntity taskInfo) {
            // 将任务实体转换为AIDL接口实现，以便在回调中使用
            ITaskInfo task = TaskMapper.entityToAidl(taskInfo);
            try {
                // 开始广播调用，获取当前注册的回调数量
                final int len = taskCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个注册的回调
                    ITaskCallback callback = taskCallbackList.getBroadcastItem(i);
                    try {
                        callback.onTaskRemoved(task);
                    } catch (Exception e) {
                        // 捕获并记录回调中的异常
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 确保广播调用被正确结束
                try {
                    taskCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能发生的异常
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当下载任务进入待下载状态时调用此方法，通知所有注册的回调对象。
         *
         * @param taskInfo 任务实体信息，包含任务的详细信息，如任务ID等。
         */
        private void onDownloadPending(TaskEntity taskInfo) {
            try {
                // 开始广播通知，获取当前注册的回调数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个回调对象的onDownloadPending方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onDownloadPending(taskInfo.getId());
                    } catch (Exception e) {
                        // 捕获并记录回调过程中的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 无论是否成功，最后都需要结束广播。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播过程中的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当下载开始时调用此方法，通知所有注册的回调对象。
         *
         * @param taskInfo 任务实体，包含下载任务的详细信息。
         */
        private void onDownloadStarted(TaskEntity taskInfo) {
            try {
                // 开始广播通知，获取当前注册的回调对象数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个回调对象的onDownloadStarted方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onDownloadStarted(taskInfo.getId());
                    } catch (Exception e) {
                        // 捕获并记录回调方法中的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 无论是否成功，最后都需要结束广播。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能出现的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当下载连接建立时调用此方法，通知所有监听者下载的连接状态。
         *
         * @param taskInfo 任务实体，包含下载任务的详细信息，如任务ID、已下载字节、总字节等。
         */
        private void onDownloadConnected(TaskEntity taskInfo) {
            try {
                // 开始广播回调，获取当前注册的回调数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个回调接口的onDownloadConnected方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onDownloadConnected(taskInfo.getId(), taskInfo.getSoFarBytes(), taskInfo.getTotalBytes());
                    } catch (Exception e) {
                        // 捕获异常并记录堆栈跟踪，避免影响其他回调执行。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有注册的回调都被调用。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 处理异常情况，记录堆栈跟踪。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当下载进度更新时，通知所有监听者。
         * @param taskInfo 任务实体，包含下载任务的当前信息，如任务ID、已下载字节和总字节。
         */
        private void onDownloadProgress(TaskEntity taskInfo) {
            try {
                // 开始广播通知，获取当前监听器数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个监听器的下载进度更新方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onDownloadProgress(taskInfo.getId(), taskInfo.getSoFarBytes(), taskInfo.getTotalBytes());
                    } catch (Exception e) {
                        // 捕获并记录调用监听器过程中可能出现的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有监听器都被正确处理。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播过程中可能出现的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当下载完成时调用此方法，通知所有注册的回调函数下载完成。
         *
         * @param taskInfo 任务实体，包含下载任务的详细信息。
         */
        private void onDownloadCompleted(TaskEntity taskInfo) {
            try {
                // 开始广播通知，获取当前注册的回调函数数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个回调函数的下载完成方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onDownloadCompleted(taskInfo.getId());
                    } catch (Exception e) {
                        // 捕获并记录调用回调函数时可能抛出的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有注册的回调函数都被调用。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能抛出的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当下载任务暂停时调用此方法，通知所有注册的回调对象。
         *
         * @param taskInfo 代表暂停的下载任务的信息。
         */
        private void onDownloadPaused(TaskEntity taskInfo) {
            try {
                // 开始广播通知，获取当前注册的回调数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个回调对象的onDownloadPaused方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onDownloadPaused(taskInfo.getId());
                    } catch (Exception e) {
                        // 捕获并记录调用回调时可能发生的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有注册的回调都被调用。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能发生的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当下载任务发生错误时调用此方法，将会通知所有注册的回调函数下载错误信息。
         *
         * @param taskInfo 任务实体，包含任务的错误信息和标识。
         */
        private void onDownloadError(TaskEntity taskInfo) {
            try {
                // 开始广播通知，获取当前注册的回调函数数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个回调函数的下载错误通知方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onDownloadError(taskInfo.getId(), taskInfo.getErrorCode());
                    } catch (Exception e) {
                        // 捕获并记录回调函数中的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有注册的回调函数都被正确处理。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能出现的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当安装任务处于待处理状态时调用此方法，通知所有注册的回调函数。
         *
         * @param taskInfo 任务信息实体，包含任务的相关信息，如任务ID。
         */
        private void onInstallPending(TaskEntity taskInfo) {
            try {
                // 开始广播回调，获取当前注册的回调数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个回调函数的onInstallPending方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onInstallPending(taskInfo.getId());
                    } catch (Exception e) {
                        // 捕获并记录回调函数中的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有注册的回调都被调用。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能出现的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当安装任务开始时调用此方法，通知所有注册的回调监听器。
         *
         * @param taskInfo 任务信息实体，包含任务的详细信息，如任务ID。
         */
        private void onInstallStarted(TaskEntity taskInfo) {
            try {
                // 开始广播通知，获取当前注册的回调监听器数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个监听器的onInstallStarted方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onInstallStarted(taskInfo.getId());
                    } catch (Exception e) {
                        // 捕获并记录调用回调时可能发生的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 无论是否发生异常，最后都尝试结束广播。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能发生的非法状态异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当安装进度更新时，通知所有监听者。
         * @param taskInfo 任务实体，包含任务ID和安装进度。
         */
        private void onInstallProgress(TaskEntity taskInfo) {
            try {
                // 开始广播安装进度更新，获取当前监听器数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并调用每个监听器的安装进度更新方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onInstallProgress(taskInfo.getId(), taskInfo.getInstallProgress());
                    } catch (Exception e) {
                        // 捕获并记录调用监听器方法时产生的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有监听器都被通知到。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能产生的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当安装完成时调用所有的回调函数。
         * @param taskInfo 任务实体，包含安装任务的相关信息。
         */
        private void onInstallCompleted(TaskEntity taskInfo) {
            try {
                // 开始广播回调，获取当前注册的回调数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并调用每个回调函数的安装完成方法。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onInstallCompleted(taskInfo.getId());
                    } catch (Exception e) {
                        // 捕获并记录回调函数中的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有注册的回调都被调用。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播时可能出现的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }


        /**
         * 当安装任务出错时调用此方法，通知所有监听者安装错误信息。
         *
         * @param taskInfo 任务实体，包含任务的错误信息。
         */
        private void onInstallError(TaskEntity taskInfo) {
            try {
                // 开始广播回调，获取当前监听者的数量。
                final int len = arrangeCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    // 获取并尝试调用每个监听者的安装错误回调。
                    IArrangeCallback callback = arrangeCallbackList.getBroadcastItem(i);
                    try {
                        callback.onInstallError(taskInfo.getId(), taskInfo.getErrorCode());
                    } catch (Exception e) {
                        // 捕获并记录回调过程中的异常。
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                // 尝试结束广播，确保所有监听者都被通知到。
                try {
                    arrangeCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    // 捕获并记录结束广播过程中的异常。
                    CommExtKt.logStackTrace(e);
                }
            }
        }

    }

    public enum Type {
        /**
         * on task added
         */
        onTaskAdded,
        /**
         * on task removed
         */
        onTaskRemoved,
        /**
         * on download pending
         */
        onDownloadPending,
        /**
         * on download started
         */
        onDownloadStarted,
        /**
         * on download connected
         */
        onDownloadConnected,
        /**
         * on download progress
         */
        onDownloadProgress,
        /**
         * on download completed
         */
        onDownloadCompleted,
        /**
         * on download paused
         */
        onDownloadPaused,
        /**
         * on download error
         */
        onDownloadError,
        /**
         * on install pending
         */
        onInstallPending,
        /**
         * on install started
         */
        onInstallStarted,
        /**
         * on install progress
         */
        onInstallProgress,
        /**
         * on install completed
         */
        onInstallCompleted,
        /**
         * on install error
         */
        onInstallError
    }
}

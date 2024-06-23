package com.zeekrlife.market.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.zeekr.basic.Common;
import com.zeekr.car.api.DeviceApiManager;
import com.zeekr.car.api.partnum.PartNumLoadListener;
import com.zeekrlife.common.util.SPUtils;
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil;
import com.zeekrlife.market.autoupdate.ThirdUpdateProvider;
import com.zeekrlife.market.manager.TaskRetryManager;
import com.zeekrlife.market.utils.NetUtils;

import java.util.Random;

/**
 * @author
 * @date description：启动自动更新服务
 */
@SuppressLint("LogNotTimber")
public class ThirdUpdateStartWorker extends Worker {

    private final static String TAG = "ThirdUpdateStartWorker";

    private final static String SIM_CARD_REAL_NAME_STATUS_KEY = "SIM_CARD_REAL_NAME_STATUS_KEY";

    private static volatile WorkRequest currentWorkRequest = null;

    /**
     * 启动工作线程。该方法用于根据网络可用性来决定如何执行任务。
     * 如果网络可用，则立即或在随机延迟后在子线程中执行任务。
     * 如果网络不可用，则将任务作为工作请求(enqueueWork)提交给WorkManager，待网络连接后再执行。
     *
     * @param context 上下文，用于访问应用的环境信息。
     */
    public static void startWorker(Context context) {
        try {
            //执行任务
//            Log.e(TAG, "startWorker hasNetworkAvailable...");
//            if (NetUtils.INSTANCE.hasNetworkAvailable(context)) {
//                Log.e(TAG, "hasNetworkAvailable true doWorkStart");
//                ThreadPoolUtil.runOnSubThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 在这里执行需要延迟的任务
//                        doWorkStart(context);
//                    }
//                },delay);
//            } else {
            int delay = new Random().nextInt(30000); // 0-30s的延迟执行
            ThreadPoolUtil.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (ThirdUpdateStartWorker.class) {
                        if (currentWorkRequest != null) {
                            Log.e(TAG, "startWorker currentWorkRequest is not null, cancelWork");
                            WorkManager.getInstance(context).cancelWorkById(currentWorkRequest.getId());
                        }
                        Log.e(TAG, "hasNetworkAvailable false enqueueWork");
                        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
                        currentWorkRequest = new OneTimeWorkRequest.Builder(ThirdUpdateStartWorker.class).setConstraints(constraints).build();
                        WorkManager.getInstance(context).enqueue(currentWorkRequest);
                    }
                }
            },delay);

//            }
        } catch (Exception e) {
            Log.e(TAG, "Error executing work: " + Log.getStackTraceString(e));
        }
    }

    /**
     * 构造函数：创建ThirdUpdateStartWorker实例。
     *
     * @param context 上下文对象，用于访问应用全局功能。
     * @param workerParams 工作参数，包含工作所需的配置和环境信息。
     */
    public ThirdUpdateStartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * 执行具体的工作任务。
     * 该方法会尝试开始执行工作，并在成功完成时返回成功的结果；如果遇到异常，则记录错误并尝试重试。
     *
     * @return Result 表示工作执行的结果，成功时返回带有成功标记的结果对象，失败时返回建议重试的结果对象。
     */
    @NonNull
    @Override
    public Result doWork() {
        try {
            // 工作开始前的初始化或配置
            doWorkStart(getApplicationContext());
            return Result.success();
        } catch (Exception e) {
            // 捕获执行过程中可能发生的任何异常，并记录错误信息
            Log.e(TAG, "Error executing work: " + Log.getStackTraceString(e));
            return Result.retry();
        } finally {
            // 无论工作成功或失败，最后都进行当前工作请求的清理
            synchronized (ThirdUpdateStartWorker.class) {
                currentWorkRequest = null;
            }
        }
    }

    private static final PartNumLoadListener PART_NUM_LOAD_LISTENER = new PartNumLoadListener() {
        /**
         * 当系统收到系统级推送通知时的回调处理。
         * <p>该方法重写了某个接口的onSystemPnReceive方法，用于在接收到系统推送通知后执行相应的操作。
         * 主要逻辑包括记录日志和启动工作流程。
         *
         * @param systemPn 系统推送的通知内容。该参数可为null，表示接收到的推送通知为空。
         */
        @Override
        public void onSystemPnReceive(@Nullable String systemPn) {
            // 记录接收到系统推送通知的日志
            Log.e(TAG, "--------systemPartNum reload success ----------");
            // 启动工作流程
//            workStart(Common.app);
        }
    };

    /**
     * 开始执行工作前的准备和校验。
     * @param context 应用的上下文环境，用于访问应用全局功能。
     */
    private static void doWorkStart(Context context) {
//        Log.e(TAG, "--------doWorkStart checkSystemPn----------");
//        // 校验系统零件号是否合法，若不合法则重新加载
//        if (DeviceApiManager.getInstance().systemPartNumVerifyIfReload(PART_NUM_LOAD_LISTENER)) {
//            Log.e(TAG, "--------doWorkStart systemPartNum reload----------");
//            return;
//        }
        workStart(context);
    }

    /**
     * 开始工作流程，首先尝试安排重试任务，然后检查SIM卡是否实名认证，如果认证了，则启动第三方更新服务。
     *
     * @param context 应用的上下文环境，用于启动服务等操作。
     */
    private static void workStart(Context context) {
        Log.e(TAG, "--------doWorkStart arrangeRetryTask----------");
        TaskRetryManager.arrangeRetryTask();
        Log.e(TAG, "--------executing work----------");
//        //该自动/强制更新功能只有当SIM卡实名后才能开启
//        boolean simCardIsAuth = SPUtils.getInstance().getBoolean(SIM_CARD_REAL_NAME_STATUS_KEY, false);
//        Log.e(TAG, "sim card real name status by cache:" + simCardIsAuth);
//
//        if (!simCardIsAuth) {
//            //获取sim卡是否实名
//            simCardIsAuth = DeviceApiManager.getInstance().simIsAuth();
//            if (simCardIsAuth) {
//                SPUtils.getInstance().put(SIM_CARD_REAL_NAME_STATUS_KEY, true);
//            }
//            Log.e(TAG, "sim card real name status by deviceApiManager:" + simCardIsAuth);
//        }
//
//        if (simCardIsAuth) {
//            Log.e(TAG, "do start thirdUpdateService");
            startThirdUpdateService(context);
//        }
    }

    /**
     * 启动第三方更新服务。
     * 该方法通过调用ThirdUpdateProvider的startService方法，以应用程序上下文和指定参数启动第三方更新服务。
     *
     * @param context 应用程序的上下文环境，用于访问全局应用程序对象。
     */
    private static void startThirdUpdateService(Context context) {
        // 使用应用程序上下文启动第三方更新服务
        ThirdUpdateProvider.startService(context.getApplicationContext(), 0);
    }
}

package com.zeekrlife.market.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.market.manager.AppPropertyManager;
import java.util.concurrent.TimeUnit;

/**
 * @author 刷新属性
 * Worker
 */
@SuppressLint("LogNotTimber")
public class RefreshPropertiesWorker extends Worker {

    private final static String TAG = "RefreshPropertiesWorker";

    /**
     * 启动一个工作线程来执行任务。
     * <p>
     * 该方法用于创建并安排一个一次性的工作请求（WorkRequest），该请求会执行{@link RefreshPropertiesWorker}类中定义的工作。
     * 工作的执行受限于网络连接，确保在网络连接时才执行。
     * </p>
     * @param context 应用的上下文环境，用于获取WorkManager的实例。
     */
    public static void startWorker(Context context) {
        try {
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshPropertiesWorker.class).setConstraints(constraints)
                .build();
            WorkManager.getInstance(context).enqueue(workRequest);
        } catch (Exception e) {
           CommExtKt.logStackTrace(e);
            Log.e(TAG, "Error executing work: " + Log.getStackTraceString(e));
        }
    }

    /**
     * 构造函数：创建一个RefreshPropertiesWorker实例。
     *
     * @param context 上下文对象，用于访问应用全局功能。
     * @param workerParams 工作参数，包含执行工作时所需的所有信息。
     */
    public RefreshPropertiesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * 执行具体的工作任务。
     * 该方法尝试进行云同步属性管理，并在成功或遇到异常时返回相应的结果。
     *
     * @return Result 返回工作执行的结果。如果执行成功，则返回一个成功结果；如果遇到异常，则返回一个重试结果。
     */
    @NonNull
    @Override
    public Result doWork() {
        try {
            // 开始执行工作，进行云同步属性管理
            Log.e(TAG, "--------executing work----------");
            AppPropertyManager.INSTANCE.cloudSyncProperties();
            return Result.success();
        } catch (Exception e) {
            // 工作执行过程中遇到异常
            Log.e(TAG, "Error executing work: " + Log.getStackTraceString(e));
            return Result.retry();
        }
    }
}
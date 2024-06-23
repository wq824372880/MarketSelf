package com.zeekrlife.market.autoupdate;

import android.content.Context;

import com.zeekrlife.common.util.SPUtils;
import com.zeekrlife.common.util.constant.SpConfig;

/**
 * @author Lei.Chen29
 */
public class ThirdUpdateProvider {

    /**
     * 启动服务的静态方法。
     *
     * @param context 上下文对象，用于启动服务。
     * @param taskDelayedTimeMillis 任务延迟执行的时间，单位为毫秒。此参数指定了服务启动的延迟时间。
     *                              如果立即启动服务，可以传入0或负值。
     */
    public static void startService(Context context, long taskDelayedTimeMillis) {
        // 调用ThirdUpdateService类的startSelf方法，以指定的延迟时间启动服务
        ThirdUpdateService.startSelf(context, taskDelayedTimeMillis);
    }

    /**
     * 启动服务的静态方法。
     *
     * @param context 上下文，用于启动服务。
     * @param taskDelayedTimeMillis 任务延迟执行的时间，单位为毫秒。如果立即执行任务，可以传入0。
     * @param actionType 任务的动作类型，用于标识服务执行的不同操作。
     */
    public static void startService(Context context, long taskDelayedTimeMillis, String actionType) {
        // 调用ThirdUpdateService中的startSelf方法启动服务，并传入参数
        ThirdUpdateService.startSelf(context, taskDelayedTimeMillis, actionType);
    }

    /**
     * 默认执行打开自动更新
     * 预置应用版本后 该值需要传回云端 由云端控制
     */
    public static boolean isOpenAutoUpdate() {
        boolean isAutoUpdate = SPUtils.getInstance().getBoolean(SpConfig.BooleanKey.AUTO_UPDATE, true);
        if (isAutoUpdate) {
            SPUtils.getInstance().put(SpConfig.BooleanKey.AUTO_UPDATE, true);
        }
        return isAutoUpdate;
    }
}

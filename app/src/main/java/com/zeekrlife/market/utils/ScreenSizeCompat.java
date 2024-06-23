package com.zeekrlife.market.utils;


import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;


import com.zeekrlife.market.R;
import com.zeekrlife.net.interception.logging.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 基于屏幕宽度的大屏缩放适配方案，限制缩放级别，避免出现界面内容过小或过大的问题。
 */
@SuppressWarnings("WeakerAccess")
public final class ScreenSizeCompat implements EmptyComponentCallbacks, EmptyActivityLifecycleCallbacks {

    private static final String TAG = "zzzScreenSizeCompat";

    /**
     * 固定屏幕宽度的 dp 值
     */
    public static final String META_DATA_FIXED_WIDTH_DP = "ScreenSizeCompat.fixedWidthDp";

    /**
     * 限制最大屏幕宽度 dp 值
     */
    public static final String META_DATA_MAX_WIDTH_DP = "ScreenSizeCompat.maxWidthDp";

    /**
     * 限制最小屏幕宽度 dp 值
     */
    public static final String META_DATA_MIN_WIDTH_DP = "ScreenSizeCompat.minWidthDp";


    private static ScreenSizeCompat sInstance = null;
    private static Attributes sAttributes = Attributes.INSTANCE;

    /**
     * 获取ScreenSizeCompat的实例。
     * 该方法是线程安全的，如果实例不存在，则通过调用init方法初始化。
     *
     * @param application 应用程序实例，用于初始化ScreenSizeCompat。
     * @return 返回ScreenSizeCompat的单例对象。
     */
    @NotNull
    public static ScreenSizeCompat getInstance(@NotNull final Application application) {
        if (sInstance == null) {
            init(application);
        }
        return sInstance;
    }

    /**
     * 初始化，自动完成屏幕大小适配
     *
     * @param application 应用程序上下文
     */
    public static void init(@NotNull final Application application) {
        if (sInstance != null) {
            sInstance.destroy();
        }
        sAttributes.init(application);
        sInstance = new ScreenSizeCompat(application);
    }

    private Application mApplication;


    /**
     * 构造函数：创建一个与指定应用相关联的ScreenSizeCompat实例。
     * 该构造函数初始化了ScreenSizeCompat实例，设置应用为必传参数，并确保其不为空。
     * 它还更新了目标显示度量标准，并在应用中注册了组件回调和活动生命周期回调。
     *
     * @param application 应用实例，不可为null，用于获取应用的资源和注册回调。
     */
    private ScreenSizeCompat(@NotNull final Application application) {
        mApplication = Objects.requireNonNull(application);
        updateTargetDisplayMetrics(
                mApplication.getResources().getConfiguration(),
                mApplication.getResources().getDisplayMetrics()
        );
        mApplication.registerComponentCallbacks(this);
        mApplication.registerActivityLifecycleCallbacks(this);

    }

    /**
     * 销毁组件和活动生命周期回调
     * 该方法取消了当前对象作为应用组件回调和活动生命周期回调的注册。它在不需要进一步监听应用状态或活动生命周期事件时被调用。
     * 无参数
     * 无返回值
     */
    private void destroy() {
        mApplication.unregisterComponentCallbacks(this);
        mApplication.unregisterActivityLifecycleCallbacks(this);
    }


    /**
     * 当配置发生改变时的回调处理。
     * <p>此方法会尝试更新应用的显示指标（DisplayMetrics），以适应新的配置。</p>
     *
     * @param newConfig 新的配置信息。如果为null，则表示没有新的配置信息，方法会直接返回。
     */
    @Override
    public void onConfigurationChanged(@Nullable Configuration newConfig) {
        if (newConfig == null) {
            return;
        }
        final boolean success = updateTargetDisplayMetrics(newConfig, mApplication.getResources().getDisplayMetrics());
        if (success) {
            LogUtils.e(TAG, "Change DisplayMetrics for application.");
        }
    }

    /**
     * 当活动创建后被调用，用于更新活动的显示度量单位。
     *
     * @param activity 指当前创建的活动。
     * @param savedInstanceState 如果活动之前被销毁，这参数包含之前的状态。如果活动没被销毁之前，这参数是null。
     */
    @Override
    public void onActivityCreated(@NotNull Activity activity, @Nullable Bundle savedInstanceState) {
        final Resources resources = activity.getResources();
        final boolean success = updateTargetDisplayMetrics(resources.getConfiguration(), resources.getDisplayMetrics());
        if (success) {
            LogUtils.e(TAG, "Change DisplayMetrics for activity: " + activity);
        }
    }

    /**
     * @param configuration  app 或 activity 当前配置
     * @param displayMetrics 针对这个做改动
     * @return 已发生改动，则返回 true
     */
    private boolean updateTargetDisplayMetrics(@NotNull Configuration configuration, @NotNull DisplayMetrics displayMetrics) {
        final float targetDensity = sAttributes.getTargetDensity(configuration, displayMetrics);
        if (targetDensity <= 0) {
            return false;
        }

        final int targetDensityDpi = Math.round(targetDensity * DisplayMetrics.DENSITY_DEFAULT);
        final float scaleMultiple = targetDensity / displayMetrics.density;
        final float targetXdpi = displayMetrics.xdpi * scaleMultiple;
        final float targetYdpi = displayMetrics.ydpi * scaleMultiple;
        final float targetScaleDensity = displayMetrics.scaledDensity * scaleMultiple;

        final String originalDisplayMetrics = displayMetrics + ", densityDpi=" + displayMetrics.densityDpi;

        displayMetrics.density = targetDensity;
        displayMetrics.densityDpi = targetDensityDpi;
        displayMetrics.xdpi = targetXdpi;
        displayMetrics.ydpi = targetYdpi;
        displayMetrics.scaledDensity = targetScaleDensity;

        final String changedDisplayMetrics = displayMetrics + ", densityDpi=" + displayMetrics.densityDpi;

        LogUtils.e(TAG, "apply display metrics" +
                "\noriginal -> " + originalDisplayMetrics +
                "\nchanged  -> " + changedDisplayMetrics);
        return true;
    }


    static class Attributes {

        static final Attributes INSTANCE = new Attributes();

        int fixedWidthDp = 0;
        int maxWidthDp = 0;
        int minWidthDp = 0;

        private Attributes() {
            // private
        }

        /**
         * 初始化参数
         */
        void init(@NotNull Application application) {
            fixedWidthDp = application.getResources().getInteger(R.integer.config_car_window_display_weight);
            LogUtils.e(TAG, "fixedWidthDp -> " + fixedWidthDp);
        }

        /**
         * 快速判断参数是否有效
         */
        boolean isValid() {
            return fixedWidthDp > 0 || maxWidthDp > 0 || minWidthDp > 0;
        }

        /**
         * 获取屏幕调整后的目标宽度
         *
         * @param configuration  app 或 activity 当前配置
         * @param displayMetrics 在这个显示器参数上做调整
         * @return 如果返回的值不大于 0，则应该忽略它
         */
        float getTargetDensity(@NotNull Configuration configuration, @NotNull DisplayMetrics displayMetrics) {
            if (!isValid()) {
                return 0;
            }
            final int originalWidthDp;
            originalWidthDp = Math.round(displayMetrics.widthPixels / displayMetrics.density);

            final int targetWidthDp;
            if (fixedWidthDp > 0) {
                targetWidthDp = fixedWidthDp;
            } else if (maxWidthDp > 0 && maxWidthDp < originalWidthDp) {
                targetWidthDp = maxWidthDp;
            } else if (minWidthDp > 0 && minWidthDp > originalWidthDp) {
                targetWidthDp = minWidthDp;
            } else {
                targetWidthDp = 0;
            }
            if (targetWidthDp <= 0 || originalWidthDp == targetWidthDp) {
                return 0;
            }
            LogUtils.e(TAG, "width dp [" + originalWidthDp + " to " + targetWidthDp + "]");

            return displayMetrics.widthPixels * 1F / targetWidthDp;
        }
    }
}

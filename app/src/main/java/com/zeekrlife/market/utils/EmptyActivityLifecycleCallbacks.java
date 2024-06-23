package com.zeekrlife.market.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 空实现 {@link Application.ActivityLifecycleCallbacks}
 * <p>
 *
 * @see Application.ActivityLifecycleCallbacks
 */
public interface EmptyActivityLifecycleCallbacks extends Application.ActivityLifecycleCallbacks {

    @Override
    /**
     * 当Activity创建后被调用的回调方法。
     *
     * @param activity 指当前创建的Activity对象，不可为null。
     * @param savedInstanceState 如果Activity被系统重新创建，这个参数包含了之前Activity结束时的状态。如果Activity是第一次创建，这个参数是null。
     */
    default void onActivityCreated(@NotNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    /**
     * 当活动（Activity）开始时的回调方法。
     *
     * @param activity 非空的Activity对象，表示当前开始的活动。
     */
    @Override
    default void onActivityStarted(@NotNull Activity activity) {

    }

    @Override
    /**
     * 当活动（Activity）恢复时的回调方法。
     * 这个方法是一个默认方法，旨在被继承类中重写，以提供特定的逻辑处理。
     *
     * @param activity 恢复的活动对象，不可为null。
     */
    default void onActivityResumed(@NotNull Activity activity) {

    }

    /**
     * 当关联的Activity被暂停时调用此方法。
     *
     * @param activity 暂停的Activity实例，不可为null。
     */
    @Override
    default void onActivityPaused(@NotNull Activity activity) {

    }

    /**
     * 当活动（Activity）停止时被调用的默认方法。
     *
     * @param activity 非空的Activity对象，表示当前被停止的活动。
     */
    @Override
    default void onActivityStopped(@NotNull Activity activity) {

    }

    /**
     * 当Activity即将保存状态时调用的回调方法。
     * <p>此方法是在Activity即将停止（但还未销毁）时调用，目的是允许你保存Activity的当前状态。
     * 保存的状态信息将会在Activity重新创建时（例如设备旋转等配置更改后）使用。</p>
     *
     * @param activity 当前正在保存状态的Activity实例。
     * @param outState 一个可变的Bundle对象，用于存储你想要保存的Activity状态。如果为null，则表示不需要保存状态。
     */
    @Override
    default void onActivitySaveInstanceState(@NotNull Activity activity, @Nullable Bundle outState) {

    }

    /**
     * 当活动（Activity）被销毁时调用的默认方法。
     *
     * @param activity 被销毁的活动对象，此参数不可为null。
     */
    @Override
    default void onActivityDestroyed(@NotNull Activity activity) {

    }
}

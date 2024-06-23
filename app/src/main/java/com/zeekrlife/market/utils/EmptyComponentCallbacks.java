package com.zeekrlife.market.utils;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

import org.jetbrains.annotations.Nullable;

/**
 * 空实现 {@link ComponentCallbacks2}
 * <p>
 */
public interface EmptyComponentCallbacks extends ComponentCallbacks2 {

    /**
     * 当配置发生改变时被调用的默认方法。
     *
     * @param newConfig 新的配置对象。如果配置未改变，可以为null。
     */
    @Override
    default void onConfigurationChanged(@Nullable Configuration newConfig) {

    }

    /**
     * 当系统内存不足时被调用的默认方法。
     * 该方法没有参数，也没有返回值。
     * 子类可以通过重写此方法来应对低内存情况，进行必要的资源释放等操作。
     */
    @Override
    default void onLowMemory() {

    }

    /**
     * 当系统进行内存优化时，回调此方法。根据传入的内存级别，调用方可以采取不同的内存优化措施。
     *
     * @param level 内存优化的级别。该参数指示当前系统内存的状态，以及需要进行的优化程度。
     *              级别越高，表示系统内存越紧张，需要进行更激烈的优化。
     */
    @Override
    default void onTrimMemory(int level) {

    }
}

package com.zeekr.car.api.media;

import android.content.Context;
import com.zeekr.sdk.base.ApiReadyCallback;

/**
 * @author Lei.Chen29
 * @date 2023/4/24 15:52
 * description：
 */
public interface MediaCenter {

    /**
     * 初始化
     */
    void init(Context context, ApiReadyCallback callback);

    /**
     * 当前播放中的应用包名
     */
    String getPlayingPackageName();
}

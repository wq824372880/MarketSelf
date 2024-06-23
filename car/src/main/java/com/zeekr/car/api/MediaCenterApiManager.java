package com.zeekr.car.api;

import android.content.Context;
import com.zeekr.car.api.media.MediaCenter;
import com.zeekr.car.api.media.MediaCenterApiWrapper;
import com.zeekr.sdk.base.ApiReadyCallback;

/**
 * @author Lei.Chen29
 * @date 2023/4/20 19:39
 * description：媒体中心
 */
public class MediaCenterApiManager {

    private static final String TAG = "MediaCenterApiManager";

    private MediaCenter mMediaCenter = null;

    private MediaCenterApiManager() {
    }

    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, ApiReadyCallback callback) {
        if (mMediaCenter == null) {
            mMediaCenter = new MediaCenterApiWrapper();
        }
        mMediaCenter.init(context, callback);
    }

    public static boolean checkAppIsPlaying(String packageName) {
        MediaCenter mediaCenter = getInstance().mMediaCenter;
        String mPlayingPackageName = null;
        if (mediaCenter != null) {
            mPlayingPackageName = mediaCenter.getPlayingPackageName();
        }
        return packageName != null && packageName.equals(mPlayingPackageName);
    }

    /**
     * 可设置自定义MediaCenter
     */
    public void setMediaCenter(MediaCenter mediaCenter) {
        mMediaCenter = mediaCenter;
    }

    public static MediaCenterApiManager getInstance() {
        return MediaCenterApiHolder.INSTANCE;
    }

    static class MediaCenterApiHolder {
        private static final MediaCenterApiManager INSTANCE = new MediaCenterApiManager();
    }
}

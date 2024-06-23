package com.zeekr.car.api.media;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.zeekr.car.util.CarLogUtils;
import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.mediacenter.IMusicPlaybackInfo;
import com.zeekr.sdk.mediacenter.IRecommend;
import com.zeekr.sdk.mediacenter.bean.IContent;
import com.zeekr.sdk.mediacenter.bean.IMedia;
import com.zeekr.sdk.mediacenter.impl.MediaCenterAPI;

import ecarx.eas.xsf.mediacenter.IMediaListsEx;
import ecarx.xsf.widget.IReceiveWidgetInfoCallback;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lei.Chen29
 * @date 2023/4/24 16:28
 * description:
 */
public class MediaCenterApiWrapper implements MediaCenter {

    private static final String TAG = "MediaCenterApiWrapper";

    //当前播放中的应用
    private String mCurrPlayingPkgName = "";

    private final AtomicBoolean isInitSuccess = new AtomicBoolean(false);

    @Override
    public void init(Context context, ApiReadyCallback callback) {
        Log.e(TAG, "----------init Start-----------");
        try {
            if (isInitSuccess.get()) {
                Log.e(TAG, "init successfully initialized");
                callback.onAPIReady(true, "");
            } else {
                MediaCenterAPI.get().init(context, (result, reason) -> {
                    Log.e(TAG, "init result : " + result + " ; reason : " + reason);

                    if (result) {
                        isInitSuccess.getAndSet(true);
                        MediaCenterAPI.get().getWidgetApi().initCallBack(receiveWidgetInfoCallback);
                    }

                    if (callback != null) {
                        callback.onAPIReady(result, reason);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkInit(Context context, ApiReadyCallback callback) {
        init(context, callback);
    }

    private final IReceiveWidgetInfoCallback receiveWidgetInfoCallback = new IReceiveWidgetInfoCallback() {
        @Override
        public void updateMusicPlayInfo(IMusicPlaybackInfo iMusicPlaybackInfo) {
            try {
                if(iMusicPlaybackInfo == null){
                    return;
                }
                Log.e(TAG, "iMusicPlaybackInfo  title " + iMusicPlaybackInfo.getTitle());
                Log.e(TAG, "iMusicPlaybackInfo  state " + iMusicPlaybackInfo.getPlaybackStatus());
                setCurrentPlayingPackageName(iMusicPlaybackInfo);
            } catch (Exception e) {
                CarLogUtils.logStackTrace(e);
            }
        }

        @Override
        public void updateProgress(long l) {

        }

        @Override
        public void updateMediaList(int i, int i1, List<IMedia> list) {

        }

        @Override
        public void updateRecommendInfo(IRecommend iRecommend) {

        }

        @Override
        public void updateMultiMediaListEx(IMediaListsEx iMediaListsEx) {

        }

        @Override
        public void updateMediaContent(List<IContent> list) {

        }

        @Override
        public void updateCollectMsg(int i, String s) {

        }
    };

    private void setCurrentPlayingPackageName(IMusicPlaybackInfo musicPlaybackInfo) {
        try {
            if (musicPlaybackInfo != null && musicPlaybackInfo.getPlaybackStatus() == 1) {
                mCurrPlayingPkgName = musicPlaybackInfo.getPackageName();
            } else {
                mCurrPlayingPkgName = "";
            }
        } catch (RemoteException e) {
           CarLogUtils.logStackTrace(e);
        }
    }

    @Override
    public String getPlayingPackageName() {
        return mCurrPlayingPkgName;
    }
}

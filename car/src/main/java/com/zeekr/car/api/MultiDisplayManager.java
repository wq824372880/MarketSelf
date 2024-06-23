package com.zeekr.car.api;

import android.content.Context;
import android.util.Log;

import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.car.callback.MultiScreenListener;
import com.zeekr.sdk.car.impl.CarProxy;
import com.zeekr.sdk.multidisplay.impl.MultidisplayAPI;
import com.zeekr.sdk.multidisplay.setting.bean.MultiDisplayActivityInfo;
import com.zeekr.sdk.multidisplay.setting.bean.ScreenActivityInfo;

import java.util.List;

/**
 * @author Lei.Chen29
 * @date 2023/4/19 19:12
 * description：
 */
public class MultiDisplayManager {

    private static final String TAG = "zzzMultiDisplayManager";

    private MultidisplayAPI multidisplayAPI = null;

    private MultiDisplayManager() {
    }

    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, ApiReadyCallback callback) {
        if (multidisplayAPI == null) {
            multidisplayAPI = getCarAPI();
        }
        multidisplayAPI.init(context, new ApiReadyCallback() {
            @Override
            public void onAPIReady(boolean b, String s) {
                Log.e(TAG,"multidisplayAPI b:" + b);
            }
        });

    }
    public MultidisplayAPI getCarAPI() {
        if (multidisplayAPI == null) {
            multidisplayAPI = MultidisplayAPI.get();
        }
        return multidisplayAPI;
    }

    /**
     * 通过包名查询某个包名应用可以展示的屏幕信息及Activity信息
     *
     */
    public List<MultiDisplayActivityInfo> activityInfoByPackageName(String packageName) {
        return multidisplayAPI.getSettingAPI().getMultiDisplayActivityInfoByPackageName (packageName);
    }

    /**
     * 通过屏幕类型获取应用信息列表
     *
     */
    public List<MultiDisplayActivityInfo> activityInfoByScreenName(String screenName) {
        return multidisplayAPI.getSettingAPI().getMultiDisplayActivityInfoByScreenName(screenName);
    }

    /**
     * 通过包名和屏幕类型获取要启动的应用信息
     *
     */
    public ScreenActivityInfo activityInfoByAppNameScreenName(String screenName, String packageName) {
        return multidisplayAPI.getSettingAPI().getMultiDisplayActivityInfoByAppNameScreenName(screenName,packageName);
    }

    /**
     * 应用市场同步云端配置到CommonApi的服务
     *
     */
    public void syncMultiDisplayActivityInfo(List<MultiDisplayActivityInfo> infos) {
        multidisplayAPI.getSettingAPI().syncMultiDisplayActivityInfo(infos);
    }



    public void setMultiDisplayAPI(MultidisplayAPI multidisplayAPI) {
        this.multidisplayAPI = multidisplayAPI;
    }

    public static MultiDisplayManager getInstance() {
        return MultiDisplayManager.MultiDisplayHolder.INSTANCE;
    }

    static class MultiDisplayHolder {
        private static final MultiDisplayManager INSTANCE = new MultiDisplayManager();
    }


}

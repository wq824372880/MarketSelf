package com.zeekr.car.api;

import com.zeekr.sdk.navi.impl.NaviAPI;

/**
 * @author Lei.Chen29
 * @date 2023/4/19 19:12
 * description：
 */
public class NaviApiManager {

    private static final String TAG = "NaviApiManager";

    private NaviAPI naviAPI = null;

    private NaviApiManager() {
    }

    public NaviAPI getNaviAPI() {
        if (naviAPI == null) {
            naviAPI = NaviAPI.get();
        }
        return naviAPI;
    }

    public void setNaviAPI(NaviAPI naviAPI) {
        this.naviAPI = naviAPI;
    }

    public static NaviApiManager getInstance() {
        return NaviApiManager.NaviApiHolder.INSTANCE;
    }

    static class NaviApiHolder {
        private static final NaviApiManager INSTANCE = new NaviApiManager();
    }
}

package com.zeekr.car.api;

import com.zeekr.sdk.user.impl.UserAPI;

/**
 * @author Lei.Chen29
 * @date 2023/4/19 19:12
 * description：
 */
public class UserApiManager {

    private static final String TAG = "UserApiManager";

    private UserAPI userAPI = null;

    private UserApiManager() {
    }

    public UserAPI getUserAPI() {
        if (userAPI == null) {
            userAPI = UserAPI.get();
        }
        return userAPI;
    }

    public void setUserAPI(UserAPI userAPI) {
        this.userAPI = userAPI;
    }

    public static UserApiManager getInstance() {
        return UserApiManager.UserApiHolder.INSTANCE;
    }

    static class UserApiHolder {
        private static final UserApiManager INSTANCE = new UserApiManager();
    }
}

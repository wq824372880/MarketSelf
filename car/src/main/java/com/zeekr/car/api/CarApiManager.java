package com.zeekr.car.api;

import android.content.Context;
import android.util.Log;

import com.zeekr.car.adaptapi.CarApiProxy;
import com.zeekr.car.api.media.MediaCenterApiWrapper;
import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.car.callback.MultiScreenListener;
import com.zeekr.sdk.car.impl.CarAPI;
import com.zeekr.sdk.car.impl.CarProxy;
import com.zeekr.sdk.user.impl.UserAPI;

/**
 * @author Lei.Chen29
 * @date 2023/4/19 19:12
 * description：
 */
public class CarApiManager {

    private static final String TAG = "zzzCarApiManager";

    private CarProxy carAPI = null;

    private CarApiManager() {
    }

    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, ApiReadyCallback callback) {
        if (carAPI == null) {
            carAPI = getCarAPI();
        }
        carAPI.init(context, new ApiReadyCallback() {
            @Override
            public void onAPIReady(boolean b, String s) {
                Log.e(TAG,"CarProxy b:" + b);
            }
        });
    }
    public CarProxy getCarAPI() {
        if (carAPI == null) {
            carAPI = CarProxy.get();
        }
        return carAPI;
    }

    public void getRegisterMultiScreenListener(int screenLocation, MultiScreenListener multiScreenListener){
        carAPI.getConfigApi().getRegisterMultiScreenListener(screenLocation,multiScreenListener);
    }



    public void setCarAPI(CarProxy carAPI) {
        this.carAPI = carAPI;
    }

    public static CarApiManager getInstance() {
        return CarApiManager.CarApiHolder.INSTANCE;
    }

    static class CarApiHolder {
        private static final CarApiManager INSTANCE = new CarApiManager();
    }


}

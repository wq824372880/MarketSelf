package com.zeekr.car.api.vehicle;

import android.content.Context;
import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.vehicle.callback.GearListener;

/**
 * @author Lei.Chen29
 * @date 2023/4/24 13:46
 * description：
 */
public interface Vehicle {

    /**
     * 初始化
     */
    void init(Context context, ApiReadyCallback callback);

    /**
     * 是否支持挡位获取、监听
     */
    boolean isGearSupport();

    /**
     * 获取车辆挡位
     */
    int getVehicleGear();

    /**
     * 是否是行车中（是否非P/N档）
     */
    boolean isOnTheRoad();

    /**
     * 挡位监听
     */
    void setGearListener(GearListener listener);
}

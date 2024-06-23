package com.zeekr.car.api.vehicle;

import android.content.Context;
import android.util.Log;

import com.zeekr.car.util.CarLogUtils;
import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.vehicle.bean.FunctionStatus;
import com.zeekr.sdk.vehicle.bean.IGearEvent;
import com.zeekr.sdk.vehicle.callback.GearListener;
import com.zeekr.sdk.vehicle.impl.VehicleAPI;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lei.Chen29
 * @date 2023/4/24 10:43
 * description：
 */
public class VehicleApiWrapper implements Vehicle {

    private static final String TAG = "VehicleApiWrapper";

    private volatile int gear = IGearEvent.GEAR_UNKNOWN;

    private volatile boolean isGearSupport;

    private volatile GearListener gearListener = null;

    private final AtomicBoolean isInitSuccess = new AtomicBoolean(false);

    @Override
    public void init(Context context, ApiReadyCallback callback) {
        Log.e(TAG, "----------init Start-----------");
        try {
            if (isInitSuccess.get()) {
                Log.e(TAG, "init successfully initialized");
                callback.onAPIReady(true, "");
            } else {
                VehicleAPI.get().init(context, (result, reason) -> {
                    Log.e(TAG, "init result : " + result + " ; reason : " + reason);

                    if (result) {
                        isInitSuccess.getAndSet(true);
                        FunctionStatus status = VehicleAPI.get().isGearSupported();
                        Log.e(TAG, "registerGearChangeWatcher status: " + status.toString());
                        isGearSupport = FunctionStatus.active.equals(status);
                        if (gearListener != null) {
                            gearListener.onSensorSupportChanged(status);
                        }

                        if (status.equals(FunctionStatus.active)) {
                            int lGear = VehicleAPI.get().getGear();
                            Log.e(TAG, "status FunctionStatus.active Gear : " + lGear);
                            gear = lGear;
                            if (gearListener != null) {
                                gearListener.onSensorEventChanged(lGear);
                            }
                            VehicleAPI.get().registerGearListener(new GearListener() {
                                @Override
                                public void onSensorSupportChanged(FunctionStatus functionStatus) {
                                    if (functionStatus != null) {
                                        Log.e(TAG, "status gearSupport change(onSensorSupportChanged): " + functionStatus.name());
                                    }
                                    isGearSupport = FunctionStatus.active.equals(functionStatus);
                                    if (gearListener != null) {
                                        gearListener.onSensorSupportChanged(functionStatus);
                                    }
                                }

                                @Override
                                public void onSensorEventChanged(int i) {
                                    Log.e(TAG, "status gear change(onSensorEventChanged): " + i);
                                    gear = i;
                                    if (gearListener != null) {
                                        gearListener.onSensorEventChanged(i);
                                    }
                                }
                            });
                        } else {
                            Log.e(TAG, "registerGearChangeWatcher gear is not supported");
                        }
                    } else {
                        Log.e(TAG, "registerGearChangeWatcher api init failed");
                    }

                    if (callback != null) {
                        callback.onAPIReady(result, reason);
                    }
                });
            }
        } catch (Exception e) {
           CarLogUtils.logStackTrace(e);
        }
    }

    public boolean isGearSupport() {
        return isGearSupport;
    }

    @Override
    public int getVehicleGear() {
        return gear;
    }

    /**
     * 非P/N档位时
     */
    @Override
    public boolean isOnTheRoad() {
        try {
            int mGear = VehicleAPI.get().getGear();
            Log.e(TAG, "isOnTheRoad called gear: " + mGear + "; isGearSupport: " + isGearSupport);
            if (gear != mGear) {
                gear = mGear;
                //可能存在挡位回调监听未触发，查询为最新状态，某些观察者状态不同步问题
                Log.e(TAG, "isOnTheRoad gear change notify!!!");
                if (gearListener != null) {
                    gearListener.onSensorEventChanged(gear);
                }
            }
            if (gear == IGearEvent.GEAR_UNKNOWN) {
                return false;
            }
        } catch (Exception e) {
           CarLogUtils.logStackTrace(e);
        }
        return isGearSupport && gear != IGearEvent.GEAR_PARK;
    }

    @Override
    public void setGearListener(GearListener listener) {
        gearListener = listener;
    }
}

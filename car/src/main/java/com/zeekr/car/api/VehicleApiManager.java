package com.zeekr.car.api;

import android.content.Context;
import android.util.Log;
import com.zeekr.car.api.vehicle.IVehicleListener;
import com.zeekr.car.api.vehicle.Vehicle;
import com.zeekr.car.api.vehicle.VehicleApiWrapper;
import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.vehicle.bean.FunctionStatus;
import com.zeekr.sdk.vehicle.callback.GearListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lei.Chen29
 * @date 2023/4/21 14:07
 * description：
 */
public class VehicleApiManager {

    private static final String TAG = "VehicleApiManager";

    private Vehicle vehicle = null;

    private final List<IVehicleListener> mVehicleListeners = new ArrayList<>();

    private VehicleApiManager() {
    }

    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, ApiReadyCallback callback) {
        if (vehicle == null) {
            vehicle = new VehicleApiWrapper();
        }
        //设置挡位监听
        vehicle.setGearListener(gearListener);
        //初始化
        vehicle.init(context, (result, reason) -> {
            Log.e(TAG, "init: result -> " + result + "; reason -> " + reason);
            if (callback != null) {
                callback.onAPIReady(result, reason);
            }
        });
    }

    private final GearListener gearListener = new GearListener() {
        @Override
        public void onSensorSupportChanged(FunctionStatus functionStatus) {
            if (vehicle == null || mVehicleListeners.isEmpty()) {
                return;
            }
            for (IVehicleListener listener : mVehicleListeners) {
                listener.onGearSupportChanged(vehicle.isGearSupport());
            }
        }

        @Override
        public void onSensorEventChanged(int i) {
            if (vehicle == null || mVehicleListeners.isEmpty()) {
                return;
            }
            for (IVehicleListener listener : mVehicleListeners) {
                listener.onGearChanged(vehicle.getVehicleGear());
            }
        }
    };

    public boolean isOnTheRoad() {
        if (vehicle == null) {
            return false;
        }
        return vehicle.isOnTheRoad();
    }

    public synchronized void addIVehicleListener(IVehicleListener iVehicleListener) {
        if (!mVehicleListeners.contains(iVehicleListener)) {
            mVehicleListeners.add(iVehicleListener);
        }
    }

    public synchronized void removeIVehicleListener(IVehicleListener iVehicleListener) {
        mVehicleListeners.remove(iVehicleListener);
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public static VehicleApiManager getInstance() {
        return VehicleApiManager.VehicleApiHolder.INSTANCE;
    }

    static class VehicleApiHolder {
        private static final VehicleApiManager INSTANCE = new VehicleApiManager();
    }
}

package com.zeekr.car.api;

import com.zeekr.car.adaptapi.device.DeviceApiImpl;
import com.zeekr.car.api.accessmemory.AccessMemory;
import com.zeekr.car.api.accessmemory.DefAccessMemoryImpl;
import com.zeekr.car.api.device.SimCard;
import com.zeekr.car.api.device.SimCardDefImpl;
import com.zeekr.car.api.partnum.DefPartNumVersionImpl;
import com.zeekr.car.api.partnum.PartNumLoadListener;
import com.zeekr.car.api.partnum.PartNumVersion;
import com.zeekr.sdk.device.impl.DeviceAPI;

/**
 * @author Lei.Chen29
 * @date 2023/4/20 11:11
 * description：
 */
public class DeviceApiManager implements SimCard {

    private static final String TAG = "DeviceApiManager";

    private DeviceAPI deviceAPI = null;

    private SimCard simCard = new SimCardDefImpl();

    /**
     * 零件号默认实现
     */
    private PartNumVersion partNumVersion = new DefPartNumVersionImpl();

    private AccessMemory accessMemory = new DefAccessMemoryImpl();

    private DeviceApiManager() {
    }

    public DeviceAPI getDeviceAPI() {
        if (deviceAPI == null) {
            deviceAPI = DeviceApiImpl.getInstance();
        }
        return deviceAPI;
    }

    public void setDeviceAPI(DeviceAPI deviceAPI) {
        this.deviceAPI = deviceAPI;
    }

    public void setSimCard(SimCard simCard) {
        this.simCard = simCard;
    }

    public String getVehicleModel() {
        DeviceAPI api = getDeviceAPI();
        if (api instanceof DeviceApiImpl) {
            return ((DeviceApiImpl) getDeviceAPI()).getVehicleModel();
        } else {
            if (api == null) {
                return "";
            }
        }
        return api.getSupplierCode() + "_" + api.getProjectCode() + "_" + api.getVehicleType();
    }

    /**
     * 获取零件号
     */
    public String getDeviceSystemPn() {
        if (partNumVersion != null) {
            return partNumVersion.systemPartNumVersion();
        }
        return "";
    }

    public String getDeviceAccessMemory(){
        if(accessMemory != null){
            return accessMemory.AccessMemorySize();
        }
        return "32";
    }



    public boolean systemPartNumVerifyIfReload(PartNumLoadListener listener) {
        if (partNumVersion != null) {
            return partNumVersion.systemPartNumVerifyIfReload(listener);
        }
        return false;
    }

    public void setPartNumVersion(PartNumVersion partNumVersion) {
        this.partNumVersion = partNumVersion;
    }

    public static DeviceApiManager getInstance() {
        return DeviceApiManager.DeviceApiHolder.INSTANCE;
    }

    @Override
    public boolean simIsAuth() {
        if (simCard != null) {
            return simCard.simIsAuth();
        }
        return false;
    }

    static class DeviceApiHolder {
        private static final DeviceApiManager INSTANCE = new DeviceApiManager();
    }
}

package com.zeekr.car.api.vehicle;

/**
 * @author
 */
public interface IVehicleListener {

    /**
     * onGearSupportChanged
     *
     * @param isSupport isSupport
     */
    void onGearSupportChanged(boolean isSupport);

    /**
     * onGearChanged
     *
     * @param i i
     */
    void onGearChanged(int i);
}
package com.zeekr.car.api.device;

/**
 * @author Lei.Chen29
 * @date 2023/7/7 13:40
 * description：
 */
public class SimCardDefImpl implements SimCard {

    /**
     * SIM卡是否实名
     */
    @Override
    public boolean simIsAuth() {
        return true;
    }
}

package com.zeekr.car.tsp;

import android.content.Context;

/**
 * @author mac
 * @date 2022/7/19 14:29
 * description：TODO
 */
public final class TspAPIImpl extends TspAPI {
    private Context context;

    public TspAPIImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public IEnvType getEnvType() {
        return new EnvType(PropertiesUtil.getStringProp(this.context, "persist.sys.tsp_env"));
    }
}

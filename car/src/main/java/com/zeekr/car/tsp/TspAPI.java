package com.zeekr.car.tsp;

import android.content.Context;

/**
 * @author mac
 * @date 2022/7/19 14:28
 * description：TODO
 */
public abstract class TspAPI {
    private static final String TAG = "TspAPI";

    public TspAPI() {
    }

    public static TspAPI create(Context context) {
        return new TspAPIImpl(context);
    }

    public abstract IEnvType getEnvType();
}

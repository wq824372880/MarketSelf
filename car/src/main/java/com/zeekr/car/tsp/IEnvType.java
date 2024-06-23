package com.zeekr.car.tsp;

/**
 * @author mac
 * @date 2022/7/19 14:27
 * description：TODO
 */
public interface IEnvType {
    boolean isProductionEnv();

    boolean isStagingEnv();

    boolean isTestingEnv();

    boolean isDevelopment();

    String string();
}

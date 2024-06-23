package com.zeekr.car.tsp;

/**
 * @author mac
 * @date 2022/7/19 14:32
 * description：TODO
 */
final public class EnvType implements IEnvType {
    public static final String PROP_PERSIST_TSP_ENV_TYPE = "persist.sys.tsp_env";
    private static final String PRODUCTION = "production";
    private static final String STAGING = "staging";
    private static final String TESTING = "testing";
    private static final String DEVELOPMENT = "development";
    private String envType;

    public EnvType(String envType) {
        if (envType != null && (envType.equals(PRODUCTION) || envType.equals(STAGING) || envType.equals(TESTING) || envType.equals(
            DEVELOPMENT))) {
            this.envType = envType;
        } else {
            this.envType = PRODUCTION;
        }
    }

    @Override
    public boolean isProductionEnv() {
        return PRODUCTION.equals(this.envType);
    }

    @Override
    public boolean isStagingEnv() {
        return STAGING.equals(this.envType);
    }

    @Override
    public boolean isTestingEnv() {
        return TESTING.equals(this.envType);
    }

    @Override
    public boolean isDevelopment() {
        return DEVELOPMENT.equals(this.envType);
    }

    @Override
    public String string() {
        return this.envType;
    }
}

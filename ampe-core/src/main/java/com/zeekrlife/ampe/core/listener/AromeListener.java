package com.zeekrlife.ampe.core.listener;

import com.alipay.arome.aromecli.response.AromeResponse;

/**
 * @author mac
 * @date 2022/8/31 17:09
 * description：TODO
 */
public interface AromeListener {
    void init(AromeResponse response);

    void launcher(AromeResponse response);

    void login(AromeResponse response);

    void logout(AromeResponse response);

    void preloadApp(AromeResponse response);

    void batchPreloadApp(AromeResponse response);

    void getUserInfo(AromeResponse response);

    void getAppStatus(AromeResponse response);

    void uploadLog(AromeResponse response);

    void launcherMiniService(AromeResponse response);

    void launchCustomService(AromeResponse response);

    void extendBridgeRequest(AromeResponse response);

    void bridgeSendEvent(AromeResponse response);
}

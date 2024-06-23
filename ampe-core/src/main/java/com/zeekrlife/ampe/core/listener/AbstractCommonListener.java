package com.zeekrlife.ampe.core.listener;

import com.alipay.arome.aromecli.response.AromeResponse;

/**
 * @author mac
 * @date 2022/8/31 17:08
 * description：TODO
 */
public abstract class AbstractCommonListener implements AromeListener,AromeExtListener {
    @Override
    public void init(AromeResponse response) {

    }

    @Override
    public void launcher(AromeResponse response) {

    }

    @Override
    public void login(AromeResponse response) {

    }

    @Override
    public void logout(AromeResponse response) {

    }

    @Override
    public void preloadApp(AromeResponse response) {

    }

    @Override
    public void batchPreloadApp(AromeResponse response) {

    }

    @Override
    public void getUserInfo(AromeResponse response) {

    }

    @Override
    public void getAppStatus(AromeResponse response) {

    }

    @Override
    public void uploadLog(AromeResponse response) {

    }

    @Override
    public void launcherMiniService(AromeResponse response) {

    }

    @Override
    public void launchCustomService(AromeResponse response) {

    }

    @Override
    public void extendBridgeRequest(AromeResponse response) {

    }

    @Override
    public void bridgeSendEvent(AromeResponse response) {

    }

    @Override
    public void initExt(AromeResponse response) {

    }

    @Override
    public void registerBiz(AromeResponse response) {

    }

    @Override
    public void sendRpc(AromeResponse response) {

    }

    @Override
    public void loadWidget(AromeResponse response) {

    }

    @Override
    public void destroyWidget(AromeResponse response) {

    }
}

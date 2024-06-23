package com.zeekrlife.ampe.core.listener;

import com.alipay.arome.aromecli.response.AromeResponse;

/**
 * @author mac
 * @date 2022/8/31 17:09
 * description：TODO
 */
public interface AromeExtListener {
    void initExt(AromeResponse response);

    void registerBiz(AromeResponse response);

    void sendRpc(AromeResponse response);

    void loadWidget(AromeResponse response);

    void destroyWidget(AromeResponse response);

}

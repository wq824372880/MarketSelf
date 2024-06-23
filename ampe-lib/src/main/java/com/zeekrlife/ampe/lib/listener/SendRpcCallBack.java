package com.zeekrlife.ampe.lib.listener;

import com.zeekrlife.ampe.aidl.AppletInfo;

/**
 * @author mac
 * @date 2023/5/9 17:08
 * description：TODO
 */
public interface SendRpcCallBack extends CallBack {
    void sendRpcCallBack(AppletInfo info);
}

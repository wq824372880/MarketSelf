package com.zeekrlife.ampe.lib.listener;

import com.zeekrlife.ampe.aidl.AppletInfo;

/**
 * @author mac
 * @date 2022/8/31 17:08
 * description：TODO
 */
public interface SendEventCallBack extends CallBack {
    void sendEventCallBack(AppletInfo info);
}

package com.zeekrlife.ampe.lib.listener;

import com.zeekrlife.ampe.aidl.AppletInfo;

/**
 * @author mac
 * @date 2022/5/9
 * description：TODO
 */
public interface RegisterBizCallBack extends CallBack {
    void registerBizCallBack(AppletInfo info);
}

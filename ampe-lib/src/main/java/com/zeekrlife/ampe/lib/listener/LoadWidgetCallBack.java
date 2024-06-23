package com.zeekrlife.ampe.lib.listener;

import com.zeekrlife.ampe.aidl.AppletInfo;

/**
 * @author mac
 * @date 2023/5/9
 * description：TODO
 */
public interface LoadWidgetCallBack extends CallBack {
    void loadWidgetCallBack(AppletInfo info);
}

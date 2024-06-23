package com.zeekrlife.ampe.core.bean;

import com.alipay.arome.aromecli.response.AromeResponse;
import com.zeekrlife.ampe.aidl.AppletInfo;

/**
 * @author mac
 * @date 2022/9/1 13:21
 * description：TODO
 */
public class AppletMapper {

    public static AppletInfo ampe2Entity(AromeResponse aromeResponse) {
        AppletInfo appletInfo = new AppletInfo();
        appletInfo.setSuccess(aromeResponse.success);
        appletInfo.setCode(aromeResponse.code);
        appletInfo.setMessage(aromeResponse.message);
        return appletInfo;
    }
}

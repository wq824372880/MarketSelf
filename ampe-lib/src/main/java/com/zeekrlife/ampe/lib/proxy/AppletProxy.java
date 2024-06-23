package com.zeekrlife.ampe.lib.proxy;

import android.content.Context;
import android.os.RemoteException;

import com.zeekrlife.ampe.lib.listener.AppStatusCallBack;
import com.zeekrlife.ampe.lib.listener.BatchPreLoadCallBack;
import com.zeekrlife.ampe.lib.listener.BridgeCallBack;
import com.zeekrlife.ampe.lib.listener.GetUserInfoCallBack;
import com.zeekrlife.ampe.lib.listener.InitAromeCallBack;
import com.zeekrlife.ampe.lib.listener.LaunchCustomServiceCallBack;
import com.zeekrlife.ampe.lib.listener.LaunchMiniServiceCallBack;
import com.zeekrlife.ampe.lib.listener.LauncherCallBack;
import com.zeekrlife.ampe.lib.listener.LoginCallBack;
import com.zeekrlife.ampe.lib.listener.LoginOutCallBack;
import com.zeekrlife.ampe.lib.listener.PreLoadCallBack;
import com.zeekrlife.ampe.lib.listener.SendEventCallBack;
import com.zeekrlife.ampe.lib.listener.UploadLogCallBack;
import com.zeekrlife.ampe.lib.manager.AppletManager;

import java.util.List;

public class AppletProxy {
    private static final String TAG = "AppletProxy";

    public static AppletProxy getInstance() {

        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        public static final AppletProxy INSTANCE = new AppletProxy();
    }

    public void initArome(Context context, String deviceId, String signature, InitAromeCallBack initAromeCallBack) throws RemoteException {

        AppletManager.getInstance().initArome(context, deviceId, signature, initAromeCallBack);
    }

    public void launcherApplet(String appletId, LauncherCallBack launcherCallBack) throws RemoteException {

        AppletManager.getInstance().launcherApplet(appletId, launcherCallBack);
    }

    public void launcherAppletWithFullScreen(String appletId, LauncherCallBack launcherCallBack) throws RemoteException {

        AppletManager.getInstance().launcherAppletWithFullScreen(appletId, launcherCallBack);
    }

    public void exitApplet() throws RemoteException {

        AppletManager.getInstance().exitApplet();
    }

    public void login(LoginCallBack callback) throws RemoteException {

        AppletManager.getInstance().login(callback);
    }

    public void loginOut(LoginOutCallBack callback) throws RemoteException {

        AppletManager.getInstance().loginOut(callback);
    }

    public void preLoad(String appletId, PreLoadCallBack callback) throws RemoteException {

        AppletManager.getInstance().preLoad(appletId, callback);
    }

    public void batchPreLoad(List<String> appletIds, BatchPreLoadCallBack callback) throws RemoteException {

        AppletManager.getInstance().batchPreLoad(appletIds, callback);
    }

    public void userInfo(GetUserInfoCallBack callback) throws RemoteException {

        AppletManager.getInstance().userInfo(callback);
    }

    public void appStatus(String appletId, AppStatusCallBack callback) throws RemoteException {

        AppletManager.getInstance().appStatus(appletId, callback);
    }

    public void upLoadLog(String startDate, String endDate, UploadLogCallBack callback) throws RemoteException {

        AppletManager.getInstance().upLoadLog(startDate, endDate, callback);
    }

    public void launcherMiNiService(String miniServiceCode, LaunchMiniServiceCallBack callback) throws RemoteException {

        AppletManager.getInstance().launcherMiNiService(miniServiceCode, callback);
    }

    public void launcherCustomService(String customServiceCode, String userIdentity, LaunchCustomServiceCallBack callback)
        throws RemoteException {

        AppletManager.getInstance().launcherCustomService(customServiceCode, userIdentity, callback);
    }

    public void extendBridgeRequest(List<String> extensionList, String bridgeExtensionParams, BridgeCallBack callback)
        throws RemoteException {

        AppletManager.getInstance().extendBridgeRequest(extensionList, bridgeExtensionParams, callback);
    }

    public void sendEvent(String eventName, String eventData, SendEventCallBack callback) throws RemoteException {

        AppletManager.getInstance().sendEvent(eventName, eventData, callback);
    }

//    public void initAromeExt(String deviceId, String locationInfo, InitAromeExtCallBack callback) throws RemoteException {
//
//        AppletManager.getInstance().initAromeExt(deviceId, locationInfo, callback);
//    }
//
//    public void registerBiz(String bizType, RegisterBizCallBack callback) throws RemoteException {
//
//        AppletManager.getInstance().registerBiz(bizType, callback);
//    }
//    public void sendRpc(String operationType, String requestData, SendEventCallBack callback) throws RemoteException {
//
//        AppletManager.getInstance().sendRpc(operationType, requestData, callback);
//    }
//    public void loadWidget(String query, boolean showPlaceholder, LoadWidgetCallBack callback) throws RemoteException {
//
//        AppletManager.getInstance().loadWidget(query, showPlaceholder, callback);
//    }
//    public void destroyWidget(String widgetId) throws RemoteException {
//
//        AppletManager.getInstance().destroyWidget(widgetId);
//    }

    public void release() {
        AppletManager.getInstance().release();
    }

    public boolean ensureServiceAvailable() {
        return AppletManager.getInstance().ensureServiceAvailable();
    }
}



// IAppletService.aidl
package com.zeekrlife.ampe.aidl;
import com.zeekrlife.ampe.aidl.IAppletCallback;

interface IAppletService {

    void initArome(String deviceId,String signature);

    void launcherApplet(String appletId);
    void launcherAppletWithFullScreen(String appletId);
    void exitApplet();

    void login();
    void logout();

    void preloadApp(String appletId);
    void batchPreloadApp(in List<String> appletIds);

    void getUserInfo();

    void getAppStatus(String appletId);

    void uploadLog(String startDate,String endDate);

    void launcherMiniService(String miniServiceCode);

    void launchCustomService(String customServiceCode,String userIdentity);

    void extendBridgeRequest(in List<String> extensionList,String bridgeExtensionParams);

    void bridgeSendEvent(String eventName,String eventData);

    void initExt(String deviceId ,String locationInfo);

    void registerBiz(String bizType);

    void sendRpc(String operationType,  String requestData);

    void loadWidget(String query,boolean showPlaceholder);

    void closeWidget(String widgetId);

    void destroyWidget(String widgetId);


    boolean registerAppletCallback(in IAppletCallback callback);
    boolean unregisterAppletCallback(in IAppletCallback callback);


}

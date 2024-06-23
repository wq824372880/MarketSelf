// IAppletCallback.aidl
package com.zeekrlife.ampe.aidl;

import com.zeekrlife.ampe.aidl.AppletInfo;

interface IAppletCallback {

        void initAromeCallBack(in AppletInfo info);

        void loginCallBack(in AppletInfo info);
        void loginOutCallBack(in AppletInfo info);

        void launcherAppletCallBack(in AppletInfo info);

        void preloadAppCallBack(in AppletInfo info);
        void batchPreloadAppCallBack(in AppletInfo info);

        void getUserInfoCallBack(in AppletInfo info);
        void getAppStatusCallBack(in AppletInfo info);

        void uploadLogCallBack(in AppletInfo info);

        void launcherMiniServiceCallBack(in AppletInfo info);
        void launchCustomServiceCallBack(in AppletInfo info);

        void bridgeRequestCallBack(in AppletInfo info);
        void bridgeSendEventCallBack(in AppletInfo info);

        void initExtCallBack(in AppletInfo info);
        void registerBizCallBack(in AppletInfo info);
        void sendRpcCallBack(in AppletInfo info);
        void loadWidgetCallBack(in AppletInfo info);
        void destroyWidgetCallBack(in AppletInfo info);

}

// ICheckUpdateCallback.aidl
package com.zeekrlife.market.update;

import com.zeekrlife.market.update.IAppInfo;

interface ICheckUpdateCallback {

    boolean onAppUpdate(boolean update,in IAppInfo appInfo);
}

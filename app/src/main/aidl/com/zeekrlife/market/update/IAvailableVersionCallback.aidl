package com.zeekrlife.market.update;

import com.zeekrlife.market.update.IAppInfo;

interface IAvailableVersionCallback {

    boolean onAppAvailableVersion(boolean hasAvailableVersion,in IAppInfo appInfo);
}

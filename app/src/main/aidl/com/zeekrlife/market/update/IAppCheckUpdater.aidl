
package com.zeekrlife.market.update;

import com.zeekrlife.market.update.ICheckUpdateCallback;
import com.zeekrlife.market.update.IAvailableVersionCallback;

interface IAppCheckUpdater {

    boolean checkAppUpdate(String packageName, in ICheckUpdateCallback callback);

    boolean hasAvailableVersion(String packageName, in IAvailableVersionCallback callback);
}
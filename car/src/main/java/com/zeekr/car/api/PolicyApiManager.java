package com.zeekr.car.api;


import android.content.Context;
import android.util.Log;

import com.zeekr.car.api.policy.IPolicyListener;
import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.policy.ability.IAppPolicy;
import com.zeekr.sdk.policy.bean.AppPolicyInfo;
import com.zeekr.sdk.policy.impl.PolicyAPI;

import java.util.ArrayList;
import java.util.List;

public class PolicyApiManager {
    public static final String TAG = "PolicyApiManager";
    private PolicyAPI policyAPI;
    private final List<IPolicyListener> iPolicyListenerList = new ArrayList<>();

    //存储当前当启动App的包名
    private final List<String> appCanUsePackageNameList = new ArrayList<>();
    //存储当前当禁止启动App的包名
    private final List<String> appNotUsePackageNameList = new ArrayList<>();

    private PolicyApiManager() {
    }

    public void init(Context context, ApiReadyCallback callback) {
        if (policyAPI == null) {
            policyAPI = PolicyAPI.get();
        }
        policyAPI.init(context, callback);
    }

    public IAppPolicy getAppPolicy() {
        if (policyAPI == null) {
            return null;
        }
        return policyAPI.getAppPolicy();
    }

    /**
     * 查询应用启动状态
     *
     * @param pkgName
     * @return
     */
    public AppPolicyInfo checkStartup(String pkgName) {
        if (policyAPI == null) {
            return null;
        }
        return policyAPI.getAppPolicy().checkStartup(pkgName);
    }

    public boolean registerStartupStateObserver(List<String> pkgNameList) {
//        try {
//            return policyAPI.getAppPolicy().registerStartupStateObserver(pkgNameList, list -> {
//                for (AppPolicyInfo info : list) {
//                    Log.i(TAG, "AppPolicyInfo ==> " + info.getPkgName() + "," + info.getCode());
//                    String pkgName = info.getPkgName();
//                    if (info.getCode() == 4) {
//                        if (!appCanUsePackageNameList.contains(pkgName)) {
//                            appCanUsePackageNameList.add(pkgName);
//                        }
//                    } else {
//                        appCanUsePackageNameList.remove(pkgName);
//                    }
//
//                    if (info.getCode() == 1) {
//                        if (!appNotUsePackageNameList.contains(pkgName)) {
//                            appNotUsePackageNameList.add(pkgName);
//                        }
//                    } else {
//                        appNotUsePackageNameList.remove(pkgName);
//                    }
//                }
//
//                for (IPolicyListener listener : iPolicyListenerList) {
//                    listener.onStateChange(list);
//                }
//            });
//        } catch (Exception exception) {
//            Log.i(TAG, "registerStartupStateObserver Error ==> " + exception);
//            return false;
//        }
        return false;
    }

    public boolean unregisterStartupStateObserver() {
        return policyAPI.getAppPolicy().unregisterStartupStateObserver();
    }

    public synchronized void addIPolicyListener(IPolicyListener iPolicyListener) {
        if (!iPolicyListenerList.contains(iPolicyListener)) {
            iPolicyListenerList.add(iPolicyListener);
        }
    }

    public synchronized void removeIPolicyListener(IPolicyListener iPolicyListener) {
        iPolicyListenerList.remove(iPolicyListener);
    }

    public static PolicyApiManager getInstance() {
        return PolicyApiManager.PolicyApiHolder.INSTANCE;
    }

    public List<String> getCanUsePkgNameList() {
        return appCanUsePackageNameList;
    }

    public List<String> getAppNotUsePkgNameList() {
        return appNotUsePackageNameList;
    }

    static class PolicyApiHolder {
        private static final PolicyApiManager INSTANCE = new PolicyApiManager();
    }
}
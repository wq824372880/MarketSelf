package com.zeekr.car.api.policy;

import com.zeekr.sdk.policy.bean.AppPolicyInfo;

import java.util.List;


public interface IPolicyListener {
    void onStateChange(List<AppPolicyInfo> appPolicyInfoList);
}
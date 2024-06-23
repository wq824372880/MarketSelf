package com.zeekrlife.market.user

import android.annotation.SuppressLint
import android.util.Log
import com.zeekr.car.api.DeviceApiManager
import com.zeekr.car.api.UserApiManager
import com.zeekr.sdk.user.bean.UserInfoBean
import com.zeekr.sdk.user.callback.ILoginCallback
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.OpenApiDeviceInfo
import com.zeekrlife.market.data.response.OpenApiInfo
import com.zeekrlife.market.data.response.OpenApiUserInfo

/**
 * @author Lei.Chen29
 * @date 2023/9/21 19:28
 * description：用户登录、登出、切换账号监听
 */
@SuppressLint("LogNotTimber")
class UserLoginCallback : ILoginCallback {

    private val TAG = "UserLoginCallback"

    /**
     * 登录成功
     */
    override fun onLogin() {
        Log.e(TAG, "[onLogin]")
        val userApi = UserApiManager.getInstance().userAPI
        val deviceApi = DeviceApiManager.getInstance().deviceAPI
        val userInfo = userApi?.userInfo

        val mUserInfo = (OpenApiUserInfo(
            userInfo?.userId, userInfo?.username, userInfo?.avatar, userInfo?.mobile,
            userInfo?.sex, userInfo?.identity, userInfo?.expand,
            userApi?.hasLogin() ?: false, userApi?.token,
        ))

        val apiCache = CacheExt.getOpenApi()
        if (apiCache == null) {
            val mDeviceInfo = (OpenApiDeviceInfo(
                deviceApi.ihuid, deviceApi.vin, "", deviceApi.xdsn, deviceApi.iccid,
                deviceApi.vehicleType, deviceApi.projectCode, deviceApi.supplierCode,
                deviceApi.operatorCode, deviceApi.operatorName, deviceApi.openIHUID,
                deviceApi.openVIN, deviceApi.ihuSerialNo, deviceApi.deviceServiceIDJson, deviceApi.vehicleTypeConfig,
            ))
            CacheExt.setOpenApi(OpenApiInfo(mUserInfo, mDeviceInfo))
        } else {
            CacheExt.setOpenApi(OpenApiInfo(mUserInfo, apiCache.deviceInfo))
        }
    }

    /**
     * 登出成功
     */
    override fun onLogout() {
        Log.e(TAG, "[onLogout]")
        val apiCache = CacheExt.getOpenApi()
        if (apiCache != null) {
            CacheExt.setOpenApi(OpenApiInfo(OpenApiUserInfo(userId = ""), apiCache.deviceInfo))
        }
    }

    /**
     * token改变
     */
    override fun onTokenChanged(token: String?) {
    }

    /**
     * 用户信息改变
     */
    override fun onUserInfoChanged(user: UserInfoBean?) {
    }

    /**
     * 切换账号
     */
    override fun onAccountSwitch(s: String?, s1: String?) {
    }
}
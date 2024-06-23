package com.zeekrlife.market.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zeekr.sdk.device.impl.DeviceAPI
import com.zeekr.sdk.user.impl.UserAPI
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.livedata.BooleanLiveData
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.repository.UserRepository
import com.zeekrlife.market.data.response.OpenApiDeviceInfo
import com.zeekrlife.market.data.response.OpenApiInfo
import com.zeekrlife.market.data.response.OpenApiUserInfo
import com.zeekrlife.market.data.response.ProtocolInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rxhttp.async

class LauncherCXViewModel : BaseViewModel() {

    var mIsLogin =  BooleanLiveData()
    var mOpenApiInfo = MutableLiveData<OpenApiInfo>()
    var mRemoteProtocolInfo = MutableLiveData<ProtocolInfoBean>()
    var mRemoteProtocolPolicy = MutableLiveData<ProtocolInfoBean>()

    fun getOpenApiInfo(openApiUserInstance: UserAPI?, openApiDeviceInstance: DeviceAPI) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val userInfo = (OpenApiUserInfo(
                    openApiUserInstance?.userInfo?.userId,
                    openApiUserInstance?.userInfo?.username,
                    openApiUserInstance?.userInfo?.avatar,
                    openApiUserInstance?.userInfo?.mobile,
                    openApiUserInstance?.userInfo?.sex,
                    openApiUserInstance?.userInfo?.identity,
                    openApiUserInstance?.userInfo?.expand,
                    openApiUserInstance?.hasLogin() ?: false,
                    openApiUserInstance?.token,
//                    openApiUserInstance.logout(),
                ))
                val deviceInfo = (OpenApiDeviceInfo(
                    openApiDeviceInstance.ihuid,
                    openApiDeviceInstance.vin,
                    "",
                    openApiDeviceInstance.xdsn,
                    openApiDeviceInstance.iccid,
                    openApiDeviceInstance.vehicleType,
                    openApiDeviceInstance.projectCode,
                    openApiDeviceInstance.supplierCode,
                    openApiDeviceInstance.operatorCode,
                    openApiDeviceInstance.operatorName,
                    openApiDeviceInstance.openIHUID,
                    openApiDeviceInstance.openVIN,
                    openApiDeviceInstance.ihuSerialNo,
                    openApiDeviceInstance.deviceServiceIDJson,
                    openApiDeviceInstance.vehicleTypeConfig,

                    ))
                CacheExt.setOpenApi(OpenApiInfo(userInfo, deviceInfo))
                mOpenApiInfo.postValue(OpenApiInfo(userInfo, deviceInfo))
            }
        }
    }

    /**
     * 首次启动获取协议详情（未有缓存）
     */
    fun getProtocolInfo() {
        rxHttpRequest {
            onRequest = {

                val remoteProtocolInfoData = UserRepository.getProtocolInfo(Constants.APPSTOREUA_BX1E).async(this)
                val remoteProtocolPolicyData = UserRepository.getProtocolInfo(Constants.APPSTOREPP_BX1E).async(this)

                mRemoteProtocolInfo.value = remoteProtocolInfoData.await()
                mRemoteProtocolPolicy.value = remoteProtocolPolicyData.await()

                mRemoteProtocolInfo.value?.let {
                    CacheExt.setUserAgreement(it)
                }
                mRemoteProtocolPolicy.value?.let {
                    CacheExt.setProtocol(it)
                }
            }

        }
    }

    /**
     * 用户签订协议
     */
    fun postProtocolSign(user:ProtocolInfoBean?, protocol: ProtocolInfoBean?, userApiInfo:OpenApiUserInfo?, status:String) {
        rxHttpRequest {
            onRequest = {
                UserRepository.postProtocolSign(user,protocol,userApiInfo,status).await()
            }
        }

    }

}
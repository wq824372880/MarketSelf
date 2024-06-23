package com.zeekrlife.market.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zeekr.sdk.user.impl.UserAPI
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.livedata.BooleanLiveData
import com.zeekrlife.common.livedata.StringLiveData
import com.zeekrlife.market.data.repository.UserRepository
import com.zeekrlife.market.data.response.UserInfo
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.logA
import com.zeekrlife.net.interception.logging.util.logI
import com.zeekrlife.net.load.LoadingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rxhttp.async

/**
 * 描述　:
 */
class LoginViewModel : BaseViewModel() {

    //账户名
    val userName = StringLiveData()

    //密码
    val password = StringLiveData()

    //是否显示明文密码（登录注册界面）
    var isShowPwd = BooleanLiveData()

    //登录请求信息
    val loginData = MutableLiveData<UserInfo>()


    /**
     * 跳转登录
     */
    fun launchToLogin() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                UserAPI.get().launchToLogin(true)
            }
        }
    }

    /**
     * 登录
     * @param phoneNumber String
     * @param password String
     */
    fun login(phoneNumber: String, password: String) {
        rxHttpRequest {
            onRequest = {
                loginData.value = UserRepository.login(phoneNumber,password).await()
            }
            loadingType = LoadingType.LOADING_DIALOG //选传
            loadingMessage = "正在登录中....." // 选传
            requestCode = NetUrl.LOGIN // 如果要判断接口错误业务 - 必传
        }
    }


    /**
     * 演示一个串行(嵌套) 请求 写法
     * @param phoneNumber String
     * @param password String
     */
    fun test1(phoneNumber: String, password: String) {
        rxHttpRequest {
            onRequest = {
                //下面2个接口按顺序请求，先请求 getList接口 请求成功后 再执行 login接口， 其中有任一接口请求失败都会走错误回调
                val listData = UserRepository.getList(0).await()
                "打印一下List的大小${listData.pageSize}".logI()
                val loginData = UserRepository.login(phoneNumber, password).await()
                loginData.username.logI()
                "打印一下用户名${loginData.username}".logI()
            }
            loadingType = LoadingType.LOADING_DIALOG
            loadingMessage = "正在登录中....." // 选传
            requestCode = NetUrl.LOGIN // 如果要判断接口错误业务 - 必传
        }
    }

    /**
     * 演示一个并行 请求 写法
     * @param phoneNumber String
     * @param password String
     */
    fun test2(phoneNumber: String, password: String) {
        rxHttpRequest {
            onRequest = {
                //下面2个接口同时请求，2个接口都请求成功后 最后合并值。 其中有任一接口请求失败都会走错误回调
                val listData = UserRepository.getList(0).async(this)
                val loginData = UserRepository.login(phoneNumber, password).async(this)
                //得到合并值
                val mergeValue = loginData.await().username + listData.await().hasMore()
                //打印一下
                mergeValue.logA()
            }
            loadingType = LoadingType.LOADING_DIALOG
            loadingMessage = "正在登录中....." // 选传
            requestCode = NetUrl.LOGIN // 如果要判断接口错误业务 - 必传
        }
    }

}
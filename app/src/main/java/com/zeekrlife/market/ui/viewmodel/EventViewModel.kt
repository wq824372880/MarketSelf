package com.zeekrlife.market.ui.viewmodel

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.zeekrlife.ampe.aidl.AppletInfo
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.market.data.response.OrderReCharged

class EventViewModel : BaseViewModel() {

    val orderReChargedEvent = UnPeekLiveData<OrderReCharged>()

    //首页切换tab
    val switchTabEvent = UnPeekLiveData<Int>()
    val switchTabCXEvent = UnPeekLiveData<Int>()

    //安装应用完成事件  此事件为了防止注册的安装监听有些未能回调成功
    val installCompletedEvent = UnPeekLiveData<Int>()
    //STR模式事件
    val strModeChangeEvent = UnPeekLiveData<Int>()

    //语音通过provider访问小程序结果回调
    val appletResultEvent = UnPeekLiveData<AppletInfo>()

    //自动更新的结果回调
    val autoUpdateSwitch = UnPeekLiveData<Boolean>()
}
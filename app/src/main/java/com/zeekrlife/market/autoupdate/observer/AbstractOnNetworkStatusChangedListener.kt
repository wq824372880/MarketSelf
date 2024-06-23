package com.zeekrlife.market.autoupdate.observer

import com.zeekrlife.common.util.NetworkUtils.OnNetworkStatusChangedListener

abstract class AbstractOnNetworkStatusChangedListener : OnNetworkStatusChangedListener {
    override fun onDisconnected() {}
}
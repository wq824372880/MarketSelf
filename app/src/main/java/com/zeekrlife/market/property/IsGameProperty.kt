package com.zeekrlife.market.property

/**
 * @author Lei.Chen29
 * @date
 * description： 是否是游戏
 * 是否支持双音源 ：0-不是，1-是
 */
class IsGameProperty : AppProperty() {

    override val propertyName: String get() = "isGame"

    override fun isContentResolverNotify() = true
}
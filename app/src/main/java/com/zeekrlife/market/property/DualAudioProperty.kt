package com.zeekrlife.market.property

import com.zeekrlife.market.provider.AppPropertiesProvider

/**
 * @author Lei.Chen29
 * @date 2022/5/20 14:35
 * description：支持双音源属性
 * 是否支持双音源 ：0-不支持，1-支持
 */
class DualAudioProperty : AppProperty() {

    override val propertyName: String get() = "isSupportDualAudio"

    override fun isContentResolverNotify() = true

    override fun onPropertyClear() {
        notifyPropertyChange(AppPropertiesProvider.getUriBuilder("clear", propertyName, "").build())
    }
}
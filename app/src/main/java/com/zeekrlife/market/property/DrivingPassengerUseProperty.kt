package com.zeekrlife.market.property

/**
 * @author Lei.Chen29
 * @date
 * description：
 * 行车中是否允许使用（滑移屏副驾位）：0-不允许，1-允许
 */
class DrivingPassengerUseProperty : NonPGearLimitProperty() {

    override fun getDefaultForbidJsonData() = "[\"com.yongshi.tenojo.zeekr\",\"com.netease.party\",\"com.netease.aceracer\",\"com.geely.pma.settings.cube\",\"com.zeekerc.lightingshow\"]"

    override val propertyName: String get() = "supportDrivingPassengerUser"
}
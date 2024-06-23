package com.zeekrlife.market.property

/**
 * @author Lei.Chen29
 * @date
 * description：
 * 行车中是否允许使用（主驾位）：0-不允许，1-允许
 */
class DrivingMainUseProperty : NonPGearLimitProperty() {

    override fun getDefaultForbidJsonData() = "[\"com.yongshi.tenojo.zeekr\",\"com.netease.party\",\"com.netease.aceracer\",\"com.Apinsky.Lite2048.zeekr\",\"com.geely.pma.settings.cube\",\"com.zeekerc.lightingshow\"]"

    override val propertyName: String get() = "supportDrivingUser"
}
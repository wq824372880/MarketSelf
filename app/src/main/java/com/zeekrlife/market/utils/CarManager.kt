package com.zeekrlife.market.utils

import com.zeekr.car.sensor.SensorAdapter
import com.zeekrlife.common.ext.getScreenWidthIs2880
import com.zeekrlife.common.ext.getScreenWidthIs3840

/**
 * @author Lei.Chen29
 * @date 2023/7/19 18:34
 * description：
 */
object CarManager {

//    dim	仪表屏
//    hud	抬头显
//    csd	CSD屏
//    console	console屏
//    armrest	扶手屏
//    door_panel	门板屏
//    backrest	椅背屏
//    tv	TV屏
//    ceiling	吸顶屏
    /**
     * 车辆屏幕类型
     */
    enum class ScreenType(val value: String) {
        DIM("dim"),
        HUD("hud"),
        CSD("csd"),
        CONSOLE("console"),
        ARMREST("armrest"),
        DOOR_PANEL("door_panel"),
        BACKREST("backrest"),
        TV("tv"),
        CEILING("ceiling");

        companion object {
            private val map = values().associateBy(ScreenType::name)

            fun getValueByName(name: String): String {
                return if(getScreenWidthIs3840()){
                    "tv"
                }else if(getScreenWidthIs2880()){
                    "ceiling"
                }else{
                    map[name]?.value?:"csd"
                }
            }
        }
    }

    private var mSensorAdapter: SensorAdapter? = null

    fun sensorAdapter() = mSensorAdapter

    fun setSensorAdapter(sensorAdapter: SensorAdapter) {
        mSensorAdapter = sensorAdapter
    }
}
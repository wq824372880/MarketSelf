package com.zeekr.car.api.partnum

/**
 * @author Lei.Chen29
 * @date 2023/6/2 14:35
 * description：零件号版本
 */
interface PartNumVersion {

    /**
     * 零件号
     */
    fun systemPartNumVersion(): String

    /**
     * 零件号的校验，如果未获取到重新获取
     */
    fun systemPartNumVerifyIfReload(listener: PartNumLoadListener?): Boolean
}
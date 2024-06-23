package com.zeekr.car.api.partnum

/**
 * @author Lei.Chen29
 * @date 2023/11/8 11:28
 * description：零件号加载监听
 */
interface PartNumLoadListener {
    fun onSystemPnReceive(systemPn: String?)
}
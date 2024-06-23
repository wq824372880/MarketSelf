package com.zeekr.car.adaptapi

/**
 * @author Lei.Chen29
 * @date 2022/7/6 9:33
 * description：
 */

object CsdPosn {
    /**
     * csd：中间
     */
    const val SLAG_CSD_POSN_MIDPOSN = 1

    /**
     * csd：副驾
     */
    const val SLAG_CSD_POSN_COPILOTPOSN = 2

    /**
     * csd：左移中
     */
    const val SLAG_CSD_POSN_SLIDINGLE = 3

    /**
     * csd：右移中
     */
    const val SLAG_CSD_POSN_SLIDINGRI = 4
}

interface SlideCsdPosnListener {

    /**
     * csd移动位置
     * @param position
     */
    fun onCsdSlide(position: Int)
}
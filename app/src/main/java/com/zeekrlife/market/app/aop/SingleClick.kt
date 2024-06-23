package com.zeekrlife.market.app.aop

/**
 *    desc   : 防重复点击注解
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
annotation class SingleClick constructor(
    /**
     * 快速点击的间隔
     */
    val value: Long = 1000
)

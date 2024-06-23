package com.zeekrlife.common.ext

import android.view.View
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

private var triggerLastTime: Long = 0

/**
 * get set
 * 给view添加一个延迟的属性（用来屏蔽连击操作）
 */
private var triggerDelay: Long = 0

/**
 * 判断时间是否满足再次点击的要求（控制点击）
 */
private fun <T : View> T.clickEnable(): Boolean {
    var clickable = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        triggerLastTime = currentClickTime
        clickable = true
    }
    return clickable
}

/***
 * 带延迟过滤点击事件的 View 扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(delay: Long = 1000, block: (T) -> Unit) {
    triggerDelay = delay
    setOnClickListener {
        if (clickEnable()) {
            block(this)
        }
    }
}

/**
 * livedata去重
 */
fun <T> LiveData<T>.observeDistinct(owner: LifecycleOwner, observer: Observer<in T>) {
    val distinctLiveData = Transformations.distinctUntilChanged(this)
    distinctLiveData.observe(owner, observer)
}


@MainThread
fun <T> LiveData<T>.observeByChanged(owner: LifecycleOwner, observer: Observer<in T>, onNoChange: ((boo: Boolean) -> Unit) = {}) {
    val outputLiveData = MediatorLiveData<T>()
    outputLiveData.addSource(this, object : Observer<T> {
        var mFirstTime = true
        override fun onChanged(currentValue: T) {
            val previousValue = outputLiveData.getValue()
            if (mFirstTime || previousValue == null && currentValue != null || previousValue != null && previousValue != currentValue) {
                mFirstTime = false
                outputLiveData.value = currentValue
                observer.onChanged(currentValue)
            } else {
                onNoChange.invoke(true)
            }
        }
    })
    outputLiveData.observe(owner, observer)
}


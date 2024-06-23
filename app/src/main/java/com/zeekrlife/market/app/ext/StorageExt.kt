package com.zeekrlife.market.app.ext

import com.tencent.mmkv.MMKV
import com.zeekrlife.market.data.ValueKey

/**
 * 描述　:
 */

/**
 * 获取MMKV
 */
val mmkv: MMKV by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    MMKV.mmkvWithID(ValueKey.MMKV_APP_KEY)
}

/**
 * 获取始终保存的MMKV实例
 */
val mmkvSave: MMKV by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    MMKV.mmkvWithID(ValueKey.MMKV_APP_KEY_SAVE)
}



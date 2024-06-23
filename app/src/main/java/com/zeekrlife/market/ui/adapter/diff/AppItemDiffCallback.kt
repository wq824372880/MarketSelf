package com.zeekrlife.market.ui.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.zeekrlife.market.data.response.AppItemInfoBean


class AppItemDiffCallback : DiffUtil.ItemCallback<AppItemInfoBean>() {
    /**
     * 判断是否是同一个item
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    override fun areItemsTheSame(
        oldItem: AppItemInfoBean,
        newItem: AppItemInfoBean
    ): Boolean {
        return (oldItem.apkPackageName == newItem.apkPackageName)
    }

    /**
     * 当是同一个item时，再判断内容是否发生改变
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    override fun areContentsTheSame(
        oldItem: AppItemInfoBean,
        newItem: AppItemInfoBean
    ): Boolean {
        return (oldItem.updateTime == newItem.updateTime)
    }

    /**
     * 可选实现
     * 如果需要精确修改某一个view中的内容，请实现此方法。
     * 如果不实现此方法，或者返回null，将会直接刷新整个item。
     *
     * @param oldItem Old data
     * @param newItem New data
     * @return Payload info. if return null, the entire item will be refreshed.
     */
    override fun getChangePayload(
        oldItem: AppItemInfoBean,
        newItem: AppItemInfoBean
    ): Any? {
        return null
    }
}
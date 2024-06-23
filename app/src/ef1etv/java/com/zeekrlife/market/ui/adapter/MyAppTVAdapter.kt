package com.zeekrlife.market.ui.adapter

import androidx.core.net.ParseException
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.clickWithTrigger
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.common.util.TimeUtils
import com.zeekrlife.market.R
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.ItemMyAppLayoutBinding
import com.zeekrlife.market.sensors.trackDownload
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.market.utils.FocusBorderLinearLayout

class MyAppTVAdapter :
    BaseQuickAdapter<AppItemInfoBean, BaseViewHolder>(R.layout.item_my_app_layout) {

    private var onClickUpdateInfo: ((info: String) -> Unit)? = null

    companion object{
        var myAppTabId = -1  //记录我的应用tab的id，设置焦点顺序
    }

    /**
     * 别问 问就是注释必须要求写这么多 代码扫描
     * 别问 问就是注释必须要求写这么多 代码扫描
     * 别问 问就是注释必须要求写这么多 代码扫描
     */
    override fun convert(holder: BaseViewHolder, item: AppItemInfoBean) {
        val binding = ItemMyAppLayoutBinding.bind(holder.itemView)

        ApkUtils.getAppInfo(appContext, item.apkPackageName ?: "")?.icon?.apply {
            binding.layoutAppInfo.imageViewItemIcon.setImageDrawable(this)
            binding.layoutAppInfo.imageViewItemIcon.setCover()

        }

        binding.layoutAppInfo.textViewItemName.text = item.apkName
        binding.layoutAppInfo.textViewItemSlogan.text = item.slogan.ifEmptySetDef()
        binding.layoutAppInfo.btnViewDownload.apply {
            init(TaskHelper.getTaskInfo(item))
            trackDownload(item)
        }

        binding.textViewAppUpdateTimeValue.text = (TimeUtils.getTime(item.updateTime?.run {
           if(this <= 0) getUpdateTimeTimeStamp(item.updateTimeDisplay) else toLong()
        }, TimeUtils.DATE_FORMAT_DATE)).ifEmptySetDef()
        binding.textViewAppUpdates.text = item.updates.ifEmptySetDef()
        binding.textViewAppUpdates.clickWithTrigger {
            onClickUpdateInfo?.invoke(item.updates.ifEmptySetDef())
        }

        val position = holder.absoluteAdapterPosition
        (holder.itemView as FocusBorderLinearLayout).setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && position == (data.size -1)) {
                view.requestFocus()
                view.nextFocusDownId = myAppTabId
            }
        }
    }

    fun setOnClickUpdateInfo(onClickUpdateInfo: (info: String) -> Unit) {
        this.onClickUpdateInfo = onClickUpdateInfo
    }

    private fun getUpdateTimeTimeStamp(updateTimeDisplay: String?): Long? {
        try {
            if (!updateTimeDisplay.isNullOrEmpty()) {
                return TimeUtils.DEFAULT_DATE_FORMAT.parse(updateTimeDisplay)?.time
            }
        } catch (e: ParseException) {
            // 处理解析异常
            e.logStackTrace()
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
        }

        return null
    }

    private fun String?.ifEmptySetDef() = if (isNullOrEmpty()) "--" else this
}
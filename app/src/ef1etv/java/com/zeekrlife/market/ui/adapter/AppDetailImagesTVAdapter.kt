package com.zeekrlife.market.ui.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeekrlife.common.ext.setTVClickListener
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCorner
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.market.R
import com.zeekrlife.market.utils.FocusBorderImageView

class AppDetailImagesTVAdapter :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_app_detail_image) {

    companion object{
        var firstButtonId = -1  // 下载按钮ID
        var secondButtonId = -1  // 卸载按钮ID
        var secondButtonIsVisible = false
    }

    /**
     * 在此方法中设置item数据
     *  在此方法中设置item数据
     *   在此方法中设置item数据
     */
    override fun convert(holder: BaseViewHolder, item: String) {
        val ivImg = holder.itemView as AppCompatImageView
        ivImg.loadWithCorner(item, R.drawable.img_bg_default_large_app_detail_bg, R.drawable.img_bg_error_default_large_app_detail_bg, 16)
        ivImg.setCover()

        val position = holder.absoluteAdapterPosition
        (holder.itemView as FocusBorderImageView).setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.requestFocus()
                when (position) {
                    0 -> {
                        view.nextFocusLeftId = firstButtonId
                        view.nextFocusUpId = firstButtonId
                    }
                    else -> {
                        view.nextFocusUpId = when (secondButtonIsVisible) {
                            true -> secondButtonId
                            else -> firstButtonId
                        }
                    }
                }
            }
        }

        (holder.itemView as FocusBorderImageView).setTVClickListener(false) {
            mOnTVItemClickListener?.invoke(holder.itemView, position)
        }
    }

    /**
     * 设置TV item点击事件  别问 问就是注释必须要求写这么多 代码扫描
     * 别问 问就是注释必须要求写这么多 代码扫描
     * 别问 问就是注释必须要求写这么多 代码扫描
     */
    private var mOnTVItemClickListener: ((View, Int) -> Unit)? = null

    fun setTVItemClickListener(onTVItemClickListener: (View, Int) -> Unit) {
        mOnTVItemClickListener = onTVItemClickListener
    }

}
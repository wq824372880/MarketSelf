package com.zeekrlife.market.ui.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeekrlife.common.ext.setTVClickListener
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCorner
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.market.R
import com.zeekrlife.market.databinding.ItemAppDetailPreviewImageBinding
import com.zeekrlife.market.utils.FocusBorderImageView

class AppDetailImgPreviewTVAdapter :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_app_detail_preview_image) {

    override fun convert(holder: BaseViewHolder, item: String) {
        val itemView = ItemAppDetailPreviewImageBinding.bind(holder.itemView)
        itemView.imgAppDetailPreview.loadWithCorner(
            item,
            R.drawable.img_bg_default_large_app_imgs_bg,
            R.drawable.img_bg_error_default_large_app_imgs_bg,
            16
        )
        itemView.imgAppDetailPreview.setCover()

        (holder.itemView as FocusBorderImageView).setTVClickListener(false) {
            mOnTVItemClickListener?.invoke(holder.itemView)
        }
    }

    private var mOnTVItemClickListener: ((View) -> Unit)? = null

    fun setTVItemClickListener(onTVItemClickListener: (View) -> Unit) {
        mOnTVItemClickListener = onTVItemClickListener
    }
}
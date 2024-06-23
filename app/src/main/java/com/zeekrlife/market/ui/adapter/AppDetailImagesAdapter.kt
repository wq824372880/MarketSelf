package com.zeekrlife.market.ui.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCorner
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.market.R

class AppDetailImagesAdapter :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_app_detail_image) {

    override fun convert(holder: BaseViewHolder, item: String) {
        val ivImg = holder.itemView as AppCompatImageView
        ivImg.loadWithCorner(item, R.drawable.img_bg_default_large_app_detail_bg, R.drawable.img_bg_error_default_large_app_detail_bg, 8)
        ivImg.setCover()

    }

}
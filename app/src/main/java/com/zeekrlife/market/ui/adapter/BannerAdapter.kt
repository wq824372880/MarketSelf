package com.zeekrlife.market.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zeekr.basic.appContext
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCornerByEach
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.market.R
import com.zeekrlife.market.databinding.ItemBannerViewBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class BannerAdapter : BaseBannerAdapter<String>() {


    override fun createViewHolder(
        parent: ViewGroup,
        itemView: View,
        viewType: Int
    ): BaseViewHolder<String> {
        return ViewBindingViewHolder(ItemBannerViewBinding.bind(itemView))
    }

    override fun bindData(holder: BaseViewHolder<String>, data: String, position: Int, pageSize: Int) {
        if (holder is ViewBindingViewHolder) {
//            val options = RequestOptions().placeholder(R.drawable.img_bg_default_large_app_banner_bg)
//                .error(R.drawable.img_bg_default_large_app_banner_bg)
//                .optionalTransform(RoundedCorners(appContext.resources.getDimensionPixelOffset(R.dimen.recommend_banner_imageview_round)))
//            Glide.with(appContext).load(data).apply(options).into(holder.viewBinding.ivBannerImg)
            holder.viewBinding.ivBannerImg.loadWithCornerByEach(data, R.drawable.img_bg_default_large_app_banner_bg, R.drawable.img_bg_default_large_app_banner_bg, R.drawable.img_bg_default_large_app_banner_bg,16,16,16,16)
            holder.viewBinding.ivBannerImg.setCover()

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_banner_view
    }

}
class ViewBindingViewHolder(var viewBinding: ItemBannerViewBinding) :
    BaseViewHolder<String>(viewBinding.root)
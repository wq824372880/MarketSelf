package com.zeekrlife.market.ui.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeekrlife.common.ext.gone
import com.zeekrlife.common.ext.setOnclickNoRepeat
import com.zeekrlife.common.ext.setTVClickListener
import com.zeekrlife.common.ext.visible
import com.zeekrlife.common.imageloader.ImageLoader.load
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCornerByEach
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.market.R
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.ItemCategoryViewBinding
import com.zeekrlife.market.sensors.trackDownload
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.market.utils.FocusBorderConstraintLayout
import com.zeekrlife.market.utils.applet.AppletUtils

class CategoryTVAdapter(layoutResId: Int, data: MutableList<AppItemInfoBean>? = null) :
    BaseQuickAdapter<AppItemInfoBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {

    var firstItemViewId: Int = View.NO_ID

    override fun convert(holder: BaseViewHolder, item: AppItemInfoBean) {
        val binding = ItemCategoryViewBinding.bind(holder.itemView)
        binding.imageViewItemIcon.load(
            item.icon ?: "", R.drawable.img_bg_default, R.drawable.img_bg_error
        )
        binding.appletShadow.loadWithCornerByEach(
             drawableId = R.drawable.applet_shadow, placeHolder = R.drawable.applet_shadow,  error = R.drawable.applet_shadow, leftCorner = 0, topCorner = 0, rightCorner = 0, bottomCorner =  16)
        binding.imageViewItemIcon.setCover()
        binding.appletShadow.setCover()
        binding.textViewItemName.text = item.apkName
        binding.textViewItemSlogan.text = item.slogan

        if(item.dataType ==1){
            binding.btnViewApplet.visible()
            binding.appletShadow.visible()
            binding.btnViewDownload.gone()
            setOnclickNoRepeat(binding.btnViewApplet,interval = 2000){
                AppletUtils.startAppletProcess(item.miniAppId.toString(),false) {
                }
            }
        }else{
            binding.btnViewApplet.gone()
            binding.appletShadow.gone()
            binding.btnViewDownload.visible()
            binding.btnViewDownload.apply {
                init(TaskHelper.getTaskInfo(item))
                trackDownload(item)
            }
        }

        val position = holder.absoluteAdapterPosition
        if (position == 0) {
            holder.itemView.id = View.generateViewId()
            firstItemViewId = holder.itemView.id
        }

        (holder.itemView as FocusBorderConstraintLayout).setTVClickListener(false) {
            mOnTVItemClickListener?.invoke(holder.itemView, position)
        }

    }

    private var mOnTVItemClickListener: ((View, Int) -> Unit)? = null

    fun setTVItemClickListener(onTVItemClickListener: (View, Int) -> Unit) {
        mOnTVItemClickListener = onTVItemClickListener
    }
}
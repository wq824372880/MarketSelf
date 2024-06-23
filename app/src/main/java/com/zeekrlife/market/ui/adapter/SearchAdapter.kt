package com.zeekrlife.market.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeekrlife.common.ext.*
import com.zeekrlife.common.imageloader.ImageLoader.load
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCorner
import com.zeekrlife.common.imageloader.ImageLoader.loadWithCornerByEach
import com.zeekrlife.common.imageloader.ImageLoader.setCover
import com.zeekrlife.common.util.Utils
import com.zeekrlife.market.R
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.databinding.ItemSearchViewBinding
import com.zeekrlife.market.sensors.trackDownload
import com.zeekrlife.market.task.TaskHelper
import com.zeekrlife.market.utils.applet.AppletUtils

class SearchAdapter(layoutResId: Int, data: MutableList<AppItemInfoBean>? = null) :
    BaseQuickAdapter<AppItemInfoBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {

    private var mKeyword: String? = null

    override fun convert(holder: BaseViewHolder, item: AppItemInfoBean) {
        val binding = ItemSearchViewBinding.bind(holder.itemView)
        binding.imageViewItemIcon.load(item.icon ?: "", R.drawable.img_bg_default, R.drawable.img_bg_error)
        binding.appletShadow.loadWithCornerByEach(
            drawableId = R.drawable.applet_shadow, placeHolder = R.drawable.applet_shadow,  error = R.drawable.applet_shadow, leftCorner = 0, topCorner = 0, rightCorner = 0, bottomCorner =  16)
        binding.imageViewItemIcon.setCover()
        binding.appletShadow.setCover()
        binding.textViewItemName.text = Utils.stringInterceptionChangeColor(item.apkName, mKeyword)
        binding.textViewItemSlogan.text = Utils.stringInterceptionChangeColor(item.slogan, mKeyword)

        if (item.dataType == 1) {
            binding.btnViewApplet.visible()
            binding.appletShadow.visible()
            binding.btnViewDownload.gone()
            binding.btnViewApplet.setOnClickListener {
                setOnclickNoRepeat(binding.btnViewApplet, interval = 2000) {
                    AppletUtils.startAppletProcess(item.miniAppId.toString(), false) {
                    }
                }
            }

        } else {
            binding.btnViewApplet.gone()
            binding.appletShadow.gone()
            binding.btnViewDownload.visible()
            binding.btnViewDownload.apply {
                init(TaskHelper.getTaskInfo(item))
                trackDownload(item)
            }
        }
    }

    /**
     * 设置关键字
     */
    fun setKeyWord(keyword: String?) {
        mKeyword = keyword
    }
}
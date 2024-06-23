package com.zeekrlife.market.ui.activity

import android.app.Activity
import android.os.Bundle
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.divider
import com.zeekrlife.common.ext.finishCurrentActivity
import com.zeekrlife.common.ext.horizontal
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.app.ext.initBack
import com.zeekrlife.market.databinding.ActivityAppDetailPtrviewImageBinding
import com.zeekrlife.market.ui.adapter.AppDetailImgPreviewAdapter
import com.zeekrlife.market.ui.viewmodel.PreviewViewModel
import com.zeekrlife.market.utils.ScreenDensityUtils

class AppDetailImgPreviewActivity : BaseActivity<PreviewViewModel, ActivityAppDetailPtrviewImageBinding>() {

    companion object {

        const val KEY_DATA_IMAGES = "data_images"

        const val KEY_IMAGE_POSITION = "image_position"

        fun start(activity: Activity?, position: Int, images: MutableList<String>) {
            toStartActivity(activity, AppDetailImgPreviewActivity::class.java, Bundle().apply {
                putInt(KEY_IMAGE_POSITION, position)
                putStringArrayList(KEY_DATA_IMAGES, ArrayList(images))
            })
        }
    }


    private val imagesAdapter: AppDetailImgPreviewAdapter by lazy { AppDetailImgPreviewAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mToolbar?.initBack(titleLeftStr = getString(R.string.app_detail_preview)) {
            finishCurrentActivity(this)
        }

        val images = intent?.getStringArrayListExtra(KEY_DATA_IMAGES) ?: arrayListOf()

        val position = intent?.getIntExtra(KEY_IMAGE_POSITION, -1) ?: -1

        mBind.rvImage.horizontal().divider {
            includeVisible = true
            setDivider(resources.getDimensionPixelOffset(R.dimen.app_detail_image_preview_rv_divider), true)
        }.adapter = imagesAdapter

        if (images.isNotEmpty()) {
            imagesAdapter.addData(images)
            if (position > 0) {
                mBind.rvImage.scrollToPosition(position)
            }
        }
    }
}
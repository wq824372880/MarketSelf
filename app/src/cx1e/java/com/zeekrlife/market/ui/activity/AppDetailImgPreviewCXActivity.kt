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
import com.zeekrlife.market.ui.viewmodel.PreviewCXViewModel
import com.zeekrlife.market.utils.ScreenDensityUtils

class AppDetailImgPreviewCXActivity :
    BaseActivity<PreviewCXViewModel, ActivityAppDetailPtrviewImageBinding>() {

    companion object {

        const val KEY_DATA_IMAGES = "data_images"

        const val KEY_IMAGE_POSITION = "image_position"

        /**
         * 开启图片预览Activity的函数。
         *
         * @param activity 上下文Activity，可以为null。
         * @param position 当前显示图片的位置索引。
         * @param images 图片列表，包含需要预览的所有图片的路径或URL。
         */
        fun start(activity: Activity?, position: Int, images: MutableList<String>) {
            // 创建Bundle并填入图片位置和图片列表数据，然后启动新的Activity
            toStartActivity(activity, AppDetailImgPreviewCXActivity::class.java, Bundle().apply {
                putInt(KEY_IMAGE_POSITION, position)
                putStringArrayList(KEY_DATA_IMAGES, ArrayList(images))
            })
        }
    }


    private val imagesAdapter: AppDetailImgPreviewAdapter by lazy { AppDetailImgPreviewAdapter() }

    /**
     * 初始化视图。
     * 该方法负责初始化界面元素，包括工具栏的设置、图片列表的显示以及滚动到指定位置。
     *
     * @param savedInstanceState 如果活动被重新创建，则包含之前保存的状态信息。
     */
    override fun initView(savedInstanceState: Bundle?) {
        // 初始化工具栏，设置返回按钮和标题，并在点击返回时关闭当前活动
        mToolbar?.initBack(titleLeftStr = getString(R.string.app_detail_preview)) {
            finishCurrentActivity(this)
        }

        // 从Intent中获取图片列表和当前显示图片的位置
        val images = intent?.getStringArrayListExtra(KEY_DATA_IMAGES) ?: arrayListOf()

        val position = intent?.getIntExtra(KEY_IMAGE_POSITION, -1) ?: -1

        // 设置图片列表的水平滚动和分隔符，并绑定适配器
        mBind.rvImage.horizontal().divider {
            includeVisible = true
            setDivider(
                resources.getDimensionPixelOffset(R.dimen.app_detail_image_preview_rv_divider),
                true
            )
        }.adapter = imagesAdapter

        // 如果图片列表不为空，则添加数据到适配器，并根据位置滚动到指定图片
        if (images.isNotEmpty()) {
            imagesAdapter.addData(images)
            if (position > 0) {
                mBind.rvImage.scrollToPosition(position)
            }
        }
    }
}
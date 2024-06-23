package com.zeekrlife.market.ui.activity

import android.app.Activity
import android.os.Bundle
import com.zeekrlife.common.ext.divider
import com.zeekrlife.common.ext.finishCurrentActivity
import com.zeekrlife.common.ext.horizontal
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.market.R
import com.zeekrlife.market.app.base.BaseActivity
import com.zeekrlife.market.databinding.ActivityAppDetailPtrviewImageBinding
import com.zeekrlife.market.ui.adapter.AppDetailImgPreviewTVAdapter
import com.zeekrlife.market.ui.viewmodel.PreviewViewModel
import com.zeekrlife.market.widget.initBack

class AppTVDetailImgPreviewActivity :
    BaseActivity<PreviewViewModel, ActivityAppDetailPtrviewImageBinding>() {

    companion object {

        const val KEY_DATA_IMAGES = "data_images"

        const val KEY_IMAGE_POSITION = "image_position"

        /**
         * 从指定的Activity启动图片预览Activity。
         *
         * @param activity 当前的Activity实例，可以为null。
         * @param position 图片在列表中的位置，用于指定预览的起始图片。
         * @param images 包含所有需要预览图片的URL的列表。
         */
        fun start(activity: Activity?, position: Int, images: MutableList<String>) {
            // 创建一个Bundle，用于传递图片位置和图片列表到目标Activity
            toStartActivity(activity, AppTVDetailImgPreviewActivity::class.java, Bundle().apply {
                putInt(KEY_IMAGE_POSITION, position) // 传递起始图片位置
                putStringArrayList(KEY_DATA_IMAGES, ArrayList(images)) // 传递图片列表
            })
        }
    }

    //这段代码定义了一个私有的属性imagesAdapter，它是一个AppDetailImgPreviewTVAdapter类型的实例。
    // 这个属性使用了Kotlin的by lazy委托，意味着它会在第一次被访问时进行初始化，之后再次访问时会返回第一次初始化的结果。
    // 这样可以保证属性只被初始化一次，并且是线程安全的
    private val imagesAdapter: AppDetailImgPreviewTVAdapter by lazy { AppDetailImgPreviewTVAdapter() }

    /**
     * 初始化视图。
     * 此函数在活动创建时被调用，用于初始化界面元素，并设置相关的点击事件和数据。
     *
     * @param savedInstanceState 如果活动之前被销毁，这参数包含之前的状态。可用来恢复界面的状态。
     */
    override fun initView(savedInstanceState: Bundle?) {
        // 请求焦点以便于工具栏文本可以接收点击事件
        mBind.toolBarTv.requestFocus()
        // 设置返回按钮的点击事件，点击后关闭当前活动
        mBind.toolBarTv.initBack {
            finishCurrentActivity(this)
        }
        // 设置工具栏左上角文本为“应用详情预览”
        mBind.toolBarTv.setLeftTitle(R.string.app_detail_preview)

        // 从意图中获取图片列表，如果不存在则初始化为空列表
        val images = intent?.getStringArrayListExtra(KEY_DATA_IMAGES) ?: arrayListOf()

        // 从意图中获取当前要显示的图片位置，如果不存在则默认为-1
        val position = intent?.getIntExtra(KEY_IMAGE_POSITION, -1) ?: -1

        // 设置图片滑动容器的水平布局，并定义分隔线样式
        mBind.rvImage.horizontal().divider {
            includeVisible = true
            setDivider(
                resources.getDimensionPixelOffset(R.dimen.app_detail_image_preview_rv_divider),
                true
            )
        }.adapter = imagesAdapter  // 设置适配器

        // 设置图片适配器的点击事件
        imagesAdapter.setTVItemClickListener { _ ->
        }

        // 如果图片列表不为空，则加载图片数据，如果存在指定显示位置，则滚动到该位置
        if (images.isNotEmpty()) {
            imagesAdapter.addData(images)
            if (position > 0) {
                mBind.rvImage.scrollToPosition(position)
            }
        }
    }

    /**
     *
     * 是否显示toolbar
     */
    override fun showToolBar(): Boolean {
        return false
    }
}
package com.zeekrlife.common.imageloader

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.zeekrlife.common.ext.getUINightMode

object ImageLoader {

    fun ImageView.load(url: String) {
        Glide.with(this).load(url).transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
    }

    fun ImageView.load(url: String, placeHolder: Int, error: Int) {
        val options = RequestOptions().placeholder(placeHolder).error(error)
        Glide.with(this).load(url).apply(options).diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
    }

    /**
     * @param url
     * @param placeHolder
     * @param error
     * @param corner
     */
    fun ImageView.loadWithCorner(url: String, placeHolder: Int, error: Int, corner: Int) {
        val options = RequestOptions().placeholder(placeHolder)
            .error(error)
            .optionalTransform(CenterCrop())
            .optionalTransform(RoundedCorners(corner))
        Glide.with(this).load(url).apply(options).diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
    }

    /**
     * 四个圆角分别自定义
     */
    fun ImageView.loadWithCornerByEach(url: String? = "",drawableId:Int?, placeHolder: Int, error: Int, leftCorner: Int,topCorner: Int,rightCorner: Int,bottomCorner: Int) {
        val options = RequestOptions().placeholder(placeHolder)
            .error(error)
//            .transform(MultiTransformation(
//                CenterCrop(),
//                RoundedCornersTransformation(leftCorner,0,RoundedCornersTransformation.CornerType.TOP_LEFT),
//                RoundedCornersTransformation(topCorner,0,RoundedCornersTransformation.CornerType.TOP_RIGHT),
//                RoundedCornersTransformation(rightCorner,0,RoundedCornersTransformation.CornerType.BOTTOM_RIGHT),
//                RoundedCornersTransformation(bottomCorner,0,RoundedCornersTransformation.CornerType.BOTTOM_LEFT)
//            ))
            .optionalTransform(CenterCrop())
            .optionalTransform(RoundBitmapTransformation(leftCorner,topCorner,rightCorner,bottomCorner))

        Glide.with(this).load(if(url?.isNotEmpty() == true) url else drawableId).apply(options).diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
    }

    /**
     * 圆形图片带边框
     *
     * @param url
     * @param placeHolder
     * @param error
     * @param corner
     */
    fun ImageView.loadCornerWithSide(
        url: String,
        placeHolder: Int,
        error: Int,
        corner: Int,
        side: Int = 6,
        color: Int = Color.LTGRAY
    ) {
        val options = RequestOptions()
            .placeholder(placeHolder)
            .error(error)
            .optionalTransform(CenterCrop())
            .optionalTransform(RoundSideHeaderTransformation(corner, side, color))
        Glide.with(this)
            .load(url)
            .apply(options)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }

    fun ImageView.load(url: String, options: RequestOptions) {
        Glide.with(this).load(url).apply(options).diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
    }

    /**
     * 图片蒙层
     */
    fun ImageView.setCover(color: Int? = Color.argb(77, 0, 0, 0)) {
        if(getUINightMode() && color != null){
            imageTintList = ColorStateList.valueOf(color)
            imageTintMode = PorterDuff.Mode.SRC_ATOP
        }

    }
}
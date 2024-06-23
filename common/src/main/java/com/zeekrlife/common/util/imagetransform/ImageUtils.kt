package com.zeekrlife.common.util.imagetransform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.ByteArrayOutputStream

/**
 * @author mac
 * @date 2022/12/1 14:35
 * description：TODO
 */
object ImageUtils {

    /**
     * 加载网络图片，并在成功后将Bitmap回传给监听，如果shareImgUrl为空，则默认使用LogoDrawableId生成默认图
     */
    fun url2ByteArray(context: Context, shareImgUrl: String?, resourceId: Int, loadListener: ImgLoadListener) {
        if (!shareImgUrl.isNullOrEmpty()) {
            Glide.with(context).asBitmap().load(shareImgUrl).listener(object : RequestListener<Bitmap?> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    loadListener.onImgLoadSuccess(bitmap2ByteArray(resource))
                    return false
                }
            }).submit()
        } else {
            if (resourceId > 0) {
                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
                if (bitmap != null) {
                    loadListener.onImgLoadSuccess(bitmap2ByteArray(bitmap))
                }
            }
        }
    }

    interface ImgLoadListener {
        fun onImgLoadSuccess(byteArray: ByteArray)
    }

    /**
     * bitmap转byte[]
     */
    private fun bitmap2ByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(PNG, 100, baos)
        return baos.toByteArray()

    }

}
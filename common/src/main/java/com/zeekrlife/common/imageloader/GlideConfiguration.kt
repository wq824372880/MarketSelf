package com.zeekrlife.common.imageloader

import android.content.Context
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.DecodeFormat

@GlideModule
class GlideConfiguration : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize
        val defaultArrayPoolSize = calculator.arrayPoolSizeInBytes
        builder.setMemoryCache(LruResourceCache((defaultMemoryCacheSize / 2).toLong()))
        builder.setBitmapPool(LruBitmapPool((defaultBitmapPoolSize / 2).toLong()))
        builder.setArrayPool(LruArrayPool(defaultArrayPoolSize / 2))
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, "GlideImgCache",
                (200 * 1024 * 1024).toLong())
        )
        builder.setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
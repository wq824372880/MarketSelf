package com.zeekrlife.market.app

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.response.AdvertisementInfoBean
import com.zeekrlife.market.data.response.HomeItemCategoryBean
import java.io.IOException

/**
 * @author Lei.Chen29
 * @date 2023/10/5 16:53
 * description：预加载数据，用于冷启动速度优化
 */
@SuppressLint("LogNotTimber")
object PreloadDataUtils {

    private const val TAG = "PreloadDataUtils"

    //分类列表缓存
    private var categoryList: List<HomeItemCategoryBean>? = null

    //精品推荐页面数据
    private var recommendList: List<AdvertisementInfoBean?>? = null
    //CX精品推荐页面数据
    private var recommendCXList: List<AdvertisementInfoBean?>? = null

    fun asyncLoad() {
        ThreadPoolUtil.runOnSubThread({
            try {
                //分类列表
                val categoryCache = CacheExt.getCategoryList()
                if (!categoryCache.isNullOrEmpty()) {
                    categoryList = GsonUtils.fromJson<MutableList<HomeItemCategoryBean>>(
                        categoryCache, object : TypeToken<MutableList<HomeItemCategoryBean>>() {}.type
                    )
                }

                //精品推荐
                val recommendCache = CacheExt.getRecommendList()
                if (!recommendCache.isNullOrEmpty()) {
                    recommendList = GsonUtils.fromJson<List<AdvertisementInfoBean>>(
                        recommendCache, object : TypeToken<List<AdvertisementInfoBean>>() {}.type
                    )
                }

                val recommendCXCache = CacheExt.getRecommendCXList()
                if (!recommendCXCache.isNullOrEmpty()) {
                    recommendCXList = GsonUtils.fromJson<List<AdvertisementInfoBean>>(
                        recommendCXCache, object : TypeToken<List<AdvertisementInfoBean>>() {}.type
                    )
                }
                Log.e(TAG, "preload category : ${categoryList?.size} recommend ${recommendList?.size}")
            } catch (e: JsonSyntaxException) {
                // 处理 JSON 解析异常
                Log.e(TAG, "JsonSyntaxException: ${e.message}")
            } catch (e: IllegalStateException) {
                // 处理状态异常
                Log.e(TAG, "IllegalStateException: ${e.message}")
            } catch (e: IOException) {
                // 处理 IO 异常
                Log.e(TAG, "IOException: ${e.message}")
            } catch (e: Exception) {
                // 处理其他异常
                e.logStackTrace()
                Log.e(TAG, "Exception: ${Log.getStackTraceString(e)}")
            }
        },0L)
    }

    fun getCategoryListCache() = categoryList

    fun getRecommendListCache() = recommendList

    fun getRecommendCXListCache() = recommendCXList

    /**
     * 更新推荐列表
     */
    fun updateRecommendListPreloadCache(list: List<AdvertisementInfoBean?>?) {
        this.recommendList = list
    }

    /**
     * 更新CX推荐列表
     */
    fun updateRecommendCXListPreloadCache(list: List<AdvertisementInfoBean?>?) {
        this.recommendCXList = list
    }

    /**
     * 更新分类类别
     */
    fun updateCategoryListPreloadCache(list: List<HomeItemCategoryBean>?) {
        this.categoryList = list
    }
}
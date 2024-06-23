package com.zeekrlife.market.ui.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.tabs.TabLayout.TabGravity
import com.google.gson.reflect.TypeToken
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.code
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.util.EncryptUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.market.app.PreloadDataUtils
import com.zeekrlife.market.data.Constants
import com.zeekrlife.market.data.Constants.APPSTORE_RECOMMEND_ADSENSE
import com.zeekrlife.market.data.Constants.APPSTORE_RECOMMEND_APP_LIST
import com.zeekrlife.market.data.Constants.APPSTORE_RECOMMEND_BANNER
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.repository.AppRepository
import com.zeekrlife.market.data.response.AdvertisementDot
import com.zeekrlife.market.data.response.AdvertisementInfoBean
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.data.response.MediaType
import com.zeekrlife.market.ui.fragment.RecommendFragment
import com.zeekrlife.net.BaseNetConstant
import com.zeekrlife.net.load.LoadingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecommendViewModel : BaseViewModel() {

    //Banner数据
    private var bannerAdvertisement: AdvertisementInfoBean? = null

    var recBannerList = MutableLiveData<List<String>>()

    //推荐位
    private var recAdvertisement: AdvertisementInfoBean? = null

    var recAdsenseList = MutableLiveData<List<String>>()

    //推荐app列表
    private var recAppListAdvertisement: AdvertisementInfoBean? = null

    var recAppList = MutableLiveData<List<AppItemInfoBean>>()

    val noDataLiveData = MutableLiveData<Boolean>()

    val errorDataLiveData = MutableLiveData<Boolean>()

    var recommendList:MutableList<AdvertisementInfoBean?>? = mutableListOf()

    companion object{
        const val TAG = "zzzRecommendViewModel"
    }

    /**
     * 获取推荐列表数据
     * @param loadingXml Boolean 请求时是否需要展示界面加载中loading
     */
    @SuppressLint("LogNotTimber") fun getRecommendApps(loadingXml: Boolean = false, isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.Default) {
                //优先加载缓存
                var recommendListCache: String? = ""
                try {
                    recommendListCache = CacheExt.getRecommendList()
                    recommendList = PreloadDataUtils.getRecommendListCache()?.toMutableList()?: mutableListOf()
                    if(recommendList.isNullOrEmpty() && !recommendListCache.isNullOrEmpty()) {
                        recommendList = GsonUtils.fromJson<MutableList<AdvertisementInfoBean?>>(
                            recommendListCache, object : TypeToken<List<AdvertisementInfoBean?>>() {}.type
                        )
                    }

                    recommendList?.apply {
                        Log.e(TAG, "load recommendAppsCache success")
                        if(!isRefresh){ //下拉刷新没必要再次刷新缓存
                            setValue(recommendList)
                        }
                    }

                } catch (e: Exception) {
                    e.logStackTrace()
                    Log.e(TAG, "load recommendListCache error")
                }

            rxHttpRequest {
                onRequest = {
                    val result = AppRepository.getRecommendAppList(
                        pointCodes = Constants.APPSTORE_ADV_DOTS
                    ).await()

                    launch(Dispatchers.IO) {
                        val recommendListStr = GsonUtils.toJson(result)
                        val recommendListCacheStr = GsonUtils.toJson(recommendListCache)
                        //推荐列表数据一般情况变化不是很频繁，计算数据MD5
                        val recommendListStrMd5 = EncryptUtils.encryptMD5ToString(recommendListStr)
                        val recommendListCacheMd5 = EncryptUtils.encryptMD5ToString(recommendListCacheStr)
                        //缓存为空或者校验数据是否变化
                        if (recommendListCache.isNullOrEmpty() || recommendListStrMd5 != recommendListCacheMd5) {
                            Log.e(TAG, "recommendList change refresh")
                            setValue(result)
                            //添加到缓存
                            CacheExt.setRecommendList(recommendListStr)
                            //更新预加里的载缓存
                            PreloadDataUtils.updateRecommendListPreloadCache(result)
                        } else {
                            Log.e(TAG, "recommendList no change")
                            noDataLiveData.postValue(true)
                        }
                    }
                }

                onError = {
                    errorDataLiveData.value = it.code.toString() != BaseNetConstant.EMPTY_CODE
                }

                loadingType = if (loadingXml) LoadingType.LOADING_XML else LoadingType.LOADING_NULL
                requestCode = RecommendFragment.REQUEST_APP_LIST_RECOMMEND_FRAGMENT
            }
        }
    }

    /**
     * 设置banner列表数据
     */
    private fun setValue(advList: List<AdvertisementInfoBean?>?) {

        val bannerList = mutableListOf<String>()
        val adsenseList = mutableListOf<String>()
        val appList = mutableListOf<AppItemInfoBean>()

        advList?.forEach { adv ->
            when (adv?.pointCode) {
                APPSTORE_RECOMMEND_BANNER -> {
                    bannerAdvertisement = adv
                    filterOutNonHideApp(bannerAdvertisement?.advertisementApiDTOS) {
                        bannerList.add(it.mediaTypes?.get(0)?.androidPic ?: "")
                    }
                }
                APPSTORE_RECOMMEND_ADSENSE -> {
                    recAdvertisement = adv
                    filterOutNonHideApp(recAdvertisement?.advertisementApiDTOS) {
                        adsenseList.add(it.mediaTypes?.get(0)?.androidPic ?: "")
                    }
                }
                APPSTORE_RECOMMEND_APP_LIST -> {
                    recAppListAdvertisement = adv
                    filterOutNonHideApp(recAppListAdvertisement?.advertisementApiDTOS) {
                        it.mediaTypes?.get(0)?.appVersionInfo?.apply { appList.add(this) }
                    }
                }
            }
        }

        recBannerList.postValue(bannerList)
        recAdsenseList.postValue(adsenseList)
        recAppList.postValue(appList)
        noDataLiveData.postValue(bannerList.isNotEmpty() || adsenseList.isNotEmpty() || appList.isNotEmpty())
    }

    /**
     * 过滤出非隐藏的应用广告点。
     *
     * 该函数遍历给定的广告点列表，对每个广告点进行判断，如果其应用版本信息不满足隐藏条件，则将该广告点传递给非隐藏回调函数进行处理；
     * 否则，从列表中移除该广告点。
     *
     * @param dots 广告点列表，可能为null。列表中的每个广告点都包含应用的版本信息和其他元数据。
     * @param nonHide 非隐藏回调函数，用于处理不满足隐藏条件的广告点。该函数接收一个广告点作为参数。
     */
    private fun filterOutNonHideApp(dots: ArrayList<AdvertisementDot>?, nonHide: (dot: AdvertisementDot) -> Unit) {
        dots?.listIterator()?.apply {
            while (hasNext()) {
                val dot = next()
                val mediaType = dot.mediaTypes?.get(0)
                // 判断当前广告点的应用版本是否应该被隐藏
                if (!isHideIcon(mediaType?.appVersionInfo)) {
                    // 如果不满足隐藏条件，调用非隐藏回调函数处理该广告点
                    nonHide(dot)
                } else {
                    // 如果满足隐藏条件，从列表中移除该广告点
                    remove()
                }
            }
        }
    }

    /**
     * 该函数用于判断是否隐藏图标，其输入参数为一个AppItemInfoBean类型的对象，该对象可能为null。函数的返回值为一个布尔值，表示是否隐藏图标。
     * 具体实现逻辑未给出，无法进一步分析函数的详细行为。
     */
    private fun isHideIcon(appVersionInfo: AppItemInfoBean?): Boolean {
        val hideIcon = appVersionInfo?.hideIcon
        if (hideIcon == null || hideIcon == 0) {
            return false
        }
        return true
    }

    /**
     * 获取应用版本ID
     */
    fun getAppVersionId(pointCode: String, position: Int): Long {
        try {
            when (pointCode) {
                APPSTORE_RECOMMEND_BANNER -> {
                    return getAppVersionId(bannerAdvertisement, position)
                }
                APPSTORE_RECOMMEND_ADSENSE -> {
                    return getAppVersionId(recAdvertisement, position)
                }
                APPSTORE_RECOMMEND_APP_LIST -> {
                    return getAppVersionId(recAppListAdvertisement, position)
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return -1
    }

    /**
     * 这个函数名为getAppRouteSource，接收两个参数：pointCode为String类型，position为Int类型，返回值为String类型。
     * 根据提供的pointCode和position参数，函数返回一个字符串类型的App路由源数据。
     * 具体实现逻辑未提供，无法详细描述其功能。
     */
    fun getAppRouteSource(pointCode: String, position: Int): String {
        try {
            when (pointCode) {
                APPSTORE_RECOMMEND_BANNER -> {
                    return getAppRouteSource(bannerAdvertisement, position)
                }
                APPSTORE_RECOMMEND_ADSENSE -> {
                    return getAppRouteSource(recAdvertisement, position)
                }
                APPSTORE_RECOMMEND_APP_LIST -> {
                    return getAppRouteSource(recAppListAdvertisement, position)
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return ""
    }

    /**
     * 获取广告点位
     * @param pointCode
     * @param position
     */
    fun getAppAdvertisementDot(pointCode: String, position: Int): AdvertisementDot? {
        try {
            when (pointCode) {
                APPSTORE_RECOMMEND_BANNER -> {
                    return getAppAdvertisementDot(bannerAdvertisement, position)
                }
                APPSTORE_RECOMMEND_ADSENSE -> {
                    return getAppAdvertisementDot(recAdvertisement, position)
                }
                APPSTORE_RECOMMEND_APP_LIST -> {
                    return getAppAdvertisementDot(recAppListAdvertisement, position)
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return null
    }

    /**
     * 获取点位App的版本id
     */
    private fun getAppVersionId(advertisement: AdvertisementInfoBean?, position: Int): Long {
        val dots = advertisement?.advertisementApiDTOS
        if (!dots.isNullOrEmpty() && position < dots.size) {
            return dots[position].mediaTypes?.get(0)?.appVersionInfo?.id ?: -1
        }
        return -1
    }

    /**
     * 获取应用跳转页面地址
     */
    private fun getAppRouteSource(advertisement: AdvertisementInfoBean?, position: Int): String {
        val dots = advertisement?.advertisementApiDTOS
        if (!dots.isNullOrEmpty() && position < dots.size) {
            return dots[position].mediaTypes?.get(0)?.appSource ?: ""
        }
        return ""
    }

    /**
     * 获取App点位
     * @param advertisement
     * @param position
     */
    private fun getAppAdvertisementDot(advertisement: AdvertisementInfoBean?, position: Int): AdvertisementDot? {
        val dots = advertisement?.advertisementApiDTOS
        if (!dots.isNullOrEmpty() && position < dots.size) {
            return dots[position]
        }
        return null
    }

    /**
     * 该函数用于获取指定包名的应用程序在recAppList中的位置。
     * 它通过遍历recAppList中的每个应用程序，使用equals()方法匹配包名。
     * 如果找到匹配项，则返回该应用程序的位置；如果没有找到匹配项，则返回-1。
     */
    fun getRecAppListPosition(packageName: String): Int {
        recAppList.value?.forEachIndexed { index, app ->
            if (app.apkPackageName.equals(packageName)) {
                return index
            }
        }
        return -1
    }

    /**
     * 测试数据
     */
    private fun mockData(): List<AdvertisementInfoBean> {
        val dots = mutableListOf<AdvertisementInfoBean>()
        //banner
        dots.add(AdvertisementInfoBean().apply {
            pointCode = APPSTORE_RECOMMEND_BANNER
            pointName = "banner"
            advertisementApiDTOS = arrayListOf<AdvertisementDot>().apply {
                add(AdvertisementDot(mediaTypes = mutableListOf<MediaType>().apply {
                    add(
                        MediaType(
                            androidPic = "https://img0.baidu.com/it/u=530426417,2082848644&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
                            appVersionInfo = null
                        )
                    )
                }))
                add(AdvertisementDot(mediaTypes = mutableListOf<MediaType>().apply {
                    add(
                        MediaType(
                            androidPic = "https://www.baidu.com/link?url=dCPbTxlQUU59yew9DIIuJN7L4KNIXYjoP5pK4LbdrVHRzZYOR0QqzPehNw8-kyl8BjOBOn6WVlxD5-rH6zaUfybwU_5kWlwnRvtgqTD3_PmwfZMgx4HOt86fJrZ-PMSJYQFDezAuS_CuIOUCMfI3KJqQSJZb0j3irybdGW2i_X7RBrLfVjZMlxRIczq7lBqdNHfbL-9Vvveqk6ltoBKPh7bfMCqdWndbqBVnnc7Qo3JfZ0a74qCHxF_IoxypG6e1pYUSg-zYXKUhdX76VMs_giP2Va1SzGBMgGRSOZ9Y6mUf2zdnOq-BctW9r1Icxe-oOaeSzFVSZPPLCU2ki0Kst7Q3Sdv20apV32oIt25TTYrorCsfznGoz3qFzRX8aODU0R01rnS89wUESj7A1IrT9tAwjXwPDQEDu7chaiCPyokyWDubUYZBUHPN0SRFINb0pHAzaCDEzhNk-YNxMEwLNm_q0TOTWlj3loJ0Oa3twDbTwfPloNNNsH9EWPsr8HTyHZ2YWXV8W9fz9KDCejg6IHz6QtxbtRKz9TCmkmKf2dpermPHimufQ_Sgo__tM1pJKPe7YObSw8xxklx9R0YMN_t1bcO1eS7cxUFVstLjwi7cy9pzXxtqYhJjeXWlbZR-MV2oyTdB_vQATTzZeDgCI_&wd=&eqid=aa6bb963000c9d170000000462ac4a5b",
                            appVersionInfo = null
                        )
                    )
                }))
            }
        })
        //推荐位
        dots.add(AdvertisementInfoBean().apply {
            pointCode = APPSTORE_RECOMMEND_ADSENSE
            pointName = "adsense"
            advertisementApiDTOS = arrayListOf<AdvertisementDot>().apply {
                add(AdvertisementDot(mediaTypes = mutableListOf<MediaType>().apply {
                    add(
                        MediaType(
                            androidPic = "https://img1.baidu.com/it/u=498718067,3363872028&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",
                            appVersionInfo = null
                        )
                    )
                }))
                add(AdvertisementDot(mediaTypes = mutableListOf<MediaType>().apply {
                    add(
                        MediaType(
                            androidPic = "https://img0.baidu.com/it/u=3643895624,2552772604&fm=253&fmt=auto&app=120&f=JPEG?w=1200&h=675",
                            appVersionInfo = null
                        )
                    )
                }))
            }
        })
        //推荐列表
        dots.add(AdvertisementInfoBean().apply {
            pointCode = APPSTORE_RECOMMEND_APP_LIST
            pointName = "appList"
            advertisementApiDTOS = arrayListOf<AdvertisementDot>().apply {
                add(AdvertisementDot(mediaTypes = mutableListOf<MediaType>().apply {
                    add(
                        MediaType(
                            androidPic = "", appVersionInfo = AppItemInfoBean(
                                apkName = "喜马拉雅",
                                apkVersion = "22",
                                apkMd5 = "f836a37a5eee5dec0611ce15a76e8fd5",
                                apkVersionName = "v1.1.1",
                                apkUrl = "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk",
                                apkPackageName = "com.liulishuo.engzo",
                                id = 4,
                                categoryPid = 1,
                                updates = "更新内容：XASDASDSADSADASDAAa阿斯顿萨达萨达萨达撒旦撒啊萨达萨达萨达是SA",
                                slogan = "美好的事情即将发生美好的事情即将发生美好的事情即将发生美好的事情即将发生"
                            )
                        )
                    )
                }))
            }
        })
        return dots
    }
}
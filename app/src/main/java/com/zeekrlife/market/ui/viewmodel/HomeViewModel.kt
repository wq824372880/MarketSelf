package com.zeekrlife.market.ui.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.zeekrlife.market.utils.CarManager
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.util.EncryptUtils
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.market.app.PreloadDataUtils
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.repository.UserRepository
import com.zeekrlife.market.data.response.HomeItemCategoryBean
import com.zeekrlife.market.data.response.ProtocolInfoBean
import com.zeekrlife.net.api.NetUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    var mCategoryList = MutableLiveData<List<HomeItemCategoryBean>>()
    var tabs = mutableListOf("我的应用", "精品推荐", "影音娱乐", "高效办公", "某某分类", "某某分类", "某某分类", "我的小程序", "UI展示分类")
    var remoteUserAgreementBean: ProtocolInfoBean? = null
    var remoteProtocolInfoBean: ProtocolInfoBean? = null
    private var categoryList : MutableList<HomeItemCategoryBean>? = mutableListOf()

    var mDoStartUpdateWorker = MutableLiveData<Boolean?>()

    /**
     * 获取首页列表
     */
    @SuppressLint("LogNotTimber") fun getHomeList() {
        viewModelScope.launch(Dispatchers.Default) {
            //优先加载缓存
            var categoryListCache: String? = ""
            try {
                categoryListCache = CacheExt.getCategoryList()
                categoryList = PreloadDataUtils.getCategoryListCache()?.toMutableList()
                if(categoryList.isNullOrEmpty() && !categoryListCache.isNullOrEmpty()) {
                    categoryList = GsonUtils.fromJson<MutableList<HomeItemCategoryBean>>(
                        categoryListCache, object : TypeToken<MutableList<HomeItemCategoryBean>>() {}.type
                    )
                }
                categoryList?.apply {
                    Log.e("HomeViewModel", "load categoryListCache success")
                    mCategoryList.postValue(this)
                }
            } catch (e: Exception) {
                e.logStackTrace()
                Log.e("HomeViewModel", "load categoryListCache error")
            }

            rxHttpRequest {
                onRequest = {
                    mDoStartUpdateWorker.postValue(true)

                    val categoryList = UserRepository.getHomeList(CarManager.ScreenType.getValueByName("CSD")).await()

                    launch(Dispatchers.IO) {
                        val categoryListStr = GsonUtils.toJson(categoryList)
                        //分类数据一般情况变化不是很频繁，计算数据MD5
                        val categoryListStrMd5 = EncryptUtils.encryptMD5ToString(categoryListStr)
                        val categoryListCacheMd5 = CacheExt.getCategoryListMd5()
                        //缓存为空或者校验数据是否变化
                        if (categoryListCache.isNullOrEmpty() || categoryListStrMd5 != categoryListCacheMd5) {
                            Log.e("HomeViewModel", "categoryList change refresh")
                            mCategoryList.postValue(categoryList)
                            //添加到缓存
                            CacheExt.setCategoryList(categoryListStr)
                            CacheExt.setCategoryListMd5(categoryListStrMd5)
                            //更新预加里的载缓存
                            PreloadDataUtils.updateCategoryListPreloadCache(categoryList)
                        } else {
                            Log.e("HomeViewModel", "categoryList no change")
                        }
                    }
                }
                requestCode = NetUrl.HOME_CATEGORT_LIST

                onEmpty = {
                    mCategoryList.postValue(mutableListOf())
                }

                onError = {
//                    CacheExt.setCategoryList("")
//                    CacheExt.setCategoryListMd5("")
                    if(categoryList.isNullOrEmpty()) {
                        Log.e("HomeViewModel22", "load categoryListCache error")
                        mCategoryList.postValue(mutableListOf())
                    }
                }
            }
        }
    }

    fun mock() {
        val mHomeItemCategoryBeanList: MutableList<HomeItemCategoryBean> = arrayListOf()
        tabs.forEach {
            val mHomeItemBean = HomeItemCategoryBean()
            mHomeItemBean.categoryName = it
            mHomeItemCategoryBeanList.add(mHomeItemBean)
        }
        mCategoryList.value = mHomeItemCategoryBeanList
    }

    /**
     *  因启动页性能更佳 获取协议详情放在首页（下次启动生效）
     */
//    fun getProtocolInfo() {
//        rxHttpRequest {
//            onRequest = {
//                val remoteProtocolInfo = UserRepository.getProtocolInfo().await()
//                remoteProtocolInfo.apply {
//                    remoteUserAgreementBean = find { it.code == Constants.APPSTOREUA_BX1E }
//                    remoteProtocolInfoBean = find { it.code == Constants.APPSTOREPP_BX1E }
//
//                    remoteUserAgreementBean?.let {
//                        if ((CacheExt.getUserAgreement()?.version) != (it.version)) {
//                            it.needUpdate = true
//                            CacheExt.setUserAgreement(it)
//                        }
//                    }
//                    remoteProtocolInfoBean?.let {
//                        if ((CacheExt.getProtocol()?.version) != (it.version)) {
//                            it.needUpdate = true
//                            CacheExt.setProtocol(it)
//                        }
//                    }
//                }
//
//            }
//
//        }
//    }
}
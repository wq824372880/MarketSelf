package com.zeekrlife.market.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.zeekrlife.market.data.repository.UserRepository
import com.zeekrlife.net.api.ApiPagerResponse
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.common.net.EmptyException
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.market.ui.fragment.CategoryFragment
import com.zeekrlife.market.utils.CarManager
import com.zeekrlife.net.BaseNetConstant
import com.zeekrlife.net.load.LoadingType

class CategoryCXViewModel : BaseViewModel() {

    private var pageIndex = 1
    var listData = MutableLiveData<ApiPagerResponse<AppItemInfoBean>>()

    /**
     * 获取列表数据
     * @param isRefresh Boolean 是否是刷新
     * @param loadingXml Boolean 请求时是否需要展示界面加载中loading
     */
    fun getCategoryList(categoryPid: Int, isRefresh: Boolean, loadingXml: Boolean = false) {
        if (isRefresh) {
            pageIndex = 1
        }
        rxHttpRequest {
            onRequest = {
                val pagerResponse = UserRepository.getCategoryList(pageIndex, categoryPid,
                    CarManager.ScreenType.getValueByName("BACKREST")).await()
                //过滤不展示应用
                pagerResponse.apply { list = list?.filter { it.hideIcon == 0 }?.let { ArrayList(it) } }
                //如果返回值第一页并且空数据
                if (isRefresh && pagerResponse.list.isNullOrEmpty()) {
                    throw EmptyException(BaseNetConstant.EMPTY_CODE, "")
                } else {
                    listData.value = pagerResponse
                    //请求成功 页码+1
                    pageIndex++
                }
            }
            loadingType = if (loadingXml) LoadingType.LOADING_XML else LoadingType.LOADING_NULL
            requestCode = CategoryFragment.REQUEST_APP_LIST_CATEGORY_FRAGMENT
            isRefreshRequest = isRefresh
        }
    }

    /**
     * 监听到应用卸载，刷新列表
     * @param packageName 包名
     */
    fun getAppListPosition(packageName: String): Int {
        listData.value?.list?.forEachIndexed { index, app ->
            if (app.apkPackageName.equals(packageName)) {
                return index
            }
        }
        return -1
    }

    /**
     * 获取列表数据
     * @param isRefresh Boolean 是否是刷新
     * @param loadingXml Boolean 请求时是否需要展示界面加载中loading
     */
    fun mock(categoryPid: Int, isRefresh: Boolean, loadingXml: Boolean = false) {
        val mHomeItemAppBeanList: MutableList<AppItemInfoBean> = arrayListOf()
        for (i in 0 until 20) {
            val mHomeItemBean = AppItemInfoBean(
                categoryName = "极氪$i",
                apkName = "极氪$i",
                apkUrl = "https://zeekrlife-oss.zeekrlife.com/app/zeekr/release/com.zeekrlife.mobile-zeekrlife-release-v1.1.36-60-20220421-152503.apk"
            )
            mHomeItemAppBeanList.add(mHomeItemBean)
        }
        val mApiPagerResponse = ApiPagerResponse(0, list = mHomeItemAppBeanList as ArrayList, 20, 100)

        listData.value = mApiPagerResponse

    }

}
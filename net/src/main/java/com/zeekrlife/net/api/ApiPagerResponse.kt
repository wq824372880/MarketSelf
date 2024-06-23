package com.zeekrlife.net.api


/**
 * 描述　: 服务器返回的列表数据基类
 */
data class ApiPagerResponse<T>(

    var pageNum:Int,
    var list: ArrayList<T>?,
    var pageSize:Int,
    var total:Int
) : BasePage<T>() {

    override fun getPageData() = list
    override fun isRefresh() = pageNum == 1
    override fun isEmpty() = total <1
    override fun hasMore() =
        if(pageSize == 0 || total < 1){
            false
        }else{
            if(total % pageSize == 0) (total /pageSize) > pageNum  else  (total /pageSize + 1) > pageNum
        }
}





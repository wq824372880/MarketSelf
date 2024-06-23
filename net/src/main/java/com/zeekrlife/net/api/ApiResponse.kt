package com.zeekrlife.net.api

/**
 * 描述　:  服务器返回的数据基类
 */
data class ApiResponse<T>(
    /**
     * //    msg	String	业务信息/错误信息/异常信息
    //    msgCode	String	全局状态码
    //    <T>content	object	业务返回数据实体
    //    debug	object	调试数据
    //    --  traceId	String	全局链路唯一标识
    //    --  bizName	String	微服务名称
    //   {
    //    "msg": "服务器内部异常",
    //    "msgCode": "01A01",
    //    "content": null,
    //    "debug":
    //     {
    //    "traceId": "848ea6fda4544390b6f8f9a977f8d68a.107.16512156336490007",
    //    "bizName": "snc-media-user"
    //     }
    //  }
     */
    var msg: String,
    var code: String,
    var data: T,
    var debug: Debug? = null,
    var total: Int,
    var success: Boolean,
) {
    class Debug(val bizName: String? = "", val time: Long, val traceId: String? = "")
}
package com.zeekrlife.net

object BaseNetConstant {
    // 服务器请求成功的 Code值
    const val SUCCESS_CODE = "0"
    const val SUCCESS_STATUS = 200
    const val ERROR_CODE = "10000"
    const val EMPTY_CODE = "99999"

    /**
     * 连接超时时间
     */
    const val CONNECT_TIME_OUT = 5L

    /**
     * 读取超时时间
     */
    const val READ_TIME_OUT = 5L

    /**
     * 写入超时时间
     */
    const val WRITE_TIME_OUT = 5L
}

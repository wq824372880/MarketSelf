package com.zeekrlife.ampe.core.bean
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * {success=true,
 * code=0,
 * message='{
 *    "result":{
 *    "isLogin":true,
 *    "userAvatar":"http://tfs.alipayobjects.com/images/partner/T1YoNwXjFXXXXXXXXX",
 *    "userKey":"aeowhrTXwqP8Q15xq29u8uDLTjnt/AD4s3SkYRCtYUI=",
 *    "userNick":"你的钱就是我的钱",
 *    "userRoute":"aeowhrTXwqP8Q105"}
 *    }'
 * }
 */

@Parcelize
data class UserInfo(
    var result:Result? = null
): Parcelable

@Parcelize
data class Result(
    var isLogin: Boolean? = false,
    var userAvatar: String? = null,
    var userKey: String? = null,
    var userNick: String? = null,
    var userRoute: String? = null
): Parcelable
package com.zeekrlife.market.data.cache

import android.annotation.SuppressLint
import android.util.Log
import com.tencent.mmkv.MMKV
import com.zeekr.basic.appContext
import com.zeekrlife.market.app.ext.mmkv
import com.zeekrlife.market.app.ext.mmkvSave
import com.zeekrlife.market.data.ValueKey
import com.zeekrlife.market.data.response.AppletSignatureInfo
import com.zeekrlife.market.data.response.OpenApiInfo
import com.zeekrlife.market.data.response.ProtocolInfoBean
import com.zeekrlife.market.data.response.UserInfo

@SuppressLint("LogNotTimber")
object CacheExt {

    /**
     * 检查MMKV是否已经初始化，如果未初始化，则进行初始化。
     * 这个函数没有参数，也没有返回值。
     */
    private fun checkMMKVInitialize() {
        // 检查MMKV的根目录是否已经设置，如果未设置，则调用初始化函数
        if (MMKV.getRootDir() == null) {
            MMKV.initialize(appContext)
        }
    }

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        checkMMKVInitialize()
        return mmkv.decodeParcelable(
            ValueKey.USER_INFO,
            UserInfo::class.java
        ) != null
    }

    /**
     * 保存用户信息
     */
    fun setUserInfo(userInfo: UserInfo) {
        checkMMKVInitialize()
        mmkv.encode(ValueKey.USER_INFO, userInfo)
    }

    /**
     * 退出登陆
     */
    fun loginOut() {
        checkMMKVInitialize()
        mmkv.clear()
    }

    /**
     * 同意协议
     */
    fun setAgreementProtocol(): Boolean {
        checkMMKVInitialize()
        val agreementProtocolKey = ValueKey.USER_AGREEMENT_PROTOCOL + (getOpenApi()?.userInfo?.userId ?: "")
        Log.e("CacheExt", "setAgreementProtocol : $agreementProtocolKey true")
        return mmkv.encode(agreementProtocolKey, true)
    }

    /**
     * 是否同意过协议
     */
    fun isAgreementProtocol(): Boolean {
        checkMMKVInitialize()
        val agreementProtocolKey = ValueKey.USER_AGREEMENT_PROTOCOL + (getOpenApi()?.userInfo?.userId ?: "")
        val isAgreementProtocol = mmkv.getBoolean(agreementProtocolKey, false)
        Log.e("CacheExt", "isAgreementProtocol : key -> $agreementProtocolKey  $isAgreementProtocol")
        return isAgreementProtocol
    }

    /**
     * 设置OpenApi信息到MMKV存储中。
     *
     * @param openApiInfo OpenApi信息对象，如果为null，则不执行任何操作。
     */
    fun setOpenApi(openApiInfo: OpenApiInfo?) {
        openApiInfo?.let {
            // 检查MMKV是否已初始化，若未初始化则进行初始化
            checkMMKVInitialize()
            // 将OpenApi信息编码并存储到MMKV中
            mmkv.encode(ValueKey.OPENAPI_INFO, openApiInfo)
        }
    }

    /**
     * OpenApi
     */
    fun getOpenApi(): OpenApiInfo? {
        checkMMKVInitialize()
        return mmkv.decodeParcelable(ValueKey.OPENAPI_INFO, OpenApiInfo::class.java)
    }

    /**
     * 设置用户协议信息。
     * 该函数将指定的用户协议信息存储到MMKV中。
     *
     * @param protocolInfoBean 协议信息对象。可为null，如果为null，则不进行存储操作。
     */
    fun setUserAgreement(protocolInfoBean: ProtocolInfoBean?) {
        protocolInfoBean?.let {
            // 检查MMKV是否已初始化
            checkMMKVInitialize()
            // 将协议信息存储到MMKV中，键名由USER_AGREEMENT_INFO和用户ID组成
            mmkv.encode(
                ValueKey.USER_AGREEMENT_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
                protocolInfoBean
            )
        }
    }

    /**
     * 设置协议信息。
     * 该函数用于将协议信息存储到MMKV中。在存储之前，会首先检查MMKV是否已经初始化。
     *
     * @param protocolInfoBean 协议信息对象。这是一个可空参数，如果为null，则不会进行存储操作。
     */
    fun setProtocol(protocolInfoBean: ProtocolInfoBean?) {
        protocolInfoBean?.let {
            // 检查MMKV是否已初始化
            checkMMKVInitialize()
            // 将协议信息存储到MMKV中，存储的键由LAUNCHER_PROTOCOL_INFO和用户ID拼接而成
            mmkv.encode(
                ValueKey.LAUNCHER_PROTOCOL_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
                protocolInfoBean
            )
        }
    }

    /**
     * 用户协议
     */
    fun getUserAgreement(): ProtocolInfoBean? {
        checkMMKVInitialize()
        return mmkv.decodeParcelable(
            ValueKey.USER_AGREEMENT_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
            ProtocolInfoBean::class.java
        )
    }

    /**
     * 隐私信息
     */
    fun getProtocol(): ProtocolInfoBean? {
        checkMMKVInitialize()
        return mmkv.decodeParcelable(
            ValueKey.LAUNCHER_PROTOCOL_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
            ProtocolInfoBean::class.java
        )
    }

    /**
     * 小程序注册信息
     */
    fun setAppletDeviceSignature(signatureInfo: AppletSignatureInfo) {
        checkMMKVInitialize()
        mmkvSave.encode(ValueKey.APPLET_DEVICE_SIGNATURE, signatureInfo)
    }

    /**
     * 获取应用小程序设备签名信息。
     * 该函数没有参数。
     * @return [AppletSignatureInfo?] 返回一个应用小程序签名信息的对象，如果不存在则返回null。
     */
    fun getAppletDeviceSignature(): AppletSignatureInfo? {
        checkMMKVInitialize()
        return mmkvSave.decodeParcelable(ValueKey.APPLET_DEVICE_SIGNATURE, AppletSignatureInfo::class.java)
    }

    /**
     * 小程序上架列表
     */
    fun setAppletLegalSet(legalSet: Set<String>) {
        checkMMKVInitialize()
        mmkvSave.encode(ValueKey.APPLET_LEGAL_LIST, legalSet)
    }

    /**
     * 获取应用合法权限集。
     * 该函数没有参数。
     * @return 返回一个字符串集，包含应用的所有合法权限。如果无法获取或没有权限数据，则返回null。
     */
    fun getAppletLegalSet(): Set<String>? {
        // 检查MMKV是否已经初始化
        checkMMKVInitialize()
        // 从MMKV中解码并返回应用合法权限列表
        return mmkvSave.decodeStringSet(ValueKey.APPLET_LEGAL_LIST)
    }

    /**
     * 获取Dhu环境变量的值。
     * 该函数首先会检查MMKV是否已经初始化，然后从MMKV中解码并返回与`ValueKey.CACHE_KEY_DHU_ENV`关联的字符串值。
     * 如果找不到该值，函数会返回空字符串。
     *
     * @return [String?] 返回从MMKV中读取的Dhu环境变量的值，如果不存在则返回null。
     */
    fun getDhuEnv(): String? {
        checkMMKVInitialize()
        return mmkvSave.decodeString(ValueKey.CACHE_KEY_DHU_ENV,"")
    }

    /**
     * 设置Dhu环境变量。
     * 该函数首先会检查MMKV是否已经初始化，然后将指定的环境变量值保存到缓存中。
     *
     * @param dhuEnv 指定的Dhu环境变量值。如果为null，则保存一个空字符串。
     */
    fun setDhuEnv(dhuEnv: String?) {
        checkMMKVInitialize()
        mmkvSave.encode(ValueKey.CACHE_KEY_DHU_ENV, dhuEnv ?: "")
    }

    /**
     * 获取分类列表的字符串表示。
     * 该方法首先会检查MMKV是否已经初始化，然后尝试从MMKV中解码并返回存储的分类列表。
     * 如果没有找到存储的分类列表，则返回空字符串。
     *
     * @return [String?] 返回分类列表的字符串表示，如果不存在则返回null。
     */
    fun getCategoryList(): String? {
        checkMMKVInitialize()
        return mmkvSave.decodeString(ValueKey.CACHE_KEY_CATEGORY_LIST,"")
    }

    /**
     * 设置分类列表到MMKV缓存中。
     *
     * @param categoryList 分类列表，类型为String。如果为null，则默认存储为空字符串。
     * 该函数没有返回值。
     */
    fun setCategoryList(categoryList: String?) {
        // 首先检查MMKV是否已经初始化
        checkMMKVInitialize()
        // 使用mmkv保存分类列表，如果列表为null，则保存为空字符串
        mmkvSave.encode(ValueKey.CACHE_KEY_CATEGORY_LIST, categoryList ?: "")
    }

    /**
     * 获取分类列表的MD5值。
     * 该函数用于从MMKV存储中检索分类列表的MD5字符串，如果不存在，则返回空字符串。
     *
     * @return [String?] 返回分类列表MD5字符串，如果未找到则返回null。
     */
    fun getCategoryListMd5(): String? {
        checkMMKVInitialize()
        return mmkvSave.decodeString(ValueKey.CACHE_KEY_CATEGORY_LIST_MD5,"")
    }

    /**
     * 设置分类列表的MD5值到MMKV存储中。
     * 这个方法用于保存或更新应用中分类列表的MD5缓存值。
     * 如果传入的MD5值为null，则保存一个空字符串。
     *
     * @param categoryListMd5 分类列表的MD5字符串。如果为null，则保存一个空字符串。
     */
    fun setCategoryListMd5(categoryListMd5: String?) {
        checkMMKVInitialize()
        mmkvSave.encode(ValueKey.CACHE_KEY_CATEGORY_LIST_MD5, categoryListMd5 ?: "")
    }


    /**
     * 获取推荐应用列表的缓存数据。
     * 该函数没有参数。
     * @return 返回推荐应用列表的字符串形式，如果缓存中不存在则返回空字符串。
     */
    fun getRecommendList() : String?{
        // 检查MMKV是否已经初始化
        checkMMKVInitialize()
        // 从MMKV中解码并返回推荐应用列表的缓存数据，如果不存在则默认返回空字符串
        return mmkvSave.decodeString(ValueKey.CACHE_KEY_RECOMMEND_APP_LIST,"")
    }

    /**
     * 获取推荐应用列表的缓存数据。
     * 该函数没有参数。
     * @return 返回推荐应用列表的字符串形式，如果缓存中不存在则返回空字符串。
     */
    fun getRecommendCXList() : String?{
        // 检查MMKV是否已经初始化
        checkMMKVInitialize()
        // 从MMKV中解码并返回推荐应用列表的缓存数据，如果不存在则默认返回空字符串
        return mmkvSave.decodeString(ValueKey.CACHE_KEY_RECOMMEND_APP_CX_LIST,"")
    }

    /**
     * 设置推荐应用列表到MMKV缓存中。
     *
     * @param recommendList 推荐应用列表，类型为String。如果为null，则默认存储为空字符串。
     * 此函数不返回任何值。
     */
    fun setRecommendList(recommendList: String?) {
        // 检查MMKV是否已经初始化
        checkMMKVInitialize()
        // 使用mmkv保存推荐列表，如果列表为null，则保存为空字符串
        mmkvSave.encode(ValueKey.CACHE_KEY_RECOMMEND_APP_LIST, recommendList ?: "")
    }

    /**
     * 设置推荐应用列表到MMKV缓存中。
     *
     * @param recommendList 推荐应用列表，类型为String。如果为null，则默认存储为空字符串。
     * 此函数不返回任何值。
     */
    fun setRecommendCXList(recommendList: String?) {
        // 检查MMKV是否已经初始化
        checkMMKVInitialize()
        // 使用mmkv保存推荐列表，如果列表为null，则保存为空字符串
        mmkvSave.encode(ValueKey.CACHE_KEY_RECOMMEND_APP_CX_LIST, recommendList ?: "")
    }

    /**
     * 获取推荐应用列表的MD5值。
     * 该函数用于从MMKV存储中检索推荐应用列表的MD5字符串。如果该值不存在，则返回空字符串。
     *
     * @return [String?] 返回推荐应用列表MD5值的字符串，如果不存在则返回null。
     */
    fun getRecommendListMd5() : String?{
        checkMMKVInitialize()
        return mmkvSave.decodeString(ValueKey.CACHE_KEY_RECOMMEND_APP_LIST_MD5,"")
    }

    /**
     * 设置推荐应用列表的MD5值到MMKV存储中。
     * 该函数用于保存推荐应用列表的MD5字符串，以便后续校验推荐列表是否更新。
     *
     * @param recommendListMd5 推荐应用列表的MD5字符串。如果为null，则保存一个空字符串。
     */
    fun setRecommendListMd5(recommendListMd5: String?) {
        // 检查MMKV是否已经初始化
        checkMMKVInitialize()
        // 使用MMKV保存推荐列表MD5值，如果传入null，则保存为空字符串
        mmkvSave.encode(ValueKey.CACHE_KEY_RECOMMEND_APP_LIST_MD5, recommendListMd5 ?: "")
    }

}
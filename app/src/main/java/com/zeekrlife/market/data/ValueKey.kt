package com.zeekrlife.market.data

/**
 * 描述　:
 */
object ValueKey {

    //mmkv全局缓存key(退出登录清除缓存)
    const val MMKV_APP_KEY = "market_app"
    //mmkv全局缓存key(退出登录不清除缓存)
    const val MMKV_APP_KEY_SAVE = "market_app_save"

    //用户协议
    const val USER_AGREEMENT_INFO = "user_agreement_info"
    //隐私信息
    const val LAUNCHER_PROTOCOL_INFO = "launcher_protocol_info"
    //同意协议
    const val USER_AGREEMENT_PROTOCOL = "user_agreement_protocol"
    //用户协议富文本
    const val USER_AGREEMENT_RICHTEXT = "user_agreement_richtext"
    //隐私信息富文本
    const val LAUNCHER_PROTOCOL_RICHTEXT = "launcher_protocol_richtext"

    //openAPi数据( 通过mmkv缓存 不用在异步去获取了)
    const val OPENAPI_INFO = "openapi_info"

    //当前用户是否显示过隐私弹窗
    const val IS_SHOWED_PRIVACY = "is_showed_privacy"

    const val USER_INFO = "user_info"

    //我的应用中已安装应用
    const val CACHE_MY_INSTALL_APPS = "MY_INSTALL_APPS"

    //ampe相关
    const val productId = 4806052L  //产品id
    const val deviceId = "zeekrlife1234567"      //设备id
    const val hostAppId = "2021003125642077"  //移动应用appid
    const val signature =
        "JyRk7JprnbD7TACuGKbaTDQvE1ECANiX4gawlXfN+iq18vXnLBt+GZ1U/4HRP96MbiheJWiXiiTJa2QI3JQp9F7lf1jKyW8e93R2ARq4dsoXxuKlDfVWpz3Louc8Ip6xrM/ylFjNVYC75Wso0vPJ/9pw0xxm6btK/Pj6Sr1DU7MU21KjvArOHCVjfelqlW615kGP1Hv4tVdA8pjhR6htbfgGNosLrwctnMwb/y3ddjL1aBuX1XXdhHrzI+/ERisDTUo9NlhV0dxvetBcAIhcO8kyl4DcMbHHloUJO62CE6HpS/FDGiG7dHZyFJPhwYVALSpbuGluVbfE2natX+sc7Q=="; //签名文件

    //    const val signature = "SaH53D1Woh+P1bSnzGW/NAzVaI/ja1OjIgxh9X7ogyeZ+XeILuHFg9fAc/SNXWlLjoX//rG0QelspLcoNJW7EdKtITjmfNb4s1emab37D+6eMwYcDZVeww7gpBFrL+PbsgdSy/1TRaT+u4erbsJ8k6gLb+1RZWDQHy9uo7hXx2ZEmiLZXJqVUPVus1LJHc+qL4unhXDTZCY2VSTYpgymnoFgMbXT8fOKp1AjGciNbtYNkf7arBRz4BgOyXJCdoanbe0wG5GSj4oqV543SzpBRIPnSuMSd0TVsKfmlMaL7ZKN+emAW94pyf5WcSGU0jvMBi3cFschijkJvbt3pEXw9Q=="

    //每页请求数量
    const val REQUEST_PAGE_SIZE = 30

    //小程序设备注册
    const val APPLET_DEVICE_SIGNATURE = "applet_device_signature"

    //小程序上架的列表
    const val APPLET_LEGAL_LIST = "applet_legal_list"

    //小程序快捷图标列表
    const val APPLET_SHORTCUT_LIST = "applet_shortcut_list"

    //DHU缓存的上一次环境
    const val CACHE_KEY_DHU_ENV = "KEY_DHU_ENV"

    //分类列表缓存
    const val CACHE_KEY_CATEGORY_LIST = "KEY_CATEGORY_LIST"

    //分类列表缓存MD5
    const val CACHE_KEY_CATEGORY_LIST_MD5 = "KEY_CATEGORY_LIST_MD5"

    //推荐列表缓存
    const val CACHE_KEY_RECOMMEND_APP_LIST = "KEY_RECOMMEND_APP_LIST"

    //推荐列表缓存CX
    const val CACHE_KEY_RECOMMEND_APP_CX_LIST = "KEY_RECOMMEND_APP_CX_LIST"

    //推荐列表缓存MD5
    const val CACHE_KEY_RECOMMEND_APP_LIST_MD5 = "KEY_RECOMMEND_APP_LIST_MD5"
}
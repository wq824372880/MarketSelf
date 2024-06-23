package com.zeekrlife.market.data;

/**
 * 描述: App常量类
 */
public class Constants {
    /**
     * 用户协议
     */
    public final static String USER_PRIVACY = "https://baidu.com";
    /**
     * 隐私政策
     */
    public final static String SECRET_PRIVACY = "https://baidu.com";

    public final static String URL_KEY = "url_key";
    public final static String URL_KEY_TITLE = "url_key_title";

    //协议编码  用户协议   隐私政策
    public final static String APPSTOREUA_BX1E = "AppStoreUA-BX1E";
    public final static String APPSTOREPP_BX1E = "AppStorePP-BX1E";
    public final static String HOME_TAB_INDEX = "home_tab_index";

    public final static String HOME_IS_RELOAD = "home_is_reload";

    /**
     * 广告位编码:Banner
     */
    public final static String APPSTORE_RECOMMEND_BANNER = "APPSTORE_REC_BANNER";

    /**
     * 广告位编码:广告位
     */
    public final static String APPSTORE_RECOMMEND_ADSENSE = "APPSTORE_REC_ADSENSE";

    /**
     * 广告位编码：推荐列表
     */
    public final static String APPSTORE_RECOMMEND_APP_LIST = "APPSTORE_REC_APP_LIST";

    /**
     * 点位集合
     */
    public final static String[] APPSTORE_ADV_DOTS = new String[] {
        APPSTORE_RECOMMEND_BANNER, APPSTORE_RECOMMEND_ADSENSE, APPSTORE_RECOMMEND_APP_LIST
    };

    /*------------------------安装校验服务所需参数key常量--------------------------------*/
    /**
     * 安装校验服务Package
     */
    public static final String INSTALL_VERIFIER_PACKAGE = "com.ecarx.xsfinstallverifier";

    /**
     * 安装校验服务Action
     */
    public static final String INSTALL_VERIFIER_ACTION = "ecarx.install.verify";

    /**
     * 导入签名CMD
     */
    public static final String INSTALL_VERIFIER_CMD_KEY = "VERIFY_CMD";

    /**
     * 导入签名CMD value
     */
    public static final int INSTALL_VERIFIER_CMD_IMPORT_RECORD = 3;

    /**
     * 包名
     */
    public static final String EXTAR_PKG_NAME = "PACKAGE_NAME";

    /**
     * versionCode
     */
    public static final String EXTAR_PKG_VERSION_CODE = "PKG_VERSION_CODE";

    /**
     * apk md5
     */
    public static final String EXTAR_APK_MD5 = "APK_MD5";

    /**
     * apk md5
     */
    public static final String EXTAR_APK_SIGN = "APK_SIGN";

    /**
     * sign
     */
    public static final String EXTAR_SIGN_TEXT = "SIGN_TEXT";

    /**
     * signType
     */
    public static final String EXTAR_SIGN_TYPE = "SIGN_TYPE";

    /*-------------------------------------------------------------------------------*/
}

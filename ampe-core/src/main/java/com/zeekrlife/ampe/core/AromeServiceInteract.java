package com.zeekrlife.ampe.core;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alipay.arome.aromecli.AromeInit;
import com.alipay.arome.aromecli.AromeInitOptions;
import com.alipay.arome.aromecli.AromeServiceInvoker;
import com.alipay.arome.aromecli.AromeServiceTask;
import com.alipay.arome.aromecli.requst.AromeActivateRequest;
import com.alipay.arome.aromecli.requst.AromeBatchPreloadAppRequest;
import com.alipay.arome.aromecli.requst.AromeExitAppRequest;
import com.alipay.arome.aromecli.requst.AromeExtendBridgeRequest;
import com.alipay.arome.aromecli.requst.AromeGetAppStatusRequest;
import com.alipay.arome.aromecli.requst.AromeGetUserInfoRequest;
import com.alipay.arome.aromecli.requst.AromeLaunchAppRequest;
import com.alipay.arome.aromecli.requst.AromeLaunchCustomServiceRequest;
import com.alipay.arome.aromecli.requst.AromeLaunchMiniServiceRequest;
import com.alipay.arome.aromecli.requst.AromeLoginRequest;
import com.alipay.arome.aromecli.requst.AromeLogoutRequest;
import com.alipay.arome.aromecli.requst.AromePreloadAppRequest;
import com.alipay.arome.aromecli.requst.AromeSendEventRequest;
import com.alipay.arome.aromecli.requst.AromeUploadLogRequest;
import com.alipay.arome.aromecli.response.AromeActivateResponse;
import com.alipay.arome.aromecli.response.AromeExitAppResponse;
import com.alipay.arome.aromecli.response.AromeGetAppStatusResponse;
import com.alipay.arome.aromecli.response.AromeGetUserInfoResponse;
import com.alipay.arome.aromecli.response.AromeLaunchAppResponse;
import com.alipay.arome.aromecli.response.AromeLoginResponse;
import com.alipay.arome.aromecli.response.AromeLogoutResponse;
import com.alipay.arome.aromecli.response.AromePreloadAppResponse;
import com.alipay.arome.aromecli.response.AromeResponse;
import com.google.gson.Gson;
import com.zeekr.car.api.NaviApiManager;
import com.zeekr.sdk.navi.bean.LatLng;
import com.zeekr.sdk.navi.bean.NaviBaseModel;
import com.zeekr.sdk.navi.bean.NaviErrorModel;
import com.zeekr.sdk.navi.bean.PoiInfo;
import com.zeekr.sdk.navi.bean.RoutePlanStrategy;
import com.zeekr.sdk.navi.bean.client.NaviRoutePlan;
import com.zeekr.sdk.navi.callback.INaviAPICallback;
import com.zeekrlife.ampe.core.bean.BridgeCallLocation;
import com.zeekrlife.ampe.core.bean.UserInfo;
import com.zeekrlife.ampe.core.listener.AbstractCommonListener;
import com.zeekrlife.common.ext.DensityExtKt;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil;
import com.zeekrlife.net.interception.logging.util.LogExtKt;
import com.zeekrlife.net.interception.logging.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

/**
 * 支付宝AMPE功能集合
 * 注：lambda表达式不能使用 支付宝sdk类型强转会导致throw
 */
public class AromeServiceInteract {
    //ampe相关

    String TAG = "zzzArome";
    //产品id
    public static final long PRODUCT_ID = 4806052L;
    //移动应用appid
    public static final String HOST_APP_ID = "2021003125642077";
    /**
     * logo汉堡王小程序
     * 2018121362506702	未锁定	-	1.10.569
     * 解除绑定版本设置添加子服务
     * 2
     * logoETCP停车
     * 2021001144643377	未锁定	-	0.0.428
     * 解除绑定版本设置
     * 3
     * logo飞猪订酒店机票火车票汽车票门票
     * 2018081461095002	未锁定	-	0.1.133
     * 解除绑定版本设置添加子服务
     * 4
     * logo淘鲜达
     * 2019052465396197	未锁定	-	0.1.136
     * 解除绑定版本设置添加子服务
     * 5
     * logo鲜花速递全国送花订花-花礼网
     * 2018091461363491	未锁定	-	2.1.3
     * 解除绑定版本设置添加子服务
     * 6
     * logo车行易查违章
     * 2017103009621199	未锁定	-	5.7.3
     * 解除绑定版本设置
     * 7
     * logo航旅纵横
     * 2018111662149799	未锁定	-	0.0.147
     * 解除绑定版本设置添加子服务
     * 8
     * logo口碑找好店
     * 2021001115677268	未锁定	-	无
     * 解除绑定
     * 9
     * logo电影演出
     * 2021001110648550	未锁定	-	0.1.1345
     * 解除绑定版本设置添加子服务
     * 10
     * logo支车车车生活
     * 2021002163620647
     */

    //  车生活 小程序id
    public static final String CAR_LIFE_APPLET_ID = "2021002163620647";

    /**
     * 初始化
     *
     * @param deviceId
     * @param signature
     * @param listener
     */
    public void AromeServiceInit(String deviceId, String signature, AbstractCommonListener listener) {
        Bundle mThemeConfig = new Bundle();
        Bundle mDeviceConfig = new Bundle();

        int[] rect;
        if (DensityExtKt.getScreenWidthIs2560()) {
            rect = new int[]{1700, 106, 2480, 1440};
        } else if (DensityExtKt.getScreenWidthIs3200()) {
            rect = new int[]{2136, 106, 3080, 1760};
        } else {
            rect = new int[]{50, 58, 792, 906};
        }

        Bundle portrait = new Bundle();
        portrait.putInt("showType", 2);
        portrait.putInt("launchWidth", 750);

        mThemeConfig.putBoolean("hideNavigationBar", false);
        mThemeConfig.putBoolean("hideOptionMenu", false);
        mThemeConfig.putBoolean("hideStatusBar", false);
        mThemeConfig.putBoolean("fitHideStatusBar", true);
        mThemeConfig.putBoolean("enableFloatWindow", true);
        mThemeConfig.putIntArray("windowBounds", rect);
        mThemeConfig.putBoolean("originalConfiguration", true);
        mThemeConfig.putBoolean("recentCloseAllApp", true);
        mThemeConfig.putInt("openThemeSwitch", 1);

        mThemeConfig.putBundle("portrait", portrait);
//        ArrayList<String> strings = new ArrayList<>();
//        strings.add(carLifeAppletId);//车生活
//        mThemeConfig.putStringArrayList("floatWindowBlacklist", strings);
        mDeviceConfig.putString("packageName", "com.zeekrlife.market");
        mDeviceConfig.putInt("resetDpi", 320);


        AromeActivateRequest activateRequest = new AromeActivateRequest();
        activateRequest.hostAppId = HOST_APP_ID;
        activateRequest.productId = PRODUCT_ID;
        activateRequest.deviceId = deviceId;
        activateRequest.signature = signature;
        activateRequest.isFinishActivityOnBackground = true;


        AromeInit.initAndActivate(
                new AromeInitOptions.Builder().loginMode(1).hardwareType(1).hardwareName("ZEEKR X").deviceConfig(mDeviceConfig).themeConfig(mThemeConfig).build(),
                activateRequest,
                new AromeInit.Callback() {
                    @Override
                    public void postInit(boolean success, int errorCode, String errorMsg) {
                        Log.e(TAG, "InitAndActivate****+ success:" + success + "errorCode:" + errorMsg);
                        AromeActivateResponse response = new AromeActivateResponse();
                        response.code = errorCode;
                        response.success = success;
                        response.message = errorMsg;
                        listener.init(response);
//                        if (response.success) {
//                            AromeServiceGetUserInfo(new AbstractCommonListener() {
//                            });
//                        }
                    }

                    @Override
                    public void serverDied() {
                        LogUtils.Companion.e("AromeServiceInteract", "serverDied");
                    }
                }

        );
    }

    /**
     * AromeServiceActivate  别问 问就是代码扫描必须要求注释
     *
     * @param DeviceId
     * @param Signature
     * @param listener
     */
    private void AromeServiceActivate(String DeviceId, String Signature, AbstractCommonListener listener) {
        //initHandler();
        AromeActivateRequest request = new AromeActivateRequest();
        request.hostAppId = HOST_APP_ID;
        request.productId = PRODUCT_ID;
        request.deviceId = DeviceId;
        request.signature = Signature;
        request.isFinishActivityOnBackground = true;

        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeActivateResponse>() {
            @Override
            public void onCallback(AromeActivateResponse response) {
                Log.e(TAG, "Activate**** success:" + response.success + "code:" + response.code + ",message:" + response.message);
                listener.init(response);
                if (response.success) {
                    AromeServiceGetUserInfo(new AbstractCommonListener() {

                    });
                }
            }
        });
    }


    /**
     * 登录
     * /**
     * * AromeServiceActivate  别问 问就是代码扫描必须要求注释
     * * @param DeviceId
     * * @param Signature
     * * @param listener
     */
    public void AromeServiceLogin(AbstractCommonListener listener) {
        AromeLoginRequest requestLogin = new AromeLoginRequest();
        AromeServiceInvoker.invoke(requestLogin, new AromeServiceTask.Callback<AromeLoginResponse>() {
            @Override
            public void onCallback(AromeLoginResponse response) {
                //mMainHandler.post(() -> ToastUtils.show("Login****+" + response.toString()));
                listener.login(response);
                AromeServiceGetUserInfo(new AbstractCommonListener() {
                });
            }
        });
    }

    /**
     * 登出
     */
    public void AromeServiceLoginOut(AbstractCommonListener listener) {
        AromeLogoutRequest requestLoginOut = new AromeLogoutRequest();
        AromeServiceInvoker.invoke(requestLoginOut, new AromeServiceTask.Callback<AromeLogoutResponse>() {
            @Override
            public void onCallback(AromeLogoutResponse response) {
                //mMainHandler.post(() -> ToastUtils.show("LoginOut****+" + response.toString()));
                listener.logout(response);
            }
        });
    }

    /**
     * 启动小程序（非全屏）
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void AromeServiceLauncher(String appletId, AbstractCommonListener listener) {
//        AromeServiceExitApp();

        AromeLaunchAppRequest request = new AromeLaunchAppRequest();
        request.appId = appletId;
        request.closeAllApp = true;
//        if(CAR_LIFE_APPLET_ID.contentEquals(appletId)){
            request.query = "openThemeSwitch=1";//主题模式
//        }
//        request.themeConfig = new Bundle();
//        request.themeConfig.putBoolean("hideNavigationBar", false);
//        request.themeConfig.putBoolean("hideOptionMenu", false);
//        request.themeConfig.putBoolean("hideStatusBar", false);

        ThreadPoolUtil.runOnUiThread(() -> {
            AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeLaunchAppResponse>() {
                @Override
                public void onCallback(AromeLaunchAppResponse response) {
                    Log.e(TAG, "AromeLaunchApp response.success:" + response.success + ",message:" + response.message);
                    listener.launcher(response);
                    if (AppletServiceImpl.userInfo != null && AppletServiceImpl.userInfo.getResult() != null) {
                        return;
                    }
//                    AromeServiceGetUserInfo(new AbstractCommonListener() {
//                    });
                }
            });
        }, 0);

    }

    /**
     * 退出所有小程序
     */
    public void AromeServiceExitApp() {
        AromeExitAppRequest request = new AromeExitAppRequest();
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeExitAppResponse>() {
            @Override
            public void onCallback(AromeExitAppResponse response) {
            }
        });
    }

    /**
     * 启动小程序（全屏）
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void AromeServiceLauncherWithFullScreen(String appletId, AbstractCommonListener listener) {
//        AromeLaunchAppRequest request = new AromeLaunchAppRequest();
//        request.appId = appletId;
//        request.themeConfig = new Bundle();
//        Bundle landscape = new Bundle();
//        landscape.putInt("showType", 2);
//        landscape.putInt("launchWidth", 750);
//        request.themeConfig.putBundle("landscape", landscape);
////        request.themeConfig.putString("screenOrientation", "landscape");
//        request.themeConfig.putBoolean("saveToRecent", false);
//        request.closeAllApp = true;
//
//        AromeExitAppRequest request2 = new AromeExitAppRequest();
//        AromeServiceInvoker.invoke(request2, new AromeServiceTask.Callback<AromeExitAppResponse>() {
//            @Override
//            public void onCallback(final AromeExitAppResponse response) {
//                if(response.success){
//                    //启动小程序
//                    AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeLaunchAppResponse>() {
//                        @Override
//                        public void onCallback(AromeLaunchAppResponse response) {
//                            listener.launcher(response);
//                        }
//                    });
//                }
//
//
//            }
//        });

    }

    /**
     * 预加载
     */
    public void AromeServicePreloadApp(String miniAppId, AbstractCommonListener listener) {
        AromePreloadAppRequest request = new AromePreloadAppRequest();
        request.appId = miniAppId;
        request.loadToMemory = false;
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromePreloadAppResponse>() {
            @Override
            public void onCallback(AromePreloadAppResponse response) {
                listener.preloadApp(response);
            }
        });
    }

    /**
     * 批量预加载
     * 包含所有预加载的小程序AppId Set，最多 10 个
     */
    public void AromeServiceBatchPreloadApp(Set<String> appIdSet, AbstractCommonListener listener) {
        AromeBatchPreloadAppRequest request = new AromeBatchPreloadAppRequest();
        request.appIds = appIdSet;
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromePreloadAppResponse>() {
            @Override
            public void onCallback(AromePreloadAppResponse response) {
                //mMainHandler.post(() -> ToastUtils.show("BatchPreloadApp****+" + response.toString()));
                listener.batchPreloadApp(response);
            }
        });
    }

    /**
     * 获取用户登录信息
     */
    public void AromeServiceGetUserInfo(AbstractCommonListener listener) {
        AromeGetUserInfoRequest request = new AromeGetUserInfoRequest();
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeGetUserInfoResponse>() {
            @Override
            public void onCallback(AromeGetUserInfoResponse response) {
                //mMainHandler.post(() -> ToastUtils.show("GetUserInfo****+" + response.toString()));
                listener.getUserInfo(response);
                if (response.success) {
                    AppletServiceImpl.userInfo = GsonUtils.fromJson(response.message, UserInfo.class);
                    LogExtKt.logE(" AppletServiceImpl.userInfo:" + GsonUtils.toJson(AppletServiceImpl.userInfo), TAG);
                } else {
                    LogExtKt.logE("AromeServiceGetUserInfo success:false,message:" + response.message, TAG);
                }
            }
        });
    }

    /**
     * 获取小程序运行状态
     * success true / false
     * code状态码
     * message状态信息
     * isRunning 小程序是否正在运行 true/false
     * isForeground 小程序是否在前台 true/false
     */
    public void AromeServiceGetAppStatus(String miniAppId, AbstractCommonListener listener) {
        AromeGetAppStatusRequest request = new AromeGetAppStatusRequest();
        request.appId = miniAppId;
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeGetAppStatusResponse>() {
            @Override
            public void onCallback(AromeGetAppStatusResponse response) {
                //mMainHandler.post(() -> ToastUtils.show("GetAppStatus****+" + response.toString()));
                listener.getAppStatus(response);
            }
        });
    }

    /**
     * 获取小程序运行状态
     * 日志开始时间，与 mEndDate 成对出现。可不传入，则上传最近一天内的日志。支持的日志格式：
     * "yyyy-MM-dd"、"yyyy-MM-dd-HH"、"yyyy-MM-dd HH:mm:ss"、"yyyy-MM-dd HH:mm:ss:SSS" 。
     */
    public void AromeServiceUploadLog(String startDate, String endDate, AbstractCommonListener listener) {
        AromeUploadLogRequest request = new AromeUploadLogRequest();
        request.mStartDate = startDate;
        request.mEndDate = endDate;
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeResponse>() {
            @Override
            public void onCallback(AromeResponse response) {
                //mMainHandler.post(() -> ToastUtils.show("UploadLog****+" + response.toString()));
                listener.uploadLog(response);
            }
        });
    }

    /**
     * 启动小程序子服务
     * miniServiceCode  子服务码，由开放平台服务端下发。
     * launchWidth  小程序主体显示宽度（可选), 取值范围 [0,750]，默认值 750, 此时小程序主体宽度与屏幕宽度一致；375 则为半屏宽度，等比类推；开发者可根据需要自行传入设置。
     * themeConfig  主题配置参数（配合 initOptions 初始化时的 themeConfig，具体子参数见 3.1 启动 > themeConfig 参数）
     * closeAllApp 启动前是否关闭所有正在运行的小程序，默认 false 不关闭（可选）
     */
    public void AromeServiceLauncherMiniService(String miniServiceCode, AbstractCommonListener listener) {
        AromeLaunchMiniServiceRequest request = new AromeLaunchMiniServiceRequest();
        request.miniServiceCode = miniServiceCode;
        request.closeAllApp = true;
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeResponse>() {
            @Override
            public void onCallback(AromeResponse response) {
                //mMainHandler.post(() -> ToastUtils.show("LauncherMiniService****+" + response.toString()));
                listener.launcherMiniService(response);
            }
        });
    }

    /**
     * 启动情景智能服务
     * 有别于子服务，情景智能服务是用户维度的服务推荐，而子服务是小程序维度的服务推荐
     * <p>
     * mServiceCode 情景智能服务码，由开放平台服务端下发。
     * mUserIdentity 关联的用户唯一标识。
     */
    public void AromeServiceLaunchCustomService(String customServiceCode, String userIdentity,
                                                AbstractCommonListener listener) {

        if (AppletServiceImpl.userInfo.getResult() == null || AppletServiceImpl.userInfo.getResult().getUserKey() == null) {
            AromeServiceLogin(new AbstractCommonListener() {
                @Override
                public void login(AromeResponse response) {
                    AromeLaunchCustomServiceRequest request = new AromeLaunchCustomServiceRequest();
                    request.mServiceCode = customServiceCode;
                    request.mUserIdentity = AppletServiceImpl.userInfo.getResult().getUserKey();
                    request.closeAllApp = true;
                    AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeResponse>() {
                        @Override
                        public void onCallback(AromeResponse response) {
                            //mMainHandler.post(() -> ToastUtils.show("LaunchCustomService****+" + response.toString()));
                            LogExtKt.logE("AromeServiceLaunchCustomService " +
                                    "AppletServiceImpl.userInfo.getResult() == null," +
                                    "success:false,message:" + response.message, TAG);
                            listener.launchCustomService(response);
                        }
                    });
                }
            });
        } else {
            AromeLaunchCustomServiceRequest request = new AromeLaunchCustomServiceRequest();
            request.mServiceCode = customServiceCode;
            request.mUserIdentity = AppletServiceImpl.userInfo.getResult().getUserKey();
            request.closeAllApp = true;
            AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeResponse>() {
                @Override
                public void onCallback(AromeResponse response) {
                    //mMainHandler.post(() -> ToastUtils.show("LaunchCustomService****+" + response.toString()));
                    LogExtKt.logE("AromeServiceLaunchCustomService " +
                            "AppletServiceImpl.userInfo.getResult() != null,success:false,message:"
                            + response.message, TAG);
                    listener.launchCustomService(response);
                }
            });
        }



    }

    /**
     * 硬件拓展能力【StartNavigation】【GetHWEnvironment】
     * 允许小程序调用第三方 app 提供的硬件能力，如小程序播放车机提供的音乐能力；
     * 或设备发送通知给到小程序，如车机通知小程序当前电（油）量低。
     * 1、进行能力注册
     */
    public void AromeExtendBridgeRequest(Application application, ArrayList<String> mExtensionList,
                                         String bridgeExtension, AbstractCommonListener listener) {
        AromeExtendBridgeRequest request = new AromeExtendBridgeRequest();
        ArrayList<String> extensionList = new ArrayList<>();
        extensionList.add("StartNavigation");
        extensionList.add("GetHWEnvironment");
        extensionList.add("makePhoneCall");
        request.mExtensionList = extensionList;
        AromeServiceInvoker.invoke(request, new AromeServiceTask.Callback<AromeResponse>() {
            @Override
            public void onCallback(final AromeResponse response) {
                if (response.success) {
                    AromeBridgeCallBack(application, bridgeExtension, listener);
                } else {
                    Log.e("zzzArome", "ExtendBridgeRequest****+" + response.message);
                    listener.extendBridgeRequest(response);
                }
            }
        });
    }

    /**
     * 2、监听小程序发出来的调用通知,进行回调(如果不需要回调传递null)
     */
    private void AromeBridgeCallBack(Application application, String bridgeExtension, AbstractCommonListener listener) {
        AromeServiceInvoker.registerBridgeExtension(new AromeServiceInvoker.BridgeExtension() {
            @Override
            public void onCalled(String action, String params, AromeServiceInvoker.BridgeCallback bridgeCallback) {
                // 如果action == "GetHWEnvironment"，回传带有硬件实时信息的结果给小程序
                LogExtKt.logE("AromeBridgeCallBack: onCall() action:" + action + ",params:" + params, "zzzArome");

                if ("StartNavigation".equals(action)) {
                    if (params != null) {
                        Gson gson = new Gson();
                        BridgeCallLocation bridgeCallLocation = gson.fromJson(params, BridgeCallLocation.class);
                        if (bridgeCallLocation != null && bridgeCallLocation.getLatitude() != null && bridgeCallLocation.getLongitude() != null) {
                            PoiInfo poiInfo = new PoiInfo();
                            poiInfo.setLatLng(new LatLng(Double.parseDouble(bridgeCallLocation.getLatitude()), Double.parseDouble(bridgeCallLocation.getLongitude())));
                            poiInfo.setAddress(bridgeCallLocation.getAddress());
                            //设置目的地
                            NaviRoutePlan reqModel = new NaviRoutePlan(poiInfo);
                            //算路偏好-使用地图默认的算路规则
                            reqModel.setStrategy(RoutePlanStrategy.DEFAULT);
                            //发起路线规划
                            reqModel.setAction(NaviRoutePlan.ACTION_ROUTE_PLAN);
                            //导航初始化

                            NaviApiManager.getInstance().getNaviAPI().routePlanOrNavi(reqModel, new INaviAPICallback() {

                                @Override
                                public void onSuccess(NaviBaseModel naviBaseModel) {
                                    LogExtKt.logE("AromeBridgeCallBack  StartNavigation: onSuccess: naviBaseModel:" + naviBaseModel.getMessage(), "zzzArome");
                                }

                                @Override
                                public void onError(NaviErrorModel naviErrorModel) {
                                    LogExtKt.logE("AromeBridgeCallBack  StartNavigation: onError, naviBaseModel:" + naviErrorModel.getMessage(), "zzzArome");
                                }
                            });
                        }
                    }

//                    AromeExtServiceInteract aromeExtServiceInteract = new AromeExtServiceInteract();
//                    aromeExtServiceInteract.AromeExtLoadWidget(null, false, new CommonListener() {
//                        @Override
//                        public void loadWidget(AromeResponse response) {
//                            LogExtKt.logE("aromeExtServiceInteract  loadWidget: loadWidget, response:" + response.message,TAG);
//                        }
//                    });


//                    NaviBaseModel naviModel = NaviAPI.get().getLastLocation();
//                    LocationInfoBean locationInfoBean = gson.fromJson(naviModel.toString(), LocationInfoBean.class);
//                    JSONObject result = new JSONObject();
//                    if(locationInfoBean != null && locationInfoBean.getLocationInfo() != null
//                            && locationInfoBean.getLocationInfo().getLatLng() != null){
//                        try {
//                            result.put("latitude", locationInfoBean.getLocationInfo().getLatLng().getLatitude());
//                            result.put("longitude", locationInfoBean.getLocationInfo().getLatLng().getLongitude());
//                            result.put("address", bridgeExtension);
//                        } catch (Throwable e) {
//                            Log.e("NavigationException", "onCalled", e);
//                        }
//                    }
//                    String jsonString = result.toString();
//                    bridgeCallback.callback(jsonString);
//                    listener.extendBridgeRequest();
                } else if ("GetHWEnvironment".equals(action)) {
                    JSONObject result = new JSONObject();
                    try {
//                        result.put("action", action);
//                        result.put("params", params);
//                        result.put("bridgeExtension",bridgeExtension);
                        result.put("isDarkMode", DensityExtKt.getUINightMode());
                    } catch (Throwable t) {
                        Log.e("GetHWEnvironment", "onCalled", t);
                    }
                    String jsonString = result.toString();
                    bridgeCallback.callback(jsonString);

                } else if ("makePhoneCall".equals(action)) {
                    JSONObject result = new JSONObject();
                    try {
                        JSONObject jsonObject = new JSONObject(params);
                        String phoneNumber = jsonObject.getString("number");
                        Uri uri = Uri.parse("tel:" + phoneNumber);
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        application.startActivity(intent);

                        result.put("success", true);
                        String jsonString = result.toString();
                        bridgeCallback.callback(jsonString);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    LogExtKt.logE("action=" + action, "zzzArome");
                }
//                JSONObject result = new JSONObject();
//                try {
//                    result.put("action", action);
//                    result.put("params", params);
//                    result.put("bridgeExtension", bridgeExtension);
//                } catch (Throwable t) {
//                    Log.e("GetHWEnvironment", "onCalled", t);
//                }
//                String jsonString = result.toString();
//                bridgeCallback.callback(jsonString);
//                AromeResponse response = new AromeResponse();
//                response.success = true;
//                response.code = 0;
//                response.message = params;
//                listener.extendBridgeRequest(response);
            }
        });
    }

    /**
     * 向小程序发送事件
     */
    public void AromeBridgeSendEvent(String eventName, String eventData, AbstractCommonListener listener) {
        AromeSendEventRequest sendEventRequest = new AromeSendEventRequest();
        //sendEventRequest.eventName = "niceToMeetYou";
        //sendEventRequest.eventData = "{\"phonetype\":\"ABC\",\"cat\":\"Android\"}\n";
        sendEventRequest.eventName = eventName;
        sendEventRequest.eventData = eventData;
        AromeServiceInvoker.invoke(sendEventRequest, new AromeServiceTask.Callback<AromeResponse>() {
            @Override
            public void onCallback(final AromeResponse response) {
                listener.bridgeSendEvent(response);
            }
        });
    }
}


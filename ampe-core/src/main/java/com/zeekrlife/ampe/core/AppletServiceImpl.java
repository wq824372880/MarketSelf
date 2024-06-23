package com.zeekrlife.ampe.core;

import android.app.Application;
import android.os.Build;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alipay.arome.aromecli.AromeInit;
import com.alipay.arome.aromecli.response.AromeResponse;
import com.zeekr.car.util.ThreadPoolUtil;
import com.zeekrlife.ampe.aidl.AppletInfo;
import com.zeekrlife.ampe.aidl.IAppletCallback;
import com.zeekrlife.ampe.aidl.IAppletService;
import com.zeekrlife.ampe.core.bean.AppletMapper;
import com.zeekrlife.ampe.core.bean.UserInfo;
import com.zeekrlife.ampe.core.listener.AbstractCommonListener;
import com.zeekrlife.common.ext.CommExtKt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * @date 2023/11/23 13:21
 * description：TODO
 */
public class AppletServiceImpl extends IAppletService.Stub {
    private Application mApplication;
    private final AromeServiceInteract aromeServiceInteract;
//    private final AromeExtServiceInteract aromeExtServiceInteract;
    private final RemoteCallbackList<IAppletCallback> appletCallbackList;
    private ScheduledThreadPoolExecutor dispatchExecutor;
    public static HashSet<String> legalAppletIdSet = new HashSet<>();
    public boolean booInitArome = false;
    public boolean booInitAromeExt = false;
    public static UserInfo userInfo = new UserInfo();

    public AppletServiceImpl(Application application) {
        this.mApplication = application;
        AromeInit.attachApplicationContext(application);
        appletCallbackList = new RemoteCallbackList<>();
        aromeServiceInteract = new AromeServiceInteract();
        dispatchExecutor = new ScheduledThreadPoolExecutor(1, new ThreadPoolUtil.CustomThreadFactory());
        dispatchExecutor.allowCoreThreadTimeOut(true);
        dispatchExecutor.setKeepAliveTime(ThreadPoolUtil.KEEP_ALIVE_SECONDS, TimeUnit.SECONDS);
//        aromeExtServiceInteract = new AromeExtServiceInteract();
        Log.e("bind", "bind call success");
    }

    /**
     * 初始化 Arome 服务。
     * 该方法通过与 Arome 服务交互来初始化服务，使用提供的设备ID和签名进行身份验证。
     * 初始化成功后，会记录初始化状态、设备ID和签名，并提交一个调度任务。
     * 初始化完成后，会尝试退出应用。
     *
     * @param deviceId 设备的唯一标识符。
     * @param signature 设备的签名信息，用于验证。
     * @throws RemoteException 如果与 Arome 服务交互时发生远程调用异常。
     */
    @Override
    public void initArome(String deviceId, String signature) throws RemoteException {
        // 与 Arome 服务初始化交互，传入设备ID和签名，并设置一个回调监听初始化结果
        aromeServiceInteract.AromeServiceInit(deviceId, signature, new AbstractCommonListener() {
            @Override
            public void init(AromeResponse response) {
                // 在回调中处理初始化结果，记录初始化状态、设备ID、签名，并调度相关任务
                Log.e("initArome", "initArome22: success:" + response.success +
                        "message:" + response.message);
                booInitArome = response.success; // 记录初始化是否成功
                dispatchExecutor.submit(new DispatchRunnable(Type.initArome,
                        AppletMapper.ampe2Entity(response))); // 提交一个调度任务
                aromeServiceInteract.AromeServiceExitApp(); // 尝试退出应用
            }
        });
    }


    /**
     * 启动指定的应用小程序。
     * @param appletId 应用小程序的ID。
     * @throws RemoteException 如果与服务交互时发生远程异常。
     * @requiresApi api = Build.VERSION_CODES.O 表示该方法需要Android 8.0（API级别26）或更高版本的支持。
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void launcherApplet(String appletId) throws RemoteException {
        // 通过AromeServiceInteract与服务交互，启动指定ID的应用小程序，使用匿名内部类实现AbstractCommonListener接口处理响应
        aromeServiceInteract.AromeServiceLauncher(appletId, new AbstractCommonListener() {
            @Override
            public void launcher(AromeResponse response) {
                // 在日志中记录启动应用小程序的响应信息
                Log.e("zzzlauncherApplet", "launcherApplet code:" + response.code + ",message:" + response.message);
                // 使用dispatchExecutor提交一个DispatchRunnable任务，处理启动应用小程序的逻辑
                dispatchExecutor.submit(new DispatchRunnable(Type.launcherApplet, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    /**
     * 在大屏幕模式下启动应用小程序。
     * @param appletId 应用小程序的ID，用于标识要启动的小程序。
     * @throws RemoteException 如果与服务交互时发生远程异常。
     * @requiresApi api = Build.VERSION_CODES.O 该方法需要Android 8.0 (Oreo)或更高版本的支持。
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void launcherAppletWithFullScreen(String appletId) throws RemoteException {
        // 通过AromeServiceInteract类的接口方法，以全屏模式启动指定ID的应用小程序。
        // 提供一个AbstractCommonListener监听器来处理启动的响应。
        aromeServiceInteract.AromeServiceLauncherWithFullScreen(appletId, new AbstractCommonListener() {
            @Override
            public void launcher(AromeResponse response) {
                // 在日志中记录启动应用小程序的结果，包括是否成功和响应消息。
                Log.e("zzzlauncherApplet", "launcherApplet success" + response.success + ",message:" + response.message);
                // 将启动应用小程序的事件提交到调度线程池中进行处理。
                dispatchExecutor.submit(new DispatchRunnable(Type.launcherApplet, AppletMapper.ampe2Entity(response)));
            }
        });
    }

    /**
     * 异常退出应用程序。
     * 此方法通过调用 AromeServiceInteract 的 AromeServiceExitApp 方法来实现应用的异常退出。
     * 该方法没有参数，也不返回任何值。
     *
     * @throws RemoteException 如果在调用远程服务过程中发生错误，则抛出 RemoteException。
     */
    @Override
    public void exitApplet() throws RemoteException {
        aromeServiceInteract.AromeServiceExitApp(); // 调用远程服务接口方法，请求应用退出
    }

    /**
     * 登录方法。
     * 通过调用 AromeServiceInteract 的 AromeServiceLogin 方法实现登录逻辑。
     * 该方法没有参数和返回值，但会触发远程登录过程，并处理登录响应。
     *
     * @throws RemoteException 如果远程方法调用失败
     */
    @Override
    public void login() throws RemoteException {
        // 使用匿名内部类实现 AbstractCommonListener 接口，
        // 以处理登录过程中的响应。
        aromeServiceInteract.AromeServiceLogin(new AbstractCommonListener() {
            @Override
            // 当登录响应收到时，将登录信息提交给调度执行器。
            public void login(AromeResponse response) {
                // 提交一个运行任务到调度线程池，处理登录响应。
                dispatchExecutor.submit(new DispatchRunnable(Type.login, AppletMapper.ampe2Entity(response)));
            }
        });
    }

    /**
     * 实现远程登出功能。
     * 该方法通过与 Arome 服务交互来执行用户的登出操作，并将登出结果提交给调度执行器进行进一步处理。
     *
     * @throws RemoteException 如果在与 Arome 服务交互过程中发生远程通信异常。
     */
    @Override
    public void logout() throws RemoteException {
        // 向 Arome 服务发起登出请求，并定义登出成功后的回调行为
        aromeServiceInteract.AromeServiceLoginOut(new AbstractCommonListener() {
            @Override
            public void logout(AromeResponse response) {
                // 当登出操作完成后，通过调度执行器提交一个登出任务
                dispatchExecutor.submit(new DispatchRunnable(Type.logout, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    /**
     * 预加载指定的应用。
     * @param appletId 应用的唯一标识符。
     * @throws RemoteException 远程调用异常。
     */
    @Override
    public void preloadApp(String appletId) throws RemoteException {
        // 通过AromeServiceInteract接口调用预加载应用的方法，传入appletId和一个监听器
        aromeServiceInteract.AromeServicePreloadApp(appletId, new AbstractCommonListener() {
            /**
             * 当预加载应用操作完成时被调用。
             * @param response 预加载操作的响应信息。
             */
            @Override
            public void preloadApp(AromeResponse response) {
                // 在日志中记录预加载操作的结果
                Log.e("zzzpreloadApp", "preloadApp success" + response.success + ",message:" + response.message);
                // 使用调度执行器提交一个运行任务，用于处理预加载完成后的逻辑
                dispatchExecutor.submit(new DispatchRunnable(Type.preloadApp, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    /**
     * 批量预加载应用。
     * 该方法将给定的应用ID列表作为参数，通过调用AROME服务接口来实现应用的批量预加载。
     * 在预加载成功后，会将相应的响应信息提交给调度线程进行进一步处理。
     *
     * @param appletIds 应用ID的列表，用于指定需要预加载的应用。
     * @throws RemoteException 如果在与AROME服务交互过程中出现远程调用异常。
     */
    @Override
    public void batchPreloadApp(List<String> appletIds) throws RemoteException {
        // 使用HashSet去重，确保appletIds集合中没有重复的ID
        Set<String> set = new HashSet<>(appletIds);

        // 调用AROME服务的批量预加载应用接口，并传入一个监听器来处理响应
        aromeServiceInteract.AromeServiceBatchPreloadApp(set, new AbstractCommonListener() {
            @Override
            public void batchPreloadApp(AromeResponse response) {
                // 打印日志信息，记录预加载应用的响应结果
                Log.e("zzzbatchPreloadApp", "batchPreloadApp success" + response.success + ",message:" + response.message);

                // 将预加载应用的响应信息提交给调度线程处理
                dispatchExecutor.submit(new DispatchRunnable(Type.batchPreloadApp, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    @Override
    public void getUserInfo() throws RemoteException {
        aromeServiceInteract.AromeServiceGetUserInfo(new AbstractCommonListener() {
            @Override
            public void getUserInfo(AromeResponse response) {
                dispatchExecutor.submit(new DispatchRunnable(Type.getUserInfo, AppletMapper.ampe2Entity(response)));
            }
        });
    }

    /**
     * 获取指定应用程序的状态。
     * @param appletId 应用程序的ID，用于标识特定的应用程序。
     * @throws RemoteException 如果远程调用服务时发生错误。
     */
    @Override
    public void getAppStatus(String appletId) throws RemoteException {
        // 通过AromeServiceInteract接口，请求获取指定appletId的应用状态，使用匿名内部类实现AbstractCommonListener接口
        aromeServiceInteract.AromeServiceGetAppStatus(appletId, new AbstractCommonListener() {
            @Override
            // 当获取到应用状态响应时，将状态提交给调度执行器进行进一步处理
            public void getAppStatus(AromeResponse response) {
                // 提交一个运行任务到dispatchExecutor，用于处理获取到的应用状态
                dispatchExecutor.submit(new DispatchRunnable(Type.getAppStatus, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    /**
     * 上传日志到服务器。
     * 该方法会将指定时间范围内的日志上传到服务器，并通过回调方式处理上传结果。
     *
     * @param startDate 开始日期，指定上传日志的起始时间范围。
     * @param endDate 结束日期，指定上传日志的结束时间范围。
     * @throws RemoteException 如果远程调用失败则抛出此异常。
     */
    @Override
    public void uploadLog(String startDate, String endDate) throws RemoteException {
        // 通过AromeServiceInteract接口调用上传日志方法，传入开始日期和结束日期，并设置一个回调监听器处理上传响应。
        aromeServiceInteract.AromeServiceUploadLog(startDate, endDate, new AbstractCommonListener() {
            @Override
            public void uploadLog(AromeResponse response) {
                // 当日志上传成功后，将上传结果提交给调度执行器进行进一步处理。
                dispatchExecutor.submit(new DispatchRunnable(Type.uploadLog, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    /**
     * 启动指定的微型服务。
     * 此方法通过与 AromeService 进行交互来启动指定的微型服务，并且会将启动结果提交给调度执行器进行进一步处理。
     *
     * @param miniServiceCode 微型服务的代码标识，用于指定要启动的微型服务。
     * @throws RemoteException 如果在远程交互过程中出现异常，则抛出此异常。
     */
    @Override
    public void launcherMiniService(String miniServiceCode) throws RemoteException {
        // 通过 AromeServiceInteract 接口启动指定的 miniServiceCode 对应的微型服务，
        // 并且提供一个 AbstractCommonListener 实现来处理启动结果。
        aromeServiceInteract.AromeServiceLauncherMiniService(miniServiceCode, new AbstractCommonListener() {
            @Override
            public void launcherMiniService(AromeResponse response) {
                // 当微型服务启动完成时，将启动结果封装为 DispatchRunnable 并提交给 dispatchExecutor 执行。
                dispatchExecutor.submit(new DispatchRunnable(Type.launcherMiniService, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    /**
     * 启动自定义服务。
     * 此方法通过提供的自定义服务代码和用户身份信息，来启动相应的服务。
     * 一旦服务启动成功，会通过调度线程提交一个运行任务。
     *
     * @param customServiceCode 自定义服务的代码，用于识别要启动的服务。
     * @param userIdentity 用户的身份标识，用于鉴权或者用户服务关联。
     * @throws RemoteException 如果远程交互过程中出现异常，则抛出此异常。
     */
    @Override
    public void launchCustomService(String customServiceCode, String userIdentity) throws RemoteException {
        // 与Arome服务交互，启动指定的自定义服务
        aromeServiceInteract.AromeServiceLaunchCustomService(
                customServiceCode,
                userIdentity,
                new AbstractCommonListener() {
            @Override
            public void launchCustomService(AromeResponse response) {
                // 当服务启动成功，将响应信息提交给调度线程进行后续处理
                dispatchExecutor.submit(new DispatchRunnable(Type.launchCustomService,
                        AppletMapper.ampe2Entity(response)
                ));
            }
        });
    }


    /**
     * 扩展桥接请求。
     * 向AROME服务发送请求以扩展桥接功能。此方法会将指定的扩展列表和参数传递给服务，并在服务响应后触发一个异步操作。
     *
     * @param extensionList 扩展列表，包含需要添加或修改的扩展项。
     * @param bridgeExtensionParams 桥接扩展参数，用于配置或指导扩展过程。
     * @throws RemoteException 如果与AROME服务交互时发生远程调用异常。
     */
    @Override
    public void extendBridgeRequest(List<String> extensionList, String bridgeExtensionParams) throws RemoteException {
        // 向AROME服务发起扩展桥接请求，传入应用实例、扩展列表和扩展参数，并设置一个监听器以处理服务的响应。
        aromeServiceInteract.AromeExtendBridgeRequest(
                mApplication,
                (ArrayList<String>) extensionList,
                bridgeExtensionParams, new AbstractCommonListener() {
            @Override
            public void extendBridgeRequest(AromeResponse response) {
                // 当收到服务响应时，将响应包装成实体并提交一个异步任务以进行进一步处理。
                dispatchExecutor.submit(new DispatchRunnable(Type.extendBridgeRequest,
                        AppletMapper.ampe2Entity(response)
                ));
            }
        });
    }


    /**
     * 通过桥接发送事件到AROME服务。
     * @param eventName 事件名称，标识要发送的事件类型。
     * @param eventData 事件数据，携带与事件相关的数据信息。
     * @throws RemoteException 当与AROME服务交互时发生远程通信异常。
     */
    @Override
    public void bridgeSendEvent(String eventName, String eventData) throws RemoteException {
        aromeServiceInteract.AromeBridgeSendEvent(eventName, eventData, new AbstractCommonListener() {
            @Override
            public void bridgeSendEvent(AromeResponse response) {
                dispatchExecutor.submit(new DispatchRunnable(Type.bridgeSendEvent, AppletMapper.ampe2Entity(response)));
            }
        });
    }


    @Override
    public void initExt(String deviceId, String locationInfo) throws RemoteException {
//        JSONObject jsonLocation = JSONObject.parseObject(locationInfo);
//        aromeExtServiceInteract.AromeExtInit(deviceId, new Function1<AppletInfo, Unit>() {
//            @Override
//            public Unit invoke(AppletInfo appletInfo) {
//                Log.e("zzzinitAromeExt", "initAromeExt success" + appletInfo.success + ",message:" + appletInfo.message);
//                dispatchExecutor.submit(new DispatchRunnable(Type.initExtArome, appletInfo));
//                return null;
//            }
//        });
    }

/**
 * 注册业务类型到系统中。
 *
 * @param bizType 业务类型的字符串标识，用于区分不同的业务。
 * @throws RemoteException 如果在注册过程中出现远程通信异常，则抛出此异常。
 */
@Override
public void registerBiz(String bizType) throws RemoteException {
    // 此处代码旨在调用远程服务注册业务类型，并通过回调方式处理注册结果
    // 由于代码不完整，具体的实现细节如下注释所示
/*
    aromeExtServiceInteract.AromeExtRegisterBiz(bizType, new CommonListener() {
        @Override
        public void registerBiz(AromeResponse response) {
            // 处理注册结果，通过调度执行器提交一个任务来处理
            dispatchExecutor.submit(new DispatchRunnable(Type.registerBiz, AppletMapper.ampe2Entity(response)));
        }
    });
*/
}


/**
 * 发送RPC（远程过程调用）请求。
 * @param operationType 操作类型，定义了RPC请求的类型或动作。
 * @param requestData 请求数据，包含了执行RPC操作所需的全部信息。
 * @throws RemoteException 当发送RPC请求时遇到远程通信问题时抛出。
 */
@Override
public void sendRpc(String operationType, String requestData) throws RemoteException {
    // 此处代码旨在通过AROME扩展服务接口发送RPC请求，但具体实现已被注释掉。
    // 实现包括构造请求并注册一个回调监听器，用于处理RPC响应。
}


    @Override
    public void loadWidget(String query, boolean showPlaceholder) throws RemoteException {
//        JSONObject jsonLocation = new JSONObject();
//        if (query != null) {
//            jsonLocation = JSONObject.parseObject(query);
//        }
//        aromeExtServiceInteract.AromeExtLoadWidget(jsonLocation, showPlaceholder, new CommonListener() {
//            @Override
//            public void loadWidget(AromeResponse response) {
//                Log.e("zzzloadWidget", "loadWidget success" + response.success + ",message:" + response.message);
//                dispatchExecutor.submit(new DispatchRunnable(Type.loadWidget, AppletMapper.ampe2Entity(response)));
//            }
//        });
    }

    @Override
    public void closeWidget(String widgetId) throws RemoteException {
//        aromeExtServiceInteract.AromeExtCloseWidget(Long.parseLong(widgetId));
    }

    @Override
    public void destroyWidget(String widgetId) throws RemoteException {
//        aromeExtServiceInteract.AromeExtDestroyWidget(Long.parseLong(widgetId));
    }


    @Override
    public boolean registerAppletCallback(IAppletCallback callback) throws RemoteException {
        return appletCallbackList.register(callback);
    }

    /**
     * 从applet回调列表中取消注册一个回调接口。
     * 这个方法会尝试从回调列表中移除指定的回调对象。如果移除成功，则返回true，否则返回false。
     *
     * @param callback 要取消注册的IAppletCallback接口实例。
     * @return boolean 返回true表示成功从列表中移除回调，返回false表示该回调未被找到或移除失败。
     * @throws RemoteException 如果在远程注册过程中出现异常，则抛出此异常。
     */
    @Override
    public boolean unregisterAppletCallback(IAppletCallback callback) throws RemoteException {
        // 尝试从回调列表中移除指定的回调对象
        return appletCallbackList.unregister(callback);
    }


    private class DispatchRunnable implements Runnable {

        private final Type type;
        private final AppletInfo appletInfo;

        DispatchRunnable(Type type, AppletInfo appletInfo) {
            this.type = type;
            this.appletInfo = appletInfo;
        }

        @Override
        public void run() {
            switch (type) {
                case initArome:
                    onInitAromeCallBack(appletInfo);
                    break;
                case launcherApplet:
                    onLauncherAppletCallBack(appletInfo);
                    break;
                case login:
                    onLoginAppletCallBack(appletInfo);
                    break;
                case logout:
                    onLoginOutAppletCallBack(appletInfo);
                    break;
                case preloadApp:
                    onPreloadAppCallBack(appletInfo);
                    break;
                case batchPreloadApp:
                    onBatchPreLoadAppCallBack(appletInfo);
                    break;
                case getUserInfo:
                    onGetUserInfoCallBack(appletInfo);
                    break;
                case getAppStatus:
                    onAppStatusCallBack(appletInfo);
                    break;
                case uploadLog:
                    onUploadLogCallBack(appletInfo);
                    break;
                case launcherMiniService:
                    onLauncherMiniServiceCallBack(appletInfo);
                    break;
                case launchCustomService:
                    onLaunchCustomServiceCallBack(appletInfo);
                    break;
                case extendBridgeRequest:
                    onExtendBridgeRequestCallBack(appletInfo);
                    break;
                case bridgeSendEvent:
                    onBridgeSendEventCallBack(appletInfo);
                    break;
                case initExtArome:
                    onInitAromeExtCallBack(appletInfo);
                    break;
                case registerBiz:
                    onRegisterBizCallBack(appletInfo);
                    break;
                case sendRpc:
                    onSendRpcCallBack(appletInfo);
                    break;
                case loadWidget:
                    onLoadWidgetCallBack(appletInfo);
                    break;
                default:
                    break;
            }
        }

        private void onInitAromeCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.initAromeCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onLauncherAppletCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.launcherAppletCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onLoginAppletCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.loginCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onLoginOutAppletCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.loginOutCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onPreloadAppCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.preloadAppCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onBatchPreLoadAppCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.batchPreloadAppCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onGetUserInfoCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.getUserInfoCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onAppStatusCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.getAppStatusCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onUploadLogCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.uploadLogCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onLauncherMiniServiceCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.launcherMiniServiceCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onLaunchCustomServiceCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.launchCustomServiceCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onExtendBridgeRequestCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.bridgeRequestCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onBridgeSendEventCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.bridgeSendEventCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onInitAromeExtCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.initExtCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onLoadWidgetCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.loadWidgetCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onRegisterBizCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.registerBizCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }

        private void onSendRpcCallBack(AppletInfo appletInfo) {
            try {
                final int len = appletCallbackList.beginBroadcast();
                for (int i = 0; i < len; i++) {
                    IAppletCallback callback = appletCallbackList.getBroadcastItem(i);
                    try {
                        callback.sendRpcCallBack(appletInfo);
                    } catch (Exception e) {
                        CommExtKt.logStackTrace(e);
                    }
                }
            } finally {
                try {
                    appletCallbackList.finishBroadcast();
                } catch (IllegalStateException e) {
                    CommExtKt.logStackTrace(e);
                }
            }
        }
    }

    public enum Type {
        /**
         * init arome
         */
        initArome,
        /**
         * launcher applet
         */
        launcherApplet,
        /**
         * login
         */
        login,
        /**
         * logout
         */
        logout,
        /**
         * preload app
         */
        preloadApp,
        /**
         * batch preload app
         */
        batchPreloadApp,
        /**
         * getuser info
         */
        getUserInfo,
        /**
         * get app status
         */
        getAppStatus,
        /**
         * upload log
         */
        uploadLog,
        /**
         * launcher mini service
         */
        launcherMiniService,
        /**
         * launch custom service
         */
        launchCustomService,
        /**
         * extend bridge request
         */
        extendBridgeRequest,
        /**
         * bridge send event
         */
        bridgeSendEvent,
        /**
         * init ext arome
         */
        initExtArome,
        /**
         * register biz
         */
        registerBiz,
        /**
         * send rpc
         */
        sendRpc,
        /**
         * load widget
         */
        loadWidget,
        /**
         * destroy widget
         */
        destroyWidget
    }
}

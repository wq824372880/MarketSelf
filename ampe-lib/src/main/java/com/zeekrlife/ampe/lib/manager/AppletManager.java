package com.zeekrlife.ampe.lib.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.zeekrlife.ampe.aidl.AppletInfo;
import com.zeekrlife.ampe.aidl.IAppletCallback;
import com.zeekrlife.ampe.aidl.IAppletService;
import com.zeekrlife.ampe.lib.CallBackType;
import com.zeekrlife.ampe.lib.listener.AppStatusCallBack;
import com.zeekrlife.ampe.lib.listener.BatchPreLoadCallBack;
import com.zeekrlife.ampe.lib.listener.BridgeCallBack;
import com.zeekrlife.ampe.lib.listener.CallBack;
import com.zeekrlife.ampe.lib.listener.GetUserInfoCallBack;
import com.zeekrlife.ampe.lib.listener.InitAromeCallBack;
import com.zeekrlife.ampe.lib.listener.LaunchCustomServiceCallBack;
import com.zeekrlife.ampe.lib.listener.LaunchMiniServiceCallBack;
import com.zeekrlife.ampe.lib.listener.LauncherCallBack;
import com.zeekrlife.ampe.lib.listener.LoginCallBack;
import com.zeekrlife.ampe.lib.listener.LoginOutCallBack;
import com.zeekrlife.ampe.lib.listener.PreLoadCallBack;
import com.zeekrlife.ampe.lib.listener.SendEventCallBack;
import com.zeekrlife.ampe.lib.listener.UploadLogCallBack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppletManager {
    private static final String TAG = "zzzAppletManager";

    private Context context;
    private ServiceConnection serviceConnection;
    private IAppletService appletService;
    private String deviceId, signature;
    private InitAromeCallBack initAromeCallBack;

    public final Map<String, CallBack> commonCallbackMap = new ConcurrentHashMap<>();

    /**
     * 获取AppletManager的单例实例。
     * 这个方法是静态的，可以通过类名直接调用，而不需要实例化类。
     * 它返回一个AppletManager对象，该对象是单例模式的实例，保证了全局唯一性。
     *
     * @return AppletManager的单例实例。
     */
    public static AppletManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static boolean initAromeSuccess = false;

    private static class InstanceHolder {
        public static final AppletManager INSTANCE = new AppletManager();
    }

    public class Connection implements ServiceConnection {

        /**
         * 当与服务连接成功时被调用。
         * @param name 组件名，表示连接的服务的名称。
         * @param service IBinder对象，通过这个对象可以调用远程服务的方法。
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 连接到appletService后打印日志
            Log.d(TAG, "appletService connected!");
            // 将IBinder对象转换为IAppletService接口
            appletService = IAppletService.Stub.asInterface(service);

            try {
                // 注册applet的回调
                appletService.registerAppletCallback(appletCallback);
                try {
                    // 清除之前的通用回调并注册新的回调
                    commonCallbackMap.clear();
                    registerCommonCallback(CallBackType.INITAROME, initAromeCallBack);
                    // 初始化Arome
                    appletService.initArome(deviceId, signature);
                } catch (Throwable throwable) {
                    // 捕获initArome过程中的异常并打印
                    Log.e(TAG, "initArome" + Log.getStackTraceString(throwable));
                }
            } catch (Throwable throwable) {
                // 捕获注册回调或调用initArome时的异常并打印
                Log.e(TAG, "register arrangeCallback or taskCallback:" + Log.getStackTraceString(throwable));
            }
        }


        /**
         * 当与服务连接断开时被调用。
         * 这个方法会清除回调映射表，并尝试注销应用let的回调。如果过程中发生异常，会记录日志。
         *
         * @param name 组件名，表示断开连接的服务的名称。
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 记录服务断开连接的日志
            Log.e(TAG, "taskService disconnected!");

            try {
                // 清除通用回调映射表
                commonCallbackMap.clear();
                // 注销应用let的回调
                appletService.unregisterAppletCallback(appletCallback);
            } catch (Throwable throwable) {
                // 如果注销过程中发生异常，记录异常日志
                Log.e(TAG, "unregister arrangeCallback or taskCallback:" + Log.getStackTraceString(throwable));
            }

            // 将appletService设置为null，表示服务已断开
            appletService = null;
        }

    }

    private final IAppletCallback appletCallback = new IAppletCallback.Stub() {
        /**
         * 初始化 Arome 回调函数。
         * 该方法会根据传入的 AppletInfo 信息进行初始化操作，并调用注册的 InitAromeCallBack 回调接口。
         *
         * @param info 包含初始化结果信息的对象，包括是否成功和错误消息。
         * @throws RemoteException 如果调用回调时发生远程方法异常。
         */
        @Override
        public void initAromeCallBack(AppletInfo info) throws RemoteException {
            // 记录日志，包含初始化结果信息
            Log.e(TAG, "initAromeCallBack:info.success="+ info.getSuccess() + "info.message:" + info.getMessage());

            // 设置初始化成功标志
            initAromeSuccess = info.getSuccess();

            // 获取并调用 INITAROME 类型的回调
            InitAromeCallBack callBack = (InitAromeCallBack) commonCallbackMap.get(CallBackType.INITAROME);
            if (callBack != null) {
                callBack.initCallBack(info);
            }
        }

        /**
         * 当登录操作完成时调用此回调函数。
         *
         * @param info 包含登录相关信息的AppletInfo对象。
         * @throws RemoteException 如果在远程方法调用中发生错误。
         */
        @Override
        public void loginCallBack(AppletInfo info) throws RemoteException {
            // 从回调映射中获取登录类型的回调对象
            LoginCallBack callBack = (LoginCallBack) commonCallbackMap.get(CallBackType.LOGIN);
            if (callBack != null) {
                // 如果回调对象不为空，则调用其loginCallBack方法
                callBack.loginCallBack(info);
            }
        }

        /**
         * 当用户登出时调用此回调函数。
         * @param info 包含登出信息的AppletInfo对象。
         * @throws RemoteException 如果调用远程回调对象时发生错误。
         */
        @Override
        public void loginOutCallBack(AppletInfo info) throws RemoteException {
            // 从回调映射中获取登录退出类型的回调对象
            LoginOutCallBack callBack = (LoginOutCallBack) commonCallbackMap.get(CallBackType.LOGINOUT);
            if (callBack != null) {
                // 如果回调对象不为空，则调用登出回调方法
                callBack.loginOutCallBack(info);
            }
        }

        /**
         * 当启动器小程序调用回调时执行此方法。
         * @param info 包含调用结果信息的对象，如是否成功和附带消息。
         * @throws RemoteException 如果调用过程中发生远程异常。
         */
        @Override
        public void launcherAppletCallBack(AppletInfo info) throws RemoteException {
            // 记录回调信息，包括成功状态和消息内容
            Log.e(TAG, "launcherAppletCallBack! success:" + info.getSuccess() + ",message:" + info.getMessage());

            // 从回调映射中获取LauncherCallBack实例
            LauncherCallBack callBack = (LauncherCallBack) commonCallbackMap.get(CallBackType.LAUNCHERAPPLET);

            // 如果找到回调实例，则调用其launcherCallBack方法
            if (callBack != null) {
                callBack.launcherCallBack(info);
            }
        }

        /**
         * 预加载应用程序回调方法。
         * 当预加载应用操作完成时，此方法被调用，用于处理预加载的回调。
         *
         * @param info 包含预加载应用操作结果信息的 AppletInfo 对象。info.getSuccess() 表示操作是否成功，info.getMessage() 包含操作的详细消息。
         * @throws RemoteException 如果调用回调对象时发生远程方法异常。
         */
        @Override
        public void preloadAppCallBack(AppletInfo info) throws RemoteException {
            // 记录预加载回调信息，包括操作是否成功和相关消息。
            Log.e(TAG, "preloadAppCallBack! success:" + info.getSuccess() + ",message:" + info.getMessage());

            // 从回调映射中获取预加载类型的回调对象，并执行预加载回调。
            PreLoadCallBack callBack = (PreLoadCallBack) commonCallbackMap.get(CallBackType.PRELOAD);
            if (callBack != null) {
                callBack.preLoadCallBack(info);
            }
        }

        /**
         * 批量预加载应用的回调方法。
         * 当批量预加载应用操作完成时，此方法将被调用，用于通知调用者预加载的结果。
         *
         * @param info 包含预加载操作结果信息的 AppletInfo 对象。info 对象包含了操作是否成功以及相关的消息。
         * @throws RemoteException 如果在回调过程中发生远程方法调用异常。
         */
        @Override
        public void batchPreloadAppCallBack(AppletInfo info) throws RemoteException {
            // 记录预加载回调信息，包括操作是否成功和相关消息
            Log.e(TAG, "batchPreloadAppCallBack! success:" + info.getSuccess() + ",message:" + info.getMessage());

            // 从回调映射中获取批量预加载的回调对象
            BatchPreLoadCallBack callBack = (BatchPreLoadCallBack) commonCallbackMap.get(CallBackType.BATCHPRDLOAD);

            // 如果存在回调对象，则调用其回调方法，传递预加载结果信息
            if (callBack != null) {
                callBack.batchPreLoadCallBack(info);
            }
        }

        /**
         * 当获取用户信息的异步操作完成时，被调用的回调函数。
         *
         * @param info 包含用户信息操作的结果，包括是否成功和操作信息的消息对象。
         * @throws RemoteException 如果在回调过程中发生远程方法调用异常。
         */
        @Override
        public void getUserInfoCallBack(AppletInfo info) throws RemoteException {
            // 记录日志，包含操作结果和消息
            Log.e(TAG, "getUserInfoCallBack! success:"+  info.getSuccess() + ",message:" + info.getMessage());

            // 从回调映射中获取特定类型的回调对象
            GetUserInfoCallBack callBack = (GetUserInfoCallBack) commonCallbackMap.get(CallBackType.USERINFO);

            // 如果找到了对应的回调对象，则调用其回调方法
            if (callBack != null) {
                callBack.getUserInfoCallBack(info);
            }
        }

        /**
         * 处理获取应用状态的回调方法。
         * 当远程调用获取应用状态操作完成后，此方法将被调用，用于处理回调结果。
         *
         * @param info 包含应用状态信息的对象。该对象含有操作是否成功及相应的消息。
         * @throws RemoteException 如果在回调过程中发生远程通信异常。
         */
        @Override
        public void getAppStatusCallBack(AppletInfo info) throws RemoteException {
            // 记录回调信息，包括操作是否成功及返回的消息。
            Log.e(TAG, "getAppStatusCallBack! success:"+  info.getSuccess() + ",message:" + info.getMessage());

            // 从回调映射中获取应用状态回调接口实例。
            AppStatusCallBack callBack = (AppStatusCallBack) commonCallbackMap.get(CallBackType.APPSTATUS);
            if (callBack != null) {
                // 如果找到回调接口实例，则调用其应用状态回调方法，传递应用状态信息。
                callBack.appStatusCallBack(info);
            }
        }


        /**
         * 上传日志回调函数。
         * 当日志上传完成后，此函数将被调用，用于通知调用者上传的结果。
         *
         * @param info 包含上传结果信息的 AppletInfo 对象。成功上传时，会包含成功状态和消息；失败时，会包含错误消息。
         * @throws RemoteException 如果调用过程中出现远程通信异常。
         */
        @Override
        public void uploadLogCallBack(AppletInfo info) throws RemoteException {
            // 记录日志上传回调信息，包括成功状态和消息。
            Log.e(TAG, "uploadLogCallBack! success:" + info.getSuccess() + ",message:" + info.getMessage());

            // 从回调映射中获取上传日志的回调对象。
            UploadLogCallBack callBack = (UploadLogCallBack) commonCallbackMap.get(CallBackType.UPLOADLOG);

            // 如果找到了回调对象，则调用其上传日志回调函数，传递上传结果信息。
            if (callBack != null) {
                callBack.uploadLogCallBack(info);
            }
        }

        /**
         * 当启动小程序服务回调被调用时执行的函数。
         * 这个方法会从一个通用回调映射中获取特定类型的回调对象，并调用其相应的方法。
         *
         * @param info 包含小程序信息的对象，此对象将传递给回调方法。
         * @throws RemoteException 如果在远程方法调用中发生错误。
         */
        @Override
        public void launcherMiniServiceCallBack(AppletInfo info) throws RemoteException {
            // 从回调映射中获取启动小程序服务的回调对象
            LaunchMiniServiceCallBack callBack = (LaunchMiniServiceCallBack) commonCallbackMap.get(CallBackType.LAUNCHERMINISERVICE);
            if (callBack != null) {
                // 如果回调对象不为空，则调用其回调方法
                callBack.launchMiniServiceCallBack(info);
            }
        }

        /**
         * 调用自定义服务回调方法。
         * 当需要启动特定的自定义服务时，此方法将被调用，它会通过回调接口传递应用信息。
         *
         * @param info 包含应用详细信息的对象，用于启动自定义服务。
         * @throws RemoteException 如果在远程方法调用过程中发生错误。
         */
        @Override
        public void launchCustomServiceCallBack(AppletInfo info) throws RemoteException {
            // 从回调映射中获取启动自定义服务的回调对象
            LaunchCustomServiceCallBack callBack = (LaunchCustomServiceCallBack) commonCallbackMap.get(CallBackType.LAUNCHERSUSTONSERVICE);
            if (callBack != null) {
                // 如果回调对象不为空，则调用其回调方法，传递应用信息
                callBack.launchCustomServiceCallBack(info);
            }
        }

        /**
         * 当桥接请求完成时，调用此方法来回调给定的Applet信息。
         * @param info 包含applet详细信息的对象。
         * @throws RemoteException 如果在回调过程中发生远程通信异常。
         */
        @Override
        public void bridgeRequestCallBack(AppletInfo info) throws RemoteException {
            // 从回调通用映射中获取BridgeCallBack类型的回调对象
            BridgeCallBack callBack = (BridgeCallBack) commonCallbackMap.get(CallBackType.BRIDGECALLBACK);
            if (callBack != null) {
                // 如果回调对象不为空，则调用其bridgeCallBack方法，传入applet信息
                callBack.bridgeCallBack(info);
            }
        }

        /**
         * 当桥接器需要发送事件回调时调用此方法。
         * @param info 包含applet信息的对象，用于回调时传递给具体的事件处理方法。
         * @throws RemoteException 如果在远程调用过程中发生错误。
         */
        @Override
        public void bridgeSendEventCallBack(AppletInfo info) throws RemoteException {
            // 从回调通用映射中获取SendEventCallBack类型的回调对象
            SendEventCallBack callBack = (SendEventCallBack) commonCallbackMap.get(CallBackType.SENDEVENT);
            if (callBack != null) {
                // 如果回调对象不为空，则调用其sendEventCallBack方法，传入applet信息
                callBack.sendEventCallBack(info);
            }
        }

        /**
         * 初始化扩展回调方法。
         * 该方法用于在应用程序中初始化一些扩展的回调功能，允许外部对Applet进行一些操作或设置。
         *
         * @param info AppletInfo对象，包含关于Applet的详细信息。该参数用于传递关于当前Applet的描述和元数据，例如Applet的名称、版本等信息。
         * @throws RemoteException 如果在远程操作过程中发生错误，则抛出此异常。
         */
        @Override
        public void initExtCallBack(AppletInfo info) throws RemoteException {

        }

        /**
         * 注册业务回调方法。
         * 该方法用于在特定条件下注册一个回调函数，以便于在后续操作中调用。此方法覆盖了原始的注册业务回调方法。
         *
         * @param info AppletInfo对象，包含应用let的详细信息。该参数用于识别具体的业务应用，以便在回调时选择正确的应用进行操作。
         * @throws RemoteException 如果在远程注册过程中出现任何异常，则抛出此异常。
         */
        @Override
        public void registerBizCallBack(AppletInfo info) throws RemoteException {

        }

        /**
         * 发送RPC回调。此方法用于向指定的应用程序发送远程过程调用的回调信息。
         * @param info AppletInfo对象，包含应用的详细信息。此参数用于指定接收回调的应用。
         * @throws RemoteException 如果在进行远程调用时出现任何异常，则抛出此异常。
         */
        @Override
        public void sendRpcCallBack(AppletInfo info) throws RemoteException {

        }

        /**
         * 此方法用于加载小部件的回调操作。
         * 当应用程序加载小部件时，会调用此回调方法。
         *
         * @param info 包含有关小部件或应用程序的详细信息的对象。
         * @throws RemoteException 如果在远程交互过程中出现异常，则抛出此异常。
         */
        @Override
        public void loadWidgetCallBack(AppletInfo info) throws RemoteException {

        }

        /**
         * 当对应的Applet信息需要被销毁时调用的回调函数。
         * 该方法为空实现，即没有进行任何操作，可以根据实际需要进行重写。
         *
         * @param info AppletInfo对象，包含了Applet的相关信息，用于在销毁时可能需要的清理工作。
         * @throws RemoteException 如果在远程通信过程中发生错误，则抛出此异常。
         */
        @Override
        public void destroyWidgetCallBack(AppletInfo info) throws RemoteException {
            // 该方法为空实现，可以根据实际需要进行重写
        }
    };

    /**
     * 初始化 Arome 服务。
     *
     * @param context 应用的上下文环境，使用应用的ApplicationContext。
     * @param deviceId 设备的唯一标识符。
     * @param signature 应用的签名信息。
     * @param callback 初始化完成后的回调接口。
     * @throws RemoteException 如果绑定服务过程出错。
     */
    public void initArome(Context context, String deviceId, String signature, InitAromeCallBack callback) throws RemoteException {
        // 保存传入的上下文、设备ID、签名信息和回调接口
        this.context = context.getApplicationContext();
        this.deviceId = deviceId;
        this.signature = signature;
        this.initAromeCallBack = callback;

        // 创建服务连接
        this.serviceConnection = new Connection();

        // 设置意图指向特定包和动作以启动服务
        Intent intent = new Intent();
        intent.setPackage("com.zeekrlife.market");
        intent.setAction("zeekrlife.intent.action.AMPE_APPLET_SERVICE_START");

        boolean result = false;
        try {
            // 尝试绑定服务
            result = this.context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable throwable) {
            // 记录绑定服务过程中的异常
            Log.e(TAG, "bind appletService:" + Log.getStackTraceString(throwable));
        }

        // 根据绑定结果记录日志
        if (result) {
            Log.e(TAG, "bind appletService success!");
        } else {
            Log.e(TAG, "bind appletService failure!");
        }
    }


    public void launcherApplet(String appletId, LauncherCallBack callback) throws RemoteException {
        try {
//            Intent intent = IntentUtils.getHomeIntent();
//            context.startActivity(intent);
            registerCommonCallback(CallBackType.LAUNCHERAPPLET, callback);
            appletService.launcherApplet(appletId);
        } catch (Throwable throwable) {
            Log.e(TAG, "launcherApplet" + Log.getStackTraceString(throwable));
        }
    }

    /**
     * 启动指定ID的Applet应用，并以全屏模式运行。
     * @param appletId Applet的唯一标识符。
     * @param callback 启动回调接口，用于处理启动完成后的回调事件。
     * @throws RemoteException 如果远程调用失败则抛出此异常。
     */
    public void launcherAppletWithFullScreen(String appletId, LauncherCallBack callback) throws RemoteException {
        try {
            // 注册通用回调，用于处理启动过程中的回调事件。
            registerCommonCallback(CallBackType.LAUNCHERAPPLET, callback);
            // 调用服务启动指定ID的Applet应用，并以全屏模式运行。
            appletService.launcherAppletWithFullScreen(appletId);
        } catch (Throwable throwable) {
            // 捕获并记录启动过程中可能发生的任何异常。
            Log.e(TAG, "launcherApplet" + Log.getStackTraceString(throwable));
        }
    }

    /**
     * 退出小应用程序的服务调用。
     * 此方法尝试调用appletService的exitApplet方法来退出小应用程序。
     * 如果在尝试退出小应用程序时发生任何异常，它将捕获异常并记录错误信息。
     *
     * @throws RemoteException 如果在调用appletService的exitApplet方法时发生远程异常。
     */
    public void exitApplet() throws RemoteException {
        try {
            // 尝试调用服务方法退出小应用程序
            appletService.exitApplet();
        } catch (Throwable throwable) {
            // 捕获任何异常，并记录错误信息
            Log.e(TAG, "exitApplet" + Log.getStackTraceString(throwable));
        }
    }

    /**
     * 登录方法。
     * 该方法会向appletService发起登录请求，并注册一个登录回调。
     * 如果登录过程中出现任何异常，将会记录日志。
     *
     * @param callback 登录回调接口，用于处理登录结果。
     * @throws RemoteException 如果调用远程方法时发生错误。
     */
    public void login(LoginCallBack callback) throws RemoteException {
        try {
            // 注册一个通用回调，用于处理登录过程中的事件。
            registerCommonCallback(CallBackType.LOGIN, callback);
            // 发起登录请求。
            appletService.login();
        } catch (Throwable throwable) {
            // 捕获并记录登录过程中可能发生的任何异常。
            Log.e(TAG, "login" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 登出功能的实现。
     * 该方法会尝试登出应用，并通过回调通知登出结果。
     *
     * @param callback 登出回调接口，用于接收登出操作的结果。
     * @throws RemoteException 如果在远程服务调用中发生错误。
     */
    public void loginOut(LoginOutCallBack callback) throws RemoteException {
        try {
            // 注册登出操作的回调，并执行登出服务。
            registerCommonCallback(CallBackType.LOGINOUT, callback);
            appletService.logout();
        } catch (Throwable throwable) {
            // 捕获并记录登出过程中可能出现的任何异常。
            Log.e(TAG, "loginOut" + Log.getStackTraceString(throwable));
        }
    }

    /**
     * 预加载指定的Applet。
     * @param appletId Applet的唯一标识符。
     * @param callback 预加载完成后的回调接口。
     * @throws RemoteException 如果远程调用失败则抛出此异常。
     */
    public void preLoad(String appletId, PreLoadCallBack callback) throws RemoteException {
        try {
            // 注册预加载的回调，并启动Applet的预加载操作。
            registerCommonCallback(CallBackType.PRELOAD, callback);
            appletService.preloadApp(appletId);
        } catch (Throwable throwable) {
            // 捕获并记录预加载过程中可能出现的任何异常。
            Log.e(TAG, "preload" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 批量预加载小程序。
     * @param appletIds 小程序ID的列表，这些小程序将被预加载。
     * @param callback 批量预加载完成后的回调接口。
     * @throws RemoteException 如果远程调用失败则抛出此异常。
     */
    public void batchPreLoad(List<String> appletIds, BatchPreLoadCallBack callback) throws RemoteException {
        try {
            // 注册预加载完成的回调，并执行批量预加载操作。
            registerCommonCallback(CallBackType.BATCHPRDLOAD, callback);
            appletService.batchPreloadApp(appletIds);
        } catch (Throwable throwable) {
            // 捕获并记录预加载过程中可能出现的任何异常。
            Log.e(TAG, "batchPreloadApp" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 请求获取指定应用的状态。
     * @param appletId 应用的唯一标识符。
     * @param callback 获取应用状态后的回调接口。
     * @throws RemoteException 如果远程调用失败则抛出此异常。
     */
    public void appStatus(String appletId, AppStatusCallBack callback) throws RemoteException {
        try {
            // 注册应用状态回调
            registerCommonCallback(CallBackType.APPSTATUS, callback);
            // 请求应用状态
            appletService.getAppStatus(appletId);
        } catch (Throwable throwable) {
            // 记录获取应用状态失败的日志
            Log.e(TAG, "appStatus" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 上传日志的函数。
     * 该方法将指定时间范围内的日志上传到服务器，并通过回调函数通知上传结果。
     *
     * @param startDate 开始日期，表示要上传日志的起始时间范围。
     * @param endDate 结束日期，表示要上传日志的结束时间范围。
     * @param callback 回调接口，用于在日志上传完成后接收通知。
     * @throws RemoteException 如果远程调用失败则抛出此异常。
     */
    public void upLoadLog(String startDate, String endDate, UploadLogCallBack callback) throws RemoteException {
        try {
            // 注册回调，以便在日志上传完成后收到通知
            registerCommonCallback(CallBackType.UPLOADLOG, callback);
            // 调用服务上传指定时间范围内的日志
            appletService.uploadLog(startDate, endDate);
        } catch (Throwable throwable) {
            // 捕获并记录上传过程中可能发生的任何异常
            Log.e(TAG, "upLoadLog" + Log.getStackTraceString(throwable));
        }
    }

    /**
     * 获取用户信息的函数。
     * 该方法会调用远程的appletService来获取用户信息，并且支持通过回调的方式来返回获取到的用户信息。
     *
     * @param callback 获取用户信息的回调接口。用于在获取用户信息成功或失败后进行相应的操作。
     * @throws RemoteException 如果调用远程服务时发生错误，则抛出此异常。
     */
    public void userInfo(GetUserInfoCallBack callback) throws RemoteException {
        try {
            // 注册通用回调，并指定回调类型为USERINFO，同时传入回调接口对象。
            registerCommonCallback(CallBackType.USERINFO, callback);
            // 调用appletService的getUserInfo方法来获取用户信息。
            appletService.getUserInfo();
        } catch (Throwable throwable) {
            // 捕获可能发生的任何异常，并记录错误日志。
            Log.e(TAG, "userInfo" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 启动小型服务的函数。
     * <p>
     * 该方法用于启动指定代码的迷你服务。它首先会注册一个回调，然后调用appletService来启动迷你服务。
     * 如果过程中发生异常，会通过日志记录异常信息。
     *
     * @param miniServiceCode 迷你服务的代码，用于标识要启动的服务。
     * @param callback        启动迷你服务后的回调接口。
     * @throws RemoteException 如果远程调用过程中发生异常。
     */
    public void launcherMiNiService(String miniServiceCode, LaunchMiniServiceCallBack callback) throws RemoteException {
        try {
            // 注册回调，以便在迷你服务启动完成后收到通知
            registerCommonCallback(CallBackType.LAUNCHERMINISERVICE, callback);
            // 调用服务启动迷你服务
            appletService.launcherMiniService(miniServiceCode);
        } catch (Throwable throwable) {
            // 捕获并记录任何在启动过程中抛出的异常
            Log.e(TAG, "launcherMiNiService" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 启动自定义服务。
     * 该方法用于通过指定的服务代码和用户身份来启动定制服务。它会注册一个回调，以便在服务启动成功或失败时收到通知。
     *
     * @param customServiceCode 自定义服务的代码，用于识别要启动的服务。
     * @param userIdentity 用户的身份标识，用于鉴权或个性化服务。
     * @param callback 启动自定义服务后的回调接口，用于接收服务启动结果。
     * @throws RemoteException 如果远程调用失败则抛出此异常。
     */
    public void launcherCustomService(String customServiceCode, String userIdentity, LaunchCustomServiceCallBack callback)
        throws RemoteException {
        try {
            // 注册回调，用于处理启动自定义服务后的响应
            registerCommonCallback(CallBackType.LAUNCHERSUSTONSERVICE, callback);
            // 调用服务启动方法
            appletService.launchCustomService(customServiceCode, userIdentity);
        } catch (Throwable throwable) {
            // 捕获并记录启动服务过程中可能发生的任何异常
            Log.e(TAG, "launcherCustomService" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 向当前的桥接请求中添加扩展项。
     * @param extensionList 扩展项列表，包含需要添加到桥接请求中的各项。
     * @param bridgeExtensionParams 桥接请求的扩展参数。
     * @param callback 操作完成后的回调接口。
     * @throws RemoteException 如果注册回调或扩展桥接请求时发生错误，则抛出远程异常。
     */
    public void extendBridgeRequest(List<String> extensionList, String bridgeExtensionParams, BridgeCallBack callback)
        throws RemoteException {
        try {
            // 注册通用回调，并将回调类型设置为BRIDGECALLBACK
            registerCommonCallback(CallBackType.BRIDGECALLBACK, callback);
            // 调用appletService服务，以扩展桥接请求
            appletService.extendBridgeRequest(extensionList, bridgeExtensionParams);
        } catch (Throwable throwable) {
            // 捕获并记录可能发生的任何异常
            Log.e(TAG, "extendBridgeRequest" + Log.getStackTraceString(throwable));
        }
    }


    /**
     * 发送事件到对应的Applet。
     *
     * @param eventName 要发送的事件名称。
     * @param eventData 事件包含的数据。
     * @param callback 发送事件后的回调接口。
     * @throws RemoteException 如果注册回调或发送事件时发生错误。
     */
    public void sendEvent(String eventName, String eventData, SendEventCallBack callback) throws RemoteException {
        try {
            // 注册回调，以便在事件发送完成后收到通知
            registerCommonCallback(CallBackType.SENDEVENT, callback);
            // 调用appletService发送事件
            appletService.bridgeSendEvent(eventName, eventData);
        } catch (Throwable throwable) {
            // 捕获并记录任何在发送事件过程中发生的异常
            Log.e(TAG, "sendEvent" + Log.getStackTraceString(throwable));
        }
    }


//    public void initAromeExt(String deviceId, String locationInfo, InitAromeExtCallBack callback) throws RemoteException {
//        try {
//            registerCommonCallback(CallBackType.INITAROMEEXT, callback);
//            appletService.initExt(deviceId, locationInfo);
//        } catch (Throwable throwable) {
//            Log.e(TAG, "initAromeExt" + Log.getStackTraceString(throwable));
//        }
//    }
//
//    public void registerBiz(String bizType, RegisterBizCallBack callback) throws RemoteException {
//        try {
//            registerCommonCallback(CallBackType.REGISTERBIZ, callback);
//            appletService.registerBiz(bizType);
//        } catch (Throwable throwable) {
//            Log.e(TAG, "registerBiz" + Log.getStackTraceString(throwable));
//        }
//    }
//
//    public void sendRpc(String operationType, String requestData, SendEventCallBack callback) throws RemoteException {
//        try {
//            registerCommonCallback(CallBackType.SENDRPC, callback);
//            appletService.sendRpc(operationType, requestData);
//        } catch (Throwable throwable) {
//            Log.e(TAG, "sendRpc" + Log.getStackTraceString(throwable));
//        }
//    }
//
//    public void loadWidget(String query, boolean showPlaceholder, LoadWidgetCallBack callback) throws RemoteException {
//        try {
//            registerCommonCallback(CallBackType.LOADWIDGET, callback);
//            appletService.loadWidget(query, showPlaceholder);
//        } catch (Throwable throwable) {
//            Log.e(TAG, "loadWidget" + Log.getStackTraceString(throwable));
//        }
//    }
//
//    public void destroyWidget(String widgetId) throws RemoteException {
//        try {
//            appletService.destroyWidget(widgetId);
//        } catch (Throwable throwable) {
//            Log.e(TAG, "destroyWidget" + Log.getStackTraceString(throwable));
//        }
//    }



    /**
     * 注册一个通用回调。
     *
     * @param key 用于标识回调的唯一键。
     * @param callback 要注册的回调对象。
     * @throws RemoteException 如果注册过程中发生远程调用异常。
     */
    public void registerCommonCallback(String key, CallBack callback) throws RemoteException {
        // 将回调与键值对映射，存储到commonCallbackMap中
        commonCallbackMap.put(key, callback);
    }


    public void release() {
        if (context != null && serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
        context = null;
        serviceConnection = null;
        commonCallbackMap.clear();
    }

    /**
     * 确保服务可用。
     * 该方法检查appletService是否为null，以及其Binder是否存活。
     * 如果服务不可用，将通过日志记录错误信息。
     *
     * @return true如果服务可用，否则返回false。
     */
    public boolean ensureServiceAvailable() {
        // 检查appletService是否已经初始化
        if (appletService == null) {
            Log.e(TAG, "service = null");
            return false;
        }
        IBinder binder = appletService.asBinder();
        // 检查获取的IBinder对象是否为null
        if (binder == null) {
            Log.e(TAG, "service.getBinder() = null");
            return false;
        }
        // 检查Binder是否存活
        if (!binder.isBinderAlive()) {
            Log.e(TAG, "service.getBinder().isBinderAlive() = false");
            return false;
        }
        // 通过pingBinder()检查服务是否响应
        if (!binder.pingBinder()) {
            Log.e(TAG, "service.getBinder().pingBinder() = false");
            return false;
        }
        return true;
    }

}

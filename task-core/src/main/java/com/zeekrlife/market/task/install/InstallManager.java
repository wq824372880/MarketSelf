package com.zeekrlife.market.task.install;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.annotation.WorkerThread;
import androidx.core.content.ContextCompat;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.market.task.utils.PackageUtils;
import com.zeekrlife.market.task.utils.ShellUtils;
import com.zeekrlife.net.interception.logging.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstallManager {
    private static final String TAG = "zzzInstallManager";

    private static final long INSTALL_TIMEOUT_IN_MILLIS = 60 * 1000L;

    public static final String ACTION_INSTALL_PACKAGE = "zeekrlife.intent.action.INSTALL_PACKAGE";

    private Context context;
    private PackageManager packageManager;

    /**
     * Android L以下使用
     */
    private BroadcastReceiver installedReceiver;
    private final Map<String, InstallCallback> installCallbackMap = new ConcurrentHashMap<>();

    /**
     * Android L使用
     */
    private PackageInstaller packageInstaller;
    private BroadcastReceiver installedReceiver4L;
    private final Map<Integer, InstallCallback> installCallbackMap4L = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    private volatile static InstallManager instance;

    public static InstallManager getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (InstallManager.class) {
                if (instance == null) {
                    instance = new InstallManager(context);
                }
            }
        }
        return instance;
    }

    public InstallManager(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.packageManager = context.getPackageManager();

        registerInstalledReceiver();

            this.packageInstaller = this.packageManager.getPackageInstaller();
            this.packageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
                /**
                 * 当创建成功时被调用的回调函数。
                 * <p>此函数会检查是否存在与给定会话ID相关的安装回调，并且如果存在，就会调用安装开始的回调方法。</p>
                 *
                 * @param sessionId 会话标识符，用于标识特定的创建会话。
                 */
                @Override
                public void onCreated(int sessionId) {
                    // 记录onCreated方法被调用，包含会话ID信息。
                    LogUtils.e(TAG, "onCreated() called with: sessionId = [" + sessionId + "]");
                    // 检查是否存在与当前会话ID关联的安装回调。
                    if (installCallbackMap4L.containsKey(sessionId)) {
                        // 如果存在，记录日志并获取对应的安装回调对象。
                        Log.d(TAG, "onCreated() called with: sessionId = [" + sessionId + "]");
                        InstallCallback installCallback = installCallbackMap4L.get(sessionId);
                        // 确保installCallback不为空，然后调用installStarted方法。
                        if (installCallback != null) {
                            installCallback.installStarted();
                        }
                    }
                }

                /**
                 * 当标记信息发生变化时回调此方法。
                 * <p>该方法没有返回值，用于响应标记变化事件。</p>
                 *
                 * @param sessionId 会话ID，标识发生标记变化的会话。
                 */
                @Override
                public void onBadgingChanged(int sessionId) {
                    // 此处为处理标记变化的逻辑代码段的起始点，可以根据需要进行相应的逻辑处理。
                }

                /**
                 * 当会话的活动状态发生变化时被调用。
                 *
                 * @param sessionId 会话的ID，用于标识特定的会话。
                 * @param active 指示会话是否处于活动状态的布尔值。true表示会话现在是活动的，false表示会话已变为非活动状态。
                 */
                @Override
                public void onActiveChanged(int sessionId, boolean active) {
                    // 此处为会话活动状态改变时的处理逻辑
                }

                /**
                 * 当安装进度发生变化时回调此方法。
                 * @param sessionId 会话ID，用于标识当前安装会话。
                 * @param progress 安装的进度，是一个浮点数，表示安装的百分比。
                 */
                @Override
                public void onProgressChanged(int sessionId, float progress) {
                    // 使用日志记录函数被调用时的参数信息
                    LogUtils.e(TAG, "onProgressChanged() called with: sessionId = [" + sessionId + "], progress = [" + progress + "]");
                    if (installCallbackMap4L.containsKey(sessionId)) {
                        // 检查installCallbackMap4L中是否包含当前sessionId对应的回调
                        InstallCallback installCallback = installCallbackMap4L.get(sessionId);
                        if (installCallback != null) {
                            // 如果找到回调，则调用其installProgress方法更新安装进度
                            installCallback.installProgress(progress);
                        }
                    }
                }

                /**
                 * 当安装过程完成时被调用。
                 *
                 * @param sessionId 会话ID，用于标识这次安装过程。
                 * @param success 表示安装是否成功。
                 */
                @Override
                public void onFinished(int sessionId, boolean success) {
                    // 在日志中记录onFinished方法被调用的信息，包括sessionId和success。
                    LogUtils.e(TAG, "onFinished() called with: sessionId = [" + sessionId + "], success = [" + success + "]");

                    // 检查是否存在对应sessionId的安装回调。
                    if (installCallbackMap4L.containsKey(sessionId)) {
                        // 在日志中记录sessionId和success，用于调试。
                        Log.d(TAG, "onFinished() called with: sessionId = [" + sessionId + "], success = [" + success + "]");

                        // 获取安装回调对象。
                        InstallCallback installCallback = installCallbackMap4L.get(sessionId);
                        // 如果安装成功，则执行安装完成的回调，并从map中移除该sessionId。
                        if (success) {
                            if (installCallback != null) {
                                // 如果回调不为空，则调用installCompleted方法。
                                installCallback.installCompleted();
                            }
                            // 从安装回调map中移除当前sessionId。
                            installCallbackMap4L.remove(sessionId);
                        }
                        // 使用同步锁，通知等待该sessionId的线程。
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }
            }, new Handler(Looper.getMainLooper()));
            installedReceiver4L = new InstallPackageReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_INSTALL_PACKAGE);
            intentFilter.setPriority(1000);
            this.context.registerReceiver(installedReceiver4L, intentFilter);
    }

    /**
     * 注册一个广播接收器，用于监听应用安装或替换的事件。
     * <p>该方法不接受任何参数，也不返回任何值。</p>
     * <p>主要步骤包括创建一个BroadcastReceiver，重写onReceive方法来处理接收到的广播，
     * 并根据广播的类型（应用安装或替换）来执行相应的逻辑。</p>
     */
    private void registerInstalledReceiver() {
        // 创建并初始化一个BroadcastReceiver，用于监听包安装或替换的广播
        installedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 如果上下文或意图为空，则直接返回
                if (context == null || intent == null) {
                    return;
                }
                // 获取广播的动作
                String action = intent.getAction();
                // 判断广播动作是否为应用安装或替换
                if (Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                    // 从广播意图中获取被安装或替换的包名
                    String packageName = intent.getData().getSchemeSpecificPart();
                    // 获取当前设备上所有已安装的应用信息
                    List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                    for (PackageInfo packageInfo : packageInfos) {
                        // 过滤掉null或applicationInfo为null的PackageInfo
                        if (packageInfo == null || packageInfo.applicationInfo == null) {
                            continue;
                        }
                        // 检查是否找到匹配的包名
                        if (packageName.equals(packageInfo.applicationInfo.packageName)) {
                            // 打印日志，记录包安装完成的信息
                            LogUtils.e(TAG, "package installed with: packageName = [" + packageName + "]");
                            // 构造回调键，并在工作线程中执行回调
                            String key = packageName + "_" + packageInfo.versionCode;
                            ThreadPoolManager.getInstance().execute(() -> {
                                synchronized (lock) {
                                    // 检查是否存在对应的安装回调，并执行回调
                                    if (installCallbackMap.containsKey(key)) {
                                        InstallCallback installCallback = installCallbackMap.get(key);
                                        if (installCallback != null) {
                                            installCallback.installCompleted();
                                        }
                                        // 从回调映射中移除该回调，并通知等待的线程
                                        installCallbackMap.remove(key);
                                        lock.notify();
                                    }
                                }
                            });
                            // 找到匹配项后跳出循环
                            break;
                        }
                    }
                }
            }
        };
        // 创建并设置IntentFilter，用于过滤广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        // 注册广播接收器
        this.context.registerReceiver(installedReceiver, intentFilter);
    }

    /**
     * invoke below Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     *
     * @param filePath
     * @param packageName
     * @param versionCode
     * @param installCallback
     * @return
     */
    @WorkerThread
    public boolean install(@NonNull String filePath, @NonNull String packageName, long versionCode, InstallCallback installCallback) {
        if (PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission()) {
            return installSilent(filePath, installCallback);
        } else {
            return installNormal(filePath, packageName, versionCode, installCallback);
        }
    }

    /**
     * invoke below Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     *
     * @param filePath
     * @param installCallback
     * @return
     */
    @WorkerThread
    @RequiresPermission(Manifest.permission.INSTALL_PACKAGES)
    public boolean installSilent(@NonNull String filePath, InstallCallback installCallback) {
        if (!(PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission())) {
            throw new UnsupportedOperationException("require system/root permission");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            throw new UnsupportedOperationException("this method only can be invoked below Android L");
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "installSilent failed since no permission " + Manifest.permission.INSTALL_PACKAGES);
            if (installCallback != null) {
                installCallback.installError();
            }
            return false;
        }
        if (installCallback != null) {
            installCallback.installStarted();
        }
        boolean result = false;
        try {
            result = PackageUtils.installSilent(context, filePath) == PackageUtils.INSTALL_SUCCEEDED;
        } catch (Throwable throwable) {
            Log.e(TAG, "installSilent", throwable);
        }
        if (installCallback != null) {
            if (result) {
                installCallback.installCompleted();
            } else {
                installCallback.installError();
            }
        }
        return result;
    }

    /**
     * invoke below Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     * <p>
     * Android LOLLIPOP 以下可通过广播 action {@link Intent#ACTION_PACKAGE_REPLACED} 来实现安装成功监听，
     * 但无法监听安装失败，故设置超时时间
     * </p>
     * <p>
     * Android N 需要 FileProvider，见Manifest
     * </p>
     * <p>
     * Android O 需要 {@link Manifest.permission#REQUEST_INSTALL_PACKAGES} 权限，
     * 并通过 {@link PackageManager#canRequestPackageInstalls()} 来判断是否开启安装未知来源应用的能力，
     * 若返回 {@code false}，则需要调整至相关页面开启，但经过调研，直接跳转至安装页面，若该能力未开启，会提示。
     * </p>
     * <p>
     * Uri uri = Uri.parse("package:" + mContext.getPackageName());
     * Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
     * mContext.startActivity(intent);
     * </p>
     *
     * @param filePath
     * @param packageName
     * @param versionCode
     * @param installCallback
     * @return
     */
    @WorkerThread
    public boolean installNormal(@NonNull String filePath, @NonNull String packageName, long versionCode, InstallCallback installCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            throw new UnsupportedOperationException("this method only can be invoked below Android L");
        }
        if (installCallback != null) {
            installCallback.installStarted();
        }
        synchronized (lock) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                context.startActivity(intent);
                if (installCallback != null) {
                    installCallback.installProgress(0);
                }

                String key = packageName + "_" + versionCode;
                installCallbackMap.put(key, installCallback);
                lock.wait(INSTALL_TIMEOUT_IN_MILLIS);
                if (installCallbackMap.containsKey(key)) {
                    InstallCallback callback = installCallbackMap.get(key);
                    if (callback != null) {
                        Log.w(TAG, "installNormal timeout with: filePath = [" + filePath + "], packageName = [" + packageName + "], versionCode = [" + versionCode + "]");
                        callback.installError();
                    }
                    installCallbackMap.remove(key);
                } else {
                    return true;
                }
            } catch (Throwable throwable) {
                Log.e(TAG, "installNormal", throwable);
                String key = packageName + "_" + versionCode;
                if (installCallback != null) {
                    installCallback.installError();
                }
                installCallbackMap.remove(key);
            }
        }
        return false;
    }

    /**
     * invoke above Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     *
     * @param filePath
     * @param packageName
     * @param versionCode
     * @param installCallback
     * @return
     */
    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean install4L(@NonNull String filePath, @NonNull String packageName, long versionCode, InstallCallback installCallback) {
        if (PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission()) {
            return installSilent4L(filePath, packageName, versionCode, installCallback);
        }
        return installNormal4L(filePath, packageName, versionCode, installCallback);
    }

    /**
     * invoke above Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     *
     * @param filePath
     * @param packageName
     * @param versionCode
     * @param installCallback
     * @return
     */
    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.INSTALL_PACKAGES)
    public boolean installSilent4L(@NonNull String filePath, @NonNull String packageName, long versionCode, InstallCallback installCallback) {
        if (!(PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission())) {
            throw new UnsupportedOperationException("require system/root permission");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new UnsupportedOperationException("this method only can be invoked above Android L");
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "installSilent4L failed since no permission " + Manifest.permission.INSTALL_PACKAGES);
            if (installCallback != null) {
                installCallback.installError();
            }
            return false;
        }
        return installSilentOrNormal4L(filePath, packageName, versionCode, installCallback);
    }

    /**
     * invoke above Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     *
     * @param filePath
     * @param packageName
     * @param versionCode
     * @param installCallback
     * @return
     */
    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @RequiresPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
    public boolean installNormal4L(@NonNull String filePath, @NonNull String packageName, long versionCode, InstallCallback installCallback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new UnsupportedOperationException("this method only can be invoked above Android L");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(context, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "installNormal4L failed since no permission " + Manifest.permission.REQUEST_INSTALL_PACKAGES);
            if (installCallback != null) {
                installCallback.installError();
            }
            return false;
        }
        return installSilentOrNormal4L(filePath, packageName, versionCode, installCallback);
    }

    /**
     * invoke above Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     *
     * @param filePath
     * @param packageName
     * @param versionCode
     * @param installCallback
     * @return
     */
    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean installSilentOrNormal4L(@NonNull String filePath, @NonNull String packageName, long versionCode, InstallCallback installCallback) {
        synchronized (lock) {
            int sessionId = 0;
            PackageInstaller.Session session = null;
            try {
                File file = new File(filePath);
                long fileLength = file.length();

                String key = packageName + "_" + versionCode;
                installCallbackMap.put(key, installCallback);

                PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                sessionId = packageInstaller.createSession(sessionParams);
                installCallbackMap4L.put(sessionId, installCallback);
                session = packageInstaller.openSession(sessionId);
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = session.openWrite("temp_" + sessionId + ".apk", 0, fileLength);

                byte[] b = new byte[65536];
                int len;
                double soFarLen = 0;
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                    soFarLen += len;
                    if (fileLength != 0) {
                        float progress = (float) soFarLen / (float) fileLength;
                        session.setStagingProgress(progress);
                    }
                }
                session.fsync(outputStream);
                outputStream.close();
                inputStream.close();
                PendingIntent pendingIntent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_INSTALL_PACKAGE).setPackage(context.getPackageName()), PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_INSTALL_PACKAGE).setPackage(context.getPackageName()), PendingIntent.FLAG_UPDATE_CURRENT);
                }
                session.commit(pendingIntent.getIntentSender());
                lock.wait(INSTALL_TIMEOUT_IN_MILLIS);
                if (installCallbackMap.containsKey(key) && installCallbackMap4L.containsKey(sessionId)) {
                    // 超时或者收到了onFinished(false)的回调，重新查一遍有没有安装成功
                    LogUtils.e(TAG, "installSilentOrNormal4L timeout with: filePath = [" + filePath + "], packageName = [" + packageName + "], versionCode = [" + versionCode + "]");
                    installCallbackMap.remove(key);
                    installCallbackMap4L.remove(sessionId);
                    List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                    for (PackageInfo packageInfo : packageInfos) {
                        if (packageInfo != null) {
                            if (packageName.equals(packageInfo.packageName) && packageInfo.versionCode == versionCode) {
                                if (installCallback != null) {
                                    installCallback.installCompleted();
                                }
                                packageInstallerSessionClose(session);
                                return true;
                            }
                        }
                    }
                    if (installCallback != null) {
                        installCallback.installError();
                    }
                } else {
                    // 安装成功
                    if (installCallbackMap.containsKey(key)) {
                        installCallbackMap.remove(key);
                    }
                    if (installCallbackMap4L.containsKey(sessionId)) {
                        installCallbackMap4L.remove(sessionId);
                    }
                    packageInstallerSessionClose(session);
                    return true;
                }
            } catch (Throwable throwable) {
                LogUtils.e(TAG, "installSilentOrNormal4L"+ throwable);
                if (installCallback != null) {
                    installCallback.installError();
                }
                installCallbackMap.remove(packageName + "_" + versionCode);
                installCallbackMap4L.remove(sessionId);
            }
            packageInstallerSessionClose(session);
            return false;
        }
    }

    private void packageInstallerSessionClose(PackageInstaller.Session session) {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Throwable thr) {
            CommExtKt.logStackTrace(thr);
        }
    }
}

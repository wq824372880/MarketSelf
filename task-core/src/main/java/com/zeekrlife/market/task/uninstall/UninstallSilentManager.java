package com.zeekrlife.market.task.uninstall;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.annotation.WorkerThread;
import androidx.core.content.ContextCompat;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.market.task.utils.PackageInstallerHelper;
import com.zeekrlife.market.task.utils.PackageManagerHelper;
import com.zeekrlife.market.task.utils.PackageUtils;
import com.zeekrlife.market.task.utils.ShellUtils;

import java.util.HashMap;
import java.util.Map;

public class UninstallSilentManager {
    private static final String TAG = UninstallSilentManager.class.getSimpleName();
    private static final long UNINSTALL_TIMEOUT_IN_MILLIS = 3 * 1000L;
    private static final String ACTION_UNINSTALL_PACKAGE = "zeekrlife.intent.action.UNINSTALL_PACKAGE";

    private Context context;
    private PackageManager packageManager;

    /**
     * 是否等待用户操作
     */
    private volatile boolean isPendingUserAction = false;

    /**
     * Android L使用
     */
    private PackageInstaller packageInstaller;
    private BroadcastReceiver uninstalledReceiver4L;
    private final Map<String, UninstallCallback> uninstallCallbackMap4L = new HashMap<>();

    private final Object lock = new Object();

    private volatile static UninstallSilentManager instance;

    public static UninstallSilentManager getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (UninstallSilentManager.class) {
                if (instance == null) {
                    instance = new UninstallSilentManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 该函数是UninstallSilentManager类的构造函数，
     * 用于创建一个UninstallSilentManager对象。它接受一个非空的Context对象作为参数，用于在卸载应用程序时进行一些操作。在函数内部，可能会对传入的Context对象进行一些初始化操作，并准备必要的资源，以便后续调用其他方法完成卸载过程。
     * @param context
     */
    public UninstallSilentManager(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.packageManager = context.getPackageManager();
        if (PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.packageInstaller = this.packageManager.getPackageInstaller();
                uninstalledReceiver4L = new BroadcastReceiver() {
                    /**
                     * 当接收到广播时，处理卸载包的逻辑。
                     *
                     * @param context 上下文，用于访问应用全局功能。
                     * @param intent 携带广播的内容。
                     */
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // 检查传入的参数是否为null
                        if (context == null || intent == null) {
                            return;
                        }
                        // 打印接收到的广播信息
                        Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
                        // 获取广播的动作
                        String action = intent.getAction();
                        // 如果广播的动作不是卸载包，则直接返回
                        if (!ACTION_UNINSTALL_PACKAGE.equals(action)) {
                            return;
                        }
                        // 初始化待用户操作的标志为false
                        isPendingUserAction = false;
                        // 尝试从intent中获取额外信息
                        Bundle extras = intent.getExtras();
                        // 如果额外信息为空，则打印日志并返回
                        if (extras == null) {
                            Log.d(TAG, "extras is null");
                            return;
                        }
                        // 从额外信息中获取卸载状态
                        int status = extras.getInt(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
                        // 从额外信息中获取包名
                        String packageName = extras.getString(PackageInstaller.EXTRA_PACKAGE_NAME, "");
                        // 根据卸载状态处理不同的逻辑
                        switch (status) {
                            case PackageInstaller.STATUS_PENDING_USER_ACTION:
                                // 如果需要用户确认，则设置待用户操作的标志为true，并启动确认Activity
                                Log.d(TAG, "uninstall pending user action");
                                isPendingUserAction = true;
                                Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                                confirmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(confirmIntent);
                                break;
                            case PackageInstaller.STATUS_SUCCESS:
                                // 如果卸载成功，则调用回调函数通知卸载完成，并移除对应的回调对象
                                Log.d(TAG, "uninstall success");
                                if (uninstallCallbackMap4L.containsKey(packageName)) {
                                    UninstallCallback uninstallCallback = uninstallCallbackMap4L.get(packageName);
                                    if (uninstallCallback != null) {
                                        uninstallCallback.uninstallCompleted();
                                    }
                                    uninstallCallbackMap4L.remove(packageName);
                                    synchronized (lock) {
                                        lock.notify();
                                    }
                                }
                                break;
                            // 处理卸载失败的各种状态
                            case PackageInstaller.STATUS_FAILURE:
                            case PackageInstaller.STATUS_FAILURE_BLOCKED:
                            case PackageInstaller.STATUS_FAILURE_ABORTED:
                            case PackageInstaller.STATUS_FAILURE_INVALID:
                            case PackageInstaller.STATUS_FAILURE_CONFLICT:
                            case PackageInstaller.STATUS_FAILURE_STORAGE:
                            case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                            default:
                                // 打印失败原因，并调用回调函数通知卸载错误
                                Log.w(TAG, "uninstall failure, status: " + status);
                                if (uninstallCallbackMap4L.containsKey(packageName)) {
                                    UninstallCallback uninstallCallback = uninstallCallbackMap4L.get(packageName);
                                    if (uninstallCallback != null) {
                                        uninstallCallback.uninstallError();
                                    }
                                    uninstallCallbackMap4L.remove(packageName);
                                    synchronized (lock) {
                                        lock.notify();
                                    }
                                }
                                break;
                        }
                    }
                };
                IntentFilter intentFilter = new IntentFilter();
                //intentFilter.addAction("ecarx.intent.action.UNINSTALL_PACKAGE");
                intentFilter.addAction(ACTION_UNINSTALL_PACKAGE);
                this.context.registerReceiver(uninstalledReceiver4L, intentFilter);
            }
        }
    }

    /**
     * invoke below Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     */
    @WorkerThread
    @RequiresPermission(Manifest.permission.DELETE_PACKAGES)
    public boolean uninstallSilent(@NonNull String packageName, boolean keepData, UninstallCallback uninstallCallback) {
        if (!(PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission())) {
            throw new UnsupportedOperationException("require system/root permission");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            throw new UnsupportedOperationException("this method only can be invoked below Android L");
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.DELETE_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "uninstallSilent failed since no permission " + Manifest.permission.DELETE_PACKAGES);
            if (uninstallCallback != null) {
                uninstallCallback.uninstallError();
            }
            return false;
        }
        if (uninstallCallback != null) {
            uninstallCallback.uninstallStarted();
        }
        boolean result = false;
        try {
            result = PackageUtils.uninstallSilent(context, packageName, keepData) == PackageUtils.DELETE_SUCCEEDED;
        } catch (Throwable throwable) {
            Log.e(TAG, "uninstallSilent", throwable);
        }
        if (uninstallCallback != null) {
            if (result) {
                uninstallCallback.uninstallCompleted();
            } else {
                uninstallCallback.uninstallError();
            }
        }
        return result;
    }

    /**
     * invoke above Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     */
    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.DELETE_PACKAGES)
    public boolean uninstallSilent4L(@NonNull String packageName, UninstallCallback uninstallCallback) {
        return uninstallSilent4L(packageName, false, uninstallCallback);
    }

    /**
     * invoke above Android 21 {@link Build.VERSION_CODES#LOLLIPOP}
     * keepData 暂时无效处理，目前暂无卸载保留数据需求
     */
    @Deprecated
    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.DELETE_PACKAGES)
    public boolean uninstallSilent4L(@NonNull String packageName, boolean keepData, UninstallCallback uninstallCallback) {
        if (!(PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission())) {
            throw new UnsupportedOperationException("require system/root permission");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new UnsupportedOperationException("this method only can be invoked above Android L");
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.DELETE_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "uninstallSilent4L failed since no permission " + Manifest.permission.DELETE_PACKAGES);
            if (uninstallCallback != null) {
                uninstallCallback.uninstallError();
            }
            return false;
        }
        synchronized (lock) {
            try {
                uninstallCallbackMap4L.put(packageName, uninstallCallback);
                PendingIntent pendingIntent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    pendingIntent =
                        PendingIntent.getBroadcast(context, 0, new Intent(ACTION_UNINSTALL_PACKAGE).setPackage(context.getPackageName()),
                            PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent =
                        PendingIntent.getBroadcast(context, 0, new Intent(ACTION_UNINSTALL_PACKAGE).setPackage(context.getPackageName()),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                }
                boolean result =
                    PackageInstallerHelper.uninstall(packageInstaller, packageName, keepData ? PackageManagerHelper.DELETE_KEEP_DATA() : 0,
                        pendingIntent.getIntentSender());
                if (!result) {
                    if (uninstallCallback != null) {
                        uninstallCallback.uninstallError();
                    }
                    uninstallCallbackMap4L.remove(packageName);
                    return false;
                }
                lock.wait(UNINSTALL_TIMEOUT_IN_MILLIS);
                if (uninstallCallbackMap4L.containsKey(packageName)) {
                    if (!isPendingUserAction) {
                        uninstallCallbackMap4L.remove(packageName);
                    }
                } else {
                    return true;
                }
            } catch (Throwable throwable) {
                Log.e(TAG, "uninstallSilent4L", throwable);
                if (uninstallCallback != null) {
                    uninstallCallback.uninstallError();
                }
                uninstallCallbackMap4L.remove(packageName);
            }
            return false;
        }
    }

    /**
     * 移除uninstallCallback
     */
    public void removeUninstallSilent4LCallBack(String packageName, UninstallCallback uninstallCallback) {
        try {
            synchronized (lock) {
                if (uninstallCallbackMap4L.containsKey(packageName) && uninstallCallbackMap4L.get(packageName) == uninstallCallback) {
                    uninstallCallbackMap4L.remove(packageName);
                }
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }
}

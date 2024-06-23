package com.zeekrlife.market.update;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author
 */
class MarketAppUpdateManager extends IAppCheckUpdater.Stub {

    private static final String TAG = "AppUpdateManager";

    private Context context;

    private ServiceConnection serviceConnection;

    private IAppCheckUpdater appCheckUpdater;

    private volatile static MarketAppUpdateManager instance;
    private Intent intent;

    public static MarketAppUpdateManager getInstance() {
        if (instance == null) {
            synchronized (MarketAppUpdateManager.class) {
                if (instance == null) {
                    instance = new MarketAppUpdateManager();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean checkAppUpdate(String packageName, ICheckUpdateCallback callback) throws RemoteException {
        Log.e(TAG, "AppCheckUpdateService checkAppUpdate : packageName -> " + packageName);
        if (!ensureServiceAvailable()) {
            Log.e(TAG, "AppCheckUpdateService service not available");
            return false;
        }
        return appCheckUpdater.checkAppUpdate(packageName, callback);
    }

    @Override
    public boolean hasAvailableVersion(String packageName, IAvailableVersionCallback callback) throws RemoteException {
        Log.e(TAG, "AppCheckUpdateService hasAvailableVersion : packageName -> " + packageName);
        if (!ensureServiceAvailable()) {
            Log.e(TAG, "AppCheckUpdateService service not available");
            return false;
        }
        return appCheckUpdater.hasAvailableVersion(packageName, callback);
    }

    public class Connection implements ServiceConnection {

        private final OnInitCallback onInitCallback;

        public Connection(OnInitCallback onInitCallback) {
            this.onInitCallback = onInitCallback;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "AppCheckUpdateService connected!");

            appCheckUpdater = Stub.asInterface(service);

            if (onInitCallback != null) {
                boolean result = appCheckUpdater != null;
                onInitCallback.onInit(result);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "AppCheckUpdateService disconnected!");
            appCheckUpdater = null;
        }
    }

    public interface OnInitCallback {
        void onInit(boolean result);
    }

    @SuppressLint("NewApi")
    public void init(Context context, OnInitCallback callback) {
        if (ensureServiceAvailable()) {
            callback.onInit(true);
            return;
        }
        this.context = context.getApplicationContext();
        this.serviceConnection = new Connection(callback);

        if (intent == null) {
            intent = new Intent();
            intent.setPackage("com.zeekrlife.market");
            intent.setAction("zeekr.intent.action.APPSTORE_UPDATE_SERVICE");
        }
        boolean result = false;
        try {
            result = this.context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable throwable) {
            Log.e(TAG, "bind AppCheckUpdateService:" + Log.getStackTraceString(throwable));
        }

        if (result) {
            Log.d(TAG, "bind AppCheckUpdateService success!");
        } else {
            Log.e(TAG, "bind AppCheckUpdateService failure!");
            if (callback != null) {
                callback.onInit(false);
            }
        }
    }

    public void release() {
        if (context != null && serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
        context = null;
        serviceConnection = null;
    }

    public boolean ensureServiceAvailable() {
        try {
            if (appCheckUpdater == null) {
                Log.e(TAG, "service = null");
                return false;
            }
            IBinder binder = appCheckUpdater.asBinder();
            if (binder == null) {
                Log.e(TAG, "service.getBinder() = null");
                return false;
            }
            if (!binder.isBinderAlive()) {
                Log.e(TAG, "service.getBinder().isBinderAlive() = false");
                return false;
            }
            if (!binder.pingBinder()) {
                Log.e(TAG, "service.getBinder().pingBinder() = false");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

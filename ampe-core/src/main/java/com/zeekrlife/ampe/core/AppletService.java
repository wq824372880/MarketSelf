package com.zeekrlife.ampe.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.zeekrlife.common.ext.CommExtKt;


public class AppletService extends Service {
    private static final String TAG = "AppletService";
    private AppletServiceImpl appletService;
    private final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "AppletService_channel";
    private final static String DEFAULT_NOTIFICATION_CHANNEL_NAME = "AppletService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return appletService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("bind", "onCreate() called");
        serviceForeground();
        appletService = new AppletServiceImpl(getApplication());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    private void serviceForeground() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = new NotificationChannel(
                        DEFAULT_NOTIFICATION_CHANNEL_ID,
                        DEFAULT_NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_LOW
                );
                notificationManager.createNotificationChannel(notificationChannel);
                Notification notification = new NotificationCompat.Builder(
                                this,
                                DEFAULT_NOTIFICATION_CHANNEL_ID
                        ).setAutoCancel(true)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setOngoing(true)
                        .setPriority(NotificationManager.IMPORTANCE_LOW)
                        .build();
                startForeground(615, notification);
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

}

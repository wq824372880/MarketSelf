package com.zeekrlife.common.util;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.content.Context.WIFI_SERVICE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import androidx.annotation.RequiresPermission;

import com.zeekr.basic.Common;
import com.zeekrlife.common.ext.CommExtKt;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class NetworkUtils {

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    public enum NetworkType {
        /**
         * network ethernet
         */
        NETWORK_ETHERNET,
        /**
         * network wifi
         */
        NETWORK_WIFI,
        /**
         * network 5g
         */
        NETWORK_5G,
        /**
         * network 4g
         */
        NETWORK_4G,
        /**
         * network 3g
         */
        NETWORK_3G,
        /**
         * network 2g
         */
        NETWORK_2G,
        /**
         * network unknown
         */
        NETWORK_UNKNOWN,
        /**
         * network no
         */
        NETWORK_NO
    }

    /**
     * Open the settings of wireless.
     */
    public static void openWirelessSettings() {
        Common.app.startActivity(
                new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * Return whether network is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected() {
//        NetworkInfo info = getActiveNetworkInfo();
//        return info != null && info.isConnected();
        ConnectivityManager connectivityManager = (ConnectivityManager) Common.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            network = connectivityManager.getActiveNetwork();
        }
        if (network != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null) {
                return isNetworkValid(capabilities);
            }
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    public static boolean isNetworkValid(NetworkCapabilities capabilities) {
        if (capabilities != null) {
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || capabilities.hasTransport(7)  //目前已知在车联网行业使用该标记作为网络类型（TBOX 网络类型）
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    || capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        return false;
    }

    /**
     * Return whether network is available using domain.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByDns() {
        return isAvailableByDns("");
    }

    /**
     * Return whether network is available using domain.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain The name of domain.
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByDns(final String domain) {
        final String realDomain = TextUtils.isEmpty(domain) ? "www.baidu.com" : domain;
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(realDomain);
            return inetAddress != null;
        } catch (UnknownHostException e) {
            CommExtKt.logStackTrace(e);
            return false;
        }
    }

    /**
     * Return whether mobile data is enabled.
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    public static boolean isMobileDataEnabled() {
        try {
            TelephonyManager tm =
                    (TelephonyManager) Common.app.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.isDataEnabled();
            }
            @SuppressLint("PrivateApi")
            Method isMobileDataEnabledMethod =
                    tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != isMobileDataEnabledMethod) {
                return (boolean) isMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
        return false;
    }

    /**
     * Return whether using mobile data.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isMobileData() {
        NetworkInfo info = getActiveNetworkInfo();
        return null != info
                && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Return whether using 4G.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean is4G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null
                && info.isAvailable()
                && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * Return whether using 4G.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
//    @RequiresPermission(ACCESS_NETWORK_STATE)
//    public static boolean is5G() {
//        NetworkInfo info = getActiveNetworkInfo();
//        return info != null
//                && info.isAvailable()
//                && info.getSubtype() == TelephonyManager.NETWORK_TYPE_NR;
//    }

    /**
     * Return whether wifi is enabled.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static boolean isWifiEnabled() {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) Common.app.getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return false;
        }
        return manager.isWifiEnabled();
    }

    /**
     * Enable or disable wifi.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    public static void setWifiEnabled(final boolean enabled) {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) Common.app.getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return;
        }
        if (enabled == manager.isWifiEnabled()) {
            return;
        }
        manager.setWifiEnabled(enabled);
    }

    /**
     * Return whether wifi is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) Common.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Return the name of network operate.
     *
     * @return the name of network operate
     */
    public static String getNetworkOperatorName() {
        TelephonyManager tm =
                (TelephonyManager) Common.app.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "";
        }
        return tm.getNetworkOperatorName();
    }

    /**
     * Return type of network.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return type of network
     * <ul>
     * <li>{@link NetworkType#NETWORK_ETHERNET} </li>
     * <li>{@link NetworkType#NETWORK_WIFI    } </li>
     * <li>{@link NetworkType#NETWORK_4G      } </li>
     * <li>{@link NetworkType#NETWORK_3G      } </li>
     * <li>{@link NetworkType#NETWORK_2G      } </li>
     * <li>{@link NetworkType#NETWORK_UNKNOWN } </li>
     * <li>{@link NetworkType#NETWORK_NO      } </li>
     * </ul>
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static NetworkType getNetworkType() {
        if (isEthernet()) {
            return NetworkType.NETWORK_ETHERNET;
        }
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetworkType.NETWORK_2G;

                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetworkType.NETWORK_3G;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetworkType.NETWORK_4G;

//                    case TelephonyManager.NETWORK_TYPE_NR:
//                        return NetworkType.NETWORK_5G;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetworkType.NETWORK_3G;
                        } else {
                            return NetworkType.NETWORK_UNKNOWN;
                        }
                }
            } else {
                return NetworkType.NETWORK_UNKNOWN;
            }
        }
        return NetworkType.NETWORK_NO;
    }

    /**
     * Return whether using ethernet.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static boolean isEthernet() {
        final ConnectivityManager cm =
                (ConnectivityManager) Common.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        final NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (info == null) {
            return false;
        }
        NetworkInfo.State state = info.getState();
        if (null == state) {
            return false;
        }
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING;
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm =
                (ConnectivityManager) Common.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return null;
        }
        return cm.getActiveNetworkInfo();
    }

    /**
     * Return the ip address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis != null && nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) {
                            return hostAddress;
                        }
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            CommExtKt.logStackTrace(e);
        }
        return "";
    }

    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis != null && nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0, size = ias.size(); i < size; i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        return broadcast.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            CommExtKt.logStackTrace(e);
        }
        return "";
    }

    /**
     * Return the domain address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain The name of domain.
     * @return the domain address
     */
    @RequiresPermission(INTERNET)
    public static String getDomainAddress(final String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            CommExtKt.logStackTrace(e);
            return "";
        }
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Common.app.getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    /**
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getGatewayByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Common.app.getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getNetMaskByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Common.app.getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getServerAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Common.app.getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }

    /**
     * Return the ssid.
     *
     * @return the ssid.
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getSSID() {
        WifiManager wm = (WifiManager) Common.app.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        WifiInfo wi = wm.getConnectionInfo();
        if (wi == null) {
            return "";
        }
        String ssid = wi.getSSID();
        if (TextUtils.isEmpty(ssid)) {
            return "";
        }
        if (ssid.length() > 2 && ssid.charAt(0) == '"' && ssid.charAt(ssid.length() - 1) == '"') {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * Register the status of network changed listener.
     *
     * @param listener The status of network changed listener
     */
    public static void registerNetworkStatusChangedListener(final OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().registerListener(listener);
    }

    /**
     * Return whether the status of network changed listener has been registered.
     *
     * @param listener The listener
     * @return true to registered, false otherwise.
     */
    public static boolean isRegisteredNetworkStatusChangedListener(final OnNetworkStatusChangedListener listener) {
        return NetworkChangedReceiver.getInstance().isRegistered(listener);
    }

    /**
     * Unregister the status of network changed listener.
     *
     * @param listener The status of network changed listener.
     */
    public static void unregisterNetworkStatusChangedListener(final OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().unregisterListener(listener);
    }

    public static final class NetworkChangedReceiver extends BroadcastReceiver {

        private static NetworkChangedReceiver getInstance() {
            return LazyHolder.INSTANCE;
        }

        private NetworkType mType;
        private Set<OnNetworkStatusChangedListener> mListeners = new HashSet<>();

        void registerListener(final OnNetworkStatusChangedListener listener) {
            if (listener == null) {
                return;
            }
            MAIN_THREAD.post(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    int preSize = mListeners.size();
                    mListeners.add(listener);
                    if (preSize == 0 && mListeners.size() == 1) {
                        mType = getNetworkType();
                        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        Common.app.registerReceiver(NetworkChangedReceiver.getInstance(), intentFilter);
                    }
                }
            });
        }

        boolean isRegistered(final OnNetworkStatusChangedListener listener) {
            if (listener == null) {
                return false;
            }
            return mListeners.contains(listener);
        }

        void unregisterListener(final OnNetworkStatusChangedListener listener) {
            if (listener == null) {
                return;
            }
            MAIN_THREAD.post(new Runnable() {
                @Override
                public void run() {
                    int preSize = mListeners.size();
                    mListeners.remove(listener);
                    if (preSize == 1 && mListeners.size() == 0) {
                        Common.app.unregisterReceiver(NetworkChangedReceiver.getInstance());
                    }
                }
            });
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                // debouncing
                MAIN_THREAD.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NetworkType networkType = NetworkUtils.getNetworkType();
                        if (mType == networkType) {
                            return;
                        }
                        mType = networkType;
                        if (networkType == NetworkType.NETWORK_NO) {
                            for (OnNetworkStatusChangedListener listener : mListeners) {
                                listener.onDisconnected();
                            }
                        } else {
                            for (OnNetworkStatusChangedListener listener : mListeners) {
                                listener.onConnected(networkType);
                            }
                        }
                    }
                }, 1000);
            }
        }

        private static class LazyHolder {
            private static final NetworkChangedReceiver INSTANCE = new NetworkChangedReceiver();
        }
    }

    public interface OnNetworkStatusChangedListener {
        void onDisconnected();

        void onConnected(NetworkType networkType);
    }
}

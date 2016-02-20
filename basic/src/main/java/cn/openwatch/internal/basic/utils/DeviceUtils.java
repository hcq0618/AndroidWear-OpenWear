package cn.openwatch.internal.basic.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.util.UUID;

public final class DeviceUtils {

    private static Boolean isWearableDevice;

    private DeviceUtils() {
    }

    public static String getCacheDir(Context context) {
        String cachePath;
        // 从Android4.4开始，应用可以管理在它外部存储上的特定包名目录，而不用获取WRITE_EXTERNAL_STORAGE权限。
        // 应用在外部存储上的缓存目录。
        File file = context.getExternalCacheDir();
        // 目录若被其他进程占用了 也会返回null
        // http://stackoverflow.com/questions/16562165/getexternalcachedir-returns-null-after-clearing-data
        if (file != null) {
            cachePath = file.getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public enum NetworkType {
        NETWORK_UNKNOWN, NETWORK_2_G, NETWORK_3_G, NETWORK_4_G, NETWORK_WIFI, NO_NETWORK;

        @Override
        public String toString() {
            switch (this) {
                case NETWORK_2_G:
                    return "2g";
                case NETWORK_3_G:
                    return "3g";
                case NETWORK_4_G:
                    return "4g";
                case NETWORK_WIFI:
                    return "wifi";

                case NO_NETWORK:
                    return "none";

                default:
                    return "unknown";
            }
        }
    }

    // <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    public static String getMacAddress(Context context) {
        String macAddress = null;
        try {
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
            if (null != info) {
                macAddress = info.getMacAddress();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        return macAddress == null ? "" : macAddress;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /* <uses-permission android:name="android.permission.READ_PHONE_STATE"/> */
    public static String getDeviceId(Context cx) {
        TelephonyManager tm = (TelephonyManager) cx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId() == null ? "" : tm.getDeviceId();
    }

    public static String getAndroidId(Context cx) {
        String androidId = Secure.getString(cx.getContentResolver(), Secure.ANDROID_ID);
        if (androidId == null)
            androidId = "";

        return androidId;
    }

    @TargetApi(13)
    @SuppressWarnings("deprecation")
    public static int[] getScreenSize(Context cx) {
        WindowManager wm = (WindowManager) cx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int[] size = new int[2];

        if (Build.VERSION.SDK_INT >= 13) {
            Point point = new Point();
            display.getSize(point);
            size[0] = point.x;
            size[1] = point.y;
        } else {
            size[0] = display.getWidth();
            size[1] = display.getHeight();
        }
        return size;
    }

    // 1、正常情况下可以通过((TelephonyManager) s_instance.getSystemService(
    // Context.TELEPHONY_SERVICE )).getDeviceId(); 来获取，但是某些平板电脑此函数会返回空
    //
    // 2、通过 Secure.getString(s_instance.getContentResolver(),
    // Secure.ANDROID_ID); 也可以获取到一个id，但是android2.2或者是某些山寨手机使用这个也是有问题的，它会返回一个固定的值
    // 9774d56d682e549c
    //
    // http://stackoverflow.com/questions/3226135/android-udid-like-iphone
    // <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    // <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    public static String getUdid(Context context) {
        String tmDevice = getDeviceId(context);
        String macAddress = getMacAddress(context);
        String androidId = getAndroidId(context);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | macAddress.hashCode());

        return deviceUuid.toString();
    }

    // http://www.binkery.com/archives/368.html
    // http://blog.csdn.net/kesenhoo/article/details/7057448
    // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    public static NetworkType getNetworkType(Context context) {
        NetworkType type = NetworkType.NETWORK_UNKNOWN;

        ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi != null && mWifi.isAvailable() && mWifi.isConnectedOrConnecting()) {
            type = NetworkType.NETWORK_WIFI;
        } else {

            NetworkInfo mMobile = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mMobile != null && mMobile.isAvailable() && mMobile.isConnectedOrConnecting()) {
                int subType = mMobile.getSubtype();

                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        type = NetworkType.NETWORK_2_G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        type = NetworkType.NETWORK_3_G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        type = NetworkType.NETWORK_4_G;
                        break;
                }

            } else {
                type = NetworkType.NO_NETWORK;
            }
        }
        return type;
    }

    /**
     * 检测网络是否连接
     *
     * @param cx context
     * @return 网络是否连接
     */
    // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    public static boolean isNetworkAvailable(Context cx) {
        ConnectivityManager manager = (ConnectivityManager) cx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isAvailable();
    }

    /**
     * 是否为手表设备
     * adb shell getprop
     * adb shell setprop
     *
     * @param context context
     * @return boolean 是否为手表设备
     */
    public static synchronized boolean isWearableDevice(Context context) {
        if (isWearableDevice == null) {
            String property = SystemPropertiesUtils.get(context, "ro.build.characteristics");
            isWearableDevice = !TextUtils.isEmpty(property) && property.contains("watch");
        }

        return isWearableDevice;
    }
}

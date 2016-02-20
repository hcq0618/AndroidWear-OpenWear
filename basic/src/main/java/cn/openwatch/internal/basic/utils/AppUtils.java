package cn.openwatch.internal.basic.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class AppUtils {

    private AppUtils() {
    }

    public static String getAppName(Context cx) {
        return cx.getString(cx.getApplicationInfo().labelRes);
    }

    public static String getVersionName(Context cx) {
        String versionName = "";
        try {
            versionName = cx.getApplicationContext().getPackageManager()
                    .getPackageInfo(cx.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
        }

        return versionName;
    }

    public static int getVersionCode(Context cx) {
        int versionCode = -1;
        try {
            versionCode = cx.getApplicationContext().getPackageManager()
                    .getPackageInfo(cx.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
        }

        return versionCode;
    }

    public static String getMeta(Context cx, String meta) {
        try {
            ApplicationInfo appInfo = cx
                    .getApplicationContext()
                    .getPackageManager()
                    .getApplicationInfo(cx.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(meta);
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean isAppInstalled(Context cx, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;

        try {
            cx.getApplicationContext()
                    .getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isMyAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return true;
    }

    public static boolean checkAppPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED ==
                context.getPackageManager().checkPermission(permission, context.getPackageName());
    }

    //获取apk文件的签名信息
    public static String getApkSignature(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
            Class<?>[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser;
            if (Build.VERSION.SDK_INT > 19) {
                pkgParser = pkgParserCls.newInstance();
            } else {
                Constructor constructor = pkgParserCls.getConstructor(typeArgs);
                pkgParser = constructor.newInstance(valueArgs);
            }

            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();

            Object pkgParserPkg;
            if (Build.VERSION.SDK_INT > 19) {
                typeArgs = new Class[2];
                typeArgs[0] = File.class;
                typeArgs[1] = int.class;

                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                        "parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[2];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                        valueArgs);
            } else {
                typeArgs = new Class[4];
                typeArgs[0] = File.class;
                typeArgs[1] = String.class;
                typeArgs[2] = DisplayMetrics.class;
                typeArgs[3] = int.class;

                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                        "parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[4];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = apkPath;
                valueArgs[2] = metrics;
                valueArgs[3] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                        valueArgs);
            }


            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = int.class;
            Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return info[0].toCharsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //获取已安装app的签名信息
    public static String getInstallPackageSignature(Context context,
                                                    String packageName) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm
                .getInstalledPackages(PackageManager.GET_SIGNATURES);

        for (PackageInfo packageinfo : apps
                ) {
            String thisName = packageinfo.packageName;
            if (thisName.equals(packageName)) {
                return packageinfo.signatures[0].toCharsString();
            }
        }

        return null;
    }
}

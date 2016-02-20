package cn.openwatch.internal.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.io.InputStream;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import cn.openwatch.internal.google.android.gms.internal.jf;
import cn.openwatch.internal.google.android.gms.internal.jo;
import cn.openwatch.internal.google.android.gms.internal.lc;
import cn.openwatch.internal.google.android.gms.internal.ll;

public final class GooglePlayServicesUtil {
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = 6587000;
    public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
    public static final String GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending";
    public static boolean Jg = false;
    public static boolean Jh = false;
    private static int Ji = -1;
    private static final Object Jj = new Object();

    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case 0:
                return "SUCCESS";
            case 1:
                return "SERVICE_MISSING";
            case 2:
                return "SERVICE_VERSION_UPDATE_REQUIRED";
            case 3:
                return "SERVICE_DISABLED";
            case 4:
                return "SIGN_IN_REQUIRED";
            case 5:
                return "INVALID_ACCOUNT";
            case 6:
                return "RESOLUTION_REQUIRED";
            case 7:
                return "NETWORK_ERROR";
            case 8:
                return "INTERNAL_ERROR";
            case 9:
                return "SERVICE_INVALID";
            case 10:
                return "DEVELOPER_ERROR";
            case 11:
                return "LICENSE_CHECK_FAILED";
        }
        return "UNKNOWN_ERROR_CODE";
    }

    public static int isGooglePlayServicesAvailable(Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        if (!jf.Mv) {
            D(context);
        }
        PackageInfo localPackageInfo1;
        try {
            localPackageInfo1 = localPackageManager.getPackageInfo("com.google.android.gms", 64);
        } catch (NameNotFoundException localNameNotFoundException) {
//			Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
            return 1;
        }
        if (lc.aU(localPackageInfo1.versionCode)) {
        } else if (lc.K(context)) {
            if (a(localPackageInfo1, b.Ix) == null) {
//				Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
                return 9;
            }
        } else {

            PackageInfo packageInfo;
            try {
                packageInfo = localPackageManager.getPackageInfo("com.android.vending", 64);
            } catch (NameNotFoundException localNameNotFoundException4) {
//				Log.w("GooglePlayServicesUtil", "Google Play Store is missing.");
                return 9;
            }
            byte[] arrayOfByte = a(packageInfo, b.Ix);
            if (arrayOfByte == null) {
//				Log.w("GooglePlayServicesUtil", "Google Play Store signature invalid.");
                return 9;
            }
            if (a(localPackageInfo1, new byte[][]{arrayOfByte}) == null) {
//				Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
                return 9;
            }
        }

        return 0;
    }

    public static void C(Context paramContext)
            throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        int i = isGooglePlayServicesAvailable(paramContext);
        if (i != 0) {
            Intent localIntent = aj(i);
//			Log.e("GooglePlayServicesUtil", "GooglePlayServices not available due to error " + i);
            if (localIntent == null) {
                throw new GooglePlayServicesNotAvailableException(i);
            }
            throw new GooglePlayServicesRepairableException(i, "Google Play Services not available", localIntent);
        }
    }

    private static void D(Context paramContext) {
        ApplicationInfo localApplicationInfo = null;
        try {
            localApplicationInfo = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(),
                    128);
        } catch (NameNotFoundException localNameNotFoundException) {
//			Log.wtf("GooglePlayServicesUtil", "This should never happen.", localNameNotFoundException);
        }
        Bundle localBundle = localApplicationInfo.metaData;
        if (localBundle != null) {
            int i = localBundle.getInt("com.google.android.gms.version");
            if (i == 6587000) {
                return;
            }
            throw new IllegalStateException(
                    "The meta-data tag in your app's AndroidManifest.xml does not have the right value.  Expected 6587000 but found "
                            + i + ".  You must have the" + " following declaration within the <application> element: "
                            + "    <meta-data android:name=\"" + "com.google.android.gms.version"
                            + "\" android:value=\"@integer/google_play_services_version\" />");
        }
        throw new IllegalStateException(
                "A required meta-data tag in your app's AndroidManifest.xml does not exist.  You must have the following declaration within the <application> element:     <meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />");
    }

    public static boolean isGoogleSignedUid(PackageManager packageManager, int uid) {
        if (packageManager == null) {
            throw new SecurityException("Unknown error: invalid Package Manager");
        }
        String[] arrayOfString = packageManager.getPackagesForUid(uid);
        if ((arrayOfString.length == 0) || (!b(packageManager, arrayOfString[0]))) {
            throw new SecurityException("Uid is not Google Signed");
        }
        return true;
    }

    public static boolean b(PackageManager paramPackageManager, String paramString) {
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = paramPackageManager.getPackageInfo(paramString, 64);
        } catch (NameNotFoundException localNameNotFoundException) {
//			if (Log.isLoggable("GooglePlayServicesUtil", 3)) {
//				Log.d("GooglePlayServicesUtil",
//						"Package manager can't find package " + paramString + ", defaulting to false");
//			}
            return false;
        }
        return a(paramPackageManager, localPackageInfo);
    }

    public static boolean a(PackageManager paramPackageManager, PackageInfo paramPackageInfo) {
        if (paramPackageInfo == null) {
            return false;
        }
        if (c(paramPackageManager)) {
            return a(paramPackageInfo, true) != null;
        }
        boolean bool = a(paramPackageInfo, false) != null;
        if ((!bool) && (a(paramPackageInfo, true) != null)) {
//			Log.w("GooglePlayServicesUtil", "Test-keys aren't accepted on this build.");
        }
        return bool;
    }

    private static byte[] a(PackageInfo paramPackageInfo, boolean paramBoolean) {
        if (paramPackageInfo.signatures.length != 1) {
//			Log.w("GooglePlayServicesUtil", "Package has more than one signature.");
            return null;
        }
        byte[] arrayOfByte = paramPackageInfo.signatures[0].toByteArray();
        Set<byte[]> localSet;
        if (paramBoolean) {
            localSet = b.gu();
        } else {
            localSet = b.gv();
        }
        if (localSet.contains(arrayOfByte)) {
            return arrayOfByte;
        }
//		if (Log.isLoggable("GooglePlayServicesUtil", 2)) {
//			Log.v("GooglePlayServicesUtil", "Signature not valid.  Found: \n" + Base64.encodeToString(arrayOfByte, 0));
//		}
        return null;
    }

    private static byte[] a(PackageInfo paramPackageInfo, byte[]... paramVarArgs) {
        if (paramPackageInfo.signatures.length != 1) {
//			Log.w("GooglePlayServicesUtil", "Package has more than one signature.");
            return null;
        }
        byte[] arrayOfByte1 = paramPackageInfo.signatures[0].toByteArray();
        for (int i = 0; i < paramVarArgs.length; i++) {
            byte[] arrayOfByte2 = paramVarArgs[i];
            if (Arrays.equals(arrayOfByte2, arrayOfByte1)) {
                return arrayOfByte2;
            }
        }
//		if (Log.isLoggable("GooglePlayServicesUtil", 2)) {
//			Log.v("GooglePlayServicesUtil", "Signature not valid.  Found: \n" + Base64.encodeToString(arrayOfByte1, 0));
//		}
        return null;
    }

    public static Intent aj(int paramInt) {
        switch (paramInt) {
            case 1:
            case 2:
                return jo.ba("com.google.android.gms");
            case 42:
                return jo.hE();
            case 3:
                return jo.aY("com.google.android.gms");
        }
        return null;
    }

    public static boolean gw() {
        if (Jg) {
            return Jh;
        }
        return "user".equals(Build.TYPE);
    }

    public static boolean b(PackageManager paramPackageManager) {
        synchronized (Jj) {
            if (Ji == -1) {
                try {
                    PackageInfo localPackageInfo = paramPackageManager.getPackageInfo("com.google.android.gms", 64);
                    if (a(localPackageInfo, new byte[][]{b.Jc[1]}) != null) {
                        Ji = 1;
                    } else {
                        Ji = 0;
                    }
                } catch (NameNotFoundException localNameNotFoundException) {
                    Ji = 0;
                }
            }
        }
        return Ji != 0;
    }

    public static boolean c(PackageManager paramPackageManager) {
        return (b(paramPackageManager)) || (!gw());
    }

    public static PendingIntent getErrorPendingIntent(int errorCode, Context context, int requestCode) {
        Intent localIntent = aj(errorCode);
        if (localIntent == null) {
            return null;
        }
        return PendingIntent.getActivity(context, requestCode, localIntent, 268435456);
    }

    public static boolean isUserRecoverableError(int errorCode) {
        switch (errorCode) {
            case 1:
            case 2:
            case 3:
            case 9:
                return true;
        }
        return false;
    }

    public static String getOpenSourceSoftwareLicenseInfo(Context context) {
        Uri localUri = new Uri.Builder().scheme("android.resource").authority("com.google.android.gms")
                .appendPath("raw").appendPath("oss_notice").build();
        try {
            InputStream localInputStream = context.getContentResolver().openInputStream(localUri);
            try {
                String str1 = new Scanner(localInputStream).useDelimiter("\\A").next();
                return str1;
            } catch (NoSuchElementException localNoSuchElementException1) {
                String str2 = null;
                return str2;
            } finally {
                if (localInputStream != null) {
                    localInputStream.close();
                }
            }

        } catch (Exception localException1) {
        }

        return null;
    }

    public static boolean a(Resources paramResources) {
        if (paramResources == null) {
            return false;
        }
        int i = (paramResources.getConfiguration().screenLayout & 0xF) > 3 ? 1 : 0;
        return ((ll.ig()) && (i != 0)) || (b(paramResources));
    }

    private static boolean b(Resources paramResources) {
        Configuration localConfiguration = paramResources.getConfiguration();
        if (ll.ii()) {
            return ((localConfiguration.screenLayout & 0xF) <= 3) && (localConfiguration.smallestScreenWidthDp >= 600);
        }
        return false;
    }

    public static Resources getRemoteResource(Context context) {
        try {
            return context.getPackageManager().getResourcesForApplication("com.google.android.gms");
        } catch (NameNotFoundException localNameNotFoundException) {
        }
        return null;
    }

    public static Context getRemoteContext(Context context) {
        try {
            return context.createPackageContext("com.google.android.gms", 3);
        } catch (NameNotFoundException localNameNotFoundException) {
        }
        return null;
    }

    public static boolean e(Context paramContext, int paramInt) {
        if (paramInt == 1) {
            PackageManager localPackageManager = paramContext.getPackageManager();
            try {
                if (localPackageManager.getApplicationInfo("com.google.android.gms",
                        ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES) != null) {
                    return true;
                }
            } catch (NameNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return false;
    }

}

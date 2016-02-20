package cn.openwatch.internal.basic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import cn.openwatch.internal.basic.utils.DeviceUtils;

public class PrefsManager {

    private static final String CACHE_FILE_NAME = "openwatch_cache";

    private static final String DEVICE_UDID_KEY = "device_udid";
    private static final String SCREEN_SHAPE_SP_KEY = "screen_shape";

    protected PrefsManager() {
        // TODO Auto-generated constructor stub
    }


//    1. apply没有返回值而commit返回boolean表明修改是否提交成功
//    2. apply是将修改数据原子提交到内存, 而后异步真正提交到硬件磁盘, 而commit是同步的提交到硬件磁盘，因此，在多个并发的提交commit的时候，他们会等待正在处理的commit保存到磁盘后在操作，从而降低了效率。而apply只是原子的提交到内容，后面有调用apply的函数的将会直接覆盖前面的内存数据，这样从一定程度上提高了很多效率。
//    3. apply方法不会提示任何失败的提示。
//	由于在一个进程中，sharedPreference是单实例，一般不会出现并发冲突，如果对提交的结果不关心的话，建议使用apply

    // Unlike commit(), which writes its preferences out to persistent storage synchronously, apply() commits its changes to the in-memory SharedPreferencesimmediately but starts an asynchronous commit to disk and you won't be notified of any failures. If another editor on this SharedPreferences does a regularcommit() while a apply() is still outstanding, the commit() will block until all async commits are completed as well as the commit itself.
    public static void saveData(Context cx, String key, String data) {
        SharedPreferences sp = cx.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit().putString(key, data);
        if (Build.VERSION.SDK_INT >= 9)
            editor.apply();
        else
            editor.commit();
    }

    public static String getData(Context cx, String key) {
        SharedPreferences sp = cx.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void clearData(Context cx, String key) {
        saveData(cx, key, "");
    }

    public static String getUDID(Context cx) {
        String udid = getData(cx, DEVICE_UDID_KEY);

        if (TextUtils.isEmpty(udid)) {
            udid = DeviceUtils.getUdid(cx);
            saveData(cx, DEVICE_UDID_KEY, udid);
        }

        return udid;
    }


    public static void getScreenShape(Activity activity, final ShapeDetector.ShapeDetectCallback callback) {

        final Context cx = activity.getApplicationContext();
        String screenShape = getData(cx, SCREEN_SHAPE_SP_KEY);

        if (TextUtils.isEmpty(screenShape)) {
            // 保存手表端屏幕形状
            ShapeDetector.detectShapeOnWatch(activity, new ShapeDetector.ShapeDetectCallback() {

                @Override
                public void onShapeDetected(int screenShape) {
                    // TODO Auto-generated method stub
                    saveData(cx, SCREEN_SHAPE_SP_KEY, String.valueOf(screenShape));

                    if (callback != null)
                        callback.onShapeDetected(screenShape);
                }
            });
        } else {
            if (callback != null && TextUtils.isDigitsOnly(screenShape))
                callback.onShapeDetected(Integer.valueOf(screenShape));
            else
                callback.onShapeDetected(ShapeDetector.SHAPE_UNKNOWN);
        }
    }

}

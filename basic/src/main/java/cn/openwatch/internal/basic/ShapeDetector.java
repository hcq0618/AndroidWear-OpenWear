package cn.openwatch.internal.basic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

/**
 * 用于检测手表端设备屏幕形状的工具类
 */
public final class ShapeDetector {

    public static final int SHAPE_ROUND = 0;
    public static final int SHAPE_SQUARE = 1;
    public static final int SHAPE_UNKNOWN = -1;

    private static int shape = SHAPE_UNKNOWN;

    private ShapeDetector() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 检测手表端设备屏幕形状
     *
     * @param view     用于检测的view
     * @param callback 检测结果的回调
     */
    @TargetApi(20)
    public static void detectShapeOnWatch(View view, final ShapeDetectCallback callback) {

        if (callback != null && shape != SHAPE_UNKNOWN) {
            callback.onShapeDetected(shape);

            return;
        }

        if (Build.VERSION.SDK_INT < 20) {
            if (callback != null) {
                callback.onShapeDetected(SHAPE_SQUARE);
            }
            return;
        }

        // Added in API level 20
        view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {

            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {

                if (insets.isRound()) {
                    shape = SHAPE_ROUND;

                } else {
                    shape = SHAPE_SQUARE;
                }

                if (callback != null) {
                    callback.onShapeDetected(shape);
                }
                return insets;
            }
        });
    }

    /**
     * 检测手表端设备屏幕形状
     *
     * @param activity 用于检测的activity
     * @param callback 检测结果的回调
     */
    @TargetApi(20)
    public static void detectShapeOnWatch(Activity activity, ShapeDetectCallback callback) {

        if (callback != null && shape != SHAPE_UNKNOWN) {
            callback.onShapeDetected(shape);

            return;
        }

        detectShapeOnWatch(activity.getWindow().getDecorView().findViewById(android.R.id.content), callback);
    }

    /**
     * 检测手表端设备屏幕形状的回调
     */
    public interface ShapeDetectCallback {
        /**
         * 手表端设备屏幕形状检测完成
         *
         * @param screenShape 设备屏幕形状类型
         * @see ShapeDetector#SHAPE_ROUND
         * @see ShapeDetector#SHAPE_SQUARE
         * @see ShapeDetector#SHAPE_UNKNOWN
         */
        void onShapeDetected(int screenShape);
    }

}

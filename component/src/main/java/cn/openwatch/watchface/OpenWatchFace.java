package cn.openwatch.watchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.format.Time;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.TimeZone;

import cn.openwatch.internal.watchface.CanvasWatchFaceService;
import cn.openwatch.internal.watchface.WatchFaceService;

/**
 * 用于创建手表端表盘
 * <p/>
 * 可以监听表盘各种状态的改变 完整的表盘生命周期的回调
 * <p/>
 * 可以监听时间和时区的改变 通过最省电的方式 适时的自动刷新表盘
 * <p/>
 * 可以通过布局文件或者自定义控件来构建表盘
 * <p/>
 * 可以自定义秒针样式 若未设置 则不显示
 */
public abstract class OpenWatchFace extends CanvasWatchFaceService {
    private WatchFaceEngine watchFaceEngine;
    private View watchFaceView;
    private Time mTime = new Time();
    private int watchFaceWidth, watchFaceHeight;
    private Rect rect;
    private Paint rectPaint = new Paint();
    private OpenWatchFaceStyle watchFaceStyle;
    private int watchFaceWidthSpec, watchFaceHeightSpec;
    private int timeUpdateMode = TIME_UPDATE_PER_MINUTE_MODE;
    private boolean isInMuteMode, isUpdateTimeTickReceiver, isNeedResetTimeUpdatePerSecond;

    private static final int MSG_TIME_PER_SECOND_UPDATE = 0;

    public static final int TIME_UPDATE_PER_SECOND_MODE = 0;

    public static final int TIME_UPDATE_PER_MINUTE_MODE = 1;

    private Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_TIME_PER_SECOND_UPDATE:

                    mTime.setToNow();

                    onTimeUpdate(mTime);

                    if (isVisible() && !isInPowerSaveMode()) {
                        handler.sendEmptyMessageDelayed(MSG_TIME_PER_SECOND_UPDATE, 1000);
                    }
                    break;
            }

            return true;
        }
    });

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    @Override
    public CanvasWatchFaceService.Engine onCreateEngine() {
        // TODO Auto-generated method stub
        watchFaceEngine = new WatchFaceEngine();
        return watchFaceEngine;
    }

    private class WatchFaceEngine extends CanvasWatchFaceService.Engine {
        private boolean isRegistedReceiver;

        /* receiver to update the time zone */
        // 时区变化
        private BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));

                OpenWatchFace.this.onTimeZoneChanged();
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            super.onCreate(holder);

            onWatchFaceCreate();

            watchFaceView = onCreateView();
            watchFaceView.setWillNotDraw(false);
        }

        @Override
        public void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();

            onWatchFaceDestory();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            super.onSurfaceChanged(holder, format, width, height);

            // 你可以把调整图片大小的时间提前到Engine.onSurfaceChanged()方法中，这里也可以获取到画布尺寸大小。
            watchFaceWidth = width;
            watchFaceHeight = height;

            rect = new Rect(0, 0, width, height);

            // Measure the view at the exact dimensions (otherwise the text
            // won't center correctly)
            watchFaceWidthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            watchFaceHeightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            isUpdateTimeTickReceiver = true;

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());

                invalidate();
            } else {
                unregisterReceiver();
            }

            if (timeUpdateMode == TIME_UPDATE_PER_SECOND_MODE) {
                if (visible)
                    postMsg(MSG_TIME_PER_SECOND_UPDATE);
                else
                    removeMsg(MSG_TIME_PER_SECOND_UPDATE);
            } else {
                removeMsg(MSG_TIME_PER_SECOND_UPDATE);
            }

            OpenWatchFace.this.onVisibilityChanged(visible);
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            // TODO Auto-generated method stub
            super.onInterruptionFilterChanged(interruptionFilter);
            isInMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);
        }

        private void registerReceiver() {
            if (isRegistedReceiver)
                return;

            isRegistedReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            OpenWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!isRegistedReceiver)
                return;

            isRegistedReceiver = false;
            OpenWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        // 由父类源码可知 在表盘可见时 分钟时间发生变化时会回调
        // 在环境模式下即省电状态 时区和日期变化时也会回调
        @Override
        public void onTimeTick() {
            super.onTimeTick();

            if (timeUpdateMode == TIME_UPDATE_PER_MINUTE_MODE) {
                if (!isUpdateTimeTickReceiver) {
                    mTime.setToNow();
                    OpenWatchFace.this.onTimeUpdate(mTime);
                }
            }

            isUpdateTimeTickReceiver = false;
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);

            boolean isLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            boolean isBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);

            OpenWatchFace.this.onPropertiesChanged(isLowBitAmbient, isBurnInProtection);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            isUpdateTimeTickReceiver = true;

            invalidate();

            if (timeUpdateMode == TIME_UPDATE_PER_SECOND_MODE) {
                if (inAmbientMode) {
                    removeMsg(MSG_TIME_PER_SECOND_UPDATE);

                    // 省电模式则切换成TIME_UPDATE_PER_MINUTE_MODE
                    setTimeUpdateMode(TIME_UPDATE_PER_MINUTE_MODE);
                    isNeedResetTimeUpdatePerSecond = true;
                } else {
                    postMsg(MSG_TIME_PER_SECOND_UPDATE);
                }
            } else {

                if (!inAmbientMode && isNeedResetTimeUpdatePerSecond) {
                    // 非省电模式 如需要 则重置回TIME_UPDATE_PER_SECOND_MODE
                    postMsg(MSG_TIME_PER_SECOND_UPDATE);
                    setTimeUpdateMode(TIME_UPDATE_PER_SECOND_MODE);
                    isNeedResetTimeUpdatePerSecond = false;
                } else {
                    removeMsg(MSG_TIME_PER_SECOND_UPDATE);
                }
            }

            OpenWatchFace.this.onPowerSaveModeChanged(inAmbientMode);

        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // TODO Auto-generated method stub
            onWatchFaceDraw(canvas, bounds);
        }

    }

    private void postMsg(int what) {
        if (!handler.hasMessages(what))
            handler.sendEmptyMessage(what);
    }

    private void removeMsg(int what) {
        if (handler.hasMessages(what))
            handler.removeMessages(what);
    }

    private void drawHandIfNeed(Canvas canvas, OpenWatchFaceHand hand, int value) {
        if (hand != null && isVisible() && !isInPowerSaveMode()) {
            float centerX = watchFaceWidth / 2.0f;
            float centerY = watchFaceHeight / 2.0f;

            if (hand.length > 0) {

                double degree = value / 30f * Math.PI;

                float secTopX, secTopY, secBottomX, secBottomY;

                if (hand.getDrawGravity() == OpenWatchFaceHand.DRAW_GRAVITY_BORDER) {
                    // 顶点极坐标
                    secTopX = (float) (centerX * Math.sin(degree));
                    secTopY = (float) (centerY * -Math.cos(degree));

                    // 底部点极坐标
                    secBottomX = (float) ((centerX - hand.length) * Math.sin(degree));
                    secBottomY = (float) ((centerX - hand.length) * -Math.cos(degree));
                } else {
                    // 顶点极坐标
                    secTopX = (float) (hand.length * Math.sin(degree));
                    secTopY = (float) (hand.length * -Math.cos(degree));

                    // 底部点极坐标
                    secBottomX = 0;
                    secBottomY = 0;
                }

                canvas.translate(centerX, centerY);
                canvas.drawLine(secTopX, secTopY, secBottomX, secBottomY, hand.getHandPaint());

            } else {
                Bitmap bitmap = hand.bitmap;
                if (bitmap != null && !bitmap.isRecycled()) {

                    Matrix matrix = hand.bitmapMatrix;
                    matrix.reset();
                    matrix.postTranslate(centerX - bitmap.getWidth() / 2.0f, 0);
                    matrix.postRotate(value * 6, centerX, centerY);

                    canvas.drawBitmap(bitmap, matrix, hand.bitmapPaint);
                }
            }

        }
    }

    /**
     * 表盘启动创建时回调
     */
    public void onWatchFaceCreate() {
    }

    /**
     * 表盘退出销毁时回调
     */
    public void onWatchFaceDestory() {
    }

    /**
     * 构建表盘布局时回调
     *
     * @return View
     */
    public abstract View onCreateView();

    /**
     * 绘制表盘时回调
     *
     * @param canvas
     * @param bounds
     */
    // http://stackoverflow.com/questions/27730714/android-wear-watch-face-can-i-only-use-ondraw-inside-canvaswatchfaceservice
    public void onWatchFaceDraw(Canvas canvas, Rect bounds) {
        if (watchFaceView != null) {

            requestLayout();

            // 不加这句绘图会有重影的bug
            canvas.drawRect(rect, rectPaint);

            watchFaceView.draw(canvas);

        }

        if (watchFaceStyle != null) {

            Time time = getTime();

            if (time != null) {
                // 时针
                drawHandIfNeed(canvas, watchFaceStyle.hourHand, time.hour);

                // 分针
                drawHandIfNeed(canvas, watchFaceStyle.minHand, time.minute);

                // 秒针
                drawHandIfNeed(canvas, watchFaceStyle.secondHand, time.second);
            }

        }
    }

    /**
     * 与{@link View#requestLayout()}类似
     */
    public void requestLayout() {
        if (watchFaceView == null)
            return;

        watchFaceView.measure(watchFaceWidthSpec, watchFaceHeightSpec);
        // Lay the view out at the rect width and height
        watchFaceView.layout(0, 0, watchFaceWidth, watchFaceHeight);
    }

    /**
     * 配置表盘上的系统ui样式 可以在任何地方配置
     *
     * @param style 配置表盘上的系统ui样式
     */
    public void setStyle(OpenWatchFaceStyle style) {
        watchFaceStyle = style;

        if (watchFaceEngine != null && style != null)
            watchFaceEngine.setWatchFaceStyle(style.builder.build());
    }

    /**
     * 表盘的省电模式发生改变 表盘应尽可能的减少刷新
     *
     * @param inPowerSavedMode 是否处于省电模式
     */
    public void onPowerSaveModeChanged(boolean inPowerSavedMode) {
    }

    /**
     * 表盘属性发生改变
     *
     * @param isLowBitAmbient    是否处于低功耗状态
     * @param isBurnInProtection 是否处于屏幕保护状态
     */
    public void onPropertiesChanged(boolean isLowBitAmbient, boolean isBurnInProtection) {
    }

    /**
     * 设置时间更新模式 默认为{@link #TIME_UPDATE_PER_MINUTE_MODE}
     *
     * @param timeUpdateMode
     * @see {@link #onTimeUpdate(Time)}
     */
    public void setTimeUpdateMode(int timeUpdateMode) {
        this.timeUpdateMode = timeUpdateMode;
    }

    /**
     * 表盘不可见时：不会回调 <br>
     * <br>
     * 表盘可见时：<br>
     * <br>
     * 省电模式下 当分钟时间、时区、日期发生变化时回调 <br>
     * <br>
     * 非省电模式下<br>
     * timeUpdateMode为 {@link #TIME_UPDATE_PER_MINUTE_MODE}时， 当分钟时间发生变化时回调<br>
     * timeUpdateMode为{@link #TIME_UPDATE_PER_SECOND_MODE}时， 当秒钟时间发生变化时回调
     *
     * @param time 当前时间
     * @see {@link #setTimeUpdateMode(int)}
     * @see {@link #onTimeZoneChanged()}
     */
    public void onTimeUpdate(Time time) {
    }

    /**
     * 表盘的可见性发生改变时回调
     *
     * @param visible 是否可见
     */
    public void onVisibilityChanged(boolean visible) {
    }

    /**
     * 时区发生改变时回调
     */
    public void onTimeZoneChanged() {
    }

    /**
     * 表盘当前是否可见
     *
     * @return boolean
     */
    public boolean isVisible() {
        if (watchFaceEngine != null)
            return watchFaceEngine.isVisible();

        return false;
    }

    /**
     * 设备是否在静音模式下
     *
     * @return boolean
     */
    public boolean isInMuteMode() {
        return isInMuteMode;
    }

    /**
     * 表盘当前是否为省电模式
     *
     * @return boolean
     */
    public boolean isInPowerSaveMode() {
        if (watchFaceEngine != null)
            return watchFaceEngine.isInAmbientMode();

        return false;
    }

    /**
     * 手动刷新表盘
     */
    public void invalidate() {
        if (watchFaceEngine != null) {
            watchFaceEngine.invalidate();
        }
    }

    /**
     * 保证在UI线程中手动刷新表盘
     */
    public void postInvalidate() {
        if (watchFaceEngine != null) {
            watchFaceEngine.postInvalidate();
        }
    }

    /**
     * 当前时间
     *
     * @return Time
     */
    public Time getTime() {
        // Update the time
        mTime.setToNow();
        return mTime;
    }

    /**
     * 表盘宽度
     *
     * @return int
     */
    public int getWatchFaceWidth() {
        return watchFaceWidth;
    }

    /**
     * 表盘高度
     *
     * @return int
     */
    public int getWatchFaceHeight() {
        return watchFaceHeight;
    }

}

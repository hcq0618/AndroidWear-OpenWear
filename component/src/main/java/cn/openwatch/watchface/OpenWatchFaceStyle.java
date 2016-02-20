package cn.openwatch.watchface;

import android.app.Service;
import android.os.Bundle;

import cn.openwatch.internal.watchface.WatchFaceStyle;

/**
 * 手表端表盘样式
 */
public final class OpenWatchFaceStyle extends WatchFaceStyle {

    protected OpenWatchFaceHand secondHand;

    protected OpenWatchFaceHand minHand;

    protected OpenWatchFaceHand hourHand;

    protected Builder builder;

    public OpenWatchFaceStyle(Service service) {
        super(new Bundle());

        builder = new Builder(service);
    }

    public OpenWatchFaceStyle(Bundle bundle) {
        super(bundle);
        // TODO Auto-generated constructor stub
    }

    /**
     * 设置表盘上将展现的第一张卡片如何显示 默认：AMBIENT_PEEK_MODE_VISIBLE
     *
     * @param ambientPeekMode 显示：OpenWatchFaceStyle.AMBIENT_PEEK_MODE_VISIBLE 或
     *                        隐藏：OpenWatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN
     * @return Builder
     * @throws IllegalArgumentException - 如果传入的参数不支持或者非法
     */
    public OpenWatchFaceStyle setAmbientPeekMode(int ambientPeekMode) {
        // TODO Auto-generated method stub
        builder.setAmbientPeekMode(ambientPeekMode);
        return this;
    }

    /**
     * 设置表盘上将展现的第一张卡片的背景如何显示 默认：BACKGROUND_VISIBILITY_INTERRUPTIVE
     *
     * @param backgroundVisibility OpenWatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE 或
     *                             OpenWatchFaceStyle.BACKGROUND_VISIBILITY_PERSISTENT
     * @return Builder
     * @throws IllegalArgumentException - 如果传入的参数不支持或者非法
     */
    public OpenWatchFaceStyle setBackgroundVisibility(int backgroundVisibility) {
        // TODO Auto-generated method stub
        builder.setBackgroundVisibility(backgroundVisibility);
        return this;
    }

    /**
     * 设置表盘上将展现的第一张卡片的高度 默认：PEEK_MODE_VARIABLE
     *
     * @param peekMode 多行：PEEK_MODE_VARIABLE 或 单行：PEEK_MODE_SHORT
     * @return Builder
     * @throws IllegalArgumentException - 如果传入的参数不支持或者非法
     */
    public OpenWatchFaceStyle setCardPeekMode(int peekMode) {
        // TODO Auto-generated method stub
        builder.setCardPeekMode(peekMode);
        return this;
    }

    /**
     * @param progressMode
     * @return Builder
     * @throws IllegalArgumentException - 如果传入的参数不支持或者非法
     */
    public OpenWatchFaceStyle setCardProgressMode(int progressMode) {
        // TODO Auto-generated method stub
        builder.setCardProgressMode(progressMode);
        return this;
    }

    /**
     * 设置表盘上热词（如：OK Google）的位置
     *
     * @param hotwordIndicatorGravity
     * @return Builder
     * @see android.view.Gravity
     */
    public OpenWatchFaceStyle setHotwordIndicatorGravity(int hotwordIndicatorGravity) {
        // TODO Auto-generated method stub
        builder.setHotwordIndicatorGravity(hotwordIndicatorGravity);
        return this;
    }

    /**
     * 设置表盘上将展现的第一张卡片的透明模式 默认：PEEK_OPACITY_MODE_OPAQUE
     *
     * @param peekOpacityMode 不透明：PEEK_OPACITY_MODE_OPAQUE 或
     *                        透明：PEEK_OPACITY_MODE_TRANSLUCENT
     * @return Builder
     * @throws IllegalArgumentException - 如果传入的参数不支持或者非法
     */
    public OpenWatchFaceStyle setPeekOpacityMode(int peekOpacityMode) {
        // TODO Auto-generated method stub
        builder.setPeekOpacityMode(peekOpacityMode);
        return this;
    }

    /**
     * 设置表盘上是否显示系统时间 默认：false
     *
     * @param showSystemUiTime
     * @return Builder
     */
    public OpenWatchFaceStyle setShowSystemUiTime(boolean showSystemUiTime) {
        // TODO Auto-generated method stub
        builder.setShowSystemUiTime(showSystemUiTime);
        return this;
    }

    /**
     * 设置表盘上是否显示通知卡片的未读数 默认：false
     *
     * @param show 默认 false
     * @return Builder
     */
    public OpenWatchFaceStyle setShowUnreadCountIndicator(boolean show) {
        // TODO Auto-generated method stub
        builder.setShowUnreadCountIndicator(show);
        return this;
    }

    /**
     * 设置表盘上状态栏的位置
     *
     * @param statusBarGravity
     * @return Builder
     * @see android.view.Gravity
     */
    public OpenWatchFaceStyle setStatusBarGravity(int statusBarGravity) {
        // TODO Auto-generated method stub
        builder.setStatusBarGravity(statusBarGravity);
        return this;
    }

    /**
     * 设置表盘上系统元素的背景显示方式 默认：背景不加底色
     *
     * @param viewProtectionMode 状态栏背景加底色：PROTECT_STATUS_BAR 或
     *                           热词背景加底色：PROTECT_HOTWORD_INDICATOR 或
     *                           全屏背景架底色：PROTECT_WHOLE_SCREEN
     * @return Builder
     * @throws IllegalArgumentException - 如果传入的参数不支持或者非法
     */
    public OpenWatchFaceStyle setViewProtectionMode(int viewProtectionMode) {
        // TODO Auto-generated method stub
        builder.setViewProtectionMode(viewProtectionMode);
        return this;
    }

    /**
     * 设置表盘秒针
     *
     * @param secondHand
     */
    public OpenWatchFaceStyle setSecondHand(OpenWatchFaceHand secondHand) {
        this.secondHand = secondHand;
        return this;
    }

    /**
     * 设置表盘分针
     *
     * @param minHand
     */
    public OpenWatchFaceStyle setMinHand(OpenWatchFaceHand minHand) {
        this.minHand = minHand;
        return this;
    }

    /**
     * 设置表盘时钟
     *
     * @param hourHand
     */
    public OpenWatchFaceStyle setHourHand(OpenWatchFaceHand hourHand) {
        this.hourHand = hourHand;
        return this;
    }

}

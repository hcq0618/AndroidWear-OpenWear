package cn.openwatch.watchface;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 手表端表盘指针样式
 */
public final class OpenWatchFaceHand {

    public static final int DRAW_GRAVITY_BORDER = 0;
    public static final int DRAW_GRAVITY_CENTER = 1;

    protected int length;
    private int width = 5;
    private int color = Color.WHITE;
    private Paint.Cap cap = Paint.Cap.ROUND;
    private Paint handPaint;
    private int drawGravity = DRAW_GRAVITY_CENTER;

    protected Bitmap bitmap;
    protected Paint bitmapPaint;
    protected Matrix bitmapMatrix;

    public int getLength() {
        return length;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getWidth() {
        return width;
    }

    public int getColor() {
        return color;
    }

    public Paint.Cap getCap() {
        return cap;
    }

    protected Paint getHandPaint() {

        if (length > 0 && handPaint == null) {
            handPaint = new Paint();
            handPaint.setAntiAlias(true);
        }

        if (handPaint != null) {
            handPaint.setColor(color);
            handPaint.setStrokeWidth(width);
            handPaint.setStrokeCap(cap);
        }

        return handPaint;
    }

    public int getDrawGravity() {
        return drawGravity;
    }

    /**
     * 设置表盘指针图案 默认：无
     *
     * @param Bitmap
     */
    public void setBitmap(Bitmap Bitmap) {
        this.bitmap = Bitmap;

        if (bitmapMatrix == null) {
            bitmapMatrix = new Matrix();
        }

        if (bitmapPaint == null) {
            bitmapPaint = new Paint();
            // 为了提高性能，使用setAntiAlias()方法来禁用抗锯齿，因为这个参数在bitmaps上没有任何效果。
            bitmapPaint.setAntiAlias(false);
            // 对于你绘制在所有元素顶部的bitmap资源，通过使用同一个paint实例的setFilterBitmap()
            // 方法来启用位图过滤。
            bitmapPaint.setFilterBitmap(true);
        }
    }

    /**
     * 设置表盘指针的端点形状 默认：Paint.Cap.ROUND
     *
     * @param cap 端点形状
     * @see Paint.Cap
     */
    public void setCap(Paint.Cap cap) {
        this.cap = cap;
    }

    /**
     * 设置表盘指针的颜色 默认：Color.white
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        this.color = color;

    }

    /**
     * 设置表盘指针的宽度 单位px 默认:5
     *
     * @param width 宽度
     */
    public void setWidth(int width) {
        this.width = width;

    }

    /**
     * 设置表盘指针的长度 单位px 默认：0
     *
     * @param length 指针长度px
     * @return OpenWatchFaceStyle
     */
    public void setLength(int length) {
        this.length = length;

    }

    /**
     * 设置指针的绘制位置 默认：DRAW_GRAVITY_CENTER
     *
     * @param drawGravity 在边界上绘制：DRAW_GRAVITY_BORDER 或 在中心绘制：DRAW_GRAVITY_CENTER
     * @throws IllegalArgumentException.IllegalArgumentException - 如果传入的参数不支持或者非法
     */
    public void setDrawGravity(int drawGravity) {
        if (drawGravity == DRAW_GRAVITY_BORDER || drawGravity == DRAW_GRAVITY_CENTER) {
            this.drawGravity = drawGravity;
        } else {
            throw new IllegalArgumentException("drawGravity must be DRAW_GRAVITY_BORDER or DRAW_GRAVITY_CENTER");
        }

    }

}

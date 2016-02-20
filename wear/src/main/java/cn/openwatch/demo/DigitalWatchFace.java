package cn.openwatch.demo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import cn.openwatch.demo.R;
import cn.openwatch.watchface.OpenWatchFace;
import cn.openwatch.watchface.OpenWatchFaceHand;
import cn.openwatch.watchface.OpenWatchFaceStyle;

public class DigitalWatchFace extends OpenWatchFace {

	private TextView timeTextView, dateTextView;

	// 构建表盘布局时回调
	@Override
	public View onCreateView() {
		// TODO Auto-generated method stub

		OpenWatchFaceStyle style = new OpenWatchFaceStyle(this);

		// 如果需要的话 构建表盘秒针 否则不会绘制秒针
		OpenWatchFaceHand secondHand = new OpenWatchFaceHand();
		secondHand.setLength(DisplayUtil.dip2px(this, 10));
		secondHand.setWidth(DisplayUtil.dip2px(this, 3));
		// 秒针在表盘边界显示
		secondHand.setDrawGravity(OpenWatchFaceHand.DRAW_GRAVITY_BORDER);

		style.setSecondHand(secondHand);

		// 并设置onTimeUpdate回调频率为TIME_UPDATE_PER_SECOND_MODE
		setTimeUpdateMode(TIME_UPDATE_PER_SECOND_MODE);

		// 表盘上的通知卡片以单行高度显示
		style.setCardPeekMode(OpenWatchFaceStyle.PEEK_MODE_SHORT);

		setStyle(style);

		View watchface = View.inflate(this, R.layout.digital_watchface_layout, null);
		timeTextView = (TextView) watchface.findViewById(R.id.watchface_time_tv);
		dateTextView = (TextView) watchface.findViewById(R.id.watchface_date_tv);

		// 可获取的相关属性 详见相关文档
		// //获取表盘宽度
		// getWatchFaceWidth();
		// //获取表盘高度
		// getWatchFaceHeight();
		// //是否为静音模式
		// isInMuteMode();
		// //表盘是否可见
		// isWatchFaceVisible();
		// //获取当前时间
		// getTime();
		// 是否为省电模式
		// isInPowerSaveMode();

		// 返回自定义view或者布局文件生成的view
		return watchface;
	}

	// 表盘创建时回调
	@Override
	public void onWatchFaceCreate() {
		// TODO Auto-generated method stub
		super.onWatchFaceCreate();

	}

	// 表盘销毁时回调
	@Override
	public void onWatchFaceDestory() {
		// TODO Auto-generated method stub
		super.onWatchFaceDestory();

	}

	// 表盘可见性发生变化时回调
	@Override
	public void onVisibilityChanged(boolean visible) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(visible);
	}

	// 表盘从省电模式切换到非省电模式时回调
	@Override
	public void onPowerSaveModeChanged(boolean inPowerSavedMode) {
		super.onPowerSaveModeChanged(inPowerSavedMode);
	}

	// 表盘属性发生变化时回调
	@Override
	public void onPropertiesChanged(boolean isLowBitAmbient, boolean isBurnInProtection) {
		// TODO Auto-generated method stub
		super.onPropertiesChanged(isLowBitAmbient, isBurnInProtection);
	}

	// 时区发生改变时回调
	@Override
	public void onTimeZoneChanged() {
		// TODO Auto-generated method stub
		super.onTimeZoneChanged();
	}

	// 表盘不可见时：不会回调
	//
	// 表盘可见时：
	//
	// 省电模式下 当分钟时间、时区、日期发生变化时回调
	//
	// 非省电模式下
	// timeUpdateMode为TIME_UPDATE_PER_MINUTE_MODE时， 当分钟时间发生变化时回调
	// timeUpdateMode为{@link #TIME_UPDATE_PER_SECOND_MODE时， 当秒钟时间发生变化时回调
	//
	@Override
	public void onTimeUpdate(Time time) {
		// TODO Auto-generated method stub
		super.onTimeUpdate(time);

		// 刷新表盘
		invalidate();
	}

	@Override
	public void onWatchFaceDraw(Canvas canvas, Rect bounds) {
		// TODO Auto-generated method stub

		Time time = getTime();
		setDate(time);
		setTime(time);

		super.onWatchFaceDraw(canvas, bounds);
	}

	private void setDate(Time time) {
		String dateStr = (time.month + 1) + "." + time.monthDay + "  " + (time.hour > 12 ? "下午" : "上午");
		dateTextView.setText(dateStr);
	}

	private void setTime(Time time) {
		String minStr = time.minute < 10 ? "0" + time.minute : String.valueOf(time.minute);
		String timeStr = (time.hour > 12 ? time.hour - 12 : time.hour) + ":" + minStr;

		timeTextView.setText(timeStr);
	}
}

package cn.openwatch.demo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.wearable.view.WearableListView;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WearListItemViews extends LinearLayout implements
		WearableListView.OnCenterProximityListener {
	private ImageView mCircle;
	private TextView mName;
	private Context cx;
	private int margin;

	public WearListItemViews(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		cx = context;
		margin = DisplayUtil.dip2px(cx, 20);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);

		initCircleView();
		initNameView();

		addView(mCircle);
		addView(mName);
	}

	public ImageView getIconView() {
		return mCircle;
	}

	public TextView getNameView() {
		return mName;
	}

	private void initCircleView() {
		mCircle = new ImageView(cx);

		int size = DisplayUtil.dip2px(getContext(), 30);
		LayoutParams lp = new LayoutParams(size, size);
		lp.bottomMargin = lp.topMargin = lp.leftMargin = margin;
		lp.gravity = Gravity.CENTER_VERTICAL;
		mCircle.setLayoutParams(lp);
		mCircle.setScaleType(ScaleType.CENTER_INSIDE);
	}

	private void initNameView() {
		mName = new TextView(getContext());
		mName.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
		mName.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
		mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		lp.rightMargin = margin;
		lp.leftMargin = margin;
		lp.gravity = Gravity.CENTER_VERTICAL;
		mName.setLayoutParams(lp);
	}

	@Override
	public void onCenterPosition(boolean b) {
		// TODO Auto-generated method stub
		mName.animate().alpha(1f).scaleX(1.1f).scaleY(1.1f);
		mCircle.animate().alpha(1f).scaleX(1.3f).scaleY(1.3f);

	}

	@Override
	public void onNonCenterPosition(boolean b) {
		// TODO Auto-generated method stub
		mName.animate().alpha(0.5f).scaleX(1f).scaleY(1f);
		mCircle.animate().alpha(0.5f).scaleX(1f).scaleY(1f);

	}

}

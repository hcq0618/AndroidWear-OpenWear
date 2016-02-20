package cn.openwatch.demo;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WearListView extends WearableListView {

	private AbsWearListBindAdapter<?> bindAdapter;

	public WearListView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public WearListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setBindAdapter(AbsWearListBindAdapter<?> bindAdapter) {
		this.bindAdapter = bindAdapter;
		if (this.bindAdapter != null) {
			this.bindAdapter.setListView(this);
		}

		setClickListener(bindAdapter);
		setVisibility(View.VISIBLE);
		setAdapter(new WearListViewAdapter());
	}

	public void clearView() {
		setBindAdapter(null);
		setVisibility(View.GONE);
	}

	private class WearListViewAdapter extends Adapter {

		@Override
		public int getItemCount() {
			// TODO Auto-generated method stub
			return bindAdapter != null ? bindAdapter.getItemCount() : 0;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			// TODO Auto-generated method stub
			if (bindAdapter != null) {
				if (holder instanceof WearableListViewHolder) {
					WearableListViewHolder viewHolder = (WearableListViewHolder) holder;

					bindAdapter.onBindView(viewHolder.textView, viewHolder.iconView, position);

				}

			}
		}

		@Override
		public WearableListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			// TODO Auto-generated method stub
			return new WearableListViewHolder(new WearListItemViews(getContext()));
		}

	}

	public static abstract class AbsWearListBindAdapter<T> implements ClickListener {
		private WearableListView listView;
		private List<T> dataSet;

		public void setListView(WearableListView listView) {
			this.listView = listView;
			dataSet = onBindData();
		}

		public int getItemCount() {
			return dataSet != null ? dataSet.size() : 0;
		}

		public void notifyDataChanged() {
			// 加入消息队列依次执行
			listView.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					dataSet = onBindData();
					listView.getAdapter().notifyDataSetChanged();
				}
			});
		}

		public T getItem(int position) {
			if (dataSet != null && position < dataSet.size()) {
				return dataSet.get(position);
			}
			return null;
		}

		public abstract void onBindView(TextView textView, ImageView iconView, int position);

		public abstract void onClick(T item, int position);

		public abstract List<T> onBindData();

		@Override
		public void onClick(ViewHolder holder) {
			// TODO Auto-generated method stub
			int position = holder.getAdapterPosition();
			onClick(getItem(position), position);
		}

		@Override
		public void onTopEmptyRegionClick() {
			// TODO Auto-generated method stub

		}
	}

	// Provide a reference to the type of views you're using
	public static class WearableListViewHolder extends ViewHolder {
		public TextView textView;
		public ImageView iconView;

		public WearableListViewHolder(WearListItemViews itemViews) {
			super(itemViews);
			// find the text view within the custom item's layout
			textView = itemViews.getNameView();
			iconView = itemViews.getIconView();
		}

	}

}

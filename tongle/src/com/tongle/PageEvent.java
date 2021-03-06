package com.tongle;

import java.util.ArrayList;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.tongle.Gateway.ActivityLiteData;
import com.tongle.Gateway.ActivitysListener;
import com.tongle.Gateway.ListListener;
import com.tongle.PageDetail.DetailArgs;
import com.tongle.PageEvent.EventArgs.Type;

import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class PageEvent extends Page {
	public static final String ARG = "event_arg";

	// view
	private	List<View> tabs = new ArrayList<View>();
	private TabHost tabHost;
	private ListView list;
	private LeyuAdapter mAdapter;
	private TextView mStatus;
	private LayoutInflater mInflater;
	// Data
	EventArgs mArgs;
	private boolean mTabInitilized;
	private	List<String> mCategorys = new ArrayList<String>();
	private String mCategory;
	private List<ActivityLiteData> m_Data = new ArrayList<ActivityLiteData>();
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mTabInitilized = false;
		mArgs = new Gson().fromJson((String) getArguments().getString(ARG), EventArgs.class);
		mRootView = inflater.inflate(R.layout.page_event, container, false);
		mStatus = (TextView)mRootView.findViewById(R.id.status);
		getCategoryList(mArgs.mType);
		//
		mActivity.initActionBar(getEvent(mArgs.mType));

		updateTabs();

		return mRootView;
	}
	
	@Override
	public void onResume() {
		mTabInitilized = false;
		super.onResume();
	}

	private void updateTabs() {
		if (mCategorys == null || mCategorys.size() == 0) {
			return;
		}

		tabHost = (TabHost) mRootView.findViewById(R.id.tabhost);
		tabHost.setup();
		int len = mCategorys.size();
		View tabIndicator = mInflater.inflate(R.layout.tabwidget, null);
		tabs.add(tabIndicator);
		final TextView tvTab = (TextView) tabIndicator.findViewById(R.id.tab_title);
		tvTab.setText(getString(R.string.all));
		tabHost.addTab(tabHost.newTabSpec("").setIndicator(tabIndicator).setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				TextView tv = new TextView(mActivity);
				tv.setText("The Text of " + tag);
				return tv;
			}
		}));
		for (int i = 0; i < len; i++) {
			tabIndicator = mInflater.inflate(R.layout.tabwidget, null);
			tabs.add(tabIndicator);
			TextView tvTab1 = (TextView) tabIndicator.findViewById(R.id.tab_title);
			tvTab1.setText(mCategorys.get(i));
			tabHost.addTab(tabHost.newTabSpec(mCategorys.get(i)).setIndicator(tabIndicator).setContent(new TabContentFactory() {

				@Override
				public View createTabContent(String tag) {
					TextView tv = new TextView(mActivity);
					tv.setText("The Text of " + tag);
					return tv;
				}
			}));
		}
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				tabHost.setOnTabChangedListener(new OnTabChangeListener() {
					@Override
					public void onTabChanged(String tabId) {

						if (mTabInitilized) {
							mCategory = tabId;
							getEvent(mArgs.mType);
						}
						if (tabHost.getCurrentTabView().findViewById(R.id.tab_title) != null && TextUtils.isEmpty(((TextView) tabHost.getCurrentTabView().findViewById(R.id.tab_title)).getText())) {
							return;
						}
						for (View tab : tabs) {
							if (tabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
								tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
								((TextView) tab.findViewById(R.id.tab_title)).setTextColor(mRes.getColor(R.color.footer));
							}
						}
						if (tabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
							tabHost.getCurrentTabView().findViewById(R.id.tab_image).setVisibility(View.VISIBLE);
							((TextView) tabHost.getCurrentTabView().findViewById(R.id.tab_title)).setTextColor(Color.WHITE);
							if (list != null) {
								list.setAdapter(null);
								mAdapter.notifyDataSetChanged();
								list.setAdapter(new LeyuAdapter());
							}
						}
					}
				});

			}
		});
		
		for (View tab : tabs) {
			tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
			tab.performClick();
		}
		for (View tab : tabs) {
			if (tabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
				tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
				((TextView) tab.findViewById(R.id.tab_title)).setTextColor(mRes.getColor(R.color.footer));
			}
		}
		tabHost.setCurrentTab(0);
		tabs.get(0).findViewById(R.id.tab_image).setVisibility(View.VISIBLE);
		((TextView) tabs.get(0).findViewById(R.id.tab_title)).setTextColor(Color.WHITE);
		mTabInitilized = true;
	}
	
	private void getCategoryList(Type type){
		mCategorys = mCacheManager.getCategoryList();
		switch (type) {
		case weekend: {
			mGateway.getWeekendCategoryList(new ListListener() {

				@Override
				public void onComplete(List<String> data) {
					if (!isAdded()) {
						return;
					}
					mCacheManager.setCategoryList(data);
					mCategorys = data;
					updateTabs();
				}

				@Override
				public void onError() {
				}
			});
			break;
		}
		case free: {
			mGateway.getFreeCategoryList(new ListListener() {

				@Override
				public void onComplete(List<String> data) {
					if (!isAdded()) {
						return;
					}
					mCacheManager.setCategoryList(data);
					mCategorys = data;
					updateTabs();
				}

				@Override
				public void onError() {
				}
			});
			break;
		}
		case hot: {
			mGateway.getCategoryList(new ListListener() {

				@Override
				public void onComplete(List<String> data) {
					if (!isAdded()) {
						return;
					}
					mCacheManager.setCategoryList(data);
					mCategorys = data;
					updateTabs();
				}

				@Override
				public void onError() {
				}
			});
			break;
		}
		case near: {
			mGateway.getCategoryList(new ListListener() {

				@Override
				public void onComplete(List<String> data) {
					if (!isAdded()) {
						return;
					}
					mCacheManager.setCategoryList(data);
					mCategorys = data;
					updateTabs();
				}

				@Override
				public void onError() {
				}
			});
			break;
		}
		default: {
			break;
		}
		}
	}
	
	private String getEvent(Type type){
		String title = "";
		switch (type) {
		case weekend: {
			title = getString(R.string.weekend);
			updateWeekend(mCacheManager.getWeekend(), true);
			// get server data
			mGateway.getWeekend(new ActivitysListener() {

				@Override
				public void onComplete(List<ActivityLiteData> data) {
					mCacheManager.setWeekend(data);
					updateWeekend(data, true);
				}

				@Override
				public void onError() {
					mStatus.setText(R.string.network_error);

				}
			}, mCategory);
			mGateway.userActionWeekend();
			break;
		}
		case free: {
			title = getString(R.string.free);
			updateFree(mCacheManager.getFree(), true);
			// get server data
			mGateway.getFree(new ActivitysListener() {

				@Override
				public void onComplete(List<ActivityLiteData> data) {
					mCacheManager.setFree(data);
					updateFree(data, true);
				}

				@Override
				public void onError() {
					mStatus.setText(R.string.network_error);

				}
			}, mCategory);
			break;
		}
		case hot: {
			title = getString(R.string.hot);
			updateHot(mCacheManager.getHot(), true);
			// get server data
			mGateway.getHot(new ActivitysListener() {

				@Override
				public void onComplete(List<ActivityLiteData> data) {
					mCacheManager.setHot(data);
					updateHot(data, true);
				}

				@Override
				public void onError() {
					mStatus.setText(R.string.network_error);

				}
			}, mCategory);
			mGateway.userActionHot();
			break;
		}
		case near: {
			title = getString(R.string.near);
			updateNear(mCacheManager.getNear(), true);
			// get server data
			mGateway.getNear(new ActivitysListener() {

				@Override
				public void onComplete(List<ActivityLiteData> data) {
					mCacheManager.setNear(data);
					updateNear(data, true);
				}

				@Override
				public void onError() {
					mStatus.setText(R.string.network_error);

				}
			}, mActivity.getLocation(), mCategory);
			mGateway.userActionNeighborhood();
			break;
		}
		default: {
			break;
		}
		}
		return title;
	}
	
	private void updateWeekend(List<ActivityLiteData> data, boolean showStatus){
		if (data == null || data.size() == 0) {
			mStatus.setText(R.string.no_weekend);
		} else {
			showList(data, showStatus);
		}
	}
	
	private void updateFree(List<ActivityLiteData> data, boolean showStatus){
		if (data == null || data.size() == 0) {
			mStatus.setText(R.string.no_free);
		} else {
			showList(data, showStatus);
		}
	}
	
	private void updateHot(List<ActivityLiteData> data, boolean hideStatus){
		if (data == null || data.size() == 0) {
			mStatus.setText(R.string.no_hot);
		} else {
			showList(data, hideStatus);
		}
	}
	
	private void updateNear(List<ActivityLiteData> data, boolean hideStatus){
		if (data == null || data.size() == 0) {
			mStatus.setText(R.string.no_near);
		} else {
			showList(data, hideStatus);
		}
	}
	
	private void showList(List<ActivityLiteData> data, boolean hideStatus){
		if (hideStatus) {
			mStatus.setVisibility(View.GONE);
		}
		m_Data = data;
		mAdapter = new LeyuAdapter();
		list = ((ListView) mRootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(mActivity);
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (46.67 * mRes.getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());
	}
	
	static public class EventArgs {
		private Type mType;

		EventArgs(Type type) {
			mType = type;
		}

		enum Type {
			weekend, free, hot, near
		}
	}

	class LeyuAdapter extends BaseAdapter {

		

		class Item {
			String mTitle;
			String mUrl;

			Item(String title, String url) {
				mTitle = title;
				mUrl = url;
			}
		}

		LeyuAdapter() {

		}

		@Override
		public int getCount() {
			return m_Data.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_event,
						parent, false);
				holder.mTitle = (TextView) convertView.findViewById(R.id.event_title);
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.event_image);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mTitle.setText(m_Data.get(position).mTitle);
			final Uri uri = Uri.parse(m_Data.get(position).mPicture);
			//
			int width, height;
			width = height = (int) (mRes.getDisplayMetrics().density * 115);
			ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
					.setResizeOptions(new ResizeOptions(width, height))
					.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
					.build();
			DraweeController controller = Fresco.newDraweeControllerBuilder()
					.setImageRequest(request).setOldController(holder.image.getController())
					.build();
			holder.image.setController(controller);
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Fragment event = new PageDetail();
					Bundle bundle = new Bundle();
					bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_Data.get(position).mID, uri.toString(), m_Data.get(position).mTitle, null)));
					event.setArguments(bundle);
					jumpPage(event, TAG);;
					
				}});

			return convertView;
		}

		class ViewHolder {
			TextView mTitle;
			SimpleDraweeView image;
		}
	}
}
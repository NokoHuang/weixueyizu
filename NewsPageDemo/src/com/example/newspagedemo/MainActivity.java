package com.example.newspagedemo;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commonality.CommonalityDataInterface;
import com.example.commonality.CommonalityInformation;
import com.example.model.NewsNavBarTitleModel;
import com.example.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActivityGroup implements OnClickListener {
	// 导航栏网络连接超时
	private final int NAVIGATION_CONNECTION_NETWORK_TIMEOUT = 0X124;
	// 导航栏加载成功
	private final int NAVIGATION_LOADING_SUCESS = 0X123;
	private LinearLayout linearLayout;
	// 导航栏标题布局列表
	private ArrayList<TextView> textViews;
	// 页面列表
	private ArrayList<View> pageViews;
	// 导航栏标题
	private ArrayList<NewsNavBarTitleModel> newsNav;
	private ViewPager viewPager;
	private HorizontalScrollView horizontalScrollView;
	private Handler handler;
	private String getString;
	private ProgressBar progressbar;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Init();
		getNavigationBarType();

		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == NAVIGATION_LOADING_SUCESS) {
					// 隐藏导航栏的加载进度框
					progressbar.setVisibility(View.GONE);
					InitNavigationBar();
					setSelector(0);
					InItView();

					viewPager.setAdapter(new PagerViewAdapter());
					viewPager.clearAnimation();

					viewPager
							.setOnPageChangeListener(new PageOnChanageListener());
				}

				if (msg.what == NAVIGATION_CONNECTION_NETWORK_TIMEOUT) {
					Toast.makeText(getApplicationContext(), "网络连接超时!",
							Toast.LENGTH_SHORT).show();
					// 计时器
					Timer timer = new Timer();
					TimerTask task = new TimerTask() {
						public void run() {
							getNavigationBarType();
						}
					};
					// 2秒之后重新尝试获取导航栏标题
					timer.schedule(task, 2000);

				}
			}
		};
	}

	public void Init() {
		/*
		 * ActionBar actionbar=getActionBar(); actionbar.hide();
		 */

		linearLayout = (LinearLayout) findViewById(R.id.ll_main);
		viewPager = (ViewPager) findViewById(R.id.pager);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		newsNav = new ArrayList<NewsNavBarTitleModel>();
		textViews = new ArrayList<TextView>();

		progressbar = (ProgressBar) findViewById(R.id.progressbar);

		// 获取屏幕宽度和高度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		CommonalityInformation.mScreenWidth = dm.widthPixels;
		CommonalityInformation.mScreenHeight = dm.heightPixels;
	}

	/**
	 *
	 */
	public void getNavigationBarType() {
		new Thread() {
			public void run() {
				try {

					getString = HttpUtil
							.sendRequestString(CommonalityDataInterface.BaseUrl
									+ CommonalityDataInterface.getInformationType
									+ "?pageSize=6");
					ParseData(getString);

					handler.sendEmptyMessage(NAVIGATION_LOADING_SUCESS);

				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(NAVIGATION_CONNECTION_NETWORK_TIMEOUT);
				}
			}
		}.start();
	}

	/**
	 * @param gettring
	 */
	public void ParseData(String gettring) {
		try {

			JSONObject json = new JSONObject(gettring);
			JSONArray array = json.getJSONArray("Result");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject) array.opt(i);
				NewsNavBarTitleModel model = new NewsNavBarTitleModel();
				model.setNavID(obj.getString("_in_id"));
				model.setNavName(obj.getString("_in_title"));
				newsNav.add(model);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * 动态加载页面布局
	 */
	@SuppressWarnings("deprecation")
	public void InItView() {
		pageViews = new ArrayList<View>();
		// getDecorView()返回一个View对象

		for (int i = 0; i < newsNav.size(); i++) {
			NewsNavBarTitleModel model = newsNav.get(i);
			NewsListPageActivity news_page_activity = new NewsListPageActivity();
			Intent intent = new Intent(this, news_page_activity.getClass());
			intent.putExtra("NavTitleID", model.getNavID());
			pageViews.add(getLocalActivityManager().startActivity("" + i,
					intent).getDecorView());
		}
	}

	/***
	 * 动态加载导航栏标题
	 */
	public void InitNavigationBar() {
		// 获取屏幕的宽度，除以5，得到每个标题的宽度
		int width = getWindowManager().getDefaultDisplay().getWidth() / 5;
		int height = 80;
		for (int i = 0; i < newsNav.size(); i++) {
			NewsNavBarTitleModel model = newsNav.get(i);
			// 添加导航栏标题
			TextView textView = new TextView(this);
			textView.setText(model.getNavName());
			textView.setTextSize(17);
			textView.setTextColor(getResources().getColor(R.color.black));
			textView.setWidth(width);
			textView.setHeight(80);
			textView.setGravity(Gravity.CENTER);
			textView.setId(i);
			textView.setOnClickListener(this);
			textViews.add(textView);
			// 导航栏标题分割线
			View view = new View(this);
			LinearLayout.LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.width = 1;
			layoutParams.height = 50;
			layoutParams.gravity = Gravity.CENTER;
			view.setLayoutParams(layoutParams);
			view.setBackgroundColor(getResources().getColor(R.color.gray));
			// 添加至导航栏框架
			linearLayout.addView(textView);
			// 避免在最后一个标题加添分隔线
			if (i != newsNav.size() - 1) {
				linearLayout.addView(view);
			}

		}
	}

	@SuppressWarnings("deprecation")
	public void setSelector(int id) {
		// 导航栏自动滚动
		horizontalScrollView.smoothScrollTo((getWindowManager()
				.getDefaultDisplay().getWidth() / (newsNav.size()))
				* id, 0);
		for (int i = 0; i < newsNav.size(); i++) {
			if (id == i) {

				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.navigation_bar_bg);
				textViews.get(id).setBackgroundDrawable(
						new BitmapDrawable(bitmap));
				textViews.get(id).setTextColor(Color.parseColor("#31b2f7"));
				viewPager.setCurrentItem(i);
			}

			else {
				textViews.get(i).setBackgroundDrawable(new BitmapDrawable());
				textViews.get(i).setTextColor(
						getResources().getColor(R.color.black));
			}
		}
	}

	@Override
	public void onClick(View v) {
		setSelector(v.getId());
	}

	/**
	 * @author Administrator
	 *
	 */
	private class PageOnChanageListener implements OnPageChangeListener {

		/**
		 * 滑动ViewPager的时候,让上方的HorizontalScrollView自动切换
		 */
		public void onPageSelected(int position) {
			setSelector(position);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

	}

	/**
	 * @author Administrator
	 *
	 */
	private class PagerViewAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

	}

}
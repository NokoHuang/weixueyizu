package com.weixue.NewUI;

import java.util.ArrayList;


import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.weixue.weixueUI.ChoiceRecoActivity;
import com.weixue.weixueUI.CourseCenterActivity;
import com.weixue.weixueUI.OpenClassActivity;
import com.weixue.weixueUI.R;

/**
 * 
 *	微学堂 
 *
 *
 */

public class CourseCenterNewActivity extends Activity implements OnClickListener {

	private TextView mTextCenter, mTextQuality, mTextPublic;
	private ImageView mImgScr;
	private ViewPager mViewPager;
	private ArrayList<View> listViews;
	private int mScreen1_3;
	private int mCurrentPageIndex;

	@SuppressWarnings("deprecation")
	private LocalActivityManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_recommend);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		mScreen1_3 = metrics.widthPixels / 3;

		init();
	}

	private void init() {
		mTextCenter = (TextView) findViewById(R.id.text_center);
		mTextCenter.setOnClickListener(this);
		mTextQuality = (TextView) findViewById(R.id.text_quality);
		mTextQuality.setOnClickListener(this);
		mTextPublic = (TextView) findViewById(R.id.text_public);
		mTextPublic.setOnClickListener(this);
		mImgScr = (ImageView) findViewById(R.id.img_scr);
		LayoutParams lp = mImgScr.getLayoutParams();
		lp.width = mScreen1_3;
		mImgScr.setLayoutParams(lp);
		mViewPager = (ViewPager) findViewById(R.id.pager_our);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				resetTestView();
				switch (position) {
				case 0:
					mTextCenter.setTextColor(Color.parseColor("#08a5f7"));
					break;
				case 1:
					mTextQuality.setTextColor(Color.parseColor("#08a5f7"));
					break;
				case 2:
					mTextPublic.setTextColor(Color.parseColor("#08a5f7"));
					break;
				}
				mCurrentPageIndex = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPx) {
				
				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mImgScr
						.getLayoutParams();
				if (mCurrentPageIndex == 0 && position == 0) {// 0->1
					lp.leftMargin = (int) (positionOffset * mScreen1_3 + mCurrentPageIndex
							* mScreen1_3);
				} else if (mCurrentPageIndex == 1 && position == 0) {// 1->0
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positionOffset - 1)
							* mScreen1_3);
				} else if (mCurrentPageIndex == 1 && position == 1) {// 1->2
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + positionOffset
							* mScreen1_3);
				} else if (mCurrentPageIndex == 2 && position == 1) {// 2->1
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positionOffset - 1)
							* mScreen1_3);
				}
				mImgScr.setLayoutParams(lp);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});

		loadData();
	}

	protected void resetTestView() {
		mTextCenter.setTextColor(Color.BLACK);
		mTextQuality.setTextColor(Color.BLACK);
		mTextPublic.setTextColor(Color.BLACK);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.text_center:
			mViewPager.setCurrentItem(0);
			break;

		case R.id.text_quality:
			mViewPager.setCurrentItem(1);
			break;

		case R.id.text_public:
			mViewPager.setCurrentItem(2);
			break;

		}

	}

	private void loadData() {

		listViews = new ArrayList<View>();
		Intent courseIntent = new Intent(this, CourseCenterActivity.class);
		listViews.add(getView("CourseCenter", courseIntent));
		Intent choiceIntent = new Intent(this, ChoiceRecoActivity.class);
		listViews.add(getView("Choice", choiceIntent));
		Intent openClassIntent = new Intent(this, OpenClassActivity.class);
		listViews.add(getView("Open", openClassIntent));
		
		mViewPager.setAdapter(new GoodsPagerAdpter(listViews));
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

}

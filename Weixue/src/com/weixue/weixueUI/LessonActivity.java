package com.weixue.weixueUI;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.weixue.weixueUI.R;
import com.weixue.Adapter.LessionRecoAdapter;
import com.weixue.Adapter.LessonAdapter;

/**
 * 精品课程推荐
 * @author Vam 
 *
 */
public class LessonActivity extends ActivityGroup {

	private RadioGroup radioGroup;
	private RadioButton rb_choice,rb_hot,rb_open;
	private ImageView imgView;
	private float mCurrentCheckedRadioLeft;//当前被选中的RadioButton距离左侧的距离
	private HorizontalScrollView mHorizontalScrollView;//上面的水平滚动控件
	private ViewPager mViewPager;	//下方的可横向拖动的控件
	private ArrayList<View> mViews;//用来存放下方滚动的layout(layout_1,layout_2,layout_3)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recommend_page);
		initContrl();
		initListener();
		initVariable();
		
		rb_choice.setChecked(true);
        mViewPager.setCurrentItem(1);
        mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();
	}

	//初始化控件
	public void initContrl(){
		radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
		rb_choice=(RadioButton) findViewById(R.id.btn1);
		rb_hot=(RadioButton) findViewById(R.id.btn2);
		rb_open=(RadioButton) findViewById(R.id.btn3);
		imgView=(ImageView) findViewById(R.id.img1);
		mViewPager=(ViewPager) findViewById(R.id.pager);
		mHorizontalScrollView =(HorizontalScrollView) findViewById(R.id.horizontalScrollView);
	}
	
	
	//初始化数组，把layout装载进数组里面
	public void initVariable(){
		mViews =new ArrayList<View>();
		
		mViews.add(getLayoutInflater().inflate(R.layout.left_end, null)); //左端
		//内容页
		Intent intent;
		intent=new Intent();
		intent.setClass(LessonActivity.this, ChoiceRecoActivity.class);
		mViews.add(this.getLocalActivityManager().startActivity("choice_reco", intent).getDecorView());
//		mViews.add(getLayoutInflater().inflate(R.layout.teacher_reco, null));
//		mViews.add(getLayoutInflater().inflate(R.layout.hot_reco, null));
//		mViews.add(getLayoutInflater().inflate(R.layout.open_class, null));
		intent=new Intent();
		intent.setClass(LessonActivity.this, HotRecoActivity.class);
		mViews.add(this.getLocalActivityManager().startActivity("hot_reco", intent).getDecorView());
		
		intent=new Intent();
		intent.setClass(LessonActivity.this, OpenClassActivity.class);
		mViews.add(this.getLocalActivityManager().startActivity("open_class", intent).getDecorView());
		mViews.add(getLayoutInflater().inflate(R.layout.right_end, null));//右端
		
		mViewPager.setAdapter(new LessionRecoAdapter(mViews));
		
	}
	
	
	//监听器
	public void initListener()
	{
		radioGroup.setOnCheckedChangeListener(myCheckedChangeListener);
		mViewPager.setOnPageChangeListener(myPageChangeListener);

	}
	
	//单选框监听
	OnCheckedChangeListener myCheckedChangeListener=new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			
			AnimationSet _AnimationSet = new AnimationSet(true);
			TranslateAnimation _TranslateAnimation;
			if (checkedId == R.id.btn1) {
				_TranslateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, getResources().getDimension(R.dimen.rdo1), 0f, 0f);
				_AnimationSet.addAnimation(_TranslateAnimation);
				_AnimationSet.setFillBefore(false);
				_AnimationSet.setFillAfter(true);
				_AnimationSet.setDuration(100);

				imgView.startAnimation(_AnimationSet);//开始上面蓝色横条图片的动画切换
				mViewPager.setCurrentItem(1);//让下方ViewPager跟随上面的HorizontalScrollView切换
			}else if (checkedId == R.id.btn2) {
				_TranslateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, getResources().getDimension(R.dimen.rdo2), 0f, 0f);

				_AnimationSet.addAnimation(_TranslateAnimation);
				_AnimationSet.setFillBefore(false);
				_AnimationSet.setFillAfter(true);
				_AnimationSet.setDuration(100);

				//mImageView.bringToFront();
				imgView.startAnimation(_AnimationSet);
				
				mViewPager.setCurrentItem(2);
			}else if (checkedId == R.id.btn3) {
				_TranslateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, getResources().getDimension(R.dimen.rdo3), 0f, 0f);
				
				_AnimationSet.addAnimation(_TranslateAnimation);
				_AnimationSet.setFillBefore(false);
				_AnimationSet.setFillAfter(true);
				_AnimationSet.setDuration(100);
				
				//mImageView.bringToFront();
				imgView.startAnimation(_AnimationSet);
				
				mViewPager.setCurrentItem(3);
			}
			
			mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();//更新当前蓝色横条距离左边的距离
			mHorizontalScrollView.smoothScrollTo((int)mCurrentCheckedRadioLeft-(int)getResources().getDimension(R.dimen.rdo2), 0);
		}};
		
		/**
	     * 获得当前被选中的RadioButton距离左侧的距离
	     */
		private float getCurrentCheckedRadioLeft() {
			// TODO Auto-generated method stub
			if (rb_choice.isChecked()) {
				return getResources().getDimension(R.dimen.rdo1);
			}else if (rb_hot.isChecked()) {
				return getResources().getDimension(R.dimen.rdo2);
			}else if (rb_open.isChecked()) {
				return getResources().getDimension(R.dimen.rdo3);
			}
			return 0f;
		}
		
		
	//页面监听
	OnPageChangeListener myPageChangeListener=new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				mViewPager.setCurrentItem(1);
			}else if (position == 1) {
				rb_choice.performClick();
			}else if (position == 2) {
				rb_hot.performClick();
			}else if (position == 3) {
				rb_open.performClick();
			}else if(position == 4)
			{
				mViewPager.setCurrentItem(3);
			}
		
		}};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("onDestroy");
		super.onDestroy();
	}

//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		System.out.println("onPause");
//		super.onPause();
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		System.out.println("onResume");
//		
//		super.onResume();
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		// TODO Auto-generated method stub
//		System.out.println("onSaveInstanceState");
//		super.onSaveInstanceState(outState);
//	}
//
//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		System.out.println("onStop");
//		super.onStop();
//	}
		
		
}

package com.weixue.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 
 * @author Vam
 *
 */
public class LessionRecoAdapter extends PagerAdapter {


	private ArrayList<View> mViews;
	public LessionRecoAdapter(ArrayList<View> mViews)
	{

		this.mViews=mViews;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mViews.size();
	}

	@Override
	public Object instantiateItem(View v, int position) {
		// TODO Auto-generated method stub
		((ViewPager)v).addView(mViews.get(position));
		return mViews.get(position);
	}
	@Override
	public void destroyItem(View v, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)v).removeView(mViews.get(position));
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}



}

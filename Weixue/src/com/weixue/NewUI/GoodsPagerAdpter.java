package com.weixue.NewUI;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * ��Ʒ����Pager
 * @author ������
 *
 */
public class GoodsPagerAdpter extends PagerAdapter {

	private List<View> list;
	
	public GoodsPagerAdpter(List<View> list) {
		this.list=list;
	}
	
	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		ViewPager pViewPager = ((ViewPager) view);
		pViewPager.removeView(list.get(position));
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		ViewPager pViewPager = ((ViewPager) view);
		pViewPager.addView(list.get(position));
		return list.get(position);
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}

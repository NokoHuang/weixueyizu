package com.weixue.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.weixue.weixueUI.KaoShiActivity;
import com.weixue.weixueUI.KaoShiFragment;


public class KaoShiAdapter extends FragmentStatePagerAdapter {

	static final int NUM_ITEMS = 10;
	private int uId;
	public KaoShiAdapter(FragmentManager fm,int uId) {
		super(fm);
		this.uId=uId;
	}

	@Override
	public Fragment getItem(int position) {
		return KaoShiFragment.newInstance(position,uId);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return NUM_ITEMS;
	}

}

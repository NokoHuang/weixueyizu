package com.weixue.Adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weixue.Model.Subject;
import com.weixue.weixueUI.R;

public class SelectSubjectAdapter extends BaseAdapter{
	private Context mContext;
	private TextView item01;
	private List<Subject> li;
	private Typeface face;
	
	public SelectSubjectAdapter(Context mContext,List<Subject> li)
	{
		this.mContext=mContext;
		this.li=li;
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return li.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return li.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		View rowView = convertView;
		if (rowView == null) {
			rowView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_selectsubject, null);
		}
		item01=(TextView) rowView.findViewById(R.id.item01);
		item01.setTypeface(face);
		item01.setText(li.get(position).getSubjectName());
		return rowView;
	}
	 //更新适配器列表
	public void updateList(List<Subject> list) {
		this.li = list;
	}

}

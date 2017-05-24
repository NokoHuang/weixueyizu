package com.weixue.Adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weixue.Model.ChooseModel;
import com.weixue.Model.Subject;
import com.weixue.weixueUI.R;

public class ChooseAdapter extends BaseAdapter{
	private Context mContext;
	private TextView item01;
	private List<ChooseModel> li;
	private Typeface face;
	
	public ChooseAdapter(Context mContext,List<ChooseModel> li){
		this.mContext=mContext;
		this.li=li;
	}
	@Override
	public int getCount() {
		return li.size();
	}
	@Override
	public Object getItem(int position) {
		return li.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			rowView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_selectsubject, null);
		}
		item01=(TextView) rowView.findViewById(R.id.item01);
		item01.setTypeface(face);
		item01.setText(li.get(position).get_subject_name());
		return rowView;
	}
	 //更新适配器列表
	public void updateList(List<ChooseModel> list) {
		this.li = list;
	}

}

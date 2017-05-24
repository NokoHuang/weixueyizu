package com.weixue.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weixue.Adapter.VideoListAdapter.CacheView;
import com.weixue.Model.Ans;
import com.weixue.Model.Units;
import com.weixue.weixueUI.R;

public class AnsAdapter extends BaseAdapter {

	private Context mContext;
	private List<Ans> li_Ans;
	private TextView mTxtTitle,mTxtResult;
	public AnsAdapter(Context c,List<Ans> li_Ans){
		mContext = c;
		this.li_Ans=li_Ans;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return li_Ans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return li_Ans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_ans_list, null);
			mTxtTitle=(TextView) view.findViewById(R.id.txt_title);
			mTxtResult=(TextView) view.findViewById(R.id.txt_result);
			mTxtTitle.setText(li_Ans.get(position).getTitle());
			mTxtResult.setText(li_Ans.get(position).getResult());
		}

		return view;
	}

}

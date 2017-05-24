package com.example.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.NewsListModel;
import com.example.newspagedemo.R;

public class NewsAdapter extends BaseAdapter{
	
	LayoutInflater inflater;
	ArrayList<NewsListModel> array;
	
	public NewsAdapter(LayoutInflater inf,ArrayList<NewsListModel> array){
		this.inflater=inf;
		this.array=array;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return array.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		ViewHolder vh;
		if(convertView==null){
			vh=new ViewHolder();
			convertView=inflater.inflate(R.layout.news_adapter, null);
			
			vh.bitmap=(ImageView)convertView.findViewById(R.id.bitmap);
			vh.title=(TextView)convertView.findViewById(R.id.title);
			vh.date_time=(TextView)convertView.findViewById(R.id.date_time);
			
			convertView.setTag(vh);
		}
		vh=(ViewHolder)convertView.getTag();
		
		vh.bitmap.setImageBitmap(array.get(position).getBitmap());
		vh.title.setText(array.get(position).getTitle());
		vh.title.setTextSize(15);
		vh.date_time.setText(array.get(position).getDate_time());
		
		return convertView;
	}
	
	public class ViewHolder{
		TextView title,date_time;
		ImageView bitmap;
	}
	
	public void addAll(ArrayList<NewsListModel> newsArray)
	{
		 if(null == array)
			 array = new ArrayList<NewsListModel>();
		 array.addAll(newsArray);
		 
		 notifyDataSetChanged();
	}
}
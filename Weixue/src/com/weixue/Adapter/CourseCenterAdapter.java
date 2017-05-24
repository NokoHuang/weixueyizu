package com.weixue.Adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weixue.Control.HorizontalListView;
import com.weixue.Model.Course;
import com.weixue.Model.Major;
import com.weixue.Model.ResolveModel;
import com.weixue.weixueUI.MoreCourseActivity;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.SearchCourseActivity;
import com.weixue.weixueUI.SelectSubjectActivity;

public class CourseCenterAdapter extends BaseAdapter{

	private Context mContext;
	private HorizontalListViewAdapter adapter ;
    private TextView course_title,more;
    private HorizontalListView hlv;
    private ResolveModel resolveModel;
    
    public CourseCenterAdapter(Context mContext,ResolveModel resolveModel){
        this.mContext = mContext;
        this.resolveModel=resolveModel;
    }
    
    public void updateList(ResolveModel resolveModel){
    	this.resolveModel=resolveModel;
        this.notifyDataSetChanged();
    }
   
    public int getCount() {
        return resolveModel.getResult().size();
    }

    
    public Object getItem(int position) {
        return resolveModel.getResult().get(position);
    }

    
    public long getItemId(int position) {
        return position;
    }

 
    public View getView(final int position, View convertView, ViewGroup parent) {
    	
    	View view = convertView;
    	
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_coursecenter_page, null);
			 course_title = (TextView) view.findViewById(R.id.course_title);
			 more = (TextView) view.findViewById(R.id.more);
			 hlv = (HorizontalListView) view.findViewById(R.id.hlv);
			 CacheView cache=new CacheView();
			 cache.course_title=course_title;
			 cache.more=more;
			 cache.hlv=hlv;
			 view.setTag(cache);
		}else{
			CacheView cache=(CacheView) view.getTag();
			course_title=cache.course_title;
			more=cache.more;
			hlv=cache.hlv;
		}
	    
	    course_title.setText(resolveModel.getResult().get(position).getIntroduce());
	     
	    more.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent =new Intent().setClass(mContext, SelectSubjectActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				intent.putExtra("mid", resolveModel.getResult().get(position).getSubjectID());
				mContext.startActivity(intent);
				
			}});
	      
	    if(resolveModel.getResult().get(position).getCourseList()!=null){
	    	adapter= new HorizontalListViewAdapter(mContext,resolveModel.getResult().get(position).getCourseList());	    
		    hlv.setAdapter(adapter);
	    }
		return view;
    }
    
    class CacheView{
    	 public TextView course_title,more;
    	 public HorizontalListView hlv;
    }
    
}

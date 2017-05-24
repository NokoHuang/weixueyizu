package com.weixue.Adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.weixue.Function.AsyncImageLoader;
import com.weixue.Function.AsyncImageLoader.ImageCallback;
import com.weixue.Model.Course;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class MoreCourseAdapter extends BaseExpandableListAdapter{
	List<String> groups;
	//List<List<Map<String, String>>> chalds;
	List<List<Course>> li_bigCourse;
	private Context mContext;
	private ImageView img;
	private TextView tv_title,tv_content,tv_time,tv_count;
	// 允许最多加载50条信息
				private AsyncImageLoader asyncBitmapLoader;
				 Drawable drawable=null;
				 
	private boolean loadMore=false;
	private Button btn;
	public MoreCourseAdapter(Context mContext,List<String> groups,List<List<Course>> li_bigCourse){
		this.mContext=mContext;
		this.groups=groups;
		this.li_bigCourse=li_bigCourse;
		 asyncBitmapLoader=new AsyncImageLoader();
		//this.chalds=chalds;
	}
	
	public void update(List<String> groups,List<List<Course>> li_bigCourse){
		this.groups=groups;
		this.li_bigCourse=li_bigCourse;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return li_bigCourse.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.per_lesson_list_item, null);
		
			img=(ImageView) convertView.findViewById(R.id.per_lesson_img);
			tv_title= (TextView) convertView.findViewById(R.id.per_lesson_title);
			tv_content= (TextView) convertView.findViewById(R.id.per_lesson_content);
			tv_time= (TextView) convertView.findViewById(R.id.per_lesson_time);
			tv_count= (TextView) convertView.findViewById(R.id.per_lesson_count);

		}
		
		 String img_url =li_bigCourse.get(groupPosition).get(childPosition).getCourse_ImgUrl();
		 
			//去掉路径的空格
			img_url=img_url.replace(" ", "");
			
			img.setTag(img_url);
			//获取图片名称
			String fileName=asyncBitmapLoader.GetImgName(img_url);	 
			File file=new File(Constants_Url.PIC_CACHE_PATH+fileName);
			if(!file.isDirectory()){
			//先判断内存是否有该图片，有责加载，否则下载
			if(asyncBitmapLoader.ishavaImg(fileName)){
	    		drawable=asyncBitmapLoader.GetImgByfileName(fileName);
	    	}
			else{
				
			 drawable=asyncBitmapLoader.loadDrawable(img_url,img, new ImageCallback() {
				
				@Override
				public void imageLoaded(Drawable dd, String img_url,ImageView imgView) {
					// TODO Auto-generated method stub
					if(img_url.equals(imgView.getTag()))
						imgView.setImageDrawable(dd);
					
					
				}});
			} 
			}else{
				drawable=null;
			}
			//如果下载成功显示默认图片，否则压缩并显示图片
			if(drawable==null){
				img.setImageResource(R.drawable.liimg);
			}
			else{

			
				img.setImageDrawable(drawable);
			}
		
		
		
		tv_title.setText(li_bigCourse.get(groupPosition).get(childPosition).getCourseName());
		tv_content.setText(li_bigCourse.get(groupPosition).get(childPosition).getIntroduction());
		tv_time.setText(li_bigCourse.get(groupPosition).get(childPosition).getPublishDate());
		tv_count.setText(String.valueOf(li_bigCourse.get(groupPosition).get(childPosition).getStudyCount()));
//		if(isLastChild){
//			loadMore=true;
//			Intent i=new Intent();
//			i.setAction("qq123");
//			mContext.sendBroadcast(i);
//		}
		
		return convertView;
		
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return li_bigCourse.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_group_morecourse, null);
		}
		TextView tv=(TextView) convertView.findViewById(R.id.group_tv);
		tv.setText(groups.get(groupPosition));
		btn=(Button) convertView.findViewById(R.id.btn_moreCourse);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i=new Intent();
				i.setAction("qq123");
				mContext.sendBroadcast(i);
			}});
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;//子菜单是否可选
	}

	public void myUpdate(){
		
		this.notifyDataSetChanged();
		//loadMore=false;
	}
	
	
}
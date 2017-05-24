package com.weixue.Adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weixue.Function.AsyncImageLoader;
import com.weixue.Function.AsyncImageLoader.ImageCallback;
import com.weixue.Model.CourseListModel;
import com.weixue.NewUI.Course_video_playbackActivity;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class HorizontalListViewAdapter extends BaseAdapter{

	private Context mContext;
	// private List<Course> li_course;
	private List<CourseListModel> courseListModels;
	// 允许最多加载50条信息
	private AsyncImageLoader asyncBitmapLoader;
	private Drawable drawable=null;

	private ImageView img;
	private TextView tv_name;
	
	public HorizontalListViewAdapter(Context mContext,List<CourseListModel> courseListModels){
		this.mContext = mContext;
		this.courseListModels=courseListModels;
		asyncBitmapLoader=new AsyncImageLoader();

	}

	public int getCount() {
		return courseListModels.size();
	}


	public Object getItem(int position) {
		return courseListModels.get(position);
	}


	public long getItemId(int position) {
		return position;
	}


	public View getView( final int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_course_msg, null);
			img = (ImageView) view.findViewById(R.id.bookImg);
			tv_name= (TextView) view.findViewById(R.id.name);
			CacheView cache=new CacheView();
			cache.img=img;
			cache.tv_name=tv_name;
			view.setTag(cache);
		}else{
			CacheView cache=(CacheView) view.getTag();
			img=cache.img;
			tv_name=cache.tv_name;
		}

		//点击跳到试看界面
		img.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(mContext,Course_video_playbackActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 			
				Log.e("查看课程ID：", courseListModels.get(position).getCourseID()+"");
				i.putExtra("course", courseListModels.get(position));	
				
				mContext.startActivity(i);
				/*Intent intent = new Intent();
				intent.putExtra("course", courseListModels.get(position));	
				intent.setClass(mContext, Course_video_playbackActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				mContext.startActivity(intent);*/
			}

		}); 
		 String img_url =courseListModels.get(position).getCourse_ImgUrl();

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
				img.setImageResource(R.drawable.list_img);
			}
			else{

				img.setImageDrawable(drawable);
			} 

		if(courseListModels.get(position)!=null){
			tv_name.setText(courseListModels.get(position).getCourseName());
		}




		return view;
	}

	class CacheView{
		public ImageView img;
		public TextView tv_name;
	}

}

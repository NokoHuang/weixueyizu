package com.weixue.Adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weixue.Function.AsyncImageLoader;
import com.weixue.Function.AsyncImageLoader.ImageCallback;
import com.weixue.Model.Course;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class AllCourseAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<Course> array;
	//限制下载
	private AsyncImageLoader asyncBitmapLoader;
	private Drawable drawable=null;
	
	
	public AllCourseAdapter(LayoutInflater inf,List<Course> array){
		this.array=array;
		inflater=inf;
		asyncBitmapLoader=new AsyncImageLoader();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.all_course_adapter, null);
			vh.image=(ImageView)convertView.findViewById(R.id.per_lesson_img);
			vh.tv_Title=(TextView)convertView.findViewById(R.id.per_lesson_title);
			vh.tv_Content=(TextView)convertView.findViewById(R.id.per_lesson_content);
			vh.tv_Time=(TextView)convertView.findViewById(R.id.per_lesson_time);
			
			convertView.setTag(vh);
		}
		else{
			vh=(ViewHolder)convertView.getTag();
		}
		
		/********************设置图片**********************/
		String img_url =array.get(position).getCourse_ImgUrl();	
		
		//去掉路径的空格
		img_url=img_url.replace(" ", "");
		
		vh.image.setTag(img_url);
		//获取图片名称
		String fileName=asyncBitmapLoader.GetImgName(img_url);	  
		File file=new File(Constants_Url.PIC_CACHE_PATH+fileName);
		if(!file.isDirectory()){
		//先判断内存是否有该图片，有责加载，否则下载
		if(asyncBitmapLoader.ishavaImg(fileName)){
    		drawable=asyncBitmapLoader.GetImgByfileName(fileName);
    	}
		else{
			
		 drawable=asyncBitmapLoader.loadDrawable(img_url,vh.image, new ImageCallback() {
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
			vh.image.setImageResource(R.drawable.liimg);
		}
		else{
			vh.image.setImageDrawable(drawable);
		}
		
		vh.tv_Title.setText(array.get(position).getCourseName());
		vh.tv_Content.setText(array.get(position).getDetailIntro());
		vh.tv_Time.setText(array.get(position).getPublishDate());
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView image;
		TextView tv_Title,tv_Content,tv_Time;
	}
}

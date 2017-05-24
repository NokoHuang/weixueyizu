package com.weixue.Adapter;


import java.io.File;
import java.util.List;

import android.content.Context;
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
/**
 * 
 * @author Vam
 *
 */
public class PersionAdapter extends BaseAdapter{
	private Context mContext;
	private List<Course> li;
	// 允许最多加载50条信息
	private AsyncImageLoader asyncBitmapLoader;
	Drawable drawable=null;
	
	public PersionAdapter(){
		
	}
	public PersionAdapter(Context context,List<Course> li)
	{
		mContext=context;
		this.li=li;
		 asyncBitmapLoader=new AsyncImageLoader();
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
		ViewHolder holder;
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.per_lesson_list_item, null);
			holder =new ViewHolder();
			holder.img=(ImageView) convertView.findViewById(R.id.per_lesson_img);
			holder.tv_title= (TextView) convertView.findViewById(R.id.per_lesson_title);
			holder.tv_content= (TextView) convertView.findViewById(R.id.per_lesson_content);
			holder.tv_time= (TextView) convertView.findViewById(R.id.per_lesson_time);
			holder.tv_count= (TextView) convertView.findViewById(R.id.per_lesson_count);
			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		 String img_url =li.get(position).getCourse_ImgUrl();
//System.out.println(li.get(position).getCourse_ImgUrl());		
		
		
			//去掉路径的空格
			img_url=img_url.replace(" ", "");
			
			holder.img.setTag(img_url);
			//获取图片名称
			String fileName=asyncBitmapLoader.GetImgName(img_url);	  
			File file=new File(Constants_Url.PIC_CACHE_PATH+fileName);
			if(!file.isDirectory()){
			//先判断内存是否有该图片，有责加载，否则下载
			if(asyncBitmapLoader.ishavaImg(fileName)){
	    		drawable=asyncBitmapLoader.GetImgByfileName(fileName);
	    	}
			else{
				
			 drawable=asyncBitmapLoader.loadDrawable(img_url,holder.img, new ImageCallback() {
				
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
				holder.img.setImageResource(R.drawable.liimg);
			}
			else{

			
				holder.img.setImageDrawable(drawable);
			}
		
		
		
		holder.tv_title.setText(li.get(position).getCourseName());
		holder.tv_title.setTextSize(15);
		holder.tv_content.setText(li.get(position).getIntroduction());
		holder.tv_time.setText(li.get(position).getPublishDate());
		holder.tv_count.setText(String.valueOf(li.get(position).getStudyCount()));
	
		return convertView;
	}

	static class ViewHolder {
		ImageView img;
		TextView tv_title;
		TextView tv_content;
		TextView tv_time;
		TextView tv_count;
	}
}

package com.weixue.Adapter;


import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.weixue.Function.AsyncImageLoader;
import com.weixue.Function.AsyncImageLoader.ImageCallback;
import com.weixue.Model.Course;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;


/**
 * 
 * @author Vam & chenjunjie
 *
 */
public class LessonAdapter extends BaseAdapter {

	private Context mContext;
	private List<Course> li_course;
	// 允许最多加载50条信息
				private AsyncImageLoader asyncBitmapLoader;
				 Drawable drawable=null;
	public LessonAdapter(){}
	public LessonAdapter(Context mContext,List<Course> li_course){
		this.mContext=mContext;
		this.li_course=li_course;
		 asyncBitmapLoader=new AsyncImageLoader();
	}
	@Override
	public int getCount() {
		return li_course.size();
	}

	@Override
	public Object getItem(int position) {
		return li_course.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(mContext).inflate(R.layout.lesson_list_item, null);
			holder=new ViewHolder();
			holder.img=(ImageView) convertView.findViewById(R.id.lesson_img);
			holder.tv_title=(TextView) convertView.findViewById(R.id.lesson_title);
			holder.tv_count=(TextView) convertView.findViewById(R.id.look_count);
			holder.rb_score=(RatingBar) convertView.findViewById(R.id.lesson_rate);
			holder.imgbtn_play=(ImageButton) convertView.findViewById(R.id.ibtn_play);
			holder.imgbtn_play.setFocusable(false);
			holder.ly_play=(LinearLayout) convertView.findViewById(R.id.lesson_ly_play);
			

			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		 String img_url =li_course.get(position).getCourse_ImgUrl();
		 
		
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
				holder.img.setImageResource(R.drawable.list_img);
			}
			else{

				holder.img.setImageDrawable(drawable);
			} 
		
		holder.tv_title.setText(li_course.get(position).getCourseName());
		holder.tv_count.setText(String.valueOf(li_course.get(position).getStudyCount()));
		holder.rb_score.setRating(Float.valueOf(String.valueOf(li_course.get(position).getPraiseDegree())));
		

		
		return convertView;
	}

	static class ViewHolder{
		ImageView img;
		TextView tv_title;
		TextView tv_count;
		RatingBar rb_score;
		LinearLayout ly_play;
		ImageButton imgbtn_play;
	} 


}

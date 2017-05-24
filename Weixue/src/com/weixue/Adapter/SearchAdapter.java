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
 * @author Vam && chenjunjie
 *
 */
public class SearchAdapter extends BaseAdapter {

	private Context context;
	private List<Course> li;
	// 允许最多加载50条信息
	private AsyncImageLoader asyncBitmapLoader;
	 Drawable drawable=null;
	public SearchAdapter(Context context,List<Course> li)
	{
		this.context=context;
		this.li=li;
		asyncBitmapLoader=new AsyncImageLoader();
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
		ViewHolder holder;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(context).inflate(R.layout.more_video_list_item, null);
			holder=new ViewHolder();
			holder.img=(ImageView) convertView.findViewById(R.id.lesson_img);
			holder.tv_title=(TextView) convertView.findViewById(R.id.lesson_title);
			holder.tv_content=(TextView) convertView.findViewById(R.id.lesson_content);
			holder.tv_enrollment=(TextView) convertView.findViewById(R.id.lesson_enrollment);
			holder.tv_updated=(TextView) convertView.findViewById(R.id.lesson_updated);
			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}		
		Course course=li.get(position);
		
		 String img_url =course.getCourse_ImgUrl();
		 
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
		
		 
		  holder.tv_title.setText(course.getCourseName());
		  holder.tv_content.setText(course.getIntroduction());
		  holder.tv_enrollment.setText(String.valueOf(course.getStudyCount()));
		  holder.tv_updated.setText(course.getPublishDate());
		
		return convertView;
	}
	
	/**
	 * 更新数据
	 * @param li
	 */
	public void updateList(List<Course> li){
		this.li=li;
		this.notifyDataSetChanged();
	}

	static class ViewHolder{
		private ImageView img;
		private TextView tv_title;
		private TextView tv_content;
		private TextView tv_enrollment;
		private TextView tv_updated;
	}
}


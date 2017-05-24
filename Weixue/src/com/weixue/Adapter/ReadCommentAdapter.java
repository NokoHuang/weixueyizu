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
import com.weixue.Model.Comment;
import com.weixue.Model.PersonInformation;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class ReadCommentAdapter extends BaseAdapter{
private Context mContext;
private List<Comment> li;
private List<PersonInformation> li_Info;
//允许最多加载50条信息
		private AsyncImageLoader asyncBitmapLoader;
		 Drawable drawable=null;
		 
		 private TextView tv_title,tv_content,tv_date;
		 private ImageView iv;
		 
public ReadCommentAdapter(Context mContext,List<Comment> li,List<PersonInformation> li_Info){
	this.mContext=mContext;
	this.li=li;
	this.li_Info=li_Info;
	  asyncBitmapLoader=new AsyncImageLoader();
}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return li.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return li.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
    	
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_lv_readcomment, null);
			 iv = (ImageView) convertView.findViewById(R.id.userMsgImg);
		     tv_title = (TextView) convertView.findViewById(R.id.title);
		     tv_content = (TextView) convertView.findViewById(R.id.content);
		     tv_date = (TextView) convertView.findViewById(R.id.time);
		    
		     CacheView cache=new CacheView();
		     cache.iv=iv;
		     cache.tv_title=tv_title;
		     cache.tv_date=tv_date;
		     cache.tv_content=tv_content;
		    
		     convertView.setTag(cache);
		}else{
			CacheView cache=(CacheView) convertView.getTag();
			iv = cache.iv;
		     tv_title = cache.tv_title;
		     tv_content = cache.tv_content;
		     tv_date = cache.tv_date;
		   
		}
		
		
		
		//System.out.println("size-->"+li.size());
		//System.out.println("li_Infosize-->"+li_Info.size());
		Comment comment=li.get(position);
		PersonInformation info=li_Info.get(position);
		
	    String img_url =info.getLargePhotoPath();
	   // System.out.println("img_url-->"+img_url);	   
		//去掉路径的空格
		img_url=img_url.replace(" ", "");
		//System.out.println("图片路径："+img_url);
		iv.setTag(img_url);
		//获取图片名称
		String fileName=asyncBitmapLoader.GetImgName(img_url);	
		File file=new File(Constants_Url.PIC_CACHE_PATH+fileName);
		if(!file.isDirectory()){
		//先判断内存是否有该图片，有责加载，否则下载
		if(asyncBitmapLoader.ishavaImg(fileName)){
    		drawable=asyncBitmapLoader.GetImgByfileName(fileName);
    	}
		else{
			
		 drawable=asyncBitmapLoader.loadDrawable(img_url,iv, new ImageCallback() {
			
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
			iv.setImageResource(R.drawable.liimg);
		}
		else{

			iv.setImageDrawable(drawable);
		}
		
		
		
		//iv.setImageResource(R.drawable.liimg);
	    tv_title.setText(info.getUsername());
	    tv_content.setText(comment.getContent());
	    tv_date.setText(comment.getCreateTime());
	  
	    
		return convertView;
	}

	  public class CacheView {
	    	public TextView tv_title,tv_content,tv_date;
	    	public ImageView iv;
	    }
	
}

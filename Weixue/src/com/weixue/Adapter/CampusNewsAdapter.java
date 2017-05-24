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
import com.weixue.Model.Status;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class CampusNewsAdapter extends BaseAdapter{

	private Context mContext;
	private List<Status> li_status;

	// 允许最多加载50条信息
		private AsyncImageLoader asyncBitmapLoader;
		 Drawable drawable=null;
	
		 private TextView tv_title,tv_content,tv_date,tv_pinglunNum;
		 private ImageView iv;
		 
    public CampusNewsAdapter(Context c,List<Status> li_status){
        mContext = c;
        this.li_status=li_status;
        asyncBitmapLoader=new AsyncImageLoader();

    }
    
    public void upDate(List<Status> li_status){
    	 this.li_status=li_status;
    }
   
    public int getCount() {
        return li_status.size();
    }

    
    public Object getItem(int position) {
        return li_status.get(position);
    }

    
    public long getItemId(int position) {
        return position;
    }

 
    public View getView(int position, View convertView, ViewGroup parent) {
    	  
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.campusnews_listview_item, null);
			 iv = (ImageView) convertView.findViewById(R.id.userMsgImg);
		     tv_title = (TextView) convertView.findViewById(R.id.title);
		     tv_content = (TextView) convertView.findViewById(R.id.content);
		     tv_date = (TextView) convertView.findViewById(R.id.time);
		     tv_pinglunNum = (TextView) convertView.findViewById(R.id.pinglunNum);
		     CacheView cache=new CacheView();
		     cache.iv=iv;
		     cache.tv_title=tv_title;
		     cache.tv_date=tv_date;
		     cache.tv_content=tv_content;
		     cache.tv_pinglunNum=tv_pinglunNum;
		     convertView.setTag(cache);
		}else{
			CacheView cache=(CacheView) convertView.getTag();
			iv = cache.iv;
		     tv_title = cache.tv_title;
		     tv_content = cache.tv_content;
		     tv_date = cache.tv_date;
		     tv_pinglunNum =cache.tv_pinglunNum;
		}
		
			
		
	    
	    Status status=li_status.get(position);
	    String img_url =status.getPicPath();
	   
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
		  
	    tv_title.setText(status.getUserName());
	    tv_content.setText(status.getContentText());
	    tv_date.setText(status.getCreateTime());
	    tv_pinglunNum.setText(String.valueOf(status.getCommentCount()));
	    
		return convertView;
    }
    
    public class CacheView {
    	public TextView tv_title,tv_content,tv_date,tv_pinglunNum;
    	public ImageView iv;
    }
    
}

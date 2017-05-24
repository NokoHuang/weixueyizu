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
import com.weixue.Model.PersonInformation;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class SearchFriendAdapter extends BaseAdapter{

	private Context mContext;
	private List<PersonInformation> li_person;
	//控件
	private ImageView iv_personHead;
	private TextView tv_personName,tv_sign;
	// 允许最多加载50条信息
	private AsyncImageLoader asyncBitmapLoader;
	 Drawable drawable=null;
	
	public SearchFriendAdapter(Context mContext,List<PersonInformation> li_person){
		this.mContext=mContext;
		this.li_person=li_person;
		 asyncBitmapLoader=new AsyncImageLoader();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return li_person.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return li_person.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(arg1==null){
			arg1=LayoutInflater.from(mContext).inflate(R.layout.item_lv_searchfriend, null);
			iv_personHead=(ImageView) arg1.findViewById(R.id.iv_personHead);
			tv_personName=(TextView) arg1.findViewById(R.id.tv_personName);
			tv_sign=(TextView) arg1.findViewById(R.id.tv_sign);
			CacheView cache=new CacheView();
			cache.iv_personHead=iv_personHead;
			cache.tv_personName=tv_personName;
			cache.tv_sign=tv_sign;
			arg1.setTag(cache);
		}else{
			CacheView cache=(CacheView) arg1.getTag();
			iv_personHead=cache.iv_personHead;
			tv_personName=cache.tv_personName;
			tv_sign=cache.tv_sign;
		}
		
		String img_url =li_person.get(arg0).getLargePhotoPath();
		 
		//去掉路径的空格
		img_url=img_url.replace(" ", "");
		
		iv_personHead.setTag(img_url);
		//获取图片名称
		String fileName=asyncBitmapLoader.GetImgName(img_url);	 
		File file=new File(Constants_Url.PIC_CACHE_PATH+fileName);
		if(!file.isDirectory()){
		//先判断内存是否有该图片，有责加载，否则下载
		if(asyncBitmapLoader.ishavaImg(fileName)){
    		drawable=asyncBitmapLoader.GetImgByfileName(fileName);
    	}
		else{
			
		 drawable=asyncBitmapLoader.loadDrawable(img_url,iv_personHead, new ImageCallback() {
			
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
			iv_personHead.setImageResource(R.drawable.liimg);
		}
		else{		
			iv_personHead.setImageDrawable(drawable);
		}
		
		tv_personName.setText(li_person.get(arg0).getUsername());
		tv_sign.setText(li_person.get(arg0).getSignature());
		
		
		return arg1;
	}
	
	//更新
	public void updateList(List<PersonInformation> li){
		this.li_person=li;
		this.notifyDataSetChanged();
	}
	
	public class CacheView{
		public ImageView iv_personHead;
		public TextView tv_personName,tv_sign;
	}

}

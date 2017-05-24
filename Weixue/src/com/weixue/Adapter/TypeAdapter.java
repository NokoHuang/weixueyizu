package com.weixue.Adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.weixue.Model.InforTypeModel;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class TypeAdapter extends BaseAdapter{

	Context mContext;
	ArrayList<InforTypeModel> array;
	private AsyncImageLoader asyncBitmapLoader;
	Drawable drawable=null;
	public TypeAdapter(Context mContext,List<InforTypeModel> array){
		this.mContext=mContext;
		this.array=(ArrayList<InforTypeModel>) array;
		asyncBitmapLoader=new AsyncImageLoader();
	}
	@Override
	public int getCount() {
		return array.size();
	}
	@Override
	public Object getItem(int arg0) {
		return array.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		ViewHolder vh;
		if(convertView==null){
			vh=new ViewHolder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.type_adapter, null);

			vh.bitmap=(ImageView)convertView.findViewById(R.id.bitmap);
			vh.title=(TextView)convertView.findViewById(R.id.title);

			convertView.setTag(vh);
		}
		vh=(ViewHolder)convertView.getTag();



		String img_url =array.get(position).get_po_image();
		System.out.println("img_url:"+img_url);

		//去掉路径的空格
		img_url=img_url.replace(" ", "");			
		vh.bitmap.setTag(img_url);
		//获取图片名称
		String fileName=asyncBitmapLoader.GetImgName(img_url);
		File file=new File(Constants_Url.PIC_CACHE_PATH+fileName);
		if(!file.isDirectory()){
			//先判断内存是否有该图片，有责加载，否则下载
			if(asyncBitmapLoader.ishavaImg(fileName)){
				drawable=asyncBitmapLoader.GetImgByfileName(fileName);
			}
			else{
				drawable=asyncBitmapLoader.loadDrawable(img_url,vh.bitmap, new ImageCallback() {
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
			vh.bitmap.setImageResource(R.drawable.list_img);
		}
		else{
			vh.bitmap.setImageDrawable(drawable);
		} 

		vh.title.setText(array.get(position).get_po_title());

		return convertView;
	}

	public class ViewHolder{
		TextView title;
		ImageView bitmap;
	}

	public void addAll(ArrayList<InforTypeModel> newsArray)
	{
		if(null == array)
			array = new ArrayList<InforTypeModel>();
		array.addAll(newsArray);
		notifyDataSetChanged();
	}
}
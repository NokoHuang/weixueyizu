package com.weixue.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weixue.Model.CourseWare;
import com.weixue.Model.Units;
import com.weixue.weixueUI.R;

public class VideoListAdapter extends BaseAdapter{

	private Context mContext;
    private List<CourseWare> li_units;
    private TextView tv_name;
    private ImageView img;
    private int  selectItem=0;
    private String type;
    public VideoListAdapter(Context c,List<CourseWare> li_units,String type){
        mContext = c;
        this.li_units=li_units;
        this.type=type;
    }
   
    public int getCount() {
        return li_units.size();
    }

    
    public Object getItem(int position) {
        return li_units.get(position);
    }

    
    public long getItemId(int position) {
        return position;
    }

 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View view = convertView;
    	
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_video_list_page, null);
			CacheView cache=new CacheView();
			tv_name=(TextView) view.findViewById(R.id.text_name);
			img=(ImageView) view.findViewById(R.id.img);
			cache.tv_name=tv_name;
			cache.mImg=img;
			view.setTag(cache);
		}else{
			CacheView cache=(CacheView) view.getTag();
			tv_name=cache.tv_name;
			img=cache.mImg;
		}
		tv_name.setText(li_units.get(position).getName());
		
	    if(type.equals("ppt")){
	    	img.setImageResource(R.drawable.fileicon_ppt);
	    }else if(type.equals("doc")){
	    	img.setImageResource(R.drawable.fileicon_word);
	    }
		return view;
    }
    
    public  void setSelectItem(int selectItem) {  
        this.selectItem = selectItem;  
   }  
    
    public class CacheView{
    	public TextView tv_name;
    	public ImageView mImg;
    }
    
}

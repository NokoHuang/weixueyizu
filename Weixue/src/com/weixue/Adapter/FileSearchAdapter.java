package com.weixue.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weixue.weixueUI.R;

public class FileSearchAdapter extends BaseAdapter{

	private Context mContext;
	private List<String> list;
    
    public FileSearchAdapter(Context c,List<String> list){
        mContext = c;
        this.list = list;
    }
   
    public int getCount() {
        return list.size();
    }

    
    public Object getItem(int position) {
        return position;
    }

    
    public long getItemId(int position) {
        return position;
    }

 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View view = convertView;
    	
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_file_search_page, null);
		}
	    TextView tv_name = (TextView) view.findViewById(R.id.name);
	    
	    String str = list.get(position);
	    String newStr = str.substring(str.lastIndexOf("/")+1,str.length());
	    tv_name.setText(newStr);
	    
		return view;
    }
    
}

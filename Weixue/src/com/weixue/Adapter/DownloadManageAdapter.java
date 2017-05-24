package com.weixue.Adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weixue.Model.DownloadFile;
import com.weixue.Service.DownLoadService;
import com.weixue.Service.DownLoadService.DownloadTask;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.Wanzheng_Course_video_playbackActivity;

public class DownloadManageAdapter extends BaseAdapter{
private Context mContext;
private List<DownloadFile> li;
//private Map<String,DownloadTask> map_task;
private TextView tv_filesize,tv_filename,tv_progress_size;
//private Button btn_playOrStop;
private ProgressBar pb;
//private Button btn_pause,btn_restart;
public DownloadManageAdapter(Context mContext,List<DownloadFile> li){
	this.mContext=mContext;
	this.li=li;

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_lv_downloadmanager, null);			
			tv_filesize=(TextView) convertView.findViewById(R.id.tv_fileSize);
			tv_filename=(TextView) convertView.findViewById(R.id.tv_fileName);
			tv_progress_size=(TextView) convertView.findViewById(R.id.tv_progressSize);
			pb=(ProgressBar) convertView.findViewById(R.id.pb_sizeChange);
			//btn_playOrStop=(Button) convertView.findViewById(R.id.btn_playOrStop);
		//	btn_pause=(Button) convertView.findViewById(R.id.btn_pause);
		//	btn_restart=(Button) convertView.findViewById(R.id.btn_restart);
			CacheView cache=new CacheView();
			cache.tv_filesize=tv_filesize;
			cache.tv_filename=tv_filename;
			cache.tv_progress_size=tv_progress_size;
			//cache.btn_playOrStop=btn_playOrStop;
			cache.pb=pb;
		//	cache.btn_pause=btn_pause;
		//	cache.btn_restart=btn_restart;
			convertView.setTag(cache);
		}else{
			CacheView cache=(CacheView) convertView.getTag();
			tv_filesize=cache.tv_filesize;
			tv_filename=cache.tv_filename;
			tv_progress_size=cache.tv_progress_size;
			//btn_playOrStop=cache.btn_playOrStop;
			pb=cache.pb;
		//	btn_pause=cache.btn_pause;
		//	btn_restart=cache.btn_restart;
		}
		
		if(pb.getMax()!=li.get(position).fileSize){
		pb.setMax(li.get(position).fileSize);
		
		tv_filesize.setText(String.valueOf(li.get(position).fileSize));
		
		tv_filename.setText(li.get(position).fileName);
		
//		btn_playOrStop.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(String.valueOf(btn_playOrStop.getText()).equals("暂停")){
//					btn_playOrStop.setText("开始");
//					map_task.get(li.get(position).getDownLoadAddress()).exit();
//					System.out.println("暂停下载");
//				}else{
//					btn_playOrStop.setText("暂停");	
//					Intent intent=new Intent(mContext,DownLoadService.class);
//					intent.putExtra("path",li.get(position).getDownLoadAddress());
//					mContext.startService(intent);
//					System.out.println("开始下载");
//					
//				}
//			}});
		
		}		
		pb.setProgress(li.get(position).downloadSize);
		//System.out.println("downLength-->"+li.get(position).downloadSize);
		tv_progress_size.setText(String.valueOf(li.get(position).downloadSize));
		
	
	
		
		return convertView;
	}
	
	public void update(List<DownloadFile> li){
		this.li=li;
		notifyDataSetChanged();
	}

	public class CacheView{
		public TextView tv_filesize,tv_filename,tv_progress_size;
		//public Button btn_playOrStop;
		public ProgressBar pb;
		//public Button btn_pause,btn_restart;
	}
}

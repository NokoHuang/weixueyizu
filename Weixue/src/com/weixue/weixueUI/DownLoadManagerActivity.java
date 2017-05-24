package com.weixue.weixueUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Adapter.DownloadManageAdapter;
import com.weixue.Control.MyFloatView;
import com.weixue.Function.MyInstance;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.DownloadFile;
import com.weixue.MyDialog.iphoneDialog;
import com.weixue.MyInterface.ProgressChangeListener;
import com.weixue.Service.DownLoadService;
import com.weixue.Service.DownLoadService.DownloadTask;
import com.weixue.Service.FileService;
import com.weixue.Utils.Constants;

public class DownLoadManagerActivity extends Activity implements ProgressChangeListener{
	private Context mContext;
	private DownLoadService services=null;
	private ListView lv_item;
	private List<DownloadFile> li;
	private Map<String,DownloadTask> map_task;
	private DownloadManageAdapter adapter;

	private FileService fileService;
	
	private ImageView img_update;
	private TextView tv_title;
	// 返回按钮
		private LinearLayout line_back;
		
		
		private int downloadNum=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.lv_downloadmanager_list_page);
		initUi();
		
	}
public void initUi(){
	mContext=this;
	fileService=new FileService(mContext);
	li=new ArrayList<DownloadFile>();
	lv_item=(ListView) findViewById(R.id.lv_item);
	
	line_back = (LinearLayout) findViewById(R.id.line_back);
	line_back.setOnClickListener(listener);
	
	
	img_update=(ImageView) findViewById(R.id.menu);	
	img_update.setOnClickListener(listener);
	
	tv_title=(TextView) findViewById(R.id.tv_title);
	
	
	
	downloadNum=MyInstance.getSharedPreferencesInt(mContext, Constants.DOWNLOADNUM);
	if(downloadNum==0){
		downloadNum=2;
	}

//if(!isServiceStart()){
//	List<String> li=fileService.getAllData();
//	for(int j=0;j<li.size();j++){	
//	Intent intent=new Intent(DownLoadManagerActivity.this,DownLoadService.class);
//	intent.putExtra("path", li.get(j));	
//	startService(intent);	
//	}
//}
	li=fileService.getAllData();
	adapter=new DownloadManageAdapter(mContext,li);
	lv_item.setAdapter(adapter);
	lv_item.setOnItemClickListener(itemListener);
	bindService();	
	
	
}
private void bindService(){
    Intent intent = new Intent(DownLoadManagerActivity.this,DownLoadService.class);
    bindService(intent, conn, Context.BIND_AUTO_CREATE);
}
ServiceConnection conn = new ServiceConnection(){

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub

		services=((DownLoadService.MyBinder) service).getService();
		services.setProgressChangeListener(DownLoadManagerActivity.this);
	
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}};

	
	@Override
	public void progressChange(boolean isFinish) {
		// TODO Auto-generated method stub
		
		handler.sendMessage(handler.obtainMessage(0, isFinish));
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			boolean isFinish=(Boolean) msg.obj;
			if(String.valueOf(tv_title.getText()).equals(mContext.getResources().getString(R.string.downloadingfile))&&!isFinish){
			adapter.notifyDataSetChanged();
			}else if(String.valueOf(tv_title.getText()).equals(mContext.getResources().getString(R.string.downloadingfile))&&isFinish){
				
				map_task=services.getMap();
				Set<String> set1=map_task.keySet();
				Object[] ob1=set1.toArray();
				li=new ArrayList<DownloadFile>();
				for(int i=0;i<map_task.size();i++){
					DownloadFile file=map_task.get(ob1[i]).getDownloadFile();
					li.add(file);
				}					
				adapter.update(li);
				
			}
			super.handleMessage(msg);
		}
		
	};
/**
 * 判断服务是否启动
 * @return
 */
	public boolean isServiceStart(){
		 ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		 List<RunningServiceInfo> infos = am.getRunningServices(30); //30是最大值
		 for(RunningServiceInfo info : infos){
		     if(info.service.getClassName().equals("com.weixue.Service.DownLoadService")){
		    	 
		         return true;
		     }
		     
		 }
		 return false;
	}
	
@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	unbindService(conn);

	super.onDestroy();
}

@Override	
public boolean onKeyDown(int keyCode, KeyEvent event) {
	 
    if (keyCode == KeyEvent.KEYCODE_BACK
             && event.getRepeatCount() == 0) {
          finish();
         return true;
     }
     return super.onKeyDown(keyCode, event);
 }
//未完成下载界面使用	
OnItemClickListener itemListener=new OnItemClickListener(){

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		// TODO Auto-generated method stub				
		iphoneDialog.iphoneDialogBuilder idb = new iphoneDialog.iphoneDialogBuilder(mContext);
		idb.setTitle(getApplicationContext().getResources().getString(R.string.prompt));
		idb.setMessage(getApplicationContext().getResources().getString(R.string.isstartdownloadfile));
		idb.setPositiveButton(getApplicationContext().getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(services.getCount()<downloadNum){
					Intent intent=new Intent(DownLoadManagerActivity.this,DownLoadService.class);
					intent.putExtra("path", li.get(arg2).getDownLoadAddress());	
					intent.putExtra("fileSize", li.get(arg2).getFileSize());	
					startService(intent);
					fileService.deleteFinishFile(li.get(arg2).getDownLoadAddress());
					li=fileService.getAllData();
					adapter.update(li);
					}else{
						Toast.makeText(mContext, "超过最大下载！正在下载"+downloadNum+"个文件!", Toast.LENGTH_SHORT).show();
					}
			}
		});
		idb.setNegativeButton(getApplicationContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		idb.show();
		
	}};
	//下载中的界面使用
	OnItemClickListener itemListener1=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			// TODO Auto-generated method stub
			
			
			iphoneDialog.iphoneDialogBuilder idb = new iphoneDialog.iphoneDialogBuilder(mContext);
			idb.setTitle(getApplicationContext().getResources().getString(R.string.prompt));
			idb.setMessage(getApplicationContext().getResources().getString(R.string.isstopdownloadfile));
			idb.setPositiveButton(getApplicationContext().getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Set<String> set=map_task.keySet();
					Object[] ob=set.toArray();
					map_task.get(ob[arg2]).exit();
					map_task=services.getMap();
					Set<String> set1=map_task.keySet();
					Object[] ob1=set1.toArray();
					li=new ArrayList<DownloadFile>();
					for(int i=0;i<map_task.size();i++){
						DownloadFile file=map_task.get(ob1[i]).getDownloadFile();
						li.add(file);
					}					
					adapter.update(li);
				}
			});
			idb.setNegativeButton(getApplicationContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			idb.show();
		}};

	OnClickListener listener=new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
			case R.id.menu:
				
				String str=mContext.getResources().getString(R.string.downloadingfile);
				if(!String.valueOf(tv_title.getText()).equals(str)){
					//如果不是正在下载界面则改为正在下载界面
					lv_item.setOnItemClickListener(itemListener1);
					li=new ArrayList<DownloadFile>();
					lv_item.setAdapter(null);
					tv_title.setText(str);
					map_task=services.getMap();					
					Set<String> set=map_task.keySet();
					Object[] ob=set.toArray();
						for(int i=0;i<map_task.size();i++){
							DownloadFile file=map_task.get(ob[i]).getDownloadFile();
							li.add(file);
						}
						adapter=new DownloadManageAdapter(mContext,li);
						lv_item.setAdapter(adapter);
				}else{
					//改为未下载界面
					lv_item.setOnItemClickListener(itemListener);
					tv_title.setText(mContext.getResources().getString(R.string.nofinishfile));					
					li=fileService.getAllData();
					adapter=new DownloadManageAdapter(mContext,li);
					lv_item.setAdapter(adapter);
				}
				break;
			case R.id.line_back:
				finish();
				break;
			}
		}};

}

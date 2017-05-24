package com.weixue.weixueUI;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.weixue.Control.MyFloatView;
import com.weixue.MyDialog.iphoneDialog;
import com.weixue.NewUI.CourseCenterNewActivity;
import com.weixue.Service.DownLoadService;
import com.weixue.Utils.Constants;
/**
 * 程序主框架
 * @author luzhanggang
 *
 */
public class MainActivity extends TabActivity {
   private static final String PERSON_INFORMATION = Constants.PERSON_INFORMATION;
   private Context mContext;
   private static String INFORMATION=Constants.INFORMATION;
	private RadioGroup group;
	private TabHost tabHost;
	private int i=0;
	private RadioButton rb1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bottom_bar);
		
		loadTab();
	}

	/**加载tab菜单*/
	private void loadTab() {
		mContext=this;
		group = (RadioGroup)findViewById(R.id.main_radio);
		tabHost = getTabHost();
		
		Intent intent;
		
		//设置第一个Tab,newTabsec为添加一个标签并命名为个人天地，setIndicator为指定标签的文本信息为个人天地,以下同理
		/*
		i = new Intent().setClass(this, PersonalCenterActivity.class);
		//用于刷新此选项卡
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		tabHost.addTab(tabHost.newTabSpec("个人天地").setIndicator("个人天地").setContent(i));*/
		
		intent=new Intent().setClass(this, PersonalCenterActivity.class);
		tabHost.addTab(tabHost.newTabSpec("个人天地").setIndicator("个人天地").setContent(intent));
		
		intent = new Intent().setClass(this, CourseCenterNewActivity.class);
		tabHost.addTab(tabHost.newTabSpec("课程中心").setIndicator("课程中心").setContent(intent));

		intent = new Intent().setClass(this, com.example.newspagedemo.MainActivity.class);
		tabHost.addTab(tabHost.newTabSpec("精品推荐").setIndicator("精品推荐").setContent(intent));

		intent = new Intent().setClass(this, CampusNewsActivity.class);
		tabHost.addTab(tabHost.newTabSpec("互动平台").setIndicator("互动平台").setContent(intent));
		
		intent = new Intent().setClass(this,MoreActivity.class);
		tabHost.addTab(tabHost.newTabSpec("更多").setIndicator("更多").setContent(intent));
		
		

		//RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.main_radio);
		rb1=(RadioButton) findViewById(R.id.radio_button0);
		rb1.setBackgroundResource(R.drawable.home_btn_bg_d);
		
		//对tabHost选项卡里相应的tab进行响应
		group.setOnCheckedChangeListener(checkedChangeListener);      
    }
	
	OnCheckedChangeListener checkedChangeListener= new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			rb1.setBackgroundResource(R.drawable.home_btn_bg);
			switch (checkedId) {
			case R.id.radio_button0:
				tabHost.setCurrentTabByTag("个人天地");
				break;
			case R.id.radio_button1:
				tabHost.setCurrentTabByTag("互动平台");
				break;
			case R.id.radio_button2:
				tabHost.setCurrentTabByTag("课程中心");
				break;
			case R.id.radio_button3:
				tabHost.setCurrentTabByTag("精品推荐");
				break;
			case R.id.radio_button4:
				tabHost.setCurrentTabByTag("更多");
				break;
			default:				
				tabHost.setCurrentTabByTag("个人天地");
				break;
			}
		}
	};

	/**
	 * 返回键监听
	 */	 
		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

//				AlertDialog.Builder builder = new AlertDialog.Builder(
//						mContext);
//				
//				builder.setMessage("确定退出?")
//						.setTitle("提示")
//						.setNegativeButton("取消",
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface arg0,
//											int arg1) {
//										arg0.cancel();
////										deleteAll(new File((android.os.Environment.getExternalStorageDirectory()).getAbsolutePath()+"/weixue"));
////									Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
//									}
//								})
//						.setPositiveButton("确定",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int id) {
//						
//										MyFloatView.onExit();
//										SharedPreferences sp=mContext.getSharedPreferences(INFORMATION, Context.MODE_PRIVATE);
//										Editor ed=sp.edit();
//										ed.remove(PERSON_INFORMATION);
//										ed.commit();
//										if(isServiceStart()){
//										Intent intent=new Intent();
//										intent.setAction(Constants.SAVEDATA);
//										sendBroadcast(intent);
//										Intent i=new Intent(mContext,DownLoadService.class);
//										mContext.stopService(i);
//										}
//										System.exit(0);
//									}
//								});
//				AlertDialog alert = builder.create();
//				alert.show();
//				
				iphoneDialog.iphoneDialogBuilder idb = new iphoneDialog.iphoneDialogBuilder(this);
				idb.setTitle(getApplicationContext().getResources().getString(R.string.prompt));
				idb.setMessage(getApplicationContext().getResources().getString(R.string.isExit));
				idb.setPositiveButton(getApplicationContext().getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MyFloatView.onExit();
						SharedPreferences sp=mContext.getSharedPreferences(INFORMATION, Context.MODE_PRIVATE);
						Editor ed=sp.edit();
						ed.remove(PERSON_INFORMATION);
						ed.commit();
						if(isServiceStart()){
						Intent intent=new Intent();
						intent.setAction(Constants.SAVEDATA);
						sendBroadcast(intent);
						Intent i=new Intent(mContext,DownLoadService.class);
						mContext.stopService(i);
						}
						System.exit(0);
					}
				});
				idb.setNegativeButton(getApplicationContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				idb.show();
				
				
				return false;
			}
			return super.dispatchKeyEvent(event);
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
	  

}

package com.weixue.weixueUI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.weixue.Function.GetDisplay;
import com.weixue.Model.Course;

public class FloatVideoActivity extends Activity{
	int model =1;
	 private String filePathForJson="";
	 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
//		getDiaplay();
		
		filePathForJson=this.getIntent().getStringExtra("path");
		//Full_play();
			floatView();
	}

	//
	// //存储屏幕宽高
	// public void getDiaplay(){
	// GetDisplay.displayWidth =
	// getWindowManager().getDefaultDisplay().getWidth();
	// GetDisplay.displayHeight =
	// getWindowManager().getDefaultDisplay().getHeight();
	// }
	//
	/**
	 * 全屏播放
	 */
	public void Full_play(){
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Intent it = new Intent(Intent.ACTION_VIEW);
		String FilePath ="/mnt/sdcard/test.mp4";
		Uri uri = Uri.parse(filePathForJson);
		it.setDataAndType(uri, "video/*");
		this.startActivity(it);
		//Toast.makeText(getApplicationContext(), "悬浮视频即将启动！", 1).show();
		//Toast.makeText(getApplicationContext(), "悬浮视频即将启动！", 1).show();
	}
	
	/**
	 * 启动线程,让悬浮层线程延迟50秒启动
	 */
	public void floatView(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				 Looper.prepare();
				 try {
					Thread.sleep(1500);
					
					Intent floatMovie = new Intent();
					floatMovie.setClass(getApplicationContext(),MoviewView.class);
					floatMovie.putExtra("path", filePathForJson);
					startActivity(floatMovie);
					finish();
//					handler.sendMessage(handler.obtainMessage(0,null));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}

	/**
	 * UI主线程启动悬浮层
	 */
	public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0: 
				
				break;
			}
		}
	};
	
}

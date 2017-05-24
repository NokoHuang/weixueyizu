package com.weixue.weixueUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.weixue.Service.MediaPlaybackService;

public class StartActivity extends Activity   {
	 private String filePathForJson="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//启动悬浮层视频播放服务类
		Intent mIntent = new Intent("createUI");
		filePathForJson=this.getIntent().getExtras().getString("path");
		mIntent.setClass(getApplicationContext(), MediaPlaybackService.class);
		mIntent.putExtra("path", filePathForJson);
		
		startService(mIntent);
		finish();
	}
}

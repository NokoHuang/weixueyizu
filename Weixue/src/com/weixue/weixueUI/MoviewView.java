package com.weixue.weixueUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MoviewView extends Activity {
	 private String filePathForJson="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//启动startActivity类,此处用这种启动方式是为了给该启动类添加singleTop属性
		filePathForJson=this.getIntent().getExtras().getString("path");
		Intent intent = new Intent("com.weixue.ACTION_START_ACTIVITY");
		intent.putExtra("path", filePathForJson);
		startActivity(intent);
		finish();
	}
	
}
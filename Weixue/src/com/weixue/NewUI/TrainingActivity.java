package com.weixue.NewUI;

import com.weixue.weixueUI.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TrainingActivity extends Activity {
	private LinearLayout line_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_training);
		
		Init();
		
		Toast.makeText(getApplicationContext(), "暂无数据！", Toast.LENGTH_LONG).show();
			
		line_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				onBackPressed();
			}
		});
	}
	
	public void Init(){
		line_back=(LinearLayout)findViewById(R.id.line_back);
	}
}

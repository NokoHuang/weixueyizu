package com.weixue.myUI;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.weixue.weixueUI.R;

public class StudyFieldItem3 extends Activity{
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_field_item3);
		Init();
	}
	
	public void Init(){
		
	}
	
	public void GoBack(View v){
		onBackPressed();
	}
}

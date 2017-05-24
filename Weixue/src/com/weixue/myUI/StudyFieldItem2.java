package com.weixue.myUI;

import com.weixue.weixueUI.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class StudyFieldItem2 extends Activity{
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_field_item2);
		Init();
	}
	
	public void Init(){
		
	}
	
	public void GoBack(View v){
		onBackPressed();
	}
}

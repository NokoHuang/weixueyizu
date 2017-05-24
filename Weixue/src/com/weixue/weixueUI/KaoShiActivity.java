package com.weixue.weixueUI;


import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.weixue.Adapter.KaoShiAdapter;

public class KaoShiActivity extends FragmentActivity implements OnClickListener {
	private KaoShiAdapter mAdapter;
	private ViewPager mPager;
	private PopupWindow pop;
	private Button mBtn0KaoShi,mBtn1KaoShi,mBtn2KaoShi;
	private Button mBtnOK,mBtnCancel;
	private EditText mEditPop;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaoshi);
		
		Init();
		
		int uId=getIntent().getExtras().getInt("UserId");
		
		mAdapter = new KaoShiAdapter(getSupportFragmentManager(),uId);  
        mPager = (ViewPager)findViewById(R.id.pager_kaoshi);  
        mPager.setAdapter(mAdapter);
	}
	
	private void Init(){
		mBtn0KaoShi=(Button) findViewById(R.id.kaoshi_button0);
		mBtn0KaoShi.setOnClickListener(this);
		mBtn1KaoShi=(Button) findViewById(R.id.kaoshi_button1);
		mBtn1KaoShi.setOnClickListener(this);
		mBtn2KaoShi=(Button) findViewById(R.id.kaoshi_button2);
		mBtn2KaoShi.setOnClickListener(this);
		
		LayoutInflater inflater=LayoutInflater.from(this);
		View view=inflater.inflate(R.layout.kaoshi_pop, null);
		mBtnOK=(Button) view.findViewById(R.id.btn_ok);
		mBtnOK.setOnClickListener(this);
		mBtnCancel=(Button) view.findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(this);
		mEditPop=(EditText) view.findViewById(R.id.edit_pop);
		pop=new PopupWindow(view,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT,false);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.kaoshi_button0:
			Intent intent=new Intent(getApplicationContext(), KaoShiResult.class);
			startActivity(intent);
			finish();
			break;
		case R.id.kaoshi_button1:
			if(pop.isShowing()) { 
				// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏 
				pop.dismiss(); 
			} else { 
				// 显示窗口 
				pop.showAsDropDown(mBtn1KaoShi,10,10);
			}  
			break;
		case R.id.kaoshi_button2:
			break;
			
		case R.id.btn_ok:
			System.out.println(mEditPop.getText().toString());
			int str= Integer.parseInt(mEditPop.getText().toString());
			
			mPager.setCurrentItem(str);
			
			break;
			
		case R.id.btn_cancel:
			pop.dismiss(); 
			break;
		}
	}
	
}

package com.weixue.weixueUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class KaoShiResult extends Activity implements OnClickListener {
	private Button mCheckAns;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kaoshi_result);
		
		mCheckAns=(Button) findViewById(R.id.btn_checkans);
		mCheckAns.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_checkans:
			Intent intent=new Intent(this, KaoShiAns.class);
			startActivity(intent);
			break;
		}
	}
}

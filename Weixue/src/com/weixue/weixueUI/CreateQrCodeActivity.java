package com.weixue.weixueUI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.WriterException;
import com.weixue.Function.EncodingHandler;
import com.weixue.Function.MyInstance;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.PersonInformation;
import com.weixue.Utils.Constants;

public class CreateQrCodeActivity extends Activity{
	//返回
	private LinearLayout line_back;
	//生成按钮
	private Button btn_create;
	//二维码图片
	private ImageView lv_qrImg;
	//用户信息
	private PersonInformation person;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.create_myqrcode_page);
		initUi();
	}

	//初始化控件
	private void initUi() {
		// TODO Auto-generated method stub
		line_back=(LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);
		
		btn_create=(Button) findViewById(R.id.btn_create);
		btn_create.setOnClickListener(listener);
		
		lv_qrImg=(ImageView) findViewById(R.id.lv_qrImg);
		
		person=ResolveJSON.JSON_To_PersonInformation(MyInstance.getSharedPreferencesString(this, Constants.PERSON_INFORMATION));
		
	}

	//点击监听
			OnClickListener listener=new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch(v.getId()){				
					case R.id.line_back:
						finish();
						break;
					case R.id.btn_create:
						Bitmap qrCodeBitmap = null;
						try {
							qrCodeBitmap = EncodingHandler.createQRCode(String.valueOf(person.getUserId()), 350);
						} catch (WriterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(qrCodeBitmap!=null){
						 lv_qrImg.setImageBitmap(qrCodeBitmap);
						}
						break;
					}
				}};
	
}

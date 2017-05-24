package com.weixue.weixueUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.weixue.Methods.ResolveJSON;
import com.weixue.Methods.UploadMessage;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants_Url;

/**
 * 
 * @author zhongzien
 *
 */
public class RegisterActivity extends Activity{
	/**/
	//上下文
	Context context;
	
	//请求url
	private static final String REGISTERURL=Constants_Url.REGISTER;
	
	private static final int SHOW_MESSAGE=0;
	
	//返回按钮
	private LinearLayout line_back;
	
	//用户名、密码和邮件输入框
	private EditText et_name,et_pwd,et_mail;
	
	//注册按钮
	private Button btn_register;
	
	//用户名、密码和邮件
	private String str_name;
	private String str_pwd;
	private String str_mail;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.registration_page);
		
		init();
	}
	
	
	public void init(){
		
		context=this;
		
		line_back=(LinearLayout) this.findViewById(R.id.line_back);
		line_back.setOnClickListener(clickListener);
		
		et_name=(EditText) findViewById(R.id.editText_name);
		et_pwd=(EditText) findViewById(R.id.editText_pwd);
		et_mail=(EditText) findViewById(R.id.editText_mailbox);
		
		btn_register=(Button)this.findViewById(R.id.btn_serccer_1);
		btn_register.setOnClickListener(clickListener);
		
		
	}
	
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) {
				case R.id.line_back:{
					jumpInLogin();
					break;
				}
				case R.id.btn_serccer_1:{	
					str_name=et_name.getText().toString();
					str_pwd=et_pwd.getText().toString();
					str_mail=et_mail.getText().toString();
					
					register(str_name, str_pwd, str_mail);
					
					break;
				}
			
			}
			
		}
	};
	
	
	public void jumpInLogin(){
		
		Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
		startActivity(intent);
		finish();
		
	}
	/**
	 * 
	 * @param str_name
	 * @param str_pwd
	 * @param str_mail
	 */
	
	public void register(final String str_name,final String str_pwd,final String str_mail){
		
		Runnable runnable=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				List<String> li_name=new ArrayList<String>();
//				li_name.add("username");
//				li_name.add("password");
//				li_name.add("email");
//				
//				List<String> li_content=new ArrayList<String>();
//				li_content.add(str_name);
//				li_content.add(str_pwd);
//				li_content.add(str_mail);
				Map<String,String> map=new HashMap<String,String>();
				map.put("username", str_name);
				map.put("password", str_pwd);
				map.put("email", str_mail);		
				String str_response="";
				
				try{
					str_response=UploadMessage.post(REGISTERURL, map);
					
				}catch(Exception e1){
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, context.getResources().getString(R.string.networkerror)));
					
					return;
				}
				
			
				try{
					Response response=ResolveJSON.JSON_To_Response(str_response);
					switch(ResolveJSON.IsHasResult(str_response)){
					case 1:		
						Looper.prepare();
						Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
						jumpInLogin();
						Looper.loop();
					break;
					case 0:							
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response.getMessage()));
							break;
							default:
								handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.error)));
								break;
						}
					
					
					
				}catch(Exception e){
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, context.getResources().getString(R.string.registerfail)));
					e.printStackTrace();
				}
				
				
			}
		};
		
		new Thread(runnable).start();
		
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SHOW_MESSAGE:
				String str_toast=(String)msg.obj;
				Toast.makeText(context, str_toast, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}
		
	};/**/

}

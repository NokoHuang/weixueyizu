package com.weixue.weixueUI;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.weixue.Methods.ResolveJSON;
import com.weixue.Methods.UploadMessage;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants_Url;

public class UserCommentActivity extends Activity{
	private static final int SHOW_MESSAGE = 0;
	
	private LinearLayout line_back;
	private Button btn_submit;
	private EditText et_content;
	
	private int uid;
	private int type;
	private int targetId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.usercomment_page);
		init();
	}
	
	//初始化
	private void init() {
		// TODO Auto-generated method stub
		line_back=(LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);
		
		btn_submit=(Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(listener);
		 
		et_content=(EditText) findViewById(R.id.et_content);
		
		Bundle b=this.getIntent().getExtras();
		uid=b.getInt("uid");
		targetId=b.getInt("targetId");
		type=b.getInt("type");
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
				case R.id.btn_submit:
					uploadMessage(uid,type,targetId,String.valueOf(et_content.getText()));
					
					break;
				}
			}};
			
			/**
			 * 发表评论
			 * @param uid
			 * @param type
			 * @param targetId
			 * @param content
			 */
			private void uploadMessage(final int uid,final int type,final int targetId,final String content){
				Runnable run=new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String requestUrl=Constants_Url.USER_COMMENT;
						Map<String,String> map=new HashMap<String,String>();
						map.put("uid", String.valueOf(uid));
						map.put("type", String.valueOf(type));
						map.put("targetID", String.valueOf(targetId));	
						map.put("content", content);	
						// 上传信息返回请求结果
						String str_Response="";
						try {
							str_Response = UploadMessage.post(requestUrl,map);
						} catch (Exception e1) {
							// TODO Auto-generated catch block								
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.networkerror)));
							return;
						}
						try {
							
							switch(ResolveJSON.IsHasResult(str_Response)){
							case 1:
							//PersonInformation person = ResolveJSON.JSON_To_PersonInformation(str_Response);
								Response response=ResolveJSON.JSON_To_Response(str_Response);
								handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response.getResult()));
								finish();
							break;
							case 0:	
								Response response0=ResolveJSON.JSON_To_Response(str_Response);
								handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response0.getMessage()));
								break;
								default:
									handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.error)));
									
									break;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block					
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,getApplicationContext().getResources().getString(R.string.loginfail)));
							e.printStackTrace();
						}
					}};
					new Thread(run).start();
			}
	
			Handler handler=new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch(msg.what){
					case SHOW_MESSAGE:
						String content=(String) msg.obj;
						Toast.makeText(getApplicationContext(), content,
								Toast.LENGTH_SHORT).show();
						break;
					}
					super.handleMessage(msg);
				}
				
			};
			
}

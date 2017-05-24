package com.weixue.MyDialog;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Methods.ResolveJSON;
import com.weixue.Methods.UploadMessage;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;
/**
 * 
 * @author chenjunjie
 *
 */
public class PublishDetailActivity extends Activity{
	//返回给前一个Activity的requestcode
		private static final int REQUESTCODE=2;
	//发表动态
	private static final int PUBLISH_STATUS=0;
	//显示信息
	private static final int SHOW_MESSAGE=1;
	//上下文
private Context mContext;
//返回按钮和发表按钮
private ImageButton imgbtn_back,imgbtn_up;
//发表的内容
private EditText et_content;
private int type=1;
//用户信息
private SharedPreferences sp;
private PersonInformation person;

private TextView tv_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_publishdetail_page);		
		initUi();
	}
	//初始化控件
	public void initUi(){
		mContext=this;
		imgbtn_back=(ImageButton) findViewById(R.id.btn_back);
		imgbtn_back.setOnClickListener(setListener());
		
		imgbtn_up=(ImageButton) findViewById(R.id.btn_up);
		imgbtn_up.setOnClickListener(setListener());
		et_content=(EditText) findViewById(R.id.et_content);
		
		type=this.getIntent().getExtras().getInt("type");
		
		tv_title=(TextView) findViewById(R.id.tv_title);
		if(type==1){
			tv_title.setText(getApplicationContext().getResources().getString(R.string.publish_detailforschool));
		}else if(type==3){
			tv_title.setText(getApplicationContext().getResources().getString(R.string.publish_detailforperson));
		}
		
		sp=this.getSharedPreferences("information", Context.MODE_PRIVATE);
		person=ResolveJSON.JSON_To_PersonInformation(sp.getString("personInformation", ""));
	}

	/**
	 * 设置按钮监听者
	 * @return OnClickListener
	 */
	public OnClickListener setListener(){
		OnClickListener clickListener=new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(v.getId()){
				case R.id.btn_back:{
					
					finish();
					break;
				}
				case R.id.btn_up:{
									
					upLoadContent(person,String.valueOf(et_content.getText()),type);
					break;
				}
				}
			}};
			return clickListener;
	}
	
	/**
	 * 保存发表动态.(1:个人发表的动态，2：系统自动发布的动态).post 表单格式：uid=789218&content=我今天学习了很多知识！ ！ &type=1&picdata=LKBHH#@@#$         
	 * @param uid
	 * @param content
	 * @param type
	 */
	public void upLoadContent(final PersonInformation personInformation,final String content,final int type){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.PUBLISH_STATUS;
//				List<String> li_name=new ArrayList<String>();
//				li_name.add("uid");
//				li_name.add("content");
//				li_name.add("type");
//				li_name.add("picdata");
//				
//				List<String> li_content=new ArrayList<String>();
//				li_content.add(String.valueOf(uid));
//				li_content.add(content);
//				li_content.add(String.valueOf(type));
//				li_content.add("");
				Map<String,String> map=new HashMap<String,String>();
				
				map.put("ContentText", content);
				map.put("StatusType", String.valueOf(type));			
				map.put("PicPath","");
				map.put("BigPicPath","");
				map.put("UserID", String.valueOf(personInformation.getUserId()));
				map.put("UserName",personInformation.getUsername());
				map.put("UserPicPath",personInformation.getPhotoPath());
				
				try {
					String str_response=UploadMessage.post(requestUrl,map);
System.out.println(str_response);
					handler.sendMessage(handler.obtainMessage(PUBLISH_STATUS, str_response));
				} catch (Exception e) {
					// TODO Auto-generated catch block
				handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.networkerror)));
				}
			}};
		new Thread(run).start();
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case PUBLISH_STATUS:{
				String jsonStr=(String) msg.obj;
				
				try {
					
					switch(ResolveJSON.IsHasResult(jsonStr)){
					case 1:
						Response response=ResolveJSON.JSON_To_Response(jsonStr);
					Toast.makeText(mContext, response.getResult(), Toast.LENGTH_SHORT).show();
				
					    setResult(REQUESTCODE);	
					    Intent i=new Intent();
					    i.setAction(Constants.STATUS_REFESH);
					    sendBroadcast(i);
					finish();
					break;
					case 0:
						Response responseStr=ResolveJSON.JSON_To_Response(jsonStr);
						Toast.makeText(mContext, responseStr.getMessage(), Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			}
			case SHOW_MESSAGE:{
				String content=(String) msg.obj;
				Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
				break;
			}
			}
			super.handleMessage(msg);
		}
		
	};
	
}

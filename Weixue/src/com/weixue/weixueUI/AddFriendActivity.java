package com.weixue.weixueUI;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.DownLoadFile;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Methods.UploadMessage;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

public class AddFriendActivity extends Activity{
	
	private Context mContext;

	private static final int SETDATA = 0;
	private  static final int SETHEAD = 1;
	private static final int SHOW_MESSAGE=2;
	
	private ImageView img_head;
	private TextView tv_name,tv_level,tv_signature,tv_learncents,tv_learnmoney;
	private Button btn_addFriend;
	private LinearLayout line_back;
	
	//用户信息
	private PersonInformation person;
	//获取传过来的ID
  private int fuid=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.personinfo_page);
		
		initUi();
	}
	//初始化
	public void initUi(){
		mContext=this;
		
		img_head=(ImageView) findViewById(R.id.img_head);
		
		tv_name=(TextView) findViewById(R.id.tv_name);
		
		tv_level=(TextView) findViewById(R.id.tv_level);
		
		tv_signature=(TextView) findViewById(R.id.tv_signature);
		
		tv_learncents=(TextView) findViewById(R.id.tv_learncents);
		
		tv_learnmoney=(TextView) findViewById(R.id.tv_learnmoney);
		
		btn_addFriend=(Button) findViewById(R.id.btn_addFriend);	
		btn_addFriend.setOnClickListener(listener);	
		
		line_back=(LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);
		
		person=ResolveJSON.JSON_To_PersonInformation(MyInstance.getSharedPreferencesString(mContext,Constants.PERSON_INFORMATION));
		
		fuid=this.getIntent().getExtras().getInt("fuid");
		getData(fuid);
	}
	
	//点击监听
	OnClickListener listener=new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_addFriend:{
				addFriend(person.getUserId(),fuid);
				break;
			}
			case R.id.line_back:{
				finish();
				break;
			}
			}
		}};
		/**
		 * 添加好友
		 * @param uid
		 * @param fuid
		 */
		public void addFriend(final int uid,final int fuid){
			Runnable run=new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String url=Constants_Url.ADD_FRIEND;
					Map<String,String> map=new HashMap<String,String>();
					map.put("uid",String.valueOf( uid));
					map.put("fuid", String.valueOf( fuid));
					try {
						String str_response=UploadMessage.post(url, map);
						//System.out.println("str_response-->"+str_response);
						switch(ResolveJSON.IsHasResult(str_response)){
						case 1:{
							Response response=ResolveJSON.JSON_To_Response(str_response);
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response.getMessage()));
							break;
						}
						case 0:{
							Response response=ResolveJSON.JSON_To_Response(str_response);
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response.getMessage()));
							break;
						}
						default:
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.error)));
							break;
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.networkerror)));
					}
				}};
				new Thread(run).start();
		}
		
	/**
	 * 获取用户信息
	 */
	public void getData(final int uid){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url=Constants_Url.PERSON_INFO+"?uid="+uid;
				
				try {
					String responseStr = NetWork.getData(url);
					handler.sendMessage(handler.obtainMessage(SETDATA, responseStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}};
			new Thread(run).start();
	}
	
	/**
	 * 使用子线程下载头像
	 * @param strUrl 头像的下载地址
	 * @param saveUrl 保存的路径
	 */
	public void downLoadHeadPhoto(final String strUrl,final String saveUrl){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				int mark=DownLoadFile.downLoadPic(strUrl, saveUrl);
				
				switch(mark){
				case 1:{
					handler.sendMessage(handler.obtainMessage(SETHEAD, saveUrl));

					break;
				}
				case 2:{
					handler.sendMessage(handler.obtainMessage(SETHEAD, saveUrl));
					break;
				}
				}				
			}};
			new Thread(run).start();
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case SETDATA:
				String content=(String) msg.obj;
				try {
					switch(ResolveJSON.IsHasResult(content)){
					case 1:{
						PersonInformation person=ResolveJSON.JSON_To_PersonInformation(content);
						tv_name.setText(person.getUsername());
						tv_level.setText(String.valueOf(person.getLevel()));
						tv_signature.setText(person.getSignature());
						tv_learncents.setText(String.valueOf(person.getLearnCents()));
						tv_learnmoney.setText(String.valueOf(person.getLearnMoney()));
					
						File fileMkdir=new File(Constants_Url.PIC_USERHEAD_PATH);
						if(!fileMkdir.exists()){
							fileMkdir.mkdirs();
						}		
						String fileName=person.getLargePhotoPath().substring(person.getLargePhotoPath().lastIndexOf("/")+1);
						
						downLoadHeadPhoto(person.getLargePhotoPath(),fileMkdir.getAbsolutePath()+"/"+fileName);
						break;
					}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case SETHEAD:
				String strUrl=(String) msg.obj;
				img_head.setImageBitmap(BitmapFactory.decodeFile(strUrl));
				break;
			case SHOW_MESSAGE:
				String showStr=(String) msg.obj;
				Toast.makeText(getApplicationContext(), showStr, Toast.LENGTH_SHORT).show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
}

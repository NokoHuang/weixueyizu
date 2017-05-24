package com.weixue.weixueUI;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weixue.Function.MyInstance;
import com.weixue.Methods.DownLoadFile;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.DownloadFile;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Status;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
/**
 * 
 * @author chenjunjie
 *
 */
public class StatusDetailActivity extends Activity{
	
	private static final int SETPIC = 0;
	
	private Context mContext;
	
	private TextView tv_title,tv_content,tv_date,tv_pinglunNum;
	private ImageView iv;	 
	// 返回按钮
	private LinearLayout line_back;
	 //动态:信息
	private Status status;
	
	//查看评论，发表评论，关注好友
	private TextView tv_readcomment,tv_uploadcomment,tv_addfriend;
	//用户信息
	private PersonInformation person;
	
	
	private int type=2;//说明是评论动态
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.statusdetail_page);
		initUi();
		loadData();
	}
	
	//初始化控件
	private void initUi() {
		mContext=this;
		
		// TODO Auto-generated method stub
		 iv = (ImageView) findViewById(R.id.userMsgImg);
	     tv_title = (TextView) findViewById(R.id.title);
	     tv_content = (TextView)findViewById(R.id.content);
	     tv_date = (TextView) findViewById(R.id.time);
	     tv_pinglunNum = (TextView) findViewById(R.id.pinglunNum);
	     
	     tv_readcomment = (TextView)findViewById(R.id.tv_readcomment);
	     tv_uploadcomment = (TextView) findViewById(R.id.tv_uploadcomment);
	     tv_addfriend = (TextView) findViewById(R.id.tv_addfriend);
	     tv_readcomment.setOnClickListener(clickListener);
	     tv_uploadcomment.setOnClickListener(clickListener);
	     tv_addfriend.setOnClickListener(clickListener);
	     
	     line_back = (LinearLayout) findViewById(R.id.line_back);
		 line_back.setOnClickListener(clickListener);
		 
		 
	}
	
	//获取数据
	private void loadData(){
		 person=ResolveJSON.JSON_To_PersonInformation(MyInstance.getSharedPreferencesString(mContext,Constants.PERSON_INFORMATION));
	     
	     status=(Status) this.getIntent().getExtras().getSerializable("status");	    	   
	     
	     tv_title.setText(status.getUserName());
	     tv_content.setText(status.getContentText());
	     tv_date.setText(status.getCreateTime());
	     tv_pinglunNum.setText(String.valueOf(status.getCommentCount()));
	     
	     //如果是本用户的评论详情,去掉关注好友项!
	     if(status.getUserName().equals(person.getUsername())){
	    	 tv_addfriend.setVisibility(View.GONE);
	     }
	   
	     String fileName=status.getPicPath().substring(status.getPicPath().lastIndexOf("/")+1);
	    
	     if(fileName!=null&&fileName.length()>0){
	     File file=new File(Constants_Url.PIC_CACHE_PATH+"/"+fileName);
	     if(file.exists()){
	    	 iv.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
	     }else{
	    	 downloadFile(status.getPicPath(),file.getAbsolutePath());
	      }
	     }
	}
	
	//下载图片
	private void downloadFile(final String strUrl,final String saveUrl){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int mark=DownLoadFile.downLoadPic(strUrl, saveUrl);
				if(mark>0){
					handler.sendMessage(handler.obtainMessage(SETPIC, saveUrl));
				}else{
					handler.sendMessage(handler.obtainMessage(SETPIC, ""));
				}
				
			}};
		new Thread(run).start();
	}
	
			
	// 点击监听者
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=null;
				switch (v.getId()) {
				
				case R.id.line_back: //返回
					finish();
					break;
				case R.id.tv_readcomment:
					i=new Intent(StatusDetailActivity.this,ReadCommentActivity.class);		
					i.putExtra("sid", status.getStatusID());
					startActivity(i); 
					break;
				case R.id.tv_uploadcomment:
					i=new Intent(StatusDetailActivity.this,UserCommentActivity.class);	
					i.putExtra("uid", person.getUserId());
					i.putExtra("targetId", status.getStatusID());
					i.putExtra("type", type);
					startActivity(i);
					break;
				case R.id.tv_addfriend:
					i=new Intent(StatusDetailActivity.this,AddFriendActivity.class);
					i.putExtra("fuid", status.getUserID());
					startActivity(i);
					break;
				
				}
			}
		};	
		
		Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case SETPIC:
					String path=(String) msg.obj;
					if(path!=""){
					 iv.setImageDrawable(Drawable.createFromPath(path));
					}else{
						iv.setImageResource(R.drawable.liimg);
					}
					break;
				}
				super.handleMessage(msg);
			}
			
		};

}

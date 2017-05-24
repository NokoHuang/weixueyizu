package com.weixue.MyDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.PersonInformation;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class CourseScoreActivity extends Activity{
private static final int UPLOADCOURSESCORE = 0;
private Button btn_sure,btn_cancel;
private RatingBar rate;
private int cid;
private PersonInformation person;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_coursescore_page);
		initUi();
	}
	//初始化
	private void initUi() {
		// TODO Auto-generated method stub
		btn_sure=(Button) findViewById(R.id.btn_sure);
		btn_sure.setOnClickListener(listener);
		btn_cancel=(Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(listener);
		
		rate = (RatingBar) findViewById(R.id.app_ratingbar);
		
		cid=this.getIntent().getExtras().getInt("cid");
		person=ResolveJSON.JSON_To_PersonInformation(MyInstance.getSharedPreferencesString(this, Constants.PERSON_INFORMATION));
	}
	
	OnClickListener listener=new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_sure:
				//System.out.println("rate.getRating()-->"+rate.getRating());
				uploadCourseScore(cid,((int)(rate.getRating()*2)),person.getUserId());
				break;
			case R.id.btn_cancel:
				finish();
				break;
			}
		}};
		
		//上传评分
		 private void uploadCourseScore(final int cid,final int score,final int uid){
			 Runnable run=new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String requestUrl=Constants_Url.COURSESCORE+"?cid="+cid+"&score="+score+"&uid="+uid;
					
					try {
						String response=NetWork.getData(requestUrl);
					
						handler.sendMessage(handler.obtainMessage(UPLOADCOURSESCORE, response));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Looper.prepare();
						Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
						Looper.loop();
					}
				}};
			 new Thread(run).start();
		 }

		 Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case UPLOADCOURSESCORE:
					String response=(String) msg.obj;
					try {
						switch(ResolveJSON.IsHasResult(response)){
						case 1:
							Toast.makeText(getApplicationContext(), ResolveJSON.JSON_To_Response(response).getResult(), Toast.LENGTH_SHORT).show();
							finish();
							break;
						case 0:
							Toast.makeText(getApplicationContext(), ResolveJSON.JSON_To_Response(response).getMessage(), Toast.LENGTH_SHORT).show();
							break;
							default:
							Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				super.handleMessage(msg);
			}
			 
		 };
}

package com.weixue.weixueUI;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Function.MyInstance;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.PersonInformation;
import com.weixue.NewUI.StudyField;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 更多
 * 
 * @author chenjunjie
 * 
 */
public class MoreActivity extends Activity {
	// 个人信息按钮,查找好友按钮,生成二维码按钮,扫面二维码按钮
	private LinearLayout line_person_information, line_searchFriend,
			line_createQrCode, line_QrCodeSearchFriend, line_StudyField;
	// 上下文
	private Context mContext;
	// 小头像
	private ImageView img_head;
	// 下载管理按钮、收藏课程按钮
	private LinearLayout line_downLoadManager, line_collectCourse;
	// 系统设置按钮
	private LinearLayout line_setting;
	// 注销登录
	private Button btn_cancellation;

	public static TextView my_profession_text;
	// 用户信息
	public PersonInformation person;

	private MyReceiver myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.more_page);

		// 初始化UI
		initUi();

		// 获取我的职位信息
		SharedPreferences profession = getSharedPreferences("profession_info",
				Context.MODE_PRIVATE);
		String my_profession = profession.getString("my_profession", "未选择职业");
		my_profession_text.setText(my_profession);
	}

	// 初始化控件
	private void initUi() {
		mContext = this;

		my_profession_text = (TextView) findViewById(R.id.my_profession_text);

		line_StudyField = (LinearLayout) findViewById(R.id.line_study_field);
		line_StudyField.setOnClickListener(listener);

		line_person_information = (LinearLayout) findViewById(R.id.line_person_information);
		line_person_information.setOnClickListener(listener);

		line_searchFriend = (LinearLayout) findViewById(R.id.line_searchFriend);
		line_searchFriend.setOnClickListener(listener);

		line_createQrCode = (LinearLayout) findViewById(R.id.line_createQrCode);
		line_createQrCode.setOnClickListener(listener);

		line_QrCodeSearchFriend = (LinearLayout) findViewById(R.id.line_QrCodeSearchFriend);
		line_QrCodeSearchFriend.setOnClickListener(listener);

		line_collectCourse = (LinearLayout) findViewById(R.id.line_collectCourse);
		line_collectCourse.setOnClickListener(listener);

		line_setting = (LinearLayout) findViewById(R.id.line_setting);
		line_setting.setOnClickListener(listener);

		person = ResolveJSON.JSON_To_PersonInformation(MyInstance
				.getSharedPreferencesString(mContext,
						Constants.PERSON_INFORMATION));

		img_head = (ImageView) findViewById(R.id.img_head);
		//获取头像地址
		String fileName = person.getLargePhotoPath().substring(
				person.getLargePhotoPath().lastIndexOf("/") + 1);
		
		Log.e("头像路径", person.getLargePhotoPath());
		
		File file = new File(Constants_Url.PIC_USERHEAD_PATH + "/" + fileName);
		if (file.exists()) {
			img_head.setImageBitmap(BitmapFactory.decodeFile(file
					.getAbsolutePath()));
			
			MyInstance.SaveLoginUserPhoto(MoreActivity.this, file
					.getAbsolutePath());
		}

		line_downLoadManager = (LinearLayout) findViewById(R.id.line_downloadmanager);
		line_downLoadManager.setOnClickListener(listener);

		btn_cancellation = (Button) findViewById(R.id.btn_cancellation);
		btn_cancellation.setOnClickListener(listener);

		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.UPDATE_USERHEAD);
		registerReceiver(myReceiver, filter);
	}

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			if (action.equals(Constants.UPDATE_USERHEAD)) {
				person = ResolveJSON.JSON_To_PersonInformation(MyInstance
						.getSharedPreferencesString(mContext,
								Constants.PERSON_INFORMATION));
				
				String fileName = person.getLargePhotoPath().substring(
						person.getLargePhotoPath().lastIndexOf("/") + 1);
				
				File file = new File(Constants_Url.PIC_USERHEAD_PATH + "/"
						+ fileName);
				if (file.exists()) {
					img_head.setImageBitmap(BitmapFactory.decodeFile(file
							.getAbsolutePath()));
					
					MyInstance.SaveLoginUserPhoto(MoreActivity.this, file
							.getAbsolutePath());
				}
			}
		}
	}

	// 点击事件
	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.line_person_information: {
				System.out.println("点击个人信息");
				Intent i = new Intent(mContext, UserInformationActivity.class);
				startActivity(i);

				break;
			}
			case R.id.line_study_field: {
				Intent i = new Intent(MoreActivity.this, StudyField.class);
				startActivity(i);
				break;
			}
			case R.id.line_downloadmanager: {
				Intent i = new Intent(MoreActivity.this,
						DownLoadManagerActivity.class);
				startActivity(i);
				break;
			}
			case R.id.line_collectCourse: {
				Intent i = new Intent(MoreActivity.this,
						CollectCourseActivity.class);
				i.putExtra("uid", person.getUserId());
				startActivity(i);

				break;
			}
			case R.id.line_setting: {
				Intent i = new Intent(MoreActivity.this, SettingActivity.class);
				startActivity(i);

				break;
			}
			case R.id.btn_cancellation: {
				//清楚自动登录
				SharedPreferences auto_info=getSharedPreferences("auto_login_info", MODE_PRIVATE);
				Editor editor=auto_info.edit();
				editor.clear();
				editor.commit();
				
				Intent i = new Intent(mContext, LoginActivity.class);
				startActivity(i);
				finish();
				Toast.makeText(
						getApplicationContext(),
						getApplicationContext().getResources().getString(
								R.string.cancellationsuccess),
						Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.line_searchFriend: {
				Intent i = new Intent(MoreActivity.this,
						SearchFriendActivity.class);
				startActivity(i);
				break;
			}
			case R.id.line_createQrCode: {
				Intent i = new Intent(MoreActivity.this,
						CreateQrCodeActivity.class);
				startActivity(i);
				break;
			}
			case R.id.line_QrCodeSearchFriend: {
				Intent i = new Intent(MoreActivity.this,
						QrCodeSearchFriendActivity.class);
				startActivity(i);
				break;
			}

			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (myReceiver != null) {
			unregisterReceiver(myReceiver);
		}
		super.onDestroy();
	}

}

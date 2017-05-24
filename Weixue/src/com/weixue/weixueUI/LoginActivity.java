package com.weixue.weixueUI;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.weixue.Function.MyInstance;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Methods.UploadMessage;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 
 * @author chenjunjie
 * 
 */
public class LoginActivity extends Activity {
	// 请求的URL
	private static final String REQUESTURL = Constants_Url.LOGIN_PATH;
	// 使用吐司显示信息
	private static final int SHOW_MESSAGE = 0;

	// 上下文
	private Context mContext;
	
	private ImageView user_photo;

	// 用户名和密码输入框
	private EditText et_name, et_pwd;
	// 登录、注册按钮
	private LinearLayout line_login, line_register;
	// 用户名，密码
	private String str_name;
	private String str_pwd;
	
	private CheckBox auto_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.login_page);
		
		Auto_Login();
		
		init();
		
		LoadUserPhoto();
	}
	
	/**
	 * 加载用户头像
	 */
	public void LoadUserPhoto(){
		SharedPreferences photo=getSharedPreferences("user_photo", MODE_PRIVATE);
		String photo_url=photo.getString("photo", "");
		
		if(photo_url!="")
			user_photo.setImageBitmap(BitmapFactory.decodeFile(photo_url));
		else
			user_photo.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_user));
	}
	
	/**
	 * 自动登录
	 */
	public void Auto_Login(){
		SharedPreferences auto_login=getSharedPreferences("auto_login_info", MODE_PRIVATE);
		str_name=auto_login.getString("auto_name", null);
		str_pwd=auto_login.getString("auto_pass", null);
		
		if(str_name!=null && str_pwd!=null){
			//连接服务器，登陆
			login(str_name, str_pwd);
		}
	}

	// 初始化控件
	public void init() {
		//获取用户头像实例
		user_photo=(ImageView)findViewById(R.id.imageView1);
		//自动登录
		auto_login=(CheckBox)findViewById(R.id.auto_login);
		auto_login.setOnCheckedChangeListener(checkedChanageListener);
		
		mContext = this;

		et_name = (EditText) findViewById(R.id.et_name);

		et_pwd = (EditText) findViewById(R.id.et_pwd);

		String saveData = MyInstance.getSharedPreferencesString(mContext,
				Constants.SAVE_LOGININFO);
		if (saveData != "") {
			str_name = saveData.substring(0, saveData.lastIndexOf(","));
			str_pwd = saveData.substring(saveData.lastIndexOf(",") + 1);
			et_name.setText(str_name);
			et_pwd.setText(str_pwd);
		}

		line_login = (LinearLayout) findViewById(R.id.line_login);
		line_login.setOnClickListener(clickListener);

		line_register = (LinearLayout) findViewById(R.id.line_register);
		line_register.setOnClickListener(clickListener);

		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.translate);
		line_login.startAnimation(anim);

		line_register.startAnimation(anim);
	}
	
	/**
	 * 自动登录监听
	 */
	OnCheckedChangeListener checkedChanageListener=new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(auto_login.isChecked()){
				SharedPreferences auto_preference=getSharedPreferences("auto_login_info", MODE_PRIVATE);
				Editor editor=auto_preference.edit();
				editor.putString("auto_name", str_name);
				editor.putString("auto_pass", str_pwd);
				editor.commit();
			}
		}
	};

	// 点击监听者
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.line_register: {// 注册
				Intent i = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(i);
				finish();
				break;
			}
			case R.id.line_login: {// 登录
				str_name = et_name.getText().toString();
				str_pwd = et_pwd.getText().toString();

				login(str_name, str_pwd);

				break;
			}

			}
		}
	};

	/**
	 * 跳到主界面
	 */
	public void jumpToMainPage() {
		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
		finish();
	}

	private AlertDialog dialog;

	/**
	 * 联网登录
	 * 
	 * @param str_name
	 * @param str_pwd
	 */
	public void login(final String str_name, final String str_pwd) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// 需要发送的内容
				// List<String> li_name = new ArrayList<String>();
				// li_name.add("username");
				// li_name.add("password");
				// li_name.add("signkey");
				// //String[] str_names={"username","password","signkey"};
				// List<String> li_content = new ArrayList<String>();
				// li_content.add(str_name);
				// li_content.add(str_pwd);
				// li_content.add("2");
				// //String[] str_contents={str_name,str_pwd,"2"};
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", str_name);
				map.put("password", str_pwd);
				map.put("signkey", "2");
				// 上传信息返回请求结果
				String str_Response = "";
				try {
					str_Response = UploadMessage.post(REQUESTURL, map);
					System.out.println("str_Response-->" + str_Response);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					handler.sendMessage(handler.obtainMessage(
							SHOW_MESSAGE,
							mContext.getResources().getString(
									R.string.networkerror)));
					return;
				}
				// System.out.println(str_Response);

				try {

					switch (ResolveJSON.IsHasResult(str_Response)) {
					case 1:
						// PersonInformation person =
						// ResolveJSON.JSON_To_PersonInformation(str_Response);
						Editor ed = MyInstance.getSharedPreferencesInstance(
								mContext).edit();
						ed.putString(Constants.PERSON_INFORMATION, str_Response);
						ed.putString(Constants.SAVE_LOGININFO, str_name + ","
								+ str_pwd);
						ed.commit();
						Response responses = ResolveJSON
								.JSON_To_Response(str_Response);
						Looper.prepare();
						Toast.makeText(mContext, responses.getMessage(),
								Toast.LENGTH_SHORT).show();
						jumpToMainPage();
						Looper.loop();
						break;
					case 0:
						Response response = ResolveJSON
								.JSON_To_Response(str_Response);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
								response.getMessage()));
						break;
					default:
						handler.sendMessage(handler.obtainMessage(
								SHOW_MESSAGE,
								mContext.getResources().getString(
										R.string.error)));

						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.sendMessage(handler.obtainMessage(
							SHOW_MESSAGE,
							mContext.getResources().getString(
									R.string.loginfail)));
					e.printStackTrace();
				}

			}
		};
		dialog = Constants.getTempDialog(this, "登录中...");
		new Thread(run).start();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (dialog != null)
				dialog.dismiss();
			switch (msg.what) {
			case SHOW_MESSAGE: {
				String content = (String) msg.obj;
				Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
				break;
			}
			}
			super.handleMessage(msg);
		}
	};
}

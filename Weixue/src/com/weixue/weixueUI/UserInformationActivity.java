package com.weixue.weixueUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.FileManager;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Methods.UploadMessage;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
import com.weixue.myUI.MyPopupWindow;

public class UserInformationActivity extends Activity {

	private static final int WHAT_HANDLER_ONCLICK=1;

	private static final int UPDATEINFO = 2;

	private static final int WHAT_HANDLER_SET=0;
	private static final int UPDATE_HEADPHOTO = 3;

	private static final int SHOW_MESSAGE=4;
	//上传用户头像对话框
	private AlertDialog temporary_dialog;

	private Context mContext;

	//选取头像
	private static final int SELECT_PIC = 0;
	private static final int TAKE_PIC = 1;
	private MyPopupWindow popup;

	private String img_path=Constants_Url.PIC_TAKEPHOTO_PATH;

	private static String PERSON_INFORMATION="personInformation";

	private TextView tv_update;

	//非编辑状态时用户信息
	private TextView tv_userName,tv_location,tv_birthday,tv_school,tv_departmant,tv_major,tv_edustartdate,tv_areasofspecificity,
	tv_introductionofseniority,tv_email,tv_website,tv_qq;

	private Spinner sp_sex;

	//提交按钮
	private Button btn_updateInfo;

	private TextView tv_laernmonay;
	private TextView tv_learncents;
	private TextView tv_level;
	private TextView tv_checkincount;
	private TextView tv_friendcount;
	private TextView tv_followercount;
	private TextView tv_statusescount;
	private TextView tv_favouritecount;
	private TextView tv_coursescount;
	private TextView tv_vip;

	private EditText edt_username;
	private EditText edt_location;
	private EditText edt_school;
	private EditText edt_departmant;
	private EditText edt_major;
	private EditText edt_edustartdate;
	private EditText edt_areasofspecificity;
	private EditText edt_introductionofseniority;
	private EditText edt_email;
	private EditText edt_website;
	private EditText edt_qq;

	private DatePicker dp_birthday;

	private PersonInformation person;
	private LinearLayout line_back;

	//头部个性签名
	private TextView tv_sign;//非编辑状态
	private EditText ed_sign;//编辑状态
	//用户头像
	private ImageView img_head;

	private int mark=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_information_page);

		init();

	}

	public void init(){
		mContext=this;

		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(onclicklistener);

		btn_updateInfo=(Button) findViewById(R.id.btn_updateInfo);
		btn_updateInfo.setOnClickListener(onclicklistener);

		tv_update=(TextView) findViewById(R.id.tv_update);
		tv_update.setOnClickListener(onclicklistener);

		//非编辑状态
		tv_userName=(TextView) findViewById(R.id.tv_username);
		tv_location=(TextView) findViewById(R.id.tv_location);
		tv_birthday=(TextView) findViewById(R.id.tv_birthday);
		tv_school=(TextView) findViewById(R.id.tv_school);
		tv_departmant=(TextView) findViewById(R.id.tv_departmant);
		tv_major=(TextView) findViewById(R.id.tv_major);
		tv_edustartdate=(TextView) findViewById(R.id.tv_edustartdate);
		tv_areasofspecificity=(TextView) findViewById(R.id.tv_areasofspecificity);
		tv_introductionofseniority=(TextView) findViewById(R.id.tv_introductionofseniority);
		tv_email=(TextView) findViewById(R.id.tv_email);
		tv_website=(TextView) findViewById(R.id.tv_website);
		tv_qq=(TextView) findViewById(R.id.tv_qq);

		//不可编辑控件
		tv_laernmonay=(TextView) findViewById(R.id.tv_laernmonay_2);
		tv_learncents=(TextView) findViewById(R.id.tv_learncents_2);
		tv_level=(TextView) findViewById(R.id.tv_level_2);
		tv_checkincount=(TextView) findViewById(R.id.tv_checkincount_2);
		tv_friendcount=(TextView) findViewById(R.id.tv_friendcount_2);
		tv_followercount=(TextView) findViewById(R.id.tv_followercount_2);
		tv_statusescount=(TextView) findViewById(R.id.tv_statusescount_2);
		tv_favouritecount=(TextView) findViewById(R.id.tv_favouritecount_2);
		tv_coursescount=(TextView) findViewById(R.id.tv_coursescount_2);

		tv_vip=(TextView) findViewById(R.id.tv_vip_2);

		edt_username=(EditText) findViewById(R.id.edt_username);

		edt_location=(EditText) findViewById(R.id.edt_location);
		//生日
		dp_birthday=(DatePicker) findViewById(R.id.dp_birthday);

		edt_school=(EditText) findViewById(R.id.edt_school);
		edt_departmant=(EditText) findViewById(R.id.edt_departmant);
		edt_major=(EditText) findViewById(R.id.edt_major);
		edt_edustartdate=(EditText) findViewById(R.id.edt_edustartdate);
		edt_areasofspecificity=(EditText) findViewById(R.id.edt_areasofspecificity);
		edt_introductionofseniority=(EditText) findViewById(R.id.edt_introductionofseniority);
		edt_email=(EditText) findViewById(R.id.edt_email);
		edt_website=(EditText) findViewById(R.id.edt_website);
		edt_qq=(EditText) findViewById(R.id.edt_qq);

		tv_sign=(TextView) findViewById(R.id.tv_sign);
		ed_sign=(EditText) findViewById(R.id.ed_sign);

		img_head=(ImageView) findViewById(R.id.img_head);
		img_head.setOnClickListener(onclicklistener);

		//性别下拉框
		sp_sex=(Spinner) findViewById(R.id.sp_sex);
		List<String> li_str=new ArrayList<String>();		
		li_str.add(getApplicationContext().getResources().getString(R.string.man));
		li_str.add(getApplicationContext().getResources().getString(R.string.girl));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				UserInformationActivity.this, R.layout.spinner_item_style,
				R.id.textView1, li_str);
		sp_sex.setAdapter(adapter);
		sp_sex.setPrompt(getApplicationContext().getResources().getString(R.string.choosesex));
		sp_sex.setEnabled(false);

		popup = new MyPopupWindow(mContext,onclicklistener,getApplicationContext().getResources().getString(R.string.takephoto),getApplicationContext().getResources().getString(R.string.selectlocal));

		sendToHandler();
	}



	OnClickListener onclicklistener=new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.tv_update:
				boolean bool=false;
				String str=tv_update.getText().toString();

				if(str.equals(getApplicationContext().getResources().getString(R.string.updateinfo))){
					bool=true;

					handler.sendMessage(handler.obtainMessage(WHAT_HANDLER_ONCLICK, bool));

				}else{

					handler.sendMessage(handler.obtainMessage(WHAT_HANDLER_ONCLICK, bool));
				}

				break;
			case R.id.line_back:
				if(mark>0){		
					Intent i=new Intent();
					i.setAction(Constants.UPDATE_USERHEAD);
					sendBroadcast(i);
				}else{
					System.out.println("复制失败或没有上传图片!");
				}
				finish();
				break;
			case R.id.img_head:
				showOrDismissMyPopup();
				break;
			case R.id.btn_updateInfo:
				updateUserInfo();
				break;
			case R.id.btn_first:
				
				//temporary_dialog=Constants.getTempDialogCanCancle(UserInformationActivity.this, "正在上传......");
				
				Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机 
				startActivityForResult(intents, TAKE_PIC);  
				showOrDismissMyPopup();
				break;
			case R.id.btn_second:
				
				//temporary_dialog=Constants.getTempDialogCanCancle(UserInformationActivity.this, "正在上传...");
				
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*") ;				
				startActivityForResult(Intent.createChooser(intent, "Select picture"),SELECT_PIC); 
				showOrDismissMyPopup();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 显示或取消显示泡泡窗口
	 */
	private void showOrDismissMyPopup(){
		if (popup.isShowing()) {
			popup.dismiss();
		} else {
			popup.showAtLocation(UserInformationActivity.this.findViewById(R.id.line_big), Gravity.BOTTOM, 0, 0);
		}
	}

	public void sendToHandler(){			
		try {
			person=ResolveJSON.JSON_To_PersonInformation(MyInstance.getSharedPreferencesString(mContext,Constants.PERSON_INFORMATION));
			handler.sendMessage(handler.obtainMessage(WHAT_HANDLER_SET, person));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_HANDLER_SET:{								
				//setEd(false);
				PersonInformation person=(PersonInformation) msg.obj;
				setText(person);

				break;
			}
			case WHAT_HANDLER_ONCLICK:{

				boolean bool=(Boolean) msg.obj;
				if(!bool){

					setEd(bool);
					tv_update.setText(getApplicationContext().getResources().getString(R.string.updateinfo));

				}else{

					setEd(bool);
					tv_update.setText(getApplicationContext().getResources().getString(R.string.cancel));
				}
				break;
			}
			case UPDATEINFO:{
				String response=(String) msg.obj;
				try {
					switch(ResolveJSON.IsHasResult(response)){
					case 1:
						Toast.makeText(getApplicationContext(), ResolveJSON.JSON_To_Response(response).getMessage(), Toast.LENGTH_SHORT).show();
						updateSaveData(person.getUserId());
						break;
					case 0:
						Toast.makeText(getApplicationContext(), ResolveJSON.JSON_To_Response(response).getMessage(), Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case UPDATE_HEADPHOTO:{
				String response=(String) msg.obj;
				Log.e("取消对话框", "wqe");
				temporary_dialog.dismiss();
				try {
					switch(ResolveJSON.IsHasResult(response)){
					case 1:
						Toast.makeText(getApplicationContext(), ResolveJSON.JSON_To_Response(response).getMessage(), Toast.LENGTH_SHORT).show();
						updateSaveData(person.getUserId());
						//此处取消对话框
						//Log.e("测试新", ResolveJSON.JSON_To_Response(response).getMessage());

						break;
					case 0:
						Toast.makeText(getApplicationContext(), ResolveJSON.JSON_To_Response(response).getMessage(), Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
				break;
			}

			}

		}

	};


	public void setText(PersonInformation person){

		tv_userName.setText(person.getUsername());		
		tv_location.setText(person.getLocation());
		tv_birthday.setText(person.getBirthday());
		tv_school.setText(person.getSchool());
		tv_departmant.setText(person.getDepartment());
		tv_major.setText(person.getMajor());
		tv_edustartdate.setText(person.getEduStartDate());
		tv_areasofspecificity.setText(person.getAreasOfSpecificity());
		tv_introductionofseniority.setText(person.getIntroductionOfSeniority());
		tv_email.setText(person.getEmail());
		tv_website.setText(person.getWebsite());
		tv_qq.setText(person.getQQ());


		edt_username.setText(person.getUsername());

		if(person.getGender().equals(getApplicationContext().getResources().getString(R.string.man))){
			sp_sex.setSelection(0);
		}else{
			sp_sex.setSelection(1);
		}

		String time=person.getBirthday();
		String[] str=time.split("\\D");
		//		for(int i=0;i<str.length;i++){
		//			System.out.println(str[i]);
		//		}
		if(str!=null&&str.length>3){
			dp_birthday.init(Integer.parseInt(str[0]), (Integer.parseInt(str[1])-1), Integer.parseInt(str[2]), null);
		}

		edt_location.setText(person.getLocation());					
		edt_school.setText(person.getSchool());		
		edt_departmant.setText(person.getDepartment());
		edt_major.setText(person.getMajor());
		edt_edustartdate.setText(person.getEduStartDate());
		edt_areasofspecificity.setText(person.getAreasOfSpecificity());
		edt_introductionofseniority.setText(person.getIntroductionOfSeniority());
		edt_email.setText(person.getEmail());
		edt_website.setText(person.getWebsite());
		edt_qq.setText(person.getQQ());


		tv_laernmonay.setText(String.valueOf(person.getLearnMoney()));
		tv_learncents.setText(String.valueOf(person.getLearnCents()));
		tv_level.setText(String.valueOf(person.getLevel()));
		tv_checkincount.setText(String.valueOf(person.getCheckInCount()));
		tv_friendcount.setText(String.valueOf(person.getFriendsCount()));
		tv_followercount.setText(String.valueOf(person.getFollowerCount()));
		tv_statusescount.setText(String.valueOf(person.getStatusesCount()));
		tv_favouritecount.setText(String.valueOf(person.getFavouritesCount()));
		tv_coursescount.setText(String.valueOf(person.getCoursesCount()));

		tv_sign.setText(person.getSignature());
		ed_sign.setText(person.getSignature());

		String fileName=person.getLargePhotoPath().substring(person.getLargePhotoPath().lastIndexOf("/")+1);

		if(fileName!=null&&fileName!=""){
			//System.out.println(fileName);
			File file =new File(Constants_Url.PIC_USERHEAD_PATH+"/"+fileName);

			if(file.exists()){
				//img_head.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(Constants_Url.PIC_USERHEAD_PATH+"/"+fileName, options);
				img_head.setImageBitmap(bm);



				System.out.println(Constants_Url.PIC_USERHEAD_PATH+"/"+fileName);
				//img_head.setImageBitmap(BitmapFactory.decodeFile(Constants_Url.PIC_USERHEAD_PATH+"/"+fileName));
			}
		}


		int j=person.getIsVIP();
		if(j==0){
			tv_vip.setText(getApplicationContext().getResources().getString(R.string.normaluser));
		}else{
			tv_vip.setText(getApplicationContext().getResources().getString(R.string.vipuser));
		}

	}



	public void setEd(boolean bool){
		if(bool){
			sp_sex.setEnabled(true);
			btn_updateInfo.setVisibility(View.VISIBLE);

			tv_sign.setVisibility(View.GONE);
			ed_sign.setVisibility(View.VISIBLE);

			tv_userName.setVisibility(View.GONE);
			edt_username.setVisibility(View.VISIBLE);

			tv_location.setVisibility(View.GONE);
			edt_location.setVisibility(View.VISIBLE);

			tv_birthday.setVisibility(View.GONE);
			dp_birthday.setVisibility(View.VISIBLE);

			tv_school.setVisibility(View.GONE);
			edt_school.setVisibility(View.VISIBLE);

			tv_departmant.setVisibility(View.GONE);
			edt_departmant.setVisibility(View.VISIBLE);

			tv_major.setVisibility(View.GONE);
			edt_major.setVisibility(View.VISIBLE);

			tv_edustartdate.setVisibility(View.GONE);
			edt_edustartdate.setVisibility(View.VISIBLE);

			tv_areasofspecificity.setVisibility(View.GONE);
			edt_areasofspecificity.setVisibility(View.VISIBLE);

			tv_introductionofseniority.setVisibility(View.GONE);
			edt_introductionofseniority.setVisibility(View.VISIBLE);

			tv_email.setVisibility(View.GONE);
			edt_email.setVisibility(View.VISIBLE);

			tv_website.setVisibility(View.GONE);
			edt_website.setVisibility(View.VISIBLE);

			tv_qq.setVisibility(View.GONE);
			edt_qq.setVisibility(View.VISIBLE);
		}else{
			sp_sex.setEnabled(false);
			btn_updateInfo.setVisibility(View.GONE);

			tv_sign.setVisibility(View.VISIBLE);
			ed_sign.setVisibility(View.GONE);

			tv_userName.setVisibility(View.VISIBLE);
			edt_username.setVisibility(View.GONE);

			tv_location.setVisibility(View.VISIBLE);
			edt_location.setVisibility(View.GONE);

			tv_birthday.setVisibility(View.VISIBLE);
			dp_birthday.setVisibility(View.GONE);

			tv_school.setVisibility(View.VISIBLE);
			edt_school.setVisibility(View.GONE);

			tv_departmant.setVisibility(View.VISIBLE);
			edt_departmant.setVisibility(View.GONE);

			tv_major.setVisibility(View.VISIBLE);
			edt_major.setVisibility(View.GONE);

			tv_edustartdate.setVisibility(View.VISIBLE);
			edt_edustartdate.setVisibility(View.GONE);

			tv_areasofspecificity.setVisibility(View.VISIBLE);
			edt_areasofspecificity.setVisibility(View.GONE);

			tv_introductionofseniority.setVisibility(View.VISIBLE);
			edt_introductionofseniority.setVisibility(View.GONE);

			tv_email.setVisibility(View.VISIBLE);
			edt_email.setVisibility(View.GONE);

			tv_website.setVisibility(View.VISIBLE);
			edt_website.setVisibility(View.GONE);

			tv_qq.setVisibility(View.VISIBLE);
			edt_qq.setVisibility(View.GONE);
		}


	}


	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data); 

		switch(requestCode){
		case SELECT_PIC:{
			if(resultCode==Activity.RESULT_OK) 
			{ 
				//设置上传对话框
				temporary_dialog=Constants.getTempDialogCanCancle(UserInformationActivity.this, "正在上传...");
				/** 
				 * 当选择的图片不为空的话，在获取到图片的途径   
				 */ 
				Uri uri = data.getData();
				String[] proj = {MediaStore.Images.Media.DATA};
				//好像是android多媒体数据库的封装接口，具体的看Android文档
				Cursor cursor = managedQuery(uri, proj, null, null, null);
				//按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				//将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				//最后根据索引值获取图片路径
				String path = cursor.getString(column_index);

				img_head.setImageDrawable(Drawable.createFromPath(path));

				uploadHeadPhoto(path,person.getUserId());

				mark=FileManager.fileCopyToOtherLocation(path, Constants_Url.PIC_USERHEAD_PATH+"/"+path.substring(path.lastIndexOf("/")+1));

			} 
			break;
		}
		case TAKE_PIC:{
			if(resultCode==Activity.RESULT_OK) 
			{ 
				//设置上传对话框
				temporary_dialog=Constants.getTempDialogCanCancle(UserInformationActivity.this, "正在上传...");
				
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				//System.out.println("bitmap-->"+bitmap.toString());		 
				SimpleDateFormat s=new SimpleDateFormat("yyyyMMddhhmmss");  
				String date=s.format(new Date()); 
				if (android.os.Environment.getExternalStorageState().equals(  
						android.os.Environment.MEDIA_MOUNTED)) { 
					File file=new File(img_path);
					if(!file.exists()){
						file.mkdirs();
					}
					String save_path=img_path+date+".jpg";
					//bitmap=CompressImg.compressImage(bitmap);
					saveBitmap(bitmap,save_path);
					img_head.setImageDrawable(Drawable.createFromPath(save_path));
					uploadHeadPhoto(save_path,person.getUserId());
					mark=FileManager.fileCopyToOtherLocation(save_path, Constants_Url.PIC_USERHEAD_PATH+"/"+save_path.substring(save_path.lastIndexOf("/")+1));
				}else{
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.noSDcard)));
				}
			}
			break;
		}

		}
	}

	//保存图片
	private void saveBitmap(Bitmap bitmap,String filePath){
		FileOutputStream bos = null;
		File imgFile = new File(filePath);
		try {
			if(!imgFile.exists())
				imgFile.createNewFile();

			bos = new FileOutputStream(imgFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				bos.flush();
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 上传头像
	 * @param path 图片本机地址
	 * @param uid 用户ID
	 */
	private void uploadHeadPhoto(final String path,final int uid){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					
					//handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.uploading)));
					String str=UploadMessage.uploadPic(Constants_Url.UPLOAD_PERSONHEAD, new File(path),uid);
					handler.sendMessage(handler.obtainMessage(UPDATE_HEADPHOTO, str));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			new Thread(run).start();
	}

	/**
	 * 修改用户信息
	 */
	private void updateUserInfo(){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.UPDATE_PERSONINFO;
				Map<String,String> map=new HashMap<String,String>();
				map.put("id", String.valueOf(person.getUserId()));
				map.put("email", String.valueOf(edt_email.getText()));
				map.put("sex", sp_sex.getSelectedItem().toString());
				map.put("location", String.valueOf(edt_location.getText()));
				//				String birthdayStr=String.valueOf(dp_birthday.getYear())+"-"+String.valueOf(dp_birthday.getMonth()+1)+"-"+String.valueOf(dp_birthday.getDayOfMonth());
				//			System.out.println(birthdayStr);	
				map.put("birthday", String.valueOf(dp_birthday.getYear())+"-"+String.valueOf(dp_birthday.getMonth()+1)+"-"+String.valueOf(dp_birthday.getDayOfMonth()));

				System.out.println("birthday:"+String.valueOf(dp_birthday.getYear())+"-"+String.valueOf(dp_birthday.getMonth()+1)+"-"+String.valueOf(dp_birthday.getDayOfMonth()));

				map.put("school", String.valueOf(edt_school.getText()));
				map.put("department", String.valueOf(edt_departmant.getText()));
				map.put("major", String.valueOf(edt_major.getText()));
				map.put("edustardate", String.valueOf(edt_edustartdate.getText()));
				map.put("areasofspecificity", String.valueOf(edt_areasofspecificity.getText()));
				map.put("edustardate", String.valueOf(edt_edustartdate.getText()));
				map.put("introductionofseniority", String.valueOf(edt_introductionofseniority.getText()));
				map.put("website", String.valueOf(edt_website.getText()));
				map.put("qq", String.valueOf(edt_qq.getText()));
				map.put("signature", String.valueOf(ed_sign.getText()));

				try {
					String str_Response = UploadMessage.post(requestUrl,map);
					handler.sendMessage(handler.obtainMessage(UPDATEINFO, str_Response));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			new Thread(run).start();
	}

	/**
	 * 更新本地保存的用户信息
	 * @param uid
	 */
	private void updateSaveData(final int uid){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url=Constants_Url.PERSON_INFO+"?uid="+uid;

				try {
					String responseStr=NetWork.getData(url);
					if(ResolveJSON.IsHasResult(responseStr)==1){
						//Response response=ResolveJSON.JSON_To_Response(responseStr);
						Editor ed=MyInstance.getSharedPreferencesInstance(mContext).edit();
						ed.putString(Constants.PERSON_INFORMATION, responseStr);
						ed.commit();
						sendToHandler();
						//System.out.println(response.getResult());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			new Thread(run).start();
	}

}

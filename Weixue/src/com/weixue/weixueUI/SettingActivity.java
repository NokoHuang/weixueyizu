package com.weixue.weixueUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Function.MyInstance;
import com.weixue.Methods.FileManager;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
import com.weixue.myUI.MyPopupWindow;

public class SettingActivity extends Activity{
	
	private Context mContext;
		private LinearLayout line_back;
	private Spinner sp_downloadNum;
	private ArrayAdapter<String> adapter;
	private List<String> li_str;
	
	private TextView tv_more;
	
	
	private String personInfoStr="";
	private int downloadNum=2;
	
	private MyPopupWindow popup;
	
private Button btn_deleteCachePic,btn_deleteAll,btn_cancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.setting_page);
		initUi();
	}
	
	//初始化
	private void initUi() {
		// TODO Auto-generated method stub
		mContext=this;
		
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);
		
		tv_more=(TextView) findViewById(R.id.tv_more);
		tv_more.setOnClickListener(listener);
		
		li_str=new ArrayList<String>();
		
		li_str.add("1");
		li_str.add("2");
		li_str.add("3");
		
		sp_downloadNum=(Spinner) findViewById(R.id.sp_downloadNum);
		sp_downloadNum.setOnItemSelectedListener(itemSelectlistener);
		adapter = new ArrayAdapter<String>(
				SettingActivity.this, R.layout.spinner_item_style,
				R.id.textView1, li_str);

		sp_downloadNum.setAdapter(adapter);
		sp_downloadNum.setPrompt(getApplicationContext().getResources().getString(R.string.selectnum));
		
		
		personInfoStr=MyInstance.getSharedPreferencesString(mContext, Constants.PERSON_INFORMATION);
		downloadNum=MyInstance.getSharedPreferencesInt(mContext, "downloadNum");
		if(downloadNum==0){
			downloadNum=2;
		}
		sp_downloadNum.setSelection((downloadNum-1));
		
		View pop_view = LayoutInflater.from(mContext).inflate(R.layout.popup_setting_page, null);
		btn_deleteCachePic=(Button) pop_view.findViewById(R.id.btn_first);
		btn_deleteAll=(Button) pop_view.findViewById(R.id.btn_second);
		btn_cancel=(Button) pop_view.findViewById(R.id.btn_cancel);
		btn_deleteCachePic.setOnClickListener(listener);
		btn_deleteAll.setOnClickListener(listener);
		btn_cancel.setOnClickListener(listener);
		
		popup = new MyPopupWindow(mContext,listener,getApplicationContext().getResources().getString(R.string.deletecacheimg),getApplicationContext().getResources().getString(R.string.deleteall));
	
		
		
	}
	
	AdapterView.OnItemSelectedListener itemSelectlistener=new AdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Editor ed=MyInstance.getSharedPreferencesInstance(mContext).edit();
			ed.putInt(Constants.DOWNLOADNUM, Integer.parseInt(li_str.get(arg2)));
			ed.commit();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}};

		OnClickListener listener=new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch(arg0.getId()){
				case R.id.line_back:					
					finish();					
					break;
				case R.id.tv_more:
					if (popup.isShowing()) {
						popup.dismiss();
					} else {
//						popup.showAsDropDown(tv_more);
						popup.showAtLocation(SettingActivity.this.findViewById(R.id.line_content), Gravity.BOTTOM, 0, 0);
					}
					
					break;
				case R.id.btn_first:
					FileManager.deleteFile(new File(Constants_Url.PIC_PATH));
					closePopupWindow();
					Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.deletecacheimgsuccess), Toast.LENGTH_SHORT).show();
					break;
				case R.id.btn_second:				
					Editor ed=MyInstance.getSharedPreferencesInstance(mContext).edit();
					ed.clear();
					ed.commit();
					FileManager.deleteFile(new File(Constants_Url.FILE_PATH));
					closePopupWindow();
					Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.deleteallsuccess), Toast.LENGTH_SHORT).show();
					ed.putString("personInformation", personInfoStr);
					ed.commit();
					
					break;
				case R.id.btn_cancel:
					closePopupWindow();
					break;
				}
			}};
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			if(popup.isShowing()){
				popup.dismiss();
			}
			super.onBackPressed();
		}
		
		/**
		 * 关闭泡泡窗口
		 */
		private void closePopupWindow(){
			if(popup.isShowing()){
				popup.dismiss();
			}
		}
			
}

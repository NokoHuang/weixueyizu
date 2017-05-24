package com.weixue.NewUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.weixue.Function.MyInstance;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.PersonInformation;
import com.weixue.Utils.Constants;
import com.weixue.myUI.StudyFieldItem1;
import com.weixue.weixueUI.CarriesListActivity;
import com.weixue.weixueUI.R;

/**
 * 学习园地
 * 
 * @author zeda
 * 
 */
public class StudyField extends Activity {
	private RelativeLayout item1, item2, item3;
	private PersonInformation person;
	private int userid;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_field);
		person = ResolveJSON
				.JSON_To_PersonInformation(MyInstance
						.getSharedPreferencesString(this,
								Constants.PERSON_INFORMATION));
		userid = person.getUserId();
		Init();
	}

	public void Init() {
		item1 = (RelativeLayout) findViewById(R.id.item1);
		item1.setOnClickListener(onclicklistener);

		item2 = (RelativeLayout) findViewById(R.id.item2);
		item2.setOnClickListener(onclicklistener);

		item3 = (RelativeLayout) findViewById(R.id.item3);
		item3.setOnClickListener(onclicklistener);
	}

	public void GoBack(View v) {
		onBackPressed();
	}

	OnClickListener onclicklistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.item1: {
				Intent intent = new Intent(StudyField.this,
						StudyFieldItem1.class);
				startActivity(intent);
				break;
			}
			case R.id.item2: {
				/*
				 * Intent intent=new
				 * Intent(StudyField.this,StudyFieldItem2.class);
				 * startActivity(intent);
				 */
				Toast.makeText(getApplicationContext(), "系统模块开发中",
						Toast.LENGTH_LONG).show();
				break;
			}
			case R.id.item3: {
				/*
				 * Intent intent=new
				 * Intent(StudyField.this,StudyFieldItem3.class);
				 * startActivity(intent);
				 */
				// Toast.makeText(getApplicationContext(), "系统模块开发中",
				// Toast.LENGTH_LONG).show();
				Intent intentKaoShi = new Intent(StudyField.this,
						CarriesListActivity.class);
				intentKaoShi.putExtra("UserId", userid);
				intentKaoShi.putExtra("mid", 1);
				intentKaoShi.putExtra("title", "在线考试");
				startActivityForResult(intentKaoShi, 1006);
				break;
			}
			}
		}
	};
}

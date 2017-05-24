package com.weixue.NewUI;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.oned.rss.FinderPattern;
import com.weixue.Model.Course;
import com.weixue.Model.CourseWare;
import com.weixue.weixueUI.CarriesListActivity;
import com.weixue.weixueUI.R;

@SuppressLint("ValidFragment")
public class MoenFragment extends Fragment {
	private List<CourseWare> li_units;
	private Activity activity;
	private int cid, userid;
	private LinearLayout mLineTotal,mLine_Question_Answer;
	private View v;

	public MoenFragment(List<CourseWare> li_units, Activity activity, int cid,
			int userid) {
		this.li_units = li_units;
		this.activity = activity;
		this.cid = cid;
		this.userid = userid;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.moen_view, container, false);
		
		Init();
		
		mLineTotal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
					Intent intentKaoShi = new Intent(activity,
							CarriesListActivity.class);
					intentKaoShi.putExtra("UserId", userid);
					intentKaoShi.putExtra("mid", cid);
					intentKaoShi.putExtra("title", Course.course.getCourseName());
					startActivityForResult(intentKaoShi, 1006);
				
			}
		});
		
		mLine_Question_Answer.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_LONG).show();
			}
			
		});
		return v;
	}
	
	public void Init(){
		mLine_Question_Answer=(LinearLayout)v.findViewById(R.id.line_question_answer);
		mLineTotal = (LinearLayout) v.findViewById(R.id.line_exam);
	}

}

package com.weixue.NewUI;

import java.util.ArrayList;
import java.util.List;

import com.weixue.Adapter.VideoListAdapter;
import com.weixue.Model.CourseWare;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.R.layout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PublicFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.moen_view, container, false); 
		
		return v;
	}
	
}

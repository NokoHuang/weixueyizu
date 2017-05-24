package com.weixue.NewUI;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.weixue.Adapter.VideoListAdapter;
import com.weixue.Model.CourseWare;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.Wanzheng_Course_video_playbackActivity;

@SuppressLint("ValidFragment")
public class VidaoFragment extends Fragment implements OnItemClickListener {

	private List<CourseWare> li_units;
	private List<CourseWare> screenList;

	public VidaoFragment(List<CourseWare> li_units) {
		this.li_units=li_units;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.vidao_view, container, false); 
		ListView listView=(ListView) v.findViewById(R.id.list_video);
		listView.setOnItemClickListener(this);
		screenList=new ArrayList<CourseWare>();
		for(CourseWare courseWare:li_units) {
			if(courseWare.getFilePath().substring(courseWare.getFilePath().lastIndexOf(".")+1).equals("mp4")){
				screenList.add(courseWare);
			}
		}
		VideoListAdapter adapter= new VideoListAdapter(v.getContext(),screenList,"");
		listView.setAdapter(adapter);

		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(null!=screenList&&screenList.size()>0){
			Intent it = new Intent(Intent.ACTION_VIEW);
			System.out.println(screenList.get(arg2).getFilePath());
			Uri uri = Uri.parse(screenList.get(arg2).getFilePath());
			it.setDataAndType(uri, "video/*");
			startActivity(it);
		}
	}

}

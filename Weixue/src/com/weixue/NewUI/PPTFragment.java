package com.weixue.NewUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.weixue.Adapter.VideoListAdapter;
import com.weixue.Model.CourseWare;
import com.weixue.MyDialog.iphoneDialog;
import com.weixue.Service.DownLoadService;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.StartOtherApp;
import com.weixue.weixueUI.Wanzheng_Course_video_playbackActivity;
import com.weixue.weixueUI.R.layout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class PPTFragment extends Fragment implements OnItemClickListener {

	private List<CourseWare> li_units;
	private List<CourseWare> screenList;

	public PPTFragment(List<CourseWare> li_units) {
		this.li_units=li_units;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.ppt_view, container, false); 
		ListView listView=(ListView) v.findViewById(R.id.list_video);
		screenList=new ArrayList<CourseWare>();
		
		listView.setOnItemClickListener(this);
		for(CourseWare courseWare:li_units) {
			if(courseWare.getFilePath().substring(courseWare.getFilePath().lastIndexOf(".")+1).equals("ppt")){
				screenList.add(courseWare);
			}
		}
		VideoListAdapter adapter= new VideoListAdapter(v.getContext(),screenList,"ppt");
		listView.setAdapter(adapter);
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(null!=screenList&&screenList.size()>0){
			String path=screenList.get(arg2).getFilePath();
			if(path!=null&&path!=""){
				String fileName=path.substring(path.lastIndexOf("/")+1);
				File file=new File(Constants_Url.OTHERFILE_PACH+"/"+fileName);
				if(!file.exists()){
					showIsdownloadDialog(path);
				}else{
					Intent intent=new Intent();
					intent.putExtra("ms", "ppt");
					intent.setClass(getActivity(),StartOtherApp.class);
					startActivity(intent);
				}
			}
			
			
		}
	}
	/**
	 * 选择是否下载对话框
	 * @param path 下载地址
	 */
	private void showIsdownloadDialog(final String path) {
		// TODO Auto-generated method stub		
		iphoneDialog.iphoneDialogBuilder idb = new iphoneDialog.iphoneDialogBuilder(getActivity());
		idb.setTitle(getActivity().getResources().getString(R.string.prompt));
		idb.setMessage(getActivity().getResources().getString(R.string.fileNotExist));
		idb.setPositiveButton(getActivity().getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent=new Intent(getActivity(),DownLoadService.class);
				intent.putExtra("path",path);
				getActivity().startService(intent);
			}
		});
		idb.setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		idb.show();
	}
	
}

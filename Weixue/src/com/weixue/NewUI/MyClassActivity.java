package com.weixue.NewUI;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.weixue.Adapter.PersionAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.PersonalCenterActivity;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.Wanzheng_Course_video_playbackActivity;

public class MyClassActivity extends Activity {

	private List<Course> courses;
	private int personId;

	private ListView lv_myClass;
	private int Count=15;
	private int pageIndex=1;
	private int pageCount=0;
	/**增加更多数据*/
	private static final int MOREITEM=3;
	/**设置加入的课程*/
	private static final int SETCOURSE=2;
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）
	private View footerView_loading;//加载更多时显示等待

	private PersionAdapter adapter;
	private LinearLayout line_loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_my_class);

		personId=getIntent().getIntExtra("personId",0);

		line_loading=(LinearLayout) findViewById(R.id.line_loading);
		footerView_loading=LayoutInflater.from(this).inflate(R.layout.loading_and_refesh_page, null);
		lv_myClass = (ListView) findViewById(R.id.per_lesson_list1);
		lv_myClass.setOnItemClickListener(onitemclicklistener);
		lv_myClass.setOnScrollListener(new MyScrollListener());

		getCourseArray(personId,pageIndex,pageCount,SETCOURSE);

	}

	/**
	 * 添加子监听
	 */
	OnItemClickListener onitemclicklistener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0,
				View arg1, int position, long arg3) {
			Intent intent = new Intent();
			//		intent.putExtra("cid", li_course.get(position).getCourseID());
			//		intent.putExtra("introduction", li_course.get(position).getIntroduction());
			//		intent.putExtra("img_path", li_course.get(position).getCourse_ImgUrl());

			Course.course=courses.get(position);		
			intent.setClass(getApplicationContext(), Wanzheng_Course_video_playbackActivity.class);
			startActivity(intent);
			finish();

		}
	};

	//监听滚动事件实现自动加载
	class MyScrollListener implements OnScrollListener{
		int position=0;
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			switch (scrollState) {
			// 当不滚动时
			case OnScrollListener.SCROLL_STATE_IDLE:
			{
				if((lv_myClass.getLastVisiblePosition()+1)==lv_myClass.getCount()&&lv_myClass.getCount()>=Count&&isNotDownLoad){
					isNotDownLoad=false;
					//System.out.println("正在加载更多数据...");
					lv_myClass.addFooterView(footerView_loading);
					position=lv_myClass.getCount();
					//System.out.println("position-->"+position);	    		
					lv_myClass.setSelection(position);
					pageIndex+=1;
					getCourseArray(personId,pageIndex,pageCount,MOREITEM);
				}
				break;
			}
			}
		}}

	public void getCourseArray(final int uid,final int pageIndex,final int pageCount,final int what){
		Runnable run=new Runnable(){
			@Override
			public void run() {
				String requestUrl=Constants_Url.ADDED_COURSE+"?uid="+uid+"&pageIndex="+pageIndex+"&pageCount="+pageCount;
				try {
					String jsonStr=NetWork.getData(requestUrl);
					handler.sendMessage(handler.obtainMessage(what, jsonStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			new Thread(run).start();
	}

	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MOREITEM:{
				String content=(String) msg.obj;	
				try {
					switch(ResolveJSON.IsHasResult(content)){
					case 1:
						List<Course> li=ResolveJSON.JSON_TO_CourseArray(content);
						for(int i=0;i<li.size();i++){
							courses.add(li.get(i));
						}
						lv_myClass.removeFooterView(footerView_loading);
						adapter.notifyDataSetChanged();
						isNotDownLoad=true;
						break;
					case 0:
						Toast.makeText(MyClassActivity.this, MyClassActivity.this.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
						lv_myClass.removeFooterView(footerView_loading);
						break;
					default:
						Toast.makeText(getApplicationContext(), MyClassActivity.this.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case SETCOURSE:{
				String content=(String) msg.obj;
				//System.out.println("+++++++"+content);
				try {

					switch(ResolveJSON.IsHasResult(content)){
					case 1:
						courses=ResolveJSON.JSON_TO_CourseArray(content);
						lv_myClass.addFooterView(footerView_loading);

						loadData(courses);
						lv_myClass.removeFooterView(footerView_loading);
						line_loading.setVisibility(View.GONE);
						break;
					case 0:
						Response response=ResolveJSON.JSON_To_Response(content);
						Toast.makeText(MyClassActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
						line_loading.setVisibility(View.GONE);
						break;
					default:
						Toast.makeText(getApplicationContext(),MyClassActivity.this.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			}
		}

		
	};

	private void loadData(List<Course> courses) {
		lv_myClass.setVisibility(View.VISIBLE);
		adapter= new PersionAdapter(getApplicationContext(),courses);
		lv_myClass.setAdapter(adapter);
	}
	
}

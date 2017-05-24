package com.weixue.weixueUI;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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

public class CollectCourseActivity extends Activity{
	private static final int SETDATA=0;
	/**增加更多数据*/
	private static final int MOREITEM=1;

	private Context mContext;
	// 返回按钮
	private LinearLayout line_back;
	private ListView lv_moreCourse;
	private PersionAdapter adapter;
	private List<Course> li_Course;

	private int pageIndex=1;
	private int pageCount=0;
	private int Count=15;
	private int uid;
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）

	private View footerView_loading;//加载更多时显示等待
	private LinearLayout line_loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.lv_morecourse_page);
		initUi();
	}
	/**
	 * 初始化控件
	 */
	public void initUi(){
		mContext=this;

		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(clickListener);

		lv_moreCourse=(ListView) findViewById(R.id.lv_moreCourse);
		lv_moreCourse.setOnScrollListener(new MyScrollListener());
		lv_moreCourse.setOnItemClickListener(onitemclicklistener);

		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null); 	
		line_loading=(LinearLayout) findViewById(R.id.line_loading);

		uid=this.getIntent().getExtras().getInt("uid");
		getCourseArray(uid,pageIndex,pageCount,SETDATA);
	}

	// 点击监听者
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.line_back: {//返回
				finish();
				break;
			}							
			}
		}
	};


	/**加载列表数据*/
	private void loadData(List<Course> li) {
		adapter= new PersionAdapter(getApplicationContext(),li);
		lv_moreCourse.setAdapter(adapter);
	}


	/**
	 * 获取用户收藏课程
	 * @param uid
	 * @param pageIndex
	 * @param pageCount
	 * @param what (msg.what的值)
	 */
	public void getCourseArray(final int uid,final int pageIndex,final int pageCount,final int what){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.GETCOLLECT_COURSE+"?uid="+uid+"&pageIndex="+pageIndex+"&pageCount="+pageCount;

				try {
					String jsonStr=NetWork.getData(requestUrl);

					handler.sendMessage(handler.obtainMessage(what, jsonStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		new Thread(run).start();
	}

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
				if((lv_moreCourse.getLastVisiblePosition()+1)==lv_moreCourse.getCount()&&lv_moreCourse.getCount()>=Count&&isNotDownLoad){
					isNotDownLoad=false;
					//System.out.println("正在加载更多数据...");
					lv_moreCourse.addFooterView(footerView_loading);
					position=lv_moreCourse.getCount();
					//System.out.println("position-->"+position);	    		
					lv_moreCourse.setSelection(position);
					pageIndex+=1;
					getCourseArray(uid,pageIndex,pageCount,MOREITEM);
				}

				break;
			}
			}
		}}	

	/**
	 * 添加子监听
	 */
	OnItemClickListener onitemclicklistener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0,
				View arg1, int position, long arg3) {
			Intent intent = new Intent();
			intent.putExtra("course", li_Course.get(position));	
			intent.setClass(mContext, Course_video_playbackActivity.class);
			startActivity(intent);


		}
	};	

	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case SETDATA:{
				String content=(String) msg.obj;
				try {

					switch(ResolveJSON.IsHasResult(content)){
					case 1:
						li_Course=ResolveJSON.JSON_TO_CourseArray(content);
						lv_moreCourse.addFooterView(footerView_loading);
						loadData(li_Course);
						lv_moreCourse.removeFooterView(footerView_loading);
						line_loading.setVisibility(View.GONE);
						break;
					case 0:
						Response response=ResolveJSON.JSON_To_Response(content);
						Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
						line_loading.setVisibility(View.GONE);
						break;
					default:
						Toast.makeText(getApplicationContext(),  mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case MOREITEM:{
				String content=(String) msg.obj;	
				try {
					switch(ResolveJSON.IsHasResult(content)){
					case 1:
						List<Course> li=ResolveJSON.JSON_TO_CourseArray(content);
						for(int i=0;i<li.size();i++){
							li_Course.add(li.get(i));
						}
						lv_moreCourse.removeFooterView(footerView_loading);
						adapter.notifyDataSetChanged();
						isNotDownLoad=true;
						break;
					case 0:
						Toast.makeText(mContext,mContext.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
						lv_moreCourse.removeFooterView(footerView_loading);
						break;
					default:
						Toast.makeText(getApplicationContext(),  mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			}
			super.handleMessage(msg);
		}

	};
}
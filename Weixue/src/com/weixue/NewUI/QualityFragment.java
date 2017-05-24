package com.weixue.NewUI;

import java.util.ArrayList;
import java.util.List;

import com.weixue.Adapter.LessonAdapter;
import com.weixue.Adapter.VideoListAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.CourseWare;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.ChoiceRecoActivity;
import com.weixue.weixueUI.Course_video_playbackActivity;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.R.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class QualityFragment extends Fragment {
	
	private Context mContext;
	/**设置数据*/
	private static final int SETDATA=0;
	private static final int MOREITEM = 1;
	//显示信息
	private static final int SHOW_MESSAGE=2;

	private ListView lv_lesson;
	public List<Course> li_course;//显示需要的数据
	private LessonAdapter adapter;

	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）
	private View footerView_loading;//加载更多时显示等待

	public int pageIndex=1;
	public int pageCount=0;
	public int count=15;//每页显示的数量
	public int type=1;
	public int online=0;
	
	private View v;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.choice_reco_page, container, false); 
		init();
		return v;
	}
	
	//初始化
		public void init(){
			mContext=getActivity();
			lv_lesson =(ListView) v.findViewById(R.id.lesson_list);
			lv_lesson.setOnItemClickListener(onitemclicklistener);
			lv_lesson.setOnScrollListener(new MyScrollListener());
			footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null); 

			System.out.println("choiceReco初始化！");
			getCourseArray(type,online,pageIndex,pageCount,SETDATA);
		}

		//加载数据
		public void loadData(List<Course> li){
			System.out.println("choiceReco数据加载！");
			adapter=new LessonAdapter(mContext,li);
			lv_lesson.setAdapter(adapter);
		}

		/**
		 * get请求获取精品课程
		 * @param uid
		 * @param pageIndex
		 * @param pageCount
		 * @param what (msg.what的值)
		 */
		public void getCourseArray(final int type,final int online,final int pageIndex,final int pageCount,final int what){
			Runnable run=new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String requestUrl=Constants_Url.GETCOURSE_BYTYPE+"?type="+type+"&online="+online+"&pageIndex="+pageIndex+"&pageCount="+pageCount;

					try {
						String jsonStr=NetWork.getData(requestUrl);

						handler.sendMessage(handler.obtainMessage(what, jsonStr));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.networkerror)));
					}
				}};
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
					if((lv_lesson.getLastVisiblePosition()+1)==lv_lesson.getCount()&&lv_lesson.getCount()>=count&&isNotDownLoad){
						isNotDownLoad=false;
						//System.out.println("正在加载更多数据...");
						lv_lesson.addFooterView(footerView_loading);
						position=lv_lesson.getCount();
						//System.out.println("position-->"+position);	    		
						lv_lesson.setSelection(position);
						pageIndex+=1;
						getCourseArray(type,online,pageIndex,pageCount,MOREITEM);
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
				intent.putExtra("course", li_course.get(position));	
				intent.setClass(mContext.getApplicationContext(), Course_video_playbackActivity.class);
				startActivity(intent);

			}
		} ;


		Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case SETDATA:{
					String content=(String) msg.obj;

					try {
						switch(ResolveJSON.IsHasResult(content)){
						case 1:

							li_course=ResolveJSON.JSON_TO_CourseArray(content);	
							lv_lesson.addFooterView(footerView_loading);
							loadData(li_course);
							lv_lesson.removeFooterView(footerView_loading);
							break;
						case 0:
							Toast.makeText(mContext, ResolveJSON.JSON_To_Response(content).getMessage(), Toast.LENGTH_SHORT).show();
							break;
						default:
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.error)));
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
								li_course.add(li.get(i));
							}
							lv_lesson.removeFooterView(footerView_loading);
							adapter.notifyDataSetChanged();
							isNotDownLoad=true;
							break;
						case 0:
							Toast.makeText(mContext, mContext.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
							lv_lesson.removeFooterView(footerView_loading);
							break;
						default:
							Toast.makeText(mContext.getApplicationContext(),  mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
					Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
					break;
				}
				}
				super.handleMessage(msg);
			}

		};
	
}

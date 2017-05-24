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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.weixue.Adapter.LessonAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants_Url;

/**
 * 热门推荐
 * @author Vam
 *
 */
public class HotRecoActivity extends Activity {
	/**设置数据*/
	private static final int SETDATA=0;
	private static final int MOREITEM = 1;
	private static final int SHOW_MESSAGE=2;
	private Context mContext;
	private ListView lv_hotReco;
	
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）
	private View footerView_loading;//加载更多时显示等待
	private LessonAdapter adapter;
	
public List<Course> li_course;//显示需要的数据
	
	public int pageIndex=1;
	public int pageCount=0;
	public int count=15;//每页显示的数量
	public int type=2;
	public int online=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_reco);
		init();
		
	}

	//初始化
	public void init(){
		mContext=this;
		lv_hotReco=(ListView) findViewById(R.id.lesson_list);
		lv_hotReco.setOnItemClickListener(onitemclicklistener);
		lv_hotReco.setOnScrollListener(new MyScrollListener());
		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null); 
		
		
		//focusableIntouchMode
		System.out.println("HotRecoActivity初始化....");
		getCourseArray(type,online,pageIndex,pageCount,SETDATA);
	}
	
	public void loadData(List<Course> li){
		adapter=new LessonAdapter(mContext,li);
		lv_hotReco.setAdapter(adapter);
	}
	/**
	 * get请求获取热门推荐
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
    	if((lv_hotReco.getLastVisiblePosition()+1)==lv_hotReco.getCount()&&lv_hotReco.getCount()>=count&&isNotDownLoad){
    		isNotDownLoad=false;
    		//System.out.println("正在加载更多数据...");
    		lv_hotReco.addFooterView(footerView_loading);
    		position=lv_hotReco.getCount();
    		//System.out.println("position-->"+position);	    		
    		lv_hotReco.setSelection(position);
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
		System.out.println("跳转中.....");
		Intent intent = new Intent();
		intent.putExtra("course", li_course.get(position));	
		intent.setClass(getApplicationContext(), Course_video_playbackActivity.class);
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
					lv_hotReco.addFooterView(footerView_loading);
					loadData(li_course);
					lv_hotReco.removeFooterView(footerView_loading);
					break;
					case 0:
//						Response response=ResolveJSON.JSON_To_Response(content);
//						Toast.makeText(getApplicationContext(),  response.getMessage(), Toast.LENGTH_SHORT).show();
					break;
					default:
						//Toast.makeText(getApplicationContext(),  mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
					lv_hotReco.removeFooterView(footerView_loading);
					adapter.notifyDataSetChanged();
					isNotDownLoad=true;
					break;
					case 0:
						Toast.makeText(mContext,mContext.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
						lv_hotReco.removeFooterView(footerView_loading);
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

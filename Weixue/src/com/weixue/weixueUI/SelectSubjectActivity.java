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
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Adapter.SelectSubjectAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.Subject;
import com.weixue.Utils.Constants_Url;

public class SelectSubjectActivity extends Activity{
	private static final int SETDATA = 0;

	private static final int MOREITEM = 1;

	private Context mContext;

	private TextView tv_title;
	private ListView lv_subject;
	private List<Subject> li_subject;
	private int mid=0;

	private int pageIndex=1;
	private int pageCount=0;
	private int Count=15;
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）

	private LinearLayout line_back;

	private SelectSubjectAdapter adapter;

	private LinearLayout line_loading;
	private View footerView_loading;//加载更多时显示等待
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.bottom_bar);
		this.setContentView(R.layout.lv_page);
		init();
	}
	private void init() {
		mContext=this;
		// TODO Auto-generated method stub
		tv_title=(TextView) findViewById(R.id.tv_title);
		tv_title.setText(mContext.getResources().getString(R.string.selectsubject));

		line_back=(LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);

		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null);
		line_loading=(LinearLayout) findViewById(R.id.line_loading);

		lv_subject=(ListView) findViewById(R.id.lv_thing);
		lv_subject.setOnItemClickListener(itemListener);
		lv_subject.setOnScrollListener(new MyScrollListener());

		mid=this.getIntent().getExtras().getInt("mid");
		getData(mid,pageIndex,SETDATA);
	}

	OnItemClickListener itemListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent i=new Intent(mContext,MoreCourseActivity.class);
			i.putExtra("sid", li_subject.get(arg2).getSubjectID());
			startActivity(i);
		}
	};

	OnClickListener listener=new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.line_back:				
				finish();
				break;
			}
		}};

		public void getData(final int mid,final int pageIndex,final int what){
			Runnable run=new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub										
					String requestUrl=Constants_Url.GETSUBJECT+"?mid="+mid+"&pageIndex="+pageIndex;			
					try {
						//首先获取所有科目
						String responseStr=NetWork.getData(requestUrl);								

						handler.sendMessage(handler.obtainMessage(what, responseStr));


						//					handler.sendMessage(handler.obtainMessage(what, model));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.networkerror)));
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
					if((lv_subject.getLastVisiblePosition()+1)==lv_subject.getCount()&&lv_subject.getCount()>=Count&&isNotDownLoad){
						isNotDownLoad=false;
						//System.out.println("正在加载更多数据...");
						lv_subject.addFooterView(footerView_loading);
						position=lv_subject.getCount();
						//System.out.println("position-->"+position);	    		
						lv_subject.setSelection(position);
						pageIndex+=1;
						getData(mid,pageIndex,MOREITEM);
					}

					break;
				}
				}
			}}	


		Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case SETDATA:
					String jsonStr=(String) msg.obj;
					try {
						switch(ResolveJSON.IsHasResult(jsonStr)){
						case 1:
							li_subject=ResolveJSON.JSON_TO_SubjectArray(jsonStr);						
							adapter=new SelectSubjectAdapter(mContext, li_subject);
							lv_subject.addFooterView(footerView_loading);
							lv_subject.setAdapter(adapter);
							lv_subject.removeFooterView(footerView_loading);
							line_loading.setVisibility(View.GONE);
							break;
						case 0:
							Toast.makeText(mContext, ResolveJSON.JSON_To_Response(jsonStr).getMessage(), Toast.LENGTH_SHORT).show();
							break;
						default:
							Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
					break;
				case MOREITEM:
					String content=(String) msg.obj;	
					try {
						switch(ResolveJSON.IsHasResult(content)){
						case 1:
							List<Subject> li=ResolveJSON.JSON_TO_SubjectArray(content);
							for(int i=0;i<li.size();i++){
								li_subject.add(li.get(i));
							}
							lv_subject.removeFooterView(footerView_loading);
							adapter.notifyDataSetChanged();
							isNotDownLoad=true;
							break;
						case 0:
							Toast.makeText(mContext,mContext.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
							lv_subject.removeFooterView(footerView_loading);
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
				super.handleMessage(msg);
			}

		};

}

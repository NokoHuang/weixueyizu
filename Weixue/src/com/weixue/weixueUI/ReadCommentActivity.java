package com.weixue.weixueUI;

import java.util.ArrayList;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.weixue.Adapter.ReadCommentAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Comment;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
/**
 * 
 * @author chenjunjie
 *
 */
public class ReadCommentActivity extends Activity{

	private static final int SETDATA = 0;

	private static final int SHOW_MESSAGE = 1;

	private static final int LOADING_GONE = 2;

	private static final int MOREITEM = 3;

	private static final int REMOVEFOOTERVIEW = 4;
	
	private Context mContext;

	private LinearLayout line_back;
	private ListView lv_thing;
	private ReadCommentAdapter adapter;
	private LinearLayout line_loading;
	private View footerView_loading;//加载更多时显示等待
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）
	
	private int pageIndex=1;
	private int pageCount=0;
	private int Count=15;//每页显示的数量
	private int sid=0;
	
	List<Comment> li;
	List<PersonInformation> li_Info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.lv_page);
		init();
	}
	
	//初始化
	private void init() {
		mContext=this;
		li=new ArrayList<Comment>();
		li_Info=new ArrayList<PersonInformation>();
		// TODO Auto-generated method stub
		line_back=(LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);
		
		lv_thing=(ListView) findViewById(R.id.lv_thing);
		lv_thing.setOnScrollListener(new MyScrollListener());
		
		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null);
		line_loading=(LinearLayout) findViewById(R.id.line_loading);
		
		Bundle b=this.getIntent().getExtras();
		sid=b.getInt("sid");
		loadData(sid,pageIndex,pageCount,SETDATA);
	}
	
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

	/**
	 * 加载数据
	 * @param sid
	 * @param pageIndex
	 * @param pageCount
	 * @param what
	 */
	private void loadData(final int sid,final int pageIndex,final int pageCount,final int what){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<Comment> li_com;
				List<PersonInformation> li_info=new ArrayList<PersonInformation>();;
				String url=Constants_Url.READCOMMENT+"?sid="+sid+"&pageIndex="+pageIndex+"&pageCount="+pageCount;
				System.out.println("READCOMMENT:"+url);
				try {
					String jsonStr=NetWork.getData(url);
					
					switch(ResolveJSON.IsHasResult(jsonStr)){
					case 1:
						li_com=ResolveJSON.JSON_TO_CommentArray(jsonStr);
						
						 //通过用户ID获取用户信息
						for(int i=0;i<li_com.size();i++){
							
							String personInfo_url=Constants_Url.PERSON_INFO+"?uid="+li_com.get(i).getUID();													
							String str=NetWork.getData(personInfo_url);
							
							PersonInformation personInfo=ResolveJSON.JSON_To_PersonInformation(str);
							li_info.add(personInfo);
						}
						
						List<Object> li_big=new ArrayList<Object>();
						li_big.add(li_com);
						li_big.add(li_info);
						handler.sendMessage(handler.obtainMessage(what, li_big));
						break;
					case 0:
						Response response=ResolveJSON.JSON_To_Response(jsonStr);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response.getMessage()));
						handler.sendEmptyMessage(LOADING_GONE);
						if(li.size()>0){
							handler.sendEmptyMessage(REMOVEFOOTERVIEW);
						}
						break;
						default:
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.error)));
							handler.sendEmptyMessage(LOADING_GONE);
							break;
					}
					//handler.sendMessage(handler.obtainMessage(what, response));
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
    	if((lv_thing.getLastVisiblePosition()+1)==lv_thing.getCount()&&lv_thing.getCount()>=Count&&isNotDownLoad){
    		isNotDownLoad=false;
    		//System.out.println("正在加载更多数据...");
    		lv_thing.addFooterView(footerView_loading);
    		position=lv_thing.getCount();
    		//System.out.println("position-->"+position);	    		
    		lv_thing.setSelection(position);
    		pageIndex+=1;
    		loadData(sid,pageIndex,pageCount,MOREITEM);
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
				List<Object> li_big=(List<Object>) msg.obj;
					
				li=(List<Comment>) li_big.get(0);
				li_Info=(List<PersonInformation>) li_big.get(1);
				
				adapter=new ReadCommentAdapter(mContext,li,li_Info);
				lv_thing.addFooterView(footerView_loading);
				lv_thing.setAdapter(adapter);
				lv_thing.removeFooterView(footerView_loading);
				line_loading.setVisibility(View.GONE);
				break;
			case SHOW_MESSAGE:
				String content=(String) msg.obj;
				Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
				break;
			case LOADING_GONE:
				line_loading.setVisibility(View.GONE);
				break;
			case MOREITEM:
				List<Object> li_big_more=(List<Object>) msg.obj;
				List<Comment> lis=(List<Comment>) li_big_more.get(0);
				for(int i=0;i<lis.size();i++){
					li.add(lis.get(i));
				}
				List<PersonInformation> li_info=(List<PersonInformation>) li_big_more.get(1);
				for(int j=0;j<li_info.size();j++){
					li_Info.add(li_info.get(j));
				}
				lv_thing.removeFooterView(footerView_loading);
				adapter.notifyDataSetChanged();
				isNotDownLoad=true;
				break;
			case REMOVEFOOTERVIEW:
				lv_thing.removeFooterView(footerView_loading);
				adapter.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
}

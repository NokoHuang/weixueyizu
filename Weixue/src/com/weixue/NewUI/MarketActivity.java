package com.weixue.NewUI;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.NewsAdapter;
import com.weixue.Adapter.ProAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.InforTypeModel;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

public class MarketActivity extends Activity {

	private static final int SETDATA = 0;

	private static final int MOREITEM = 1;

	private Context mContext;

	private TextView tv_title;
	private ListView lv_subject;
	private List<InforTypeModel> li_Type;

	private int pageIndex=1;
	private int pageCount=0;
	private int Count=15;
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）

	private LinearLayout line_back;

	private ProAdapter adapter;

	private LinearLayout line_loading;
	private View footerView_loading;//加载更多时显示等待

	private int typeid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_activity_market);

		init();
	}

	private void init() {
		mContext=this;
		typeid=getIntent().getIntExtra("typeid",0);

		tv_title=(TextView) findViewById(R.id.tv_title);

		line_back=(LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);

		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null);
		line_loading=(LinearLayout) findViewById(R.id.line_loading);

		lv_subject=(ListView) findViewById(R.id.lv_thing);
		lv_subject.setOnItemClickListener(itemListener);
		lv_subject.setOnScrollListener(new MyScrollListener());

		getData(typeid,pageIndex,SETDATA);
	}

	OnClickListener listener=new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.line_back:				
				finish();
				break;
			}
		}};

		OnItemClickListener itemListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InforTypeModel listModel=(InforTypeModel) parent.getAdapter().getItem(position);
				Intent intent=new Intent(MarketActivity.this,WebActivity.class);
				intent.putExtra("context",listModel.get_po_context());
				intent.putExtra("title", listModel.get_po_title());
				startActivity(intent);
			}
		};

		public void getData(final int typeid,final int pageIndex,final int what){
			Runnable run=new Runnable(){

				@Override
				public void run() {
					String requestUrl=Constants_Url.GET_INFOR_TYPE+"?typeid="+typeid+"&pageSize="+Count+"&pageIndex="+pageIndex;			
					try {
						String responseStr=NetWork.getData(requestUrl);								
						handler.sendMessage(handler.obtainMessage(what, responseStr));
					} catch (Exception e) {
					}
				}};
				new Thread(run).start();
		}

		Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case SETDATA:
					String jsonStr=(String) msg.obj;
					try {
						switch(ResolveJSON.IsHasResult(jsonStr)){
						case 1:
							li_Type=ResolveJSON.JSON_TO_Type(jsonStr);						
							adapter=new ProAdapter(mContext,li_Type); 
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
						e.printStackTrace();
					}				
					break;
				case MOREITEM:
					String content=(String) msg.obj;	
					try {
						switch(ResolveJSON.IsHasResult(content)){
						case 1:
							List<InforTypeModel> li=ResolveJSON.JSON_TO_Type(content);
							for(int i=0;i<li.size();i++){
								li_Type.add(li.get(i));
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
						e.printStackTrace();
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

		//监听滚动事件实现自动加载
		class MyScrollListener implements OnScrollListener{
			int position=0;
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
				{
					if((lv_subject.getLastVisiblePosition()+1)==lv_subject.getCount()&&lv_subject.getCount()>=Count&&isNotDownLoad){
						isNotDownLoad=false;
						lv_subject.addFooterView(footerView_loading);
						position=lv_subject.getCount();
						lv_subject.setSelection(position);
						pageIndex+=1;
						getData(typeid,pageIndex,MOREITEM);
					}
					break;
				}
				}
			}}
}

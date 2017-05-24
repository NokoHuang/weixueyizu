package com.weixue.NewUI;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Adapter.ChooseAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.ChooseModel;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.MoreActivity;
import com.weixue.weixueUI.PersonalCenterActivity;
import com.weixue.weixueUI.R;


public class ChooseActivity extends Activity {

	private static final int SETDATA = 0;

	private static final int MOREITEM = 1;

	private Context mContext;

	private ListView lv_subject;
	//private List<Subject> li_subject;
	private List<ChooseModel> li_Choose;

	private int pageIndex=1;
	private int pageCount=0;
	private int Count=15;
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）

	private ChooseAdapter adapter;

	private LinearLayout line_loading;
	private View footerView_loading;//加载更多时显示等待

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_activity_choose);
		init();
	}

	private void init() {
		mContext=this;
		
		// TODO Auto-generated method stub

		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null);
		line_loading=(LinearLayout) findViewById(R.id.line_loading);

		lv_subject=(ListView) findViewById(R.id.lv_thing);
		lv_subject.setOnItemClickListener(itemListener);
		lv_subject.setOnScrollListener(new MyScrollListener());

		getData(pageIndex,SETDATA);
	}

	public void GoBack(View v){
		onBackPressed();
	}

	OnItemClickListener itemListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
				long arg3) {
			
			new AlertDialog.Builder(ChooseActivity.this)
			.setMessage("是否选择"+li_Choose.get(arg2).get_subject_name()+"?")
			.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					SharedPreferences save_Profession=getSharedPreferences("profession_info", Context.MODE_PRIVATE);
					Editor editor=save_Profession.edit();
					editor.putString("my_profession", li_Choose.get(arg2).get_subject_name());
					editor.commit();
					if(MoreActivity.my_profession_text!=null){
						MoreActivity.my_profession_text.setText(li_Choose.get(arg2).get_subject_name());
					}
					Toast.makeText(getApplicationContext(), "你选择了"+li_Choose.get(arg2).get_subject_name(), Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			}).show();
		}
	};

		public void getData(final int pageIndex,final int what){
			Runnable run=new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub										
					String requestUrl=Constants_Url.GET_ALL_SUB+"?pageSize="+Count+"&pageIndex="+pageIndex;			
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
							li_Choose=ResolveJSON.JSON_TO_Choose(jsonStr);						
							adapter=new ChooseAdapter(mContext,li_Choose);
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
							List<ChooseModel> li=ResolveJSON.JSON_TO_Choose(content);
							for(int i=0;i<li.size();i++){
								li_Choose.add(li.get(i));
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
							getData(pageIndex,MOREITEM);
						}
						break;
					}
				}
			}
		}

}

package com.weixue.weixueUI;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.weixue.Adapter.SearchFriendAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.MyDialog.GetAboutus;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

public class SearchFriendActivity extends Activity{
	private static final int SHOW_MESSAGE = 0;//显示信息
	private static final int SETDATA = 1;//设置
	private static final int MOREITEM = 2;
	

	
	private Context mContext;
	// 返回按钮
	private LinearLayout line_back;		
	//用户名
	private EditText ed_personName;
	//数据
	private ListView lv_friendList;
	private SearchFriendAdapter adapter;
	private List<PersonInformation> li_person;
	
	private ImageView iv_soundRecording;//语音搜索
	
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）
	private View footerView_loading;//加载更多时显示等待
	
	private int pageIndex=1;
	private int count=15;
	private String searchContent="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.searchfriend_page);
		initUi();
		
	}
	//初始化控件
	private void initUi() {
		mContext=this;
		// TODO Auto-generated method stub
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(clickListener);
		
		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null); 
		
		ed_personName=(EditText) findViewById(R.id.ed_personName);
		ed_personName.addTextChangedListener(edit_watcher);
		
		lv_friendList=(ListView) findViewById(R.id.lv_friendList);
		lv_friendList.setOnScrollListener(new MyScrollListener());
		lv_friendList.setOnItemClickListener(itemListener);
		
		iv_soundRecording=(ImageView) findViewById(R.id.iv_soundRecording);
		iv_soundRecording.setOnClickListener(clickListener);
		
	}
	OnItemClickListener itemListener=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent i=new Intent(mContext,AddFriendActivity.class);
			i.putExtra("fuid", li_person.get(arg2).getUserId());
			startActivity(i);
		}};
	
	//文本录入监听
		TextWatcher edit_watcher=new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				  
			}

			@Override
			public void afterTextChanged(Editable s) {
				//System.out.println("字符改变-->"+String.valueOf(s));	
				pageIndex=1;
				searchContent=String.valueOf(s);
				if(searchContent!=""&&searchContent.length()>0){
					searchFriend(searchContent,pageIndex,SETDATA);
				}else{
					li_person=new ArrayList<PersonInformation>();
					if(adapter!=null){
					adapter.updateList(li_person);	
					}
				}
				
			 }
			};

	//点击监听
	OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.line_back:
				finish();
				break;
			case R.id.iv_soundRecording:
				PackageManager pm = getPackageManager();
		        List<ResolveInfo> activities = pm.queryIntentActivities(
		                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		        if (activities.size() != 0) {
		        	startVoiceRecognitionActivity();
		        } else {			            
		            Log.e("TestActivity", "Recognizer not present");
		        }
				break;
			}
		}
	};
	
	/**
	 * 启动语音识别activity，接收用户语音输入
	 */	
	private void startVoiceRecognitionActivity() {
		// 通过Intent传递语音识别的模式
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// 语言模式：自由形式的语音识别
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// 提示语音开始
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, Constants.SPEECH_PROMPT);
		// 开始执行我们的Intent、语音识别 并等待返回结果
		startActivityForResult(intent, Constants.VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		// 确定是语音识别activity返回的结果
		if (requestCode == Constants.VOICE_RECOGNITION_REQUEST_CODE) {
			// 确定返回结果的状态是成功
			if (resultCode == RESULT_OK) {
				// 获取语音识别结果
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if(matches.size() >0){					
					ed_personName.setText(matches.get(0));
				}
				
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	/**
	 * 搜索好友
	 * @param condition
	 * @param pageIndex
	 */
	private void searchFriend(final String condition,final int pageIndex,final int what){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.SEARCHFRIEND+"?condition="+condition+"&pageIndex="+pageIndex;
			
				try {
					String responseStr=NetWork.getData(requestUrl);
					
					handler.sendMessage(handler.obtainMessage(what, responseStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,getApplicationContext().getResources().getString(R.string.networkerror)));
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
			    	if((lv_friendList.getLastVisiblePosition()+1)==lv_friendList.getCount()&&lv_friendList.getCount()>=count&&isNotDownLoad){
			    		isNotDownLoad=false;
			    		//System.out.println("正在加载更多数据...");
			    		lv_friendList.addFooterView(footerView_loading);
			    		position=lv_friendList.getCount();
			    		//System.out.println("position-->"+position);	    		
			    		lv_friendList.setSelection(position);
			    		pageIndex+=1;
			    		searchFriend(searchContent,pageIndex,MOREITEM);
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
			case SHOW_MESSAGE:
				String content=(String) msg.obj;
				Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
				break;
			case SETDATA:
				String responseStr=(String) msg.obj;
				try {
					switch(ResolveJSON.IsHasResult(responseStr)){
					case 1:
						li_person=ResolveJSON.JSON_TO_PersonInformationArray(responseStr);
						if(li_person!=null&&li_person.size()>0){
							
						adapter=new SearchFriendAdapter(mContext, li_person);
						lv_friendList.addFooterView(footerView_loading);
						lv_friendList.setAdapter(adapter);
						lv_friendList.removeFooterView(footerView_loading);
						
						}
						break;
					case 0:
						List<PersonInformation> lis=new ArrayList<PersonInformation>();
						adapter=new SearchFriendAdapter(mContext, lis);
						lv_friendList.setAdapter(adapter);
						Response response=ResolveJSON.JSON_To_Response(responseStr);
						Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
						
						break;
					
						default:
							Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
							break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case MOREITEM:
				String moreContent=(String) msg.obj;
				
				try {
					switch(ResolveJSON.IsHasResult(moreContent)){
					case 1:
					List<PersonInformation> li=ResolveJSON.JSON_TO_PersonInformationArray(moreContent);
					for(int i=0;i<li.size();i++){
						li_person.add(li.get(i));
					}
					lv_friendList.removeFooterView(footerView_loading);
					adapter.updateList(li_person);
					isNotDownLoad=true;
					break;
					case 0:
						Toast.makeText(mContext, mContext.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
						lv_friendList.removeFooterView(footerView_loading);
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

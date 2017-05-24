package com.weixue.weixueUI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Adapter.SearchAdapter;
import com.weixue.Control.KeywordsFlowView;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.Response;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 搜索界面
 * @author  chenjunjie
 *
 */
public class SearchCourseActivity extends Activity {

	private static final int SETDATA = 0;
	private static final int MOREITEM = 1;
	private static final int UPDATEWORD = 2;
	private static final int SHOW_MESSAGE=3;
	private static final int DEFAULTCOURSE=4;//初始显示的课程名
	

	
	
	private Context mContext;
	private ListView lv_video;
	private EditText edit_search;

	
	
	private LinearLayout line_back;//返回
	private ImageView iv_soundRecording;//语音搜索
	
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）
	private View footerView_loading;//加载更多时显示等待
	
	private List<Course> li_course;
	public int pageIndex=1;
	public int pageCount=0;
	public int count=15;//每页显示的数量
	public int type=1;
	public int online=0;
	public int pageIndexForDefaultCourse=1;
	
	private String searchContent="";
	private SearchAdapter adapter;
	
	private KeywordsFlowView keywordsFlow;
	private  List<String> keywords ;
	private List<Course>  li;//飞入飞出显示的课程(名称)

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_page);
		init();
		loadFirstData(type,online,pageIndexForDefaultCourse,pageCount,DEFAULTCOURSE);
	}

	//初始化控件
	public void init(){
		mContext=this;
		lv_video =(ListView) findViewById(R.id.lesson_list);
		lv_video.setOnScrollListener(new MyScrollListener());
		lv_video.setOnItemClickListener(onitemclicklistener);
		edit_search=(EditText) findViewById(R.id.search);
		edit_search.addTextChangedListener(edit_watcher);
		
		line_back=(LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(listener);
		
		iv_soundRecording=(ImageView) findViewById(R.id.iv_soundRecording);
		iv_soundRecording.setOnClickListener(listener);
		
		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null); 
		//list_video.setAdapter(new SearchAdapter(getApplicationContext()));
		
		keywords=new ArrayList<String>();
	
			keywords.add(getApplicationContext().getResources().getString(R.string.app_name));
		
		
		
		keywordsFlow = (KeywordsFlowView) findViewById(R.id.keywordsFlow);
		keywordsFlow.setDuration(4000);
		keywordsFlow.setOnItemClickListener(listenerForkeywordsFlow);
		// 添加
		feedKeywordsFlowForList(keywordsFlow, keywords);
		keywordsFlow.go2Show(KeywordsFlowView.ANIMATION_IN);
		autoplay();
				
	}
	
	private void loadFirstData( int type, int online, int pageIndex, int pageCount, int what){
		getCourseArray(type,online,pageIndex,pageCount,what);
	}
	
	//飞入飞出字体监听
	OnClickListener listenerForkeywordsFlow=new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 if(v instanceof TextView){
					//System.out.println("点击文本-->"+((TextView)v).getText());
					String clickContent=String.valueOf(((TextView)v).getText());
					//String keyword = ((TextView) v).getText().toString();
					for(int i=0;i<li.size();i++){
						if(li.get(i).getCourseName().equals(clickContent)){
							Intent intent=new Intent(mContext,Course_video_playbackActivity.class);
							intent.putExtra("course", li.get(i));
							startActivity(intent);
						}
					}
					
				}
		}};

	
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
	
	//随机选择需要显示的文字
	private void feedKeywordsFlowForList(KeywordsFlowView keywordsFlow,
			List<String> arr) {
		// TODO Auto-generated method stub
		Random random = new Random();
		for (int i = 0; i < KeywordsFlowView.MAX; i++) {
			int ran = random.nextInt(arr.size());
			String tmp = arr.get(ran);
			keywordsFlow.feedKeyword(tmp);
		}
	}


	
	//自动换文字
	public void autoplay() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {					
					handler.sendEmptyMessage(UPDATEWORD);	
					autoplay();
			}
		};
		
		handler.postDelayed(runnable, 7000);
		
	}
	
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
					edit_search.setText(matches.get(0));
				}
				
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
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
			 loadData(searchContent,1,SETDATA);
			}else{
				li_course=new ArrayList<Course>();
				if(adapter!=null){
				adapter.updateList(li_course);
				}
				keywordsFlow.setVisibility(View.VISIBLE);
			}
			
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
		    	if((lv_video.getLastVisiblePosition()+1)==lv_video.getCount()&&lv_video.getCount()>=count&&isNotDownLoad){
		    		isNotDownLoad=false;
		    		//System.out.println("正在加载更多数据...");
		    		lv_video.addFooterView(footerView_loading);
		    		position=lv_video.getCount();
		    		//System.out.println("position-->"+position);	    		
		    		lv_video.setSelection(position);
		    		pageIndex+=1;
		    		loadData(searchContent,pageIndex,MOREITEM);
		    	}
					
		    	break;
		    }
		  }
		}}
		
		
		//监听
		OnClickListener listener=new OnClickListener(){

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
			}};
		
		
		/**
		 * 添加子监听
		 */
		OnItemClickListener onitemclicklistener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0,
				View arg1, int position, long arg3) {
			
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), Course_video_playbackActivity.class);
			intent.putExtra("course", li_course.get(position));
			startActivity(intent);
			
			}
		} ;
		
		/**
		 * 加载数据
		 * @param content
		 * @param pageIndex
		 */
		private void loadData(final String content,final int pageIndex,final int what) {
			// TODO Auto-generated method stub
			Runnable run=new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String str="";
					try {
						str=URLEncoder.encode(content, "utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String url=Constants_Url.SEARCHCOURSE+"?condition="+str+"&pageIndex="+pageIndex;
					
				//	System.out.println(url);
					try {
						String response=NetWork.getData(url);
						handler.sendMessage(handler.obtainMessage(what, response));
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
				case SETDATA:
					String jsonStr=(String) msg.obj;
					
					try {
						switch(ResolveJSON.IsHasResult(jsonStr)){
						case 1:
							li_course=ResolveJSON.JSON_TO_CourseArray(jsonStr);
							if(li_course!=null&&li_course.size()>0){
								
							adapter=new SearchAdapter(mContext, li_course);
							lv_video.addFooterView(footerView_loading);
							lv_video.setAdapter(adapter);
							lv_video.removeFooterView(footerView_loading);
							keywordsFlow.setVisibility(View.GONE);
							}
							break;
						case 0:
							List<Course> lis=new ArrayList<Course>();
							adapter=new SearchAdapter(mContext, lis);
							lv_video.setAdapter(adapter);
							Response response=ResolveJSON.JSON_To_Response(jsonStr);
							Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
							keywordsFlow.setVisibility(View.VISIBLE);
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
					String content=(String) msg.obj;
					
					try {
						switch(ResolveJSON.IsHasResult(content)){
						case 1:
						List<Course> li=ResolveJSON.JSON_TO_CourseArray(content);
						for(int i=0;i<li.size();i++){
							li_course.add(li.get(i));
						}
						lv_video.removeFooterView(footerView_loading);
						adapter.notifyDataSetChanged();
						isNotDownLoad=true;
						break;
						case 0:
							Toast.makeText(mContext, mContext.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
							lv_video.removeFooterView(footerView_loading);
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
				case UPDATEWORD:
					keywordsFlow.rubKeywords();
					keywordsFlow.rubAllViews();
					feedKeywordsFlowForList(keywordsFlow,  keywords);
					keywordsFlow.go2Show(KeywordsFlowView.ANIMATION_OUT);				
//					autoplay();
					break;
				case SHOW_MESSAGE:
					String showContent=(String) msg.obj;
					Toast.makeText(getApplicationContext(), showContent, Toast.LENGTH_SHORT).show();
					break;
				case DEFAULTCOURSE:
                        String DefaultjsonStr=(String) msg.obj;					
					try {
						switch(ResolveJSON.IsHasResult(DefaultjsonStr)){
						case 1:
						li=ResolveJSON.JSON_TO_CourseArray(DefaultjsonStr);
							if(li!=null&&li.size()>0){
							keywords=new ArrayList<String>();
							for(int i=0;i<li.size();i++){
								keywords.add(li.get(i).getCourseName());
							 }
							}
							break;
						default:								
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

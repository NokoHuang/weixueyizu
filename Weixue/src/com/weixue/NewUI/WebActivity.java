package com.weixue.NewUI;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commonality.CommonalityDataInterface;
import com.example.commonality.CommonalityInformation;
import com.example.commonality.CommonalityMethod;
import com.example.model.NewsModel;
import com.example.newspagedemo.R;
import com.example.util.HttpUtil;

public class WebActivity extends Activity{
	//获取数据
	private final int GET_DATA=0X123;
	//网络连接超时
	private final int CONNECTION_NETWORK_TIMEOUT=0X124;
	private String id,encoding,pageType;
	private LinearLayout news_content_loading_layout;
	private Handler handler;
	private String getString;
	private TextView display_News_Title;
	private WebView mWvContent;
	private ProgressBar progressBar;
	private Button refresh_Button;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_page);
		Init();
	}
	
	/**
	 *将获取的新闻数据加载出来 
	 */
	public void Load_Data(){
		//加载进度框
		news_content_loading_layout.setVisibility(View.GONE);
		mWvContent.setVisibility(View.VISIBLE);
		
		display_News_Title.setText(getIntent().getStringExtra("title"));
		String content=getIntent().getStringExtra("context");
		content=content.replaceAll("/ckfinder/userfiles/", CommonalityDataInterface.BaseUrl + "/ckfinder/userfiles/");
		content="<style>img{width:"
				+ (CommonalityMethod.px2dip(this, CommonalityInformation.mScreenWidth) - 20)
				+ "px!important;height:auto!important}</style>" + content;
		
		mWvContent.loadDataWithBaseURL(null, content, pageType, encoding, null);
	}
	
	/**
	 *初始化控件实例 
	 */
	public void Init(){
		//获取新闻内容布局实例
		//news_content_ScrollView_layout=(ScrollView)findViewById(R.id.news_content_ScrollView_layout);
		//获取新闻加载布局实例
		news_content_loading_layout=(LinearLayout)findViewById(R.id.news_content_loading_layout);
		display_News_Title=(TextView)findViewById(R.id.display_news_title);
		mWvContent=(WebView)findViewById(R.id.mWvContent);
		
		encoding="utf-8";
		pageType="text/html";
		
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		refresh_Button=(Button)findViewById(R.id.refresh_button);
		Load_Data();
	}
	
	public void GoBack(View v){
		onBackPressed();
	}
}

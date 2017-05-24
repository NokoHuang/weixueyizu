package com.example.newspagedemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
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
import com.example.util.HttpUtil;

import org.json.JSONObject;

public class NewsPageActivity extends Activity {
	// 获取数据
	private final int GET_DATA = 0X123;
	// 网络连接超时
	private final int CONNECTION_NETWORK_TIMEOUT = 0X124;
	private String id, encoding, pageType;
	private LinearLayout news_content_loading_layout;
	private Handler handler;
	private String getString;
	private NewsModel model;
	private TextView display_News_Title;
	private WebView mWvContent;
	private ProgressBar progressBar;
	private Button refresh_Button;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_page);

		Init();
		// 检查网络连接
		CheckNetWork();

		refresh_Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CheckNetWork();
			}
		});

		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == GET_DATA) {
					Load_Data();
				}

				if (msg.what == CONNECTION_NETWORK_TIMEOUT) {
					mWvContent.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					refresh_Button.setVisibility(View.VISIBLE);
					Toast.makeText(getApplicationContext(), "网络连接超时！请重新连接",
							Toast.LENGTH_LONG).show();
				}
			}
		};

	}

	public void CheckNetWork() {
		if (CommonalityMethod.isNetworkConnected(this)) {
			// 显示进度框，隐藏刷新按钮
			progressBar.setVisibility(View.VISIBLE);
			refresh_Button.setVisibility(View.GONE);
			GetData();
		} else {
			// 显示刷新按钮，隐藏进度框
			progressBar.setVisibility(View.GONE);
			refresh_Button.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "请检查网络连接!",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 将获取的新闻数据加载出来
	 */
	public void Load_Data() {
		// 加载进度框
		news_content_loading_layout.setVisibility(View.GONE);
		mWvContent.setVisibility(View.VISIBLE);

		display_News_Title.setText(model.getTitle());
		String content = model.getContent();
		content = content.replaceAll("/ckfinder/userfiles/",
				CommonalityDataInterface.BaseUrl + "/ckfinder/userfiles/");
		content = "<style>img{width:"
				+ (CommonalityMethod.px2dip(this,
				CommonalityInformation.mScreenWidth) - 20)
				+ "px!important;height:auto!important}</style>" + content;

		mWvContent.loadDataWithBaseURL(null, content, pageType, encoding, null);
	}

	/**
	 *
	 * 获取的新闻数据
	 */
	public void GetData() {
		new Thread() {
			public void run() {
				try {
					getString = HttpUtil
							.sendRequestString(CommonalityDataInterface.BaseUrl
									+ CommonalityDataInterface.getNewsInformationById
									+ "?id=" + id + "");
					ParseNewsData(getString);

					handler.sendEmptyMessage(GET_DATA);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(CONNECTION_NETWORK_TIMEOUT);
				}
			}
		}.start();
	}

	/**
	 * 解析新闻数据
	 *
	 * @param getString
	 */
	// http://222.126.246.151:9120/app/InformationServices/Information/GetNewsInformationById?id=1
	public void ParseNewsData(String getString) throws Exception {
		JSONObject json = new JSONObject(getString);
		JSONObject obj = json.getJSONObject("Result");
		model.setBitmap(CommonalityMethod.getBitmap(obj.getString("_ne_image")));
		model.setTitle(obj.optString("_ne_title"));
		model.setContent(obj.getString("_ne_content"));
		Log.e("newsContent", obj.getString("_ne_content"));
		String temp = dataSub(obj.getString("_ne_datetime"));
		model.setDate_time(CommonalityMethod.getDateString(Long.valueOf(temp)));
	}

	public String dataSub(String dateString) {
		return dateString.substring(dateString.indexOf('(', 0) + 1,
				dateString.indexOf('+', 0));
	}

	/**
	 * 初始化控件实例
	 */
	public void Init() {

		// 获取新闻内容布局实例
		// news_content_ScrollView_layout=(ScrollView)findViewById(R.id.news_content_ScrollView_layout);
		// 获取新闻加载布局实例
		news_content_loading_layout = (LinearLayout) findViewById(R.id.news_content_loading_layout);

		display_News_Title = (TextView) findViewById(R.id.display_news_title);
		// display_news_content=(TextView)findViewById(R.id.display_news_content);
		// display_news_date_time=(TextView)findViewById(R.id.display_news_date_time);

		id = getIntent().getStringExtra("id");
		Log.e("我的id", id);
		model = new NewsModel();

		mWvContent = (WebView) findViewById(R.id.mWvContent);

		encoding = "utf-8";
		pageType = "text/html";

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		refresh_Button = (Button) findViewById(R.id.refresh_button);

		// image_Layout=(LinearLayout)findViewById(R.id.imageview_layout);
	}

	public void GoBack(View v) {
		onBackPressed();
	}
}

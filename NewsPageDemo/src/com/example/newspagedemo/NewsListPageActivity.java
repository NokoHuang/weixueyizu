package com.example.newspagedemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.adapter.NewsAdapter;
import com.example.commonality.CommonalityDataInterface;
import com.example.commonality.CommonalityMethod;
import com.example.model.NewsListModel;
import com.example.util.HttpUtil;
import com.eyoung.xlistview.XListView;
import com.eyoung.xlistview.XListView.IXListViewListener;

public class NewsListPageActivity extends Activity implements
		IXListViewListener {
	// 连接网络超时
	private final int CONNECTION_NETWORK_TIMEOUT = 0X124;
	// 获取新闻数据
	private final int GET_NEWS_DATA = 0x123;
	private XListView newsList;
	private String NavTitleID;
	private Handler handler;
	private String getDataString;
	private ArrayList<NewsListModel> newsArray;
	private Intent intent;
	// private RefreshableView refresh;
	private LinearLayout news_LinearLayout;
	private ProgressBar progressBar;
	private Button refresh_button;

	private static final int TASK_NORMAL = 0;
	private static final int TASK_REFRESH = 1;
	private static final int TASK_LOADMORE = 2;

	// 评论页数
	private int pageIndex = 1;

	private NewsAdapter adapter;

	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list_page);

		Init();
		// 检查网络连接
		CheckNetWork(TASK_NORMAL);

		// 单击列表事件
		newsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				NewsListModel listModel = (NewsListModel) parent.getAdapter()
						.getItem(position);
				intent = new Intent(NewsListPageActivity.this,
						NewsPageActivity.class);
				intent.putExtra("id", String.valueOf(listModel.getId()));
				System.out.println("position:" + position + "  id:"
						+ listModel.getId());
				startActivity(intent);
			}
		});

		// 下拉列表刷新
		/*
		 * refresh.setOnRefreshListener(new PullToRefreshListener() {
		 *
		 * @Override public void onRefresh() { // TODO Auto-generated method
		 * stub try { newsArray=new ArrayList<NewsListModel>(); CheckNetWork();
		 * } catch (Exception e) { e.printStackTrace(); }
		 * refresh.finishRefreshing(); } }, 0);
		 */

		handler = new Handler() {
			public void handleMessage(Message msg) {

				// if (msg.what == GET_NEWS_DATA) {
				// news_LinearLayout.setVisibility(View.GONE);
				//
				// LoadData();
				// }

				switch (msg.what) {
					case TASK_NORMAL:
						news_LinearLayout.setVisibility(View.GONE);
						LoadData((ArrayList<NewsListModel>) msg.obj);
						break;
					case TASK_REFRESH:
						news_LinearLayout.setVisibility(View.GONE);
						LoadDataREFRESH((ArrayList<NewsListModel>) msg.obj);
						break;
					case TASK_LOADMORE:
						news_LinearLayout.setVisibility(View.GONE);
						LoadDataLOADMORE((ArrayList<NewsListModel>) msg.obj);
						break;
				}
			}
		};
	}

	@Override
	public void onRefresh() {
		CheckNetWork(TASK_REFRESH);
	}

	@Override
	public void onLoadMore() {
		CheckNetWork(TASK_LOADMORE);
	}

	public void CheckNetWork(int TASKID) {
		if (CommonalityMethod.isNetworkConnected(NewsListPageActivity.this)) {
			GetData(TASKID);
		} else {
			progressBar.setVisibility(View.GONE);
			refresh_button.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "请检查网络连接",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 加载数据
	 */
	public void LoadData(ArrayList<NewsListModel> newsArray) {
		if (newsArray != null && newsArray.size() > 0) {
			adapter = new NewsAdapter(getLayoutInflater(), newsArray);
			newsList.setAdapter(adapter);
		} else {
			newsList.setPullRefreshEnable(false);
		}

		if (newsArray != null && newsArray.size() < 20) {
			newsList.setPullLoadEnable(false);
		}
	}

	public void LoadDataREFRESH(ArrayList<NewsListModel> newsArray) {
		adapter = new NewsAdapter(getLayoutInflater(), newsArray);
		newsList.setAdapter(adapter);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sd = sdf.format(new Date());
		newsList.setRefreshTime(sd + "");
		newsList.stopRefresh();

		if (newsArray.size() < 20) {
			newsList.setPullLoadEnable(false);
		}
	}

	public void LoadDataLOADMORE(ArrayList<NewsListModel> newsArray) {
		if (newsArray.size() > 0) {
			adapter.addAll(newsArray);
			newsList.stopLoadMore();
		} else {
			Toast.makeText(this, "已经为最后一条", Toast.LENGTH_LONG).show();
			newsList.setPullLoadEnable(false);
		}
	}

	/**
	 * 获取服务器数据
	 */
	public void GetData(final int TASKID) {
		switch (TASKID) {
			case TASK_NORMAL:
				break;
			case TASK_REFRESH:
				pageIndex = 1;
				break;
			case TASK_LOADMORE:
				++pageIndex;
				break;
		}
		new Thread() {
			public void run() {
				try {
					getDataString = HttpUtil
							.sendRequestString(CommonalityDataInterface.BaseUrl
									+ CommonalityDataInterface.getNewsInformationListByTypeId
									+ "?typeid=" + NavTitleID
									+ "&pageSize=20&pageIndex=" + pageIndex);

					Log.e(NavTitleID, getDataString);
					// http://222.126.246.151:9120/app/InformationServices/Information/GetNewsInformationListByTypeId

					Message msg = new Message();// 获取设置一个信息保存点
					msg.what = TASKID;
					ArrayList<NewsListModel> list = ParseNewsListData(getDataString);
					msg.obj = list;
					handler.sendMessage(msg);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 解析列表数据
	 *
	 * @param getDataString
	 */
	public ArrayList<NewsListModel> ParseNewsListData(String getDataString)
			throws Exception {
		ArrayList<NewsListModel> arrayList = new ArrayList<NewsListModel>();
		try {// http://222.126.246.151:9120/app/InformationServices/Information/GetNewsInformationListByTypeId?typeid=1&pageSize=20&pageIndex=1
			JSONObject json = new JSONObject(getDataString);
			JSONArray array = json.getJSONArray("Result");

			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject) array.opt(i);
				Log.e("解析" + i, obj.getString("_ne_content"));
				NewsListModel model = new NewsListModel();
				try {
					Bitmap bitmap = CommonalityMethod.getBitmap(obj
							.getString("_ne_image"));
					if (bitmap == null)
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.image);

					model.setBitmap(bitmap);
					model.setId(String.valueOf(obj.getInt("_ne_id")));
					model.setTitle(obj.getString("_ne_title"));
					String temp = dataSub(obj.getString("_ne_datetime"));
					model.setDate_time(CommonalityMethod.getDateString(Long
							.valueOf(temp)));
					arrayList.add(model);
				} catch (Exception e) {
					model.setBitmap(null);
				}
			}
			return arrayList;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param dateString
	 * @return
	 */
	public String dataSub(String dateString) {
		return dateString.substring(dateString.indexOf('(', 0) + 1,
				dateString.indexOf('+', 0));
	}

	public void Init() {
		NavTitleID = getIntent().getStringExtra("NavTitleID");
		Log.e("标题ID", "标题ID" + NavTitleID);
		newsList = (XListView) findViewById(R.id.newsList);
		newsList.setPullRefreshEnable(true);
		newsList.setPullLoadEnable(true);
		newsList.setXListViewListener(this);

		newsArray = new ArrayList<NewsListModel>();
		// refresh=(RefreshableView)findViewById(R.id.refresh);
		news_LinearLayout = (LinearLayout) findViewById(R.id.news_list_loading_layout);

		refresh_button = (Button) findViewById(R.id.refresh_button);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

}

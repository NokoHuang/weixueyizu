package com.weixue.weixueUI;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weixue.Function.NetWork;
import com.weixue.Model.CarriesItemModel;
import com.weixue.Tool.PullToRefreshView;
import com.weixue.Tool.PullToRefreshView.OnFooterRefreshListener;
import com.weixue.Tool.PullToRefreshView.OnHeaderRefreshListener;
import com.weixue.Tool.ThreadPool;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 试卷列表
 * 
 * @author zeda
 * 
 */
public class CarriesListActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener {

	private LinearLayout mList;
	private PullToRefreshView mRefresh;
	private ImageButton mBack;
	private TextView mTvTitle;

	private String title;
	private int id;
	private int userId;
	private String baseUrl;

	private Activity mContext;
	private LayoutInflater mInflater;
	private ExecutorService pool;// 线程池

	private int pageIndex = 1;
	private int savePageIndex = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_carries_list);
		mContext = this;
		mInflater = getLayoutInflater();
		pool = ThreadPool.getInstance();
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		id = intent.getIntExtra("mid", 1);

		userId = intent.getIntExtra("UserId", 1);

		id = 1;// 临时
		baseUrl = Constants_Url.CARRIES_LIST + id + "&userId=" + userId
				+ "&pageSize=10" + "&pageIndex=";
		init();
	}

	private void init() {
		bindView();
		mRefresh.headerRefreshing(false);
		refresh();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mRefresh.onHeaderRefreshComplete();
			mRefresh.onFooterRefreshComplete();
			if (msg.what == 1) {
				updateView((ArrayList<CarriesItemModel>) msg.getData()
						.getSerializable("datas"), msg.arg1);
			} else if (msg.what == 2) {
				pageIndex = savePageIndex;
				Constants.showMessage(mContext, "没有数据");
			} else if (msg.what == 5) {
				pageIndex = savePageIndex;
				Constants.showMessageByNetExpetion(mContext);
			}
		}
	};

	private void refresh() {
		if (NetWork.hasNetWorkConnect(mContext)) {
			savePageIndex = pageIndex;
			pageIndex = 1;
			loadData(baseUrl + pageIndex, Constants.REFRESH_STATE);
		} else {
			mRefresh.onHeaderRefreshComplete();
			Constants.showMessageByNoNet(mContext);
		}

	}

	private void addPage() {
		if (NetWork.hasNetWorkConnect(mContext)) {
			savePageIndex = pageIndex;
			pageIndex++;
			loadData(baseUrl + pageIndex, Constants.ADD_STATE);
		} else {
			mRefresh.onFooterRefreshComplete();
			Constants.showMessageByNoNet(mContext);
		}

	}

	/**
	 * 更新数据到控件
	 */
	private void updateView(ArrayList<CarriesItemModel> datas, int state) {
		if (state == Constants.REFRESH_STATE) {
			mList.removeAllViews();
			mRefresh.updateDate();
		}
		for (int i = 0; i < datas.size(); i++) {
			CarriesItemModel data = datas.get(i);
			View view = mInflater.inflate(R.layout.carries_item, null);
			TextView mTvTitle = (TextView) view.findViewById(R.id.title);
			mTvTitle.setText(data.getTitle());
			TextView mTvTime = (TextView) view.findViewById(R.id.time);
			mTvTime.setText("考试时间：" + data.getTime() + "分钟");
			TextView mTvMaxScore = (TextView) view.findViewById(R.id.maxscore);
			mTvMaxScore.setText("满分：" + data.getMaxScore());
			view.setOnClickListener(new OnCarriesItemClickListener(data));
			mList.addView(view);
		}
	}

	private void loadData(final String url, final int state) {
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String result = NetWork.getData(url);
					// System.out.println(url + result);
					JSONObject json = new JSONObject(result);
					JSONArray array = json.getJSONArray("Result");
					ArrayList<CarriesItemModel> datas = new ArrayList<CarriesItemModel>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.opt(i);
						CarriesItemModel data = new CarriesItemModel();
						data.setId(obj.getInt("_te_id"));
						data.setTitle(obj.getString("_te_title"));
						data.setMaxScore(obj.getInt("_te_totalscore"));
						data.setTime(obj.getInt("_te_totaltime"));
						data.setPassScore(obj.getInt("_te_passingscore"));
						data.setQuestionNum(obj.getInt("_te_sparetwo"));
						data.setTotalNum(obj.getInt("_te_spareone"));
						data.setPass(obj.getInt("_te_sparethree"));
						datas.add(data);
					}
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("datas", datas);
					msg.what = 1;
					msg.arg1 = state;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (JSONException e) {
					handler.sendEmptyMessage(2);
					e.printStackTrace();
				} catch (Exception e) {
					handler.sendEmptyMessage(5);
					e.printStackTrace();
				}
			}
		});
	}

	private void bindView() {
		mBack = (ImageButton) findViewById(R.id.back);
		mRefresh = (PullToRefreshView) findViewById(R.id.refresh);
		mList = (LinearLayout) findViewById(R.id.list);
		mTvTitle = (TextView) findViewById(R.id.title);
		Log.e("我的测试", title);
		if(title.length()>=10){
			title=title.substring(0, 10);
			title+="......";
		}
		mTvTitle.setText(title);

		mBack.setOnClickListener(this);
		mRefresh.setOnHeaderRefreshListener(this);
		mRefresh.setOnFooterRefreshListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == -1006) {// 考试完毕后返回
			mRefresh.headerRefreshing(false);
			refresh();
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		addPage();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		refresh();
	}

	@Override
	public void onClick(View v) {
		if (v == mBack) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	class OnCarriesItemClickListener implements OnClickListener {

		private CarriesItemModel data;

		public OnCarriesItemClickListener(CarriesItemModel data) {
			this.data = data;
		}

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent();
			intent.setClass(mContext, CarriesInfoActivity.class);
			intent.putExtra("data", data);
			startActivityForResult(intent, 1006);
		}
	}

}

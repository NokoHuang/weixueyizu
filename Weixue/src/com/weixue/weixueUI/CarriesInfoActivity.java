package com.weixue.weixueUI;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.AnswerModel;
import com.weixue.Model.CarriesItemModel;
import com.weixue.Model.CarriesQueModel;
import com.weixue.Model.PersonInformation;
import com.weixue.Tool.ImageDealTool;
import com.weixue.Tool.ImageLoader;
import com.weixue.Tool.ThreadPool;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 试卷详情
 * 
 * @author zeda
 * 
 */
public class CarriesInfoActivity extends Activity implements OnClickListener {

	public static CarriesItemModel data;
	private CarriesItemModel saveData;
	private int id;
	private ImageView mHead;
	private TextView mTvTitle, mTvUserName, mTvName, mTvQueNum, mTvTotalNum,
			mTvScore, mTvTime;
	private ImageButton mBtnBack;
	private Button mBtnTotal, mBtnCheckAnswer;
	private LinearLayout mContent;

	private Activity mContext;

	private ExecutorService pool;

	private String checkUrl;// 检查今天是否能考试接口

	private int isPass = 0;

	private int userId;

	// 用户信息
	private PersonInformation person;

	private ImageLoader mLoader;

	private String typeStrs[] = { "", "一、单选题", "二、多选题", "三、简答题", "四、计算题" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_carries_info);
		saveData = (CarriesItemModel) getIntent().getSerializableExtra("data");
		mContext = this;
		mLoader = new ImageLoader(mContext);
		pool = ThreadPool.getInstance();
		id = saveData.getId();
		isPass = saveData.getPass();
		person = ResolveJSON.JSON_To_PersonInformation(MyInstance
				.getSharedPreferencesString(mContext,
						Constants.PERSON_INFORMATION));
		userId = person.getUserId();
		checkUrl = Constants_Url.IS_CAN_TOTAL + userId + "&testId=" + id;
		init();
	}

	private void init() {
		bindView();
		initData();
		// loadTotalNum();
	}

	private void bindView() {
		mHead = (ImageView) findViewById(R.id.head);
		mTvTitle = (TextView) findViewById(R.id.title);
		mTvUserName = (TextView) findViewById(R.id.username);
		mTvTotalNum = (TextView) findViewById(R.id.totalnum);
		mTvName = (TextView) findViewById(R.id.name);
		mTvQueNum = (TextView) findViewById(R.id.que_num);
		mTvTime = (TextView) findViewById(R.id.time);
		mTvScore = (TextView) findViewById(R.id.score);
		mContent = (LinearLayout) findViewById(R.id.content);
		mBtnBack = (ImageButton) findViewById(R.id.back);
		mBtnTotal = (Button) findViewById(R.id.total);
		mBtnCheckAnswer = (Button) findViewById(R.id.check);
		int margin = (int) (Constants.full_display_width * 1.0 / 6);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent
				.getLayoutParams();
		lp.leftMargin = margin;
		lp.rightMargin = margin;
		mContent.setLayoutParams(lp);

		if (isPass == 1) {// 已经通过
			mBtnCheckAnswer.setVisibility(View.VISIBLE);
			mBtnTotal.setVisibility(View.GONE);
		}

		mBtnBack.setOnClickListener(this);
		mBtnCheckAnswer.setOnClickListener(this);
		mBtnTotal.setOnClickListener(this);
	}

	private void initData() {
		String fileName = person.getLargePhotoPath().substring(
				person.getLargePhotoPath().lastIndexOf("/") + 1);
		File file = new File(Constants_Url.PIC_USERHEAD_PATH + "/" + fileName);
		if (file.exists()) {
			mHead.setImageBitmap(BitmapFactory.decodeFile(file
					.getAbsolutePath()));
		}
		String headUrl = ResolveJSON.JSON_To_PersonInformation(
				MyInstance.getSharedPreferencesString(mContext,
						Constants.PERSON_INFORMATION)).getPhotoPath();
		if (headUrl != null && headUrl.equals(""))
			mLoader.download(headUrl, mHead);
		mTvUserName.setText(person.getUsername());
		if (saveData != null) {
			String name = saveData.getTitle();
			mTvName.setText(name);
			mTvTitle.setText(name);
			mTvTotalNum.setText("考试次数：" + saveData.getTotalNum());
			mTvQueNum.setText("题数：" + saveData.getQuestionNum() + "题");
			mTvScore.setText("合格标准：满分" + saveData.getMaxScore() + "分，"
					+ saveData.getPassScore() + "分及格");
			mTvTime.setText("考试时间：" + saveData.getTime() + "分钟");
		}
	}

	AlertDialog dialog;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				loadCarriesQue();
			} else {
				dialog.dismiss();
				if (msg.what == 5)
					Constants.showMessageByNetExpetion(mContext);
				else if (msg.what == 0)
					Constants.showMessage(mContext, "您今天的考试不及格，请明天再来考。");
				else if (msg.what == 3)
					Constants.showMessage(mContext, "加载试卷出错");
				else if (msg.what == 4) {
					// System.out.println("start total!");
					saveData.setQues((HashMap<Integer, CarriesQueModel>) msg
							.getData().getSerializable("datas"));
					data = saveData;
					if (isPass == 0) {
						Intent intent = new Intent();
						intent.setClass(mContext, CarriesTotalActivity.class);
						intent.putExtra("UserId", userId);
						startActivityForResult(intent, 1006);
					} else {
						Intent intent = new Intent();
						intent.setClass(mContext, CheckAnswersActivity.class);
						startActivityForResult(intent, 1006);
					}
				}
			}
		}
	};

	/**
	 * 检查今天是否还能考试
	 */
	private void checkIsCanTotal() {
		if (NetWork.hasNetWorkConnect(mContext)) {
			dialog = Constants.getTempDialog(mContext, "正在加载...");
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String result = NetWork.getData(checkUrl);
						System.out.println(result + " " + checkUrl);
						JSONObject json = new JSONObject(result);
						int resultInt = json.getInt("Result");
						if (resultInt == 1)
							mHandler.sendEmptyMessage(1);
						else
							mHandler.sendEmptyMessage(0);
					} catch (Exception e) {
						mHandler.sendEmptyMessage(5);
						e.printStackTrace();
					}
				}
			});
		} else
			Constants.showMessageByNoNet(mContext);
	}

	// 上一个题目类型
	private int lastType = 0;
	private int queNo = 1;

	/**
	 * 加载试卷题目
	 */
	private void loadCarriesQue() {
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String result = NetWork.getData(Constants_Url.CARRIES_QUE
							+ id);
					// System.out.println(result);
					JSONObject json = new JSONObject(result);
					JSONObject resultObj = json.getJSONObject("Result");
					JSONArray array = resultObj.getJSONArray("topicList");
					HashMap<Integer, CarriesQueModel> datas = new HashMap<Integer, CarriesQueModel>();
					lastType = 0;
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.opt(i);
						CarriesQueModel data = new CarriesQueModel();

						// 解析答案选项
						HashMap<Integer, AnswerModel> answers = new HashMap<Integer, AnswerModel>();
						JSONArray ans = obj.getJSONArray("answerList");
						for (int j = 0; j < ans.length(); j++) {
							JSONObject an = (JSONObject) ans.opt(j);
							AnswerModel answer = new AnswerModel();
							answer.setAnswer(an.getString("content"));
							answer.setCorrect(an.getBoolean("isCorrect"));
							answers.put(j, answer);
						}
						data.setAnswers(answers);
						String image = obj.getString("image");
						if (image != null && !image.equals(""))// Constants_Url.SERVER_URL+
							data.setImage(ImageDealTool.revitionImageSize(
									ImageDealTool.getImage(image),
									Constants.full_display_width
											- Constants.dip2px(mContext, 20)));
						data.setInfo(obj.getString("analysis"));
						data.setQue(obj.getString("question"));
						int type = obj.getInt("type");
						data.setTypeStr(typeStrs[type]);

						if (lastType != type) {
							queNo = 1;
							data.setFirst(true);
						}
						data.setNo(type + "." + queNo + " ");
						queNo++;
						lastType = type;

						data.setType(type);
						data.setScore(obj.getInt("value"));
						data.setLastSelectIndex(-1);
						data.setGetScore(false);
						datas.put(i, data);
					}
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("datas", datas);
					msg.what = 4;
					msg.setData(bundle);
					mHandler.sendMessage(msg);
				} catch (JSONException e) {
					mHandler.sendEmptyMessage(3);
					e.printStackTrace();
				} catch (Exception e) {
					mHandler.sendEmptyMessage(5);
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent res) {
		if (resultCode == -1006) {// 返回
			setResult(-1006);
			finish();
		} else if (resultCode == -1007) {// 重新考试
			data = saveData;
			Intent intent = new Intent();
			intent.setClass(mContext, CarriesTotalActivity.class);
			intent.putExtra("type", 1);
			intent.putExtra("UserId", userId);
			startActivityForResult(intent, 1006);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnBack) {
			setResult(RESULT_CANCELED);
			finish();
		} else if (v == mBtnTotal) {
			checkIsCanTotal();
		} else if (v == mBtnCheckAnswer) {
			if (NetWork.hasNetWorkConnect(mContext)) {
				dialog = Constants.getTempDialog(mContext, "加载中...");
				loadCarriesQue();
			} else
				Constants.showMessageByNoNet(mContext);
		}
	}

}

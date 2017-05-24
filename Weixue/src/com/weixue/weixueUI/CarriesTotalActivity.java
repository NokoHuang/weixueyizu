package com.weixue.weixueUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weixue.Function.NetWork;
import com.weixue.Model.AnswerModel;
import com.weixue.Model.CarriesItemModel;
import com.weixue.Model.CarriesQueModel;
import com.weixue.Model.ImageModel;
import com.weixue.Tool.ThreadPool;
import com.weixue.Tool.TimeTextView;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 考试
 * 
 * @author zeda
 * 
 */
public class CarriesTotalActivity extends Activity implements OnClickListener {

	private HashMap<Integer, CarriesQueModel> queList;// 试卷题目
	private CarriesItemModel data;

	private ImageButton mBtnBack;
	private Button mBtnPre, mBtnNext, mBtnSubmit;
	private TextView mTvName, mTvQue, mTvTypeNo;
	private LinearLayout mListAnswers;
	private TimeTextView mTtvTime;

	private int curQueIndex = 0;// 当前题目index

	private Activity mContext;
	private LayoutInflater mInflater;

	private int queCount;// 题数

	private int score;// 当前题分数
	private int time;// 当前题目限定的时间（分）
	private int curScore = 0;// 当前得到的分数
	private int curQueTrue = 0;// 当前题中对的选项数
	private CarriesQueModel queData;// 当前题目数据

	private ArrayList<ImageView> answerViews;// 选项控件

	private HashMap<Integer, AnswerModel> answers;// 当前题目对应的答案选项

	private ExecutorService pool;

	private boolean isEnd = false;// 是否考试结束

	private int userid;

	private int isJudge = 0;// 是否及格

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_carries_total);
		mContext = this;
		data = CarriesInfoActivity.data;
		queList = data.getQues();
		queCount = data.getQuestionNum();
		time = data.getTime();
		mInflater = getLayoutInflater();
		pool = ThreadPool.getInstance();
		userid = getIntent().getIntExtra("UserId", 1);

		init();
	}

	/**
	 * 监听倒计时
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (dialog != null)
				dialog.dismiss();
			if (msg.what == 1) {
				if (!isEnd) {
					isEnd = true;
					submitCarries();// 交卷
				}
			} else if (msg.what == 2) {// 交卷成功
				// 算使用时间
				int[] noewTime = mTtvTime.getTimes();
				int useM = 0, useS = 0;
				if (noewTime[1] == 0) {// 秒数为0
					useM = time - noewTime[0];
				} else {
					useM = time - 1 - noewTime[0];
					useS = 60 - noewTime[1];
				}
				String useTimeStr = "用时：" + useM + "分" + useS + "秒";

				Intent intent = new Intent();
				intent.setClass(mContext, TestResultActivity.class);
				intent.putExtra("state", isJudge);
				intent.putExtra("useTimeStr", useTimeStr);
				intent.putExtra("score", curScore);
				startActivityForResult(intent, 1006);
			} else if (msg.what == 0) {
				Constants.showMessage(mContext, "交卷失败");
			} else if (msg.what == 5) {
				Constants.showMessageByNetExpetion(mContext);
			}
		}
	};

	private void init() {
		bindView();

		// 第一题
		queData = queList.get(curQueIndex);
		if (queData != null) {
			int type = queData.getType();
			if (type <= 3)
				updateQueView(type);
			else
				nextQue();
		}

		mTtvTime.setTimes(new int[] { time, 0 }, handler);// 设置时间，分、秒
		mTtvTime.setRun(true);
		mTtvTime.run();
	}

	private void bindView() {
		mTvName = (TextView) findViewById(R.id.title);
		mTvQue = (TextView) findViewById(R.id.que);
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		mBtnSubmit = (Button) findViewById(R.id.submit);
		mBtnBack = (ImageButton) findViewById(R.id.back);
		mTtvTime = (TimeTextView) findViewById(R.id.time);
		mTvTypeNo = (TextView) findViewById(R.id.que_type_no);
		mListAnswers = (LinearLayout) findViewById(R.id.answers);

		mTvName.setText(data.getTitle());

		mBtnNext.setOnClickListener(this);
		mBtnPre.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
	}

	/**
	 * 更新问题以及答案页
	 * 
	 */
	private void updateQueView(int type) {

		answerViews = new ArrayList<ImageView>();

		mTvQue.setText(queData.getNo() + queData.getQue());
		// 不同类型的第一题才可以显示题目类型
		if (queData.isFirst()) {
			mTvTypeNo.setText(queData.getTypeStr());
			mTvTypeNo.setVisibility(View.VISIBLE);
		} else {
			mTvTypeNo.setVisibility(View.GONE);
		}

		ImageView mIvImage = (ImageView) findViewById(R.id.image);
		ImageModel image = queData.getImage();
		if (image != null) {
			Bitmap bmp = image.getBitmap();
			if (bmp != null) {
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvImage
						.getLayoutParams();
				lp.width = image.getWidth();
				lp.height = image.getHeight();
				mIvImage.setLayoutParams(lp);
				mIvImage.setVisibility(View.VISIBLE);
				mIvImage.setImageBitmap(bmp);
			} else
				mIvImage.setVisibility(View.GONE);
		} else
			mIvImage.setVisibility(View.GONE);
		mListAnswers.removeAllViews();
		score = queData.getScore();

		answers = queData.getAnswers();
		if (answers == null)
			return;
		curQueTrue = 0;
		// 答案选项
		for (int i = 0; i < answers.size(); i++) {
			AnswerModel answer = answers.get(i);
			View view = mInflater.inflate(R.layout.answer_item, null);
			TextView mTvIndex = (TextView) view.findViewById(R.id.answer_index);
			TextView mTvAnswer = (TextView) view.findViewById(R.id.answer);
			ImageView mIvSelect = (ImageView) view.findViewById(R.id.select);

			answerViews.add(mIvSelect);

			if (answer.isSelected())
				mIvSelect.setBackgroundResource(R.drawable.radio_checked);

			mTvAnswer.setText(answer.getAnswer());
			mTvIndex.setText((char) (65 + i) + "、");
			view.setOnClickListener(new OnAnswerClickListener(answer,
					mIvSelect, i, type));
			mListAnswers.addView(view);

			if (answer.isCorrect())
				curQueTrue++;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnBack) {
			exitTip();
		} else if (v == mBtnSubmit) {
			if (NetWork.hasNetWorkConnect(mContext))
				submitTip();
			else
				Constants.showMessageByNoNet(mContext);
		} else if (v == mBtnPre) {
			preQue();
		} else if (v == mBtnNext) {
			nextQue();
		}
	}

	/**
	 * 上一题
	 */
	private void preQue() {
		if (curQueIndex == 0)
			Constants.showMessage(mContext, "当前是第一题");
		else {
			computeScore();

			curQueIndex--;
			queData = queList.get(curQueIndex);
			if (queData != null) {
				int type = queData.getType();
				if (type < 3)
					updateQueView(type);
				else
					preQue();
			}
		}
	}

	/**
	 * 下一题
	 */
	private void nextQue() {
		if (curQueIndex >= queCount - 1)
			Constants.showMessage(mContext, "当前是最后一题");
		else {
			computeScore();

			curQueIndex++;
			queData = queList.get(curQueIndex);
			if (queData != null) {
				int type = queData.getType();
				if (type < 3)
					updateQueView(type);
				else
					nextQue();
			}
		}
	}

	/**
	 * 计算分数(当前题目是否得分)
	 * 
	 * @return
	 */
	private void computeScore() {
		int trueNum = 0;
		for (int i = 0; i < answers.size(); i++) {
			AnswerModel answer = answers.get(i);
			if (answer.isSelected()) {// 选中
				if (answer.isCorrect())// 正确
					trueNum++;
				else if (!answer.isCorrect()) {// 错误的 有一个则不能得分
					trueNum = 0;
					break;
				}
			}
		}
		if (trueNum == curQueTrue) {// 对的选项数相等
			if (!queData.isGetScore()) {// 未加过分数
				curScore += score;
				queData.setGetScore(true);
			}
		} else {// 我选中的正确答案选项数和题目的正确答案选项数不相等
			if (queData.isGetScore()) {// 已经加过分数
				curScore -= score;
				queData.setGetScore(false);
			}
		}
		queData.setAnswers(answers);
		queList.put(curQueIndex, queData);

	}

	AlertDialog dialog;

	/**
	 * 手动交卷前提示
	 */
	private void submitTip() {
		dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("温馨提示");
		dialog.setMessage("确定现在交卷，结束考试吗？");
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				isEnd = true;
				mTtvTime.setRun(false);// 停止计时
				submitCarries();
			}
		});
		dialog.setButton2("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 退出考试前前提示
	 */
	private void exitTip() {
		dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("温馨提示");
		dialog.setMessage("您确定退出本次考试吗？");
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				isEnd = true;
				mTtvTime.setRun(false);
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		dialog.setButton2("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private List<NameValuePair> pairs;

	/**
	 * 交卷
	 */
	private void submitCarries() {
		dialog = Constants.getTempDialog(mContext, "交卷中...");
		computeScore();// 先计算分数

		isJudge = 0;
		if (curScore >= data.getPassScore())// 及格
			isJudge = 1;

		pairs = new LinkedList<NameValuePair>();// userId=会员编号&testId=试卷编号&score=考试分数
		pairs.add(new BasicNameValuePair("userId", String.valueOf(userid)));
		pairs.add(new BasicNameValuePair("testId", String.valueOf(data.getId())));
		pairs.add(new BasicNameValuePair("score", String.valueOf(curScore)));
		pairs.add(new BasicNameValuePair("judge", String.valueOf(isJudge)));
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(pairs.toString());
					String result = NetWork.httpPost(
							Constants_Url.SAVE_TEST_RESULT, pairs);
					JSONObject json = new JSONObject(result);
					int resultInt = json.getInt("Status");
					if (resultInt == 1)
						handler.sendEmptyMessage(2);
					else
						handler.sendEmptyMessage(0);
				} catch (Exception e) {
					handler.sendEmptyMessage(5);
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode);
		finish();
	}

	/**
	 * 单选、判断点击处理
	 * 
	 * @author zeda
	 * 
	 */
	class OnAnswerClickListener implements OnClickListener {
		private AnswerModel answer;
		private ImageView selectView;
		private int answerIndex;// 当前答案选项对应的index
		private int type;// 1为单选、2为多选、3为判断

		public OnAnswerClickListener(AnswerModel answer, ImageView selectView,
				int answerIndex, int type) {
			this.answer = answer;
			this.selectView = selectView;
			this.answerIndex = answerIndex;
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			if (isEnd)// 已经考试结束
				return;
			boolean isSelect = answer.isSelected();
			if (type == 2) {// 多选
				if (isSelect) {
					selectView
							.setBackgroundResource(R.drawable.radio_unchecked);
				} else {
					selectView.setBackgroundResource(R.drawable.radio_checked);
				}
				isSelect = !isSelect;
				answer.setSelected(isSelect);
				answers.put(answerIndex, answer);
			} else {
				if (isSelect)// 已经选中
					return;
				else {
					selectView.setBackgroundResource(R.drawable.radio_checked);
					int lastQueSelectIndex = queData.getLastSelectIndex();
					if (lastQueSelectIndex != -1) {// 有选中的选项
						answerViews.get(lastQueSelectIndex)
								.setBackgroundResource(
										R.drawable.radio_unchecked);
						AnswerModel lastSelectAnswer = answers
								.get(lastQueSelectIndex);
						lastSelectAnswer.setSelected(false);
						answers.put(lastQueSelectIndex, lastSelectAnswer);
					}
					queData.setLastSelectIndex(answerIndex);
					answer.setSelected(true);
					answers.put(answerIndex, answer);
				}
			}
		}
	}

	/**
	 * 返回键监听
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			exitTip();
			return false;
		}
		return super.dispatchKeyEvent(event);
	};

	@Override
	protected void onDestroy() {
		if (!isEnd) {
			isEnd = true;
			mTtvTime.setRun(false);// 停止计时
		}
		super.onDestroy();
	}

}

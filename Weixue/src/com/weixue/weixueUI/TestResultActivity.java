package com.weixue.weixueUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 考试结果
 * 
 * @author zeda
 * 
 */
public class TestResultActivity extends Activity implements OnClickListener {

	private int state;// 1为及格 0为不及格
	private String useTimeStr;// 使用时间（字符串）
	private int score;// 得到的分数

	private ImageButton mBtnBack;
	private TextView mTvMsg, mTvMsg1, mTvScore, mTvUseTime;
	private Button mBtnCheck, mBtnExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_test_result);
		Intent intent = getIntent();
		state = intent.getIntExtra("state", 0);
		useTimeStr = intent.getStringExtra("useTimeStr");
		score = intent.getIntExtra("score", 0);

		init();
	}

	private void init() {
		bindView();
		if (state == 1) {
			mBtnCheck.setVisibility(View.VISIBLE);
			// mBtnAgain.setVisibility(View.VISIBLE);
			mTvMsg1.setVisibility(View.GONE);
			mTvMsg.setText("考试及格!");
		}
		mTvScore.setText("成绩：" + score + "分");
		mTvUseTime.setText(useTimeStr);
	}

	private void bindView() {
		mBtnBack = (ImageButton) findViewById(R.id.back);
		mBtnCheck = (Button) findViewById(R.id.check_answers);
		mBtnExit = (Button) findViewById(R.id.exit_test);
		mTvMsg = (TextView) findViewById(R.id.message);
		mTvMsg1 = (TextView) findViewById(R.id.message1);
		mTvScore = (TextView) findViewById(R.id.score);
		mTvUseTime = (TextView) findViewById(R.id.usetime);

		mBtnCheck.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnExit.setOnClickListener(this);
	}

	/**
	 * 返回键监听
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			setResult(-1006);
			finish();
			return false;
		}
		return super.dispatchKeyEvent(event);
	};

	@Override
	public void onClick(View v) {
		if (v == mBtnBack || v == mBtnExit) {
			setResult(-1006);
			finish();
		}
		// else if (v == mBtnAgain) {
		// setResult(-1007);
		// finish();
		// }
		else if (v == mBtnCheck) {
			Intent intent = new Intent();
			intent.setClass(this, CheckAnswersActivity.class);
			startActivityForResult(intent, 1006);
		}
	}
}

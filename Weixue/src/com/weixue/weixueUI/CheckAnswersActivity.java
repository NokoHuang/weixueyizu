package com.weixue.weixueUI;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weixue.Model.AnswerModel;
import com.weixue.Model.CarriesItemModel;
import com.weixue.Model.CarriesQueModel;
import com.weixue.Model.ImageModel;
import com.weixue.Utils.Constants;

/**
 * 查看答案
 * 
 * @author zeda
 * 
 */
public class CheckAnswersActivity extends Activity implements OnClickListener {

	private HashMap<Integer, CarriesQueModel> queList;// 试卷题目
	private CarriesItemModel data;

	private ImageButton mBtnBack;
	private Button mBtnPre, mBtnNext, mBtnQue;
	private TextView mTvQue, mTvAnswerDetail, mTvTypeNo;
	private LinearLayout mListAnswers;

	private int curQueIndex = 0;// 当前题目index

	private Activity mContext;
	private LayoutInflater mInflater;

	private int queCount;// 题数

	private CarriesQueModel queData;// 当前题目数据

	private HashMap<Integer, AnswerModel> answers;// 当前题目对应的答案选项

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_check_answers);
		mContext = this;
		data = CarriesInfoActivity.data;
		queList = data.getQues();
		queCount = data.getQuestionNum();
		mInflater = getLayoutInflater();

		init();
	}

	private void init() {
		bindView();
		updateQueView();
	}

	private void bindView() {
		mTvQue = (TextView) findViewById(R.id.que);
		mTvAnswerDetail = (TextView) findViewById(R.id.answer_detail);
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		mBtnQue = (Button) findViewById(R.id.menu_que);
		mBtnBack = (ImageButton) findViewById(R.id.back);
		mTvTypeNo = (TextView) findViewById(R.id.que_type_no);
		mListAnswers = (LinearLayout) findViewById(R.id.answers);

		mBtnNext.setOnClickListener(this);
		mBtnPre.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnQue.setOnClickListener(this);
	}

	/**
	 * 更新问题以及答案页
	 * 
	 */
	private void updateQueView() {
		queData = queList.get(curQueIndex);
		if (queData != null) {
			mTvQue.setText(queData.getNo() + queData.getQue());
			// 不同类型的第一题才可以显示题目类型
			if (queData.isFirst()) {
				mTvTypeNo.setText(queData.getTypeStr());
				mTvTypeNo.setVisibility(View.VISIBLE);
			} else {
				mTvTypeNo.setVisibility(View.GONE);
			}

			mTvAnswerDetail.setText(queData.getInfo());

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

			answers = queData.getAnswers();
			if (answers == null)
				return;
			// 答案选项
			for (int i = 0; i < answers.size(); i++) {
				AnswerModel answer = answers.get(i);
				View view = mInflater.inflate(R.layout.answer_item, null);
				TextView mTvIndex = (TextView) view
						.findViewById(R.id.answer_index);
				TextView mTvAnswer = (TextView) view.findViewById(R.id.answer);
				ImageView mIvSelect = (ImageView) view
						.findViewById(R.id.select);

				mTvAnswer.setText(answer.getAnswer());
				mTvIndex.setText((char) (65 + i) + "、");
				mListAnswers.addView(view);

				if (answer.isCorrect())
					mIvSelect.setBackgroundResource(R.drawable.radio_checked);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode > -1) {
			curQueIndex = resultCode;
			updateQueView();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnBack) {
			setResult(RESULT_CANCELED);
			finish();
		} else if (v == mBtnQue) {
			Intent intent = new Intent();
			intent.setClass(mContext, CheckAnswersListActivity.class);
			intent.putExtra("num", queCount);
			startActivityForResult(intent, 1006);
		} else if (v == mBtnPre) {
			if (curQueIndex == 0)
				Constants.showMessage(mContext, "当前是第一题");
			else {
				curQueIndex--;
				updateQueView();
			}
		} else if (v == mBtnNext) {
			if (curQueIndex >= queCount - 1)
				Constants.showMessage(mContext, "当前是最后一题");
			else {
				curQueIndex++;
				updateQueView();
			}
		}
	}

}

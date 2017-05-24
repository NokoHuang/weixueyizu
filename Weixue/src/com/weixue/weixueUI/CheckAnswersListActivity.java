package com.weixue.weixueUI;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.weixue.Model.CarriesQueModel;
import com.weixue.Utils.Constants;

/**
 * 查看答案（题目）
 * 
 * @author zeda
 * 
 */
public class CheckAnswersListActivity extends Activity {
	private GridView grid;
	private ImageButton mBtnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_check_answers_list);
		mBtnBack = (ImageButton) findViewById(R.id.back);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(-1);
				finish();
			}
		});

		grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(new GridViewAdapter(this, getIntent().getIntExtra(
				"num", 0)));
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				setResult(pos);
				finish();
			}
		});
	}

	private class GridViewAdapter extends BaseAdapter {

		private Context context;
		private int count;
		private int height;

		public GridViewAdapter(Context context, int num) {
			this.context = context;
			this.count = num;
			this.height = (int) ((Constants.full_display_width - Constants
					.dip2px(context, 2) * 5.0f) / 6);
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView result = new TextView(context);
			HashMap<Integer, CarriesQueModel> ques = CarriesInfoActivity.data
					.getQues();

			// 获取题号
			String queNo = "";
			if (ques != null) {
				CarriesQueModel que = ques.get(position);
				if (que != null)
					queNo = que.getNo();
			}
			if (!queNo.equals(""))
				queNo = queNo.substring(0, queNo.length() - 1);

			result.setText(queNo);
			result.setTextColor(Color.BLACK);
			result.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			result.setLayoutParams(new AbsListView.LayoutParams(
					new LayoutParams(LayoutParams.MATCH_PARENT, height)));
			result.setBackgroundResource(R.drawable.answer_list_item_ontouch); // 设置背景颜色
			result.setGravity(Gravity.CENTER);
			return result;
		}

	}

}
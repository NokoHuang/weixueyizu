package com.weixue.Tool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.weixue.weixueUI.R;

/**
 * 自定义倒计时文本控件
 * 
 * @author Administrator
 * 
 */
public class TimeTextView extends TextView implements Runnable {
	Paint mPaint; // 画笔,包含了画几何图形、文本等的样式和颜色信息
	private int[] times = new int[2];
	// private long mday, mhour;
	private int mmin, msecond;// 天，小时，分钟，秒
	private boolean run = false; // 是否启动了
	private Handler handler;

	public TimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TimeTextView);
		array.recycle(); // 一定要调用，否则这次的设定会对下次的使用造成影响
	}

	public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TimeTextView);
		array.recycle(); // 一定要调用，否则这次的设定会对下次的使用造成影响
	}

	public TimeTextView(Context context) {
		super(context);
	}

	public int[] getTimes() {
		times[0] = mmin;
		times[1] = msecond;
		return times;
	}

	public void setTimes(int[] times, Handler handler) {
		// mday = times[0];
		// mhour = times[1];
		this.handler = handler;
		mmin = times[0];
		msecond = times[1];
	}

	/**
	 * 倒计时计算
	 */
	private void ComputeTime() {
		msecond--;
		if (msecond < 0) {
			mmin--;
			msecond = 59;
			if (mmin < 0) {
				mmin = 0;
				msecond = 0;
				run = false;
				handler.sendEmptyMessage(1);
				// mmin = 59;
				// mhour--;
				// if (mhour < 0) {
				// // 倒计时结束
				// mhour = 59;
				// mday--;
				// }
			}
		}
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	@Override
	public void run() {
		if (!run)
			return;
		ComputeTime();
		String min = String.valueOf(mmin);
		if (min.length() == 1) {
			if (mmin == 0)
				min += "0";
			else
				min = "0" + min;
		}

		String second = String.valueOf(msecond);
		if (second.length() == 1) {
			if (msecond == 0)
				second += "0";
			else
				second = "0" + second;
		}

		String strTime = min + ":" + second;
		this.setText(Html.fromHtml(strTime));
		if (run) {
			postDelayed(this, 1000);
		}
	}
}

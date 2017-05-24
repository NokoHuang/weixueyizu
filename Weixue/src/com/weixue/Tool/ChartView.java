package com.weixue.Tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ChartView extends View {

	private int[] data_total;
	private int flag;
	private int margin;
	private Chart chart;
	private Paint paint;

	public ChartView(Context context, int flag) {
		super(context);
		this.flag = flag;
		margin = 0;
		chart = new Chart();
		data_total = new int[] { 30, 70, 50 };
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	/** 画底部文字 */
	public void drawAxis(Canvas canvas) {
		paint.setColor(Color.BLACK);
		paint.setTextSize(15);
		String[] title = new String[] { "学分", "学币", "完成课程" };
		int x = 55;
		for (int i = 0; i < 3; i++) { // 画x轴线下面的那些字,57代表与下一个的x轴的距离
			canvas.drawText(title[i], x, 180, paint);
			x += 50;
			if (i == 0) {
				x += 12;
			}
			if (i == 1) {
				x += 5;
			}
		}
	}

	/** 画柱子 */
	public void drawChart(Canvas canvas) {
		if (flag == 2) {
			int[] color = new int[] { Color.BLUE, Color.RED, Color.GREEN,
					Color.LTGRAY, Color.MAGENTA };
			String[] title = new String[] { "234分", "50币", "50%" };
			int temp = 15;
			int x;
			paint.setStrokeWidth(10);
			for (int i = 0; i < 3; i++) {
				paint.setColor(color[i]);
				chart.setH(data_total[i]);
				if (i == 1) {
					chart.setX(temp + 40 + margin);
					x = temp + 40 + margin;
				}
				if (i == 2) {
					chart.setX(temp + 50 + margin);
					x = temp + 50 + margin;
				}
				else {
					chart.setX(temp + 40 + margin);
					x = temp + 40 + margin;
				}

				chart.drawSelf(canvas, paint);

				paint.setColor(Color.BLACK);
				
				canvas.drawText(title[i], x, 140 - data_total[i], paint);

				margin = 20;

				temp = chart.getX();

			}
		}
	}


	@Override
	public void onDraw(Canvas canvas) {
		drawAxis(canvas);
		drawChart(canvas);
	}
}

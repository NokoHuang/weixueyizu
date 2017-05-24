package com.weixue.Tool ;

import android.graphics.Canvas ;
import android.graphics.Paint ;

public class Chart {

	private final int w = 30 ;
	private double  h ;
	private final int total_y = 150 ;
	private int x ;

	public int getX() {
		return x ;
	}

	public void setX(int x) {
		this.x = x ;
	}

	public double getH() {
		return (int)h*2.5 ;
	}

	public void setH(double d) {
		this.h = d ;
	}
	public void drawSelf(Canvas canvas, Paint paint) {
		canvas.drawRect(x, (float) (total_y -h), w + x, total_y, paint) ;
		
	}
}

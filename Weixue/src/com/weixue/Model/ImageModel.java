package com.weixue.Model;

import android.graphics.Bitmap;

/**
 * 图像model
 * 
 * @author zeda
 * 
 */
public class ImageModel {
	private int width;
	private int height;
	private Bitmap bitmap;

	public ImageModel(int width, int height, Bitmap bitmap) {
		this.width = width;
		this.height = height;
		this.bitmap = bitmap;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

}

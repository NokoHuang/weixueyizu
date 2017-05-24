package com.example.model;

import android.graphics.Bitmap;

public class NewsListModel {
	private String id;
	private Bitmap bitmap;
	private String title;
	private String date_time;
	
	public NewsListModel(){
		
	}
	
	public NewsListModel(String id,Bitmap bitmap,String title,String date_time){
		this.id=id;
		this.bitmap=bitmap;
		this.title=title;
		this.date_time=date_time;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDate_time() {
		return date_time;
	}
	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}
}

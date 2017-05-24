package com.weixue.MyDialog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.weixue.Function.CompressImg;
import com.weixue.Model.Course;
import com.weixue.weixueUI.R;

public class CourseBrightspotActivity extends Activity{
	private TextView introduction;
	private View v;
	private Bitmap bitmap;//图片
	private String str=null;//内容
	private Context mContext;
	private Display display;
	private  int width ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_coursebrightspot_page);
		v=this.getWindow().getDecorView();
		 mContext=this;
		 display =getWindowManager().getDefaultDisplay();
		 width= display.getWidth();
		 initUi();
		//loadProData() ;
	}
	
	private void initUi(){
		
		introduction=(TextView) findViewById(R.id.tv_detail);
		introduction.setVerticalScrollBarEnabled(false); 
		
		introduction.setText(Html.fromHtml(this.getIntent().getExtras().getString("content"),imgGetter,null));
	}
	
	public void loadProData() {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				
			}
		};

		new Thread(runnable).start();
		// 调用getMessageList缓冲数据
	}
	private Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				{
					str=(String) msg.obj;
					
					if(str!=null){
						introduction.setText(Html.fromHtml(str,imgGetter,null));
					}
					break;
				}				
			case 3:{//使用非主线程下载图片完成后，刷新界面
				bitmap=(Bitmap) msg.obj;
				introduction.setText(Html.fromHtml(str,imgGetter,null));
				//introduction.loadDataWithBaseURL("",str, "text/html", "GB2312", null);  
				v.invalidate();
				break;
			}
			
			}
		}
	};
	
	
	
	
	
	//获取drawable对象
		ImageGetter imgGetter = new Html.ImageGetter(){  
		    @Override  
		    public Drawable getDrawable(String source) {  
		    	if(bitmap==null){
		    	new Thread(new TakePicture(source)).start();
		    	return null;
		    	}else{
		    		Drawable d=new BitmapDrawable(bitmap);
		    		
		    		 
		    		d.setBounds(0, 0, width,  
		            		d.getIntrinsicHeight());  
		    		return d;
		    	}
		    }  
		};
	/**
	 * 使用线程从网络中获取图片
	 * */	
class TakePicture implements Runnable{
			private String source;
			private  InputStream is = null; 
			
		public TakePicture(String source){
			this.source=source;
		
		}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
		       	 is = new URL(source).openConnection().getInputStream();
		       	 
		       Bitmap bitmap=BitmapFactory.decodeStream(is);
		       bitmap= CompressImg.compressImage(bitmap);
		       	// ByteArrayOutputStream bos=new ByteArrayOutputStream();
		       	
		           // byte[] data = bos.toByteArray();
		       
		       	 
					//int len=0;
					System.out.println("加载完成!");
					handler.sendMessage(handler.obtainMessage(3, bitmap));
						//fileout.write(data);
					
					
					is.close();
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
		}
}

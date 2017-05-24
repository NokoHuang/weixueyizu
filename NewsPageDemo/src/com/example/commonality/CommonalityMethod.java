package com.example.commonality;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.view.WindowManager;

import com.example.newspagedemo.R;

public class CommonalityMethod {

	/**
	 * ??????
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context){
		ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm!=null){
			NetworkInfo[] infos=cm.getAllNetworkInfo();
			if(infos!=null){
				for(NetworkInfo ni:infos){
					if(ni.isConnected())
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * ?????
	 * @param activity
	 * @return
	 */
	public static AlertDialog  getLoadDataDialog(Activity activity){
		AlertDialog dialog=new AlertDialog.Builder(activity).create();
		dialog.show();

		dialog.setCancelable(false);
		Window window=dialog.getWindow();
		WindowManager.LayoutParams lp=window.getAttributes();
		lp.alpha=0.5f;
		window.setAttributes(lp);
		window.setContentView(R.layout.load_progress_login);

		return dialog;
	}



	/**
	 * ????????
	 * @param BitmapUrl
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getBitmap(String BitmapUrl)throws Exception{
		Bitmap bitmap=null;
		//??????
		URL url=new URL(BitmapUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//??????
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		//?????????
		if(conn.getResponseCode()==200){
			InputStream is=conn.getInputStream();
			bitmap=BitmapFactory.decodeStream(is);
			//?????
			is.close();
			//??????
			return bitmap;
		}
		else
			return null;
	}

	/**
	 * ????String
	 *
	 * @param time
	 * @return
	 */
	public static String getDateString(long time){
		Date date=new Date(time);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy:MM:dd");

		return sdf.format(date);
	}

	/**
	 * px?dip
	 *
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	/**
	 * ?dip?dp????px??????????
	 *
	 * @param dipValue
	 *            ?DisplayMetrics????density?
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

}








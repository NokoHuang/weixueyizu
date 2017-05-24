package com.weixue.Function;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.weixue.MyDialog.GetAboutus;
import com.weixue.Utils.Constants;

public class MyInstance{
	private static SharedPreferences sp=null;
	/**
	 * 单一实例
	 * @param mContext
	 * @return
	 */
	public static SharedPreferences getSharedPreferencesInstance(Context mContext){
		if(sp==null){
			sp=mContext.getSharedPreferences(Constants.INFORMATION, Context.MODE_PRIVATE);
		}
		return sp;
	}
	
	/**
	 * 获取SharedPreferences保存的字符串
	 * @param mContext
	 * @param name
	 * @return
	 */
	public static String getSharedPreferencesString(Context mContext,String name){
		if(sp==null){
			sp=mContext.getSharedPreferences(Constants.INFORMATION, Context.MODE_PRIVATE);
		}
		
		return sp.getString(name, "");
	}
	
	/**
	 * 获取SharedPreferences保存的数字
	 * @param mContext
	 * @param name
	 * @return
	 */
	public static int getSharedPreferencesInt(Context mContext,String name){
		if(sp==null){
			sp=mContext.getSharedPreferences(Constants.INFORMATION, Context.MODE_PRIVATE);
		}
	
		return sp.getInt(name, 0);
	}
	
	/**
	 * 保存登陆界面的用户头像
	 * @param mContext
	 * @param photo_url
	 */
	public static void SaveLoginUserPhoto(Context mContext,String photo_url){
		SharedPreferences user_photo=mContext.getSharedPreferences("user_photo", Context.MODE_PRIVATE);
		Editor editor=user_photo.edit();
			editor.putString("photo", photo_url);
			editor.commit();
	}
}

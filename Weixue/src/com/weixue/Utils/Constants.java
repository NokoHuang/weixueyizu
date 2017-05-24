package com.weixue.Utils;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Function.GetDisplay;
import com.weixue.weixueUI.R;

public class Constants {
	/** 停止服务保存未下载完成的数据 */
	public static final String SAVEDATA = "com.weixue.savedata";

	/** 刷新动态 */
	public static final String STATUS_REFESH = "com.weixue.statusrefesh";

	public static final String UPDATE_USERHEAD = "com.weixue.update_userhead";

	public static int float_display_width = GetDisplay.displayWidth - 15;
	public static int float_display_height = GetDisplay.displayWidth / 2 + 40;

	public static int full_display_width = GetDisplay.displayWidth;
	public static int full_display_height = GetDisplay.displayHeight;

	/** 获取本应用SharedPreferences（以下SharedPreferences简称sp） */
	public static final String INFORMATION = "information";

	/** 存取sp中的用户信息 */
	public static final String PERSON_INFORMATION = "personInformation";

	/** 存取sp中动态模块的背景图 */
	public static final String STATUS_BG = "status_bg";

	/** 书签中点击的pagerIndex **/
	public static int pagerIndex;

	/** 存取下载任务数 */
	public static final String DOWNLOADNUM = "downloadNum";

	/** 保存最后一次登录的用户名和密码 */
	public static final String SAVE_LOGININFO = "lastLoginInfo";

	/** 语音搜索 */
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	public static final String SPEECH_PROMPT = "请讲话";

	public static final int REFRESH_STATE = 1, ADD_STATE = 2;

	/**
	 * 获取临时弹出框
	 */
	public static AlertDialog getTempDialog(Activity activity, String title) {
		AlertDialog tempDialog = new AlertDialog.Builder(activity).create();
		tempDialog.show();
		tempDialog.setCancelable(false);
		Window window = tempDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.6f;
		View view = activity.getLayoutInflater().inflate(
				com.weixue.weixueUI.R.layout.load_progress, null);
		TextView mTvTitle = (TextView) view.findViewById(R.id.title);
		mTvTitle.setText(title);
		window.setAttributes(lp);
		window.setContentView(view);
		return tempDialog;
	}

	/**
	 * 获取临时弹出框(能取消)
	 */
	public static AlertDialog getTempDialogCanCancle(Activity activity,
			String title) {
		AlertDialog tempDialog = new AlertDialog.Builder(activity).create();
		tempDialog.show();
		Window window = tempDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.6f;
		View view = activity.getLayoutInflater().inflate(
				com.weixue.weixueUI.R.layout.load_progress, null);
		TextView mTvTitle = (TextView) view.findViewById(R.id.title);
		mTvTitle.setText(title);
		window.setAttributes(lp);
		window.setContentView(view);
		return tempDialog;
	}

	/**
	 * px转dip
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
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 提示无网络
	 */
	public static void showMessageByNoNet(Activity context) {
		Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 提示网络异常
	 */
	public static void showMessageByNetExpetion(Activity context) {
		Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 提示指定内容
	 * 
	 * @param value
	 */
	public static void showMessage(Activity context, String value) {
		Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 得到sd卡路径 ""为sd卡不存在
	 * 
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		} else
			return "";
		return sdDir.toString();

	}

}

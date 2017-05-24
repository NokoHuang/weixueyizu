package com.weixue.weixueUI;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Function.DownloadProgressListener;
import com.weixue.Function.GetDisplay;
import com.weixue.Function.NetWork;
import com.weixue.Methods.FileDownloader;
import com.weixue.Model.Version;
import com.weixue.Utils.Constants_Url;

/**
 * 启动界面
 * 
 * @author chenjunjie
 * 
 */
public class SplashActivity extends Activity {
	// 跳到主页
	private static final int JUMPTOMAIN = 3;
	// 判断是否更新
	private static final int IS_UPDATE = 0;
	// 安装apk表示的常量
	private static final int INSTALL_APK = 1;
	// 显示的信息
	private static final int SHOW_MESSAGE = 2;
	// 连接网络超时时间
	private static final int TIME_OUT = 3 * 1000;
	// 获取现在的版本号
	private int versionCode;
	// 上下文
	private Context mContext;
	// 显示是否有新版本
	private TextView tv_showcontent;
	// 显示进度的对话框
	private ProgressDialog pd;
	// 线程
	private DownloadTask task;
	// 下载进度
	private int progressSize = 0;

	private String Imei;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.splash_page);
		// UmsAgent.setBaseURL("http://192.168.0.159:8111/index.php?");
		// UmsAgent.update(this);
		// UmsAgent.onError(this);
		// UmsAgent.setDefaultReportPolicy(this, 1);
		Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
				.getDeviceId();
		// UmsAgent.bindUserIdentifier(this, Imei);
		// UmsAgent.postClientData(this);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		GetDisplay.displayWidth = dm.widthPixels;// 获取分辨率宽度
		GetDisplay.displayHeight = dm.heightPixels;

		init();
	}

	// 初始化
	private void init() {
		versionCode = getVersionCode();
		mContext = this;
		tv_showcontent = (TextView) findViewById(R.id.tv_showcontent);
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(3000);
					handler.sendEmptyMessage(JUMPTOMAIN);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
		// getServiceVersionCode();
		// 进度条窗口
		// pd = new ProgressDialog(mContext);
		// pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// pd.setMessage(returnString(R.string.downloading));
		// pd.setIndeterminate(false);// 设置进度条是否为不明确
		// pd.setButton(returnString(R.string.cancel), new OnClickListener(){
		//
		// public void onClick(DialogInterface arg0, int arg1) {
		// // TODO Auto-generated method stub
		// //tv_showcontent.setText(returnString(R.string.wait));
		// task.exit();
		// arg0.dismiss();
		//
		// jumpMainPage();
		// System.out.println("点击取消按钮");
		// }});
		// pd.setCancelable(false);// 设置进度条是否可以按退回键取消
	}

	/** 返回string.xml中的字符串 */
	private CharSequence returnString(int stringId) {
		return mContext.getResources().getText(stringId);
	}

	/** 获取服务端的版本号 */
	private void getServiceVersionCode() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String result = NetWork.postData(
							Constants_Url.IS_NEW_VERSION, "utf-8", TIME_OUT);
					// 解析数据
					JSONObject object = new JSONObject(result);
					Version version = new Version();
					version.setVersionCode(object.getInt("versionCode"));
					version.setContent(object.getString("content"));
					// 发到handler处理
					handler.sendMessage(handler.obtainMessage(IS_UPDATE,
							version));
				} catch (Exception e) {
					// 异常处理
					e.printStackTrace();
					// handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
					// returnString(R.string.networkerror)));
					jumpMainPage();
				}
			}

		};
		new Thread(run).start();
	}

	/** 跳到主界面 */
	private void jumpMainPage() {
		Intent intent = new Intent();
		intent.setClass(mContext, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 获取客户端版本号
	 * */
	private int getVersionCode() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(),
					PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

			return info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			jumpMainPage();
			System.out.println(e.toString());
		}
		return 0;
	}

	/**
	 * 判断是否存在SD卡，存在则创建目录
	 * 
	 * @return File对象
	 * */
	private File createFileToSD() {
		// TODO Auto-generated method stub

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {

			File file = new File(Constants_Url.APK_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					jumpMainPage();
				}
			}
			// System.out.println(file.getAbsolutePath());
			return file;
		} else {
			Toast.makeText(getApplicationContext(), "请先插入SD卡",
					Toast.LENGTH_SHORT).show();
			return null;
		}

	}

	// 使用handler更新UI
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			super.handleMessage(msg);

			switch (msg.what) {
			case IS_UPDATE: {

				Version version = (Version) msg.obj;

				if (versionCode < version.getVersionCode()) {

					// 调用显示更新对话框
					showDialog(version);

				} else {

					// 版本为最新时，停留2秒
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							jumpMainPage();
						}
					}).start();

				}
				// System.out.println("versionCode-->"+versionCode);
				// System.out.println("version.getVersionCode()-->"+version.getVersionCode());
				break;
			}
			case INSTALL_APK: {
				File apkFile = (File) msg.obj;

				Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.fromFile(apkFile),
						"application/vnd.android.package-archive");
				startActivity(i);
				finish();
				break;
			}
			case SHOW_MESSAGE: {
				String show_content = (String) msg.obj;
				Toast.makeText(getApplicationContext(), show_content,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case JUMPTOMAIN: {
				jumpMainPage();
			}
			}
		}

		/**
		 * 显示更新对话框
		 * */
		private void showDialog(Version version) {
			// TODO Auto-generated method stub

			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setPositiveButton(returnString(R.string.update),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							if (!pd.isShowing()) {
								pd.show();
								File file = createFileToSD();
								if (file != null) {
									download(
											Constants_Url.DOWNLOAD_NEW_VERSION,
											file);
								} else {
									pd.dismiss();
								}

								// downLoadapk();
							}

						}

					});
			dialog.setNeutralButton(returnString(R.string.cancel),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							jumpMainPage();
						}
					});
			dialog.setMessage(version.getContent());
			dialog.setTitle(returnString(R.string.splash_title));
			dialog.show();
		}

	};

	/** 下载apk */
	private void download(String path, File saveDir) {// 运行在主线程
		task = new DownloadTask(path, saveDir);
		new Thread(task).start();
	}

	/**
	 * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
	 * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
	 */
	private final class DownloadTask implements Runnable {
		private String path;
		private File saveDir;
		private FileDownloader loader;
		private String fileName;
		boolean isshow = false;

		public DownloadTask(String path, File saveDir) {
			this.path = path;
			this.saveDir = saveDir;
		}

		/**
		 * 退出下载
		 */
		public void exit() {
			if (loader != null)
				loader.exit();
		}

		public void run() {
			try {
				if (path != null) {
					fileName = path.substring((path.lastIndexOf("/") + 1),
							path.length());
				}
				// 多线程
				loader = new FileDownloader(getApplicationContext(), path,
						saveDir, 3);
				pd.setMax((loader.getFileSize() / 1024));// 设置进度条的最大刻度

				loader.download(new DownloadProgressListener() {
					public void onDownloadSize(int size) {
						size = size / 1024;
						progressSize += size;
						System.out.println("size-->" + size);

						pd.setProgress(size);
						if (pd.getProgress() == pd.getMax() && (!isshow)) {
							isshow = true;
							handler.sendMessage(handler.obtainMessage(
									INSTALL_APK, new File(saveDir, fileName)));
							pd.dismiss();
						}
					}
				});
				// 单线程
				// boolean
				// mark=DownLoadFile.downLoadApk(Constants_Url.DOWNLOAD_NEW_VERSION,
				// new File(saveDir,fileName), pd);
				// if(mark){
				// handler.sendMessage(handler.obtainMessage(INSTALL_APK, new
				// File(saveDir,fileName)));
				// pd.dismiss();
				// }
			} catch (Exception e) {
				System.out.println(e.toString());
				exit();
				handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
						returnString(R.string.networkerror)));
				jumpMainPage();
			}
		}
	}

	// activity销毁
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (pd != null) {
			pd.dismiss();
		}
	}

}

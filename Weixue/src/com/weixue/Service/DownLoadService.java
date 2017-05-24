package com.weixue.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.weixue.Function.DownloadProgressListener;
import com.weixue.Methods.FileDownloader;
import com.weixue.Model.DownloadFile;
import com.weixue.MyInterface.ProgressChangeListener;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

public class DownLoadService extends Service {

	// 线程
	private Map<String, DownloadTask> map_task;
	private ProgressChangeListener listeners;
	private FileService fileService;
	private MyReceiver myReceiver;
	private SharedPreferences sp;
	private int downloadNum = 2;

	public void setProgressChangeListener(ProgressChangeListener listener) {
		this.listeners = listener;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}

	public class MyBinder extends Binder {
		public DownLoadService getService() {
			return DownLoadService.this;
		}

	}

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(Constants.SAVEDATA)) {
				Set<String> set = map_task.keySet();
				Object[] ob = set.toArray();
				for (int i = 0; i < map_task.size(); i++) {
					map_task.get(ob[i]).exit();
				}
			}
		}

	}

	public Map<String, DownloadTask> getMap() {
		return map_task;
	}

	public int getCount() {
		return map_task.size();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		map_task = new HashMap<String, DownloadTask>();
		fileService = new FileService(getApplicationContext());
		sp = getApplicationContext().getSharedPreferences("information",
				Context.MODE_PRIVATE);
		downloadNum = sp.getInt("downloadNum", 2);
		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.SAVEDATA);
		registerReceiver(myReceiver, filter);
		super.onCreate();
	}

	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		unregisterReceiver(myReceiver);
		return super.stopService(name);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub

		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		String path = intent.getExtras().getString("path");
		System.out.println("path-->" + path);
		if (!fileService.isTaskNotFinish(path)) {
			if (map_task.get(path) == null) {
				if (map_task.size() < downloadNum) {
					download(path);
					showMessage("开始下载");
				} else {
					showMessage("已超过最大下载数！正在下载" + downloadNum + "个文件!");
					// int fileSize=intent.getExtras().getInt("fileSize");
					// fileService.saveNoFinishFile(path,fileSize);
				}
			} else {
				showMessage("任务正在下载中");
			}
		} else {
			showMessage("已经存在此任务");
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void showMessage(String content) {
		Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 下载
	 * 
	 * @param path
	 *            下载地址
	 */
	private void download(String path) {// 运行在主线程
		DownloadTask task = new DownloadTask(path);
		Thread t = new Thread(task);
		t.start();
		map_task.put(path, task);
	}

	/**
	 * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
	 * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
	 */
	public final class DownloadTask implements Runnable {
		private String path;
		private File saveDir;
		private FileDownloader loader;
		private String fileName;

		private DownloadFile downloadFile;
		private int fileSize = 0;
		private int downLength = 0;

		public DownloadTask(String path) {

			this.path = path;
			this.saveDir = new File(Constants_Url.OTHERFILE_PACH);
			downloadFile = new DownloadFile();
			if (path != null) {
				fileName = path.substring((path.lastIndexOf("/") + 1),
						path.length());
				downloadFile.setFileName(fileName);
				downloadFile.setDownLoadAddress(path);
			}
		}

		/**
		 * 退出下载(记录了下载长度)
		 */
		public void exit() {
			fileService.saveNoFinishFile(path, fileSize);
			// System.out.println("downLength-->"+downLength);
			fileService.updateNoFinishFileLength(path, downLength);
			if (loader != null)
				loader.exit();
			map_task.remove(path);
			System.out.println();
			if (listeners != null) {
				listeners.progressChange(false);
			}
		}

		/**
		 * 退出并删除下载长度
		 */
		public void delete() {
			if (loader != null)
				loader.exit();
			fileService.delete(path);
			fileService.deleteFinishFile(path);
			map_task.remove(path);
			if (listeners != null) {
				listeners.progressChange(true);
			}
		}

		public DownloadFile getDownloadFile() {
			return downloadFile;
		}

		public void run() {
			try {

				// 多线程
				loader = new FileDownloader(getApplicationContext(), path,
						saveDir, 3);
				// pd.setMax((loader.getFileSize()/1024));//设置进度条的最大刻度
				fileSize = loader.getFileSize() / 1024;

				downloadFile.setFileSize(fileSize);

				loader.download(new DownloadProgressListener() {
					public void onDownloadSize(int size) {
						size = size / 1024;
						downLength = size;
						// progressSize+=size;
						// System.out.println("size-->"+size);
						downloadFile.setDownloadSize(size);
						if (listeners != null
								&& downloadFile.getFileSize() != size) {
							listeners.progressChange(false);
						} else if (listeners != null) {
							Looper.prepare();
							map_task.remove(path);
							showMessage("下载" + fileName + "成功!");
							showMessage("文件路径:" + saveDir.getAbsolutePath()
									+ "/" + fileName);
							delete();
							Looper.loop();
							// fileService.deleteFinishFile(path);

						}

					}
				});

			} catch (Exception e) {
				System.out.println(e.toString());
				Looper.prepare();
				delete();
				showMessage("错误的下载地址!");
				// Toast.makeText(getApplicationContext(), "错误的下载地址!",
				// Toast.LENGTH_LONG).show();
				Looper.loop();

			}
		}
	}

}

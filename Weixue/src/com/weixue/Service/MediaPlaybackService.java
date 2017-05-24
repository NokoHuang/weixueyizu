
package com.weixue.Service;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.ViewGroup;

import com.weixue.Control.MyFloatView;
import com.weixue.weixueUI.VideoPlayerActivity;
/**
 * 悬浮视频播放服务类
 *
 */
public class MediaPlaybackService extends Service {
	//悬浮层布局
	public ViewGroup fView;
	//悬浮视频处理类
	public MyFloatView sFloatView;
	 private String filePathForJson="";
	/**
	 * 创建方法
	 */
	public void onCreate() {
		super.onCreate();
	}

	private void createView(Context context) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.putExtra("path", filePathForJson);
		
		intent.setClass(getApplicationContext(), VideoPlayerActivity.class);
		startActivity(intent);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			filePathForJson=intent.getExtras().getString("path");
			if ("createUI".equals(action)) {
				createView(this);
			} 
			else if ("removeUI".equals(action)) {
				fView = null;
				sFloatView = null;
			}
		}
		return START_STICKY;
	}

	boolean iPlayState = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

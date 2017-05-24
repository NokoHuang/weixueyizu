package com.weixue.Control;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

import com.weixue.Function.GetDisplay;
import com.weixue.Service.MediaPlaybackService;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.VideoPlayerActivity;
/**
 * 悬浮视频处理类
 *
 */
public class MyFloatView {
	private float mTouchStartX;
	private float mTouchStartY;
	public ImageView close,changeSize;
	private float x;
	private float y;
	public static ViewGroup mlayoutView;
	static Context context;
	static Display currentDisplay;
	public static SurfaceView surfaceView;
	Button mButton;
	static MediaPlayer mediaPlayer;// 使用的是MediaPlayer来播放视频
	int videoWidth = 0; // 视频的宽度，初始化，后边会对其进行赋值
	int videoHeight = 0; // 同上
	int flag = 0;
	boolean readyToPlayer = false;
	static int VIEW_WIDTH,VIEW_HEIGHT;
	public final static String LOGCAT = "CUSTOM_VIDEO_PLAYER";

	public static final String ACTION_DESTROY_MOVIE = "com.lnc.send.movie.Destroy";

	public MyFloatView(ViewGroup layoutView,int display_flag) {
		mlayoutView = layoutView;
		context = mlayoutView.getContext();
		//给悬浮层添加手势监听
		mlayoutView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
				onTouchEvent(paramMotionEvent);
				return false;
			}
		});

		initWindow(display_flag);
	}

	public View getLayoutView() {
		return mlayoutView;
	}

	public void onResume() {
	}

	//初始化悬浮窗体
	public static void initWindow(int display_flag) {
		// 获取WindowManager
		wm = (WindowManager) context.getApplicationContext().getSystemService(
				"window");
		wmParams = new WindowManager.LayoutParams();
		
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT
				| LayoutParams.TYPE_SYSTEM_OVERLAY; // 设置window type
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.TRANSPARENT;
		// 设置Window flag
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		currentDisplay = wm.getDefaultDisplay();
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		
		if(display_flag ==0){
			wmParams.gravity = Gravity.CENTER | Gravity.TOP; // 调整悬浮窗口至左上角
			wmParams.width = GetDisplay.displayWidth-15;
			VIEW_WIDTH = GetDisplay.displayWidth-15;
			wmParams.height = GetDisplay.displayWidth/2+40;
			VIEW_HEIGHT = GetDisplay.displayWidth/2+40;
		}
		else{
			wmParams.gravity = Gravity.CENTER;
			wmParams.width = GetDisplay.displayWidth;
			VIEW_WIDTH =  GetDisplay.displayWidth;
			wmParams.height = GetDisplay.displayHeight;
			VIEW_HEIGHT = GetDisplay.displayHeight;
		}
	}
	

	public void bindViewListener() {
		initialUI();
	}

	//初始化悬浮层视图
	private void initialUI() {
		surfaceView = (SurfaceView) mlayoutView.findViewById(R.id.myView);
	}

	//关闭悬浮层
	public static void onExit() {
		try {
			wm.removeView(mlayoutView);
			Intent mIntent = new Intent("removeUI");
			mIntent.setClass(context, MediaPlaybackService.class);
			context.stopService(mIntent);
			mediaPlayer.pause();
			mediaPlayer.stop();
			mediaPlayer.release();
			System.exit(0);
		} 
		catch (Exception e) {
			
		}
	}
	
	public static void display_switch(){
		wm.removeView(mlayoutView);
	}


	public void showLayoutView() {
		wm.addView(mlayoutView, wmParams);

	}

	public static  WindowManager wm = null;
	public static WindowManager.LayoutParams wmParams = null;

	public boolean onTouchEvent(MotionEvent event) {
		
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY(); // 25是系统状态栏的高度
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 获取相对View的坐标，即以此View左上角为原点
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			
			break;
			
			
		case MotionEvent.ACTION_MOVE:
			updateViewPosition();
			break;

		case MotionEvent.ACTION_UP:

			if (!VideoPlayerActivity.isControllerShow) {// 是否显示控制器
				VideoPlayerActivity.showController();// 显示控制器
				VideoPlayerActivity.hideControllerDelay();// 延迟隐藏控制器
			} else {
				VideoPlayerActivity.cancelDelayHide();// 取消隐藏延迟
				VideoPlayerActivity.hideController();// 隐藏控制器
			}

			break;
		default:
			break;
		}
		return false;
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(mlayoutView, wmParams);

	}

}
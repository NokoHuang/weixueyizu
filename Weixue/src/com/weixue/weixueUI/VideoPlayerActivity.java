package com.weixue.weixueUI;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Control.MyFloatView;
import com.weixue.Control.SoundView;
import com.weixue.Control.SoundView.OnVolumeChangedListener;
import com.weixue.Control.VideoView;
import com.weixue.Control.VideoView.MySizeChangeLinstener;
import com.weixue.Function.GetDisplay;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.CourseWare;
import com.weixue.Utils.Constants_Url;

public class VideoPlayerActivity extends Activity {

	private final static String TAG = "VideoPlayerActivity";
	private static boolean isOnline = false; //是否在线播放
	private boolean isChangedVideo = false;//是否改变视频

	public static LinkedList<MovieInfo> playList = new LinkedList<MovieInfo>();//视频信息集合
	public class MovieInfo{
		String displayName;//视频名称  
		String path;//视频路径
	}
	private Uri videoListUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;//SDCard媒体的URI
	private static int position ;//定位
	private int playedTime;//已播放时间

	private static VideoView vv = null; //视频视图
	private static SeekBar seekBar = null;//可拖拽的进度条  
	private TextView durationTextView = null;//视频的总时间
	private static TextView playedTextView = null;//播放时间
	private GestureDetector mGestureDetector = null;//手势识别
	private AudioManager mAudioManager = null; //音频管理

	private int maxVolume = 0;//最大声音
	private int currentVolume = 0;//当前声音  

	private ImageButton bn1 = null;//全屏、标准
	private ImageButton bn2 = null;//上一个
	private ImageButton bn3 = null;//播放、暂停
	private ImageButton bn4 = null;//下一个
	private ImageButton bn5 = null;//声音
	private ImageButton bn6=null;

	private View controlView = null;//控制器视图
	private static PopupWindow controler = null;//控制器

	private SoundView mSoundView = null;//声音视图
	private static PopupWindow mSoundWindow = null;//声音控制器


	private static int screenWidth = 0;//屏幕宽度
	private static int screenHeight = 0;//屏幕高度
	private static int controlHeight = 0; // 控制器高度

	private final static int TIME = 6868;//控制器显示持续时间(毫秒)  

	public static boolean isControllerShow = true;//是否显示控制器
	private boolean isPaused = false;//是否暂停
	private boolean isFullScreen = false;//是否全屏
	private boolean isSilent = false;//是否静音
	private static boolean isSoundShow = false;//是否显示声音


	// 悬浮层布局
	public ViewGroup fView;
	// 悬浮视频处理类
	public MyFloatView sFloatView;


	private String filePathForJson="";
	private String fileName="";
	private List<CourseWare> li_courseWare;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);  

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.floadbg_page);

		filePathForJson=this.getIntent().getExtras().getString("path");


		fileName=Wanzheng_Course_video_playbackActivity.playingVideoFileName;

		//        li_courseWare=ResolveJSON.JSON_TO_CourseWareArray(filePathForJson);
		//        for(int i=0;i<li_courseWare.size();i++){
		MovieInfo info=new MovieInfo();
		info.displayName=fileName;
		info.path=filePathForJson;
		playList.add(info);
		//   }
		//        MovieInfo info=new MovieInfo();
		//    	info.displayName="hoot.mp4";
		//    	info.path=filePathForJson;
		//    	playList.add(info);

		fView = (ViewGroup) View.inflate(getApplicationContext(), R.layout.mainforvideo, null);
		// 显示myFloatView图像

		sFloatView = new MyFloatView(fView,0);

		sFloatView.bindViewListener();
		sFloatView.showLayoutView();

		vv = (VideoView) MyFloatView.surfaceView;

		/**
		 * 向消息队列中添加一个新的MessageQueue.IdleHandler。
		 * 当调用IdleHandler.queueIdle()返回false时，
		 * 此MessageQueue.IdleHandler会自动的从消息队列中移除。
		 */
		Looper.myQueue().addIdleHandler(new IdleHandler(){

			public boolean queueIdle() {//空闲的队列
				if(controler != null && vv.isShown()){//controler控制器(PopupWindow)
					controler.showAtLocation(vv, Gravity.BOTTOM,0,0);//屏幕底部显示控制器
					controler.update(0, 0, screenWidth, controlHeight);//控制器宽、高
				}
				return false;  
			}
		});

		controlView = getLayoutInflater().inflate(R.layout.controler, null);

		controler = new PopupWindow(controlView);
		durationTextView = (TextView) controlView.findViewById(R.id.duration);
		playedTextView = (TextView) controlView.findViewById(R.id.has_played);

		mSoundView = new SoundView(this);
		mSoundView.setOnVolumeChangeListener(new OnVolumeChangedListener(){
			public void setYourVolume(int index) {
				cancelDelayHide();//取消隐藏延迟
				updateVolume(index);//更新音量
				hideControllerDelay();//延迟隐藏控制器
			}
		});

		mSoundWindow = new PopupWindow(mSoundView);

		position = -1;

		bn1 = (ImageButton) controlView.findViewById(R.id.button1);
		bn2 = (ImageButton) controlView.findViewById(R.id.button2);
		bn3 = (ImageButton) controlView.findViewById(R.id.button3);
		bn4 = (ImageButton) controlView.findViewById(R.id.button4);
		bn5 = (ImageButton) controlView.findViewById(R.id.button5);
		bn6 = (ImageButton) controlView.findViewById(R.id.button6);

		vv.setOnErrorListener(new OnErrorListener(){
			public boolean onError(MediaPlayer mp, int what, int extra) {
				vv.stopPlayback();//停止视频播放
				isOnline = false;

				new AlertDialog.Builder(VideoPlayerActivity.this)
				.setTitle("对不起")
				.setMessage("您所播的视频格式不正确，播放已停止。")
				.setPositiveButton("知道了",
						new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						vv.stopPlayback();
					}
				})
				.setCancelable(false)
				.show();
				return false;
			}

		});

		Uri uri = getIntent().getData();
		if(uri!=null){
			vv.stopPlayback();//停止视频播放
			vv.setVideoURI(uri);//设置视频文件URI
			isOnline = true;

			bn3.setImageResource(R.drawable.pause);
		}else{
			bn3.setImageResource(R.drawable.play);
		}

		//        getVideoFile(playList, new File("/sdcard/"));//获得视频文件
		//        
		//        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
		//        	//将sdcard中的视频信息存放到LinkedList<MovieInfo>集合中
		//        	
		//        	Cursor cursor = getContentResolver().query(videoListUri, new String[]{"_display_name","_data"}, null, null, null);
		//            int n = cursor.getCount();
		//            cursor.moveToFirst();
		//            LinkedList<MovieInfo> playList2 = new LinkedList<MovieInfo>();
		//            for(int i = 0 ; i != n ; ++i){
		//            	MovieInfo mInfo = new MovieInfo();
		//            	mInfo.displayName = cursor.getString(cursor.getColumnIndex("_display_name"));
		//            	mInfo.path = cursor.getString(cursor.getColumnIndex("_data"));
		//            	playList2.add(mInfo);
		//            	cursor.moveToNext();
		//            }
		//            
		//            if(playList2.size() > playList.size()){
		//            	playList = playList2;//视频信息集合
		//            }
		// }


		vv.setMySizeChangeLinstener(new MySizeChangeLinstener(){
			public void doMyThings() {
				setVideoScale(SCREEN_DEFAULT);//设置视频显示尺寸
			}
		});

		bn6.setAlpha(0xBB);      
		bn2.setAlpha(0xBB);  
		bn3.setAlpha(0xBB);
		bn4.setAlpha(0xBB);
		bn1.setAlpha(0xBB);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		bn5.setAlpha(findAlphaFromSound());//设置声音按键透明度

		//选择视频
		bn1.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//				Intent intent = new Intent();
				//				intent.setClass(VideoPlayerActivity.this, VideoChooseActivity.class);
				//				VideoPlayerActivity.this.startActivityForResult(intent, 0);
				//				cancelDelayHide();//取消隐藏延迟


				//				setVideoScale(SCREEN_FULL);

				MyFloatView.onExit();
				finish();


				//setVideoScale(SCREEN_DEFAULT);

			}

		});

		bn6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "===", Toast.LENGTH_SHORT).show();
				//////////
				String path=null;
				if(position<0){
					path=playList.get(0).path;
				}else{
					path=playList.get(position).path;
				}

				//System.out.println("++++\n"+playList.get(0).path+"+++\n"+position);
				Intent i=new Intent(getApplicationContext(),VideoFullScreenActivity.class);
				i.putExtra("videoPath", path);
				startActivity(i);

				MyFloatView.onExit();
				finish();

			}
		});


		//下一个
		bn4.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int n = playList.size();
				isOnline = false;
				if(++position < n){
					vv.setVideoPath(playList.get(position).path);
					//vv.setVideoPath("http://222.126.246.151:9111/Content/Video/hoot.mp4");
					cancelDelayHide();//取消隐藏延迟
					hideControllerDelay();//延迟隐藏控制器
				}else{
					VideoPlayerActivity.this.finish();
				}
			}

		});

		bn3.setImageResource(R.drawable.pause);

		if(playList.size()>0){
			vv.setVideoPath(playList.get(0).path);
			vv.start();
			cancelDelayHide();//取消隐藏延迟
			hideControllerDelay();//延迟隐藏控制器
			Toast.makeText(getApplicationContext(), "开始加载视频，耐心等待...", 1).show();
		}else{
			Toast.makeText(getApplicationContext(), "没有视频资源!", 1).show();
		}

		//播放或者暂停
		bn3.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				cancelDelayHide();//取消隐藏延迟
				if(isPaused){
					vv.start();
					bn3.setImageResource(R.drawable.pause);
					hideControllerDelay();//延迟隐藏控制器
				}else{
					vv.pause();
					bn3.setImageResource(R.drawable.play);
				}
				isPaused = !isPaused;

			}

		});

		//上一个视频
		bn2.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				isOnline = false;
				if(--position>=0){
					vv.setVideoPath(playList.get(position).path);
					cancelDelayHide();//取消隐藏延迟
					hideControllerDelay();//延迟隐藏控制器
				}else{
					VideoPlayerActivity.this.finish();
				}
			}
		});

		//声音控制面板
		bn5.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				cancelDelayHide();//取消隐藏延迟
				if(isSoundShow){
					mSoundWindow.dismiss();//SoundWindow销毁(不显示)
				}else{
					if(mSoundWindow.isShowing()){
						mSoundWindow.update(15,0,SoundView.MY_WIDTH,SoundView.MY_HEIGHT);
					}
					else{
						mSoundWindow.showAtLocation(vv, Gravity.RIGHT|Gravity.CENTER_VERTICAL, 15, 0);
						mSoundWindow.update(15,0,SoundView.MY_WIDTH,SoundView.MY_HEIGHT);
					}
				}
				isSoundShow = !isSoundShow;
				hideControllerDelay();//延迟隐藏控制器
			}   
		});

		//长按开关音量
		bn5.setOnLongClickListener(new OnLongClickListener(){
			public boolean onLongClick(View arg0) {
				if(isSilent){
					bn5.setImageResource(R.drawable.soundcontrol);
				}else{
					bn5.setImageResource(R.drawable.soundmute);
				}
				isSilent = !isSilent;
				updateVolume(currentVolume);
				cancelDelayHide();//取消隐藏延迟
				hideControllerDelay();//延迟隐藏控制器
				return true;
			}
		});

		//进度条
		seekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				if(fromUser){
					vv.seekTo(progress);//设置播放位置
				}
			}
			public void onStartTrackingTouch(SeekBar arg0) {
				myHandler.removeMessages(HIDE_CONTROLER);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}
		});

		getScreenSize();//获得屏幕尺寸大小

		//手势监听
		mGestureDetector = new GestureDetector(new SimpleOnGestureListener(){
			//双击屏幕
			public boolean onDoubleTap(MotionEvent e) {
				if(isFullScreen){
					setVideoScale(SCREEN_DEFAULT);//设置视频显示尺寸
				}
				else{
					setVideoScale(SCREEN_FULL);//设置视频显示尺寸
				}
				isFullScreen = !isFullScreen;
				Log.d(TAG, "onDoubleTap");

				if(isControllerShow){
					showController();//显示控制器
				}
				//return super.onDoubleTap(e);
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {//轻击屏幕
				if(!isControllerShow){//是否显示控制器
					showController();//显示控制器
					hideControllerDelay();//延迟隐藏控制器
				}
				else {
					cancelDelayHide();//取消隐藏延迟
					hideController();//隐藏控制器
				}
				//return super.onSingleTapConfirmed(e);
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {//长按屏幕
				if(isPaused){
					vv.start();
					bn3.setImageResource(R.drawable.pause);
					cancelDelayHide();//取消隐藏延迟
					hideControllerDelay();//延迟隐藏控制器
				}else{
					vv.pause();
					bn3.setImageResource(R.drawable.play);
					cancelDelayHide();//取消隐藏延迟
					showController();//显示控制器
				}
				isPaused = !isPaused;
				//super.onLongPress(e);
			}	
		});


		vv.setOnPreparedListener(new OnPreparedListener(){//注册在媒体文件加载完毕，可以播放时调用的回调函数
			public void onPrepared(MediaPlayer arg0) {//加载
				setVideoScale(SCREEN_DEFAULT);
				isFullScreen = false; 
				if(isControllerShow){
					showController();//显示控制器  
				}
				int i = vv.getDuration();//获得所播放视频的总时间
				Log.d("onCompletion", ""+i);
				seekBar.setMax(i);
				i/=1000;
				int minute = i/60;
				int hour = minute/60;
				int second = i%60;
				minute %= 60;
				durationTextView.setText(String.format("%02d:%02d:%02d", hour,minute,second));

				vv.start();  
				bn3.setImageResource(R.drawable.pause);
				hideControllerDelay();//延迟隐藏控制器
				myHandler.sendEmptyMessage(PROGRESS_CHANGED);
			}	
		});

		vv.setOnCompletionListener(new OnCompletionListener(){//注册在媒体文件播放完毕时调用的回调函数
			public void onCompletion(MediaPlayer arg0) {
				int n = playList.size();
				isOnline = false;
				if(++position < n){
					vv.setVideoPath(playList.get(position).path);
				}
				else{
					vv.stopPlayback();
					VideoPlayerActivity.this.finish();
				}
			}
		});


		Intent intent = new Intent("com.example.weixue.wanzhengvideo");
		startActivity(intent);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0&&resultCode==Activity.RESULT_OK){

			vv.stopPlayback();//停止视频播放

			int result = data.getIntExtra("CHOOSE", -1);
			Log.d("RESULT", ""+result);
			if(result!=-1){
				isOnline = false;
				isChangedVideo = true;
				vv.setVideoPath(playList.get(result).path);
				position = result;
			}else{
				String url = data.getStringExtra("CHOOSE_URL");
				if(url != null){
					vv.setVideoPath(url);//设置视频文件路径
					isOnline = true;
					isChangedVideo = true;
				}
			}

			return ;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;

	static Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){

			case PROGRESS_CHANGED://进度改变

				int i = vv.getCurrentPosition();
				seekBar.setProgress(i);

				if(isOnline){
					int j = vv.getBufferPercentage();
					seekBar.setSecondaryProgress(j * seekBar.getMax() / 100);
				}else{
					seekBar.setSecondaryProgress(0);
				}

				i/=1000;
				int minute = i/60;
				int hour = minute/60;
				int second = i%60;
				minute %= 60;
				playedTextView.setText(String.format("%02d:%02d:%02d", hour,minute,second));

				sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
				break;

			case HIDE_CONTROLER://隐藏控制器
				hideController();//隐藏控制器
				break;
			}

			super.handleMessage(msg);
		}	
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {//实现该方法来处理触屏事件
		boolean result = mGestureDetector.onTouchEvent(event);
		if(!result){
			if(event.getAction()==MotionEvent.ACTION_UP){

				/*if(!isControllerShow){
					showController();
					hideControllerDelay();
				}else {
					cancelDelayHide();
					hideController();
				}*/
			}
			result = super.onTouchEvent(event);
		}

		return result;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		getScreenSize();//获得屏幕尺寸大小
		if(isControllerShow){
			cancelDelayHide();//取消隐藏延迟
			hideController();//隐藏控制器
			showController();//显示控制器
			hideControllerDelay();//延迟隐藏控制器
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		playedTime = vv.getCurrentPosition();
		vv.pause();
		bn3.setImageResource(R.drawable.play);
		super.onPause();   
	}

	@Override
	protected void onResume() {//恢复挂起的播放器
		if(!isChangedVideo){
			vv.seekTo(playedTime);//设置播放位置   playedTime已播放时间
			vv.start();  
		}else{
			isChangedVideo = false;
		}

		//if(vv.getVideoHeight()!=0){
		if(vv.isPlaying()){
			bn3.setImageResource(R.drawable.pause);
			hideControllerDelay();//延迟隐藏控制器
		}
		Log.d("REQUEST", "NEW AD !");

		//		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){//设置屏幕横屏
		//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {//销毁
		if(controler.isShowing()){
			controler.dismiss();//控制器销毁(释放资源)
			//			extralWindow.dismiss();
		}
		if(mSoundWindow.isShowing()){
			mSoundWindow.dismiss();//声音控制器销毁(释放资源)
		}

		myHandler.removeMessages(PROGRESS_CHANGED);//进度改变 释放
		myHandler.removeMessages(HIDE_CONTROLER);//隐藏控制器  释放

		if(vv.isPlaying()){
			vv.stopPlayback();//停止视频播放
		}

		playList.clear();//视频信息集合 清空

		super.onDestroy();
	}     

	private void getScreenSize()//获得屏幕尺寸大小
	{
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth()-16;
		controlHeight = screenHeight/11;

	}

	public static  void hideController(){//隐藏控制器
		if(controler.isShowing()){
			controler.update(0,0,0, 0);
			isControllerShow = false;
		}
		if(mSoundWindow.isShowing()){
			mSoundWindow.dismiss();
			isSoundShow = false;
		}
	}

	public static void hideControllerDelay(){//延迟隐藏控制器
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	public static void showController(){//显示控制器
		controler.update(0,0,screenWidth, controlHeight);

		isControllerShow = true;
	}

	public static void cancelDelayHide(){//取消隐藏延迟
		myHandler.removeMessages(HIDE_CONTROLER);
	}

	private final static int SCREEN_FULL = 0;
	private final static int SCREEN_DEFAULT = 1;

	private void setVideoScale(int flag){//设置视频显示尺寸

		switch(flag){
		case SCREEN_FULL://全屏

			screenWidth = GetDisplay.displayWidth;

			MyFloatView.display_switch();

			sFloatView = new MyFloatView(fView,1);

			sFloatView.bindViewListener();

			sFloatView.showLayoutView();

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			break;

		case SCREEN_DEFAULT://标准

			int videoWidth = vv.getVideoWidth();
			int videoHeight = vv.getVideoHeight();
			int mWidth = screenWidth;
			int mHeight = screenHeight - 25;

			if (videoWidth > 0 && videoHeight > 0) {
				if ( videoWidth * mHeight  > mWidth * videoHeight ) {
					//Log.i("@@@", "image too tall, correcting");
					mHeight = mWidth * videoHeight / videoWidth;
				} else if ( videoWidth * mHeight  < mWidth * videoHeight ) {
					//Log.i("@@@", "image too wide, correcting");
					mWidth = mHeight * videoWidth / videoHeight;
				} else {

				}
			}

			vv.setVideoScale(mWidth, mHeight);

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			break;
		}
	}


	private int findAlphaFromSound(){//设置声音按键透明度
		if(mAudioManager!=null){
			int alpha = currentVolume * (0xCC-0x55) / maxVolume + 0x55;
			return alpha;
		}else{
			return 0xCC;
		}
	}

	private void updateVolume(int index){//更新音量
		if(mAudioManager!=null){
			if(isSilent){//是否静音
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			}else{
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
			}
			currentVolume = index;
			bn5.setAlpha(findAlphaFromSound());
		}
	}

	private void getVideoFile(final LinkedList<MovieInfo> list,File file){//获得视频文件

		file.listFiles(new FileFilter(){

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				String name = file.getName();
				int i = name.indexOf('.');
				if(i != -1){
					name = name.substring(i);
					if(name.equalsIgnoreCase(".mp4")||name.equalsIgnoreCase(".3gp")||name.equalsIgnoreCase(".wmv")||name.equalsIgnoreCase(".flv")){

						MovieInfo mi = new MovieInfo();
						mi.displayName = file.getName();
						mi.path = file.getAbsolutePath();
						list.add(mi);
						return true;
					}
				}else if(file.isDirectory()){
					getVideoFile(list, file);
				}
				return false;
			}
		});
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				Intent intent = new Intent("com.example.weixue.wanzhengvideo");
				startActivity(intent);
			}
			return true;
		} else
			return super.dispatchKeyEvent(event);
	}

}
package com.weixue.weixueUI;

import java.io.File;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Model.Units;
import com.weixue.MyDialog.CourseBrightspotActivity;
import com.weixue.MyDialog.CourseScoreActivity;
import com.weixue.MyDialog.TryToSeeAndEnrollDialog;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R.string;

/**
 * 视频播放（）
 * @author chenjunjie
 *
 */
public class Course_video_playbackActivity extends Activity implements OnRatingBarChangeListener{
	private Context mContext;

	private static final int PLAY_VIDEO = 0;
	private static final int SHOW_MESSAGE = 1;

	private static final int ISCOLLECTTHISCOURSE = 2;
	private static final int COLLECT_COURSE = 3;
	private static final int CANCEL_COLLECT=4;

	private static final int GOFULLSCREEN = 5;

	private static final int SETVIDEO_BG = 6;

	private ImageView play;
	private LinearLayout play_bg;
	private VideoView videoView,full;
	private RatingBar rate;
	private float  rateNum;
	private String rating_;
	private ListView video_list;
	private LinearLayout baodu;
	private LinearLayout line_collect;
	private TextView tv_collect;
	private LinearLayout mLayoutBreak;

	private TextView tv_introduction;
	private ImageView img_course;
	private String introduction,courseImgUrl;

	private int type=1;
	private int pageIndex=1;
	private int pageCount=0;
	private int cid=0;

	private Course course;
	private int howMuch;//课程单价


	private PersonInformation person;

	int count=0;
	long firClick,secClick;

	private int width,height;

	private String videoPath="";//视频地址

	private boolean isConnectioning=false;//判断是否正在连接网络
	private int video_state=3;//表示获取视频的url
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.course_video_playback_page);
		init();
		getAllUnits(cid,false);
	}


	/**
	 * 获取视频缩略图
	 * @param videoPath
	 * @param width
	 * @param height
	 * @param kind
	 * @return
	 */
	private Bitmap getVideoThumbnail(String path,int width , int height, int kind){
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(path, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}


	/**
	 * 加载视频列表数据
	 */
	//	private void loadDate() {
	//		VideoListAdapter adapter = new VideoListAdapter(getApplicationContext());
	//		video_list.setAdapter(adapter);
	//	}

	/**
	 * 初始化
	 */
	private void init() {

		mContext=this;

		play = (ImageView) findViewById(R.id.play);
		play.setOnClickListener(OnClickListener);
		play_bg = (LinearLayout) findViewById(R.id.play_bg);
		videoView = (VideoView) findViewById(R.id.videoview);
		video_list = (ListView) findViewById(R.id.video_list);
		baodu = (LinearLayout) findViewById(R.id.baodu);
		baodu.setOnClickListener(OnClickListener);
		tv_collect=(TextView) findViewById(R.id.tv_collect);
		line_collect=(LinearLayout) findViewById(R.id.line_collect);

		mLayoutBreak=(LinearLayout) findViewById(R.id.line_back);
		mLayoutBreak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Course_video_playbackActivity.this.finish();
			}
		});

		tv_introduction=(TextView) findViewById(R.id.tv_introduction);
		tv_introduction.setMovementMethod(ScrollingMovementMethod.getInstance());

		img_course=(ImageView) findViewById(R.id.img_videoImg);
		img_course.setOnClickListener(OnClickListener);

		person=ResolveJSON.JSON_To_PersonInformation(MyInstance.getSharedPreferencesString(mContext,Constants.PERSON_INFORMATION));

		course=(Course) this.getIntent().getExtras().getSerializable("course");
		introduction=course.getIntroduction();
		tv_introduction.setText(Html.fromHtml(introduction));
		courseImgUrl=course.getCourse_ImgUrl();
		cid=course.getCourseID();
		howMuch=course.getCourse_LearnMoney();

		rate = (RatingBar) findViewById(R.id.app_ratingbar);
		//rate.setOnRatingBarChangeListener(this);
		rate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("cid", cid);
				intent.setClass(Course_video_playbackActivity.this,CourseScoreActivity.class);
				startActivity(intent);
				return true;
			}
		});
		float floatNum=Float.valueOf(course.getPraiseDegree())/2;
		rate.setRating(floatNum);

		if(courseImgUrl!=null&&courseImgUrl!=""){
			String fileName=courseImgUrl.substring(courseImgUrl.lastIndexOf("/")+1, courseImgUrl.length());
			File file=new File(Constants_Url.PIC_CACHE_PATH+"/"+fileName);			
			if(file.exists()){			
				img_course.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
			}
		}

		isCollectThisCourse(person.getUserId(),cid);
	}

	/**
	 * 获取评分
	 */
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		rating_ = String.valueOf(rating);
	}

	/**
	 * 根据课程ID获取此课程所有单元名与单元ID
	 * @param cid 课程ID
	 */
	private void getAllUnits(final int cid,final boolean isGotoPlay){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.GETALLUNITS_BYCOURSEID+"?courseid="+cid;
				try {
					String response=NetWork.getData(requestUrl);
					switch(ResolveJSON.IsHasResult(response)){
					case 1:
						List<Units> li_units=ResolveJSON.JSON_TO_UnitsArray(response);	
						//System.out.println("====\n"+response);
						getCoueseWareUrlByUnitsId(li_units.get(0).getUnitID(),video_state,GOFULLSCREEN,isGotoPlay);
						break;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			
		new Thread(run).start();
	}

	/**
	 * 根据单元ID获取课件URL
	 * @param unitsId
	 * @param state
	 * @param what
	 * @param isGotoPlay 是否播放
	 */
	private void getCoueseWareUrlByUnitsId(final int unitsId,final int state,final int what,final boolean isGotoPlay){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				isConnectioning=true;
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.GETCOURSEWARE_BYUNITSID+"?unitid="+unitsId+"&state="+state;
				try {
					String jsonStr=NetWork.getData(requestUrl);
					switch(ResolveJSON.IsHasResult(jsonStr)){
					case 1:
						Response response=ResolveJSON.JSON_To_Response(jsonStr);
						JSONArray arr=new JSONArray(response.getResult());
						
						
						videoPath=arr.getString(0);
						if(isGotoPlay){
							handler.sendMessage(handler.obtainMessage(what, videoPath));
						}else{
							Log.e("测试1", "asdsad");
							Bitmap b=getVideoThumbnail(videoPath,300,300,1);
							handler.sendMessage(handler.obtainMessage(SETVIDEO_BG, b));
						}
						break;
					case 0:
						if(isGotoPlay){
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.noResources)));
						}
						break;
					default:
						break;
					}
					isConnectioning=false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					isConnectioning=false;
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.networkerror)));
				}
			}};
			new Thread(run).start();
	}


	/** 添加点击监听 */
	OnClickListener OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.play:
				
				if(videoPath.trim()!=""){
					Log.e("测试播放1", "测试");
					handler.sendMessage(handler.obtainMessage(GOFULLSCREEN, videoPath));
				}else{
					//课程ID
					Toast.makeText(getApplicationContext(), "没有资源", Toast.LENGTH_LONG).show();
					getAllUnits(cid,true);
				}
				break;
				//课程按钮
			case R.id.baodu:
				
				TryToSeeAndEnrollDialog.Builder dialogBuilder=new TryToSeeAndEnrollDialog.Builder(mContext,howMuch,person.getLearnCents());
				dialogBuilder.setDeleteButtonClickListener(new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				dialogBuilder.setEnrollButtonClickListener(new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						PersonAddCourse(person.getUserId(),cid);
						dialog.dismiss();
					}
				});
				dialogBuilder.setTryToSeeButtonClickListener(new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						if(videoPath.trim()!=""){
							handler.sendMessage(handler.obtainMessage(GOFULLSCREEN, videoPath));
						}
						else{
							Toast.makeText(getApplicationContext(), "没有资源", Toast.LENGTH_LONG).show();
						}
						dialog.dismiss();
						
					}
				});
				TryToSeeAndEnrollDialog dialog=dialogBuilder.create();

				dialog.show();
				break;
				//设置图书图片
			case R.id.img_videoImg:
				Course.course=course;	
				Intent intent=new Intent(mContext,CourseBrightspotActivity.class);
				intent.putExtra("content", Course.course.getDetailIntro());
				startActivity(intent);
				break;
			}
		}
	};

	/**
	 * 判断是否已经收藏过了课程
	 * @param uid
	 * @param cid
	 */
	private void isCollectThisCourse(final int uid,final int cid){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url=Constants_Url.ISCOLLECT_COURSE+"?uid="+uid+"&cid="+cid;

				try {
					String response=NetWork.getData(url);

					handler.sendMessage(handler.obtainMessage(ISCOLLECTTHISCOURSE, response));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			new Thread(run).start();
	}


	/**
	 * 用户加入课程
	 * @param uid
	 * @param cid
	 */
	public void PersonAddCourse(final int uid,final int cid){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url=Constants_Url.ADDCOURSE+"?uid="+uid+"&cid="+cid;
				try {
					String jsonStr=NetWork.getData(url);
					switch(ResolveJSON.IsHasResult(jsonStr)){
					case 1:
						Response response=ResolveJSON.JSON_To_Response(jsonStr);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response.getResult()));
						break;
					case 0:
						Response responseStr=ResolveJSON.JSON_To_Response(jsonStr);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, responseStr.getMessage()));
						break;
					default:
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.error)));
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			new Thread(run).start();
	}

	/**
	 * 用户取消收藏课程
	 * @param uid
	 * @param cid
	 */
	public void cancelCollectCourse(final int uid,final int cid){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url=Constants_Url.CANCELCOLLECT_COURSE+"?uid="+uid+"&cid="+cid;

				try {
					String jsonStr=NetWork.getData(url);
					switch(ResolveJSON.IsHasResult(jsonStr)){
					case 1:
						Response response=ResolveJSON.JSON_To_Response(jsonStr);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, response.getMessage()));
						handler.sendEmptyMessage(CANCEL_COLLECT);
						break;
					case 0:
						Response responseStr=ResolveJSON.JSON_To_Response(jsonStr);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, responseStr.getMessage()));
						break;
					default:
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.error)));
						//Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			new Thread(run).start();
	}






	/**
	 * 收藏课程
	 * @param uid
	 * @param cid
	 */
	public void collectCourse(final int uid,final int cid){
		Runnable run=new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.COLLECT_COURSE+"?uid="+uid+"&cid="+cid;

				try {
					String jsonStr=NetWork.getData(requestUrl);

					handler.sendMessage(handler.obtainMessage(COLLECT_COURSE, jsonStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.networkerror)));
				}
			}};
			new Thread(run).start();
	}
	//监听收藏课程按钮
	OnClickListener collectListener=new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			collectCourse(person.getUserId(),cid);
			//System.out.println("加入收藏");
		}};
		//监听取消收藏按钮
		OnClickListener cancelCollectListener=new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cancelCollectCourse(person.getUserId(),cid);
				//System.out.println("取消收藏");	
			}};



			Handler handler=new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch(msg.what){
					case PLAY_VIDEO:{
						Intent i=new Intent(mContext,VideoFullScreenActivity.class);
						startActivity(i);
						break;
					}//上一个case结束
					case SHOW_MESSAGE:{
						
						String content=(String) msg.obj;
						Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
						
						break;
					}//上一个case结束
					case ISCOLLECTTHISCOURSE:{
						String response=(String) msg.obj;
						try {
							switch(ResolveJSON.IsHasResult(response)){
							case 1:
								//						JSONArray arr=new JSONArray(ResolveJSON.JSON_To_Response(response).getResult());
								//						boolean mark=arr.getBoolean(0);							
								tv_collect.setText(getApplicationContext().getResources().getString(R.string.cancelCollect));

								line_collect.setOnClickListener(cancelCollectListener);

								break;
							case 0:

								line_collect.setOnClickListener(collectListener);
								break;					
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;

					}//上一个case结束
					case COLLECT_COURSE:{
						String jsonStr=(String) msg.obj;
						try {
							switch(ResolveJSON.IsHasResult(jsonStr)){
							case 1:
								Response response=ResolveJSON.JSON_To_Response(jsonStr);
								Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
								tv_collect.setText(getApplicationContext().getResources().getString(R.string.cancelCollect));
								line_collect.setOnClickListener(cancelCollectListener);
								break;
							case 0:
								Response responseStr=ResolveJSON.JSON_To_Response(jsonStr);
								Toast.makeText(mContext, responseStr.getMessage(), Toast.LENGTH_SHORT).show();
								break;
							default:
								Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}//上一个case结束
					case CANCEL_COLLECT:{
						tv_collect.setText(getApplicationContext().getResources().getString(R.string.Collection));

						line_collect.setOnClickListener(collectListener);
						break;
					}//上一个case结束
					case GOFULLSCREEN:{
						String path=(String) msg.obj;

						String firstpath=path.substring(0,path.lastIndexOf("/")+1);					
						String oldfileName=path.substring(path.lastIndexOf("/")+1);
						String newfileName=Uri.encode(oldfileName);

						//System.out.println("====\n"+path);
						/*Intent i=new Intent(mContext,VideoFullScreenActivity.class);
						i.putExtra("videoPath", firstpath+newfileName);
						startActivity(i);*/
						
						//System.out.println(firstpath+newfileName);
						Intent it = new Intent(Intent.ACTION_VIEW);
						Uri uri = Uri.parse(firstpath+newfileName);
						it.setDataAndType(uri, "video/*");
						startActivity(it);
						
						break;
					}
					case SETVIDEO_BG:{
						Bitmap b=(Bitmap) msg.obj;
						play_bg.setBackgroundDrawable(new BitmapDrawable(b));
						break;
					}
					}
					super.handleMessage(msg);
				}

			};
}

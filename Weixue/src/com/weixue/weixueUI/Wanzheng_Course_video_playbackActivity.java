package com.weixue.weixueUI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.weixue.Adapter.VideoListAdapter;
import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.CourseWare;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Model.Units;
import com.weixue.MyDialog.CourseBrightspotActivity;
import com.weixue.MyDialog.CourseScoreActivity;
import com.weixue.MyDialog.iphoneDialog;
import com.weixue.NewUI.JiaoAnFragment;
import com.weixue.NewUI.MoenFragment;
import com.weixue.NewUI.PPTFragment;
import com.weixue.NewUI.VidaoFragment;
import com.weixue.Service.DownLoadService;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 视频播放（完整）
 * 
 * @author luzhanggang & chenjunjie
 * 
 */
public class Wanzheng_Course_video_playbackActivity extends FragmentActivity
		implements OnRatingBarChangeListener {

	private static final int SET_DETAIL = 0;
	private static final int SETUNITS = 1;
	private static final int SHOW_MESSAGE = 2;
	private static final int COLLECT_COURSE = 3;
	private static final int STARTDOWNLOADFILE = 4;// 开始下载文件
	private static final int ISCOLLECTTHISCOURSE = 5;
	private static final int FOR_PLAY = 6;
	private static final int FOR_PPT = 7;
	private static final int FOR_LESSONPLAN = 8;
	private static final int CANCELCOLLECT = 9;

	private Context mContext;
	private RatingBar rate;
	private ImageView img_download;

	// 简介(显示),收藏(按钮),分享(按钮)
	private TextView tv_introduction, tv_collect, tv_share;

	private TextView mTextVdieo, mTextPPT, mTextJiaoAN, mTextMoen;
	private ImageView mImgScr;
	private ViewPager mViewPager;
	private FragmentPagerAdapter mFragmentPagerAdapter;
	private List<Fragment> mFragments;
	private int mScreen1_4;
	private int mCurrentPageIndex;
	private LinearLayout mLayoutBreak;

	private ImageView img_video;
	private String introduction;
	private String courseImgUrl = "";
	private int cid = 0;
	private List<CourseWare> li_units;

	Map<Integer, Integer> map;
	String[] wareItems;
	boolean[] mark_idChecked;
	private int selectingUnits = 0;// 记录选择了哪条数据
	private VideoListAdapter adapter;
	// 获取用户信息

	private PersonInformation person;

	private UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);
	private Activity mActivity;

	public static String playingVideoFileName = "";// 正在播放或马上要播放的文件名

	private boolean isConnectioning = false;// 判断是否正在连接网络

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wanzheng_course_video_playback_page);
		
		/*Display display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);*/
		mScreen1_4 = Constants.full_display_width/ 4;
		
		init();
		
	}

	/**
	 * 
	 * 加载视频列表数据
	 * 
	 */
	private void loadData(List<CourseWare> li_units) {
		
		/*
		 * adapter= new VideoListAdapter(getApplicationContext(),li_units);
		 * lv_videoList.setAdapter(adapter);
		 */
		VidaoFragment vidaoFragment = new VidaoFragment(li_units);
		PPTFragment pptFragment = new PPTFragment(li_units);
		JiaoAnFragment jiaoAnFragment = new JiaoAnFragment(li_units);
		//课程ID，用户ID
		MoenFragment moenFragment = new MoenFragment(li_units, this, cid,person.getUserId());
		mFragments.add(vidaoFragment);
		mFragments.add(pptFragment);
		mFragments.add(jiaoAnFragment);
		mFragments.add(moenFragment);
		
		mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mFragments.get(arg0);
			}
		};
		
		mViewPager.setAdapter(mFragmentPagerAdapter);

		mTextVdieo.setOnClickListener(OnClickText);
		mTextPPT.setOnClickListener(OnClickText);
		mTextJiaoAN.setOnClickListener(OnClickText);
		mTextMoen.setOnClickListener(OnClickText);

	}

	OnClickListener OnClickText = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.text_video:
				mViewPager.setCurrentItem(0);
				break;
			case R.id.text_ppt:
				mViewPager.setCurrentItem(1);
				break;
			case R.id.text_jiaoan:
				mViewPager.setCurrentItem(2);
				break;
			case R.id.text_moen:
				mViewPager.setCurrentItem(3);
				break;
			}
		}
	};

	/**
	 * 初始化
	 */
	private void init() {
		li_units=new ArrayList<CourseWare>();
		
		mActivity = this;
		mContext = this;

		map = new HashMap<Integer, Integer>();
		wareItems = new String[4];
		wareItems[0] = "word";
		wareItems[1] = "ppt";
		wareItems[2] = "视频";
		wareItems[3] = "其它";

		img_download = (ImageView) findViewById(R.id.img_download);
		img_download.setOnClickListener(OnClickListener);

		tv_collect = (TextView) findViewById(R.id.tv_collect);
		tv_collect.setOnClickListener(OnClickListener);

		tv_share = (TextView) findViewById(R.id.tv_share);
		tv_share.setOnClickListener(OnClickListener);

		rate = (RatingBar) findViewById(R.id.app_ratingbar);
		// rate.setOnRatingBarChangeListener(this);
		// rate.setOnClickListener(OnClickListener);
		rate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent = new Intent();
				intent.putExtra("cid", cid);
				intent.setClass(Wanzheng_Course_video_playbackActivity.this,
						CourseScoreActivity.class);
				startActivity(intent);
				return true;
			}
		});

		tv_introduction = (TextView) findViewById(R.id.tv_introduction);
		tv_introduction
				.setMovementMethod(ScrollingMovementMethod.getInstance());

		img_video = (ImageView) findViewById(R.id.videoImg);
		img_video.setOnClickListener(OnClickListener);

		person = ResolveJSON.JSON_To_PersonInformation(MyInstance
				.getSharedPreferencesString(mContext,
						Constants.PERSON_INFORMATION));

		if (Course.course != null) {
			cid = Course.course.getCourseID();
			introduction = Course.course.getDetailIntro();
			tv_introduction.setText(Html.fromHtml(introduction));
			courseImgUrl = Course.course.getCourse_ImgUrl();
			float floatNum = Course.course.getPraiseDegree() / 2;
			rate.setRating(floatNum);
			// getCourseDetail(Course.list.get(0).getCourseID(),type,pageIndex,pageCount,SET_VIDEOWARE);
			// getAllUnits(Course.course.getCourseID());
			getCourseDetail(Course.course.getCourseID(), 0, 1, 20, SETUNITS);
			isCollectThisCourse(person.getUserId(), Course.course.getCourseID());
			// System.out.println("----->   "+person.getUserId());
		}

		if (courseImgUrl != null && courseImgUrl != "") {

			String fileName = courseImgUrl.substring(
					courseImgUrl.lastIndexOf("/") + 1, courseImgUrl.length());
			File file = new File(Constants_Url.PIC_CACHE_PATH + "/" + fileName);

			if (file.exists()) {
				img_video.setImageDrawable(Drawable.createFromPath(file
						.getAbsolutePath()));
			}
		}

		mLayoutBreak = (LinearLayout) findViewById(R.id.line_back);
		mLayoutBreak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				Wanzheng_Course_video_playbackActivity.this.finish();
			}
		});

		mTextVdieo = (TextView) findViewById(R.id.text_video);
		mTextPPT = (TextView) findViewById(R.id.text_ppt);
		mTextJiaoAN = (TextView) findViewById(R.id.text_jiaoan);
		mTextMoen = (TextView) findViewById(R.id.text_moen);
		mImgScr = (ImageView) findViewById(R.id.img_scr);
		LayoutParams lp = mImgScr.getLayoutParams();
		lp.width = mScreen1_4;
		mImgScr.setLayoutParams(lp);
		mViewPager = (ViewPager) findViewById(R.id.pager_our);
		mFragments = new ArrayList<Fragment>();

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				resetTestView();
				switch (position) {
				case 0:
					mTextVdieo.setTextColor(Color.parseColor("#08a5f7"));
					break;
				case 1:
					mTextPPT.setTextColor(Color.parseColor("#08a5f7"));
					break;
				case 2:
					mTextJiaoAN.setTextColor(Color.parseColor("#08a5f7"));
					break;
				case 3:
					mTextMoen.setTextColor(Color.parseColor("#08a5f7"));
					break;

				}
				mCurrentPageIndex = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPx) {
				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mImgScr
						.getLayoutParams();
				if (mCurrentPageIndex == 0 && position == 0) {// 0->1
					lp.leftMargin = (int) (positionOffset * mScreen1_4 + mCurrentPageIndex
							* mScreen1_4);
				} else if (mCurrentPageIndex == 1 && position == 0) {// 1->0
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + (positionOffset - 1)
							* mScreen1_4);
				} else if (mCurrentPageIndex == 1 && position == 1) {// 1->2
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + positionOffset
							* mScreen1_4);
				} else if (mCurrentPageIndex == 2 && position == 1) {// 2->1
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + (positionOffset - 1)
							* mScreen1_4);
				} else if (mCurrentPageIndex == 2 && position == 2) {// 2->3
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + positionOffset
							* mScreen1_4);
				} else if (mCurrentPageIndex == 3 && position == 2) {// 3->2
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + (positionOffset - 1)
							* mScreen1_4);
				}
				mImgScr.setLayoutParams(lp);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	protected void resetTestView() {
		mTextVdieo.setTextColor(Color.BLACK);
		mTextPPT.setTextColor(Color.BLACK);
		mTextJiaoAN.setTextColor(Color.BLACK);
		mTextMoen.setTextColor(Color.BLACK);
	}

	/**
	 * 获取评分
	 */
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		Intent intent = new Intent();
		intent.setClass(Wanzheng_Course_video_playbackActivity.this,
				CourseScoreActivity.class);
		startActivity(intent);

	}

	/**
	 * 判断是否已经收藏过了课程
	 * 
	 * @param uid
	 * @param cid
	 */
	private void isCollectThisCourse(final int uid, final int cid) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Constants_Url.ISCOLLECT_COURSE + "?uid=" + uid
						+ "&cid=" + cid;

				try {
					String response = NetWork.getData(url);
					handler.sendMessage(handler.obtainMessage(
							ISCOLLECTTHISCOURSE, response));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * 用户取消收藏课程
	 * 
	 * @param uid
	 * @param cid
	 */
	public void cancelCollectCourse(final int uid, final int cid) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Constants_Url.CANCELCOLLECT_COURSE + "?uid=" + uid
						+ "&cid=" + cid;

				try {
					String jsonStr = NetWork.getData(url);
					switch (ResolveJSON.IsHasResult(jsonStr)) {
					case 1:
						Response response = ResolveJSON
								.JSON_To_Response(jsonStr);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
								response.getMessage()));
						handler.sendEmptyMessage(CANCELCOLLECT);
						break;
					case 0:
						Response responseStr = ResolveJSON
								.JSON_To_Response(jsonStr);
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
								responseStr.getMessage()));
						break;
					default:
						handler.sendMessage(handler.obtainMessage(
								SHOW_MESSAGE,
								mContext.getResources().getString(
										R.string.error)));
						// Toast.makeText(mContext,
						// mContext.getResources().getString(R.string.error),
						// Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * get请求课程详细信息及视频课件信息
	 * 
	 * @param cid
	 * @param type
	 * @param pageIndex
	 * @param pageCount
	 * @param what
	 */
	public void getCourseDetail(final int cid, final int type,
			final int pageIndex, final int pageCount, final int what) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					// 获取课件
					String url = Constants_Url.GETCOURSEWARE_BYCOURSEID
							+ "?cid=" + cid + "&type=" + type + "&pageIndex="
							+ pageIndex + "&pageCount=" + pageCount;
					// System.out.println(url);
					String jsonStrForWare = NetWork.getData(url);
					// System.out.println(jsonStrForWare);
					handler.sendMessage(handler.obtainMessage(what,
							jsonStrForWare));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	/** 添加点击监听 */
	OnClickListener OnClickListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.img_download:
				if (li_units != null && li_units.size() > 0) {
					map = new HashMap<Integer, Integer>();
					mark_idChecked = new boolean[4];
					showCheckWareDialog();
				} else {
					handler.sendMessage(handler.obtainMessage(
							SHOW_MESSAGE,
							getApplicationContext().getResources().getString(
									R.string.nodata)));
				}

				break;
			case R.id.tv_collect:
				collectCourse(person.getUserId(), cid);
				break;
			case R.id.tv_share:
				// 打开分享平台选择面板
				mController.openShare(mActivity, false);
				break;
			case R.id.videoImg:
				if (Course.course != null) {
					Intent intent = new Intent(mContext,
							CourseBrightspotActivity.class);
					intent.putExtra("content", Course.course.getDetailIntro());
					startActivity(intent);
				}
				break;
			}
		}
	};

	/**
	 * 收藏课程
	 * 
	 * @param uid
	 * @param cid
	 */
	public void collectCourse(final int uid, final int cid) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl = Constants_Url.COLLECT_COURSE + "?uid="
						+ uid + "&cid=" + cid;

				try {
					String jsonStr = NetWork.getData(requestUrl);

					handler.sendMessage(handler.obtainMessage(COLLECT_COURSE,
							jsonStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.sendMessage(handler.obtainMessage(
							SHOW_MESSAGE,
							mContext.getResources().getString(
									R.string.networkerror)));
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * 根据课程ID获取此课程所有单元名与单元ID
	 * 
	 * @param cid
	 *            课程ID
	 */
	private void getAllUnits(final int cid) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl = Constants_Url.GETALLUNITS_BYCOURSEID
						+ "?courseid=" + cid;

				// http://222.126.246.151:9120/app/courseservices/coursewares/units?courseid=cid
				// http://222.126.246.151:9120/app/courseservices/coursewares/units?courseid=1

				System.out.println("requestUrl" + requestUrl);
				try {
					String response = NetWork.getData(requestUrl);
					System.out.println("response" + response);
					handler.sendMessage(handler.obtainMessage(SETUNITS,
							response));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * 根据单元ID获取课件URL
	 * 
	 * @param unitsId
	 * @param state
	 * @param what
	 */
	private void getCoueseWareUrlByUnitsId(final int unitsId, final int state,
			final int what) {
		Runnable run = new Runnable() {
			// http://222.126.246.151:9120/app/courseservices/coursewares/uac?unitid=4&state=3
			@Override
			public void run() {
				isConnectioning = true;
				// TODO Auto-generated method stub
				String requestUrl = Constants_Url.GETCOURSEWARE_BYUNITSID
						+ "?unitid=" + unitsId + "&state=" + state;
				// System.out.println("requestUrl-->"+requestUrl);
				try {
					String jsonStr = NetWork.getData(requestUrl);
					// System.out.println(response);
					switch (ResolveJSON.IsHasResult(jsonStr)) {
					case 1:
						Response response = ResolveJSON
								.JSON_To_Response(jsonStr);
						JSONArray arr = new JSONArray(response.getResult());
						String path = arr.getString(0);
						// System.out.println("path-->"+path);
						// handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
						// "开始下载"+wareItems[state-1]+"资源"));
						handler.sendMessage(handler.obtainMessage(what, path));
						break;
					case 0:

						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
								"暂时没有" + wareItems[state - 1] + "资源"));
						break;
					default:
						break;
					}
					isConnectioning = false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					isConnectioning = false;
					handler.sendMessage(handler.obtainMessage(
							SHOW_MESSAGE,
							getApplicationContext().getResources().getString(
									R.string.networkerror)));
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * 根据单元ID获取课件URL
	 * 
	 * @param unitsId
	 * @param state
	 * @param what
	 */
	private void getCoueseWareUrlByUnitsIdToDownLoad(final int unitsId,
			final int state, final int what) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				isConnectioning = true;
				// TODO Auto-generated method stub
				String requestUrl = Constants_Url.GETCOURSEWARE_BYUNITSID
						+ "?unitid=" + unitsId + "&state=" + state;
				// System.out.println("requestUrl-->"+requestUrl);
				try {
					String jsonStr = NetWork.getData(requestUrl);
					// System.out.println(response);
					switch (ResolveJSON.IsHasResult(jsonStr)) {
					case 1:
						Response response = ResolveJSON
								.JSON_To_Response(jsonStr);
						JSONArray arr = new JSONArray(response.getResult());
						String path = arr.getString(0);
						// System.out.println("path-->"+path);
						// handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
						// "开始下载"+wareItems[state-1]+"资源"));
						handler.sendMessage(handler.obtainMessage(what, path));
						break;
					case 0:
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
								"暂时没有" + wareItems[state - 1] + "资源"));
						break;
					default:
						break;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.sendMessage(handler.obtainMessage(
							SHOW_MESSAGE,
							getApplicationContext().getResources().getString(
									R.string.networkerror)));
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * 显示下载文件选择对话框
	 */
	public void showCheckWareDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				Wanzheng_Course_video_playbackActivity.this)
				.setTitle("请选择你要下载选中单元的哪种文件:")
				.setMultiChoiceItems(wareItems, mark_idChecked,
						new OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								// TODO Auto-generated method stub
								if (isChecked) {
									map.put(which, which + 1);
								} else {
									map.remove(which);
								}
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Set<Integer> set = map.keySet();
						Object[] ob = set.toArray();
						for (int i = 0; i < ob.length; i++) {

							// getCoueseWareUrlByUnitsIdToDownLoad(li_units.get(selectingUnits).getUnitID(),map.get(ob[i]),STARTDOWNLOADFILE);
						}
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * 返回键监听
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
			return true;
		} else
			return super.dispatchKeyEvent(event);
	}

	// 监听收藏课程按钮
	OnClickListener collectListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			collectCourse(person.getUserId(), cid);
			// System.out.println("加入收藏");
		}
	};
	// 监听取消收藏按钮
	OnClickListener cancelCollectListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			cancelCollectCourse(person.getUserId(), cid);
			// System.out.println("取消收藏");
		}
	};

	// 创建Handler对象
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SET_DETAIL: {
				String jsonStr = (String) msg.obj;
				try {
					switch (ResolveJSON.IsHasResult(jsonStr)) {
					case 1:

						Course course = ResolveJSON.JSON_TO_Course(jsonStr);
						tv_introduction.setText(course.getIntroduction());
						break;
					case 0:
						Response response = ResolveJSON
								.JSON_To_Response(jsonStr);
						Toast.makeText(mContext, response.getMessage(),
								Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.error), Toast.LENGTH_SHORT)
								.show();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case SETUNITS: {
				String jsonStrForWare = (String) msg.obj;

				try {
					switch (ResolveJSON.IsHasResult(jsonStrForWare)) {
					case 1:
						li_units = ResolveJSON
								.JSON_TO_CourseWareArray(jsonStrForWare);
						loadData(li_units);
						Log.e("测试", "asdasdsad");
						break;
					case 0:
						Response response = ResolveJSON
								.JSON_To_Response(jsonStrForWare);
						/*Toast.makeText(mContext, response.getMessage(),
								Toast.LENGTH_SHORT).show();*/
						loadData(li_units);
						Log.e("有没有值", response.getMessage());
						
						
						break;
					default:
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.error), Toast.LENGTH_SHORT)
								.show();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case SHOW_MESSAGE: {
				String content = (String) msg.obj;
				Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
				break;
			}
			case COLLECT_COURSE: {
				String jsonStr = (String) msg.obj;
				try {
					switch (ResolveJSON.IsHasResult(jsonStr)) {
					case 1:
						Response response = ResolveJSON
								.JSON_To_Response(jsonStr);
						Toast.makeText(mContext, response.getMessage(),
								Toast.LENGTH_SHORT).show();
						tv_collect.setText(mContext.getResources().getString(
								R.string.cancelCollect));
						tv_collect.setOnClickListener(cancelCollectListener);
						break;
					case 0:
						Response responseStr = ResolveJSON
								.JSON_To_Response(jsonStr);
						Toast.makeText(mContext, responseStr.getMessage(),
								Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.error), Toast.LENGTH_SHORT)
								.show();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case STARTDOWNLOADFILE: {
				String path = (String) msg.obj;
				Intent intent = new Intent(mContext, DownLoadService.class);
				intent.putExtra("path", path);
				mContext.startService(intent);
				break;
			}
			case ISCOLLECTTHISCOURSE: {
				String response = (String) msg.obj;
				try {
					switch (ResolveJSON.IsHasResult(response)) {
					case 1:
						tv_collect.setText(getApplicationContext()
								.getResources().getString(
										R.string.cancelCollect));
						tv_collect.setOnClickListener(cancelCollectListener);
						break;
					case 0:
						tv_collect.setOnClickListener(collectListener);
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			}
			case FOR_PLAY: {
				String path = (String) msg.obj;
				isConnectioning = false;
				if (path != null && path != "") {
					String firstpath = path.substring(0,
							path.lastIndexOf("/") + 1);
					String oldfileName = path
							.substring(path.lastIndexOf("/") + 1);
					String newfileName = Uri.encode(oldfileName);

					// System.out.println("path   "+path);
					// System.out.println(firstpath+newfileName);//http://222.126.246.151:9105/Content/Video/Courseware/201405161041_test.mp4

					/*
					 * Intent intent=new Intent();
					 * intent.setClass(getApplicationContext(),
					 * FloatVideoActivity.class);
					 * //intent.setClass(getApplicationContext(),
					 * VideoViewDemo.class);
					 * intent.putExtra("path",firstpath+newfileName);
					 * //System.out.println("--->"+firstpath+newfileName);
					 * playingVideoFileName=oldfileName; startActivity(intent);
					 */
					// finish();

					playingVideoFileName = oldfileName;
					Intent it = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(firstpath + newfileName);
					// Uri uri =
					// Uri.parse("http://222.126.246.151:9105/Content/Video/Courseware/201405190127.mp4");
					// System.out.println("uri:"+uri);
					it.setDataAndType(uri, "video/*");
					Wanzheng_Course_video_playbackActivity.this
							.startActivity(it);

					/*
					 * Intent intent = new Intent();
					 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * intent.putExtra("path", firstpath+newfileName);
					 * playingVideoFileName=oldfileName;
					 * intent.setClass(getApplicationContext(),
					 * VideoPlayerActivity.class);
					 * Wanzheng_Course_video_playbackActivity
					 * .this.startActivity(intent);
					 */

				}
				break;
			}
			case FOR_PPT: {
				String path = (String) msg.obj;
				isConnectioning = false;
				if (path != null && path != "") {
					String fileName = path.substring(path.lastIndexOf("/") + 1);
					File file = new File(Constants_Url.OTHERFILE_PACH + "/"
							+ fileName);
					if (!file.exists()) {
						showIsdownloadDialog(path);
					} else {
						Intent intent = new Intent();
						intent.putExtra("ms", "ppt");
						intent.setClass(
								Wanzheng_Course_video_playbackActivity.this,
								StartOtherApp.class);
						startActivity(intent);
					}
				}
				break;
			}
			case FOR_LESSONPLAN: {
				String path = (String) msg.obj;
				isConnectioning = false;
				if (path != null && path != "") {
					String fileName = path.substring(path.lastIndexOf("/") + 1);
					File file = new File(Constants_Url.OTHERFILE_PACH + "/"
							+ fileName);
					if (!file.exists()) {
						showIsdownloadDialog(path);
					} else {
						Intent intent = new Intent();
						intent.putExtra("ms", "word");
						intent.setClass(
								Wanzheng_Course_video_playbackActivity.this,
								StartOtherApp.class);
						startActivity(intent);
					}
				}
				break;
			}
			case CANCELCOLLECT: {
				tv_collect.setText(mContext.getResources().getString(
						R.string.Collection));
				tv_collect.setOnClickListener(collectListener);
				break;
			}

			}
			super.handleMessage(msg);
		}

		/**
		 * 选择是否下载对话框
		 * 
		 * @param path
		 *            下载地址
		 */
		private void showIsdownloadDialog(final String path) {
			// TODO Auto-generated method stub
			iphoneDialog.iphoneDialogBuilder idb = new iphoneDialog.iphoneDialogBuilder(
					mContext);
			idb.setTitle(getApplicationContext().getResources().getString(
					R.string.prompt));
			idb.setMessage(getApplicationContext().getResources().getString(
					R.string.fileNotExist));
			idb.setPositiveButton(getApplicationContext().getResources()
					.getString(R.string.sure),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(mContext,
									DownLoadService.class);
							intent.putExtra("path", path);
							mContext.startService(intent);
						}
					});
			idb.setNegativeButton(getApplicationContext().getResources()
					.getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			idb.show();
		}

	};

}

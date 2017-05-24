package com.weixue.weixueUI;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Adapter.PersionAdapter;
import com.weixue.Adapter.TypeAdapter;
import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.DownLoadFile;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Methods.UploadMessage;
import com.weixue.Model.Course;
import com.weixue.Model.InforTypeModel;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.MyDialog.CourseBrightspotActivity;
import com.weixue.NewUI.ChooseActivity;
import com.weixue.NewUI.MarketActivity;
import com.weixue.NewUI.PlanningActivity;
import com.weixue.NewUI.TrainingActivity;
import com.weixue.Tool.ChartView;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 个人中心
 * 
 * @author luzhanggang && chenjunjie
 * 
 */
public class PersonalCenterActivity extends Activity implements
		android.view.View.OnClickListener {

	private Context mContext;
	private static final int SETDATA = 8;
	/** 设置头像 */
	private static final int SETHEAD = 0;
	/** 吐司显示信息 */
	private static final int SHOW_MESSAGE = 1;
	/** 设置加入的课程 */
	private static final int SETCOURSE = 2;
	/** 增加更多数据 */
	private static final int MOREITEM = 3;
	/** 签到后更新学币 */
	private static final int UPDATE = 4;
	/** 加入的课程 及初始化显示数量 */
	public List<Course> li_course = null;
	private int pageIndex = 1;
	private int pageCount = 0;
	private int Count = 15;
	public boolean isNotDownLoad = true;// 标记是否已经正在下载内容（true为没有下载，false为正在下载中）

	private LinearLayout Ly_charview;
	private ChartView chartView;

	private PersionAdapter adapter;
	private View footerView_loading;// 加载更多时显示等待
	private LinearLayout line_loading;

	private ListView lv_myClass, lv_cer;
	private LinearLayout mLayoutPro;
	private ImageView mLayoutChoose, mLayoutMarket, mLayoutTraining,
			mLayoutPlanning;
	private TextView mTextMyclass, mTextChoose, mTextCertificate;
	private ScrollView mScrollView;
	private List<InforTypeModel> li_Type;
	private TypeAdapter adapterType;

	// 签到按钮
	private ImageButton CheckIn;

	private MyReceiver myReceiver;
	// 头像
	private ImageView img_head;
	// 姓名、等级、签名、学币、学分
	private TextView tv_name, tv_level, tv_signatrue, tv_learnCents,
			tv_learnMoney;
	// 学币、学分
	private View v_showLearnCents, v_showLearnMoney;
	private boolean[] mark = new boolean[2];// 用来标记控件高度只获取一次
	// 用户信息
	private PersonInformation person;

	private TextView tv_showUser_sign;
	private final static int DIALOG_SEARCH = 0x100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.per_page);

		init();
		// loadChartView();

	}

	/** 加载列表数据 */
	private void loadData(List<Course> li) {
		lv_myClass.addFooterView(footerView_loading);


		adapter = new PersionAdapter(getApplicationContext(), li);
		lv_myClass.setAdapter(adapter);

		lv_myClass.removeFooterView(footerView_loading);
		line_loading.setVisibility(View.GONE);
		lv_myClass.setVisibility(View.VISIBLE);
	}

	private void loadType(List<InforTypeModel> typeModels) {
		lv_cer.addFooterView(footerView_loading);
		adapterType = new TypeAdapter(mContext, li_Type);
		lv_cer.setAdapter(adapterType);
		lv_cer.removeFooterView(footerView_loading);

		line_loading.setVisibility(View.GONE);
		lv_cer.setVisibility(View.VISIBLE);
	}

	boolean hasMeasured = false;

	/** 初始化控件 */
	private void init() {
		mContext = this;
		Ly_charview = (LinearLayout) findViewById(R.id.chatview);
		lv_myClass = (ListView) findViewById(R.id.per_lesson_list1);
		lv_myClass.setOnItemClickListener(onitemclicklistener);
		lv_myClass.setOnScrollListener(new MyScrollListener());

		lv_cer = (ListView) findViewById(R.id.per_lesson_certificate);
		mLayoutPro = (LinearLayout) findViewById(R.id.line_professional);

		mLayoutChoose = (ImageView) findViewById(R.id.layout_choose);
		mLayoutMarket = (ImageView) findViewById(R.id.layout_market);
		mLayoutTraining = (ImageView) findViewById(R.id.layout_training);
		mLayoutPlanning = (ImageView) findViewById(R.id.layout_planning);
		mLayoutChoose.setOnClickListener(this);
		mLayoutMarket.setOnClickListener(this);
		mLayoutTraining.setOnClickListener(this);
		mLayoutPlanning.setOnClickListener(this);
		// 设置职业选择界面的布局
		SetProfessionLayout();

		mTextMyclass = (TextView) findViewById(R.id.text_myclass);
		mTextChoose = (TextView) findViewById(R.id.text_choose);
		mTextCertificate = (TextView) findViewById(R.id.text_certificate);
		mTextMyclass.setOnClickListener(this);
		mTextChoose.setOnClickListener(this);
		mTextCertificate.setOnClickListener(this);
		// mScrollView=(ScrollView) findViewById(R.id.scrollView1);

		footerView_loading = LayoutInflater.from(mContext).inflate(
				R.layout.loading_and_refesh_page, null);
		line_loading = (LinearLayout) findViewById(R.id.line_loading);

		CheckIn = (ImageButton) findViewById(R.id.imgbtn_checkin);
		CheckIn.setOnClickListener(OnClickListener);

		person = ResolveJSON.JSON_To_PersonInformation(MyInstance
				.getSharedPreferencesString(mContext,
						Constants.PERSON_INFORMATION));
		
		//获取头像实例
		img_head = (ImageView) findViewById(R.id.img_head);
		img_head.setOnClickListener(OnClickListener);
		
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(person.getUsername());
		tv_level = (TextView) findViewById(R.id.tv_level);
		tv_level.setText(String.valueOf(person.getLevel()));
		tv_signatrue = (TextView) findViewById(R.id.tv_signature);
		tv_signatrue.setText(person.getSignature());
		tv_signatrue.setOnClickListener(OnClickListener);
		tv_showUser_sign = (TextView) findViewById(R.id.tv_showUser_sign);
		tv_showUser_sign.setOnClickListener(OnClickListener);

		tv_learnMoney = (TextView) findViewById(R.id.tv_learnMoney);
		tv_learnMoney.setText(String.valueOf(person.getLearnMoney()));
		tv_learnCents = (TextView) findViewById(R.id.tv_learnCents);
		tv_learnCents.setText(String.valueOf(person.getLearnCents()));

		v_showLearnMoney = findViewById(R.id.v_showLearnMoney);
		setControlHeigth(v_showLearnMoney, person.getLearnMoney(), 0);

		v_showLearnCents = findViewById(R.id.v_showLearnCents);
		setControlHeigth(v_showLearnCents, person.getLearnCents(), 1);

		File fileMkdir = new File(Constants_Url.PIC_USERHEAD_PATH);
		if (!fileMkdir.exists()) {
			fileMkdir.mkdirs();
		}
		String fileName = person.getLargePhotoPath().substring(
				person.getLargePhotoPath().lastIndexOf("/") + 1);
		
		String strUrl = Constants_Url.FIRST_PATH
				+ person.getLargePhotoPath().substring(
						person.getLargePhotoPath().indexOf("/", 7));
		
		if((fileMkdir.getAbsolutePath()+"/"+fileName)!=""){
			downLoadHeadPhoto(strUrl,fileMkdir.getAbsolutePath()+"/"+fileName);
		}//else
			//img_head.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_user));
		//
		getCourseArray(person.getUserId(), pageIndex, pageCount, SETCOURSE);
		getData(SETDATA);
		upDate();
		
		//刷新用户已经更改的用户头像
		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.UPDATE_USERHEAD);
		registerReceiver(myReceiver, filter);
	}
	
	/**
	 * 广播，通知用户头像已经更改
	 * @author Administrator
	 *
	 */
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			if (action.equals(Constants.UPDATE_USERHEAD)) {
				person = ResolveJSON.JSON_To_PersonInformation(MyInstance
						.getSharedPreferencesString(mContext,
								Constants.PERSON_INFORMATION));
				
				String fileName = person.getLargePhotoPath().substring(
						person.getLargePhotoPath().lastIndexOf("/") + 1);
				
				File file = new File(Constants_Url.PIC_USERHEAD_PATH + "/"
						+ fileName);
				if (file.exists()) {
					img_head.setImageBitmap(BitmapFactory.decodeFile(file
							.getAbsolutePath()));
					
					MyInstance.SaveLoginUserPhoto(PersonalCenterActivity.this, file.getAbsolutePath());
				}
			}
		}
	}

	public void SetProfessionLayout() {
		// 计算左边图片和底部图片的比重（宽度除以高度）
		float proportionItem1 = 120 / 160.0f;
		float proportionItem3 = 280 / 80.0f;

		// 获取左边，图片宽度
		int item1Width = Math.round(((Constants.float_display_width - Constants
				.dip2px(this, 30)) / 2.0f));
		// 获取左边，图片高度
		int item1Height = Math.round(item1Width / proportionItem1);

		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayoutMarket
				.getLayoutParams();
		lp.width = item1Width;
		lp.height = item1Height;
		mLayoutMarket.setLayoutParams(lp);

		// 获取右边图片，高度
		int item2Height = Math
				.round((item1Height - Constants.dip2px(this, 10)) / 2.0f);

		lp = (LinearLayout.LayoutParams) mLayoutPlanning.getLayoutParams();
		lp.width = item1Width;
		lp.height = item2Height;
		mLayoutPlanning.setLayoutParams(lp);

		lp = (LinearLayout.LayoutParams) mLayoutTraining.getLayoutParams();
		lp.width = item1Width;
		lp.height = item2Height;
		mLayoutTraining.setLayoutParams(lp);

		int item3Width = Constants.full_display_width
				- Constants.dip2px(this, 20);
		lp = (LinearLayout.LayoutParams) mLayoutChoose.getLayoutParams();
		lp.width = item3Width;
		lp.height = Math.round(item3Width / proportionItem3);
		mLayoutChoose.setLayoutParams(lp);
	}

	@Override
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.layout_choose:
			intent = new Intent(PersonalCenterActivity.this,
					ChooseActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_market:
			intent = new Intent(PersonalCenterActivity.this,
					MarketActivity.class);
			intent.putExtra("typeid", 2);
			startActivity(intent);
			break;
		case R.id.layout_planning:
			intent = new Intent(PersonalCenterActivity.this,
					PlanningActivity.class);
			intent.putExtra("typeid", 3);
			startActivity(intent);
			break;
		case R.id.layout_training:
			intent = new Intent(PersonalCenterActivity.this,
					TrainingActivity.class);
			intent.putExtra("typeid", 4);
			startActivity(intent);
			break;

		case R.id.text_myclass:
			if (numbText != 1) {
				numbText = 1;
				loadContent();
				resetTestView();
				mTextMyclass.setTextColor(Color.parseColor("#08a5f7"));
			}
			break;
		case R.id.text_choose:
			if (numbText != 2) {
				numbText = 2;
				loadContent();
				resetTestView();
				mTextChoose.setTextColor(Color.parseColor("#08a5f7"));
			}
			break;
		case R.id.text_certificate:
			if (numbText != 3) {
				numbText = 3;
				loadContent();
				resetTestView();
				mTextCertificate.setTextColor(Color.parseColor("#08a5f7"));
			}
			break;
		}
	}

	int numbText = 1;

	private void loadContent() {
		if (numbText == 1) {
			if (li_course != null) {
				lv_cer.setVisibility(View.GONE);
				mLayoutPro.setVisibility(View.GONE);
				lv_myClass.setVisibility(View.VISIBLE);
				loadData(li_course);
				// mScrollView.fullScroll(ScrollView.FOCUS_UP);
			} else {
				lv_cer.setVisibility(View.GONE);
				mLayoutPro.setVisibility(View.GONE);
				lv_myClass.setVisibility(View.GONE);
				line_loading.setVisibility(View.VISIBLE);
			}
		} else if (numbText == 2) {
			lv_cer.setVisibility(View.GONE);
			mLayoutPro.setVisibility(View.VISIBLE);
			lv_myClass.setVisibility(View.GONE);
			line_loading.setVisibility(View.GONE);
		} else if (numbText == 3) {
			if (li_Type != null) {
				lv_cer.setVisibility(View.VISIBLE);
				mLayoutPro.setVisibility(View.GONE);
				lv_myClass.setVisibility(View.GONE);
				loadType(li_Type);
				// mScrollView.fullScroll(ScrollView.FOCUS_UP);
			} else {
				lv_cer.setVisibility(View.GONE);
				mLayoutPro.setVisibility(View.GONE);
				lv_myClass.setVisibility(View.GONE);
				line_loading.setVisibility(View.VISIBLE);
			}
		}
	}

	private void resetTestView() {
		mTextMyclass.setTextColor(Color.BLACK);
		mTextChoose.setTextColor(Color.BLACK);
		mTextCertificate.setTextColor(Color.BLACK);
	}

	/**
	 * 设置控件高度
	 * 
	 * @param v
	 * @param size
	 *            (单位px)
	 * @param which
	 */
	public void setControlHeigth(final View v, final int size, final int which) {
		ViewTreeObserver vto = v.getViewTreeObserver();

		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (mark[which] == false) {
					int height = v.getMeasuredHeight();
					int width = v.getMeasuredWidth();
					// 获取到宽度和高度后，可用于计算
					int setHeightSize = dipTopx(mContext, size);
					if (setHeightSize > height) {
						setHeightSize = height;
					}
					LayoutParams params = v.getLayoutParams();
					params.height = setHeightSize;
					v.setLayoutParams(params);

					mark[which] = true;
				}
				return true;
			}
		});
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dipTopx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * get请求获取用户加入的课程
	 * 
	 * @param uid
	 * @param pageIndex
	 * @param pageCount
	 * @param what
	 *            (msg.what的值)
	 */
	public void getCourseArray(final int uid, final int pageIndex,
			final int pageCount, final int what) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				String requestUrl = Constants_Url.ADDED_COURSE + "?uid=" + uid
						+ "&pageIndex=" + pageIndex + "&pageCount=" + pageCount;
				try {
					String jsonStr = NetWork.getData(requestUrl);
					handler.sendMessage(handler.obtainMessage(what, jsonStr));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * 返回证书数据
	 * 
	 * @param what
	 */
	public void getData(final int what) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				String requestUrl = Constants_Url.GET_INFOR_TYPE + "?typeid="
						+ 1 + "&pageSize=" + 20 + "&pageIndex=" + 1;
				try {
					String responseStr = NetWork.getData(requestUrl);
					handler.sendMessage(handler
							.obtainMessage(what, responseStr));
				} catch (Exception e) {
				}
			}
		};
		new Thread(run).start();
	}

	/**
	 * 使用子线程下载头像
	 * 
	 * @param strUrl
	 *            头像的下载地址
	 * @param saveUrl
	 *            保存的路径
	 */
	public void downLoadHeadPhoto(final String strUrl, final String saveUrl) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//下载用户头像
				int mark = DownLoadFile.downLoadPic(strUrl, saveUrl);
				
				
				switch (mark) {
					case 0:{
						Log.e("文件下载失败", "下载失败");
						break;
					}
					
					case 1: {
						Log.e("已经存在", saveUrl);
						
						handler.sendMessage(handler.obtainMessage(SETHEAD, saveUrl));
						break;
					}
					
					case 2: {

						Log.e("下载成功", saveUrl);
						
						handler.sendMessage(handler.obtainMessage(SETHEAD, saveUrl));
						break;
					}
				}
			}
		};
		new Thread(run).start();
	}

	/** 添加点击监听 */
	OnClickListener OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgbtn_checkin:
				// Intent i=new
				// Intent(PersonalCenterActivity.this,CheckInActivity.class);
				// startActivity(i);
				new Thread(run_CheckIn).start();
				break;
				//点击进入头像
			case R.id.img_head:
				Intent i = new Intent(mContext, UserInformationActivity.class);
				startActivity(i);
				break;
			case R.id.tv_signature:
				showDialogForPersonSign();
				break;
			case R.id.tv_showUser_sign:
				showDialogForPersonSign();
				break;
			}
		}
	};

	/**
	 * 弹出显示个性签名
	 */
	private void showDialogForPersonSign() {
		Intent intent = new Intent(mContext, CourseBrightspotActivity.class);
		intent.putExtra("content", person.getSignature());
		startActivity(intent);
	}

	// 使用子线程连接网络签到
	Runnable run_CheckIn = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Map<String, String> map = new HashMap<String, String>();
			map.put("uid", String.valueOf(person.getUserId()));

			String jsonStr = "";
			try {
				jsonStr = UploadMessage.post(Constants_Url.PERSON_CHECKIN, map);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				// 吐司显示结果
				handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
						mContext.getResources()
								.getString(R.string.networkerror)));
				return;
			}
			String result = "";
			try {
				switch (ResolveJSON.IsHasResult(jsonStr)) {
				case 1:

					result = ResolveJSON.JSON_To_Response(jsonStr).getResult();
					// 吐司显示结果
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
							result));
					upDate();

					break;
				case 0:
					result = ResolveJSON.JSON_To_Response(jsonStr).getResult();
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
							result));
					break;
				default:
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
							mContext.getResources().getString(R.string.error)));
					break;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE,
						mContext.getResources().getString(R.string.error)));
			}

		}
	};

	/**
	 * 更新用户信息
	 */
	public void upDate() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Constants_Url.PERSON_INFO + "?uid="
						+ person.getUserId();

				try {
					String responseStr = NetWork.getData(url);
					handler.sendMessage(handler.obtainMessage(UPDATE,
							responseStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	// 监听滚动事件实现自动加载
	class MyScrollListener implements OnScrollListener {
		int position = 0;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			switch (scrollState) {
			// 当不滚动时
			case OnScrollListener.SCROLL_STATE_IDLE: {
				if ((lv_myClass.getLastVisiblePosition() + 1) == lv_myClass
						.getCount()
						&& lv_myClass.getCount() >= Count
						&& isNotDownLoad) {
					isNotDownLoad = false;
					// System.out.println("正在加载更多数据...");
					lv_myClass.addFooterView(footerView_loading);
					position = lv_myClass.getCount();
					// System.out.println("position-->"+position);
					lv_myClass.setSelection(position);
					pageIndex += 1;
					getCourseArray(person.getUserId(), pageIndex, pageCount,
							MOREITEM);
				}

				break;
			}
			}
		}
	}

	/**
	 * 添加子监听
	 */
	OnItemClickListener onitemclicklistener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Intent intent = new Intent();
			// intent.putExtra("cid", li_course.get(position).getCourseID());
			// intent.putExtra("introduction",
			// li_course.get(position).getIntroduction());
			// intent.putExtra("img_path",
			// li_course.get(position).getCourse_ImgUrl());

			Course.course = li_course.get(position);
			intent.setClass(getApplicationContext(),
					Wanzheng_Course_video_playbackActivity.class);
			startActivity(intent);
			finish();

		}
	};

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SETHEAD: {
				String strUrl = (String) msg.obj;
				img_head.setImageBitmap(BitmapFactory.decodeFile(strUrl));
				
				MyInstance.SaveLoginUserPhoto(PersonalCenterActivity.this, strUrl);
				
				break;
			}
			case SHOW_MESSAGE: {
				String content = (String) msg.obj;
				Toast.makeText(PersonalCenterActivity.this, content,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case SETCOURSE: {
				String content = (String) msg.obj;
				try {

					switch (ResolveJSON.IsHasResult(content)) {
					case 1:
						System.out.println(content);
						li_course = ResolveJSON.JSON_TO_CourseArray(content);
						if (numbText == 1) {
							loadData(li_course);
						}
						break;
					case 0:
						Response response = ResolveJSON
								.JSON_To_Response(content);
						Toast.makeText(PersonalCenterActivity.this,
								response.getMessage(), Toast.LENGTH_SHORT)
								.show();
						line_loading.setVisibility(View.GONE);
						break;
					default:
						Toast.makeText(
								getApplicationContext(),
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
			case MOREITEM: {
				String content = (String) msg.obj;
				try {
					switch (ResolveJSON.IsHasResult(content)) {
					case 1:
						List<Course> li = ResolveJSON
								.JSON_TO_CourseArray(content);
						for (int i = 0; i < li.size(); i++) {
							li_course.add(li.get(i));
						}
						lv_myClass.removeFooterView(footerView_loading);
						adapter.notifyDataSetChanged();
						isNotDownLoad = true;
						break;
					case 0:
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.no_moredata),
								Toast.LENGTH_SHORT).show();
						lv_myClass.removeFooterView(footerView_loading);
						break;
					default:
						Toast.makeText(
								getApplicationContext(),
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
			case UPDATE: {
				String content = (String) msg.obj;
				try {
					if (ResolveJSON.IsHasResult(content) == 1) {
						Editor ed = MyInstance.getSharedPreferencesInstance(
								mContext).edit();
						ed.putString("personInformation", content);
						ed.commit();
						PersonInformation personInfo = ResolveJSON
								.JSON_To_PersonInformation(content);
						tv_learnCents.setText(String.valueOf(personInfo
								.getLearnCents()));
						tv_learnMoney.setText(String.valueOf(personInfo
								.getLearnMoney()));
						if (v_showLearnCents.getHeight() > 0
								&& personInfo.getLearnCents() < 80) {
							LayoutParams params = v_showLearnCents
									.getLayoutParams();
							params.height = dipTopx(mContext,
									personInfo.getLearnCents());
							v_showLearnCents.setLayoutParams(params);
						} else if (v_showLearnCents.getHeight() > 0
								&& personInfo.getLearnCents() > 80) {
							LayoutParams params = v_showLearnCents
									.getLayoutParams();
							params.height = dipTopx(mContext, 80);
							v_showLearnCents.setLayoutParams(params);
						}
						if (v_showLearnMoney.getHeight() > 0
								&& personInfo.getLearnMoney() < 80) {
							LayoutParams params = v_showLearnMoney
									.getLayoutParams();
							params.height = dipTopx(mContext,
									personInfo.getLearnMoney());
							v_showLearnMoney.setLayoutParams(params);
						} else if (v_showLearnMoney.getHeight() > 0
								&& personInfo.getLearnMoney() > 80) {
							LayoutParams params = v_showLearnMoney
									.getLayoutParams();
							params.height = dipTopx(mContext, 80);
							v_showLearnMoney.setLayoutParams(params);
						}

						tv_name.setText(personInfo.getUsername());

						tv_level.setText(String.valueOf(personInfo.getLevel()));

						tv_signatrue.setText(personInfo.getSignature());

						File fileMkdir = new File(
								Constants_Url.PIC_USERHEAD_PATH);
						if (!fileMkdir.exists()) {
							fileMkdir.mkdirs();
						}
						String fileName = person.getPhotoPath().substring(
								person.getPhotoPath().lastIndexOf("/") + 1,
								person.getPhotoPath().length());
						//下载地址，保存路径
						 downLoadHeadPhoto(person.getPhotoPath(),fileMkdir.getAbsolutePath()+"/"+fileName);

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

			case SETDATA:
				String jsonStr = (String) msg.obj;
				try {
					switch (ResolveJSON.IsHasResult(jsonStr)) {
					case 1:
						li_Type = ResolveJSON.JSON_TO_Type(jsonStr);
						if (numbText == 3) {
							loadType(li_Type);
						}
						break;
					case 0:
						// Toast.makeText(mContext,
						// ResolveJSON.JSON_To_Response(jsonStr).getMessage(),
						// Toast.LENGTH_SHORT).show();
						break;
					default:
						// Toast.makeText(mContext,
						// mContext.getResources().getString(R.string.error),
						// Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			}
			super.handleMessage(msg);
		}

	};
}

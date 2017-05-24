package com.weixue.weixueUI;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.weixue.Adapter.CourseCenterAdapter;
import com.weixue.Adapter.Welcome_page_adpter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.DownLoadFile;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.Major;
import com.weixue.Model.ResolveModel;
import com.weixue.Model.Subject;
import com.weixue.NewUI.JSONUtil;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;

/**
 * 课程中心
 * 
 * @author chenjunjie
 * 
 */
public class CourseCenterActivity extends Activity {
	// 设置专业名
	private static final int SET_MAJOR = 0;
	// 显示信息
	private static final int SHOW_MESSAGE = 1;
	// 设置横幅图片
	private static final int SET_BANNER = 2;
	// 加载更多数据
	private static final int MOREITEM = 3;
	// 幻灯片循环位置
	private static final int SET_HEAD_IMG_POSITION = 4;

	private Context mContext;

	private PopupWindow pop_all_type;
	// private ImageView menu;
	private TextView title;
	private int flag = 0;
	private ImageView img_search;
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ImageView imageView;
	private ImageView[] imageViews;
	private ViewGroup main;
	private ViewGroup group;
	public ListView lv_course_list;
	private CourseCenterAdapter adapter;
	private View footerView_loading;// 加载更多时显示等待
	private LinearLayout line_loading;

	private int pageIndex = 1;
	private int pageCount = 0;
	private int Count = 15;
	public boolean isNotDownLoad = true;// 标记是否已经正在下载内容（true为没有下载，false为正在下载中）

	private List<Major> li_major = null;

	private List<List<Course>> lis_course = null;// 存放专业对应的课程

	// 显示进度的对话框
	private ProgressDialog pd;

	private ImageView line_one;// 横幅图片

	private boolean mark = true;// 标记一直循环焦点图，activity销毁时结束
	private boolean mark_has_Runnable = false;// 判断是否已经存在线程来循环图片
	private int length = 3;// 设置头部图片有多少张，默认显示3张默认图
	private int howTime = 3000;// 每隔3秒显示下一张图片

	private int headImgWidth;
	private int headImgHeight;
	private float scale = 2.16f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.course_center_page);
		pd = new ProgressDialog(this);
		headImgWidth = Constants.full_display_width
				- Constants.dip2px(this, 30);
		headImgHeight = Math.round(headImgWidth / scale);
		topImg(null);
		initUi();
		getBannerImg();

	}

	// 初始化控件
	private void initUi() {
		mContext = this;

		li_major = new ArrayList<Major>();
		lis_course = new ArrayList<List<Course>>();

		title = (TextView) findViewById(R.id.title);
		title.setOnClickListener(OnClickListener);
		img_search = (ImageView) findViewById(R.id.seach);
		img_search.setOnClickListener(OnClickListener);

		lv_course_list = (ListView) findViewById(R.id.course_list);
		lv_course_list.setOnScrollListener(new MyScrollListener());

		footerView_loading = LayoutInflater.from(mContext).inflate(
				R.layout.loading_and_refesh_page, null);
		line_loading = (LinearLayout) findViewById(R.id.line_loading);

	}

	/**
	 * 初始化控件并获取数据
	 */
	private void init() {
		mContext = this;

		li_major = new ArrayList<Major>();
		lis_course = new ArrayList<List<Course>>();

		/*
		 * menu = (ImageView) findViewById(R.id.menu);
		 * menu.setOnClickListener(OnClickListener);
		 */
		title = (TextView) findViewById(R.id.title);
		title.setOnClickListener(OnClickListener);
		img_search = (ImageView) findViewById(R.id.seach);
		img_search.setOnClickListener(OnClickListener);

		lv_course_list = (ListView) findViewById(R.id.course_list);
		lv_course_list.setOnScrollListener(new MyScrollListener());

		footerView_loading = LayoutInflater.from(mContext).inflate(
				R.layout.loading_and_refesh_page, null);
		line_loading = (LinearLayout) findViewById(R.id.line_loading);

		if (!mark_has_Runnable) {
			roundImg(howTime);
		}

		getMajorArray(pageIndex, pageCount, SET_MAJOR);
	}

	/** 创建头部滑动图 */
	private void topImg(String[] imgPath) {
		
		LayoutInflater inflater = getLayoutInflater();
		main = (ViewGroup) inflater.inflate(R.layout.course_center_page, null);
		group = (ViewGroup) main.findViewById(R.id.viewGroup);
		viewPager = (ViewPager) main.findViewById(R.id.guidePages);

		if (imgPath != null && imgPath.length > 0) {
			length = imgPath.length;
		}
		// 添加滑动的视图
		pageViews = new ArrayList<View>();
		for (int i = 0; i < length; i++) {
			View v1 = inflater.inflate(R.layout.item_welcome_page_one, null);
			if (imgPath != null && imgPath.length > 0) {
				line_one = (ImageView) v1.findViewById(R.id.welcome_page_01);
				
				//根据路径显示下载的图片
				/*line_one.setBackground(Drawable
						.createFromPath(imgPath[i]));*/
				
				//设置图片显示
				switch(i){
					case 0:
						line_one.setBackground(getResources().getDrawable(R.drawable.nav_pic1));
						break;
					case 1:
						line_one.setBackground(getResources().getDrawable(R.drawable.nav_pic2));
						break;
					case 2:
						line_one.setBackground(getResources().getDrawable(R.drawable.nav_pic3));
						break;
				}
			}
			
			pageViews.add(v1);
			
		}
		RelativeLayout.LayoutParams vlp = (RelativeLayout.LayoutParams) viewPager
				.getLayoutParams();
		vlp.width = headImgWidth;
		vlp.height = headImgHeight;
		viewPager.setLayoutParams(vlp);

		imageViews = new ImageView[pageViews.size()];

		// 创建点
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(CourseCenterActivity.this);
			imageView.setLayoutParams(new LayoutParams(15, 15));
			//imageView.setPadding(0, 0, 0, 0);
			imageViews[i] = imageView;

			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.focus);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.no_focus);
			}

			group.addView(imageViews[i]);
		}

		setContentView(main);
		Welcome_page_adpter adpter = new Welcome_page_adpter(
				getApplicationContext(), pageViews);
		viewPager.setAdapter(adpter);
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	// http://222.126.246.151:9120/app/courseservices/GetAllCoursesBySubjects/list

	// 循环头部焦点图
	public void roundImg(final int time) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					mark_has_Runnable = true;
					while (mark) {

						Thread.sleep(time);
						int i = viewPager.getCurrentItem();

						i = i + 1;
						if (i < length) {
							handler.sendMessage(handler.obtainMessage(
									SET_HEAD_IMG_POSITION, i));
						} else {
							handler.sendMessage(handler.obtainMessage(
									SET_HEAD_IMG_POSITION, 0));
						}
					}
					mark_has_Runnable = false;
				} catch (InterruptedException e) {
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
				/*
				 * if((lv_course_list.getLastVisiblePosition()+1)==lv_course_list
				 * .
				 * getCount()&&lv_course_list.getCount()>=Count&&isNotDownLoad){
				 * isNotDownLoad=false; //System.out.println("正在加载更多数据...");
				 * pd.show(); pd.setMessage("正在加载...");
				 * position=lv_course_list.getCount();
				 * //System.out.println("position-->"+position);
				 * lv_course_list.setSelection(position); pageIndex+=1;
				 * getMajorArray(pageIndex,pageCount,MOREITEM); }
				 */

				break;
			}
			}
		}
	}

	/** 添加点击监听 */
	OnClickListener OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			/*
			 * case R.id.menu: // if (pop_all_type.isShowing()) { //
			 * pop_all_type.dismiss(); // } else { //
			 * pop_all_type.showAsDropDown(v); // } if(flag == 0){
			 * title.setText("在线课程"); flag = 1; } else{ title.setText("直播课程");
			 * flag = 0; } break;
			 */
			case R.id.seach:
				Intent intent = new Intent().setClass(getApplicationContext(),
						SearchCourseActivity.class);

				startActivity(intent);
				break;
			}
		}
	};

	// 设置适配器
	private void loadData(ResolveModel resolveModel) {
		lv_course_list.setVisibility(View.VISIBLE);

		/*
		 * ViewGroup.LayoutParams params = lv_course_list.getLayoutParams();
		 * params.height=resolveModel.getResult().size()*630;
		 * System.out.println("ooo->"+li_major.size());
		 * lv_course_list.setLayoutParams(params);
		 */

		adapter = new CourseCenterAdapter(getApplicationContext(), resolveModel);
		lv_course_list.setAdapter(adapter);
	}

	/**
	 * 获取所有专业及内容并显示
	 * 
	 * @param pageIndex
	 * @param pageCount
	 * @param what
	 *            msg.what的值
	 */
	public void getMajorArray(final int pageIndex, final int pageCount,
			final int what) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// CourseCenterModel model=new CourseCenterModel();

				// http://222.126.246.151:9120/app/examservices/majors/list?pageIndex=2&pageCount=0
				// String
				// requestUrl=Constants_Url.MAJORS+"?pageIndex="+pageIndex+"&pageCount="+pageCount;
				String requestUrl = "http://wxyzinterface.zhanyun360.com/app/courseservices/GetAllCoursesBySubjects/list";
				// System.out.println(requestUrl);

				try {
					// 首先获取所有专业
					String responseStr = NetWork.getData(requestUrl);
					// System.out.println("responseStr-->"+responseStr);
					Log.e("asdsd", responseStr);
					// 判断专业是否有数据
					if (ResolveJSON.IsHasResult(responseStr) == 1) {
						// List<Major>
						// li_major=ResolveJSON.JSON_TO_MajorArray(responseStr);

						ResolveModel resolveModel = JSONUtil.fromJson(
								responseStr, ResolveModel.class);

						/*
						 * for(int i=0;i<li_major.size();i++){
						 * 
						 * //List<Course> li_courses=new ArrayList<Course>();
						 * //http
						 * ://maps.googleapis.com/maps/api/geocode/json?latlng
						 * =23.1744289398,113.422&sensor=false Major
						 * major=li_major.get(i);
						 * //System.out.println("major-->"
						 * +major.getMajorName()); String
						 * url=Constants_Url.GETSUBJECT
						 * +"?mid="+major.getMajorID()+"&pageIndex="+pageIndex;
						 * //通过专业ID获取科目信息 String result=NetWork.getData(url);
						 * //System.out.println("result-->"+result);
						 * //判断专业是否含有课程
						 * (无论有无课程都必须让List<Major>大小和List<List<Course>>
						 * 相同,才能让专业与其含有的课程对应)
						 * if(ResolveJSON.IsHasResult(result)==1){ List<Subject>
						 * li_subject=ResolveJSON.JSON_TO_SubjectArray(result);
						 * /
						 * /System.out.println("li_subject-->"+li_subject.get(which
						 * ).getSubjectName());
						 * ////////////////////////////////////////// int
						 * which=(int) (Math.random()*li_subject.size()); String
						 * requesturl
						 * =Constants_Url.GETCOURSE+"?sid="+li_subject.
						 * get(which).getSubjectID()+"&pageIndex="+pageIndex;
						 * //String
						 * requesturl=Constants_Url.GETCOURSE+"?sid="+1+
						 * "&pageIndex="+pageIndex; //通过专业ID获取课程信息 String
						 * results=NetWork.getData(requesturl);
						 * System.out.println
						 * ("|||results-->"+results+"|||which"+which);
						 * if(ResolveJSON.IsHasResult(results)==1){ List<Course>
						 * li_courses=ResolveJSON.JSON_TO_CourseArray(results);
						 * lis_course.add(li_courses); //
						 * System.out.println("li_courses-->"
						 * +li_courses.get(0).getCourseName()); }else{ //
						 * System.out.println("li_courses-->null"); List<Course>
						 * li_course=new ArrayList<Course>();
						 * lis_course.add(li_course); }
						 * ////////////////////////////////////////
						 * 
						 * /////////////////////////////////////// for(int
						 * k=0;k<2;k++){ String
						 * requesturl=Constants_Url.GETCOURSE
						 * +"?sid="+li_subject.
						 * get(k).getSubjectID()+"&pageIndex="+pageIndex;
						 * System.out.println(requesturl); String
						 * results=NetWork.getData(requesturl);
						 * if(ResolveJSON.IsHasResult(results)==1){ Course
						 * course=ResolveJSON.JSON_TO_Course(results);
						 * li_courses.add(course); }else{ Course course=new
						 * Course(); li_courses.add(course); } }
						 * lis_course.add(li_courses);
						 * /////////////////////////////////////// }else{ //
						 * System.out.println("li_courses11111-->null");
						 * List<Course> li_course=new ArrayList<Course>();
						 * lis_course.add(li_course); } }
						 */

						/*
						 * model.majors=li_major; model.lis_course=lis_course;
						 * model.isSuccess=true;
						 */
						handler.sendMessage(handler.obtainMessage(what,
								resolveModel));
					} else {
						/*
						 * model.majors= new ArrayList<Major>();
						 * model.lis_course=new ArrayList<List<Course>>();
						 * model.isSuccess=false;
						 */
					}
					// handler.sendMessage(handler.obtainMessage(what, model));
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
	 * 获取横幅
	 */
	public void getBannerImg() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String json_url = "";
				int mark = 0;
				String[] img_path;
				try {
					json_url = NetWork.getData(Constants_Url.BANNER);

					// System.out.println("99999\n" + json_url);

				} catch (Exception e1) {
					handler.sendMessage(handler.obtainMessage(
							SHOW_MESSAGE,
							mContext.getResources().getString(
									R.string.networkerror)));
				}

				try {
					JSONObject json = new JSONObject(json_url);
					int state = json.getInt("Status");
					// if (ResolveJSON.IsHasResult(json_url) == 1) {
					if (state == 1) {
						JSONArray array = json.getJSONArray("Result");
						ArrayList<String> list_coures = new ArrayList<String>();
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = (JSONObject) array.opt(i);
							list_coures.add(obj.getString("_ca_image"));
						}
						System.out.println(list_coures.toString());
						img_path = new String[list_coures.size()];
						for (int i = 0; i < list_coures.size(); i++) {
							String str_couresImg = list_coures.get(i);
							String fileName = str_couresImg
									.substring(str_couresImg.lastIndexOf("/") + 1);

							String str_fileimg = Constants_Url.PIC_CACHE_PATH
									+ "/" + fileName;
							img_path[i] = str_fileimg;
							
							Log.e("图片", "str_fileimg-->" + str_fileimg);
							
							mark += DownLoadFile.downLoadPic(str_couresImg,
									str_fileimg);
						}
						System.out.println(mark + "==" + list_coures.size());// 3
																				// 4
						if (mark >= list_coures.size()) {
							System.out.println(img_path.length);
							handler.sendMessage(handler.obtainMessage(
									SET_BANNER, img_path));
						} else {
							handler.sendMessage(handler.obtainMessage(
									SET_BANNER, null));
						}
					} else {
						handler.sendMessage(handler.obtainMessage(SET_BANNER,
								null));
					}

				} catch (Exception e2) {
					handler.sendMessage(handler.obtainMessage(SET_BANNER, null));
				}

			}
		};
		new Thread(run).start();
	}

	/** 页面改变监听 */
	private class GuidePageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.focus);

				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.no_focus);
				}
			}
		}
	}

	private class CourseCenterModel {
		public List<Major> majors = null;
		public List<List<Course>> lis_course = null;
		public boolean isSuccess = false;
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SET_MAJOR: {
				ResolveModel model = (ResolveModel) msg.obj;
				if (model != null) {
					/*
					 * li_major=model.majors; lis_course=model.lis_course;
					 * lv_course_list.addFooterView(footerView_loading);
					 */
					// loadData(li_major,lis_course);
					// lv_course_list.removeFooterView(footerView_loading);

					loadData(model);
					// String string=model.getResult().get(0).getSubjectName();

					line_loading.setVisibility(View.GONE);
				} else {

					line_loading.setVisibility(View.GONE);
				}

				break;
			}
			case SHOW_MESSAGE: {
				String content = (String) msg.obj;
				Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
				break;
			}
			case SET_BANNER: {
				String[] str_down_img = (String[]) msg.obj;
				// int i=str_down_img.length;
				// System.out.println(i);
				if (str_down_img != null && str_down_img.length > 0) {
					topImg(str_down_img);
					init();
				} else {
					topImg(null);
					init();
				}

				break;
			}
			case MOREITEM: {
				CourseCenterModel model = (CourseCenterModel) msg.obj;
				if (model.isSuccess) {
					List<Major> major = model.majors;
					for (int i = 0; i < major.size(); i++) {
						li_major.add(major.get(i));
					}
					List<List<Course>> course = model.lis_course;
					for (int j = 0; j < course.size(); j++) {
						lis_course.add(course.get(j));

					}
					isNotDownLoad = true;
				} else {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.no_moredata), Toast.LENGTH_SHORT)
							.show();

				}
				// adapter.updateList(resolveModel);
				pd.dismiss();

				break;
			}

			case SET_HEAD_IMG_POSITION: {
				int position = (Integer) msg.obj;
				viewPager.setCurrentItem(position);
				break;
			}

			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mark = false;
		super.onDestroy();
	}

}

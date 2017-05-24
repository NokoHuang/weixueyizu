package com.weixue.NewUI;

import java.util.ArrayList;
import java.util.List;

import com.weixue.Adapter.CourseCenterAdapter;
import com.weixue.Adapter.VideoListAdapter;
import com.weixue.Adapter.Welcome_page_adpter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.DownLoadFile;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Model.CourseWare;
import com.weixue.Model.Major;
import com.weixue.Model.Subject;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.CourseCenterActivity;
import com.weixue.weixueUI.R;
import com.weixue.weixueUI.SearchCourseActivity;
import com.weixue.weixueUI.R.layout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class CenterFragment extends Fragment {

	private View v;

	//设置专业名
	private static final int SET_MAJOR = 0;
	//显示信息
	private static final int SHOW_MESSAGE=1;
	//设置横幅图片
	private static final int SET_BANNER=2;
	//加载更多数据
	private static final int MOREITEM = 3;
	//幻灯片循环位置
	private static final int SET_HEAD_IMG_POSITION = 4;

	private Context mContext;

	private PopupWindow pop_all_type;
	private ImageView menu;
	private TextView title;
	private int flag =0;
	private ImageView img_search;
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ImageView imageView;
	private ImageView[] imageViews;
	private ViewGroup main;
	private ViewGroup group;
	public  ListView lv_course_list;
	private CourseCenterAdapter adapter;
	private View footerView_loading;//加载更多时显示等待
	private LinearLayout line_loading;

	private int pageIndex=1;
	private int pageCount=0;
	private int Count=15;
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）

	private List<Major> li_major=null;

	private List<List<Course>> lis_course=null;//存放专业对应的课程

	//显示进度的对话框
	private ProgressDialog pd;

	private ImageView line_one;//横幅图片

	private boolean mark = true;// 标记一直循环焦点图，activity销毁时结束
	private boolean mark_has_Runnable = false;// 判断是否已经存在线程来循环图片
	int length=3;//设置头部图片有多少张，默认显示3张默认图
	private int howTime = 3000;//每隔3秒显示下一张图片

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.new_course_center_page, container, false); 
		getBannerImg();
		return v;
	}

	/**
	 * 获取横幅
	 */
	public void getBannerImg(){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String json_url="";
				int mark=0;
				String[] img_path;
				try{
					json_url=NetWork.getData(Constants_Url.BANNER);

					//System.out.println("99999\n"+json_url);

				}catch(Exception e1){
					handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.networkerror)));
				}

				try{

					if(ResolveJSON.IsHasResult(json_url)==1){

						List<Course> list_coures=ResolveJSON.JSON_TO_CourseArray(json_url);
						img_path=new String[list_coures.size()];
						for(int i=0;i<list_coures.size();i++){
							String str_couresImg=list_coures.get(i).getCourse_ImgUrl();
							String fileName=str_couresImg.substring(str_couresImg.lastIndexOf("/")+1);

							String str_fileimg=Constants_Url.PIC_CACHE_PATH+"/"+fileName;
							img_path[i]=str_fileimg;
							System.out.println("str_fileimg-->"+str_fileimg);
							mark+=DownLoadFile.downLoadPic(str_couresImg, str_fileimg);
							System.out.println(mark);														
						}
						System.out.println(mark+"=="+list_coures.size());//3 4
						if(mark>=list_coures.size()){
							System.out.println(img_path.length);
							handler.sendMessage(handler.obtainMessage(SET_BANNER, img_path));
						}else{
							handler.sendMessage(handler.obtainMessage(SET_BANNER, null));
						}
					}else{
						handler.sendMessage(handler.obtainMessage(SET_BANNER, null));
					}

				}catch(Exception e2){
					handler.sendMessage(handler.obtainMessage(SET_BANNER, null));
				}

			}};
			new Thread(run).start();
	}

	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case SET_BANNER:{
				String[] str_down_img=(String[]) msg.obj;
				//int i=str_down_img.length;
				//System.out.println(i);
				if(str_down_img!=null&&str_down_img.length>0){
					topImg(str_down_img);
					//init();
				}else{
					topImg(null);
					//init();	
				}
				break;
			}
			}
			super.handleMessage(msg);
		}

	};

	/**创建头部滑动图*/
	private void topImg(String[] imgPath){

	}
}

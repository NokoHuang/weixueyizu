package com.weixue.myUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.weixue.Adapter.PersionAdapter;
import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Course;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
import com.weixue.weixueUI.R;

/**
 * 书籍
 * 
 * @author zeda
 * 
 */
public class StudyFieldItem1 extends Activity {
	// 显示所有书籍
	private final int DISPLAY_ALL_COURSE = 2;

	private String stringJson = null;
	private List<Course> list = null;
	private ListView all_course_listView;
	private LinearLayout line_loading;

	private Activity mContext;

	// 下载
	private DownloadManager manager;
	private DownloadCompleteReceiver receiver;
	private long downid;
	private static String SD_PATH = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_field_item1);
		mContext = this;

		// 获取下载服务
		manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		receiver = new DownloadCompleteReceiver();
		SD_PATH = Constants.getSDPath();

		Init();
		GetData();

	}

	public void LoadData(List<Course> list) {
		/*
		 * AllCourseAdapter adapter=new
		 * AllCourseAdapter(getLayoutInflater(),list);
		 * all_course_listView.setAdapter(adapter);
		 */
		PersionAdapter adapter = new PersionAdapter(getApplicationContext(),
				list);
		all_course_listView.setAdapter(adapter);
	}

	/**
	 * 
	 */
	public void GetData() {
		new Thread() {
			public void run() {
				try {
					// http://222.126.246.151:9120/app/courseservices/GetAllCoursesBySubjects/list
					// 获取返回的JSON数据
					stringJson = NetWork.getData(Constants_Url.GET_ALL_COURSE);
					System.out.println("stringJson-->>" + stringJson);
					// 判断获取JSON的状态值，1是成功，0是失败
					switch (ResolveJSON.IsHasResult(stringJson)) {
					case 0:
						Log.e("dasd", ResolveJSON.JSON_To_Response(stringJson)
								.getStatus() + "");

						break;
					case 1:
						handler.sendMessage(handler.obtainMessage(
								DISPLAY_ALL_COURSE, stringJson));
						break;
					default:
						handler.sendMessage(handler.obtainMessage(
								DISPLAY_ALL_COURSE, ""));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					Constants.showMessage(mContext, "获取数据异常");
				}

			}
		}.start();
	}

	/**
	 * 
	 */
	public void Init() {
		all_course_listView = (ListView) findViewById(R.id.all_course_listview);
		all_course_listView.setOnItemClickListener(onitemclicklistener);
		list = new ArrayList<Course>();
		line_loading = (LinearLayout) findViewById(R.id.line_loading);
	}

	public void GoBack(View v) {
		onBackPressed();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// 隐藏加载框
			line_loading.setVisibility(View.GONE);

			switch (msg.what) {
			case 0x123: {
				break;
			}

			case DISPLAY_ALL_COURSE: {
				String content = (String) msg.obj;

				try {
					switch (ResolveJSON.IsHasResult(content)) {
					case 1: {
						try {
							list = ResolveJSON
									.Book_JSON_TO_CourseArray(content);
							LoadData(list);

						} catch (Exception e) {
							e.printStackTrace();
							Log.e("测试", "异常");
						}
						break;
					}

					case 0: {

						break;
					}

					default: {
						Log.e("是否返回", "错误");
						break;
					}
					}
				} catch (Exception e) {
					Log.e("是否返回", "错误eeee>>");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			}
			super.handleMessage(msg);
		}
	};

	private AlertDialog dialog;
	private String resultMsg;
	private String title;

	OnItemClickListener onitemclicklistener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			Course item = list.get(pos);
			final int id = item.getCourseID();
			title = item.getCourseName();
			dialog = Constants.getTempDialogCanCancle(mContext, "加载中...");
			new Thread() {
				@Override
				public void run() {
					try {
						String result = NetWork
								.getData(Constants_Url.GetcourseIDByPDF + id);
						System.out.println("result-->>" + result + "  url-->>"
								+ Constants_Url.GetcourseIDByPDF + id);
						JSONObject json = new JSONObject(result);
						int status = json.getInt("Status");
						resultMsg = json.getString("Message");
						Message msg = new Message();
						if (status == 1) {
							msg.what = 1;
							msg.obj = json.getString("Result");
						} else
							msg.what = 0;
						mHandler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(5);
					}
				}
			}.start();
		}
	};

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				dealUrl((String) msg.obj);
				System.out.println((String) msg.obj);
			} else if (msg.what == 5) {
				dialog.dismiss();
				Constants.showMessageByNetExpetion(mContext);
			} else {
				dialog.dismiss();
				Constants.showMessage(mContext, resultMsg);
			}
		}
	};
	private File f;
	private String basepath;
	String fileName;
	String path;

	private void dealUrl(String url) {
		if (url.length() > 3 && url.substring(url.length() - 3).equals("pdf")) {
			if (SD_PATH == null || SD_PATH.equals("")) {
				Constants.showMessage(mContext, "SD卡不存在");
				dialog.dismiss();
			} else {
				// Intent intent = new Intent();
				// intent.setClass(BookListActivity.this,
				// PdfViewerActivity.class);
				// intent.putExtra("url", urlstr);
				// intent.putExtra("type", 2);
				// intent.putExtra("title", title);
				// startActivityForResult(intent, 2);
				dealPdf(url);
			}
		} else {
			dialog.dismiss();
			Constants.showMessage(mContext, "暂无资源");
		}
	}

	private void dealPdf(String url) {
		basepath = "/Weixue/download/";
		String filepath = SD_PATH + basepath;
		fileName = url.substring((url.lastIndexOf("/") + 1), url.length());
		path = filepath + fileName;
		// System.out.println(path + "path-->>");
		File fP = new File(filepath);
		if (!fP.exists())
			fP.mkdirs();
		f = new File(path);
		if (f.exists()) {// 存在
			dialog.dismiss();
			openPdf();
		} else {
			if (NetWork.hasNetWorkConnect(this)) {
				// bindService();
				download(url);
			} else {
				dialog.dismiss();
				Constants.showMessageByNoNet(mContext);
			}
		}
	}

	private void openPdf() {
		Uri uri = Uri.parse(path);
		Intent intent = new Intent(this, MuPDFActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(uri);
		intent.putExtra("title", title);
		startActivityForResult(intent, 1006);
	}

	/**
	 * 下载
	 * 
	 * @param urlstr
	 */
	private void download(String url) {
		// 创建下载请求
		DownloadManager.Request down = new DownloadManager.Request(
				Uri.parse(url));
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
				| DownloadManager.Request.NETWORK_WIFI);
		// 禁止发出通知，既后台下载
		down.setShowRunningNotification(false);
		// 不显示下载界面
		down.setVisibleInDownloadsUi(false);
		// 设置下载后文件存放的位置
		down.setDestinationInExternalPublicDir(basepath, fileName);
		// down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
		// fileName);
		// 将下载请求放入队列
		downid = manager.enqueue(down);
		// System.out.println("start-->>" + basepath + " " + fileName);
	}

	// 接受下载完成后的intent
	class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				long downId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if (downid == downId) {// 下载成功
					if (!dialog.isShowing())
						return;
					dialog.dismiss();
					openPdf();
				}
			}
		}
	}

}

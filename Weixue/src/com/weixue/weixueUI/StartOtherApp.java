package com.weixue.weixueUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weixue.Adapter.FileSearchAdapter;

/**
 * 调用第三方打开PPT,word,excel等文件
 * 
 * @author luzhanggang
 * 
 */
public class StartOtherApp extends ActivityGroup {

	private PackageManager packManager;
	private Window subActivity;
	private LinearLayout page;
	private ListView fileList;
	private File file;
	private String path;
	private List<String> list = new ArrayList<String>();
	private String[] str;
	private String ms;
	private TextView title;

	String exampleFile = Environment.getExternalStorageDirectory()
			+ "/weixue/example.ppt";
	String docFile = Environment.getExternalStorageDirectory()
			+ "/weixue/fe.doc";
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_other_app_page);

		init();
		startRawToSD();
		
	}
	
	public void GoBack(View v){
		onBackPressed();
	}

	/**
	 * 初始化
	 */
	private void init() {
		file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/weixue");
		fileList = (ListView) findViewById(R.id.fileList);
		page = (LinearLayout) findViewById(R.id.page);
		fileList.setOnItemClickListener(onitemclicklistener);
		title = (TextView) findViewById(R.id.title);
	}

	/**
	 * 把资源拷贝到SD卡
	 */
	public int RawToSD(String path, int p) {
		File demoFile = new File(path);
		InputStream inputStream = null;
		if (!demoFile.exists()) {
			
			if (p == 1) {
				inputStream = getResources().openRawResource(R.raw.example);
			}
			if (p == 2) {
				inputStream = getResources().openRawResource(R.raw.fe);
			} 
			
			try {
				FileOutputStream fos = new FileOutputStream(path);
				byte[] buffer = new byte[512 * 1024];
				int count;
				while ((count = inputStream.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.flush();
				fos.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	/**
	 * 查找文件类型
	 */
	public void fileType() {
		Intent i = this.getIntent();
		Bundle bundle = i.getExtras();
		ms = bundle.getString("ms");
		if (ms.equals("ppt")) {
			title.setText("查看PPT");
			str = new String[2];
			str[0] = ".ppt";
			str[1] = ".pptx";
		}
		if (ms.equals("word")) {
			title.setText("查看教案");
			str = new String[5];
			str[0] = ".doc";
			str[1] = ".docx";
			str[2] = ".excel";
			str[3] = ".excelx";
			str[4] = ".pdf";
		}
	}

	/**
	 * 添加子监听
	 */
	OnItemClickListener onitemclicklistener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			String path = list.get(position);
			
			File file = new File(path);

			openFile(file);

		}
	};

	/**
	 * 遍历 Files
	 * 
	 * @param file
	 */
	public List<String> toSearchFiles(File file) {
		File[] files = file.listFiles();
		for (File tf : files) {
			if (tf.isDirectory()) {
				toSearchFiles(tf);
			} else {
				try {
					if (ms.equals("ppt")) {
						if (tf.getName().indexOf(str[0]) > -1
								|| tf.getName().indexOf(str[1]) > -1) {
							path = tf.getPath();
							list.add(path);
						}
					} else {
						if (tf.getName().indexOf(str[0]) > -1
								|| tf.getName().indexOf(str[1]) > -1
								|| tf.getName().indexOf(str[2]) > -1
								|| tf.getName().indexOf(str[3]) > -1
								|| tf.getName().indexOf(str[4]) > -1) {
							path = tf.getPath();
							list.add(path);
						}
					}
				} catch (Exception e) {
					Toast.makeText(this, "文件搜索失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}
		return list;
	}

	/**
	 * 加载数据
	 */
	private void loadDate(List<String> list) {
		FileSearchAdapter adapter = new FileSearchAdapter(
				getApplicationContext(), list);
		fileList.setAdapter(adapter);
	}

	/**
	 * 子线程copyraw资源到sd卡
	 * 
	 * @param file
	 */
	private void startRawToSD() {
		page.setVisibility(View.VISIBLE);
		Runnable runnable = new Runnable() {
			public void run() {
				Looper.prepare();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
					if (RawToSD(exampleFile, 1) == 1) {
						if (RawToSD(docFile, 2) == 1) {
							
						}
					}
				
				handler.sendMessage(handler.obtainMessage(1,null));
			}
		};
		new Thread(runnable).start();
	}
	
	
	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	private void openFile(final File file) {
		page.setVisibility(View.VISIBLE);
		Runnable runnable = new Runnable() {
			public void run() {
				Looper.prepare();
				try {
					Thread.sleep(1000);
					handler.sendMessage(handler.obtainMessage(0,file));
				} catch (Exception e) {
					
				}
			}
		};
		new Thread(runnable).start();
	}
	
	
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try{
					File file = (File) msg.obj;
					Intent intent = new Intent();
					packManager = getApplicationContext().getPackageManager();
					intent = packManager
							.getLaunchIntentForPackage("cn.wps.moffice_eng");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					String type = "application/vnd.ms-powerpoint";
					intent.setDataAndType(Uri.fromFile(file), type);
					startActivity(intent);
					page.setVisibility(View.GONE);
				}
				catch(Exception e){
					page.setVisibility(View.GONE);
					show();
				}
				break;
			case 1:
				
				fileType();
				loadDate(toSearchFiles(file));
				page.setVisibility(View.GONE);
				break;
				}
			}
		};
	

	/**
	 * 对画框
	 */
	public void show() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				StartOtherApp.this);
		builder.setTitle("温馨提示");
		builder.setMessage("检测到未安装office工具，是否立即安装？");
		builder.setPositiveButton("立即安装",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
//						lauchOffice();
						Uri uri = Uri.parse("market://details?id=cn.wps.moffice_eng");//重点
						Intent it = new Intent(Intent.ACTION_VIEW, uri); 
						startActivity(it);
					}
				});
		builder.setNegativeButton("稍后安装",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	/**
	 * 安装office软件
	 */
	public void lauchOffice() {
		File file = new File("");
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(i);
	}

}

package com.weixue.weixueUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.weixue.Adapter.CampusNewsAdapter;
import com.weixue.Function.MyInstance;
import com.weixue.Function.NetWork;
import com.weixue.Methods.DownLoadFile;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Model.Status;
import com.weixue.MyDialog.PublishDetailActivity;
import com.weixue.Utils.Constants;
import com.weixue.Utils.Constants_Url;
import com.weixue.myUI.MyPopupWindow;

/**
 * 互动平台
 * @author luzhanggang && chenjunjie
 * 
 */
public class CampusNewsActivity extends Activity{
	//选择图片或拍照弹出框
	private MyPopupWindow popup;
	//选择图片(startAvtivityForResult的requestcode)
	private static final int SELECT_PIC=0;
	//拍照(startAvtivityForResult的requestcode)
	private static final int TAKE_PIC=1;
	//发表动态(startAvtivityForResult的requestcode)
	private static final int SET_STATUS=2;
	
	private String img_path=Constants_Url.PIC_TAKEPHOTO_PATH;
	
    //设置数据
	private static final int SETDATA = 0;
	/**增加更多数据*/
	private static final int MOREITEM=1;
	private static final int SETHEAD = 2;
	private static final int SHOW_MESSAGE=3;
	private static final String STATUS_BG = "status_bg";
	
	private Context mContext;
	private LinearLayout popu_btn;
	
	private PopupWindow pop_all_type;
	//泡泡窗口显示的内容
	private TextView tv_allStatus,tv_schoolStatus,tv_friendStatus,tv_myStatus,tv_publishdetailForSchool,tv_publishdetailForPerson,tv_shareCourse;
	
	
	public static ListView lv_newMsg;	
	private View footerView_loading;//加载更多时显示等待
	private LinearLayout line_loading;
	public boolean isNotDownLoad=true;//标记是否已经正在下载内容（true为没有下载，false为正在下载中）
//	private boolean Rotate=true;
	
	//用户信息
	public PersonInformation person;

	private List<Status> li_status;
	private CampusNewsAdapter adapter;
	private int tag=0;
	private int pageIndex=1;
	private int pageCount=0;
	private int Count=15;//每页显示的数量
	
	//设置上半背景及用户图片及提示信息
	private LinearLayout line_setbg;
	private ImageView img_userHead;
	private TextView tv_prompt;
	
	private int type=2;//说明是评论动态
	
	private MyReceiver myReceiver;
	
	private TextView tv_statusTitle;//标题
	
	private  UMSocialService mController = UMServiceFactory.getUMSocialService(  
            "com.umeng.share", RequestType.SOCIAL);  
	private Activity mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.campus_news_page);
		
		myReceiver=new MyReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(Constants.STATUS_REFESH);
		registerReceiver(myReceiver, filter);
		init();
	
	}
	
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			if(action.equals(Constants.STATUS_REFESH)){
				if(pop_all_type!=null&&pop_all_type.isShowing()){
					pop_all_type.dismiss();
				}
				line_loading.setVisibility(View.VISIBLE);
				tv_statusTitle.setText(getApplicationContext().getResources().getString(R.string.popu_1));
				refreshStartData();
				tag=0;
				getStatusArray(person.getUserId(), tag, pageIndex, pageCount, SETDATA);
			}
		}
		
	}
	
	

	/** 初始化 */
	private void init() {
		mContext=this;
		mActivity=this;
		
		mController.setShareContent(getApplicationContext().getResources().getString(R.string.app_name));
	
		
		popu_btn = (LinearLayout) findViewById(R.id.popu_btn);
		popu_btn.setOnClickListener(OnClickListener);
		
		//标题
		tv_statusTitle=(TextView) findViewById(R.id.tv_statusTitle);
		
		//选择背景对话框
		popup = new MyPopupWindow(mContext,OnClickListener,getApplicationContext().getResources().getString(R.string.takephoto),getApplicationContext().getResources().getString(R.string.selectlocal));

		LayoutInflater inflater = LayoutInflater.from(this);
		View pop_all_type_view = inflater.inflate(R.layout.window, null);
		pop_all_type = new PopupWindow(pop_all_type_view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		pop_all_type.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.paopao));
		tv_allStatus=(TextView) pop_all_type_view.findViewById(R.id.tv_allstatus);
		tv_allStatus.setOnClickListener(OnClickListener);
		tv_schoolStatus=(TextView) pop_all_type_view.findViewById(R.id.tv_schoolstatus);
		tv_schoolStatus.setOnClickListener(OnClickListener);
		tv_friendStatus=(TextView) pop_all_type_view.findViewById(R.id.tv_friendstatus);
		tv_friendStatus.setOnClickListener(OnClickListener);
		tv_myStatus=(TextView) pop_all_type_view.findViewById(R.id.tv_mystatus);
		tv_myStatus.setOnClickListener(OnClickListener);
		
		tv_publishdetailForSchool=(TextView) pop_all_type_view.findViewById(R.id.tv_publish_detailforschool);
		tv_publishdetailForSchool.setOnClickListener(OnClickListener);
		tv_publishdetailForPerson=(TextView) pop_all_type_view.findViewById(R.id.tv_publish_detailforperson);
		tv_publishdetailForPerson.setOnClickListener(OnClickListener);
		tv_shareCourse=(TextView) pop_all_type_view.findViewById(R.id.tv_shareCourse);
		tv_shareCourse.setOnClickListener(OnClickListener);
		

		
		
		lv_newMsg = (ListView) findViewById(R.id.newsMsg);		
		lv_newMsg.setOnScrollListener(new MyScrollListener());
		lv_newMsg.setOnItemClickListener(itemListener);
		footerView_loading=LayoutInflater.from(mContext).inflate(R.layout.loading_and_refesh_page, null);
		line_loading=(LinearLayout) findViewById(R.id.line_loading);
		
		
		person=ResolveJSON.JSON_To_PersonInformation(MyInstance.getSharedPreferencesString(mContext,Constants.PERSON_INFORMATION));
		
		line_setbg=(LinearLayout) findViewById(R.id.line_setbg);
		line_setbg.setOnClickListener(OnClickListener);
		tv_prompt=(TextView) findViewById(R.id.tv_prompt);
		String path=MyInstance.getSharedPreferencesString(mContext,Constants.STATUS_BG);
		if(path!=""){
			File file=new File(path);
			if(file.exists()){
			line_setbg.setBackgroundDrawable(Drawable.createFromPath(path));
			tv_prompt.setVisibility(View.GONE);
			}
		}
		
		
		img_userHead=(ImageView) findViewById(R.id.userPic);
		String fileName=person.getLargePhotoPath().substring(person.getLargePhotoPath().lastIndexOf("/")+1);
		File file=new File(Constants_Url.PIC_USERHEAD_PATH+"/"+fileName);
		if(file.exists()){
			img_userHead.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
		}else{
			downLoadHeadPhoto(person.getLargePhotoPath(),file.getAbsolutePath());
		}
		getStatusArray(person.getUserId(), tag, pageIndex, pageCount, SETDATA);
	}
	
	/**
	 * 显示或取消显示泡泡窗口(选择背景的)
	 */
	private void showOrDismissMyPopup(){
		if (popup.isShowing()) {
			popup.dismiss();
		} else {
			popup.showAtLocation(CampusNewsActivity.this.findViewById(R.id.line_big), Gravity.BOTTOM, 0, 0);
		}
	}
	
	
	/**
	 * 使用子线程下载头像
	 * @param strUrl 头像的下载地址
	 * @param saveUrl 保存的路径
	 */
	public void downLoadHeadPhoto(final String strUrl,final String saveUrl){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				int mark=DownLoadFile.downLoadPic(strUrl, saveUrl);
				
				switch(mark){
				case 1:{
					handler.sendMessage(handler.obtainMessage(SETHEAD, saveUrl));
					
					break;
				}
				case 2:{
					handler.sendMessage(handler.obtainMessage(SETHEAD, saveUrl));
					break;
				}
				}				
			}};
			new Thread(run).start();
	}
	

	/** 添加点击监听 */
	OnClickListener OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.popu_btn:{
				//RotatePic(popu_btn);
				if (pop_all_type.isShowing()) {
					pop_all_type.dismiss();
				} else {
					pop_all_type.showAsDropDown(v);
				}
				break;
			}
			case R.id.tv_allstatus:{
				System.out.println("点击全部动态");
				tv_statusTitle.setText(getApplicationContext().getResources().getString(R.string.popu_1));
				refreshStartData();
				tag=0;
				getStatusArray(person.getUserId(), tag, pageIndex, pageCount, SETDATA);
				break;
			}
			case R.id.tv_schoolstatus:{
				System.out.println("点击校园动态");
				tv_statusTitle.setText(getApplicationContext().getResources().getString(R.string.popu_2));
				refreshStartData();
				tag=1;
				getStatusArray(person.getUserId(), tag, pageIndex, pageCount, SETDATA);
				break;
			}
			case R.id.tv_friendstatus:{
				System.out.println("点击好友动态");
				tv_statusTitle.setText(getApplicationContext().getResources().getString(R.string.popu_3));
				refreshStartData();
				tag=2;
				getStatusArray(person.getUserId(), tag, pageIndex, pageCount, SETDATA);
				break;
			}
			case R.id.tv_mystatus:{
				System.out.println("点击个人动态");
				tv_statusTitle.setText(getApplicationContext().getResources().getString(R.string.popu_4));
				refreshStartData();
				tag=3;
				getStatusArray(person.getUserId(), tag, pageIndex, pageCount, SETDATA);
				break;
			}
			case R.id.tv_publish_detailforschool:{
				Intent i=new Intent(CampusNewsActivity.this,PublishDetailActivity.class);
				i.putExtra("type", 1);
				CampusNewsActivity.this.getParent().startActivityForResult(i, SET_STATUS);
				break;
			}
			case R.id.tv_publish_detailforperson:{
				Intent i=new Intent(CampusNewsActivity.this,PublishDetailActivity.class);
				i.putExtra("type", 3);
				CampusNewsActivity.this.getParent().startActivityForResult(i, SET_STATUS);
				break;
			}
			case R.id.tv_shareCourse:{
				// 打开分享平台选择面板  
                mController.openShare(mActivity, false); 
				break;
			}
			case R.id.line_setbg:{
				showOrDismissMyPopup();			
				break;
				
			}
			case R.id.btn_first:{
				Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机 
				startActivityForResult(intents, TAKE_PIC);   
				showOrDismissMyPopup(); 
				break;
			}
			case R.id.btn_second:{
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*") ;				
				startActivityForResult(Intent.createChooser(intent, "Select picture"),SELECT_PIC); 
				showOrDismissMyPopup();   
				break;
			}
			}
		}
	};
	
	private void refreshStartData(){
		isNotDownLoad=true;
		pageIndex=1;
		line_loading.setVisibility(View.VISIBLE);
		li_status=new ArrayList<Status>();
		if(adapter!=null){
		adapter.upDate(li_status);
		adapter.notifyDataSetChanged();
		}
	}
	
	
	/**
	 * 使用子线程获取Status数组(List)
	 * @param uid
	 * @param tag
	 * @param pageIndex
	 * @param pageCount
	 * @param what(msg.what的值)
	 */
	public void getStatusArray(final int uid,final int tag,final int pageIndex,final int pageCount,final int what){
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.GETSTATUS+"?uid="+uid+"&tag="+tag+"&pageIndex="+pageIndex+"&pageCount="+pageCount;
	System.out.println("requestUrl:"+requestUrl);		  
				try {
				String jsonStr=NetWork.getData(requestUrl);
				
				handler.sendMessage(handler.obtainMessage(what, jsonStr));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.networkerror)));
			}
			}};
			new Thread(run).start();
	}

	/** 加载列表数据 */
	private void loadData(List<Status> li) {
		 adapter = new CampusNewsAdapter(
				getApplicationContext(),li);
		 lv_newMsg.setAdapter(adapter);
	}

//	/**旋转图片*/
//	private void RotatePic(ImageView imageView) {
//		if(Rotate){
//			Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_90);
//	        animation.setFillAfter(true);
//	        imageView.startAnimation(animation);
//	        Rotate = false;
//		}
//		else{
//			Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//	        animation.setFillAfter(true);
//	        imageView.startAnimation(animation);
//	        Rotate = true;
//		}
//	}
	
	
	OnItemClickListener itemListener=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
				long arg3) {
			// TODO Auto-generated method stub
//			if(!li_status.get(arg2).getUserName().equals(person.getUsername())){
//			CharSequence[] items = {"关注好友", "发表评论","查看评论"}; 			
//			AlertDialog.Builder dialog=new AlertDialog.Builder(CampusNewsActivity.this);
//			dialog.setTitle("选择操作") ;
//			dialog.setItems(items, new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					if(which == 0){
//						Intent i=new Intent(CampusNewsActivity.this,AddFriendActivity.class);
//						i.putExtra("fuid", li_status.get(arg2).getUserID());
//						startActivity(i);
//					}else if(which==1){
//						Intent i=new Intent(CampusNewsActivity.this,UserCommentActivity.class);	
//						i.putExtra("uid", person.getUserId());
//						i.putExtra("targetId", li_status.get(arg2).getStatusID());
//						i.putExtra("type", type);
//						startActivity(i);   
//					}else{
//						Intent i=new Intent(CampusNewsActivity.this,ReadCommentActivity.class);		
//						i.putExtra("sid", li_status.get(arg2).getStatusID());
//						startActivity(i);  
//						
//					}
//				}});
//			dialog.create().show();
//			}else{
//				CharSequence[] items = {"发表评论","查看评论"}; 			
//				AlertDialog.Builder dialog=new AlertDialog.Builder(CampusNewsActivity.this);
//				dialog.setTitle("选择操作") ;
//				dialog.setItems(items, new DialogInterface.OnClickListener(){
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						if(which==0){
//							Intent i=new Intent(CampusNewsActivity.this,UserCommentActivity.class);	
//							i.putExtra("uid", person.getUserId());
//							i.putExtra("targetId", li_status.get(arg2).getStatusID());
//							i.putExtra("type", type);
//							startActivity(i);   
//						}else{
//							Intent i=new Intent(CampusNewsActivity.this,ReadCommentActivity.class);		
//							i.putExtra("sid", li_status.get(arg2).getStatusID());
//							startActivity(i);  
//							
//						}
//					}});
//				dialog.create().show();
//			}
			Intent i=new Intent(mContext,StatusDetailActivity.class);
			i.putExtra("status", li_status.get(arg2));			
			startActivity(i);
		}};
	//监听滚动事件实现自动加载
class MyScrollListener implements OnScrollListener{
 int position=0;


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
    case OnScrollListener.SCROLL_STATE_IDLE:
    {
    	if((lv_newMsg.getLastVisiblePosition()+1)==lv_newMsg.getCount()&&lv_newMsg.getCount()>=Count&&isNotDownLoad){
    		isNotDownLoad=false;
    		//System.out.println("正在加载更多数据...");
    		lv_newMsg.addFooterView(footerView_loading);
    		position=lv_newMsg.getCount();
    		//System.out.println("position-->"+position);	    		
    		lv_newMsg.setSelection(position);
    		pageIndex+=1;
    		 getStatusArray(person.getUserId(), tag, pageIndex, pageCount, MOREITEM);
    	}
			
    	break;
    }
  }
}}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case SETDATA:{
				String jsonStr=(String) msg.obj;

				try {
					
					switch(ResolveJSON.IsHasResult(jsonStr)){
						case 1:
							//Response response=ResolveJSON.JSON_To_Response(jsonStr);
						li_status=ResolveJSON.JSON_TO_StatusArray(jsonStr);
						
						lv_newMsg.addFooterView(footerView_loading);
						
						loadData(li_status);
						
						lv_newMsg.removeFooterView(footerView_loading);
						
						line_loading.setVisibility(View.GONE);
						break;
					case 0:					
						Response response2=ResolveJSON.JSON_To_Response(jsonStr);
						Toast.makeText(CampusNewsActivity.this, response2.getMessage(), Toast.LENGTH_SHORT).show();
						line_loading.setVisibility(View.GONE);
						break;
						default:
							handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.error)));
							break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case MOREITEM:{
				String content=(String) msg.obj;
				try {
					switch(ResolveJSON.IsHasResult(content)){
					case 1:
					List<Status> li=ResolveJSON.JSON_TO_StatusArray(content);
					for(int i=0;i<li.size();i++){
						li_status.add(li.get(i));
					}
					lv_newMsg.removeFooterView(footerView_loading);
					adapter.upDate(li_status);
					adapter.notifyDataSetChanged();
					isNotDownLoad=true;
					break;
					case 0:
						Toast.makeText(mContext,mContext.getResources().getString(R.string.no_moredata), Toast.LENGTH_SHORT).show();
						lv_newMsg.removeFooterView(footerView_loading);
						
					break;
					default:
						handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, mContext.getResources().getString(R.string.error)));
						
						break;
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case SETHEAD:{
				String path=(String) msg.obj;
				img_userHead.setImageBitmap(BitmapFactory.decodeFile(path));
				break;
			}
			case SHOW_MESSAGE:{
				String content=(String) msg.obj;
				Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
				break;
			}
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		 super.onActivityResult(requestCode, resultCode, data); 
	
		 switch(requestCode){
		 case SELECT_PIC:{
			 if(resultCode==Activity.RESULT_OK) 
	         { 
	             /** 
	              * 当选择的图片不为空的话，在获取到图片的途径   
	              */ 
	             Uri uri = data.getData();
	             String[] proj = {MediaStore.Images.Media.DATA};
	             //好像是android多媒体数据库的封装接口，具体的看Android文档
	             Cursor cursor = managedQuery(uri, proj, null, null, null);
	             //按我个人理解 这个是获得用户选择的图片的索引值
	             int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	             //将光标移至开头 ，这个很重要，不小心很容易引起越界
	             cursor.moveToFirst();
	             //最后根据索引值获取图片路径
	             String path = cursor.getString(column_index);
	             tv_prompt.setVisibility(View.GONE);
	             line_setbg.setBackgroundDrawable(Drawable.createFromPath(path));
	             Editor ed=MyInstance.getSharedPreferencesInstance(mContext).edit();
	             ed.putString(STATUS_BG, path);
	             ed.commit();
	             //System.out.println(path);
	             
	            
	         } 
			 break;
		 }
		 case TAKE_PIC:{
			 if(resultCode==Activity.RESULT_OK) 
	         { 
				 Bitmap bitmap = (Bitmap) data.getExtras().get("data");
		//System.out.println("bitmap-->"+bitmap.toString());		 
				 SimpleDateFormat s=new SimpleDateFormat("yyyyMMddhhmmss");  
				 String    date    =    s.format(new Date()); 
				 
				 if (android.os.Environment.getExternalStorageState().equals(  
						    android.os.Environment.MEDIA_MOUNTED)) { 
				 File file=new File(img_path);
				 if(!file.exists()){
					 file.mkdirs();
				 }
				  String save_path=img_path+date+".jpg";
				  //bitmap=CompressImg.compressImage(bitmap);
				saveBitmap(bitmap,save_path);
				tv_prompt.setVisibility(View.GONE);
	             line_setbg.setBackgroundDrawable(Drawable.createFromPath(save_path));
	             Editor ed=MyInstance.getSharedPreferencesInstance(mContext).edit();
	             ed.putString(STATUS_BG, save_path);
	             ed.commit();
				 }else{
					 handler.sendMessage(handler.obtainMessage(SHOW_MESSAGE, getApplicationContext().getResources().getString(R.string.noSDcard)));
				 }
	         }
			 break;
		 }
		
		 }
		 }
	//保存图片
			private void saveBitmap(Bitmap bitmap,String filePath){
				FileOutputStream bos = null;
				File imgFile = new File(filePath);
				try {

					
						 if(!imgFile.exists())
							 imgFile.createNewFile();
					
					bos = new FileOutputStream(imgFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						bos.flush();
						bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}


			@Override
			protected void onDestroy() {
				// TODO Auto-generated method stub
				unregisterReceiver(myReceiver);
				super.onDestroy();
			}
			
			
			
			
			
}

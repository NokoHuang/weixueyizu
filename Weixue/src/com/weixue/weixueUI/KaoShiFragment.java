package com.weixue.weixueUI;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.weixue.Function.NetWork;
import com.weixue.Methods.ResolveJSON;
import com.weixue.Model.Title;
import com.weixue.Utils.Constants_Url;

public class KaoShiFragment extends Fragment {
	private int mNum=300;
	private int uId;
	private View v = null; 
	private TextView mTitle;
	private	RadioButton radioA;
	private	RadioButton radioB;
	private	RadioButton radioC;
	private	RadioButton radioD;
	private RadioGroup radioGroup;
	private ProgressDialog pd;
	
	private int isShowDialog=0;
	
	public static KaoShiFragment newInstance(int num,int uId) {  
		KaoShiFragment  array= new KaoShiFragment();  
		Bundle args = new Bundle();  
		args.putInt("num", num);
		args.putInt("uId", uId);
		array.setArguments(args);  
		return array;  
	}

	@Override  
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		mNum = getArguments() != null ? getArguments().getInt("num") : 1; 
		uId=getArguments() != null ? getArguments().getInt("uId") : 1; 
		System.out.println("mNum Fragment create ="+ mNum); 
		
		pd = new ProgressDialog(getActivity());
		pd.setTitle("提示");
		pd.setMessage("正在获取数据");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}  

	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  
			Bundle savedInstanceState) {  
		System.out.println("onCreateView = ");  
		//在这里加载每个 fragment的显示的 View 
		v = inflater.inflate(R.layout.fragment_kaoshi, container, false); 
		Init();
		mTitle.setText(mNum+"."+mTitle.getText());
		/*if(mTitle.getText().equals("test")){
			pd.show();
		}*/
		GetTitle();
		return v;  
	} 

	public void Init(){
		/*LayoutInflater inflater=getLayoutInflater(null);
		View view= inflater.inflate(R.layout.fragment_kaoshi, null,false);*/
		mTitle=(TextView) v.findViewById(R.id.pro_text);
		radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);  
		radioA = (RadioButton) v.findViewById(R.id.radioA);
		radioB = (RadioButton) v.findViewById(R.id.radioB);
		radioC = (RadioButton) v.findViewById(R.id.radioC);
		radioD = (RadioButton) v.findViewById(R.id.radioD);
	}

	public void GetTitle(){
		Runnable run=new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUrl=Constants_Url.GET_TITLE+"?uid="+2+"&qid="+1+"&answer="+"D";

				try {
					String jsonStr=NetWork.getData(requestUrl);
					//System.out.println("--->"+jsonStr);  
					handler.sendMessage(handler.obtainMessage(1, jsonStr));
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}};
			new Thread(run).start();
	}
	
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 1:
				String jsonStr=(String) msg.obj;
				try {
					pd.dismiss();
					Title title=ResolveJSON.JSON_TO_Title(jsonStr);
					String titleStr=title.getqContent();
					String[] tmp_waset = new String[6];
					tmp_waset=titleStr.split("#");
					
					mTitle.setText(mNum+"."+tmp_waset[0]);
					radioA.setText("A."+tmp_waset[1]);
					radioB.setText("B."+tmp_waset[2]);
					radioC.setText("C."+tmp_waset[3]);
					radioD.setText("D."+tmp_waset[4]); 
					isShowDialog=1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		};
	};
	
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if(isShowDialog==0){
				pd.show();
			}
        } else {
        	System.out.println("相当于Fragment的onPause = "+mNum); 
        }
	}
	
}

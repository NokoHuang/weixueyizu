package com.weixue.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.weixue.Function.NetWork;
import com.weixue.Utils.Constants_Url;

public class GetAboutus {
	public static final String ABOUTUS_URL = "http://dekang.ezoas.com/app/ezoas.load.about.us.asp";
	
	public static String getAboutus()throws Exception{
		//String str = "";
		String str_return="";
		JSONObject json=null;
		//获取对象
		try {
			String result=NetWork.postData(ABOUTUS_URL,"GB2312");
		/*	char[] ch=result.toCharArray();
			int c=1;
			int mark=0;
			int mark2=0;
			for(int i=0;i<ch.length;i++){
				if(ch[i]=='\"'){
					if(c==4){
					mark=i;
					}
					else if(c==5){
						mark2=i;
					}
					c++;
				}
			}
			
			for(int i=0;i<ch.length;i++){
				if(i!=mark&&i!=mark2){
					str+=String.valueOf(ch[i]);				
				}else{
					str+=String.valueOf('\'');	
				}
			}*/
			if(result!=null){
			json= new JSONObject(result);			
			str_return=json.getString("aboutus");
			}
			//str_return=str_return.replace("\'", "\"");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str_return;
		
	}
	


}

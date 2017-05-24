package com.weixue.Methods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.app.ProgressDialog;

import com.weixue.Function.NetWork;

public class DownLoadFile {
	/**下载apk*/
public static boolean downLoadApk(String requestUrl,File file,ProgressDialog pd)throws Exception{
	FileOutputStream fileout = new FileOutputStream(file);
	HttpURLConnection con=NetWork.returnHttpURLConnection(requestUrl);
	InputStream in=con.getInputStream();
	
	int contentLength=con.getContentLength()/1024;
	pd.setMax(contentLength);
	byte[] buffer=new byte[1024];
	int readLength=0;
	int process = 0;
	System.out.println("contentLength-->"+contentLength);
	while((readLength=in.read(buffer))!=-1){
		fileout.write(buffer, 0, readLength);
		process+=readLength;
		pd.setProgress((process/1024));
	    
		
		//Thread.sleep(10);
	}
	fileout.flush();
	fileout.close();
	in.close();
	
	System.out.println("下载完成");
	return true;
} 
/**
 * 下载图片并保存
 * @param strUrl 头像的下载地址
 * @param saveUrl 保存的路径
 * @return 0、文件下载失败；1、文件已存在；2、文件下载成功
 */
public static int downLoadPic(String strUrl,String saveUrl){
	int result=0;
	FileOutputStream fileout=null;
	InputStream in=null;
	File fileContent=new File(saveUrl);
	
	if(!fileContent.exists()){
		try {
			fileContent.createNewFile();					
			fileout=new FileOutputStream(fileContent);
			in=NetWork.returnStream(strUrl);
			
			int i=0;
			byte[] b=new byte[1024];
			while((i=in.read(b))!=-1){
				fileout.write(b, 0, i);
			}
			fileout.flush();							
			//下载成功
			result=2;
			
		} catch (Exception e) {
			// TODO Auto-generated catch blockr
			result=0;
			fileContent.delete();
		
			return result;
			
		}finally{
			try {
				if(fileout!=null) fileout.close();
				if(in!=null) in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			}
			
		}
	}else{
		result=1;		
	}

	return result;
 }
}

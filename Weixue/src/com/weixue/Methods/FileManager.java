package com.weixue.Methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {
	
	/**
	 * 复制文件
	 * @param oldFileLocation
	 * @param newFileLocation
	 * @return  0、文件复制失败；1、文件已存在；2、文件复制成功
	 */
public static int fileCopyToOtherLocation(String  oldFileLocation,String newFileLocation){
	int result=0;
	FileInputStream fileIn=null;
	FileOutputStream fileOut=null;
	
		File oldFile=new File(oldFileLocation);
		File newFile=new File(newFileLocation);
		
		if(!newFile.exists()){
			try {							
				newFile.createNewFile();	
				fileIn=new FileInputStream(oldFile);
				fileOut=new FileOutputStream(newFile);				
				
				int i=0;
				byte[] b=new byte[1024];
				while((i=fileIn.read(b))!=-1){
					fileOut.write(b, 0, i);
				}
				fileOut.flush();							
				//复制成功
				result=2;
				
			} catch (Exception e) {
				// TODO Auto-generated catch blockr
				result=0;
				newFile.delete();			
				return result;
				
			}finally{
				try {
					if(fileOut!=null) fileOut.close();
					if(fileIn!=null) fileIn.close();
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

/**
 * 删除文件
 * @param file 目录或者单个文件都行
 */
	public static void deleteFile(File file){
		if(file.exists()){
		if (file.isFile() || file.listFiles().length == 0) {
			file.delete();
			
		}else {
			File[] files = file.listFiles();
			for(File f : files){
				deleteFile(f);
				f.delete();
			}
	
		  }
		}
	}


}

package com.weixue.Model;

public class DownloadFile {

	public int id;
	public String fileName;
	public String downLoadAddress;
	public int fileSize;
	public String downloadState;
	public int downloadSize;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDownLoadAddress() {
		return downLoadAddress;
	}
	public void setDownLoadAddress(String downLoadAddress) {
		this.downLoadAddress = downLoadAddress;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public String getDownloadState() {
		return downloadState;
	}
	public void setDownloadState(String downloadState) {
		this.downloadState = downloadState;
	}
	public int getDownloadSize() {
		return downloadSize;
	}
	public void setDownloadSize(int downloadSize) {
		this.downloadSize = downloadSize;
	}
	
	
}

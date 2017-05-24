package com.weixue.Model;

import java.io.Serializable;

public class Status implements Serializable{

	private static final long serialVersionUID = 1L;
private String bigPicPath;
private int commentCount;
private String contentText;
private String createTime;
private String picPath;
private int statusID;
private int statusType;
private int userID;
private String userName;
private String userPicPath;
public String getBigPicPath() {
	return bigPicPath;
}
public void setBigPicPath(String bigPicPath) {
	this.bigPicPath = bigPicPath;
}
public int getCommentCount() {
	return commentCount;
}
public void setCommentCount(int commentCount) {
	this.commentCount = commentCount;
}
public String getContentText() {
	return contentText;
}
public void setContentText(String contentText) {
	this.contentText = contentText;
}
public String getCreateTime() {
	return createTime;
}
public void setCreateTime(String createTime) {
	this.createTime = createTime;
}
public String getPicPath() {
	return picPath;
}
public void setPicPath(String picPath) {
	this.picPath = picPath;
}
public int getStatusID() {
	return statusID;
}
public void setStatusID(int statusID) {
	this.statusID = statusID;
}
public int getStatusType() {
	return statusType;
}
public void setStatusType(int statusType) {
	this.statusType = statusType;
}
public int getUserID() {
	return userID;
}
public void setUserID(int userID) {
	this.userID = userID;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getUserPicPath() {
	return userPicPath;
}
public void setUserPicPath(String userPicPath) {
	this.userPicPath = userPicPath;
}

}

package com.weixue.Model;

public class Comment {
private String content;
private String createTime;
private int ID;
private int targetID;
private int type;
private int UID;
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getCreateTime() {
	return createTime;
}
public void setCreateTime(String createTime) {
	this.createTime = createTime;
}
public int getID() {
	return ID;
}
public void setID(int iD) {
	ID = iD;
}
public int getTargetID() {
	return targetID;
}
public void setTargetID(int targetID) {
	this.targetID = targetID;
}
public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}
public int getUID() {
	return UID;
}
public void setUID(int uID) {
	UID = uID;
}


}

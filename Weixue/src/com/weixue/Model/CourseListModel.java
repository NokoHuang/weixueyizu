package com.weixue.Model;

import java.io.Serializable;

public class CourseListModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4366171739518915682L;

	/*"Category": 0,
    "CourseID": 1,
    "CourseName": "Linux",
    "Course_ImgUrl": "http://222.126.246.151:9105/Content/Imgs/Course/20140515204452.jpg",
    "Course_LearnCents": 0,
    "Course_LearnMoney": 0,
    "CoursewareCount": 0,
    "DetailIntro": "Linux",
    "Introduction": "Linux课程",
    "PraiseDegree": 0,
    "PublishDate": null,
    "StudyCount": 0,
    "Tags": null,
    "Type": 1*/
	
	private int Category;
	private int CourseID;
	private String CourseName;
	private String Course_ImgUrl;
	private int Course_LearnCents;
	private int Course_LearnMoney;
	private int CoursewareCount;
	private String DetailIntro;
	private String Introduction;
	private int PraiseDegree;
	private String PublishDate;
	private int StudyCount;
	private String Tags;
	public int getCategory() {
		return Category;
	}
	public void setCategory(int category) {
		Category = category;
	}
	public int getCourseID() {
		return CourseID;
	}
	public void setCourseID(int courseID) {
		CourseID = courseID;
	}
	public String getCourseName() {
		return CourseName;
	}
	public void setCourseName(String courseName) {
		CourseName = courseName;
	}
	public String getCourse_ImgUrl() {
		return Course_ImgUrl;
	}
	public void setCourse_ImgUrl(String course_ImgUrl) {
		Course_ImgUrl = course_ImgUrl;
	}
	public int getCourse_LearnCents() {
		return Course_LearnCents;
	}
	public void setCourse_LearnCents(int course_LearnCents) {
		Course_LearnCents = course_LearnCents;
	}
	public int getCourse_LearnMoney() {
		return Course_LearnMoney;
	}
	public void setCourse_LearnMoney(int course_LearnMoney) {
		Course_LearnMoney = course_LearnMoney;
	}
	public int getCoursewareCount() {
		return CoursewareCount;
	}
	public void setCoursewareCount(int coursewareCount) {
		CoursewareCount = coursewareCount;
	}
	public String getDetailIntro() {
		return DetailIntro;
	}
	public void setDetailIntro(String detailIntro) {
		DetailIntro = detailIntro;
	}
	public String getIntroduction() {
		return Introduction;
	}
	public void setIntroduction(String introduction) {
		Introduction = introduction;
	}
	public int getPraiseDegree() {
		return PraiseDegree;
	}
	public void setPraiseDegree(int praiseDegree) {
		PraiseDegree = praiseDegree;
	}
	public String getPublishDate() {
		return PublishDate;
	}
	public void setPublishDate(String publishDate) {
		PublishDate = publishDate;
	}
	public int getStudyCount() {
		return StudyCount;
	}
	public void setStudyCount(int studyCount) {
		StudyCount = studyCount;
	}
	public String getTags() {
		return Tags;
	}
	public void setTags(String tags) {
		Tags = tags;
	}
	
}

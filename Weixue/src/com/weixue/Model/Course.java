package com.weixue.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
	private static final long serialVersionUID = 2L;
	private int category;// 0
	private int courseID;// 8
	private String courseName;// JAVA
	private String course_ImgUrl;// http:\/\/222.126.246.151:9105\/Content\/Imgs\/Course\/20131225144135.jpg
	private int coursewareCount;// 0
	private String detailIntro;// ""
	private String introduction;// JAVA
	private int praiseDegree;// 0
	private String publishDate;// 2013-12-25 14:41:50
	private int studyCount;// 0
	private String tags;// JAVA
	private int type;// 1
	private int course_LearnCents;// 0
	private int course_LearnMoney;// 0

	/*
	 * [{"Category":0,"CourseID":8,"CourseName":"JAVA","Course_ImgUrl":
	 * "http:\/\/222.126.246.151:9105\/Content\/Imgs\/Course\/20131225144135.jpg",
	 * "Course_LearnCents"
	 * :0,"Course_LearnMoney":0,"CoursewareCount":0,"DetailIntro":"",
	 * "Introduction"
	 * :"JAVA","PraiseDegree":0,"PublishDate":"2013-12-25 14:41:50"
	 * ,"StudyCount":0,"Tags":"JAVA","Type":1}]
	 */
	public int getCourse_LearnCents() {
		return course_LearnCents;
	}

	public void setCourse_LearnCents(int course_LearnCents) {
		this.course_LearnCents = course_LearnCents;
	}

	public int getCourse_LearnMoney() {
		return course_LearnMoney;
	}

	public void setCourse_LearnMoney(int course_LearnMoney) {
		this.course_LearnMoney = course_LearnMoney;
	}

	public static Course course = null;

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourse_ImgUrl() {
		return course_ImgUrl;
	}

	public void setCourse_ImgUrl(String course_ImgUrl) {
		this.course_ImgUrl = course_ImgUrl;
	}

	public int getCoursewareCount() {
		return coursewareCount;
	}

	public void setCoursewareCount(int coursewareCount) {
		this.coursewareCount = coursewareCount;
	}

	public String getDetailIntro() {
		return detailIntro;
	}

	public void setDetailIntro(String detailIntro) {
		this.detailIntro = detailIntro;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public int getPraiseDegree() {
		return praiseDegree;
	}

	public void setPraiseDegree(int praiseDegree) {
		this.praiseDegree = praiseDegree;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public int getStudyCount() {
		return studyCount;
	}

	public void setStudyCount(int studyCount) {
		this.studyCount = studyCount;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}

package com.weixue.Model;

import java.util.List;

public class BookResultModel {
	private int SubjectID;
	private String SubjectName;
	private String Introduce;
	private List<Course> CourseList;

	public List<Course> getCourseList() {
		return CourseList;
	}

	public void setCourseList(List<Course> courseList) {
		CourseList = courseList;
	}

	public int getSubjectID() {
		return SubjectID;
	}

	public void setSubjectID(int subjectID) {
		SubjectID = subjectID;
	}

	public String getSubjectName() {
		return SubjectName;
	}

	public void setSubjectName(String subjectName) {
		SubjectName = subjectName;
	}

	public String getIntroduce() {
		return Introduce;
	}

	public void setIntroduce(String introduce) {
		Introduce = introduce;
	}

}

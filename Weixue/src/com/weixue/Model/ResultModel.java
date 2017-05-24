package com.weixue.Model;

import java.io.Serializable;
import java.util.List;

public class ResultModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8898859464708580229L;

	 /*"Introduce": "Linux应用",
     "SubjectID": 1,
     "SubjectName": "Linux"*/
	
	private List<CourseListModel> CourseList;
	
	private String Introduce;
	
	private int SubjectID;
	
	private String SubjectName;

	public List<CourseListModel> getCourseList() {
		return CourseList;
	}

	public void setCourseList(List<CourseListModel> courseList) {
		CourseList = courseList;
	}

	public String getIntroduce() {
		return Introduce;
	}

	public void setIntroduce(String introduce) {
		Introduce = introduce;
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
	
}

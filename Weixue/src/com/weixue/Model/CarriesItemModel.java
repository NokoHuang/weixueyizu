package com.weixue.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 试卷model
 * 
 * @author zeda
 * 
 */
public class CarriesItemModel implements Serializable {
	private int id;
	private String title;
	private int maxScore;
	private int passScore;
	private int time;
	private int questionNum;
	private int pass;// 是否已经通过该门考试
	private int totalNum;
	private HashMap<Integer, CarriesQueModel> ques;

	public int getPass() {
		return pass;
	}

	public void setPass(int pass) {
		this.pass = pass;
	}

	public HashMap<Integer, CarriesQueModel> getQues() {
		return ques;
	}

	public void setQues(HashMap<Integer, CarriesQueModel> ques) {
		this.ques = ques;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public int getPassScore() {
		return passScore;
	}

	public void setPassScore(int passScore) {
		this.passScore = passScore;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(int questionNum) {
		this.questionNum = questionNum;
	}

}

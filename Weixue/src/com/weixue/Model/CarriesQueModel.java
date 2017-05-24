package com.weixue.Model;

import java.util.HashMap;

/**
 * 试卷题目model
 * 
 * @author zeda
 * 
 */
public class CarriesQueModel {
	private int index;
	private String que;
	private String info;// 详细解答
	private ImageModel image;
	private int type;// 1单选 2多选 3判断
	private String typeStr;
	private int score;// 这道题的分数
	private String no;// 题号
	private boolean isGetScore;
	private HashMap<Integer, AnswerModel> answers;// 选项列表
	private int lastSelectIndex;// 上一个选中的选项，单选和判断题的时候用,默认为-1
	private boolean isFirst;

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public boolean isGetScore() {
		return isGetScore;
	}

	public void setGetScore(boolean isGetScore) {
		this.isGetScore = isGetScore;
	}

	public int getLastSelectIndex() {
		return lastSelectIndex;
	}

	public void setLastSelectIndex(int lastSelectIndex) {
		this.lastSelectIndex = lastSelectIndex;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getQue() {
		return que;
	}

	public void setQue(String que) {
		this.que = que;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public ImageModel getImage() {
		return image;
	}

	public void setImage(ImageModel image) {
		this.image = image;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public HashMap<Integer, AnswerModel> getAnswers() {
		return answers;
	}

	public void setAnswers(HashMap<Integer, AnswerModel> answers) {
		this.answers = answers;
	}

}

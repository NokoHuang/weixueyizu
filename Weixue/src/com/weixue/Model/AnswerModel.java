package com.weixue.Model;

/**
 * 答案选项model
 * 
 * @author zeda
 * 
 */
public class AnswerModel {
	private String answer;
	private boolean isSelected;
	private boolean isCorrect;

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

}

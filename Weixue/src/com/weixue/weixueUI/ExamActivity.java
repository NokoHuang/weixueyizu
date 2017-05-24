package com.weixue.weixueUI;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 考试
 * @author chenjunjie
 *
 */
public class ExamActivity extends Activity{
	//问题
	private TextView tv_uestion;
	//答案选项
private RadioGroup rg_answers;
private RadioButton rb_answer1,rb_answer2,rb_answer3,rb_answer4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.exam_page);
	}

	//初始化
	public void initUi(){
		//问题
		tv_uestion=(TextView) findViewById(R.id.tv_question);
		//答案选项
		rg_answers=(RadioGroup) findViewById(R.id.rg_answers);
		rb_answer1=(RadioButton) findViewById(R.id.rb_answer1);
		rb_answer2=(RadioButton) findViewById(R.id.rb_answer2);
		rb_answer3=(RadioButton) findViewById(R.id.rb_answer3);
		rb_answer4=(RadioButton) findViewById(R.id.rb_answer4);
	}
}

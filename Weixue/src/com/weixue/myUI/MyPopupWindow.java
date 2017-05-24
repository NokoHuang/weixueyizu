package com.weixue.myUI;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.weixue.weixueUI.R;

public class MyPopupWindow extends PopupWindow {

	private Button btn_first, btn_second, btn_cancel;
	private View mMenuView;

	public MyPopupWindow(Context context, OnClickListener itemsOnClick,
			String firstStr, String secondStr) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popup_setting_page, null);
		btn_first = (Button) mMenuView.findViewById(R.id.btn_first);
		btn_second = (Button) mMenuView.findViewById(R.id.btn_second);
		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		// 取消按钮
		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});
		// 设置按钮监听
		btn_first.setOnClickListener(itemsOnClick);
		btn_second.setOnClickListener(itemsOnClick);

		if (firstStr != null && firstStr != "") {
			btn_first.setText(firstStr);
		}

		if (secondStr != null && secondStr != "") {
			btn_second.setText(secondStr);
		}

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.PopupAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);

	}

}

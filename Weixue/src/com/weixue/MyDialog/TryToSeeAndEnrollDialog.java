package com.weixue.MyDialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.weixue.weixueUI.R;

/**
 * 
 * @author chenjunjie
 *
 */

public class TryToSeeAndEnrollDialog extends Dialog{

	public TryToSeeAndEnrollDialog(Context context, int theme) {
		super(context, theme);
	}

	public TryToSeeAndEnrollDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static class Builder {

		private Context context;
		private int howMuch=0;
		private int balance=0;
		
		private DialogInterface.OnClickListener deleteButtonClickListener,
				tryToSeeButtonClickListener,enrollButtonClickListener;

		public Builder(Context context,int howMuch,int balance) {
			this.context = context;
			this.howMuch=howMuch;
			this.balance=balance;
		}

		public Builder setContentView(View v) {
			
			return this;
		}
		
		/**
		 * 设置报读按钮监听者
		 */
		public Builder setEnrollButtonClickListener(DialogInterface.OnClickListener listener) {
			this.enrollButtonClickListener = listener;
			return this;
		}

		
		
		
		/**
		 * 设置删除对话框按钮监听者
		 */
		public Builder setDeleteButtonClickListener(
				DialogInterface.OnClickListener listener) {
			
			this.deleteButtonClickListener = listener;
			return this;
		}
		/**
		 * 设置试听按钮监听者
		 */
		public Builder setTryToSeeButtonClickListener(
				DialogInterface.OnClickListener listener) {
			
			this.tryToSeeButtonClickListener = listener;
			return this;
		}

	

		public TryToSeeAndEnrollDialog create() {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final TryToSeeAndEnrollDialog dialog = new TryToSeeAndEnrollDialog(context, R.style.myDialog);
			View layout = inflater.inflate(R.layout.dialog_trytosee_enroll_page, null);
			TextView tv_showHowMuch=(TextView) layout.findViewById(R.id.tv_showHowMuch);
			TextView tv_balance=(TextView) layout.findViewById(R.id.tv_balance);
			tv_showHowMuch.setText(String.valueOf(howMuch));
			tv_balance.setText(String.valueOf(balance));
			dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		

				if (deleteButtonClickListener != null) {

					((ImageButton) layout.findViewById(R.id.imgbtn_delete_dialog))

					.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							deleteButtonClickListener.onClick(dialog,
									1);
						}
					});

				}
			
			
				if (tryToSeeButtonClickListener != null) {
					((ImageButton) layout.findViewById(R.id.img_trytosee))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {

									tryToSeeButtonClickListener.onClick(dialog,
											2);
								}
							});

				}
			 
			
			if (enrollButtonClickListener != null) {
				((ImageButton) layout.findViewById(R.id.img_enroll))
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {

								enrollButtonClickListener.onClick(dialog,
										3);
							}
						});

			}
		
			
			//dialog.setContentView(layout);
			return dialog;
		}

		
	}
	
}

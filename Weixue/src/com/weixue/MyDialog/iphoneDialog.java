package com.weixue.MyDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.weixue.weixueUI.R;

/**
 * 对话框[仿Iphone风格]
 * @author chenjunjie
 *
 */
public class iphoneDialog extends AlertDialog {

	private iphoneDialogView view;
	private LayoutInflater mInflater;
	private Context context;

	protected iphoneDialog(Context context) {
		super(context);
		this.context = context;
		mInflater = LayoutInflater.from(this.context);
		view = (iphoneDialogView) mInflater.inflate(R.layout.dialog_iphone, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(view);
	}

	@Override
	public void setMessage(CharSequence message) {
		view.setMessage(message);
	}

	@Override
	public void setTitle(CharSequence title) {
		view.setTitle(title);
	}

	@Override
	public void setButton(CharSequence text, final OnClickListener listener) {
		final Button button = (Button) view.findViewById(R.id.dialog_yes);
		button.setText(text);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				listener.onClick(iphoneDialog.this, 0);
				dismiss();
			}
		});

		super.setButton(text, listener);
	}

	@Override
	public void setButton2(CharSequence text, final OnClickListener listener) {
		final Button button = (Button) view.findViewById(R.id.dialog_no);
		button.setText(text);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				listener.onClick(iphoneDialog.this, 0);
				dismiss();
			}
		});
		super.setButton2(text, listener);
	}

	@Override
	public void setButton3(CharSequence text, final OnClickListener listener) {
		final Button button = (Button) view.findViewById(R.id.dialog_cancel);
		button.setText(text);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				listener.onClick(iphoneDialog.this, 0);
				dismiss();
			}
		});
		super.setButton3(text, listener);
	}
	
	public static class iphoneDialogBuilder extends AlertDialog.Builder {

		private iphoneDialog md;
		private Context context;

		public iphoneDialogBuilder(Context context) {
			super(context);
			md = new iphoneDialog(context);
			this.context = context;
		}

		public iphoneDialogBuilder setMessage(int messageId) {
			md.setMessage(context.getResources().getString(messageId));
			return this;
		}

		public iphoneDialogBuilder setMessage(CharSequence message) {
			md.setMessage(message);
			return this;
		}

		public iphoneDialogBuilder setTitle(int titleId) {
			md.setTitle(context.getResources().getString(titleId));
			return this;
		}

		public iphoneDialogBuilder setTitle(CharSequence title) {
			md.setTitle(title);
			return this;
		}

		// 认同按钮
		public iphoneDialogBuilder setPositiveButton(int textId,
				OnClickListener listener) {
			md.setButton(context.getResources().getString(textId), listener);
			return this;
		}

		// 认同按钮
		public iphoneDialogBuilder setPositiveButton(CharSequence text,
				OnClickListener listener) {
			md.setButton(text, listener);
			return this;
		}

		// 中立按钮
		public iphoneDialogBuilder setNeutralButton(int textId,
				OnClickListener listener) {
			md.setButton2(context.getResources().getString(textId), listener);
			return this;
		}

		// 中立按钮
		public iphoneDialogBuilder setNeutralButton(CharSequence text,
				OnClickListener listener) {
			md.setButton2(text, listener);
			return this;
		}

		// 否定按钮
		public iphoneDialogBuilder setNegativeButton(int textId,
				OnClickListener listener) {
			md.setButton3(context.getResources().getString(textId), listener);
			return this;
		}

		// 否定按钮
		public iphoneDialogBuilder setNegativeButton(CharSequence text,
				OnClickListener listener) {
			md.setButton3(text, listener);
			return this;
		}

		@Override
		public iphoneDialog create() {
			return md;
		}
	}
	
}

package com.example.mytakeout.ui.views;

import android.app.Dialog;
import android.content.Context;

/**
 * 自定义dialog
 * @author admin
 *
 */
public class CustomDialog extends Dialog {

	public interface CustomDialogHelper {
		/**
		 * 用户windowsfocused的时候展示，用户自定义的操作
		 * 
		 * @param dialog
		 */
		void showDialog(CustomDialog dialog);
	}

//	private static CustomDialog customDialog = null;
	private CustomDialogHelper cdHelper;

	private CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public void setCdHelper(CustomDialogHelper cdHelper) {
		this.cdHelper = cdHelper;
	}

/*	public static CustomDialog createDialog(Context context, int contentView) {
		if(customDialog!=null){
			customDialog.dismiss();
			customDialog=null;
		}
		customDialog = new CustomDialog(context, R.style.CustomDialog);
		customDialog.setContentView(contentView);
		customDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customDialog;
	}*/

/*	*//**
	 * 对于dialog的展示和隐藏的转换
	 *//*
	public void changeDialogZT() {
		if (customDialog == null) {
			return;
		}
		if (!customDialog.isShowing()) {
			customDialog.show();
		} else {
			customDialog.dismiss();
		}
	}*/
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (cdHelper != null) {
			cdHelper.showDialog(this);
		}
	}
}

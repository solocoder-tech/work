package com.example.mytakeout.ui.views;

import android.content.Context;
import android.view.Gravity;

import com.example.mytakeout.R;


public class CommonDialogManager {
	CustomDialog customDialog=null;
	private static CommonDialogManager instance=null;
	private CommonDialogManager(){};
	public static CommonDialogManager getInstance(){
		if(instance==null){
			instance=new CommonDialogManager();
		}
		return instance;
	}
	
	public CustomDialog createDialog(Context context, int contentView) {
		if(customDialog!=null){
			customDialog.dismiss();
			customDialog=null;
		}
		customDialog = new CustomDialog(context, R.style.CustomDialog);
		customDialog.setContentView(contentView);
		customDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return customDialog;
	}
}

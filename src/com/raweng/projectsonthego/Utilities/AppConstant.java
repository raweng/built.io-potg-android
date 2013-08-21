package com.raweng.projectsonthego.Utilities;

import android.app.ProgressDialog;
import android.content.Context;

import com.raweng.projectsonthego.R;

public class AppConstant {

	public static boolean DEBUG = true;

	// user role
	public static enum userRole{

		admin, moderator, member,guest
	}
	public static String[] status = {"Open","In Progress","To Be Tested","Closed"};
	public static String[] severity = {"Show Stopper","Critical","Major","Minor"};
	public static enum reproducible{

		Always,Sometimes,Rarely,Unable
	}
	public static enum monthName{

		January, February, March, April, May, June, July, August, September, October, November, December

	}
	public static String extractMonthName(int month){
		switch(month){
		case 1:
			return  AppConstant.monthName.January.toString();
		case 2:
			return AppConstant.monthName.February.toString();
		case 3:
			return AppConstant.monthName.March.toString();
		case 4:
			return AppConstant.monthName.April.toString();
		case 5:
			return AppConstant.monthName.May.toString();
		case 6:
			return AppConstant.monthName.June.toString();
		case 7:
			return AppConstant.monthName.July.toString();
		case 8:
			return AppConstant.monthName.August.toString();
		case 9:
			return AppConstant.monthName.September.toString();
		case 10:
			return AppConstant.monthName.October.toString();
		case 11:
			return AppConstant.monthName.November.toString();
		case 12:
			return AppConstant.monthName.December.toString();
		}
		return null;
	}

	// bundle key
	public static final String ARG_MENU_DETAIL_NUMBER 			= "menu_index";


	// menu type
	public static final int PROJECT 		  = 0;
	public static final int TASK 			  = 1;
	public static final int BUG 			  = 2;
	public static final int MILESTONE 		  = 3;
	public static final int PROJECT_TASK 	  = 4;
	public static final int PROJECT_BUG 	  = 5;
	public static final int PROJECT_MILESTONE = 6;

	public static final int COMMENT_COUNT_REQUEST_CODE = 5000;

	public static ProgressDialog getProgressDialog(Context context){
		ProgressDialog	progressDialog;
		if (context != null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle(context.getResources().getString(
					R.string.loading));
			progressDialog.setMessage(context.getResources().getString(
					R.string.please_wait));
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			return progressDialog;
		}
		return null;
	}
}

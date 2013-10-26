package com.raweng.projectsonthego;

import android.app.Application;

import com.raweng.built.Built;
import com.raweng.projectsonthego.Utilities.AppUtils;

public class ApplicationClass extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();		
		try {
			//Initializing built with your application
			Built.initializeWithApiKey(getApplicationContext(), "blt3b011c0e38ed1d82", "potg");
		} catch (Exception e) {
			AppUtils.showLog("ApplicationCclass", e.toString());
		}
	}

}

package com.raweng.projectsonthego.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	
	    public static SharedPreferences get(Context context) {
	    	
	        return context.getSharedPreferences("BUILT_IO_PROJECT_APP", 0);
	}
}

package com.raweng.built.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class BuiltSharedPrefrence {

	public static SharedPreferences get(Context context) {

		return context.getSharedPreferences("BUILT_IO", 0);
	}

	public static void setFlushInterval(Context context, long flushInterval) {
		SharedPreferences prefs = get(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("flushInterval", flushInterval);
		editor.commit();
	}

	public static long getFlushInterval(Context context) {
		SharedPreferences prefs = get(context);
		return prefs.getLong("flushInterval", 60000);

	}
}

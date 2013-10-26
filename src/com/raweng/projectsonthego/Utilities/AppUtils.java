package com.raweng.projectsonthego.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

public class AppUtils {


	
	/**
	 * To show log.
	 * 
	 * @param tag
	 * 			class name
	 * @param message
	 * 			string message
	 *            
	 */
	public static void showLog(String tag, String message) {
		if(AppConstant.DEBUG){
			Log.i(tag, message);
		}
	}
	
	/**
	 * 
	 * Returns calendar object
	 * @param
	 * 		string date
	 * 
	 */
	public static Calendar parseDate(String date) throws ParseException {
		Date dateObject 	= null;
		String month 		= "";
		String day 			= "";
		String year 		= "";
		String hourOfDay 	= "";
		String min 			= "";
		String sec			= "";
		Calendar cal 		= Calendar.getInstance();

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateObject = dateFormatter.parse(date);

		month 	  = new SimpleDateFormat("MM").format(dateObject);
		day 	  = new SimpleDateFormat("dd").format(dateObject);
		year 	  = new SimpleDateFormat("yyyy").format(dateObject);
		hourOfDay = new SimpleDateFormat("HH").format(dateObject);
		min 	  = new SimpleDateFormat("mm").format(dateObject);
		sec 	  = new SimpleDateFormat("ss").format(dateObject);


		cal.setTimeZone(TimeZone.getDefault());
		cal.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hourOfDay), Integer.valueOf(min), Integer.valueOf(sec));

		month 	   = null;
		day 	   = null;
		year 	   = null;
		hourOfDay  = null;
		min 	   = null;
		sec        = null;
		dateObject = null;

		return cal;
	}
}

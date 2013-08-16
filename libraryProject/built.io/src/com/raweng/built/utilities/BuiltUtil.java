package com.raweng.built.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Utility class.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltUtil {

	/**
	 * Convert given date in user&#39;s timezone.
	 * 
	 * @param date
	 * 				date in &#34;yyyy-MM-dd&#39;T&#39;HH:mm:ssZ&#34; format.
	 * @return
	 * 				{@link Calendar} object.
	 * 
	 * @throws ParseException 
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

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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

	/**
	 * Convert given date in user&#39;s timezone.
	 * 
	 * @param date
	 * 				date in string format.
	 * 
	 * @param dateFormat
	 * 				 date format.
	 * 
	 * @return
	 * 				{@link Calendar} object.
	 * 
	 * @throws ParseException 
	 */
	public static Calendar parseDate(String date, String dateFormat) throws ParseException {
		Date dateObject   = null;
		String month      = "";
		String day        = "";
		String year       = "";
		String hourOfDay  = "";
		String min        = "";
		String sec        = "";
		Calendar cal      = Calendar.getInstance();

		SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
		dateObject = dateFormatter.parse(date);

		month     = new SimpleDateFormat("MM").format(dateObject);
		day       = new SimpleDateFormat("dd").format(dateObject);
		year      = new SimpleDateFormat("yyyy").format(dateObject);
		hourOfDay = new SimpleDateFormat("HH").format(dateObject);
		min       = new SimpleDateFormat("mm").format(dateObject);
		sec       = new SimpleDateFormat("ss").format(dateObject);


		cal.setTimeZone(TimeZone.getDefault());
		cal.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hourOfDay), Integer.valueOf(min), Integer.valueOf(sec));

		month     = null;
		day       = null;
		year      = null;
		hourOfDay = null;
		min       = null;
		sec       = null;
		dateObject = null;

		return cal;
	}

	/**
	 * Type to compare dates. 
	 * 
	 * @author raw engineering, Inc
	 *
	 */
	public static enum DateComapareType{

		WEEK, DAY, HOURS, MINUTES, SECONDS

	};

	/**
	 * To provide date difference between dateOne and dateTwo.<br>
	 * difference provided into {@link DateComapareType} which will be specified.
	 * 
	 * @param dateOne
	 * 				{@link Date} object.
	 * 
	 * @param dateTwo
	 * 				{@link Date} object .
	 * @param type
	 * 				{@link DateComapareType} object to specify date Comparison type.
	 * 
	 * @return
	 * 				{@link Long} value.
	 * 
	 * @throws Exception 
	 * 
	 */
	public static long compareDates(Date dateOne, Date dateTwo, DateComapareType type) throws Exception{
	
			if(type.equals(DateComapareType.SECONDS)){

				return  TimeUnit.MILLISECONDS.toSeconds(dateOne.getTime() - dateTwo.getTime());

			}else if(type.equals(DateComapareType.MINUTES)){

				return  TimeUnit.MILLISECONDS.toMinutes(dateOne.getTime() - dateTwo.getTime());

			}else if(type.equals(DateComapareType.HOURS)){

				return  TimeUnit.MILLISECONDS.toHours(dateOne.getTime() - dateTwo.getTime());

			}else if(type.equals(DateComapareType.DAY)){

				return  TimeUnit.MILLISECONDS.toDays(dateOne.getTime() - dateTwo.getTime());

			}else if(type.equals(DateComapareType.WEEK)){

				long day = TimeUnit.MILLISECONDS.toDays(dateOne.getTime() - dateTwo.getTime());
				return day/7;

			}else{
				return (Long) null;
			}
	}

	/**
	 * To provide date difference between input date and current date.<br>
	 * input date will need to have format of &#34;yyyy-MM-ddTHH:mm:ssZ&#34;
	 * 
	 * 
	 * @param date
	 * 			date.
	 * 
	 * @return
	 * 			date string.
	 * 
	 * @throws ParseException Thrown when the string being parsed is not in the correct form. 
	 * 						  
	 */
	public static String formatDateInWeek(String date) throws ParseException {

		Date dateObject   = null;
		String dateString = "";
		String timeString = "";

		SimpleDateFormat timedateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat dateFormatter 	  = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat timeFormatter    = new SimpleDateFormat("hh:mm a");

		dateObject = timedateFormatter.parse(date);
		dateString = dateFormatter.format(dateObject);
		timeString = timeFormatter.format(dateObject);

		Calendar now 	  = Calendar.getInstance();
		Calendar postdate = Calendar.getInstance();

		now.getTime();
		postdate.setTime(dateObject);

		long milis1         = postdate.getTimeInMillis();
		long milis2         = now.getTimeInMillis();
		long difference     = milis2 - milis1;
		long weekdifference = difference / (7*24 * 60 * 60 * 1000);

		if(weekdifference != 0){
			return weekdifference + " Week Before";

		}else{

			if(now.get(Calendar.DATE) == postdate.get(Calendar.DATE)){
				return "Today " + dateString + " " + timeString+" ";

			}else if(now.get(Calendar.DATE)-postdate.get(Calendar.DATE) == 1){

				return "Yesterday " + dateString + " " + timeString+" ";

			}else{
				return "This Week " + dateString + " " + timeString+" ";
			}
		}
	}

	/**
	 * Check device type phone/table.
	 * Return true if device is tablet otherwise return false.
	 * 
	 * @param context
	 * 					application context.
	 * @return 
	 * 			true/false
	 *
	 * 
	 * @throws Exception 
	 */
	public static boolean isTablet(Context context) throws Exception { 

		DisplayMetrics dm  = context.getResources().getDisplayMetrics(); 
		float screenWidth  = dm.widthPixels / dm.xdpi; 
		float screenHeight = dm.heightPixels / dm.ydpi; 
		double size = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2)); 

		return size >= 6; 
	}

	/**
	 * To return pixel value.
	 * 
	 * @param context
	 * 				activity context.
	 * 
	 * @param value
	 * 				value.
	 * @return	
	 * 			pixel value.
	 * 
	 * @throws Exception 
	 */
	public static int convertToPixel(Context context, int value) throws Exception {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
		return (int)px;
	}

	/**
	 * To check SDcard present or not.<br>
	 * It will be use to check media is present and mounted at its mount point with read/write access. 
	 * Return true if media is present and mounted otherwise return false.
	 * 
	 * @return true/false
	 */
	public static boolean isSdPresent() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	} 

}

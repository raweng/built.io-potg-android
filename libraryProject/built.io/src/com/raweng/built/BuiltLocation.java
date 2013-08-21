package com.raweng.built;


import android.app.Activity;
import android.content.Context;

import com.raweng.built.utilities.BuiltAppConstants;

/**
 * This class is used to set location while creating new object or used for making geo point related queries.
 *  
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltLocation{

	private  Double latitude;
	private  Double longitude;


	/**
	 * Returns latitude value.
	 * 
	 * @return 
	 * 			latitude.
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Returns longitude value.
	 * 
	 * @return 
	 * 			longitude.
	 */
	public Double getLongitude() {
		return longitude;
	}


	/**
	 * Sets latitude and longitude value.
	 * 
	 * @param latitude
	 * 					latitude.
	 * 
	 * @param longitude
	 * 					longitude.
	 */
	public void setLocation(Double latitude, Double longitude) {

		if(latitude != null && longitude != null){
			this.latitude = latitude;
			this.longitude = longitude;
		}
	}

	/**
	 * To fetch current location.
	 * 
	 * @param context
	 * 					  application context.
	 * @param activity
	 *                    {@link Activity} instance.
	 * 
	 * @param callback   {@link BuiltLocationCallback} instance.
	 * 
	 * @throws Exception
	 * 
	 * <p>
	 * <b>Note :</b>
	 * User need to add following permission in manifest.<br>
	 * <li><a href="http://developer.android.com/reference/android/Manifest.permission.html#ACCESS_FINE_LOCATION"> To access precise location from location sources.</a></li>
	 * 
	 */
	public static void getCurrentLocation(Context context, Activity activity, BuiltLocationCallback callback) throws Exception{

		if(context == null){

			throwsException(callback, BuiltAppConstants.ErrorMessage_ApplicationContextIsNull);

		}else if(activity == null){
			throwsException(callback, BuiltAppConstants.ErrorMessage_ActivityObjectIsNull);

		}else if (callback == null){
			new Exception(BuiltAppConstants.ErrorMessage_callbackIsNull);

		}else{	
			
			CurrentLocation location = new CurrentLocation();
			location.getCurrentLocation(context, activity, callback);
		}
	}

	private static void throwsException(BuiltLocationCallback callback, String error) {
		if(callback != null){
			BuiltError builtError = new BuiltError();
			builtError.setErrorMessage(error);
			callback.onRequestFail(builtError);
		}
	}

}

package com.raweng.built.utilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class RawAppUtils{

	public RawAppUtils(){}



	/**
	 * To show built.io logs
	 * 
	 * @param tag
	 * 			class or activity from where the log call occurs.
	 * 
	 * @param error
	 * 				message.
	 */
	public static void showLog(String tag, String error) {
		if(BuiltAppConstants.debug){
			Log.i(tag, "built.io log:----|" + error);
		}
	}

	/**
	 * To retrieve session data. 
	 * 
	 * @param file 
	 * 				session file.
	 * @return	
	 * 				saved session info.
	 */				
	public JSONArray getSessionArrayFromSessionFile(File file) {

		JSONObject json 			 = null;
		InputStream input 			 = null;
		ByteArrayOutputStream buffer = null;
		try{
			input = new BufferedInputStream(new FileInputStream(file));
			if(input != null){
				buffer = new ByteArrayOutputStream();
				byte[] temp = new byte[1024];
				int read;
				while((read = input.read(temp)) > 0){
					buffer.write(temp, 0, read);
				}
				if(buffer.size() > 0){
					json = new JSONObject(buffer.toString("UTF-8"));
					if(json != null){
					}
				}
			}
			if(json.has("session")){
				buffer.flush();
				buffer.close();
				input.close();
				return json.optJSONArray("session");
			}else{
				buffer.flush();
				buffer.close();
				input.close();
				return null;
			}

		}catch (Exception e) {
			showLog("appUtils", "------------getJsonFromFilec catch-|" + e.toString());
			return null;
		}

	}



	/**
	 * To retrieve installation data. 
	 * 
	 * @param file 
	 * 				installation file.
	 * @return	
	 * 				saved installation  info.
	 */				
	public JSONArray getInstallationArrayFromInstallationFile(File file) {

		JSONObject json              = null;
		InputStream input            = null;
		ByteArrayOutputStream buffer = null;
		try{
			input = new BufferedInputStream(new FileInputStream(file));
			if(input != null){
				buffer = new ByteArrayOutputStream();
				byte[] temp = new byte[1024];
				int read;
				while((read = input.read(temp)) > 0){
					buffer.write(temp, 0, read);
				}
				if(buffer.size() > 0){
					json = new JSONObject(buffer.toString("UTF-8"));
					if(json != null){
					}
				}
			}
			if(json.has("installation")){
				buffer.flush();
				buffer.close();
				input.close();
				return json.optJSONArray("installation");
			}else{
				buffer.flush();
				buffer.close();
				input.close();
				return null;
			}
		}catch (Exception e) {
			showLog("appUtils", "------------getJsonFromFilec catch-|" + e.toString());
			return null;
		}
	}


	/**
	 * To retrieve data from cache.
	 * 
	 * @param file
	 * 				cache file.
	 * @return
	 * 				cache data in JSON. 
	 */
	public static JSONObject getJsonFromCacheFile(File file) {

		JSONObject json              = null;
		InputStream input            = null;
		ByteArrayOutputStream buffer = null;
		try{

			input = new BufferedInputStream(new FileInputStream(file));
			buffer = new ByteArrayOutputStream();
			byte[] temp = new byte[1024];
			int read;
			while((read = input.read(temp)) > 0){
				buffer.write(temp, 0, read);
			}
			json = new JSONObject(buffer.toString("UTF-8"));
			buffer.flush();
			buffer.close();
			input.close();
			return json;
		}catch (Exception e) {
			showLog("appUtils", "------------getJsonFromFilec catch-|" + e.toString());
			return null;
		}
	}

	/**
	 * To check if required response within given time window available in cache 
	 * 
	 * @param file
	 * 				cache file.
	 * @param time
	 * 				time 
	 * 
	 * @return
	 * 			true if cache data available which satisfy given time condition.
	 */
	public boolean getResponseTimeFromCacheFile(File file,long time) {
		try{
			JSONObject jsonObj = getJsonFromCacheFile(file);
			long responseDate =  Long.parseLong(jsonObj.optString("timestamp"));

			Calendar responseCalendar = Calendar.getInstance();

			responseCalendar.add(Calendar.MINUTE, 0);
			responseCalendar.set(Calendar.SECOND, 0);
			responseCalendar.set(Calendar.MILLISECOND, 0);
			responseCalendar.setTimeInMillis(responseDate);
			responseCalendar.getTimeInMillis();


			Calendar currentCalendar = Calendar.getInstance();
			currentCalendar.setTime(new Date());
			currentCalendar.getTimeInMillis();

			long dateDiff = (currentCalendar.getTimeInMillis() - responseCalendar.getTimeInMillis());
			long dateDiffInMin = dateDiff / (60 * 1000);       


			if(dateDiffInMin > (time / 60000)){
				return true;// need to send call.
			}else{
				return false;// no need to send call.
			}

		}catch(Exception e) {
			showLog("appUtils", "------------getJsonFromFilec catch-|" + e.toString());
			return false;
		}
	}

	/**
	 * To encrypt given value. 
	 * 
	 * @param value
	 * 				string
	 * 
	 * @return 
	 * 			MD5 value
	 */
	public String getMD5FromString(String value) {
		String output;
		output = value.toString().trim();
		if(value.length() > 0){
			try {
				// Create MD5 Hash
				MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
				digest.reset();
				digest.update(output.getBytes());
				byte messageDigest[] = digest.digest();

				// Create Hex String
				StringBuffer hexString = new StringBuffer();
				for (int i = 0; i < messageDigest.length; i++) {

					String hex = Integer.toHexString(0xFF & messageDigest[i]);
					if(hex.length() == 1){
						hexString.append('0');
					}
					hexString.append(hex);
				}

				return hexString.toString();

			} catch (Exception e) {
				showLog("appUtils", "------------getMD5FromString catch-|" + e.toString());
				return null;
			}
		}else{
			return null;
		}
	}


}

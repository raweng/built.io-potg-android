package com.raweng.built;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.RawAppUtils;

/**
 * Contains all Built API classes and functions.
 * 
 * @author  raw engineering, Inc
 * 
 */
public class Built {

	protected static HeaderGroup headerGroup ;
	protected static String applicationKey ;
	protected static String applicationUid ;



	/**
	 * Authenticates the api key and application uid of your application.
	 * <p>
	 * This must be called before your application uses built.io sdk.
	 * <br>
	 * You can find your application key and application uid from web.
	 *  
	 *  @param context
	 *  				Application context.
	 *  
	 * @param apiKey 
	 * 				 	Application api Key of your application on built.io.
	 * 
	 * @param appUid 
	 *             		Application uid of your application on built.io.
	 *             
	 * @throws Exception 
	 */
	public static void initializeWithApiKey(Context	context,String apiKey, String appUid) throws Exception{
		if(context != null){

			if(apiKey != null){

				if(appUid != null){

					applicationKey = apiKey;
					applicationUid = appUid;
					headerGroup    = new HeaderGroup();

					setHeader("application_uid", applicationUid);
					setHeader("application_api_key", applicationKey);

					isNetworkAvailable(context);

					//cache folder
					File queryCacheFile                    = context.getDir("BuiltCache", 0);
					BuiltAppConstants.cacheFolderName      = queryCacheFile.getPath();
					BuiltAppConstants.sessionFileName      = BuiltAppConstants.cacheFolderName + File.separator + "Session";
					BuiltAppConstants.installationFileName = BuiltAppConstants.cacheFolderName + File.separator + "Installation";


					// to create folder to save offline calls
					BuiltAppConstants.offlineCallsFolderName = context.getDir("OfflineCalls", 0).getPath();
					java.io.File offlineCallsFolder          = new java.io.File(BuiltAppConstants.offlineCallsFolderName);
					if(! offlineCallsFolder.exists()){
						offlineCallsFolder.createNewFile();
					}

					// to create session folder
					java.io.File sessionFile = new java.io.File(BuiltAppConstants.sessionFileName);

					if(! sessionFile.exists()){
						sessionFile.createNewFile();
					}

					java.io.File installationFile = new java.io.File(BuiltAppConstants.installationFileName);

					if(! installationFile.exists()){
						installationFile.createNewFile();
					}

					// to fetch authtoken from session file
					JSONArray array = new RawAppUtils().getSessionArrayFromSessionFile(new File(BuiltAppConstants.sessionFileName));

					if(array != null && array.length() > 0){
						int count = array.length();

						for(int i = 0; i < count; i++){

							if(array.get(i).toString().contains(applicationKey)){
								JSONObject valueJson           = new JSONObject();
								JSONObject appKeyJson          = new JSONObject();
								JSONObject applicationUserJobj = new JSONObject();

								valueJson           = (JSONObject) array.get(i);
								appKeyJson          = valueJson.optJSONObject(applicationKey);
								applicationUserJobj = appKeyJson.optJSONObject("application_user");
								String authToken    = applicationUserJobj.optString("authtoken");

								if(authToken != null){
									setHeader("authtoken", authToken);
								}
								break;
							}
						}
					}

					clearCache(context);
				}else{
					throw new Exception(BuiltAppConstants.ErrorMessage_ApplicationUidIsNull);
				}
			}else{
				throw new Exception(BuiltAppConstants.ErrorMessage_ApplicationApiKeyIsNull);
			}
		}else{
			throw new Exception(BuiltAppConstants.ErrorMessage_ApplicationContextIsNull);
		}

	}



	/**
	 * Sets the header for all built.io rest calls.
	 * 
	 * @param headerGroupObject
	 * 			           {@link HeaderGroup} object.
	 */
	public static void setHeader(HeaderGroup headerGroupObject){
		if(headerGroupObject != null){
			int count = headerGroupObject.getAllHeaders().length;
			if(headerGroup == null){
				headerGroup = new HeaderGroup();
			}
			for(int i = 0; i < count; i++){
				headerGroup.addHeader(headerGroupObject.getAllHeaders()[i]);
			}

		}
	}

	/**
	 * Removes a header for specific key.
	 *  
	 * @param key
	 * 			   removes the header against given header key.
	 */
	public static  void removeHeader(String key){
		if(headerGroup != null){
			if(headerGroup.containsHeader(key)){
				org.apache.http.Header header = headerGroup.getCondensedHeader(key);
				headerGroup.removeHeader(header);
			}
		}
	}

	/**
	 * Set the common headers for built.io rest calls .
	 * @param key 
	 * 				header name.
	 * @param value 	
	 * 				header value against given header name.
	 * 
	 */
	public static void setHeader(String key, String value){
		if(key != null && value != null){
			if(headerGroup == null){
				headerGroup = new HeaderGroup();
			}
			headerGroup.addHeader(new BasicHeader(key, value));
		}
	}

	/**
	 * Sets the URL and host name of the built.io server.
	 * 
	 * @param hostName 
	 * 					host name.
	 * 
	 * @param protocol  
	 * 					HTTP protocol for initiating calls (HTTP/HTTPS).
	 * 
	 * <p>
	 *  <b>Note:</b> Default hostname sets to <a href ="https://api.built.io"> api.built.io </a>
	 *   and default protocol is HTTPS.
	 */
	public static void setURL(String hostName, String protocol){
		BuiltAppConstants.URLSCHEMA = protocol + "://";
		BuiltAppConstants.URL = hostName;
	}

	/**
	 * Sets the URL of the built.io server.
	 * 
	 * @param hostName
	 *                 host name.
	 *  <p>
	 *  <b>Note:</b> Default hostname sets to <a href ="https://api.built.io"> api.built.io </a>
	 */
	public static void setURL(String hostName){
		BuiltAppConstants.URL = hostName;
	}

	/**
	 * Set tenant uid.
	 * 
	 * @param tenantUid
	 * 					tenant uid.
	 */
	public static void setTenant(String tenantUid) {
		setHeader("tenant_uid", tenantUid);
	}

	/**
	 * 
	 * To cancel all network calls.
	 */
	public static void cancelAllCalls() {
		try{
			BuiltAppConstants.cancelledCallController.add(callController.BUILTANALYTICS.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTAPPLICATION.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTDELTA.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTINSTALLATION.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTOBJECT.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTQUERY.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTROLE.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTROLROBJECT.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTUSER.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTFILE.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTNOTIFICATION.toString());
			BuiltAppConstants.cancelledCallController.add(callController.BUILTCLOUD.toString());
			BuiltAppConstants.cancelMediaFileUploadNetworkCalls = true;

			if(BuiltAppConstants.uploadAsyncInstanceList != null){
				int count = BuiltAppConstants.uploadAsyncInstanceList.size();
				if(count > 0){
					ArrayList<Object> asyncs = BuiltAppConstants.uploadAsyncInstanceList;

					for(int i = 0; i < count; i++){
						if(((UploadAsync)asyncs.get(i)).request != null){
							((UploadAsync)asyncs.get(i)).request.cancelCall();
						}
					}
					BuiltAppConstants.uploadAsyncInstanceList.clear();
				}
			}

			if(BuiltAppConstants.updateUploadAsyncInstanceList != null){
				int updateCount = BuiltAppConstants.updateUploadAsyncInstanceList.size();
				if(updateCount > 0){
					ArrayList<Object> asyncs = BuiltAppConstants.updateUploadAsyncInstanceList;

					for(int i = 0; i < updateCount; i++){
						if(((UploadAsync)asyncs.get(i)).request != null){
							((UploadAsync)asyncs.get(i)).request.cancelCall();
						}
					}
					BuiltAppConstants.updateUploadAsyncInstanceList.clear();
				}
			}
		}catch (Exception error) {
			RawAppUtils.showLog("Built", "cancelAllCalls--|" + error);
		}

	}


	/*****************************************************************************************************************************************************************************
	 * 
	 ************  Private Methods  *******************
	 * 
	 ****************************************************************************************/

	/**
	 * To check network availability. 
	 */
	private static void isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if(connectivityManager.getNetworkInfo(0) != null || connectivityManager.getNetworkInfo(1).getState() != null){

			if (connectivityManager.getActiveNetworkInfo() == null) {
				BuiltAppConstants.isNetworkAvailable = false;
			}else{
				BuiltAppConstants.isNetworkAvailable = true;
			}

		}else if(connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ){
			BuiltAppConstants.isNetworkAvailable = true;
		}else{
			BuiltAppConstants.isNetworkAvailable = false;
		}
	}

	/**
	 * To start schedule for clearing cache. 
	 * 
	 * @param context
	 * 					application context.
	 */
	private static void clearCache(Context context) {

		Intent alarmIntent = new Intent("StartClearingCache");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),AlarmManager.INTERVAL_DAY, pendingIntent);

	}
}

package com.raweng.built;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;

/**
 * To broadcast push notifications using built.io library.
 *   
 * @author raw engineering, Inc
 *
 */
public class BuiltNotification {

	private HeaderGroup headerGroup_local;
	private static String applicationKey_local;
	private String applicationUid_local;


	private Calendar timestamp;
	private boolean sentLocalPush = false;



	/**
	 * Create a new instance of {@link BuiltNotification} class.
	 * 
	 */
	public BuiltNotification(){
		headerGroup_local = new HeaderGroup();
	}

	/**
	 *  Sets the api key and Application uid for {@link BuiltNotification} instance.
	 *   <br>             
	 * Scope is limited to this object only.
	 *  @param apiKey 
	 * 				Application api Key of your application on built.io.
	 * 
	 * @param appUid 
	 *             Application uid of your application on built.io.
	 */
	public void setApplication(String apiKey, String appUid) {
		applicationKey_local = apiKey;
		applicationUid_local = appUid;

		setHeader("application_uid", applicationUid_local);
		setHeader("application_api_key", applicationKey_local);
	}


	/**
	 * To set headers for built.io rest calls.
	 * <br>
	 * Scope is limited to this object only. 
	 * @param key 
	 * 				header name.
	 * @param value 	
	 * 				header value against given header name.
	 * 
	 */
	public void setHeader(String key, String value){

		if(key != null && value != null){
			headerGroup_local.addHeader(new BasicHeader(key,value));
		}
	}

	/**
	 * Remove a header for a given key from headers.
	 * <br>
	 * Scope is limited to this object only.
	 *  
	 * @param key
	 * 			   header key.
	 *  
	 */
	public void removeHeader(String key){
		if(headerGroup_local.containsHeader(key)){
			org.apache.http.Header header =  headerGroup_local.getCondensedHeader(key);
			headerGroup_local.removeHeader(header);
		}
	}


	/**
	 * To send push notification.
	 * 
	 * @param message
	 * 					message which will be sent with the notification.
	 * 
	 * @param userUid 
	 * 					 Specify an array of user&#39;s uids to send notifications to.
	 * 
	 * @param callback
	 * 					  {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * <p>
	 * <b> Note: </b> You can add following filter with the notification.
	 * <li> You can set timestamp on which you want built.io to send the notification. You can set timestamp using {@link #setTimestamp(Calendar)}.</li>
	 * <li> To send the notification at users&#39; timezone. Use {@link #SentLocalPush()}.</li>
	 */
	public void sendNotification(String message, String[] userUid, BuiltResultCallBack callback) {

		try{
			if(message != null){ 

				if(userUid != null && userUid.length > 0){

					JSONObject mainJson = new JSONObject();
					JSONObject notificationValueJson = new JSONObject();

					notificationValueJson.put("message", message);

					JSONArray useruidArray = new JSONArray();
					int count = userUid.length;

					for(int i = 0; i < count; i++){
						useruidArray.put(userUid[i]);
					}

					notificationValueJson.put("user_uids", useruidArray);

					if(timestamp != null){
						Calendar calObject = new GregorianCalendar(timestamp.getTimeZone());
						calObject.setTimeInMillis((timestamp).getTimeInMillis() +

								(timestamp).getTimeZone().getOffset((timestamp).getTimeInMillis()) -

								TimeZone.getDefault().getOffset((timestamp).getTimeInMillis()));
						calObject.setTimeZone(TimeZone.getTimeZone("GMT"));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
						String utcTime = sdf.format(calObject.getTime());

						notificationValueJson.put("send_at", utcTime);
					}

					if(sentLocalPush){
						notificationValueJson.put("local_push", sentLocalPush);
					}

					mainJson.put("push", notificationValueJson);

					String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/push";

					new BuiltCallBackgroundTask(this, BuiltControllers.SENDBROADCASTNOTIFICATION, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTNOTIFICATION.toString(), callback);

				}else{
					throwExeception(callback, BuiltAppConstants.ErrorMessage_NotificationUserUidIsNull);
				}
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_NotificationMessageIsNull);
			}
		} catch(Exception e) {
			throwExeception(callback, e.toString());
		}

	}

	/**
	 * {@link Calendar} instance to set the notification sent time. If not specified, the notification will be sent immediately.
	 * 
	 * @param calendar
	 * 					{@link Calendar} instance.
	 * 			
	 */
	public void setTimestamp(Calendar calendar) {
		timestamp = calendar;
	}


	/**
	 * Whether to send the notification local to the users&#39; timezones. Useful only if {@link #setTimestamp(Calendar)} is set.
	 */
	public void SentLocalPush() {
		sentLocalPush = true;
	}



	/**
	 * 
	 * **********************************************
	 */

	private void throwExeception(ResultCallBack callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.errorMessage(errorMessage);
		if(callback != null){
			callback.onRequestFail(error);
		}
	}

	private HeaderGroup getHeaders(HeaderGroup localHeaders){
		HeaderGroup mainHeaderGroup = new HeaderGroup();
		HeaderGroup endHeaderGroup  = new HeaderGroup();

		mainHeaderGroup = Built.headerGroup;

		if(localHeaders != null){
			if(mainHeaderGroup != null && mainHeaderGroup.getAllHeaders().length > 0){
				int countMainHeader = mainHeaderGroup.getAllHeaders().length;
				for(int i = 0; i < countMainHeader; i++){

					if(mainHeaderGroup.getAllHeaders()[i].getName().equalsIgnoreCase("authtoken")){
						if(localHeaders.containsHeader("application_uid") && localHeaders.containsHeader("application_api_key")){

						}else{
							endHeaderGroup.addHeader(mainHeaderGroup.getAllHeaders()[i]);
						}
					}else{
						endHeaderGroup.addHeader(mainHeaderGroup.getAllHeaders()[i]);
					}
				}
			}

			int countLocalHeader = localHeaders.getAllHeaders().length;
			for(int i = 0; i < countLocalHeader; i++){

				if(endHeaderGroup.containsHeader(localHeaders.getAllHeaders()[i].getName())){

					org.apache.http.Header header = headerGroup_local.getCondensedHeader(localHeaders.getAllHeaders()[i].getName());
					endHeaderGroup.removeHeader(header);
				}

				endHeaderGroup.addHeader(localHeaders.getAllHeaders()[i]);
			}
			return endHeaderGroup;
		}else{
			return mainHeaderGroup;
		}
	}

}

package com.raweng.built;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.BuiltSharedPrefrence;
import com.raweng.built.utilities.RawAppUtils;

/**
 * The {@link BuiltAnalytics} class provides an interface to built.io&#39;s analytics backend.
 * 
 * @author  raw engineering, Inc
 *
 */
public class BuiltAnalytics {

	private static BuiltAnalytics BuiltAnalyitcsObjects 	= null;
	private JSONObject superPropertiesJson 					= null;
	private Context context 								= null;
	private boolean isMuiltTriggerStart						= false;


	private long flushInterval = 60000;//60*1000


	private HeaderGroup headerGroup_local;
	private String applicationKey_local;
	private String applicationUid_local;


	protected BuiltAnalytics(Context context){
		this.context = context;
		BuiltAnalyitcsObjects  = this;
		superPropertiesJson    = new JSONObject();
		headerGroup_local      = new HeaderGroup();

		if(! isMuiltTriggerStart){
			isMuiltTriggerStart = true;
			StartThread();
		}

	}


	/**
	 *  Sets the api key and Application uid for {@link BuiltAnalytics} class instance.
	 *   <br>             
	 * Scope is limited to this object only.
	 *  @param apiKey 
	 * 				Application api Key of your application on built.io.
	 * 
	 * @param appUid 
	 *             Application uid of your application on built.io.
	 *  
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
			removeHeader(key);
			headerGroup_local.addHeader(new BasicHeader(key, value));
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
		if(headerGroup_local != null){
			if(headerGroup_local.containsHeader(key)){
				org.apache.http.Header header =  headerGroup_local.getCondensedHeader(key);
				headerGroup_local.removeHeader(header);
			}
		}
	}

	/**
	 * Create new instance of {@link BuiltAnalytics} or return previously instantiated singleton instance of {@link BuiltAnalytics}.
	 * 
	 * @param context
	 * 					application context.
	 * @return 
	 * 			{@link BuiltAnalytics} object.
	 * 
	 * @throws Exception 
	 */
	public static BuiltAnalytics sharedInstance(Context context) throws Exception{
		if(BuiltAnalyitcsObjects != null){
			return BuiltAnalyitcsObjects;
		}else{
			if(context != null){
				return new BuiltAnalytics(context.getApplicationContext());
			}else{
				throw new Exception(BuiltAppConstants.ErrorMessage_ApplicationContextIsNull);
			}
		}
	}

	/**
	 * Set Global Properties for all Events.
	 * Super properties, once registered automatically sent as properties for all event tracking calls.
	 * 
	 * @param superProperties   &nbsp;
	 * 						 {@linkplain HashMap} object contains property name as key and its respective value as value.   
	 * 							
	 */
	public void superProperties(HashMap<String, Object> superProperties) {
		try{
			for(Entry<String, java.lang.Object> entry : superProperties.entrySet()){
				superPropertiesJson.put(entry.getKey(), entry.getValue());
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltAnalytics", "---------------registerSuperProperties-catch|" + e);
		}
	}

	/**
	 * Set batch event execution interval time. 
	 * 
	 * @param interval
	 * 					flush interval time in second.
	 * <p>
	 * <b> Note:- </b> Default batch execution interval is set to 1 minute. 
	 */
	public void setFlushInterval(int interval) {
		try{
			flushInterval = interval * 1000;
			if(context != null){
				BuiltSharedPrefrence.setFlushInterval(context, flushInterval);
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltAnalytics", "---------------setInterval-catch|" + e);
		}
	}


	/**
	 *Request to cancel all analytics network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTANALYTICS.toString());
	}

	/**
	 * Request to track an event.
	 * 
	 * @param builtEventInstance
	 * 							 {@link BuiltEvent} instance containing event info.
	 * 		
	 */
	public void trigger(BuiltEvent builtEventInstance) {
		try {
			if(builtEventInstance != null){

				JSONObject valueJson      = new JSONObject();
				JSONObject propertiesJson = new JSONObject();


				if(builtEventInstance.eventUid != null){
					if((builtEventInstance.properties != null) && (builtEventInstance.properties.size() > 0)){

						for(Entry<String, Object> entry : builtEventInstance.properties.entrySet()){
							propertiesJson.put(entry.getKey(), entry.getValue());
						}
					}

					if((superPropertiesJson != null) && (superPropertiesJson.length() > 0)){

						JSONArray array = superPropertiesJson.names();

						int propertyCount = array.length();
						for(int j = 0; j < propertyCount; j++){
							String key  = array.getString(j);
							propertiesJson.put(key, superPropertiesJson.get(key));
						}
					}

					valueJson.put("properties", propertiesJson);

					if(builtEventInstance.previousEventUid != null){
						valueJson.put("previous_event_uid", builtEventInstance.previousEventUid);
					}

					JSONObject headerJson = new JSONObject();
					Header [] header = getHeaders(headerGroup_local).getAllHeaders();
					if(header != null && header.length > 0){

						int count = header.length;
						for(int i = 0; i < count; i++){
							headerJson.put(header[i].getName(), header[i].getValue());
						}
					}


					AnalyticsAdapter analyticsAdapter = new AnalyticsAdapter(context);
					analyticsAdapter.addJSON(builtEventInstance.eventUid, valueJson, headerJson);

				}
			}

		} catch (Exception error) {
			RawAppUtils.showLog("BuiltAnalytics", "---------------triggerEvent-catch|" + error);
		}

	}


	/*************************************************************************************************
	 * 
	 ************  Private Methods  *******************
	 * 
	 ****************************************************************************************/



	private void StartThread() {
		if(context != null){
			flushInterval = BuiltSharedPrefrence.getFlushInterval(context);
		}
		Timer timer = new Timer(); 
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				new CallTimerThread().run();
			}
		}, 0, flushInterval);
	}


	private class CallTimerThread extends TimerTask {

		@Override
		public void run() {
			sendtriggerEventRequest();
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


	/**
	 * To executes batch calls.
	 */
	private void sendtriggerEventRequest() {

		RawAppUtils.showLog("BuiltAnalytics", "---------------sendtriggerEventRequest-called|");
		if(BuiltAppConstants.isNetworkAvailable){
			AnalyticsAdapter analyticsAdapter = new AnalyticsAdapter(context);
			JSONObject eventJson = analyticsAdapter.getJsonObject();

			if(eventJson != null && (eventJson.length() > 0)){

				try{
					JSONObject mainJson = new JSONObject();

					mainJson.put("events", eventJson);

					String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/events/trigger_multiple";

					Header [] header = getHeaders(headerGroup_local).getAllHeaders();
					if(header != null && header.length > 0){
						new BuiltCallBackgroundTask(this, BuiltControllers.TRIGGEREVENT, URL, header, mainJson, null, callController.BUILTANALYTICS.toString());
					}else{

						BuiltError error = new BuiltError();
						error.setErrorMessage(BuiltAppConstants.ErrorMessage_CalledBuiltDefaultMethod);

					}
					eventJson = new JSONObject();
					analyticsAdapter.cleanTable();
				}catch (Exception e) {
					RawAppUtils.showLog("BuiltAnalytics", "---------------triggerEvent-catch|" + e);
				}
			}
		}
	}

}
package com.raweng.built;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.RawAppUtils;

/**
 * BuiltDelta fetch objects that are modified (created/updated/deleted) at/on specified date.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltDelta implements INotifyClass {


	private HeaderGroup headerGroup_local;
	private String applicationKey_local;
	private String applicationUid_local;
	private String classUid;

	JSONObject mainJson;
	JSONObject valueJson;

	BuiltDeltaResultCallback builtDeltaResultCallback;

	/**
	 * {@link BuiltDelta} instance.
	 * 
	 * @param classUid
	 * 					Class uid of which delta object needs to be fetched.
	 */
	public BuiltDelta(String classUid){
		headerGroup_local  = new HeaderGroup();
		this.classUid      = classUid;
		mainJson           = new JSONObject();
		valueJson          = new JSONObject();
	}

	/**
	 *  Sets the api key and application uid for BuiltDelta instance.
	 *  <br>
	 *  Scope is limited to this object only. 
	 *  
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
		setHeader("application_api_key", applicationKey_local );
	}

	/**
	 * To set headers for built.io rest calls.
	 * 
	 * <br>
	 * Scope is limited to this object only.
	 *  
	 * @param key 
	 * 				header name.
	 * @param value 	
	 * 				header value against given header name.
	 * 
	 */
	public void setHeader(String key, String value){

		if(key != null && value != null){
			headerGroup_local.addHeader(new BasicHeader(key, value));
		}
	}


	/**
	 * Remove a header for a given key from headers..
	 *  <br>
	 * Scope is limited to this object only.
	 * 
	 * @param key
	 * 			  header key for which to remove the header value.
	 * 
	 */
	public  void removeHeader(String key){
		if(headerGroup_local.containsHeader(key)){
			org.apache.http.Header header =  headerGroup_local.getCondensedHeader(key);
			headerGroup_local.removeHeader(header);
		}
	}


	/**
	 * Get delta objects created on and after certain time given by user.
	 * 
	 * @param calendarObject
	 * 						Calendar object condition on which delta to be applied.
	 * 
	 * @return 	
	 * 			Returns {@link BuiltDelta} object, so you can chain this call.  
	 * 
	 */
	public BuiltDelta createdAt(Calendar calendarObject){
		try{
			if(calendarObject != null){

				Calendar value = new GregorianCalendar(calendarObject.getTimeZone());
				value.setTimeInMillis(calendarObject.getTimeInMillis() + 
						calendarObject.getTimeZone().getOffset(calendarObject.getTimeInMillis()) - 
						TimeZone.getDefault().getOffset(calendarObject.getTimeInMillis()));
				value.setTimeZone(TimeZone.getTimeZone("GMT"));

				SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				simpleDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				String utcTime = simpleDateFormatter.format(value.getTime());

				valueJson.put("created_at", utcTime);
				RawAppUtils.showLog("BuiltDelta", "--------------createdAt-utcTime|" + utcTime);
			}

		}catch(Exception e) {
			RawAppUtils.showLog("BuiltDelta", "-----------------createdAt|" + e);
		}
		return this;
	}


	/**
	 * Get delta objects updated on and after certain time given by user.
	 * 
	 * @param calendarObject
	 * 						Calendar object condition on which delta to be applied.
	 * 
	 * @return 	
	 * 			Returns {@link BuiltDelta} object, so you can chain this call.  
	 * 
	 */
	public BuiltDelta updatedAt(Calendar calendarObject) {
		try{
			if(calendarObject != null){

				Calendar value = new GregorianCalendar(calendarObject.getTimeZone());
				value.setTimeInMillis(calendarObject.getTimeInMillis() + 
						calendarObject.getTimeZone().getOffset(calendarObject.getTimeInMillis()) - 
						TimeZone.getDefault().getOffset(calendarObject.getTimeInMillis()));
				value.setTimeZone(TimeZone.getTimeZone("GMT"));

				SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				simpleDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				String utcTime = simpleDateFormatter.format(value.getTime());

				valueJson.put("updated_at", utcTime);
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltDelta", "-----------------updatedAt|" + e);
		}
		return this;
	}

	/**
	 * Get delta objects deleted on and after certain time given by user.
	 * 
	 * @param calendarObject
	 * 						Calendar object condition on which delta to be applied.
	 * 
	 * @return 	
	 * 			Returns {@link BuiltDelta} object, so you can chain this call.  
	 * 
	 */
	public BuiltDelta deletedAt(Calendar calendarObject){
		try{
			if(calendarObject != null){
				Calendar value = new GregorianCalendar(calendarObject.getTimeZone());
				value.setTimeInMillis(calendarObject.getTimeInMillis() + 
						calendarObject.getTimeZone().getOffset(calendarObject.getTimeInMillis()) - 
						TimeZone.getDefault().getOffset(calendarObject.getTimeInMillis()));
				value.setTimeZone(TimeZone.getTimeZone("GMT"));

				SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				simpleDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				String utcTime = simpleDateFormatter.format(value.getTime());

				valueJson.put("deleted_at", utcTime);
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltDelta", "-----------------deletedAt|" + e);
		}
		return this;
	}

	/**
	 * Get all delta objects updated, deleted, created on and after certain time given by user.
	 * 
	 * @param calendarObject
	 * 						Calendar object condition on which delta to be applied.
	 * 
	 * @return 	
	 * 			Returns {@link BuiltDelta} object, so you can chain this call.  
	 * 
	 */
	public BuiltDelta allDeltaAt(Calendar calendarObject){
		try{
			if(calendarObject != null){
				Calendar value = new GregorianCalendar(calendarObject.getTimeZone());
				value.setTimeInMillis(calendarObject.getTimeInMillis() + 
						calendarObject.getTimeZone().getOffset(calendarObject.getTimeInMillis()) - 
						TimeZone.getDefault().getOffset(calendarObject.getTimeInMillis()));
				value.setTimeZone(TimeZone.getTimeZone("GMT"));

				SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				simpleDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				String utcTime = simpleDateFormatter.format(value.getTime());

				valueJson.put("ALL", utcTime);
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltDelta", "-----------------allDeltaAt|" + e);
		}
		return this;
	}

	/**
	 * Execute Delta object query to get delta objects.
	 * 
	 * @param callback 
	 * 					 {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 			Returns {@link BuiltDelta} object, so you can chain this call.  
	 */
	public BuiltDelta exec(BuiltDeltaResultCallback callback){
		try{
			if(classUid != null){

				if(valueJson != null && valueJson.length() > 0){

					mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());
					mainJson.put("delta", valueJson);

					String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/" + classUid + "/objects";

					builtDeltaResultCallback = callback;

					new BuiltCallBackgroundTask(this, BuiltControllers.BUILTDELTA, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTDELTA.toString(), callback); 

				}else{
					throwExeception(callback, BuiltAppConstants.ErrorMessage_CalendarObjectIsNull);
				}
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_ClassUID);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
		return this;
	}

	/**
	 * To cancel all {@link BuiltDelta}  network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTDELTA.toString());
	}


	/*****************************************************************************************************************************************************************************
	 * 
	 *
	 * 
	 ****************************************************************************************/




	@Override
	public void getResult(java.lang.Object obj) {}


	@Override
	public void getResultObject(List<java.lang.Object> obj, JSONObject jsonobject) {
		DeltaResult result = new DeltaResult(jsonobject);
		if(builtDeltaResultCallback != null){
			builtDeltaResultCallback.onRequestFinish(result);
		}
	}


	private void throwExeception(BuiltDeltaResultCallback callback, String errorMessage) {
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
				int count = mainHeaderGroup.getAllHeaders().length;

				for(int i = 0; i < count; i++){

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



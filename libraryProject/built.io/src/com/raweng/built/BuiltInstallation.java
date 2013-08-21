package com.raweng.built;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.TimeZone;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.RawAppUtils;

/**
 * BuiltInstallation class for creating installation and subscribe/unsubscribe for push notifications against a channel.
 * 
 * @author  raw engineering, Inc

 *
 */
public class BuiltInstallation {

	protected String deviceType = null;
	protected String deviceToken = null;
	protected ArrayList<String> subscribedChannelList = null; 
	protected JSONObject json = null;


	private HeaderGroup headerGroup_local;
	private static String applicationKey_local;
	private String applicationUid_local;
	private JSONObject mainJson; 
	private JSONObject valueJson; 
	private JSONArray channelJsonArray;
	private JSONArray geoLocationArray	= null;
	private String timeZone = null;


	/**
	 * Creates new {@link BuiltInstallation} instance to update/delete installations or subscribe/unsubscribe to installation channels.
	 */
	public BuiltInstallation(){
		headerGroup_local 	  = new HeaderGroup();
		subscribedChannelList = new ArrayList<String>();
	}

	/**
	 *  Sets the api key and Application uid for {@link BuiltInstallation} instance.
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
			removeHeader(key);
			headerGroup_local.addHeader(new BasicHeader(key,value));
		}
	}

	/**
	 *  Remove a header for a given key from headers.
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
	 * Create a new installation.
	 * 
	 * 
	 * Request to create installation data.
	 * 
	 * @param deviceToken
	 * 					Registration token which user received from google server for GCM.					
	 * 
	 * @param installationChannels
	 * 				channels subscribe to. Channels can have the values below:
	 * 
	 *<pre>
	 *<i>//To notify when new class in an application with api key &#60;application_api_key&#62; is created created.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;create </b> 
	 *
	 *<i>//To notify when new object with class uid &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is created.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;create </b> 
	 *
	 *<i>//To notify when class with given &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;update </b> 
	 *
	 *<i>//To notify when object with given &#60;object_uid&#62; of &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;&#60;object_uid&#62;&#46;update </b> 
	 *
	 *<i>//To notify when class with given &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;delete </b> 
	 *
	 *<i>//To notify when object with given &#60;object_uid&#62; of &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;&#60;object_uid&#62;&#46;delete </b> 
	 *
	 *<i>//To notify when any class in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;update </b> 
	 *
	 *<i>//To notify when any class in an application with api key &#60;application_api_key&#62;is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;delete </b> 
	 *
	 *<i>//if any object with class uid  &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;update </b> 
	 *
	 *<i>//if any object with class uid  &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62;  is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;delete </b> 
	 *
	 * You can add custom channels as well.
	 * @param callback
	 * 						{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * </pre>
	 * 
	 */
	public void createInstallation(String deviceToken, ArrayList<String> installationChannels, BuiltResultCallBack callback){

		createInstallation(deviceToken, installationChannels, false, callback);
	}


	/**
	 * Delete an installation.
	 * <br>
	 * Using this resource you can delete the installation data.
	 * 

	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 */
	public void destroy(BuiltResultCallBack callback){

		String installationUid = getInstallationUid();

		if(installationUid != null){
			try{
				mainJson = new JSONObject();
				mainJson.put("_method", BuiltAppConstants.RequestMethod.DELETE.toString());

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_installation_data/objects/" + installationUid;

				new BuiltCallBackgroundTask(this, BuiltControllers.DELETEINSTALLATION, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTINSTALLATION.toString(), callback);

			} catch(Exception e) {
				throwExeception(callback, e.toString());
			}
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_InstallationUidIsNull);
		}
	}

	/**
	 * Update the current installation with the logged in user.
	 * 
	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 */
	public void updateInstallation(BuiltResultCallBack callback){

		String installationUid = getInstallationUid();
		if(installationUid != null){
			try{
				mainJson  = new JSONObject();
				valueJson = new JSONObject();


				if(geoLocationArray != null && geoLocationArray.length() > 0){
					valueJson.put("__loc", geoLocationArray);
				}

				if(timeZone != null){
					valueJson.put("timezone", timeZone);
					timeZone = null;
				}

				mainJson.put("object", valueJson);
				mainJson.put("_method", BuiltAppConstants.RequestMethod.PUT.toString());
				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_installation_data/objects/" + installationUid;

				new BuiltCallBackgroundTask(this,BuiltControllers.UPDATEINSTALLATION, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTINSTALLATION.toString(), callback);

			}catch (Exception e){
				throwExeception(callback, e.toString());
			}
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_InstallationUidIsNull);
		}
	}


	/**
	 * Subscribe an installation to more channels.
	 * <br>
	 * Using this resource you can add more channels to your subscription list.
	 * 
	 * @param installationChannels
	 * 						Array of channel we wish to subscribe to.
	 * 
	 * 
	 * channels subscribe to. Channels can have the values below:
	 * 
	 * <pre class="prettyprint">
	 * 
	 * <i>//To notify when new class in an application with api key &#60;application_api_key&#62; is created.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;create </b> 
	 *
	 *<i>//To notify when new object with class uid &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is created.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;create </b> 
	 *
	 *<i>//To notify when class with given &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46update </b> 
	 *
	 *<i>//To notify when object with given &#60;object_uid&#62; of &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;&#60;object_uid&#62;&#46;update </b> 
	 *
	 *<i>//To notify when class with given &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46delete </b> 
	 *
	 *<i>//To notify when object with given &#60;object_uid&#62; of &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;&#60;object_uid&#62;&#46;delete </b> 
	 *
	 *<i>//To notify when any class in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;update </b> 
	 *
	 *<i>//To notify when any class in an application with api key &#60;application_api_key&#62;is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;delete </b> 
	 *
	 *<i>//if any object with class uid  &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;update </b> 
	 *
	 *<i>//if any object with class uid  &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62;  is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;delete </b> 
	 *
	 *
	 * You can add custom channels as well.
	 *</pre> 
	 * @param callback
	 * 						{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 */
	public void subscribeToChannels(ArrayList<String> installationChannels, BuiltResultCallBack callback){

		String installationUid = getInstallationUid();

		if(installationUid != null){
			subscribeInstallationData(installationUid, installationChannels, false, callback);
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_InstallationUidIsNull);
		}
	}



	/**
	 * Unsubscribe an installation from channels.
	 * <br>
	 * Using this resource you can unsubscribe from certain channels.
	 * 
	 * 
	 * @param installationChannels
	 * 							 Array of channel we wish to unsubscribe to.
	 * 
	 * <pre class="prettyprint">
	 * channels subscribe to. Channels can have the values below:
	 *
	 *<i>//To notify when new class in an application with api key &#60;application_api_key&#62; is created.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;create </b> 
	 *
	 *<i>//To notify when new object with class uid &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is created.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;create </b> 
	 *
	 *<i>//To notify when class with given &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46update </b> 
	 *
	 *<i>//To notify when object with given &#60;object_uid&#62; of &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;&#60;object_uid&#62;&#46;update </b> 
	 *
	 *<i>//To notify when class with given &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46delete </b> 
	 *
	 *<i>//To notify when object with given &#60;object_uid&#62; of &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;&#60;object_uid&#62;&#46;delete </b> 
	 *
	 *<i>//To notify when any class in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;update </b> 
	 *
	 *<i>//To notify when any class in an application with api key &#60;application_api_key&#62;is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;class&#46;delete </b> 
	 *
	 *<i>//if any object with class uid &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is updated.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;update </b> 
	 *
	 *<i>//if any object with class uid &#60;class_uid&#62; in an application with api key &#60;application_api_key&#62; is deleted.</i>
	 *<b> &#60;application_api_key&#62;&#46;&#60;class_uid&#62;&#46;object&#46;delete </b> 
	 *
	 * You can add custom channels as well.
	 *</pre> 
	 * 
	 * @param callback
	 * 				   {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 */
	public void unsubscribeToChannels(ArrayList<String> installationChannels, BuiltResultCallBack callback){

		String installationUid = getInstallationUid();

		if(installationUid != null){
			unsubscribeInstallationData(installationUid, installationChannels, false, callback);
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_InstallationUidIsNull);
		}
	}



	/**
	 * To get installations details of the installation.
	 * 
	 * <p>
	 * <b> Note: </b> You can access application installations data by accessing {@link #getDeviceType()}, {@link #getDeviceToken()}, {@link #getSubscribedChannelList()}
	 * 
	 */
	public static BuiltInstallation currentInstallation(){
		try {
			String applicationKey  = applicationKey_local;
			if(applicationKey == null){
				applicationKey = Built.applicationKey;
			}

			JSONArray array = new RawAppUtils().getInstallationArrayFromInstallationFile(new File(BuiltAppConstants.installationFileName));

			if(array != null && array.length() > 0){
				int count = array.length();

				for(int i = 0; i < count; i++){

					if(array.get(i).toString().contains(applicationKey)){
						JSONObject valueJson = new JSONObject();

						valueJson             = (JSONObject) array.get(i);

						JSONObject appKeyJson = valueJson.optJSONObject(applicationKey);

						BuiltInstallation builtInstallationInstance  = new BuiltInstallation();
						builtInstallationInstance.deviceType         = appKeyJson.optString("device_type");
						builtInstallationInstance.deviceToken        = appKeyJson.optString("device_token");

						JSONArray channels = appKeyJson.optJSONArray("subscribed_to_channels");
						int channelCount = channels.length();
						for(int j = 0; j < channelCount; j++){
							builtInstallationInstance.subscribedChannelList.add(channels.optString(j));
						}

						return builtInstallationInstance;
					}
				}
			}
			return null;
		} catch (Exception error) {
			RawAppUtils.showLog("BuiltInstallation", "---------------currentInstallation-catch|" + error);
		}
		return null;

	}

	/**
	 * To set geo location for this object.
	 * 
	 * @param location
	 *               {@link BuiltLocation} instance. 
	 * 
	 * @return
	 * 			{@link BuiltInstallation} object, so you can chain this call.
	 */
	public BuiltInstallation setLocation(BuiltLocation location) {

		if(location != null && location.getLatitude() != null && location.getLongitude() != null){

			try{
				geoLocationArray = new JSONArray();
				geoLocationArray.put(location.getLongitude());
				geoLocationArray.put(location.getLatitude());
			}catch (Exception e) {
				RawAppUtils.showLog("BuiltObject", "-----------------setLocation|"+e);
			}
		}

		return this;

	}

	/**
	 * To set timezone for this installation
	 * 
	 *  @return
	 * 			{@link BuiltInstallation} object, so you can chain this call.
	 */
	public BuiltInstallation setTimeZone() {

		TimeZone timeZone = TimeZone.getDefault();
		String timeZoneValue = timeZone.getDisplayName(false, TimeZone.SHORT);
		this.timeZone = timeZoneValue.substring(3);
		return this;
	}

	/**
	 * Returns device type which can be either android or ios. 
	 * 
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * Returns device unique token.
	 * 
	 */
	public String getDeviceToken() {

		return deviceToken;
	}

	/**
	 * Returns subscribed channel list. 
	 * 
	 */
	public ArrayList<String> getSubscribedChannelList() {
		return subscribedChannelList;
	}

	/**
	 * To cancel all {@link BuiltInstallation} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTINSTALLATION.toString());
	}

	/**
	 * 
	 * Returns JSON representation of Object data.
	 * 
	 */
	public JSONObject toJSON(){
		return json ;
	}


	/*****************************************************************************************************************************************************************************
	 * 
	 * 
	 * 
	 ****************************************************************************************/

	private void createInstallation(String deviceToken, ArrayList<String> installationChannels, boolean isOfflineCall, BuiltResultCallBack callback){


		if(deviceToken != null){
			try{
				mainJson         = new JSONObject();
				valueJson        = new JSONObject();
				channelJsonArray = new JSONArray();

				JSONObject upsertValueJson = new JSONObject();


				valueJson.put("device_type", "android");
				valueJson.put("device_token", deviceToken.toString().trim());

				if(geoLocationArray != null && geoLocationArray.length() > 0){
					valueJson.put("__loc", geoLocationArray);
				}

				upsertValueJson.put("device_token", deviceToken.toString().trim());

				if(installationChannels != null){
					int count = installationChannels.size();
					for(int i = 0; i < count; i++){
						channelJsonArray.put(installationChannels.get(i));
					}

					valueJson.put("subscribed_to_channels", channelJsonArray);
				}


				mainJson.put("UPSERT", upsertValueJson);

				if(timeZone != null){
					valueJson.put("timezone", timeZone);
					timeZone = null;
				}

				mainJson.put("object", valueJson);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_installation_data/objects";

				new BuiltCallBackgroundTask(this,BuiltControllers.CREATEINSTALLATION, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTINSTALLATION.toString(), callback);

			}catch (Exception e) {
				throwExeception(callback, e.toString());
			}
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_RegTokenIsNull);
		}
	}


	private void subscribeInstallationData(String installationUid, ArrayList<String> installationChannels, boolean isOfflineCall, BuiltResultCallBack callback){

		if(installationUid != null){

			try{
				mainJson         = new JSONObject();
				channelJsonArray = new JSONArray();

				JSONObject pushJson           = new JSONObject();
				JSONObject pushValueJson      = new JSONObject();
				JSONObject subscribeToChannel = new JSONObject();



				if(installationChannels != null){
					int count = installationChannels.size();
					for(int i = 0; i < count; i++){
						channelJsonArray.put(installationChannels.get(i));
					}

					pushValueJson.put("data", channelJsonArray);
				}

				pushValueJson.put("index", 1);
				pushJson.put("PUSH", pushValueJson);
				subscribeToChannel.put("subscribed_to_channels", pushJson);

				mainJson.put("_method",  BuiltAppConstants.RequestMethod.PUT.toString());
				mainJson.put("object", subscribeToChannel);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_installation_data/objects/" + installationUid;

				new BuiltCallBackgroundTask(this, BuiltControllers.SUBSCRIBEINSTALLATIONDATA, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTINSTALLATION.toString(), callback);

			}catch(Exception e) {
				throwExeception(callback, e.toString());
			}
		}else{
			throwExeception(callback,BuiltAppConstants.ErrorMessage_InstallationUidIsNull);
		}
	}



	private void unsubscribeInstallationData(String installationUid, ArrayList<String> installationChannels, boolean isOfflineCall, BuiltResultCallBack callback){

		if(installationUid != null){

			try{
				mainJson         = new JSONObject();
				channelJsonArray = new JSONArray();

				JSONObject pullJson           = new JSONObject();
				JSONObject pullValueJson      = new JSONObject();
				JSONObject subscribeToChannel = new JSONObject();


				if(installationChannels != null){
					int count = installationChannels.size();
					for(int i = 0; i < count; i++){
						channelJsonArray.put(installationChannels.get(i));
					}

					pullValueJson.put("data", channelJsonArray);
				}


				pullJson.put("PULL", pullValueJson);
				subscribeToChannel.put("subscribed_to_channels", pullJson);

				mainJson.put("_method",  BuiltAppConstants.RequestMethod.PUT.toString());
				mainJson.put("object", subscribeToChannel);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_installation_data/objects/" + installationUid;

				new BuiltCallBackgroundTask(this, BuiltControllers.UNSUBSCRIBEINSTALLATIONDATA, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTINSTALLATION.toString(), callback);

			}catch(Exception e) {
				throwExeception(callback, e.toString());
			}
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_InstallationUidIsNull);
		}
	}

	private void throwExeception(BuiltResultCallBack callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.setErrorMessage(errorMessage);
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

	protected void clearAll() {
		mainJson 			= null;
		valueJson 			= null;
		channelJsonArray 	= null;
		deviceToken 		= null;
		deviceType 			= null;
		timeZone			= null;
		json				= null;

		applicationKey_local = null;
		applicationUid_local = null;

		geoLocationArray = new JSONArray();

		subscribedChannelList.clear();
		headerGroup_local.clear();
		clearInstallationUid();
	}

	protected void saveInstallationData(JSONObject responseJson) {

		try {
			String applicationKey = applicationKey_local;
			if(applicationKey == null){
				applicationKey = Built.applicationKey;
			}

			if(responseJson != null && applicationKey != null){
				JSONObject mainJsonObj = new JSONObject();
				mainJsonObj.put(applicationKey, responseJson);

				JSONArray jArray      = new RawAppUtils().getInstallationArrayFromInstallationFile(new File(BuiltAppConstants.installationFileName));
				JSONArray resultArray = new JSONArray();

				if(jArray == null){
					jArray = new JSONArray();

				}else{
					int count = jArray.length();
					for(int i = 0; i < count; i++){

						if(jArray.get(i).toString().contains(applicationKey)){

						}else{
							resultArray.put(jArray.get(i));
						}
					}
				}

				resultArray.put(mainJsonObj);
				java.io.File installationFile = new java.io.File(BuiltAppConstants.installationFileName);

				if(installationFile.exists()){
					installationFile.delete();
					installationFile.createNewFile();
				}else{
					installationFile.createNewFile();
				}

				FileWriter file = new FileWriter(installationFile, false);
				JSONObject resultJson = new JSONObject();
				resultJson.put("installation", resultArray);
				file.write(resultJson.toString());
				file.flush();
				file.close();

			}else{

			}
		} catch (Exception e) {

		}
	}

	protected String getInstallationUid(){
		try {
			String applicationKey  = applicationKey_local;
			String installationUid = null;
			if(applicationKey == null){
				applicationKey = Built.applicationKey;
			}

			JSONArray array = new RawAppUtils().getInstallationArrayFromInstallationFile(new File(BuiltAppConstants.installationFileName));

			if(array != null && array.length() > 0){
				int count = array.length();

				for(int i = 0; i < count; i++){

					if(array.get(i).toString().contains(applicationKey)){
						JSONObject valueJson = new JSONObject();

						valueJson             = (JSONObject) array.get(i);
						JSONObject appKeyJson = valueJson.optJSONObject(applicationKey);

						installationUid = appKeyJson.optString("uid");

						return installationUid;
					}
				}
			}
			return null;
		} catch (Exception error) {
			RawAppUtils.showLog("BuiltInstallation", "---------------getInstallationUid-catch|" + error);
		}
		return null;
	}


	protected void clearInstallationUid(){
		try {
			String applicationKey  = applicationKey_local;
			if(applicationKey == null){
				applicationKey = Built.applicationKey;
			}

			JSONArray array = new RawAppUtils().getInstallationArrayFromInstallationFile(new File(BuiltAppConstants.installationFileName));
			JSONArray resultArray = new JSONArray();

			if(array != null && array.length() > 0){
				int count = array.length();

				for(int i = 0; i < count; i++){

					if(array.get(i).toString().contains(applicationKey)){

					}else{
						resultArray.put(array.get(i));
					}
				}
			}

			java.io.File installationFile = new java.io.File(BuiltAppConstants.installationFileName);

			if(installationFile.exists()){
				installationFile.delete();
				installationFile.createNewFile();
			}else{
				installationFile.createNewFile();
			}

			FileWriter file = new FileWriter(installationFile, false);
			JSONObject sessionJson = new JSONObject();
			sessionJson.put("installation", resultArray);
			file.write(sessionJson.toString());
			file.flush();
			file.close();

		} catch (Exception error) {
			RawAppUtils.showLog("BuiltInstallation", "---------------clearInstallationUid-catch|" + error);
		}
	}


}
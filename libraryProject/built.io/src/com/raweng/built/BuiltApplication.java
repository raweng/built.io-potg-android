package com.raweng.built;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;

/**
 * To fetch application level information of your application from built.io server.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltApplication {


	private HeaderGroup headerGroup_local;
	private String applicationKey_local;
	private String applicationUid_local;

	protected HashMap<String, String> applicationVariableHashmap = null;

	private boolean includeApplicationVariables = false;



	protected HashMap<String, Object> applicationSettings   = null;
	protected String applicationKey 						= null;
	protected String applicationName 						= null;
	protected String applicationUid 						= null;
	protected String accountName 							= null;
	protected JSONObject json								= null;

	/**
	 * Returns application data in {@link HashMap} format if request to fetch application settings data executes successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #getApplicationVariableForKey(String)} 
	 * 
	 */
	public HashMap<String, Object> getApplicationSettings() {
		return applicationSettings;
	}

	/**
	 * Returns application key if request to fetch application settings data executes successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #getApplicationVariableForKey(String)} 
	 *  
	 */
	public String getApplicationKey() {
		return applicationKey;
	}

	/**
	 * Returns application name if request to fetch application settings data executes successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #getApplicationVariableForKey(String)} 
	 * 
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Returns application uid if request to fetch application settings data executes successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #getApplicationVariableForKey(String)} 
	 *  
	 */
	public String getApplicationUid() {
		return applicationUid;
	}


	/**
	 * Returns account name if request to fetch application settings data executes successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #getApplicationVariableForKey(String)} 
	 * 
	 */

	public String getAccountName() {
		return accountName;
	}

	/**
	 * Create a new BuiltApplication object.
	 */
	public BuiltApplication() {
		headerGroup_local = new HeaderGroup();
	}

	/**
	 *  Sets the api key and Application uid for {@link BuiltApplication} class instance.
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
		if(headerGroup_local.containsHeader(key)){
			org.apache.http.Header header =  headerGroup_local.getCondensedHeader(key);
			headerGroup_local.removeHeader(header);
		}
	}


	/**
	 * To include all the &#34;application_variables&#34;.
	 */
	public void includeApplicationVariables() {
		includeApplicationVariables = true;
	}


	/**
	 * To retrieve current application&#39;s settings information.
	 * 
	 * @param callback &nbsp;
	 * 					  {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * @return
	 * 				 {@link BuiltApplication} object, so you can chain this call.
	 * 
	 * <p>
	 * <b> Note: </b> You can get application settings data by accessing {@link #getAccountName()}, {@link #getApplicationSettings()}
	 * , {@link #getApplicationKey()}, {@link #getApplicationUid()}, {@link #getApplicationName()}
	 */
	public BuiltApplication fetchApplicationSettings(BuiltResultCallBack callback){
		try{
			JSONObject mainJson = new JSONObject();
			mainJson.put("_method",  BuiltAppConstants.RequestMethod.GET.toString());

			if(includeApplicationVariables){
				mainJson.put("include_application_variables", true);
				includeApplicationVariables = false;
			}

			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/applications/" + Built.applicationUid;

			Header[] header = getHeaders(headerGroup_local).getAllHeaders();
			if(header != null && header.length > 0){
				new BuiltCallBackgroundTask(this, BuiltControllers.APPLICATIONSETTINGS, URL, header, mainJson, null, callController.BUILTAPPLICATION.toString(), callback);
			}else{
				BuiltError error = new BuiltError();
				error.errorMessage(BuiltAppConstants.ErrorMessage_CalledBuiltDefaultMethod);
				if(callback != null){
					callback.onRequestFail(error);
				}
			}
		}catch (Exception e) {
			BuiltError error = new BuiltError();
			error.errorMessage(e.toString());
			if(callback != null){
				callback.onRequestFail(error);
			}
		}
		return this;
	}

	/**
	 * To cancel all {@link BuiltApplication} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTAPPLICATION.toString());
	}


	/**
	 * Provides {@link BuiltQuery} object to retrieve application user. 
	 * This method returns an object of {@link BuiltQuery} class, 
	 * all the methods of the {@link BuiltQuery} class are available and can be used to query the Application user class of built.io
	 * 
	 * @return {@link BuiltQuery} object.
	 */
	public BuiltQuery applicationUserQuery(){

		return new BuiltQuery("built_io_application_user");

	}

	/**
	 * To get value of the application variable for the given key.
	 * 
	 * @param key
	 * 				the key of the application variable.
	 * @return
	 * 			the value for the given key.
	 */
	public String getApplicationVariableForKey(String key){

		if(applicationVariableHashmap != null && applicationVariableHashmap.containsKey(key)){

			return applicationVariableHashmap.get(key);

		}else{
			return null;
		}
	}

	/**
	 * 
	 * Returns JSON representation of this {@link BuiltApplication} instance data.
	 * 
	 */

	public JSONObject toJSON() {

		return json;
	}
	
	/*************************************************************************************************
	 * 
	 ************  Private Methods  *******************
	 * 
	 ****************************************************************************************/

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

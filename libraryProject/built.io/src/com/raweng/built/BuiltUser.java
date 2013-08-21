package com.raweng.built;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.userInterface.BuiltUILoginController;
import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.built.utilities.RawAppUtils;

/**
 * This class allows registering user for an application and other 
 * features such as logging in and logging out.
 * <br>
 * After the log in, the {@link #getAuthToken()} will be given in response. 
 * This {@link #getAuthToken} is to be supplied for subsequent requests if the the request is to be identified as being from that user.
 * 
 * @author  raw engineering, Inc
 *
 */
public class BuiltUser implements INotifyClass {


	private JSONArray geoLocationArray       = null;
	private String userUid                   = null;

	protected HeaderGroup headerGroup_local ;
	protected static String applicationKey_local ;
	protected String applicationUid_local ;

	protected JSONObject json 							= null;
	protected JSONObject sessionJson  					= null;
	protected String authToken 							= null;
	protected String firstName   						= null;
	protected String lastName   						= null;
	protected String userName    						= null;
	protected String email       						= null;
	protected String googleAccessToken  				= null;
	protected HashMap<String, Object> googleAuthData 	= null;

	protected static BuiltUser builtUserInstance = null;

	/**
	 * Creates new  instance of {@link BuiltUser} object.
	 */
	public BuiltUser(){
		headerGroup_local = new HeaderGroup();
	}

	/**
	 *  Sets the api key and Application uid for BuiltUser class instance.
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
	 * 
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
	 * Remove a header for a given key from headers.
	 *  
	 * @param key header key.
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
	 * Whether the user is an authenticated object for the device. An authenticated BuiltUser 
	 * is one that is obtained via a signUp or logIn method.
	 *  An authenticated object is required in order to save (with altered values) or delete it
	 * 
	 * @return true , if user is already logged in or had a authtoken else return false.
	 */
	public boolean isAuthenticated(){
		return (authToken != null);
	}

	/**
	 * To set geo location for this user.
	 * 
	 * @param location
	 *               {@link BuiltLocation} instance 
	 * 
	 * @return
	 * 			{@link BuiltUser} object, so you can chain this call.
	 */
	public BuiltUser setLocation(BuiltLocation location) {

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
	 * Login BuiltUser by providing email as ID and sign-up password.
	 * 
	 * @param email 
	 * 				email.
	 * 
	 * @param password 
	 * 					password.
	 * 
	 * @param callback  
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * <p>
	 * <b> Note: </b> 
	 * <li>You can access application user data by accessing  {@link #getAuthToken()}, {@link #getUserUid()}, {@link #getFirstName()},
	 * {@link #getLastName()}, {@link #getUserName()}, {@link #getEmailId()} </li>
	 * 
	 * <li> You must call createInstallation from {@link BuiltInstallation} class after this method.</li>
	 * 
	 * <li> If you want built.io SDK to hold your login response then you must call {@link #saveSession()} method after logged-in. </li>
	 * 
	 */
	public void login(String email, String password, BuiltResultCallBack callback){

		if(email == null){

			throwExeception(callback, BuiltAppConstants.ErrorMessage_UserEmailIdIsNull);
		}else if(password == null){

			throwExeception(callback, BuiltAppConstants.ErrorMessage_UserPasswordIsNull);
		}else{
			login(email, password, false, callback);
		}
	}


	/**
	 * Login using Google OAuth 2.0 access token.
	 * 
	 * @param accessToken
	 * 			Google OAuth 2.0 access token.
	 * 
	 * @param callback
	 * 				{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * <p>
	 * <b> Note: </b> 
	 * <li> You can access application user data by accessing  {@link #getAuthToken()}, {@link #getUserUid()}, {@link #getFirstName()},
	 * {@link #getLastName()}, {@link #getUserName()}, {@link #getEmailId()} </li>
	 * 
	 * <li> You must call createInstallation from {@link BuiltInstallation} class after this method.</li>
	 * 
	 *  <li> If you want built.io SDK to hold your login response then you must call {@link #saveSession()} method after logged-in. </li>
	 */
	public void loginWithGoogleAuthAccessToken(String accessToken, BuiltResultCallBack callback) {

		if(accessToken == null){
			throwExeception(callback, BuiltAppConstants.ErrorMessage_GoogleAuthTokenIsNull);

		}else{
			loginWithGoogleAuthAccessToken(accessToken, false, callback);
		}
	}


	/**
	 * Login using facebook access token.
	 * 
	 * @param accessToken
	 * 			facebook access token.
	 * 
	 * @param callback
	 * 				{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * <p>
	 * <b> Note: </b> 
	 * <li> You can access application user data by accessing   {@link #getAuthToken()}, {@link #getUserUid()}, {@link #getFirstName()},
	 * {@link #getLastName()}, {@link #getUserName()}, {@link #getEmailId()} </li>
	 * 
	 * <li> You must call createInstallation from {@link BuiltInstallation} class after this method. </li>
	 * 
	 * <li> If you want built.io SDK to hold your login response then you must call {@link #saveSession()} method after logged-in. </li>
	 */
	public void loginWithFacebookAuthAccessToken(String accessToken, BuiltResultCallBack callback) {

		if(accessToken == null){
			throwExeception(callback, BuiltAppConstants.ErrorMessage_FacebookAccessTokenIsNull);

		}else{
			loginWithFacebookAuthAccessToken(accessToken, false, callback);
		}
	}


	/**
	 * Login using twitter access token.
	 * 
	 * @param twitterAccessToken
	 * 			                 twitter access token.
	 * 
	 * @param twitterAccessTokenSecret
	 * 			                     twitter access token secret.
	 * 
	 * @param twitterConsumerKey
	 * 								 twitter consumer key.
	 * 
	 * @param twitterConsumerSecret
	 * 								twitter consumer secret.			
	 * 
	 * @param callback
	 * 				{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * <p>
	 * <b> Note: </b> 
	 * <li> You can access application user data by accessing   {@link #getAuthToken()}, {@link #getUserUid()}, {@link #getFirstName()},
	 * {@link #getLastName()}, {@link #getUserName()}, {@link #getEmailId()} </li>
	 * 
	 * <li> You must call createInstallation from {@link BuiltInstallation} class after this method. </li>
	 * 
	 * <li> If you want built.io SDK to hold your login response then you must call {@link #saveSession()} method after logged-in. </li>
	 */
	public void loginWithTwitterAuthAccessToken(String twitterAccessToken, String twitterAccessTokenSecret, String twitterConsumerKey, String twitterConsumerSecret, BuiltResultCallBack callback) {

		if(twitterAccessToken == null){
			throwExeception(callback, BuiltAppConstants.ErrorMessage_TwitterAccessTokenIsNull);

		}else if(twitterAccessTokenSecret == null){
			throwExeception(callback, BuiltAppConstants.ErrorMessage_TwitterAccessTokenSecretIsNull);
		}else{

			loginWithTwitterOAuthToken(twitterAccessToken,twitterAccessTokenSecret,twitterConsumerKey,twitterConsumerSecret, false, callback);
		}
	}



	/**
	 * Login using tibbr.
	 * 
	 * @param accessToken
	 * 			tibbr acccess token.
	 * 
	 * @param hostName 
	 * 		tibbr host name.
	 * 		tibbr host name must be supply in &#34;https://customhost.tibbr.com&#34; format.
	 * 
	 * @param callback
	 * 				{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * <p>
	 * <b> Note: </b>
	 * <li> You can access application user data by accessing  {@link #getAuthToken()}, {@link #getUserUid()}, {@link #getFirstName()},
	 * {@link #getLastName()}, {@link #getUserName()}, {@link #getEmailId()} </li>
	 * 
	 * <li> You must call createInstallation from {@link BuiltInstallation} class after this method.</li>
	 * 
	 * <li> If you want built.io SDK to hold your login response then you must call {@link #saveSession()} method after logged-in. </li>
	 */
	public void loginWithtibbrAuthAccessToken(String accessToken, String hostName, BuiltResultCallBack callback) {

		if(accessToken == null){
			throwExeception(callback, BuiltAppConstants.ErrorMessage_tibbrAccessTokenIsNull);

		}else if(hostName == null){
			throwExeception(callback, BuiltAppConstants.ErrorMessage_tibbrHostNameIsNull);

		}else{
			loginWithtibbrAuthAccessToken(accessToken,hostName, false, callback);
		}
	}




	/**
	 * This retrieves the currently logged in BuiltUser from memory.
	 * 
	 * @return 
	 * 			{@link BuiltUser} instance.
	 * 
	 * <p>
	 * <b> Note :- </b>
	 * <li> This method will return logged-in  builtUser if any else, returns null. 
	 * 
	 */
	public static BuiltUser getCurrentUser(){

		return builtUserInstance;

	}

	/**
	 * Fetch logged In user&#39;s all information from built.io server.
	 * 
	 * @param callback  
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 			 {@link BuiltUser} object, so you can chain this call.
	 */
	public BuiltUser refreshUserInfo(BuiltResultCallBack callback){
		try{

			JSONObject mainJson = new JSONObject();
			mainJson.put("_method",  BuiltAppConstants.RequestMethod.GET.toString());

			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/current";

			new BuiltCallBackgroundTask(this, BuiltControllers.CHECKAPPLICATIONUSERPROFILE, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);

		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
		return this;
	}

	/**
	 * Request to register user.
	 * 
	 * @param userInfo
	 * 					{@link HashMap} object containing user info .
	 *                           UserInfo object contains following keys :
	 *					        <br>
	 *					        email: required,
	 *					        <br>
	 *					        password:required,
	 *					        <br>
	 *					        password_confirmation:required,
	 *					        <br>
	 *					        username:optional,
	 *					        <br>
	 *					        first_name:optional,
	 *					        <br>
	 *					        last_name:optional,
	 *					        <br>
	 *					        anydata:anyvalue
	 *
	 * 
	 * @param callback
	 *                 {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return  
	 * 			 {@link BuiltUser} object, so you can chain this call.
	 * 
	 * <p>
	 * <b> Note: </b>
	 * <li> You can access application user data by accessing  {@link #getAuthToken()}, {@link #getUserUid()}, {@link #getFirstName()},
	 * {@link #getLastName()}, {@link #getUserName()}, {@link #getEmailId()} </li>
	 * 
	 * 
	 */
	public BuiltUser register(HashMap<String, Object> userInfo, BuiltResultCallBack callback){

		if(userInfo != null && userInfo.size() > 0){
			register(userInfo, false, callback);
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_UesrInfoHashMapIsNull);
		}

		return this;
	}


	/** 
	 * Logs out the currently logged in user on disk.
	 * 
	 * @param callback
	 *                 {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 			 {@link BuiltUser} object, so you can chain this call.  
	 * 
	 *   <br><br>
	 * <b> NOTE :- </b> You must call deleteInstallationData from {@link BuiltInstallation} class before this.
	 */

	public BuiltUser logout(BuiltResultCallBack callback){ 

		return logout(false, callback);
	}


	/**
	 * Deactivate user&#39;s account on built.io.
	 * 
	 * @param callback 
	 * 					 {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 */
	public void deactivate(BuiltResultCallBack callback) {

		deactivate(false, callback);
	}

	/**
	 * To activate user.
	 * 
	 * @param userUid
	 * 					user uid.
	 * 
	 * @param activationToken
	 * 					activation token.
	 * 
	 * @param callback
	 * 					 {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 */
	public void activateUser(String userUid, String activationToken, BuiltResultCallBack callback){
		try{
			if(userUid == null){
				throwExeception(callback, BuiltAppConstants.ErrorMessage_userIDIsNull);

			}else if(activationToken == null){
				throwExeception(callback, BuiltAppConstants.ErrorMessage_ActivationTokenIsNull);

			}else{
				JSONObject mainJson = new JSONObject();
				mainJson.put("_method",  BuiltAppConstants.RequestMethod.GET.toString());

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/" + userUid + "/activate/" + activationToken;


				new BuiltCallBackgroundTask(this, BuiltControllers.ACTIVATEUSERUSINGACTIVATIONCODE, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);

			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}

	}

	/**
	 * To retrieve user uid using specified email id.
	 * 
	 * @param emailId
	 * 					email id.
	 * 
	 * @param callback
	 * 					{@link BuildUserResultCallback} object to notify the application when the request has completed.
	 * 
	 * <p>
	 * <b> Note : </b> 
	 * <li> This call will allow you to retrieve the uid for an user, whether or not the user exists. 
	 * A means for identifying the user needs to be provided. 
	 * This comes in handy, for example, when you need to apply ACL for an user that may not exist in the system at the moment. </li>
	 */
	public void fetchUserUidForEmail(String emailId, BuildUserResultCallback callback){
		if(emailId == null){

			throwExeception(callback, BuiltAppConstants.ErrorMessage_UserEmailIdIsNull);
		}else{

			try{
				JSONObject mainJson  = new JSONObject();
				JSONObject valueJson = new JSONObject();

				valueJson.put("email", emailId);
				mainJson.put("application_user", valueJson);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/retrieve_user_uid";

				new BuiltCallBackgroundTask(this, BuiltControllers.FETCHUESRUIDFOREMAIL, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
			}catch (Exception e) {
				throwExeception(callback, e.toString());
			}

		}
	}


	/**
	 * To retrieve user uid using specified email id.
	 * 
	 * @param emailId
	 * 					email id with google domain.
	 * 
	 * @param callback
	 * 					{@link BuildUserResultCallback} object to notify the application when the request has completed.
	 * 
	 * <p>
	 * <b> Note : </b> 
	 * <li> This call will allow you to retrieve the uid for an user, whether or not the user exists. 
	 * A means for identifying the user needs to be provided. 
	 * This comes in handy, for example, when you need to apply ACL for an user that may not exist in the system at the moment.</li>
	 */

	public void fetchUserUidForGoogleEmail(String emailId, BuildUserResultCallback callback){
		if(emailId == null){

			throwExeception(callback, BuiltAppConstants.ErrorMessage_UserEmailIdIsNull);
		}else{

			try{
				JSONObject mainJson  = new JSONObject();
				JSONObject valueJson = new JSONObject();
				JSONObject authDataJson = new JSONObject();
				JSONObject googleJson = new JSONObject();

				valueJson.put("email", emailId);
				googleJson.put("google", valueJson);
				authDataJson.put("auth_data", googleJson);

				mainJson.put("application_user", authDataJson);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/retrieve_user_uid";

				new BuiltCallBackgroundTask(this, BuiltControllers.FETCHUESRUIDFOREMAIL, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
			}catch (Exception e) {
				throwExeception(callback, e.toString());
			}

		}
	}



	/**
	 * To retrieve user uid for specified tibbr user id.
	 *  
	 * @param userId
	 * 					tibbr user id.
	 * 
	 * @param hostName 
	 * 		tibbr host name.
	 * 		tibbr host name must be supply in &#34;https://customhost.tibbr.com&#34; format.
	 * 
	 * @param callback
	 * 					{@link BuildUserResultCallback} object to notify the application when the request has completed.
	 * 
	 * <p>
	 * <b> Note : </b> 
	 * <li> This call will allow you to retrieve the uid for an user, whether or not the user exists. 
	 * A means for identifying the user needs to be provided. 
	 * This comes in handy, for example, when you need to apply ACL for an user that may not exist in the system at the moment.</li>
	 */

	public void fetchUserUidFortibbr(String userId, String hostName, BuildUserResultCallback callback){
		if(userId == null){

			throwExeception(callback, BuiltAppConstants.ErrorMessage_tibbrUserIDIsNull);

		}else if(hostName == null){

			throwExeception(callback, BuiltAppConstants.ErrorMessage_tibbrHostNameIsNull);

		}else{

			try{
				JSONObject mainJson  = new JSONObject();
				JSONObject valueJson = new JSONObject();
				JSONObject authDataJson = new JSONObject();
				JSONObject googleJson = new JSONObject();

				valueJson.put("user_id", userId);
				valueJson.put("host", hostName);
				googleJson.put("tibbr", valueJson);
				authDataJson.put("auth_data", googleJson);

				mainJson.put("application_user", authDataJson);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/retrieve_user_uid";

				new BuiltCallBackgroundTask(this, BuiltControllers.FETCHUESRUIDFOREMAIL, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
			}catch (Exception e) {
				throwExeception(callback, e.toString());
			}
		}
	}


	/**
	 * To retrieve user uid for specified facebook user id.
	 * 
	 * @param userId
	 * 					facebook user Id.
	 * 
	 * @param callback
	 * 					{@link BuildUserResultCallback} object to notify the application when the request has completed.
	 * 
	 * <p>
	 * <b> Note : </b> 
	 * <li> This call will allow you to retrieve the uid for an user, whether or not the user exists. 
	 * A means for identifying the user needs to be provided. 
	 * This comes in handy, for example, when you need to apply ACL for an user that may not exist in the system at the moment.</li>
	 */

	public void fetchUserUidForFacebook(String userId, BuildUserResultCallback callback){
		if(userId == null){

			throwExeception(callback, BuiltAppConstants.ErrorMessage_FacebookUserIdIsNull);
		}else{

			try{
				JSONObject mainJson  = new JSONObject();
				JSONObject valueJson = new JSONObject();
				JSONObject authDataJson = new JSONObject();
				JSONObject facebookJson = new JSONObject();

				valueJson.put("user_id", userId);
				facebookJson.put("facebook", valueJson);
				authDataJson.put("auth_data", facebookJson);

				mainJson.put("application_user", authDataJson);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/retrieve_user_uid";

				new BuiltCallBackgroundTask(this, BuiltControllers.FETCHUESRUIDFOREMAIL, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
			}catch (Exception e) {
				throwExeception(callback, e.toString());
			}

		}
	}


	/**
	 * Reset password.
	 * 
	 * @param emailId
	 * 					email id. 
	 * 
	 * @param callback 
	 * 				{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 */
	public void forgotPassword(String emailId, BuiltResultCallBack callback){

		if(emailId != null){
			forgotPassword(emailId, false, callback);
		}else{
			throwExeception(callback, BuiltAppConstants.ErrorMessage_ProvideValidEmailId);
		}
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link Object} object with key as given key.
	 * 
	 */
	public Object get(String key){
		try{
			if(json != null){

				if(json.get(key) instanceof JSONObject){

					return createHashResult((JSONObject) json.get(key));

				}else if(json.get(key) instanceof JSONArray){

					return json.get(key);

				}else{
					return  json.get(key);
				}
			}else{
				return null;
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltUser", "-----------------get|" + e);
			return null;
		}
	}

	/**
	 * To get this object geo location.
	 * 
	 * @return 
	 * 			{@link BuiltLocation} instance.
	 * 
	 */
	public BuiltLocation getLocation() {
		try {
			if(json != null){
				JSONObject userJsonObject = json.optJSONObject("application_user");
				if(userJsonObject != null){
					if(userJsonObject.has("__loc")){
						Object value =  userJsonObject.optString("__loc");
						if(value instanceof JSONArray){
							BuiltLocation location = new BuiltLocation();
							location.setLocation(((JSONArray) value).getDouble(1), ((JSONArray) value).getDouble(0));
							return location;
						}
					}
				}
			}
		} catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------getLocation|" + e);
		}
		return null;
	}

	//	***********************************************************
	/**
	 * Saves current logged-in builtUser on disk.
	 * 
	 * <p>
	 * 
	 * <b> Usage: </b>
	 * <p font size = 2>
	 * <pre>
	 * <code>final BuiltUser user = new BuiltUser();
	 * user.login("abc@xyz.com", "password",new BuiltResultCallBack() {
	 *	 
	 *	 {@literal @}Override
	 *        public void onSuccess() {
	 *				try {
	 *					user.saveSession();
	 *				} catch(Exception error) {
	 *					Log.i("SaveSession", "--Error while saving session|" + error);
	 *				}
	 *			}
	 *
	 *		 {@literal @}Override
	 *			public void onError(BuiltError error) {
	 *			}
	 *
	 *			 {@literal @}Override
	 *			public void onAlways() {}
	 *		});
	 * </code>
	 * </pre>
	 *</p>
	 * 
	 * @throws Exception 
	 */
	public void saveSession() throws Exception{

		String applicationKey = applicationKey_local;
		if(applicationKey == null){
			applicationKey = Built.applicationKey;
		}

		if(json != null && applicationKey != null){
			JSONObject mainJsonObj = new JSONObject();
			mainJsonObj.put(applicationKey, json);

			JSONArray jArray      = new RawAppUtils().getSessionArrayFromSessionFile(new File(BuiltAppConstants.sessionFileName));
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
			java.io.File sessionFile = new java.io.File(BuiltAppConstants.sessionFileName);

			if(sessionFile.exists()){
				sessionFile.delete();
				sessionFile.createNewFile();
			}else{
				sessionFile.createNewFile();
			}

			FileWriter file = new FileWriter(sessionFile, false);
			JSONObject sessionJson = new JSONObject();
			sessionJson.put("session", resultArray);
			file.write(sessionJson.toString());
			file.flush();
			file.close();

		}else{

			if(json == null){
				throw new Exception(BuiltAppConstants.ErrorMessage_LoginFirst);
			}else{
				throw new Exception(BuiltAppConstants.ErrorMessage_ApplicationKeyNotAvailable);
			}
		}
	}

	/**
	 * To set {@link BuiltUser} instance with a valid session, on memory.
	 * 
	 * @param builtUser
	 * 					{@link BuiltUser} instance. 
	 * 
	 * <p>
	 * <b> Note: </b>
	 * User can use this method with {@link #getSession()};
	 * For example after restarting app user can fetch current logged-in builtUser using  {@link #getSession()} method and then set retrieved builtUser instance by using this method.
	 * 
	 */
	public static void setCurrentUser(BuiltUser builtUser) throws Exception{

		if(builtUser  != null  && builtUser.authToken != null){

			builtUserInstance = builtUser;

			Built.setHeader("authtoken", (String) builtUserInstance.authToken);

		}else{
			if(builtUser == null){
				throw new Exception(BuiltAppConstants.ErrorMessage_BuitUserObjectIsNull);
			}else{
				throw new Exception(BuiltAppConstants.ErrorMessage_AuthtokenIsNull);
			}
		}

	}

	/**
	 * Clears {@link BuiltUser}&#39;s session.
	 * 
	 */
	public void clearSession(){
		try{
			String applicationKey = applicationKey_local;
			if(applicationKey == null){
				applicationKey = Built.applicationKey;
			}

			JSONArray array  = new RawAppUtils().getSessionArrayFromSessionFile(new File(BuiltAppConstants.sessionFileName));
			if(array != null){

				JSONArray jArray = new JSONArray();
				int count = array.length();
				for(int i = 0; i < count; i++){

					if(array.get(i).toString().contains(applicationKey)){

					}else{
						jArray.put(array.get(i));
					}

				}
				java.io.File sessionFile = new java.io.File(BuiltAppConstants.sessionFileName);

				if(sessionFile.exists()){
					sessionFile.delete();
					sessionFile.createNewFile();
				}

				FileWriter file = new FileWriter(sessionFile, false);
				JSONObject sessionJson = new JSONObject();
				sessionJson.put("session", jArray);
				file.write(sessionJson.toString());
				file.flush();
				file.close();
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltUser", "-----------------clearSession|" + e);
		}

	}

	/**
	 * To retrieve  {@link BuiltUser} Session from disk.
	 * 
	 * @return {@link BuiltUser} instance.
	 * 
	 * <p>
	 * <b> Note : </b>
	 * <li> To retrieve user session, you must call {@link #saveSession()} method after logged-in. </li>

	 */
	public static BuiltUser getSession(){

		try{
			JSONObject appKeyJson = new JSONObject();
			JSONArray array = new RawAppUtils().getSessionArrayFromSessionFile(new File(BuiltAppConstants.sessionFileName));


			int count = array.length();
			String appKey = null;
			if(applicationKey_local != null){
				appKey = applicationKey_local;
			}else{
				appKey = Built.applicationKey;
			}

			for(int i = 0; i < count; i++){

				if(array.get(i).toString().contains(appKey)){
					JSONObject valueJson = new JSONObject();
					valueJson = (JSONObject) array.get(i);

					appKeyJson = valueJson.optJSONObject(appKey);

					BuiltApplicationUserModel model = new BuiltApplicationUserModel(appKeyJson);
					BuiltUser builtUserObject = new BuiltUser();
					builtUserObject.json            = appKeyJson;
					builtUserObject.authToken 		= model.authToken;
					builtUserObject.userName 		= model.userName;
					builtUserObject.firstName 		= model.firstName;
					builtUserObject.lastName 		= model.lastName;
					builtUserObject.email 			= model.email;
					builtUserObject.setUserUid(model.userUid);
					builtUserObject.googleAuthData  = model.googleAuthData;

					return builtUserObject;
				}

			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltUser", "-----------------getSession|"+e);
			return null;
		}
		return null;

	}

	/**
	 * Sets user uid. 
	 * 
	 * @param userUid
	 * 					user uid.
	 * 	
	 */
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	/**
	 * Returns {@link BuiltUser} uid token if user logged-in successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #login(String, String, BuiltResultCallBack)}  
	 * {@link #loginWithGoogleAuthAccessToken(String, BuiltResultCallBack)} 
	 * 
	 */
	public String getUserUid() {
		return userUid;
	}

	/**
	 * Returns  {@link BuiltUser} authentication token if user logged-in successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #login(String, String, BuiltResultCallBack)}  
	 * {@link #loginWithGoogleAuthAccessToken(String, BuiltResultCallBack)} 
	 * 
	 */
	public String getAuthToken() {
		return authToken;
	}


	/**
	 * Returns  {@link BuiltUser} first name if user logged-in successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #login(String, String, BuiltResultCallBack)}  
	 * {@link #loginWithGoogleAuthAccessToken(String, BuiltResultCallBack)} 
	 * 
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Returns  {@link BuiltUser} last name if user logged-in successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #login(String, String, BuiltResultCallBack)}  
	 * {@link #loginWithGoogleAuthAccessToken(String, BuiltResultCallBack)} 
	 * 
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Returns  {@link BuiltUser} user name if user logged-in successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #login(String, String, BuiltResultCallBack)}  
	 * {@link #loginWithGoogleAuthAccessToken(String, BuiltResultCallBack)} 
	 *  
	 */
	public String getUserName() {
		return userName;
	}


	/**
	 * Returns  {@link BuiltUser} email if user logged-in successfully.
	 * 
	 * <p>
	 * <b> Ref:- </b>
	 * {@link #login(String, String, BuiltResultCallBack)}  
	 * {@link #loginWithGoogleAuthAccessToken(String, BuiltResultCallBack)} 
	 *  
	 */
	public String getEmailId() {
		return email;
	}



	/**
	 * Returns  {@link BuiltUser}&#39;s google login response data. 
	 */
	private HashMap<String, Object> getGoogleAuthData() {
		return googleAuthData;
	}

	/**
	 *Returns twitter access token in in {@link BuiltUILoginController} success override method.   
	 * 
	 *<br><b>Example</b>
	 * 
	 *<pre class="prettyprint">
	 *<br>&#160;&#160;&#160;&#160;@Override
	 *<br>&#160;&#160;&#160;&#160;public void loginSuccess(BuiltUser user) {
	 *<br>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; String twitterAccessToken = user.getTwitterAccessToken();
	 *
	 *<br>&#160;&#160;&#160;&#160;	}
	 *
	 *</pre>
	 */
	public String getTwitterAccessToken(){

		return BuiltAppConstants.TWITTER_ACCESS_TOKEN;
	}

	/**
	 *Returns twitter access token secret in {@link BuiltUILoginController} success override method.   
	 * 
	 * <br><b>Example</b>
	 * 
	 *<pre class="prettyprint">
	 *<br>&#160;&#160;&#160;&#160;@Override
	 *<br>&#160;&#160;&#160;&#160;public void loginSuccess(BuiltUser user) {
	 *<br>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; String twitterAccessTokenSecret = user.getTwitterAccessTokenSecret();
	 *
	 *<br>&#160;&#160;&#160;&#160;	}
	 *
	 *</pre>
	 */
	public String getTwitterAccessTokenSecret(){

		return  BuiltAppConstants.TWITTER_ACCESS_TOKEN_SECRET;
	}

	/**
	 * To cancel all {@link BuiltUser} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTUSER.toString());
	}


	/**
	 * Get {@link BuiltUser} creation date.
	 * 
	 * 
	 * @return 
	 * 			{@link Calendar} instance.
	 * 
	 */
	public Calendar getCreateAt(){

		try {
			if(json != null){
				JSONObject userJsonObject = json.optJSONObject("application_user");
				if(userJsonObject != null){
					if(userJsonObject.has("created_at")){
						String value = userJsonObject.optString("created_at");
						return BuiltUtil.parseDate(value);
					}
				}
			}
		} catch (Exception e) {
			RawAppUtils.showLog("BuiltUser", "-----------------getCreateAtDate|" + e);
		}
		return null;
	}


	/**
	 * Get {@link BuiltUser} updation date.
	 * 
	 * 
	 * @return 
	 * 			{@link Calendar} instance.
	 * 
	 */
	public Calendar getUpdateAt(){

		try {
			if(json != null){
				JSONObject userJsonObject = json.optJSONObject("application_user");
				if(userJsonObject != null){
					if(userJsonObject.has("updated_at")){
						String value = userJsonObject.optString("updated_at");
						return BuiltUtil.parseDate(value);
					}
				}
			}
		} catch (Exception e) {
			RawAppUtils.showLog("BuiltUser", "-----------------getUpdateAtDate|" + e);
		}
		return null;
	}




	/**
	 * 
	 * Returns JSON representation of this {@link BuiltUser} instance data.
	 * 
	 */

	public JSONObject toJSON() {

		return json;
	}

	public void setGoogleAccessToken(String googleAccessToken){
		this.googleAccessToken = googleAccessToken;
	}

	/**
	 * Google oauth 2.0 access token from google used to log a user into your application.
	 * Access token is short lived, it will get expired in sometime.
	 * 
	 * Returns google access token in {@link BuiltUILoginController} success override method. 
	 * @return
	 * 			google oauth 2.0 access token.
	 * 
	 *<br><b>Example</b>
	 * 
	 *<pre class="prettyprint">
	 *<br>&#160;&#160;&#160;&#160;@Override
	 *<br>&#160;&#160;&#160;&#160;public void loginSuccess(BuiltUser user) {
	 *<br>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; String googleAccessToken = user.getGoogleAccessToken();
	 *
	 *<br>&#160;&#160;&#160;&#160;	}
	 *
	 *</pre>
	 */
	public String getGoogleAccessToken(){

		return googleAccessToken;

	}

	/***********************************************************************************************************
	 * 
	 * 
	 *************************************/

	@Override
	public void getResult(java.lang.Object object) {
		if(object != null){ // login call

			authToken = (String) object;
			Built.setHeader("authtoken", (String) object);
			builtUserInstance = this;

		}else{ // logged - out call 

			if(headerGroup_local.containsHeader("authtoken")){
				org.apache.http.Header header =  headerGroup_local.getCondensedHeader("authtoken");
				headerGroup_local.removeHeader(header);
			}
			Built.removeHeader("authtoken");
			builtUserInstance = null;
			authToken = null;
			try {
				clearSession();
			} catch (Exception e) {
				RawAppUtils.showLog("BuiltUser", e.toString());		
			}
		}
	}

	@Override
	public void getResultObject(List<java.lang.Object> obj,
			JSONObject jsonobject) {

	}



	private void login(String email, String password, boolean isOfflineCall, BuiltResultCallBack callback) {
		try{
			JSONObject mainJson = new JSONObject();
			JSONObject valueJson = new JSONObject();

			valueJson.put("email", email);
			valueJson.put("password", password);
			valueJson.put("device_type", "android");

			mainJson.put("application_user", valueJson);

			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/login";

			new BuiltCallBackgroundTask(this, BuiltControllers.LOGIN, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);

		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}	
	}


	private void loginWithGoogleAuthAccessToken(String accessToken, boolean isOfflineCall, BuiltResultCallBack callback) {
		try{
			JSONObject mainJson     = new JSONObject();
			JSONObject authDataJson = new JSONObject();
			JSONObject googleJson   = new JSONObject();
			JSONObject valueJson    = new JSONObject();


			valueJson.put("access_token", accessToken);
			googleJson.put("google", valueJson);
			authDataJson.put("auth_data", googleJson);
			authDataJson.put("device_type", "android");

			if(geoLocationArray != null && geoLocationArray.length() > 0){
				authDataJson.put("__loc", geoLocationArray);
			}

			mainJson.put("application_user", authDataJson);


			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users";

			new BuiltCallBackgroundTask(this, BuiltControllers.LOGIN, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void loginWithFacebookAuthAccessToken(String accessToken, boolean isOfflineCall, BuiltResultCallBack callback) {
		try{
			JSONObject mainJson     = new JSONObject();
			JSONObject authDataJson = new JSONObject();
			JSONObject googleJson   = new JSONObject();
			JSONObject valueJson    = new JSONObject();


			valueJson.put("access_token", accessToken);
			googleJson.put("facebook", valueJson);
			authDataJson.put("auth_data", googleJson);
			authDataJson.put("device_type", "android");

			if(geoLocationArray != null && geoLocationArray.length() > 0){
				authDataJson.put("__loc", geoLocationArray);
			}

			mainJson.put("application_user", authDataJson);


			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users";

			new BuiltCallBackgroundTask(this, BuiltControllers.LOGIN, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void loginWithTwitterOAuthToken(String twitterAccessToken,String twitterAccessTokenSecret,String twitterConsumerKey,String twitterConsumerSecret, boolean isOfflineCall,BuiltResultCallBack callback) {
		try{
			JSONObject mainJson     = new JSONObject();
			JSONObject authDataJson = new JSONObject();
			JSONObject googleJson   = new JSONObject();
			JSONObject valueJson    = new JSONObject();


			valueJson.put("token", twitterAccessToken);
			valueJson.put("token_secret", twitterAccessTokenSecret);
			valueJson.put("consumer_key", twitterConsumerKey);
			valueJson.put("consumer_secret", twitterConsumerSecret);
			googleJson.put("twitter", valueJson);
			authDataJson.put("auth_data", googleJson);
			authDataJson.put("device_type", "android");

			if(geoLocationArray != null && geoLocationArray.length() > 0){
				authDataJson.put("__loc", geoLocationArray);
			}

			mainJson.put("application_user", authDataJson);


			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users";

			new BuiltCallBackgroundTask(this, BuiltControllers.LOGIN, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void loginWithtibbrAuthAccessToken(String accessToken, String host, boolean isOfflineCall, BuiltResultCallBack callback) {
		try{
			JSONObject mainJson     = new JSONObject();
			JSONObject authDataJson = new JSONObject();
			JSONObject tibbrJson   = new JSONObject();
			JSONObject valueJson    = new JSONObject();


			valueJson.put("access_token", accessToken);
			valueJson.put("host", host);
			tibbrJson.put("tibbr", valueJson);
			authDataJson.put("auth_data", tibbrJson);
			authDataJson.put("device_type", "android");

			if(geoLocationArray != null && geoLocationArray.length() > 0){
				authDataJson.put("__loc", geoLocationArray);
			}

			mainJson.put("application_user", authDataJson);


			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users";

			new BuiltCallBackgroundTask(this, BuiltControllers.LOGIN, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void register(HashMap<String, Object> userInfo, boolean isOfflineCall, BuiltResultCallBack callback) {
		try{

			JSONObject mainJson = new JSONObject();
			JSONObject userJson = new JSONObject();
			if(userInfo != null && userInfo.size() > 0){
				for(Entry<String, Object> entry : userInfo.entrySet()){
					userJson.put(entry.getKey(), entry.getValue());
				}

				if(geoLocationArray != null && geoLocationArray.length() > 0){
					userJson.put("__loc", geoLocationArray);
				}

				mainJson.put("application_user", userJson);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users";

				new BuiltCallBackgroundTask(this, BuiltControllers.REGISTER, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
			}else{

				throwExeception(callback, BuiltAppConstants.ErrorMessage_RegisteringUser);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}

	}


	private BuiltUser logout(boolean isOfflineCall, BuiltResultCallBack callback){ 
		try{
			JSONObject mainJson = new JSONObject();
			mainJson.put("_method",  BuiltAppConstants.RequestMethod.DELETE.toString());

			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/logout";

			new BuiltCallBackgroundTask(this, BuiltControllers.LOGOUT, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
		return this;
	}


	private void deactivate(boolean isOfflineCall, BuiltResultCallBack callback) {

		try{
			if(getUserUid() == null){
				throwExeception(callback, BuiltAppConstants.ErrorMessage_userIDIsNull);

			}else if(authToken == null){
				throwExeception(callback, BuiltAppConstants.ErrorMessage_AuthtokenIsNull);

			}else{
				JSONObject mainJson = new JSONObject();
				mainJson.put("_method",  BuiltAppConstants.RequestMethod.DELETE.toString());

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/" + getUserUid();


				new BuiltCallBackgroundTask(this, BuiltControllers.DELETEAPPLICATIONUSER, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);

			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void forgotPassword(String emailId, boolean isOfflineCall, BuiltResultCallBack callback){
		try{
			JSONObject mainJson  = new JSONObject();
			JSONObject valueJson = new JSONObject();

			valueJson.put("email", emailId);
			mainJson.put("application_user", valueJson);

			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/application/users/forgot_password/request_password_reset" ;

			new BuiltCallBackgroundTask(this, BuiltControllers.FORGOTPASSWORD, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTUSER.toString(), callback);
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void throwExeception(ResultCallBack callback, String errorMessage) {
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

					org.apache.http.Header header =  headerGroup_local.getCondensedHeader(localHeaders.getAllHeaders()[i].getName());
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
		json 				 = null;
		authToken 			 = null;
		userName 			 = null;
		firstName 			 = null;
		lastName 			 = null;
		email 				 = null;
		setUserUid(null);
		sessionJson 		 = null;
		applicationKey_local = null;
		applicationUid_local = null;

		headerGroup_local.clear();

		if(googleAuthData != null){
			googleAuthData.clear();
		}
	}

	private HashMap< String, Object> createHashResult(JSONObject jsonObj) {
		HashMap< String, Object> valueHash = new HashMap<String, Object>();
		JSONObject valueObject             = new JSONObject();
		valueObject                        = jsonObj;

		Iterator<?> iterator = valueObject.keys();
		while (iterator.hasNext()) {
			String hashKey = (String) iterator.next();
			try {
				Object value = valueObject.get(hashKey);

				if(value instanceof JSONObject){

					valueHash.put(hashKey, createHashResult((JSONObject) value));

				}else if(value instanceof JSONArray){
					HashMap< String, Object> valueArrayHash = new HashMap<String, Object>();
					int count = ((JSONArray)value).length();
					for(int i = 0; i < count; i++){
						valueArrayHash.put(hashKey, createHashResult(((JSONArray)value).optJSONObject(i)));
					}
					valueHash.put(hashKey, valueArrayHash);

				}else{
					valueHash.put(hashKey, value);
				}
			} catch (Exception e) {
				RawAppUtils.showLog("BuiltObject", "-createHashResult-catch|"+e);
			}
		}
		return valueHash;
	}
}

package com.raweng.built;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import com.raweng.built.utilities.RawAppUtils;

/**
 * To accept application user data in JSON format and parse it.
 * 
 * @author raw engineering, Inc
 *
 */
class BuiltApplicationUserModel {

	private  JSONObject mainJsonObject   	= null;
	private  JSONObject jsonObject 	        = null;

	protected  String authToken			    		 = null;
	protected  String userUid 						 = null;
	protected  String firstName			    		 = null;
	protected  String lastName 						 = null;
	protected  String userName 						 = null;
	protected  String email 						 = null;
	protected HashMap<String, Object> googleAuthData = null;


	protected BuiltApplicationUserModel(JSONObject object){
		try{

			mainJsonObject = object;

			jsonObject = mainJsonObject.opt("application_user") == null ? null : mainJsonObject.optJSONObject("application_user");

			authToken =  (String) (jsonObject.opt("authtoken") == null ? null : jsonObject.opt("authtoken"));

			if(jsonObject.opt("uid") != null){
				userUid = (String) jsonObject.opt("uid");
			}

			if(jsonObject.has("auth_data")){
				JSONObject authData = jsonObject.optJSONObject("auth_data");

				if(authData.has("google")){
					JSONObject googleJson = authData.optJSONObject("google");  
					JSONObject userProfileJson = googleJson.optJSONObject("user_profile");  
					googleAuthData = new HashMap<String, Object>();
					Iterator<String> iterator = userProfileJson.keys();
					while (iterator.hasNext()) {
						String key = iterator.next();
						googleAuthData.put(key, userProfileJson.optString(key));
					}

				}
			}

			if(jsonObject.opt("email") != null && (! jsonObject.opt("email").toString().equalsIgnoreCase("null"))){
				email =  (String) jsonObject.opt("email");
			}


			if((jsonObject.opt("first_name") != null) && (! jsonObject.opt("first_name").toString().equalsIgnoreCase("null"))){
				firstName = (String) jsonObject.opt("first_name");

			}else if((jsonObject.opt("given_name") != null) && (! jsonObject.opt("given_name").toString().equalsIgnoreCase("null"))){
				firstName = (String) jsonObject.opt("given_name");
			}

			if(jsonObject.opt("last_name") != null && (! jsonObject.opt("last_name").toString().equalsIgnoreCase("null"))){
				lastName =  (String) jsonObject.opt("last_name");

			}else if(jsonObject.opt("family_name") != null && (! jsonObject.opt("family_name").toString().equalsIgnoreCase("null"))){
				lastName =  (String) jsonObject.opt("family_name");
			}

			if(jsonObject.opt("username") != null && (! jsonObject.opt("username").toString().equalsIgnoreCase("null"))){
				userName =  (String) jsonObject.opt("username");

			}else if(jsonObject.opt("name") != null && (! jsonObject.opt("name").toString().equalsIgnoreCase("null"))){
				userName =  (String) jsonObject.opt("name");
			}


		}catch (Exception e) {
			RawAppUtils.showLog("BuiltApplicationUserModel", "---------parsing error catch block|" + e);
		}
	}

}

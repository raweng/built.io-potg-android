package com.raweng.built;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

/**
 * To parse application level JSON.
 * 
 * @author raw engineering, Inc
 *
 */
class ApplicationModel {

	HashMap<String, Object> applicationHashmap 			= null;
	HashMap<String, String> applicationVariableHashmap 	= null;
	String applicationName 					   			= null;
	String applicationUid			 		   			= null;
	String applicationKey 					   			= null;
	String accountName 						   			= null;

	private JSONObject applicationJson         			= null;

	ApplicationModel(JSONObject json) {

		applicationHashmap = new HashMap<String, Object>();

		applicationJson = json.optJSONObject("application");
		applicationName = applicationJson.optString("name");
		applicationUid  = applicationJson.optString("uid");
		applicationKey	= applicationJson.optString("api_key");
		accountName		= applicationJson.optString("account_name");	

		if(applicationJson.has("application_variables")){
			applicationVariableHashmap = new HashMap<String, String>();
			
			JSONObject applicationVariable = applicationJson.optJSONObject("application_variables");
			Iterator<String> iterator = applicationVariable.keys();
			while (iterator.hasNext()) {
				String key = iterator.next();
				applicationVariableHashmap.put(key, applicationVariable.optString(key));
			}
		}

		Iterator<String> iterator = json.keys();
		while (iterator.hasNext()){
			String key = iterator.next();
			applicationHashmap.put(key, json.opt(key));
		}
	}
}

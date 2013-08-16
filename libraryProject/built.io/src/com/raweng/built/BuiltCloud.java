package com.raweng.built;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;

/**
 * This class is used to make cloud related network calls.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltCloud {


	/**
	 * Makes a call to a cloud function.
	 * 
	 * @param logicId
	 * 					cloud logic id.
	 * 
	 * @param properties
	 * 			{@linkplain HashMap} contains properties , property name as key and its respective value as value. 
	 * 
	 * @param callback
	 * 					 {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 */
	public static void execute(String logicId, HashMap<String, Object> properties, BuiltResultCallBack callback) {
		try{
			if(logicId != null){

				JSONObject mainJson = new JSONObject();


				if(properties != null && properties.size() > 0){
					for (Entry<String, java.lang.Object> e : properties.entrySet()) {
						mainJson.put(e.getKey(), e.getValue());
					}
				}

				String URL = BuiltAppConstants.URLSCHEMA_HTTPS + BuiltAppConstants.URLCloud + "/" + logicId;

				new BuiltCallBackgroundTask(BuiltControllers.CLOUDCALL, URL, getHeaders().getAllHeaders(), mainJson, null, callController.BUILTCLOUD.toString(), callback);

			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_CloudLogicIdIsNull);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}

	}
	

	/**
	 * To cancel all {@link BuiltCloud} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTCLOUD.toString());
	}
	
	/**
	 * 
	 * 
	 ******************************************************/

	private static HeaderGroup getHeaders() {

		HeaderGroup mainHeaderGroup = new HeaderGroup();
		mainHeaderGroup.addHeader(new BasicHeader("application_api_key", Built.applicationKey));
		return mainHeaderGroup;
	}

	private static void throwExeception(BuiltResultCallBack callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.errorMessage(errorMessage);
		if(callback != null){
			callback.onRequestFail(error);
		}
	}

}

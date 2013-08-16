package com.raweng.built;

import java.util.ArrayList;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;

/**
 * To create, modify or delete roles on built.io server.
 * Roles are a sort of group containing users and other roles. 
 * So instead of giving permission to each user, could instead add those users to and assign permissions to the role. 
 * All users in the role would inherit permissions that the role receives.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltRole {

	private HeaderGroup headerGroup_local ;
	private String applicationKey_local ;
	private String applicationUid_local ;
	protected ArrayList<RoleObject> roleList  = new ArrayList<RoleObject>();
	protected JSONObject json = null;


	/**
	 * Creates a new instance of {@link BuiltRole} class.
	 */
	public BuiltRole(){
		headerGroup_local = new HeaderGroup();
	}


	/**
	 *  Sets the api key and Application uid for BuiltObject class instance.
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
	 * To fetch roles from server.
	 * 
	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * <p>
	 * <b> Note: </b> You can access role data using {@link #getRole(String)}.
	 */
	public void fetchRoles(BuiltResultCallBack callback) {
		try{
			JSONObject mainJson = new JSONObject();
			mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());

			String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_application_user_role/objects?include_user=true";

			new BuiltCallBackgroundTask(this, BuiltControllers.GETROLES, URL, getHeaders().getAllHeaders(), mainJson, null, callController.BUILTROLE.toString(), callback);
		}catch (Exception e) {
			BuiltError error = new BuiltError();
			error.errorMessage(e.toString());
			if(callback != null){
				callback.onRequestFail(error);
			}
		}
	}


	/**
	 * Get {@link RoleObject} with the name specified.
	 * 
	 * @param roleName 
	 * 					role name.
	 * 
	 * @return
	 * 			{@link RoleObject} object, so you can chain this call.
	 */
	public RoleObject getRole(String roleName){
		if(roleList.size() > 0){
			int count = roleList.size();
			for(int i = 0; i < count; i++){
				if(roleList.get(i).roleName.equalsIgnoreCase(roleName)){
					return roleList.get(i);
				}
			}
			return null;
		}else{
			return null;
		}
	}

	/**
	 * Check whether role with specified name exists in application.
	 * 
	 * @param roleName
	 * 					role name.
	 * 
	 * @return 
	 * 			returns true if role is exists else returns false.
	 */
	public boolean hasRole(String roleName){
		if(roleList.size() > 0){
			int count = roleList.size();
			for(int i = 0; i < count; i++){
				if(roleList.get(i).roleName.equalsIgnoreCase(roleName)){
					return true;
				}
			}
			return false;
		}else{
			return false;
		}
	}

	/**
	 * Returns the count of all Roles in application.
	 * 
	 */
	public int count(){

		return roleList.size();
	}


	/**
	 * Creates a {@link RoleObject} with specified name.
	 * 
	 * @param roleName
	 * 					role name.
	 * 
	 * @return 
	 * 			{@link RoleObject} object, so you can chain this call.
	 */
	public static RoleObject createRoleWithName(String roleName) {

		RoleObject roleObject = new RoleObject(roleName);
		roleObject.isCreate   = true;
		return roleObject;
	}

	/**
	 * To cancel all {@link BuiltRole} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTROLE.toString());
	}
	
	/**
	 * 
	 * Returns JSON representation of Object data.
	 * 
	 */
	public JSONObject toJSON(){
		return json;
	}
	

	/**************************************************************************************
	 * 
	 *
	 *
	 *
	 ****************************/

	protected HeaderGroup getHeaders(){
		HeaderGroup localHeaders    = headerGroup_local;
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

}
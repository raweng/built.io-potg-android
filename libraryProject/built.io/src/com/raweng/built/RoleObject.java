package com.raweng.built;


import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.RawAppUtils;

/**
 * To modify a role.
 * 
 * @author raw engineering, Inc
 *
 */
public class RoleObject {

	protected JSONArray userJsonArray;
	protected JSONArray roleJsonArray;
	protected BuiltACL builtACLUserObject 	= null;
	protected JSONObject resultJson 		= null;
	protected String roleName				= null;
	protected String roleUid 				= null;
	protected boolean isCreate  			= false;
	protected String ownerUid   			= null;

	protected JSONArray usersJsonArrayPUSH = null;
	protected JSONArray usersJsonArrayPULL = null;
	protected JSONArray rolesJsonArrayPUSH = null;
	protected JSONArray rolesJsonArrayPULL = null;
	private JSONArray geoLocationArray     = null;

	/**
	 * Create {@link RoleObject} with specified role name.
	 * 
	 * @param roleName
	 * 					 role name.
	 */
	RoleObject(String roleName){
		this.roleName = roleName;
		userJsonArray = new JSONArray();
		roleJsonArray = new JSONArray();

		usersJsonArrayPUSH = new JSONArray();
		usersJsonArrayPULL = new JSONArray();
		rolesJsonArrayPUSH = new JSONArray();
		rolesJsonArrayPULL = new JSONArray();
	}


	/**
	 * To set ACL on this object.
	 * 
	 * @param builtACL
	 * 					object of {@linkplain BuiltACL} class.
	 * 
	 * @return
	 * 			{@link RoleObject} object, so you can chain this call.
	 */
	public RoleObject setACL(BuiltACL builtACLInstance) {
		builtACLUserObject = builtACLInstance;
		return this;

	}


	/**
	 * Set role uid.
	 * 
	 * @param uid given uid will be set as a object role uid.
	 */
	public void setRoleUid(String uid){
		this.roleUid = uid;
		isCreate = false;

	}




	/**
	 * To add user in this role object.
	 * 
	 * @param userUid 
	 * 					user uid.
	 */
	public void addUser(String userUid) {
		usersJsonArrayPUSH.put(userUid);
	}

	/**
	 * To add role in this role object.
	 * 
	 * @param roleUid
	 * 				role uid.
	 * 			
	 */
	public void addRole(String roleUid) {
		rolesJsonArrayPUSH.put(roleUid);
	}

	/**
	 * To get user&#39;s uid belongs to this role.
	 * 
	 * @return
	 * 			array of user who  belongs to this role.
	 * 
	 */
	public String[] getUsers() {
		String[] users = null;
		try{
			if(userJsonArray != null){
				users = new String[userJsonArray.length()];
				int count = userJsonArray.length();
				for(int j = 0; j < count; j++){
					users[j] = userJsonArray.optString(j);
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("RoleObject", "-------catch----------getUsers|"+e);
		}
		return users;
	}

	/**
	 * To get sub-roles belongs to this role.
	 * 
	 * @return
	 * 			array of sub-roles.
	 */
	public String[] getRoles() {

		String[] roles = null;

		try{
			if(roleJsonArray != null){

				roles = new String[roleJsonArray.length()];
				int count = roleJsonArray.length();
				for(int j = 0; j < count; j++){
					roles[j] = roleJsonArray.optString(j);
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("RoleObject", "----------catch-------getRoles|"+e);
		}
		return roles;
	}

	/**
	 * To remove user from this role.
	 * 
	 * @param userName
	 *                user name.
	 * 
	 */
	public void removeUser(String userName){
		usersJsonArrayPULL.put(userName);
	}

	/**
	 *  To remove a sub-role from this role.
	 *  
	 * @param roleName
	 * 					sub role name.
	 */
	public void removeRole(String roleName){

		rolesJsonArrayPULL.put(roleName);
	}

	/**
	 * Whether the user is present in this role.
	 * 
	 * @param userUid
	 * 					  userUid - To check with.
	 * 
	 * @return
	 *			returns true, if user exists in this role else returns false.
	 * 
	 */
	public boolean hasUser(String userUid){
		try{
			if(userJsonArray != null){
				int count = userJsonArray.length();
				for(int i = 0; i < count; i++){
					if(userJsonArray.opt(i).equals(userUid)){
						return true;
					}
				}
			}
			return false;
		}catch (Exception e) {
			RawAppUtils.showLog("RoleObject", "-hasUser-catch|"+e);
			return false;
		}
	}

	/**
	 * To check whether the sub-role is present in this role or not.
	 * 
	 * @param roleUid
	 * 				 role uid.
	 * 
	 * @return
	 * 			returns true, if role exists else returns false.
	 */
	public boolean hasRole(String roleUid){
		try{
			if(roleJsonArray != null){
				int count = roleJsonArray.length();
				for(int i = 0; i < count; i++){
					if(roleJsonArray.opt(i).equals(roleUid)){
						return true;
					}
				}
			}
			return false;
		}catch (Exception e) {
			RawAppUtils.showLog("RoleObject", "-hasRole-catch|"+e);
			return false;
		}
	}

	/**
	 * Checks whether the logged-in user is owner of this role or not.
	 * 
	 * @return 
	 * 			returns true if current logged-in user is owner of this role else returns false.
	 */
	public boolean isOwner() {
		try{
			BuiltUser user  = new BuiltUser();
			user = BuiltUser.currentUser();
			if(user != null && user.getUserUid() != null && ownerUid != null){
				if(user.getUserUid().equalsIgnoreCase(ownerUid)){
					return true;
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("RoleObjects", "-----isOwner|"+e);
		}
		return false;
	}

	/**
	 * To set geo location for this role.
	 * 
	 * @param location
	 *               {@link BuiltLocation} instance 
	 * 
	 * @return
	 * 			{@link RoleObject} object, so you can chain this call.
	 */
	public RoleObject setLocation(BuiltLocation location) {

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
	 * Save role data in built.io server.
	 * 
	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 */
	public void save(BuiltResultCallBack callback) {

		save(callback, false);
	}


	/**
	 * Delete role from built.io server.
	 * 
	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 */
	public void destroy(BuiltResultCallBack callback){
		try{
			if(roleUid != null){
				JSONObject mainJson = new JSONObject();
				mainJson.put("_method", BuiltAppConstants.RequestMethod.DELETE.toString());

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_application_user_role/objects/" + roleUid;

				new BuiltCallBackgroundTask(this, BuiltControllers.DELETEROLE, URL, new BuiltRole().getHeaders().getAllHeaders(), mainJson, null, callController.BUILTROLROBJECT.toString(), callback);
			}else{

				throwExeception(callback, BuiltAppConstants.ErrorMessage_RoleUidIsNull);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}


	/**
	 * 
	 * Returns JSON representation of this {@link RoleObject} instance data.
	 */

	public JSONObject toJSON() {

		return resultJson;
	}

	/**
	 * To cancel all {@link RoleObject} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTROLROBJECT.toString());
	}

	

	//////////////////////////////////////////////////////////////

	private void save(BuiltResultCallBack callback, boolean isSaveCallOffline) {

		if(isCreate){
			try{
				JSONObject mainJson  = new JSONObject();
				JSONObject valueJson = new JSONObject();

				if(builtACLUserObject != null){
					JSONObject ACLObj = new JSONObject();

					if(builtACLUserObject.othersJsonObject.length() > 0){
						ACLObj.put("others",builtACLUserObject.othersJsonObject);
					}

					if(builtACLUserObject.userArray.length() > 0){
						ACLObj.put("users", builtACLUserObject.userArray);
					}

					if(builtACLUserObject.roleArray.length() > 0){
						ACLObj.put("roles", builtACLUserObject.roleArray);
					}

					valueJson.put("ACL", ACLObj);
				}

				valueJson.put("name", roleName);
				valueJson.put("users", usersJsonArrayPUSH);
				valueJson.put("roles", rolesJsonArrayPUSH);

				if(geoLocationArray != null && geoLocationArray.length() > 0){
					valueJson.put("__loc", geoLocationArray);
				}

				mainJson.put("object", valueJson);

				String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_application_user_role/objects";

				new BuiltCallBackgroundTask(this, BuiltControllers.CREATEROLE, URL, new BuiltRole().getHeaders().getAllHeaders(), mainJson, null, callController.BUILTROLROBJECT.toString(), callback);
			}catch (Exception e) {
				throwExeception(callback, e.toString());
			}

		}else{
			if(roleUid != null){
				try{
					JSONObject mainJson  = new JSONObject();
					JSONObject valueJson = new JSONObject();


					BuiltUser user     = new BuiltUser();
					user = BuiltUser.currentUser();

					if(builtACLUserObject != null){
						JSONObject ACLObj = new JSONObject();
						
						if(builtACLUserObject.othersJsonObject.length() > 0){
							ACLObj.put("others", builtACLUserObject.othersJsonObject);
						}
						
						if(builtACLUserObject.userArray.length() > 0){
							ACLObj.put("users", builtACLUserObject.userArray);
						}
						
						if(builtACLUserObject.roleArray.length() > 0){
							ACLObj.put("roles", builtACLUserObject.roleArray);
						}
						
						valueJson.put("ACL", ACLObj);
					}

					valueJson.put("name", roleName);

					JSONObject userJson          = new JSONObject();
					JSONObject pushUserValueJson = new JSONObject();
					JSONObject pullhUsersJson    = new JSONObject();
					JSONObject pushUsersJson     = new JSONObject();

					JSONObject roleJson      = new JSONObject();
					JSONObject pushRolesJson = new JSONObject();
					JSONObject pullRolesJson = new JSONObject();

					if(usersJsonArrayPUSH.length() > 0){

						pushUsersJson.put("data", usersJsonArrayPUSH);

						if(usersJsonArrayPULL.length() == 0){
							pushUserValueJson.put("PUSH", pushUsersJson);
							valueJson.put("users", pushUserValueJson);
						}

						usersJsonArrayPUSH = null;
						usersJsonArrayPUSH = new JSONArray();
					}

					if(usersJsonArrayPULL.length() > 0){
						pullhUsersJson.put("data", usersJsonArrayPULL);

						if(pushUsersJson.length() > 0){
							userJson.put("PUSH", pushUsersJson);
						}
						userJson.put("PULL", pullhUsersJson);

						valueJson.put("users", userJson);
						usersJsonArrayPULL = null;
						usersJsonArrayPULL = new JSONArray();
					}


					if(rolesJsonArrayPUSH.length() > 0){
						JSONObject pushRoleValueJson = new JSONObject();
						pushRolesJson.put("data", rolesJsonArrayPUSH);

						if(rolesJsonArrayPULL.length() == 0){
							pushRoleValueJson.put("PUSH", pushRolesJson);
							valueJson.put("roles", pushRoleValueJson);
						}
						rolesJsonArrayPUSH = null;
						rolesJsonArrayPUSH = new JSONArray();
					}

					if(rolesJsonArrayPULL.length() > 0){

						pullRolesJson.put("data", rolesJsonArrayPULL);

						roleJson.put("PULL", pullRolesJson);
						if(pushRolesJson.length() > 0){
							roleJson.put("PUSH", pushRolesJson);
						}

						valueJson.put("roles", roleJson);
						rolesJsonArrayPULL = null;
						rolesJsonArrayPULL = new JSONArray();
					}

					if(geoLocationArray != null && geoLocationArray.length() > 0){
						valueJson.put("__loc", geoLocationArray);
					}
					
					mainJson.put("_method", BuiltAppConstants.RequestMethod.PUT.toString());
					mainJson.put("object", valueJson);

					String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/built_io_application_user_role/objects/" + roleUid;

					if(user != null && user.getUserUid() != null){
						if(user.getUserUid().equalsIgnoreCase(ownerUid)){
							new BuiltCallBackgroundTask(this, BuiltControllers.UPDATEROLE, URL, new BuiltRole().getHeaders().getAllHeaders(), mainJson, null, callController.BUILTROLROBJECT.toString(), callback);
						}else{
							throwExeception(callback, BuiltAppConstants.ErrorMessage_NOTAUTHORISED);
						}
					}else{
						new BuiltCallBackgroundTask(this, BuiltControllers.UPDATEROLE, URL, new BuiltRole().getHeaders().getAllHeaders(), mainJson, null, callController.BUILTROLROBJECT.toString(), callback);
					}

				}catch (Exception e) {
					throwExeception(callback, e.toString());
				}
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_RoleUidIsNull);
			}
		}
	}

	private void throwExeception(BuiltResultCallBack callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.errorMessage(errorMessage);
		if(callback != null){
			callback.onRequestFail(error);
		}
	}

	protected void clearAll() {
		roleName 			= null;
		roleUid 			= null;

		userJsonArray 		= new JSONArray();
		roleJsonArray 	    = new JSONArray();

		resultJson          = new JSONObject();

		usersJsonArrayPUSH 	= new JSONArray();
		usersJsonArrayPULL 	= new JSONArray();
		rolesJsonArrayPUSH 	= new JSONArray();
		rolesJsonArrayPULL  = new JSONArray();
		geoLocationArray    = new JSONArray();

		isCreate = false;
		builtACLUserObject = null;
		ownerUid		   = null;

	}
}
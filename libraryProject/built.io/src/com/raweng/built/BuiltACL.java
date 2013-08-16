package com.raweng.built;

import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.RawAppUtils;

/**
 * A {@link BuiltACL} is used to control which users can access or modify a particular object. 
 * Each {@link BuiltObject} can have its own {@link BuiltACL} .
 * You can grant read and write permissions separately to specific users, to groups of users that belong to roles, 
 * or you can grant permissions to &#34;the public&#34; so that, for example, any logged-in user could read a particular object but only a particular set of users could write to that object. 
 * To specify an ACL to any user who is not logged into the system ,you can use special user uid, &#34;anonymous&#34;. 
 * Thus using this uid, you can assign permissions to anonymous users.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltACL {

	private JSONObject userJsonObject 	    = null;
	private JSONObject roleJsonObject 	    = null;
	protected JSONObject othersJsonObject 	= null;
	protected JSONArray userArray 			= null;
	protected JSONArray roleArray			= null;


	/**
	 * Creates a new {@link BuiltACL} instance with no permissions granted.
	 */
	public BuiltACL(){
		othersJsonObject 	= new JSONObject();
		userArray 			= new JSONArray();
		roleArray 			= new JSONArray();
	}

	/**
	 * Check whether public is allowed to read this object or not.
	 * 
	 * @return 
	 * 			returns true, if public is allowed to read this object else returns false.
	 */
	public boolean getPublicReadAccess(){
		try{
			if(othersJsonObject.has("read")){
				return othersJsonObject.optBoolean("read");
			}else{
				return false;
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getPublicReadAccess|" + e);
			return false;
		}
	}

	/**
	 * Set whether the public is allowed to read this object.
	 * 
	 * @param read
	 * 				boolean value.
	 * 
	 * @return
	 * 	 		{@link BuiltACL} object, so you can chain this call.
	 */
	public BuiltACL setPublicReadAccess(boolean read){
		try{
			othersJsonObject.put("read", read);
		}catch(Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------setPublicReadAccess|" + e);
		}
		return this;
	}

	/**
	 * Check whether public is allowed to write this object or not.
	 * 
	 * @return 
	 * 			returns true, if public is allowed to update this object else returns false.
	 */
	public boolean getPublicWriteAccess(){
		try{
			if(othersJsonObject.has("update")){
				return othersJsonObject.optBoolean("update");
			}else{
				return false;
			}
		}catch(Exception e){
			RawAppUtils.showLog("BuiltACL", "----------------getPublicWriteAccess|" + e);
			return false;
		}
	}


	/**
	 * Set whether the public is allowed to update this object.
	 * 
	 * @param update
	 *    				boolean value.
	 * 
	 * @return
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */
	public BuiltACL setPublicWriteAccess(boolean update){
		try{
			othersJsonObject.put("update", update);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------setPublicWriteAccess|" + e);
		}
		return this;
	}

	/**
	 * Check whether public is allowed to delete this object or not.
	 * 
	 * @return 
	 * 			true if public is allowed to delete this object else returns false.
	 * 
	 */
	public boolean getPublicDeleteAccess(){
		try{
			if(othersJsonObject.has("delete")){
				return othersJsonObject.optBoolean("delete");
			}else{
				return false;
			}
		}catch(Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getPublicDeleteAccess|" + e);
			return false;
		}
	}

	/**
	 * Set whether public is allowed to delete this object.
	 * 
	 * @param delete 
	 * 					boolean value. 
	 * 
	 * @return  
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */

	public BuiltACL setPublicDeleteAccess(boolean delete){

		try{
			othersJsonObject.put("delete", delete);
		}catch(Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------setPublicDeleteAccess|" + e);
		}
		return this;
	}

	/**
	 * Set whether the given user uid is allowed to read this object.
	 * 
	 * @param userUid
	 * 				  User&#39;s uid.
	 * 
	 * @param read
	 * 				  boolean value.	
	 * 
	 * @return
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */

	public BuiltACL setUserReadAccess(String userUid, boolean read) {
		try{
			userJsonObject = new JSONObject();
			int count = userArray.length();
			for(int i = 0; i < count; i++){
				JSONObject value = userArray.getJSONObject(i);
				if(value.get("uid").equals(userUid)){
					value.put("read", read);
					return this;
				}
			}
			userJsonObject.put("uid", userUid);
			userJsonObject.put("read", read);
			userArray.put(userJsonObject);
		}catch(Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------setReadAccess|" + e);
		}
		return this;

	}

	/**
	 * Check whether given user uid is allowed to read this object or not.
	 * 
	 * @param userUid
	 * 				User&#39;s uid.
	 * 	
	 * @return
	 *          returns true,if given user uid is allowed to read this object else returns false. 
	 */
	public boolean getUserReadAccess(String userUid){
		try{
			int count = userArray.length();
			for(int i = 0 ;i < count; i++){
				JSONObject valueJson = userArray.getJSONObject(i);
				if(valueJson.get("uid").equals(userUid)){
					if(valueJson.has("read")){
						return valueJson.getBoolean("read");
					}
				}
			}
		}catch(Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getReadAccess|" + e);
		}
		return false;
	}

	/**
	 * Set whether the given user uid is allowed to Update this object.
	 * 
	 * @param userUid
	 * 				  User&#39;s uid.
	 * 
	 * @param update
	 * 				  boolean value.	
	 * 
	 * @return
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */
	public BuiltACL setUserWriteAccess(String userUid, boolean update){
		try{
			userJsonObject = new JSONObject();
			int count = userArray.length();
			for(int i = 0; i < count; i++){
				JSONObject value = userArray.getJSONObject(i);
				if(value.get("uid").equals(userUid)){
					value.put("update", update);
					return this;
				}
			}
			userJsonObject.put("uid", userUid);
			userJsonObject.put("update", update);
			userArray.put(userJsonObject);
		}catch(Exception e) {
			RawAppUtils.showLog("BuiltACL", "---------------setWriteAccess-|" + e);
		}
		return this;
	}

	/**
	 * Check whether given user uid is allowed to update this object or not.
	 * 
	 * @param userUid
	 * 				User&#39;s uid.
	 * 	
	 * @return
	 *          returns true,if given user uid is allowed to update this object else returns false. 
	 *          
	 */
	public boolean getUserWriteAccess(String userUid) {
		try{
			int count = userArray.length();
			for(int i = 0; i < count; i++){
				JSONObject valueJson = userArray.getJSONObject(i);
				if(valueJson.get("uid").equals(userUid)){
					if(valueJson.has("update")){
						return valueJson.getBoolean("update");
					}
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getWriteAccess|" + e);
		}
		return false;
	}

	/**
	 * Set whether the given user uid is allowed to delete this object.
	 * 
	 * @param userUid
	 * 				  User&#39;s uid.
	 * 
	 * @param delete
	 * 				  boolean value.	
	 * 
	 * @return
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */
	public BuiltACL setUserDeleteAccess(String userUid, boolean delete){
		try{
			userJsonObject = new JSONObject();
			int count = userArray.length();
			for(int i = 0; i < count; i++){
				JSONObject value = userArray.getJSONObject(i);
				if(value.get("uid").equals(userUid)){
					value.put("delete", delete);
					return this;
				}
			}
			userJsonObject.put("uid", userUid);
			userJsonObject.put("delete", delete);
			userArray.put(userJsonObject);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------setDeleteAccess|" + e);
		}
		return this;
	}


	/**
	 * Check whether given user uid is allowed to delete this object or not.
	 * 
	 * @param userUid
	 * 				User&#39;s uid.
	 * 
	 * @return 
	 * 			returns true,if given user uid is allowed to delete this object else returns false.
	 */
	public boolean getUserDeleteAccess(String userUid){
		try{
			int count = userArray.length();
			for(int i = 0; i < count; i++){
				JSONObject valueJson = userArray.getJSONObject(i);
				if(valueJson.get("uid").equals(userUid)){
					if(valueJson.has("delete")){
						return valueJson.getBoolean("delete");
					}
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getDeleteAccess|" + e);
		}
		return false;
	}



	/**
	 * Set whether the given role uid is allowed to read this object.
	 * 
	 * @param roleUid
	 * 				  role&#39;s uid.
	 * 
	 * @param read
	 * 				  boolean value.	
	 * 
	 * @return
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */

	public BuiltACL setRoleReadAccess(String roleUid,boolean read) {
		try{
			roleJsonObject = new JSONObject();
			int count = roleArray.length();
			for(int i = 0; i < count; i++){
				JSONObject value = roleArray.getJSONObject(i);
				if(value.get("uid").equals(roleUid)){
					value.put("read", read);
					return this;
				}
			}
			roleJsonObject.put("uid", roleUid);
			roleJsonObject.put("read", read);
			roleArray.put(roleJsonObject);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------setRoleReadAccess|" + e);
		}
		return this;
	}

	/**
	 * Check whether given role uid is allowed to read this object or not.
	 * 
	 * @param roleUid
	 * 				role&#39;s uid.
	 * 	
	 * @return
	 *          returns true,if given role uid is allowed to read this object else returns false. 
	 */
	public boolean getRoleReadAccess(String roleUid){
		try{
			int count = roleArray.length();
			for(int i = 0; i < count; i++){
				JSONObject valueJson = roleArray.getJSONObject(i);
				if(valueJson.get("uid").equals(roleUid)){
					if(valueJson.get("uid").equals(roleUid)){
						if(valueJson.has("read")){
							return valueJson.getBoolean("read");
						}
					}
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getRoleReadAccess|" + e);
		}
		return false;
	}


	/**
	 * Set whether the given role uid is allowed to Update this object.
	 * 
	 * @param roleUid
	 * 				  role&#39;s uid.
	 * 
	 * @param update
	 * 				  boolean value.	
	 * 
	 * @return
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */
	public BuiltACL setRoleWriteAccess(String roleUid,boolean update){
		try{
			roleJsonObject = new JSONObject();
			int count = roleArray.length();
			for(int i = 0; i < count; i++){
				JSONObject value = roleArray.getJSONObject(i);
				if(value.get("uid").equals(roleUid)){
					value.put("update", update);
					return this;
				}
			}
			roleJsonObject.put("uid", roleUid);
			roleJsonObject.put("update", update);
			roleArray.put(roleJsonObject);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "---------------setRoleWriteAccess-|" + e);
		}
		return this;
	}

	/**
	 * Check whether given role uid is allowed to update this object or not.
	 * 
	 * @param roleUid
	 * 				role&#39;s uid.
	 * 	
	 * @return
	 *          returns true,if given role uid is allowed to update this object else returns false. 
	 *          
	 */
	public boolean getRoleWriteAccess(String roleUid) {
		try{
			int count = roleArray.length();
			for(int i = 0; i < count; i++){
				JSONObject valueJson = roleArray.getJSONObject(i);
				if(valueJson.get("uid").equals(roleUid)){
					if(valueJson.has("update")){
						return valueJson.getBoolean("update");
					}
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getRoleWriteAccess|" + e);
		}
		return false;
	}




	/**
	 * Set whether the given role uid is allowed to delete this object.
	 * 
	 * @param roleUid
	 * 				  role&#39;s uid.
	 * 
	 * @param delete
	 * 				  boolean value.	
	 * 
	 * @return
	 * 			{@link BuiltACL} object, so you can chain this call.
	 */
	public BuiltACL setRoleDeleteAccess(String roleUid, boolean delete){
		try{
			roleJsonObject = new JSONObject();
			int count = roleArray.length();
			for(int i = 0; i < count; i++){
				JSONObject value = roleArray.getJSONObject(i);
				if(value.get("uid").equals(roleUid)){
					value.put("delete", delete);
					return this;
				}
			}
			roleJsonObject.put("uid", roleUid);
			roleJsonObject.put("delete", delete);
			roleArray.put(roleJsonObject);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------setRoleDeleteAccess|" + e);
		}
		return this;
	}


	/**
	 * Check whether given role uid is allowed to delete this object or not.
	 * 
	 * @param roleUid
	 * 				role&#39;s uid.
	 * 
	 * @return 
	 * 			returns true,if given role uid is allowed to delete this object else returns false.
	 */
	public boolean getRoleDeleteAccess(String roleUid){
		try{
			int count = roleArray.length();
			for(int i = 0; i < count; i++){
				JSONObject valueJson = roleArray.getJSONObject(i);
				if(valueJson.get("uid").equals(roleUid)){
					if(valueJson.has("delete")){
						return valueJson.getBoolean("delete");
					}
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------getRoleDeleteAccess|" + e);
		}
		return false;
	}




	/*************************************************************************************************
	 * 
	 ************  Private Methods  *******************
	 * 
	 ****************************************************************************************/



	protected static BuiltACL setAcl(JSONObject json) {

		BuiltACL builtACL = new BuiltACL();

		try {
			if(json != null && json.length() > 0){
				if(json.has("users")){
					JSONArray userArray =json.optJSONArray("users");
					int count = userArray.length();

					for(int i = 0; i < count; i++){

						JSONObject userJsonObject = userArray.optJSONObject(i);
						JSONObject userValueObject = new JSONObject();
						userValueObject.put("uid", userJsonObject.opt("uid"));

						if(userJsonObject.has("delete")){
							userValueObject.put("delete", userJsonObject.opt("delete"));
						}

						if(userJsonObject.has("read")){
							userValueObject.put("read", userJsonObject.opt("read"));
						}

						if(userJsonObject.has("update")){
							userValueObject.put("update", userJsonObject.opt("update"));
						}

						builtACL.userArray.put(userValueObject);
					}

				}


				if(json.has("roles")){
					JSONArray rolesArray =json.optJSONArray("roles");
					int count = rolesArray.length();

					for(int i = 0; i < count; i++){

						JSONObject roleJsonObject = rolesArray.optJSONObject(i);
						JSONObject roleValueObject = new JSONObject();
						roleValueObject.put("uid", roleJsonObject.opt("uid"));

						if(roleJsonObject.has("delete")){
							roleValueObject.put("delete", roleJsonObject.opt("delete"));
						}

						if(roleJsonObject.has("read")){
							roleValueObject.put("read", roleJsonObject.opt("read"));
						}

						if(roleJsonObject.has("update")){
							roleValueObject.put("update", roleJsonObject.opt("update"));
						}

						builtACL.roleArray.put(roleValueObject);
					}

				}

				if(json.has("others")){
					JSONObject others = (JSONObject) json.opt("others");


					if(others.has("delete")){
						builtACL.othersJsonObject.put("delete", others.opt("delete"));
					}

					if(others.has("read")){
						builtACL.othersJsonObject.put("read", others.opt("read"));
					}

					if(others.has("update")){
						builtACL.othersJsonObject.put("update", others.opt("update"));
					}
				}
			}

		} catch (Exception e) {
			RawAppUtils.showLog("BuiltACL", "----------------SetAcl|" + e);

		}


		return builtACL;
	}
}

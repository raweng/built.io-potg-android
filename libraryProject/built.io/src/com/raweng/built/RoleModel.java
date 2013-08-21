package com.raweng.built;

import org.json.JSONArray;
import org.json.JSONObject;

public class RoleModel {

	private String roleName;
	private String uid;
	RoleObject roleObjectInstance;
	JSONObject roleJson;
	
	protected String[] tags					   = null;
	protected BuiltACL builtACLInstance 	   = null;
	
	private JSONArray tagsArray = null;

	RoleModel(JSONObject responseJson, boolean isFromRolesModel){

		if(! isFromRolesModel){
			roleJson = responseJson.optJSONObject("object");

		}else{
			roleJson = responseJson;
		}


		JSONArray usersArray = new JSONArray();
		JSONArray rolesArray = new JSONArray();


		usersArray = roleJson.optJSONArray("users");
		rolesArray = roleJson.optJSONArray("roles");

		roleName = roleJson.optString("name");
		uid      = roleJson.optString("uid");
		
		tagsArray = (JSONArray) roleJson.opt("tags");
		if(tagsArray.length() > 0){
			int count = tagsArray.length();
			tags = new String[count];
			for(int i = 0; i < count; i++){
				tags[i] = (String) tagsArray.opt(i);
			}
		}
		
		if(roleJson.has("ACL")){

			builtACLInstance = BuiltACL.setAcl(roleJson.optJSONObject("ACL"));
		}

		roleObjectInstance 					  = new RoleObject(roleName);
		roleObjectInstance.roleName 		  = roleName;
		roleObjectInstance.roleUid 			  = uid;
		roleObjectInstance.userJsonArray	  = usersArray;
		roleObjectInstance.roleJsonArray 	  = rolesArray;
		roleObjectInstance.resultJson 		  = responseJson;
		roleObjectInstance.builtACLUserObject = builtACLInstance;

	}

}

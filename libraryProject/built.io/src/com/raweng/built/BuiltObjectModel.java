package com.raweng.built;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.RawAppUtils;

/**
 * To accept BuiltObject related data in JSON format and parse it.
 * 
 * @author raw engineering, Inc
 *
 */
class BuiltObjectModel{

	protected JSONObject jsonObject;
	protected String objectUid;
	protected String ownerEmailId 			   = null;
	protected String ownerUid 				   = null;
	protected HashMap<String, Object> ownerMap = null;
	protected String[] tags					   = null;
	protected BuiltACL builtACLInstance 	   = null;
	
	private JSONArray tagsArray = null;


	protected BuiltObjectModel(JSONObject jsonObj, String objectUid, boolean isFromObjectsModel ,boolean isFromCache, boolean isFromDeltaResponse) {
		try{
			this.objectUid = objectUid;
			if(isFromObjectsModel){
				jsonObject = jsonObj;
				this.objectUid = (String) (jsonObject.isNull("uid") == true ? " " : jsonObject.opt("uid"));
			}else{

				if(isFromCache){
					jsonObject = jsonObj.opt("response") == null ? null : jsonObj.optJSONObject("response");
				}else{
					jsonObject = jsonObj;
				}

				if(isFromDeltaResponse){
					this.objectUid = (String) (jsonObject.isNull("uid") == true ? " " : jsonObject.opt("uid"));
				}else{
					jsonObject = jsonObject.opt("object") == null ? null : jsonObject.optJSONObject("object");
				}
			}

			if(jsonObject.has("uid")){
				this.objectUid = (String) (jsonObject.isNull("uid") == true ? " " : jsonObject.opt("uid"));
			}
			
			tagsArray = (JSONArray) jsonObject.opt("tags");
			if(tagsArray.length() > 0){
				int count = tagsArray.length();
				tags = new String[count];
				for(int i = 0; i < count; i++){
					tags[i] = (String) tagsArray.opt(i);
				}
			}

			if(jsonObject.has("_owner") && (jsonObject.opt("_owner") != null) && (! jsonObject.opt("_owner").toString().equalsIgnoreCase("null")) ){
				JSONObject ownerObject = jsonObject.optJSONObject("_owner");
				if(ownerObject.has("email")  && ownerObject.opt("email") != null){
					ownerEmailId = (String) ownerObject.opt("email");
				}

				if(ownerObject.has("uid")  && ownerObject.opt("uid") != null){
					ownerUid = ownerObject.opt("uid").toString();
				}
				JSONObject owner = jsonObject.optJSONObject("_owner");
				Iterator<String> iterator = owner.keys();
				ownerMap = new HashMap<String, Object>();
				while (iterator.hasNext()) {
					String key = iterator.next();
					ownerMap.put(key, owner.optString(key));
				}
			}

			if(jsonObject.has("ACL")){

				builtACLInstance = BuiltACL.setAcl(jsonObject.optJSONObject("ACL"));
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObjectModel", "---------------------BuiltObjectModel---------err|" + e);
		}
	}
}
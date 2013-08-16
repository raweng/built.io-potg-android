package com.raweng.built;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.RawAppUtils;

/**
 * To parse and format built object.
 * 
 * @author raw engineering, Inc
 *
 */

class BuiltObjectsModel{

	protected JSONObject jsonObject;
	protected String classUid;
	protected List<java.lang.Object> objectList;



	protected BuiltObjectsModel(JSONObject jsonObj,String classUid, boolean isFromCache){
		try {
			if (isFromCache){
				this.jsonObject = (jsonObj.opt("response") == null ? null : jsonObj.optJSONObject("response"));
			}else{
				this.jsonObject = jsonObj;
			}

			this.classUid   = classUid;
			objectList      = new ArrayList<java.lang.Object>();

			JSONArray objectsArray =  jsonObject.opt("objects") == null ? null : jsonObject.optJSONArray("objects");

			if(objectsArray != null && objectsArray.length() > 0){
				int count = objectsArray.length();
				for(int i = 0; i < count; i++){
					BuiltObjectModel builtObjectModelObject = new BuiltObjectModel(objectsArray.optJSONObject(i), null, true, isFromCache, false);
					objectList.add(builtObjectModelObject);
				}
			}
		}catch (Exception localException){
			RawAppUtils.showLog("BuiltObjectsModel", "----------------------parsing error|" + localException);
		}
	}
}



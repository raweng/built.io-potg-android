package com.raweng.built;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.RawAppUtils;

/**
 * To parse and format class schema JSON.
 * 
 *   @author raw engineering, Inc
 */
class ClassModel {

	protected ArrayList<HashMap<String, java.lang.Object>> classSchemaList = null; 

	private JSONObject jsonObject;

	protected ClassModel(JSONObject jsonObj , boolean isFromClassesModel, boolean isFromCache) {
		try{
			if(isFromClassesModel){
				jsonObject = jsonObj;
			}else{

				if (isFromCache){
					this.jsonObject = (jsonObj.opt("response") == null ? null : jsonObj.optJSONObject("response"));
				}else{
					this.jsonObject = jsonObj;
				}
				jsonObject = jsonObj.opt("class") == null ? null : jsonObj.optJSONObject("class");
			}

			if(! jsonObject.isNull("schema")){

				classSchemaList = new ArrayList<HashMap<String, java.lang.Object>>(); 

				JSONObject jsonSchemaObject = new JSONObject();
				JSONArray classSchemaArray =  jsonObject.opt("schema") == null ? null : jsonObject.optJSONArray("schema");

				int count = classSchemaArray.length();
				for(int i = 0; i < count; i++){
					HashMap<String, java.lang.Object> classSchemaHashMap = new HashMap<String, java.lang.Object>();
					jsonSchemaObject = classSchemaArray.getJSONObject(i);
					if(jsonSchemaObject != null){
						Iterator childKeys = jsonSchemaObject.keys();
						while(childKeys.hasNext()){
							String childkey = (String) childKeys.next();
							classSchemaHashMap.put(childkey, jsonSchemaObject.opt(childkey) == null ? null : jsonSchemaObject.opt(childkey));
						}
					}
					classSchemaList.add(classSchemaHashMap);
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltClassModel", "---------parsing err catch block|" + e);
		}
	}

}

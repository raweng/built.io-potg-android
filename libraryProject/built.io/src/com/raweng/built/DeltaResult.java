package com.raweng.built;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.RawAppUtils;

/**
 * Helper class for parsing the result of {@link BuiltDelta}.
 * 
 * @author  raw engineering, Inc
 *
 */
public class DeltaResult {

	private List<BuiltObject> createdAtObjectList;
	private List<BuiltObject> updatedAtObjectList;
	private List<BuiltObject> deletedAtObjectList;

	private JSONArray createdAtJsonArray ;
	private JSONArray updatedAtJsonArray ;
	private JSONArray deletedAtJsonArray ;
	private JSONObject mainJson;

	DeltaResult() {}

	DeltaResult(JSONObject jsonobject) {
		try{
			mainJson =  jsonobject.opt("objects") == null ? null : jsonobject.optJSONObject("objects");


			if(mainJson.has("created_at")){
				createdAtObjectList = new ArrayList<BuiltObject>();
				createdAtJsonArray  = mainJson.optJSONArray("created_at");
				if(createdAtJsonArray != null){
					int count = createdAtJsonArray.length();
					for(int i = 0; i < count; i++){

						BuiltObjectModel builtObjectModelObject = new BuiltObjectModel(createdAtJsonArray.optJSONObject(i),null, false, false, true);
						BuiltObject builtObject         = new BuiltObject(builtObjectModelObject.objectUid);
						builtObject.resultJson          = builtObjectModelObject.jsonObject;
						builtObject.ownerEmailId 		= builtObjectModelObject.ownerEmailId;
						builtObject.ownerUid     		= builtObjectModelObject.ownerUid;
						builtObject.owner		 		= builtObjectModelObject.ownerMap;
						builtObject.uid		   			= builtObjectModelObject.objectUid;
						builtObject.builtACLUserObject 	= builtObjectModelObject.builtACLInstance;
						builtObject.setTags(builtObjectModelObject.tags);

						createdAtObjectList.add(builtObject);
					}
				}
			}


			if(mainJson.has("updated_at")){
				updatedAtObjectList = new ArrayList<BuiltObject>();
				updatedAtJsonArray  = mainJson.optJSONArray("updated_at");
				if(createdAtJsonArray != null){
					int count = updatedAtJsonArray.length();
					for(int i = 0; i < count; i++){

						BuiltObjectModel  builtObjectModelObject = new BuiltObjectModel(updatedAtJsonArray.optJSONObject(i),null, false, false, true);
						BuiltObject builtObject = new BuiltObject(builtObjectModelObject.objectUid);
						builtObject.resultJson = builtObjectModelObject.jsonObject;
						builtObject.ownerEmailId 		= builtObjectModelObject.ownerEmailId;
						builtObject.ownerUid     		= builtObjectModelObject.ownerUid;
						builtObject.owner		 		= builtObjectModelObject.ownerMap;
						builtObject.uid		   			= builtObjectModelObject.objectUid;
						builtObject.builtACLUserObject 	= builtObjectModelObject.builtACLInstance;
						builtObject.setTags(builtObjectModelObject.tags);
						updatedAtObjectList.add(builtObject);
					}
				}
			}


			if(mainJson.has("deleted_at")){
				deletedAtObjectList = new ArrayList<BuiltObject>();
				deletedAtJsonArray  = mainJson.optJSONArray("deleted_at");
				if(deletedAtJsonArray != null){
					int count = deletedAtJsonArray.length();
					for(int i = 0; i < count; i++){

						BuiltObjectModel  builtObjectModelObject = new BuiltObjectModel(deletedAtJsonArray.optJSONObject(i),null, false, false, true);
						BuiltObject builtObject = new BuiltObject(builtObjectModelObject.objectUid);
						builtObject.resultJson = builtObjectModelObject.jsonObject;
						builtObject.ownerEmailId 		= builtObjectModelObject.ownerEmailId;
						builtObject.ownerUid     		= builtObjectModelObject.ownerUid;
						builtObject.owner		 		= builtObjectModelObject.ownerMap;
						builtObject.uid		   			= builtObjectModelObject.objectUid;
						builtObject.builtACLUserObject 	= builtObjectModelObject.builtACLInstance;
						builtObject.setTags(builtObjectModelObject.tags);
						deletedAtObjectList.add(builtObject);
					}
				}
			}

		}catch (Exception e) {
			RawAppUtils.showLog("deltaResult", "-----------DeltaResult|" + e);
		}
	}

	/**
	 * To get all created objects returned by {@link BuiltDelta}.
	 * 
	 * @return 
	 * 			list of {@link BuiltObject} instances created on and after given time or 
	 * 			null if network call not executed successfully.
	 */

	public List<BuiltObject> createdAt(){

		return createdAtObjectList;
	}

	/**
	 * To get all updated objects returned by {@link BuiltDelta}.
	 * 
	 * @return 
	 * 			list of {@link BuiltObject} instances updated on and after given time or 
	 * 			null if network call not executed successfully.
	 */
	public List<BuiltObject> updatedAt(){

		return updatedAtObjectList;
	}

	/**
	 * To get all deleted objects returned by {@link BuiltDelta}.
	 * 
	 * @return 
	 * 			list of {@link BuiltObject} instances deleted on and after given time or 
	 * 			null if network call not executed successfully.
	 */
	public List<BuiltObject> deletedAt(){

		return deletedAtObjectList;
	}

	/**
	 * To get all created/updated/deleted objects returned by {@link BuiltDelta}.
	 * 
	 * @return {@link HashMap} object contains {@link ArrayList} of all created/updated/deleted {@link BuiltObject}.
	 * Hashmap keys:-
	 * <li>
	 * createdAt :-  Using this key you can fetch {@link ArrayList} of all created object.
	 * </li>
	 *  <li>
	 * updatedAt :-  Using this key you can fetch {@link ArrayList} of all updated object.
	 * </li>
	 *  <li>
	 * deletedAt :-  Using this key you can fetch {@link ArrayList} of all deleted object.
	 * </li>
	 */
	public HashMap<String, List<BuiltObject>> All() {

		HashMap<String, List<BuiltObject>> map = new HashMap<String, List<BuiltObject>>();
		map.put("createdAt", createdAtObjectList);
		map.put("updatedAt", updatedAtObjectList);
		map.put("deletedAt", deletedAtObjectList);
		return map;

	}


	/**
	 * 
	 * Returns JSON representation of this {@link DeltaResult} instance data.
	 * 
	 */

	public JSONObject toJSON() {

		return mainJson;
	}

}


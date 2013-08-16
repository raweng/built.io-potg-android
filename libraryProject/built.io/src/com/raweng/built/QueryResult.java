package com.raweng.built;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Helper class for parsing the result of {@link BuiltQuery} 
 * 
 * @author raw engineering, Inc
 *
 */
public class QueryResult{

	JSONObject receiveJson = new JSONObject();
	private int totalCount = 0;
	private int count = 0;
	private List<BuiltObject> resultObjects;
	private List<BuiltObject> schema = new ArrayList<BuiltObject>();
	
	protected resultStatus status = resultStatus.FROM_NETWORK;


	/**
	 * Returns count of all objects in a class.
	 */
	public int getTotalCount(){
		return totalCount;
	}

	/**
	 * Returns object count that match the given conditions.
	 */
	public int getCount(){
		return count;
	}

	/**
	 * Returns {@link BuiltObject} objects list.
	 */
	public List<BuiltObject> getResultObjects(){
		return resultObjects;
	}


	/**
	 * Returns class&#39;s schema if call to fetch schema executed successfully.
	 * 
	 */
	public List<BuiltObject> getSchema(){
		return schema;
	}

	/**
	 * Returns query response status.
	 * <br>
	 *  It can be "FROM_CACHE" if query response fetch from cache else returns "FROM_NETWORK".
	 */
	public resultStatus getResponseStatus(){
		return status;
	}



	QueryResult(JSONObject jsonobject){
		receiveJson = jsonobject;
	}


	QueryResult() {}
	
	/**
	 * 
	 * Returns JSON representation of Object data.
	 * 
	 */
	public JSONObject toJSON(){
		return receiveJson ;
	}



	/******************************************************************
	 * 
	 * 
	 * 
	 *
	 ********************/

	protected static enum resultStatus{

		FROM_CACHE, FROM_NETWORK;
	}

	protected void setJSON(JSONObject jsonobject,List<BuiltObject> objectList) {
		receiveJson = jsonobject;
		resultObjects = objectList;
		try{
			if(receiveJson != null){
				if(receiveJson.has("schema")){
					JSONArray jsonarray = new JSONArray();
					jsonarray  = receiveJson.getJSONArray("schema");
					if(jsonarray != null){
						int count = jsonarray.length();
						for(int i = 0; i < count; i++){
							BuiltObjectModel  builtObjectModelObject = new BuiltObjectModel(jsonarray.getJSONObject(i),null, false, false, true);
							BuiltObject builtObject 		= new BuiltObject(builtObjectModelObject.objectUid);
							builtObject.resultJson 			= builtObjectModelObject.jsonObject;
							builtObject.ownerEmailId 		= builtObjectModelObject.ownerEmailId;
							builtObject.ownerUid     		= builtObjectModelObject.ownerUid;
							builtObject.owner		 		= builtObjectModelObject.ownerMap;
							builtObject.builtACLUserObject 	= builtObjectModelObject.builtACLInstance;
							builtObject.setTags(builtObjectModelObject.tags);

							schema.add(builtObject);
						}
					}
				}

				if(receiveJson.has("count")){
					count = receiveJson.getInt("count");
				}

				if(receiveJson.has("objects")){
					totalCount = receiveJson.getInt("objects");
				}
			}


		}catch(Exception e){
			BuiltError error = new BuiltError();
			error.errorMessage(e.toString());
		}
	}

}


package com.raweng.built;

import org.json.JSONArray;
import org.json.JSONObject;

public class UploadedFileModel {

	String uploadedUid;
	String contentType;
	String fileSize;
	String fileName;
	String uploadUrl;
	String[] tags;
	JSONObject json;
	int totalCount = 0;
	int count = 0;
	protected BuiltACL builtACLInstance = null;

	public UploadedFileModel(JSONObject responseJSON, boolean isArray) {

		if(isArray){
			json = responseJSON;
		}else{
			json = responseJSON.optJSONObject("upload");
		}

		uploadedUid = (String) json.opt("uid");
		contentType = (String) json.opt("content_type");
		fileSize = (String) json.opt("file_size");
		fileName = (String) json.opt("filename");
		uploadUrl = (String) json.opt("url");

		if(json.optString("tags").isEmpty()){
			JSONArray tagsArray =  (JSONArray) json.opt("tags");
			if(tagsArray.length() > 0){
				int count = tagsArray.length();
				tags = new String[count];
				for(int i = 0; i < count; i++){
					tags[i] = (String) tagsArray.opt(i);
				}
			}
		}

		if(json.has("ACL")){

			builtACLInstance = BuiltACL.setAcl(json.optJSONObject("ACL"));
		}

		if(responseJSON.has("count")){
			count = responseJSON.optInt("count");
		}

		if(responseJSON.has("objects")){
			totalCount = responseJSON.optInt("objects");
		}

	}


}

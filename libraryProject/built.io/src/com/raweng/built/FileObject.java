package com.raweng.built;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.array;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;

/**
 * Helper class to upload a single file with ACL and tags to built.io.
 * 
 * @author raw engineering, Inc
 *
 */
public class FileObject{


	protected String uploadUid    = null;
	protected String contentType  = null;
	protected String fileSize     = null;
	protected String fileName     = null;
	protected String uploadUrl    = null;
	protected JSONObject json;



	String mediaFilePath = null;
	private String URL;


	protected JSONArray tagsArray         = null;
	protected BuiltACL builtACLUserObject = null;

	private HeaderGroup headerGroup_local;
	private String applicationKey_local;
	private String applicationUid_local;

	private boolean isCallForUpload = false;
	private boolean isCallToFetch	= true;


	/**
	 * {@link FileObject} instance.
	 * 
	 */
	public FileObject(){
		headerGroup_local  = new HeaderGroup();
	}


	/**
	 *  Sets the api key and application uid for {@link FileObject} instance.
	 *  <br>
	 *  Scope is limited to this object only. 
	 *  
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
		setHeader("application_api_key", applicationKey_local );
	}

	/**
	 * To set headers for built.io rest calls.
	 * 
	 * <br>
	 * Scope is limited to this object only.
	 *  
	 * @param key 
	 * 				header name.
	 * @param value 	
	 * 				header value against given header name.
	 * 
	 */
	public void setHeader(String key, String value){

		if(key != null && value != null){
			headerGroup_local.addHeader(new BasicHeader(key, value));
		}
	}


	/**
	 * Remove a header for a given key from headers.
	 *  <br>
	 * Scope is limited to this object only.
	 * 
	 * @param key
	 * 			  header key for which to remove the header value.
	 * 
	 */
	public  void removeHeader(String key){
		if(headerGroup_local.containsHeader(key)){
			org.apache.http.Header header =  headerGroup_local.getCondensedHeader(key);
			headerGroup_local.removeHeader(header);
		}
	}


	/**
	 * To set uid of media file which is uploaded on built.io server. 
	 * 
	 * @param uploadUid
	 * 					upload uid.
	 */
	public void setUid(String uploadUid) {
		this.uploadUid = uploadUid;
	}

	/**
	 * To set file path.
	 * 
	 * @param filePath
	 * 					valid media file path.
	 * 
	 */
	public void setFile(String filePath) {
		mediaFilePath = filePath;
	}

	/**
	 * To set tags for this object.
	 * 
	 * @param tags
	 * 				array of tag. 
	 * 
	 * @return  
	 * 			{@link FileObject} object, so you can chain this call.
	 */
	public FileObject setTags(String[] tags){
		if(tagsArray == null){
			tagsArray = new JSONArray();
		}
		if(tags != null){
			for(String tag : tags){
				tagsArray.put(tag);
			}
		}
		return this;
	}



	/**
	 * Returns tags of this object.
	 * 
	 * 
	 * @return  
	 * 			{@link array} of tags which belongs to this media file instance.
	 */
	public String[] getTags(){
		if(tagsArray != null){

			String [] tags = new String[tagsArray.length()];
			for(int i = 0; i < tags.length; i++){
				tags[i] = tagsArray.optString(i);
			}

			return tags;

		}else{
			return null;
		}
	}

	/**
	 * To set ACL on this object.
	 * 
	 * @param builtACL
	 * 					object of {@linkplain BuiltACL} class.
	 * 
	 * @return
	 * 			{@link FileObject} object, so you can chain this call.
	 */
	public FileObject setACL(BuiltACL builtACLInstance) {
		builtACLUserObject = builtACLInstance;
		return this;

	}

	/**
	 * Fetches an object.
	 * 
	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return
	 * 				{@link FileObject} object, so you can chain this call.
	 */
	public FileObject fetch(BuiltResultCallBack callback) {
		if(uploadUid == null){
			if(callback != null){
				BuiltError error = new BuiltError();
				error.errorMessage(BuiltAppConstants.ErrorMessage_UploadUidIsNull);
				callback.onError(error);
			}
		}else{
			try{
				URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads/" + uploadUid;

				JSONObject mainJson = new JSONObject();
				mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());

				new BuiltCallBackgroundTask(this, BuiltControllers.GETUPLOADEDFILE, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, null, callback);

			}catch(Exception e) {
				throwExeception(callback, e.toString());
			}
		}
		return this;
	}

	/**
	 * Removes the media file with specified uploaded uid from built.io server.
	 * 
	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return
	 * 				{@link FileObject} object, so you can chain this call.
	 */
	public FileObject destroy(BuiltResultCallBack callback) {
		if(uploadUid == null){
			if(callback != null){
				BuiltError error = new BuiltError();
				error.errorMessage(BuiltAppConstants.ErrorMessage_UploadUidIsNull);
				callback.onRequestFail(error);
			}
		}else{
			try{
				URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads/" + uploadUid;

				JSONObject mainJson = new JSONObject();
				mainJson.put("_method", BuiltAppConstants.RequestMethod.DELETE.toString());

				if(getHeaders(headerGroup_local).getAllHeaders().length > 0){
					new BuiltCallBackgroundTask(this, BuiltControllers.DELETEUPLOADEDFILE, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, null, callback);
				}else{
					throwExeception(callback, BuiltAppConstants.ErrorMessage_CalledBuiltDefaultMethod);
				}
			}catch(Exception e) {
				throwExeception(callback, e.toString());
			}
		}
		return this;
	}

	/**
	 * Save {@link FileObject} instance on built.io server.
	 * 
	 * @param callback
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return
	 * 				{@link FileObject} object, so you can chain this call.
	 */
	public FileObject save(BuiltResultCallBack callback) {

		try{
			if(mediaFilePath != null){

				File fileToUpload = new File(mediaFilePath);
				if(fileToUpload.exists()){

				}else{
					if(callback != null){
						BuiltError error = new BuiltError();
						error.errorMessage(BuiltAppConstants.ErrorMessage_FilePATHINVALID);
						callback.onError(error);
					}
					return this;
				}
			}

			UploadAsync uploadAsync = new UploadAsync();

			if(tagsArray != null && tagsArray.length() > 0){
				uploadAsync.tagsArray = tagsArray;
			}

			if(builtACLUserObject != null){
				uploadAsync.builtACLUserObject = builtACLUserObject;
			}

			if(getHeaders(headerGroup_local) != null && getHeaders(headerGroup_local).getAllHeaders().length > 0){
				if (uploadUid != null){
					uploadAsync.isUpdatedCall = true;
					uploadAsync.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, 
							new java.lang.Object[]{"upload[upload]", null, uploadUid, this, getHeaders(headerGroup_local), callback});
					BuiltAppConstants.updateUploadAsyncInstanceList.add(uploadAsync);
				}else{
					uploadAsync.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, 
							new java.lang.Object[]{"upload[upload]", null, "TEST", this, getHeaders(headerGroup_local), callback});
					BuiltAppConstants.uploadAsyncInstanceList.add(uploadAsync);
				}
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_CalledBuiltDefaultMethod);
			}


		}catch(Exception e) {
			throwExeception(callback, e.toString());
		}

		return this;
	}

	/**
	 * To cancel all {@link BuiltFile} network calls.
	 */
	public void cancelCall() {
		if(isCallForUpload){
			// update upload call
			BuiltAppConstants.cancelMediaFileUploadNetworkCalls = true;

			int count = BuiltAppConstants.updateUploadAsyncInstanceList.size();
			if(count > 0){
				ArrayList<Object> asyncs = BuiltAppConstants.updateUploadAsyncInstanceList;

				for(int i = 0; i < count; i++){
					if(((UploadAsync)asyncs.get(i)).request != null){
						((UploadAsync)asyncs.get(i)).request.cancelCall();
					}
				}
				BuiltAppConstants.updateUploadAsyncInstanceList.clear();
			}
		}
		if(isCallToFetch){
			// other network calls initiated from this FileObject instance.
			BuiltAppConstants.cancelledCallController.add(callController.BUILTFILE.toString());
		}

	}

	/**
	 * Returns media file upload uid. You will get uploaded uid after uploading media file on built.io server.
	 */
	public String getUploadUid() {
		return uploadUid;
	}

	/**
	 * Returns content type of the uploaded file.
	 * 
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns file size of the uploaded file.
	 */
	public String getFileSize(){
		return fileSize;
	}

	/**
	 * Returns file name of the uploaded file.
	 */
	public String getFileName(){
		return fileName;
	}

	/**
	 * Returns upload url by which you can download media file uploaded on built.io server.
	 *  You will get uploaded url after uploading media file on built.io server.
	 */
	public String getUploadUrl(){
		return uploadUrl;
	}


	/**
	 * 
	 * Returns JSON representation of this {@link FileObject} instance data.
	 * 
	 */

	public JSONObject toJSON() {

		return json;
	}

	/**
	 * Get ACL of this instance.
	 * 
	 * 
	 * @return 
	 * 			{@link BuiltACL} instance.
	 * 
	 */
	public BuiltACL getACL(){

		return BuiltACL.setAcl(json.optJSONObject("ACL"));
	}

	/********************************************************************************
	 * 
	 * 
	 * 
	 * 
	 * 
	 */




	private void throwExeception(BuiltResultCallBack callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.errorMessage(errorMessage);
		if(callback != null){
			callback.onRequestFail(error);
		}
	}

	protected HeaderGroup getHeaders(HeaderGroup localHeaders){
		HeaderGroup mainHeaderGroup = new HeaderGroup();
		HeaderGroup endHeaderGroup  = new HeaderGroup();

		mainHeaderGroup = Built.headerGroup;

		if(localHeaders != null){
			if(mainHeaderGroup != null && mainHeaderGroup.getAllHeaders().length > 0){
				int count = mainHeaderGroup.getAllHeaders().length;
				for(int i = 0; i < count; i++){

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

					org.apache.http.Header header = headerGroup_local.getCondensedHeader(localHeaders.getAllHeaders()[i].getName());
					endHeaderGroup.removeHeader(header);
				}
				endHeaderGroup.addHeader(localHeaders.getAllHeaders()[i]);
			}

			return endHeaderGroup;
		}else{
			return mainHeaderGroup;
		}
	}

	protected void clearAll() {

		contentType   			= null;
		fileSize      			= null;
		uploadUrl     			= null;
		tagsArray          		= null;
		uploadUid     			= null;
		mediaFilePath 			= null;
		builtACLUserObject 		= null;
		applicationKey_local 	= null;
		applicationUid_local 	= null;

		json = new JSONObject();

		headerGroup_local.clear();

		isCallForUpload = false;
		isCallToFetch	 = true;

	}

}

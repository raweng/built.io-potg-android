package com.raweng.built;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.RawAppUtils;

/**
 * BuiltFile class to upload media files on built.io server.
 * 
 * @author  raw engineering, Inc
 *
 */
public class BuiltFile implements INotifyUploadDone{

	private int totalUploadCount = 0;
	private int counter    		 = 0;

	private HashMap<String, FileObject> uploadFile;
	private HashMap<String, FileObject> uploadFileResult;

	private BuildFileResultCallback buildFileResultCallback;

	private HeaderGroup headerGroup_local ;
	private String applicationKey_local ;
	private String applicationUid_local ;

	private String URL;
	private JSONObject mainJson;

	private boolean isJsonProper    = true;
	private String errorFilterName  = null;
	private String errorMesage      = null;
	private boolean isCallForUpload = false;
	private boolean isCallToFetch	= true;

	protected JSONObject json = null;
	/**
	 * {@link BuiltFile} instance.
	 */
	public BuiltFile(){
		headerGroup_local   = new HeaderGroup();
		uploadFile          = new HashMap<String, FileObject>();
		uploadFileResult    = new HashMap<String, FileObject>();
		mainJson 			= new JSONObject();
	}

	
	protected int totalCount = 0;
	protected int count      = 0;
	
	

	/**
	 *  Sets the api Key and Application uid for BuiltFile class instance.
	 *  <br>             
	 * Scope is limited to this object only.
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
		setHeader("application_api_key", applicationKey_local);
	}

	/**
	 * To set headers for built.io rest calls.
	 * <br>
	 * Scope is limited to this object only. 
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
	 * <br>
	 * Scope is limited to this object only.
	 *  
	 * @param key
	 * 			   header key.
	 *  
	 */
	public void removeHeader(String key){
		if(headerGroup_local.containsHeader(key)){
			org.apache.http.Header header =  headerGroup_local.getCondensedHeader(key);
			headerGroup_local.removeHeader(header);
		}
	}



	/**
	 * The number of objects to skip before returning any.
	 * 
	 * @param number
	 * 				No of objects to skip from returned objects.
	 * 
	 * @return 
	 * 			 {@link BuiltFile} object, so you can chain this call.
	 * 
	 *  <p>
	 * <b>Note :- </b> You can use this method to add constraint in {@link #fetchAll(BuildFilesResultCallback)}, {@link #fetchImages(BuildFilesResultCallback)}
	 * and {@link #fetchVideos(BuildFilesResultCallback)} methods.
	 */
	public BuiltFile skip(int number){
		try {
			mainJson.put("skip",  number);
		}catch(Exception e) {
			throwExeceptionWithMessage("skip", e);
		}
		return this;
	}

	/**
	 * A limit on the number of objects to return.
	 * <br>
	 * Note: If you are calling findObject with limit = 1, you may find it easier to use getFirst instead.
	 * 
	 * @param number
	 * 				No of objects to limit.
	 * 
	 * @return
	 * 		    {@link BuiltFile} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note :- </b> You can use this method to add constraint in {@link #fetchAll(BuildFilesResultCallback)}, {@link #fetchImages(BuildFilesResultCallback)}
	 * and {@link #fetchVideos(BuildFilesResultCallback)} methods.
	 */
	public BuiltFile limit(int number){
		try {
			mainJson.put("limit", number);
		}catch(Exception e) {
			throwExeceptionWithMessage("limit", e);
		}
		return this;
	}


	/**
	 * Gives object count along with objects returned in response.
	 * 
	 * @return 
	 * 			 {@link BuiltFile} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note :- </b> You can use this method to add constraint in {@link #fetchAll(BuildFilesResultCallback)}, {@link #fetchImages(BuildFilesResultCallback)}
	 * and {@link #fetchVideos(BuildFilesResultCallback)} methods.
	 */
	public BuiltFile includeCount(){
		try {
			mainJson.put("include_count",true);
		} catch (Exception e) {
			throwExeceptionWithMessage("includeCount", e);
		}
		return this;
	}

	/**
	 * Gives only the count of objects returned in response.
	 * 
	 * 
	 * @return 
	 *       {@link BuiltFile} object, so you can chain this call.
	 *       
	 * <p>
	 * <b>Note :- </b> You can use this method to add constraint in {@link #fetchAll(BuildFilesResultCallback)}, {@link #fetchImages(BuildFilesResultCallback)}
	 * and {@link #fetchVideos(BuildFilesResultCallback)} methods.
	 */
	public BuiltFile count(){
		try {
			mainJson.put("count", "true");
		} catch (Exception e) {
			throwExeceptionWithMessage("count", e);
		}
		return this;
	}

	/**
	 * Uploads one or more images and other files to built.io server.
	 * 
	 * @param callback
	 * 					{@link BuildFileResultCallback} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 		  Returns {@link BuiltFile} object, so you can chain this call.  
	 */
	public BuiltFile save(BuildFileResultCallback callback){

		buildFileResultCallback = callback;
		uploadFileResult.clear();

		if(BuiltAppConstants.isNetworkAvailable){
			if(uploadFile.size() < 1){
				BuiltError error = new BuiltError();
				error.errorMessage(BuiltAppConstants.ErrorMessage_NoFileToUpload);
				if(callback != null){
					callback.onRequestFail(error);
				}
			}else{
				for(Entry<String, FileObject> entry : uploadFile.entrySet()){

					File fileToUpload = new File((String) entry.getValue().mediaFilePath);
					if(fileToUpload.exists()){

						if(entry.getValue().getHeaders(headerGroup_local) != null && entry.getValue().getHeaders(headerGroup_local).getAllHeaders().length > 0){


							UploadAsync uploadAsync = new UploadAsync();

							uploadAsync.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR,
									new java.lang.Object[]{"upload[upload]", this, entry.getKey(), entry.getValue(), entry.getValue().getHeaders(headerGroup_local), callback});

							if(entry.getValue().tagsArray != null && entry.getValue().tagsArray.length() > 0){
								uploadAsync.tagsArray = (entry.getValue().tagsArray);

							}
							if(entry.getValue().builtACLUserObject != null){
								uploadAsync.builtACLUserObject = (entry.getValue().builtACLUserObject);
							}
							BuiltAppConstants.uploadAsyncInstanceList.add(uploadAsync);
							totalUploadCount ++;
						}else{

							BuiltError error = new BuiltError();
							error.errorMessage(BuiltAppConstants.ErrorMessage_CalledBuiltDefaultMethod);
							if(callback != null){
								callback.onRequestFail(error);
							}
						}
					}else{

						BuiltError error = new BuiltError();
						error.errorMessage(BuiltAppConstants.ErrorMessage_FilePATHINVALID);
						if(callback != null){
							callback.onRequestFail(error);
						}
					}
				}
			}
		}else{
			BuiltError error = new BuiltError();
			error.errorCode(BuiltAppConstants.NONETWORKCONNECTION);
			error.errorMessage(BuiltAppConstants.ErrorMessage_NoNetwork);
			if(buildFileResultCallback != null){
				buildFileResultCallback.onRequestFail(error);
			}
		}
		return this;
	}

	/**
	 * Add method to add media files in upload queue.
	 * 
	 * @param key
	 * 				Unique key for each upload like : upload1.
	 * 	
	 * @param fileObject
	 * 				{@link FileObject} instance.
	 * 			
	 */
	public void addFile(String key, FileObject fileObject){

		uploadFile.put(key, fileObject);
	}

	/**
	 * To fetch All media files uploaded on built.io server.
	 * 
	 * @param callback
	 * 					{@link BuildFilesResultCallback} object to notify the application when the request has completed.
	 * 
	 * @return
	 * 				{@link BuiltFile} object, so you can chain this call.
	 */
	public BuiltFile fetchAll(BuildFilesResultCallback callback) {
		try{
			if(isJsonProper){
				URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads";

				mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());

				new BuiltCallBackgroundTask(this, BuiltControllers.GETAllUPLOADEDFILE, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, null, callback);

			}else{
				BuiltError error = new BuiltError();
				HashMap<String, Object> errorHashMap = new HashMap<String, Object>();
				errorHashMap.put(errorFilterName, errorMesage);
				error.errors(errorHashMap);
				error.errorMessage(BuiltAppConstants.ErrorMessage_JsonNotProper);
				if(callback != null){
					callback.onRequestFail(error);
				}
			}
		}catch(Exception e) {
			throwExeception(callback, e.toString());
		}

		return this;
	}

	/**
	 * To fetch All image files uploaded on built.io server.
	 * 
	 * @param callback
	 * 					{@link BuildFilesResultCallback} object to notify the application when the request has completed.
	 * 
	 * @return
	 * 				{@link BuiltFile} object, so you can chain this call.
	 */
	public BuiltFile fetchImages(BuildFilesResultCallback callback) {
		try{
			if(isJsonProper){
				URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads/images";

				JSONObject mainJson = new JSONObject();
				mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());

				new BuiltCallBackgroundTask(this, BuiltControllers.GETAllUPLOADEDIMAGEFILE, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, null, callback);

			}else{
				BuiltError error = new BuiltError();
				HashMap<String, Object> errorHashMap = new HashMap<String, Object>();
				errorHashMap.put(errorFilterName, errorMesage);
				error.errors(errorHashMap);
				error.errorMessage(BuiltAppConstants.ErrorMessage_JsonNotProper);
				if(callback != null){
					callback.onRequestFail(error);
				}
			}
		}catch(Exception e) {
			throwExeception(callback, e.toString());
		}

		return this;
	}

	/**
	 * To fetch all video files uploaded on built.io server.
	 * 
	 * @param callback
	 * 					{@link BuildFilesResultCallback} object to notify the application when the request has completed.
	 * 
	 * @return
	 * 				{@link BuiltFile} object, so you can chain this call.
	 */
	public BuiltFile fetchVideos(BuildFilesResultCallback callback) {
		try{
			if(isJsonProper){
				URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads/videos";

				JSONObject mainJson = new JSONObject();
				mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());

				new BuiltCallBackgroundTask(this, BuiltControllers.GETALLUPLOADEDVIDEOFILE, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, null, callback);

			}else{
				BuiltError error = new BuiltError();
				HashMap<String, Object> errorHashMap = new HashMap<String, Object>();
				errorHashMap.put(errorFilterName, errorMesage);
				error.errors(errorHashMap);
				error.errorMessage(BuiltAppConstants.ErrorMessage_JsonNotProper);
				if(callback != null){
					callback.onRequestFail(error);
				}
			}
		}catch(Exception e) {
			throwExeception(callback, e.toString());
		}
		return this;
	}
	
	/**
	 * Count of all objects in a class.if {@link #count()} is called.
	 * 
	 * @return
	 * 			count of all objects in a class.if {@link #count()} is called.
	 */
	public int getTotalCount() {
		return totalCount;
	}

	
	/**
	 * Object count that match the given conditions. if {@link #includeCount()} is called.
	 * 
	 * @return
	 * 			object count that match the given conditions. if {@link #includeCount()} is called.
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * 
	 * Returns JSON representation of this {@link BuiltFile} instance data.
	 * 
	 */
	public JSONObject toJSON() {
		return json;
	}

	/**
	 * To cancel all {@link BuiltFile} network calls.
	 */
	public void cancelCall() {
		RawAppUtils.showLog("upload", "--isCallForUpload|"+isCallForUpload);

		//  upload call
		BuiltAppConstants.cancelMediaFileUploadNetworkCalls = true;


		int count = BuiltAppConstants.uploadAsyncInstanceList.size();
		if(count > 0){
			ArrayList<Object> asyncs = BuiltAppConstants.uploadAsyncInstanceList;

			for(int i = 0; i < count; i++){
				if(((UploadAsync)asyncs.get(i)).request != null){
					((UploadAsync)asyncs.get(i)).request.cancelCall();
				}
			}
			BuiltAppConstants.uploadAsyncInstanceList.clear();
		}

		if(isCallToFetch){
			// other network calls initiated from this FileObject instance.
			BuiltAppConstants.cancelledCallController.add(callController.BUILTFILE.toString());
		}

	}

	/*****************************************************************************************************************************************************************************
	 * 
	 *
	 * 
	 ****************************************************************************************/

	@Override
	public void getResult(String key, FileObject value) {
		counter ++ ;
		if(! BuiltAppConstants.cancelMediaFileUploadNetworkCalls){
			this.uploadFileResult.put(key, value);
		}
		if(counter == totalUploadCount){
			uploadFile.clear();
			if(! BuiltAppConstants.cancelMediaFileUploadNetworkCalls){
				if(buildFileResultCallback != null){
					buildFileResultCallback.onRequestFinish(uploadFileResult);
				}
			}else{
				BuiltAppConstants.cancelMediaFileUploadNetworkCalls = true;
			}
		}
	}

	private void throwExeception(BuildFilesResultCallback callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.errorMessage(errorMessage);
		if(callback != null){
			callback.onRequestFail(error);
		}
	}

	private void throwExeceptionWithMessage(String filterName, Exception exception) {
		isJsonProper = false;
		errorFilterName = filterName;
		errorMesage = exception.toString();
		RawAppUtils.showLog("BuiltFile", "---------" + filterName + "|" + exception);
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

}

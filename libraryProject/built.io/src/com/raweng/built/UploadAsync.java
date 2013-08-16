package com.raweng.built;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.RawAppUtils;


class UploadAsync extends AsyncTask<java.lang.Object, Integer, Void> implements IRequestModel{
	private List<NameValuePair> formParams          		= new ArrayList<NameValuePair>();
	private String fileField 						 		= null;
	private BuildFileResultCallback callBackObjectForUpload = null;
	private BuiltResultCallBack callBackObjectForUpdate 	= null;
	private BuiltFile BuiltFileInstance 					= null;
	private String requestKey 								= null;
	private FileObject requestValue 						= null;
	BuiltFileUploadRequest request;
	private HeaderGroup headerGroup_local ;
	protected JSONArray tagsArray;
	protected BuiltACL builtACLUserObject 	= null;
	protected boolean isUpdatedCall = false;



	@Override
	protected Void doInBackground(java.lang.Object... params) {

		this.fileField    = (String) params[0];

		if(params[1] != null){
			BuiltFileInstance = (BuiltFile) params[1];
		}

		if(params[2] != null){
			requestKey = (String) params[2];
		}

		requestValue      = (FileObject) params[3];
		headerGroup_local = (HeaderGroup) params[4];

		if(params[5] instanceof BuildFileResultCallback){
			callBackObjectForUpload = (BuildFileResultCallback) params[5];
		}else{
			callBackObjectForUpdate = (BuiltResultCallBack) params[5];
		}

		if(! BuiltAppConstants.cancelMediaFileUploadNetworkCalls){
			sendRequest();
		}
		return null;
	}




	@Override
	public void sendRequest() {

		String url = null;
		request = new BuiltFileUploadRequest(BuiltFileInstance, this, requestKey, requestValue, callBackObjectForUpload);

		if(isUpdatedCall){
			// update call
			url = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads" + "/" + requestKey;
			request.setRequestMethod(BuiltAppConstants.RequestMethod.PUT.toString());

		}else{
			url = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads";
			request.setRequestMethod(BuiltAppConstants.RequestMethod.POST.toString());

		}

		if(BuiltFileInstance != null){
			request.setCallbackObject(callBackObjectForUpload);
		}else{
			request.setCallbackObject(callBackObjectForUpdate);
		}

		/*//create call
		if(BuiltFileInstance != null){
			//			url = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads";
			request.setRequestMethod(BuiltAppConstants.RequestMethod.POST.toString());
			request.setCallbackObject(callBackObjectForUpload);
		}else{
			// update call
			//			url = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/uploads" + "/" + requestKey;
			request.setRequestMethod(BuiltAppConstants.RequestMethod.PUT.toString());
			request.setCallbackObject(callBackObjectForUpdate);
		}*/
		request.setURL(url);
		request.setHeaders(getHeaders(headerGroup_local).getAllHeaders());

		try{

			JSONObject uploadValueJson 	= new JSONObject();
			if(tagsArray != null){
				if(tagsArray.length() > 0){

					String tag = null;
					int count = tagsArray.length();
					for(int i = 0; i < count; i++){
						if(i == 0){
							tag = tagsArray.optString(i);
						}else{
							tag = tag + "," + tagsArray.optString(i);
						}			
					}
					uploadValueJson.put("tags", tag);
				}
			}

			if(builtACLUserObject != null){
				JSONObject ACLObj = new JSONObject();

				if(builtACLUserObject.othersJsonObject.length() > 0){
					ACLObj.put("others",builtACLUserObject.othersJsonObject);
				}

				if(builtACLUserObject.userArray.length() > 0){
					ACLObj.put("users", builtACLUserObject.userArray);
				}

				if(builtACLUserObject.roleArray.length() > 0){
					ACLObj.put("roles", builtACLUserObject.roleArray);
				}
				uploadValueJson.put("ACL", ACLObj);

			}

			if(uploadValueJson.length() > 0){
				JSONObject uploadJson = new JSONObject();
				uploadJson.put("upload", uploadValueJson);
				formParams.add(new BasicNameValuePair("PARAM", uploadJson.toString()));
			}

		}catch (Exception error) {
			RawAppUtils.showLog("UploadAsync", error.toString());
		}

		request.setFormParams(formParams);
		request.setTreatDuplicateKeysAsArrayItems(false);

		if(requestValue.mediaFilePath != null){
			File file = new File(requestValue.mediaFilePath);
			formParams.add(new BasicNameValuePair("upload[type]", getMimeTypeOfFile(file.getName())));
			request.addFile(this.fileField, file.getPath(), file.getName());
		}else{
			request.addFile(null, null, null);
		}


		if(BuiltAppConstants.isNetworkAvailable){
			RawAppUtils.showLog("upload", "START--upload");
			request.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{});
		}else {
			BuiltError error = new BuiltError();
			error.errorCode(BuiltAppConstants.NONETWORKCONNECTION);
			error.errorMessage(BuiltAppConstants.ErrorMessage_NoNetwork);
			if(callBackObjectForUpload != null){
				callBackObjectForUpload.onRequestFail(error);
			}
		} 
	}

	@Override
	public void onRequestFailed(java.lang.Object error, String statusCode,
			BuiltResultCallBack builtResultCallBackObject) {
	}

	@Override
	public void onRequestFinished(IURLRequest request) {}


	private String getMimeTypeOfFile(String filename) {
		filename = filename.toLowerCase();
		if (filename.endsWith(".jpg") || 
				filename.endsWith(".jpeg") || 
				filename.endsWith(".jfif") ||
				filename.endsWith(".jpe")) {
			return "image/jpeg";

		} else if (filename.endsWith(".bmp")) {
			return "image/bmp";

		} else if (filename.endsWith(".gif")) {
			return "image/gif";

		} else if (filename.endsWith(".ico")) {
			return "image/x-icon";

		} else if (filename.endsWith(".mdi")) {
			return "image/vndms-modi";

		} else if ((filename.endsWith(".pct")) || 
				(filename.endsWith(".pict"))) {
			return "image/pict";

		} else if (filename.endsWith(".psd")) {
			return "image/photoshop";

		} else if (filename.endsWith(".png")) {
			return "image/png";

		} else if (filename.endsWith(".qtif")) {
			return "image/x-quicktime";

		} else if (filename.endsWith(".rle")) {
			return "image/rle";

		} else if ((filename.endsWith(".tif")) || 
				(filename.endsWith(".tiff"))) {
			return "image/tiff";

		} else if (filename.endsWith(".wmf")) {
			return "image/wmf";

		} else if (filename.endsWith(".xbm")) {
			return "image/x-xbitmap";

		} else if ((filename.endsWith(".asf")) || 
				(filename.endsWith(".asx"))) {
			return "video/x-ms-asf";

		} else if (filename.endsWith(".avi")) {
			return "video/avi";

		} else if (filename.endsWith(".dv")) {
			return "video/x-dv";

		} else if (filename.endsWith(".m1v")) {
			return "video/mpeg";

		} else if (filename.endsWith(".m4v")) {
			return "video/m4v";

		} else if (filename.endsWith(".mov")) {
			return "video/quicktime";

		} else if (filename.endsWith(".mp2v")) {
			return "video/mpeg";

		} else if (filename.endsWith(".mp4")) {
			return "video/mp4";

		} else if ((filename.endsWith(".mpa")) ||
				(filename.endsWith(".mpe"))||
				(filename.endsWith(".mpeg"))||
				(filename.endsWith(".mpg"))) {
			return "video/mpeg";

		} else if ((filename.endsWith(".mqv")) || 
				(filename.endsWith(".qt"))) {
			return "video/quicktime";

		} else if (filename.endsWith(".wm")) {
			return "video/x-ms-wm";

		} else if (filename.endsWith(".wmv")) {
			return "video/x-ms-wmv";

		}else if (filename.endsWith(".wmx")) {
			return "video/x-ms-wmx";

		} else if (filename.endsWith(".wvx")) {
			return "video/x-ms-wvx";

		} else if (filename.endsWith(".aa")) {
			return "audio/audible";	

		} else if (filename.endsWith(".aac")) {
			return "audio/aac";

		} else if (filename.endsWith(".adts")) {
			return "audio/aac";

		} else if ((filename.endsWith(".aif")) ||
				(filename.endsWith(".aifc")) ||
				(filename.endsWith(".aiff")) ||
				(filename.endsWith(".cdda"))) {
			return "audio/aiff";

		} else if (filename.endsWith(".amr")) {
			return "audio/amr";

		} else if (filename.endsWith(".au")) {
			return "audio/basic";

		} else if (filename.endsWith(".caf")) {
			return "audio/x-caf";

		} else if (filename.endsWith(".gsm")) {
			return "audio/x-gsm";

		} else if (filename.endsWith(".m3u")) {
			return "audio/mpegurl";

		} else if (filename.endsWith(".m4a")) {
			return "audio/m4a";

		} else if (filename.endsWith(".m4b")) {
			return "audio/m4b";

		} else if (filename.endsWith(".m4p")) {
			return "audio/m4p";

		} else if ((filename.endsWith(".mid")) || 
				(filename.endsWith(".midi"))) {
			return "audio/mid";

		} else if ((filename.endsWith(".mp2")) ||
				(filename.endsWith(".mp3"))) {
			return "audio/mpeg";

		} else if (filename.endsWith(".ogg")) {
			return "audio/ogg";

		} else if (filename.endsWith(".pls")) {
			return "audio/scpls";

		} else if (filename.endsWith(".rmi")) {
			return "audio/mid";

		} else if (filename.endsWith(".rmm")) {
			return "audio/x-pn-realaudio";

		} else if (filename.endsWith(".sd2")) {
			return "audio/x-sd2";

		} else if (filename.endsWith(".snd")) {
			return "audio/basic";

		} else if ((filename.endsWith(".wav")) || 
				(filename.endsWith(".wave"))) {
			return "audio/wav";

		} else if (filename.endsWith(".wax")) {
			return "audio/x-ms-wax";

		} else if (filename.endsWith(".wma")) {
			return "audio/x-ms-wma";

		} else if (filename.endsWith(".pdf")) {
			return "application/pdf";

		} else if (filename.endsWith(".doc")) {
			return "application/msword";

		} else if (filename.endsWith(".docx")) {
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

		} else if (filename.endsWith(".ppt")) {
			return "application/vnd.ms-powerpoint";

		} else if (filename.endsWith(".pptx")) {
			return "application/vnd.openxmlformats-officedocument.presentationml.presentation";

		} else if (filename.endsWith(".xls")) {
			return "application/vnd.ms-excel";

		} else if (filename.endsWith(".xlsx")) {
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

		} else if (filename.endsWith(".accdb")) {
			return "application/msaccess";
		} else {
			return "text/plain";
		}
	}

	private HeaderGroup getHeaders(HeaderGroup localHeaders){
		HeaderGroup mainHeaderGroup = new HeaderGroup();
		HeaderGroup endHeaderGroup = new HeaderGroup();

		mainHeaderGroup = Built.headerGroup;

		if(localHeaders != null){
			if(mainHeaderGroup != null && mainHeaderGroup.getAllHeaders().length > 0){
				int countMainHeader = mainHeaderGroup.getAllHeaders().length;
				for(int i = 0; i < countMainHeader; i++){

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
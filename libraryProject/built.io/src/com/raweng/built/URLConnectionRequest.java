package com.raweng.built;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.RawAppUtils;

public class URLConnectionRequest extends AsyncTask<java.lang.Object, java.lang.Object, Void> implements IRequestModelHTTP  {

	private final int requestFinish 				= 1;
	private final int requestFail 					= 2;
	private String urlToCall 					    = null;
	private BuiltAppConstants.RequestMethod  method = null;
	private String requestInfo 						= null;
	private org.apache.http.Header[] header 		= null;
	private String cacheFileName 					= null;
	private String controller 						= null;
	private BuiltError errorObject				    = null;
	private JSONObject errorJObject 				= null;
	private ResultCallBack callBackObject           = null;
	private HTTPConnection connection = null;
	private JSONObject responseJSON;	
	private BuiltObject builtObjectInstance;
	private BuiltUser builtUserObject;
	private JSONObject paramsJSON;
	private BuiltInstallation builtInstallationInstance;
	private RoleObject builtRoleObjectInstance;
	private BuiltRole  builtRoleInstance;
	private INotifyClass notifyClass;
	private BuiltApplication builtApplicationInstance;
	private FileObject fileObjectInstance;
	private BuiltFile builtFileInstance;




	protected URLConnectionRequest(INotifyClass INotifyClassObj){
		errorObject = new BuiltError();
		notifyClass = INotifyClassObj;
	}

	protected URLConnectionRequest(BuiltObject builtObject){
		errorObject = new BuiltError();
		builtObjectInstance = builtObject;
	}

	protected URLConnectionRequest(BuiltUser builtUserObject){
		errorObject = new BuiltError();
		this.builtUserObject = builtUserObject;
		notifyClass = builtUserObject;
	}


	protected URLConnectionRequest(BuiltInstallation builtInstallationInstance) {
		errorObject = new BuiltError();
		this.builtInstallationInstance = builtInstallationInstance;
	}

	protected URLConnectionRequest(BuiltDelta builtDeltaInstance) {
		errorObject = new BuiltError();
		notifyClass = builtDeltaInstance;
	}

	protected URLConnectionRequest(RoleObject builtRoleObjectInstance){
		errorObject = new BuiltError();
		this.builtRoleObjectInstance = builtRoleObjectInstance;
	}

	protected URLConnectionRequest(BuiltRole builtRoleInstance){
		errorObject = new BuiltError();
		this.builtRoleInstance = builtRoleInstance;
	}

	public URLConnectionRequest(BuiltApplication builtApplicationInstance) {
		errorObject = new BuiltError();
		this.builtApplicationInstance = builtApplicationInstance;	
	}

	public URLConnectionRequest(FileObject builtFileObjectInstance) {
		errorObject = new BuiltError();
		this.fileObjectInstance = builtFileObjectInstance;	
	}

	public URLConnectionRequest(BuiltFile builtFileInstance) {
		errorObject = new BuiltError();
		this.builtFileInstance = builtFileInstance;	
	}



	public URLConnectionRequest() {
		errorObject = new BuiltError();
	}



	@Override
	protected Void doInBackground(java.lang.Object... params) {

		RawAppUtils.showLog("BuiltURLConnectionRequest","ParallelTasks------|"+ params[0] + " started");

		if(! isCancelled()){

			this.urlToCall    = (String) params[0];
			this.method       = BuiltAppConstants.RequestMethod.POST;
			this.controller   = (String) params[1];
			paramsJSON        = (JSONObject) params[2];
			this.header       = (org.apache.http.Header[]) params[3];

			if(params[4]!= null){
				cacheFileName = (String) params[4];
			}

			if(params[5]!= null){
				requestInfo   = (String) params[5];
			}

			if(params[6]!= null){
				callBackObject = (ResultCallBack) params[6];
			}

			sendRequest();
		}else {
			RawAppUtils.showLog("BuiltURLConnectionRequest","-----------------------already cancelled");
		}
		return (null);
	}

	@Override

	public void sendRequest() {
		if(! isCancelled()){
			connection = new HTTPConnection(urlToCall, this);
			connection.setController(controller);
			connection.setHeaders(header);
			connection.setInfo(requestInfo);
			connection.setFormParamsPOST(paramsJSON);
			connection.setRequestMethod(method);
			connection.setCallBackObject(callBackObject);
			connection.send();
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		RawAppUtils.showLog("BuiltURLConnectionRequest","----------onPostExecute-reqInfo|"+requestInfo);
	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(callBackObject != null){
				if(msg.what == requestFail){
					((ResultCallBack)msg.obj).onRequestFail(errorObject);
				}else if(msg.what == requestFinish){
					sendOnSucessResponse((HTTPConnection) msg.obj);
					if(cacheFileName != null){
						createFileIntoCacheDir(responseJSON);
					}
				}
			}
		}
	};


	protected void sendOnSucessResponse(HTTPConnection request) {

		responseJSON = request.getResponse();

		if(request.getController().toString().equalsIgnoreCase(BuiltControllers.GETOBJECT)){

			BuiltObjectModel model         			= new BuiltObjectModel(responseJSON, null,false,false,false);
			builtObjectInstance.resultJson 			= model.jsonObject;
			builtObjectInstance.ownerEmailId 		= model.ownerEmailId;
			builtObjectInstance.ownerUid     		= model.ownerUid;
			builtObjectInstance.owner		 		= model.ownerMap;
			builtObjectInstance.uid		   			= model.objectUid;
			builtObjectInstance.builtACLUserObject 	= model.builtACLInstance;
			builtObjectInstance.setTags(model.tags);
			model = null;
			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if((request.getController().toString().equalsIgnoreCase(BuiltControllers.CREATEOBJECT)) ||
				(request.getController().toString().equalsIgnoreCase(BuiltControllers.UPDATEOBJECT))){

			BuiltObjectModel model         			= new BuiltObjectModel(responseJSON, null,false,false,false);
			builtObjectInstance.resultJson 			= model.jsonObject;
			builtObjectInstance.uid		   			= model.objectUid;
			builtObjectInstance.ownerEmailId 		= model.ownerEmailId;
			builtObjectInstance.ownerUid     		= model.ownerUid;
			builtObjectInstance.owner		 		= model.ownerMap;
			builtObjectInstance.builtACLUserObject 	= model.builtACLInstance;
			builtObjectInstance.setTags(model.tags);

			if((request.getController().toString().equalsIgnoreCase(BuiltControllers.CREATEOBJECT))){
				builtObjectInstance.clearJson();
			}

			model = null;
			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.DELETEOBJECT)){

			if(builtObjectInstance != null){
				builtObjectInstance.clearAll();
			}

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}


		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.GETCLASS)){

			ClassModel model = new ClassModel(responseJSON,false,false);
			builtObjectInstance.schema = model.classSchemaList;

			model = null;
			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.QUERYOBJECT)){

			BuiltObjectsModel model = new BuiltObjectsModel(responseJSON, null,false);
			notifyClass.getResult(model.classUid);
			notifyClass.getResultObject(model.objectList,responseJSON);
			model = null;

		}else if((request.getController().toString().equalsIgnoreCase(BuiltControllers.LOGIN))
				||(request.getController().toString().equalsIgnoreCase(BuiltControllers.REGISTER))){

			BuiltApplicationUserModel model = new BuiltApplicationUserModel(responseJSON);
			builtUserObject.json            = responseJSON;
			builtUserObject.authToken 		= model.authToken;
			builtUserObject.userName 		= model.userName;
			builtUserObject.firstName 		= model.firstName;
			builtUserObject.lastName 		= model.lastName;
			builtUserObject.email 			= model.email;
			builtUserObject.setUserUid(model.userUid);
			builtUserObject.googleAuthData  = model.googleAuthData;
			getNotifyClass().getResult(model.authToken);

			model = null;
			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.CHECKAPPLICATIONUSERPROFILE)){

			BuiltApplicationUserModel model = new BuiltApplicationUserModel(responseJSON);
			builtUserObject.json            = responseJSON;
			builtUserObject.authToken 		= model.authToken;
			builtUserObject.userName 		= model.userName;
			builtUserObject.firstName 		= model.firstName;
			builtUserObject.lastName 		= model.lastName;
			builtUserObject.email 			= model.email;
			builtUserObject.setUserUid(model.userUid);
			builtUserObject.googleAuthData  = model.googleAuthData;

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

			model = null;

		}else if((request.getController().toString().equalsIgnoreCase(BuiltControllers.LOGOUT))){

			if(builtUserObject != null){
				builtUserObject.json 		= null;
				builtUserObject.authToken 	= null;
				builtUserObject.userName 	= null;
				builtUserObject.firstName 	= null;
				builtUserObject.lastName 	= null;
				builtUserObject.email 		= null;
				builtUserObject.setUserUid(null);
				builtUserObject.sessionJson = null;

				if(builtUserObject.googleAuthData != null){
					builtUserObject.googleAuthData.clear();
				}
			}
			getNotifyClass().getResult(null);

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.DELETEAPPLICATIONUSER)){

			if(builtUserObject != null){
				builtUserObject.clearAll();
			}

			getNotifyClass().getResult(null);

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.CREATEINSTALLATION) ||
				(request.getController().toString().equalsIgnoreCase(BuiltControllers.UPDATEINSTALLATION)) ||
				(request.getController().toString().equalsIgnoreCase(BuiltControllers.SUBSCRIBEINSTALLATIONDATA)) ||
				(request.getController().toString().equalsIgnoreCase(BuiltControllers.UNSUBSCRIBEINSTALLATIONDATA))){

			JSONObject installation = responseJSON.optJSONObject("object");
			if(installation != null){		
				builtInstallationInstance.deviceType     = installation.optString("device_type");
				builtInstallationInstance.deviceToken    = installation.optString("device_token");
				builtInstallationInstance.json			 = responseJSON;	
				builtInstallationInstance.saveInstallationData(installation);


				JSONArray channels = installation.optJSONArray("subscribed_to_channels");
				if(channels != null){
					int count = channels.length();
					for(int i = 0; i < count; i++){
						builtInstallationInstance.subscribedChannelList.add(channels.optString(i));
					}
				}
				if(request.getCallBackObject() != null){
					((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
				}
			}else{

				if(builtInstallationInstance != null){
					builtInstallationInstance.deviceType     		= null;
					builtInstallationInstance.deviceToken    		= null;
					builtInstallationInstance.subscribedChannelList = null;
					builtInstallationInstance.json			        = null;	
					builtInstallationInstance.clearInstallationUid();
				}

				BuiltError error = new BuiltError();
				error.setErrorMessage(BuiltAppConstants.ErrorMessage_InstallationResponse);
				if(request.getCallBackObject() != null){
					((BuiltResultCallBack) request.getCallBackObject()).onRequestFail(error);
				}
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.DELETEINSTALLATION)){

			if(builtInstallationInstance != null){

				builtInstallationInstance.clearAll();

				if(request.getCallBackObject() != null){
					((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
				}

			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.BUILTDELTA)){

			notifyClass.getResultObject(null,responseJSON);

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.GETROLES)){

			RolesModel model 			= new RolesModel(responseJSON);
			builtRoleInstance.roleList  = model.roleList;
			builtRoleInstance.json      = responseJSON;


			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}
			model = null;

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.CREATEROLE) ||
				request.getController().toString().equalsIgnoreCase(BuiltControllers.UPDATEROLE)){

			RoleModel model = new RoleModel(responseJSON, false);
			builtRoleObjectInstance.roleJsonArray 		= model.roleObjectInstance.roleJsonArray;
			builtRoleObjectInstance.userJsonArray 		= model.roleObjectInstance.userJsonArray;
			builtRoleObjectInstance.roleName 	  		= model.roleObjectInstance.roleName;
			builtRoleObjectInstance.roleUid       		= model.roleObjectInstance.roleUid;
			builtRoleObjectInstance.resultJson    		= responseJSON;
			builtRoleObjectInstance.builtACLUserObject 	= model.builtACLInstance;
			builtRoleObjectInstance.isCreate      		= false;


			builtRoleObjectInstance.usersJsonArrayPUSH = new JSONArray();
			builtRoleObjectInstance.usersJsonArrayPULL = new JSONArray();
			builtRoleObjectInstance.rolesJsonArrayPUSH = new JSONArray();
			builtRoleObjectInstance.rolesJsonArrayPULL = new JSONArray();


			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

			model = null;

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.DELETEROLE)){

			if(builtRoleObjectInstance != null){
				builtRoleObjectInstance.clearAll();

			}
			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.APPLICATIONSETTINGS)){

			ApplicationModel model 								= new ApplicationModel(responseJSON);
			builtApplicationInstance.applicationKey 			= model.applicationKey;
			builtApplicationInstance.applicationName 			= model.applicationName;
			builtApplicationInstance.applicationUid				= model.applicationUid;
			builtApplicationInstance.accountName                = model.accountName;
			builtApplicationInstance.applicationSettings 		= model.applicationHashmap;
			builtApplicationInstance.applicationVariableHashmap = model.applicationVariableHashmap;
			builtApplicationInstance.json						= responseJSON;

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}
			model = null;

		}else if((request.getController().toString().equalsIgnoreCase(BuiltControllers.GETUPLOADEDFILE)) || 
				(request.getController().toString().equalsIgnoreCase(BuiltControllers.UPDATEUPLOADEDFILE))){

			UploadedFileModel model = new UploadedFileModel(responseJSON, false);
			fileObjectInstance.contentType  = model.contentType;
			fileObjectInstance.fileSize     = model.fileSize;
			fileObjectInstance.uploadUrl    = model.uploadUrl;
			fileObjectInstance.fileName     = model.fileName;
			fileObjectInstance.json         = model.json;
			fileObjectInstance.uploadUid    = model.uploadedUid;
			fileObjectInstance.setTags(model.tags);

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}
			model = null;

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.DELETEUPLOADEDFILE)){

			if(fileObjectInstance != null){
				fileObjectInstance.clearAll();
			}

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if((request.getController().toString().equalsIgnoreCase(BuiltControllers.GETAllUPLOADEDFILE)) ||
				(request.getController().toString().equalsIgnoreCase(BuiltControllers.GETAllUPLOADEDIMAGEFILE)) ||
				(request.getController().toString().equalsIgnoreCase(BuiltControllers.GETALLUPLOADEDVIDEOFILE))){

			ArrayList<FileObject> arrayList = null;
			JSONArray array = responseJSON.optJSONArray("uploads");
			if(array != null && array.length() > 1){
				int count = array.length();
				if(count > 0){
					arrayList = new ArrayList<FileObject>();
					for (int i = 0; i < count; i++) {

						UploadedFileModel model         		= new UploadedFileModel(array.optJSONObject(i), true);
						FileObject fileObjectInstance   		= new FileObject();
						fileObjectInstance.contentType  		= model.contentType;
						fileObjectInstance.fileSize     		= model.fileSize;
						fileObjectInstance.fileName     		= model.fileName;
						fileObjectInstance.uploadUrl    		= model.uploadUrl;
						fileObjectInstance.json        		 	= model.json;
						fileObjectInstance.uploadUid    		= model.uploadedUid;
						fileObjectInstance.builtACLUserObject 	= model.builtACLInstance;
						fileObjectInstance.setTags(model.tags);
						arrayList.add(fileObjectInstance);
						model = null;
					}
				}
			}
			if(builtFileInstance != null){

				if(responseJSON.has("count")){
					builtFileInstance.count = responseJSON.optInt("count");
				}

				if(responseJSON.has("uploads")){
					builtFileInstance.totalCount = responseJSON.optInt("uploads");
				}


				builtFileInstance.json = responseJSON;
			}

			if(request.getCallBackObject() != null){
				((BuildFilesResultCallback) request.getCallBackObject()).onRequestFinish(arrayList);
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.SENDBROADCASTNOTIFICATION)){

			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.FETCHUESRUIDFOREMAIL)){

			if(request.getCallBackObject() != null){
				((BuildUserResultCallback) request.getCallBackObject()).onRequestFinish(responseJSON.optString("uid"));
			}

		}else if(request.getController().toString().equalsIgnoreCase(BuiltControllers.CLOUDCALL)){

			if(request.getCallBackObject() != null){
				((BuiltExtensionCallback) request.getCallBackObject()).onRequestFinish(responseJSON);
			}

		}else{
			if(request.getCallBackObject() != null){
				((BuiltResultCallBack) request.getCallBackObject()).onRequestFinish();
			}
		}
	}


	protected void createFileIntoCacheDir(java.lang.Object jsonObject) {
		try{
			JSONObject jsonObj     = new JSONObject();
			JSONObject mainJsonObj = new JSONObject();
			JSONObject headerJson  = new JSONObject();


			jsonObj = paramsJSON;

			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal.setTime(new Date());
			long gmtTime = cal.getTimeInMillis();


			mainJsonObj.put("url", urlToCall.toString().trim());
			mainJsonObj.put("timestamp", gmtTime);
			mainJsonObj.put("params", jsonObj);
			mainJsonObj.put("response", jsonObject);
			if(requestInfo != null){
				mainJsonObj.put("classUID", requestInfo);
			}

			int count = header.length;
			for(int i = 0; i < count; i++){
				headerJson.put(header[i].getName(), header[i].getValue());
			}
			mainJsonObj.put("header", headerJson);

			File cacheFile = new File(cacheFileName);

			if(cacheFile.exists()){
				cacheFile.delete();
			}
			FileWriter file = new FileWriter( cacheFile);
			file.write(mainJsonObj.toString());
			file.flush();
			file.close();
		}catch (Exception e) {
			BuiltError error = new BuiltError();
			error.setErrorMessage(BuiltAppConstants.ErrorMessage_SavingNetworkCallResponseForCache);
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("error", e);
			error.setErrors(hashMap);
			if(callBackObject != null){
				callBackObject.onRequestFail(error);
			}
			RawAppUtils.showLog("BuiltURLConnectionRequest", "-----built.io----------createCacheFile-------cach |"+e);
		}
	}


	@Override
	public void onRequestFinished(HTTPConnection request) {

		Message msg = new Message();
		msg.what    = requestFinish;
		msg.obj     = request;
		handler.sendMessage(msg);
	}

	@Override
	public void onRequestFailed(JSONObject error, int statusCode, ResultCallBack callBackObject) {
		Message msg         = new Message();
		String errorMessage = null;
		int errorCode       = statusCode;
		HashMap<String, Object> resultHashMap = null;
		msg.what = requestFail;
		msg.obj  = callBackObject;
		try {
			errorJObject = error;

			if(errorJObject != null){
				errorMessage = (errorJObject).isNull("error_message") == true ? "" : (errorJObject).optString("error_message");

				if((! errorJObject.isNull("error_code")) && (! errorJObject.optString("error_code").contains(" "))){
					errorCode = (Integer) errorJObject.opt("error_code");
				}

				if(! errorJObject.isNull("errors")){
					resultHashMap = new HashMap<String, Object>();
					if(errorJObject.opt("errors") instanceof JSONObject){
						JSONObject errorsJsonObj =  errorJObject.optJSONObject("errors");
						Iterator<String> iterator = errorsJsonObj.keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							Object value = errorsJsonObj.opt(key);
							resultHashMap.put(key, value);
						}
					}else{
						resultHashMap.put("errors", errorJObject.get("errors"));
					}
				}
			}

		} catch (Exception e) {
			RawAppUtils.showLog("BuiltURLConnectionRequest", "------------------catch 210 urlReq---|"+e);
			errorMessage = e.getLocalizedMessage();
		}
		if(errorMessage == null){
			errorMessage = BuiltAppConstants.ErrorMessage_Default;
		}
		errorObject.setErrorCode(errorCode);
		errorObject.setErrorMessage(errorMessage);
		errorObject.setErrors(resultHashMap);
		handler.sendMessage(msg);
	}

	protected void setNotifyClass(INotifyClass notifyClass) {
		this.notifyClass = notifyClass;
	}

	protected INotifyClass getNotifyClass() {
		return notifyClass;
	}
}
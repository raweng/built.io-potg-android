package com.raweng.built;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.RawAppUtils;

/**
 * 
 * 
 * @author raw engineering, Inc
 *
 */
class BuiltCallBackgroundTask {

	private URLConnectionRequest URLConnectionRequestObj;
	private Handler mHandler = new Handler(Looper.getMainLooper());


	protected BuiltCallBackgroundTask(final BuiltObject builtObjectInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, boolean isOfflineCall, final BuiltResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){

			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtObjectInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url, controller, params, headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}

		}else{	
			if(isOfflineCall){
				createCacheFile(url, controller, params, headers, cacheFilePath, requestInfo, callback);
				if(callback != null){
					callback.onAlways();
				}
			}else{
				sendErrorToUser(callback);
			}
		}	
	}

	protected BuiltCallBackgroundTask(final BuiltQuery builtQueryInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {

						URLConnectionRequestObj = new URLConnectionRequest(builtQueryInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url, controller, params, headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{

			sendErrorToUser(callback);

		}	
	}

	protected BuiltCallBackgroundTask(final BuiltUser builtUserInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final BuiltResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtUserInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url, controller, params, headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{	

			sendErrorToUser(callback);
		}
	}


	protected BuiltCallBackgroundTask(final BuiltUser builtUserInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final BuildUserResultCallback callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtUserInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url, controller, params, headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{	

			sendErrorToUser(callback);
		}
	}


	protected BuiltCallBackgroundTask(final BuiltInstallation builtInstallationInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtInstallationInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url,controller, params , headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{

			sendErrorToUser(callback);
		}	
	}


	protected BuiltCallBackgroundTask(final BuiltDelta builtDeltaInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtDeltaInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url,controller, params, headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{	

			sendErrorToUser(callback);
		}	
	}

	protected BuiltCallBackgroundTask(final RoleObject builtRoleObjectInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtRoleObjectInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url,controller, params, headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{	

			sendErrorToUser(callback);
		}
	}

	protected BuiltCallBackgroundTask(final BuiltRole builtRoleInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtRoleInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{url,controller, params , headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{		

			sendErrorToUser(callback);

		}
	}
	protected BuiltCallBackgroundTask(BuiltAnalytics instance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequest URLConnectionRequestObject = new URLConnectionRequest();
						URLConnectionRequestObject.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, 
								new java.lang.Object[]{url,controller, params ,headers, cacheFilePath, requestInfo, null});	  

					}
				});
			}else{

			}
		}
	}

	protected BuiltCallBackgroundTask(final BuiltApplication builtApplicationInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtApplicationInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{
								url,controller, params ,headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{		

			sendErrorToUser(callback);

		}
	}


	protected BuiltCallBackgroundTask(final FileObject builtFileObjectInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtFileObjectInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{
								url,controller, params , headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{		

			sendErrorToUser(callback);
		}
	}

	protected BuiltCallBackgroundTask(final BuiltFile builtFileInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest(builtFileInstance);
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{
								url,controller, params , headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{		

			sendErrorToUser(callback);
		}
	}

	protected BuiltCallBackgroundTask(final BuiltNotification builtNotificationInstance, final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest();
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{
								url,controller, params , headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{		

			sendErrorToUser(callback);
		}
	}

	
	protected BuiltCallBackgroundTask(final String controller, final String url, final Header[] headers, final JSONObject params, final String cacheFilePath, final String requestInfo, final ResultCallBack callback){

		if(BuiltAppConstants.isNetworkAvailable){
			if(headers != null && headers.length > 0){
				mHandler.post(new Runnable() {
					public void run() {
						URLConnectionRequestObj = new URLConnectionRequest();
						URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{
								url,controller, params , headers, cacheFilePath, requestInfo, callback});
					}
				});
			}else{
				sendErrorForHeader(callback);
			}
		}else{		

			sendErrorToUser(callback);
		}
	}

	//called when network is not available.
	private void sendErrorToUser(ResultCallBack callbackObject){
		BuiltError error = new BuiltError();
		error.setErrorCode(BuiltAppConstants.NONETWORKCONNECTION);
		error.setErrorMessage(BuiltAppConstants.ErrorMessage_NoNetwork);
		if(callbackObject != null){
			callbackObject.onRequestFail(error);
		}
	}

	private void sendErrorForHeader(ResultCallBack callbackObject) {
		BuiltError error = new BuiltError();
		error.setErrorMessage(BuiltAppConstants.ErrorMessage_CalledBuiltDefaultMethod);
		if(callbackObject != null){
			callbackObject.onRequestFail(error);
		}
	}


	// to create cache file.
	private void createCacheFile(String url, String controller, JSONObject params, Header[] headers, String cacheFileName, String requestInfo, BuiltResultCallBack callback) {
		try{
			String methodName ;
			JSONObject headerJson = new JSONObject() ;

			if(params.has("_method") && (params.opt("_method").toString().equalsIgnoreCase(BuiltAppConstants.RequestMethod.GET.toString()))){
				// not for get call yet
			}else{

				methodName = (String) params.opt("_method");

				JSONObject mainJsonObj = new JSONObject();

				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone("UTC"));
				cal.setTime(new Date());
				long gmtTime = cal.getTimeInMillis();

				mainJsonObj.put("timestamp",(gmtTime));
				mainJsonObj.put("controller", controller.toString().trim());
				mainJsonObj.put("url", url.toString().trim());
				mainJsonObj.put("method", methodName);
				mainJsonObj.put("params",params);

				int count = headers.length;
				for(int i = 0; i < count; i++){
					headerJson.put(headers[i].getName(), headers[i].getValue());
				}

				mainJsonObj.put("headers",headerJson);
				mainJsonObj.put("cacheFileName",cacheFileName);
				mainJsonObj.put("requestInfo", requestInfo);

				File cacheFolder = new File(BuiltAppConstants.offlineCallsFolderName);
				int countFile = 0;
				if(!cacheFolder.exists()){
					cacheFolder.mkdirs();
				}else{
					if(cacheFolder.isDirectory()){
						countFile = (cacheFolder.list().length) + 1;
					}
				}

				File childFile = new File(cacheFolder.getPath() + File.separator + countFile);
				FileWriter file = new FileWriter(childFile);
				file.write(mainJsonObj.toString());
				file.flush();
				file.close();
			}
		}catch(Exception e) {
			BuiltError error = new BuiltError();
			error.setErrorMessage(BuiltAppConstants.ErrorMessage_SavingNetworkCallForOfflineSupport);
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("error", e);
			error.setErrors(hashMap);
			RawAppUtils.showLog("BuiltCallBackgroundTask", "-----built.io----------createCacheFile-------cach|" + e);
			if(callback != null){
				callback.onRequestFail(error);
			}
		}
	}
}

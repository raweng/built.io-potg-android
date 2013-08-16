package com.raweng.built;


import java.io.File;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.RawAppUtils;

/**
 * To check network availability and execute network call initiated during connection not available.
 * 
 * @author raw engineering, Inc
 *
 */
public class ConnectionStatus extends BroadcastReceiver {

	public ConnectionStatus() {

	}

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean connectionLost = intent.getBooleanExtra("noConnectivity", false);

		if(connectionLost){
			//no net connection
			BuiltAppConstants.isNetworkAvailable = false;
		}else{
			try{
				JSONObject jsonObj = null;
				JSONObject headerObject = null;
				HeaderGroup headerGroup = new HeaderGroup();

				BuiltAppConstants.isNetworkAvailable = true;

				File offlineCallsFolder = new File(context.getDir("OfflineCalls", 0).getPath());

				if(offlineCallsFolder.isDirectory()){
					File[] childFiles = offlineCallsFolder.listFiles();
					for(File child :childFiles){
						File file = new File(offlineCallsFolder, child.getName());
						if(file.exists()){
							jsonObj =  RawAppUtils.getJsonFromCacheFile(file);

							headerObject = jsonObj.optJSONObject("headers"); 

							int count = headerObject.names().length();
							for(int i = 0; i < count; i++){
								String key = headerObject.names().getString(i);
								headerGroup.addHeader(new BasicHeader(key,headerObject.optString(key)));
							}


							URLConnectionRequest URLConnectionRequestObj = new URLConnectionRequest();

							URLConnectionRequestObj.executeOnExecutor(com.raweng.built.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Object[]{
									jsonObj.opt("url").toString(),
									jsonObj.opt("controller").toString(), 
									jsonObj.optJSONObject("params"),
									headerGroup.getAllHeaders(),
									jsonObj.opt("cacheFileName"),
									jsonObj.opt("requestInfo"),
									null
							});
							child.delete();
						}else{
							RawAppUtils.showLog("ConnectionStatus", "--------------------no offline network calls");
						}
					}
				}
			}catch (Exception e) {
				RawAppUtils.showLog("ConnectionStatus", "-----built.io----------send  saved network calls-------catch|" + e);
			}
		}

		RawAppUtils.showLog("ConnectionStatus", "---------------------BuiltAppConstants.isNetworkAvailable|" + BuiltAppConstants.isNetworkAvailable);

	}
}

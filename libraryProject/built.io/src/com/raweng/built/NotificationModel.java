package com.raweng.built;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltUtil;
import com.raweng.built.utilities.RawAppUtils;

public class NotificationModel {

	protected String progress;
	protected String sender;
	protected Calendar timestamp;
	protected String message;
	protected String application;
	protected String status;
	protected String[] channels;
	protected int badge;


	NotificationModel(JSONObject response){

		progress = response.optString("progress");
		sender = response.optString("sender");
		try{
			if(response.optString("timestamp") != null){
				timestamp = BuiltUtil.parseDate(response.optString("timestamp")) ;
			}
		}catch (Exception e) {
			RawAppUtils.showLog("NotificationModel", "-----------e|" + e);
		}
		message = response.optString("message");
		application = response.optString("application");
		status = response.optString("status");

		if(response.has("channels")){
			JSONArray channelsArray = response.optJSONArray("channels");
			int count = channelsArray.length();
			channels = new String[count];
			for(int i = 0; i < count; i++){
				channels[i] = channelsArray.optString(i);
			}
		}

		badge = response.optInt("badge");
	}

}

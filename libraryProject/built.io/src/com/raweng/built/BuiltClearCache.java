package com.raweng.built;


import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.raweng.built.utilities.RawAppUtils;

/**
 * To delete expired cache files from cache directory.
 * 
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltClearCache extends BroadcastReceiver {

	public BuiltClearCache(){}

	@Override
	public void onReceive(Context context, Intent intent) {

		File cacheResponseFolder = new File(context.getDir("BuiltCache", 0).getPath());

		if(cacheResponseFolder.isDirectory()){
			File[] childFiles = cacheResponseFolder.listFiles();

			for(File child :childFiles){
				File file = new File(cacheResponseFolder, child.getName());
				
				File sessionFile      = new File(cacheResponseFolder.getPath()+ File.separator + "Session");
				File installationFile = new File(cacheResponseFolder.getPath()+ File.separator + "Installation");
				
				if((file.getName().equalsIgnoreCase(sessionFile.getName())) || (file.getName().equalsIgnoreCase(installationFile.getName()))){

				}else{

					if(file.exists()){
						JSONObject jsonObj = RawAppUtils.getJsonFromCacheFile(file);
						if(jsonObj != null){
							if(jsonObj.optString("timestamp") != null){

								long responseTime =  Long.parseLong(jsonObj.optString("timestamp"));

								Date responseDate = new Date(responseTime);

								Calendar cal = Calendar.getInstance();
								cal.setTimeZone(TimeZone.getTimeZone("UTC"));
								cal.setTime(new Date());
								Date currentDate = new Date(cal.getTimeInMillis());

								long hourBetween = TimeUnit.MILLISECONDS.toHours(currentDate.getTime() - responseDate.getTime());

								if(hourBetween >= 24){
									file.delete();
								}		
							}
						}
					}else{
						RawAppUtils.showLog("BuiltClearCache", "--------------------no offline network calls");
					}
				}
			}
		}
	}

}

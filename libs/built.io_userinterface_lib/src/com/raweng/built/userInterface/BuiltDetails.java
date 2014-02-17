package com.raweng.built.userInterface;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;

/**
 * BuiltDetails is use to create BuiltObject inside Class.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltDetails {
	
	String classUid;
	BuiltListViewResultCallBack listListener;
	
	/**
	 * Initialise {@link BuiltDetails} instance.
	 * 
	 * @param classUid
	 * 					class uid.
	 */
	public void init(String classUid){
		this.classUid = classUid;
	}
	
	
	/**
	 * Create Object inside class which is set through {@link #init(String)} method.
	 * 
	 * 
	 * @param objectUid
	 * 				   set object uid to Object. 
	 * 
	 * @return this {@link BuiltObject} instance uid.
	 */
	public BuiltObject createObject(String objectUid, BuiltListViewResultCallBack listener){
		
		listListener = listener;
		BuiltObject object = new BuiltObject(classUid);
		
		if(objectUid != null){
			
			object.setUid(objectUid);
			object.fetch(new BuiltResultCallBack() {

				@Override
				public void onSuccess() {		
					
				}

				@Override
				public void onError(BuiltError error) {
					
				}

				@Override
				public void onAlways() {

				}

			});
		}
		return object;
	}
}

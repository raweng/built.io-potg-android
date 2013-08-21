package com.raweng.built;

import java.util.List;

import org.json.JSONObject;

/**
 * To notify class which initiate network call when network call complete.
 * 
 * @author raw engineering, Inc
 * 
 */
public interface INotifyClass {
	
	public void getResult(Object obj);
	
	public void getResultObject(List<java.lang.Object> object, JSONObject jsonobject);
	

}

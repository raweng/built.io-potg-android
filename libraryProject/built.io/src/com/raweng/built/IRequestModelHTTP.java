package com.raweng.built;

import org.json.JSONObject;


/**
 * 
 * @author raw engineering, Inc
 *
 */
public interface IRequestModelHTTP {

	public void sendRequest();

	public void onRequestFailed(JSONObject error, int statusCode, ResultCallBack callBackObject);

	public void onRequestFinished(HTTPConnection request);
}

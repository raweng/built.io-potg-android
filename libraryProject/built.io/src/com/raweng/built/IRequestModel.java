package com.raweng.built;



/**
 *
 * @author raw engineering, Inc
 */
public interface IRequestModel {

    public void sendRequest();

    public void onRequestFailed(Object error, String statusCode, BuiltResultCallBack builtResultCallBackObject);

    public void onRequestFinished(IURLRequest request);

	
}

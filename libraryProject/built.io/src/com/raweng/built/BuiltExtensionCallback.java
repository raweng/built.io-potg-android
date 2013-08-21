package com.raweng.built;

import org.json.JSONObject;

/**
 * {@link BuiltExtension} class callback.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class BuiltExtensionCallback extends ResultCallBack{

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param responseJson
	 * 						{@linkplain JSONObject} instance.
	 * 
	 */
	public abstract void onSuccess(JSONObject responseJson);


	/**
	 * Triggered after network call execution fails.
	 *   
	 *  @param error
	 *  			{@link BuiltError} instance contains more information regarding call execution failure. 
	 */
	public abstract void onError(BuiltError error);


	/**
	 * Called always after onSuccess() or onError().
	 */
	public abstract void onAlways();


	void onRequestFinish(JSONObject result){
		onSuccess(result);
		onAlways();
	}

	@Override
	void always() {
		onAlways();
	}


	@Override
	void onRequestFail(BuiltError error) {
		onError(error);
		always();
	}

}

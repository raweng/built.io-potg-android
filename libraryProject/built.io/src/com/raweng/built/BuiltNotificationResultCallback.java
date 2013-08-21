package com.raweng.built;

import java.util.ArrayList;

/**
 *  {@link BuiltNotification} class callback.
 *  
 * @author raw engineering, Inc
 *
 */
public abstract class BuiltNotificationResultCallback extends ResultCallBack{

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param resultObjects
	 * 							 {@link ArrayList} contains {@link BuiltNotification} instance.
	 * 
	 */
	public abstract void onSuccess(ArrayList<BuiltNotification> resultObjects);


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


	void onRequestFinish(ArrayList<BuiltNotification> resultObjects){
		onSuccess(resultObjects);
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

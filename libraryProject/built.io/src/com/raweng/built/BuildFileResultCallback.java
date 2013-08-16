package com.raweng.built;

import java.util.HashMap;

/**
 * {@link BuiltFile} class callback.
 * 
 * @author raw engineering, Inc
 *
 */

public abstract class BuildFileResultCallback extends ResultCallBack{

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param uploadFileResult
	 * 							 {@linkplain HashMap} object contains user given id as key and {@link FileObject} instance.
	 * 
	 */
	public abstract void onSuccess(HashMap<String, FileObject> uploadFileResult);


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


	void onRequestFinish(HashMap<String, FileObject>  object){
		onSuccess(object);
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

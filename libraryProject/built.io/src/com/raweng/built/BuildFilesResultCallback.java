package com.raweng.built;

import java.util.ArrayList;

/**
 * {@link BuiltFile} class callback.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class BuildFilesResultCallback extends ResultCallBack{

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param uploadFileResult
	 * 							 {@linkplain ArrayList} of {@link FileObject} instance.
	 * 
	 */
	public abstract void onSuccess(ArrayList<FileObject> fileObjectList);


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


	void onRequestFinish(ArrayList<FileObject> fileObjectList){
		onSuccess(fileObjectList);
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

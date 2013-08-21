package com.raweng.built;


/**
 *  {@link BuiltUser} class callback.
 *  
 * @author raw engineering, Inc
 *
 */
public abstract class BuildUserResultCallback extends ResultCallBack{

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param userUid
	 * 				  returns user&#39;s uid.
	 * 
	 */
	public abstract void onSuccess(String userUid);


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


	void onRequestFinish(String userUid){
		onSuccess(userUid);
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

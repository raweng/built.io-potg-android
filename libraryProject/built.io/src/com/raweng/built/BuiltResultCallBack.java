package com.raweng.built;

/**
 * 
 * built.io callback.
 * 
 * @author raw engineering, Inc
 *
 */

public abstract class BuiltResultCallBack extends ResultCallBack{

	/**
	 * Triggered after network call executes successfully.
	 */
	public abstract void onSuccess();

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


	void onRequestFinish(){
		onSuccess();
		always();
	}

	@Override
	void onRequestFail(com.raweng.built.BuiltError error) {
		onError(error);
		always();
	}

	@Override
	void always() {
		onAlways();
	}
}

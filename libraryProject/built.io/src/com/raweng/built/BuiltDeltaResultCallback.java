package com.raweng.built;


/**
 * {@link BuiltDelta} class callback.
 * 
 * notify class after network call executed.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class BuiltDeltaResultCallback extends ResultCallBack {

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param object
	 * 					{@linkplain DeltaResult} object.
	 */
	public abstract void onSuccess(DeltaResult object);

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


	void onRequestFinish(DeltaResult object){
		onSuccess(object);
		onAlways();
	}

	@Override
	void onRequestFail(BuiltError error) {
		onError(error);
		always();
	}

	@Override
	void always() {
		onAlways();
	}

}

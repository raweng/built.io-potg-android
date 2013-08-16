package com.raweng.built;

/**
 * {@link BuiltLocation} callback.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class BuiltLocationCallback extends ResultCallBack {

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param builtLocationInstance
	 * 									{@link BuiltLocation} instance.
	 */
	public abstract void onSuccess(BuiltLocation builtLocationInstance);

	/**
	 * Triggered after network call execution fails.
	 * 
	 * @param error
	 * 					{@link BuiltError} instance contains more information regarding call execution failure. 
	 */
	public abstract void onError(BuiltError error);


	/**
	 * Called always after onSuccess() or onError().
	 */
	public abstract void onAlways();


	void onRequestFinish(BuiltLocation object){
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

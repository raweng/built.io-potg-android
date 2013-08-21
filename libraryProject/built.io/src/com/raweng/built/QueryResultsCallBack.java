package com.raweng.built;


/**
 * built.io callback for {@link BuiltQuery} class&#39;s methods. 
 * 
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class QueryResultsCallBack extends ResultCallBack{

	/**
	 * Triggered after network call executes successfully.
	 * 
	 * @param builtqueryresult
	 * 							 {@linkplain QueryResult} object.
	 */
	public abstract void onSuccess(QueryResult builtqueryresult);

	/**
	 * Triggered after network call execution fails.
	 *  
	 *  @param error
	 *  			{@link BuiltError} instance contains more information regarding call execution failure. 
	 */
	public abstract void onError(com.raweng.built.BuiltError error);

	/**
	 * Called always after onSuccess() or onError().
	 */
	public abstract void onAlways();


	void onRequestFinish(QueryResult builtqueryresult){
		onSuccess(builtqueryresult);
		onAlways();
	}

	@Override
	void always() {
		onAlways();
	}


	@Override
	void onRequestFail(com.raweng.built.BuiltError error) {
		onError(error);
		always();
	}

}

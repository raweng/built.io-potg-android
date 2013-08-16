package com.raweng.built;

/**
 * built.io callback to notify class after network call has been executed.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class ResultCallBack {

	abstract void onRequestFail(com.raweng.built.BuiltError error);
	abstract void always();
}

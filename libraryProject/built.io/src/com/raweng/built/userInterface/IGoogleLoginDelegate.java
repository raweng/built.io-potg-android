package com.raweng.built.userInterface;

import com.raweng.built.BuiltError;
/**
 * 
 * @author raw engineering, Inc
 *
 */
public interface IGoogleLoginDelegate {
	
	public void onSuccess(String token);
	public void onError(BuiltError error,int requestCode);
	public void onAlways();
}

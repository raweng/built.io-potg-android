package com.raweng.built.userInterface;

import android.view.View;
import android.view.ViewGroup;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;

/**
 * {@link BuiltUIListViewController} class callback.
 * 
 * notify class after {@link BuiltUIListViewController} network call executed.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class BuiltListViewResultCallBack {
	
	/**
	 * Called when a Call completes.
     *  
	 * @param position
	 * 					position.
	 * @param convertView
	 * 					convertView for arrange view in Customise list view.
	 * @param parent
	 * 					parent ViewGroup.
	 * @param builtObject
	 * 					List of BuiltObject.
	 * @return 
	 */
    public abstract View getView(int position, View convertView, ViewGroup parent, BuiltObject builtObject);
   
    /**
     * Called when a error Occurrence.
     * 
     * @param BuiltErrorObject
     * 				 {@link BuiltError} object.
     */
    public abstract void onError(BuiltError error);

    /**
     * Called always after onSuccess() or onError().
     * 
     */
    public abstract void onAlways();
}


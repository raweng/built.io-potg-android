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
	 * Triggered after call completes.
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
     * Get the type of View that will be created by getView(int, View, ViewGroup) for the specified item.
     * 
     * @param position
     * 					The position of the item within the adapter's data set whose view type we want.
     * @return
     * 			An integer representing the type of View.Two views should share the same type if one can be converted to the other in getView(int, View, ViewGroup). Note: Integers must be in the range 0 to getViewTypeCount() - 1. IGNORE_ITEM_VIEW_TYPE can also be returned.
     */
    public abstract int getItemViewType(int position);		
    		
    /**
     *  Returns the number of types of Views that will be created by getView(int, View, ViewGroup). 
     *  Each type represents a set of views that can be converted in getView(int, View, ViewGroup). 
     *  If the adapter always returns the same type of View for all items, this method should return 1.This method will only be called when when the adapter is set on the the AdapterView.
     *  
     * @return
     * 			The number of types of Views that will be created by this adapter
     */
    public abstract int getViewTypeCount();
   
    
    /**
     * Triggered after an error occurs.
     * 
     * @param BuiltErrorObject
     * 				 {@link BuiltError} object.
     */
    public abstract void onError(BuiltError error);

    /**
     * Triggered after onSuccess() or onError().
     * 
     */
    public abstract void onAlways();
}


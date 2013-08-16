package com.raweng.built.userInterface;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltUser;

/**
 * {@link BuiltLogin} and {@link BuiltSignUp} class callback.
 * 
 * Notify class after {@link BuiltLogin} and {@link BuiltSignUp} network calls has been executed.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class BuiltAuthResultCallBack {

	public abstract void onSuccess(BuiltUser user);
   /**
    * Called when a Call completes.
    *  
    * @param user
    * 			user BuiltUser object to store authTokan or Register user.
    * 
    */
    void onComplete(BuiltUser user){
     onSuccess(user);
    }
    /**
     * Called when a error Occurrence.
     * 
     *  @param error
     * 				error
     */
    public abstract void onError(BuiltError error);

    /**
     * Called always after onSuccess() or onError().
     * 
     */
    public abstract void onAlways();
    
}
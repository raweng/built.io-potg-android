package com.raweng.built.userInterface;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltUser;

/**
 * {@link BuiltUILoginController} and {@link BuiltUISignUpController} class callback.
 * 
 * Notify class after {@link BuiltUILoginController} and {@link BuiltUISignUpController} network calls has been executed.
 * 
 * @author raw engineering, Inc
 *
 */
public abstract class BuiltAuthResultCallBack {

	public abstract void onSuccess(BuiltUser user);
   /**
    * Triggered after network call executes successfully.
    *  
    * @param user
    * 			user BuiltUser object to store authToken or Register user.
    * 
    */
    void onComplete(BuiltUser user){
     onSuccess(user);
    }
    /**
     * Triggered after network call execution fails.
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
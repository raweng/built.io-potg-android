package com.raweng.built.userInterface;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.raweng.built.BuiltError;

/**
 * 
 * @author raw engineering, Inc
 *
 */
class GetGoogleAccessToken extends AsyncTask<Void, Void, Void>{

	Activity loginActivity;
	Context loginContext;
	String loginScope;
	String loginEmail;
	String accessToken;
	int requestCode;
	int errorCode;
	boolean sendForSuccess = true;
	
	final int ERROR_OCCURRED            = 0;
	final int SUCCESS_FETCH_ACCESSTOKEN = 1;
	final int NEED_PERMISSION           = 2;

	IGoogleLoginDelegate googleLoginInstance;
	
	Handler handleSendToken = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == SUCCESS_FETCH_ACCESSTOKEN){
				googleLoginInstance.onSuccess(accessToken);
			}else if(msg.what == NEED_PERMISSION){
				sendForSuccess = false;
			}else if(msg.what == ERROR_OCCURRED){
				sendForSuccess = false;
				googleLoginInstance.onError(error,errorCode);
			}
		}
	};
	BuiltError error;

	public GetGoogleAccessToken(Context context, Activity loginActivityInstance, IGoogleLoginDelegate googleDelegate, String email, String scope, int requestCode) {
		this.loginContext   = context;
		this.loginActivity  = loginActivityInstance;
		this.loginScope     = scope;
		this.loginEmail     = email;
		this.requestCode    = requestCode;
		googleLoginInstance = googleDelegate;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			fetchGoogleAccessToken();
		} catch (IOException networkError) {
			Message message = new Message();
			message.what = ERROR_OCCURRED;
			handleSendToken.sendMessage(message);
			onError(networkError.getMessage(),408,networkError);
		}
		return null;
	}

	protected void onError(String msg,int responseCode, Exception e) {
		final String errorMsg = e.getMessage();
		errorCode = responseCode;
		Object exceptionObject = e;
		
		Message message = new Message();
		message.what = ERROR_OCCURRED;
		error = new BuiltError();
		error.setErrorMessage(errorMsg);
		HashMap<String, Object> setError = new HashMap<String, Object>();
		setError.put(msg, exceptionObject);
		error.setErrors(setError);
		if(errorCode != 0){
			error.setErrorCode(errorCode);
		}
		handleSendToken.sendMessage(message);
	}
	protected void fetchGoogleAccessToken() throws IOException {
		try {
			accessToken = GoogleAuthUtil.getToken(loginContext, loginEmail, loginScope);
			
			Message message = new Message();
			message.what = SUCCESS_FETCH_ACCESSTOKEN;
			handleSendToken.sendMessage(message);
			
		} catch (UserRecoverableAuthException userRecoverableException) {
			
			Message message = new Message();
			message.what = NEED_PERMISSION;
			handleSendToken.sendMessage(message);
			
			loginActivity.startActivityForResult(userRecoverableException.getIntent(), requestCode);
			onError(userRecoverableException.getMessage(),requestCode, userRecoverableException);

		} catch (GoogleAuthException fatalException) {

			onError("Unrecoverable error " + fatalException.getMessage(),0, fatalException);
		} catch(Exception e){
			
			onError(e.getMessage(),0, e);
		}
	}
}



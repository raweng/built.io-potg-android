package com.raweng.projectsonthego;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltRole;
import com.raweng.built.BuiltUser;
import com.raweng.built.RoleObject;
import com.raweng.built.userInterface.BuiltUILoginController;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;

/**
 * Login Activity.
 * @author raw engineering, Inc
 *
 */
public class LoginActivity extends BuiltUILoginController{

	private final String TAG = "LoginActivity";
	Context context;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = LoginActivity.this;
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(getResources().getString(R.string.loading));
		progressDialog.setTitle(getResources().getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		
		//set progress dialog.
		setProgressDialog(progressDialog);
		
		//Check if user is already logged in.
		if(AppSettings.getIsLoggedIn(context)){
			Intent mainIntent = new Intent(context, MainActivity.class);
			startActivity(mainIntent);	
			finish();
		}
		
		//Set visibility false to default closeImageView in BuiltLogin layout.
		closeImageView.setVisibility(View.GONE);

		Intent signUpIntent = new Intent(context,SignUpActivity.class);
		
		//Intent to open signUp Activity.
		setSignUpIntent(signUpIntent);
		
		//Set twitter consumer key and consumer secret.
		setUpTwitter("s0z8YN9VKG3oYWKWp3JCA", "MorhBcl9ITaw0xnuGbaSjKrNNCWCD1XUzxScPsUjoI");
	}
	/**
	 * Fetch user type of logged in user.
	 * 
	 */
	private void checkAdminRole() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(getResources().getString(R.string.checking_for_admin));
		progressDialog.setTitle(getResources().getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		
		//Create object of built role.
		final BuiltRole roles = new BuiltRole();
		//Make call to fetch a role of logged in user.
		roles.fetchRoles(new BuiltResultCallBack() {

			@Override
			public void onSuccess() {
				
				//Create a built user object.
				BuiltUser user = new BuiltUser();
				String UID = null;
				
				//Get logged in user.
				user =  BuiltUser.getSession();
				if(user != null && user.getUserUid() != null){
					UID = user.getUserUid();
				}

				//Check the role for admin.
				RoleObject adminRoleObject = roles.getRole("admin");

				if(adminRoleObject.hasUser(UID)){
					AppSettings.setUserType(AppConstant.userRole.admin.toString(), context);
				}else{
					AppSettings.setUserType(AppConstant.userRole.guest.toString(), context);
				}

				setResult(RESULT_OK);
				Intent launchMainActIntent = new Intent(context, MainActivity.class);
				startActivity(launchMainActIntent);
				finish();

			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG,error.getErrorMessage());
			}

			@Override
			public void onAlways() {
				progressDialog.dismiss();
			}
		});
	}
	
	
	@Override
	public void loginSuccess(BuiltUser user) {
		//Set true when user successfully logged in.
		AppSettings.setIsLoggedIn(true, context);
		
		//Set user uid.
		AppSettings.setUserUid(user.getUserUid(), context);
		try {
			//Save the session of logged in user.
			user.saveSession();
		} catch (Exception e) {
			AppUtils.showLog(TAG, e.toString());
		}
		checkAdminRole();
	}
	@Override
	public void loginError(BuiltError error) {
		AppUtils.showLog(TAG, error.getErrorMessage());
		Toast.makeText(context,error.getErrorMessage(),Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	

}


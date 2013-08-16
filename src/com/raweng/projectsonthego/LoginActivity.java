package com.raweng.projectsonthego;

import android.app.Activity;
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
import com.raweng.built.userInterface.BuiltAuthResultCallBack;
import com.raweng.built.userInterface.BuiltLogin;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;

/**
 * Login Activity.
 * @author raw engineering, Inc
 *
 */
public class LoginActivity extends Activity{

	private final String TAG = "LoginActivity";
	Context context;
	BuiltLogin builtlogin;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = LoginActivity.this;
		getActionBar().setDisplayHomeAsUpEnabled(false);

		//Initialise BuiltLogin instance.
		builtlogin = new BuiltLogin(context);
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(getResources().getString(R.string.loading));
		progressDialog.setTitle(getResources().getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		builtlogin.setProgressDialog(progressDialog);
		
		//Set login layout from built.io sdk.
		setContentView(builtlogin.getView());

		//Check if user is already logged in.
		if(AppSettings.getIsLoggedIn(context)){
			Intent mainIntent = new Intent(context, MainActivity.class);
			startActivity(mainIntent);	
			finish();
		}
		//Set visibility false to default closeImageView in BuiltLogin layout.
		builtlogin.closeImageView.setVisibility(View.GONE);

		//Open sign up activity.
		builtlogin.signUpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent signUpIntent = new Intent(context, SignUpActivity.class);				
				startActivity(signUpIntent);
			}
		});

		//Provide functionality of google login.
		builtlogin.googleLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//This method is used for google login.
				builtlogin.loginWithGoogle(new BuiltAuthResultCallBack() {
					@Override
					public void onSuccess(BuiltUser user) {
						
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
					public void onError(BuiltError error) {
						AppUtils.showLog(TAG, error.errorMessage());
						Toast.makeText(context,error.errorMessage(),Toast.LENGTH_LONG).show();
					}

					@Override
					public void onAlways() {		
					}
				});
			}
		});

		//Provide functionality of built login.
		builtlogin.loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				//Check error for LoginActivity's edit texts. 
				boolean checkError = builtlogin.checkLoginFieldsValidation();
				if (!checkError) {

					//This method is used for login with built.
					builtlogin.loginWithBuiltUser(new BuiltAuthResultCallBack() {

						@Override
						public void onSuccess(BuiltUser user) {
							
							//Set true when user successfully logged in.
							AppSettings.setIsLoggedIn(true, context);
							
							//Set user uid.
							AppSettings.setUserUid(user.getUserUid(), context);
							try {
								//Save the session of logged in user.
								user.saveSession();
							} catch (Exception e) {
								AppUtils.showLog(TAG,e.toString());
							}
							checkAdminRole();
						}

						@Override
						public void onError(BuiltError error) {
							AppUtils.showLog(TAG,error.errorMessage());
							Toast.makeText(context,error.errorMessage(),Toast.LENGTH_LONG).show();
						}

						@Override
						public void onAlways() {
						}

					});
				}
			}
		});
		
		//Set twitter consumer key and consumer secret.
		builtlogin.setUpTwitter("YOUR_CONSUMER_KEY", "YOUR_CONSUMER_SECRET");
		
		builtlogin.twitterLoginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				builtlogin.loginWithTwitter(new BuiltAuthResultCallBack() {
					
					@Override
					public void onSuccess(BuiltUser user) {
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
					public void onError(BuiltError error) {
						AppUtils.showLog(TAG, error.errorMessage());
						Toast.makeText(context,error.errorMessage(),Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onAlways() {
					}
				});
			}
		});

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
				user =  BuiltUser.currentUser();
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
				AppUtils.showLog(TAG,error.errorMessage());
			}

			@Override
			public void onAlways() {
				progressDialog.dismiss();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		//This method is used to handle first time google login.
		builtlogin.setOnActivityResult(requestCode, resultCode, data);
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


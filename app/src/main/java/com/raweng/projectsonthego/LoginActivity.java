package com.raweng.projectsonthego;

import java.util.List;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltQuery;
import com.raweng.built.BuiltRole;
import com.raweng.built.BuiltUser;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.built.userInterface.BuiltUILoginController;
import com.raweng.built.utilities.BuiltConstant;
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
    private BuiltApplication builtApplication;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = LoginActivity.this;
		getActionBar().setDisplayHomeAsUpEnabled(false);

        try {
            builtApplication = Built.application(context , "API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        //Set api key for login
        setApplicationKey("API_KEY");

		//Set visibility false to default closeImageView in BuiltLogin layout.
		closeImageView.setVisibility(View.GONE);

		Intent signUpIntent = new Intent(context,SignUpActivity.class);
		
		//Intent to open signUp Activity.
		setSignUpIntent(signUpIntent);
		
		//Set twitter consumer key and consumer secret.
		setUpTwitter("YOUR_CONSUMER_KEY", "YOUR_CONSUMER_SECRET");
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
		//final BuiltRole roles = new BuiltRole();
		//Make call to fetch a role of logged in user.

        BuiltQuery query = null;

        try {
            query = builtApplication.getRolesQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }

        query.where("name", "admin");
		query.execInBackground(new QueryResultsCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {

                if (builtError == null){
                    boolean isAdmin = false;

                    //Create a built user object.

                    BuiltUser user = builtApplication.user();
                    String UID = null;

                    //Get logged in user.
                    user = builtApplication.getCurrentUser();
                    if (user != null && user.getUserUid() != null) {
                        UID = user.getUserUid();
                    }

                    List<BuiltRole> roles = queryResult.getRoles();
                    for (int i = 0; i < roles.size(); i++) {

                        JSONArray array = roles.get(i).getJSONArray("users");

                        for (int j = 0; j < array.length(); j++) {

                            if (UID.equalsIgnoreCase(array.optString(i))) {
                                isAdmin = true;
                            }
                        }
                    }

                    if (isAdmin) {
                        AppSettings.setUserType(AppConstant.userRole.admin.toString(), context);
                    } else {
                        AppSettings.setUserType(AppConstant.userRole.guest.toString(), context);
                    }

                    setResult(RESULT_OK);
                    Intent launchMainActIntent = new Intent(context, MainActivity.class);
                    startActivity(launchMainActIntent);
                    finish();

                }else {
                    AppUtils.showLog(TAG, builtError.getErrorMessage());
                }
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


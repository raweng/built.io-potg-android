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
import com.raweng.built.BuiltUser;
import com.raweng.built.userInterface.BuiltUISignUpController;
import com.raweng.projectsonthego.Utilities.AppUtils;


public class SignUpActivity extends BuiltUISignUpController {

	private final String TAG = "SignUpActivity";
	Context context;
    ProgressDialog progressDialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = SignUpActivity.this;

		getActionBar().setDisplayHomeAsUpEnabled(false);

		progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
       
        //Intent to open signUp Activity.
        setProgressDialog(progressDialog);
        
		//Set visibility false to default closeImageView in BuiltLogin layout.
		closeImageView.setVisibility(View.GONE);

        //Set api key for signup
        setApplicationKey("API_KEY");
		
	}
	
	@Override
	public void signUpSuccess(BuiltUser user) {
		Toast.makeText(SignUpActivity.this,R.string.login_for_access,Toast.LENGTH_LONG).show();

		Intent loginIntent = new Intent(context, LoginActivity.class);
		startActivity(loginIntent);

		finish();
	}
	@Override
	public void signUpError(BuiltError error) {
		AppUtils.showLog(TAG,error.getErrorMessage());
		Toast.makeText(context,error.getErrorMessage(),Toast.LENGTH_LONG).show();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}	

	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	


}


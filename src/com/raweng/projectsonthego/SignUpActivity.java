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
import com.raweng.built.BuiltUser;
import com.raweng.built.userInterface.BuiltAuthResultCallBack;
import com.raweng.built.userInterface.BuiltSignUp;
import com.raweng.projectsonthego.Utilities.AppUtils;


public class SignUpActivity extends Activity {

	private final String TAG = "SignUpActivity";
	Context context;
	BuiltSignUp builtSignup;
    ProgressDialog progressDialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = SignUpActivity.this;
		//Initialise BuiltSignUp instance.
		builtSignup = new BuiltSignUp(context);

		getActionBar().setDisplayHomeAsUpEnabled(false);

		//Set sign up layout from built.io sdk.
		setContentView(builtSignup.getView());
		
		progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
       
        
		//Set visibility false to default closeImageView in BuiltLogin layout.
		builtSignup.closeImageView.setVisibility(View.GONE);

		//Provide functionality of sign up.
		builtSignup.signUpButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View veiw) {

				boolean checkValidation = builtSignup.checkSignUpFieldValidation();
				if(!checkValidation){
					progressDialog.show();
					//This method is used for signup using built.io sdk.
					builtSignup.signupWithBuilt(new BuiltAuthResultCallBack() {
						
						@Override
						public void onSuccess(BuiltUser user) {

							Toast.makeText(SignUpActivity.this,R.string.login_for_access,Toast.LENGTH_LONG).show();

							Intent loginIntent = new Intent(context, LoginActivity.class);
							startActivity(loginIntent);

							finish();
						}

						@Override
						public void onError(BuiltError error) {

							AppUtils.showLog(TAG,error.errorMessage());
							Toast.makeText(context,error.errorMessage(),Toast.LENGTH_LONG).show();
						}

						@Override
						public void onAlways() {
							progressDialog.dismiss();
						}
					});
				}
			}
		});
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}	

	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}


}


package com.raweng.built.userInterface;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;
import com.raweng.built.R;
import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.built.utilities.RawAppUtils;

/**
 * This class provides SignUp functionality for built.io.
 * 
 * @author raw engineering, Inc<br>
 * 
 * <br><b>Example</b><br>
  *<pre class="prettyprint">
 * public class UserActivity extends BuiltUISignUpController<br>
 * 
 * </pre>
 */
public abstract class BuiltUISignUpController extends FragmentActivity {

	private static final String TAG = "BuiltUISignUpController";
	Context signupContext;
	BuiltAuthResultCallBack builtAuthCallBack;

	ProgressDialog progressDialog;
	boolean isProgressDialog = false;

	/**
	 * Provide ImageView object which will be used for default logo image.
	 */
	public ImageView logoImageView;

	/**
	 * Provide ImageView object which will be used to close {@link BuiltUISignUpController} activity.
	 */
	public ImageView closeImageView;

	/**
	 * Provide EditText object which will be used for UserName.
	 */
	public EditText userNameEditText;

	/**
	 * Provide EditText object which will be used for Email.
	 */
	public EditText emailEditText;

	/**
	 * Provide EditText object which will be used for Password.
	 */
	public EditText passwordEditView;

	/**
	 * Provide EditText object which will be used for Retype Password.
	 */
	public EditText retypePasswordEditView;	

	/**
	 * Provide Button object which will be used for {@link BuiltUISignUpController} using {@link BuiltUser} method.
	 */
	public Button signUpButton;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		signupContext = this;
		final Activity signUpActivityInstance = (Activity) this;

		setContentView(R.layout.activity_signup);

		userNameEditText       = (EditText) findViewById(R.id.accountName);
		emailEditText 	       = (EditText) findViewById(R.id.emailEditViewSignUp2);
		passwordEditView 	   = (EditText) findViewById(R.id.passwordSignUp);
		retypePasswordEditView = (EditText) findViewById(R.id.retypePassword);

		closeImageView         = (ImageView) findViewById(R.id.closeLogoSignUp);
		logoImageView          = (ImageView) findViewById(R.id.signUpImageViewLogo);

		signUpButton           = (Button) findViewById(R.id.signMeUpButton);

		signUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean checkValidation = checkSignUpFieldValidation();
				if(!checkValidation){
					signupWithBuilt(new BuiltAuthResultCallBack() {

						@Override
						public void onSuccess(BuiltUser user) {
							signUpSuccess(user);
						}

						@Override
						public void onError(BuiltError error) {
							signUpError(error);
						}

						@Override
						public void onAlways() {
						}
					});
				}

			}
		});

		closeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				signUpActivityInstance.finish();
			}
		});
	}

	/**
	 * Provide sign up using built.io.<br>
	 *  
	 * @param callback
	 * 					{@link BuiltAuthResultCallBack} object to notify the application when the request has completed.
	 * 
	 *  <br><b>Example</b><br>
	 *  <pre class="prettyprint">  
	 * &#160;builtSignUpObject.signupWithBuilt(new BuiltAuthResultCallBack{<br>
	 * &#160;@Override<br>
	 * &#160;public void onSuccess(BuiltUser user) { }<br>
	 * 
	 * &#160;@Override<br>
	 * &#160;public void onError(String error) { }<br>
	 * 
	 * &#160;@Override <br>
	 * &#160;public void onAlways() { }<br>
	 * });
	 *  </pre>
	 */
	private void signupWithBuilt(BuiltAuthResultCallBack callback){

		builtAuthCallBack = callback;
		HashMap<String, Object> usrInfo = new HashMap<String, Object>();
		usrInfo.put("email", emailEditText.getText());
		usrInfo.put("password", passwordEditView.getText());
		usrInfo.put("password_confirmation", retypePasswordEditView.getText());
		usrInfo.put("username", userNameEditText.getText());

		try{
			if(isProgressDialog){
				progressDialog.show();
			}
		}catch(Exception e){
			RawAppUtils.showLog(TAG, e.toString());
		}
		final BuiltUser user = new BuiltUser();
		user.register(usrInfo, new BuiltResultCallBack() {

			public void onSuccess() {	
				try{
					if(isProgressDialog){
						progressDialog.dismiss();
					}
				}catch(Exception e){
					RawAppUtils.showLog(TAG, e.toString());
				}
				if(builtAuthCallBack != null){
					builtAuthCallBack.onComplete(user);
				}
			}

			public void onError(BuiltError error) {	
				try{
					if(isProgressDialog){
						progressDialog.dismiss();
					}
				}catch(Exception e){
					RawAppUtils.showLog(TAG, e.toString());
				}
				if(builtAuthCallBack != null){
					builtAuthCallBack.onError(error);
				}
			}

			public void onAlways() {
				if(builtAuthCallBack != null){
					builtAuthCallBack.onAlways();
				}
			}

		});
	}

	/**
	 * Provide the boolean value that EditText Consist of error or not.
	 * if return value is true then error is occurred.
	 * 
	 * @return true/false
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * boolean hasError = builtSignUpObject.checkSignUpFieldValidation();
	 * </pre>
	 */
	private boolean checkSignUpFieldValidation(){
		boolean checkUserName = checkUserName();
		boolean checkEmail = checkEmail();
		boolean checkPassWord = checkPassWord();
		boolean checkRetype = checkRetype();

		if(checkUserName || checkEmail || checkPassWord || checkRetype){
			return true;
		}
		return false;
	}

	/**
	 * Set default logo image using source id and image scale type.
	 * 
	 * @param resId 
	 * 					identifier of the resource.
	 * 
	 * @param scaleType
	 * 					options for scaling the bounds of an image to the bounds of view.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * setLogoImage(R.drawable.ic_built,ScaleType.CENTER);
	 * </pre>
	 */
	public void setLogoImage(int resId, ScaleType scaleType){

		logoImageView.setImageResource(resId);
		if(scaleType != null){
			logoImageView.setScaleType(scaleType);
		}
	}	

	/**
	 * Sets the logo image description.
	 * Set this property to enable better accessibility support for your application.
	 * This is especially true for views that do not have textual representation (For example, ImageView).
	 *  
	 * @param contentDescription
	 * 							contentDescription.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">  
	 * setImageContentDescription("content description");
	 * </pre>
	 */
	public void setImageContentDescription(String contentDescription){
		if(contentDescription != null){
			logoImageView.setContentDescription(contentDescription);
		}
	}

	/**
	 * Sets default close image using source id and image scale type.
	 * 
	 * @param resId 
	 * 					identifier of the resource.
	 * 
	 * @param scaleType
	 * 					options for scaling the bounds of an image to the bounds of view. 
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">  
	 * setCloseImageView(R.drawable.ic_built,ScaleType.CENTER);
	 * </pre>
	 */
	public void setCloseImageView(int resId, ScaleType scaleType){
		closeImageView.setImageResource(resId);
		if(scaleType != null){
			closeImageView.setScaleType(scaleType);
		}
	}

	/**
	 * Sets the margins for provided view inside RelativeLayout.
	 * provide values in integer. 
	 *  
	 * @param view
	 * 				set view.
	 * @param top
	 * 				set top Margin
	 * @param bottom
	 * 				set bottom Margin
	 * @param left
	 * 				set left Margin
	 * @param right
	 * 				set right Margin
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">  
	 * setMargin(imageviewObject,100,100,100,100);
	 * </pre>
	 */
	public void setMargin(View view, int top, int bottom, int left, int right){

		try {
			int topMargin    = BuiltUtil.convertToPixel(signupContext, top);
			int bottomMargin = BuiltUtil.convertToPixel(signupContext, bottom);
			int leftMargin   = BuiltUtil.convertToPixel(signupContext, left);
			int rightMargin  = BuiltUtil.convertToPixel(signupContext, right);

			RelativeLayout.LayoutParams layoutparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutparam.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
			view.setLayoutParams(layoutparam);
		} catch (Exception e) {
			RawAppUtils.showLog(TAG, e.toString());
		}
	}

	/**
	 * Set progress dialog.
	 * 
	 * @param progressDialog
	 * 						 {@link ProgressDialog} instance.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * setProgressDialog(progressDialogObject);
	 * </pre>
	 */
	public void setProgressDialog(ProgressDialog progressDialog){
		if(progressDialog != null){
			this.isProgressDialog =  true;
			this.progressDialog = progressDialog;
		}
	}


	/*****************************************************************************************************************************************************************************
	 * 
	 ******************************* Private Methods  *************************************************************
	 * 
	 ****************************************************************************************/

	private boolean checkEmail() {

		emailEditText.setError(null);
		String strEmail = emailEditText.getText().toString();

		if (TextUtils.isEmpty(strEmail)) {
			emailEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_REQUIRED_ERROR);
			return true;
		} else if (!strEmail.contains("@") && !strEmail.contains(".")) {
			emailEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_VALIDATION_ERROR);
			return true;
		}
		return false;
	}

	private boolean checkPassWord(){
		passwordEditView.setError(null);
		String strPassword = passwordEditView.getText().toString();

		if (TextUtils.isEmpty(strPassword)) {
			passwordEditView.setError(BuiltAppConstants.CHECKED_FIELD_FOR_REQUIRED_ERROR);
			return true;
		} else if (strPassword.length() < 4) {
			passwordEditView.setError(BuiltAppConstants.CHECKED_FIELD_FOR_VALIDATION_ERROR);
			return true;
		}
		return false;
	}

	private boolean checkUserName() {
		userNameEditText.setError(null);
		String strUserName = userNameEditText.getText().toString();

		if(TextUtils.isEmpty(strUserName)){
			userNameEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_REQUIRED_ERROR);
			return true;
		}
		return false;
	}

	private boolean checkRetype() {
		retypePasswordEditView.setError(null);
		String strRetype = retypePasswordEditView.getText().toString();

		if(TextUtils.isEmpty(strRetype)){
			retypePasswordEditView.setError(BuiltAppConstants.CHECKED_FIELD_FOR_REQUIRED_ERROR);
			return true;

		}else if(!passwordEditView.getText().toString().equals(strRetype)){
			retypePasswordEditView.setError(BuiltAppConstants.CHECKED_FIELD_FOR_MISMATCH_ERROR);
			return true;
		}
		return false;
	}

	/**
	 * Provides the BuiltUser object after successful SignUp.
	 * @param user
	 * 			{@link BuiltUser} object
	 */
	public abstract void signUpSuccess(BuiltUser user);

	/**
	 * Provides the BuiltError object after SignUp process has failed.
	 * @param error
	 * 				{@link BuiltError} object
	 */
	public abstract void signUpError(BuiltError error);
}



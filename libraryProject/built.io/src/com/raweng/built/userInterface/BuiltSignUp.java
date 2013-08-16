package com.raweng.built.userInterface;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
 * <pre class="prettyprint">
 * BuiltSignUp builtSignUpObject = new BuiltSignUp(context);
 * setContentView(builtSignUpObject.getView());
 *</pre>
 */
public class BuiltSignUp {
	private static final String TAG = "BuiltSignUp";
	Context signupContext;
	View signupView;
	BuiltAuthResultCallBack builtAuthCallBack;

	ProgressDialog progressDialog;
	boolean isProgressDialog = false;

	/**
	 * Provide ImageView object which will be used for default logo image.
	 */
	public ImageView logoImageView;

	/**
	 * Provide ImageView object which will be used to close {@link BuiltSignUp} activity.
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
	 * Provide Button object which will be used for {@link BuiltSignUp} using {@link BuiltUser} method.
	 */
	public Button signUpButton;

	/**
	 * Initialise {@link BuiltSignUp} instance.
	 * 
	 * @param context
	 * 							set application context.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">  
	 * &#160;BuiltSignUp builtSignUpObject = new BuiltSignUp(context);
	 * </pre>
	 */
	public BuiltSignUp(Context context) {
		try{
			signupContext = context;

			LayoutInflater inflaterLayout  = (LayoutInflater) signupContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
			signupView 		               = inflaterLayout.inflate(R.layout.activity_signup, null);

			userNameEditText       = (EditText) signupView.findViewById(R.id.accountName);
			emailEditText 	       = (EditText) signupView.findViewById(R.id.emailEditViewSignUp2);
			passwordEditView 	   = (EditText) signupView.findViewById(R.id.passwordSignUp);
			retypePasswordEditView = (EditText) signupView.findViewById(R.id.retypePassword);

			closeImageView         = (ImageView) signupView.findViewById(R.id.closeLogoSignUp);
			logoImageView          = (ImageView) signupView.findViewById(R.id.signUpImageViewLogo);

			signUpButton           = (Button) signupView.findViewById(R.id.signMeUpButton);
		}catch(Exception e){
			RawAppUtils.showLog(TAG, e.toString());
		}

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
	public void signupWithBuilt(BuiltAuthResultCallBack callback){

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
	 * This provide view for {@link BuiltSignUp} Layout.
	 *
	 * @return 
	 * 			provide signupView, so user can set Layout. 
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * setContentView(builtSignUpObject.getView());
	 * </pre>
	 */
	public View getView(){
		return signupView; 
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
	public boolean checkSignUpFieldValidation(){
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
	 * builtSignUpObject.setLogoImage(R.drawable.ic_built,ScaleType.CENTER);
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
	 * This is especially true for views that do not have textual representation (For example, ImageView).	 *
	 *  
	 * @param contentDescription
	 * 							contentDescription.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">  
	 * builtSignUpObject.setImageContentDescription("content description");
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
	 * builtSignUpObject.setCloseImageView(R.drawable.ic_built,ScaleType.CENTER);
	 * </pre>
	 */
	public void setCloseImageView(int resId, ScaleType scaleType){
		closeImageView.setImageResource(resId);
		if(scaleType != null){
			closeImageView.setScaleType(scaleType);
		}
	}

	/**
	 * Sets the margins for provided view inside RelativeLayout.<br>
	 * Only for provided sign up layout.<br>
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
	 * builtSignUpObject.setMargin(imageviewObject,100,100,100,100);
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
	 * show {@link ProgressDialog} while {@link #signupWithBuilt(BuiltAuthResultCallBack)}. 
	 * 
	 * @param ProgressDialog
	 * 						 {@link ProgressDialog} instance.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * builtSignUpObject.setProgressDialog(progressDialogObject);
	 * </pre>
	 */
	public void setProgressDialog(ProgressDialog ProgressDialog){
		if(progressDialog != null){
			this.isProgressDialog =  true;
			this.progressDialog = ProgressDialog;
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
}



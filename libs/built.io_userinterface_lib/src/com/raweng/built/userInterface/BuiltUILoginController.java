package com.raweng.built.userInterface;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.auth.GoogleAuthUtil;
import com.raweng.built.Built;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltError.ResponseType;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;
import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltUtil;

/**
 * This class provides login functionality for built.io.<br>
 * 
 * @author raw engineering, Inc<br><br>
 * 
 * <b>Note :</b><br>
 * User needs to add following permissions in manifest.<br><br>
 * <li><a href="http://developer.android.com/reference/android/Manifest.permission.html#GET_ACCOUNTS">Allows access to the list of accounts in the Accounts Service.</a></li><br>
 * <li><a href="http://developer.android.com/reference/android/Manifest.permission.html#USE_CREDENTIALS"/>Allows an application to request authtokens from the AccountManager.</a></li><br>
 * <li>Add <b>com.google.android.providers.gsf.permission.READ_GSERVICES</b> permission to allows the API to access Google web-based services.</li><br>
 * <li>Add <b>&#60;meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" /&#62;</b><br> in application tag as required for upgraded google play services</li><br>
 * <br>
 * 
 * <b>Note :</b><br><br>
 * <li>User can open sign up activity by passing intent.</li>
 * <pre class="prettyprint">
 * setSignUpIntent(signUpIntent);
 * </pre>
 * 
 * <li>For twitter login user can set consumer key and consumer secret using {@link #setUpTwitter(String, String)}.</li><br>
 * <pre class="prettyprint">
 * setUpTwitter("consumerKey","consumerSecret");
 * </pre>
 * 
 *<br><b>Example</b><br>
 *<pre class="prettyprint">
 * public class UserActivity extends BuiltUILoginController<br>
 * 
 * </pre>
 *
 *
 */
public abstract class BuiltUILoginController extends FragmentActivity{

	private static final String TAG = "BuiltUILoginController";
	protected Context loginContext;
	protected Activity loginActivityInstance;
	protected BuiltACL builtUserACL;
	private BuiltAuthResultCallBack builtAuthCallBack;
	Intent signUpIntent;
	String error = null;

	String selectedEmail;

	/**
	 * Provides Button object which will be used for {@link BuiltUILoginController} using {@link BuiltUser} method.
	 */
	public Button loginButton;

	/**
	 * Provides Button object which will be used to open {@link BuiltUISignUpController} Activity.
	 */
	public Button signUpButton;

	/**
	 * Provides Button object which will be used for Google Login using {@link BuiltUser} method.
	 */
	public Button googleLoginButton;

	/**
	 * Provides Button object which will be used for Twitter Login using {@link BuiltUser} method.
	 */
	public Button twitterLoginButton;

	/**
	 * Provides ImageView object which will be used for default Image Forgot Password.
	 */
	public ImageView forgetPasswordImageView;

	/**
	 * Provides EditText object which will be used for Tenant.
	 */
	public EditText tenantEditText;

	/**
	 * Provides EditText object which will be used for Email.
	 */
	public EditText emailEditText;

	/**
	 * Provides EditText object which will be used for Password.
	 */
	public EditText passwordEditText;

	/**
	 * Provides ImageView object which will be used for close view.
	 */
	public ImageView closeImageView;

	/**
	 * Provides ImageView object which will be used for default logo image.
	 */
	public ImageView logoImageView;


	ProgressDialog progressDialog;
	ProgressDialog customProgressDialog;

	boolean isProgressDialog = false;
	boolean isTenantEnable   = false;

	IGoogleLoginDelegate googleDelegate;

	AlertDialog.Builder alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.loginContext 	      = this;
		this.loginActivityInstance= (Activity) this;

		setContentView(R.layout.builtio_login_activity);

		tenantEditText 	             =  (EditText) findViewById(R.id.tenant);
		emailEditText 	             =  (EditText) findViewById(R.id.emailSignUp);
		passwordEditText             =  (EditText) findViewById(R.id.password);

		closeImageView               =  (ImageView) findViewById(R.id.closeLogo);
		logoImageView                =  (ImageView) findViewById(R.id.imageViewLogo);
		forgetPasswordImageView      =  (ImageView) findViewById(R.id.forgotpasswordImageView);

		signUpButton                 =  (Button) findViewById(R.id.signUpButton);
		loginButton 		         =  (Button) findViewById(R.id.loginInButton);
		googleLoginButton            =  (Button) findViewById(R.id.logInWithGoogle);
		twitterLoginButton			 =	(Button) findViewById(R.id.logInWithTwitter);

		progressDialog = new ProgressDialog(loginContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(BuiltAppConstants.GOOGLE_ACCOUNT);
		progressDialog.setMessage(BuiltAppConstants.PROGRESS_MESSAGE);

		forgetPasswordCall();

		googleLoginButton.setOnClickListener(OnClickListener);
		signUpButton.setOnClickListener(OnClickListener);
		loginButton.setOnClickListener(OnClickListener);
		twitterLoginButton.setOnClickListener(OnClickListener);
		closeImageView.setOnClickListener(OnClickListener);

	}

	OnClickListener OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			if(view == googleLoginButton){

				loginWithGoogle(new BuiltAuthResultCallBack() {

					@Override
					public void onSuccess(BuiltUser user) {
						loginSuccess(user);
					}

					@Override
					public void onError(BuiltError error) {
						loginError(error);
					}

					@Override
					public void onAlways() {
					}
				});

			}else if(view == signUpButton){
				if(signUpIntent != null){
					loginActivityInstance.startActivity(signUpIntent);
				}

			}else if(view == loginButton){

				boolean checkError = checkLoginFieldsValidation();
				if (!checkError){
					loginWithBuiltUser(new BuiltAuthResultCallBack() {

						@Override
						public void onSuccess(BuiltUser user) {
							loginSuccess(user);
						}

						@Override
						public void onError(BuiltError error) {
							loginError(error);
						}

						@Override
						public void onAlways() {
						}
					});
				}

			}else if(view == twitterLoginButton){

				loginWithTwitter(new BuiltAuthResultCallBack() {

					@Override
					public void onSuccess(BuiltUser user) {
						loginSuccess(user);
					}

					@Override
					public void onError(BuiltError error) {
						loginError(error);
					}

					@Override
					public void onAlways() {
					}
				});

			}else if(view == closeImageView){
				loginActivityInstance.finish();
			}

		}
	};

	/**
	 * To set ACL on this object.
	 * 
	 * @param builtACL
	 * 					object of {@linkplain BuiltACL} class.<br>
	 * 
	 * <b> Note :- </b>
	 * For simple built login {@link BuiltACL} object gets set while sign up.
	 * 
	 */
	public void setACL(BuiltACL builtACL){
		builtUserACL = builtACL;
	}

	/**
	 * Intent to open builtSignUp activity.
	 * @param intent
	 * 				intent
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint">
	 * setSignUpIntent(signUpIntent);
	 * </pre>
	 */
	public void setSignUpIntent(Intent intent){
		if(intent != null){
			signUpIntent = intent;
		}
	}

	/**
	 * To enable tenant.
	 * This will enable tenant container.
	 * 
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint">
	 * enableTenant();
	 * </pre>
	 */
	public void enableTenant(){
		isTenantEnable = true;
		tenantEditText.setVisibility(View.VISIBLE);
	}

	/**
	 * To remove tenant.
	 * This will remove tenant from container.
	 * 
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint">
	 * removeTenant();
	 * </pre>
	 */
	public void removeTenant(){
		isTenantEnable = false;
		tenantEditText.setVisibility(View.GONE);
	}

	/**
	 * Provide the login using {@link BuiltUser}.
	 * 
	 * @param callback
	 * 					{@link BuiltAuthResultCallBack} object to notify the application when the request has completed.
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">
	 * builtloginObject.loginWithBuiltUser(new BuiltAuthResultCallBack{<br>
	 * &#160;@Override<br>
	 * &#160;public void onSuccess(BuiltUser user) { }<br>
	 * 
	 * &#160;@Override<br>
	 * &#160;public void onError(String error) { }<br>
	 *
	 * &#160;@Override<br>
	 * &#160;public void onAlways(){ }<br> });<br>
	 * </pre>
	 */
	private void loginWithBuiltUser(BuiltAuthResultCallBack callback){
		String email = emailEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		builtAuthCallBack = callback;
		try{
			if(isProgressDialog){
				customProgressDialog.show();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(isTenantEnable){
			if(tenantEditText.getText().toString().trim().length() > 0){
				Built.setTenant(tenantEditText.getText().toString().trim());
			}
		}
		final BuiltUser user = new BuiltUser();
		
		if(builtUserACL != null){
			user.setACL(builtUserACL);
		}
		user.login(email, password, new BuiltResultCallBack() {

			@Override
			public void onSuccess() {
				if(builtAuthCallBack != null){
					builtAuthCallBack.onComplete(user);
				}
				try{
					if(isProgressDialog){
						customProgressDialog.dismiss();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			@Override
			public void onError(BuiltError error) {
				try{
					if(isProgressDialog){
						customProgressDialog.dismiss();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				if(builtAuthCallBack != null){
					builtAuthCallBack.onError(error);
				}
			}
			@Override
			public void onAlways() {
				if(builtAuthCallBack != null){
					builtAuthCallBack.onAlways();
				}
			}

		});

	}

	/**
	 * Provide the Google login through register account in Gmail Application.
	 *
	 * @param callback
	 * 					{@link BuiltAuthResultCallBack} object to notify the application when the request has completed.
	 * 
	 *User need to add following permissions in manifest.<br>
	 * <li><a href="http://developer.android.com/reference/android/Manifest.permission.html#GET_ACCOUNTS">Allows access to the list of accounts in the Accounts Service.</a></li>
	 * <li><a href="http://developer.android.com/reference/android/Manifest.permission.html#USE_CREDENTIALS"/>Allows an application to request authtokens from the AccountManager.</a></li>
	 * <li>Add com.google.android.providers.gsf.permission.READ_GSERVICES permission Allows the API to access Google web-based services.</li>
	 * <li>Add &#60;meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" /&#62; inside Application tag to use google play services for upgraded version of play services as per requirement.</li>
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">
	 * builtloginObject.loginWithGoogle(new BuiltAuthResultCallBack{<br>
	 * &#160;@Override<br>
	 * &#160;public void onSuccess(BuiltUser user) {<br>
	 * &#160;&#47;&#47;user will get google access token if user want.<br>
	 * &#160;String googleAccessToken = user.getGoogleAccessToken();
	 * }<br>
	 * 
	 * &#160;@Override<br>
	 * &#160;public void onError(String error) { }<br>
	 *
	 * &#160;@Override<br>
	 * &#160;public void onAlways(){ }<br> });
	 * </pre>
	 *  
	 */
	private void loginWithGoogle(BuiltAuthResultCallBack callback) {
		builtAuthCallBack = callback;
		if(error != null){  
			BuiltError errorMsg = new BuiltError();
			errorMsg.setErrorMessage(error);
			errorMsg.setResponseType(ResponseType.UNKNOWN);
			builtAuthCallBack.onError(errorMsg);
			try{
				if(isProgressDialog){
					customProgressDialog.dismiss();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{

			googleLogin(new IGoogleLoginDelegate() {

				@Override
				public void onSuccess(String token) {
					final BuiltUser googleUser = new BuiltUser();

					if(builtUserACL != null){
						googleUser.setACL(builtUserACL);
					}

					googleUser.setGoogleAccessToken(token);
					googleUser.loginWithGoogleAuthAccessToken(token, new BuiltResultCallBack() {

						@Override
						public void onSuccess() {
							try{
								if(isProgressDialog){
									customProgressDialog.dismiss();
								}
							}catch(Exception e){
								e.printStackTrace();
							}
							if(builtAuthCallBack != null){
								builtAuthCallBack.onComplete(googleUser);
							}
						}

						@Override
						public void onError(BuiltError error) {
							try{
								if(isProgressDialog){
									customProgressDialog.dismiss();
								}
							}catch(Exception e){
								e.printStackTrace();
							}
							if(builtAuthCallBack != null){
								builtAuthCallBack.onError(error);
							}
						}

						@Override
						public void onAlways() {	
							if(builtAuthCallBack != null){
								builtAuthCallBack.onAlways();
							}
						}
					});
				}

				@Override
				public void onError(BuiltError error,int requestCode) {
					try{
						if(isProgressDialog){
							customProgressDialog.dismiss();
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					if(requestCode != BuiltAppConstants.REQUEST_CODE_RECOVER_FROM_AUTH){
						builtAuthCallBack.onError(error);
						builtAuthCallBack.onAlways();
					}
				}

				@Override
				public void onAlways() {
				}
			});
		}
	}

	/**
	 * Provide twitter login.
	 *
	 * @param callback
	 * 					{@link BuiltAuthResultCallBack} object to notify the application when the request has completed.
	 * 
	 * <br><b>Note:</b><br>
	 * <ol>
	 * <li>
	 * Add following code to your application's manifest file:
	 *  	<br>&#60;activity<br>
	 *       &#160;&#160;&#160;&#160;android:name="com.raweng.built.userInterface.BuiltTwitterLoginActivity"<br>
	 *       &#160;&#160;&#160;&#160;android:screenOrientation="portrait"&#62;<br>
	 *       &#160;&#160;&#160;&#160;&#160;&#60;intent-filter&#62;<br>
	 *       &#160;&#160;&#160;&#160;&#160;&#160;&#60;action android:name="android.intent.action.VIEW" &#47;&#62;<br>
	 *
	 *       &#160;&#160;&#160;&#160;&#160;&#160;&#60;category android:name="android.intent.category.DEFAULT" &#47;&#62;<br>
	 *       &#160;&#160;&#160;&#160;&#160;&#160;&#60;category android:name="android.intent.category.BROWSABLE" &#47;&#62;<br>
	 *	
	 *       &#160;&#160;&#160;&#160;&#160;&#160;&#60;data&#160;android:host="twitterlogin"<br>
	 *       &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;android:scheme="x-oauthflow-twitter" &#47;&#62;<br>
	 *       &#160;&#160;&#160;&#160;&#60;&#47;intent-filter&#62;<br>
	 *    	 &#60;&#47;activity&#62;<br>
	 *    
	 * </li>
	 *    
	 * <li> setup twitter with your consumer key and consumer secret before you call {@link #loginWithTwitter(BuiltAuthResultCallBack)}.</li> <br>
	 * </ol>
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">
	 * 
	 * <br>BuiltUILoginController builtLogin = new BuiltUILoginController(context);<br>
	 * builtLogin.setUpTwitter("Your_Consumer_Key", "Your_Consumer_Secret");<br><br>
	 * 
	 * builtloginObject.loginWithTwitter(new BuiltAuthResultCallBack{<br>
	 * &#160;@Override<br>
	 * &#160;public void onSuccess(BuiltUser user) {<br>
	 * }<br>
	 * 
	 * &#160;@Override<br>
	 * &#160;public void onError(String error) { }<br>
	 *
	 * &#160;@Override<br>
	 * &#160;public void onAlways(){ }<br> });
	 * </pre>
	 *  
	 */
	private void loginWithTwitter(BuiltAuthResultCallBack callback){
		builtAuthCallBack = callback;
		loginActivityInstance.startActivityForResult(new Intent(loginContext,BuiltTwitterLoginActivity.class),5000);
	}

	/**
	 * Provide the boolean value true if any edit text consist error in validation.
	 * 
	 * @return true/false
	 * 
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint">
	 * boolean hasError = builtloginObject.checkLoginFieldsValidation();
	 * </pre>
	 */
	private boolean checkLoginFieldsValidation(){
		boolean checkEmail = checkUserNameValidation();
		boolean checkPassWord = checkPasswordValidation();
		boolean checkTenant = checkTenantValidation();
		if(checkEmail || checkPassWord || checkTenant){
			return true;
		}
		return false;
	}

	/**
	 * Sets default logo image using source id and image scale type.
	 * 
	 * @param resId 
	 * 					identifier of the resource.
	 * 
	 * @param scaleType
	 * 					options for scaling the bounds of an image to the bounds of this view. 
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint">
	 * setLogoImageView(R.drawable.ic_built,ScaleType.CENTER);
	 * </pre>
	 */
	public void setLogoImageView(int resId, ScaleType scaleType){
		logoImageView.setImageResource(resId);
		if(scaleType != null){
			logoImageView.setScaleType(scaleType);
		}
	}

	/**
	 * Sets default close image using source id and image scale type.
	 * 
	 * @param resId 
	 * 					identifier of the resource.
	 * 
	 * @param scaleType
	 * 					options for scaling the bounds of an image to the bounds of this view. 
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
	 * Sets the logo image description. 
	 * Set this property to enable better accessibility support for your application.
	 * This is especially true for views that do not have textual representation (For example, ImageView).
	 * 
	 * @param contentDescription
	 * 							The content description.
	 * <br><b>Example</b><br>
	 *<pre class="prettyprint">
	 *setImageContentDescription("content description");
	 *</pre>
	 */
	public void setImageContentDescription(String contentDescription){
		if(contentDescription != null){
			logoImageView.setContentDescription(contentDescription);
		}
	}

	/**
	 * Sets the margins for provided view inside RelativeLayout.
	 * Provide values in integer. 
	 * 
	 * @param view
	 * 				set view.
	 * @param top
	 * 				set top Margin.
	 * @param bottom
	 * 				set bottom Margin.
	 * @param left
	 * 				set left Margin.
	 * @param right
	 * 				set right Margin.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint">
	 * setMargin(imageviewObject,100,100,100,100);
	 * </pre>
	 */
	public void setMargin(View view, int top, int bottom, int left, int right){

		try {
			int topMargin    = BuiltUtil.convertToPixel(loginContext, top);
			int bottomMargin = BuiltUtil.convertToPixel(loginContext, bottom);
			int leftMargin   = BuiltUtil.convertToPixel(loginContext, left);
			int rightMargin  = BuiltUtil.convertToPixel(loginContext, right);

			RelativeLayout.LayoutParams layoutparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutparam.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
			view.setLayoutParams(layoutparam);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Set progress dialog.
	 * 
	 * @param progressDialog
	 * 						progressDialog Object.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint">
	 * setProgressDialog(progressDialogObject);
	 * </pre>
	 */
	public void setProgressDialog(android.app.ProgressDialog progressDialog){
		if(progressDialog != null){
			this.isProgressDialog =  true;
			this.customProgressDialog = progressDialog;
		}
	}


	/**
	 * Set the consumer key and the consumer secret key.
	 * 
	 * @param consumerKey 
	 * 						  twitter application consumer key.
	 * @param consumerSecret 
	 * 						  twitter application consumer secret.
	 */
	public void setUpTwitter(String consumerKey, String consumerSecret){
		BuiltAppConstants.TWITTER_CONSUMER_KEY =  consumerKey;
		BuiltAppConstants.TWITTER_CONSUMER_SECRET = consumerSecret;
	}


	/**
	 * Set requestCode,resultCode and data.<br>
	 * This function is called in onActivityResult() inside Activity. 
	 * 
	 * @param requestCode
	 * 					 set requestCode from onActivityResult.
	 * 
	 * @param resultCode
	 * 					 set resultCode from onActivityResult.
	 * 
	 * @param data
	 * 					 set intent from onActivityResult.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint">
	 * &#160;@Override<br>
	 * &#160;protected void onActivityResult(int requestCode, int resultCode, Intent data){ <br>
	 * &#160;&#160;builtloginObject.setOnActivityResult(requestCode,resultCode,data);<br>
	 * }
	 * </pre>
	 */
	private void setOnActivityResult(int requestCode, int resultCode, Intent data){
		if(BuiltAppConstants.REQUEST_CODE_RECOVER_FROM_AUTH == requestCode && data != null && loginContext != null){
			if(resultCode == -1){
				new GetGoogleAccessToken(loginContext,loginActivityInstance,googleDelegate, selectedEmail, BuiltAppConstants.SCOPE,BuiltAppConstants.REQUEST_CODE_RECOVER_FROM_AUTH).execute();
			}
		}else if(BuiltAppConstants.REQUEST_CODE_TWITTER_SUCCESS == resultCode && loginContext != null){

			final BuiltUser builtUser = new BuiltUser();

			if(builtUserACL != null){
				builtUser.setACL(builtUserACL);
			}
			builtUser.loginWithTwitterAuthAccessToken(BuiltAppConstants.TWITTER_ACCESS_TOKEN, BuiltAppConstants.TWITTER_ACCESS_TOKEN_SECRET,
					BuiltAppConstants.TWITTER_CONSUMER_KEY, BuiltAppConstants.TWITTER_CONSUMER_SECRET, new BuiltResultCallBack() {

				@Override
				public void onSuccess() {
					if (builtAuthCallBack != null) {
						builtAuthCallBack.onComplete(builtUser);
					}
				}

				@Override
				public void onError(BuiltError error) {
					if (builtAuthCallBack != null) {
						builtAuthCallBack.onError(error);
					}
				}

				@Override
				public void onAlways() {
					if (builtAuthCallBack != null) {
						builtAuthCallBack.onAlways();
					}
				}
			});
		}else if(BuiltAppConstants.REQUEST_CODE_TWITTER_FAILED == resultCode ){
			if (builtAuthCallBack != null && data != null) {
				BuiltError error = new BuiltError();
				if (data.getExtras().get("error_message") != null && data.getExtras().get("error_code") != null) {
					error.setErrorMessage(data.getExtras().getString("error_message"));
					error.setErrorCode(data.getExtras().getInt("error_code"));
				}else{
					error.setErrorMessage(data.getExtras().getString("error_message"));
				}
				builtAuthCallBack.onError(error);
			}
		}
	}

	/*****************************************************************************************************************************************************************************
	 * 
	 ******************************* Private Methods  *************************************************************
	 * 
	 ****************************************************************************************/
	private String[] getEmailAccountNames() {
		try{
			AccountManager accountManager = AccountManager.get(loginContext);
			Account[] accounts = accountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
			String[] names = new String[accounts.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = accounts[i].name;
			}
			return names;
		}catch(Exception e){
			error = e.toString();
		}
		return null;
	} 

	private boolean checkTenantValidation() {
		if(isTenantEnable){
			tenantEditText.setError(null);
			String tenant = tenantEditText.getText().toString();

			if (TextUtils.isEmpty(tenant)) {
				tenantEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_REQUIRED_ERROR);
				tenantEditText.requestFocus();
				return true;
			} 
		}
		return false;
	}

	private boolean checkUserNameValidation() {

		emailEditText.setError(null);
		String email = emailEditText.getText().toString();

		if (TextUtils.isEmpty(email)) {
			emailEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_REQUIRED_ERROR);
			emailEditText.requestFocus();
			return true;
		} else if (!email.contains("@") && !email.contains(".")) {
			emailEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_VALIDATION_ERROR);
			emailEditText.requestFocus();
			return true;
		}
		return false;
	}

	private boolean checkPasswordValidation(){

		passwordEditText.setError(null);
		String password = passwordEditText.getText().toString();

		if (TextUtils.isEmpty(password)) {
			passwordEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_REQUIRED_ERROR);
			passwordEditText.requestFocus();
			return true;
		} else if (password.length() < 4) {

			passwordEditText.setError(BuiltAppConstants.CHECKED_FIELD_FOR_VALIDATION_ERROR);
			passwordEditText.requestFocus();
			return true;
		}
		return false;
	}

	private void googleLogin(IGoogleLoginDelegate googleLoginDelegate ){
		googleDelegate = googleLoginDelegate;
		final String[] emailArray = getEmailAccountNames();

		if(emailArray.length > 0){ 
			AlertDialog.Builder builder = new AlertDialog.Builder(loginContext); 
			builder.setTitle(BuiltAppConstants.GOOGLE_ACCOUNT).setItems(emailArray, new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int which) { 
					selectedEmail = emailArray[which];
					try{
						if(isProgressDialog){
							customProgressDialog.show();
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					new GetGoogleAccessToken(loginContext, loginActivityInstance, googleDelegate, emailArray[which], BuiltAppConstants.SCOPE, BuiltAppConstants.REQUEST_CODE_RECOVER_FROM_AUTH).execute(); 
				} 
			}); 
			builder.show(); 
		}else{ 
			AlertDialog.Builder builder = new AlertDialog.Builder(loginContext); 
			builder.setTitle(BuiltAppConstants.NO_GOOGLE_ACCOUNT).setMessage(BuiltAppConstants.NO_GOOGLE_ACCOUNT_ERROR).create().show();
			try{
				if(isProgressDialog){
					customProgressDialog.dismiss();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
	}

	private void forgotPassword(String email) {
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(BuiltAppConstants.FORGOT_PASSWORD);
		progressDialog.setMessage(BuiltAppConstants.PROGRESS_MESSAGE);

		try{
			progressDialog.show();
		}catch(Exception e){
			e.printStackTrace();
		}

		BuiltUser user = new BuiltUser();
		user.forgotPassword(email, new BuiltResultCallBack() {

			@Override
			public void onSuccess() {
				try{
					progressDialog.dismiss();
				}catch(Exception e){
					e.printStackTrace();
				}

				alertDialog = new AlertDialog.Builder(loginContext); 
				alertDialog.setIcon(R.drawable.ic_builtio_alertsuccess).setTitle(BuiltAppConstants.FORGOT_PASSWORD_ALERT)
				.setMessage(BuiltAppConstants.FORGOT_PASSWORD_ALERT_SUCCESS).create().show(); 
			}

			@Override
			public void onError(BuiltError error) {
				progressDialog.dismiss();
				alertDialog = new AlertDialog.Builder(loginContext); 
				alertDialog.setIcon(R.drawable.ic_builtio_alertclose).setTitle(BuiltAppConstants.FORGOT_PASSWORD_ALERT)
				.setMessage(error.getErrorMessage().toString()).create().show(); 
			}

			@Override
			public void onAlways() {
			}

		});
	}

	private void forgetPasswordCall(){

		forgetPasswordImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final String email = emailEditText.getText().toString().trim();

				if(email.isEmpty()){

					alertDialog = new AlertDialog.Builder(loginContext); 
					alertDialog.setIcon(R.drawable.ic_builtio_alertclose)
					.setTitle(BuiltAppConstants.FORGOT_PASSWORD_INCORRECT_USERNAME)
					.setMessage(BuiltAppConstants.FORGOT_PASSWORD_NO_USERNAME_ERROR)
					.setPositiveButton(BuiltAppConstants.FORGOT_PASSWORD_POSITIVE_BUTTON,new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.cancel();
							emailEditText.requestFocus();
						}
					}).create().show();

				}else if(!email.isEmpty()){

					alertDialog = new AlertDialog.Builder(loginContext);
					alertDialog.setIcon(android.R.drawable.ic_dialog_alert).setTitle(BuiltAppConstants.FORGOT_PASSWORD).setMessage(BuiltAppConstants.FORGOT_PASSWORD_MESSAGE)
					.setCancelable(false)
					.setPositiveButton(BuiltAppConstants.FORGOT_PASSWORD_YES_IN_DIALOG, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							forgotPassword(email);
						}
					})
					.setNegativeButton(BuiltAppConstants.FORGOT_PASSWORD_NO_IN_DIALOG, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = alertDialog.create();
					alert.show();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		setOnActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Provides the BuiltUser object after successful Login.
	 * @param user
	 * 			{@link BuiltUser} object
	 * 
	 *<br><b>Example</b><br> 
	 *<pre class="prettyprint">
	 *&#64;Override
	 *public void loginSuccess(BuiltUser user) {<br>
	 *   &#47;&#47;User can get Access tokens after success.<br>
	 *   String googleAccessToken = user.getGoogleAccessToken();<br>
	 *   String twitterAccessToken = user.getTwitterAccessToken();<br>
	 *   String twitterAccessTokenSecret = user.getTwitterAccessTokenSecret();<br>
	 *}
	 *
	 *</pre>
	 */
	public abstract void loginSuccess(BuiltUser user);

	/**
	 * Provides the BuiltError object after login process has failed.
	 * @param error
	 * 				{@link BuiltError} object
	 */
	public abstract void loginError(BuiltError error);


}
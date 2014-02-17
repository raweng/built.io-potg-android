package com.raweng.built.userInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.raweng.built.BuiltError;
import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.twitter4j.Twitter;
import com.raweng.twitter4j.TwitterFactory;
import com.raweng.twitter4j.auth.AccessToken;
import com.raweng.twitter4j.auth.RequestToken;
import com.raweng.twitter4j.conf.Configuration;
import com.raweng.twitter4j.conf.ConfigurationBuilder;
/**
 * Activity for twitter authentication.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltTwitterLoginActivity extends Activity {


	private static final String TAG = "BuiltTwitterLoginActivity";

	final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";

	private WebView twitterLoginWebView;
	private ProgressDialog progressDialog;

	private static Twitter twitter;
	private static RequestToken requestToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.builtio_twitter_login_activity);

		if(BuiltAppConstants.TWITTER_CONSUMER_KEY == null || BuiltAppConstants.TWITTER_CONSUMER_SECRET == null){
			
			BuiltTwitterLoginActivity.this.setResult(BuiltAppConstants.REQUEST_CODE_TWITTER_FAILED);
			BuiltTwitterLoginActivity.this.finish();
		}

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(BuiltAppConstants.PROGRESS_MESSAGE);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		twitterLoginWebView = (WebView)findViewById(R.id.twitter_login_web_view);
		twitterLoginWebView.setBackgroundColor(Color.TRANSPARENT);
		twitterLoginWebView.setWebViewClient( new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				if( url.contains(BuiltAppConstants.TWITTER_CALLBACK_URL)){
					Uri uri = Uri.parse(url);
					saveAccessTokenAndFinish(uri);
					return true;
				}
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				if(progressDialog != null) progressDialog.dismiss();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

				if(progressDialog != null) progressDialog.show();
			}
		});


		fetchTwitterAuth();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(progressDialog != null) {
			progressDialog.dismiss();}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void saveAccessTokenAndFinish(final Uri uri){
		new Thread(new Runnable() {
			@Override
			public void run() {
				String verifier = uri.getQueryParameter(IEXTRA_OAUTH_VERIFIER);
				try { 

					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
					BuiltAppConstants.TWITTER_ACCESS_TOKEN = accessToken.getToken();
					BuiltAppConstants.TWITTER_ACCESS_TOKEN_SECRET =  accessToken.getTokenSecret();
					BuiltTwitterLoginActivity.this.setResult(BuiltAppConstants.REQUEST_CODE_TWITTER_SUCCESS);
				} catch (Exception e) { 
					BuiltError builtError = new BuiltError();
					builtError.setErrorMessage(e.toString());
					Intent twitterAuthFail = new Intent();
					twitterAuthFail.putExtra("error_message", e.toString());
					BuiltTwitterLoginActivity.this.setResult(BuiltAppConstants.REQUEST_CODE_TWITTER_FAILED, twitterAuthFail);
				}
				BuiltTwitterLoginActivity.this.finish();
			}
		}).start();
	}

	private void fetchTwitterAuth() {		
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(BuiltAppConstants.TWITTER_CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(BuiltAppConstants.TWITTER_CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (BuiltAppConstants.isNetworkAvailable) {
						requestToken = twitter.getOAuthRequestToken(BuiltAppConstants.TWITTER_CALLBACK_URL);
					}else{
						Intent noNetworkIntent = new Intent();
						noNetworkIntent.putExtra("error_message", BuiltAppConstants.ErrorMessage_NoNetwork);
						noNetworkIntent.putExtra("error_code", BuiltAppConstants.NONETWORKCONNECTION);
						BuiltTwitterLoginActivity.this.setResult(BuiltAppConstants.REQUEST_CODE_TWITTER_FAILED, noNetworkIntent);
						BuiltTwitterLoginActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progressDialog.cancel();
								finish();
							}
						});
						return;
					}
					
				} catch (Exception error) {
					
					Intent twitterAuthFail = new Intent();
					twitterAuthFail.putExtra("error_message", error.getMessage());
					BuiltTwitterLoginActivity.this.setResult(BuiltAppConstants.REQUEST_CODE_TWITTER_FAILED, twitterAuthFail);
					BuiltTwitterLoginActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.cancel();
							finish();
						}
					});
					return;
				}

				BuiltTwitterLoginActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						twitterLoginWebView.loadUrl(requestToken.getAuthenticationURL());
					}
				});
			}
		}).start();
	}

}

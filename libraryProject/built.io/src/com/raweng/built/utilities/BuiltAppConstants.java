package com.raweng.built.utilities;

import java.util.ArrayList;

import android.net.Uri;



public class BuiltAppConstants {

	// CHECKS RESPONSE STATUS
	public static final int OK                	= 200;
	public static final int CREATED           	= 201;
	public static final int BAD_REQUEST     	= 400;
	public static final int UNAUTHORIZED    	= 401;
	public static final int INTERNAL_ERROR    	= 500;
	public final static int NONETWORKCONNECTION = 408;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// request method

	public static enum RequestMethod {

		GET ,POST, PUT, DELETE
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// CONNECTION TIMEOUT
	public static final int CONNECTION_TIMEOUT 		= 120000;
	public static final int CONNECTION_SO_TIMEOUT 	= 120000;
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// url
	public static String URL            = "api.built.io";
	public static String URLCloud       = "extension.built.io/api";
	public static String URLSCHEMA 		= "https://";
	public static String VERSION 		= "v1"; 


	public static String URLSCHEMA_HTTP 	= "http://";
	public static String URLSCHEMA_HTTPS 	= "https://";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*Directory path to store offline data. 
	 * used to saved cached network calls with response.
	 */
	public static String cacheFolderName;

	// session file name where user session data saved. 
	public static String sessionFileName;

	// installations file name where user installation data saved. 
	public static String installationFileName;

	// to store network call information initiate when connection not available.
	public static String offlineCallsFolderName;

	// if true shows built.io logs.
	public static boolean debug = false;

	// used to check network availability.
	public static boolean isNetworkAvailable = true;


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// 

	//Implemented for single network call cancellation. for class-level network call cancellation.
	public static ArrayList<String> cancelledCallController = new ArrayList<String>();

	public static enum callController{

		BUILTOBJECT, BUILTQUERY, BUILTUSER, BUILTINSTALLATION, BUILTDELTA, 
		BUILTROLE, BUILTROLROBJECT, BUILTANALYTICS, BUILTAPPLICATION, 
		BUILTNOTIFICATION, BUILTFILE, BUILTCLOUD
	}

	// list of upload media file async task instance.  
	public static ArrayList<Object> uploadAsyncInstanceList = new ArrayList<Object>();

	// list of upload media file async task instance.  
	public static ArrayList<Object> updateUploadAsyncInstanceList = new ArrayList<Object>();

	// sets to true if user want to cancel upload process.
	public static boolean cancelMediaFileUploadNetworkCalls = false;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	// error messages

	public final static String ErrorMessage_ClassUID 							= "Set class uid first.";
	public final static String ErrorMessage_ObjectUID 							= "Set object uid first.";
	public final static String ErrorMessage_NoNetwork 							= "Network not available.";
	public final static String ErrorMessage_NoFileToUpload 						= "File path not provided to upload file.";
	public final static String ErrorMessage_FilePATHINVALID 					= "File path is invalid.";
	public final static String ErrorMessage_CacheFileNotAvailable				= "Cache file not found.";
	public final static String ErrorMessage_NoSpaceForCacheFile	 				= "No external memory avaliable for offline support.";
	public final static String ErrorMessage_userIDIsNull 						= "User ID is null.";
	public final static String ErrorMessage_AuthtokenIsNull 					= "AuthToken is null.";
	public final static String ErrorMessage_ActivationTokenIsNull 				= "Activation token can not be null.";
	public final static String ErrorMessage_Default 							= "Oops! Something went wrong. Please try again.";
	public final static String ErrorMessage_LoginFirst 							= "User is not logged-in.";
	public final static String ErrorMessage_ApplicationKeyNotAvailable 			= "Application key not found.";
	public final static String ErrorMessage_RoleNotFound 						= "Role not found.";
	public final static String ErrorMessage_NOTAUTHORISED 						= "You are not authorised to carry out this modification.";
	public final static String ErrorMessage_ObjectNotFoundInCache 				= "Object not found in cache.";
	public final static String ErrorMessage_CalendarObjectIsNull 				= "Provide valid calender object.";
	public final static String ErrorMessage_RegTokenIsNull		 				= "GCM registration token is null.";
	public final static String ErrorMessage_InstallationUidIsNull				= "Installation uid is null.";
	public final static String ErrorMessage_InstallationResponse 				= "Error in installation call response. Please try again.";
	public final static String ErrorMessage_JsonNotProper		        		= "Please provide valid JSON.";
	public final static String ErrorMessage_QueryFilterException	   			= "Please provide valid params.";
	public final static String ErrorMessage_SavingNetworkCallForOfflineSupport	= "Error while saving network call.";
	public final static String ErrorMessage_SavingNetworkCallResponseForCache 	= "Error while saving network call response.";
	public final static String ErrorMessage_RegisteringUser 	                = "Please provide valid data.";
	public final static String ErrorMessage_ProvideValidEmailId 	            = "Please provide valid email id.";
	public final static String ErrorMessage_UploadUidIsNull		                = "Set media file upload uid first.";
	public final static String ErrorMessage_RoleUidIsNull		                = "Set role uid first.";
	public final static String ErrorMessage_NotificationMessageIsNull		    = "Notification message can not be blank.";
	public final static String ErrorMessage_NotificationUserUidIsNull		    = "User's list can not be blank.";
	public final static String ErrorMessage_UserEmailIdIsNull		            = "Email id can not be blank";
	public final static String ErrorMessage_UserPasswordIsNull		            = "Password can not be blank";
	public final static String ErrorMessage_GoogleAuthTokenIsNull		        = "Google authToken can not be blank";
	public final static String ErrorMessage_FacebookAccessTokenIsNull		    = "Facebook access token can not be blank";
	public final static String ErrorMessage_FacebookUserIdIsNull		        = "Facebook user id can not be blank";
	public final static String ErrorMessage_TwitterAccessTokenIsNull		    = "Twitter access token can not be blank";
	public final static String ErrorMessage_TwitterAccessTokenSecretIsNull		= "Twitter access token secret can not be blank";
	public final static String ErrorMessage_tibbrAccessTokenIsNull		        = "tibbr access token can not be blank";
	public final static String ErrorMessage_tibbrUserIDIsNull		            = "tibbr user id can not be blank";
	public final static String ErrorMessage_tibbrHostNameIsNull		            = "tibbr host name can not be blank";
	public final static String ErrorMessage_UesrInfoHashMapIsNull		        = "Provide valid user information";
	public final static String ErrorMessage_CalledBuiltDefaultMethod		    = "You must called Built.initializeWithApiKey()first";
	public final static String ErrorMessage_ApplicationContextIsNull		    = "Context can not be null.";
	public final static String ErrorMessage_ApplicationApiKeyIsNull		        = "Application api key can not be null.";
	public final static String ErrorMessage_ApplicationUidIsNull		        = "Application uid can not be null.";
	public final static String ErrorMessage_LocationSettingIsDisable		    = "Please enable location settings.";
	public final static String ErrorMessage_ActivityObjectIsNull		        = "Activity instance can not be null.";
	public final static String ErrorMessage_callbackIsNull		                = "BuiltLocationCallback instance can not be null.";
	public final static String ErrorMessage_UnknownActivityRequestCode	        = "Received an unknown activity request code %1$d in onActivityResult.";
	public final static String ErrorMessage_CurrentLocationConnected 			= "Client connected";
	public final static String ErrorMessage_CurrentLocationErrorResloved 		= "Error resolved. Please re-try operation."; 
	public final static String ErrorMessage_CurrentLocationErrorNotResloved 	= "Google Play services: unable to resolve connection error.";
	public final static String ErrorMessage_CloudLogicIdIsNull				    = "Set cloud logic id first.";
	public final static String ErrorMessage_BuitUserObjectIsNull 				= "builtUser object is null.";
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// messages related to userInterface package.

	public static final int REQUEST_CODE_RECOVER_FROM_AUTH 					= 1001;
	public static final int REQUEST_CODE_TWITTER_SUCCESS 					= 5000;
	public static final int REQUEST_CODE_TWITTER_FAILED 					= 5001;

	public static final String SCOPE 										= "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	public static final String PULL_DOWN_REFRESH_LABEL 						= "Refreshing...";
	public static final String PULL_UP_REFRESH_LABEL 						= "See more...";
	public static final String SEARCH_HINT 									= "\t\tSearch ";
	public static final String SEARCH_HINT_NOT_PRESENT						= "field uid missing please set";

	public static final String PROGRESS_MESSAGE 							= "Please wait...";

	public static final String FORGOT_PASSWORD_MESSAGE						= "Forgot your password?";
	public static final String FORGOT_PASSWORD_INCORRECT_USERNAME 			= "Incorrect username";
	public static final String FORGOT_PASSWORD_NO_USERNAME_ERROR 			= "Username should be filled before proceeding further";
	public static final String FORGOT_PASSWORD 								= "Forgot Password";
	public static final String FORGOT_PASSWORD_POSITIVE_BUTTON 				= "Go For,Login";
	public static final String FORGOT_PASSWORD_YES_IN_DIALOG 				= "Yes, I forgot";
	public static final String FORGOT_PASSWORD_NO_IN_DIALOG 				= "No, I Know it";
	public static final String FORGOT_PASSWORD_ALERT 						= "Password reset failed!";
	public static final String FORGOT_PASSWORD_ALERT_SUCCESS 				= "You will receive email shortly with link to reset password of your account. Do check your mail !";

	public static final String GOOGLE_ACCOUNT 								= "Accounts";
	public static final String NO_GOOGLE_ACCOUNT 							= "No accounts found";
	public static final String NO_GOOGLE_ACCOUNT_ERROR 						= "Please login into a gmail app";

	public static final String CHECKED_FIELD_FOR_REQUIRED_ERROR 			= "Field cannot be blank";
	public static final String CHECKED_FIELD_FOR_VALIDATION_ERROR 		    = "Provide appropriate value";
	public static final String CHECKED_FIELD_FOR_MISMATCH_ERROR 			= "Field mismatch";
	public static final String TWITTER_CONSUMER_KEY_SECRET_NULL             = "Consumer key and consumer secret is null.";
	public static final String TWITTER_AUTHENTICATION_FAILED                = "Twitter authentication failed.";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Uri IMAGE_CAPTURE_URI = null;

	// TWITTER OAUTH

	public static String TWITTER_ACCESS_TOKEN         = "";
	public static String TWITTER_ACCESS_TOKEN_SECRET  = "";
	public static String TWITTER_CONSUMER_KEY         = "";
	public static String TWITTER_CONSUMER_SECRET      = "";


	public static final String TWITTER_CALLBACK_URL = "x-oauthflow-twitter://twitterlogin";



}

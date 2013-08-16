package com.raweng.built;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.RawAppUtils;

/**
 * To fetch current location.
 * 
 * @author raw engineering, Inc
 *
 */
class CurrentLocation extends FragmentActivity implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	Context context;
	Activity activityInstance;
	static CurrentLocation currentLocation = null;
	private LocationClient mLocationClient;
	private BuiltLocationCallback locationCallbackInstance = null;

	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	CurrentLocation currentLocationObject(Context context){
		this.context = context;
		
		return this;
	}

	public void getCurrentLocation(Context context, Activity activity, BuiltLocationCallback callback) {

		if(context != null && activity != null && callback != null){
			currentLocation = currentLocationObject(context);
			this.context = context;
			activityInstance = activity;
			mLocationClient = new LocationClient(context, this, this);
			mLocationClient.connect();
			locationCallbackInstance = callback;
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						activityInstance,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException error) {

				throwsException(error.toString());
			}
		} else {

			// If no resolution is available, display a dialog to the user with the error.
			showErrorDialog(connectionResult.getErrorCode());
		}

	}

	/*
	 * Handle results returned to this Activity by other Activities started with
	 * startActivityForResult(). In particular, the method onConnectionFailed() in
	 * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
	 * start an Activity that handles Google Play services problems. The result of this
	 * call returns here, to onActivityResult.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		// Choose what to do based on the request code
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case CONNECTION_FAILURE_RESOLUTION_REQUEST :

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				RawAppUtils.showLog("CurrentLocation", BuiltAppConstants.ErrorMessage_CurrentLocationConnected);
				RawAppUtils.showLog("CurrentLocation", BuiltAppConstants.ErrorMessage_CurrentLocationErrorResloved);
				break;

				// If any other result was returned by Google Play services
			default:
				// Log the result
				RawAppUtils.showLog("CurrentLocation", BuiltAppConstants.ErrorMessage_CurrentLocationErrorNotResloved);
				break;
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			RawAppUtils.showLog("CurrentLocation", BuiltAppConstants.ErrorMessage_UnknownActivityRequestCode);
			break;
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		
			Location mCurrentLocation = mLocationClient.getLastLocation();
			if(mCurrentLocation != null){
				if(locationCallbackInstance != null){
					BuiltLocation builtLocation = new BuiltLocation();
					builtLocation.setLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
					locationCallbackInstance.onRequestFinish(builtLocation);
					mLocationClient.disconnect();
				}else{
					mLocationClient.disconnect();
				}
			}else{
				mLocationClient.disconnect();
				throwsException(BuiltAppConstants.ErrorMessage_LocationSettingIsDisable);
			}
		

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * Show a dialog returned by Google Play services for the
	 * connection error code
	 *
	 * @param errorCode An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
				errorCode,
				activityInstance,
				CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			errorDialog.show();
		}
	}

	private  void throwsException(String error) {
		if(locationCallbackInstance != null){
			BuiltError builtError = new BuiltError();
			builtError.errorMessage(error);
			locationCallbackInstance.onRequestFail(builtError);
		}
	}
}

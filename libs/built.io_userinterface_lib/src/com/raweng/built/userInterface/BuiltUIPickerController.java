package com.raweng.built.userInterface;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltUtil;


/**
 * 
 * Helper class to select a file, click image or record video .
 * 
 * @author raw engineering, Inc
 *  
 * <p>
 * User needs to add following entries in manifest.
 * <li><a href="http://docs.built.io/quickstart/index#android">Add activity {@link UIAndroidExplorerScreen} to your manifest.</a></li><br>
 * <li><a href="http://docs.built.io/quickstart/index#android">Add activity {@link BuiltTwitterLoginActivity} to your manifest.</a></li><br>
 * <li><a href="http://developer.android.com/reference/android/Manifest.permission.html#WRITE_EXTERNAL_STORAGE">Allows an application to write to external storage.</a></li><br>
 * </p>
 * 
 * <h2>Usage</h2>
 * Following code snippet shows how to process the result on calling {@link Activity} instance.
 * <pre class="prettyprint">
 * 
 * BuiltUIPickerController pickerObject = new BuiltUIPickerController(activity);
 *	try {
 *	pickerObject.showPicker(true);
 *	} catch (Exception error) {
 *		error.printStackTrace();
 *	}
 *
 *  &#64;Override<br>
 *	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *		super.onActivityResult(requestCode, resultCode, data);
 *
 *		if(requestCode == PickerResultCode.SELECT_FROM_FILE_SYSTEM_REQUEST_CODE.getValue() && data != null){
 *			if(resultCode == RESULT_OK){
 *
 *				Log.i("BuiltUIPickerController",(String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));
 *				Log.i("BuiltUIPickerController", (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileName"));
 *				Log.i("BuiltUIPickerController",Long.toString((Long) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileSize")));
 *
 *			}else if(resultCode == RESULT_CANCELED){
 *				Log.i("BuiltUIPickerController", "Nothing selected");
 *			}
 *		}else if(requestCode == PickerResultCode.SELECT_IMAGE_FROM_GALLERY_REQUEST_CODE.getValue() && data != null){
 *			if(resultCode == RESULT_OK){
 *
 *				Log.i("BuiltUIPickerController",(String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));
 *				Log.i("BuiltUIPickerController", (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileName"));
 *				Log.i("BuiltUIPickerController",Long.toString((Long) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileSize")));
 *
 *			}else if(resultCode == RESULT_CANCELED){
 *				Log.i("BuiltUIPickerController", "Nothing selected");
 *			}
 *		}else if(requestCode == PickerResultCode.CAPTURE_IMAGE_REQUEST_CODE.getValue()){
 *			if(resultCode == RESULT_OK){
 *
 *				Log.i("BuiltUIPickerController",(String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));
 *				Log.i("BuiltUIPickerController", (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileName"));
 *				Log.i("BuiltUIPickerController",Long.toString((Long) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileSize")));
 *
 *			}else if(resultCode == RESULT_CANCELED){
 *				Log.i("BuiltUIPickerController", "Nothing selected");
 *			}
 *		}else if(requestCode == PickerResultCode.CAPTURE_VIDEO_REQUEST_CODE.getValue() && data != null){
 *			if(resultCode == RESULT_OK){
 *
 *				Log.i("BuiltUIPickerController",(String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));
 *				Log.i("BuiltUIPickerController", (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileName"));
 *				Log.i("BuiltUIPickerController",Long.toString((Long) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("fileSize")));
 *
 *
 *			}else if(resultCode == RESULT_CANCELED){
 *				Log.i("BuiltUIPickerController", "Nothing selected");
 *			}
 *		}
 *	}
 * 
 * </pre>
 *
 */

public class BuiltUIPickerController {

	private final String TAG = "BuiltUIPickerController";
	private Activity activity;
	public Uri uri;
	int maxFileSize = 10;

	/**
	 * Constructor.
	 * 
	 * @param activity	
	 * 					{@link Activity} instance.
	 */
	public BuiltUIPickerController(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Display a dialog with options to click image, record video, select image from gallery and select a file.
	 * 
	 * @param showFileExplorer 
	 * 					if true &#58;&#45; It will enable simple file manager option to select a file. 
	 * 
	 */
	public void showPicker(boolean showFileExplorer) throws Exception {
		final Dialog chooseImageDialogBox = new Dialog(this.activity);
		chooseImageDialogBox.setContentView(R.layout.builtio_dialog_box_builtpicker);
		chooseImageDialogBox.setTitle(activity.getString(R.string.builtio_choose_options_title));
		Button chooseLibrary = (Button) chooseImageDialogBox.findViewById(R.id.choose_library);
		Button captureVideo  = (Button) chooseImageDialogBox.findViewById(R.id.capture_video);

		if (showFileExplorer){

			chooseLibrary.setVisibility(Button.VISIBLE);
			captureVideo.setVisibility(Button.VISIBLE);
		}

		Button chooseGallery    = (Button) chooseImageDialogBox.findViewById(R.id.choose_frmgallery);
		Button chooseClickPhoto = (Button) chooseImageDialogBox.findViewById(R.id.click_pic);
		chooseImageDialogBox.show();

		chooseGallery.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				chooseImageDialogBox.dismiss();
				if (BuiltUtil.isSdPresent()) {
					Intent i = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); 
					i.setType("image/*");
					BuiltUIPickerController.this.activity.startActivityForResult(Intent.createChooser(i, activity.getString(R.string.builtio_select_picture_text)), PickerResultCode.SELECT_IMAGE_FROM_GALLERY_REQUEST_CODE.ordinal());
				} else {
					Toast.makeText(activity, activity.getString(R.string.builtio_sdcard_not_found_error_text), Toast.LENGTH_SHORT).show();
				}
			}
		});


		chooseClickPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				chooseImageDialogBox.dismiss();
				if (BuiltUtil.isSdPresent()){
					//define the file-name to save photo taken by Camera activity
					String fileName = "new-photo-name.jpg";

					//create parameters for Intent with filename
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, fileName);
					values.put(MediaStore.Images.Media.DESCRIPTION, activity.getString(R.string.builtio_image_capture_by_camera_text));			
					values.put(MediaStore.Images.Media.ORIENTATION,Configuration.ORIENTATION_LANDSCAPE);

					uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					if(uri != null){
						BuiltAppConstants.IMAGE_CAPTURE_URI = uri;
					}
					Intent intent =	new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					intent.putExtra("imageUri", uri);
					BuiltUIPickerController.this.activity.startActivityForResult(intent, PickerResultCode.CAPTURE_IMAGE_REQUEST_CODE.ordinal());
				} else {
					Toast.makeText(activity, activity.getString(R.string.builtio_sdcard_not_found_error_text), Toast.LENGTH_SHORT).show();
				}
			}
		});

		chooseLibrary.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				chooseImageDialogBox.dismiss();
				if (BuiltUtil.isSdPresent()) {
					BuiltUIPickerController.this.activity.startActivityForResult(new Intent(BuiltUIPickerController.this.activity, UIAndroidExplorerScreen.class), PickerResultCode.SELECT_FROM_FILE_SYSTEM_REQUEST_CODE.ordinal());
				} else {
					Toast.makeText(activity, activity.getString(R.string.builtio_sdcard_not_found_error_text), Toast.LENGTH_SHORT).show();
				}
			}
		});

		captureVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseImageDialogBox.dismiss();
				if (BuiltUtil.isSdPresent()) {					
					Toast.makeText(activity, activity.getString(R.string.builtio_capture_video_text), Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					activity.startActivityForResult(intent, PickerResultCode.CAPTURE_VIDEO_REQUEST_CODE.ordinal());
				} else {
					Toast.makeText(activity, activity.getString(R.string.builtio_sdcard_not_found_error_text), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}


	/**
	 * Converts Uri to string
	 * 
	 * @param imageUri 
	 * 					{@link #uri}
	 * 
	 * @param activity
	 * 					{@link Activity}
	 * @return
	 * 					file path of image.
	 */
	private String convertImageUriToFile(Uri imageUri, Activity activity) {
		try {
			Cursor cursor = null;
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.ORIENTATION };

			cursor = activity.getContentResolver().query(imageUri, proj, null, null, null);
			int file_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.moveToFirst()) {  
				return cursor.getString(file_ColumnIndex);
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
		return null;
	}


	/**
	 * Converts Uri to string
	 * 
	 * @param cameraVideoUri 
	 * 					{@link #uri}
	 * 
	 * @param activity
	 * 					{@link Activity}
	 * @return
	 * 					file path of image.
	 */

	private String convertVideo(Uri cameraVideoUri, Activity activity) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Video.Media.DATA};
			cursor = activity.getContentResolver().query(cameraVideoUri, proj, // Which
					// columns
					// to return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null); // Order-by clause (ascending by name)

			int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

			if (cursor.moveToFirst()) {
				String recordedVideoFilePath = cursor.getString(columnIndexData);
				return recordedVideoFilePath;
			}

		}catch (Exception error) {
			error.printStackTrace();
		}
		return null;
	}


	/**
	 * To get file path, file name and file size.
	 * 
	 * @param data
	 * 				{@link Intent} object.
	 * 
	 * @param pickerRequestCode
	 * 						request code.
	 * @return
	 * 				{@link HashMap} object contains file&#39;s path and file&#39;s name and the key for hashmap are
	 *  <b> &#34;filePath&#34;</b> , <b>&#34;fileName&#34;</b>  and <b>  &#34;fileSize&#34; </b> in bytes.
	 */
	public HashMap<Object, Object> getFileInfoForMediaFile(Intent data ,int pickerRequestCode) {
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

		String filePath = "";
		String fileName = "";
		File assetFile ;
		if(pickerRequestCode == PickerResultCode.CAPTURE_IMAGE_REQUEST_CODE.getValue()){

			filePath  = convertImageUriToFile(BuiltAppConstants.IMAGE_CAPTURE_URI, activity);
			fileName  = filePath.substring(filePath.lastIndexOf("/"), filePath.length());
			assetFile = new File(filePath);

			hashMap.put("filePath", filePath);
			hashMap.put("fileName", fileName);
			hashMap.put("fileSize", assetFile.length());

		}else if(pickerRequestCode == PickerResultCode.CAPTURE_VIDEO_REQUEST_CODE.getValue()){

			filePath  = convertVideo(data.getData(),activity);
			fileName  = filePath.substring(filePath.lastIndexOf("/"), filePath.length());
			assetFile = new File(filePath);

			hashMap.put("filePath", filePath);
			hashMap.put("fileName", fileName);
			hashMap.put("fileSize", assetFile.length());

		}else if(pickerRequestCode == PickerResultCode.SELECT_IMAGE_FROM_GALLERY_REQUEST_CODE.getValue()){
			Uri selectedImage = data.getData();
			if (selectedImage != null) {
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = activity.getContentResolver().query(selectedImage, proj, null, null, null);
				if (cursor != null) {
					int column_index = cursor.getColumnIndexOrThrow(proj[0]);
					cursor.moveToNext();
					if (!cursor.isNull(column_index)) {
						filePath = cursor.getString(column_index);
						fileName = filePath.substring(filePath.lastIndexOf("/"), filePath.length());
						assetFile = new File(filePath);
						hashMap.put("filePath", filePath);
						hashMap.put("fileName", fileName);
						hashMap.put("fileSize", assetFile.length());
					}
				}else{
					Log.i(TAG, "Error reading file");
				}
				cursor.close();
			}

		}else if(pickerRequestCode == PickerResultCode.SELECT_FROM_FILE_SYSTEM_REQUEST_CODE.getValue()){
			if (data.hasExtra("fileName") && data.hasExtra("filePath")) {
				fileName = data.getStringExtra("fileName");
				filePath = data.getStringExtra("filePath");

				if(filePath.contains("/mnt")){
					filePath = filePath.substring(filePath.indexOf("/mnt")+4);
				}                
				assetFile = new File(filePath);
				hashMap.put("filePath", filePath);
				hashMap.put("fileName", fileName);
				hashMap.put("fileSize", assetFile.length());
			}

		}
		return hashMap;
	}


	/**
	 * BuiltUIPickerController request code
	 * 
	 * @author raw engineering, Inc
	 */
	public static enum PickerResultCode {

		/**
		 * To fetch image from gallery.
		 */

		SELECT_IMAGE_FROM_GALLERY_REQUEST_CODE(0), 

		/**
		 * To capture image from camera.
		 */

		CAPTURE_IMAGE_REQUEST_CODE(1),

		/**
		 * To capture video from camera.
		 */

		CAPTURE_VIDEO_REQUEST_CODE(2), 

		/**
		 * To fetch file from file-system.
		 */
		SELECT_FROM_FILE_SYSTEM_REQUEST_CODE(3);

		final int resultCode;

		private PickerResultCode(int resultCode){
			this.resultCode = resultCode;
		}

		public int getValue(){
			return this.resultCode;
		}

	}
}

package com.raweng.projectsonthego;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.BuildFileResultCallback;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltFile;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.FileObject;
import com.raweng.built.userInterface.BuiltUIPickerController;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.Utilities.FlowLayout;

/**
 * To create a bug.
 * 
 * @author raw engineering, Inc
 *
 */
/**
 * @author bostan
 *
 */
/**
 * @author bostan
 *
 */
public class UICreateBugScreen extends FragmentActivity implements IFetchUserList {

	private static final String BUG_CLASS_UID = "bugs";
	private final String TAG = "UICreateBugScreen";
	private final Context context = UICreateBugScreen.this;

	EditText bugTitleEditText;
	EditText bugDescribtionEditText;
	EditText bugDueDateEditText;

	DatePicker datepicker;

	Button attachmentFileButton;
	Button bugAddUserButton;
	ProgressDialog progressDialog;

	Spinner  bugStatusSpinner;
	Spinner  bugSeveritySpineer;
	Spinner  bugReproducibleSpineer;

	FlowLayout userContainerFlowLayout;
	FlowLayout addAttachmentFlowLayout;

	String userType   = null;
	String projectUid = null;
	String moderatorRoleUid = null;
	String memberRoleUid = null;
	String[] assigneesUid;

	BuiltUIPickerController picker;


	BuiltFile builtfile = new BuiltFile();

	/**
	 * Temporary hashmap to store successfully created bugs.
	 */
	public static HashMap<Object, Object> createBugHashMap; 

	HashMap<String,String> attchmentsFileName = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_bug);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		bugAddUserButton		= (Button)   findViewById(R.id.createBugAddUserButton);
		attachmentFileButton 	= (Button)   findViewById(R.id.createBugAttachfile);		
		bugTitleEditText       	= (EditText) findViewById(R.id.createBugName);
		bugDescribtionEditText 	= (EditText) findViewById(R.id.createBugDesc);
		bugDueDateEditText     	= (EditText) findViewById(R.id.createBugDueDate);
		bugStatusSpinner 		= (Spinner)  findViewById(R.id.createBugspinnerStatus);
		bugSeveritySpineer 		= (Spinner)  findViewById(R.id.createBugspinnerSeverity);
		bugReproducibleSpineer  = (Spinner)  findViewById(R.id.createBugspinnerReproducible);
		userContainerFlowLayout = (FlowLayout)findViewById(R.id.createBugUserContainer);
		addAttachmentFlowLayout	= (FlowLayout)findViewById(R.id.createBugAttachmentContainer);

		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);


		createBugHashMap = new HashMap<Object, Object>();
		userType         = AppSettings.getUserType(context);

		Bundle args = getIntent().getExtras();
		if (args != null) {
			projectUid = args.getString("projectUID"); 
			moderatorRoleUid  = args.getString("moderatorsRoleUid"); 
			memberRoleUid	  = args.getString("membersRoleUid"); 
		}


		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.status, android.R.layout.simple_spinner_item);
		bugStatusSpinner.setAdapter(adapter);
		adapter = ArrayAdapter.createFromResource(context,R.array.severity, android.R.layout.simple_spinner_item);
		bugSeveritySpineer.setAdapter(adapter);
		adapter = ArrayAdapter.createFromResource(context,R.array.reproducible, android.R.layout.simple_spinner_item);
		bugReproducibleSpineer.setAdapter(adapter);

		SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = new Date();
		String date = timeStampFormat.format(myDate);
		bugDueDateEditText.setText(date);


		bugAddUserButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addAssignees();
			}
		});

		attachmentFileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Activity activity = UICreateBugScreen.this;
				picker = new BuiltUIPickerController(activity);
				try {
					picker.showPicker(true);
				} catch (Exception e) {
					AppUtils.showLog(TAG,e.toString());
				}
			}
		});

		//If a due date edit text gets focus then it will open  Dialog to set due date.
		bugDueDateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1) {
					bugDueDateEditText.performClick();
				}
			}
		});

		bugDueDateEditText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Dialog datedialog = new Dialog(context);
				datedialog.setTitle(getString(R.string.set_due_date));
				datepicker = new DatePicker(context);
				datepicker.setCalendarViewShown(false);
				datepicker.setBackgroundColor(Color.GRAY);
				datedialog.addContentView(datepicker, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

				datedialog.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface arg0) {
						int month = datepicker.getMonth()+1;
						int day = datepicker.getDayOfMonth();
						String dateString = null;

						if(month < 10 && day > 9){
							dateString = datepicker.getYear()+"-0"+month+"-"+day;
						}else if(month > 10 && day < 10){
							dateString = datepicker.getYear()+"-"+month+"-0"+day;
						}else if(month < 10 && day < 10){
							dateString = datepicker.getYear()+"-0"+month+"-0"+day;
						}else{
							dateString = datepicker.getYear()+"-0"+month+"-0"+day;
						}
						bugDueDateEditText.setText(dateString);
					}
				});
				datedialog.show();
			}
		});
	}

	/**
	 * This method uploads the attachment in the bug. 
	 *
	 */
	@SuppressWarnings("rawtypes")
	protected void fileAttachmentCall() {
		progressDialog.show();

		final ArrayList<String> attachmentsUid = new ArrayList<String>();
		if(attchmentsFileName.isEmpty()){
			updateBug(attachmentsUid);

		}else{
			Set set = (Set) attchmentsFileName.entrySet();
			Iterator iterator = set.iterator();

			while (iterator.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) iterator.next();

				FileObject object = new FileObject();
				object.setFile((String) mapEntry.getValue());
				builtfile.addFile((String) mapEntry.getKey(), object);
			}
			//Upload attachment files.
			builtfile.save(new BuildFileResultCallback() {

				@Override
				public void onSuccess(HashMap<String, FileObject> uploadFileResult) {
					try{
						Set set = (Set) attchmentsFileName.entrySet();
						Iterator iterator = set.iterator();
						while (iterator.hasNext()) {

							Map.Entry mapEntry = (Map.Entry) iterator.next();
							attachmentsUid.add(uploadFileResult.get((String) mapEntry.getKey()).getUploadUid());

						}
					}catch(Exception e){
						AppUtils.showLog(TAG,e.toString());
					}
				}

				@Override
				public void onError(BuiltError error) {
					AppUtils.showLog(TAG,error.getErrorMessage());
				}

				@Override
				public void onAlways() {
					updateBug(attachmentsUid);
				}
			});
		}
	}


	/**
	 * Checks for empty field
	 */
	protected boolean performValidation() {
		bugTitleEditText.setError(null);
		boolean cancel = false;

		if (TextUtils.isEmpty(bugTitleEditText.getText().toString())) {
			bugTitleEditText.setError(getString(R.string.error_field_required));
			bugTitleEditText.requestFocus();
			cancel = true;
		} 
		return cancel;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action, menu);

		MenuItem deleteBug= menu.findItem(R.id.delete);
		deleteBug.setVisible(false);

		MenuItem commentBug= menu.findItem(R.id.comment);
		commentBug.setVisible(false);

		MenuItem updateBug= menu.findItem(R.id.bugaction);
		updateBug.setTitle(getString(R.string.create));

		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			hideKeyboard();
			return true;

		case R.id.bugaction:
			if(!performValidation()){
				fileAttachmentCall();
			}
		}
		return false;

	}


	/**
	 * hides the  keyboard for all the edit texts in this class
	 */
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(bugTitleEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(bugDescribtionEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(bugDueDateEditText.getWindowToken(), 0);
	}

	/**
	 * This method is used to call the {@link UserListDialogFragment}. 
	 *
	 */
	private void addAssignees() {
		FragmentManager fragmentManager = getFragmentManager();
		UserListDialogFragment userListDialog = new UserListDialogFragment();
		userListDialog.iFetchUserList = UICreateBugScreen.this;
		userListDialog.show(fragmentManager, getString(R.string.fragment_edit_name));
	}

	/**
	 * Set icon depending on the file type.
	 * 
	 */
	private void setFileIcon(final String fileName, String filePath){

		String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());	
		final ImageView imageView = new ImageView(context);
		attchmentsFileName.put(fileName,filePath);

		if(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")){
			imageView.setBackgroundResource(R.drawable.jpgformat);

		}else if(extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")){
			imageView.setBackgroundResource(R.drawable.docxformat);

		}else if(extension.equalsIgnoreCase("pdf")){
			imageView.setBackgroundResource(R.drawable.pdfformat);

		}else if(extension.equalsIgnoreCase("png")){
			imageView.setBackgroundResource(R.drawable.pngformat);

		}else if(extension.equalsIgnoreCase("mp3")){
			imageView.setBackgroundResource(R.drawable.mp3format);

		}else if(extension.equalsIgnoreCase("gif")){
			imageView.setBackgroundResource(R.drawable.ic_gif_format);

		}else{
			imageView.setBackgroundResource(R.drawable.abstractfile);
		}
		addAttachmentFlowLayout.addView(imageView);

		//remove the selected attachment.
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				addAttachmentFlowLayout.removeViewInLayout(imageView);
				addAttachmentFlowLayout.invalidate();
				attchmentsFileName.remove(fileName);
			}
		});
	}

	/**
	 * This method create a bug. 
	 * 
	 * @param attachmentUid
	 * 						attached files uid pass to add in reference
	 */
	protected void updateBug(ArrayList<String> attachmentUid){
		//Create a bug object.
		final BuiltObject object = new BuiltObject(BUG_CLASS_UID);

		//Set values for fields.
		object.set("name", bugTitleEditText.getText().toString());

		if(bugDescribtionEditText.length() > 0){
			object.set("description", bugDescribtionEditText.getText().toString());
		}

		object.set("reproducible",  bugReproducibleSpineer.getSelectedItem().toString());

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String time = timeFormat.format(new Date());
		object.set("due_date", bugDueDateEditText.getText().toString()+"T"+time);

		object.set("severity", bugSeveritySpineer.getSelectedItem().toString());
		object.set("status",  bugStatusSpinner.getSelectedItem().toString());
		object.set("assignees", assigneesUid);

		//Set attached files uid as reference to bug.
		if(attachmentUid != null){
			final String[] attachedFileUid = new String[attachmentUid.size()];
			for(int i =0; i<attachmentUid.size(); i++){
				attachedFileUid[i] = attachmentUid.get(i);

			}
			object.set("attachments",  attachedFileUid);
		}

		String[] uidArray = new String[]{projectUid};

		//To set project reference to this bug.
		object.setReference("project",uidArray);

		try {
			BuiltACL bugACL = new BuiltACL();
			//Member have read access for a bug. Moderators have read, update, and delete access for a bug.
			//Set permission for team_memberRoleUid && moderatorRoleUid 
			if(memberRoleUid != null){
				bugACL.setRoleReadAccess(memberRoleUid, true);
			}
			if(moderatorRoleUid != null){
				bugACL.setRoleReadAccess(moderatorRoleUid, true);
				bugACL.setRoleWriteAccess(moderatorRoleUid, true);
				bugACL.setRoleDeleteAccess(moderatorRoleUid, true);
			}
			//Provide read and write access to assignees.
			if(assigneesUid!= null &&  assigneesUid.length > 0){
				for(int i=0; i<assigneesUid.length; i++){
					bugACL.setUserReadAccess(assigneesUid[i].toString(), true);
					bugACL.setUserWriteAccess(assigneesUid[i].toString(), true);
				}
			}

			//Bug can be created by anyone.
			bugACL.setPublicReadAccess(true);
			bugACL.setPublicWriteAccess(true);

			//Set ACL to this bug.
			object.setACL(bugACL);
		} catch(Exception e) {
			AppUtils.showLog(TAG,e.toString());
		}

		//Save the bug object.
		object.save(new BuiltResultCallBack() {
			@Override
			public void onSuccess() {		
				System.out.println("----------------||"+object.toJSON());
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);		    

				if(object.has("uid")){
					try {
						object.setUid(object.getString("uid").toString());
						createBugHashMap.put("builtObject", object);
					} catch (Exception e) {
						AppUtils.showLog(TAG,e.toString());
					}
				}

				Toast.makeText(context,getString(R.string.bug_created_successfully),Toast.LENGTH_LONG).show();
				Intent resultIntent = new Intent();
				setResult(Activity.RESULT_OK, resultIntent);

				//hide keyboard
				hideKeyboard();
				finish();

			}

			@Override
			public void onAlways() {
				progressDialog.dismiss();
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG,error.getErrorMessage());
				Toast.makeText(context,error.getErrorCode()+" : "+error.getErrorMessage(),Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void fetchUserList(ArrayList<UserModel> objects, boolean isModeratorList) {

		assigneesUid = new String[objects.size()];
		int count = objects.size();

		for(int i = 0; i < count; i++){
			TextView emailIdView = new TextView(context);
			emailIdView.setBackgroundResource(R.drawable.bubble_background);
			emailIdView.setTextSize(getResources().getDimension(R.dimen.assignee_email_font_size));
			emailIdView.setPadding(10, 10, 10, 10);
			emailIdView.setText(objects.get(i).email);
			emailIdView.setTextColor(getResources().getColor(R.color.assignee_email_text));
			try {
				FlowLayout.LayoutParams flowParams = new FlowLayout.LayoutParams(BuiltUtil.convertToPixel(context, 4), BuiltUtil.convertToPixel(context, 4));
				emailIdView.setLayoutParams(flowParams);

			} catch (Exception e) {
				AppUtils.showLog(TAG, e.toString());
			}

			userContainerFlowLayout.addView(emailIdView);

			assigneesUid[i] =  objects.get(i).uid;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String filePath = null;
		String fileName = null;

		//Selected using built picker file manager option.
		if(requestCode == BuiltUIPickerController.PickerResultCode.SELECT_FROM_FILE_SYSTEM_REQUEST_CODE.getValue()){
			if(resultCode == RESULT_OK){

				filePath = (String) picker.getFileInfoForMediaFile(data, requestCode).get("filePath");
				fileName =  (String) picker.getFileInfoForMediaFile(data ,requestCode).get("fileName");

				setFileIcon(fileName,filePath);

			}else if(resultCode == RESULT_CANCELED){
				AppUtils.showLog(getString(R.string.nothing_selected), getString(R.string.select_file_for_attachment));
			}
		}
		//Selected using built picker gallery option.
		else if(requestCode == BuiltUIPickerController.PickerResultCode.SELECT_IMAGE_FROM_GALLERY_REQUEST_CODE.getValue()){
			if(resultCode == RESULT_OK){

				filePath = (String) picker.getFileInfoForMediaFile(data, requestCode).get("filePath");
				fileName =  (String) picker.getFileInfoForMediaFile(data ,requestCode).get("fileName");

				setFileIcon(fileName,filePath);


			}else if(resultCode == RESULT_CANCELED){
				AppUtils.showLog(getString(R.string.nothing_selected), getString(R.string.select_file_for_attachment));
			}
		}
		//Selected using built picker camera option.
		else if(requestCode == BuiltUIPickerController.PickerResultCode.CAPTURE_IMAGE_REQUEST_CODE.getValue()){
			if(resultCode == RESULT_OK){

				filePath = (String) picker.getFileInfoForMediaFile(data, requestCode).get("filePath");
				fileName =  (String) picker.getFileInfoForMediaFile(data ,requestCode).get("fileName");

				setFileIcon(fileName,filePath);

			}else if(resultCode == RESULT_CANCELED){
				AppUtils.showLog(getString(R.string.nothing_selected), getString(R.string.select_file_for_attachment));
			}
		}
		//Selected using built picker camera-video option.
		else if(requestCode == BuiltUIPickerController.PickerResultCode.CAPTURE_VIDEO_REQUEST_CODE.getValue()){
			if(resultCode == RESULT_OK){

				filePath = (String) picker.getFileInfoForMediaFile(data, requestCode).get("filePath");
				fileName =  (String) picker.getFileInfoForMediaFile(data ,requestCode).get("fileName");

				setFileIcon(fileName,filePath);

			}else if(resultCode == RESULT_CANCELED){
				AppUtils.showLog(getString(R.string.nothing_selected), getString(R.string.select_file_for_attachment));
			}
		}
	}
}

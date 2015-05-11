package com.raweng.projectsonthego;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONArray;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltImageDownloadCallback;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUpload;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.built.androidquery.AQuery;
import com.raweng.built.utilities.BuiltConstant;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.built.view.BuiltImageView;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.Utilities.FlowLayout;

/**
 * To display details of selected bug.
 * 
 * @author raw engineering, Inc
 *
 */
public class UIBugDetailScreen extends FragmentActivity implements IFetchUserList{

	private final String PROJECT_UID = "projectUID";
	private final String UID = "UID";
	private final String POSITION = "position";
	private final String TAG = "UIBugDetailsScreen";
	private final String BUG_CLASS_UID = "bugs";
	private final String COMMENT_CLASS_UID   = "comment";
	Context context;

	EditText bugTitleEditText;
	EditText bugDescribtionEditText;
	EditText bugCreatedByEditText;
	EditText bugCreatedTimeEditText;
	TextView commentCountTextView;

	EditText dialogDueDateEditText;
	Button   dialogUpdateButton;
	Button   dialogCancleButton;
	Spinner  dialogStatusSpinner;
	Spinner  dialogSeveritySpinner;
	Spinner  dialogReproducibleSpinner;

	Button bugAssignees;
	Dialog updateDialog;

	ProgressDialog progressDialog;
	FlowLayout userContainer;
	LinearLayout attachmentContainer;

	String bugUid     = null;
	String userType   = null;
	String due_date   = null;
	String projectUid = null;
	String dateString = null;

	public String[] assigneeUid ;
	public ArrayList<String> assigneeIdList;

	//AQuery aQuery;
	int position;
	MenuItem updateMenu;
    private BuiltApplication builtApplication;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bug_details);

		//aQuery = new AQuery(this);
		context = UIBugDetailScreen.this;

		if (getIntent().getExtras() != null) {
			//provides position of list view.
			position = getIntent().getExtras().getInt(POSITION);
			//provides selected bug uid.
			bugUid = getIntent().getExtras().getString(UID);
			//provides project uid inside the bug object present.
			projectUid = getIntent().getStringExtra(PROJECT_UID); 
		}

        try {
            builtApplication = Built.application(context, "blt3b011c0e38ed1d82");
        } catch (Exception e) {
            e.printStackTrace();
        }

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		bugTitleEditText       = (EditText) findViewById(R.id.bugTitleDetailView);
		bugCreatedByEditText   = (EditText) findViewById(R.id.bugCreatedBy); 
		bugCreatedTimeEditText = (EditText) findViewById(R.id.bugCreateTime);
		bugDescribtionEditText = (EditText) findViewById(R.id.bugDescriptionDetailView);
		Button commentLabel    = (Button)findViewById(R.id.commentLabel);
		attachmentContainer    = (LinearLayout) findViewById(R.id.bugAttachmentsProvideContainer);

		updateDialog = new Dialog(context);
		updateDialog.setContentView(R.layout.bug_detail_dialog);
		updateDialog.setTitle(getString(R.string.update_bug));


		dialogUpdateButton     		= (Button)  updateDialog.findViewById(R.id.save);
		dialogCancleButton    	    = (Button)  updateDialog.findViewById(R.id.cancle);
		bugAssignees    		    = (Button)  updateDialog.findViewById(R.id.addUserButton);
		dialogStatusSpinner 		= (Spinner) updateDialog.findViewById(R.id.spinnerStatus);
		dialogSeveritySpinner 		= (Spinner) updateDialog.findViewById(R.id.spinnerSeverity);
		dialogReproducibleSpinner 	= (Spinner) updateDialog.findViewById(R.id.spinnerReproducible);
		dialogDueDateEditText		= (EditText)updateDialog.findViewById(R.id.datePicker);
		userContainer       		= (FlowLayout)updateDialog.findViewById(R.id.bugDetailsUserContainer);
		commentCountTextView        = (TextView)findViewById(R.id.commentCountTextView);


		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		/*Get the user type of the logged in user.
		 *user types are admin, moderator, member*/
		userType = AppSettings.getUserType(context);

		ArrayList<String> assignees = new ArrayList<String>();
		assignees.add("assignees");

		progressDialog.show();

		//Create a query object to fetch details of BuiltObject inside bugs class.
		BuiltQuery builtquery = builtApplication.classWithUid(BUG_CLASS_UID).query();

		//Execute the query to fetch current bug object along with owner data and assignees data in the response.
		builtquery.includeOwner().where("uid", bugUid).includeReference(assignees).execInBackground(new QueryResultsCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {

                if (builtError == null) {
                    //QueryResult object return the array list of BuiltObject and its complete field information.
                    ArrayList<BuiltObject> listObject = (ArrayList<BuiltObject>) queryResult.getResultObjects();
                    int size = listObject.size();

                    for (int i = 0; i < size; i++) {
                        //Extracting information of built object.
                        extractData(listObject.get(i));
                    }
                    fetchCommentsCount();

                } else {
                    AppUtils.showLog(TAG, builtError.getErrorMessage());
                }
            }
        });

		//To open comments screen.
		commentLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent commentIntent = new Intent(context, UICommentScreen.class);
				commentIntent.putExtra("menuType", AppConstant.PROJECT_BUG);
				commentIntent.putExtra("menuUid", bugUid);
				commentIntent.putExtra("projectUid", projectUid);

				//For fetching comment count.
				startActivityForResult(commentIntent, AppConstant.COMMENT_COUNT_REQUEST_CODE);
				overridePendingTransition( R.anim.builtio_pulltorefresh_slide_in_from_bottom, R.anim.builtio_pulltorefresh_slide_out_to_top );
			}
		});
	}

	/**
	 * Extract response which comes from built.io server after executing network call.
	 * 
	 * @param builtObject
	 * 					builtObject.
	 */
	protected void extractData(BuiltObject builtObject) {

		ArrayAdapter<CharSequence> adapter;

		adapter = ArrayAdapter.createFromResource(context,R.array.status, android.R.layout.simple_spinner_item);
		dialogStatusSpinner.setAdapter(adapter);

		adapter = ArrayAdapter.createFromResource(context,R.array.severity, android.R.layout.simple_spinner_item);
		dialogSeveritySpinner.setAdapter(adapter);

		adapter = ArrayAdapter.createFromResource(context,R.array.reproducible, android.R.layout.simple_spinner_item);
		dialogReproducibleSpinner.setAdapter(adapter);

		try {
			//Check for created_at field present or not
			if (builtObject.has("created_at")) {
				Calendar createDateCalendar = null;
				try {

					// BuiltUtil.parseDate("dateString") provides the calendar object.
					// builtObject.get("created_at") returns the date string.

					createDateCalendar = BuiltUtil.parseDate(builtObject.getString("created_at"), TimeZone.getDefault());

				} catch (ParseException e) {
					AppUtils.showLog(TAG,e.toString());
				}
				bugCreatedTimeEditText.setText(createDateCalendar.get(Calendar.DATE) + "/"
						+ createDateCalendar.get(Calendar.MONTH) + "/"
						+ createDateCalendar.get(Calendar.YEAR));
			}

			//Check for name field present or not
			if(builtObject.has("name")){
				// builtObject.get("name") return bug name.
				bugTitleEditText.setText(builtObject.getString("name"));
			}

			//Check for description field present or not
			if(builtObject.has("description")){

				// builtObject.get("description") return bug description.

				bugDescribtionEditText.setText(builtObject.getString("description"));
			}

			//Check for attachments fields present or not
			if(builtObject.has("attachments")){

				///builtObject.getAllObjects(key, classUid) used for get array list of built object inside references field
				JSONArray array = builtObject.getJSONArray("attachments");
				
				for (int i = 0; i < array.length(); i++) {
					BuiltUpload builtFile = builtApplication.upload();
                    builtFile.configure(array.optJSONObject(i));
					setFileIcon(builtFile);
				}				
			}

			//Check for assignees fields present or not
			if(builtObject.has("assignees")){

				///builtObject.getAllObjects(key, classUid) used for get array list of built object inside references field

				ArrayList<BuiltObject> assignees = builtObject.getAllObjects("assignees", bugUid);
				assigneeUid = new String[assignees.size()];
				assigneeIdList = new ArrayList<String>();
				userContainer.removeAllViews();

				for(int i = 0; i < assignees.size(); i++){
					assigneeUid[i] =  assignees.get(i).getString("uid");

					assigneeIdList.add(assignees.get(i).getString("uid"));

					TextView emailIdView = new TextView(context);
					emailIdView.setBackgroundResource(R.drawable.bubble_background);
					emailIdView.setTextSize(getResources().getDimension(R.dimen.assignee_email_font_size));
					emailIdView.setPadding(10, 10, 10, 10);
					emailIdView.setText(assignees.get(i).getString("email").toString());
					emailIdView.setTextColor(getResources().getColor(R.color.assignee_email_text));
					FlowLayout.LayoutParams flowParams = new FlowLayout.LayoutParams(4, 4);
					emailIdView.setLayoutParams(flowParams);

					userContainer.addView(emailIdView);
				}
			}

			//Check for due_date fields present or not
			if(builtObject.has("due_date")){

				/// builtObject.get("due_date") returns due_date.

				due_date = builtObject.getString("due_date");
			}

			//Check for _owner fields present or not
			if (builtObject.has("_owner")) {
				String createrUid = null;
				String userName = null;
				try{
					///builtObject.getOwner() used for to get creator details who create a built object 
					if(builtObject.getOwner().containsKey("first_name") && builtObject.getOwner().containsKey("last_name")){
						userName = builtObject.getOwner().get("first_name")+" "+builtObject.getOwner().get("last_name");
						createrUid   =	builtObject.getOwner().get("uid").toString();
					}else{
						String email = builtObject.getOwner().get("email").toString();
						createrUid   =	builtObject.getOwner().get("uid").toString();
						int index = email.indexOf('@');		
						if (index!= -1){
							userName = email.substring(0,index);
						}
					}
					bugCreatedByEditText.setText(userName);
				}catch(Exception e){
					AppUtils.showLog(TAG,e.toString());
				}

				checkAccessForAssignees(createrUid);
			}


		} catch (NotFoundException e) {
			AppUtils.showLog(TAG,e.toString());
		} 
	}

	/**
	 * Set file icon for attachment file.
	 * 
	 * @param builtFileObject
	 */
	private void setFileIcon(/*String file, String fileUrl*/final BuiltUpload builtFileObject){

		RelativeLayout view = (RelativeLayout)getLayoutInflater().inflate( R.layout.aquerry_image_layout, null,false);
		LinearLayout.LayoutParams viewParams;
		try {
			viewParams = new LinearLayout.LayoutParams(BuiltUtil.convertToPixel(context, 200),BuiltUtil.convertToPixel(context, 200));
			viewParams.setMargins(10, 10, 10, 10);
			view.setLayoutParams(viewParams);
		} catch (Exception e) {
			AppUtils.showLog(TAG, e.toString());
		}


		BuiltImageView imageView  = (BuiltImageView)view.findViewById(R.id.image);
		TextView fileNameTextView = (TextView) view.findViewById(R.id.fileName);
		ProgressBar progressbar   = (ProgressBar) view.findViewById(R.id.progress);

		String imageName = null;
		if(builtFileObject != null){
			imageName = builtFileObject.getFileName();
		}
		String extension = null;
		imageView.showProgressOnLoading(progressbar);

		if(imageName != null){
			fileNameTextView.setText(imageName);
			extension = imageName.substring(imageName.lastIndexOf(".") + 1, imageName.length());
		}

		if(extension != null){

			if(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")){
				imageView.setBuiltUpload(UIBugDetailScreen.this, builtFileObject, new BuiltImageDownloadCallback() {

                    @Override
                    public void onCompletion(BuiltConstant.ResponseType responseType, Bitmap bitmap, BuiltError builtError) {
                        if (builtError == null){
                            AppUtils.showLog(TAG, "Download Success");
                        }else {
                            AppUtils.showLog(TAG, builtError.getErrorMessage());
                        }
                    }
                });

				imageView.setScaleType(ScaleType.CENTER_CROP);

			}else if(extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")){
				imageView.setBackgroundResource(R.drawable.docxformat);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);

			}else if(extension.equalsIgnoreCase("pdf")){
				imageView.setBackgroundResource(R.drawable.pdfformat);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);

			}else if((extension.equalsIgnoreCase("png")) || (extension.equalsIgnoreCase("gif"))){
				imageView.setBuiltUpload(UIBugDetailScreen.this, builtFileObject, new BuiltImageDownloadCallback() {

                    @Override
                    public void onCompletion(BuiltConstant.ResponseType responseType, Bitmap bitmap, BuiltError builtError) {

                        if (builtError == null){
                            AppUtils.showLog(TAG, "Download Success");
                        }else {
                            AppUtils.showLog(TAG, builtError.getErrorMessage());
                        }
                    }
                });

				imageView.setScaleType(ScaleType.CENTER_INSIDE);

			}else if(extension.equalsIgnoreCase("mp3")){
				imageView.setBackgroundResource(R.drawable.mp3format);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);

			}else{
				imageView.setBackgroundResource(R.drawable.abstractfile);
				imageView.setScaleType(ScaleType.CENTER);
			}
		}

		attachmentContainer.addView(view);

		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				WebView webview = new WebView(context);
				webview.loadUrl(builtFileObject.getUploadUrl());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action, menu);
		MenuItem commentBug= menu.findItem(R.id.comment);
		commentBug.setVisible(false);
		MenuItem deleteBug= menu.findItem(R.id.delete);
		updateMenu = menu.findItem(R.id.bugaction);

		//Verify user type for admin and moderator to allow permission for delete milestone.
		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString()) || userType.equalsIgnoreCase(AppConstant.userRole.moderator.toString())){
			deleteBug.setVisible(true);
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			finish();
			return true;
		case R.id.bugaction:
			showUpdateBugDialog();
			return true;

		case R.id.delete:

			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
			dlgAlert.setMessage(R.string.really_want_to_delete_bug);
			dlgAlert.setTitle(R.string.delete_bug);
			dlgAlert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					progressDialog.setMessage(getString(R.string.deleting_bug_));
					progressDialog.show();

					//Delete a bug object.
					final BuiltObject built = builtApplication.classWithUid(BUG_CLASS_UID).object(bugUid);

					//Delete bug object using destroy call. 
					built.destroyInBackground(new BuiltResultCallBack() {

                        @Override
                        public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError error) {
                            if (error == null){
                                Toast.makeText(context, getString(R.string.bug_deleted_successfully), Toast.LENGTH_LONG).show();

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(POSITION, position - 1);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();

                            }else {

                                AppUtils.showLog(TAG, error.getErrorMessage());
                                Toast.makeText(context, error.getErrorCode() + " : " + error.getErrorMessage(), Toast.LENGTH_LONG).show();

                            }

                            progressDialog.dismiss();

                        }
                    });
				}
			});
			dlgAlert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void fetchUserList(ArrayList<UserModel> objects,boolean isModeratorList) {

		int count       = objects.size();
		String[] result = null;
		assigneeUid     = new String[objects.size()];
		userContainer.removeAllViews();

		for(int i = 0; i < count; i++){
			assigneeUid[i] =  objects.get(i).uid;

			Set<String> temp = new LinkedHashSet<String>(Arrays.asList(assigneeUid));
			result = temp.toArray( new String[temp.size()] );
		}

		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				TextView emailIdView = new TextView(context);
				emailIdView.setBackgroundResource(R.drawable.bubble_background);
				emailIdView.setTextSize(getResources().getDimension(R.dimen.assignee_email_font_size));
				emailIdView.setPadding(10, 10, 10, 10);
				emailIdView.setText(objects.get(i).email);
				emailIdView.setTextColor(getResources().getColor(R.color.assignee_email_text));
				FlowLayout.LayoutParams flowParams = new FlowLayout.LayoutParams(4, 4);
				emailIdView.setLayoutParams(flowParams);

				userContainer.addView(emailIdView);
			}
		}
	}

	/**
	 * This method is used to call the {@link UserListDialogFragment}. 
	 */
	private void showEditDialog() {
		FragmentManager fragmentManager = getFragmentManager();
		UserListDialogFragment userListDialog = new UserListDialogFragment();
		userListDialog.iFetchUserList = UIBugDetailScreen.this;
		if (assigneeIdList != null) {
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("AssigneeUid",assigneeIdList);
			userListDialog.setArguments(bundle);
		}
		userListDialog.show(fragmentManager, getString(R.string.fragment_edit_name));


	}

	/**
	 * To show update updateDialog if user is admin, moderator or present in assignees.
	 */
	private void showUpdateBugDialog(){
		if(due_date != null ){
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
			try {
				Date date = formatDate.parse(due_date);
				dateString = formatDate.format(date);
				SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				dialogDueDateEditText.setText(isoDate.format(date).toString());
			}catch (Exception e) {
				AppUtils.showLog(TAG,e.toString());
			}
		}

		bugAssignees.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Shows updateDialog to add a user.
				showEditDialog();
			}
		});

		dialogDueDateEditText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {   

				Dialog dialog = new Dialog(context);
				dialog.setTitle(getString(R.string.set_due_date));
				final DatePicker datePicker = new DatePicker(context);
				datePicker.setCalendarViewShown(false);
				dialog.setContentView(datePicker);
				dialog.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface arg0) {
						int month = datePicker.getMonth()+1;
						int day = datePicker.getDayOfMonth();

						SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
						String currentTime = timeFormat.format(new Date());

						if(month < 10 && day > 9){
							dateString = datePicker.getYear()+"-0"+month+"-"+day;
						}else if(month > 9 && day < 10){
							dateString = datePicker.getYear()+"-"+month+"-0"+day;
						}else if(month < 10 && day < 10){
							dateString = datePicker.getYear()+"-0"+month+"-0"+day;
						}else{
							dateString = datePicker.getYear()+"-"+month+"-"+day;
						}
						dialogDueDateEditText.setText(dateString+" "+currentTime);
						dateString = dateString+"T"+currentTime;
					}
				});
				dialog.show();
			}
		});

		dialogUpdateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//Update a bug object and Set a unique uid of bug object which comes in the response of execute call of the Built Query..
				BuiltObject object = builtApplication.classWithUid(BUG_CLASS_UID).object(bugUid);

				//Set a value for name field.
				object.set("name", bugTitleEditText.getText().toString());

				if(bugDescribtionEditText.length() > 0){
					//Set a value for description field.
					object.set("description", bugDescribtionEditText.getText().toString());
				}

				//Set a value for reproducible field. 
				object.set("reproducible", dialogReproducibleSpinner.getSelectedItem().toString());
				//Set a value for due_date field. 
				object.set("due_date",dateString);
				//Set a value for severity field. 
				object.set("severity", dialogSeveritySpinner.getSelectedItem().toString());
				//Set a value for status field. 
				object.set("status",  dialogStatusSpinner.getSelectedItem().toString());

				//Set assignees uid's to assignees field.
				object.setReference("assignees", assigneeUid);
				//Set permission newly added assignees.
				BuiltACL builtacl = new BuiltACL();
				if(assigneeUid != null){
					for(int i = 0; i < assigneeUid.length; i++){
						builtacl.setUserReadAccess(assigneeUid[i], true);
						builtacl.setUserWriteAccess(assigneeUid[i], true);
					}
				}
				object.setACL(builtacl);

				//Set reference uid of project inside which this milestone object present.
				object.setReference("project",projectUid);

				progressDialog.setMessage(getString(R.string.updating_bug));
				progressDialog.show();

				//Update call for bug object.
				object.saveInBackground(new BuiltResultCallBack() {

                    @Override
                    public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError error) {

                        if (error == null){
                            Toast.makeText(context, getString(R.string.bug_updated_successfully), Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            AppUtils.showLog(TAG, error.getErrorMessage());
                            Toast.makeText(context, error.getErrorCode() + "" + error.getErrorMessage(), Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();

                    }
                });
				updateDialog.dismiss();
			}
		});

		dialogCancleButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updateDialog.dismiss();
			}
		});
		updateDialog.show();

	}

	/**
	 * This method is use to check assignees access for update.
	 * 
	 * @param createrUid 
	 * 					createrUid of bug.
	 */
	private void checkAccessForAssignees(String createrUid){

		String loginUserUid = AppSettings.getUserUid(context);
		boolean isUserFromAssignees = false;

		//Verify user type for admin, moderator or user present in assignees to allow permission for update bug.
		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString()) || userType.equalsIgnoreCase(AppConstant.userRole.moderator.toString())){
			isUserFromAssignees = true;
		}

		if(assigneeUid != null){
			for(int i=0; i<assigneeUid.length; i++){
				if(assigneeUid[i].equalsIgnoreCase(loginUserUid)){
					isUserFromAssignees = true;
				}
			}
		}

		if(createrUid != null){
			if(createrUid.equalsIgnoreCase(loginUserUid)){
				isUserFromAssignees = true;
			}
		}

		if(!isUserFromAssignees){
			updateMenu.setVisible(false);
		}
	}
	/**
	 * Fetch comment count.
	 */
	private void fetchCommentsCount(){

		progressDialog.setMessage(getString(R.string.loading_comments_count));

		//Create built query object of comment class.
		BuiltQuery query = builtApplication.classWithUid(COMMENT_CLASS_UID).query();

		//Create built query object of bug class.
		BuiltQuery builtQueryObject = builtApplication.classWithUid(BUG_CLASS_UID).query();

		//Fetch a particular built object using uid.
		builtQueryObject.where("uid", bugUid);

		//Fetch reference objects of type bug
		query.inQuery("for_bug", builtQueryObject);

		//Execute a query.
		query.execInBackground(new QueryResultsCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {
                if (builtError == null){
                    commentCountTextView.setText("" + queryResult.getResultObjects().size());

                }else {
                    AppUtils.showLog(TAG, builtError.getErrorMessage());

                }

                progressDialog.dismiss();

            }
        });


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		//To fetch the updated comments count.
		case AppConstant.COMMENT_COUNT_REQUEST_CODE:{
			if (resultCode == RESULT_OK && data != null) {
				int commentCount = data.getExtras().getInt("commentCount");
				commentCountTextView.setText(""+commentCount);
			}
		}
		break;

		default:
			break;
		}
	}

}

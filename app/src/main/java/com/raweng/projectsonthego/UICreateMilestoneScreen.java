package com.raweng.projectsonthego;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.utilities.BuiltConstant;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.Utilities.FlowLayout;

/**
 * To create new milestone of a selected project,
 * Note : admin or project moderator can create or update task.
 * 
 * @author raw engineering, Inc
 *
 */
public class UICreateMilestoneScreen extends Activity implements IFetchUserList{

	private static final String MILESTONE_CLASS_UID = "milestone";
	private final String TAG = "UICreateMilestoneScreen";
	Context context; 
	
	EditText milstoneName;
	EditText milstoneDescribtion;
	Button	 milstoneAddUser;
	TextView milstoneStartDate;
	TextView milstoneStartMonthYear;
	TextView milstoneEndDate;
	TextView milstoneEndMonthYear;
	DatePicker datepicker;

	RelativeLayout milestoneEndDateContainer;
	RelativeLayout milestoneStartDateContainer;
	FlowLayout userContainer;

	ProgressDialog progressDialog;

	String startDateString;
	String endDateString;
	String monthName;
	String userType = null;
	String projectUid;
	String[] assigneesUid = null ;
	private String moderatorRoleUid;
	private String memberRoleUid;
    private BuiltApplication builtApplication;

	/**
	 * Temporary hashmap to store successfully created milestones.
	 */
	public static HashMap<Object, Object> createMilestoneHashMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_milestone);

		createMilestoneHashMap = new HashMap<Object, Object>();
		context = UICreateMilestoneScreen.this;

        /*
         * Initialised builtApplication here.
         */
        try {
            builtApplication = Built.application(context ,  "blt3b011c0e38ed1d82");
        } catch (Exception e) {
            e.printStackTrace();
        }

        milstoneAddUser			= (Button)   findViewById(R.id.addUserButton);
		milstoneName       		= (EditText) findViewById(R.id.milestoneNameFeild);
		milstoneDescribtion 	= (EditText) findViewById(R.id.milestoneDescriptionFeild);
		milstoneStartDate		= (TextView) findViewById(R.id.mileStoneStartDate);
		milstoneStartMonthYear	= (TextView) findViewById(R.id.mileStoneStartDateMonthYear);
		milstoneEndDate			= (TextView) findViewById(R.id.mileStoneEndDate);
		milstoneEndMonthYear 	= (TextView) findViewById(R.id.mileStoneEndDateMonthYear);
		userContainer           = (FlowLayout)findViewById(R.id.userContainer);

		milestoneStartDateContainer = (RelativeLayout) findViewById(R.id.milestoneStartDateContainer);
		milestoneEndDateContainer   = (RelativeLayout) findViewById(R.id.milestoneEndDateContainer);

		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		milstoneName.setFocusable(true);
		milstoneName.setClickable(true);
		milstoneName.setCursorVisible(true);
		milstoneName.setFocusableInTouchMode(true);

		milstoneDescribtion.setFocusable(true);
		milstoneDescribtion.setClickable(true);
		milstoneDescribtion.setCursorVisible(true);
		milstoneDescribtion.setFocusableInTouchMode(true);

		//Get the user type of the logged in user.
		userType   = AppSettings.getUserType(context);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setCurrentDate();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			//Get the projectUid from intent.
			projectUid = args.getString("projectUID"); 
			moderatorRoleUid  = args.getString("moderatorsRoleUid"); 
			memberRoleUid	  = args.getString("membersRoleUid"); 
		}

		milstoneAddUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showEditDialog();
			}
		});

		//To select a start date.
		milestoneStartDateContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final Dialog datedialog = new Dialog(context);
				datedialog.setTitle(getString(R.string.set_date));
				datepicker = new DatePicker(context);
				datepicker.setCalendarViewShown(false);
				datepicker.setBackgroundColor(Color.GRAY);
				datedialog.addContentView(datepicker, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				datedialog.show();

				datedialog.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface arg0) {

						int countStartDate  = datepicker.getDayOfMonth();
						int countStartMonth = datepicker.getMonth()+1;
						monthName  = AppConstant.extractMonthName(countStartMonth);
						milstoneStartMonthYear.setText(monthName+"  "+datepicker.getYear());
						milstoneStartDate.setText(""+countStartDate+"");

						if(countStartMonth < 10 && countStartDate > 9){
							startDateString = datepicker.getYear()+"-0"+countStartMonth+"-"+countStartDate;
						}else if(countStartMonth > 9 && countStartDate < 10){
							startDateString = datepicker.getYear()+"-"+countStartMonth+"-0"+countStartDate;
						}else if(countStartMonth < 10 && countStartDate < 10){
							startDateString = datepicker.getYear()+"-0"+countStartMonth+"-0"+countStartDate;
						}else{
							startDateString = datepicker.getYear()+"-"+countStartMonth+"-"+countStartDate;
						}
					}
				});
			}
		});

		//To select a end date.
		milestoneEndDateContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final Dialog datedialog = new Dialog(context);
				datedialog.setTitle(getString(R.string.set_date));
				datepicker = new DatePicker(context);
				datepicker.setCalendarViewShown(false);
				datepicker.setBackgroundColor(Color.GRAY);
				datedialog.addContentView(datepicker, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				datedialog.show();

				datedialog.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface arg0) {

						int countEndDate  = datepicker.getDayOfMonth();
						int countEndMonth = datepicker.getMonth()+1;
						monthName  = AppConstant.extractMonthName(countEndMonth);
						milstoneEndMonthYear.setText(monthName+"  "+datepicker.getYear());
						milstoneEndDate.setText(""+countEndDate+"");

						if(countEndMonth < 10 && countEndDate > 9){
							endDateString = datepicker.getYear()+"-0"+countEndMonth+"-"+countEndDate;
						}else if(countEndMonth > 9 && countEndDate < 10){
							endDateString = datepicker.getYear()+"-"+countEndMonth+"-0"+countEndDate;
						}else if(countEndMonth < 10 && countEndDate < 10){
							endDateString = datepicker.getYear()+"-0"+countEndMonth+"-0"+countEndDate;
						}else{
							endDateString = datepicker.getYear()+"-"+countEndMonth+"-"+countEndDate;
						}
					}
				});
			}
		});
	}

	/**
	 * This method is used to create a milestone.
	 *
	 */
	protected void createMilestone() {
		progressDialog.show();

		//Create a milestone object.
		final BuiltObject object = builtApplication.classWithUid(MILESTONE_CLASS_UID).object();
		//Set values for fields
		object.set("name", milstoneName.getText().toString().trim());
		if(milstoneDescribtion.length() > 0){
			object.set("description", milstoneDescribtion.getText().toString().trim());
		}

		//Set the user id to assignees field.
		object.set("assignees", assigneesUid);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String time = timeFormat.format(new Date());
		object.set("start_date",startDateString+"T"+time);
		object.set("end_date", endDateString+"T"+time);

		//To set project reference to this milestone.
		object.setReference("project",projectUid);

		try {
			BuiltACL milestoneACL = new BuiltACL();

			//Members have read access for a milestone. Moderators have read, update, delete access for a milestone.
			if(memberRoleUid != null){
				milestoneACL.setRoleReadAccess(memberRoleUid, true);
			}
			
			if(moderatorRoleUid != null){
				milestoneACL.setRoleReadAccess(moderatorRoleUid, true);
				milestoneACL.setRoleWriteAccess(moderatorRoleUid, true);
				milestoneACL.setRoleDeleteAccess(moderatorRoleUid, true);
			}

			//Guest users only read access.
			milestoneACL.setPublicReadAccess(true);
			milestoneACL.setPublicWriteAccess(false);

			//Set ACL to this milestone.
			object.setACL(milestoneACL);
		} catch(Exception e) {
			AppUtils.showLog(TAG,e.toString());
		}

		//Save the milestone object.
		object.saveInBackground(new BuiltResultCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError error) {

                if(error == null){
                    Toast.makeText(context, getString(R.string.milestone_created_successfully), Toast.LENGTH_LONG).show();

                    //Once successfully created get the json response to get the uid of the milestone created.
                    if (object.has("uid")) {
                        try {
                            builtApplication.classWithUid(MILESTONE_CLASS_UID).object(object.getString("uid").toString());
//                            object.setUid(object.getString("uid").toString());
                            createMilestoneHashMap.put("builtObject", object);
                        } catch (Exception e) {
                            AppUtils.showLog(TAG, e.toString());
                        }
                    }

                    //To notify that the milestone has been created successfully
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    hideKeyboard();
                    finish();

                }else {
                    AppUtils.showLog(TAG, error.getErrorMessage() + error.toString());
                    Toast.makeText(context, error.getErrorCode() + " : " + error.getErrorMessage(), Toast.LENGTH_LONG).show();

                }

            }
        });

	}

	/**
	 * This method is used to call the {@link UserListDialogFragment}. 
	 *
	 */
	private void showEditDialog() {
		FragmentManager fragmentManager = getFragmentManager();
		UserListDialogFragment userListDialog = new UserListDialogFragment();
		userListDialog.iFetchUserList = UICreateMilestoneScreen.this;
		userListDialog.show(fragmentManager, getString(R.string.fragment_edit_name));
	}

	/**
	 * Checks for empty field
	 */
	protected boolean performValidation() {
		milstoneName.setError(null);
		boolean cancel = false;

		if (TextUtils.isEmpty(milstoneName.getText().toString())) {
			milstoneName.setError(getString(R.string.builtio_error_field_required));
			milstoneName.requestFocus();
			cancel = true;
		} 
		return cancel;

	}

	/**
	 * To set current date
	 */
	private void setCurrentDate(){
		Calendar calendar = Calendar.getInstance();

		int date  = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH)+1;
		monthName  = AppConstant.extractMonthName(month);

		if(month < 10 && date > 9){
			startDateString = calendar.get(Calendar.YEAR)+"-0"+month+"-"+date;
		}else if(month > 9 && date < 10){
			startDateString = calendar.get(Calendar.YEAR)+"-"+month+"-0"+date;
		}else if(month < 10 && date < 10){
			startDateString = calendar.get(Calendar.YEAR)+"-0"+month+"-0"+date;
		}else{
			startDateString = calendar.get(Calendar.YEAR)+"-"+month+"-"+date;
		}
		endDateString = startDateString;
		
		milstoneStartMonthYear.setText(monthName+"  "+calendar.get(Calendar.YEAR));
		milstoneStartDate.setText(""+date+"");

		milstoneEndMonthYear.setText(monthName+"  "+calendar.get(Calendar.YEAR));
		milstoneEndDate.setText(""+date+"");
	}

	@Override
	public void fetchUserList(ArrayList<UserModel> objects,boolean isModeratorList) {

		assigneesUid = new String[objects.size()];
		int count = objects.size();
		String[] result = null;
		userContainer.removeAllViews();
		for(int i = 0; i < count; i++){
			
			//Hack to remove the duplicate values.
			Set<String> temp = new LinkedHashSet<String>(Arrays.asList(assigneesUid));
			result = temp.toArray( new String[temp.size()] );

			assigneesUid[i] =  objects.get(i).uid;
		}

		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				TextView emailIdView = new TextView(this);
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

				userContainer.addView(emailIdView);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action, menu);

		MenuItem deleteBug= menu.findItem(R.id.delete);
		deleteBug.setVisible(false);

		MenuItem commentBug= menu.findItem(R.id.comment);
		commentBug.setVisible(false);

		MenuItem updateBug= menu.findItem(R.id.bugaction);
		updateBug.setTitle(R.string.create);

		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			hideKeyboard();
			finish();
			return true;

		case R.id.bugaction:
			if(!performValidation()){
				createMilestone();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * hides the  keyboard for all the edit texts in this class
	 */
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(milstoneName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(milstoneDescribtion.getWindowToken(), 0);
	}
}

package com.raweng.projectsonthego;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.Utilities.FlowLayout;

/**
 * To create new task of a selected project,
 * Note : admin or project moderator can create or update task.
 * @author raw engineering, Inc
 *
 */
public class UICreateTaskScreen extends Activity implements IFetchUserList{

	private static final String TASK_CLASS_UID = "task";
	private final String TAG = "UICreateTaskScreen";
	private Context context;

	EditText taskName;
	EditText taskDescribtion;
	Button addAssignee;
	Button addSteps;
	LinearLayout stepsContainer;
	ProgressDialog progressDialog;
	FlowLayout userContainerFlowLayout;

	List<HashMap<String, Object>> stepsList =  new ArrayList<HashMap<String,Object>>(); 

	String userType   = null;
	String projectUID = null;
	String[] assigneesUid;
	String moderatorRoleUid;
	String memberRoleUid;

	public static HashMap<Object, Object> createTaskHashMap = new HashMap<Object, Object>();
    private BuiltApplication builtApplication;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_task_details);


		context = UICreateTaskScreen.this;

        /*
         * Initialised builtApplication here.
         */
        try {
            builtApplication = Built.application(context, "API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }

		userContainerFlowLayout = (FlowLayout)findViewById(R.id.taskAssigneeContainer);
		taskName 			= (EditText) findViewById(R.id.taskNameField);
		taskDescribtion 	= (EditText) findViewById(R.id.taskDescriptionField);
		stepsContainer      = (LinearLayout) findViewById(R.id.stepsContainer);
		addAssignee 		= (Button)findViewById(R.id.addAssignee);
		addSteps 			= (Button)findViewById(R.id.addSteps);

		addAssignee.setVisibility(View.VISIBLE);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(getResources().getString(R.string.please_wait));
		progressDialog.setMessage(getResources().getString(R.string.creating_task));

		userType   = AppSettings.getUserType(context);

		Bundle args = getIntent().getExtras();
		if (args != null) {
			//Get the projectUid from intent.
			projectUID = args.getString("projectUID"); 
			moderatorRoleUid  = args.getString("moderatorsRoleUid"); 
			memberRoleUid	  = args.getString("membersRoleUid"); 
		}

		addAssignee.setOnClickListener(clickListener);
		addSteps.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			if(view == addAssignee){

				// to add assignees to this task this will call UserListDialogFragment class.
				FragmentManager fragmentManager = getFragmentManager();
				UserListDialogFragment userListDialog = new UserListDialogFragment();
				userListDialog.iFetchUserList = UICreateTaskScreen.this;
				userListDialog.show(fragmentManager, getString(R.string.fragment_edit_name));

			}else if(view == addSteps){

				LayoutInflater inflater = LayoutInflater.from(context);
				View childView = inflater.inflate(R.layout.view_steps_task, null);
				ImageView removeStepButton = (ImageView) childView.findViewById(R.id.closeStepsTask);
				removeStepButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						stepsContainer.removeView((View) view.getParent());

					}
				});
				stepsContainer.addView(childView);
			}

		}
	};

	public void createTask() {
		progressDialog.show();
		
		//create a Task  object
		final BuiltObject object = builtApplication.classWithUid(TASK_CLASS_UID).object();
		
		//set values to the field
		object.set("name", taskName.getText().toString().trim());
		if(taskDescribtion.length() > 0){
			object.set("description", taskDescribtion.getText().toString().trim());
		}
		object.setReference("project", projectUID);
		addStepsList();
		object.set("steps", stepsList);
		object.set("assignees", assigneesUid);
		try {
			BuiltACL taskACL = builtApplication.acl();
			//Members have read access for a milestone. Moderators have read, update, delete access for a milestone.
			//Set permission for team_memberRoleUid && moderatorRoleUid 
			if(memberRoleUid != null){
				taskACL.setRoleReadAccess(memberRoleUid, true);
			}
			
			if(moderatorRoleUid != null){
				taskACL.setRoleReadAccess(moderatorRoleUid, true);
				taskACL.setRoleWriteAccess(moderatorRoleUid, true);
				taskACL.setRoleDeleteAccess(moderatorRoleUid, true);
			}
			
			//Guest users can only have read access.
			taskACL.setPublicReadAccess(true);
			taskACL.setPublicWriteAccess(false);

			//Set ACL to this bug.
			object.setACL(taskACL);
		} catch(Exception e) {
			AppUtils.showLog(TAG,e.toString());
		}

		object.saveInBackground(new BuiltResultCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {
                if (builtError == null){
                    //Once successfully created get the json response to get the uid of the task created.
                    if (object.has("uid")) {
                        try {
                            //object.setUid(object.getString("uid"));
                            createTaskHashMap.put("builtObject", object);
                        } catch (Exception e) {
                            AppUtils.showLog(TAG, e.toString());
                        }
                    }

                    Toast.makeText(UICreateTaskScreen.this, getString(R.string.task_created_successfully), Toast.LENGTH_LONG).show();
                    //To notify that the milestone has been created successfully
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    hideKeyboard();
                    finish();

                }else {
                    AppUtils.showLog(TAG, builtError.getErrorMessage());
                }

                progressDialog.dismiss();
            }
        });

	}

	//

	/**
	 * Add steps to list 
	 */
	public void addStepsList() {

		if(stepsContainer.getChildCount() > 0){
			int viewCounter = stepsContainer.getChildCount();

			for(int i = 0; i < viewCounter; i++){
				View childView = stepsContainer.getChildAt(i);
				EditText stepTaskName = (EditText) childView.findViewById(R.id.stepTaskName);
				EditText stepTaskDescription = (EditText) childView.findViewById(R.id.stepTaskDescription);
				CheckBox isCompleteCheck = (CheckBox) childView.findViewById(R.id.isCompleteCheck);

				HashMap<String, Object> hashmapObject = new HashMap<String, Object>();

				hashmapObject.put("name", stepTaskName.getText().toString().trim());

				if(stepTaskDescription.getText().length() > 0){
					hashmapObject.put("description", stepTaskDescription.getText().toString().trim());
				}
				if(isCompleteCheck.isChecked()){
					hashmapObject.put("complete", true);
				}else{
					hashmapObject.put("complete", false);
				}

				stepsList.add(hashmapObject);
			}
		}
	}

	@Override
	public void fetchUserList(ArrayList<UserModel> objects,
			boolean isModeratorList) {
		assigneesUid = new String[objects.size()];
		int count = objects.size();
		userContainerFlowLayout.removeAllViews();
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
			return true;

		case R.id.bugaction:
			// To create new task.
			if(!performValidation()){
				createTask();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * Checks for empty field
	 */
	protected boolean performValidation() {
		taskName.setError(null);
		boolean cancel = false;

		if (TextUtils.isEmpty(taskName.getText().toString())) {
			taskName.setError(getString(R.string.builtio_error_field_required));
			taskName.requestFocus();
			cancel = true;
		} 
		return cancel;

	}
	
	/**
	 * hides the  keyboard for all the edit texts in this class
	 */
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(taskName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(taskDescribtion.getWindowToken(), 0);
	}
}

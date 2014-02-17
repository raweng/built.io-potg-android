package com.raweng.projectsonthego;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.Utilities.FlowLayout;

/**
 * To create new project.
 * Note : Only "admin" user can create new project. 
 * @author raw engineering, Inc
 *
 */
public class UICreateProjectScreen extends Activity implements IFetchUserList{

	private static final String PROJECT_CLASS_UID = "project";
	private final String TAG = "UICreateProjectScreen";
	private Context context;

	private FlowLayout projectModeratorsContainer;
	private FlowLayout projectMemberContainer;

	private EditText projectName;
	private EditText projectDescription;

	private String[] moderatorsUid;
	private String[] membersUid;

	private ProgressDialog progressDialog;
	private Button addProjectModeratorsButton;
	private Button addProjectMembersButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_project);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = UICreateProjectScreen.this;

		projectName 				= (EditText)findViewById(R.id.projectNameField);
		projectDescription 			= (EditText)findViewById(R.id.projectDescriptionField);
		projectModeratorsContainer 	= (FlowLayout)findViewById(R.id.projectModeratorsContainer);
		projectMemberContainer  	= (FlowLayout)findViewById(R.id.projectMembersContainer);
		addProjectModeratorsButton 	= (Button)findViewById(R.id.addProjectModeratorsButton);
		addProjectMembersButton 	= (Button)findViewById(R.id.addProjectMembersButton);

		addProjectModeratorsButton.setOnClickListener(onClickListner);
		addProjectMembersButton.setOnClickListener(onClickListner);

		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setMessage(getString(R.string.loading));

	}

	View.OnClickListener onClickListner = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(view == addProjectModeratorsButton){
				callUserDialogFragment(true);

			}else if(view == addProjectMembersButton){
				callUserDialogFragment(false);
			}
		}
	};


	@Override
	public void fetchUserList(ArrayList<UserModel> objects,boolean isModeratorList) {

		int count = objects.size();
		if(isModeratorList){
			moderatorsUid = new String[count];
			for(int i = 0; i < count; i++){
				moderatorsUid[i] = objects.get(i).uid;
			}
		}else{
			membersUid = new String[count];
			for(int i = 0; i < count; i++){
				membersUid[i] = objects.get(i).uid;
			}
		}


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

			if(isModeratorList){
				projectModeratorsContainer.addView(emailIdView);
			}else{
				projectMemberContainer.addView(emailIdView);
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

				progressDialog.show();

				//Create a object of BuiltObject.
				final BuiltObject builtObject = new BuiltObject(PROJECT_CLASS_UID);
				//Set values of object.
				builtObject.set("name", projectName.getText().toString().trim());

				if(projectDescription.length() > 0){
					builtObject.set("description", projectDescription.getText().toString().trim());
				}

				//Set a members role.
				BuiltObject roleObjectMember = new BuiltObject("built_io_application_user_role");
				roleObjectMember.set("name", projectName.getText().toString().trim() + "_members");

				roleObjectMember.set("users", membersUid);

				//Set a moderators role.
				BuiltObject roleObjectModerators = new BuiltObject("built_io_application_user_role");
				roleObjectModerators.set("name", projectName.getText().toString().trim() + "_moderators");
				
				roleObjectModerators.set("users", moderatorsUid);
				
				builtObject.setReference("members", roleObjectMember);
				builtObject.setReference("moderators", roleObjectModerators);
				

				//Call for create Role.
				builtObject.save(new BuiltResultCallBack() {

					@Override
					public void onSuccess() {

						JSONArray moderatorsArray = new JSONArray();
						JSONArray membersArray = new JSONArray();
						moderatorsArray = builtObject.getJSONArray("moderators");
						membersArray = builtObject.getJSONArray("members");

						//Set a ACL for object.
						BuiltACL acl = new BuiltACL();
						acl.setRoleReadAccess((String) membersArray.opt(0), true);
						acl.setRoleReadAccess((String) moderatorsArray.opt(0), true);
						acl.setRoleWriteAccess((String) moderatorsArray.opt(0), true);
						acl.setRoleDeleteAccess((String) moderatorsArray.opt(0), true);
						builtObject.setUid((String) builtObject.getString("uid"));
						builtObject.setACL(acl);

						//Call for create Project.
						builtObject.save(new BuiltResultCallBack() {


							@Override
							public void onSuccess() {
								Toast.makeText(context, getString(R.string.project_created_successfully), Toast.LENGTH_SHORT).show();
								finish();
								
								hideKeyboard();
							}

							@Override
							public void onError(BuiltError error) {
								AppUtils.showLog(TAG,error.getErrorMessage());
								AppUtils.showLog("TAG----------",error.getErrors().toString());

							}

							@Override
							public void onAlways() {
								progressDialog.dismiss();
							}
						});

					}

					@Override
					public void onError(BuiltError error) {
						AppUtils.showLog(TAG,error.getErrorMessage());		
						AppUtils.showLog(TAG,error.getErrors().toString());
					}

					@Override
					public void onAlways() {

					}
				});
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
		imm.hideSoftInputFromWindow(projectName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(projectDescription.getWindowToken(), 0);
	}

	/**
	 * Open User Dialog Fragment.
	 * @param isModeratorUserList
	 * 							true/false
	 */
	private void callUserDialogFragment(boolean isModeratorUserList){
		FragmentManager fragmentManager = getFragmentManager();
		UserListDialogFragment userListDialog = new UserListDialogFragment();
		Bundle args = new Bundle();
		userListDialog.isModeratorUserList = isModeratorUserList;
		userListDialog.iFetchUserList = UICreateProjectScreen.this;
		userListDialog.setArguments(args);
		userListDialog.show(fragmentManager, getString(R.string.fragment_edit_name));
	}

	/**
	 * Checks for empty field
	 */
	protected boolean performValidation() {
		projectName.setError(null);
		boolean cancel = false;

		if (TextUtils.isEmpty(projectName.getText().toString())) {
			projectName.setError(getString(R.string.builtio_error_field_required));
			projectName.requestFocus();
			cancel = true;
		} 
		return cancel;

	}
}

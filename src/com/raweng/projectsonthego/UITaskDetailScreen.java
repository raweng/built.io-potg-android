package com.raweng.projectsonthego;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.projectsonthego.Models.StepsModel;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.Utilities.FlowLayout;

/**
 * To display details of selected task.
 * 
 * @author raw engineering,Inc
 *
 */
public class UITaskDetailScreen extends Activity implements IFetchUserList{

	private final String PROJECT_UID         = "projectUID";
	private final String UID                 = "UID";
	private static final String NAME         = "name";
	private static final String POSITION     = "position";
	private final String TAG 				 = "UITaskDetailScreen";
	private final String CLASS_UID_TASK      = "task";
	private final String CLASS_UID_COMMENT   = "comment";
	Context context;
	
	ArrayList<BuiltObject> listObject;

	String taskNameValue = null;
	String taskUid  = null;
	String userType = null;
	String assignName = null;

	EditText taskName;
	EditText taskDescribtion;
	FlowLayout assigneeContainer;
	LinearLayout stepsContainer;
	Button addAssignee;
	Button addSteps;
	Button commentLabel;
	TextView commentCountTextView;
	ImageView removeStepButton;

	ProgressDialog progressDialog;
	
	ArrayList<StepsModel> stepsList;
	List<HashMap<String, Object>> stepsMap =  new ArrayList<HashMap<String,Object>>(); 
	private String[] assigneeUid;

	String projectUID;
	public ArrayList<String> assigneeIdList;
	int position;

	private RelativeLayout commentsContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_task_details);
		
		context = UITaskDetailScreen.this;
		
		if (getIntent().getExtras() != null) {
			position = getIntent().getExtras().getInt(POSITION);
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);

		taskNameValue 	= getIntent().getExtras().getString(NAME);
		taskUid 		= getIntent().getExtras().getString(UID);
		projectUID      = getIntent().getStringExtra(PROJECT_UID); 

		/*Get the user type of the logged in user.
		 *user types are admin, moderator, member*/
		userType 		= AppSettings.getUserType(context);

		taskName       		= (EditText) findViewById(R.id.taskNameField);
		taskDescribtion 	= (EditText) findViewById(R.id.taskDescriptionField);
		assigneeContainer	= (FlowLayout) findViewById(R.id.taskAssigneeContainer);	
		stepsContainer      = (LinearLayout) findViewById(R.id.stepsContainer);
		addAssignee 		= (Button)findViewById(R.id.addAssignee);
		addSteps 			= (Button)findViewById(R.id.addSteps);
		
		commentsContainer = (RelativeLayout) findViewById(R.id.commentsContainer);
		commentsContainer.setVisibility(View.VISIBLE);
		commentCountTextView        = (TextView)findViewById(R.id.commentCountTextView);
		commentLabel        = (Button)findViewById(R.id.commentLabel);

		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setMessage(getString(R.string.loading));

		progressDialog.show();

		addAssignee.setOnClickListener(clickListener);
		addSteps.setOnClickListener(clickListener);
		commentLabel.setOnClickListener(clickListener);

		//Create a query object to fetch details of BuiltObject inside task class.
		final BuiltQuery builtquery = new BuiltQuery(CLASS_UID_TASK);
		ArrayList<String> user = new ArrayList<String>();
		user.add("assignees");

		//Execute the query to fetch current task object along with owner data and assignees data in the response.
		builtquery.includeOwner().include(user).where("uid", taskUid).exec(new QueryResultsCallBack() {

			@Override
			public void onSuccess(QueryResult builtqueryresult) {

				//QueryResult object return the array list of BuiltObject and its complete field information.
				listObject = (ArrayList<BuiltObject>) builtqueryresult.getResultObjects();
				int size = listObject.size();

				for(int i = 0;i < size;i++){
					//Extracting information of built object.
					extractData(listObject.get(i));
				}
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG,error.errorMessage());
			}

			@Override
			public void onAlways() {
				//progressDialog can be dismissed in fetch comments.
			}
		});
	}
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			
			if(view == addAssignee){

				// to add assignees to this task.
				FragmentManager fragmentManager = getFragmentManager();
				UserListDialogFragment userListDialog = new UserListDialogFragment();
				userListDialog.iFetchUserList = UITaskDetailScreen.this;
				if (assigneeIdList != null) {
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("AssigneeUid",assigneeIdList);
					userListDialog.setArguments(bundle);
				}
				userListDialog.show(fragmentManager, getString(R.string.fragment_edit_name));

			}else if(view == addSteps){

				// add steps.
				LayoutInflater inflater = LayoutInflater.from(context);
				View childView = inflater.inflate(R.layout.view_steps_task, null);
				removeStepButton = (ImageView) childView.findViewById(R.id.closeStepsTask);
				removeStepButton.setOnClickListener(clickListener);
				stepsContainer.addView(childView);

			}else if(view == removeStepButton){

				// to remove added step.
				stepsContainer.removeView((View) view.getParent());
			}else if (view ==  commentLabel) {
				
				//to open comments
				Intent commentIntent = new Intent(context, UICommentScreen.class);
				commentIntent.putExtra("menuType", AppConstant.PROJECT_TASK);
				commentIntent.putExtra("menuUid", taskUid);
				commentIntent.putExtra("projectUid", projectUID);
				startActivityForResult(commentIntent, AppConstant.COMMENT_COUNT_REQUEST_CODE);
				overridePendingTransition( R.anim.slide_in_from_bottom, R.anim.slide_out_to_top );
			}

		}
	};

	/**
	 * extract response which comes from built.io server after executing network call.
	 * 
	 * @param builtObject
	 * 				builtObject.
	 */
	protected void extractData(BuiltObject builtObject) {

		try{
			 
			//builtObject.get("name") returns task name.
			taskName.setText(builtObject.getString(NAME));
			
			//Check for description field present or not
			if (builtObject.has("description")) {
				 
				//builtObject.get("description") returns task description.
				taskDescribtion.setText(builtObject.getString("description"));
			}

			//Check for assignees fields present or not
			if(builtObject.has("assignees")){
				
				//builtObject.getAllObjects(key, classUid) used for get array list of built object inside references field
				ArrayList<BuiltObject> assignees = builtObject.getAllObjects("assignees", taskUid);
				int assigneeCounter = assignees.size();

				assigneeIdList = new ArrayList<String>();
				assigneeContainer.removeAllViews();

				for(int i = 0; i < assigneeCounter; i++){
					TextView  assignNameView = new TextView(context);

					assignNameView.setBackgroundResource(R.drawable.bubble_background);
					assignNameView.setTextSize(getResources().getDimension(R.dimen.assignee_email_font_size));
					assignNameView.setPadding(10, 10, 10, 10);
					assignNameView.setTextColor(getResources().getColor(R.color.assignee_email_text));
					FlowLayout.LayoutParams flowParams = new FlowLayout.LayoutParams(4, 4);
					assignNameView.setLayoutParams(flowParams);

					assigneeIdList.add(assignees.get(i).getString("uid"));
					assignNameView.setText(assignees.get(i).getString("email"));

					assigneeContainer.addView(assignNameView);
				}
			}
			
			//Check for assignees fields present or not
			if(builtObject.has("steps")){

				//builtObject.getAllObjects(key, classUid) used for get array list of built object inside references field.
				ArrayList<BuiltObject> steps = builtObject.getAllObjects("steps", taskUid);

				int stepCounter   = steps.size();
				stepsList = new ArrayList<StepsModel>();

				for(int i = 0; i < stepCounter; i++){

					StepsModel model = new StepsModel();

					model.name        = steps.get(i).getString(NAME);
					model.description = steps.get(i).getString("description");
					model.complete    = (Boolean) steps.get(i).getBoolean("complete");
					stepsList.add(model);
				}

				int stepsListCounter = stepsList.size();
				for(int i = 0; i < stepsListCounter; i++){
					setStepsData(stepsList.get(i));
				}
			}
			
			fetchCommentsCount();
		}catch(Exception exe){
			exe.printStackTrace();
		}
	}

	/**
	 * set steps data and create a view to display steps.
	 * 
	 * @param stepsModel
	 * 						step model.
	 */
	private void setStepsData(StepsModel stepsModel) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.view_steps_task, null);

		EditText stepTaskName = (EditText) view.findViewById(R.id.stepTaskName);
		EditText stepTaskDescription = (EditText) view.findViewById(R.id.stepTaskDescription);
		CheckBox isCompleteCheck = (CheckBox) view.findViewById(R.id.isCompleteCheck);
		removeStepButton = (ImageView) view.findViewById(R.id.closeStepsTask);
		removeStepButton.setOnClickListener(clickListener);

		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString()) || userType.equalsIgnoreCase(AppConstant.userRole.moderator.toString())){
			isCompleteCheck.setEnabled(true);
			removeStepButton.setEnabled(true);
		}else{
			isCompleteCheck.setEnabled(false);
			removeStepButton.setEnabled(false);
		}

		stepTaskName.setText(stepsModel.name);
		stepTaskDescription.setText(stepsModel.description);

		if(stepsModel.complete){
			isCompleteCheck.setChecked(true);
		}else{
			isCompleteCheck.setChecked(false);
		}

		stepsContainer.addView(view);

	}

	/**
	 * This method is used to update a task.
	 */
	private void updateTask() {
		progressDialog.setMessage(getString(R.string.updating_task));
		progressDialog.show();
		
		//Update a task object.
		BuiltObject object = new BuiltObject(CLASS_UID_TASK);
		
		//Set a value for name field.
		object.set(NAME, taskName.getText().toString().trim());
		
		if(taskDescribtion.length() > 0){
			//Set a value for description field.
			object.set("description", taskDescribtion.getText().toString().trim());
		}
		
		//Set a unique uid of task object which comes in response of the execute call of the Built Query.
		object.setUid(taskUid);
		
		//Set assignees uid's to assignees field.
		object.setReference("assignees", assigneeUid);
		addStepsList();
		//Set a value for steps field.
		object.set("steps", stepsMap);

		//Update call for task object.
		object.save(new BuiltResultCallBack() {

			@Override
			public void onSuccess() {
				Toast.makeText(context, getString(R.string.task_updated_successfully), Toast.LENGTH_SHORT).show();
				hideKeyboard();
				finish();
				
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG,error.errorMessage());
				Toast.makeText(context, error.errorMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {
				progressDialog.dismiss();
			}
		});

	}

	/**
	 * This method add steps to task.
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

				hashmapObject.put(NAME, stepTaskName.getText().toString().trim());

				if(stepTaskDescription.getText().length() > 0){
					hashmapObject.put("description", stepTaskDescription.getText().toString().trim());
				}
				if(isCompleteCheck.isChecked()){
					hashmapObject.put("complete", true);
				}else{
					hashmapObject.put("complete", false);
				}
				//adding hash map to steps field.
				stepsMap.add(hashmapObject);
			}
		}
	}

	@Override
	public void fetchUserList(ArrayList<UserModel> objects,boolean isModeratorList) {
		
		int count = objects.size();
		assigneeUid = new String[count];
		String[] result = null;
		assigneeContainer.removeAllViews();

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

				assigneeContainer.addView(emailIdView);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action, menu);
		MenuItem updateMenu = menu.findItem(R.id.bugaction);
		updateMenu.setTitle(R.string.update);
		updateMenu.setVisible(false);
		MenuItem commentMenu = menu.findItem(R.id.comment);
		commentMenu.setVisible(false);

		MenuItem deleteTask= menu.findItem(R.id.delete);
		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString()) || userType.equalsIgnoreCase(AppConstant.userRole.moderator.toString())){
			updateMenu.setVisible(true);
			deleteTask.setVisible(true);
		}
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
			// Too update task on built.io server.
			updateTask();

			return true;

		case R.id.delete:


			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(UITaskDetailScreen.this);
			dlgAlert.setMessage(R.string.really_want_to_delete_task);
			dlgAlert.setTitle(R.string.delete_task);
			dlgAlert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					progressDialog.setMessage(getString(R.string.deleting_task));
					progressDialog.show();
					BuiltObject built = new BuiltObject(CLASS_UID_TASK);
					built.setUid(taskUid);
					built.destroy(new BuiltResultCallBack() {


						@Override
						public void onSuccess() {
							Toast.makeText(context,getString(R.string.task_deleted_successfully),Toast.LENGTH_LONG).show();

							Intent resultIntent = new Intent();
							resultIntent.putExtra(POSITION, position-1);
							setResult(Activity.RESULT_OK, resultIntent);
							
							hideKeyboard();
							finish();
						}

						@Override
						public void onError(BuiltError error) {
							AppUtils.showLog(TAG,error.errorMessage());
							Toast.makeText(UITaskDetailScreen.this,error.errorCode()+" : "+error.errorMessage(),Toast.LENGTH_LONG).show();
						}

						@Override
						public void onAlways() {
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
		}
		return false;
	}

	/**
	 * Fetch comments count.
	 */
	private void fetchCommentsCount(){

		progressDialog.setMessage(getString(R.string.loading_comments_count));
		
		//Create built query object of comment class.
		BuiltQuery query = new BuiltQuery(CLASS_UID_COMMENT);

		//Create built query object of task class.
		BuiltQuery builtQueryObject = new BuiltQuery(CLASS_UID_TASK);

		//Fetch a particular built object using uid.
		builtQueryObject.where("uid", taskUid);

		//Fetch reference objects of type task
		query.inQuery("for_task", builtQueryObject);

		//Execute a query.
		query.exec(new QueryResultsCallBack() {

			@Override
			public void onSuccess(QueryResult builtqueryresult) {
				commentCountTextView.setText(""+builtqueryresult.getResultObjects().size());
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG,error.errorMessage());
			}

			@Override
			public void onAlways() {
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
	
	/**
	 * hides the  keyboard for all the edit texts in this class
	 */
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(taskName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(taskDescribtion.getWindowToken(), 0);
	}

}

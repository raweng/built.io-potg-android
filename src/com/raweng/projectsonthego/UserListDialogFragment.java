package com.raweng.projectsonthego;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.datasource.UserListDataSource;

/**
 * Dialog fragment to select user's from list.
 * @author raw engineering, Inc
 *
 */
public class UserListDialogFragment extends DialogFragment implements OnEditorActionListener{
	
	private final String TAG = "UserListDialogFragment";

	LinearLayout         dialogAssignMember;
	ListView   	         dialogAssignMemberList;
	Button 				 saveUserList;
	Button				 cancelButton;
	ProgressBar 		 progressBar;
	HorizontalScrollView assignNameScroll;

	//List of already selected assigneeUid's.
	ArrayList<String> assigneeUid;

	//Adapter to be set for the listview.
	ArrayAdapter<String> adapter;

	//List of all selected assigneeUid
	public ArrayList<BuiltObject> userListObject = new ArrayList<BuiltObject>();

	UserListDataSource datasource;
	ViewGroup view;

	IFetchUserList iFetchUserList;
	boolean isModeratorUserList = false;
	
	//Empty constructor for fragment.
	public UserListDialogFragment(){}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		view = (ViewGroup) inflater.inflate(R.layout.view_list_users, container);

		dialogAssignMember	   = (LinearLayout) view.findViewById(R.id.assingeePanel);
		dialogAssignMemberList = (ListView) view.findViewById(R.id.userListView);
		assignNameScroll	   = (HorizontalScrollView) view.findViewById(R.id.assingeeContainerss);
		saveUserList		   = (Button)view.findViewById(R.id.saveMilstone);
		cancelButton		   		   = (Button)view.findViewById(R.id.cancelMilstone);
		progressBar		       = (ProgressBar)view.findViewById(R.id.lodingUser);

		getDialog().setTitle(R.string.select_user);
		
		 Bundle args = getArguments();
		    if (args == null) {
		    } else if(args.containsKey("AssigneeUid") && !args.getStringArrayList("AssigneeUid").isEmpty()) {
		    	
		       //Get the assigneeId from arguments.
		    	assigneeUid = args.getStringArrayList("AssigneeUid");
		    } 
		    
		//To create a new instance.    
		BuiltApplication builtApp = new BuiltApplication();
		
		//To get BuiltQuery instance, using this instance you can query and fetch list of users.
		BuiltQuery appUser = builtApp.applicationUserQuery();
		
		//To Execute a query.
		appUser.exec(new QueryResultsCallBack() {

			@Override
			public void onSuccess(QueryResult builtqueryresult) {
				progressBar.setVisibility(View.GONE);
				saveUserList.setVisibility(View.VISIBLE);
				cancelButton.setVisibility(View.VISIBLE);
				
				ArrayList<BuiltObject> listObject = (ArrayList<BuiltObject>) builtqueryresult.getResultObjects();

					try {
						if (listObject.size() > 0 && listObject != null) {
							datasource = new UserListDataSource(getActivity(),
									R.layout.view_list_row_user, listObject,
									assigneeUid);
							dialogAssignMemberList.setAdapter(datasource);
						}
					} catch (Exception e) {
						AppUtils.showLog(TAG, e.toString());
					}
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(""+error.getErrorCode(), ""+error.getErrorMessage());
				Toast.makeText(getActivity(),error.getErrorCode()+""+error.getErrorMessage(),Toast.LENGTH_LONG).show();
			}

			@Override
			public void onAlways() {

			}
		});
		
		//On save button click return the selected assignees to calling class.
		saveUserList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				int count = userListObject.size();
				
				ArrayList<UserModel> userList = new ArrayList<UserModel>();
				for(int i = 0; i < count; i++){
					userList.add(new UserModel(userListObject.get(i)));
				}
				
				int userModelCount = datasource.getAssigneeModelList().size();
				for(int i = 0; i < userModelCount; i++){
					if(userList.contains(datasource.getAssigneeModelList().get(i))){
					}else{
						userList.add(datasource.getAssigneeModelList().get(i));
					}
				}
				
				//Return the list of assignees to the calling class.
				iFetchUserList.fetchUserList(userList, isModeratorUserList);
				getDialog().dismiss();
			}
		});
		
		//To cancel the dialog fragment.
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getDialog().dismiss();
			}
		});
		
		//On list click, check and uncheck the users.
		dialogAssignMemberList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position,long id) {
				final CheckBox checkedUser = (CheckBox)view.findViewById(R.id.CheckBox01);
				final TextView userName = (TextView) view.findViewById(R.id.userName);
				
				//Get the data associated with the listview of that position.
				BuiltObject object  = (BuiltObject) dialogAssignMemberList.getItemAtPosition(position);
				boolean check = checkedUser.isChecked();
				if(check){
					
					//Uncheck user on unselect.
					checkedUser.setChecked(false);
					
					//Black text color for unchecked user.
					userName.setTextColor(Color.BLACK);
					
					//Remove the object at position.
					userListObject.remove(object);
				}else{
					//Check when selected.
					checkedUser.setChecked(true);
					
					//Red color for checked user.
					userName.setTextColor(Color.RED);
					
					try {
						
						//Add object at position.
						userListObject.add(object);
					} catch (Exception e) {
						AppUtils.showLog(TAG,e.toString());
					}
				}
			}
		});
		
		return view;
	}
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);

	}
	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		
		return false;
	}

}

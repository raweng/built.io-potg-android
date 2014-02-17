package com.raweng.projectsonthego;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.userInterface.BuiltListViewResultCallBack;
import com.raweng.built.userInterface.BuiltUIListViewController;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.ViewHolders.TaskViewHolder;

/**
 * To display list of task of selected project.
 * 
 * @author raw engineering,Inc
 *
 */
public class ProjectTaskFragment extends Fragment {

	private static final String CLASSUID = "task";
	private String menuUID; // project UID 
	private final int CREATE_TASK_REQUEST_CODE = 5000;
	private final int DELETE_TASK_REQUEST_CODE = 5001;
	private BuiltUIListViewController listView;
	private final String TAG = "ProjectTaskFragment";
	private String membersRoleUid;
	private String moderatorsRoleUid;
	
	public ProjectTaskFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String menuName = getArguments().getString(getActivity().getString(R.string.menu_name));
		menuUID  = getArguments().getString(getActivity().getString(R.string.menu_uid));
		this.setHasOptionsMenu(true);
		
		Bundle args = getActivity().getIntent().getExtras();
		if (args != null) {
			membersRoleUid    = args.getString("membersRoleUid");
			moderatorsRoleUid = args.getString("moderatorsRoleUid");
		}

		//Initialize BuiltListViewProvider instance using context and class uid.
		listView = new BuiltUIListViewController(getActivity(), CLASSUID);
		ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage(getActivity().getString(R.string.loading_task_list));
		listView.setProgressDialog(progress);
		getActivity().setTitle(menuName);
		
		//Enable pull to  refresh.
		listView.setPullToRefresh(true);
		
		//Set limit for object to load while fetch call. 
		listView.setLimit(5);
		
		//Add function to built query instance of BuiltListViewProvider class to get extra information.
		listView.getBuiltQueryInstance().includeOwner().descending("updated_at");

		String[] projectUIDList = new String[]{menuUID};
		
		//Fetch the tasks belonging to this project.
		listView.getBuiltQueryInstance().containedIn("project", projectUIDList);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				Intent taskDetailIntent = new Intent(getActivity(), UITaskDetailScreen.class);
				
				//To get the name and uid of this task from BuiltObject instance.
				BuiltObject builtObjectInstance = (BuiltObject) arg0.getAdapter().getItem(position);
				taskDetailIntent.putExtra("name", builtObjectInstance.getString("name").toString());
				taskDetailIntent.putExtra("UID", builtObjectInstance.getUid().toString());
				taskDetailIntent.putExtra("projectUID", menuUID);
				taskDetailIntent.putExtra("position", position);//position to keep track, if user wants to delete this task.
				startActivityForResult(taskDetailIntent, DELETE_TASK_REQUEST_CODE);

			}
		});


		//Load listview with task objects.
		listView.loadData(new BuiltListViewResultCallBack() {

			@Override
			public View getView(int position, View convertView,ViewGroup parent, BuiltObject builtObject) {

				TaskViewHolder holder = null;
				if(convertView == null){
					LayoutInflater inflater = LayoutInflater.from(getActivity());

					convertView = inflater.inflate(R.layout.view_list_row_task, parent, false);

					holder                    = new TaskViewHolder();
					holder.taskNameInitial 	  = (TextView) convertView.findViewById(R.id.taskNameInitial);
					holder.taskName           = (TextView) convertView.findViewById(R.id.taskName);

					convertView.setTag(holder);
				}else{
					holder = (TaskViewHolder) convertView.getTag();

				}
				holder.populateView(builtObject);
				//return view to set list row.
				return convertView;
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG, error.getErrorMessage());
			}

			@Override
			public void onAlways() {
				//It called when call is complete.
			}

			@Override
			public int getItemViewType(int position) {
				return 0;
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}
		});
		
		//Set listview layout from built.io sdk
		return listView.getLayout();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem menuItemTask= menu.findItem(R.id.action);
		menuItemTask.setTitle(R.string.create_task);
		
		//Get logged in user type.
		String userType = AppSettings.getUserType(getActivity());
		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString()) || userType.equalsIgnoreCase(AppConstant.userRole.moderator.toString())){
			menuItemTask.setVisible(true);
		}else{
			menuItemTask.setVisible(false).setEnabled(false);
		}
		menuItemTask.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				//Start activity on menu click
				Intent intent = new Intent(getActivity(), UICreateTaskScreen.class);
				Bundle args = new Bundle();
				args.putString("projectUID", menuUID);
				args.putString("membersRoleUid",membersRoleUid);
				args.putString("moderatorsRoleUid",moderatorsRoleUid);
				intent.putExtras(args);
				startActivityForResult(intent, CREATE_TASK_REQUEST_CODE);
				return false;
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		try {
			switch (requestCode) {
			
			//On create task update list
			case CREATE_TASK_REQUEST_CODE:{
				if (resultCode == Activity.RESULT_OK && data != null) {
					if (listView.getDataSourceObject() != null) {
						//added new built object in list
						listView.insertBuiltObjectAtIndex(0, (BuiltObject) UICreateTaskScreen.createTaskHashMap.get("builtObject"));
						listView.notifyDataSetChanged();
					}
				}
			}
			break;
			
			//On delete task update list
			case DELETE_TASK_REQUEST_CODE:{
				if(resultCode == Activity.RESULT_OK && data != null){
					//deleted built object at particular position.
					listView.deleteBuiltObjectAtIndex(data.getExtras().getInt("position"));
					listView.notifyDataSetChanged();
				}
			}
			break;
			default:
				break;
			}
		} catch (Exception e) {
			AppUtils.showLog(TAG,e.toString());
		}
	}
}


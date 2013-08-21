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
import com.raweng.projectsonthego.ViewHolders.MilestoneViewHolder;

public class ProjectMilestoneFragment extends Fragment {

	private static final String CLASSUID = "milestone";
	private String menuUID; // project UID 

	private final int CREATE_MILESTONE_REQUEST_CODE = 5000; 
	private final int DELETE_MILESTONE_REQUEST_CODE = 5001;
	private BuiltUIListViewController listView;
	private final String TAG = "ProjectMilestoneFragment";
	private String membersRoleUid;
	private String moderatorsRoleUid;
	
	public ProjectMilestoneFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String menuName = getArguments().getString(getActivity().getString(R.string.menu_name));
		menuUID  = getArguments().getString(getActivity().getString(R.string.menu_uid));
		this.setHasOptionsMenu(true);

		//Initialize BuiltListViewProvider instance using context and CLASSUID.
		listView = new BuiltUIListViewController(getActivity(), CLASSUID);


		getActivity().setTitle(menuName);

		Bundle args = getActivity().getIntent().getExtras();
		if (args != null) {
			membersRoleUid    = args.getString("membersRoleUid");
			moderatorsRoleUid = args.getString("moderatorsRoleUid");
		}

		ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage(getResources().getString(R.string.loading_milestone_list));
		//set progressDialog object inside class while list loading.
		listView.setProgressDialog(progress);

		//Set pull to refresh enable.
		listView.setPullToRefresh(true);
		//Set limit for object to load while fetch call. 
		listView.setLimit(5);
		//Add function to built query instance of BuiltListViewProvider class to get extra information.
		listView.builtQueryInstance.includeOwner().descending("updated_at");

		String[] projectUIDList = new String[]{menuUID};
		//Fetch the tasks belonging to this project.
		listView.builtQueryInstance.containedIn("project", projectUIDList);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				//To get the name and uid of this task from BuiltObject instance.
				Intent milestoneDetailIntent = new Intent(getActivity(), UIMilestoneDetailScreen.class);
				BuiltObject builtObjectInstance = (BuiltObject) arg0.getAdapter().getItem(position);
				milestoneDetailIntent.putExtra("name", builtObjectInstance.getString("name").toString());
				milestoneDetailIntent.putExtra("UID", builtObjectInstance.getUid().toString());
				milestoneDetailIntent.putExtra("projectUID", menuUID);
				milestoneDetailIntent.putExtra("position", position);
				startActivityForResult(milestoneDetailIntent, DELETE_MILESTONE_REQUEST_CODE);

			}
		});

		//Make a call to Load data in list.
		listView.loadData(new BuiltListViewResultCallBack() {


			@Override
			public void onAlways() {
				//It called when call is complete.
			}

			@Override
			public View getView(int position, View convertView,ViewGroup parent, BuiltObject builtObject) {

				MilestoneViewHolder viewHolder = null;

				if(convertView == null){
					LayoutInflater inflater = LayoutInflater.from(getActivity());

					convertView = inflater.inflate(R.layout.list_row_milestones, parent, false);

					viewHolder                         = new MilestoneViewHolder();
					viewHolder.milestoneTitleInitial   = (TextView) convertView.findViewById(R.id.milestoneTitleInitial);
					viewHolder.milestoneName           = (TextView) convertView.findViewById(R.id.milestoneName);
					viewHolder.milestoneStartEndDate   = (TextView) convertView.findViewById(R.id.milestoneStartEndDate);

					convertView.setTag(viewHolder);
				}else{
					viewHolder = (MilestoneViewHolder) convertView.getTag();
				}
				viewHolder.populateView(builtObject);
				//return view to set list row.
				return convertView;
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG, error.getErrorMessage());
				
			}
		});

		//Set listview layout from built.io sdk
		return listView.getLayout();
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);

		MenuItem menuItemMilstone = menu.findItem(R.id.action);
		menuItemMilstone.setTitle(getString(R.string.create_milestone));
		String userType = AppSettings.getUserType(getActivity());

		//If user type is admin/moderator, can create a milestone. Else cannot create a milestone.
		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString()) || userType.equalsIgnoreCase(AppConstant.userRole.moderator.toString())){
			menuItemMilstone.setVisible(true);
		}else{
			menuItemMilstone.setVisible(false).setEnabled(false);
		}

		menuItemMilstone.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getActivity(), UICreateMilestoneScreen.class);
				Bundle args = new Bundle();
				args.putString("projectUID", menuUID);
				args.putString("membersRoleUid",membersRoleUid);
				args.putString("moderatorsRoleUid",moderatorsRoleUid);
				intent.putExtras(args);
				startActivityForResult(intent, CREATE_MILESTONE_REQUEST_CODE);
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
			case CREATE_MILESTONE_REQUEST_CODE:
				if(resultCode == Activity.RESULT_OK && data != null){
					//added new built object in list
					listView.insertBuiltObjectAtIndex(0, (BuiltObject) UICreateMilestoneScreen.createMilestoneHashMap.get("builtObject"));
					listView.notifyDataSetChanged();
				}
				break;

				//On delete task update list
			case DELETE_MILESTONE_REQUEST_CODE:{
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
			AppUtils.showLog(TAG, e.toString());
		
		}
	}

}

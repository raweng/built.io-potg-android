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
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.ViewHolders.BugViewHolder;

public class ProjectBugFragment extends Fragment{

	private static final int CREATE_BUG_REQUEST_CODE = 5000; 
	private static final int DELETE_BUG_REQUEST_CODE = 5001;
	private static final String TAG = "ProjectBugFragment";
	private static final String CLASSUID = "bugs";
	private String menuUID; // project UID 
	private BuiltUIListViewController listView;
	private String moderatorRoleUid;
	private String memberRoleUid;

	public ProjectBugFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		String menuName = getArguments().getString(getActivity().getString(R.string.menu_name));
		menuUID  = getArguments().getString(getActivity().getString(R.string.menu_uid));
		this.setHasOptionsMenu(true);


		//Initialize BuiltListViewProvider instance using context and classuid.
		listView = new BuiltUIListViewController(getActivity(), CLASSUID);

		getActivity().setTitle(menuName);

		Bundle args = getActivity().getIntent().getExtras();
		if (args != null) {
			moderatorRoleUid  = args.getString("moderatorsRoleUid"); 
			memberRoleUid	   = args.getString("membersRoleUid"); 
		}


		//Set pull to refresh enable.
		ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage(getResources().getString(R.string.loading_bugs_list));
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
				Intent bugDetailIntent = new Intent(getActivity(), UIBugDetailScreen.class);
				BuiltObject builtObjectInstance = (BuiltObject) arg0.getAdapter().getItem(position);
				bugDetailIntent.putExtra("name", builtObjectInstance.getString("name").toString());
				bugDetailIntent.putExtra("UID", builtObjectInstance.getUid().toString());
				bugDetailIntent.putExtra("projectUID", menuUID);
				bugDetailIntent.putExtra("position", position);
				startActivityForResult(bugDetailIntent, DELETE_BUG_REQUEST_CODE);

			}
		});

		listViewLoadData();

		//Set listview layout from built.io sdk
		return listView.getLayout();
	}

	private void listViewLoadData(){
		
		//Make a call to Load data in list.
		listView.loadData(new BuiltListViewResultCallBack() {

			@Override
			public void onAlways() {
				//It called when call is complete.
			}

			@Override
			public View getView(int position, View convertView,ViewGroup parent, BuiltObject builtObject) {

				BugViewHolder viewHolder = null;
				if(convertView == null){
					LayoutInflater inflater = LayoutInflater.from(getActivity());

					convertView = inflater.inflate(R.layout.list_row_bug, parent, false);

					viewHolder                 = new BugViewHolder();
					viewHolder.bugTitleInitial = (TextView) convertView.findViewById(R.id.bugTitleInitial);
					viewHolder.bugTitle        = (TextView) convertView.findViewById(R.id.bugTitle);
					viewHolder.bugAssignee     = (TextView) convertView.findViewById(R.id.bugAssignee);
					viewHolder.bugStatus       = (TextView) convertView.findViewById(R.id.bugStatus);
					viewHolder.bugSeverity     = (TextView) convertView.findViewById(R.id.bugSeverity);

					convertView.setTag(viewHolder);

				}else{
					viewHolder = (BugViewHolder) convertView.getTag();
				}

				viewHolder.populateView(builtObject);
				//return view to set list row.
				return convertView;
			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG,error.getErrorMessage());

			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.main, menu);
		MenuItem menuItemMilstone= menu.findItem(R.id.action);
		menuItemMilstone.setTitle(getString(R.string.title_activity_createbug));

		menuItemMilstone.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getActivity(), UICreateBugScreen.class);
				
				Bundle args = new Bundle();
				args.putString("projectUID", menuUID);
				args.putString("membersRoleUid",memberRoleUid);
				args.putString("moderatorsRoleUid",moderatorRoleUid);
				intent.putExtras(args);
				startActivityForResult(intent, CREATE_BUG_REQUEST_CODE);
				return false;
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			switch (requestCode) {
			case CREATE_BUG_REQUEST_CODE:{
				if(resultCode == Activity.RESULT_OK && data != null){
					if (listView.getDataSourceObject() != null) {
						//added new built object in list
						listView.insertBuiltObjectAtIndex(0, (BuiltObject) UICreateBugScreen.createBugHashMap.get("builtObject"));
						listView.notifyDataSetChanged();
					}
				}
			}
			break;

			case DELETE_BUG_REQUEST_CODE:{
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
			e.printStackTrace();
		}
	}

}


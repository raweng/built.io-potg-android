package com.raweng.projectsonthego;

import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltRole;
import com.raweng.built.BuiltUser;
import com.raweng.built.RoleObject;
import com.raweng.built.userInterface.BuiltListViewProvider;
import com.raweng.built.userInterface.BuiltListViewResultCallBack;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.ViewHolders.ProjectViewHolder;

/**
 * Loads the list of projects.
 * @author raw engineering, Inc
 *
 */
public class UIProjectListScreen extends Fragment {

	public TextView title;
	public int menuIndex; // menu index with respect to constants

	String menuName;
	String classUID;
	String menuUID; // project UID 

	ProgressDialog progressDialog;
	BuiltListViewProvider listView;
	private final String TAG = "UIProjectListScreen";

	public UIProjectListScreen() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		menuIndex = getArguments().getInt(AppConstant.ARG_MENU_DETAIL_NUMBER);
		menuName = getResources().getStringArray(R.array.class_array)[menuIndex];
		getActivity().setTitle(menuName);
		classUID = "project";

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getResources().getString(R.string.loading_project_list));
		progressDialog.setTitle(getResources().getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		progressDialog.show();

		//Intialize BuiltListViewProvider instance.
		listView = new BuiltListViewProvider(getActivity(), "project");

		//Make a call for load project list.
		listView.loadData(new BuiltListViewResultCallBack() {

			@Override
			public View getView(int position, View convertView,ViewGroup parent, BuiltObject builtObject) {

				ProjectViewHolder holder = null;
				
				if(convertView == null){
					LayoutInflater inflater = LayoutInflater.from(getActivity());

					convertView = inflater.inflate(R.layout.list_row_project_name, parent, false);

					holder                    = new ProjectViewHolder();
					holder.projectNameInitial = (TextView) convertView.findViewById(R.id.taskNameInitial);
					holder.projectName        = (TextView) convertView.findViewById(R.id.taskName);

					convertView.setTag(holder);
				}else{
					holder = (ProjectViewHolder) convertView.getTag();

				}
				holder.populateView(builtObject);
				return convertView;

			}

			@Override
			public void onError(BuiltError error) {
				AppUtils.showLog(TAG,error.errorMessage());
				Toast.makeText(getActivity(), R.string.oops_something_went_wrong, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {
				progressDialog.dismiss();
			}
		});

		//Set pull to refresh enable.
		listView.setPullToRefresh(true);
		//Set limit for object to load while fetch call. 
		listView.setLimit(10);
		//Add function to built query instance of BuiltListViewProvider class to get extra information.
		listView.builtQueryInstance.includeOwner().descending("updated_at");

		String[] projectUIDList = new String[]{menuUID};
		//Fetch the tasks belonging to this project.
		listView.builtQueryInstance.containedIn("project", projectUIDList);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String userType = AppSettings.getUserType(getActivity());
				if(!userType.equalsIgnoreCase(AppConstant.userRole.admin.toString())){
					setUserType(parent,position);	
				}else{
					Intent projectDetailIntent = new Intent(getActivity(), UIProjectDetailScreen.class);
					BuiltObject builtObjectInstance = (BuiltObject) parent.getAdapter().getItem(position);
					projectDetailIntent.putExtra("name", (builtObjectInstance.getString("name")));
					projectDetailIntent.putExtra("UID", builtObjectInstance.getUid().toString());
					startActivity(projectDetailIntent);

				}
			}

		});
		return listView.getLayout();
	}


	/**
	 * Fetch user type of logged in user.
	 * @param position 
	 * 					position.
	 * @param parent 
	 * 					parent.
	 */
	private void setUserType(final AdapterView<?> parent, final int position) {

		progressDialog.setMessage(getResources().getString(R.string.fetching_role));
		progressDialog.show();
		//Create object of built role.
		final BuiltRole roles = new BuiltRole();
		final BuiltObject builtObjectInstance = (BuiltObject) parent.getAdapter().getItem(position);
		//Make call to fetch a role of logged in user.
		roles.fetchRoles(new BuiltResultCallBack() {

			@Override
			public void onSuccess() {

				//Create a built user object.
				BuiltUser user = new BuiltUser();
				String uid = null;

				//Get logged in user.
				user =  BuiltUser.currentUser();
				if(user != null && user.getUserUid() != null){
					uid = user.getUserUid();
				}

				//Get the role for specified RoleObject.
				if(builtObjectInstance.has("name")){
					try{
						RoleObject memberRoleObject = null;
						RoleObject moderatorsRoleObject = null;

						/* Fetch a role for members and moderators.
						 * builtObject.get("name") returns project name.*/
						moderatorsRoleObject = roles.getRole(builtObjectInstance.getString("name")+"_moderators");
						memberRoleObject = roles.getRole(builtObjectInstance.getString("name")+"_members");
						
						JSONObject moderator = moderatorsRoleObject.toJSON();
						JSONObject member    = memberRoleObject.toJSON();
						
						//Check for uid present inside moderators role or not.
						if(moderatorsRoleObject.hasUser(uid)){
							if(moderatorsRoleObject != null){
								AppSettings.setUserType(AppConstant.userRole.moderator.toString(), getActivity());
							}
							//Check for uid present inside member role or not.
						}else if(memberRoleObject.hasUser(uid)){
							if(memberRoleObject != null){
								AppSettings.setUserType(AppConstant.userRole.member.toString(), getActivity());
							}

						}else{
							AppSettings.setUserType(AppConstant.userRole.guest.toString(), getActivity());
						}


						Intent projectDetailIntent = new Intent(getActivity(), UIProjectDetailScreen.class);
						projectDetailIntent.putExtra("name", (builtObjectInstance.getString("name")));
						projectDetailIntent.putExtra("UID", builtObjectInstance.getUid().toString());

						Bundle bundle = new Bundle();
						bundle.putString("membersRoleUid",member.getString("uid"));
						bundle.putString("moderatorsRoleUid",moderator.getString("uid"));
						projectDetailIntent.putExtras(bundle);

						startActivity(projectDetailIntent);
					}catch(Exception e){
						AppUtils.showLog(TAG,e.toString());
					}
				}
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

}

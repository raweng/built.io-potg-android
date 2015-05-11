package com.raweng.projectsonthego;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.R.bool;
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

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.BuiltRole;
import com.raweng.built.BuiltUser;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.built.userInterface.BuiltListViewResultCallBack;
import com.raweng.built.userInterface.BuiltUIListViewController;
import com.raweng.built.utilities.BuiltConstant;
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
	BuiltUIListViewController listView;
	private final String TAG = "UIProjectListScreen";
    private BuiltApplication builtApplication;

    public UIProjectListScreen() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		menuIndex = getArguments().getInt(AppConstant.ARG_MENU_DETAIL_NUMBER);
		menuName = getResources().getStringArray(R.array.class_array)[menuIndex];
		getActivity().setTitle(menuName);
		classUID = "project";

        try {
            builtApplication = Built.application(getActivity(), "API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getResources().getString(R.string.loading_project_list));
		progressDialog.setTitle(getResources().getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		progressDialog.show();

		//Intialize BuiltListViewProvider instance.
		listView = new BuiltUIListViewController(getActivity(),"API_KEY","project");

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
				AppUtils.showLog(TAG,error.getErrorMessage());
				Toast.makeText(getActivity(), R.string.oops_something_went_wrong, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {
				progressDialog.dismiss();
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

		//Set pull to refresh enable.
		listView.setPullToRefresh(true);
		//Set limit for object to load while fetch call. 
		listView.setLimit(10);
		//Add function to built query instance of BuiltListViewProvider class to get extra information.
		listView.getBuiltQueryInstance().includeOwner().descending("updated_at");

		String[] projectUIDList = new String[]{menuUID};
		//Fetch the tasks belonging to this project.
		listView.getBuiltQueryInstance().containedIn("project", projectUIDList);

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
		final BuiltObject builtObjectInstance = (BuiltObject) parent.getAdapter().getItem(position);

        try {
            BuiltQuery moderatorQuery = builtApplication.getRolesQuery();
            BuiltQuery memberQuery = builtApplication.getRolesQuery();
            moderatorQuery.where("name", builtObjectInstance.getString("name")+"_moderators");
            memberQuery.where("name", builtObjectInstance.getString("name")+"_members");

            ArrayList<BuiltQuery> queryList = new ArrayList<BuiltQuery>();
            queryList.add(moderatorQuery);
            queryList.add(memberQuery);

            BuiltQuery query = builtApplication.getRolesQuery();
            query.or(queryList);

            //Make call to fetch a role of logged in user.
            query.execInBackground(new QueryResultsCallBack() {

                @Override
                public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {

                    if (builtError == null){

                        BuiltUser user;
                        String uid           = null;
                        JSONObject moderator = null;
                        JSONObject member    = null;
                        boolean isModerator  = false;
                        boolean isMember     = false;

                        //Get logged in user.
                        user =  builtApplication.getCurrentUser();
                        if(user != null && user.getUserUid() != null){
                            uid = user.getUserUid();
                        }

                        List<BuiltRole> roles = queryResult.getRoles();

                        for (int i = 0; i < roles.size(); i++) {
                            if(roles.get(i).getString("name").equalsIgnoreCase(builtObjectInstance.getString("name")+"_moderators")){
                                moderator = roles.get(i).toJSON();
                                isModerator = true;
                            }

                            if(roles.get(i).getString("name").equalsIgnoreCase(builtObjectInstance.getString("name")+"_members")){
                                member    = roles.get(i).toJSON();
                                isMember  = true;
                            }

                        }

                        //Get the role for specified RoleObject.
                        try{

                            //Check for uid present inside moderators role or not.
                            if(isModerator){
                                AppSettings.setUserType(AppConstant.userRole.moderator.toString(), getActivity());
                                //Check for uid present inside member role or not.
                            }else if(isMember){
                                AppSettings.setUserType(AppConstant.userRole.member.toString(), getActivity());

                            }else{
                                AppSettings.setUserType(AppConstant.userRole.guest.toString(), getActivity());
                            }


                            Intent projectDetailIntent = new Intent(getActivity(), UIProjectDetailScreen.class);
                            projectDetailIntent.putExtra("name", (builtObjectInstance.getString("name")));
                            projectDetailIntent.putExtra("UID", builtObjectInstance.getUid().toString());


                            Bundle bundle = new Bundle();
                            if(member != null){
                                bundle.putString("membersRoleUid",member.optString("uid"));
                            }
                            if(moderator != null){
                                bundle.putString("moderatorsRoleUid",moderator.optString("uid"));
                            }
                            projectDetailIntent.putExtras(bundle);

                            startActivity(projectDetailIntent);
                        }catch(Exception e){
                            AppUtils.showLog(TAG,e.toString());
                        }


                    }else {
                        AppUtils.showLog(TAG,builtError.getErrorMessage());
                    }

                    progressDialog.dismiss();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}

package com.raweng.projectsonthego.datasource;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.raweng.built.BuiltObject;
import com.raweng.projectsonthego.R;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.ViewHolders.UserListViewHolder;

public class UserListDataSource extends ArrayAdapter<BuiltObject> {

	Context context;
	int resource;
	ArrayList<BuiltObject> objects;
	ArrayList<String> assigneeUid;
	private ArrayList<UserModel> tempAssigneeList;

	public UserListDataSource(Context context, int resource,List<BuiltObject> objects,ArrayList<String> assigneeId) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.objects = (ArrayList<BuiltObject>) objects;
		this.assigneeUid = assigneeId;
		tempAssigneeList = new ArrayList<UserModel>();
		tempAssigneeList.clear();
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserListViewHolder viewHolder = null;
		if(convertView == null){

			LayoutInflater inflater = LayoutInflater.from(this.context);
			convertView = inflater.inflate(this.resource, parent, false);

			viewHolder          = new UserListViewHolder();
			viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
			viewHolder.userChecked = (CheckBox) convertView.findViewById(R.id.CheckBox01);

			convertView.setTag(viewHolder);
		}else{
			viewHolder = (UserListViewHolder) convertView.getTag();
			viewHolder.userName.setText("");

		}

		try {

			if (assigneeUid != null && assigneeUid.size() > 0) {

				int assigneeCount = assigneeUid.size();
				for(int i = 0; i<assigneeCount;i++){
					if (assigneeUid.get(i).contains(this.objects.get(position).getUid())) {

						tempAssigneeList.add(new UserModel(this.objects.get(position)));

						viewHolder.userChecked.setChecked(true);
						viewHolder.userName.setTextColor(Color.RED);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.objects.size() > 0 && this.objects != null) {
			viewHolder.populateFrom(new UserModel(this.objects.get(position)));
		}

		return convertView;
	}

	public ArrayList<UserModel>  getAssigneeModelList(){
		return tempAssigneeList;
	}

}

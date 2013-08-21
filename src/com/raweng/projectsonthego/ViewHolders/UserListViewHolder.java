package com.raweng.projectsonthego.ViewHolders;

import android.widget.CheckBox;
import android.widget.TextView;

import com.raweng.projectsonthego.Models.UserModel;

public class UserListViewHolder {
	public TextView userName;
	public CheckBox userChecked;
	
	public void populateFrom(UserModel userListModel){
		if(userListModel != null){
				userName.setText(userListModel.email);
				userName.setTag(userListModel.uid);
				
		}
	}
}

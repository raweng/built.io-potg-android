package com.raweng.projectsonthego;

import java.util.ArrayList;

import com.raweng.projectsonthego.Models.UserModel;

/**
 * To send user email id and UID from {@link UserListDialogFragment} to calling activity.
 * 
 * @author raw engineering, Inc
 *
 */
public interface IFetchUserList {


	public abstract void fetchUserList(ArrayList<UserModel> objects, boolean isModeratorList);


}

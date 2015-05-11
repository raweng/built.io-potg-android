package com.raweng.projectsonthego.Models;

import java.util.ArrayList;

import com.raweng.built.BuiltObject;

public class UserModel {
	
	public String uid;
	public String email;
	public String first_name;
	public String last_name;
	public boolean isEmailSelected;
	public ArrayList<BuiltObject> userListObject = new ArrayList<BuiltObject>();
	
	public UserModel(){
		
	}
	public UserModel(BuiltObject builtObject){
		try{
			
			email = builtObject.getString("email");
			uid	  = builtObject.getString("uid");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setUserList(boolean isAdded,BuiltObject object){
		if(isAdded){
			userListObject.add(object);
		}else{
			userListObject.remove(object);
		}
	}
}

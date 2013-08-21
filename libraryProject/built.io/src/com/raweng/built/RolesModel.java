package com.raweng.built;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.raweng.built.utilities.RawAppUtils;

/**
 * To accept roles data in JSON format and parse it.
 * 
 * @author raw engineering, Inc
 *
 */
public class RolesModel {


	protected ArrayList<RoleObject> roleList = new ArrayList<RoleObject>();

	RolesModel(JSONObject json){
		try{

			JSONArray roleObjectsArray = json.opt("objects") == null ? null : json.optJSONArray("objects");
			int count = roleObjectsArray.length();
			for(int i = 0; i < count; i++){

				RoleModel roleModel = new RoleModel(roleObjectsArray.optJSONObject(i), true);
				roleList.add(roleModel.roleObjectInstance);
			}

		}catch (Exception e) {
			RawAppUtils.showLog("RoleModel", "-------------------rolemodel parsing catch block|" + e);
		}
	}
}

package com.raweng.projectsonthego.ViewHolders;

import android.widget.TextView;

import com.raweng.built.BuiltObject;

public class ProjectViewHolder {

	public TextView projectNameInitial;
	public TextView projectName;

	public void populateView(BuiltObject builtObject) {

		String title = builtObject.getString("name");

		projectNameInitial.setText(title.substring(0, 1));
		projectName.setText(title);
	}

}

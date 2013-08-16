package com.raweng.projectsonthego.ViewHolders;

import android.widget.TextView;

import com.raweng.built.BuiltObject;

public class TaskViewHolder {
	public TextView taskNameInitial;
	public TextView taskName;

	public void populateView(BuiltObject builtObject) {

		String title = builtObject.getString("name");

		taskNameInitial.setText(title.substring(0, 1));
		taskName.setText(title);
	}
}

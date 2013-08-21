package com.raweng.projectsonthego.ViewHolders;

import android.view.View;
import android.widget.TextView;

import com.raweng.built.BuiltObject;

public class BugViewHolder {

	public TextView bugTitleInitial;
	public TextView bugTitle;
	public TextView bugAssignee;
	public TextView bugStatus;
	public TextView bugSeverity;

	public void populateView(BuiltObject builtObject) {

		String title = builtObject.getString("name");

		bugTitleInitial.setText(title.substring(0, 1));
		bugTitle.setText(title);
		if((builtObject.getString("status") != null)){
			bugStatus.setText(builtObject.getString("status"));
		}

		if((builtObject.getString("severity") != null)){
			bugSeverity.setText(builtObject.getString("severity"));
		}
		bugAssignee.setVisibility(View.INVISIBLE);
	}

}

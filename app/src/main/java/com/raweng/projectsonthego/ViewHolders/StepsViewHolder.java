package com.raweng.projectsonthego.ViewHolders;

import android.widget.CheckBox;
import android.widget.EditText;

import com.raweng.projectsonthego.Models.StepsModel;

public class StepsViewHolder {

	EditText stepTaskName;
	EditText stepTaskDescription;
	CheckBox isCompleteCheck;

	public void populateView(StepsModel stepsModel) {

		stepTaskName.setText(stepsModel.name);
		stepTaskDescription.setText(stepsModel.description);

		if(stepsModel.complete){
			isCompleteCheck.setChecked(true);
		}else{
			isCompleteCheck.setChecked(false);
		}

	}

}

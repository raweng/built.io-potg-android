package com.raweng.projectsonthego.ViewHolders;

import java.util.Calendar;

import android.widget.TextView;

import com.raweng.built.BuiltObject;
import com.raweng.projectsonthego.Utilities.AppUtils;

public class MilestoneViewHolder {

	public TextView milestoneTitleInitial;
	public TextView milestoneName;
	public TextView milestoneStartEndDate;
	Calendar startDateCalendar;
	Calendar endDateCalendar;
	
	public void populateView(BuiltObject builtObject) {

		String title = builtObject.getString("name");

		milestoneTitleInitial.setText(title.substring(0, 1));
		milestoneName.setText(title);

		String startDate = builtObject.getString("start_date");
		String endDate   = builtObject.getString("end_date");
		
		try{
			startDateCalendar = AppUtils.parseDate(startDate);
			endDateCalendar   = AppUtils.parseDate(endDate);
		}catch(Exception e){
			AppUtils.showLog("MilestoneViewHolder", "----------------------|"+e.toString());
		}
		
		if (startDateCalendar != null ) {
			String date = "From " + startDateCalendar.get(Calendar.DATE) + "/"
					+ startDateCalendar.get(Calendar.MONTH) + "/"
					+ startDateCalendar.get(Calendar.YEAR);
			if (endDateCalendar != null){
				date = date + " To " + endDateCalendar.get(Calendar.DATE) + "/"
				+ endDateCalendar.get(Calendar.MONTH) + "/"
				+ endDateCalendar.get(Calendar.YEAR);
			}
			milestoneStartEndDate.setText(date);
		}

	}

}

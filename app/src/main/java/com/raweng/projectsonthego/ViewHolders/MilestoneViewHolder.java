package com.raweng.projectsonthego.ViewHolders;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.raweng.built.BuiltObject;
import com.raweng.projectsonthego.Utilities.AppUtils;

public class MilestoneViewHolder {

	public TextView milestoneTitleInitial;
	public TextView milestoneName;
	public TextView milestoneStartEndDate;
	Date startDateCalendar;
	Date endDateCalendar;
	
	@SuppressLint("SimpleDateFormat")
	public void populateView(BuiltObject builtObject) {

		String title = builtObject.getString("name");

		milestoneTitleInitial.setText(title.substring(0, 1));
		milestoneName.setText(title);

		String startDate = builtObject.getString("start_date");
		String endDate   = builtObject.getString("end_date");
		
		try{

			SimpleDateFormat tibdateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			startDateCalendar = tibdateFormatter.parse(startDate);
			endDateCalendar   = tibdateFormatter.parse(endDate);
		
		}catch(Exception e){
			AppUtils.showLog("MilestoneViewHolder", "----------------------|"+e.toString());
		}
		
		if (startDateCalendar != null ) {
			SimpleDateFormat dateFormatter  = new SimpleDateFormat("dd");
			SimpleDateFormat monthFormatter = new SimpleDateFormat("MM");
			SimpleDateFormat yearFormatter  = new SimpleDateFormat("yyyy");
			
			String date = "From " + dateFormatter.format(startDateCalendar) + "/"
					+ monthFormatter.format(startDateCalendar) + "/"
					+ yearFormatter.format(startDateCalendar);
			if (endDateCalendar != null){
				date = date + " To " + dateFormatter.format(endDateCalendar)  + "/"
				+ monthFormatter.format(endDateCalendar) + "/"
				+ yearFormatter.format(endDateCalendar);
			}
			milestoneStartEndDate.setText(date);
		}

	}

}

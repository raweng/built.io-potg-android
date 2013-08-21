package com.raweng.projectsonthego;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;

/**
 * 
 * @author raw engineering, Inc
 *
 */
public class MainActivity extends Activity {
	
	private final String TAG = "MainActivity";
	Context context;
	
	private String[] list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main_layout);
		
		context = MainActivity.this;
		list = getResources().getStringArray(R.array.class_array);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(true);

		AppUtils.showLog(TAG,AppSettings.getUserType(getApplicationContext()));
		if (savedInstanceState == null) {
			selectItem(AppConstant.PROJECT);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem menuItem = menu.findItem(R.id.action);

		String userType = AppSettings.getUserType(context);
		
		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString())){    
			menuItem.setVisible(true);
			menuItem.setTitle(getString(R.string.create_project));
		}else{
			menuItem.setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action:
			// To create new project.
			startActivity(new Intent(context, UICreateProjectScreen.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * To open fragment.
	 * 
	 * @param position
	 */
	private void selectItem(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = new UIProjectListScreen();
		Bundle args = new Bundle();
		args.putInt(AppConstant.ARG_MENU_DETAIL_NUMBER, position);
		fragment.setArguments(args);
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		setTitle(list[position]);
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}

}

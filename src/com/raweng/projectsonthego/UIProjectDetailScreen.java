package com.raweng.projectsonthego;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.raweng.projectsonthego.Utilities.AppConstant;

public class UIProjectDetailScreen extends Activity{
	
	public static final String Task = "Task";
	public static final String Bugs = "Bugs";
	public static final String Milestone = "Milestone";

	Context context;
	
	private DrawerLayout mDrawerLayout;
	private ListView projectDetailListView;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] list;
	private String projectName;
	private String projectUID;
	private String moderatorRoleUid;
	private String memberRoleUid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_project_details);
		context = UIProjectDetailScreen.this;

		projectName = getIntent().getExtras().getString("name");
		projectUID = getIntent().getExtras().getString("UID");

		Bundle args = getIntent().getExtras();
		if (args != null) {
			moderatorRoleUid  = args.getString("moderatorsRoleUid"); 
			memberRoleUid	   = args.getString("membersRoleUid"); 
		}
		
		
		mTitle = mDrawerTitle = projectName;
		list = new String[] {"Bugs", "Task", "Milestone"};

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		projectDetailListView = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		projectDetailListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, list));
		projectDetailListView.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(AppConstant.PROJECT_BUG);
			projectDetailListView.setItemChecked(0, true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the navigation drawer is open, hide action items related to the content view

		boolean viewAction = mDrawerLayout.isDrawerOpen(projectDetailListView);
		menu.findItem(R.id.action).setVisible(!viewAction);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch(item.getItemId()) {
		case R.id.action:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if(list[position].equalsIgnoreCase(Task)){
				selectItem(AppConstant.PROJECT_TASK);
			}else if(list[position].equalsIgnoreCase(Bugs)){
				selectItem(AppConstant.PROJECT_BUG);
			}else if(list[position].equalsIgnoreCase(Milestone)){
				selectItem(AppConstant.PROJECT_MILESTONE);
			}
			setTitle(list[position]);
			projectDetailListView.setItemChecked(position, true);


		}
	}

	private void selectItem(int position) {

		Fragment fragment = null;
		FragmentManager fragmentManager = getFragmentManager();
		if(position == AppConstant.PROJECT_BUG){
			fragment = new ProjectBugFragment();

		}else if(position == AppConstant.PROJECT_MILESTONE){
			fragment = new ProjectMilestoneFragment();

		}else{
			fragment = new ProjectTaskFragment();

		}
		Bundle args = new Bundle();
		args.putString(getResources().getString(R.string.menu_name), projectName);
		args.putString(getResources().getString(R.string.menu_uid), projectUID);
		args.putString("membersRoleUid",memberRoleUid);
		args.putString("moderatorsRoleUid",moderatorRoleUid);
		fragment.setArguments(args);
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer


		mDrawerLayout.closeDrawer(projectDetailListView);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}

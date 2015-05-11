package com.raweng.projectsonthego;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;
import com.raweng.built.utilities.BuiltConstant;
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
    private BuiltApplication builtApplication;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_layout);


		context = MainActivity.this;

        try {
            builtApplication = Built.application(context, "blt3b011c0e38ed1d82");
        } catch (Exception e) {
            e.printStackTrace();
        }

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
		
		MenuItem menuItem  = menu.findItem(R.id.action);
		MenuItem menuItem1  = menu.findItem(R.id.logout);
		
		String userType = AppSettings.getUserType(context);

		if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString())){    
			menuItem.setVisible(true);
			menuItem1.setVisible(true);
			menuItem.setTitle(getString(R.string.create_project));
		}else{
			menuItem1.setVisible(true);
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

		case R.id.logout:

			final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage(getResources().getString(R.string.loading));
			progressDialog.setTitle(getResources().getString(R.string.please_wait));
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);

			progressDialog.show();
			builtApplication.getCurrentUser().logoutInBackground(new BuiltResultCallBack() {

                @Override
                public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {
                    if (builtError == null){
                        Toast.makeText(context, "logout successfully...", Toast.LENGTH_SHORT).show();
                        AppSettings.setIsLoggedIn(false, context);
                        finish();
                    }else {
                        Toast.makeText(context, "Error :" + builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });

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

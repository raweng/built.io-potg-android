package com.raweng.built.userInterface;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Simple file manager.
 * 
 * @author raw engineering, Inc
 *
 */
public class UIAndroidExplorerScreen extends ListActivity {

	private final String TAG = "UIAndroidExplorerScreen";

	// Stores names of traversed directories
	private ArrayList<String> directoryName = new ArrayList<String>();

	// Check if the first level of the directory structure is the one showing
	private Boolean firstLevel = true;
	private Item[] fileList;
	private File path = new File(Environment.getExternalStorageDirectory() + "");
	private String chosenFile;
	private ListAdapter adapter;



	public UIAndroidExplorerScreen(){
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.builtio_list_choose_file_name);
		loadFileList();
	}

	private class Item {
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		}
	}

	private void loadFileList() {
		try {
			path.mkdirs();

			// Checks whether path exists
			if (path.exists()) {
				FilenameFilter filter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						File sel = new File(dir, filename);
						// Filters based on whether the file is hidden or not
						return (sel.isFile() || sel.isDirectory()) && !sel.isHidden();

					}
				};

				String[] fList = path.list(filter);
				fileList = new Item[fList.length];
				for (int i = 0; i < fList.length; i++) {
					fileList[i] = new Item(fList[i], R.drawable.ic_builtio_file);
					// Convert into file path
					File sel = new File(path, fList[i]);

					// Set drawables
					if (sel.isDirectory()) {
						fileList[i].icon = R.drawable.ic_builtio_folder;
					} else {
					}
				}

				if (!firstLevel) {
					Item temp[] = new Item[fileList.length + 1];
					for (int i = 0; i < fileList.length; i++) {
						temp[i + 1] = fileList[i];
					}
					temp[0] = new Item("\\", Color.TRANSPARENT);
					fileList = temp;
				}
			} else {
				Log.i(TAG, "path does not exist");
			}

			adapter = new ArrayAdapter<Item>(this, R.layout.builtio_fileselect_row_inpicker, this.fileList) {

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					try {
						TextView rowtext = (TextView) view.findViewById(R.id.rowtext);

						// put the image on the text view
						rowtext.setCompoundDrawablesWithIntrinsicBounds(fileList[position].icon, 0, 0, 0);

						// add margin between image and text (support various
						// screen
						// densities)
						int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
						rowtext.setCompoundDrawablePadding(dp5);
					} catch (Exception error) {
						error.printStackTrace();
					}

					return view;
				}

			};

			setListAdapter(adapter);
		} catch (SecurityException error) {
			
			error.printStackTrace();
			
		} catch (NullPointerException error) {
			
			error.printStackTrace();
			
		} catch (Exception error) {
			
			error.printStackTrace();
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		try {
			final String filePath = path.getAbsolutePath().replace("/storage/sdcard0", "/sdcard");
			chosenFile = fileList[position].file;

			File sel = new File(path + "/" + chosenFile);
			if (sel.isDirectory()) {
				firstLevel = false;
				// Adds chosen directory to list
				directoryName.add(chosenFile);
				fileList = null;
				path = new File(sel + "");

				loadFileList();
			}

			// Checks if 'up' was clicked
			else if (chosenFile.equalsIgnoreCase("\\") && !sel.exists()) {
				// present directory removed from list
				String s = directoryName.remove(directoryName.size() - 1);

				// path modified to exclude present directory
				path = new File(path.toString().substring(0, path.toString().lastIndexOf(s)));
				fileList = null;
				// if there are no more directories in the list, then
				// its the first level

				if (directoryName.isEmpty()) {
					firstLevel = true;
				}
				loadFileList();
			}
			// File picked
			else {
				// Perform action with file picked
				new AlertDialog.Builder(this).setIcon(R.drawable.ic_builtio_attach_icon).setTitle("[" + chosenFile + "]").setPositiveButton("OK", new DialogInterface.OnClickListener() {

					// @Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.putExtra("fileName", chosenFile);
						intent.putExtra("filePath", filePath + "/" + chosenFile);
						setResult(Activity.RESULT_OK, intent);
						finish();
					}
				}).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

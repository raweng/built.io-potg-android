package com.raweng.built;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.raweng.built.utilities.RawAppUtils;

/**
 * BuiltAnalytics class related database functions.
 * 
 * @author raw engineering, Inc
 *
 */
public class AnalyticsAdapter {

	private SQLiteDatabase database;
	private static final String DATABASE_NAME 	= "built.io";
	private static final int DATABASE_VERSION 	= 4;

	private static final String TABLE_NAME 		= "AnalyticsEvent";
	private static final String KEY_EVENTID 	= "eventId";
	private static final String KEY_DATA 		= "JsonData";
	private static final String KEY_HEADER		= "header";
	
	private AnalyticsDbHelper databaseHelper = null;

	private static final String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_NAME  
	+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
	+ KEY_EVENTID + " STRING NOT NULL, " 
	+ KEY_DATA + " STRING NOT NULL, "
	+ KEY_HEADER + " STRING NOT NULL" +
	");";

	/**
	 * constructor.
	 * 
	 * @param context
	 * 					application context.
	 */
	protected AnalyticsAdapter(Context context){
		if(this.databaseHelper == null){
			this.databaseHelper = new AnalyticsDbHelper(context);
		}
	}

	/**
	 * To open database connection.
	 */
	private void open(){
		this.database = databaseHelper.getWritableDatabase();
	}

	/**
	 * To close connection.
	 */
	private void close() {
		this.databaseHelper.close();
	}

	/**
	 * To add event information in database.
	 * 
	 * @param eventId
	 * 					event id.
	 * @param json
	 * 					event related data in JSON format.
	 */
	protected void addJSON(String eventId, JSONObject json, JSONObject headerJson){
		try{
			open();
			ContentValues cv = new ContentValues();

			cv.put(KEY_EVENTID, eventId);
			cv.put(KEY_DATA, json.toString());
			cv.put(KEY_HEADER, headerJson.toString());

			this.database.insert(TABLE_NAME, null, cv);
			close();
		} catch(Exception e){
			RawAppUtils.showLog("AnalyticsAdapter", "------addJSON-catch|" + e);
		}
	}

	/**
	 * return saved events information .
	 * 
	 * @return
	 * 			all events information in JSON format.
	 * 			
	 */

	protected JSONObject getJsonObject() {
		try{
			open();
			JSONObject eventsJson = new JSONObject();
			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME , null);
			if(cursor != null){
				while (cursor.moveToNext()) {
					String eventID = cursor.getString(cursor.getColumnIndex(KEY_EVENTID));
					String eventData = cursor.getString(cursor.getColumnIndex(KEY_DATA));
					JSONObject eventJson = new JSONObject(eventData);
					
					if(eventsJson.has(eventID)){
						JSONArray eventValueJson = eventsJson.getJSONArray(eventID);
						eventValueJson.put(eventJson);
					}else{
						JSONArray valueArray = new JSONArray();
						valueArray.put(eventJson);
						eventsJson.put(eventID, valueArray);
					}
					
				}
				cursor.close();
			}
			close();

			return eventsJson;
		}catch (Exception e) {
			RawAppUtils.showLog("AnalyticsAdapter", "------getJsonObject-catch|" + e);
			close();
			return null;
		}
	}

	/**
	 * To delete all events from database.
	 */
	protected void cleanTable() {
		try{
			open();
			database.delete(TABLE_NAME, null, null);
			close();

		}catch(Exception e) {
			close();
			RawAppUtils.showLog("AnalyticsAdapter", "------cleanTable-catch|" + e);
		}
	}


	private class AnalyticsDbHelper extends SQLiteOpenHelper{

		AnalyticsDbHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db){
			db.execSQL(AnalyticsAdapter.CREATE_EVENTS_TABLE);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			db.execSQL("DROP TABLE IF EXISTS " + AnalyticsAdapter.TABLE_NAME);
			db.execSQL(AnalyticsAdapter.CREATE_EVENTS_TABLE);
		}
	}
}

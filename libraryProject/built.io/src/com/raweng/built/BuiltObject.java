package com.raweng.built;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.array;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.built.utilities.RawAppUtils;

/**
 * BuiltObject is used to create, update and delete built object on the built.io server.
 * 
 * @author  raw engineering, Inc
 *
 */
public class BuiltObject{


	private JSONArray geoLocationArray	= null;
	private JSONArray tagsArray	   		= null;
	private JSONObject upsertValueJson 	= null;
	private JSONObject updateValueJson	= null;

	protected String uid                  				= null;
	protected BuiltACL builtACLUserObject 				= null;
	protected String ownerEmailId 						= null;
	protected String ownerUid 							= null;
	protected JSONObject resultJson 					= null;
	protected HashMap<String, Object> owner 			= null;
	protected ArrayList<HashMap<String, Object>> schema = null;



	private HashMap<String, java.lang.Object> keyValue;
	private HashMap<String, java.lang.Object> refKeyValue;
	private String URL;
	protected String classUid;
	private HeaderGroup headerGroup_local;
	private String applicationKey_local;
	private String applicationUid_local;
	private boolean isPublish = true;




	/**
	 * Create a new instance of {@link BuiltObject} class.
	 * 
	 * @param classUid 
	 * 
	 * 				uid of the class that is created using Web UI.
	 */

	public BuiltObject(String classUid){
		this.classUid     = classUid;
		headerGroup_local = new HeaderGroup();
		keyValue 		  = new HashMap<String, Object>();
		tagsArray 		  = new JSONArray();
		updateValueJson   = new JSONObject();
		upsertValueJson   = new JSONObject();

	}

	/**
	 * Sets class uid for this {@link BuiltObject} instance.
	 * 
	 * @param classUid
	 * 					uid of the class that is created using Web UI.
	 * 				
	 */
	public void setClassUid(String classUid) {
		this.classUid     = classUid;
	}

	/**
	 *  Sets the api key and Application uid for {@link BuiltObject} class instance.
	 *   <br>             
	 * Scope is limited to this object only.
	 *  @param apiKey 
	 * 				Application api Key of your application on built.io.
	 * 
	 * @param appUid 
	 *             Application uid of your application on built.io.
	 *  
	 */
	public void setApplication(String apiKey, String appUid) {
		applicationKey_local = apiKey;
		applicationUid_local = appUid;
		setHeader("application_uid", applicationUid_local);
		setHeader("application_api_key", applicationKey_local);
	}

	/**
	 * To set headers for built.io rest calls.
	 * <br>
	 * Scope is limited to this object only. 
	 * @param key 
	 * 				header name.
	 * @param value 	
	 * 				header value against given header name.
	 * 
	 */
	public void setHeader(String key, String value){

		if(key != null && value != null){
			removeHeader(key);
			headerGroup_local.addHeader(new BasicHeader(key, value));
		}
	}

	/**
	 * Remove a header for a given key from headers.
	 * <br>
	 * Scope is limited to this object only.
	 *  
	 * @param key
	 * 			   header key.
	 *  
	 */
	public void removeHeader(String key){
		if(headerGroup_local.containsHeader(key)){
			org.apache.http.Header header =  headerGroup_local.getCondensedHeader(key);
			headerGroup_local.removeHeader(header);
		}
	}

	/**
	 * To set timeless header for a built.io rest call.
	 * To issue a timeless update. The date of creation, update will not change.
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject setTimeless(){

		setHeader("timeless", "true");

		return this;
	}


	/**
	 * Check if the object is new or present on server.
	 * 
	 * @return 
	 * 			true if object is new.
	 */
	public boolean isNew(){
		return (uid == null);
	}


	/**
	 * Create clone of an object.
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject clone() {
		BuiltObject object = new BuiltObject(classUid);
		object.setUid(uid);

		return object;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link HashMap} object with key as given key.
	 * 
	 */

	public Object get(String key){

		try{
			if(resultJson != null){

				if(resultJson.get(key) instanceof JSONObject){

					return createHashResult((JSONObject) resultJson.get(key));

				}else if(resultJson.get(key) instanceof JSONArray){

					return resultJson.get(key);

				}else{

					return resultJson.get(key);
				}
			}else{
				return null;
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------get|" + e);
			return null;
		}
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link String} value of specified key.
	 * 			Returns null if there is no such key or if it is not a {@link String}.
	 * 
	 */
	public String getString(String key){
		Object value = get(key);
		if(value != null){
			if(value instanceof String){
				return (String) value;
			}
		}
		return null;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link Boolean} value of specified key.
	 * 			Returns false if there is no such key or if it is not a {@link Boolean}.
	 * 
	 */
	public Boolean getBoolean(String key){
		Object value = get(key);
		if(value != null){
			if(value instanceof Boolean){
				return (Boolean) value;
			}
		}
		return false;
	}

	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link JSONArray} value of specified key.
	 * 			Returns null if there is no such key or if it is not a {@link JSONArray}.
	 * 
	 */
	public JSONArray getJSONArray(String key){
		Object value = get(key);
		if(value != null){
			if(value instanceof JSONArray){
				return (JSONArray) value;
			}
		}
		return null;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link JSONObject} value of specified key.
	 * 			Returns null if there is no such key or if it is not a {@link JSONObject}.
	 * 
	 */
	public JSONObject getJSONObject(String key){
		Object value = get(key);
		if(value != null){
			if(value instanceof JSONObject){
				return (JSONObject) value;
			}
		}
		return null;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link Number} value of specified key.
	 * 			Returns null if there is no such key or if it is not a {@link Number}.
	 * 
	 */
	public Number getNumber(String key){
		Object value = get(key);
		if(value != null){
			if(value instanceof Number){
				return (Number) value;
			}
		}
		return null;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link int} value of specified key.
	 * 			Returns 0 if there is no such key or if it is not a {@link int}.
	 * 
	 */
	public int getInt(String key){
		Number value = getNumber(key);
		if(value != null){
			return value.intValue();
		}
		return 0;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link Float} value of specified key.
	 * 			Returns 0 if there is no such key or if it is not a {@link Float}.
	 * 
	 */
	public float getFloat(String key){
		Number value = getNumber(key);
		if(value != null){
			return value.floatValue();
		}
		return (float) 0;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link Double} value of specified key.
	 * 			Returns 0 if there is no such key or if it is not a {@link Double}.
	 * 
	 */
	public double getDouble(String key){
		Number value = getNumber(key);
		if(value != null){
			return value.doubleValue();
		}
		return (double) 0;
	}

	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link Long} value of specified key.
	 * 			Returns 0 if there is no such key or if it is not a {@link Long}.
	 * 
	 */
	public long getLong(String key){
		Number value = getNumber(key);
		if(value != null){
			return value.longValue();
		}
		return (long) 0;
	}


	/**
	 * Get ACL of this instance.
	 * 
	 * 
	 * @return 
	 * 			{@link BuiltACL} instance.
	 * 		
	 */
	public BuiltACL getACL(){

		return BuiltACL.setAcl(resultJson.optJSONObject("ACL"));
	}



	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link short} value of specified key.
	 * 			Returns 0 if there is no such key or if it is not a {@link short}.
	 * 
	 */
	public short getShort(String key){
		Number value = getNumber(key);
		if(value != null){
			return value.shortValue();
		}
		return (short) 0;
	}


	/**
	 * Get value for the given key.
	 * 
	 * @param key 
	 * 				key of a field.
	 * 
	 * @return 
	 * 			{@link Calendar} value of specified key.
	 * 			Returns null if there is no such key or if it is not a {@link Calendar}.
	 * 
	 */
	public Calendar getDate(String key){

		try {
			String value = getString(key);
			return BuiltUtil.parseDate(value);
		} catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------getDate|" + e);
		}
		return null;
	}


	/**
	 * Get {@link BuiltObject} creation date.
	 * 
	 * 
	 * @return 
	 * 			{@link Calendar} instance.
	 * 
	 */
	public Calendar getCreateAt(){

		try {
			String value = getString("created_at");
			return BuiltUtil.parseDate(value);
		} catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------getCreateAtDate|" + e);
		}
		return null;
	}


	/**
	 * Get {@link BuiltObject} updation date.
	 * 
	 * 
	 * @return 
	 * 			{@link Calendar} instance.
	 * 
	 */
	public Calendar getUpdateAt(){

		try {
			String value = getString("updated_at");
			return BuiltUtil.parseDate(value);
		} catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------getUpdateAtDate|" + e);
		}
		return null;
	}

	/**
	 * To get this object geo location.
	 * 
	 * @return 
	 * 			{@link BuiltLocation} instance.
	 * 
	 */
	public BuiltLocation getLocation() {
		try {
			Object value = get("__loc");
			if(value instanceof JSONArray){
				BuiltLocation location = new BuiltLocation();
				location.setLocation(((JSONArray) value).getDouble(1), ((JSONArray) value).getDouble(0));
				return location;
			}
		} catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------getLocation|" + e);
		}
		return null;

	}


	/**
	 * Get value for the given reference key.
	 * 
	 * @param key 
	 * 			  key of a reference field.
	 * 
	 * @param classUid
	 * 					 class uid.
	 * 
	 * @return 
	 * 			{@link ArrayList} of {@link BuiltObject} instances.
	 * Also specified classUid value will be set as class uid for all {@link BuiltObject} instance.
	 * 
	 */
	public ArrayList<BuiltObject> getAllObjects(String key, String classUid){
		try{
			if(resultJson != null){

				if(resultJson.get(key) instanceof JSONArray){

					int count = ((JSONArray)resultJson.get(key)).length();
					ArrayList<BuiltObject> builtObjectList = new ArrayList<BuiltObject>();
					for(int i = 0; i < count; i++){

						BuiltObjectModel model  = new BuiltObjectModel(((JSONArray)resultJson.get(key)).getJSONObject(i), null, false, false, true);
						BuiltObject builtObject = new BuiltObject(classUid);
						builtObject.setUid(model.objectUid);
						builtObject.ownerEmailId 		= model.ownerEmailId;
						builtObject.ownerUid     		= model.ownerUid;
						builtObject.owner		 		= model.ownerMap;
						builtObject.resultJson 			= model.jsonObject;
						builtObject.builtACLUserObject 	= model.builtACLInstance;
						builtObject.setTags(model.tags);

						builtObjectList.add(builtObject);
						model = null;
					}

					return builtObjectList;

				}else{
					return null;
				}
			}else{
				return null;
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------get|" + e);
			return null;
		}
	}
	/**
	 * Returns owner information if the object has owner.
	 * 
	 * @return {@link HashMap} object.
	 */
	public HashMap<String, Object> getOwner() {
		return owner;
	}

	/**
	 * To get uid of this {@link BuiltObject} instance&#39;s owner.
	 * 
	 * @return 
	 * 			owner uid.
	 */
	public String ownerUid(){
		return ownerUid;
	}

	/**
	 * To get email id of owner.
	 * 
	 * @return 
	 * 			owner e-mail ID.
	 */
	public String ownerEmail() {
		return ownerEmailId;
	}

	/**
	 * Check whether this object has owner or not. 
	 * 
	 * @return 
	 * 			returns true if this object has owner else returns false.
	 */
	public boolean hasOwner() {

		return (ownerUid != null);
	}



	/**
	 * To set tags for this object.
	 * 
	 * @param tags
	 * 				array of tag. 
	 * 
	 * @return  
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject setTags(String[] tags){
		if(tags != null){
			for(String tag : tags){
				tagsArray.put(tag);
			}
		}
		return this;

	}

	/**
	 * To set geo location for this object.
	 * 
	 * @param location
	 *               {@link BuiltLocation} instance 
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject setLocation(BuiltLocation location) {

		if(location != null && location.getLatitude() != null && location.getLongitude() != null){

			try{
				geoLocationArray = new JSONArray();
				geoLocationArray.put(location.getLongitude());
				geoLocationArray.put(location.getLatitude());
			}catch (Exception e) {
				RawAppUtils.showLog("BuiltObject", "-----------------setLocation|"+e);
			}
		}

		return this;

	}

	/**
	 * Returns tags of this object.
	 * 
	 * 
	 * @return  
	 * 			{@link array} of tags.
	 */
	public String[] getTags(){
		if(tagsArray != null){

			String [] tags = new String[tagsArray.length()];
			for(int i = 0; i < tags.length; i++){
				tags[i] = tagsArray.optString(i);
			}

			return tags;

		}else{
			return null;
		}
	}

	/**
	 * To set object/ uid for a specified reference field.
	 * 
	 * @param key
	 * 				field.
	 * 
	 * @param value
	 * 				value can be {@link BuiltObject} instance or array of field uids of a referenced object 
	 * 				or uid of a referenced object.
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject setReference(String key, Object value){
		if(value instanceof BuiltObject){
			setRef(key, ((BuiltObject)value).keyValue);

		}else if(value instanceof String){
			if(refKeyValue == null){
				refKeyValue = new HashMap<String, java.lang.Object>();
			}
			refKeyValue.put(key, value);

		}else if(value instanceof Object[]){

			if(((Object[])value).length > 0){

				int count = ((Object[])value).length;
				JSONArray array = new JSONArray();


				for(int i = 0; i < count; i++){
					if(((Object[])value)[i] instanceof BuiltObject){

						array.put(((BuiltObject)((Object[])value)[i]).keyValue);

					}else{

						array.put(((Object[])value)[i]);
					}

				}
				if(refKeyValue == null){
					refKeyValue = new HashMap<String, java.lang.Object>();
				}
				refKeyValue.put(key, array);
			}
		}
		return this;
	}


	/**
	 * To update/insert value.
	 * 
	 * @param value 
	 * 				{@link HashMap} object containing key value pair on which searched is performed
	 * 				 and if an existing object is found containing those pair(s), 
	 * 				it is updated else a new object is created(when the object is saved).
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject upsert(HashMap<String, Object> value){
		try{
			for(Entry<String, java.lang.Object> entry : value.entrySet()){
				upsertValueJson.put(entry.getKey(), entry.getValue());
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------upsert|"+e);
		}

		return this;
	}

	/**
	 * To add value at specific index to a field that allows multiple values.
	 * 
	 * @param key
	 * 			   field uid.
	 *  	
	 * @param value
	 * 				value to enter in field.
	 * 
	 * @param index	
	 * 				index at which value is to be added.  
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject pushValue(String key, Object value, int index) {
		try{
			JSONObject dataJson      = new JSONObject();
			JSONObject pushValueJson = new JSONObject();

			if(value instanceof List<?>){
				JSONArray valueJsonArray = new JSONArray();
				for(Object obj: (List<?>)value){
					valueJsonArray.put(obj);
				}
				dataJson.put("data", valueJsonArray);

			}else{
				dataJson.put("data", value);
			}
			if(index != -1 ){
				pushValueJson.put("index", index);
			}
			pushValueJson.put("PUSH", dataJson);
			updateValueJson.put(key, pushValueJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------pushValue|"+e);
		}
		return this;
	}

	/**
	 * To add value to a field that allows multiple values.
	 * 
	 * @param key
	 * 			   field uid.
	 *  	
	 * @param value
	 * 				value to enter in field.
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject pushValue(String key, Object value) {
		pushValue(key, value, -1);
		return this;
	}

	/**
	 * To remove value at specific index from a field that allows multiple values.
	 * 
	 * @param key
	 *         field uid.
	 *         
	 * @param index	
	 * 				index at which value is to be removed.  
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject pullValueAtIndex(String key, int index){
		try{
			JSONObject pullJson  = new JSONObject();
			JSONObject indexJson = new JSONObject();

			indexJson.put("index", index);
			pullJson.put("PULL", indexJson);

			updateValueJson.put(key, pullJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------pullvalueAtIndex|"+e);
		}
		return this;

	}

	/**
	 * To remove value from a field that allows multiple values.
	 * 
	 * @param key
	 * 			   field uid.
	 *  	
	 * @param value
	 * 				field value.
	 * 				Value can be a list of objects or a string value. 
	 * 
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject pullValue(String key, Object value){
		try{
			JSONObject dataJson = new JSONObject();
			JSONObject pullJson = new JSONObject();
			JSONArray dataArray = new JSONArray();

			if(value instanceof List<?>){
				JSONArray valueJsonArray = new JSONArray();
				for(Object obj: (List<?>)value){
					valueJsonArray.put(obj);
				}
				dataJson.put("data", valueJsonArray);

			}else{
				dataArray.put(value);
				dataJson.put("data", dataArray);
			}
			pullJson.put("PULL", dataJson);

			updateValueJson.put(key, pullJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------pullValue|"+e);
		}
		return this;
	}

	/**
	 * To update a value in a field that allows multiple values.
	 * 
	 * @param key
	 * 			   field uid.
	 *  	
	 * @param value
	 * 				new value to be updated.
	 * 
	 * @param index	
	 * 				index at which the value is to be updated.  
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * 
	 */
	public BuiltObject updateValue(String key, String value, int index) {
		try{
			JSONObject dataJson        = new JSONObject();
			JSONObject updateValueJson = new JSONObject();

			dataJson.put("data", value);
			dataJson.put("index", index);

			updateValueJson.put("UPDATE", dataJson);
			this.updateValueJson.put(key, updateValueJson);

		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-----------------updateValue|"+e);
		}
		return this;
	}

	/**
	 * To set reference to this object.
	 * set object for reference field where the value {@link HashMap} matches the objects of the referred class.
	 * 
	 * @param referenceKey
	 * 						reference field uid.
	 * 
	 * @param value 
	 * 				{@link HashMap} object.
	 * 				Contains referred class field uid and its respective value.
	 * 				reference set on object where all key value pairs of {@link HashMap} matches.
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject setReferenceWhere(String referenceKey, HashMap<String, Object> value) {
		try{
			JSONObject valueJson = new JSONObject();
			JSONObject refJson   = new JSONObject();

			for(Entry<String, Object> entry : value.entrySet()){
				valueJson.put(entry.getKey(), entry.getValue());
			}

			refJson.put("WHERE", valueJson);
			updateValueJson.put(referenceKey, refJson);

		}catch (Exception e) {
			RawAppUtils.showLog("BuiltObject", "-setReferenceWhere-catch|"+e);
		}
		return this;
	}

	/**
	 * Check if key exists in an object.
	 * 
	 * @param key 
	 * 			 the key(field uid in your class).
	 * 
	 * @return true if key exist else return false
	 */
	public boolean has(String key) {

		return resultJson.has(key);
	}

	/**
	 * Set object uid.
	 * 
	 * @param uid given uid will be set as a object uid.
	 */
	public void setUid(String uid){
		this.uid = uid;

	}

	/**
	 * Returns this {@link BuiltObject} instance&#39;s uid.
	 * 
	 * @return
	 * 			Object Uid.	
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Returns class schema of which this {@link BuiltObject} instance belongs to or null if request to fetch class schema from built.io server is not executed successfully.
	 */
	public ArrayList<HashMap<String, Object>> getSchema() {
		return schema;
	}

	/**
	 * Saves the {@link BuiltObject} to built.io servers.
	 * 
	 * @param callback  
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 */
	public void save(BuiltResultCallBack callback){
		try{
			isPublish = true;
			if(builtACLUserObject != null){
				JSONObject ACLObj = new JSONObject();
				if(builtACLUserObject.othersJsonObject.length() > 0){
					ACLObj.put("others",builtACLUserObject.othersJsonObject);
				}

				if(builtACLUserObject.userArray.length() > 0){
					ACLObj.put("users", builtACLUserObject.userArray);
				}

				if(builtACLUserObject.roleArray.length() > 0){
					ACLObj.put("roles", builtACLUserObject.roleArray);
				}
				keyValue.put("ACL", ACLObj);
			}

			if(uid == null){
				createObjectForClassUid(classUid, keyValue, tagsArray, false, callback);
			}else{
				updateObjectForClassUid(classUid, uid, keyValue, tagsArray, false, callback);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	/**
	 * Saves the {@link BuiltObject} to built.io servers.
	 * 
	 * @param callback
	 * 				   {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * <p>
	 * <b>Note: </b> if connection available then it executes network call else sdk will notify UI thread on onAlways() method of callback object and executes network call when connection is available in future. 
	 */
	public void saveEventually(BuiltResultCallBack callback){
		try{
			isPublish = true;
			if(builtACLUserObject != null){
				JSONObject ACLObj = new JSONObject();

				if(builtACLUserObject.othersJsonObject.length() > 0){
					ACLObj.put("others",builtACLUserObject.othersJsonObject);
				}

				if(builtACLUserObject.userArray.length() > 0){
					ACLObj.put("users", builtACLUserObject.userArray);
				}

				if(builtACLUserObject.roleArray.length() > 0){
					ACLObj.put("roles", builtACLUserObject.roleArray);
				}

				keyValue.put("ACL", ACLObj);
			}

			if(uid == null){

				createObjectForClassUid(classUid, keyValue, tagsArray, true, callback);

			}else{

				updateObjectForClassUid(classUid, uid, keyValue, tagsArray, true, callback);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}


	/**
	 * Save object data to built.io server with &#34;unpublished&#34; status 
	 * so that it is not visible unless explicitly called for. see {@link com.raweng.built.BuiltQuery #includeDrafts()}.
	 * 
	 * @param callback 
	 * 					 {@link BuildFileResultCallback} object to notify the application when the request has completed.
	 */
	public void saveAsDraft(BuiltResultCallBack callback){
		try{

			isPublish = false;
			if(builtACLUserObject != null){
				JSONObject ACLObj = new JSONObject();
				if(builtACLUserObject.othersJsonObject.length() > 0){
					ACLObj.put("others",builtACLUserObject.othersJsonObject);
				}

				if(builtACLUserObject.userArray.length() > 0){
					ACLObj.put("users", builtACLUserObject.userArray);
				}

				if(builtACLUserObject.roleArray.length() > 0){
					ACLObj.put("roles", builtACLUserObject.roleArray);
				}
				keyValue.put("ACL", ACLObj);
			}

			if(uid == null){

				createObjectForClassUid(classUid, keyValue, tagsArray, false, callback);
			}else{
				updateObjectForClassUid(classUid, uid, keyValue,tagsArray, false, callback);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}


	/**
	 * Save object data to built.io server with &#34;unpublished&#34; status,
	 * so that it is not visible unless explicitly called for. see {@link com.raweng.built.BuiltQuery #includeDrafts()}.
	 * 
	 * @param callback	
	 * 					 {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 			
	 *  <p>
	 * <b>NOTE:</b> if connection available then it executes network call else sdk will notify UI thread on onAlways() method of callback object and executes network call when connection is available in future. 
	 */
	public void saveAsDraftEventually(BuiltResultCallBack callback){

		try{
			isPublish = false;
			if(builtACLUserObject != null){
				JSONObject ACLObj = new JSONObject();

				if(builtACLUserObject.othersJsonObject.length() > 0){
					ACLObj.put("others",builtACLUserObject.othersJsonObject);
				}

				if(builtACLUserObject.userArray.length() > 0){
					ACLObj.put("users", builtACLUserObject.userArray);
				}

				if(builtACLUserObject.roleArray.length() > 0){
					ACLObj.put("roles", builtACLUserObject.roleArray);
				}

				keyValue.put("ACL", ACLObj);
			}

			if(uid == null){

				createObjectForClassUid(classUid, keyValue, tagsArray, true, callback);
			}else{
				updateObjectForClassUid(classUid, uid, keyValue, tagsArray, true, callback);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	/**
	 * 
	 * Returns JSON representation of Object data.
	 */
	public JSONObject toJSON(){
		return resultJson ;
	}

	/**
	 * To set field value to a specified field.
	 * 
	 * @param key 
	 * 				field uid.
	 * 
	 * @param value 
	 * 				value of a specified field.
	 * 
	 * <br>
	 * <b>Note</b> :-To add values to a <b>group field </b>provide list of {@link HashMap} objects. 
	 * 	To add values to a <b>multiple field </b>provide array of values. 
	 * 
	 * 
	 */
	public void set(String key, Object value){
		if(keyValue == null){
			keyValue = new HashMap<String, java.lang.Object>();
		}
		if(value instanceof Calendar){
			Calendar calObject = new GregorianCalendar(((Calendar)value).getTimeZone());
			calObject.setTimeInMillis(((Calendar)value).getTimeInMillis() +
					((Calendar)value).getTimeZone().getOffset(((Calendar)value).getTimeInMillis()) -
					TimeZone.getDefault().getOffset(((Calendar)value).getTimeInMillis()));
			calObject.setTimeZone(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String utcTime = sdf.format(calObject.getTime());
			keyValue.put(key, utcTime);

		}else if(value instanceof List<?>){
			int count = ((List<?>)value).size();
			JSONArray array = new JSONArray();
			for(int i = 0; i< count; i++){

				if(((List<?>)value).get(i) instanceof HashMap<?, ?>){

					JSONObject object = new JSONObject();

					for (Entry<?, ?> e : ((HashMap<?, ?>)((List<?>)value).get(i)).entrySet()) {
						try{
							object.put((String) e.getKey(), e.getValue());
						}catch (Exception exception) {
							// TODO: handle exception
						}
					}
					array.put(object);
				}
			}
			keyValue.put(key, array);

		}else if(value instanceof Object[]){

			if(((Object[])value).length > 0){

				int count = ((Object[])value).length;
				JSONArray array = new JSONArray();

				for(int i = 0; i < count; i++){
					array.put(((Object[])value)[i]);
				}

				keyValue.put(key, array);
			}
		}else{
			keyValue.put(key, value);
		}

	}

	/**
	 * To set field value.
	 *  
	 * @param keyValueObject
	 * 							{@link HashMap} object.
	 * 
	 */
	public void set(HashMap<String, Object> keyValueObject){
		if(keyValue == null){
			keyValue = new HashMap<String, Object>();
		}else{
			if(keyValueObject != null){
				for (Entry<String, java.lang.Object> entry : keyValueObject.entrySet()){
					if(entry.getValue() instanceof Calendar){
						Calendar calObject = new GregorianCalendar(((Calendar)entry.getValue()).getTimeZone());
						calObject.setTimeInMillis(((Calendar)entry.getValue()).getTimeInMillis() +
								((Calendar)entry.getValue()).getTimeZone().getOffset(((Calendar)entry.getValue()).getTimeInMillis()) -
								TimeZone.getDefault().getOffset(((Calendar)entry.getValue()).getTimeInMillis()));
						calObject.setTimeZone(TimeZone.getTimeZone("GMT"));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
						String utcTime = sdf.format(calObject.getTime());
						keyValue.put(entry.getKey(), utcTime);
					}else{
						keyValue.put(entry.getKey(), (String) entry.getValue());
					}
				}
			}
		}
	}

	/**
	 * Fetches an object provided object uid and class uid.
	 * 
	 * @param callback  
	 * 					{@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return  
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject fetch(BuiltResultCallBack callback){

		getObjectForClassUid(classUid, uid, callback);
		return this;
	}

	/**
	 * Delete the BuiltObject with specified object uid from built.io server.
	 * 
	 * 
	 * @param callback 
	 * 				  {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject destroy(BuiltResultCallBack callback){

		deleteObjectForClassUid(classUid, uid, false, callback);
		return this;

	}

	/**
	 * Delete the BuiltObject with specified object uid from built.io server.
	 * 
	 * 
	 * @param callback 
	 * 				  {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>NOTE:</b> if connection available then it executes network call else sdk will notify UI thread on onAlways() method of callback object and executes network call when connection is available in future. 
	 */
	public BuiltObject destroyEventually(BuiltResultCallBack callback){

		deleteObjectForClassUid(classUid, uid, true, callback);
		return this;

	}

	/**
	 * Fetches and returns class&#39;s schema.
	 * 
	 * @param callback 
	 * 		  {@link BuiltResultCallBack} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 			{@link BuiltObject} object, so you can chain this call.
	 *<p>
	 *You can get schema by accessing {@link #getSchema()} 
	 */
	public BuiltObject fetchSchema(BuiltResultCallBack callback){

		getClassForUid(classUid, callback);
		return this;	
	}

	/**
	 * To set ACL on this object.
	 * 
	 * @param builtACL
	 * 					object of {@linkplain BuiltACL} class.
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject setACL(BuiltACL builtACLInstance) {
		builtACLUserObject = builtACLInstance;
		return this;

	}

	/**
	 * Include custom filter in key value string.
	 * 
	 * @param key
	 * 				Filter name to include.
	 * @param value
	 * 				Filter value to include.
	 * @return
	 * 				{@link BuiltObject} object, so you can chain this call.
	 */
	public BuiltObject includeFilter(String key, String value){
		try{
			if(key != null && value != null){
				updateValueJson.put(key, value);
			}
		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--includeFilter-catch|"+e);
		}
		return this;
	}

	/**
	 * Increment the given key by one.
	 * 
	 * @param key
	 * 				the key to increment.
	 *  
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * <p>
	 * <b>Note:</b> Applicable only for number field.
	 */
	public BuiltObject increment(String key){
		try{
			JSONObject incrementValueJson = new JSONObject();
			incrementValueJson.put("ADD", 1);
			updateValueJson.put(key, incrementValueJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--incrementKey-catch|"+e);
		}
		return this;
	}

	/**
	 * Increment the given key by given number.
	 * 
	 * @param key
	 * 				the key to increment.
	 *  
	 * @param value
	 * 				 the number by which to increment.					
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note:</b> Applicable only for number field.
	 */
	public BuiltObject increment(String key, int value){
		try{
			JSONObject incrementValueJson = new JSONObject();
			incrementValueJson.put("ADD", value);
			updateValueJson.put(key, incrementValueJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--incrementByAmount-catch|"+e);
		}
		return this;
	}

	/**
	 * To decrement the given key by one.
	 * 
	 * @param key
	 * 				the key to decrement.
	 *  
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note:</b> Applicable only for number field.
	 */
	public BuiltObject decrement(String key){
		try{
			JSONObject decrementValueJson = new JSONObject();
			decrementValueJson.put("SUB", 1);
			updateValueJson.put(key, decrementValueJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--decrement-catch|"+e);
		}
		return this;
	}

	/**
	 * To decrement the given key by  by given number.
	 * 
	 * @param key
	 * 				the key to decrement.
	 *  
	 * @param value
	 * 				 the number by which to decrement.					
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note:</b> Applicable only for number field.
	 */
	public BuiltObject decrement(String key, int value){
		try{
			JSONObject decrementValueJson = new JSONObject();
			decrementValueJson.put("SUB", value);
			updateValueJson.put(key, decrementValueJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--decrementByAmount-catch|"+e);
		}
		return this;
	}

	/**
	 * To multiply the given key by a given number.
	 * 
	 * @param key
	 * 				the key to be multiplied by.
	 *  
	 * @param value
	 * 				 the number by which to multiply.					
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note:</b> Applicable only for number field.
	 */
	public BuiltObject multiply(String key, int value){
		try{
			JSONObject multiplyValueJson = new JSONObject();
			multiplyValueJson.put("MUL", value);
			updateValueJson.put(key, multiplyValueJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--multiply-catch|"+e);
		}
		return this;
	}

	/**
	 * To divide the given key by a given number.
	 * 
	 * @param key
	 * 				the key to be divided by.
	 *  
	 * @param value
	 * 				 the number by which to divide.					
	 * 
	 * @return
	 * 			{@link BuiltObject} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note:</b> Applicable only for number field.
	 */
	public BuiltObject divide(String key, int value){
		try{
			JSONObject multiplyValueJson = new JSONObject();
			multiplyValueJson.put("DIV", value);
			updateValueJson.put(key, multiplyValueJson);
		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--divide-catch|" + e);
		}
		return this;
	}


	/**
	 * To cancel all {@link BuiltObject} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTOBJECT.toString());
	}

	/**
	 * To update/insert values of referenced object while creating new object.
	 * 
	 * @param key
	 * 				field uid.
	 * 
	 * @param referenceKey
	 * 						reference field uid on which insert/update is to be performed.
	 * 
	 * 
	 * @param referenceValue
	 * 						reference field value.
	 * 						
	 * 
	 * @param newFieldValue
	 * 						{@link HashMap} object containing  key value pairs for the fields inside the referenced object.
	 * 
	 * @return
	 * 					{@link BuiltObject} object, so you can chain this call.
	 * 
	 * 
	 */
	public BuiltObject upsertForReference(String key, String referenceKey, Object referenceValue, HashMap<String, Object> newFieldValue){
		try{

			JSONObject refJson = new JSONObject();
			JSONObject newValueJson = new JSONObject();
			JSONArray valueArray = new JSONArray();

			if(((referenceKey != null && referenceValue != null)) && ((newFieldValue != null && newFieldValue.size() > 0))){

				refJson.put(referenceKey, referenceValue);

				for(Entry<String, Object> entry : newFieldValue.entrySet()){
					newValueJson.put(entry.getKey(), entry.getValue());
				}

				newValueJson.put("UPSERT", refJson);
				valueArray.put(newValueJson);
				updateValueJson.put(key, valueArray);
			}

		}catch (Exception e) {
			RawAppUtils.showLog("BUiltObject", "--upsertForReference-catch|" + e);
		}

		return this;
	}


	/*****************************************************************************************************************************************************************************
	 * 
	 *
	 * 
	 ****************************************************************************************/

	private void setRef(String key, Object value){
		if(refKeyValue == null){
			refKeyValue = new HashMap<String, java.lang.Object>();
		}
		if(value instanceof Calendar){
			Calendar calObject = new GregorianCalendar(((Calendar)value).getTimeZone());
			calObject.setTimeInMillis(((Calendar)value).getTimeInMillis() +
					((Calendar)value).getTimeZone().getOffset(((Calendar)value).getTimeInMillis()) -
					TimeZone.getDefault().getOffset(((Calendar)value).getTimeInMillis()));
			calObject.setTimeZone(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String utcTime = sdf.format(calObject.getTime());
			refKeyValue.put(key, utcTime);
		}else{
			refKeyValue.put(key, value);
		}

	}

	private void getClassForUid(String classUid, BuiltResultCallBack callback){ 
		try{
			if(classUid != null){

				URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/" + classUid;

				JSONObject mainJson = new JSONObject();
				mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());

				new BuiltCallBackgroundTask(this, BuiltControllers.GETCLASS, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTOBJECT.toString(), false, callback);

			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_ClassUID);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void deleteObjectForClassUid(String classUid, String objectUid, boolean isOfflineCall, BuiltResultCallBack callback){
		try{
			if(classUid != null){
				if(objectUid != null){
					URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/" + classUid + "/objects/" + objectUid;

					JSONObject mainJson = new JSONObject();
					mainJson.put("_method", BuiltAppConstants.RequestMethod.DELETE.toString());

					new BuiltCallBackgroundTask(this, BuiltControllers.DELETEOBJECT, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTOBJECT.toString(), isOfflineCall, callback);

				}else{
					throwExeception(callback, BuiltAppConstants.ErrorMessage_ObjectUID);
				}
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_ClassUID);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}


	private void getObjectForClassUid(String classUid, String objectUid, BuiltResultCallBack callback){
		try{
			if(classUid != null){
				if(objectUid != null){
					URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/" + classUid + "/objects/" + objectUid;

					JSONObject mainJson = new JSONObject();
					mainJson.put("_method", BuiltAppConstants.RequestMethod.GET.toString());

					new BuiltCallBackgroundTask(this, BuiltControllers.GETOBJECT, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTOBJECT.toString(), false, callback);

				}else{
					throwExeception(callback, BuiltAppConstants.ErrorMessage_ObjectUID);
				}
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_ClassUID);
			}
		}catch(Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void createObjectForClassUid(String classUid, HashMap<String,Object> valuesAndFields, JSONArray tagList, boolean isOfflineCall, BuiltResultCallBack callback){

		try{
			if(classUid != null){

				JSONObject mainJson = new JSONObject();
				JSONObject valueJson = updateValueJson;

				String tag = null;

				if(tagList != null ){
					int count = tagList.length();
					for(int i = 0; i < count; i++){
						if(i == 0){
							tag = tagList.getString(i);
						}else{
							tag = tag + "," + tagList.getString(i);
						}			
					}
					valueJson.put("tags", tag);
				}

				if(valuesAndFields != null && valuesAndFields.size() > 0){
					for (Entry<String, java.lang.Object> e : valuesAndFields.entrySet()) {
						valueJson.put(e.getKey(), e.getValue());
					}
				}
				if(refKeyValue != null && refKeyValue.size() > 0){

					for (Entry<String, java.lang.Object> e : refKeyValue.entrySet()) {
						JSONArray refArray = new JSONArray();
						if(e.getValue() instanceof HashMap<?,?>){
							JSONObject refJson = new JSONObject();
							if( ((HashMap<?,?>)e.getValue()).size() > 0){
								for (Entry<String, Object> value : ((HashMap<String,Object>)e.getValue()).entrySet()) {

									refJson.put(value.getKey(), value.getValue());
								}
							}
							refArray.put(refJson);
							valueJson.put(e.getKey(), refArray);
						}else if(e.getValue() instanceof JSONArray){
							valueJson.put(e.getKey(), e.getValue());

						}else{
							refArray.put(e.getValue());
							valueJson.put(e.getKey(), refArray);
						}
					}
				}


				if(upsertValueJson != null && upsertValueJson.length() > 0){ 
					mainJson.put("UPSERT", upsertValueJson);
				}


				if(geoLocationArray != null && geoLocationArray.length() > 0){
					valueJson.put("__loc", geoLocationArray);
				}

				valueJson.put("published", isPublish);
				mainJson.put("object", valueJson);

				URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/" + classUid + "/objects";

				new BuiltCallBackgroundTask(this, BuiltControllers.CREATEOBJECT, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTOBJECT.toString(), isOfflineCall, callback);

				setHeader("timeless", "false");
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_ClassUID);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void updateObjectForClassUid(String classUid, String objectUid, HashMap<String,Object> valuesAndFields, JSONArray tagList, boolean isOfflineCall, BuiltResultCallBack callback ){
		try{
			if(classUid != null){
				if(objectUid != null){
					JSONObject mainJson = new JSONObject();

					if(valuesAndFields != null && valuesAndFields.size() > 0){
						for (Entry<String, java.lang.Object> e : valuesAndFields.entrySet()) {
							updateValueJson.put( e.getKey(), e.getValue());
						} 
					}
					updateValueJson.put("published", isPublish);

					String tag = null;

					if(tagList != null ){
						int count = tagList.length();
						for(int i = 0; i < count; i++){
							if(i == 0){
								tag = tagList.getString(i);
							}else{
								tag = tag + "," + tagList.getString(i);
							}			
						}
						updateValueJson.put("tags", tag);
					}

					if(refKeyValue != null && refKeyValue.size() > 0){


						for (Entry<String, Object> e : refKeyValue.entrySet()) {
							JSONArray refArray = new JSONArray();
							if(e.getValue() instanceof HashMap<?,?>){

								JSONObject refJson = new JSONObject();

								if(((HashMap<?,?>)e.getValue()).size() > 0){
									for (Entry<String, Object> value : ((HashMap<String,Object>)e.getValue()).entrySet()) {

										refJson.put(value.getKey(), value.getValue());
									}
								}
								refArray.put(refJson);
								updateValueJson.put(e.getKey(), refArray);

							}else if(e.getValue() instanceof JSONArray){
								updateValueJson.put(e.getKey(), e.getValue());

							}else{
								refArray.put(e.getValue());
								updateValueJson.put(e.getKey(), refArray);
							}

						}
					}

					if(upsertValueJson != null && upsertValueJson.length() > 0){ 
						mainJson.put("UPSERT", upsertValueJson);
					}


					if(geoLocationArray != null && geoLocationArray.length() > 0){
						updateValueJson.put("__loc", geoLocationArray);
					}


					mainJson.put("object", updateValueJson);
					mainJson.put("_method", BuiltAppConstants.RequestMethod.PUT.toString());

					URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/" + classUid + "/objects/" + objectUid;

					new BuiltCallBackgroundTask(this, BuiltControllers.UPDATEOBJECT, URL, getHeaders(headerGroup_local).getAllHeaders(), mainJson, null, callController.BUILTOBJECT.toString(), isOfflineCall, callback);

					setHeader("timeless", "false");
				}else{
					throwExeception(callback, BuiltAppConstants.ErrorMessage_ObjectUID);
				}
			}else{
				throwExeception(callback, BuiltAppConstants.ErrorMessage_ClassUID);
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
	}

	private void throwExeception(BuiltResultCallBack callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.errorMessage(errorMessage);
		if(callback != null){
			callback.onRequestFail(error);
		}
	}

	private HeaderGroup getHeaders(HeaderGroup localHeaders){
		HeaderGroup mainHeaderGroup = new HeaderGroup();
		HeaderGroup endHeaderGroup  = new HeaderGroup();

		mainHeaderGroup = Built.headerGroup;

		if(localHeaders != null){
			if(mainHeaderGroup != null && mainHeaderGroup.getAllHeaders().length > 0){
				int countMainHeader = mainHeaderGroup.getAllHeaders().length;
				for(int i = 0; i < countMainHeader; i++){

					if(mainHeaderGroup.getAllHeaders()[i].getName().equalsIgnoreCase("authtoken")){
						if(localHeaders.containsHeader("application_uid") && localHeaders.containsHeader("application_api_key")){

						}else{
							endHeaderGroup.addHeader(mainHeaderGroup.getAllHeaders()[i]);
						}
					}else{
						endHeaderGroup.addHeader(mainHeaderGroup.getAllHeaders()[i]);
					}
				}
			}
			int countLocalHeader = localHeaders.getAllHeaders().length;
			for(int i = 0; i < countLocalHeader; i++){

				if(endHeaderGroup.containsHeader(localHeaders.getAllHeaders()[i].getName())){

					org.apache.http.Header header = headerGroup_local.getCondensedHeader(localHeaders.getAllHeaders()[i].getName());
					endHeaderGroup.removeHeader(header);
				}

				endHeaderGroup.addHeader(localHeaders.getAllHeaders()[i]);
			}
			return endHeaderGroup;
		}else{
			return mainHeaderGroup;
		}
	}

	private HashMap< String, Object> createHashResult(JSONObject jsonObj) {
		HashMap< String, Object> valueHash = new HashMap<String, Object>();
		JSONObject valueObject             = new JSONObject();
		valueObject                        = jsonObj;

		Iterator<?> iterator = valueObject.keys();
		while (iterator.hasNext()) {
			String hashKey = (String) iterator.next();
			try {
				Object value = valueObject.get(hashKey);

				if(value instanceof JSONObject){

					valueHash.put(hashKey, createHashResult((JSONObject) value));

				}else if(value instanceof JSONArray){
					HashMap< String, Object> valueArrayHash = new HashMap<String, Object>();
					int count = ((JSONArray)value).length();
					for(int i = 0; i < count; i++){
						valueArrayHash.put(hashKey, createHashResult(((JSONArray)value).optJSONObject(i)));
					}
					valueHash.put(hashKey, valueArrayHash);

				}else{
					valueHash.put(hashKey, value);
				}
			} catch (Exception e) {
				RawAppUtils.showLog("BuiltObject", "-createHashResult-catch|"+e);
			}
		}
		return valueHash;
	}

	// To clear ALL hashmaps and json objects excluding resultJson
	protected void clearJson() {
		if(keyValue != null){
			keyValue.clear();
		}
		if(refKeyValue != null){
			refKeyValue.clear();
		}
		upsertValueJson = new JSONObject();
		updateValueJson = new JSONObject();
	}


	protected void clearAll() {

		headerGroup_local.clear();
		if(keyValue != null){
			keyValue.clear();
		}

		if(refKeyValue != null){
			refKeyValue.clear();
		}
		tagsArray 		  = new JSONArray();
		updateValueJson   = new JSONObject();
		upsertValueJson   = new JSONObject();
		geoLocationArray  = new JSONArray();

		this.classUid 		 = null;
		resultJson 			 = null;
		uid 				 = null;
		applicationKey_local = null;
		applicationUid_local = null;
		builtACLUserObject	 = null;

		ownerEmailId = null;
		ownerUid 	 = null;

		if(owner != null){
			owner.clear();
		}

		isPublish = true;

		if(schema != null){
			schema.clear();
		}

	}
}
package com.raweng.built;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.array;
import android.R.string;

import com.raweng.built.QueryResult.resultStatus;
import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.callController;
import com.raweng.built.utilities.BuiltControllers;
import com.raweng.built.utilities.RawAppUtils;



/**
 * A class that defines a query that is used to query for {@link BuiltObject} instance.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltQuery implements INotifyClass {

	private QueryResultsCallBack builtQueryResultsCallBack;
	private HeaderGroup headerGroup_local ;
	private String applicationKey_local ;
	private String applicationUid_local ;

	private JSONArray objectUidForExcept 	  = null; // this is used to store objectUids for except query.(except with ref uid.)
	private JSONObject exceptJsonObject 	  = null;
	private JSONArray objectUidForInclude 	  = null; 
	private JSONArray objectUidForOnly 		  = null; // this is used to store objectUids for only query.(only with ref uid.)
	private JSONObject onlyJsonObject 		  = null;




	/////////////////////////
	private JSONObject queryValueJson    = null;
	protected String classUid 		     = null;
	private   JSONObject jsonMain;

	private boolean isJsonProper   = true;
	private String errorFilterName = null;
	private String errorMesage     = null;


	// cache policy 
	private long maxCachetimeForCall      = 0; //local cache time interval
	private long defaultCacheTimeInterval = 0;
	private CachePolicy cachePolicyForCall ;




	/**
	 * Initializes and returns a {@link BuiltQuery} instance for a class.
	 * 
	 * @param classUid
	 * 				a class uid for which Query has to be made.
	 * @return 
	 *          {@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery(String classUid){
		this.classUid  = classUid;
		jsonMain       = new JSONObject();
		queryValueJson = new JSONObject();

		headerGroup_local        = new HeaderGroup();
		defaultCacheTimeInterval = 24 * 60000;
		/*File cacheDir            = new File(BuiltAppConstants.cacheFolderName);
		if(! cacheDir.exists()){
			cacheDir.mkdirs();
		}*/

		cachePolicyForCall = CachePolicy.IGNORE_CACHE;
	}


	/**
	 * Sets the api key and application uid for {@link BuiltQuery} class instance.
	 * <br>             
	 * Scope is limited to this object only.
	 * 
	 * @param apiKey 
	 * 				Application api Key of your application on built.io.
	 * 
	 * @param appUid 
	 *             Application uid of your application on built.io.
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
	 * 
	 * @param key 
	 * 				header name.
	 * @param value 	
	 * 				header value against given header name.
	 * 
	 */

	public void setHeader(String key, String value){

		if(key != null && value != null){
			headerGroup_local.addHeader(new BasicHeader(key,value));
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
	 * Add a constraint to the query that requires a particular key object to be less than the provided object.
	 * 
	 * @param key
	 * 				the key to be constrained.
	 * 
	 * @param value
	 * 				the value that provides an upper bound.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery lessThan(String key, Object value){
		if(key != null && value != null){
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("$lt", value);
				queryValueJson.put(key, jsonObject);
			} catch(Exception e) {
				throwExeceptionWithMessage("lessThan", e);
			}
		}else{
			throwExeception("lessThan");
		}
		return this;
	}

	/**
	 * Add a constraint to the query that requires a particular key object to be less than or equal to the provided object.
	 * 
	 * @param key
	 * 				The key to be constrained
	 * @param value
	 * 				The value that must be equalled.
	 * 
	 * @return
	 * 		   {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery lessThanOrEqualTo(String key, Object value){
		if(key != null && value != null){

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("$lte", value);
				queryValueJson.put(key, jsonObject);

			} catch (Exception e) {
				throwExeceptionWithMessage("lessThanOrEqualTo", e);
			}
		}else{
			throwExeception("lessThanOrEqualTo");
		}
		return this;
	}



	/**
	 * Add a constraint to the query that requires a particular key object to be greater than the provided object.
	 * 
	 * @param key
	 * 		 		The key to be constrained.
	 * 
	 * @param value
	 * 				The value that provides an lower bound.
	 * 
	 * @return 
	 * 		   {@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery greaterThan(String key, Object value){
		if(key != null && value != null){

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("$gt", value);
				queryValueJson.put(key, jsonObject);

			} catch (Exception e) {
				throwExeceptionWithMessage("greaterThan", e);
			}
		}else{
			throwExeception("greaterThan");
		}
		return this;
	}


	/**
	 * Add a constraint to the query that requires a particular key object to be greater than or equal to the provided object.
	 * 
	 * @param key
	 * 		 		The key to be constrained.
	 * 
	 * @param value
	 * 				The value that provides an lower bound.
	 * 
	 * @return 
	 * 		   {@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery greaterThanOrEqualTo(String key, Object value){
		if(key != null && value != null){

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("$gte", value);
				queryValueJson.put(key, jsonObject);

			} catch (Exception e) {
				throwExeceptionWithMessage("greaterThanOrEqualTo", e);
			}
		}else{
			throwExeception("greaterThanOrEqualTo");
		}
		return this;
	}


	/**
	 *Add a constraint to the query that requires a particular key&#39;s object to be not equal to the provided object.
	 * 
	 * @param key
	 * 				The key to be constrained.
	 * 
	 * @param value
	 * 				The object that must not be equaled.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery notEqualTo(String key, Object value){
		if(key != null && value != null){

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("$ne", value);
				queryValueJson.put(key, jsonObject);

			} catch (Exception e) {
				throwExeceptionWithMessage("notEqualTo", e);
			}
		}else{
			throwExeception("notEqualTo");
		}
		return this;
	}

	/**
	 * Add a constraint to the query that requires a particular key&#39;s object to be contained in the provided array.
	 * 
	 * @param key  
	 * 				The key to be constrained.
	 * 
	 * @param values
	 *  			 The possible values for the key&#39;s object.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery containedIn(String key, String[] values){
		try {
			if(key != null && values != null){
				JSONArray valuesArray  = new JSONArray();
				int length = values.length;
				for(int i = 0; i < length; i++){
					valuesArray.put(values[i]);
				}
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("$in", valuesArray);
				queryValueJson.put(key, jsonObject);
			}else{
				throwExeception("containedIn");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("containedIn", e);
		}
		return this;
	}

	/**
	 * Add a constraint to the query that requires a particular key object&#39;s object not be contained in the provided array.
	 * 
	 * @param key
	 * 				The key to be constrained.
	 * 
	 * @param values
	 * 				The list of values the key object should not be.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery notContainedIn(String key, int[] values){
		try {
			if(key != null && values != null){
				JSONArray valuesArray  = new JSONArray();
				int length = values.length;
				for(int i = 0; i < length; i++){
					valuesArray.put(values[i]);
				}
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("$nin", valuesArray);
				queryValueJson.put(key, jsonObject);
			}else{
				throwExeception("notContainedIn");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("notContainedIn", e);
		}
		return this;
	}


	/**
	 * Add a constraint that requires, a specified key exists in response.
	 * 
	 * @param key
	 * 			  The key to be constrained.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call. 
	 */
	public BuiltQuery exists(String key){
		if(key != null){

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(key, true);
				jsonMain.put("exists", jsonObject);

			} catch (Exception e) {
				throwExeceptionWithMessage("existsKey", e);
			}
		}else{
			throwExeception("existsKey");
		}
		return this;
	}


	/**
	 * Add a constraint that requires, a specified key does not exists in response.
	 * 
	 * @param key
	 * 			  The key to be constrained.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call. 
	 */
	public BuiltQuery doesNotExists(String key){
		if(key != null){

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(key, false);
				jsonMain.put("exists", jsonObject);

			} catch (Exception e) {
				throwExeceptionWithMessage("existsKey", e);
			}
		}else{
			throwExeception("existsKey");
		}
		return this;
	}


	/**
	 *
	 * Add a regular expression constraint for finding string values that match the provided regular expression. 
	 * This may be slow for large datasets.
	 * 
	 * @param key
	 * 				The key to be constrained.
	 * 
	 * @param regex
	 * 				The regular expression pattern to match.
	 * 
	 * @param modifiers
	 * 				Any of the following supported Regular expression modifiers.
	 *  				<li>use <b> i </b> for case-insensitive matching.</li>
	 *					<li>use <b> m </b> for making dot match newlines.</li>
	 *					<li>use <b> x </b> for ignoring whitespace in regex</li>
	 * 
	 * @return 
	 *		   {@link BuiltQuery} object, so you can chain this call.
	 *
	 */
	public BuiltQuery matches(String key, String regex, String modifiers){
		if(key != null && regex != null){

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("$regex", regex);

				if(modifiers != null){
					jsonObject.put("$options", modifiers);
				}

				queryValueJson.put(key, jsonObject);

			} catch (Exception e) {
				throwExeceptionWithMessage("matches", e);
			}
		}else{
			throwExeception("matches");
		}
		return this;
	}

	/**
	 * Include schemas of all returned objects along with objects themselves.
	 * 
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call. 
	 */
	public BuiltQuery includeSchema(){
		try {
			jsonMain.put("include_schema",true);
		} catch (Exception e) {
			throwExeceptionWithMessage("includeSchema", e);
		}
		return this;
	}

	/**
	 * Gives only the count of objects returned in response.
	 * 
	 * 
	 * @return 
	 *       {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery count(){
		try {
			jsonMain.put("count", "true");
		} catch (Exception e) {
			throwExeceptionWithMessage("count", e);
		}
		return this;
	}


	/**
	 * Gives object count along with objects returned in response.
	 * 
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery includeCount(){
		try {
			jsonMain.put("include_count",true);
		} catch (Exception e) {
			throwExeceptionWithMessage("includeCount", e);
		}
		return this;
	}

	/**
	 * Include object owner&#39;s profile in the objects data.
	 * 
	 * @return
	 * 		    {@linkplain BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery includeOwner(){
		try {
			jsonMain.put("include_owner",true);
		} catch (Exception e) {
			throwExeceptionWithMessage("includeUser", e);
		}
		return this;
	}

	/**
	 * Include all unpublished objects of a class.
	 * 
	 * @return 
	 * 			{@linkplain BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery includeDrafts(){
		try {

			jsonMain.put("include_unpublished",true);
		} catch (Exception e) {
			throwExeceptionWithMessage("includeDrafts", e);
		}
		return this;
	}


	/**
	 * Include only unpublished objects in response.
	 * 
	 * @return
	 * 		   {@linkplain BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery onlyDrafts(){
		try {
			jsonMain.put("include_unpublished",true);
			queryValueJson.put("published",false);
		} catch (Exception e) {
			throwExeceptionWithMessage("onlyDrafts", e);
		}
		return this;
	}

	/**
	 * Include tags with which to search objects.
	 * 
	 * @param tags
	 * 				Comma separated array of tags with which to search objects.
	 * 
	 * @return
	 *           {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery whereTags(String[] tags){
		try {
			if(tags != null){

				String tagsvalue = null;
				int count = tags.length;
				for(int i = 0; i < count; i++){
					tagsvalue = tagsvalue + "," + tags[i];
				}
				jsonMain.put("tags", tagsvalue);
			}else{
				throwExeception("tags");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("tags", e);
		}
		return this;
	}




	/**
	 * Sort the results in descending order with the given key.
	 * <br>
	 * Sort the returned objects in descending order of the provided key.
	 * 
	 * @param key 
	 * 				The key to order by.
	 * 
	 * @return  
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery descending(String key){
		if(key != null){
			try {
				jsonMain.put("desc",key);
			}catch(Exception e) {
				throwExeceptionWithMessage("descending", e);
			}
		}else{
			throwExeception("descending");
		}
		return this;
	}

	/**
	 * Sort the results in ascending order with the given key.
	 * <br>
	 * Sort the returned objects in ascending order of the provided key.
	 * 
	 * @param key 
	 * 			 The key to order by.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery ascending(String key){
		if(key != null){
			try {
				jsonMain.put("asc",key);
			}catch(Exception e) {
				throwExeceptionWithMessage("ascending", e);
			}
		}else{
			throwExeception("ascending");
		}
		return this;
	}


	/**
	 * Returns objects after specified uid.
	 * 
	 * @param uid
	 * 			   uid after which objects should be returned.
	 * 
	 * @return 
	 *          {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery afterUid(String uid){
		if(uid != null){
			try {
				jsonMain.put("after_uid", uid);
			}catch(Exception e) {
				throwExeceptionWithMessage("afterUid", e);
			}
		}else{
			throwExeception("afterUid");
		}
		return this;
	}

	/**
	 * Returns objects before specified uid.
	 * 
	 * @param uid
	 * 			  uid before which objects should be returned.
	 * 
	 * @return 
	 *          {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery beforeUid(String uid){
		if(uid != null){
			try {
				jsonMain.put("before_uid", uid);
			}catch(Exception e) {
				throwExeceptionWithMessage("beforeUid", e);
			}
		}else{
			throwExeception("beforeUid");
		}
		return this;
	}

	/**
	 * The number of objects to skip before returning any.
	 * 
	 * @param number
	 * 				No of objects to skip from returned objects.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 * <p>
	 * <b> Note: </b>The skip parameter can be used for pagination, &#34;skip&#34; specifies the number of objects to skip in the response.
	 */
	public BuiltQuery skip(int number){
		try {
			jsonMain.put("skip",  number);
		}catch(Exception e) {
			throwExeceptionWithMessage("skip", e);
		}
		return this;
	}

	/**
	 * A limit on the number of objects to return.
	 * 
	 * @param number
	 * 				No of objects to limit.
	 * 
	 * @return
	 * 		    {@link BuiltQuery} object, so you can chain this call.
	 * <p>
	 * <b> Note:</b> The limit parameter can be used for pagination, &#34;limit&#34; specifies the number of objects to limit to in the response.
	 * 
	 */
	public BuiltQuery limit(int number){
		try {
			jsonMain.put("limit", number);
		}catch(Exception e) {
			throwExeceptionWithMessage("limit", e);
		}
		return this;
	}


	/**
	 * Specifies list of field uids that would be &#39;excluded&#39; from the response.
	 * 
	 * @param fieldUid
	 * 					field uid  which get &#39;excluded&#39; from the response.
	 * 
	 *  
	 * @return 
	 *           {@link BuiltQuery} object, so you can chain this call. 
	 */
	public BuiltQuery except(ArrayList<String> fieldUid){
		try{
			if(fieldUid != null && fieldUid.size() > 0){
				if(objectUidForExcept == null){
					objectUidForExcept = new JSONArray();
				}

				int count = fieldUid.size();
				for(int i = 0; i < count; i++){
					objectUidForExcept.put(fieldUid.get(i));
				}
			}else{
				throwExeception("except");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("except", e);
		}
		return this;
	}

	/**
	 * Specifies an array of &#39;only&#39; keys that would be &#39;included&#39; in the response.
	 * 
	 * @param fieldUid
	 * 					Array of the &#39;only&#39; reference keys to be included in response.
	 * 
	 * @param referenceFieldUid
	 * 					Key who has reference to some other class object..
	 *  
	 * @return 
	 *           {@link BuiltQuery} object, so you can chain this call. 
	 */
	public BuiltQuery onlyWithReferenceUid(ArrayList<String> fieldUid, String referenceFieldUid){
		try{
			if(fieldUid != null && referenceFieldUid != null){
				if(onlyJsonObject == null){
					onlyJsonObject = new JSONObject();
				}
				JSONArray fieldValueArray = new JSONArray();
				int count = fieldUid.size();
				for(int i = 0; i < count; i++){
					fieldValueArray.put(fieldUid.get(i));
				}

				onlyJsonObject.put(referenceFieldUid, fieldValueArray);

				if(objectUidForInclude == null){
					objectUidForInclude = new JSONArray();
				}
				objectUidForInclude.put(referenceFieldUid);

			}else{
				throwExeception("onlyWithReferenceUid");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("onlyWithReferenceUid", e);
		}
		return this;
	}

	/**
	 * Specifies an array of &#39;only&#39; keys in BASE object that would be &#39;included&#39; in the response.
	 * 
	 * @param fieldUid
	 * 					Array of the &#39;only&#39; reference keys to be included in response.
	 *  
	 * @return 
	 *           {@link BuiltQuery} object, so you can chain this call. 
	 */
	public BuiltQuery only(ArrayList<String> fieldUid){
		try{
			if(fieldUid != null && fieldUid.size() > 0){
				if(objectUidForOnly == null){
					objectUidForOnly = new JSONArray();
				}

				int count = fieldUid.size();
				for(int i = 0; i < count; i++){
					objectUidForOnly.put(fieldUid.get(i));
				}
			}else{
				throwExeception("only");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("only", e);
		}
		return this;
	}


	/**
	 * Add a constraint that requires a particular reference key details.
	 * 
	 * @param keys
	 * 				 {@link array} of keys that to be constrained.
	 * 
	 * @return 
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery include(ArrayList<String> keys){
		if(keys != null && keys.size() > 0){

			if(objectUidForInclude == null){
				objectUidForInclude = new JSONArray();
			}

			int count = keys.size();
			for(int i = 0; i < count; i++){
				objectUidForInclude.put(keys.get(i));
			}
		}else{
			throwExeception("include");
		}
		return this;
	}

	/**
	 * Add a constraint to fetch all objects that do not contains given value against specified reference key.
	 * 
	 * @param key
	 * 				the key of the referred object.
	 * 
	 * @param builtQueryObject	
	 * 						The query to perform on the this {@link BuiltQuery} object.
	 * 
	 * @return
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 * <p>
	 * <b> Note:</b> :- This will fetch objects for a class by querying the fields in the referred object. 
	 * Use notinQuery if you want to fetch objects that do not match the conditions specified (negation of inQuery). 
	 * Can be used for nested references.(referred object inside referred object and so on).
	 */
	public BuiltQuery notInQuery(String key, BuiltQuery builtQueryObject){
		try {
			if(builtQueryObject != null){

				JSONObject notContainedjson = new JSONObject();
				if(builtQueryObject.queryValueJson != null && builtQueryObject.queryValueJson.length() > 0){
					notContainedjson.put("$nin_query", builtQueryObject.queryValueJson);
					queryValueJson.put(key, notContainedjson);
				}else{
					throwExeception("notInQuery");
				}
			}else{
				throwExeception("notInQuery");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("notInQuery", e);
		}
		return this;
	}

	/**
	 * Add a constraint to fetch all objects that contains given value against specified reference key.
	 * 
	 * @param key
	 * 				The key of the referred object.
	 * 
	 * 
	 * @param builtQueryObject	
	 * 					 {@link BuiltQuery} object.
	 * 					 The query to perform on the referred object	
	 * 
	 * @return
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note :</b>This will fetch objects for a class by querying the fields in the referred object. 
	 * Use inQuery if you want to fetch objects that match the conditions specified. Can be used for nested references.
	 * (referred object inside referred object and so on).
	 */

	public BuiltQuery inQuery(String key, BuiltQuery builtQueryObject){
		try {
			if(builtQueryObject != null){

				JSONObject notContainedjson = new JSONObject();
				if(builtQueryObject.queryValueJson != null && builtQueryObject.queryValueJson.length() > 0){
					notContainedjson.put("$in_query", builtQueryObject.queryValueJson);
					queryValueJson.put(key, notContainedjson);
				}else{
					throwExeception("inQuery");
				}

			}else{
				throwExeception("inQuery");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("inQuery", e);
		}
		return this;
	}

	/**
	 * Add a constraint to fetch all objects that contains given value against specified  key.
	 * 
	 * @param key
	 * 				field uid.
	 * 
	 * 
	 * @param value	
	 * 				 field value which get &#39;included&#39; from the response.
	 * 
	 * @return
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 * 
	 * <p>
	 * <b>Note :</b> for group field provide key in a &#34;key.groupFieldUid&#34; format.
	 */
	public BuiltQuery where(String key, Object value){
		try {
			if(key != null && value != null){

				queryValueJson.put(key, value);
			}else{
				throwExeception("where");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("where", e);
		}
		return this;
	}


	/**
	 * Add a constraint to fetch all objects which satisfy <b> any </b> queries.
	 * 
	 * @param queryObjects
	 * 					list of {@link BuiltQuery} instances on which OR query executes.
	 * 
	 * @return
	 * 			{@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery or(ArrayList<BuiltQuery> queryObjects){
		try {
			if(queryObjects != null && queryObjects.size() > 0){

				JSONArray orValueJson = new JSONArray();
				int count = queryObjects.size();

				for(int i = 0; i < count; i++){
					orValueJson.put(queryObjects.get(i).queryValueJson);
				}
				queryValueJson.put("$or", orValueJson);
			}else{
				throwExeception("or");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("or", e);
		}
		return this;
	}


	/**
	 * Add a constraint to fetch all objects which satisfy <b> all </b> queries.
	 * 
	 * @param queryObjects
	 * 					list of {@link BuiltQuery} instances on which AND query executes.
	 * 
	 * @return
	 * 			{@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery and(ArrayList<BuiltQuery> queryObjects){
		try {
			if(queryObjects != null && queryObjects.size() > 0){

				JSONArray orValueJson = new JSONArray();
				int count = queryObjects.size();

				for(int i = 0; i < count; i++){
					orValueJson.put(queryObjects.get(i).queryValueJson);
				}
				queryValueJson.put("$and", orValueJson);
			}else{
				throwExeception("and");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("and", e);
		}
		return this;
	}

	/**
	 * To construct complex queries.
	 * 
	 * @param custom 
	 * 				 {@link HashMap} object.
	 *  
	 * @return
	 * 			{@link BuiltQuery} object, so you can chain this call.
	 * 
	 * <p>
	 * <b> Note:</b> If a complex query needs to be performed and if it is difficult to specify it using the {@link BuiltQuery} methods,
	 *  a {@link HashMap} can be provided that specifies all the conditions. 
	 *  <br>
	 *  The {@link HashMap} should be as per the JSON format specified in the <a href=" https://api.built.io/docs/v1> REST API docs </a>.
	 */
	public BuiltQuery customQuery(HashMap<String, Object> custom) {
		try {
			if(custom != null && custom.size() > 0){

				for (Map.Entry<?, ?> entry : custom.entrySet()) {
					String key = (String) entry.getKey();
					queryValueJson.put(key, custom.get(key));
				}
			}else{
				throwExeception("complexQuery");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("complexQuery", e);
		}
		return this;
	}

	/**
	 * Add a constraint to the query that requires a particular key&#39;s object to be equal to the provided object.
	 * 
	 * @param whereKey
	 * 					The key to be constrained.
	 * 
	 * @param builtQueryObject
	 * 							{@link BuiltQuery} object on which select query will executes.
	 * 		
	 * 
	 * @param pickedKey
	 * 						the key for which the values should be returned after executing select query.
	 * 		
	 * 
	 * @return
	 * 				{@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery select(String whereKey, BuiltQuery builtQueryObject, String pickedKey) {
		try {
			if(whereKey != null && builtQueryObject.queryValueJson != null && builtQueryObject.queryValueJson.length() > 0 && pickedKey != null){

				JSONObject selectQueryValueJson = new JSONObject();
				JSONObject selectQueryJson      = new JSONObject();

				selectQueryValueJson.put("query", builtQueryObject.queryValueJson);
				selectQueryValueJson.put("class_uid", builtQueryObject.classUid);
				selectQueryValueJson.put("key", pickedKey);

				selectQueryJson.put("$select", selectQueryValueJson);
				queryValueJson.put(whereKey, selectQueryJson);

			}else{
				throwExeception("select");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("select", e);
		}
		return this;

	}

	/**
	 * Add a constraint to the query that requires a particular key&#39;s object to be equal to the provided object.
	 * 
	 * @param whereKey
	 * 						The key to be constrained.
	 * 
	 * @param builtQueryObject
	 * 							{@link BuiltQuery} object on which dontSelect query will executes.
	 * 							 This is the inverse of the select query. Returns all the objects that do not match the conditions.
	 * 
	 * @param pickedKey
	 * 						the key for which the values should be returned after executing dontSelect query.
	 * 
	 * @return
	 * 			{@link BuiltQuery} object, so you can chain this call.
	 * 
	 */
	public BuiltQuery dontSelect(String whereKey, BuiltQuery builtQueryObject, String pickedKey) {
		try {
			if(whereKey != null && builtQueryObject.queryValueJson != null && builtQueryObject.queryValueJson.length() > 0 && pickedKey != null){

				JSONObject selectQueryValueJson = new JSONObject();
				JSONObject selectQueryJson      = new JSONObject();

				selectQueryValueJson.put("query", builtQueryObject.queryValueJson);
				selectQueryValueJson.put("class_uid", builtQueryObject.classUid);
				selectQueryValueJson.put("key", pickedKey);

				selectQueryJson.put("$dont_select", selectQueryValueJson);
				queryValueJson.put(whereKey, selectQueryJson);


			}else{
				throwExeception("dontSelect");
			}
		}catch(Exception e) {
			throwExeceptionWithMessage("dontSelect", e);
		}
		return this;

	}

	/**
	 * Remove provided query key from custom query if exist. 
	 * 
	 * @param key
	 * 				Query name to remove.
	 * 
	 * @return
	 * 			{@linkplain BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery removeFilter(String key) {
		try{
			if(jsonMain.has(key)){
				jsonMain.remove(key);
			}
		}catch (Exception e) {
			throwExeceptionWithMessage("removeFilter", e);
		}
		return this;
	}





	/**
	 * Execute a Query and Caches its result (Optional)
	 * 
	 * @param callback
	 * 				{@link QueryResultsCallBack} object to notify the application when the request has completed.
	 * 
	 * @return 
	 * 		{@linkplain BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery exec(QueryResultsCallBack callback){
		try{
			if(isJsonProper){

				if(classUid != null){

					String URL = BuiltAppConstants.URLSCHEMA + BuiltAppConstants.URL + "/" + BuiltAppConstants.VERSION + "/classes/" + classUid + "/objects";

					setQueryJson(callback);
					jsonMain.put("_method", BuiltAppConstants.RequestMethod.GET.toString());
					if(! jsonMain.has("include_owner")){
						jsonMain.put("include_owner", true);
					}

					Header[] headers = getHeaders(headerGroup_local).getAllHeaders();
					HashMap<String, String> headerAll = new HashMap<String, String>();
					int count = headers.length;
					for(int i = 0; i < count; i++){

						headerAll.put(headers[i].getName(), headers[i].getValue());

					}

					if(headerAll.size() < 1){

						BuiltError error = new BuiltError();
						error.errorMessage(BuiltAppConstants.ErrorMessage_CalledBuiltDefaultMethod);
						if(callback != null){
							callback.onRequestFail(error);
						}

					}else{

						String mainStringForMD5 = URL + jsonMain.toString() + headerAll.toString();
						String md5Value = new RawAppUtils().getMD5FromString(mainStringForMD5.trim());

						File cacheFile = new File(BuiltAppConstants.cacheFolderName + File.separator + md5Value);
						builtQueryResultsCallBack = callback;
						switch (cachePolicyForCall) {
						case IGNORE_CACHE:

							new BuiltCallBackgroundTask(this,BuiltControllers.QUERYOBJECT, URL,headers, jsonMain, null, classUid, callback);
							break;

						case CACHE_ONLY:

							if(cacheFile.exists()){
								boolean needToSendCall = false;

								if(maxCachetimeForCall > 0){

									needToSendCall = new RawAppUtils().getResponseTimeFromCacheFile(cacheFile, (int) maxCachetimeForCall);
								}else{
									needToSendCall = new RawAppUtils().getResponseTimeFromCacheFile(cacheFile, (int) defaultCacheTimeInterval);
								}
								if(needToSendCall){
									throwExeception(callback, BuiltAppConstants.ErrorMessage_ObjectNotFoundInCache);
								}else{
									BuiltObjectsModel model = new BuiltObjectsModel(RawAppUtils.getJsonFromCacheFile(cacheFile), null, true);
									List<BuiltObject> objectList   = new ArrayList<BuiltObject>();
									List<java.lang.Object> objects = model.objectList;

									int countObject = objects.size();
									for(int i = 0; i < countObject; i++){
										BuiltObject builtObject = new BuiltObject(classUid);
										builtObject.setUid(((BuiltObjectModel)objects.get(i)).objectUid);
										builtObject.resultJson   		= ((BuiltObjectModel)objects.get(i)).jsonObject;
										builtObject.ownerEmailId 		= ((BuiltObjectModel)objects.get(i)).ownerEmailId;
										builtObject.ownerUid     		= ((BuiltObjectModel)objects.get(i)).ownerUid;
										builtObject.owner		 		= ((BuiltObjectModel)objects.get(i)).ownerMap;
										builtObject.builtACLUserObject 	= ((BuiltObjectModel)objects.get(i)).builtACLInstance;
										builtObject.setTags(((BuiltObjectModel)objects.get(i)).tags);		 		
										objectList.add(builtObject);
									}
									QueryResult queryResultObject = new QueryResult();
									queryResultObject.setJSON(model.jsonObject,objectList);
									queryResultObject.status = resultStatus.FROM_CACHE;
									builtQueryResultsCallBack.onRequestFinish(queryResultObject);
									model = null;
								}
							}else{
								throwExeception(callback, BuiltAppConstants.ErrorMessage_ObjectNotFoundInCache);
							}

							break;

						case NETWORK_ONLY:

							new BuiltCallBackgroundTask(this, BuiltControllers.QUERYOBJECT, URL, headers, jsonMain, cacheFile.getPath(), callController.BUILTQUERY.toString(), callback);
							break;

						case CACHE_ELSE_NETWORK:

							builtQueryResultsCallBack = callback;
							if(cacheFile.exists()){
								boolean needToSendCall = false;
								if(maxCachetimeForCall > 0){

									needToSendCall = new RawAppUtils().getResponseTimeFromCacheFile(cacheFile, (int) maxCachetimeForCall);
								}else{
									needToSendCall = new RawAppUtils().getResponseTimeFromCacheFile(cacheFile, (int) defaultCacheTimeInterval);
								}
								if(needToSendCall){

									new BuiltCallBackgroundTask(this, BuiltControllers.QUERYOBJECT, URL, headers, jsonMain, cacheFile.getPath(), callController.BUILTQUERY.toString(), callback);

								}else{
									BuiltObjectsModel model = new BuiltObjectsModel(RawAppUtils.getJsonFromCacheFile(cacheFile), null,true);
									List<BuiltObject> objectList = new ArrayList<BuiltObject>();
									List<java.lang.Object> objects = model.objectList;
									int countObjects = objects.size();
									for(int i = 0; i < countObjects; i++){
										BuiltObject builtObject = new BuiltObject(classUid);
										builtObject.setUid(((BuiltObjectModel)objects.get(i)).objectUid);
										builtObject.resultJson   		= ((BuiltObjectModel)objects.get(i)).jsonObject;
										builtObject.ownerEmailId 		= ((BuiltObjectModel)objects.get(i)).ownerEmailId;
										builtObject.ownerUid     		= ((BuiltObjectModel)objects.get(i)).ownerUid;
										builtObject.owner		 		= ((BuiltObjectModel)objects.get(i)).ownerMap;
										builtObject.builtACLUserObject 	= ((BuiltObjectModel)objects.get(i)).builtACLInstance;
										builtObject.setTags(((BuiltObjectModel)objects.get(i)).tags);		 	
										objectList.add(builtObject);
									}
									QueryResult queryResultObject = new QueryResult();
									queryResultObject.setJSON(model.jsonObject,objectList);
									queryResultObject.status = resultStatus.FROM_CACHE;
									builtQueryResultsCallBack.onRequestFinish(queryResultObject);
									model = null;
								}

							}else{
								new BuiltCallBackgroundTask(this, BuiltControllers.QUERYOBJECT, URL, headers, jsonMain, cacheFile.getPath(), callController.BUILTQUERY.toString(), callback);
							}
							break;

						case NETWORK_ELSE_CACHE:

							if(BuiltAppConstants.isNetworkAvailable){
								new BuiltCallBackgroundTask(this, BuiltControllers.QUERYOBJECT, URL, headers, jsonMain, cacheFile.getPath(), callController.BUILTQUERY.toString(), callback);

							}else{
								if(cacheFile.exists()){
									BuiltObjectsModel model        = new BuiltObjectsModel(RawAppUtils.getJsonFromCacheFile(cacheFile), null,true);
									List<BuiltObject> objectList   = new ArrayList<BuiltObject>();
									List<java.lang.Object> objects = model.objectList;
									int countObject = objects.size();
									for(int i = 0; i < countObject; i++){
										BuiltObject builtObject = new BuiltObject(classUid);
										builtObject.setUid(((BuiltObjectModel)objects.get(i)).objectUid);
										builtObject.resultJson   		= ((BuiltObjectModel)objects.get(i)).jsonObject;
										builtObject.ownerEmailId 		= ((BuiltObjectModel)objects.get(i)).ownerEmailId;
										builtObject.ownerUid     		= ((BuiltObjectModel)objects.get(i)).ownerUid;
										builtObject.owner		 		= ((BuiltObjectModel)objects.get(i)).ownerMap;
										builtObject.builtACLUserObject 	= ((BuiltObjectModel)objects.get(i)).builtACLInstance;
										builtObject.setTags(((BuiltObjectModel)objects.get(i)).tags);		 	
										objectList.add(builtObject);
									}
									QueryResult queryResultObject = new QueryResult();
									queryResultObject.setJSON(model.jsonObject,objectList);
									queryResultObject.status = resultStatus.FROM_CACHE;
									builtQueryResultsCallBack.onRequestFinish(queryResultObject);
									model = null;
								}else{
									throwExeception(callback, BuiltAppConstants.ErrorMessage_ObjectNotFoundInCache);
								}
							}

							break;

						case CACHE_THEN_NETWORK:

							// from cache
							if(cacheFile.exists()){
								BuiltObjectsModel model        = new BuiltObjectsModel(RawAppUtils.getJsonFromCacheFile(cacheFile), null, true);
								List<BuiltObject> objectList   = new ArrayList<BuiltObject>();
								List<java.lang.Object> objects = model.objectList;
								int countObject = objects.size();
								for(int i = 0; i < countObject; i++){
									BuiltObject builtObject = new BuiltObject(classUid);
									builtObject.setUid(((BuiltObjectModel)objects.get(i)).objectUid);
									builtObject.resultJson   		= ((BuiltObjectModel)objects.get(i)).jsonObject;
									builtObject.ownerEmailId 		= ((BuiltObjectModel)objects.get(i)).ownerEmailId;
									builtObject.ownerUid     		= ((BuiltObjectModel)objects.get(i)).ownerUid;
									builtObject.owner		 		= ((BuiltObjectModel)objects.get(i)).ownerMap;
									builtObject.builtACLUserObject 	= ((BuiltObjectModel)objects.get(i)).builtACLInstance;
									builtObject.setTags(((BuiltObjectModel)objects.get(i)).tags);		 	
									objectList.add(builtObject);
								}
								QueryResult queryResultObject = new QueryResult();
								queryResultObject.setJSON(model.jsonObject,objectList);
								queryResultObject.status = resultStatus.FROM_CACHE;
								builtQueryResultsCallBack.onRequestFinish(queryResultObject);
								model = null;
							}else{
								throwExeception(callback, BuiltAppConstants.ErrorMessage_ObjectNotFoundInCache);
							}

							// from network
							new BuiltCallBackgroundTask(this, BuiltControllers.QUERYOBJECT, URL, headers, jsonMain, cacheFile.getPath(), callController.BUILTQUERY.toString(), callback);
							break;

						default:
							break;
						}
					}
				}else{
					throwExeception(callback, BuiltAppConstants.ErrorMessage_ClassUID);
				}

			}else{
				BuiltError error = new BuiltError();
				HashMap<String, Object> errorHashMap = new HashMap<String, Object>();
				errorHashMap.put(errorFilterName, errorMesage);
				error.errors(errorHashMap);
				error.errorMessage(BuiltAppConstants.ErrorMessage_JsonNotProper);
				if(callback != null){
					callback.onRequestFail(error);
				}
			}
		}catch (Exception e) {
			throwExeception(callback, e.toString());
		}
		return this;
	}


	//*****************************************************************************

	/* 
	 * Cache related methods
	 * 
	 */

	/**
	 * To set time in milliseconds till which cache result is valid.
	 * 
	 * @param timeoutIntervalInMilliseconds 
	 * 										 {@link Long} value.
	 * <p>
	 * <b>Note: </b> Default time out interval is 24 hours.		
	 */
	public void setTimeOutInterval(long timeoutIntervalInMilliseconds){
		this.maxCachetimeForCall = timeoutIntervalInMilliseconds;
	}

	/**
	 * To set cache policy.
	 * 
	 * @param cachePolicy cache policy from {@link CachePolicy}.
	 * 
	 * <p>
	 * <b>Note: </b> Default cache policy is set to {@link com.raweng.built.BuiltQuery.CachePolicy #IGNORE_CACHE}.
	 * 
	 */
	public void setCachePolicy(CachePolicy cachePolicy) {
		cachePolicyForCall = cachePolicy;
	}
	/**
	 * cache policy.
	 * 
	 * @author raw engineering, Inc
	 *
	 */
	public static enum CachePolicy{
		/**
		 * To fetch data from cache.
		 */
		CACHE_ONLY, 

		/**
		 * To fetch data from network and response will be saved in cache.
		 */
		NETWORK_ONLY, 

		/**
		 * To fetch data from cache if data not available in cache then it will send a network call and response will be saved in cache.
		 */
		CACHE_ELSE_NETWORK, 

		/**
		 * To fetch data from network and response will be saved in cache ; if network not available then it will fetch data from cache.
		 */
		NETWORK_ELSE_CACHE, 

		/**
		 * To fetch data from cache and send a network call and response will be saved in cache.
		 * user can identify data type  by checking com.raweng.built.QueryResult.status variable.
		 */
		CACHE_THEN_NETWORK, 

		/**
		 * To fetch data from network call and response will not be saved cache. 
		 */
		IGNORE_CACHE;
	}

	/**
	 * To cancel all {@link BuiltQuery} network calls.
	 */
	public void cancelCall() {
		BuiltAppConstants.cancelledCallController.add(callController.BUILTQUERY.toString());
	}

	/**
	 * Add a constraint to fetch all objects that contains given value against specified  key.
	 * 
	 * @param key
	 * 				field uid.
	 * 
	 * @param value	
	 * 				 field value which get &#39;included&#39; from the response.
	 * 
	 * @return
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery includeFilter(String key, String value){
		try {
			if(key != null && value != null){

				jsonMain.put(key, value);
			}else{
				throwExeception("includeFilter");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("includeFilter", e);
		}
		return this;
	}

	/**
	 *  Add a constraint to fetch all objects near specified location, search for objects within specified radius.
	 * 
	 * @param locationObject
	 * 						 either {@link BuiltLocation} instance or object uid in {@link string}. 
	 *  In latter case, it will use the object&#39;s location.
	 * 
	 * 
	 * @param radius
	 *                radius in meters. 
	 * @return
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery nearLocation(Object locationObject, int radius){
		try {

			if(locationObject != null){

				JSONObject valueJson = new JSONObject();
				if(locationObject instanceof BuiltLocation){
					if(((BuiltLocation)locationObject).getLatitude() != null  && 
							((BuiltLocation)locationObject).getLongitude() != null){


						JSONArray coordsValue = new JSONArray();

						coordsValue.put(((BuiltLocation)locationObject).getLongitude());
						coordsValue.put(((BuiltLocation)locationObject).getLatitude());

						valueJson.put("coords", coordsValue);
					}else{
						throwExeception("nearLocation");
						return this;
					}

				}else{
					if(locationObject instanceof String){

						JSONObject coordsValue = new JSONObject();
						coordsValue.put("object", (String)locationObject);
						valueJson.put("coords", coordsValue);

					}else{
						throwExeception("nearLocation");
						return this;
					}
				}

				if(radius < 1){
					radius = 0;
				}
				valueJson.put("radius", radius);

				queryValueJson.put("$near", valueJson);
			}else{
				throwExeception("nearLocation");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("nearLocation", e);
			return this;
		}
		return this;
	}



	/**
	 * Add a constraint to fetch all objects within specified points.
	 * 
	 * @param locationObjectList
	 *                           {@link ArrayList} of location objects. Location object can be either {@link BuiltLocation} instance or object uid in {@link string}. 
	 *  							In latter case, it will use the object&#39;s location.
	 *  						At least three objects have to be specified in order to form the simplest polygon. 
	 * 
	 * @return
	 * 			 {@link BuiltQuery} object, so you can chain this call.
	 */
	public BuiltQuery withInLocation(ArrayList<Object> locationObjectList) {

		try {
			if(locationObjectList != null && locationObjectList.size() > 0){
				int count = locationObjectList.size();

				JSONArray withInValue = new JSONArray();
				for(int i = 0; i < count; i++){

					Object locationObject = locationObjectList.get(i);
					if(locationObject instanceof String){

						JSONObject coordsValue = new JSONObject();
						coordsValue.put("object", (String)locationObject);
						withInValue.put(coordsValue);

					}else if(locationObject instanceof BuiltLocation){
						if(((BuiltLocation)locationObject).getLatitude() != null  && 
								((BuiltLocation)locationObject).getLongitude() != null){


							JSONArray coordsValue = new JSONArray();

							coordsValue.put(((BuiltLocation)locationObject).getLongitude());
							coordsValue.put(((BuiltLocation)locationObject).getLatitude());

							withInValue.put(coordsValue);
						}else{
							throwExeception("withInLocation");
						}
					}
				}

				queryValueJson.put("$within", withInValue);

			}else{

				throwExeception("withInLocation");
			}
		} catch (Exception e) {
			throwExeceptionWithMessage("withInLocation", e);
			return this;
		}

		return this;

	}

	/**************************************************************************************************************
	 * 
	 * 
	 * 
	 *************************/

	@Override
	public void getResult(Object obj) {}


	@Override
	public void getResultObject(List<java.lang.Object> object, JSONObject jsonobject) {
		List<BuiltObject> objectList = new ArrayList<BuiltObject>();
		int countObject = object.size();
		for(int i = 0; i < countObject; i++){
			BuiltObject builtObject = new BuiltObject(classUid);
			builtObject.setUid(((BuiltObjectModel)object.get(i)).objectUid);
			builtObject.resultJson   		= ((BuiltObjectModel)object.get(i)).jsonObject;
			builtObject.ownerEmailId 		= ((BuiltObjectModel)object.get(i)).ownerEmailId;
			builtObject.ownerUid     		= ((BuiltObjectModel)object.get(i)).ownerUid;
			builtObject.owner		 		= ((BuiltObjectModel)object.get(i)).ownerMap;
			builtObject.builtACLUserObject 	= ((BuiltObjectModel)object.get(i)).builtACLInstance;
			builtObject.setTags(((BuiltObjectModel)object.get(i)).tags);		 	
			objectList.add(builtObject);
		}
		QueryResult queryResultObject = new QueryResult();
		queryResultObject.setJSON(jsonobject,objectList);
		builtQueryResultsCallBack.onRequestFinish(queryResultObject);
	}

	private void setQueryJson(QueryResultsCallBack callback) {
		try{

			if(queryValueJson != null && queryValueJson.length() > 0){
				jsonMain.put("query", queryValueJson);
			}

			if(objectUidForExcept != null && objectUidForExcept.length() > 0){
				JSONObject exceptValueJson = new JSONObject();
				exceptValueJson.put("BASE", objectUidForExcept);
				jsonMain.put("except", exceptValueJson);
				objectUidForExcept = null;

			}
			if(exceptJsonObject != null && exceptJsonObject.length() > 0){

				jsonMain.put("except", exceptJsonObject);
				exceptJsonObject = null;
			}

			if(objectUidForOnly!= null && objectUidForOnly.length() > 0){
				JSONObject onlyValueJson = new JSONObject();
				onlyValueJson.put("BASE", objectUidForOnly);
				jsonMain.put("only", onlyValueJson);
				objectUidForOnly = null;

			}
			if(onlyJsonObject != null && onlyJsonObject.length() > 0){

				jsonMain.put("only", onlyJsonObject);
				onlyJsonObject = null;
			}

			if(objectUidForInclude!= null && objectUidForInclude.length() > 0){
				jsonMain.put("include", objectUidForInclude);
				objectUidForInclude = null;
			}


		}catch (Exception e) {
			BuiltError error = new BuiltError();
			HashMap<String, Object> errorHashMap = new HashMap<String, Object>();
			errorHashMap.put(errorFilterName, errorMesage);
			error.errors(errorHashMap);
			error.errorMessage(BuiltAppConstants.ErrorMessage_JsonNotProper);
			if(callback != null){
				callback.onRequestFail(error);
			}
		}
	}

	private void throwExeception(QueryResultsCallBack callback, String errorMessage) {
		BuiltError error = new BuiltError();
		error.errorMessage(errorMessage);
		if(callback != null){
			callback.onRequestFail(error);
		}
	}

	private void throwExeceptionWithMessage(String filterName, Exception exception) {
		isJsonProper = false;
		errorFilterName = filterName;
		errorMesage = exception.toString();
		RawAppUtils.showLog("BuiltQuery", "---------" + filterName + "|" + exception);
	}

	private void throwExeception(String filterName) {
		isJsonProper = false;
		errorFilterName = filterName;
		errorMesage = BuiltAppConstants.ErrorMessage_QueryFilterException;
		RawAppUtils.showLog("BuiltQuery", "---------" + filterName + "|" + errorMesage);
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
					org.apache.http.Header header =  headerGroup_local.getCondensedHeader(localHeaders.getAllHeaders()[i].getName());
					endHeaderGroup.removeHeader(header);
				}
				endHeaderGroup.addHeader(localHeaders.getAllHeaders()[i]);
			}
			return endHeaderGroup;
		}else{
			return mainHeaderGroup;
		}
	}





}
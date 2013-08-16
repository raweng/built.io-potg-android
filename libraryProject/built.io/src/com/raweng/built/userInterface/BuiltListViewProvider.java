package com.raweng.built.userInterface;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.built.R;
import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.RawAppUtils;

/**
 * This class provides ListView with support of built.io SDK.
 * 
 * @author raw engineering, Inc
 * 
 * <br><b>Example</b><br>
 * <pre class="prettyprint">
 * BuiltListViewProvider listviewproviderObject = new BuiltListViewProvider(context,"classUid");<br>
 * setContentView(listviewproviderObject.getLayout());
 * </pre>
 * <b>OR</b>
 * <p>
 *  It can be added to any layout like :<br>
 * <pre class="prettyprint">
 *  addView(listviewproviderObject.getLayout());</pre><br>
 *  Sample Code:<br>
 * <pre class="prettyprint">
 *  &#160;listviewproviderObject.setProgressDialog(progressDialogObject);
 *  &#160;listviewproviderObject.setLimit(5);
 *  &#160;listviewproviderObject.enableAutoScroll();
 *  &#160;listviewproviderObject.setPullToRefresh(true);
 *  &#160;listviewproviderObject.enableSearchView();
 *  &#160;listviewproviderObject.setSearchByUid("fieldUid");
 *  &#160;listviewproviderObject.setListViewBackgroundColor("#d2b48c");
 *  &#160;listviewproviderObject.builtQueryInstance.includeOwner();<br>
 *  &#160;listviewproviderObject.loadData(new BuiltListViewResultCallBack(){<br>
 * 
 * 	&#64;Override<br>
 *        public void onError(BuiltError error){}<br>
 * 
 * 	&#64;Override<br>
 * 	  public void onAlways(){}<br>
 * 									
 * 	&#64;Override<br>
 * 	  public View getView(int position, View convertView, ViewGroup parent,BuiltObject builtObject){<br>
 * 
 * 			LayoutInflater inflater = LayoutInflater.from(context);
 * 			convertView             = inflater.inflate(R.layout.your_layout, parent, false);
 * 			TextView textView       = (TextView) convertView.findViewById(R.id.textview);
 * 			textView.setText(builtObject.getString("name"));
 * 
 * 			return convertView;<br>
 * 	 	}<br>
 * 	});
 *</pre>
 */
public class BuiltListViewProvider{

	private static final String TAG = "BuiltListViewProvider";

	Context builtListViewContext;

	PullToRefreshListView listview;
	PullToRefreshListView searchListView;

	ProgressDialog  progressDialog;

	List<BuiltObject> builtObjects;
	List<BuiltObject> datasourceBuiltObjects;

	ResultDataSource  datasource;

	int limit     			 = 10;
	int fetchLimit 			 = 0;

	boolean isSearchViewEnable		      = false;
	boolean isPullToRefreshEnable 		  = true;
	boolean isListRefreshed          	  = false;
	boolean isProgressDialogEnable 	      = false;
	boolean isAutoScrollEnable 	  	      = false;

	String searchFieldUid = BuiltAppConstants.SEARCH_HINT_NOT_PRESENT;
	String hexCode = "#d3d3d3";

	BuiltQuery searchBuiltQueryInstance;
	BuiltListViewResultCallBack listviewResultCallBack;

	View 	  listContainer;
	ViewGroup searchViewHeader;

	AdapterView.OnItemClickListener listClickListener;
	ResultDataSource serachDataSource;

	/**
	 * Provide TextView object which is used when List is empty.
	 */
	public TextView emptyTextView;

	/**
	 * Provides {@link BuiltQuery} object which is used for retrieving data.
	 */
	public BuiltQuery builtQueryInstance;

	/**
	 * Provide boolean object which indicates whether loading is started.
	 */
	private boolean isLoadingStart = false;

	/**
	 * Initialize {@link BuiltListViewProvider} instance.
	 * 
	 * @param context
	 * 				 	set application context.
	 * @param classUid
	 * 					set class uid.
	 *  <br><b>Example</b><br> 
	 *  <pre class="prettyprint">
	 *  &#160;BuiltListViewProvider listviewproviderObject = new BuiltListViewProvider(context, &#34;classUid&#34;);
	 *  </pre>
	 */
	public BuiltListViewProvider(Context context, String classUid){
		try{
			builtListViewContext = context;

			LayoutInflater mainLayoutInflater 	= (LayoutInflater) builtListViewContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
			listContainer 		= mainLayoutInflater.inflate(R.layout.view_list, null);

			listview 			= (PullToRefreshListView)listContainer.findViewById(R.id.pullToRefreshListView);
			searchListView 		= (PullToRefreshListView)listContainer.findViewById(R.id.pullToRefreshSearchListView);

			emptyTextView 		= (TextView)listContainer.findViewById(R.id.noComment);


			builtQueryInstance 		 		 = new BuiltQuery(classUid);
			searchBuiltQueryInstance 		 = new BuiltQuery(classUid);

			datasourceBuiltObjects           = new ArrayList<BuiltObject>();
			datasource = new ResultDataSource(builtListViewContext,datasourceBuiltObjects);
			
			searchListView.setMode(Mode.BOTH);
			listview.setVisibility(View.GONE);
			searchListView.setVisibility(View.GONE);
			emptyTextView.setVisibility(View.GONE);

			setPullToRefresh(true);
		}catch(Exception e){
			RawAppUtils.showLog(TAG,e.toString());
		}
	}

	/**
	 * This provides the  parent layout of this  {@link BuiltListViewProvider} instance.
	 *
	 * @return 
	 * 			 {@link View} which is set for {@link BuiltListViewProvider} instance. 
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;setContentView(listviewproviderObject.getLayout());
	 * </pre>
	 */
	public View getLayout(){

		return listContainer;	
	}

	/**
	 * This provides the object of {@link PullToRefreshListView}.
	 * 
	 * @return 
	 * 			Returns {@link PullToRefreshListView} object.
	 * 
	 */
	public PullToRefreshListView getListView(){
		return listview;
	}

	/**
	 * This provides the object of {@link PullToRefreshListView}.
	 * 
	 * 
	 * @return 
	 * 			Returns {@link PullToRefreshListView} object.
	 * 
	 */
	public PullToRefreshListView getSearchListView(){
		return searchListView;
	}

	/**
	 * Provides adapter object.
	 * 
	 * @return
	 * 			provide ArrayAdapter&#60;BuiltObject&#62; Object. 
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;ArrayAdapter&#60;BuiltObject&#62; adapterObject = listviewprovider.getDataSourceObject();
	 * </pre>
	 */
	public ArrayAdapter<BuiltObject> getDataSourceObject(){
		return datasource;
	}

	/**
	 * Provides the count of elements in adapter.
	 * 
	 * @return 
	 * 			provides number of adapter objects.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;int count = listviewproviderObject.getCount();
	 * </pre>
	 */
	public int getCount(){
		if(datasource != null){
			return datasource.getCount();
		}
		return 0;
	}

	/**
	 * Set limit to ListView.
	 * 
	 * @param loadLimit
	 * 					set limit.
	 * 
	 * <br><b>Default</b><br> loadLimit is 10.
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setLimit(20);
	 * </pre>
	 * 
	 */
	public void setLimit(int loadLimit){
		limit = loadLimit;
	}

	/**
	 * Get limit which is set list to load object.
	 * 
	 * @return loadLimit
	 * 					provide limit which was set for load object. 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;int limit = listviewproviderObject.getLimit();	
	 * </pre>
	 */
	public int getLimit(){
		return limit;
	}

	/**
	 * Delete the object at given position from adapter.
	 *       
	 * @param position
	 * 				position of row.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.deleteBuiltObjectAtIndex(5);
	 * </pre>
	 */
	public void deleteBuiltObjectAtIndex(int position){
		if(datasource != null){
			if(position < datasource.getSize()){
				datasource.remove(datasource.getItem(position));
			}
		}
	}

	/**
	 * Insert the object at position inside adapter.
	 * @param position
	 * 					position of row.
	 * @param object
	 * 					BuiltObject instance.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.insertBuiltObjectAtIndex(0,builtObject);
	 * </pre>
	 */
	public void insertBuiltObjectAtIndex(int position,BuiltObject object){
		if(datasource != null && object != null){
			if(position < datasource.getSize() && position >= 0){
				datasource.insert(object, position);
			}
		}
	}

	/**
	 * Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh itself.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.notifyDataSetChanged();
	 * </pre>
	 */
	public void notifyDataSetChanged(){
		if(datasource != null){
			datasource.notifyDataSetChanged();
		}	
	}

	/**
	 * Set background color to search view.
	 * 
	 * @param hexNumber
	 * 				Provide Hex number
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setSearchViewColor("#d2b48c");
	 * </pre>
	 */
	public void setSearchViewBackgroundColor(String hexNumber){
		Pattern pattern;
		Matcher matcher;
		String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
		pattern = Pattern.compile(HEX_PATTERN);
		if(hexNumber != null){
			matcher = pattern.matcher(hexNumber);
			if(matcher.matches()){
				hexCode = hexNumber;
			}
		}
	}

	/**
	 * Clears all items in adapter.
	 * 
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.clearAll();
	 * </pre>
	 */
	public void clearAll(){
		if(datasource != null){
			datasource.clearAll();
		}
	}

	/**
	 * Set background color to ListView.
	 * 
	 * @param hexNumber
	 * 				Provide Hex number
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setListViewBackgroundColor(&#34;#d2b48c&#34;);
	 * </pre>
	 */
	public void setListViewBackgroundColor(String hexNumber){
		Pattern pattern;
		Matcher matcher;
		String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
		pattern = Pattern.compile(HEX_PATTERN);
		if(hexNumber != null){
			matcher = pattern.matcher(hexNumber);
			if(matcher.matches()){
				listview.setBackgroundColor(Color.parseColor(hexNumber));
			}
		}
	}

	/**
	 * Set uid of the field to search for.
	 * 
	 * @param fieldUid
	 * 			  set uid.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setSearchByUid(&#34;name&#34;);
	 * </pre>
	 */
	public void setSearchByUid(String fieldUid){
		if(fieldUid != null){
			searchFieldUid = fieldUid;
		}
	}

	/**
	 * Get the search field&#39;s uid.
	 * 		
	 * @return uid. 
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;String uid = listviewproviderObject.getSearchFieldUid();
	 * </pre>
	 */
	public String getSearchFieldUid(){
		return searchFieldUid;
	}

	/**
	 * Enables search view.
	 * Use {@link #setSearchByUid} to set FieldUid.
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.enableSearchView();
	 * </pre>
	 */
	public void enableSearchView(){
		isSearchViewEnable = true;
		RelativeLayout relativeLayout = (RelativeLayout) listContainer.findViewById(R.id.listviewContainer);

		RelativeLayout.LayoutParams listparams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		LayoutInflater searchViewInflater = LayoutInflater.from(builtListViewContext);

		searchViewHeader = (ViewGroup)searchViewInflater.inflate(R.layout.search, listview, false);
		searchViewHeader.setBackgroundColor(Color.parseColor(hexCode));
		relativeLayout.addView(searchViewHeader);
		listparams.addRule(RelativeLayout.BELOW, searchViewHeader.getId());
		listview.setLayoutParams(listparams);
		searchListView.setLayoutParams(listparams);
	}

	/**
	 * Enable or disable pulltorefresh feature.
	 * 
	 * @param isPullToRefresh
	 * 						 true/false
	 * <br><b>Default</b><br>  true
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setPullToRefresh(false);
	 * </pre>
	 */
	public void setPullToRefresh(boolean isPullToRefresh){

		listview.setVisibility(View.VISIBLE);
		if(isPullToRefresh){

			isPullToRefreshEnable = true;
			listview.setMode(Mode.BOTH);

			if(listClickListener != null){
				setOnItemClickListener(listClickListener);
			}

		}else{

			isPullToRefreshEnable = false; 
			listview.setMode(Mode.PULL_FROM_END);

			if(listClickListener != null){
				setOnItemClickListener(listClickListener);
			}
		}
		if(isAutoScrollEnable){
			listview.setMode(Mode.DISABLED);
			enableAutoScroll();
		}
	}

	/**
	 * Load the next object on scroll.
	 * 
	 * If its disabled then user will have to pull-up the list from bottom to load next objects.
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setAutoScroll();
	 * </pre>
	 */
	public void enableAutoScroll(){
		isAutoScrollEnable = true;

		if(isPullToRefreshEnable && isAutoScrollEnable){
			listview.setMode(Mode.PULL_FROM_START);
		}else if(!isPullToRefreshEnable && isAutoScrollEnable){
			listview.setMode(Mode.DISABLED);
		}
	}

	/**
	 * Provides list of {@link BuiltObject}<br>
	 *  
	 * @param callback
	 * 					{@link BuiltListViewResultCallBack} object to notify the application when the request has completed and provides getView().
	 * 
	 * <br><b>Example</b><br>
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.loadData(new BuiltListViewResultCallBack{<br>
	 * 
	 * &#160;&#64;Override<br>
	 * &#160;public void getView(int position, View convertView, ViewGroup parent, BuiltObject builtObject) { }<br>
	 * 
	 * &#160;&#64;Override<br>
	 * &#160;public void onError(String error) { }<br>
	 *  
	 * &#160;&#64;Override <br>
	 * &#160;public void onAlways() { } });
	 * </pre>
	 *  
	 */
	public void loadData(BuiltListViewResultCallBack callback) {

		listviewResultCallBack = callback;
		isLoadingStart = true;
		datasource.setCallBack(callback);
		try{
			if(isProgressDialogEnable)
				progressDialog.show();
		}catch(Exception e){
			RawAppUtils.showLog("BuiltLogin", e.toString());
		}

		if(isPullToRefreshEnable){
			if(isListRefreshed){
				fetchLimit 	    = 0;
				isListRefreshed = false;
			}
			builtQueryInstance.skip(fetchLimit).limit(limit);
			queryExecuteAtFirstTime(builtQueryInstance);
		}else{
			builtQueryInstance.limit(limit);
			queryExecuteAtFirstTime(builtQueryInstance);
		}
		refreshListView();
		loadMoreOnScroll();
		loadObjectOnSearchList();
	}

	/**
	 * Callback method to be invoked when an item in this AdapterView has been clicked.
	 *  
	 * @param AdapterView.OnItemClickListener
	 * 						AdapterView.OnItemClickListener Object.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setOnItemClickListener(new OnItemClickListener() {<br>
	 * 
	 * &#160;@Override<br>
	 * &#160;public void onItemClick(AdapterView<?> parent, View view, int position,long id) {}<br>
	 * &#160;});<br>
	 * </pre>
	 */
	public void setOnItemClickListener(AdapterView.OnItemClickListener listViewItemClickListener){

		listClickListener = listViewItemClickListener;

		listview.setOnItemClickListener(listClickListener);
		searchListView.setOnItemClickListener(listClickListener);

	}

	/**
	 * Set Progress Dialog.<br>
	 * 
	 * @param progressDialog
	 * 							progressDialog Object.
	 * <br><b>Example</b><br> 
	 * <pre class="prettyprint"> 
	 * &#160;listviewproviderObject.setProgressDialog(progressDialogObject);
	 * </pre>
	 */
	public void setProgressDialog(ProgressDialog progressDialog){
		if(progressDialog != null){
			isProgressDialogEnable =  true;
			this.progressDialog = progressDialog;
		}
	}

	/*****************************************************************************************************************************************************************************
	 * 
	 ****************************** Private Methods  *************************************************************
	 * 
	 ****************************************************************************************/

	private void queryExecuteAtFirstTime(BuiltQuery builtQueryInstance){

		builtQueryInstance.exec(new QueryResultsCallBack() {

			@Override  
			public void onSuccess(QueryResult builtqueryresult) {
				datasourceBuiltObjects.clear();
				listview.onRefreshComplete();

				try{
					if(isProgressDialogEnable)
						progressDialog.dismiss();
				}catch(Exception e){
					RawAppUtils.showLog("BuiltLogin", e.toString());
				}
				int size = 0;
				if(builtqueryresult.getResultObjects() != null){
					size = builtqueryresult.getResultObjects().size();
				}

				if(size <= 0 || limit <= 0){ 
					listview.setEmptyView(emptyTextView);
					emptyTextView.setVisibility(View.VISIBLE);
					listviewResultCallBack.onAlways();
				}else if(size != 0){

					emptyTextView.setVisibility(View.GONE);
					builtObjects = builtqueryresult.getResultObjects();
					if(listviewResultCallBack != null){
						
						for(int i = 0; i < size; i++){
							datasource.add(builtObjects.get(i));
						}
						
						listview.setAdapter(datasource);
					}
				}
			}
			@Override
			public void onError(BuiltError error) {
				listview.onRefreshComplete();
				if(listviewResultCallBack != null){
					listviewResultCallBack.onError(error);
				}
				try{
					if(isProgressDialogEnable){
						progressDialog.dismiss();
					}
				}catch(Exception e){
					RawAppUtils.showLog("BuiltLogin", e.toString());
				}
			}
			@Override
			public void onAlways() {
				isLoadingStart = false;
				if(listviewResultCallBack != null){
					listviewResultCallBack.onAlways();
				}
			} 
		});
	}
	private void loadMoreOnScroll(){

		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {

				final int lastItem = firstVisibleItem + visibleItemCount;
				if(lastItem == totalItemCount) {
					if(!isLoadingStart && isAutoScrollEnable && !isListRefreshed ){
						addObjectOnScroll();
					}
				}
			}

		});
	}

	@SuppressWarnings("deprecation")
	private void refreshListView(){

		listview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { 
				isListRefreshed = true;
				listview.setRefreshingLabel(BuiltAppConstants.PULL_DOWN_REFRESH_LABEL);
				fetchLimit = 0;

				if(!isLoadingStart){ 
					isProgressDialogEnable = false;
					loadData(listviewResultCallBack);
				}			
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				listview.setRefreshingLabel(BuiltAppConstants.PULL_UP_REFRESH_LABEL);

				if(!isPullToRefreshEnable){
					addObjectOnScroll();
				}else{
					if(!isLoadingStart){
						isProgressDialogEnable = false;
						addObjectOnScroll();
					} 
				}
			}
		});
	}
	private void addObjectOnScroll(){

		isLoadingStart = true;

		if(isListRefreshed){
			isListRefreshed = false;
		}else{
			fetchLimit = limit + fetchLimit;
		}

		builtQueryInstance.skip(fetchLimit).exec(new QueryResultsCallBack() {

			@Override  
			public void onSuccess(QueryResult builtqueryresult) { 

				listview.onRefreshComplete();
				int size = 0; 

				if(builtqueryresult.getResultObjects() != null){
					size = builtqueryresult.getResultObjects().size();
				}
				if(size != 0){
					builtObjects = builtqueryresult.getResultObjects();

					if(listviewResultCallBack != null){
						
						for(int i = 0; i < size; i++){
							datasource.add(builtObjects.get(i));
						}
						int currentPosition = listview.getRefreshableView().getFirstVisiblePosition()+1;
						listview.getRefreshableView().setSelection(currentPosition);
					}
				}else{
					if(isAutoScrollEnable){
						isListRefreshed = true;
					}else{
						isListRefreshed = false;
					}
				}
			}
			@Override
			public void onError(BuiltError error) {
				listview.onRefreshComplete();
				if(listviewResultCallBack != null){
					listviewResultCallBack.onError(error);
				}
			}
			@Override
			public void onAlways() {
				isLoadingStart = false;
				if(listviewResultCallBack != null){
					listviewResultCallBack.onAlways();
				}
			}
		});
	}

	private void loadObjectOnSearchList(){
		if(isSearchViewEnable){

			final Button searchButton              = (Button)searchViewHeader.findViewById(R.id.buttonSearch);
			final Button backButton 			   = (Button)searchViewHeader.findViewById(R.id.buttonBack);

			final ImageButton closeSearchImageView = (ImageButton)searchViewHeader.findViewById(R.id.closeSearchImage);
			final EditText searchKeyEditText       = (EditText)searchViewHeader.findViewById(R.id.searchKey);
			final ImageView searchImageView        = (ImageView)searchViewHeader.findViewById(R.id.searchImage);

			searchViewHeader.setBackgroundColor(Color.parseColor(hexCode));
			
			searchKeyEditText.setHint(BuiltAppConstants.SEARCH_HINT + searchFieldUid);

			searchKeyEditText.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {

					closeSearchImageView.setEnabled(true);	
					closeSearchImageView.setVisibility(View.VISIBLE);


					searchImageView.setVisibility(View.GONE);

					searchKeyEditText.setFocusable(true);
					searchKeyEditText.requestFocus();

					searchButton.setEnabled(true);
				}
			});
			searchImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {

					closeSearchImageView.setEnabled(true);	
					closeSearchImageView.setVisibility(View.VISIBLE);

					searchImageView.setVisibility(View.GONE);

					searchKeyEditText.setFocusable(true);
					searchKeyEditText.setEnabled(true);
					searchKeyEditText.requestFocus();

					searchButton.setEnabled(true);
				}
			});

			closeSearchImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					searchKeyEditText.setText("");

					searchImageView.setVisibility(View.VISIBLE);
					closeSearchImageView.setVisibility(View.GONE);
					closeSearchImageView.setEnabled(false);
					searchKeyEditText.setEnabled(false);
					searchButton.setEnabled(false);

				}
			});

			backButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {

					backButton.setEnabled(false);
					searchButton.setEnabled(false);

					searchKeyEditText.setEnabled(false);
					searchKeyEditText.setText("");
					searchImageView.setVisibility(View.VISIBLE);

					if(serachDataSource != null){
						serachDataSource.clearAll();
					}

					listview.setVisibility(View.VISIBLE);
					searchListView.setVisibility(View.GONE);
				}
			});

			searchButton.setOnClickListener(new View.OnClickListener() {


				@Override
				public void onClick(View v) {

					backButton.setEnabled(true);

					String searchString = searchKeyEditText.getText().toString().trim();
					searchKeyEditText.setText("");
					
					searchImageView.setVisibility(View.VISIBLE);
					closeSearchImageView.setVisibility(View.GONE);
					searchKeyEditText.setEnabled(true);
					closeSearchImageView.setEnabled(false);

					listview.setVisibility(View.INVISIBLE);
					searchListView.setVisibility(View.VISIBLE);

					if(!searchString.isEmpty()){
						searchButton.setEnabled(false);
						
						if(serachDataSource != null){
							serachDataSource.clearAll();
						}

						try{
							if(!isProgressDialogEnable){
								progressDialog.show(); 
							}
						}catch(Exception e){
							RawAppUtils.showLog("BuiltLogin", e.toString());
						}
						isLoadingStart = true;
						searchBuiltQueryInstance.skip(0).where(searchFieldUid,searchString).includeOwner().exec(new QueryResultsCallBack() {

							@Override  
							public void onSuccess(QueryResult builtqueryresult) {
								searchListView.onRefreshComplete();
								int size = 0;
								try{
									if(isProgressDialogEnable){
										progressDialog.dismiss();
									}
								}catch(Exception e){
									RawAppUtils.showLog("BuiltLogin", e.toString());
								}
								
								if(builtqueryresult.getResultObjects() != null){
									size = builtqueryresult.getResultObjects().size();
								}
								
								if(size != 0){
									if(listviewResultCallBack != null){
										builtObjects = builtqueryresult.getResultObjects();
										serachDataSource = new ResultDataSource(builtListViewContext,builtObjects);
										serachDataSource.setCallBack(listviewResultCallBack);
										searchListView.setAdapter(serachDataSource);
									}
								}else{
									searchListView.setEmptyView(emptyTextView);
								}
							}
							@Override
							public void onError(BuiltError error) {
								try{
									if(isProgressDialogEnable)
										progressDialog.dismiss();
								}catch(Exception e){
									RawAppUtils.showLog("BuiltLogin", e.toString());
								}
								if(listviewResultCallBack != null){
									listviewResultCallBack.onError(error);
								}
								searchListView.onRefreshComplete();

							}

							@Override
							public void onAlways() {
								isLoadingStart = false;
								searchKeyEditText.setEnabled(false);
								if(listviewResultCallBack != null){
									listviewResultCallBack.onAlways();
								}
							} 
						});

					}else{
						searchListView.setEmptyView(emptyTextView);
					}
				}
			});
		}
	}
}


package com.raweng.built.userInterface;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.raweng.built.BuiltObject;

/**
 * 
 * @author raw engineering, Inc
 *
 */
class ResultDataSource extends ArrayAdapter<BuiltObject> {

	private BuiltListViewResultCallBack  resultlistener;
	List<BuiltObject> resultObjects;

	protected ResultDataSource(Context vContext, List<BuiltObject> objects) {
		super(vContext, objects.size(), objects);

		this.resultObjects = objects;
	}

	public int getSize(){

		return resultObjects.size();
	}

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {  

		return getCallBack().getView(position, convertView, parent, resultObjects.get(position));
	}

	@Override
	public int getViewTypeCount() {

		if(getCallBack().getViewTypeCount() <= 0){
			return super.getViewTypeCount();
		}else{
			return getCallBack().getViewTypeCount();
		}

	}

	@Override
	public int getItemViewType(int position) {
		return getCallBack().getItemViewType(position);

	}

	public void clear(){

		resultObjects.clear();
		notifyDataSetChanged();
	}

	public void clearAll(){

		if(resultObjects.size() != 0){
			resultObjects.clear();
			notifyDataSetChanged();
		}
	}

	public BuiltListViewResultCallBack getCallBack() {
		return resultlistener;
	}

	public void setCallBack(BuiltListViewResultCallBack resultlistener) {
		this.resultlistener = resultlistener;
	}
} 

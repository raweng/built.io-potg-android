package com.raweng.projectsonthego.datasource;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.raweng.built.BuiltObject;
import com.raweng.projectsonthego.R;
import com.raweng.projectsonthego.ViewHolders.CommentViewHolder;

public class CommentListDataSource extends ArrayAdapter<BuiltObject> {

	Context context;
	int resource;
	ArrayList<BuiltObject> objects;

	public CommentListDataSource(Context context, int resource,List<BuiltObject> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.objects = (ArrayList<BuiltObject>) objects;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentViewHolder viewHolder = null;
		if(convertView == null){

			LayoutInflater inflater = LayoutInflater.from(this.context);
			convertView = inflater.inflate(this.resource, parent, false);

			viewHolder            = new CommentViewHolder();
			viewHolder.comment    = (TextView) convertView.findViewById(R.id.commentTitle);

			convertView.setTag(viewHolder);
		}else{
			
			viewHolder = (CommentViewHolder) convertView.getTag();
			viewHolder.comment.setText("");

		}

		viewHolder.populateView(objects.get(position));
		return convertView;
	}


}

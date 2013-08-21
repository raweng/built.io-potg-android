package com.raweng.projectsonthego.ViewHolders;

import android.widget.TextView;

import com.raweng.built.BuiltObject;

public class CommentViewHolder {

	public TextView comment;

	public void populateView(final BuiltObject builtObject) {

		comment.setText(builtObject.getString("content"));
	}

}

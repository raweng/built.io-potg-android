package com.raweng.projectsonthego;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.built.utilities.BuiltConstant;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.datasource.CommentListDataSource;

public class UICommentScreen extends Activity{
	
	private static final String MILESTONE_CLASS_UID = "milestone";
	private static final String TASK_CLASS_UID = "task";
	private static final String BUGS_CLASS_UID = "bugs";
	private static final String COMMENT_CLASS_UID = "comment";
	private static final String MENU_TYPE = "menuType";
	private static final String MENU_UID = "menuUid";
	private static final String PROJECT_UID = "projectUid";
	private final String TAG = "UICommentScreen";
	EditText commentText;
	Button saveComment;
	ListView commentList;

	public String projectUid;
	public String menuUid;

	public boolean isTask = false;
	public boolean isMilestone = false;
	public boolean isBug = false;

	CommentListDataSource commentListDataSource;
	ProgressDialog progressDialog;
    private BuiltApplication builtApplication;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_comment_list);

        /*
         * Initialised builtApplication here.
         */
        try {
            builtApplication = Built.application(this, "API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }
		progressDialog = new ProgressDialog(UICommentScreen.this);
		progressDialog.setMessage(getString(R.string.loading_comments_list));
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		commentText = (EditText) findViewById(R.id.commentText);
		saveComment = (Button) findViewById(R.id.saveComment);
		commentList = (ListView) findViewById(R.id.commentList);
		RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.view_list_no_data, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);
		addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		commentList.setEmptyView(view);

		//Get the project uid.
		projectUid = (String) getIntent().getExtras().get(PROJECT_UID);
		//Get the uid for bug/task/milestone.
		menuUid = (String) getIntent().getExtras().get(MENU_UID);

		//Get the type(bug/task/milestone).
		int menuType =  (Integer) getIntent().getExtras().get(MENU_TYPE);

		getActionBar().setDisplayHomeAsUpEnabled(true);


		switch (menuType) {
		case AppConstant.PROJECT_BUG:
			isBug  		= true;
			isTask 		= false;
			isMilestone = false;
			break;

		case AppConstant.PROJECT_TASK:
			isBug 		= false;
			isTask 		= true;
			isMilestone = false;
			break;

		default:
			isBug 		= false;
			isTask 		= false;
			isMilestone = true;
			break;
		}
		fetchComment();

		saveComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog.setMessage(getString(R.string.making_comment));
				progressDialog.show();

				//Create a comment object.
				final BuiltObject builtObject = builtApplication.classWithUid(COMMENT_CLASS_UID).object();

				//Set values for fields.
				builtObject.set("content", commentText.getText().toString());

				//To set project reference to this comment.
				builtObject.setReference("project", projectUid);

				//To set type(bug/milestone/task) reference to this comment.
				if(isBug){
					builtObject.setReference("for_bug", menuUid);

				}else if(isTask){
					builtObject.setReference("for_task", menuUid);

				}else if(isMilestone){
					builtObject.setReference("for_milestone", menuUid);

				}


				//Save the comment object.
				builtObject.saveInBackground(new BuiltResultCallBack() {

                    @Override
                    public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {
                        if (builtError == null){
                            commentListDataSource.add(builtObject);
                            commentListDataSource.notifyDataSetChanged();
                            commentText.setText("");

                        }else {
                            AppUtils.showLog(TAG, builtError.getErrorMessage());
                        }

                        progressDialog.dismiss();
                    }
                });
			}
		});

	}

	/**
	 * Fetch comments
	 */
	private void fetchComment() {

		//Create built query object of comment class.
		final BuiltQuery query = builtApplication.classWithUid(COMMENT_CLASS_UID).query();
		if(isBug){

			//Create built query object of bugs class.
			BuiltQuery builtQueryObject = builtApplication.classWithUid(BUGS_CLASS_UID).query();

			//Fetch a particular built object using uid.
			builtQueryObject.where("uid", menuUid);

			//Fetch reference objects of type bug
			query.inQuery("for_bug", builtQueryObject);

		}else if(isTask){

			//Create built query object of task class.
			BuiltQuery builtQueryObject = builtApplication.classWithUid(TASK_CLASS_UID).query();

			//Fetch a particular built object using uid.
			builtQueryObject.where("uid", menuUid);

			//Fetch reference objects of type task
			query.inQuery("for_task", builtQueryObject);

		}else if(isMilestone){

			//Create built query object of milestone class.
			BuiltQuery builtQueryObject = builtApplication.classWithUid(MILESTONE_CLASS_UID).query();

			//Fetch a particular built object using uid.
			builtQueryObject.where("uid", menuUid);

			//Fetch reference objects of type task
			query.inQuery("for_milestone", builtQueryObject);

		}
		progressDialog.show();

		//Execute a query.
		query.execInBackground(new QueryResultsCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {
                if (builtError == null){
                    commentListDataSource = new CommentListDataSource(UICommentScreen.this, R.layout.list_row_comment, queryResult.getResultObjects());
                    commentList.setAdapter(commentListDataSource);

                }else {
                    AppUtils.showLog(TAG, builtError.getErrorMessage());
                }

                progressDialog.dismiss();
            }
        });

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//Send comment count to calling activity.
			Intent commentCountIntent = new Intent();
			commentCountIntent.putExtra("commentCount", commentListDataSource.getCount());
			setResult(Activity.RESULT_OK, commentCountIntent);
			finish(); 
			
			//Animate the activity
			overridePendingTransition( R.anim.builtio_pulltorefresh_slide_in_from_top, R.anim.builtio_pulltorefresh_slide_out_to_bottom );
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		
		//Send comment count to calling activity.
		Intent commentCountIntent = new Intent();
		commentCountIntent.putExtra("commentCount", commentListDataSource.getCount());
		setResult(Activity.RESULT_OK, commentCountIntent);
		
		
		super.onBackPressed();
		//Animate the activity
		overridePendingTransition( R.anim.builtio_pulltorefresh_slide_in_from_top, R.anim.builtio_pulltorefresh_slide_out_to_bottom );
	}
}

package com.raweng.projectsonthego;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.built.utilities.BuiltConstant;
import com.raweng.built.utilities.BuiltUtil;
import com.raweng.projectsonthego.Models.UserModel;
import com.raweng.projectsonthego.Utilities.AppConstant;
import com.raweng.projectsonthego.Utilities.AppSettings;
import com.raweng.projectsonthego.Utilities.AppUtils;
import com.raweng.projectsonthego.Utilities.FlowLayout;

/**
 * To display details of selected milestone.
 *
 * @author raw engineering,Inc
 *
 */
public class UIMilestoneDetailScreen extends Activity implements IFetchUserList{

    private final String PROJECT_UID = "projectUID";
    private final String UID = "UID";
    private final String POSITION = "position";
    private final String TAG = "UIMilestoneDetailScreen";
    private final String CLASS_UID_MILESTONE = "milestone";
    private final String CLASS_UID_COMMENT   = "comment";
    Context context;

    EditText milstoneName;
    EditText milstoneDescribtion;
    Button	 milstoneAddUser;
    TextView milstoneStartDate;
    TextView milstoneStartMonthYear;
    TextView milstoneEndDate;
    TextView milstoneEndMonthYear;
    DatePicker datepicker;

    RelativeLayout milestoneEndDateContainer;
    RelativeLayout milestoneStartDateContainer;
    FlowLayout userContainer;

    ProgressDialog progressDialog;

    public String[] assigneeUid ;
    public ArrayList<String> assigneeIdList;

    boolean isStartDateCalled = false;
    boolean isEndDateCalled   = false;

    String startDate     = null;
    String endDate       = null;
    String milestonesUid = null;
    String userType      = null;
    String projectUid    = null;
    private int position;

    private TextView commentCountTextView;
    private Button commentLabel;
    private RelativeLayout commentsContainer;
    private BuiltApplication builtApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_milestone);
        context = UIMilestoneDetailScreen.this;

        /*
         * Initialised builtApplication here.
         */
        try {
            builtApplication = Built.application(context, "API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent().getExtras() != null) {
            //provides position of list view.
            position = getIntent().getExtras().getInt(POSITION);
            //provides selected milestone uid.
            milestonesUid = getIntent().getExtras().getString(UID);
            //provides project uid inside the milestone object present.
            projectUid = getIntent().getStringExtra(PROJECT_UID);
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);

		/*Get the user type of the logged in user.
		 *user types are admin, moderator, member*/
        userType =AppSettings.getUserType(context);

        milstoneAddUser			= (Button)   findViewById(R.id.addUserButton);
        milstoneName       		= (EditText) findViewById(R.id.milestoneNameFeild);
        milstoneDescribtion 	= (EditText) findViewById(R.id.milestoneDescriptionFeild);
        milstoneStartDate		= (TextView) findViewById(R.id.mileStoneStartDate);
        milstoneStartMonthYear	= (TextView) findViewById(R.id.mileStoneStartDateMonthYear);
        milstoneEndDate			= (TextView) findViewById(R.id.mileStoneEndDate);
        milstoneEndMonthYear 	= (TextView) findViewById(R.id.mileStoneEndDateMonthYear);
        userContainer           = (FlowLayout)findViewById(R.id.userContainer);

        milestoneStartDateContainer = (RelativeLayout) findViewById(R.id.milestoneStartDateContainer);
        milestoneEndDateContainer   = (RelativeLayout) findViewById(R.id.milestoneEndDateContainer);

        commentsContainer = (RelativeLayout) findViewById(R.id.commentsContainer);
        commentsContainer.setVisibility(View.VISIBLE);
        commentLabel = (Button) findViewById(R.id.commentLabel);
        commentCountTextView = (TextView) findViewById(R.id.commentCountTextView);


        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        ArrayList<String> assignees = new ArrayList<String>();
        assignees.add("assignees");

        progressDialog.show();

        //Create a query object to fetch details of BuiltObject inside milestone class.
        final BuiltQuery builtquery = builtApplication.classWithUid(CLASS_UID_MILESTONE).query();

        //Execute the query to fetch current milestone object along with owner data and assignees data in the response.
        builtquery.includeOwner().where("uid", milestonesUid).includeReference(assignees).execInBackground(new QueryResultsCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError error) {
                if (error == null){
                    //QueryResult object return the array list of BuiltObject and its complete field information.
                    ArrayList<BuiltObject> listObject = (ArrayList<BuiltObject>) queryResult.getResultObjects();
                    int size = listObject.size();

                    for (int i = 0; i < size; i++) {
                        //Extracting information of built object.
                        extractData(listObject.get(i));
                    }

                }else {
                    Toast.makeText(context, error.getErrorCode() + "" + error.getErrorMessage(), Toast.LENGTH_LONG).show();
                    AppUtils.showLog(TAG, error.getErrorMessage());

                }

            }
        });
        milstoneAddUser.setOnClickListener(clickListener);
        milestoneStartDateContainer.setOnClickListener(clickListener);
        milestoneEndDateContainer.setOnClickListener(clickListener);
        commentLabel.setOnClickListener(clickListener);
    }
    android.view.View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if(view == milstoneAddUser){
                //Shows dialog to add a user.
                showEditDialog();

            }else if(view == milestoneStartDateContainer){

                final Dialog datedialog = new Dialog(context);
                datedialog.setTitle("Set Start Date");
                datepicker = new DatePicker(getApplicationContext());
                datepicker.setCalendarViewShown(false);
                datepicker.setBackgroundColor(Color.GRAY);
                datedialog.addContentView(datepicker, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
                datedialog.show();

                datedialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {

                        isStartDateCalled = true;

                        int countStartDate  = datepicker.getDayOfMonth();
                        int countStartMonth = datepicker.getMonth() + 1;
                        String monthName       = AppConstant.extractMonthName(countStartMonth);

                        milstoneStartMonthYear.setText(monthName+"  "+datepicker.getYear());
                        milstoneStartDate.setText(""+countStartDate+"");

                        if(countStartMonth < 10 && countStartDate > 9){
                            startDate = datepicker.getYear()+"-0"+countStartMonth+"-"+countStartDate;
                        }else if(countStartMonth > 9 && countStartDate < 10){
                            startDate = datepicker.getYear()+"-"+countStartMonth+"-0"+countStartDate;
                        }else if(countStartMonth < 10 && countStartDate < 10){
                            startDate = datepicker.getYear()+"-0"+countStartMonth+"-0"+countStartDate;
                        }else{
                            startDate = datepicker.getYear()+"-"+countStartMonth+"-"+countStartDate;
                        }
                    }
                });

            }else if(view == milestoneEndDateContainer){

                final Dialog datedialog = new Dialog(context);
                datedialog.setTitle("Set End Date");
                datepicker = new DatePicker(context);
                datepicker.setCalendarViewShown(false);
                datepicker.setBackgroundColor(Color.GRAY);
                datedialog.addContentView(datepicker, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
                datedialog.show();

                datedialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {

                        isEndDateCalled = true;

                        int countEndDate  = datepicker.getDayOfMonth();
                        int countEndMonth = datepicker.getMonth() + 1;
                        String monthName     = AppConstant.extractMonthName(countEndMonth);

                        milstoneEndMonthYear.setText(monthName+"  "+datepicker.getYear());
                        milstoneEndDate.setText(""+countEndDate+"");

                        if(countEndMonth < 10 && countEndDate > 9){
                            endDate = datepicker.getYear()+"-0"+countEndMonth+"-"+countEndDate;
                        }else if(countEndMonth > 9 && countEndDate < 10){
                            endDate = datepicker.getYear()+"-"+countEndMonth+"-0"+countEndDate;
                        }else if(countEndMonth < 10 && countEndDate < 10){
                            endDate = datepicker.getYear()+"-0"+countEndMonth+"-0"+countEndDate;
                        }else{
                            endDate = datepicker.getYear()+"-"+countEndMonth+"-"+countEndDate;
                        }
                    }
                });
            }else if( view == commentLabel){
                //Open the comment activity to make comment.
                Intent commentIntent = new Intent(context, UICommentScreen.class);
                commentIntent.putExtra("menuType", AppConstant.PROJECT_MILESTONE);
                commentIntent.putExtra("menuUid", milestonesUid);
                commentIntent.putExtra("projectUid", projectUid);
                startActivityForResult(commentIntent, AppConstant.COMMENT_COUNT_REQUEST_CODE);
                overridePendingTransition( R.anim.builtio_pulltorefresh_slide_in_from_bottom, R.anim.builtio_pulltorefresh_slide_out_to_top );
            }
        }
    };

    /**
     * This method is used to update a milestone.
     *
     */

    protected void updateMilestone() {
        progressDialog.setMessage(getResources().getString(R.string.updating_milestone));
        progressDialog.show();

        //Update a milestone object and Set a unique uid of milestone object which comes in the response of execute call of the Built Query.
        BuiltObject builtMilestoneObject = builtApplication.classWithUid(CLASS_UID_MILESTONE).object(milestonesUid);

        //Set a value for name field.
        builtMilestoneObject.set("name", milstoneName.getText().toString());

        if(milstoneDescribtion.length() > 0){
            //Set a value for description field.
            builtMilestoneObject.set("description", milstoneDescribtion.getText().toString());
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(new Date());

        //Set a value for start_date field.
        if(isStartDateCalled){
            builtMilestoneObject.set("start_date",startDate+"T"+time);
        }else{
            builtMilestoneObject.set("start_date",startDate);
        }

        //Set a value for end_date field.
        if(isEndDateCalled){
            builtMilestoneObject.set("end_date", endDate+"T"+time);
        }else{
            builtMilestoneObject.set("end_date", endDate);
        }

        //Set assignees uid's to assignees field.
        builtMilestoneObject.set("assignees", assigneeUid);

        String[] uidArray = new String[]{projectUid};
        //Set reference uid of project inside which this milestone object present.
        builtMilestoneObject.setReference("project",uidArray);

        //Update call for milestone object.
        builtMilestoneObject.saveInBackground(new BuiltResultCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError error) {
                if (error == null){
                    Toast.makeText(context, getString(R.string.milestone_updated_successfully), Toast.LENGTH_LONG).show();
                    hideKeyboard();
                    finish();

                }else {
                    Toast.makeText(context, error.getErrorCode() + " : " + error.getErrorMessage(), Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();

            }
        });

    }

    /**
     * Extract response which comes from built.io server after executing network call.
     *
     * @param builtObject
     * 				builtObject.
     */
    protected void extractData(BuiltObject builtObject) {

        try{

            //Check for name field present or not
            if (builtObject.has("name")) {

                //builtObject.get("name") return milestone name.
                milstoneName.setText(builtObject.getString("name"));
            }

            //Check for description field present or not
            if (builtObject.has("description")) {

                //builtObject.get("description") return milestone description.
                milstoneDescribtion.setText(builtObject.getString("description"));
            }

            //Check for assignees fields present or not
            if (builtObject.has("assignees")) {

                //builtObject.getAllObjects(key, classUid) used for get array list of built object inside references field.
                ArrayList<BuiltObject> assignees = builtObject.getAllObjects("assignees", milestonesUid);
                assigneeUid = new String[assignees.size()];
                assigneeIdList = new ArrayList<String>();
                for (int i = 0; i < assignees.size(); i++) {

                    //builtObject.get("uid") returns uid.
                    assigneeUid[i] =  assignees.get(i).getString("uid");
                    assigneeIdList.add( assignees.get(i).getString("uid"));

                    //builtObject.get("email") returns email.
                    String email = assignees.get(i).getString("email");

                    TextView emailIdView = new TextView(context);
                    emailIdView.setBackgroundResource(R.drawable.bubble_background);
                    emailIdView.setTextSize(getResources().getDimension(R.dimen.assignee_email_font_size));
                    emailIdView.setPadding(10, 10, 10, 10);
                    emailIdView.setText(email);
                    emailIdView.setTextColor(getResources().getColor(R.color.assignee_email_text));
                    try {
                        FlowLayout.LayoutParams flowParams = new FlowLayout.LayoutParams(BuiltUtil.convertToPixel(context, 4), BuiltUtil.convertToPixel(context, 4));
                        emailIdView.setLayoutParams(flowParams);

                    } catch (Exception e) {
                        AppUtils.showLog(TAG, e.toString());
                    }

                    userContainer.addView(emailIdView);
                }
            }
            //Check for start_date and end_date fields present or not
            if(builtObject.has("start_date") && builtObject.has("end_date")){

                //builtObject.get("start_date") returns start_date.
                String startDate = builtObject.getString("start_date");

                //builtObject.get("end_date") returns end_date.
                String endDate = builtObject.getString("end_date");

                sortDate(startDate,endDate);
            }

            fetchCommentsCount();
        }catch(Exception exe){
            AppUtils.showLog(TAG,exe.toString());
        }
    }

    /**
     * Format start date and end date.
     * @param startDate
     * 					startDates
     * @param endDate
     * 					endDates
     */
    protected void sortDate(String startDate, String endDate){

        Date startDateObject = null;
        Date endDateObject = null;
        try{

            SimpleDateFormat tibdateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            startDateObject = tibdateFormatter.parse(startDate);
            endDateObject   = tibdateFormatter.parse(endDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd");
            milstoneStartDate.setText(dateFormatter.format(startDateObject));
            milstoneEndDate.setText(dateFormatter.format(endDateObject));

            SimpleDateFormat monthYear = new SimpleDateFormat("MMM yyyy");
            milstoneStartMonthYear.setText(monthYear.format(startDateObject));
            milstoneEndMonthYear.setText(monthYear.format(endDateObject));

            this.startDate = startDate;
            this.endDate   = endDate;

        }catch(Exception e){
            AppUtils.showLog(TAG,e.toString());
        }
    }

    /**
     * This method is used to call the {@link UserListDialogFragment}.
     *
     */
    private void showEditDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        UserListDialogFragment userListDialog = new UserListDialogFragment();
        userListDialog.iFetchUserList = UIMilestoneDetailScreen.this;
        if (assigneeIdList != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("AssigneeUid",assigneeIdList);
            userListDialog.setArguments(bundle);
        }
        userListDialog.show(fragmentManager, getString(R.string.fragment_edit_name));
    }

    @Override
    public void fetchUserList(ArrayList<UserModel> objects,boolean isModeratorList) {

        assigneeUid = new String[objects.size()];
        int count = objects.size();
        String[] result = null;
        userContainer.removeAllViews();
        for(int i = 0; i < count; i++){
            assigneeUid[i] =  objects.get(i).uid;

            Set<String> temp = new LinkedHashSet<String>(Arrays.asList(assigneeUid));
            result = temp.toArray( new String[temp.size()] );
        }

        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                TextView emailIdView = new TextView(context);
                emailIdView.setBackgroundResource(R.drawable.bubble_background);
                emailIdView.setTextSize(getResources().getDimension(R.dimen.assignee_email_font_size));
                emailIdView.setPadding(10, 10, 10, 10);
                emailIdView.setText(objects.get(i).email);
                emailIdView.setTextColor(getResources().getColor(R.color.assignee_email_text));
                FlowLayout.LayoutParams flowParams = new FlowLayout.LayoutParams(4, 4);
                emailIdView.setLayoutParams(flowParams);

                userContainer.addView(emailIdView);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        MenuItem updateMenu = menu.findItem(R.id.bugaction);
        updateMenu.setTitle(R.string.update);
        updateMenu.setVisible(false);
        MenuItem commentMenu = menu.findItem(R.id.comment);
        commentMenu.setVisible(false);

        MenuItem deleteMilestone = menu.findItem(R.id.delete);

        //Verify user type for admin and moderator to allow permission for delete milestone .
        if(userType.equalsIgnoreCase(AppConstant.userRole.admin.toString()) || userType.equalsIgnoreCase(AppConstant.userRole.moderator.toString())){
            updateMenu.setVisible(true);
            deleteMilestone.setVisible(true);
        }else{
            updateMenu.setVisible(false);
            deleteMilestone.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                finish();
                return true;

            case R.id.bugaction:
                //Make Update Call.
                updateMilestone();
                return true;
            case R.id.delete:

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
                dlgAlert.setMessage(R.string.really_want_to_delete_milestone);
                dlgAlert.setTitle(R.string.delete_milestone);
                dlgAlert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage(getString(R.string.deleting_milestone));
                        progressDialog.show();

                        //Delete a milestone object.
                        BuiltObject built = builtApplication.classWithUid(CLASS_UID_MILESTONE).object(milestonesUid);

                        //Delete milestone object using destroy call.
                        built.destroyInBackground(new BuiltResultCallBack() {

                            @Override
                            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError error) {
                                if (error == null){

                                    Toast.makeText(context, getString(R.string.milestone_deleted_successfully), Toast.LENGTH_LONG).show();

                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra(POSITION, position - 1);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    hideKeyboard();
                                    finish();

                                }else {
                                    Toast.makeText(context, error.getErrorCode() + " : " + error.getErrorMessage(), Toast.LENGTH_LONG).show();
                               }

                                progressDialog.dismiss();


                            }
                        });
                    }
                });
                dlgAlert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

        }
        return false;
    }

    /**
     * Fetch comment count.
     */
    private void fetchCommentsCount(){

        progressDialog.setMessage(getString(R.string.loading_comments_count));

        //Create built query object of comment class.
        BuiltQuery query = builtApplication.classWithUid(CLASS_UID_COMMENT).query();

        //Create built query object of task class.
        BuiltQuery builtQueryObject = builtApplication.classWithUid(CLASS_UID_MILESTONE).query();

        //Fetch a particular built object using uid.
        builtQueryObject.where("uid", milestonesUid);

        //Fetch reference objects of type milestone
        query.inQuery("for_milestone", builtQueryObject);

        //Execute a query.
        query.execInBackground(new QueryResultsCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {
                if (builtError == null){
                    commentCountTextView.setText("" + queryResult.getResultObjects().size());

                }else {
                    AppUtils.showLog(TAG, builtError.getErrorMessage());

                }

                progressDialog.dismiss();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            //To fetch the updated comments count.
            case AppConstant.COMMENT_COUNT_REQUEST_CODE:{
                if (resultCode == RESULT_OK && data != null) {
                    int commentCount = data.getExtras().getInt("commentCount");
                    commentCountTextView.setText(""+commentCount);
                }
            }
            break;

            default:
                break;
        }
    }
    /**
     * hides the  keyboard for all the edit texts in this class
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(milstoneName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(milstoneDescribtion.getWindowToken(), 0);
    }
}

package com.scenekey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.feedsViewPager.HZFeedViewPagerAdapter;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.WebServices;
import com.scenekey.liveSideWork.LiveProfileActivity;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.Feeds;
import com.scenekey.model.ImagesUpload;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.verticleViewPager.HorizontalViewPager;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static UserInfo userInfo;
    private final String TAG = LiveProfileActivity.class.toString();
    //public ViewPager viewPager;
    public ArrayList<ImagesUpload> imageList;
    public HZFeedViewPagerAdapter hzFeedViewPagerAdapter;
    private CardView layout_message;
    private ImageView iv_open_msg_box;
    private Utility utility;
    private LinearLayout ly_msg_box;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private ImageView img_green, img_yellow, img_red;
    private EventAttendy eventAttendy;
    private TextView tvHomeTitle, tv_user_name, tv_send_btn, tv_bio;
    private ImageView img_profile_pic2, iv_for_status, img_back, iv_report;
    private View bottom_sheet, bottom_sheet_self_user;
    private String name = "";
    private CustomProgressBar customProgressBar;
    private RelativeLayout ly_match_profile;
    private EditText et_prvt_message;
    private String eventId;
    private RelativeLayout customizeView;
    private CardView tv_demoMassage;
    private String facebookId = "";
    private String userid = "";
    private ArrayList<Feeds> feedsList;
    private ArrayList<EventAttendy> userList;
    private Feeds feeds1;

    private TextView tv_update_btn;
    private EditText tv_bio_own;
    //HoriZontal
    private HorizontalViewPager horizontalViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_user_profile);
        inItView();
    }

    private void inItView() {
        utility = new Utility(this);
        customProgressBar = new CustomProgressBar(this);

        horizontalViewPager = findViewById(R.id.feedVpHorizontal);


        userInfo = SceneKey.sessionManager.getUserInfo();
        tvHomeTitle = findViewById(R.id.tvHomeTitle);
        img_profile_pic2 = findViewById(R.id.img_profile_pic2);
        iv_for_status = findViewById(R.id.iv_for_status);
        tv_user_name = findViewById(R.id.tv_user_name);
        img_back = findViewById(R.id.img_back);
        tv_send_btn = findViewById(R.id.tv_send_btn);
        iv_report = findViewById(R.id.iv_report);
        iv_report.setOnClickListener(this);

        //viewpagger
        //viewPager = findViewById(R.id.room_slider_pager);
        ly_match_profile = findViewById(R.id.ly_match_profile);
        et_prvt_message = findViewById(R.id.et_prvt_message);
        customizeView = findViewById(R.id.customizeView);

        img_green = findViewById(R.id.img_green);
        img_yellow = findViewById(R.id.img_yellow);
        img_red = findViewById(R.id.img_red);
        tv_bio = findViewById(R.id.tv_bio);

        tv_update_btn = findViewById(R.id.tv_update_btn);
        tv_bio_own = findViewById(R.id.tv_bio_own);
        tv_update_btn.setOnClickListener(this);

        imageList = new ArrayList<>();

        layout_message = findViewById(R.id.layout_message);
        tv_demoMassage = findViewById(R.id.tv_demoMassage);
        iv_open_msg_box = findViewById(R.id.iv_open_msg_box);
        ly_msg_box = findViewById(R.id.ly_msg_box);
        iv_open_msg_box.setOnClickListener(this);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        bottom_sheet_self_user = findViewById(R.id.bottom_sheet_self_user);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        img_back.setOnClickListener(this);
        iv_report.setOnClickListener(this);
        tv_send_btn.setOnClickListener(this);
        bottom_sheet.setOnClickListener(this);
        iv_open_msg_box.setVisibility(View.VISIBLE);

        img_green.setOnClickListener(this);
        img_yellow.setOnClickListener(this);
        img_red.setOnClickListener(this);

        bottom_sheet.setVisibility(View.VISIBLE);
        bottom_sheet_self_user.setVisibility(View.GONE);

        layout_message.setVisibility(View.GONE);
        iv_open_msg_box.setVisibility(View.VISIBLE);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int dpHeight = outMetrics.heightPixels;
        int dpWidth = outMetrics.widthPixels;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) customizeView.getLayoutParams();
        params.height = (dpWidth - 20);
        customizeView.setLayoutParams(params);

        if (getIntent().getSerializableExtra("feedsList") != null) {
            feedsList = (ArrayList<Feeds>) getIntent().getSerializableExtra("feedsList");
            userList = (ArrayList<EventAttendy>) getIntent().getSerializableExtra("userList");
            int pos = getIntent().getIntExtra("feedPostion", 0);
            eventId = getIntent().getStringExtra("eventid");
            Feeds feeds = (Feeds) getIntent().getSerializableExtra("feedsData");

            feeds1 = feedsList.get(pos);
            String facebookId = feeds1.userFacebookId;

            if (feeds1.userid != null) {

                tv_demoMassage.setVisibility(View.GONE);
                iv_open_msg_box.setVisibility(View.VISIBLE);
                iv_for_status.setVisibility(View.VISIBLE);
                img_profile_pic2.setVisibility(View.VISIBLE);

                if (feeds1.username.equalsIgnoreCase(userInfo.userName)) {
                    bottom_sheet.setVisibility(View.GONE);
                    bottom_sheet_self_user.setVisibility(View.VISIBLE);
                    iv_report.setVisibility(View.GONE);

                    switch (feeds1.user_status) {
                        case "1":
                            img_green.setImageResource(R.drawable.ic_active_grn_circle);
                            break;
                        case "2":
                            img_yellow.setImageResource(R.drawable.ic_active_ylw_circle);
                            break;
                        case "3":
                            img_red.setImageResource(R.drawable.ic_active_red_circle);
                            break;
                        default:
                            img_red.setImageResource(R.drawable.ic_active_red_circle);

                            break;
                    }
                } else {
                    bottom_sheet.setVisibility(View.VISIBLE);
                    bottom_sheet_self_user.setVisibility(View.GONE);
                    iv_report.setVisibility(View.VISIBLE);
                    et_prvt_message.setText("");
                    name = feeds1.username;

                    switch (feeds1.user_status) {
                        case "1":
                            iv_for_status.setImageResource(R.drawable.ic_active_grn_circle);
                            break;
                        case "2":
                            iv_for_status.setImageResource(R.drawable.ic_active_ylw_circle);
                            break;
                        case "3":
                            iv_for_status.setImageResource(R.drawable.ic_active_red_circle);
                            break;
                        default:
                            iv_for_status.setImageResource(R.drawable.ic_active_red_circle);
                            break;
                    }
                }

                tvHomeTitle.setText(feeds1.username);
                tv_user_name.setText(feeds1.username);
                tv_user_name.setText(feeds1.username);
                tv_bio.setText(feeds1.bio);
                name = feeds1.username;

                facebookId = feeds1.userFacebookId;
                userid = feeds1.userid;

                hzFeedViewPagerAdapter = new HZFeedViewPagerAdapter(getSupportFragmentManager());
                hzFeedViewPagerAdapter.setUserList(feedsList);
                horizontalViewPager.setAdapter(hzFeedViewPagerAdapter);
                horizontalViewPager.setCurrentItem(pos);
            }
        }

        horizontalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                feeds1 = feedsList.get(position);

                facebookId = feeds1.userFacebookId;
                userid = feeds1.userid;

                if (feeds1.username.equalsIgnoreCase(userInfo.userName)) {
                    bottom_sheet.setVisibility(View.GONE);
                    bottom_sheet_self_user.setVisibility(View.VISIBLE);
                    iv_report.setVisibility(View.GONE);

                    switch (feeds1.user_status) {
                        case "1":
                            img_green.setImageResource(R.drawable.ic_active_grn_circle);
                            break;
                        case "2":
                            img_yellow.setImageResource(R.drawable.ic_active_ylw_circle);
                            break;
                        case "3":
                            img_red.setImageResource(R.drawable.ic_active_red_circle);
                            break;
                        default:
                            img_red.setImageResource(R.drawable.ic_active_red_circle);

                            break;
                    }

                } else {

                    if (layout_message.getVisibility() == View.VISIBLE) {
                        layout_message.setVisibility(View.GONE);
                        tv_send_btn.setVisibility(View.GONE);
                        iv_open_msg_box.setVisibility(View.VISIBLE);
                    }

                    bottom_sheet.setVisibility(View.VISIBLE);
                    bottom_sheet_self_user.setVisibility(View.GONE);
                    iv_report.setVisibility(View.VISIBLE);
                    et_prvt_message.setText("");

                    switch (feeds1.user_status) {
                        case "1":
                            iv_for_status.setImageResource(R.drawable.ic_active_grn_circle);
                            break;
                        case "2":
                            iv_for_status.setImageResource(R.drawable.ic_active_ylw_circle);
                            break;
                        case "3":
                            iv_for_status.setImageResource(R.drawable.ic_active_red_circle);
                            break;
                        default:
                            iv_for_status.setImageResource(R.drawable.ic_active_red_circle);

                            break;
                    }
                }

                tvHomeTitle.setText(feeds1.username);
                tv_user_name.setText(feeds1.username);
                tv_user_name.setText(feeds1.username);
                tv_bio.setText(feeds1.bio);
                name = feeds1.username;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_back:
                onBackPressed();
                break;


            case R.id.tv_update_btn:
                if (!tv_bio_own.getText().toString().trim().isEmpty() || !tv_bio_own.getText().toString().equals("")) {
                    updateBio(tv_bio_own.getText().toString().trim());
                } else {
                    utility.showCustomPopup("Please enter bio", String.valueOf(R.font.montserrat_medium));
                }
                break;

            case R.id.img_green:
                img_red.setImageResource(R.drawable.bg_red_ring);
                img_yellow.setImageResource(R.drawable.bg_yellow_ring);
                setUserStatus(1, (ImageView) view);
                break;

            case R.id.img_yellow:
                img_green.setImageResource(R.drawable.bg_green_ring);
                img_red.setImageResource(R.drawable.bg_red_ring);
                setUserStatus(2, (ImageView) view);
                break;

            case R.id.img_red:
                img_yellow.setImageResource(R.drawable.bg_yellow_ring);
                img_green.setImageResource(R.drawable.bg_green_ring);
                setUserStatus(3, (ImageView) view);
                break;


            case R.id.tv_send_btn:

                if (et_prvt_message.getText().toString().trim().isEmpty() || et_prvt_message.getText().toString().trim().equals("")) {
                    utility.showCustomPopup("please enter message", String.valueOf(R.font.montserrat_medium));
                } else {
                    sendNudgeApi(eventAttendy);
                }
                break;

            case R.id.iv_open_msg_box:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                layout_message.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_to_position);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottom_sheet.getLayoutParams();
                layoutParams.topMargin = 0;
                ly_msg_box.setVisibility(View.VISIBLE);
                tv_send_btn.setVisibility(View.VISIBLE);
                iv_open_msg_box.setVisibility(View.GONE);
                bottom_sheet.setLayoutParams(layoutParams);
                bottom_sheet.startAnimation(animation);
                break;

            case R.id.iv_report:
                Intent intent = new Intent(this, ReportActivity.class);
                intent.putExtra("reportUser", name);
                startActivityForResult(intent, 2);
                break;
        }
    }


    private void updateBio(final String bio) {
        showProgDialog(false);
        final Utility utility = new Utility(this);

        Log.e("step12", "Pass");
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.BIO, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {

                    Log.e("step12", "Pass");
                    // get response
                    JSONObject jsonObject;
                    try {
                        dismissProgDialog();
                        // System.out.println(" login response" + response);
                        jsonObject = new JSONObject(Response);
                        int statusCode = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (statusCode == 1) {
                            UserInfo userInfo = userInfo();
                            userInfo.bio = bio;
                            updateSession(userInfo);
                            utility.showCustomPopup("Bio updated successfully", String.valueOf(R.font.montserrat_medium));
                            //  callIntent();
                            Log.e("step13", "Pass");
                        } else {
                            Utility.showToast(FeedUserProfileActivity.this, message, 0);
                            Log.e("step14", "Pass");
                        }

                    } catch (Exception ex) {
                        dismissProgDialog();
                        ex.printStackTrace();
                        Log.e("step15", "Pass");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("bio", bio);
                    params.put("user_id", userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showToast(this, getResources().getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgDialog(boolean b) {
        customProgressBar.setCanceledOnTouchOutside(b);
        customProgressBar.setCancelable(b);
        customProgressBar.show();
    }


    private void setUserStatus(int i, ImageView imageView) {

        switch (i) {
            case 1:
                imageView.setImageResource(R.drawable.ic_active_grn_circle);
                setUserStatus(i);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_active_ylw_circle);
                setUserStatus(i);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_active_red_circle);
                setUserStatus(i);
                break;

        }
    }

    private void setUserStatus(final int value) {
        final Utility utility = new Utility(this);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.SET_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.v("response", response);
                    Utility.e(TAG, response);
                    UserInfo userInfo = userInfo();
                    userInfo.user_status = String.valueOf(value);
                    updateSession(userInfo);
                    dismissProgDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                    Utility.showToast(FeedUserProfileActivity.this, getResources().getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("status", "" + value);
                    params.put("user_id", userInfo.userid);
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showToast(FeedUserProfileActivity.this, getResources().getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }


    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(FeedUserProfileActivity.this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
    }

    private void sendNudgeApi(final EventAttendy eventAttendy) {

        if (utility.checkInternetConnection()) {
            customProgressBar = new CustomProgressBar(this);
            showProgDialog(false);
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_NUDGE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    JSONObject jsonObject;
                    //{"success":1,"msg":"Nudges Submitted"}
                    try {
                        jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("msg");
                        String status = jsonObject.getString("success");
                        if (status.equals("1")) {

                            Intent intent = getIntent();
                            intent.putExtra("incresePoint", "1");
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Toast.makeText(FeedUserProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            dismissProgDialog();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                    Utility.showToast(FeedUserProfileActivity.this, getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    //params.put("event_id", userInfo.userName);
                    params.put("event_id", eventId);
                    params.put("nudges_to", feeds1.userid);
                    params.put("nudges_by", userInfo.userid);
                    params.put("facebook_id", feeds1.userFacebookId);
                    params.put("nudges", et_prvt_message.getText().toString().trim());
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(ly_match_profile, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 2 && resultCode == RESULT_OK) {

                String value = data.getStringExtra("dialog");
                if (value.equals("1")) {
                    utility.showCustomPopup(getString(R.string.report_submitted_successfully), String.valueOf(R.font.montserrat_medium));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}

package com.scenekey.liveSideWork;

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

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.scenekey.R;
import com.scenekey.activity.ReportActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.WebServices;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.ImagesUpload;
import com.scenekey.model.KeyInUserModal;
import com.scenekey.model.UserInfo;
import com.scenekey.util.CircularViewPagerHandler;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.verticleViewPager.HorizontalViewPager;
import com.scenekey.verticleViewPager.HorizontalViewPagerAdapter;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static UserInfo userInfo;
    private final String TAG = LiveProfileActivity.class.toString();
    public ArrayList<ImagesUpload> imageList;
    public HorizontalViewPager viewPager;
    public HorizontalViewPagerAdapter horizontalViewPagerAdapter;
    private CardView layout_message;
    private ImageView iv_open_msg_box;
    private Utility utility;
    private LinearLayout ly_msg_box;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private ImageView img_green, img_yellow, img_red;
    private EventAttendy eventAttendy;
    private ArrayList<EventAttendy> eventAttendyArrayList;
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
    private CognitoCredentialsProvider credentialsProvider;
    private TextView tv_own_user_name;

    private TextView tv_update_btn;
    private EditText tv_bio_own;

    private  ArrayList<KeyInUserModal>keyInUserModalArrayList;
    private  KeyInUserModal keyInUserModal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_profile);
        credentialsProvider = getCredentials();
        inItView();
    }

    private void inItView() {
        viewPager = findViewById(R.id.vpHorizontal);

        utility = new Utility(this);
        customProgressBar = new CustomProgressBar(this);
        userInfo = SceneKey.sessionManager.getUserInfo();
        tvHomeTitle = findViewById(R.id.tvHomeTitle);
        iv_for_status = findViewById(R.id.iv_for_status);
        tv_user_name = findViewById(R.id.tv_user_name);
        img_back = findViewById(R.id.img_back);
        tv_send_btn = findViewById(R.id.tv_send_btn);
        iv_report = findViewById(R.id.iv_report);
        tv_own_user_name = findViewById(R.id.tv_own_user_name);
        iv_report.setOnClickListener(this);

        //viewpagger
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
        keyInUserModalArrayList = new ArrayList<>();

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


        if (getIntent().getStringExtra("from")!=null){
            String from = getIntent().getStringExtra("from");

            switch (from){
                case "fromTheRoomActivity":
                    if (getIntent().getSerializableExtra("fromLiveRoomList") != null) {
                        eventAttendyArrayList = (ArrayList<EventAttendy>) getIntent().getSerializableExtra("fromLiveRoomList");
                        int pos = getIntent().getIntExtra("fromliveRoomadptPostion", 0);
                        eventId = getIntent().getStringExtra("eventId");
                        //String feedsid = getIntent().getStringExtra("feedsid");

                        showUI(pos);
                    }
                    break;

                case "fromTrendingHomeActivity":
                    if (getIntent().getSerializableExtra("keyInUserModalArrayList") != null) {
                        keyInUserModalArrayList = (ArrayList<KeyInUserModal>) getIntent().getSerializableExtra("keyInUserModalArrayList");
                        int pos = getIntent().getIntExtra("fromTrendingHomePostion", 0);
                        //eventId = getIntent().getStringExtra("eventId");
                        //String feedsid = getIntent().getStringExtra("feedsid");

                        showKeyInUI(pos);
                    }
                    break;


                case "fromProfileAdapter":
                    if (getIntent().getSerializableExtra("fromLiveRoomList") != null) {
                        eventAttendyArrayList = (ArrayList<EventAttendy>) getIntent().getSerializableExtra("fromLiveRoomList");
                        int pos = 0;
                        eventId = getIntent().getStringExtra("eventId");
                        String userid = getIntent().getStringExtra("feedsid");

                        int size =eventAttendyArrayList.size();
                        for (int i = 0; i <size ; i++) {
                            if (userid.equals(eventAttendyArrayList.get(i).userid)){
                                pos = i;
                                break;
                            }
                        }
                        showUI(pos);
                    }
                    break;
            }
        }
    }

    private void showUI(int pos) {
        eventAttendy = eventAttendyArrayList.get(pos);

        tv_demoMassage.setVisibility(View.GONE);
        iv_open_msg_box.setVisibility(View.VISIBLE);
        iv_for_status.setVisibility(View.VISIBLE);
        //img_profile_pic2.setVisibility(View.VISIBLE);

        if (eventAttendy.username.equals(userInfo.fullname)) {
            bottom_sheet.setVisibility(View.GONE);
            bottom_sheet_self_user.setVisibility(View.VISIBLE);
            iv_report.setVisibility(View.GONE);
            tv_bio_own.setText(eventAttendy.bio);

            switch (eventAttendy.user_status) {
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
            name = eventAttendy.username;
            if(!tv_bio.equals("")){
                tv_bio.setText(eventAttendy.bio);
            }else{
                tv_bio.setText("Demo bio");
            }

            switch (eventAttendy.user_status) {
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

        tvHomeTitle.setText(eventAttendy.username);
        tv_user_name.setText(eventAttendy.username);
        tv_own_user_name.setText(eventAttendy.username);


        name = eventAttendy.username;

        facebookId = eventAttendy.userFacebookId;
        userid = eventAttendy.userid;

        downloadFileFromS3(eventAttendy, (credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));

        horizontalViewPagerAdapter = new HorizontalViewPagerAdapter(getSupportFragmentManager());
        horizontalViewPagerAdapter = new HorizontalViewPagerAdapter(getSupportFragmentManager());
        horizontalViewPagerAdapter.setUserList(eventAttendyArrayList);
        viewPager.setAdapter(horizontalViewPagerAdapter);
        viewPager.setCurrentItem(pos);
        viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                eventAttendy = eventAttendyArrayList.get(position);

                facebookId = eventAttendy.userFacebookId;
                userid = eventAttendy.userid;

                if (eventAttendy.username.equals(userInfo.fullname)) {
                    bottom_sheet.setVisibility(View.GONE);
                    bottom_sheet_self_user.setVisibility(View.VISIBLE);
                    iv_report.setVisibility(View.GONE);
                    tv_bio_own.setText(eventAttendy.bio);

                    switch (eventAttendy.user_status) {
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

                    switch (eventAttendy.user_status) {
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

                tvHomeTitle.setText(eventAttendy.username);
                tv_user_name.setText(eventAttendy.username);
                tv_own_user_name.setText(eventAttendy.username);
                tv_bio.setText(eventAttendy.bio);
                name = eventAttendy.username;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void showKeyInUI(int pos) {
        keyInUserModal = keyInUserModalArrayList.get(pos);

        tv_demoMassage.setVisibility(View.GONE);
        iv_open_msg_box.setVisibility(View.VISIBLE);
        iv_for_status.setVisibility(View.VISIBLE);
        //img_profile_pic2.setVisibility(View.VISIBLE);

        if (keyInUserModal.userName.equals(userInfo.fullname)) {
            bottom_sheet.setVisibility(View.GONE);
            bottom_sheet_self_user.setVisibility(View.VISIBLE);
            iv_report.setVisibility(View.GONE);
            tv_bio_own.setText(keyInUserModal.bio);

            switch ("1") {
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
            name = keyInUserModal.userName;
            if(!tv_bio.equals("")){
                tv_bio.setText(keyInUserModal.bio);
            }else{
                tv_bio.setText("Demo bio");
            }

            switch ("1") {
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

        tvHomeTitle.setText(keyInUserModal.userName);
        tv_user_name.setText(keyInUserModal.userName);
        tv_own_user_name.setText(keyInUserModal.userName);


        name = keyInUserModal.userName;

        facebookId = keyInUserModal.userFacebookId;
        userid = keyInUserModal.userid;

        downloadFileFromS3(eventAttendy, (credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));

        horizontalViewPagerAdapter = new HorizontalViewPagerAdapter(getSupportFragmentManager());
        horizontalViewPagerAdapter.setUserList(eventAttendyArrayList);
        viewPager.setAdapter(horizontalViewPagerAdapter);
        viewPager.setCurrentItem(pos);
        viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                keyInUserModal = keyInUserModalArrayList.get(position);

                facebookId = keyInUserModal.userFacebookId;
                userid = keyInUserModal.userid;

                if (keyInUserModal.userName.equals(userInfo.fullname)) {
                    bottom_sheet.setVisibility(View.GONE);
                    bottom_sheet_self_user.setVisibility(View.VISIBLE);
                    iv_report.setVisibility(View.GONE);
                    tv_bio_own.setText(keyInUserModal.bio);

                    switch ("1") {
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

                    switch ("1") {
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

                tvHomeTitle.setText(keyInUserModal.userName);
                tv_user_name.setText(keyInUserModal.userName);
                tv_own_user_name.setText(keyInUserModal.userName);
                tv_bio.setText(keyInUserModal.bio);
                name = keyInUserModal.userName;
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

            case R.id.img_red:
                img_yellow.setImageResource(R.drawable.bg_yellow_ring);
                img_green.setImageResource(R.drawable.bg_green_ring);
                setUserStatus(3, (ImageView) view);
                break;

            case R.id.img_yellow:
                img_green.setImageResource(R.drawable.bg_green_ring);
                img_red.setImageResource(R.drawable.bg_red_ring);
                setUserStatus(2, (ImageView) view);
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
                            Utility.showToast(LiveProfileActivity.this, message, 0);
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
                    Utility.showToast(LiveProfileActivity.this, getResources().getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("status", value + "");
                    params.put("user_id", userInfo.userid);
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showToast(LiveProfileActivity.this, getResources().getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }


    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(LiveProfileActivity.this);
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
                            Toast.makeText(LiveProfileActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Utility.showToast(LiveProfileActivity.this, getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    //params.put("event_id", userInfo.userName);
                    params.put("event_id", eventId);
                    params.put("nudges_to", eventAttendy.userid);
                    params.put("nudges_by", userInfo.userid);
                    params.put("facebook_id", eventAttendy.userFacebookId);
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

    private void downloadFileFromS3(final EventAttendy eventAttendy, CognitoCredentialsProvider credentialsProvider) {//, CognitoCachingCredentialsProvider credentialsProvider){
        try {
            final AmazonS3Client s3Client;
            s3Client = new AmazonS3Client(credentialsProvider);

            // Set the region of your S3 bucket
            s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        ObjectListing listing;

                        if (!eventAttendy.userFacebookId.isEmpty()) {
                            listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + eventAttendy.userFacebookId);
                        } else {
                            listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + eventAttendy.userid);
                        }

                        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

                        while (listing.isTruncated()) {

                            listing = s3Client.listNextBatchOfObjects(listing);
                            summaries.addAll(listing.getObjectSummaries());
                        }

                        updateImages(summaries);

                    } catch (Exception e) {
                        dismissProgDialog();
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            Utility.e("AMAZON", e.toString());
        }
    }

    private void updateImages(final List<S3ObjectSummary> summaries) {
        Toast.makeText(this, "close", Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageList.clear();
                for (S3ObjectSummary obj : summaries) {
                    imageList.add(new ImagesUpload(obj.getKey()));
                    Log.v("imageList", "" + imageList.size());
                }
            }
        });
    }

    private CognitoCredentialsProvider getCredentials() {
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider("us-west-2:86b58a3e-0dbd-4aad-a4eb-e82b1a4ebd91", Regions.US_WEST_2);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, this);

        Map<String, String> logins = new HashMap<String, String>();

        String token = "";
        try {
            token = AccessToken.getCurrentAccessToken().getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (token != null && !token.equals("")) {
            logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
        } else {
            logins.put("graph.facebook.com", Constant.Token);
        }
        credentialsProvider.setLogins(logins);
        return credentialsProvider;
    }
}
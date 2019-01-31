package com.scenekey.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
import com.scenekey.aws_service.Aws_Web_Service;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.WebServices;
import com.scenekey.liveSideWork.NotificationPaggerSlider;
import com.scenekey.model.NotificationData;
import com.scenekey.model.UserInfo;
import com.scenekey.util.CircularViewPagerHandler;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.scenekey.activity.HomeActivity.userInfo;

public class ForNotificactionActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private final String TAG = ForNotificactionActivity.class.toString();
    NotificationPaggerSlider setViewPager;
    private ArrayList<String> getlist;
    private TextView tv_userName_not;
    private ImageView img_profile_pic2, iv_for_not_status;
    private ViewPager viewPager;
    private TextView tv_bio_notification, et_demo_message_noti, tvHomeTitle_notif;
    private View bottom_sheet;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private CardView layout_message;
    private int maxNudes;
    private RelativeLayout customizeView;
    private ArrayList<NotificationData> nudgeDataList;
    private ImageView img_back_notif;
    private ImageView iv_forwordd_notify;
    private LinearLayout ly_msg_box;
    private TextView tv_send_btn_notify;
    private EditText et_prvt_message_notifiy;
    private Utility utility;
    private String name = "";
    private NotificationData nudgeData;
    private int pos;
    private int countupdate;
    private ImageView iv_report_notify;
    private CustomProgressBar customProgressBar;
    private RelativeLayout mainView;
    private String eventId = "";
    private int notifyCount, lastPosition;
    private String nudgeCount = "";
    private int lastCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_notificaction);
        inItView();
    }

    private void inItView() {
        getlist = new ArrayList<>();
        utility = new Utility(this);
        tv_userName_not = findViewById(R.id.tv_userName_not);
        img_profile_pic2 = findViewById(R.id.img_profile_pic2);
        tv_bio_notification = findViewById(R.id.tv_bio_notification);
        et_demo_message_noti = findViewById(R.id.et_demo_message_noti);
        iv_for_not_status = findViewById(R.id.iv_for_not_status);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        layout_message = findViewById(R.id.layout_message);
        viewPager = findViewById(R.id.room_slider_pager);
        customizeView = findViewById(R.id.customizeView);
        img_back_notif = findViewById(R.id.img_back_notif);
        tvHomeTitle_notif = findViewById(R.id.tvHomeTitle_notif);
        iv_forwordd_notify = findViewById(R.id.iv_forwordd_notify);
        tv_send_btn_notify = findViewById(R.id.tv_send_btn_notify);
        et_prvt_message_notifiy = findViewById(R.id.et_prvt_message_notifiy);
        ly_msg_box = findViewById(R.id.ly_msg_box);
        iv_report_notify = findViewById(R.id.iv_report_notify);
        mainView = findViewById(R.id.mainView);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int dpHeight = outMetrics.heightPixels;
        int dpWidth = outMetrics.widthPixels;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) customizeView.getLayoutParams();
        params.height = (dpWidth - 20);
        customizeView.setLayoutParams(params);


        img_back_notif.setOnClickListener(this);
        iv_forwordd_notify.setOnClickListener(this);
        tv_send_btn_notify.setOnClickListener(this);
        iv_report_notify.setOnClickListener(this);

        nudgeDataList = new ArrayList<>();

        if (getIntent().getSerializableExtra("nudgeList") != null) {
            nudgeDataList = (ArrayList<NotificationData>) getIntent().getSerializableExtra("nudgeList");
            notifyCount = getIntent().getIntExtra("noNotify", 0);
            eventId = getIntent().getStringExtra("eventId");

            //Collections.reverse(nudgeDataList);
            nudgeCount = String.valueOf(notifyCount);
            nudgeData = nudgeDataList.get(notifyCount-1);
            //nudgeData = nudgeDataList.get(notifyCount);
            tv_userName_not.setText(nudgeData.username);
            tvHomeTitle_notif.setText(nudgeData.username);
            tv_bio_notification.setText(nudgeData.bio);
            et_demo_message_noti.setText(nudgeData.nudges);

            Log.i("notifyCount", "" + notifyCount);
            name = nudgeData.username;
            switch (nudgeData.user_status) {
                case "1":
                    iv_for_not_status.setImageResource(R.drawable.ic_active_grn_circle);
                    break;
                case "2":
                    iv_for_not_status.setImageResource(R.drawable.ic_active_ylw_circle);
                    break;
                case "3":
                    iv_for_not_status.setImageResource(R.drawable.ic_active_red_circle);
                    break;
                default:
                    iv_for_not_status.setImageResource(R.drawable.ic_active_grn_circle);
                    break;
            }
        }

        if (nudgeDataList.size() != 0) {
            setViewPager = new NotificationPaggerSlider(this, nudgeDataList);
            viewPager.setAdapter(setViewPager);
            viewPager.setCurrentItem(0);
            viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));
            viewPager.setOnPageChangeListener(ForNotificactionActivity.this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_message:
                layout_message.setVisibility(View.VISIBLE);
                break;

            case R.id.img_back_notif:
                onBackPressed();
                break;

            case R.id.iv_report_notify:
                Intent intent = new Intent(this, ReportActivity.class);
                intent.putExtra("reportUser", name);
                startActivity(intent);
                break;

            case R.id.iv_forwordd_notify:
                layout_message.setVisibility(View.VISIBLE);
                ly_msg_box.setVisibility(View.VISIBLE);
                iv_forwordd_notify.setVisibility(View.GONE);
                break;

            case R.id.tv_send_btn_notify:
                if (et_prvt_message_notifiy.getText().toString().equals("") || et_prvt_message_notifiy.getText().toString().isEmpty()) {
                    utility.showCustomPopup("Please enter message.", String.valueOf(R.font.arial_regular));
                } else {
                    sendNudgeApi(nudgeData);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        nudgeDataList.remove(lastPosition);
        intent.putExtra("nudgeDataList", nudgeDataList);
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.v("position", "" + position);

        nudgeData = nudgeDataList.get(position);
        layout_message.setVisibility(View.GONE);
        ly_msg_box.setVisibility(View.GONE);
        iv_forwordd_notify.setVisibility(View.VISIBLE);
        et_prvt_message_notifiy.setText("");

        switch (nudgeData.user_status) {
            case "1":
                iv_for_not_status.setImageResource(R.drawable.ic_active_grn_circle);
                break;
            case "2":
                iv_for_not_status.setImageResource(R.drawable.ic_active_ylw_circle);
                break;
            case "3":
                iv_for_not_status.setImageResource(R.drawable.ic_active_red_circle);
                break;
            default:
                iv_for_not_status.setImageResource(R.drawable.ic_active_red_circle);
                break;
        }

        tv_userName_not.setText(nudgeData.username);
        tvHomeTitle_notif.setText(nudgeData.username);
        tv_bio_notification.setText(nudgeData.bio);
        et_demo_message_noti.setText(nudgeData.nudges);
        name = nudgeData.username;

        lastPosition = position;
        lastCount = nudgeDataList.size();
        Log.v("lastListSize", "" +nudgeDataList.size());
        getNudges(nudgeDataList.get(position).nudgeId);
    }

    @Override
    public void onPageSelected(int position) {
        Log.v("onPageSelected", "" + position);
        if (position > 0) {
            nudgeDataList.remove(position - 1);
            setViewPager = new NotificationPaggerSlider(this, nudgeDataList);
            viewPager.setAdapter(setViewPager);
            countupdate = nudgeDataList.size();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    private void sendNudgeApi(final NotificationData nudgeData) {

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
                            nudgeDataList.remove(lastPosition);
                            intent.putExtra("nudgeDataList", nudgeDataList);
                            setResult(RESULT_OK, intent);
                            finish();

                            //incrementKeyPoints("");

                        } else {
                            Toast.makeText(ForNotificactionActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Utility.showToast(ForNotificactionActivity.this, getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    //params.put("event_id", userInfo.userName);
                    params.put("event_id", eventId);
                    params.put("nudges_to", nudgeData.user_id);
                    params.put("nudges_by", userInfo.userid);
                    params.put("facebook_id", nudgeData.facebook_id);
                    params.put("nudges", et_prvt_message_notifiy.getText().toString().trim());
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(mainView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    /***
     * For getting the nudge at notification popUp and show on it
     * @param nudgeId
     */
    private void getNudges(final String nudgeId) {
        //canGetNotification = false;

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.GET_NUDGE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.v("response", response);
                    dismissProgDialog();
                    // get response
                    try {
                        JSONObject nudgeJson = new JSONObject(response);
                        if (nudgeJson.has("success") && nudgeJson.getInt("success") == 0) {
                            // Toast.makeText(ForNotificactionActivity.this, "No nudge available", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        NotificationData nudge = new NotificationData();
                        if (nudgeJson.has("nudges"))
                            nudge.nudges = ((nudgeJson.getString("nudges")));
                        if (nudgeJson.has("user_id"))
                            nudge.user_id = (nudgeJson.getString("user_id"));
                        if (nudgeJson.has("facebook_id"))
                            nudge.facebook_id = (nudgeJson.getString("facebook_id"));
                        if (nudgeJson.has("username"))
                            nudge.username = (nudgeJson.getString("username"));
                        if (nudgeJson.has("bio"))
                            nudge.bio = (nudgeJson.getString("bio"));
                        if (nudgeJson.has("userimage"))
                            nudge.userimage = (nudgeJson.getString("userimage"));
                        if (nudge.nudges.equals(Constant.NUDGE_YOUR)) nudge.message = false;

                        nudgeDataList.add(nudge);

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    params.put("user_id", userInfo.userid);
                    params.put("event_id", eventId);
                    params.put("nudges_no", nudgeCount);
                    params.put("nudgeId", nudgeId);
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(mainView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    //TODO message on increment or decrement
    public void incrementKeyPoints(String msg) {
        final int points = Integer.parseInt(userInfo.key_points);
        new Aws_Web_Service() {
            @Override
            public okhttp3.Response onResponseUpdate(okhttp3.Response response) {
                Utility.e(TAG, "Increment response " + response);
                if (response == null) return null;
                try {
                    String s = response.body().string();
                    if (new JSONObject(s).getInt("serverStatus") == 2) {
                        Utility.e("Response", s);
                        userInfo.key_points = ((points + 1) + "");
                        showKeyPoints("+1");
                        updateSession(userInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }.updateKeyPoint(points + 1, userInfo.userid);
    }

    private void showKeyPoints(String s) {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_keypoint_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationLeftRight; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);

        TextView tvKeyPoint;

        tvKeyPoint = dialog.findViewById(R.id.tvKeyPoint);
        tvKeyPoint.setText(s);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
                dialog.dismiss();
            }
        }, 3000);

        dialog.show();
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
        try {
            //Picasso.with(this).load(userInfo.getUserImage()).placeholder(R.drawable.image_default_profile).into(img_profile);
            //userInfo.key_points
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

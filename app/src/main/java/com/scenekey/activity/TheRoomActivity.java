package com.scenekey.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;

import com.scenekey.adapter.TheKeyInUserAdapter;
import com.scenekey.adapter.TheRoomAdapter;
import com.scenekey.aws_service.Aws_Web_Service;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.KeyinUserListener;
import com.scenekey.listener.RoomListener;
import com.scenekey.liveSideWork.LiveProfileActivity;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.Events;
import com.scenekey.model.ImagesUpload;
import com.scenekey.model.KeyInUserModal;
import com.scenekey.model.UserInfo;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TheRoomActivity extends BaseActivity implements View.OnClickListener {

    public static UserInfo userInfo;
    private final String TAG = TheRoomActivity.class.toString();
    public boolean isApiM;
    public ArrayList<ImagesUpload> imageList;
    private ArrayList<EventAttendy> eventAttendyArrayList;
    private ArrayList<Card> rooDemoList;
    private ImageView img_f11_back;
    private String username;
    private ScrollView no_member_yet;
    private String eventId;
    private ArrayList<KeyInUserModal> keyInUserModalList;
    //private CognitoCredentialsProvider credentialsProvider;
    Events object;
    ImageView iv_event_detail,img_ListIcon;
    private String[] currentLatLng;
    boolean fromTrending = false;
    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.exit_to_left, R.anim.enter_from_left);
        setContentView(R.layout.activity_the_room);
        inItView();
    }

    private void inItView() {
        utility = new Utility(this);
        userInfo = SceneKey.sessionManager.getUserInfo();
        imageList = new ArrayList<>();
        no_member_yet = findViewById(R.id.no_member_yet);
        iv_event_detail = findViewById(R.id.iv_event_detail);
        img_ListIcon = findViewById(R.id.img_ListIcon);
        iv_event_detail.setOnClickListener(this);
        img_ListIcon.setOnClickListener(this);
        //downloadFileFromS3(eventAttendy, (credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));

        if (getIntent().getStringExtra("noMemberYet") != null) {
            String getValue = getIntent().getStringExtra("noMemberYet");
            if (getValue.equals("No")) {
                no_member_yet.setVisibility(View.VISIBLE);
            } else {
                no_member_yet.setVisibility(View.GONE);
            }
        }

        fromTrending = getIntent().getBooleanExtra("fromTrending",false);

        if (getIntent().getSerializableExtra("commentPesionList") != null) {
            eventAttendyArrayList = (ArrayList<EventAttendy>) getIntent().getSerializableExtra("commentPesionList");
            eventId = getIntent().getStringExtra("eventId");
        }

        if (getIntent().getSerializableExtra("demoRoomarrayList") != null) {
            rooDemoList = (ArrayList<Card>) getIntent().getSerializableExtra("demoRoomarrayList");
        }

        if (getIntent().getStringExtra("userName") != null) {
            username = getIntent().getStringExtra("userName");
        }

        if (getIntent().getSerializableExtra("fromTrendingHome") != null) {
            keyInUserModalList = (ArrayList<KeyInUserModal>) getIntent().getSerializableExtra("fromTrendingHome");

            if(keyInUserModalList.size() > 0){
                no_member_yet.setVisibility(View.GONE);
            }
            else {
                no_member_yet.setVisibility(View.VISIBLE);
            }
        }

        object = (Events) getIntent().getSerializableExtra("object");
        currentLatLng = (String[]) getIntent().getSerializableExtra("currentLatLng");
        RecyclerView theRoomRecyclerView = findViewById(R.id.theRoomRecyclerView);
        img_f11_back = findViewById(R.id.img_f11_back);
        img_f11_back.setOnClickListener(this);

        if(object != null){
            keyInToEvent();
          //  callAddEventApi(object);
            if(object.getVenue().getIs_tag_follow().equalsIgnoreCase("0"))
                tagFollowUnfollow(1,object.getVenue().getBiz_tag_id(),1);
        }

        if (eventAttendyArrayList != null) {

            TheRoomAdapter theRoomAdapter = new TheRoomAdapter(eventAttendyArrayList, TheRoomActivity.this, eventId, new RoomListener() {
                @Override
                public void getRoomData(int pos, ArrayList<EventAttendy> list, String eventId) {
                    Intent intent = new Intent(TheRoomActivity.this, LiveProfileActivity.class);
                    intent.putExtra("from", "fromTheRoomActivity");
                    intent.putExtra("fromliveRoomadptPostion", pos);
                    intent.putExtra("fromLiveRoomList", list);
                    intent.putExtra("eventId", eventId);
                    startActivityForResult(intent, 2);
                }
            });
            theRoomRecyclerView.setLayoutManager(new GridLayoutManager(TheRoomActivity.this, 3));
            theRoomRecyclerView.setAdapter(theRoomAdapter);
        }

        if (keyInUserModalList != null) {

            TheKeyInUserAdapter theKeyInUserAdapter = new TheKeyInUserAdapter(keyInUserModalList, this, new KeyinUserListener() {
                @Override
                public void getRoomData(int pos, ArrayList<KeyInUserModal> keyInUserModalArrayList) {

                    Intent intent = new Intent(TheRoomActivity.this, LiveKeyInProfileActivity.class);
                    intent.putExtra("from", "fromTrendingHomeActivity");
                    intent.putExtra("fromTrendingHomePostion", pos);
                    intent.putExtra("keyInUserModalArrayList", keyInUserModalArrayList);
                    //intent.putExtra("eventId", eventId);
                    startActivityForResult(intent, 2);


                }
            });
            theRoomRecyclerView.setLayoutManager(new GridLayoutManager(TheRoomActivity.this, 3));
            theRoomRecyclerView.setAdapter(theKeyInUserAdapter);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
                case R.id.img_f11_back:
                    onBackPressed();
                    break;

                case R.id.iv_event_detail:
                    callCheckEventStatusApi(object.getEvent().event_name, object.getEvent().event_id, object.getVenue(),object,currentLatLng
                            ,new String[]{object.getVenue().getLatitude(), object.getVenue().getLongitude()});

//                    Intent intent = new Intent(TheRoomActivity.this, EventDetailsActivity.class);
//                    intent.putExtra("event_id", object.getEvent().event_id);
//                    intent.putExtra("fromTab", "trending");
//                    intent.putExtra("venueName", object.getVenue().getVenue_name());
//                    intent.putExtra("currentLatLng", currentLatLng);
//                    intent.putExtra("event_name", object.getEvent().event_name);
//                    intent.putExtra("object", object);
//                    intent.putExtra("venueId", object.getVenue().getVenue_id());
//
//                    startActivity(intent);
                break;

                case R.id.img_ListIcon:
                    Intent intent1 = new Intent(TheRoomActivity.this, OnBoardActivity.class);
                    intent1.putExtra("eventid", object.getEvent());
                    intent1.putExtra("venuid", object.getVenue());
                    intent1.putExtra("object", object);
                    intent1.putExtra("currentLatLng", currentLatLng);
                    if(fromTrending){
                        intent1.putExtra("fromTrending", true);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        finish();
                    }
                    else {
                        startActivity(intent1);
                    }

                break;
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
                dialog.dismiss();
            }
        }, 3000);

        dialog.show();
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 2) {
                    String requiredValue = data.getStringExtra("incresePoint");
                    if (requiredValue.equals("1")) {
                        //incrementKeyPoints("");
                    }
                } else if (requestCode == 3) {
                    String requiredValue = data.getStringExtra("demoRoomIncres");
                    if (requiredValue.equals("1")) {
                        showKeyPoints("+1");
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void callCheckEventStatusApi(final String event_name, final String event_id, final Venue venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(this);
        showProgDialog(false, "");
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.CHECK_EVENT_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        dismissProgDialog();
                        jsonObject = new JSONObject(Response);

                        String status = jsonObject.getString("status");
                        boolean isKeyInAble;

                        // If Not exist then isKeyInAble is false
                        // If exist then isKeyInAble is true
                        isKeyInAble = !status.equals("not exist");

                        if (!isKeyInAble && userInfo().key_points.equals("0")) {
                            Toast.makeText(TheRoomActivity.this, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(TheRoomActivity.this, EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("fromTab", "trending");
                            intent.putExtra("venueName", venue_name.getVenue_name());
                            intent.putExtra("currentLatLng", currentLatLng);
                            intent.putExtra("event_name", event_name);
                            intent.putExtra("object", object);
                            intent.putExtra("venueId", venue_name.getVenue_id());
                            if(fromTrending){
                                intent.putExtra("fromTrending", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                startActivity(intent);
                            }

                         /*   Event_Fragment fragment = Event_Fragment.newInstance("trending");
                            fragment.setData(event_id, venue_name, object, currentLatLng, strings, isKeyInAble);
                            activity.addFragment(fragment, 0);*/
                        }

                    } catch (Exception ex) {
                        dismissProgDialog();
                        ex.printStackTrace();
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
                    params.put("eventid", event_id);
                    params.put("userid", userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(TheRoomActivity.this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(TheRoomActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

    private void callAddEventApi(final Events object) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        dismissProgDialog();
                        jsonObject = new JSONObject(Response);
                        String status = jsonObject.getString("status");

                        if (status.equals("event_Added")) {
                            showKeyPoints("+5");
                        } else if (status.equals("exist")) {

                        }

                    } catch (Exception ex) {
                        dismissProgDialog();
                        ex.printStackTrace();
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
                    params.put("userid", userInfo().userid);
                    params.put("eventname", object.getEvent().event_name);
                    params.put("eventid", object.getEvent().event_id);
                    params.put("eventdate", object.getEvent().event_time);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final int callFrom) { // 0 from search, 1 for tags long press
        utility = new Utility(TheRoomActivity.this);
        if (utility.checkInternetConnection()) {
            showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        dismissProgDialog();
                        if(jo.has("status")){

                        }

                    } catch (Exception e) {
                        dismissProgDialog();
                        Utility.showToast(TheRoomActivity.this, getString(R.string.somethingwentwrong), 0);
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
                    params.put("biz_tag_id",biz_tag_id);
                    params.put("follow_status", String.valueOf(followUnfollow));
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(TheRoomActivity.this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            dismissProgDialog();
        }
    }



    //---------------

    private void keyInToEvent(){
        try {
            if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
                addUserIntoEvent(-1);
            } else if (getDistance(new Double[]{Double.valueOf(object.getVenue().getLatitude()), Double.valueOf(object.getVenue().getLongitude()), Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE
                    &&isEventOnline(object.getEvent().event_date,userInfo().currentDate)) {
                addUserIntoEvent(-1);
            }
        } catch (Exception d) {
            d.getMessage();
        }

    }

    private boolean isEventOnline(String eventDate, String serverCurrentDate){
        boolean returnValue = false;
        eventDate = eventDate.split("TO")[0];

        eventDate = eventDate.replace("T"," ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date eventDateFinal = sdf.parse(eventDate);
            Date serverCurrentDateFinal = sdf.parse(serverCurrentDate);

            if(serverCurrentDateFinal.getTime() >= eventDateFinal.getTime()){
                returnValue = true;
            }
            else
                returnValue = false;
        } catch (ParseException e) {
            e.printStackTrace();
        }

       return returnValue;
    }

    public int getDistance(Double[] LL) {
        Utility.e("LAT LONG ", LL[0] + " " + LL[1] + " " + LL[2] + " " + LL[3]);
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(LL[0]);
        startPoint.setLongitude(LL[1]);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(LL[2]);
        endPoint.setLongitude(LL[3]);

        double distance = startPoint.distanceTo(endPoint);

        return (int) distance;
    }

    private void addUserIntoEvent(final int type) {
        if (type != -1) showProgDialog(false, TAG);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //String userExistOrNot = "no exist";
                    dismissProgDialog();
                    // get response
                    try {

                        JSONObject jo = new JSONObject(response);
                        if (jo.getInt("success") == 0) {
                            //incrementKeyPoints(getString(R.string.kp_keyin));
                        }
                        else {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgDialog();
                        Utility.showToast(TheRoomActivity.this, getString(R.string.somethingwentwrong), 0);
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

                    params.put("userid", userInfo().userid);
                    params.put("eventid", eventId);
                    params.put("eventname", object.getEvent().event_name);
                    params.put("eventdate",object.getEvent().event_time);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
           // utility.snackBar(feedLIstRecyclerView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }
}

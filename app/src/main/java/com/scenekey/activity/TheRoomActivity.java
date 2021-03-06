package com.scenekey.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
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
import com.scenekey.activity.Bottomsheet.PeopleBottomSheetDialog;
import com.scenekey.activity.trending_summery.Summary_Activity;
import com.scenekey.adapter.TheKeyInUserAdapter;
import com.scenekey.adapter.TheRoomAdapter;
import com.scenekey.aws_service.Aws_Web_Service;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.KeyinUserListener;
import com.scenekey.listener.RoomListener;
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

public class TheRoomActivity extends BaseActivity implements View.OnClickListener, PeopleBottomSheetDialog.PeopleSheetListener {

    public static UserInfo userInfo;
    private final String TAG = TheRoomActivity.class.toString();
    public boolean isApiM;
    public ArrayList<ImagesUpload> imageList;
    //private CognitoCredentialsProvider credentialsProvider;
    Events object;
    ImageView iv_event_detail, img_ListIcon;
    boolean fromTrending = false;
    Utility utility;
    TheKeyInUserAdapter theKeyInUserAdapter;
    private ArrayList<EventAttendy> eventAttendyArrayList;
    private ArrayList<Card> rooDemoList;
    private ImageView img_f11_back;
    private String username;
    private ScrollView no_member_yet;
    private String eventId;
    private ArrayList<KeyInUserModal> keyInUserModalList;
    private String[] currentLatLng;
    private ImageView img_dot;

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
        img_dot = findViewById(R.id.img_dot);
        no_member_yet = findViewById(R.id.no_member_yet);
        iv_event_detail = findViewById(R.id.iv_event_detail);
        img_ListIcon = findViewById(R.id.img_ListIcon);
        TextView txt_event_name = findViewById(R.id.txt_event_name);
        iv_event_detail.setOnClickListener(this);
        img_ListIcon.setOnClickListener(this);
        img_dot.setOnClickListener(this);
        //downloadFileFromS3(eventAttendy, (credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));

        if (getIntent().getStringExtra("noMemberYet") != null) {
            String getValue = getIntent().getStringExtra("noMemberYet");
            if (getValue.equals("No")) {
                no_member_yet.setVisibility(View.VISIBLE);
            } else {
                no_member_yet.setVisibility(View.GONE);
            }
        }

        fromTrending = getIntent().getBooleanExtra("fromTrending", false);

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

            if (keyInUserModalList.size() > 0) {
                no_member_yet.setVisibility(View.GONE);
            } else {
                no_member_yet.setVisibility(View.VISIBLE);
            }
        }

        object = (Events) getIntent().getSerializableExtra("object");
        currentLatLng = (String[]) getIntent().getSerializableExtra("currentLatLng");
        RecyclerView theRoomRecyclerView = findViewById(R.id.theRoomRecyclerView);
        img_f11_back = findViewById(R.id.img_f11_back);
        img_f11_back.setOnClickListener(this);
        txt_event_name.setText(object.getEvent().event_name);

        if (object != null) {
            keyInToEvent();
            if (object.getVenue().getIs_tag_follow().equalsIgnoreCase("0"))
                tagFollowUnfollow(1, object.getVenue().getBiz_tag_id(), 1);
        }

        if (eventAttendyArrayList != null) {

            TheRoomAdapter theRoomAdapter = new TheRoomAdapter(eventAttendyArrayList, TheRoomActivity.this, eventId, new RoomListener() {
                @Override
                public void getRoomData(int pos, ArrayList<EventAttendy> list, String eventId) {
//                    Intent intent = new Intent(TheRoomActivity.this, LiveProfileActivity.class);
//                    intent.putExtra("from", "fromTheRoomActivity");
//                    intent.putExtra("fromliveRoomadptPostion", pos);
//                    intent.putExtra("fromLiveRoomList", list);
//                    intent.putExtra("eventId", eventId);
//                    startActivityForResult(intent, 2);
                }
            });
            theRoomRecyclerView.setLayoutManager(new GridLayoutManager(TheRoomActivity.this, 3));
            theRoomRecyclerView.setAdapter(theRoomAdapter);
        }

        if (keyInUserModalList != null) {

            theKeyInUserAdapter = new TheKeyInUserAdapter(keyInUserModalList, this, new KeyinUserListener() {
                @Override
                public void getRoomData(int pos, ArrayList<KeyInUserModal> keyInUserModalArrayList) {


                    if (keyInUserModalList.get(pos).keyIn != null && keyInUserModalList.get(pos).keyIn.equals("0") && !userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {

                        utility.showCustomPopup("Sorry! you must be at this venue to see profiles", String.valueOf(R.font.montserrat_medium));

                    } else {
                        if (keyInUserModalArrayList.get(pos).userid.equalsIgnoreCase(userInfo().userid)) {
                            startActivity(new Intent(TheRoomActivity.this, OwnProfileActivity.class));

                        } else {
                            KeyInUserModal keyInUserModal = new KeyInUserModal();
                            keyInUserModal.userid = keyInUserModalArrayList.get(pos).userid;
                            keyInUserModal.userImage = keyInUserModalArrayList.get(pos).userImage;
                            keyInUserModal.bio = keyInUserModalArrayList.get(pos).bio;
                            keyInUserModal.userName = keyInUserModalArrayList.get(pos).userName;
                            keyInUserModal.userFacebookId = keyInUserModalArrayList.get(pos).userFacebookId;
                            keyInUserModal.keyIn = keyInUserModalArrayList.get(pos).keyIn;

                            Intent intent = new Intent(TheRoomActivity.this, ProfileOtherUserNewActivity.class);
                            intent.putExtra("from", "fromRoom");
                            intent.putExtra("fromTrendingHomePostion", pos);
                            intent.putExtra("from", "fromProfileAdapter");
                            intent.putExtra("keyInUserModal", keyInUserModal);
                            startActivity(intent);
                        }

                    }
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
            case R.id.img_dot:
                PeopleBottomSheetDialog bottomSheet = new PeopleBottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "BoardBottomSheet");
                break;

            case R.id.iv_event_detail:
                callCheckEventStatusApi(object.getEvent().event_name, object.getEvent().event_id, object.getVenue(), object, currentLatLng
                        , new String[]{object.getVenue().getLatitude(), object.getVenue().getLongitude()});

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
                if (fromTrending) {
                    intent1.putExtra("fromTrending", true);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    finish();
                } else {
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
                        showKeyPoints("+1",1);
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

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
    }

    @Override
    public void onBackPressed() {
        if (SceneKey.sessionManager.getBackOrIntent() && SceneKey.sessionManager.getMapFragment().equalsIgnoreCase("trending")) {
            Intent intent = new Intent(TheRoomActivity.this, HomeActivity.class);
            intent.putExtra("fromSearch1", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else if (SceneKey.sessionManager.getMapFragment().equalsIgnoreCase("map")){
            Intent intent = new Intent(TheRoomActivity.this, HomeActivity.class);
            intent.putExtra("fromSearch2", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else {
            super.onBackPressed();
        }

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
                        showKeyPoints("+1",1);
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
                            if (fromTrending) {
                                intent.putExtra("fromTrending", true);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
//                                finish();
                            } else {
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
            Utility.showCheckConnPopup(TheRoomActivity.this,"No network connection","","");
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
                        String success = jsonObject.getString("success");

                        KeyInUserModal keyInUserModal = new KeyInUserModal();
                        if (success.equalsIgnoreCase("1") || success.equalsIgnoreCase("0")) {
                            if (status.equals("event_Added")) {
                                showKeyPoints("+3",3);

                            } else if (status.equals("exist")) {
                                keyInUserModal.keyIn = "1";
                            }

                            keyInUserModal.userid = userInfo().userid;
                            keyInUserModal.userImage = userInfo().userImage;
                            keyInUserModal.userName = userInfo().userName;
                            keyInUserModal.bio = userInfo().bio;
                            keyInUserModal.userFacebookId = userInfo().userFacebookId;

                            boolean bValue = true;
                            if (keyInUserModalList != null) {
                                for (int i = 0; i < keyInUserModalList.size(); i++) {
                                    if (keyInUserModalList.get(i).userid.equalsIgnoreCase(keyInUserModal.userid)) {
                                        bValue = false;
                                        if (!keyInUserModalList.get(i).userid.equalsIgnoreCase(SceneKey.sessionManager.getUserInfo().userid)) {
                                            keyInUserModalList.get(i).keyIn = "0";
                                        }

                                    }
                                    else {
                                        keyInUserModalList.get(i).keyIn = "1";

                                    }
                                }
                            }
                            if (bValue) {
                                if (keyInUserModalList != null) {
                                    keyInUserModalList.add(keyInUserModal);
                                }

                                no_member_yet.setVisibility(View.GONE);
                                if (theKeyInUserAdapter != null)
                                    theKeyInUserAdapter.notifyDataSetChanged();
                            }

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
                    params.put("big_tag_id", object.getVenue().getBiz_tag_id());
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(TheRoomActivity.this,"No network connection","","");
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
                        if (jo.has("status")) {

                        }

                    } catch (Exception e) {
                        dismissProgDialog();
//                        Utility.showToast(TheRoomActivity.this, getString(R.string.somethingwentwrong), 0);
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
                    params.put("biz_tag_id", biz_tag_id);
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

    private void showKeyPoints(String s,int value) {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_keypoint_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);

        TextView tvKeyPoint;

        tvKeyPoint = dialog.findViewById(R.id.tvKeyPoint);
        tvKeyPoint.setText(s);
        userInfo().key_points = String.valueOf(Integer.parseInt(userInfo().key_points) + value);


        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                50);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        ((ViewGroup) dialog.getWindow().getDecorView()).getChildAt(0).startAnimation(animate);
        Handler handler = new Handler();

        handler.postDelayed((Runnable) () -> {
            TranslateAnimation animate1 = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    50,  // fromYDelta
                    0);                // toYDelta
            animate1.setDuration(500);
            animate1.setFillAfter(true);
            ((ViewGroup) dialog.getWindow().getDecorView()).getChildAt(0).startAnimation(animate1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAlertDialog("You've keyed into " + object.getEvent().event_name);
                    dialog.dismiss();
                }
            }, 500);


        }, 1400);


        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            //use a log message
        }

    }

    private void keyInToEvent() {
        try {
            if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
                callAddEventApi(object);
            } else if (object.getEvent().ableToKeyIn) {
                callAddEventApi(object);
            }
            else{
                if (keyInUserModalList != null) {
                    for (int i = 0; i < keyInUserModalList.size(); i++) {
                                keyInUserModalList.get(i).keyIn = "0";
                            }

                }
            }
        } catch (Exception d) {
            d.getMessage();
        }

    }

    private boolean isEventOnline(String eventDate, String serverCurrentDate) {
        boolean returnValue = false;
        eventDate = eventDate.split("TO")[0];

        eventDate = eventDate.replace("T", " ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date eventDateFinal = sdf.parse(eventDate);
            Date serverCurrentDateFinal = sdf.parse(serverCurrentDate);

            if (serverCurrentDateFinal.getTime() >= eventDateFinal.getTime()) {
                returnValue = true;
            } else
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
                        } else {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgDialog();
//                        Utility.showToast(TheRoomActivity.this, getString(R.string.somethingwentwrong), 0);
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
                    params.put("eventdate", object.getEvent().event_time);
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

    @Override
    public void onButtonClicked(String text) {

        switch (text) {
            case "board": {
                Intent intent1 = new Intent(TheRoomActivity.this, OnBoardActivity.class);
                intent1.putExtra("eventid", object.getEvent());
                intent1.putExtra("venuid", object.getVenue());
                intent1.putExtra("object", object);
                intent1.putExtra("currentLatLng", currentLatLng);
                if (fromTrending) {
                    intent1.putExtra("fromTrending", true);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
//                    finish();
                } else {
                    startActivity(intent1);
                }

            }

            break;
            case "summery": {
                Intent intent = new Intent(TheRoomActivity.this, Summary_Activity.class);
                intent.putExtra("event_id", object.getEvent().event_id);
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                if (fromTrending) {
                    intent.putExtra("fromTrending", true);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    finish();
                } else {
                    startActivity(intent);
                }

            }
            break;

            case "post": {
                callCheckEventStatusApi(object.getEvent().event_name, object.getEvent().event_id, object.getVenue(), object, currentLatLng
                        , new String[]{object.getVenue().getLatitude(), object.getVenue().getLongitude()});


            }
            break;

        }
    }
}

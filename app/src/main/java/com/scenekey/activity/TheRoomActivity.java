package com.scenekey.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import com.amazonaws.auth.CognitoCredentialsProvider;
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
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.KeyinUserListener;
import com.scenekey.listener.RoomDemoListener;
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
import java.util.ArrayList;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left, R.anim.fab_slide_out_to_left);
        setContentView(R.layout.activity_the_room);
        inItView();
    }

    private void inItView() {
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
                        incrementKeyPoints("");
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
}

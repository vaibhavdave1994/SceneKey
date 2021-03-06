package com.scenekey.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.Bottomsheet.BoardBottomSheetDialog;
import com.scenekey.activity.trending_summery.Summary_Activity;
import com.scenekey.adapter.VenueBoardAdapter;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.WebServices;
import com.scenekey.model.Event;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.Events;
import com.scenekey.model.UserInfo;
import com.scenekey.model.Venue;
import com.scenekey.model.VenueBoard;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OnBoardActivity extends BaseActivity implements View.OnClickListener, BoardBottomSheetDialog.BoardSheetListener {

    public Events object;
    Event event;
    Venue venue;
    RelativeLayout venuName;
    boolean fromTrending = false;
    boolean fromAlert = false;
    RelativeLayout lnrLeft, comeInUser_lnr;
    String venuid = "";
    String frequency = "";
    private ArrayList<EventAttendy> attendyList;
    private TextView onBoard_txt_event_name;
    private ImageView img_eventDetail_back;
    private Utility utility;
    private RelativeLayout container;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardList;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardListSpecial;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardListHappyHour;
    private ArrayList<VenueBoard.EventTagBean> venuBoardEventTagBeanList;
    private VenueBoardAdapter venueBoardAdapter;
    private RecyclerView venuRecyclerView;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardCatList;
    private String[] currentLatLng;
    private ImageView iv_tag__special_image, img_no_member, iv_group;
    private TextView tag__vanue_name;
    private ArrayList<Events> eventsArrayList;
    private ImageView img_dot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        inItView();
    }

    private void inItView() {
        utility = new Utility(OnBoardActivity.this);
        venuBoardList = new ArrayList<>();
        venuBoardCatList = new ArrayList<>();

        img_dot = findViewById(R.id.img_dot);
        container = findViewById(R.id.container);
        onBoard_txt_event_name = findViewById(R.id.onBoard_txt_event_name);
        img_eventDetail_back = findViewById(R.id.img_eventDetail_back);
        venuRecyclerView = findViewById(R.id.venuRecyclerView);
        img_no_member = findViewById(R.id.img_no_member);
        venuName = findViewById(R.id.venuName);
        venuName.setVisibility(View.GONE);
        iv_group = findViewById(R.id.iv_group);
        img_no_member.setOnClickListener(this);
        iv_group.setOnClickListener(this);
        iv_tag__special_image = findViewById(R.id.iv_tag__special_image);
        tag__vanue_name = findViewById(R.id.tag__vanue_name);
        lnrLeft = findViewById(R.id.lnrLeft);
        comeInUser_lnr = findViewById(R.id.comeInUser_lnr);

//        if (getIntent().getSerializableExtra("event") != null) {
//           Event event = (Event) getIntent().getSerializableExtra("event");
//            //onBoard_txt_event_name.setText("" + event.event_name);
//        }

        img_eventDetail_back.setOnClickListener(this);
        img_dot.setOnClickListener(this);

        fromAlert = getIntent().getBooleanExtra("fromAlert", false);
        if (fromAlert) {
            venuid = getIntent().getStringExtra("venuid");
            frequency = getIntent().getStringExtra("frequency");
            String event_name = getIntent().getStringExtra("event_name");
            String week = Utility.checkWeek();
            if (frequency.contains(",")) {
                String[] animalsArray = frequency.split(",");
                for (String s : animalsArray) {
                    if (week.equals(s)) {
                        frequency = s;
                    }

                }
            }


            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            if (frequency.equalsIgnoreCase(date) || frequency.equalsIgnoreCase("daily") || week.equalsIgnoreCase(frequency)) {
                img_dot.setVisibility(View.VISIBLE);

            } else {
                img_dot.setVisibility(View.GONE);
            }

            getDataViaAlert(frequency, venuid);


        }

        if (getIntent().getSerializableExtra("eventid") != null) {
            Event event = (Event) getIntent().getSerializableExtra("eventid");
            this.event = event;
            Venue venuid = (Venue) getIntent().getSerializableExtra("venuid");
            venue = venuid;
            object = (Events) getIntent().getSerializableExtra("object");
            currentLatLng = (String[]) getIntent().getSerializableExtra("currentLatLng");
            if (currentLatLng == null) {
                currentLatLng = new String[]{userInfo().lat, userInfo().longi};
            }
            if (object != null)
                keyInToEvent();

            onBoard_txt_event_name.setText(event.event_name);
            getSearchTagList(event.event_id, venuid.getVenue_id());

            if (object != null) {
                if (object.getVenue().getIs_tag_follow().equalsIgnoreCase("0"))
                    tagFollowUnfollow(1, object.getVenue().getBiz_tag_id(), 1);
            }
        } else {
            String event_name = getIntent().getStringExtra("event_name");
            onBoard_txt_event_name.setText(event_name);
        }

        fromTrending = getIntent().getBooleanExtra("fromTrending", false);

        venuBoardEventTagBeanList = new ArrayList<>();

        getAllData();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_eventDetail_back:
                onBackPressed();
                break;


            case R.id.img_dot: {
                BoardBottomSheetDialog bottomSheet = new BoardBottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "BoardBottomSheet");
            }
            break;

            case R.id.iv_group:
                Intent intent = new Intent(OnBoardActivity.this, TheRoomActivity.class);
                intent.putExtra("fromTrendingHome", event.keyInUserModalList);
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                if (fromTrending) {
                    intent.putExtra("fromTrending", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(intent);
                }

                break;

            case R.id.img_no_member:
                callCheckEventStatusApi(event.event_name, event.event_id, venue, object, currentLatLng
                        , new String[]{venue.getLatitude(), venue.getLongitude()});
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (SceneKey.sessionManager.getBackOrIntent() && SceneKey.sessionManager.getMapFragment().equalsIgnoreCase("trending")) {
            Intent intent = new Intent(OnBoardActivity.this, HomeActivity.class);
            intent.putExtra("fromSearch1", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (SceneKey.sessionManager.getMapFragment().equalsIgnoreCase("map")) {
            Intent intent = new Intent(OnBoardActivity.this, HomeActivity.class);
            intent.putExtra("fromSearch2", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    //-------from alert---------
    private void getDataViaAlert(final String frequency, final String venue_id) {

        if (utility.checkInternetConnection()) {

            StringRequest request = new StringRequest(Request.Method.POST, WebServices.VENUEBOARD, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    setLoading(false);
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        String status = jo.getString("status");
                        String event_name = jo.getString("event_name");
                        onBoard_txt_event_name.setText(event_name);
                        if (status.equals("success")) {

                            venuBoardEventTagBeanList = new ArrayList<>();
                            JSONArray eventTag = jo.getJSONArray("eventTag");
                            VenueBoard.EventTagBean eventTagBean;
                            VenueBoard.EventTagBean eventTagBeanSpecial = null;
                            VenueBoard.EventTagBean eventTagBeanHappyHour = null;
                            for (int i = 0; i < eventTag.length(); i++) {

                                JSONObject jsonObject1 = eventTag.getJSONObject(i);
                                JSONArray tagList = jsonObject1.getJSONArray("tagList");
                                if (tagList.length() > 0) {

                                    if (jsonObject1.getString("category_name").equalsIgnoreCase("Specials")) {
                                        eventTagBeanSpecial = new VenueBoard.EventTagBean();
                                        eventTagBeanSpecial.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBeanSpecial.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBeanSpecial.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBeanSpecial.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBeanSpecial.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardListSpecial = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardListSpecial.add(tagListBean);
                                        }
                                        eventTagBeanSpecial.setTagList(venuBoardListSpecial);
                                    } else if (jsonObject1.getString("category_name").equalsIgnoreCase("Happy Hour")) {
                                        eventTagBeanHappyHour = new VenueBoard.EventTagBean();
                                        eventTagBeanHappyHour.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBeanHappyHour.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBeanHappyHour.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBeanHappyHour.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBeanHappyHour.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardListHappyHour = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardListHappyHour.add(tagListBean);
                                        }
                                        eventTagBeanHappyHour.setTagList(venuBoardListHappyHour);
                                    } else {
                                        eventTagBean = new VenueBoard.EventTagBean();
                                        eventTagBean.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBean.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBean.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBean.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBean.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardList = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardList.add(tagListBean);
                                        }
                                        eventTagBean.setTagList(venuBoardList);
                                        venuBoardEventTagBeanList.add(eventTagBean);
                                    }
                                }
                            }

                            if (eventTagBeanHappyHour != null) {
                                venuBoardEventTagBeanList.add(eventTagBeanHappyHour);
                            }

                            if (eventTagBeanSpecial != null) {
                                venuBoardEventTagBeanList.add(eventTagBeanSpecial);
                            }
                            //venueBoardAdapter.notifyDataSetChanged();
                            venueBoardAdapter = new VenueBoardAdapter(OnBoardActivity.this, venuBoardEventTagBeanList, fromTrending);
                            venuRecyclerView.setAdapter(venueBoardAdapter);

                            //-------------eventdata-----------------
                            JSONObject jsonEventData = jo.getJSONObject("eventData");
                            if (jsonEventData.has("events")) {
                                if (eventsArrayList == null) eventsArrayList = new ArrayList<>();
                                else eventsArrayList.clear();
                                JSONArray eventAr = jsonEventData.getJSONArray("events");
                                for (int i = 0; i < eventAr.length(); i++) {
                                    JSONObject jobject = eventAr.getJSONObject(i);
                                    Events events = new Events();
                                    if (jobject.has("venue"))
                                        events.setVenueJSON(jobject.getJSONObject("venue"));
                                    if (jobject.has("artists"))
                                        events.setArtistsArray(jobject.getJSONArray("artists"));
                                    if (jobject.has("events"))
                                        events.setEventJson(jobject.getJSONObject("events"));
                                    try {
                                        events.setOngoing(events.checkWithTime(events.getEvent().event_date, events.getEvent().interval));

                                        // New Code
                                        checkWithDate(events.getEvent().event_date, events.getEvent().rating, events);
                                    } catch (Exception e) {
                                        Utility.e("Date exception", e.toString());
                                    }

                                    try {
                                        int time_format = 0;
                                        try {
                                            time_format = Settings.System.getInt(getContentResolver(), Settings.System.TIME_12_24);
                                        } catch (Settings.SettingNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                        events.settimeFormat(time_format);
                                    } catch (Exception e) {
                                        Utility.e("Exception time", e.toString());
                                    }
                                    try {
                                        events.setRemainingTime();
                                    } catch (Exception e) {
                                        Utility.e("Exception Remaining", e.toString());
                                    }
                                    eventsArrayList.add(events);

                                    if (i == 0) {
                                        event = events.getEvent();
                                        venue = events.getVenue();
                                        object = events;
                                        forAlertEventApi(object.getEvent().event_name, object.getEvent().event_id, object.getEvent().event_time);
                                    }
                                }

                            }
                            getAllData();
                        }

                    } catch (Exception e) {
                        setLoading(false);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    setLoading(false);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("frequency", frequency);
                    params.put("venue_id", venue_id);
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    params.put("lat", userInfo().lat);
                    params.put("long", userInfo().longi);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
//            utility.snackBar(container, getString(R.string.internetConnectivityError), 0);
        }
    }

    public void checkWithDate(final String startDate, final String rating, Events events) {  //2018-11-12 18:00:00TO08:00:00

        String[] dateSplit = (startDate.replace("TO", "T")).replace(" ", "T").split("T");
        try {
            Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).parse(dateSplit[0] + " " + dateSplit[1]);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            UserInfo userInfo = SceneKey.sessionManager.getUserInfo();
            Date date = df.parse(userInfo.currentDate);
            String formattedDate = df.format(date);
            Date curTime = df.parse(formattedDate);
            getDayDifference(startTime, curTime, rating, events);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //********** day diffrence  ****************//
    public void getDayDifference(Date startDate, Date endDate, String rating, Events events) {
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (different > 0) {
            if (elapsedHours > 0) {
                events.getEvent().returnDay = elapsedHours + "hr";
                events.getEvent().strStatus = 2;
            } else if (elapsedMinutes > 0) {
                events.getEvent().returnDay = elapsedMinutes + "mins";
                events.getEvent().strStatus = 2;
            } else if (elapsedSeconds > 0) {
                events.getEvent().returnDay = elapsedSeconds + "secs";
                //strStatus = 2;
                events.getEvent().strStatus = 2;
            } else {
                if (rating.equals("0")) {
                    events.getEvent().returnDay = "--";
                    //strStatus = 0;
                    events.getEvent().strStatus = 0;
                } else {
                    //strStatus = 1;
                    events.getEvent().strStatus = 1;
                    events.getEvent().returnDay = rating;
                }
            }
        } else {
            if (rating.equals("0")) {
                events.getEvent().returnDay = "--";
                //strStatus = 0;
                events.getEvent().strStatus = 0;
            } else {
                events.getEvent().strStatus = 1;
                events.getEvent().returnDay = rating;
            }
        }
    }

    private void getSearchTagList(final String event_id, final String venue_id) {

        if (utility.checkInternetConnection()) {
//            setLoading(true);
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.VENUEBOARD_EVENT_TAG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    setLoading(false);
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        String status = jo.getString("status");
                        if (status.equals("success")) {
                            venuBoardEventTagBeanList = new ArrayList<>();
                            JSONArray eventTag = jo.getJSONArray("eventTag");
                            VenueBoard.EventTagBean eventTagBean;
                            VenueBoard.EventTagBean eventTagBeanSpecial = null;
                            VenueBoard.EventTagBean eventTagBeanHappyHour = null;
                            for (int i = 0; i < eventTag.length(); i++) {

                                JSONObject jsonObject1 = eventTag.getJSONObject(i);
                                JSONArray tagList = jsonObject1.getJSONArray("tagList");
                                if (tagList.length() > 0) {

                                    if (jsonObject1.getString("category_name").equalsIgnoreCase("Specials")) {
                                        eventTagBeanSpecial = new VenueBoard.EventTagBean();
                                        eventTagBeanSpecial.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBeanSpecial.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBeanSpecial.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBeanSpecial.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBeanSpecial.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardListSpecial = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardListSpecial.add(tagListBean);
                                        }
                                        eventTagBeanSpecial.setTagList(venuBoardListSpecial);
                                    } else if (jsonObject1.getString("category_name").equalsIgnoreCase("Happy Hour")) {
                                        eventTagBeanHappyHour = new VenueBoard.EventTagBean();
                                        eventTagBeanHappyHour.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBeanHappyHour.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBeanHappyHour.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBeanHappyHour.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBeanHappyHour.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardListHappyHour = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardListHappyHour.add(tagListBean);
                                        }
                                        eventTagBeanHappyHour.setTagList(venuBoardListHappyHour);
                                    } else {
                                        eventTagBean = new VenueBoard.EventTagBean();
                                        eventTagBean.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBean.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBean.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBean.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBean.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardList = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardList.add(tagListBean);
                                        }
                                        eventTagBean.setTagList(venuBoardList);
                                        venuBoardEventTagBeanList.add(eventTagBean);
                                    }
                                }
                            }

                            if (eventTagBeanHappyHour != null) {
                                venuBoardEventTagBeanList.add(eventTagBeanHappyHour);
                            }

                            if (eventTagBeanSpecial != null) {
                                venuBoardEventTagBeanList.add(eventTagBeanSpecial);
                            }
                            //venueBoardAdapter.notifyDataSetChanged();
                            venueBoardAdapter = new VenueBoardAdapter(OnBoardActivity.this, venuBoardEventTagBeanList, fromTrending);
                            venuRecyclerView.setAdapter(venueBoardAdapter);
                        }

                    } catch (Exception e) {
                        setLoading(false);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    setLoading(false);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("event_id", event_id);
                    params.put("venue_id", venue_id);
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
//            utility.snackBar(container, getString(R.string.internetConnectivityError), 0);
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
                            Toast.makeText(OnBoardActivity.this, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(OnBoardActivity.this, EventDetailsActivity.class);
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
            VolleySingleton.getInstance(OnBoardActivity.this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
            Toast.makeText(OnBoardActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

    /**
     * GetALl the data for that event
     */
    public void getAllData() {

        // showProgDialog(false,"");
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LISTEVENTFEED, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.e("Responce129", response);
                    // get response
                    try {
                        if (response != null) getResponse(response);
//                        else
//                            Utility.showToast(OnBoardActivity.this, getString(R.string.somethingwentwrong), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgDialog();
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

                    params.put("event_id", event.event_id);
                    params.put("user_id", userInfo().userid);

                    // Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            //utility.snackBar(feedLIstRecyclerView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    /**
     * @param response the responce provided by getAlldata()
     * @throws JSONException
     */
    private void getResponse(String response) throws Exception {
        JSONObject obj1 = new JSONObject(response);
        dismissProgDialog();
        try {
            if (obj1.has("eventattendy")) {
                Object objectType = obj1.get("eventattendy");

                if (objectType instanceof String) {
                    iv_group.setVisibility(View.VISIBLE);
                } else if (objectType instanceof JSONArray) {
                    setAttendyJson(obj1.getJSONArray("eventattendy"));
                    iv_group.setVisibility(View.GONE);

                }
            }

            if (fromAlert)
                manageFrequency(frequency);

        } catch (JSONException e) {
            e.printStackTrace();
            if (fromAlert)
                manageFrequency(frequency);
        }

    }

    public void setAttendyJson(JSONArray Json) throws JSONException {
        if (attendyList != null) attendyList.clear();
        if (attendyList == null) attendyList = new ArrayList<>();
        for (int i = 0; i < Json.length(); i++) {
            EventAttendy attendy = new EventAttendy();
            JSONObject attendyJosn = Json.getJSONObject(i);
            if (attendyJosn.has("username"))
                attendy.username = (attendyJosn.getString("username"));
            if (attendyJosn.has("userFacebookId"))
                attendy.userFacebookId = (attendyJosn.getString("userFacebookId"));
            if (attendyJosn.has("userid")) attendy.userid = (attendyJosn.getString("userid"));
            if (attendyJosn.has("user_status"))
                attendy.user_status = (attendyJosn.getString("user_status"));
            if (attendyJosn.has("usertype"))
                attendy.usertype = (attendyJosn.getString("usertype"));
            if (attendyJosn.has("rating")) attendy.rating = (attendyJosn.getInt("rating") + "");
            if (attendyJosn.has("stagename"))
                attendy.stagename = (attendyJosn.getString("stagename"));
            if (attendyJosn.has("bio"))
                attendy.bio = (attendyJosn.getString("bio"));
            if (attendyJosn.has("userimage"))
                attendy.setUserimage(attendyJosn.getString("userimage"));
            attendyList.add(attendy);
        }
        setRecyclerView(attendyList);
    }

    /***
     * For setting the Grid Layout of room Persons showing at bottom of the Room
     *
     * @param list
     */
    public void setRecyclerView(final ArrayList<EventAttendy> list) {
        CircularImageView comeInUserProfile = null;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = findViewById(R.id.comeInUser_lnr);
        parent.removeAllViews();
        int loopCount = list.size();
        if (loopCount > 5) {
            loopCount = 5;
        }
        if (list.size() != 0) {
            parent.setVisibility(View.VISIBLE);
            iv_group.setVisibility(View.GONE);
        } else {
            parent.setVisibility(View.GONE);
            iv_group.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < loopCount; i++) {
            assert inflater != null;
            View v = inflater.inflate(R.layout.trend_user_view, null);
            comeInUserProfile = v.findViewById(R.id.comeInProfile_t);
            TextView no_count = v.findViewById(R.id.no_count_t);
            RelativeLayout marginlayout = v.findViewById(R.id.mainProfileView_t);

            if (i == 0) {

                parent.addView(v, i);
                String image = "";

                if (!list.get(i).getUserimage().contains("dev-")) {
                    image =  list.get(i).getUserimage();
//                    image = "dev-" + list.get(i).getUserimage();
                } else {
                    //image = keyInUserModalList.get(i).userImage;
                    image = list.get(i).getUserimage();
                }

                Glide.with(this).load(image)
                        .thumbnail(0.5f)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_img)
                        .error(R.drawable.placeholder_img)
                        .into(comeInUserProfile);


            } else {
                if (i == 1) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    parent.addView(v, i);
                    String image = "";

                    if (!list.get(i).getUserimage().contains("dev-")) {
                        image =  list.get(i).getUserimage();
//                        image = "dev-" + list.get(i).getUserimage();
                    } else {
                        image = list.get(i).getUserimage();
                    }

                    Glide.with(this).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                } else if (i == 2) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    parent.addView(v, i);
                    String image = "";

                    if (!list.get(i).getUserimage().contains("dev-")) {
                        image =  list.get(i).getUserimage();
//                        image = "dev-" + list.get(i).getUserimage();
                    } else {
                        image = list.get(i).getUserimage();
                    }

                    Glide.with(this).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                }
                if (i == 3) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    parent.addView(v, i);
                    no_count.setText(" +" + (list.size() - i));
                    no_count.setVisibility(View.VISIBLE);
                }
            }
        }

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OnBoardActivity.this, TheRoomActivity.class);
                intent.putExtra("commentPesionList", list);
                intent.putExtra("eventid", object.getEvent());
                intent.putExtra("venuid", object.getVenue());
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                if (fromTrending) {
                    intent.putExtra("fromTrending", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(intent);
                }
                //  }
            }
        });
    }

    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final int callFrom) { // 0 from search, 1 for tags long press
        utility = new Utility(OnBoardActivity.this);
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
                            if (jo.getString("status").equalsIgnoreCase("success")) {
                                venuBoardEventTagBeanList = new ArrayList<>();
                                getSearchTagList(event.event_id, object.getVenue().getVenue_id());
                            }
                        }

                    } catch (Exception e) {
                        dismissProgDialog();
//                        Utility.showToast(OnBoardActivity.this, getString(R.string.somethingwentwrong), 0);
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
            VolleySingleton.getInstance(OnBoardActivity.this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    public void manageFrequency(String frequency) {

        switch (frequency) {
            case "daily":
                iv_group.setVisibility(View.VISIBLE);
                img_no_member.setVisibility(View.VISIBLE);
                comeInUser_lnr.setVisibility(View.VISIBLE);
                break;

            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                day = day - 1;

                if (day == Integer.parseInt(frequency)) {
                    iv_group.setVisibility(View.VISIBLE);
                    img_no_member.setVisibility(View.VISIBLE);
                    comeInUser_lnr.setVisibility(View.VISIBLE);
                } else {
                    iv_group.setVisibility(View.GONE);
                    img_no_member.setVisibility(View.GONE);
                    comeInUser_lnr.setVisibility(View.GONE);
                }
                break;

            default:

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date d = new Date();
                String curDate = sdf.format(d);

                if (frequency.equalsIgnoreCase(curDate)) {
                    iv_group.setVisibility(View.VISIBLE);
                    img_no_member.setVisibility(View.VISIBLE);
                    comeInUser_lnr.setVisibility(View.VISIBLE);
                } else {
                    iv_group.setVisibility(View.GONE);
                    img_no_member.setVisibility(View.GONE);
                    comeInUser_lnr.setVisibility(View.GONE);
                }
                break;

        }

    }

    private void callAddEventApi(final Events object) {
        final Utility utility = new Utility(this);

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
                            showKeyPoints("+3");
                        } else if (status.equals("exist")) {
                        }

                        // if(object.getVenue().getIs_tag_follow().equalsIgnoreCase(""))

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
            Utility.showCheckConnPopup(this, "No network connection", "", "");
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

    private void showKeyPoints(String s) {
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
            } else if (event.ableToKeyIn) {
                callAddEventApi(object);
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
        if (type != -1) showProgDialog(false, "");

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
//                        Utility.showToast(OnBoardActivity.this, getString(R.string.somethingwentwrong), 0);
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
                    params.put("eventid", object.getEvent().event_id);
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
            case "post": {
                callCheckEventStatusApi(event.event_name, event.event_id, venue, object, currentLatLng
                        , new String[]{venue.getLatitude(), venue.getLongitude()});

            }
            break;
            case "summery": {
                Intent intent = new Intent(OnBoardActivity.this, Summary_Activity.class);
                intent.putExtra("event_id", event.event_id);
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

            case "people": {

                Intent intent = new Intent(OnBoardActivity.this, TheRoomActivity.class);
                intent.putExtra("fromTrendingHome", event.keyInUserModalList);
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

        }


    }

    private void forAlertEventApi(final String event_name, String event_id, String event_time) {
        final Utility utility = new Utility(this);

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
                            showKeyPoints("+3");
                        } else if (status.equals("exist")) {
                        }

                        // if(object.getVenue().getIs_tag_follow().equalsIgnoreCase(""))

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
                    params.put("eventname", event_name);
                    params.put("eventid", event_id);
                    params.put("eventdate", event_time);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }


}

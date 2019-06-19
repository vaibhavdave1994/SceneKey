package com.scenekey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.adapter.TrendingSearchAdapter;
import com.scenekey.adapter.Trending_Adapter;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.listener.FollowUnfollowLIstner;
import com.scenekey.model.Events;
import com.scenekey.model.TagModal;
import com.scenekey.model.UserInfo;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrendinSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "TrendinSearchActivity";
    private ImageView img_search_back;
    private TrendingSearchAdapter trendingAdapter;
    private ArrayList<Events> eventsArrayList;
    private RecyclerView searchTrending_recycler_view;
    private Utility utility;
    private CustomProgressBar customProgressBar;
    private RelativeLayout continer;
    private UserInfo userInfo;
    private String tag_name = "";
    private String tag_text = "";
    private String tag_image = "";
    private ScrollView no_data_trending;
    private TextView btn_follow,btn_unfollow,tv_error;
    private ImageView ivTag;
    boolean from_tagadapter = false;
    boolean fromSpecial = false;
    TagModal tagModal;
    int isTagFollowed = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trendin_search);
        inItView();
    }

    private void inItView() {
        utility = new Utility(this);
        userInfo = SceneKey.sessionManager.getUserInfo();
        customProgressBar = new CustomProgressBar(this);

        img_search_back = findViewById(R.id.img_search_back);
        btn_follow = findViewById(R.id.btn_follow);
        btn_unfollow = findViewById(R.id.btn_unfollow);
        tv_error = findViewById(R.id.tv_error);
        btn_follow.setOnClickListener(this);
        btn_unfollow.setOnClickListener(this);
        TextView txt_f1_title = findViewById(R.id.txt_f1_title);
        searchTrending_recycler_view = findViewById(R.id.searchTrending_recycler_view);
        no_data_trending = findViewById(R.id.no_data_trending);
        ivTag = findViewById(R.id.ivTag);
        continer = findViewById(R.id.continer);

        fromSpecial =  getIntent().getBooleanExtra("fromSpecial",false);
        from_tagadapter = getIntent().getBooleanExtra("from_tagadapter",false);

        tagModal = (TagModal) getIntent().getSerializableExtra("tagmodel");
        eventsArrayList = new ArrayList<>();
        img_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (getIntent().getStringExtra("tag_name") != null)
            tag_name = getIntent().getStringExtra("tag_name");

            if(fromSpecial){
                if (getIntent().getStringExtra("tag_text") != null) {
                    tag_text = getIntent().getStringExtra("tag_text");
                    txt_f1_title.setText("" + tag_text);
                }
            }
            else {
                    txt_f1_title.setText("" + tag_name);

            }

        if (getIntent().getStringExtra("tag_image") != null) {
            tag_image = getIntent().getStringExtra("tag_image");
            Picasso.with(this).load(tag_image).placeholder(R.drawable.app_icon)
                    .error(R.drawable.app_icon).into(ivTag);
        }

        setRecyclerView();
        getTrendingData();
    }

    private void setRecyclerView() {
        if (trendingAdapter == null) {
            trendingAdapter = new TrendingSearchAdapter(TrendinSearchActivity.this, eventsArrayList, new String[]{userInfo.lat, userInfo.longi}, new CheckEventStatusListener() {
                @Override
                public void getCheckEventStatusListener(String eventNAme, String event_id, Venue venue_name, Events object, String[] currentLatLng, String[] strings) {
                    callCheckEventStatusApi(eventNAme, event_id, venue_name.getVenue_name(), object, currentLatLng, strings);
                }
            }
                    ,
                    new FollowUnfollowLIstner() {
                        @Override
                        public void getFollowUnfollow(int followUnfollow, String biz_tag_id,int position) {
                            tagFollowUnfollow(followUnfollow,biz_tag_id,position);
                        }
                    }
            );

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            searchTrending_recycler_view.setLayoutManager(layoutManager);
            searchTrending_recycler_view.setAdapter(trendingAdapter);
            trendingAdapter.notifyDataSetChanged();
            searchTrending_recycler_view.setHasFixedSize(true);
        } else {
            trendingAdapter.notifyDataSetChanged();
            searchTrending_recycler_view.setHasFixedSize(true);
        }
    }

    // New Code
    private void callCheckEventStatusApi(final String event_name, final String event_id, final String venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(this);

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
                            Toast.makeText(TrendinSearchActivity.this, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(TrendinSearchActivity.this, EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("fromTab", "trending");
                            intent.putExtra("venueName", venue_name);
                            intent.putExtra("currentLatLng", currentLatLng);
                            intent.putExtra("event_name", event_name);
                            intent.putExtra("object", object);

                            startActivity(intent);

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
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(TrendinSearchActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
           /* case R.id.img_search_back:
                onBackPressed();
                break;*/
            case R.id.btn_follow:
                tagFollowUnfollow(1);
                break;

            case R.id.btn_unfollow:
                  tagFollowUnfollow(0);
                break;
        }
    }

    public UserInfo userInfo() {

        if (!SceneKey.sessionManager.isLoggedIn()) {
            SceneKey.sessionManager.logout(TrendinSearchActivity.this);
        }
        return SceneKey.sessionManager.getUserInfo();
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
    }

    public void getTrendingData() {
        if (utility.checkInternetConnection()) {
            showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TRENDING_TAG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if(jo.has("is_tag_follow")){
                            isTagFollowed = jo.getInt("is_tag_follow");
                            if(isTagFollowed == 0){
                                if(fromSpecial){
                                    btn_follow.setVisibility(View.GONE);
                                    btn_unfollow.setVisibility(View.GONE);
                                }
                                else {
                                    btn_follow.setVisibility(View.VISIBLE);
                                    btn_unfollow.setVisibility(View.GONE);
                                }

                            }
                            else {
                                if (fromSpecial) {
                                    btn_follow.setVisibility(View.GONE);
                                    btn_unfollow.setVisibility(View.GONE);
                                } else {
                                    btn_follow.setVisibility(View.GONE);
                                    btn_unfollow.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        if (jo.has("status")) {
                            int status = jo.getInt("status");
                            if (status == 0) {
                                dismissProgDialog();
                                try {
                                    no_data_trending.setVisibility(View.VISIBLE);
                                    tv_error.setText("Unfortunately there are no results for '"+tag_name+"' in your area at this time. Follow the token and we will notify you of any activity!");
                                    if(tagModal.status != null) {
                                        if (!tagModal.status.equalsIgnoreCase("active")) {
                                            tv_error.setText("Unfortunately '" + tag_name + "' is not active in your area at this time. Follow it for notifications when it comes in your area. To follow, hold down the button below.");
                                        }
                                    }
                                    if(fromSpecial){
                                        btn_follow.setVisibility(View.GONE);
                                        btn_unfollow.setVisibility(View.GONE);
                                    }
//                                    else {
//                                        btn_follow.setVisibility(View.VISIBLE);
//                                        btn_unfollow.setVisibility(View.GONE);
//                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                // New Code
                                if (jo.has("userInfo")) {
                                    UserInfo userInfo = userInfo();
                                    JSONObject user = jo.getJSONObject("userInfo");
                                    if (user.has("makeAdmin"))
                                        userInfo.makeAdmin = (user.getString("makeAdmin"));

                                    if (user.has("lat"))
                                        userInfo.lat = (user.getString("lat"));

                                    if (user.has("longi"))
                                        userInfo.longi = (user.getString("longi"));

                                    if (user.has("address"))
                                        userInfo.address = (user.getString("address"));

                                    if (user.has("fullname"))
                                        userInfo.fullname = (user.getString("fullname"));

                                    if (user.has("key_points"))
                                        userInfo.key_points = (user.getString("key_points"));

                                    if (user.has("bio"))
                                        userInfo.bio = (user.getString("bio"));
                                    updateSession(userInfo);

                                    if (user.getString("fullname").equals("")) {
                                        SceneKey.sessionManager.logout(TrendinSearchActivity.this);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {

                                // New Code
                                if (jo.has("userInfo")) {
                                    UserInfo userInfo = userInfo();
                                    JSONObject user = jo.getJSONObject("userInfo");
                                    if (user.has("makeAdmin"))
                                        userInfo.makeAdmin = (user.getString("makeAdmin"));

                                    if (user.has("lat"))
                                        userInfo.lat = (user.getString("lat"));

                                    if (user.has("longi"))
                                        userInfo.longi = (user.getString("longi"));

                                    if (user.has("address"))
                                        userInfo.address = (user.getString("address"));

                                    if (user.has("fullname"))
                                        userInfo.fullname = (user.getString("fullname"));

                                    if (user.has("key_points"))
                                        userInfo.key_points = (user.getString("key_points"));

                                    if (user.has("bio"))
                                        userInfo.bio = (user.getString("bio"));

                                    updateSession(userInfo);

                                    if (user.getString("fullname").equals("")) {
                                        SceneKey.sessionManager.logout(TrendinSearchActivity.this);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (jo.has("events")) {
                                if (eventsArrayList == null) eventsArrayList = new ArrayList<>();
                                else eventsArrayList.clear();
                                JSONArray eventAr = jo.getJSONArray("events");
                                for (int i = 0; i < eventAr.length(); i++) {
                                    JSONObject object = eventAr.getJSONObject(i);
                                    Events events = new Events();
                                    if (object.has("venue"))
                                        events.setVenueJSON(object.getJSONObject("venue"));
                                    if (object.has("artists"))
                                        events.setArtistsArray(object.getJSONArray("artists"));
                                    if (object.has("events"))
                                        events.setEventJson(object.getJSONObject("events"));
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
                                    // Util.printLog("Result",events.toString());
                                }
                                if (eventsArrayList.size() <= 0) {
                                    Toast.makeText(TrendinSearchActivity.this, "No Event found near your location", Toast.LENGTH_LONG).show();
                                }
                                setRecyclerView();
                            }
                            dismissProgDialog();
                            no_data_trending.setVisibility(View.GONE);
                            btn_follow.setVisibility(View.GONE);
                            btn_unfollow.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        dismissProgDialog();
                        Utility.showToast(TrendinSearchActivity.this, getString(R.string.somethingwentwrong), 0);
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
                    params.put("lat", userInfo.lat);
                    params.put("long", userInfo.longi);
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    params.put("tag", tag_name);
                    if(fromSpecial){
                        params.put("type", tag_text);
                    }else {
                        params.put("type", "");
                    }

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    public void checkWithDate(final String startDate, final String rating, Events events) {  //2018-11-12 18:00:00TO08:00:00
        String[] dateSplit = (startDate.replace("TO", "T")).replace(" ", "T").split("T");
        try {
            Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())).parse(dateSplit[0] + " " + dateSplit[1]);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = df.format(c.getTime());

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
                    // events.getEvent().returnDay = String.format("%.2f", Double.parseDouble(rating));
                    events.getEvent().returnDay = rating;
                }
            }
        } else {
            /*returnDay = "--";
            strStatus = 0;*/

            if (rating.equals("0")) {
                events.getEvent().returnDay = "--";
                //strStatus = 0;
                events.getEvent().strStatus = 0;
            } else {
                events.getEvent().strStatus = 1;
                //strStatus = 1;
                // events.getEvent().returnDay = String.format("%.2f", Double.parseDouble(rating));
                events.getEvent().returnDay = rating;
            }
        }
    }

    public void showProgDialog(boolean b, String TAG) {
        try {
            customProgressBar.setCanceledOnTouchOutside(b);
            customProgressBar.setCancelable(b);
            customProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(fromSpecial){
            super.onBackPressed();
            return;
        }
        if(!from_tagadapter) {
            Intent intent = new Intent(TrendinSearchActivity.this, HomeActivity.class);
            intent.putExtra("fromSearch", true);
            intent.putExtra("name", tag_name);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        else {
            super.onBackPressed();
        }
    }

    //-------FOLLOW/UNFOLLOW-----------
    public void tagFollowUnfollow(final int followUnfollow) {
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
                                if(jo.getString("status").equalsIgnoreCase("success")){
                                    if(followUnfollow == 0){
                                        isTagFollowed = 0;
                                        if(fromSpecial){
                                            btn_follow.setVisibility(View.GONE);
                                            btn_unfollow.setVisibility(View.GONE);
                                        }
                                        else {
                                            btn_follow.setVisibility(View.VISIBLE);
                                            btn_unfollow.setVisibility(View.GONE);
                                        }
                                    }else {
                                        isTagFollowed = 1;
                                        if(fromSpecial){
                                            btn_follow.setVisibility(View.GONE);
                                            btn_unfollow.setVisibility(View.GONE);
                                        }
                                        else {
                                            btn_follow.setVisibility(View.GONE);
                                            btn_unfollow.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }

                    } catch (Exception e) {
                        dismissProgDialog();
                        Utility.showToast(TrendinSearchActivity.this, getString(R.string.somethingwentwrong), 0);
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
                    params.put("biz_tag_id",tagModal.biz_tag_id);
                    params.put("follow_status", String.valueOf(followUnfollow));
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    //-------temporery method-
    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final int pos) {
        if (utility.checkInternetConnection()) {
            showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if(jo.has("status")){
                            if(jo.getString("status").equalsIgnoreCase("success")){
                                if(followUnfollow == 0){
                                    Venue venue = eventsArrayList.get(pos).getVenue();
                                    venue.setIs_tag_follow("0");
                                    eventsArrayList.get(pos).setVenue(venue);
                                    trendingAdapter.notifyItemChanged(pos);
                                }else {
                                    Venue venue = eventsArrayList.get(pos).getVenue();
                                    venue.setIs_tag_follow("1");
                                    eventsArrayList.get(pos).setVenue(venue);
                                    trendingAdapter.notifyItemChanged(pos);
                                }
//                                setRecyclerView();
                                // getTrendingData();

                            }
                        }

                    } catch (Exception e) {
                        dismissProgDialog();
                        Utility.showToast(TrendinSearchActivity.this, getString(R.string.somethingwentwrong), 0);
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
            VolleySingleton.getInstance(TrendinSearchActivity.this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }
}

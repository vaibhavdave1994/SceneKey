package com.scenekey.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.adapter.Profile_Adapter;
import com.scenekey.adapter.UserInAdapter;
import com.scenekey.aws_service.Aws_Web_Service;
import com.scenekey.cropper.CropImage;
import com.scenekey.cropper.CropImageView;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.CustomeClick;
import com.scenekey.helper.Permission;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.ForDeleteFeed;
import com.scenekey.model.EmoziesModal;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.EventDetailsForActivity;
import com.scenekey.model.Events;
import com.scenekey.model.FeedSmily;
import com.scenekey.model.Feeds;
import com.scenekey.model.UserInfo;
import com.scenekey.model.Wallets;
import com.scenekey.util.ImageUtil;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static UserInfo userInfo;
    private static String FROM_TAB = "from_tab";
    private static Timer timerHttp;
    public final String TAG = EventDetailsActivity.class.toString();
    //DataType Decleration...............
    public Double latitude, longitude;
    public ArrayList<Card> cardsList;
    public List<EmoziesModal> all_emaozieList;
    public ArrayList<FeedSmily> feedlikeList;
    //Adaaptersss Declartions..............
    public Profile_Adapter adapter;
    // others..............
    public String[] currentLatLng;
    private String eventId;
    private boolean myProfile;
    private String date, cutrrentDate, time;
    private String venueName;
    private String from_tab;
    private boolean isKeyInAble = false;
    private boolean canCallWebservice;
    //View Declartions...................
    private ImageView img_eventDetail_back, addButtonForKeyIn, img_postImage;
    private TextView txt_event_name;
    private RecyclerView feedLIstRecyclerView;
    private RecyclerView usercomeInRecyclerView;
    private TextView txt_post_comment;
    private EditText et_comment_feed;
    private ImageView img_no_member;
    //LIst Declaration...............
    private ArrayList<Feeds> feedList;
    private ArrayList<EventAttendy> userList;
    //Modal Declation............
    private EventDetailsForActivity eventDetails;
    private Uri imageUri;

    //calling other class
    private Utility utility;
    private CustomProgressBar customProgressBar;
    private Events event;
    private String eventName = "";

    public static List<EmoziesModal> loadCountries(EventDetailsActivity eventDetailsActivity) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Type listType = new TypeToken<List<EmoziesModal>>() {
            }.getType();
            List<EmoziesModal> list = gson.fromJson(loadJSONFromAsset(eventDetailsActivity, "AllEmojiNew.json"), listType);
            ;
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is = null;
        try {
            AssetManager manager = context.getAssets();
            Log.d("JSON Path ", jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    //EmoziesModal For Emoziesss(Emozies Parsing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        inItView();
    }

    private void inItView() {

        userInfo = SceneKey.sessionManager.getUserInfo();
        customProgressBar = new CustomProgressBar(this);
        utility = new Utility(this);


        img_eventDetail_back = findViewById(R.id.img_eventDetail_back);
        txt_event_name = findViewById(R.id.txt_event_name);
        addButtonForKeyIn = findViewById(R.id.addButtonForKeyIn);
        feedLIstRecyclerView = findViewById(R.id.feedLIstRecyclerView);
        usercomeInRecyclerView = findViewById(R.id.usercomeInRecyclerView);
        txt_post_comment = findViewById(R.id.txt_post_comment);
        et_comment_feed = findViewById(R.id.et_comment_feed);
        img_postImage = findViewById(R.id.img_postImage);
        img_no_member = findViewById(R.id.img_no_member);

        //ArraryLIst
        feedList = new ArrayList<>();
        userList = new ArrayList<>();
        cardsList = new ArrayList<>();
        //feedlikeList = new ArrayList<>();

        //getUserEmoziesList = new ArrayList<>();


        setOnClick(img_eventDetail_back, addButtonForKeyIn, txt_post_comment, et_comment_feed, img_postImage);

        if (getIntent().getStringExtra("event_id") != null) {
            eventId = getIntent().getStringExtra("event_id");
            from_tab = getIntent().getStringExtra("fromTab");
            eventName = getIntent().getStringExtra("event_name");
            venueName = getIntent().getStringExtra("venueName");
            currentLatLng = getIntent().getStringArrayExtra("currentLatLng");
            event = (Events) getIntent().getSerializableExtra("object");
        }


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                showProgDialog(false, TAG);
                getAllData();
            }
        });

        if (timerHttp == null) setDataTimer();

        /*adapter = new Profile_Adapter(this, feedList, myProfile, userList, eventId);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        feedLIstRecyclerView.setLayoutManager(layoutManager);
        feedLIstRecyclerView.setAdapter(adapter);*/


        date = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.getDefault()).format(new Date());
        cutrrentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Log.i("date", date);


        //callAddEventApi() Api for trending tanb and map tab
        CustomeClick.getmInctance().setListner(new CustomeClick.ExploreSearchListener() {
            @Override
            public void onTextChange(UserInfo user) {
                if (from_tab.equals("trending") && !isKeyInAble) {
                    callAddEventApi(eventId, venueName, event, currentLatLng, new String[]{latitude.toString(), longitude.toString()});
                } else if (from_tab.equals("map_tab") && !isKeyInAble) {
                    callAddEventApi(eventId, venueName, event, currentLatLng, new String[]{latitude.toString(), longitude.toString()});
                }
            }
        });


        //txt_event_name.setText(venueName);
        txt_event_name.setText(eventName);


        //......................................................

        all_emaozieList = loadCountries(this);
        assert all_emaozieList != null;


        //feedlikeList.add(0, null);
//        adapter = new Profile_Adapter(this, feedList, myProfile, userList, eventId,getUserEmoziesList,all_emaozieList);
        adapter = new Profile_Adapter(this, userInfo.userid, feedList, myProfile, userList, eventId, all_emaozieList, new ForDeleteFeed() {
            @Override
            public void getFeedIdForDelete(String id) {
                deleteFeed(id);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        feedLIstRecyclerView.setLayoutManager(layoutManager);
        feedLIstRecyclerView.setAdapter(adapter);
    }

    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_eventDetail_back:
                onBackPressed();
                break;

            case R.id.addButtonForKeyIn:
                //For key in

                if (eventDetails != null && eventDetails.getProfile_rating() != null) {
                    if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
                        try {
                            if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
                                addUserIntoEvent(-1, null);
                            } else if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                                addUserIntoEvent(-1, null);
                            } else cantJoinDialog();
                        } catch (ParseException d) {
                            d.getMessage();
                        }
                    }
                }

                break;

            case R.id.img_postImage:

                try {
                    if (userInfo().makeAdmin.equals(Constant.ADMIN_YES) && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        captureImage();
                    } else if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        captureImage();

//                        getAllData();
                    } else {
                        cantJoinDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.showToast(this, getResources().getString(R.string.somethingwentwrong), 0);
                }
                break;


            case R.id.txt_post_comment:
                if (!et_comment_feed.getText().toString().isEmpty()) {
                    try {
                        if (userInfo().makeAdmin.equals(Constant.ADMIN_YES) && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                            canCallWebservice = false;
                            showProgDialog(true, TAG);
                            commentEvent();
                        } else if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                            canCallWebservice = false;
                            showProgDialog(true, TAG);
                            commentEvent();
                        } else {
                            cantJoinDialog();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.showToast(this, getResources().getString(R.string.somethingwentwrong), 0);
                    }

                } else {

                    utility.showCustomPopup("Please enter comment", String.valueOf(R.font.montserrat_medium));
                }

                break;
        }
    }


    private void setDataTimer() {
        if (timerHttp == null) timerHttp = new Timer();

        //Set the schedule function and rate
        //TODO timer changed as required
        timerHttp.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {
                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      Utility.e(TAG, "TimerVolley Event fragment");
                                                      try {
                                                          if (canCallWebservice) getAllData();
                                                      } catch (Exception e) {
                                                          e.printStackTrace();
                                                      }
                                                  }
                                              });
                                          }
                                      },
                //Set how long before to start calling the TimerTask (in milliseconds)
                60000,
                //Set the amount of time between each execution (in milliseconds)
                60000);
    }


    @Override
    public void onResume() {
        super.onResume();
        getAllData();
        //setBBVisibility(View.GONE, TAG);
        //hideStatusBar();
        dismissProgDialog();
        canCallWebservice = true;
        if (timerHttp == null) setDataTimer();
    }

    @Override
    public void onPause() {
        if (timerHttp != null) timerHttp.cancel();
        timerHttp = null;
        super.onPause();
    }


    @Override
    public void onDestroy() {
        if (timerHttp != null) timerHttp.cancel();
        timerHttp = null;

        super.onDestroy();
    }

    private void commentEvent() {
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_COMMENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.v("response", response);

                    dismissProgDialog();
                    // get response

                    try {
                        if (new JSONObject(response).getString("msg").equals("Success")) {
                            et_comment_feed.setText("");
                            incrementKeyPoints(getString(R.string.kp_keyin));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!canCallWebservice) {
                        canCallWebservice = true;
                        getAllData();
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

                    params.put("user_id", userInfo().userid);
                    params.put("event_id", eventId);
                    params.put("location", getLocation());
                    params.put("comment", et_comment_feed.getText().toString().trim());
                    params.put("ratingtime", getCurrentTimeInFormat());
                    Utility.e(TAG, "" + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1));
        } else {
            utility.snackBar(feedLIstRecyclerView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }


    //GetAllDataApi...........................................................

    /**
     * GetALl the data for that event
     */
    public void getAllData() {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LISTEVENTFEED, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.e("Responce129", response);
                    // get response
                    try {
                        if (response != null) getResponse(response);
                        else
                            Utility.showToast(EventDetailsActivity.this, getString(R.string.somethingwentwrong), 0);
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

                    params.put("event_id", eventId);
                    params.put("user_id", userInfo().userid);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1));
        } else {
            utility.snackBar(feedLIstRecyclerView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }
    //endGetAllDataApi........................................................


    /**
     * @param response the responce provided by getAlldata()
     * @throws JSONException
     */
    private void getResponse(String response) throws Exception {
        JSONObject obj1 = new JSONObject(response);
        dismissProgDialog();
        if (eventDetails == null) eventDetails = new EventDetailsForActivity();
        try {
            if (obj1.has("eventattendy")) {
                // eventDetails.setAttendyJson(obj1.getJSONArray("eventattendy"), this);
                Object objectType = obj1.get("eventattendy");

                if (objectType instanceof String) {
                    if (eventDetails.getAttendyList() == null) {
                        img_no_member.setVisibility(View.VISIBLE);
                        usercomeInRecyclerView.setVisibility(View.GONE);
                    }

                } else if (objectType instanceof JSONArray) {
                    eventDetails.setAttendyJson(obj1.getJSONArray("eventattendy"), this);
                    img_no_member.setVisibility(View.GONE);
                    usercomeInRecyclerView.setVisibility(View.VISIBLE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("nudges_count"))
                eventDetails.setNudges_count(obj1.getString("nudges_count"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("allTags")) {
                eventDetails.setTagList(obj1.getJSONArray("allTags"), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("allfeeds")) {
                //  eventDetails.setFeedsJson(obj1.getJSONArray("allfeeds"), this);
                Object objectType = obj1.get("allfeeds");

                if (objectType instanceof String) {

                    if (eventDetails.getFeedList() == null) {
                        if (feedList.size() == 0) {
                            if (obj1.has("event_profile_rating")) {
                                JSONObject obj = obj1.getJSONObject("event_profile_rating");

                                Feeds feeds = new Feeds();
                                String event_name = obj.getString("event_name");
                                String[] event_name_array = event_name.split("@");
                                feeds.username = event_name_array[1];

                                feeds.userimage = event.getVenue().getImage();
                                feeds.type = Constant.FEED_TYPE_COMMENT;

                                if (from_tab.equals("event_tab")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                        incrementKeyPoints("");
                                        getAllData();
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    }

                                } else if (from_tab.equals("trending")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                    }
                                } else if (from_tab.equals("map_tab")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                    }
                                } else if (from_tab.equals("homeTab")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                        getAllData();
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    }
                                }

                                feedList.add(feeds);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else if (objectType instanceof JSONArray) {
                    feedList.clear();
                    eventDetails.setFeedsJson(obj1.getJSONArray("allfeeds"), this);
                    feedList.addAll(eventDetails.getFeedList());
                    adapter.notifyDataSetChanged();
                    Log.e("FEEDS LIST SIZE", eventDetails.getFeedList().size() + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (obj1.has("event_profile_rating")) {
                eventDetails.setProfile_ratingJSon(obj1.getJSONObject("event_profile_rating"), EventDetailsActivity.this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("userInfo")) {

                Object intervention = obj1.get("userInfo");
                if (intervention instanceof JSONArray) {
                    SceneKey.sessionManager.logout(this);
                }
                JSONObject user = obj1.getJSONObject("userInfo");
                if (user.has("makeAdmin")) {
                    userInfo().makeAdmin = (user.getString("makeAdmin"));

                }
                if (user.has("lat")) userInfo().lat = (user.getString("lat"));
                if (user.has("longi")) userInfo().longi = (user.getString("longi"));

                /*if (user.has("adminLat")) userInfo().latitude = (user.getString("adminLat"));
                if (user.has("adminLong")) userInfo().longitude = (user.getString("adminLong"));*/

                if (user.getString("adminLat").isEmpty()) {
                    userInfo.adminLat = user.getString("lat");
                    userInfo.adminLong = user.getString("longi");
                    userInfo.currentLocation = true;
                } else {
                    userInfo.adminLat = user.getString("adminLat");
                    userInfo.adminLong = user.getString("adminLong");
                    userInfo.currentLocation = false;
                }
                if (user.has("address")) userInfo().address = (user.getString("address"));
                updateSession(userInfo());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

       /* try {
            if (obj1.has("nudgeList")) {
                Object objectType = obj1.get("nudgeList");

                if (objectType instanceof String) {
                    if (eventDetails.getNudgeList() == null) {
                       *//* tv_no_members.setVisibility(View.GONE);
                        usercomeInRecyclerView.setVisibility(View.VISIBLE);*//*
                    }
                } else if (objectType instanceof JSONArray) {
                    eventDetails.setNudgeJson(obj1.getJSONArray("nudgeList"), this);
                   *//* tv_no_members.setVisibility(View.GONE);
                    usercomeInRecyclerView.setVisibility(View.VISIBLE);*//*
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/


      /*  int height = (int) getResources().getDimension(R.dimen._125sdp);//activity().ActivityWidth;
        int width = activity.ActivityWidth;
        String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=12&size=" + width + "x" + height + "&sensor=false";
        Utility.e(TAG, "URL" + url + "Lat lin" + latitude + " : " + longitude);

        try {
            Picasso.with(activity).load(url).into(image_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        if (cardsList.size() <= 0) {
            Card card = new Card();
            card.imageUrl = null;
            card.text = "Welcome to the " + venueName + "! Join the fun! Share your pics & comments right here!";
            cardsList.add(card);
        }
    }


    private void callAddEventApi(final String event_id, final String venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
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
                            isKeyInAble = true;
                            feedList.clear();
                            getAllData();
                            adapter.notifyDataSetChanged();
                        } else if (status.equals("exist")) {
                            isKeyInAble = true;
                            feedList.clear();
                            getAllData();
                            adapter.notifyDataSetChanged();
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


    /***
     * For setting the Grid Layout of room Persons showing at bottom of the Room
     *
     * @param list
     */
    public void setRecyclerView(final ArrayList<EventAttendy> list) {
        userList.clear();
        userList.addAll(list);
        adapter.notifyDataSetChanged();
        //this.userList  = list;
        // UserInAdapter adapter = new UserInAdapter(list, this, eventId);
        // usercomeInRecyclerView.setAdapter(adapter);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) findViewById(R.id.comeInUser_lnr);
        parent.removeAllViews();
        for (int i = 0; i < list.size(); i++) {

            View v = inflater.inflate(R.layout.userin_view, null);
            CircularImageView comeInUserProfile = v.findViewById(R.id.comeInProfile);
            TextView no_count = v.findViewById(R.id.no_count);
            RelativeLayout marginlayout = v.findViewById(R.id.mainProfileView);
            if (i == 0) {

                parent.addView(v);

            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(30, 0, 0, 0);
                marginlayout.setLayoutParams(params);
                parent.addView(v);
            }
            String image = "";

            if (!list.get(i).getUserimage().contains("dev-")) {
                image = "dev-" + list.get(i).getUserimage();
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

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailsActivity.this, TheRoomActivity.class);
                intent.putExtra("commentPesionList", list);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });
    }


    /* common methods used somewhere else  */

   /* public void addChips(ArrayList<Tags> tag) {
        try {
            Grid_multiRow layout = this.getView().findViewById(R.id.chip_linear);
            layout.setAdapter(new GridChipsAdapter(this, tag));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public void cantJoinDialog() {

        if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else {
            utility.showCustomPopup(getString(R.string.enotat), String.valueOf(R.font.montserrat_medium));
        }
    }


    private void captureImage() {

        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_takephoto_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        TextView tv_camera, tv_cancel;

        tv_camera = dialog.findViewById(R.id.tv_camera);
        tv_cancel = dialog.findViewById(R.id.tv_cancel);

        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Permission permission = new Permission(EventDetailsActivity.this);
                if (permission.checkCameraPermission()) callIntent(Constant.INTENT_CAMERA);
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }


    ///////////////////////////////////////////////////////////////////
    private void deleteFeed(final String id) {

        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_feed_delet);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        TextView tv_for_delete_feed, tv_not_delete;

        tv_for_delete_feed = dialog.findViewById(R.id.tv_for_delete_feed);
        tv_not_delete = dialog.findViewById(R.id.tv_not_delete);

        tv_for_delete_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteFeedApi(id);
                dialog.cancel();
            }
        });

        tv_not_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void deleteFeedApi(final String feedId) {
        showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.REWARD_WALLETS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.v("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            getAllData();
                        }
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
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid + "");
                    Utility.e(TAG, " feed_id " + feedId);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            //utility.snackBar(rcViewTrending,getString(R.string.internetConnectivityError),0);
            Toast.makeText(this, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }


    private void callIntent(int caseId) {

        switch (caseId) {
            case Constant.INTENT_CAMERA:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                    } else {
                        imageUri = Uri.fromFile(file);
                    }

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //  intent.putExtra("android.intent.extras.CAMERA_FACING", 1); //for front camera
                    startActivityForResult(intent, Constant.INTENT_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
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


    public boolean checkWithTime(final String date, double interval) throws ParseException {
        String[] dateSplit = (date.replace("TO", "T")).replace(" ", "T").split("T");
        Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).parse(dateSplit[0] + " " + dateSplit[1]);

        long currentTime = Calendar.getInstance().getTime().getTime();


        return currentTime > startTime.getTime();  //old ios logic

        //  return currentTime < endTime.getTime() && currentTime > startTime.getTime();

    }

    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(EventDetailsActivity.this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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


    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getAddress(double latitude, double longitude) {
        String result = null;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            //  String addressLine = addresses.get(0).getAddressLine(1);
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            // String postalCode = addresses.get(0).getPostalCode();
            // String knownName = addresses.get(0).getFeatureName();
            //result = knownName + " ," + addressLine + " , " + city + "," + state + "," + country + " counter" + counter;// Here 1 represent max location result to returned, by documents it recommended 1 to 5
            result = address + "," + city + "," + state + "," + country;// Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private String getLocation() {
        String result;
        if (userInfo().address.length() > 1) {
            result = userInfo().address;
        } else {
            result = getAddress(Double.parseDouble(currentLatLng[0]), Double.parseDouble(currentLatLng[1]));
        }
        return result;
    }


    public void decrementKeyPoints(final String msg) {
        final int points = Integer.parseInt(userInfo.key_points);
        new Aws_Web_Service() {
            @Override
            public okhttp3.Response onResponseUpdate(okhttp3.Response response) {
                if (response == null) return null;
                try {
                    String s = response.body().string();
                    if (new JSONObject(s).getInt("serverStatus") == 2) {
                        //   utility.showCustomPopup(msg, String.valueOf(R.font.arial_regular));

                        showKeyPoints("-1");
                        userInfo.key_points = (points <= 0 ? 0 + "" : (points - 1) + "");
                        updateSession(userInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }.updateKeyPoint((points <= 0 ? 25 : points - 1), userInfo.userid);
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


    public String getCurrentTimeInFormat() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())).format(new Date(System.currentTimeMillis()));
    }


    /****
     * This method is used when The user is not exist in the event to first time key in the user
     *
     * @param type must be 0 or 1
     */
    private void addUserIntoEvent(final int type, @Nullable final Bitmap bitmap) {
        if (type != -1) showProgDialog(false, TAG);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.getInt("success") == 0) {
                            incrementKeyPoints(getString(R.string.kp_keyin));
                        }

                      /*  if (type == 0) likeEvent();
                        else if (type == 1) sendPicture(bitmap);*/
                        getAllData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgDialog();
                        Utility.showToast(EventDetailsActivity.this, getString(R.string.somethingwentwrong), 0);
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
                    params.put("eventname", eventDetails.getProfile_rating().getEvent_name());
                    Log.e("eventdate", event.getEvent().event_time);
                    params.put("eventdate", event.getEvent().event_time);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(feedLIstRecyclerView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }


    /**
     * @param bitmap the bitmap return by the activity result
     */
    private void sendPicture(final Bitmap bitmap) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_POST_PIC, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    Utility.e("Send picture response", response);
                    getAllData();
                    try {
                        JSONObject respo = new JSONObject(response);
                        if (respo.getInt("success") == 0) {
                            Utility.showToast(EventDetailsActivity.this, respo.getString("msg"), 0);
                            //activity.showCustomPopup("Photo has been posted successfully.", 1);
                            decrementKeyPoints("");
                        } else {
//                            activity.showCustomPopup("Photo has been posted successfully.", 1);
                            incrementKeyPoints("");
                        }
                    } catch (Exception e) {
                        //activity.incrementKeyPoints(getString(R.string.kp_img_post));
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                    getAllData();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("user_id", userInfo().userid);
                    params.put("event_id", eventId);
                    params.put("location", getLocation()); //TODO location
                    params.put("image", ImageUtil.encodeTobase64(bitmap));
                    params.put("ratingtime", getCurrentTimeInFormat());

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(feedLIstRecyclerView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == Constant.INTENT_CAMERA) {

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160, 160).setMaxCropResultSize(4000, 3500).setAspectRatio(400, 300).start(this);

                } else {
                    Utility.showToast(this, getString(R.string.somethingwentwrong), 0);
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null) {

                        Feeds feeds = new Feeds();
                        feeds.type = Constant.FEED_TYPE_PICTURE;
                        feeds.date = cutrrentDate;
                        feeds.user_status = userInfo.user_status;
                        feeds.userimage = userInfo.getUserImage();
                        feeds.username = userInfo.userName;
                        feeds.feed = result.getUri().toString();
                        feeds.isUri = true;
                        feedList.add(0, feeds);
                        adapter.notifyItemInserted(0);
                        feedLIstRecyclerView.scrollToPosition(0);


                        Bitmap eventImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                        if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST))
                            addUserIntoEvent(1, eventImg);
                        else sendPicture(eventImg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2 && data.hasExtra("incresePoint")) {

                if (data.hasExtra("incresePoint")) {

                    String requiredValue = data.getStringExtra("incresePoint");
                    if (requiredValue.equals("1")) {
                        //activity.incrementKeyPoints("");
                    }
                }
            }
        }
    }

}

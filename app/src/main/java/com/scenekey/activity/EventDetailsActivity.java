package com.scenekey.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.adapter.Profile_Adapter;
import com.scenekey.adapter.SmilyUserAdapter;
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
import com.scenekey.listener.GetZoomImageListener;
import com.scenekey.listener.LikeFeedListener;
import com.scenekey.model.EmoziesModal;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.EventDetailsForActivity;
import com.scenekey.model.Events;
import com.scenekey.model.Feeds;
import com.scenekey.model.ReactionUserModal;
import com.scenekey.model.UserInfo;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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

    //Adaaptersss Declartions..............
    public Profile_Adapter adapter;
    // others..............
    public String[] currentLatLng;
    private String eventId;

    private String cutrrentDate;
    private String time;
    private String venueName,venueId;
    private String from_tab;
    private boolean isKeyInAble = false;
    private boolean canCallWebservice;
    private ImageView addButtonForKeyInuser;
    public static RecyclerView feedLIstRecyclerView;
    private EditText et_comment_feed;
    private ImageView img_no_member, img_ListIcon;


    //LIst Declaration...............
    private ArrayList<Feeds> feedList;
    private ArrayList<EventAttendy> userList;
    private ArrayList<ReactionUserModal> reactionUserLIst;


    //Modal Declation................
    private EventDetailsForActivity eventDetails;
    private Uri imageUri;

    //calling other class
    private Utility utility;
    private CustomProgressBar customProgressBar;
    private Events event;
    private String eventName = "";
    private SmilyUserAdapter smilyUserAdapter;
    private TextView txt_show_emojies;

    private View blurView;
    private String userExistOrNotonActivty = "";
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    public static FrameLayout detail_frame_fragments;
    boolean fromTrending = false;
//  ............................................................................

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getAllData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left, R.anim.fab_slide_out_to_left);
        registerReceiver(myReceiver, new IntentFilter("BroadcastNotification"));
        setContentView(R.layout.activity_event_details);
        inItView();

    }

    private void inItView() {

        userInfo = SceneKey.sessionManager.getUserInfo();
        customProgressBar = new CustomProgressBar(this);
        utility = new Utility(this);

        blurView = findViewById(R.id.blurView);

        //View Declartions...................
        ImageView img_eventDetail_back = findViewById(R.id.img_eventDetail_back);
        TextView txt_event_name = findViewById(R.id.txt_event_name);
        addButtonForKeyInuser = findViewById(R.id.addButtonForKeyInuser);
        feedLIstRecyclerView = findViewById(R.id.feedLIstRecyclerView);
        detail_frame_fragments = findViewById(R.id.detail_frame_fragments);

        TextView txt_post_comment = findViewById(R.id.txt_post_comment);
        et_comment_feed = findViewById(R.id.et_comment_feed);
        ImageView img_postImage = findViewById(R.id.img_postImage);
        img_no_member = findViewById(R.id.img_no_member);
        img_ListIcon = findViewById(R.id.img_ListIcon);
        txt_show_emojies = findViewById(R.id.txt_show_emojies);

        //ArraryLIst
        feedList = new ArrayList<>();
        userList = new ArrayList<>();
        cardsList = new ArrayList<>();
        reactionUserLIst = new ArrayList<>();

        setOnClick(img_no_member, blurView, img_eventDetail_back, addButtonForKeyInuser, txt_post_comment, et_comment_feed, img_postImage, img_ListIcon);

        isKeyInAble = getIntent().getBooleanExtra("isKeyInAble",false);
        fromTrending = getIntent().getBooleanExtra("fromTrending",false);
        if (getIntent().getStringExtra("event_id") != null) {
            eventId = getIntent().getStringExtra("event_id");
            from_tab = getIntent().getStringExtra("fromTab");
            eventName = getIntent().getStringExtra("event_name");
            venueName = getIntent().getStringExtra("venueName");
            venueId = getIntent().getStringExtra("venueId");
            currentLatLng = getIntent().getStringArrayExtra("currentLatLng");
            event = (Events) getIntent().getSerializableExtra("object");
        }

        if(event != null) {
         if(event.getVenue().getIs_tag_follow().equalsIgnoreCase("0"))
            tagFollowUnfollow(1, event.getVenue().getBiz_tag_id());
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                showProgDialog(false, TAG);
                getAllData();
            }
        });

        if (timerHttp == null) setDataTimer();

        String date = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.getDefault()).format(new Date());
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

        txt_event_name.setText(eventName);

        //......................................................

        all_emaozieList = loadCountries(this);
        assert all_emaozieList != null;

        adapter = new Profile_Adapter(event,this, userInfo.userid, feedList, userList, eventId, all_emaozieList, new ForDeleteFeed() {
            @Override
            public void getFeedIdForDelete(String feedid, String feedSmilyid, String reaction) {

                if (!feedid.isEmpty()) {
                    deleteFeed(feedid);
                }

                if (!feedSmilyid.isEmpty()) {
                    showFeedSmilyList(feedSmilyid, reaction);
                }

            }
        }, new LikeFeedListener() {
            @Override
            public void likeFeedByReaction(String addFeedReaction, String deleteFeedRaction, String getReactionFromList) {

                if (!deleteFeedRaction.isEmpty()) {
                    deletFeedRaction(deleteFeedRaction);
                }

                if (!getReactionFromList.isEmpty() && !addFeedReaction.isEmpty()) {
                    addFeedFormListreaction(getReactionFromList, addFeedReaction);

                } else {
                    addFeedreaction(addFeedReaction);
                }
            }
        }, new GetZoomImageListener() {
            @Override
            public void getImageUrl(String imageUrl) {
                Intent intent = new Intent(EventDetailsActivity.this, ZoomImageActivity.class);
                intent.putExtra("imageUrl", imageUrl);
//                if(fromTrending){
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//                }
//                else {
                    startActivity(intent);
                //}
            }
        });

//      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(EventDetailsActivity.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        feedLIstRecyclerView.setLayoutManager(layoutManager);
        feedLIstRecyclerView.setAdapter(adapter);
        blurView.setVisibility(View.GONE);
    }


    public static List<EmoziesModal> loadCountries(EventDetailsActivity eventDetailsActivity) {
        try {
            try {
                JSONArray mEmoziArray = new JSONArray(loadJSONFromAsset(eventDetailsActivity));
                List<EmoziesModal> mList = new ArrayList<>();
                for (int i = 0; i < mEmoziArray.length(); i++) {

                    JSONObject object = mEmoziArray.getJSONObject(i);
                    EmoziesModal mModal = new EmoziesModal();
                    mModal.setName(object.getString("name"));
                    mModal.setCharacter(object.getString("character"));

                    List<String> mAlias = new ArrayList<>();
                    JSONArray mAliasArray = object.getJSONArray("aliases");
                    for (int x = 0; x < mAliasArray.length(); x++) {
                        mAlias.add(mAliasArray.get(x).toString());
                    }
                    mModal.setAliases(mAlias);

                    List<String> mGroup = new ArrayList<>();
                    JSONArray mGroupArray = object.getJSONArray("groups");
                    for (int x = 0; x < mGroupArray.length(); x++) {
                        mGroup.add(mGroupArray.get(x).toString());
                    }
                    mModal.setGroups(mGroup);
                    mList.add(mModal);
                }
                return mList;
            } catch (JSONException je) {
                je.printStackTrace();
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String loadJSONFromAsset(Context context) {
        String json = null;
        InputStream is = null;
        try {
            AssetManager manager = context.getAssets();
            Log.d("JSON Path ", "AllEmojiNew.json");
            is = manager.open("AllEmojiNew.json");
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

    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.blurView:
                break;

            case R.id.img_eventDetail_back:
                onBackPressed();
                break;

            case R.id.img_no_member:

//                if (!userExistOrNotonActivty.equals("")) {
//                    cantJoinNotExixtUserDialog(userExistOrNotonActivty);
//
//                } else {
                    Intent intent1 = new Intent(this, TheRoomActivity.class);
                    intent1.putExtra("noMemberYet", "No");
                if(fromTrending){
                    intent1.putExtra("fromTrending", true);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    finish();
                }
                else {
                    startActivity(intent1);
                }
                //}
                break;

            case R.id.img_ListIcon:
                Intent intent = new Intent(EventDetailsActivity.this, OnBoardActivity.class);
                intent.putExtra("eventid", event.getEvent());
                intent.putExtra("venuid", event.getVenue());
                intent.putExtra("object", event);
                intent.putExtra("currentLatLng", currentLatLng);
                if(fromTrending){
                    intent.putExtra("fromTrending", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    startActivity(intent);
                }
                break;


            case R.id.addButtonForKeyInuser:
                //For key in
                if (eventDetails != null && eventDetails.getProfile_rating() != null) {
                    if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {

                        try {
                            keyInToEvent();
//                            if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
//                                addUserIntoEvent(-1);
//                            } else if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
//                                addUserIntoEvent(-1);
//                            } else {
//                                if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE) {
//                                    blurView.setVisibility(View.VISIBLE);
//                                    adapter.userExistOrNot = "notStart";
//                                    userExistOrNotonActivty = "notStart";
//
//                                } else if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
//                                    blurView.setVisibility(View.VISIBLE);
//                                    adapter.userExistOrNot = "notStart";
//                                    userExistOrNotonActivty = "notStart";
//                                } else {
//                                    blurView.setVisibility(View.VISIBLE);
//                                    adapter.userExistOrNot = "notArrived";
//                                    userExistOrNotonActivty = "notArrived";
//                                }
//                                //cantJoinDialog();
//                            }
                        } catch (Exception d) {
                            d.getMessage();
                        }
                    }
                    else {
                        isKeyInAble = true;
                    }
                }
                break;

            case R.id.img_postImage:

                if (eventDetails != null && eventDetails.getProfile_rating() != null) {
                    if (!eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {

                        try {
                            if (userInfo().makeAdmin.equals(Constant.ADMIN_YES) && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                                captureImage();
                            } else if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                                captureImage();

                            } else {
                                cantJoinDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utility.showToast(this, getResources().getString(R.string.somethingwentwrong), 0);
                        }
                    } else {
                        cantJoinDialog();
                    }
                }

                break;


            case R.id.txt_post_comment:

                if (eventDetails != null && eventDetails.getProfile_rating() != null) {
                    if (!eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
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
                    } else {
                        cantJoinDialog();
                    }
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

    //....................GetAllDataApi...........................................................

    @Override
    public void onDestroy() {
        if (timerHttp != null) timerHttp.cancel();
        timerHttp = null;
        mHandler.removeCallbacks(mRunnable);
        //dialog = null;
        super.onDestroy();
    }
    //endGetAllDataApi........................................................

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
                            //incrementKeyPoints(getString(R.string.kp_keyin));
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
                Object objectType = obj1.get("eventattendy");

                if (objectType instanceof String) {
                    if (eventDetails.getAttendyList() == null) {
                        img_no_member.setVisibility(View.VISIBLE);
                    }

                } else if (objectType instanceof JSONArray) {
                    eventDetails.setAttendyJson(obj1.getJSONArray("eventattendy"), this);
                    img_no_member.setVisibility(View.GONE);

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
                Object objectType = obj1.get("allfeeds");

                if (objectType.equals("")) {
                    feedList.clear();
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
                                //incrementKeyPoints("");
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
                } else if (objectType instanceof String) {

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
                                        //incrementKeyPoints("");
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
                    //feedlikeList.add(0, null);
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
                if (user.has("currentDate")) userInfo().currentDate = (user.getString("currentDate"));
                updateSession(userInfo());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addButtonForKeyInuser.callOnClick();

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
                            showKeyPoints("+5");
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

    //----------------follow / unfollow -----------------------------------
    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id) {
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // get response
                    try {


                    } catch (Exception e) {
                        Utility.showToast(EventDetailsActivity.this, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
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
            VolleySingleton.getInstance(EventDetailsActivity.this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
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
        CircularImageView comeInUserProfile = null;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = findViewById(R.id.comeInUser_lnr);
        parent.removeAllViews();

        int loopCount  = list.size();
        if(loopCount >5){
            loopCount = 5;
        }
        if (list.size() != 0) {
            parent.setVisibility(View.VISIBLE);
            img_no_member.setVisibility(View.GONE);
        } else {
            parent.setVisibility(View.GONE);
            img_no_member.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < list.size(); i++) {
            assert inflater != null;
            View v = inflater.inflate(R.layout.trend_user_view, null);
            comeInUserProfile = v.findViewById(R.id.comeInProfile_t);
            TextView no_count = v.findViewById(R.id.no_count_t);
            RelativeLayout marginlayout = v.findViewById(R.id.mainProfileView_t);

            if (i == 0) {

                parent.addView(v, i);
                String image = "";

                if (!list.get(i).getUserimage().contains("dev-")) {
                    image = "dev-" + list.get(i).getUserimage();
                } else {
                    //image = keyInUserModalList.get(i).userImage;
                    image = list.get(i).getUserimage();
                }

                Glide.with(getApplicationContext()).load(image)
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
                        image = "dev-" + list.get(i).getUserimage();
                    } else {
                        image = list.get(i).getUserimage();
                    }

                    Glide.with(getApplicationContext()).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                }
                else
                    if (i == 2) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(15 * i, 0, 0, 0);
                        marginlayout.setLayoutParams(params);
                        parent.addView(v, i);
                        String image = "";

                        if (!list.get(i).getUserimage().contains("dev-")) {
                            image = "dev-" + list.get(i).getUserimage();
                        } else {
                            image = list.get(i).getUserimage();
                        }

                        Glide.with(getApplicationContext()).load(image)
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
//                if (!userExistOrNotonActivty.equals("")) {
//                    cantJoinNotExixtUserDialog(userExistOrNotonActivty);
//
//                } else {
                    Intent intent = new Intent(EventDetailsActivity.this, TheRoomActivity.class);
                    intent.putExtra("commentPesionList", list);
                    intent.putExtra("eventid", event.getEvent());
                    intent.putExtra("venuid", event.getVenue());
                    intent.putExtra("object", event);
                    intent.putExtra("currentLatLng", currentLatLng);
                if(fromTrending){
                    intent.putExtra("fromTrending", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    startActivity(intent);
                }
              //  }
            }
        });
    }

    public void cantJoinNotExixtUserDialog(String userStaus) {

        if (userStaus.equals("notStart")) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else if (userStaus.equals("notStart")) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else if (userStaus.equals("notArrived")) {
            utility.showCustomPopup(getString(R.string.enotat), String.valueOf(R.font.montserrat_medium));
        }
    }

    public void cantJoinDialog() {

        if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE) {
            blurView.setVisibility(View.VISIBLE);
            adapter.userExistOrNot = "notStart";
            userExistOrNotonActivty = "notStart";

        } else if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
            blurView.setVisibility(View.VISIBLE);
            adapter.userExistOrNot = "notStart";
            userExistOrNotonActivty = "notStart";
        } else {
            blurView.setVisibility(View.VISIBLE);
            adapter.userExistOrNot = "notArrived";
            userExistOrNotonActivty = "notArrived";
            utility.showCustomPopup(getString(R.string.enotat), String.valueOf(R.font.montserrat_medium));
        }
    }

    /*.........................captureImage.............................*/
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

    /*.........................deleteFeed.............................*/
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

    /*.........................deleteFeedApi().............................*/
    private void deleteFeedApi(final String feedId) {
        showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.DELETE_FEED, new Response.Listener<String>() {
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
                    params.put("feed_id", feedId);
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

    /*.........................showFeedSmilyList().............................*/
    private void showFeedSmilyList(final String feedSmilyId, String reaction) {

        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.custom_smyliy_list);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RecyclerView smaesamlyUserRview = dialog.findViewById(R.id.smaesamlyUserRview);

        smilyUserAdapter = new SmilyUserAdapter(this, reactionUserLIst);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        smaesamlyUserRview.setLayoutManager(layoutManager);
        smaesamlyUserRview.setAdapter(smilyUserAdapter);


        txt_show_emojies.setText(reaction);
        txt_show_emojies.setVisibility(View.VISIBLE);

        dialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        txt_show_emojies.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                }
        );

        getReactionUSerList(feedSmilyId);

        dialog.show();
    }

    private void deletFeedRaction(final String deleteFeedReaction) {

        showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.DELETEREACTION_USER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.v("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        reactionUserLIst.clear();
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
                    params.put("feed_reaction_id", deleteFeedReaction);
                    params.put("user_id", userInfo.userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(this, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

    private void addFeedFormListreaction(final String getReactionFromList, final String addFeedReaction) {

        showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADDREACTION_USER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.v("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        reactionUserLIst.clear();
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
                    params.put("event_id", eventId);
                    params.put("feed_id", addFeedReaction);
                    params.put("reaction", getReactionFromList);
                    params.put("user_id", userInfo.userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(this, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }


    }

    private void addFeedreaction(final String addFeedReaction) {

        showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADDREACTION_USER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.v("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        reactionUserLIst.clear();
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
                    params.put("event_id", eventId);
                    params.put("feed_id", addFeedReaction);
                    params.put("reaction", "❤");
                    params.put("user_id", userInfo.userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(this, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }


    }

    private void getReactionUSerList(final String feedSmilyId) {

        // showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.REACTION_USER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // dismissProgDialog();
                    Log.v("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        reactionUserLIst.clear();
                        if (status.equals("success")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("ractionUser");
                            for (int r = 0; r < jsonArray.length(); r++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(r);
                                ReactionUserModal reactionUserModal = new ReactionUserModal();
                                if (jsonObject1.has("id"))
                                    reactionUserModal.id = (jsonObject1.getString("id"));
                                if (jsonObject1.has("user_id"))
                                    reactionUserModal.user_id = (jsonObject1.getString("user_id"));
                                if (jsonObject1.has("user_image"))
                                    reactionUserModal.user_image = (jsonObject1.getString("user_image"));
                                if (jsonObject1.has("fullname"))
                                    reactionUserModal.fullname = (jsonObject1.getString("fullname"));
                                reactionUserLIst.add(reactionUserModal);
                            }

                            smilyUserAdapter.notifyDataSetChanged();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("feed_reaction_id", feedSmilyId);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(this, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
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
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
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

/*
    Dialog dialog = null;
    private void showKeyPoints(String s) {
        dialog = new Dialog(this);
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
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    if (dialog != null)
                    dialog.dismiss();
                }
            }
        };
        mHandler.postDelayed(mRunnable, 3000);

        if (dialog != null) {
            dialog.show();
        }
    }*/


    private void showKeyPoints(String s) {
        final Dialog dialog = new Dialog(this);

        try {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.custom_keypoint_layout);
            assert dialog.getWindow() != null;
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

                    try {
                        dialog.dismiss();
                    } catch (Exception e) {

                        e.printStackTrace();
                        dialog.dismiss();
                    }


                }
            }, 1500);

            dialog.show();
        } catch (Exception e) {

            e.printStackTrace();
            dialog.dismiss();
        }
    }

    public String getCurrentTimeInFormat() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())).format(new Date(System.currentTimeMillis()));
    }

    /****
     * This method is used when The user is not exist in the event to first time key in the user
     *
     * @param type must be 0 or 1
     */
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
                            isKeyInAble = true;
                            feedList.clear();
                            adapter.notifyDataSetChanged();
                        }

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
        showProgDialog(false, TAG);
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

                        showProgDialog(false, TAG);
                        Feeds feeds = new Feeds();
                        feeds.type = Constant.FEED_TYPE_PICTURE;
                        feeds.date = cutrrentDate;
                        feeds.user_status = userInfo.user_status;
                        feeds.userimage = userInfo.getUserImage();
                        feeds.username = userInfo.userName;
                        feeds.feed = result.getUri().toString();
                        feeds.isUri = true;
                        feedList.add(0, feeds);
                        adapter.notifyDataSetChanged();
                        feedLIstRecyclerView.scrollToPosition(0);

                        Bitmap eventImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                        if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST))
                            keyInToEvent();
                           // addUserIntoEvent(1);
                        else sendPicture(eventImg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2 && data.hasExtra("incresePoint")) {

                if (data.hasExtra("incresePoint")) {

                    String requiredValue = data.getStringExtra("incresePoint");
                    if (requiredValue.equals("1")) {

                    }
                }
            }
        }
    }



    private void keyInToEvent(){

        try {
            if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
                addUserIntoEvent(-1);
            } else if (getDistance(new Double[]{Double.valueOf(event.getVenue().getLatitude()), Double.valueOf(event.getVenue().getLongitude()), Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE
                    &&isEventOnline(event.getEvent().event_date,userInfo().currentDate)) {
                addUserIntoEvent(-1);
            } else {
                if (getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE) {
                    blurView.setVisibility(View.VISIBLE);
                    adapter.userExistOrNot = "notStart";
                    userExistOrNotonActivty = "notStart";

                } else if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
                    blurView.setVisibility(View.VISIBLE);
                    adapter.userExistOrNot = "notStart";
                    userExistOrNotonActivty = "notStart";
                } else {
                    blurView.setVisibility(View.VISIBLE);
                    adapter.userExistOrNot = "notArrived";
                    userExistOrNotonActivty = "notArrived";
                }
                //cantJoinDialog();
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

}

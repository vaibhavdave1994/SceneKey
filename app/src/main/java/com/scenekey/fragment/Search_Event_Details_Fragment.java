package com.scenekey.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.TheRoomActivity;
import com.scenekey.adapter.GridChipsAdapter;
import com.scenekey.adapter.Profile_Adapter;
import com.scenekey.adapter.UserInAdapter;
import com.scenekey.aws_service.AWSImage;
import com.scenekey.cropper.CropImage;
import com.scenekey.cropper.CropImageView;
import com.scenekey.cus_view.Grid_multiRow;
import com.scenekey.cus_view.ProfilePopUp_Notification;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomeClick;
import com.scenekey.helper.Permission;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.StatusBarHide;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.Events;
import com.scenekey.model.Feeds;
import com.scenekey.model.NotificationData;
import com.scenekey.model.Tags;
import com.scenekey.model.UserInfo;
import com.scenekey.otherClass.SearchEvent;
import com.scenekey.util.ImageUtil;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Search_Event_Details_Fragment extends Fragment implements View.OnClickListener, StatusBarHide {

    private static final String FROM_TAB = "from_tab";
    private static Timer timerHttp;
    private static UserInfo userInfo;
    public final String TAG = Event_Fragment.class.toString();
    public boolean canCallWebservice, isInfoVisible, isPopUpShowing, canGetNotification;
    public Double latitude, longitude;
    public TextView txt_event_name, txt_discrp, txt_f2_badge, btn_got_it, txt_discipI_f2, txt_hide_all;
    public ArrayList<Card> cardsList;
    private LinearLayout info_view;
    private ImageView img_infoget_f2, img_f10_back;
    private RecyclerView usercomeInRecyclerView;
    private ScrollView scrl_all;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private String eventId, venueName;
    private String[] currentLatLng;
    private int currentNudge, noNotify, timer;
    private Handler handler;
    private View popupview;
    private Dialog dialog;
    private Timer timerNudge;
    private Uri imageUri;
    private SearchEvent searchEvent;
    private Events event;
    private ArrayList<NotificationData> nudgeList;
    //map data
    private MapView map_view;
    private GoogleMap googleMap;
    private ProfilePopUp_Notification popup;
    private AWSImage awsImage;
    private CognitoCredentialsProvider credentialsProvider;



    //New Code Shubham
    private RecyclerView listViewFragProfile;
    private Profile_Adapter adapter;
    private ArrayList<Feeds> feedsList;
    private ArrayList<EventAttendy> userLIst;
    private boolean myProfile;
    private EventAttendy attendy;
    private boolean isKeyInAble = false;
    private String from_tab;
    private TextView tv_no_members;

    public static Search_Event_Details_Fragment newInstance(String from_tab) {
        Bundle args = new Bundle();
        Search_Event_Details_Fragment fragment = new Search_Event_Details_Fragment();
        fragment.setArguments(args);
        args.putString(FROM_TAB, from_tab);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getString(FROM_TAB) != null) {
                from_tab = getArguments().getString(FROM_TAB);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_demo_event, container, false);
        //for status bar manage
        userInfo = SceneKey.sessionManager.getUserInfo();
        activity.setTopStatus();
        activity.showProgDialog(false, TAG);
        //TODO handling on grid adapter click if user is not key in
        //usercomeInRecyclerView = view.findViewById(R.id.usercomeInRecyclerView);

        map_view = view.findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        map_view.onResume();

        // New Code
        userLIst = new ArrayList<>();
        txt_event_name = view.findViewById(R.id.txt_event_name);

        txt_hide_all = view.findViewById(R.id.txt_hide_all);
        tv_no_members = view.findViewById(R.id.tv_no_members);

        activity.setBBVisibility(View.GONE, TAG);

        txt_discipI_f2.setText(event.getVenue().getAddress());


        //New Code Shubham

        try {
            attendy = new EventAttendy();
            attendy.userid = (userInfo.userid);
            attendy.userFacebookId = (userInfo.userFacebookId);
            attendy.setUserimage(userInfo.getUserImage());
            attendy.username = (userInfo.userName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        listViewFragProfile = view.findViewById(R.id.listViewFragProfile);

        feedsList = new ArrayList<>();
        adapter = new Profile_Adapter(context, userInfo.userid,feedsList,userLIst,eventId,null,null,null,null);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        listViewFragProfile.setLayoutManager(layoutManager);
        listViewFragProfile.setAdapter(adapter);


        /*..................................................................*/

        //keypoint decrement if event distance is more than 100m
        int distance = activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])});
        if (distance > Constant.MAXIMUM_DISTANCE) {
            activity.keyPointsUpdate();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                activity.showProgDialog(false, TAG);
                getAllData();
            }
        });

        if (userInfo().firstTimeDemo) {
            userInfo().firstTimeDemo = (false);
            activity.updateSession(userInfo());

        }

        if (timerHttp == null) setDataTimer();

        isInfoVisible = false;
        // rclv_grid.hasFixedSize();
        txt_event_name.setText("");

        // New Code
        setOnClick(tv_no_members, img_f10_back, img_infoget_f2); //mainlayout

        cardsList = new ArrayList<>();
        info_view.setVisibility(View.GONE);

        awsImage = new AWSImage(activity);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mapAsyncer(latitude, longitude);
            }
        });

        activity.dismissProgDialog();
        activity.hideStatusBar();

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
    }

    private UserInfo userInfo() {
        return activity.userInfo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
        utility = new Utility(context);
    }

    @Override
    public void onStart() {
        super.onStart();

        activity.setBBVisibility(View.GONE, TAG);
        canCallWebservice = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setBBVisibility(View.GONE, TAG);
        activity.hideStatusBar();
        canCallWebservice = true;
        if (timerHttp == null) setDataTimer();
    }

    @Override
    public void onPause() {
        if (timerHttp != null) timerHttp.cancel();
        timerHttp = null;
        super.onPause();
    }


    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_no_members:
                Intent intent = new Intent(context, TheRoomActivity.class);
                intent.putExtra("noMemberYet", "No");
                startActivity(intent);
                break;
        }
    }


    private void captureImage() {

        final Dialog dialog = new Dialog(context);
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
                Permission permission = new Permission(activity);
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

    private void callIntent(int caseId) {

        switch (caseId) {
            case Constant.INTENT_CAMERA:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
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

    /****
     * This method is used when The user is not exist in the event to first time key in the user
     *
     * @param type must be 0 or 1
     */
    private void addUserIntoEvent(final int type, @Nullable final Bitmap bitmap) {
        if (type != -1) activity.showProgDialog(false, TAG);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.getInt("success") == 0) {
                            activity.incrementKeyPoints(getString(R.string.kp_keyin));
                        }
                        if (type == 0) likeEvent();
                        else if (type == 1) sendPicture(bitmap);
                        getAllData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("userid", userInfo().userid);
                    params.put("eventname", searchEvent.getProfile_rating().getEvent_name());
                    params.put("eventid", eventId);
                    params.put("Eventdate", searchEvent.getProfile_rating().getDate_in_format());

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }

    }

    /***
     * Event like volley
     */
    private void likeEvent() {
        activity.showProgDialog(false, TAG);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_LIKE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("success")) if (object.getInt("success") == 1) {
                            if (object.getString("msg").contains(" liked the event.")) {
                                activity.showCustomPopup("You liked this event.", 1);


                            } else if (object.getString("msg").contains("unliked the event.")) {
                                activity.showCustomPopup(getString(R.string.kp_unlike), 0);
                            }
                            getAllData();
                            //{"success":1,"msg":"your have liked the event."}
                            //{"success":1,"msg":"your have unliked the event."}
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utility.showToast(context, getResources().getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("user_id", userInfo().userid);
                    params.put("event_id", eventId);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
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
                    activity.dismissProgDialog();
                    // get response
                    Utility.e("Send picture response", response);
                    getAllData();
                    try {
                        JSONObject respo = new JSONObject(response);
                        if (respo.getInt("success") == 0) {
                            Utility.showToast(context, respo.getString("msg"), 0);
                            activity.showCustomPopup("Photo has been posted successfully.", 1);
                        } else {
                            activity.showCustomPopup("Photo has been posted successfully.", 1);
                        }
                    } catch (Exception e) {
                        activity.incrementKeyPoints(getString(R.string.kp_img_post));
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
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
                    params.put("ratingtime", activity.getCurrentTimeInFormat());

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }


    }

    private String getLocation() {
        String result;
        if (userInfo().address.length() > 1) {
            result = userInfo().address;
        } else {
            result = activity.getAddress(Double.parseDouble(currentLatLng[0]), Double.parseDouble(currentLatLng[1]));
        }
        return result;
    }

    /**
     * The dialogue use to show if user is not in the range of the event and evneet is not started yet
     */
    public void cantJoinDialog() {

        if (activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else if (activity.userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else {
            utility.showCustomPopup(getString(R.string.enotat), String.valueOf(R.font.montserrat_medium));
        }

    }

    /**
     * @param response the responce provided by getAlldata()
     * @throws JSONException
     */
    private void getResponse(String response) throws Exception {


        Log.e("Responce12", response);
        JSONObject obj1 = new JSONObject(response);
        activity.dismissProgDialog();
        if (searchEvent == null) searchEvent = new SearchEvent();
        try {
            if (obj1.has("eventattendy")) {
                // searchEvent.setAttendyJson(obj1.getJSONArray("eventattendy"), this);
                Object objectType = obj1.get("eventattendy");

                if (objectType instanceof String) {
                    if (searchEvent.getAttendyList() == null) {
                        tv_no_members.setVisibility(View.VISIBLE);
                        usercomeInRecyclerView.setVisibility(View.GONE);
                    }

                } else if (objectType instanceof JSONArray) {
                    searchEvent.setAttendyJson(obj1.getJSONArray("eventattendy"), this);
                    tv_no_members.setVisibility(View.GONE);
                    usercomeInRecyclerView.setVisibility(View.VISIBLE);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("nudges_count"))
                searchEvent.setNudges_count(obj1.getString("nudges_count"));
            noNotify = Integer.parseInt(searchEvent.getNudges_count());
            setTextBadge();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("allTags")) {
                searchEvent.setTagList(obj1.getJSONArray("allTags"), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("allfeeds")) {
                //  searchEvent.setFeedsJson(obj1.getJSONArray("allfeeds"), this);

                Object objectType = obj1.get("allfeeds");

                if (objectType instanceof String) {

                    if (searchEvent.getFeedList() == null) {
                        if (feedsList.size() == 0) {
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
                                        feeds.feed = "Hi " + activity.userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                        activity.incrementKeyPoints("");
                                        getAllData();
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    }

                                } else if (from_tab.equals("trending")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + activity.userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                    }
                                } else if (from_tab.equals("map_tab")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + activity.userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                    }
                                }
                                feedsList.add(feeds);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }

                } else if (objectType instanceof JSONArray) {
                    searchEvent.setFeedsJson(obj1.getJSONArray("allfeeds"), this);
                    feedsList.addAll(searchEvent.getFeedList());
                    adapter.notifyDataSetChanged();
                    Log.e("FEEDS LIST SIZE", searchEvent.getFeedList().size() + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (obj1.has("event_profile_rating")) {
                searchEvent.setProfile_ratingJSon(obj1.getJSONObject("event_profile_rating"), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("userInfo")) {

                Object intervention = obj1.get("userInfo");
                if (intervention instanceof JSONArray) {
                    SceneKey.sessionManager.logout(activity);
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
                activity.updateSession(userInfo());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int height = (int) getResources().getDimension(R.dimen._125sdp);//activity().ActivityWidth;
        int width = activity.ActivityWidth;
        String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=12&size=" + width + "x" + height + "&sensor=false";
        Utility.e(TAG, "URL" + url + "Lat lin" + latitude + " : " + longitude);


        if (cardsList.size() <= 0) {
            Card card = new Card();
            card.imageUrl = null;
            card.text = "Welcome to the " + venueName + "! Join the fun! Share your pics & comments right here!";
            cardsList.add(card);
        }
    }

    private void mapAsyncer(final double lat, final double lng) {
        map_view.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                Marker m = null;
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //callPermission(Constants.TYPE_PERMISSION_FINE_LOCATION);
                        } else if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //callPermission(Constants.TYPE_PERMISSION_CORAS_LOCATION);
                        }
                    }
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(lat, lng);


                final Marker mr = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .anchor(0.5f, 0.5f)
                        .title(event.getVenue().getVenue_name())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                mr.showInfoWindow();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (searchEvent.getProfile_rating().getVenue_long() != null)
                            activity.addFragment(new SingleMap_Fragment().setData(searchEvent.getProfile_rating().getVenue_lat(), searchEvent.getProfile_rating().getVenue_long()), 1);
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if (searchEvent.getProfile_rating().getVenue_long() != null)
                            activity.addFragment(new SingleMap_Fragment().setData(searchEvent.getProfile_rating().getVenue_lat(), searchEvent.getProfile_rating().getVenue_long()), 1);
                        marker.showInfoWindow();

                        return true;
                    }
                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (searchEvent.getProfile_rating().getVenue_long() != null)
                            activity.addFragment(new SingleMap_Fragment().setData(searchEvent.getProfile_rating().getVenue_lat(), searchEvent.getProfile_rating().getVenue_long()), 1);
                        mr.showInfoWindow();

                    }
                });//TODO check with iphone

                Handler handler = new Handler();
                final Marker finalM = m;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalM != null) finalM.showInfoWindow();
                    }
                }, 2000);


            }
        });

    }

    /**
     * text badge count from 15 to 0 sec.
     */
    private void setTextBadge() {

        txt_f2_badge.setText(noNotify + "");
        if (noNotify > 0) {
            txt_f2_badge.setBackground(getResources().getDrawable(R.drawable.bg_circle_red_badge));

            if (noNotify > 99) txt_f2_badge.setText("99+");
            txt_f2_badge.setVisibility(View.VISIBLE);
        } else {
            txt_f2_badge.setVisibility(View.GONE);
        }
    }

    private void setDataTimer() {
        if (timerHttp == null) timerHttp = new Timer();

        //Set the schedule function and rate
        //TODO timer changed as required
        timerHttp.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {
                                              activity.runOnUiThread(new Runnable() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {


            if (requestCode == Constant.INTENT_CAMERA) {

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160, 160).setMaxCropResultSize(4000, 3500).setAspectRatio(400, 300).start(context, this);

                } else {
                    Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null) {
                        Bitmap eventImg = MediaStore.Images.Media.getBitmap(context.getContentResolver(), result.getUri());
                        if (searchEvent.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST))
                            addUserIntoEvent(1, eventImg);
                        else sendPicture(eventImg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Utility.showToast(context, "permission denied by user ", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (timerHttp != null) timerHttp.cancel();
        if (timerNudge != null) timerNudge.cancel();
        timerHttp = null;
        timerNudge = null;

        for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
            try {
                ((Trending_Fragment) fragment).getTrendingData();
                activity.setTitle(getString(R.string.trending));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ((Map_Fragment) fragment).checkEventAvailability();
                activity.setTitle(getString(R.string.map));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((Event_Search_Tag_Fragment) fragment).setVisibility();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        VolleySingleton.getInstance(context).cancelPendingRequests(TAG);
        activity.showStatusBar();
        //  handler.removeCallbacksAndMessages(null);
        activity.setBBVisibility(View.VISIBLE, 300, TAG);
        super.onDestroyView();
    }

    @Override
    public boolean onStatusBarHide() {
        return false;
    }

    /* common methods used somewhere else  */

    /*public void addChips(ArrayList<Tags> tag) {
        try {
            Grid_multiRow layout = this.getView().findViewById(R.id.chip_linear);
            layout.setAdapter(new GridChipsAdapter(context, tag));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

//    }

    public Search_Event_Details_Fragment setData(String eventId, String venueName, Events event, String[] currentLatLng, String[] venuLatLng, boolean isKeyInAble) {
        this.eventId = eventId;
        this.venueName = venueName;
        this.event = event;
        this.currentLatLng = currentLatLng;
        latitude = Double.valueOf(venuLatLng[0]);
        longitude = Double.valueOf(venuLatLng[1]);
        this.isKeyInAble = isKeyInAble;
        return this;
    }

    /**
     * GetALl the data for that event
     */
    public void getAllData() {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LISTEVENTFEED, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    // get response
                    try {
                        if (response != null) getResponse(response);
                        else Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
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
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    /***
     * For setting the Grid Layout of room Persons showing at bottom of the Room
     *
     * @param list
     */
    public void setRecyclerView(ArrayList<EventAttendy> list) {

        userLIst = list;
        UserInAdapter adapter = new UserInAdapter(list, context,eventId);
        usercomeInRecyclerView.setAdapter(adapter);
    }

    public void addNudge(final String attendyId, final String attendyFBID, final String nudge, final Dialog profilePop) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_NUDGE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();

                    activity.showCustomPopup(getResources().getString(R.string.goodNudge), 1);

                    if (dialog != null) dialog.dismiss();
                    if (profilePop != null) profilePop.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    if (dialog != null) dialog.dismiss();
                    Utility.showToast(getContext(), getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("event_id", eventId);
                    params.put("nudges_to", attendyId);
                    params.put("nudges_by", userInfo().userid);
                    params.put("facebook_id", attendyFBID);
                    params.put("nudges", nudge);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    /**
     * Tost shown at popup of user.
     */
    public void cantInteract() {
        utility.showCustomPopup(getString(R.string.sorryEvent), String.valueOf(R.font.montserrat_medium));
    }

    public boolean check() throws ParseException {
        boolean result;
        if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
            result = true;
        } else if (activity.getDistance(new Double[]{latitude, longitude, Double.parseDouble(currentLatLng[0]), Double.parseDouble(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && activity.checkWithTime(searchEvent.getProfile_rating().getEvent_date(), Double.parseDouble(searchEvent.getProfile_rating().getInterval()))) {
            if (searchEvent.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
                result = false;//addUserIntoEvent(0, null);
            } else result = true;
        } else {
            result = false;
        }
        if (searchEvent.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
            result = false;//addUserIntoEvent(0, null);
        }

        return result;
    }

    public void noNotification() {
        utility.showCustomPopup(getString(R.string.noNotification), String.valueOf(R.font.montserrat_medium));
    }

    public void popupNotification_New() {
        activity.showProgDialog(false, TAG);
        NotificationData nudge = nudgeList.get(nudgeList.size() - 1);
        awsImage.downloadFileFromS3(awsImage.getFacebookId(nudge.facebook_id, nudge.user_id), (credentialsProvider == null ? credentialsProvider = awsImage.getCredentials() : credentialsProvider));
        currentNudge = 0;
        isPopUpShowing = true;
        popup = new ProfilePopUp_Notification(activity, awsImage, 4, nudge) {
            @Override
            public void onClickView(TextView textView, ProfilePopUp_Notification profilePopUp) {
                profilePopUp.setText(textView.getText().toString());
            }

            @Override
            public void onSendCLick(TextView textView, ProfilePopUp_Notification profilePopUp, NotificationData obj) {
                Log.e("Value ", profilePopUp.list.toString());
                String s = profilePopUp.list.toString();
                byte[] ptext = (s = s.substring(1, s.length() - 1).replace("", "")).getBytes();

                addNudge(obj.user_id, obj.facebook_id, StringEscapeUtils.escapeJava(s).replace(" +", ""), profilePopUp);
            }

            @Override
            public void onPrevClick(ImageView textView, ProfilePopUp_Notification profilePopUp) {
                if (currentNudge > 0) {
                    currentNudge -= 1;
                    updateData(nudgeList.get(currentNudge));
                } else {
                    Toast.makeText(getContext(), "No nudge available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNextClick(ImageView textView, ProfilePopUp_Notification profilePopUp) {
                if (currentNudge == (nudgeList.size() - 1) && canGetNotification) {
                } else {
                    currentNudge += 1;
                    updateData(nudgeList.get(currentNudge));
                }
            }

            @Override
            public void onDismiss(ProfilePopUp_Notification profilePopUp) {
                nudgeList.clear();
                isPopUpShowing = false;
            }
        };
        popup.show();
    }

    private void callAddEventApi(final String event_id, final String venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(context);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        activity.dismissProgDialog();
                        jsonObject = new JSONObject(Response);
                        String status = jsonObject.getString("status");

                        if (status.equals("event_Added")) {
                            isKeyInAble = true;
                            feedsList.clear();
                            getAllData();
                            adapter.notifyDataSetChanged();
                        } else if (status.equals("exist")) {
                            isKeyInAble = true;
                            feedsList.clear();
                            getAllData();
                            adapter.notifyDataSetChanged();
                        }

                    } catch (Exception ex) {
                        activity.dismissProgDialog();
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userid", activity.userInfo().userid);
                    params.put("eventname", object.getEvent().event_name);
                    params.put("eventid", object.getEvent().event_id);
                    params.put("Eventdate", object.getEvent().event_date);

                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }
}

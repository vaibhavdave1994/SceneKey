package com.scenekey.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.OnBoardActivity;
import com.scenekey.activity.TheRoomActivity;
import com.scenekey.activity.trending_summery.Summary_Activity;
import com.scenekey.adapter.MapInfo_Adapter;
import com.scenekey.adapter.TrendingFeedSlider;
import com.scenekey.helper.Constant;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.model.Events;
import com.scenekey.model.ImageSlidModal;
import com.scenekey.model.KeyInUserModal;
import com.scenekey.model.UserInfo;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Map_Fragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private static Timer timerHttp;
    private final String TAG = Map_Fragment.class.toString();
    public boolean canCallWebservice;
    String targetdatevalue = "";
    // New Code
    String returnDay = "";
    private Context context;
    private HomeActivity activity;
    private String lat = "", lng = "";
    private boolean clicked = false;
    private Utility utility;
    private MapView mMapView;
    private GoogleMap googleMap;
    private MapInfo_Adapter mapInfoAdapter;
    private ArrayList<Events> eventArrayMarker;
    private Marker lastClick;
    private int position;
    private Events events;
    private ImageView heart;
    private TextView txt_like;
    private LinearLayout ll_heart;
    private TextView tv_follow;
    private ViewGroup comeInUser_lnr;
    private CheckEventStatusListener listener;
    private ImageView iv_group;
    private ImageView iv_summery;

    public static String parseDate(String inputDateString, SimpleDateFormat inputDateFormat, SimpleDateFormat outputDateFormat) {
        Date date = null;
        String outputDateString = null;
        try {
            date = inputDateFormat.parse(inputDateString);
            outputDateString = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        activity.setTitleVisibility(View.VISIBLE);
        mMapView = v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        SceneKey.sessionManager.putMapFragment("map");
        if(!Utility.checkInternetConnection1(activity)){
            Utility.showCheckConnPopup(activity,"No network connection","","");
        }

        mMapView.onResume();
        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle(context.getResources().getString(R.string.map));

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                sydney = new LatLng(38.2492, -122.0405);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14));
                googleMap.setMinZoomPreference(13);
            }
        });
        showNearByEventMarker();
    }

    @Override
    public void onStart() {
        super.onStart();
        canCallWebservice = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Utility.e(TAG,"TimerVolley start");
        canCallWebservice = true;
        // if (timerHttp == null) setDataTimer();
    }

    private void showNearByEventMarker() {
        String[] latLng = activity.getLatLng();
        lat = latLng[0];
        lng = latLng[1];
        if (lat.equals("0.0") && lng.equals("0.0")) {
            latLng = activity.getLatLng();
            lat = latLng[0];
            lng = latLng[1];

            retryLocation();
        } else {
            activity.showProgDialog(false, TAG);
            checkEventAvailability();
            // if (timerHttp == null) setDataTimer();
        }
    }

    private void retryLocation() {
        final Dialog dialog = new Dialog(context,R.style.DialogTheme);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_with_btn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //      deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id

        TextView tvCancel, tvTryAgain, tvTitle, tvMessages;

        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvMessages = dialog.findViewById(R.id.tvMessages);
        tvCancel = dialog.findViewById(R.id.tvPopupCancel);
        tvTryAgain = dialog.findViewById(R.id.tvPopupOk);

        tvTitle.setText(R.string.gps_new);
        tvMessages.setText(R.string.couldntGetLocation);

        tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {

                        if (utility.checkNetworkProvider() | utility.checkGPSProvider()) {
                            showNearByEventMarker();
                        } else {
                            utility.checkGpsStatus();
                            showNearByEventMarker();
                        }
                    }
                }, 3 * 1000); // wait for 3 seconds
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.dismissProgDialog();
                dialog.cancel();

            }
        });
        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
        utility = new Utility(context);
    }

    public void checkEventAvailability() {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.NEW_EVENT_BY_LOCAL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.has("status")) {
                            int status = jo.getInt("status");
                            if (status == 0) {
                                activity.dismissProgDialog();
                                showNoEventDialog();
                            }
                            if (jo.has("userInfo")) {
                                UserInfo userInfo = activity.userInfo();
                                JSONObject user = jo.getJSONObject("userInfo");
                                if (user.has("makeAdmin"))
                                    userInfo.makeAdmin = (user.getString("makeAdmin"));

                                if (user.has("lat"))
                                    userInfo.lat = (user.getString("lat"));

                                if (user.has("longi"))
                                    userInfo.longi = (user.getString("longi"));

                               /* if (user.has("adminLat"))
                                    userInfo.adminLat = (user.getString("adminLat"));

                                if (user.has("adminLong"))
                                    userInfo.adminLong = (user.getString("adminLong"));*/

                                if (user.has("adminLat")) {
                                    if (user.getString("adminLat").isEmpty()) {
                                        userInfo.adminLat = user.getString("lat");
                                        userInfo.adminLong = user.getString("longi");
                                        userInfo.currentLocation = true;
                                    } else {
                                        userInfo.adminLat = user.getString("adminLat");
                                        userInfo.adminLong = user.getString("adminLong");
                                        userInfo.currentLocation = false;
                                    }
                                }

                                if (user.has("address"))
                                    userInfo.address = (user.getString("address"));

                                if (user.has("fullname"))
                                    userInfo.fullname = (user.getString("fullname"));

                                if (user.has("key_points"))
                                    userInfo.key_points = (user.getString("key_points"));

                                activity.updateSession(userInfo);

                                // New Code
                                if (user.getString("fullname").equals("")) {
                                    SceneKey.sessionManager.logout(activity);
                                }
                            }
                        } else {
                           /* if (jo.has("userinfo")) {
                                UserInfo  userInfo = activity.userInfo();
                                JSONObject user = jo.getJSONObject("userInfo");
                                if(user.has("makeAdmin"))   userInfo.makeAdmin=(user.getString("makeAdmin"));
                                if(user.has("lat"))         userInfo.latitude=(user.getString("lat"));
                                if(user.has("longi"))       userInfo.longitude=(user.getString("longi"));
                                if(user.has("adminLat"))    userInfo.latitude=(user.getString("adminLat"));
                                if(user.has("adminLong"))   userInfo.longitude=(user.getString("adminLong"));
                                if(user.has("address"))     userInfo.address=(user.getString("address"));
                                if(user.has("fullname"))       userInfo.fullName=(user.getString("fullname"));
                                if(user.has("key_points"))userInfo.keyPoints=(user.getString("key_points"));
                                activity.updateSession(userInfo);
                            }*/

                            // New Code
                            if (jo.has("userInfo")) {
                                UserInfo userInfo = activity.userInfo();
                                JSONObject user = jo.getJSONObject("userInfo");
                                if (user.has("makeAdmin"))
                                    userInfo.makeAdmin = (user.getString("makeAdmin"));

                                if (user.has("lat"))
                                    userInfo.lat = (user.getString("lat"));

                                if (user.has("longi"))
                                    userInfo.longi = (user.getString("longi"));

                                /*if (user.has("adminLat"))
                                    userInfo.adminLat = (user.getString("adminLat"));

                                if (user.has("adminLong"))
                                    userInfo.adminLong = (user.getString("adminLong"));*/

                                if (user.has("adminLat")) {
                                    if (user.getString("adminLat").isEmpty()) {
                                        userInfo.adminLat = user.getString("lat");
                                        userInfo.adminLong = user.getString("longi");
                                        userInfo.currentLocation = true;
                                    } else {
                                        userInfo.adminLat = user.getString("adminLat");
                                        userInfo.adminLong = user.getString("adminLong");
                                        userInfo.currentLocation = false;
                                    }
                                }

                                if (user.has("address"))
                                    userInfo.address = (user.getString("address"));

                                if (user.has("fullname"))
                                    userInfo.fullname = (user.getString("fullname"));

                                if (user.has("key_points"))
                                    userInfo.key_points = (user.getString("key_points"));

                                if (user.has("bio"))
                                    userInfo.bio = (user.getString("bio"));
                                activity.updateSession(userInfo);

                                if (user.getString("fullname").equals("")) {
                                    SceneKey.sessionManager.logout(activity);
                                }
                            }
                            if (jo.has("events")) {
                                if (eventArrayMarker == null) eventArrayMarker = new ArrayList<>();
                                else eventArrayMarker.clear();

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

                                    //   events.setOngoing(events.checkWithTime(events.getEvent().event_date , events.getEvent().interval));

                                    // New Code
                                    checkWithDate(events.getEvent().event_date, events.getEvent().rating, events);

                                    int time_format = 0;
                                    try {
                                        time_format = Settings.System.getInt(context.getContentResolver(), Settings.System.TIME_12_24);
                                    } catch (Settings.SettingNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    events.settimeFormat(time_format);
                                    events.setRemainingTime();

                                    eventArrayMarker.add(events);
                                }
                                prepareMap();
                            }
                        }
                        activity.dismissProgDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
//                        Utility.showToast(context, getResources().getString(R.string.somethingwentwrong), 0);
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
                    params.put("lat", lat);
                    params.put("long", lng);
                    params.put("user_id", activity.userInfo().userid);
                    params.put("updateLocation", Constant.ADMIN_NO);
                    params.put("fullAddress", activity.userInfo().address);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
//            Utility.showCheckConnPopup(activity,"No network connection","","");
//            utility.snackBar(mMapView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    private void prepareMap() {
        Marker marker = null;

        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // For dropping a marker at a point on the Map
        LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));


        //marker.icon(BitmapDescriptorFactory.fromBitmap(ImageUtil.resizeBitmap( ImageUtil.getBitmapByUrl(markerUrl) , 2 ,2)));
        // eventArrayMarker = activity.getEventsArrayList();
        try {
            if (!(eventArrayMarker == null || eventArrayMarker.size() <= 0)) {
                googleMap.clear();
                lastClick = null;
                Map_Fragment.this.getView().findViewById(R.id.all).setVisibility(View.GONE);
                mapInfoAdapter = new MapInfo_Adapter(activity, eventArrayMarker);
                googleMap.setInfoWindowAdapter(mapInfoAdapter);
                for (int position = 0; position < eventArrayMarker.size(); position++) {
                    // Util.printLog(TAG, eventArrayMarker.get(i).getEvent().getEvent_name() + " : " + eventArrayMarker.get(i).getEvent().getEvent_id() + " : " + eventArrayMarker.get(i).getVenue().getLatitude() + " : " + eventArrayMarker.get(i).getVenue().getLongitude());
                    if (position == 0) {
                        sydney = new LatLng(Double.parseDouble(eventArrayMarker.get(position).getVenue().getLatitude()), Double.parseDouble(eventArrayMarker.get(position).getVenue().getLongitude()));
                        marker = createMarker(eventArrayMarker.get(position).getVenue().getLatitude(), eventArrayMarker.get(position).getVenue().getLongitude(), R.drawable.inactive_map_ico, position);

                    } else
                        createMarker(eventArrayMarker.get(position).getVenue().getLatitude(), eventArrayMarker.get(position).getVenue().getLongitude(), R.drawable.inactive_map_ico, position);
                }
            }
        } catch (Exception e) {
            //  Util.printLog(TAG, " " + e);
            e.printStackTrace();
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Util.printLog(TAG,"Info window clicked"+marker.getId()+":"+marker.getZIndex());

                //check
                      /*  Events events = eventArrayMarker.get(Integer.parseInt(marker.getId().replace("m", "")));
                        Event_Fragment frg = new Event_Fragment();
                        frg.setData(events.getEvent().getEvent_id(),events.getVenue().getVenue_name(),events);
                        activity.addFragment(frg, 0);
                        activity.setBBvisiblity(View.GONE,TAG);
                        LatLng sydney = new LatLng(Double.parseDouble(events.getVenue().getLatitude()), Double.parseDouble(events.getVenue().getLongitude()));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    showInfo(marker);
                    if (lastClick != null)
                        lastClick.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.inactive_map_ico));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.active_map_ico));
                    lastClick = marker;
                } catch (Exception e) {
                    // Util.printLog("Marker"+marker.getId(),e.toString());
                }
                return true;
            }
        });

        final Marker finalM = marker;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finalM != null) {
                    try {
                        showInfo(finalM);

                        finalM.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.active_map_ico));
                        lastClick = finalM;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 1000);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    Map_Fragment.this.getView().findViewById(R.id.all).setVisibility(View.GONE);
                    if (lastClick != null)
                        lastClick.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.inactive_map_ico));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Marker createMarker(String latitude, String longitude, int resource, int position) {
        double lat = Double.parseDouble(latitude);
        double lng = Double.parseDouble(longitude);
        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .anchor(0.5f, 0.5f)
                .snippet(position + "")
                .icon(BitmapDescriptorFactory.fromResource(resource)));

    }

    public void notifyAdapter(ArrayList<Events> eventsArrayList) {
        if (mapInfoAdapter != null) {
            mapInfoAdapter.setEventArrayList(eventsArrayList);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showInfo(Marker marker) throws Exception {

        position = Integer.parseInt(marker.getSnippet());
        events = eventArrayMarker.get(position);

        LatLng latLng = new LatLng(Double.parseDouble(events.getVenue().getLatitude()) - 0.008D, Double.parseDouble(events.getVenue().getLongitude()));
        //CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        googleMap.setMinZoomPreference(13);


        View myContentsView = this.getView();
        assert myContentsView != null;
        RelativeLayout all = myContentsView.findViewById(R.id.all);
        all.setVisibility(View.VISIBLE);
        TextView txt_eventName = myContentsView.findViewById(R.id.txt_eventName);
        TextView txt_eventAddress = myContentsView.findViewById(R.id.txt_eventAdress);
        txt_like = myContentsView.findViewById(R.id.txt_like);
        TextView txt_time = myContentsView.findViewById(R.id.txt_time);
        LinearLayout rl_anim = myContentsView.findViewById(R.id.rl_anim);
        TextView txt_live = myContentsView.findViewById(R.id.txt_live);
        ImageView green_dot = myContentsView.findViewById(R.id.green_dot);
        FrameLayout frame_keyinbutton = myContentsView.findViewById(R.id.frame_keyinbutton);
        TextView txt_eventMile = myContentsView.findViewById(R.id.txt_eventmile);
        comeInUser_lnr = myContentsView.findViewById(R.id.comeInUser_lnr);
        iv_group = myContentsView.findViewById(R.id.iv_group);
        iv_summery = myContentsView.findViewById(R.id.iv_summery);


        heart = myContentsView.findViewById(R.id.heart);
        tv_follow = myContentsView.findViewById(R.id.tv_follow);
        tv_follow = myContentsView.findViewById(R.id.tv_follow);
        ViewPager viewPager = myContentsView.findViewById(R.id.image_slider_pager);
        LinearLayout indicator_linear_layout = myContentsView.findViewById(R.id.indicator_linear_layout);
        LinearLayout ll_Board = myContentsView.findViewById(R.id.ll_Board);
        LinearLayout ll_status = myContentsView.findViewById(R.id.ll_status);
        LinearLayout ll_people = myContentsView.findViewById(R.id.ll_people);
//        LinearLayout hour = myContentsView.findViewById(R.id.hour);
        ll_heart = myContentsView.findViewById(R.id.ll_heart);
        newCheckWithDate(events.getEvent().event_date, events.getEvent().rating, events);
//        listener.getCheckEventStatusListener(events.getEvent().event_name, events.getEvent().event_id, events.getVenue(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()});
        tv_follow.setOnClickListener(view -> {
            clicked = true;
            if (events.getVenue().getIs_tag_follow().equalsIgnoreCase("0")) {
                tagFollowUnfollow(1, events.getVenue().getBiz_tag_id(), events.getVenue().getVenue_id(), position);
            } else {
                tagFollowUnfollow(0, events.getVenue().getBiz_tag_id(), events.getVenue().getVenue_id(), position);
            }
        });

        ll_heart.setOnClickListener(view -> {
            clicked = true;
            like_Api(events);
        });


        // Unfollow and follow
        if (events.getVenue().getIs_tag_follow().equalsIgnoreCase("0")) {
            tv_follow.setText("Follow");
            tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_border_gray));
            tv_follow.setTextColor(activity.getResources().getColor(R.color.reward_day_color));
        } else {
            tv_follow.setText("Unfollow");
            tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_active_gray));
            tv_follow.setTextColor(activity.getResources().getColor(R.color.white));
        }


        // Like and unlike
        if (events.getEvent().likeCount != null && !events.getEvent().likeCount.isEmpty()) {
            txt_like.setText(events.getEvent().likeCount);
        } else {
            txt_like.setText("0");
        }

        if (events.getEvent().isEventLike != null && events.getEvent().isEventLike.equals("1")) {
            heart.setImageResource(R.drawable.active_like_ico);
        } else {
            heart.setImageResource(R.drawable.inactive_like_ico);
        }


        //show image in viewpager
        ArrayList<ImageSlidModal> imageslideList = new ArrayList<>();
        if (events.getEvent().imageslideList.isEmpty()) {

            if (events.getEvent().getImage().contains("defaultevent.jpg")) {
                ImageSlidModal imageSlidModal = new ImageSlidModal();
                imageSlidModal.id = "1";
                imageSlidModal.feed_image = events.getVenue().getImage();
                imageslideList.add(imageSlidModal);
            }
        }

        if (!clicked) {
            Collections.reverse(events.getEvent().imageslideList);
        }

        if (events.getEvent().imageslideList.isEmpty()) {

            TrendingFeedSlider trendingFeedSlider = new TrendingFeedSlider(activity, imageslideList, events.getEvent(), events.getVenue(), listener, events, new String[]{lat, lng}, this::callCheckEventStatusApi);
            viewPager.setAdapter(trendingFeedSlider);
        } else {
            TrendingFeedSlider trendingFeedSlider = new TrendingFeedSlider(activity, events.getEvent().imageslideList, events.getEvent(), events.getVenue(), listener, events, new String[]{lat, lng}, this::callCheckEventStatusApi);
            viewPager.setAdapter(trendingFeedSlider);
        }

        int listSize = events.getEvent().imageslideList.size();
        if (listSize == 0) {
            indicator_linear_layout.setVisibility(View.GONE);
        } else {
            indicator_linear_layout.setVisibility(View.VISIBLE);
            addBottomDots(indicator_linear_layout, listSize, 0);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    addBottomDots(indicator_linear_layout, events.getEvent().imageslideList.size(), position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        //distance bw event and user
        final double Miles = activity.getDistanceMile(new Double[]{Double.valueOf(events.getVenue().getLatitude()), Double.valueOf(events.getVenue().getLongitude()), Double.valueOf(lat), Double.valueOf(lng)});
        final int distance = activity.getDistance(new Double[]{Double.valueOf(events.getVenue().getLatitude()), Double.valueOf(events.getVenue().getLongitude()), Double.valueOf(lat), Double.valueOf(lng)});
        String miles = String.valueOf(Miles);
        if (Miles == 0.0) {
            txt_eventMile.setText(miles + "0 " + " M");

        } else if (String.valueOf(Miles).length() == 3) {
            txt_eventMile.setText(String.format("%.2f", Miles) + " M");

        } else {
            txt_eventMile.setText(miles + " M");
        }

        //Event live or not
        String[] hhmmss = events.getEvent().event_time.split(":");
        String a = null;
        if (Integer.valueOf(hhmmss[0]) > 12) {
            a = " PM";
            hhmmss[0] = String.valueOf(Integer.valueOf(hhmmss[0]) - 12);
        } else {
            a = " AM";
        }
        String mTime = hhmmss[0] + ":" + hhmmss[1] + a;

        if (events.getEvent().strStatus == 0) {
            //live
            txt_time.setVisibility(View.GONE);
            txt_live.setVisibility(View.VISIBLE);
            green_dot.setVisibility(View.VISIBLE);
            green_dot.setImageResource(R.drawable.abc_dot);
        } else {
            txt_time.setVisibility(View.VISIBLE);
            txt_live.setVisibility(View.GONE);
            green_dot.setVisibility(View.GONE);
            txt_time.setText(mTime);
        }


        //Keyin or not
        if (distance < Constant.MAXIMUM_DISTANCE && events.getEvent().strStatus != 2) {
            frame_keyinbutton.setVisibility(View.VISIBLE);
            events.getEvent().ableToKeyIn = true;
        } else {
            frame_keyinbutton.setVisibility(View.GONE);
            events.getEvent().ableToKeyIn = false;
        }

        //key animation
        try {
            AlphaAnimation blinkanimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            blinkanimation.setDuration(1000); // duration - half a second
            blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            blinkanimation.setRepeatMode(Animation.REVERSE);
            rl_anim.startAnimation(blinkanimation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Intent to board activity

        ll_Board.setOnClickListener(view -> {
            Intent intent = new Intent(activity, OnBoardActivity.class);
            intent.putExtra("eventid", events.getEvent());
            intent.putExtra("venuid", events.getVenue());
            intent.putExtra("object", events);
            intent.putExtra("currentLatLng", new String[]{lat, lng});
            intent.putExtra("fromTrending", true);
            activity.startActivity(intent);
        });

        //Intent to status activity

        ll_status.setOnClickListener(view -> {


            if (activity.userInfo().makeAdmin != null && activity.userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
                try {
                    activity.showProgDialog(false, "");
                    callCheckEventStatusApi(events.getEvent().event_name, events.getEvent().event_id, events.getVenue(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()});
                } catch (NullPointerException e) {
                    activity.dismissProgDialog();
                    e.printStackTrace();
                }
            } else {

                if (events.getEvent().isFeed != 0) {

                    try {
                        activity.showProgDialog(false, "");
                        callCheckEventStatusApi(events.getEvent().event_name, events.getEvent().event_id, events.getVenue(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()});
                    } catch (NullPointerException e) {
                        activity.dismissProgDialog();
                        e.printStackTrace();
                    }

                } else if (events.getEvent().strStatus != 2 && distance < Constant.MAXIMUM_DISTANCE) {
                    try {
                        activity.showProgDialog(false, "");
                        listener.getCheckEventStatusListener(events.getEvent().event_name, events.getEvent().event_id, events.getVenue(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()});
                    } catch (NullPointerException e) {
                        activity.dismissProgDialog();
                        e.printStackTrace();
                    }
                } else {


                    Intent intent = new Intent(activity, OnBoardActivity.class);
                    intent.putExtra("eventid", events.getEvent());
                    intent.putExtra("venuid", events.getVenue());
                    intent.putExtra("object", events);
                    intent.putExtra("currentLatLng", new String[]{lat, lng});
                    intent.putExtra("fromTrending", true);
                    activity.startActivity(intent);
                }

            }


        });


        //List of people

//        Collections.sort(eventArrayMarker, new SortByPoint());
        try {

            if (events.getEvent().keyInUserModalList.size() != 0) {
                iv_group.setVisibility(View.GONE);
                comeInUser_lnr.setVisibility(View.VISIBLE);
                setRecyclerView(events.getEvent().keyInUserModalList, events);

            } else {
                iv_group.setVisibility(View.VISIBLE);
                comeInUser_lnr.setVisibility(View.GONE);
            }

           /* for (int i = 0; i < eventArrayMarker.size() ; i++) {
                if (eventArrayMarker.get(i).getEvent().keyInUserModalList.size() != 0) {
                    iv_group.setVisibility(View.GONE);
                    comeInUser_lnr.setVisibility(View.VISIBLE);
                    Log.v("keyInUser", "" + eventArrayMarker.get(i).getEvent().keyInUserModalList);
                    setRecyclerView(eventArrayMarker.get(i).getEvent().keyInUserModalList, events);
                }
                else {
                    iv_group.setVisibility(View.VISIBLE);
                    comeInUser_lnr.setVisibility(View.GONE);
                }
*/



            //for IMage
        } catch (Exception e) {
            //Picasso.with(activity).load(event.getImage().contains("defaultevent.jpg") ? venue.getImage() : event.getImage()).placeholder(R.drawable.transparent).into(holder.img_event);
            e.printStackTrace();
        }

        iv_summery.setOnClickListener(view -> {
            Intent intent = new Intent(activity, Summary_Activity.class);
            intent.putExtra("event_id", events.getEvent().event_id);
            intent.putExtra("object", events);
            intent.putExtra("currentLatLng", new String[]{lat,lng});
            activity.startActivity(intent);
        });

        //People intent
       iv_group.setOnClickListener(v -> {
           Intent intent = new Intent(activity, TheRoomActivity.class);
           intent.putExtra("fromTrendingHome", events.getEvent().keyInUserModalList);
           intent.putExtra("object", events);
           intent.putExtra("currentLatLng", new String[]{lat,lng});
           intent.putExtra("fromTrending", true);
           activity.startActivity(intent);
       });

        //keyin intent

        rl_anim.setOnClickListener(view -> {
            try {
                activity.showProgDialog(false, "");
                callCheckEventStatusApi(events.getEvent().event_name, events.getEvent().event_id, events.getVenue(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()});
            } catch (NullPointerException e) {
                activity.dismissProgDialog();
                e.printStackTrace();
            }
        });

        txt_eventName.setText(events.getEvent().event_name);
        txt_eventAddress.setText((events.getVenue().getVenue_name().trim().length() > 27 ? events.getVenue().getVenue_name().trim().substring(0, 27) : events.getVenue().getVenue_name().trim()));


    }


    public void newCheckWithDate(final String startDate, final String rating, Events events) {
        String[] dateSplit = (startDate.replace("TO", "T")).replace(" ", "T").split("T");
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            //1current date
            UserInfo userInfo = SceneKey.sessionManager.getUserInfo();
            Date currentdate = simpleDateFormat.parse(userInfo.currentDate);

            //2start date
            Date startdate = simpleDateFormat.parse(dateSplit[0] + " " + dateSplit[1]);


            //3 End date
            Date enddate = simpleDateFormat.parse(dateSplit[0] + " " + dateSplit[2]);


            compareDateLiveOrNot(currentdate, startdate, enddate, rating, events);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void compareDateLiveOrNot(Date currentdate, Date startDate, Date endDate, String rating, Events events) {

        if (currentdate.compareTo(startDate) < 0) {
            events.getEvent().strStatus = 2;

        } else {
            if (currentdate.compareTo(endDate) < 0) {
                events.getEvent().strStatus = 2;
            } else {
                events.getEvent().strStatus = 0;
            }
        }

    }

    private void showNoEventDialog() {
        if (isAdded()) {
            utility.showCustomPopup(getString(R.string.mapNoevent), String.valueOf(R.font.montserrat_medium));
            if (eventArrayMarker == null) eventArrayMarker = new ArrayList<>();
            else eventArrayMarker.clear();
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
                                                      Utility.e(TAG, "TimerVolley Map");
                                                      try {

                                                          if (canCallWebservice) {
                                                              checkEventAvailability();
                                                          }
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
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public void onPause() {
        //  Utility.e(TAG,"TimerVolley cancel");
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

    // New Code
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
        /*String[] dateSplit = (startDate.replace("TO", "T")).replace(" ", "T").split("T");
        try {
            Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())).parse(dateSplit[0] + " " + dateSplit[1]);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = df.format(c.getTime());

            Date curTime = df.parse(formattedDate);

            getDayDifference(startTime, curTime, rating, events);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy K:mm a");

            targetdatevalue = parseDate(formattedDate, dateFormat, targetFormat);

        } catch (ParseException e) {
            e.printStackTrace();
        }
*/

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

        /*if (elapsedDays == 0) {
            if (elapsedHours == 0) {
                if (elapsedMinutes == 0) {
                    returnDay = *//*elapsedSeconds +*//* " Just now";
                } else {
                    returnDay = elapsedMinutes + " minutes ago";
                }
            } else if (elapsedHours == 1) {
                returnDay = elapsedHours + " hour ago";
            } else {
                returnDay = elapsedHours + " hours ago";
            }
        } else if (elapsedDays == 1) {
            returnDay =  *//*elapsedDays + *//*"yesterday";
        } else {
            returnDay = elapsedDays + " days ago";
        }*/

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

    private void callCheckEventStatusApi(final String event_id, final String venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(context);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.CHECK_EVENT_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        activity.dismissProgDialog();
                        jsonObject = new JSONObject(Response);

                        String status = jsonObject.getString("status");
                        boolean isKeyInAble;

                        // If Not exist then isKeyInAble is false
                        // If exist then isKeyInAble is true
                        isKeyInAble = !status.equals("not exist");

                        if (!isKeyInAble && activity.userInfo().key_points.equals("0")) {
                            Toast.makeText(context, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {


                            Intent intent = new Intent(context, EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("fromTab", "map");
                            startActivity(intent);

                            /*Event_Fragment fragment = Event_Fragment.newInstance("trending");
                            fragment.setData(event_id, venue_name, object, currentLatLng, strings, isKeyInAble);
                            activity.addFragment(fragment, 0);*/
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
                    params.put("eventid", event_id);
                    params.put("userid", activity.userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(activity,"No network connection","","");
//            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }


    private void like_Api(final Events object) {

        activity.showProgDialog(true, "TAG");

        StringRequest request = new StringRequest(Request.Method.POST, WebServices.LIKE_EVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                activity.dismissProgDialog();
                // get response
                try {
                    JSONObject jo = new JSONObject(response);

                    //{"success":1,"msg":"your have liked the event."}

                    if (jo.has("success")) {
                        int status = jo.getInt("success");
                        String msg = jo.getString("msg");
                        int likeCount = Integer.parseInt(object.getEvent().likeCount);
                        if (msg.equals("your have liked the event.")) {
                            int newLikeCount = likeCount + 1;
                            txt_like.setText(String.valueOf(newLikeCount));
                            object.getEvent().likeCount = String.valueOf(newLikeCount);
                            heart.setImageResource(R.drawable.active_like_ico);


                        } else {
                            int newLikeCount = likeCount - 1;
                            txt_like.setText(likeCount >= 0 ? newLikeCount + "" : "0");
                            object.getEvent().likeCount = String.valueOf(newLikeCount);
                            heart.setImageResource(R.drawable.inactive_like_ico);
                        }
                    }
                } catch (Exception e) {
                    activity.dismissProgDialog();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                activity.dismissProgDialog();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", object.getEvent().event_id);
                params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                Utility.e(TAG, " params " + params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(activity).addToRequestQueue(request, "HomeApi");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
    }

    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final String venueId, final int pos) {
        if (utility.checkInternetConnection()) {
            activity.showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();

                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.has("status")) {
                            if (jo.getString("status").equalsIgnoreCase("success")) {
                                if (followUnfollow == 0) {
                                    tv_follow.setText("Follow");
                                    events.getVenue().setIs_tag_follow("0");
                                    tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_border_gray));
                                    tv_follow.setTextColor(activity.getResources().getColor(R.color.reward_day_color));

                                } else {
                                    tv_follow.setText("Unfollow");
                                    events.getVenue().setIs_tag_follow("1");
                                    tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_active_gray));
                                    tv_follow.setTextColor(activity.getResources().getColor(R.color.white));

                                }
                            }
                        }

                    } catch (Exception e) {
                        activity.dismissProgDialog();
                        activity.dismissProgDialog();
//                        Utility.showToast(getActivity(), getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    activity.dismissProgDialog();

                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("biz_tag_id", biz_tag_id);
                    params.put("follow_status", String.valueOf(followUnfollow));
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    params.put("venue_id", venueId);
                    params.put("lat", lat);
                    params.put("long", lng);
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(activity,"No network connection","","");
            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
            activity.dismissProgDialog();

        }
    }

    private void addBottomDots(LinearLayout indicator_linear_layout, int size, int i) {
        ImageView[] dots = new ImageView[size];
        indicator_linear_layout.removeAllViews();


        if (dots.length > 1) {
            for (int j = 0; j < dots.length; j++) {
                dots[j] = new ImageView(activity);
                dots[j].setImageResource(R.drawable.inactive_dot_img);

                if (j == 0 || j == 1 || j == 2) {
                    indicator_linear_layout.addView(dots[j]);
                }
            }


            if (i == 0) {

                dots[0].setImageResource(R.drawable.dot_ico);
            }
            if (i == 1) {
                dots[1].setImageResource(R.drawable.dot_ico);
            }
            if (i > 1) {
                if (dots.length - 1 == i) {
                    dots[2].setImageResource(R.drawable.dot_ico);
                } else {
                    dots[1].setImageResource(R.drawable.dot_ico);
                }

            }
        }


    }

    // New Code
    private void callCheckEventStatusApi(final String event_name, final String event_id, final Venue venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(context);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.CHECK_EVENT_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        activity.dismissProgDialog();

                        jsonObject = new JSONObject(Response);

                        String status = jsonObject.getString("status");
                        boolean isKeyInAble;

                        // If Not exist then isKeyInAble is false
                        // If exist then isKeyInAble is true
                        isKeyInAble = !status.equals("not exist");

                        if (!isKeyInAble && activity.userInfo().key_points.equals("0")) {
                            Toast.makeText(context, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(context, EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("fromTab", "trending");
                            intent.putExtra("venueName", venue_name.getVenue_name());
                            intent.putExtra("currentLatLng", currentLatLng);
                            intent.putExtra("event_name", event_name);
                            intent.putExtra("object", object);
                            intent.putExtra("venueId", venue_name.getVenue_id());
                            intent.putExtra("fromTrending", true);
                            intent.putExtra("isKeyInAble", isKeyInAble);

                            startActivity(intent);

                         /*   Event_Fragment fragment = Event_Fragment.newInstance("trending");
                            fragment.setData(event_id, venue_name, object, currentLatLng, strings, isKeyInAble);
                            activity.addFragment(fragment, 0);*/
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
                    params.put("eventid", event_id);
                    params.put("userid", activity.userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
        } else {
//            Utility.showCheckConnPopup(activity,"No network connection","","");
//            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();

        }
    }

    private void setRecyclerView(final ArrayList<KeyInUserModal> keyInUserModalList, final Events object) {

        CircularImageView comeInUserProfile = null;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        comeInUser_lnr.removeAllViews();
        int loopCount = keyInUserModalList.size();
        if (loopCount > 5) {
            loopCount = 5;
        }
        for (int i = 0; i < loopCount; i++) {

            assert inflater != null;
            View v = inflater.inflate(R.layout.trend_user_view, null);
            comeInUserProfile = v.findViewById(R.id.comeInProfile_t);
            TextView no_count = v.findViewById(R.id.no_count_t);
            RelativeLayout marginlayout = v.findViewById(R.id.mainProfileView_t);

            if (i == 0) {
                comeInUser_lnr.addView(v, i);
                String image = "";
                image = keyInUserModalList.get(i).getUserimage();

                /*if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                    image = "dev-" + keyInUserModalList.get(i).getUserimage();
                } else {
                    //image = keyInUserModalList.get(i).userImage;
                    image = keyInUserModalList.get(i).getUserimage();
                }
*/
                Glide.with(activity).load(image)
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
                    params.setMargins(20 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    comeInUser_lnr.addView(v, i);
                    String image = "";
                    image = keyInUserModalList.get(i).getUserimage();

                    /*if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                        image = "dev-" + keyInUserModalList.get(i).getUserimage();
                    } else {
                        image = keyInUserModalList.get(i).getUserimage();
                    }*/

                    Glide.with(activity).load(image)
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
                    params.setMargins(20 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    comeInUser_lnr.addView(v, i);
                    String image = "";
                    image = keyInUserModalList.get(i).getUserimage();

/*

                    if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                        image = "dev-" + keyInUserModalList.get(i).getUserimage();
                    } else {
                        image = keyInUserModalList.get(i).getUserimage();
                    }
*/

                    Glide.with(activity).load(image)
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
                    params.setMargins(20 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    comeInUser_lnr.addView(v, i);
                    no_count.setText(" +" + (keyInUserModalList.size() - i));
                    String image = "";

                    image = keyInUserModalList.get(i).getUserimage();

                   /* if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                        image = "dev-" + keyInUserModalList.get(i).getUserimage();
                    } else {
                        image = keyInUserModalList.get(i).getUserimage();
                    }*/

                    Glide.with(activity).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);

                    no_count.setVisibility(View.VISIBLE);
                }
            }
        }


        comeInUser_lnr.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TheRoomActivity.class);
            intent.putExtra("fromTrendingHome", keyInUserModalList);
            intent.putExtra("object", object);
            intent.putExtra("currentLatLng",new  String[]{lat,lng});
            intent.putExtra("fromTrending", true);
            activity.startActivity(intent);
        });
    }
}


//        ImageView img_event = myContentsView.findViewById(R.id.img_event);


        /*try {
            Bitmap bitmap = ImageUtil.getBitmapByUrl(events.getEvent().getImage());
            //bitmap = (new RoundedTransformation(radius,1).transform(bitmap));
            img_event.setImageBitmap(bitmap);

            String result = events.getEvent().getImage().contains("defaultevent.jpg") ? events.getVenue().getImage() : events.getEvent().getImage();
            Utility.e("Map Image---", result);
            Picasso.with(getContext()).load(events.getEvent().getImage().contains("defaultevent.jpg") ? events.getVenue().getImage() : events.getEvent().getImage()).into(img_event);

        } catch (Exception e) {
            String result = events.getEvent().getImage().contains("defaultevent.jpg") ? events.getVenue().getImage() : events.getEvent().getImage();
            Utility.e("Map Image---", result);
            Picasso.with(getContext()).load(events.getEvent().getImage().contains("defaultevent.jpg") ? events.getVenue().getImage() : events.getEvent().getImage()).into(img_event);
            e.printStackTrace();
        }*/


// Picasso.with(activity).load(events.getEvent().getImage()).transform(new RoundedTransformation(radius,1)).into(img_event);
// Util.printLog("map", events.getEvent().getEvent_name() + " : " + events.getEvent().getImage());



      /*  try {
            int radius = (int) getResources().getDimension(R.dimen.trending_round);
            String result = events.getEvent().getImage();
            Utility.e("click image--", result);

            String img = events.getEvent().getImage().contains("defaultevent.jpg") ? events.getVenue().getImage() : events.getEvent().getImage();
            Picasso.with(context).load(img).resize(img_event.getWidth(), img_event.getHeight()).transform(new RoundedTransformation(radius, 0)).into(img_event);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


       /* if (events.isOngoing) {
            hour.setVisibility(View.GONE);
            like.setVisibility(View.VISIBLE);
            heart.setImageResource(R.drawable.ic_heart_new);

            if(Integer.parseInt(event.rating)==0)txt_like.setText("--");
            else {
                txt_like.setText(event.rating);
                heart.setImageResource(R.drawable.ic_favorite_heart);
            }

        }
        else {
            hour.setVisibility(View.VISIBLE);
            like.setVisibility(View.GONE);
            txt_time.setText(events.remainingTime);
        }*/


           /* if (events.getEvent().strStatus == 0) {
//            hour.setVisibility(View.GONE);
            like.setVisibility(View.VISIBLE);
            heart.setImageResource(R.drawable.ic_heart_new);
            txt_like.setText(events.getEvent().returnDay);

        } else if (events.getEvent().strStatus == 1) {
//            hour.setVisibility(View.GONE);
            like.setVisibility(View.VISIBLE);
            heart.setImageResource(R.drawable.ic_favorite_heart);
            txt_like.setText(events.getEvent().returnDay);

        } else {
//            hour.setVisibility(View.VISIBLE);
            like.setVisibility(View.GONE);
            txt_time.setText(events.getEvent().returnDay);
        }*/


           /*  myContentsView.findViewById(R.id.all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                v.setClickable(false);
                try {
                    activity.showProgDialog(true, TAG);
                    if (!clicked) {
                        *//*Event_Fragment frg = Event_Fragment.newInstance("map_tab");
                        frg.setData(event.event_id, events.getVenue().getVenue_name(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()}, false);
                        activity.addFragment(frg, 0);*//*

                        callCheckEventStatusApi(event.event_id, events.getVenue().getVenue_name(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()});

                        clicked = true;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setClickable(true);
                            clicked = false;
                        }
                    }, 2000);
                } catch (Exception e) {
                    activity.dismissProgDialog();
                    e.printStackTrace();
                }

            }
        });*/

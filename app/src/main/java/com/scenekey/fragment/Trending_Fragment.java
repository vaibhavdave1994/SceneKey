package com.scenekey.fragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.Trending_Adapter;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.listener.FollowUnfollowLIstner;
import com.scenekey.model.Events;
import com.scenekey.model.UserInfo;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

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
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Trending_Fragment extends Fragment {

    private static Timer timerHttp;
    private final String TAG = Trending_Fragment.class.toString();
    public boolean canCallWebservice;
    UserInfo myUserInfo;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private CustomProgressBar progressBar;
    private RecyclerView rcViewTrending;
    private String lat = "", lng = "";
    private Trending_Adapter trendingAdapter;
    private ArrayList<Events> eventsArrayList;
    private ScrollView no_data_trending;
    private int pos = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trending, container, false);
        rcViewTrending = v.findViewById(R.id.rcViewTrending);
        no_data_trending = v.findViewById(R.id.no_data_trending);
        activity.setTitleVisibility(View.VISIBLE);

        SessionManager sessionManager = new SessionManager(context);
        progressBar = new CustomProgressBar(context);

        sessionManager.backOrIntent(true);
        SceneKey.sessionManager.putMapFragment("trending");

//        recalling();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle(context.getResources().getString(R.string.trending));

        if(!Utility.checkInternetConnection1(activity)){
            Utility.showCheckConnPopup(activity,"No network connection","","");
        }
        trendingData();
    }

    @Override
    public void onStart() {
        super.onStart();
        canCallWebservice = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.showProgDialog(true,"");
//        recalling();
        canCallWebservice = true;
//        if (timerHttp == null) setDataTimer();

//        getTrendingData();
    }

    /*private void recalling(){
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // your code here
                       getTrendingData();



//                      trendingAdapter.notifyItemChanged(0);
                    }
                },
                60000
        );
    }*/

    private void trendingData() {
        String[] latLng = activity.getLatLng();
        lat = latLng[0];
        lng = latLng[1];
        if (lat.equals("0.0") && lng.equals("0.0")) {
            latLng = activity.getLatLng();
            lat = latLng[0];
            lng = latLng[1];
            retryLocation();
        } else {
//            progressBar.show();
//            activity.showProgDialog(false, TAG);
            getTrendingData();
            if (timerHttp == null) setDataTimer();
        }
    }

    private void retryLocation() {
        final Dialog dialog = new Dialog(context);
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
//                progressBar.show();
                activity.showProgDialog(false, TAG);
                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {

                        if (utility.checkNetworkProvider() || utility.checkGPSProvider()) {
                            trendingData();
                        } else {
                            utility.checkGpsStatus();
                            trendingData();
                        }
                    }
                }, 3 * 1000); // wait for 3 seconds
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.dismiss();
//                activity.dismissProgDialog();
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

    private void setRecyclerView() {
        if (trendingAdapter == null) {
            trendingAdapter = new Trending_Adapter(activity, eventsArrayList, new String[]{lat, lng}, new CheckEventStatusListener() {
                @Override
                public void getCheckEventStatusListener(String eventNAme, String event_id, Venue venue_name, Events object, String[] currentLatLng, String[] strings) {
                    callCheckEventStatusApi(eventNAme, event_id, venue_name, object, currentLatLng, strings);
                }
            },
                    new FollowUnfollowLIstner() {
                        @Override
                        public void getFollowUnfollow(int followUnfollow, String biz_tag_id, Object object, int position) {
                            Events events = (Events) object;
                            tagFollowUnfollow(followUnfollow, biz_tag_id, events.getVenue().getVenue_id(), position);
                        }
                    });


            LinearLayoutManager layoutManager = new LinearLayoutManager(activity) {

                @Override
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    LinearSmoothScroller smoothScroller = new LinearSmoothScroller(activity) {

                        private static final float SPEED = 300f;

                        @Override
                        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                            return SPEED / displayMetrics.densityDpi;
                        }

                    };
                    smoothScroller.setTargetPosition(position);
                    startSmoothScroll(smoothScroller);
                }

            };

            rcViewTrending.setLayoutManager(layoutManager);
            rcViewTrending.setAdapter(trendingAdapter);
            trendingAdapter.notifyDataSetChanged();
            rcViewTrending.setHasFixedSize(true);
        } else {
            trendingAdapter.notifyDataSetChanged();
            rcViewTrending.setHasFixedSize(true);
        }
    }

    public void getTrendingData() {

        activity.showProgDialog(true,"");
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (pos == 0) {
                    rcViewTrending.scrollToPosition(0);
                }
            }
        });

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TRENDING, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // get response
                    activity.dismissProgDialog();
                    try {
                        JSONObject jo = new JSONObject(response);

                        if (jo.has("status")) {
                            int status = jo.getInt("status");
                            if (status == 0) {
                                activity.dismissProgDialog();
                                progressBar.dismiss();

                                try {
                                    no_data_trending.setVisibility(View.VISIBLE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                // New Code
                                if (jo.has("userInfo")) {
                                    UserInfo userInfo = activity.userInfo();
                                    JSONObject user = jo.getJSONObject("userInfo");
                                    if (user.has("makeAdmin"))
                                        userInfo.makeAdmin = (user.getString("makeAdmin"));

                                    if (user.has("currentDate"))
                                        userInfo.currentDate = (user.getString("currentDate"));

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
                                    activity.updateSession(userInfo);
                                    myUserInfo = userInfo;
                                    if (user.getString("fullname").equals("")) {
                                        SceneKey.sessionManager.logout(activity);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {

                                // New Code
                                if (jo.has("userInfo")) {
                                    UserInfo userInfo = activity.userInfo();

                                    JSONObject user = jo.getJSONObject("userInfo");
                                    if (user.has("makeAdmin"))
                                        userInfo.makeAdmin = (user.getString("makeAdmin"));

                                    if (user.has("currentDate"))
                                        userInfo.currentDate = (user.getString("currentDate"));

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


                                    myUserInfo = userInfo;
                                    activity.updateSession(myUserInfo);

                                    if (user.getString("fullname").equals("")) {
                                        SceneKey.sessionManager.logout(activity);
                                    }
                                }
                            } catch (Exception e) {
                                if (e.getMessage().equals("Value [] at userInfo of type org.json.JSONArray cannot be converted to JSONObject")) {
                                    SceneKey.sessionManager.logout(getActivity());
                                }
                                e.printStackTrace();
                                activity.dismissProgDialog();
                                progressBar.dismiss();

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
                                        String dateSplit = "0.0";
                                        if (events.getEvent().interval != null) {
                                            String interval = String.valueOf(events.getEvent().interval);

                                            if (interval.contains(":")) {
                                                dateSplit = (interval.replace(":", "."));
                                            } else {
                                                dateSplit = String.valueOf(events.getEvent().interval);
                                            }

                                            events.setOngoing(events.checkWithTime(events.getEvent().event_date, Double.parseDouble(dateSplit)));
                                        }

                                        // New Code
//                                        checkWithDate(events.getEvent().event_date, events.getEvent().rating, events);
                                        newCheckWithDate(events.getEvent().event_date, events.getEvent().rating, events);
                                    } catch (Exception e) {
                                        Utility.e("Date exception", e.toString());
                                    }

                                    try {
                                        int time_format = 0;
                                        try {
                                            time_format = Settings.System.getInt(context.getContentResolver(), Settings.System.TIME_12_24);
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
                                    Toast.makeText(activity, "No Event found near your location", Toast.LENGTH_LONG).show();
                                }
                                setRecyclerView();

                                for (int i = 0; i < eventsArrayList.size(); i++) {

                                    if (SceneKey.sessionManager.getPosTrendingList().equalsIgnoreCase(eventsArrayList.get(i).getEvent().event_id)) {
                                        pos = i;

                                    }
                                }
                                rcViewTrending.scrollToPosition(pos);
                                SceneKey.sessionManager.putPosTrendingList("");
                                pos=0;

                            }
                            progressBar.dismiss();
                            activity.dismissProgDialog();
                            no_data_trending.setVisibility(View.GONE);
                        }
                        progressBar.dismiss();
                        activity.dismissProgDialog();
                    } catch (Exception e) {
                        activity.dismissProgDialog();
                        progressBar.dismiss();
//                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    progressBar.dismiss();

                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("lat", lat);
                    params.put("long", lng);
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid + "");

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
        } else {
//            utility.snackBar(rcViewTrending, getString(R.string.internetConnectivityError), 0);
//            Utility.showCheckConnPopup(activity,"No network connection","","");

            activity.dismissProgDialog();
            progressBar.dismiss();

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
                                                      Utility.e(TAG, "TimerVolley Trending");
                                                      try {

                                                          if (canCallWebservice) {
                                                              SceneKey.sessionManager.putPosTrendingList("");
                                                              getTrendingData();

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
                //Sest the amount of time between each execution (in milliseconds)
                60000);
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
                        progressBar.dismiss();

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
                        progressBar.dismiss();
                        activity.dismissProgDialog();
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    progressBar.dismiss();

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
            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
//            Utility.showCheckConnPopup(activity,"No network connection","","");
            activity.dismissProgDialog();
            progressBar.dismiss();

        }
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


    public void checkWithDate(final String startDate, final String rating, Events events) {  //2018-11-12 18:00:00TO08:00:00
        String[] dateSplit = (startDate.replace("TO", "T")).replace(" ", "T").split("T");
        try {
//            Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).parse(dateSplit[0] + " " + dateSplit[1]);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date startTime = simpleDateFormat.parse(dateSplit[0] + " " + dateSplit[1]);

            //3 End date
            Date Enddate = simpleDateFormat.parse(dateSplit[0] + " " + dateSplit[2]);


//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            UserInfo userInfo = SceneKey.sessionManager.getUserInfo();
            Date date = simpleDateFormat.parse(userInfo.currentDate);
           /* String formattedDate = df.format(date);
            Date curTime = df.parse(formattedDate);*/
//            getDayDifference(startTime, Enddate, rating, events);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

//    //********** day diffrence  ****************//
//    public void getDayDifference(Date startDate, Date endDate, String rating, Events events) {
//        //milliseconds
//        long different = startDate.getTime() - endDate.getTime();
//
//
//        Log.v("different", "" + different);
//
//        long secondsInMilli = 1000;
//        long minutesInMilli = secondsInMilli * 60;
//        long hoursInMilli = minutesInMilli * 60;
//        long daysInMilli = hoursInMilli * 24;
//
//        long elapsedDays = different / daysInMilli;
//        different = different % daysInMilli;
//
//        long elapsedHours = different / hoursInMilli;
//        different = different % hoursInMilli;
//
//        long elapsedMinutes = different / minutesInMilli;
//        different = different % minutesInMilli;
//
//        long elapsedSeconds = different / secondsInMilli;
//
//
//        //befor Change 6.21 minute
//       /* if (different > 0) {
//            if (elapsedHours > 0) {
//                events.getEvent().returnDay = elapsedHours + "hr";
//                events.getEvent().strStatus = 2;
//            } else if (elapsedMinutes > 0) {
//                events.getEvent().returnDay = elapsedMinutes + "mins";
//                events.getEvent().strStatus = 2;
//            } else if (elapsedSeconds > 0) {
//                events.getEvent().returnDay = elapsedSeconds + "secs";
//                //strStatus = 2;
//                events.getEvent().strStatus = 2;
//            } else {
//                if (rating.equals("0")) {
//                    events.getEvent().returnDay = "--";
//                    //strStatus = 0;
//                    events.getEvent().strStatus = 0;
//                } else {
//                    //strStatus = 1;
//                    events.getEvent().strStatus = 1;
//                    // events.getEvent().returnDay = String.format("%.2f", Double.parseDouble(rating));
//                    events.getEvent().returnDay = rating;
//                }
//            }
//        } else {
//            *//*returnDay = "--";
//            strStatus = 0;*//*
//
//            if (rating.equals("0")) {
//                events.getEvent().returnDay = "--";
//                //strStatus = 0;
//                events.getEvent().strStatus = 0;
//            } else {
//                events.getEvent().strStatus = 1;
//                //strStatus = 1;
//                // events.getEvent().returnDay = String.format("%.2f", Double.parseDouble(rating));
//                events.getEvent().returnDay = rating;
//            }
//        }*/
//
//        // change by shubham 6.21 minute
//
//        if (different > 0) {
//            if (elapsedHours > 0) {
//                events.getEvent().returnDay = elapsedHours + "hr";
//                events.getEvent().strStatus = 2;
//            } else if (elapsedMinutes > 0) {
//                events.getEvent().returnDay = elapsedMinutes + "mins";
//                events.getEvent().strStatus = 2;
//            } else if (elapsedSeconds > 0) {
//                events.getEvent().returnDay = elapsedSeconds + "secs";
//                //strStatus = 2;
//                events.getEvent().strStatus = 2;
//            } else {
//                if (rating.equals("0")) {
//                    events.getEvent().returnDay = "--";
//                    //strStatus = 0;
//                    events.getEvent().strStatus = 0;
//                } else {
//                    //strStatus = 1;
//                    events.getEvent().strStatus = 1;
//                    // events.getEvent().returnDay = String.format("%.2f", Double.parseDouble(rating));
//                    events.getEvent().returnDay = rating;
//                }
//            }
//        } else {
//            /*returnDay = "--";
//            strStatus = 0;*/
//
//            if (rating.equals("0")) {
//                events.getEvent().returnDay = "--";
//                //strStatus = 0;
//                events.getEvent().strStatus = 0;
//            } else {
//                events.getEvent().strStatus = 1;
//                //strStatus = 1;
//                // events.getEvent().returnDay = String.format("%.2f", Double.parseDouble(rating));
//                events.getEvent().returnDay = rating;
//            }
//        }
//    }

    //----------------follow / unfollow----------------
    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final String venueId, final int pos) {
        if (utility.checkInternetConnection()) {
//            activity.showProgDialog(true, "TAG");
            progressBar.show();
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    progressBar.dismiss();

                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.has("status")) {
                            if (jo.getString("status").equalsIgnoreCase("success")) {
                                if (followUnfollow == 0) {
                                    Venue venue = eventsArrayList.get(pos).getVenue();
                                    venue.setIs_tag_follow("0");
                                    eventsArrayList.get(pos).setVenue(venue);
                                    trendingAdapter.notifyDataSetChanged();
                                } else {
                                    Venue venue = eventsArrayList.get(pos).getVenue();
                                    venue.setIs_tag_follow("1");
                                    eventsArrayList.get(pos).setVenue(venue);
                                    trendingAdapter.notifyDataSetChanged();
                                }
//                                setRecyclerView();
                                // getTrendingData();

                            }
                        }

                    } catch (Exception e) {
                        progressBar.dismiss();
                        activity.dismissProgDialog();
//                        Utility.showToast(getActivity(), getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    progressBar.dismiss();

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

            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
            progressBar.dismiss();

        }
    }
}

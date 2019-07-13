package com.scenekey.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.MapInfo_Adapter;
import com.scenekey.helper.Constant;
import com.scenekey.helper.WebServices;
import com.scenekey.model.Event;
import com.scenekey.model.Events;
import com.scenekey.model.UserInfo;
import com.scenekey.util.ImageUtil;
import com.scenekey.util.RoundedTransformation;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

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
import java.util.Timer;
import java.util.TimerTask;

public class Map_Fragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private final String TAG = Map_Fragment.class.toString();
    private Context context;
    private HomeActivity activity;
    private String lat = "", lng = "";
    private boolean clicked;

    private Utility utility;

    private MapView mMapView;
    private GoogleMap googleMap;

    private MapInfo_Adapter mapInfoAdapter;
    private ArrayList<Events> eventArrayMarker;

    private Marker lastClick;

    public boolean canCallWebservice;
    private static Timer timerHttp;

    String targetdatevalue = "";
    // New Code
    String returnDay = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        activity.setTitleVisibility(View.VISIBLE);

        mMapView = v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
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
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_BY_LOCAL, new Response.Listener<String>() {
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
                                mapAsync();
                            }
                        }
                        activity.dismissProgDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
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
            utility.snackBar(mMapView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    private void mapAsync() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                Marker marker = null;

                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
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
                                marker = createMarker(eventArrayMarker.get(position).getVenue().getLatitude(), eventArrayMarker.get(position).getVenue().getLongitude(), R.drawable.map_pin, position);

                            } else
                                createMarker(eventArrayMarker.get(position).getVenue().getLatitude(), eventArrayMarker.get(position).getVenue().getLongitude(), R.drawable.map_pin, position);
                        }
                    }
                } catch (Exception e) {
                    //  Util.printLog(TAG, " " + e);
                    e.printStackTrace();
                }

                //sydney = new LatLng(38.222046D, -122.144755D);
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                                lastClick.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_active));
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

                                finalM.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_active));
                                lastClick = finalM;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 3000);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        try {
                            Map_Fragment.this.getView().findViewById(R.id.all).setVisibility(View.GONE);
                            if (lastClick != null)
                                lastClick.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });

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

    void showInfo(Marker marker) throws Exception {

        int position = Integer.parseInt(marker.getSnippet());
        final Events events = eventArrayMarker.get(position);

        LatLng latLng = new LatLng(Double.parseDouble(events.getVenue().getLatitude()) - 0.008D, Double.parseDouble(events.getVenue().getLongitude()));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        View myContentsView = this.getView();
        assert myContentsView != null;
        RelativeLayout all = myContentsView.findViewById(R.id.all);
        all.setVisibility(View.VISIBLE);
        ImageView img_event = myContentsView.findViewById(R.id.img_event);


        try {
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
        }


        // Picasso.with(activity).load(events.getEvent().getImage()).transform(new RoundedTransformation(radius,1)).into(img_event);
        // Util.printLog("map", events.getEvent().getEvent_name() + " : " + events.getEvent().getImage());
        TextView txt_eventName = myContentsView.findViewById(R.id.txt_eventName);
        TextView txt_eventAddress = myContentsView.findViewById(R.id.txt_eventAdress);
        TextView txt_eventDate = myContentsView.findViewById(R.id.txt_eventDate);
        TextView txt_like = myContentsView.findViewById(R.id.txt_like);
        TextView txt_time = myContentsView.findViewById(R.id.txt_time);
        TextView txt_eventMile = myContentsView.findViewById(R.id.txt_eventmile);
        ImageView heart = myContentsView.findViewById(R.id.heart);
        LinearLayout hour = myContentsView.findViewById(R.id.hour);
        LinearLayout like = myContentsView.findViewById(R.id.like);

        try {
            int radius = (int) getResources().getDimension(R.dimen.trending_round);
            String result = events.getEvent().getImage();
            Utility.e("click image--", result);

            String img = events.getEvent().getImage().contains("defaultevent.jpg") ? events.getVenue().getImage() : events.getEvent().getImage();
            Picasso.with(context).load(img).resize(img_event.getWidth(), img_event.getHeight()).transform(new RoundedTransformation(radius, 0)).into(img_event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Event event = events.getEvent();

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

        // New Code
        if (events.getEvent().strStatus == 0) {
            hour.setVisibility(View.GONE);
            like.setVisibility(View.VISIBLE);
            heart.setImageResource(R.drawable.ic_heart_new);
            txt_like.setText(events.getEvent().returnDay);

        } else if (events.getEvent().strStatus == 1) {
            hour.setVisibility(View.GONE);
            like.setVisibility(View.VISIBLE);
            heart.setImageResource(R.drawable.ic_favorite_heart);
            txt_like.setText(events.getEvent().returnDay);

        } else {
            hour.setVisibility(View.VISIBLE);
            like.setVisibility(View.GONE);
            txt_time.setText(events.getEvent().returnDay);
        }

        txt_eventName.setText(event.event_name);

        txt_eventAddress.setText((events.getVenue().getVenue_name().trim().length() > 27 ? events.getVenue().getVenue_name().trim().substring(0, 27) : events.getVenue().getVenue_name().trim()));
        String miles = String.valueOf(activity.getDistanceMile(new Double[]{Double.valueOf(events.getVenue().getLatitude()), Double.valueOf(events.getVenue().getLongitude()), Double.valueOf(lat), Double.valueOf(lng)}));

        txt_eventMile.setText(miles + " M");
       // txt_eventDate.setText(events.timeFormat);
        txt_eventDate.setText(targetdatevalue);

        myContentsView.findViewById(R.id.all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setClickable(false);
                try {
                    activity.showProgDialog(false, TAG);
                    if (!clicked) {
                        /*Event_Fragment frg = Event_Fragment.newInstance("map_tab");
                        frg.setData(event.event_id, events.getVenue().getVenue_name(), events, new String[]{lat, lng}, new String[]{events.getVenue().getLatitude(), events.getVenue().getLongitude()}, false);
                        activity.addFragment(frg, 0);*/

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
        });

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


    }

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


                            Intent intent = new Intent(context,EventDetailsActivity.class);
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
            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }

}

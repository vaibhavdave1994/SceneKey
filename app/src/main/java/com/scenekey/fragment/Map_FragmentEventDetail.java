package com.scenekey.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

@SuppressLint("ValidFragment")
public class Map_FragmentEventDetail extends Fragment implements OnMapReadyCallback{

    private final String TAG = Map_FragmentEventDetail.class.toString();
    private Context context;
    private GoogleMap mMap;
    Events events;
    TextView date_time,tvAddress;
    @SuppressLint("ValidFragment")
    public Map_FragmentEventDetail(Events events) {
        this.events = events;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.new_map_on_activity_detail, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date_time = view.findViewById(R.id.date_time);
        tvAddress = view.findViewById(R.id.tvAddress);

        String sourcedatevalue = events.getEvent().event_date;
        String[] strDateOnly = sourcedatevalue.split("TO");
        sourcedatevalue = strDateOnly[0].replace("T"," ");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy K:mm a");

        String targetdatevalue = parseDate(sourcedatevalue, dateFormat, targetFormat);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:SS");
        SimpleDateFormat targetFormat1 = new SimpleDateFormat("K:mm a");
        targetdatevalue = targetdatevalue + " TO "+parseDate(strDateOnly[1], dateFormat1, targetFormat1);

        date_time.setText(targetdatevalue);
        tvAddress.setText(events.getVenue().getAddress());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(img);
            // Add a marker in Sydney and move the camera
            LatLng eventLoc = new LatLng(Double.parseDouble(events.getVenue().getLatitude()), Double.parseDouble(events.getVenue().getLongitude()));
            Marker marker = mMap.addMarker(new MarkerOptions().position(eventLoc).title(events.getEvent().event_name)
                    .icon(bitmapDescriptor));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(eventLoc));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(events.getVenue().getLatitude()), Double.parseDouble(events.getVenue().getLongitude())), 15.0f));
            marker.showInfoWindow();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

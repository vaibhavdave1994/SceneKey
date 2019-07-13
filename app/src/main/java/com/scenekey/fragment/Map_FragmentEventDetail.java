package com.scenekey.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scenekey.R;
import com.scenekey.model.Events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

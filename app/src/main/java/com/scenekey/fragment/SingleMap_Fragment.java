package com.scenekey.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.scenekey.activity.HomeActivity;
import com.scenekey.helper.CustomAlertDialog;
import com.scenekey.util.Utility;

public class SingleMap_Fragment extends Fragment implements View.OnClickListener {

    private final String TAG = SingleMap_Fragment.class.toString();

    private MapView mMapView;
    private GoogleMap googleMap;

    private HomeActivity activity;
    private Utility utility;
    private Double lat, lng;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_single_map, container, false);

        mMapView = view.findViewById(R.id.map_view);
        view.findViewById(R.id.txt_getDirection).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //for status bar manage
        activity.setTopStatus();
        activity.showStatusBar();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //for status bar manage
        activity.setTopStatus();
        activity.showStatusBar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapAsyncer();
    }


    public Fragment setData(String venue_lat, String venue_long) {
        lat = Double.valueOf(venue_lat);
        lng = Double.valueOf(venue_long);
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity= (HomeActivity) getActivity();
        utility=new Utility(context);
    }

    private void mapAsyncer() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                Marker m = null;
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
                LatLng sydney = new LatLng(lat,lng);


                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .anchor(0.5f, 0.5f)
                        .title("HI this is tile")
                        .snippet("A new Snippet")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(activity) {
                            @Override
                            public void leftButtonClick() {
                                googleMapCall();
                                this.dismiss();
                            }

                            @Override
                            public void rightButtonClick() {
                                this.dismiss();
                            }
                        };
                        customAlertDialog.setMessage(getString(R.string.closeformap));
                        customAlertDialog.show();

                        return true;
                    }
                });

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


   private void googleMapCall(){
        //Uri routeUri = Uri.parse("http://maps.google.com/maps?saddr="+activity.getLatitude()+","+activity.getLongiude()+"&daddr="+lat+","+longe);
        Uri routeUri = Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lng);

        Intent i = new Intent(Intent.ACTION_VIEW, routeUri);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                activity.onBackPressed();
                break;

            case R.id.txt_getDirection:
                CustomAlertDialog customAlertDialog = new CustomAlertDialog(activity) {
                    @Override
                    public void leftButtonClick() {
                        googleMapCall();
                        this.dismiss();
                    }

                    @Override
                    public void rightButtonClick() {
                        this.dismiss();
                    }
                };
                customAlertDialog.setMessage(getString(R.string.closeformap));
                customAlertDialog.show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.hideStatusBar();
    }
}
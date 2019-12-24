package com.scenekey.activity.trending_summery;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scenekey.R;
import com.scenekey.activity.trending_summery.Model.MapModel;

public class MapActivity extends AppCompatActivity {

    private MapView mMapView;
    private GoogleMap googleMap;
    private String lat = "", lng = "", img = "", vname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setStatusBarColor();
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getIntentData();
        inItView();

    }

    private void inItView() {
        TextView btn = findViewById(R.id.btn);
        ImageView img_back = findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomMapPopup();
            }
        });

    }

    private void setStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void getIntentData() {

        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("long");
        img = getIntent().getStringExtra("img");
        vname = getIntent().getStringExtra("vname");
        MapWorking();
    }


    private void MapWorking() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                LatLng sydney;
                sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
                googleMap.setMinZoomPreference(13);
                MarkerOptions markerOptions = new MarkerOptions();
                googleMap.addMarker(markerOptions
                        .position(sydney)
                        .anchor(0.5f, 0.5f)
                        .snippet(sydney + "")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.inactive_map_ico)));
                MapModel mapModel = new MapModel();
                mapModel.setImg(img);
                mapModel.setName(vname);

                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(MapActivity.this, mapModel);
                googleMap.setInfoWindowAdapter(customInfoWindow);


                Marker m = googleMap.addMarker(markerOptions);
                m.setTag(mapModel);
                m.showInfoWindow();


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 2000);


                googleMap.setOnInfoWindowClickListener(marker -> Log.e("map", "map"));

                googleMap.setOnMarkerClickListener(marker -> {
                    Log.e("map", "map");
                    return false;
                });


            }
        });

    }

    public void showCustomMapPopup() {
        final Dialog dialog = new Dialog(MapActivity.this,R.style.DialogTheme);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_reward);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvPopupOk, tvMessages, tvPopupCancel;

        tvMessages = dialog.findViewById(R.id.tvMessages);
        tvPopupCancel = dialog.findViewById(R.id.tvPopupCancel);
        tvPopupOk = dialog.findViewById(R.id.tvPopupOk);
        tvMessages.setText(getResources().getString(R.string.mapstr));
        tvPopupCancel.setText("YES");
        tvPopupOk.setText("NO");

        tvPopupOk.setOnClickListener(view -> {
            dialog.dismiss();
        });

        tvPopupCancel.setOnClickListener(view -> {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("www.google.com")
                    .appendPath("maps")
                    .appendPath("dir")
                    .appendPath("")
                    .appendQueryParameter("api", "1")
                    .appendQueryParameter("destination", Double.parseDouble(lat) + "," + Double.parseDouble(lng));
            String url = builder.build().toString();
            Log.d("Directions", url);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            dialog.dismiss();
        });

        dialog.show();
    }

}

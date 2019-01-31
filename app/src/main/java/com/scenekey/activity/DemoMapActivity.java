package com.scenekey.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scenekey.R;
import com.scenekey.helper.CustomAlertDialog;
import com.scenekey.util.Utility;

public class DemoMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gmap;
    private TextView text_view;
    private Utility utility;
    private ImageView img_f10_back_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        utility = new Utility(this);
        text_view = findViewById(R.id.text_view);
        img_f10_back_map = findViewById(R.id.img_f10_back_map);
        text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAlertDialog customAlertDialog = new CustomAlertDialog(DemoMapActivity.this) {
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
                customAlertDialog.setMessage("This will close Scencekey and open the maps application. Continue?");
                customAlertDialog.show();

                //utility.showCustomPopup("This will close SceneKey and open the maps application. Continue?", String.valueOf(R.font.montserrat_medium));
            }
        });


        img_f10_back_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void googleMapCall() {
        //Uri routeUri = Uri.parse("http://maps.google.com/maps?saddr="+activity.getLatitude()+","+activity.getLongiude()+"&daddr="+lat+","+longe);
        Uri routeUri = Uri.parse("http://maps.google.com/maps?daddr=" + 36.1699 + "," + 115.1398);
        Intent i = new Intent(Intent.ACTION_VIEW, routeUri);
        startActivity(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(36.1699, 115.1398);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));

        gmap.addMarker(new MarkerOptions()
                .position(ny)
                .title("Las Vegas")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)));
    }
}

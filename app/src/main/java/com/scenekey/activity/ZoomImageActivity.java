package com.scenekey.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scenekey.R;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

public class ZoomImageActivity extends AppCompatActivity {

    RelativeLayout uperView,mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        mainView = findViewById(R.id.mainView);
        uperView = findViewById(R.id.uperView);
//        uperView.setVisibility(View.GONE);
        PhotoView photoView = findViewById(R.id.photo_view);
        ImageView img_f1_back = findViewById(R.id.img_f1_back);

        img_f1_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        if(getIntent().getStringExtra("imageUrl")!= null){
             String imageUrl = getIntent().getStringExtra("imageUrl");
            Picasso.with(this).load(imageUrl).placeholder(R.drawable.bg_event_card).into(photoView);
        }
    }
}

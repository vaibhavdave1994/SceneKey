package com.scenekey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;

public class ScrollTempActivity extends AppCompatActivity {

    ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_profile_fragment);
//        scrollView = findViewById(R.id.scrollView);
//        scrollView.fullScroll(View.FOCUS_DOWN);
    }


}
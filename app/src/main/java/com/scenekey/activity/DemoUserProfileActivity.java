package com.scenekey.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.scenekey.R;
import com.scenekey.helper.SessionManager;
import com.scenekey.model.UserInfo;

public class DemoUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_user_profile);
        inItView();
    }

    private void inItView() {
        ImageView img_demoUserback = findViewById(R.id.img_demoUserback);
        EditText tv_bio_demo = findViewById(R.id.tv_bio_demo);
        ImageView img_red_demo = findViewById(R.id.img_red_demo);
        ImageView img_yellow_demo = findViewById(R.id.img_yellow_demo);
        ImageView img_green_demo = findViewById(R.id.img_green_demo);

        setOnClick(img_demoUserback, img_red_demo, img_yellow_demo, img_green_demo);

        UserInfo userInfo = SessionManager.getInstance().getUserInfo();
        if (userInfo.user_status != null) {
            switch (userInfo.user_status) {
                case "1":
                    img_green_demo.setImageResource(R.drawable.ic_active_grn_circle);
                    img_red_demo.setImageResource(R.drawable.bg_red_ring);
                    img_yellow_demo.setImageResource(R.drawable.bg_yellow_ring);
                    break;

                case "2":
                    img_green_demo.setImageResource(R.drawable.bg_green_ring);
                    img_red_demo.setImageResource(R.drawable.bg_red_ring);
                    img_yellow_demo.setImageResource(R.drawable.ic_active_ylw_circle);
                    break;

                case "3":
                    img_green_demo.setImageResource(R.drawable.bg_green_ring);
                    img_red_demo.setImageResource(R.drawable.ic_active_red_circle);
                    img_yellow_demo.setImageResource(R.drawable.bg_yellow_ring);
                    break;
            }
        }
    }

    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_demoUserback:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

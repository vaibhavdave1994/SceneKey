package com.scenekey.activity.new_reg_flow;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.activity.RegistrationActivity;
import com.scenekey.cropper.CropImage;
import com.scenekey.cropper.CropImageView;
import com.scenekey.helper.Constant;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.Pop_Up_Option;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivityNewGender extends AppCompatActivity {

     AppCompatImageView img_back;
     AppCompatButton btn_next;
     Utility utility;
     private String  profileImageUrl = "";
     Context context = this;
     CardView cv_female,cv_male;
     ImageView iv_male_chk,iv_female_chk;
     boolean isMaleSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_gender);
        setStatusBarColor();
        initView();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

    private void initView() {

        btn_next = findViewById(R.id.btn_next);
        img_back = findViewById(R.id.img_back);
        iv_male_chk = findViewById(R.id.iv_male_chk);
        iv_female_chk = findViewById(R.id.iv_female_chk);
        cv_female = findViewById(R.id.cv_female);
        cv_male = findViewById(R.id.cv_male);

        utility = new Utility(this);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        Intent intent = new Intent(context, RegistrationActivity.class);
//                        intent.putExtra("imageUri", profileImageUrl);
//                        intent.putExtra("f_name", et_f_name.getText().toString().trim());
//                        intent.putExtra("l_name", et_l_name.getText().toString().trim());
//                        startActivity(intent);

            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cv_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMaleSelected){
                    isMaleSelected = true;
                    iv_male_chk.setImageDrawable(getResources().getDrawable(R.drawable.active_tick_ico));
                    iv_female_chk.setImageDrawable(getResources().getDrawable(R.drawable.inactive_dot_img));
                }
            }
        });

        cv_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMaleSelected){
                    isMaleSelected = false;
                    iv_female_chk.setImageDrawable(getResources().getDrawable(R.drawable.active_tick_ico));
                    iv_male_chk.setImageDrawable(getResources().getDrawable(R.drawable.inactive_dot_img));
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

package com.scenekey.activity.new_reg_flow;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.scenekey.R;
import com.scenekey.activity.RegistrationActivity;
import com.scenekey.model.UserInfo;
import com.scenekey.util.Utility;

public class RegistrationActivityNewGender extends RegistrationActivity {

     AppCompatImageView img_back;
     AppCompatButton btn_next;
     Utility utility;
     Context context = this;
     CardView cv_female,cv_male;
     ImageView iv_male_chk,iv_female_chk;
     boolean isMaleSelected = true;
     UserInfo userInfo = null;
     Bitmap profileImageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_gender);
        setStatusBarColor();
        initView();
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

    private void initView() {

        btn_next = findViewById(R.id.btn_next);
        img_back = findViewById(R.id.img_back);
        iv_male_chk = findViewById(R.id.iv_male_chk);
        iv_female_chk = findViewById(R.id.iv_female_chk);
        cv_female = findViewById(R.id.cv_female);
        cv_male = findViewById(R.id.cv_male);

        utility = new Utility(this);

        Intent intent = getIntent();
        final String fName = intent.getStringExtra("f_name");
        final String l_name = intent.getStringExtra("l_name");
        final String imageUri = intent.getStringExtra("imageUri");
        final String email = getIntent().getStringExtra("email");

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RegistrationActivityNewCreatePassword.class);

                if (userInfo != null) {
                    if (isMaleSelected)
                        userInfo.userGender = "male";
                    else
                        userInfo.userGender = "female";

//                    if(profileImageBitmap == null)
//                    profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userInfo.userImage);

                    if(userInfo.byteArray != null){
                        profileImageBitmap = BitmapFactory.decodeByteArray(userInfo.byteArray, 0, userInfo.byteArray.length);
                    }
                    else{
                        if(imageUri != null) {
                            try {
                                intent.putExtra("imageUri", imageUri);
                                //profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    intent.putExtra("userInfo", userInfo);


                    //doRegistration(userInfo.fullname, userInfo.lastName, userInfo.userEmail, userInfo.password, userInfo.userGender, userInfo.userFacebookId,profileImageBitmap,userInfo.loginstatus);
                }
                else {

                    intent.putExtra("imageUri", imageUri);
                    intent.putExtra("f_name", fName);
                    intent.putExtra("l_name", l_name);
                    intent.putExtra("email", email);
                    if (isMaleSelected)
                        intent.putExtra("gender", "male");
                    else
                        intent.putExtra("gender", "female");

                }
                startActivity(intent);
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

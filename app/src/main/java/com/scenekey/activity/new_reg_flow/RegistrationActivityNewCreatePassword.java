package com.scenekey.activity.new_reg_flow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.activity.RegistrationActivity;
import com.scenekey.activity.TnCAndPrivacyActivity;
import com.scenekey.activity.TnCWebView;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.Utility;

import java.io.IOException;

public class RegistrationActivityNewCreatePassword extends RegistrationActivity {

     EditText et_password;
     AppCompatButton btn_signin;
     String email;
     AppCompatImageView img_back;
     TextView tv_tnc;
     Context context = this;
     Bitmap profileImageBitmap;
     LinearLayout ll_cb;
    Utility utility;
    TextView tv_forgot_pass,tv_heading,tv_privacy_policy;
    UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_password);
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
        img_back = findViewById(R.id.img_back);
        et_password = findViewById(R.id.et_password);
        btn_signin = findViewById(R.id.btn_signin);
        ll_cb = findViewById(R.id.ll_cb);
        tv_tnc = findViewById(R.id.tv_tnc);
        tv_privacy_policy = findViewById(R.id.tv_privacy_policy);
        tv_forgot_pass = findViewById(R.id.tv_forgot_pass);
        tv_heading = findViewById(R.id.tv_heading);
        tv_heading.setText("Choose a password");
        btn_signin.setText("Sign Up");
        tv_forgot_pass.setVisibility(View.GONE);
        ll_cb.setVisibility(View.VISIBLE);

        utility = new Utility(context);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        final String fName = intent.getStringExtra("f_name");
        final String l_name = intent.getStringExtra("l_name");
        final String imageUri = intent.getStringExtra("imageUri");
        final String gender = intent.getStringExtra("gender");

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        try {
            if(imageUri != null)
            profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = et_password.getText().toString().trim();
                if(!password.equalsIgnoreCase("")){
                    if(password.length() >= 6){

                        if (userInfo != null) {
                            userInfo.password = password;
                            if(userInfo.byteArray != null){
                             profileImageBitmap = BitmapFactory.decodeByteArray(userInfo.byteArray, 0, userInfo.byteArray.length);
                        }
                            else{
                                if(imageUri != null) {
                                    try {
                                        profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            doRegistration(userInfo.fullname, userInfo.lastName, userInfo.userEmail, userInfo.password, userInfo.userGender, userInfo.userFacebookId,profileImageBitmap,userInfo.loginstatus);
                        }
                        else
                        doRegistration(fName, l_name, email, password, gender, "",profileImageBitmap,"");
                    } else {
                        //Toast.makeText(context, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
                        utility.showCustomPopup("Password must be atleast 6 characters long.", String.valueOf(R.font.montserrat_medium));
                    }
                }
                else {
                    utility.showCustomPopup("Please enter password.", String.valueOf(R.font.montserrat_medium));
                    //Toast.makeText(RegistrationActivityNewCreatePassword.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToWebView = new Intent(RegistrationActivityNewCreatePassword.this,
                        TnCWebView.class);
                intentToWebView.putExtra("url", WebServices.TNC_WEBURL);
                startActivity(intentToWebView);
            }
        });

        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToWebView = new Intent(RegistrationActivityNewCreatePassword.this,
                        TnCWebView.class);
                intentToWebView.putExtra("url", WebServices.PRIVACY_POLICY_WEBURL);
                startActivity(intentToWebView);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

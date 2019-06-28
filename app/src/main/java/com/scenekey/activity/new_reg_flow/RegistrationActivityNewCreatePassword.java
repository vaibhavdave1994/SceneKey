package com.scenekey.activity.new_reg_flow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.activity.LoginActivity;
import com.scenekey.activity.RegistrationActivity;
import com.scenekey.helper.Validation;
import com.scenekey.util.Utility;

import java.io.IOException;

public class RegistrationActivityNewCreatePassword extends RegistrationActivity {

     EditText et_password;
     AppCompatButton btn_signin;
     String email;
     AppCompatImageView img_back;
     CheckBox cb_tnc;
     Context context = this;
    private Bitmap profileImageBitmap;
    Utility utility;
    TextView tv_forgot_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_password);
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
        img_back = findViewById(R.id.img_back);
        et_password = findViewById(R.id.et_password);
        btn_signin = findViewById(R.id.btn_signin);
        cb_tnc = findViewById(R.id.cb_tnc);
        tv_forgot_pass = findViewById(R.id.tv_forgot_pass);
        tv_forgot_pass.setVisibility(View.GONE);
        cb_tnc.setVisibility(View.VISIBLE);

        utility = new Utility(context);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        final String fName = intent.getStringExtra("f_name");
        final String l_name = intent.getStringExtra("l_name");
        final String imageUri = intent.getStringExtra("imageUri");
        final String gender = intent.getStringExtra("gender");
        try {
            profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = et_password.getText().toString().trim();
                if(!password.equalsIgnoreCase("")){
                    if (cb_tnc.isChecked()) {
                        doRegistration(fName, l_name, email, password, gender, "",profileImageBitmap);
                    } else {
                        //Toast.makeText(context, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
                        utility.showCustomPopup("Please accept terms and conditions.", String.valueOf(R.font.montserrat_medium));
                    }
                }
                else {
                    Toast.makeText(RegistrationActivityNewCreatePassword.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

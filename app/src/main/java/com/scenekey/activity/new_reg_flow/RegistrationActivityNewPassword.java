package com.scenekey.activity.new_reg_flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.activity.ForgotPasswordActivity;
import com.scenekey.activity.LoginActivity;

public class RegistrationActivityNewPassword extends LoginActivity {

     EditText et_password;
     AppCompatButton btn_signin;
     String email;
     AppCompatImageView img_back;
     TextView tv_forgot_pass;

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
        tv_forgot_pass = findViewById(R.id.tv_forgot_pass);
        email = getIntent().getStringExtra("email");

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = et_password.getText().toString().trim();
                if(!password.equalsIgnoreCase("")){
                    doLogin(email, password);
                }
                else {
                    Toast.makeText(RegistrationActivityNewPassword.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPassword = new Intent(RegistrationActivityNewPassword.this, ForgotPasswordActivity.class);
                startActivity(forgotPassword);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

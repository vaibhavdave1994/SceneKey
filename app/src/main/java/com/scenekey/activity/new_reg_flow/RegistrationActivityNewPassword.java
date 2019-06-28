package com.scenekey.activity.new_reg_flow;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.activity.LoginActivity;
import com.scenekey.helper.Constant;

public class RegistrationActivityNewPassword extends LoginActivity {

     EditText et_password;
     AppCompatButton btn_signin;
     String email;
     AppCompatImageView img_back;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

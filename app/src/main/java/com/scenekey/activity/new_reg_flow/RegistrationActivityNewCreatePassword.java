package com.scenekey.activity.new_reg_flow;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.scenekey.R;
import com.scenekey.activity.RegistrationActivity;
import com.scenekey.activity.TnCWebView;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.Utility;

import java.io.IOException;

public class RegistrationActivityNewCreatePassword extends RegistrationActivity {

    public static int click = 0;
    private EditText et_password;
    private AppCompatButton btn_signin;
    private String email;
    private AppCompatImageView img_back;
    private TextView tv_tnc;
    private Context context = this;
    private Bitmap profileImageBitmap;
    private LinearLayout ll_cb;
    private Utility utility;
    private TextView tv_forgot_pass, tv_heading, tv_privacy_policy;
    private UserInfo userInfo = null;
    private ImageView iv_checkbox;
    private boolean ischeck = false;

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
        iv_checkbox = findViewById(R.id.iv_checkbox);
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


        if (userInfo != null) {
            iv_checkbox.setVisibility(View.VISIBLE);
        } else {
            ischeck = true;
        }


        try {
            if (imageUri != null)
                profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (click == 0) {
                    click = R.id.btn_signin;
                    String password = et_password.getText().toString().trim();
                    if (!ischeck) {
                        click = 0;
                        showDefaultDialog();
                    } else {
                        if (!password.equalsIgnoreCase("")) {
                            if (password.length() >= 6) {
                                if (userInfo != null) {
                                    userInfo.password = password;
                                    if (userInfo.byteArray != null) {
                                        profileImageBitmap = BitmapFactory.decodeByteArray(userInfo.byteArray, 0, userInfo.byteArray.length);
                                    } else {
                                        if (imageUri != null) {
                                            try {
                                                profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
                                            } catch (IOException e) {
                                                click = 0;
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    doRegistration(userInfo.fullname, userInfo.lastName, userInfo.userEmail, userInfo.password, userInfo.userGender, userInfo.userFacebookId, profileImageBitmap, userInfo.loginstatus);
                                } else
                                    doRegistration(fName, l_name, email, password, gender, "", profileImageBitmap, "");
                            } else {
                                click = 0;
                                //Toast.makeText(context, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
                                utility.showCustomPopup("Password must be atleast 6 characters long.", String.valueOf(R.font.montserrat_medium));
                            }
                        } else {
                            click = 0;
                            utility.showCustomPopup("Please enter password.", String.valueOf(R.font.montserrat_medium));
                            //Toast.makeText(RegistrationActivityNewCreatePassword.this, "Enter Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        iv_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ischeck) {
                    ischeck = false;
                    click = 0;
                    iv_checkbox.setImageDrawable(getResources().getDrawable(R.drawable.uncheck_box));
                } else {
                    click = 0;
                    ischeck = true;
                    iv_checkbox.setImageDrawable(getResources().getDrawable(R.drawable.check_box));

                }
            }
        });

        img_back.setOnClickListener(v -> onBackPressed());

        tv_tnc.setOnClickListener(v -> {
            Intent intentToWebView = new Intent(RegistrationActivityNewCreatePassword.this,
                    TnCWebView.class);
            intentToWebView.putExtra("url", WebServices.TNC_WEBURL);
            startActivity(intentToWebView);
        });

        tv_privacy_policy.setOnClickListener(v -> {
            Intent intentToWebView = new Intent(RegistrationActivityNewCreatePassword.this,
                    TnCWebView.class);
            intentToWebView.putExtra("url", WebServices.PRIVACY_POLICY_WEBURL);
            startActivity(intentToWebView);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showDefaultDialog() {
        final Dialog dialog = new Dialog(context,R.style.DialogTheme);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_title_btn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //      deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id

        TextView tvPopupOk, tvTitle, tvMessages;
        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.alert);
        tvMessages = dialog.findViewById(R.id.tvMessages);
        tvMessages.setText(R.string.acceptterms);
        tvPopupOk = dialog.findViewById(R.id.tvPopupOk);
        tvPopupOk.setText(R.string.ok);
        tvPopupOk.setOnClickListener(view -> {
            // Show location settings when the user acknowledges the alert dialog
            dialog.cancel();
            click = 0;
        });
        dialog.show();
    }


}

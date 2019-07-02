package com.scenekey.activity.new_reg_flow;


import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.scenekey.R;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.WebServices;
import com.scenekey.model.Events;
import com.scenekey.model.UserInfo;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleyMultipartRequest;
import com.scenekey.volleymultipart.VolleySingleton;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivityNewEmail extends BaseActivity {

     EditText et_email;
     AppCompatImageView img_back;
     AppCompatButton btn_next;
     boolean isValidEmail = false;
     Utility utility;
     UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_email);
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
        et_email = findViewById(R.id.et_email);
        btn_next = findViewById(R.id.btn_next);
        img_back = findViewById(R.id.img_back);
        textWatcher(et_email);
        utility = new Utility(this);

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        if(userInfo != null){
            if(!userInfo.userEmail.equalsIgnoreCase("") || userInfo.userEmail != null)
            et_email.setText(userInfo.userEmail);
        }
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail){
                    checkEmailRegisteration(et_email.getText().toString());
                }
                else {
                    Toast.makeText(RegistrationActivityNewEmail.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
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

    //------text watcher-----------
    private void textWatcher(EditText et_serch_post) {

        et_serch_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String searchText = editable.toString();

                if (searchText.toLowerCase().matches(Constant.emailPattern)){
                    btn_next.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_reg_btn_back_primary));
                    btn_next.setTextColor(getResources().getColor(R.color.white));
                    isValidEmail = true;
                    return;
                }
                else {
                    isValidEmail = false;
                    btn_next.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_next_btn_desable));
                    btn_next.setTextColor(getResources().getColor(R.color.button_text_new_reg));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void checkEmailRegisteration(final String email) {

        if (utility.checkInternetConnection()) {

            showProgDialog(false,"RegistrationActivityNewEmail");

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.CHECK_EMAIL_REG, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.v("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            dismissProgDialog();

                            if(message.equalsIgnoreCase("exist")){
                                Intent intent = new Intent(RegistrationActivityNewEmail.this, RegistrationActivityNewPassword.class);
                                intent.putExtra("email",email);
                                startActivity(intent);
                            }
                            else {
                                if(userInfo != null){
                                    Intent intent = new Intent(RegistrationActivityNewEmail.this, RegistrationActivityNewBasicInfo.class);
                                    userInfo.userEmail = et_email.getText().toString().trim();
                                    intent.putExtra("userInfo",userInfo);
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(RegistrationActivityNewEmail.this, RegistrationActivityNewBasicInfo.class);
                                    intent.putExtra("email",email);
                                    startActivity(intent);
                                }

                            }

                        } else {
                            Toast.makeText(RegistrationActivityNewEmail.this, message, Toast.LENGTH_SHORT).show();
                            dismissProgDialog();
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Toast.makeText(RegistrationActivityNewEmail.this, networkResponse + "", Toast.LENGTH_SHORT).show();

                    dismissProgDialog();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userEmail", email);
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(RegistrationActivityNewEmail.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(RegistrationActivityNewEmail.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }
}

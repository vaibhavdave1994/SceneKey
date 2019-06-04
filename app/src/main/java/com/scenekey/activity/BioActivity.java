package com.scenekey.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.fragment.Bio_Fragment;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.StatusBarUtil;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BioActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = BioActivity.class.toString();
    private UserInfo userInfo;
    private TextView tv_for_remainChar;
    private EditText et_for_enterTxt;
    private CustomProgressBar prog;
    private String fromScreen ="";
    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  StatusBarUtil.setTranslucent(this);
        setContentView(R.layout.fragment_bio);

        utility = new Utility(this);
        userInfo = SceneKey.sessionManager.getUserInfo();
        prog = new CustomProgressBar(this);

        tv_for_remainChar = findViewById(R.id.tv_for_remainChar);
        et_for_enterTxt = findViewById(R.id.et_for_enterTxt);

        findViewById(R.id.btn_for_done).setOnClickListener(this);
        findViewById(R.id.img_f1_back).setOnClickListener(this);


        et_for_enterTxt.setText(userInfo.bio);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 60 - s.length();
                tv_for_remainChar.setText(text + "");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et_for_enterTxt.addTextChangedListener(textWatcher);


        if(getIntent().getStringExtra("from")!= null){
            fromScreen  = getIntent().getStringExtra("from");
        }

    }


    private void showProgDialog(boolean cancelable) {
        prog.setCancelable(cancelable);
        prog.setCanceledOnTouchOutside(cancelable);
        prog.show();
    }

    private void dismissProgDialog() {
        if (prog != null) prog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_for_done:
                try {
                    String bio = et_for_enterTxt.getText().toString();
                    /*if (bio.isEmpty()){
                        callIntent();
                    }else
                        updateBio(bio);*/

                    // New Code
                    if (bio.isEmpty()) {
                        utility.showCustomPopup("Please enter bio", String.valueOf(R.font.montserrat_medium));
                       // Toast.makeText(this, "Please enter Bio", Toast.LENGTH_SHORT).show();
                    } else {
                        updateBio(bio);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.img_f1_back:
                onBackPressed();
                break;
        }
    }

    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }

    private void updateBio(final String bio) {
        showProgDialog(false);
        final Utility utility = new Utility(this);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.BIO, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        dismissProgDialog();
                        // System.out.println(" login response" + response);
                        jsonObject = new JSONObject(Response);
                        int statusCode = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (statusCode == 1) {
                            UserInfo userInfo = userInfo();
                            userInfo.bio = bio;
                            SceneKey.sessionManager.createSession(userInfo);

                            if(fromScreen.equals("setting")){
                              onBackPressed();
                            }else {

                                callIntent();
                            }

                        } else {
                            Utility.showToast(BioActivity.this, message, 0);
                        }

                    } catch (Exception ex) {
                        dismissProgDialog();
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("bio", bio);
                    params.put("user_id", userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(et_for_enterTxt, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    private void callIntent() {
        ImageSessionManager.getInstance().setScreenFlag(0);

        Intent intent = new Intent(this, HomeActivity.class);
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
